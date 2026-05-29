/**
 * 위키/이슈 내부 링크 자동완성 (wikiWrite) 및 보기 시 링크 변환 (wikiView)
 *
 * [작성 흐름]
 *   1. 사용자가 [[ 또는 [[# 입력
 *   2. detectTrigger → 커서 앞 문자열에서 트리거·검색어 추출
 *   3. fetchSuggestions → GET /project/wiki/link-suggest (WikiController)
 *   4. 드롭다운 표시 → 클릭/Enter 시 applyItem으로 본문에 insert 문자열 삽입
 *   5. DB에는 [[제목]], [[#번호 제목]] 형태로 저장 (URL 없음)
 *
 * [조회 흐름]
 *   wikiView에서 expandInternalLinks() 호출
 *   → [[#81 제목]], [[위키제목]] 을 [텍스트](URL) 마크다운 링크로 치환 후 Viewer 렌더
 *
 * 사용처:
 *   - wikiWrite.html: WikiLinkAutocomplete.init(editor, '#wikiEditor')
 *   - wikiView.html:  WikiLinkAutocomplete.expandInternalLinks(markdown)
 */
var WikiLinkAutocomplete = (function () {
    /** [[# ...]] — 이슈 링크 (마크다운 Heading # 과 충돌하지 않도록 [[# 사용) */
    var ISSUE_TRIGGER = /\[\[#([^\]]*)$/;
    /** [[ ...]] — 위키 링크 ([[# 로 시작하는 경우는 제외) */
    var WIKI_TRIGGER = /\[\[(?!#)([^\]]*)$/;

    /**
     * TOAST UI Editor 마크다운 + 커서 위치 → 전체 텍스트와 커서 기준 before/after 분리
     * @returns {{ md: string, before: string, after: string, pos: number }}
     */
    function getMarkdownContext(editor) {
        var md = editor.getMarkdown();
        var sel = editor.getSelection();
        if (!sel || !sel[0]) {
            return { md: md, before: md, after: '', pos: md.length };
        }
        var lines = md.split('\n');
        var lineIdx = sel[0][0];
        var ch = sel[0][1];
        var pos = 0;
        for (var i = 0; i < lineIdx && i < lines.length; i++) {
            pos += lines[i].length + 1;
        }
        if (lineIdx < lines.length) {
            pos += Math.min(ch, lines[lineIdx].length);
        }
        return {
            md: md,
            before: md.substring(0, pos),
            after: md.substring(pos),
            pos: pos
        };
    }

    /**
     * 커서 앞 텍스트에 [[ / [[# 트리거가 있는지 판별
     * @returns {{ type: 'wiki'|'issue', query: string, replaceStart: number, replaceEnd: number }|null}
     */
    function detectTrigger(before) {
        var issueMatch = before.match(ISSUE_TRIGGER);
        if (issueMatch) {
            return {
                type: 'issue',
                query: (issueMatch[1] || '').trim(),
                replaceStart: before.lastIndexOf('[[#'),
                replaceEnd: before.length
            };
        }
        var wikiMatch = before.match(WIKI_TRIGGER);
        if (wikiMatch) {
            return {
                type: 'wiki',
                query: (wikiMatch[1] || '').trim(),
                replaceStart: before.lastIndexOf('[['),
                replaceEnd: before.length
            };
        }
        return null;
    }

    /** API 재호출 시 이전 응답 무시용 — 트리거 상태가 동일한지 비교 */
    function triggersEqual(a, b) {
        if (!a || !b) return false;
        return a.type === b.type && a.query === b.query
            && a.replaceStart === b.replaceStart && a.replaceEnd === b.replaceEnd;
    }

    /**
     * 서버에서 위키/이슈 제안 목록 조회
     * 응답: [{ type, label, insert }, ...]  (insert 예: [[API 설계]], [[#81 버그 수정]])
     */
    function fetchSuggestions(type, query) {
        var url = '/project/wiki/link-suggest?type=' + encodeURIComponent(type)
            + '&q=' + encodeURIComponent(query || '');
        return fetch(url).then(function (res) {
            if (!res.ok) return [];
            return res.json();
        }).catch(function () {
            return [];
        });
    }

    /**
     * 위키 작성 에디터에 자동완성 UI 연결
     * @param {object} editor - toastui.Editor 인스턴스
     * @param {string} anchorSelector - 드롭다운 위치 기준 요소 (예: '#wikiEditor')
     */
    function init(editor, anchorSelector) {
        if (!editor) return;

        var dropdown = document.createElement('div');
        dropdown.className = 'wiki-link-autocomplete';
        dropdown.setAttribute('role', 'listbox');
        dropdown.hidden = true;

        var header = document.createElement('div');
        header.className = 'wiki-link-autocomplete-header';
        dropdown.appendChild(header);

        var list = document.createElement('div');
        list.className = 'wiki-link-autocomplete-list';
        dropdown.appendChild(list);

        document.body.appendChild(dropdown);

        var state = {
            trigger: null,       // 현재 [[ / [[# 트리거 정보
            items: [],           // API에서 받은 제안 목록
            activeIndex: -1,     // 키보드/마우스로 선택 중인 항목
            debounceTimer: null,
            fetchSeq: 0,         // 늦게 도착한 fetch 응답 무시용 시퀀스
            suppressUntil: 0,    // 삽입 직후 change 이벤트로 목록이 다시 뜨는 것 방지
            pointerInside: false, // 드롭다운 위에 마우스 — 닫기 방지
            open: false
        };

        function hideDropdown() {
            if (state.pointerInside) return;
            dropdown.hidden = true;
            header.textContent = '';
            list.innerHTML = '';
            state.trigger = null;
            state.items = [];
            state.activeIndex = -1;
            state.open = false;
        }

        /** 에디터 하단(또는 공간 부족 시 상단)에 드롭다운 고정 배치 */
        function positionDropdown() {
            var anchor = anchorSelector ? document.querySelector(anchorSelector) : null;
            if (!anchor) return;
            var rect = anchor.getBoundingClientRect();
            var top = rect.bottom + 4;
            var maxH = 260;
            if (top + maxH > window.innerHeight - 8) {
                top = Math.max(8, rect.top - maxH - 4);
            }
            dropdown.style.left = Math.max(8, rect.left) + 'px';
            dropdown.style.top = top + 'px';
            dropdown.style.minWidth = Math.min(Math.max(rect.width, 280), 480) + 'px';
            dropdown.style.maxWidth = '480px';
        }

        function setHeader(trigger) {
            if (!trigger) {
                header.textContent = '';
                return;
            }
            if (trigger.type === 'issue') {
                header.textContent = '이슈 링크 — [[#번호 제목]] · ↑↓ 이동, Enter 선택, Esc 닫기';
            } else {
                header.textContent = '위키 링크 — [[제목]] · ↑↓ 이동, Enter 선택, Esc 닫기';
            }
        }

        function renderDropdown() {
            list.innerHTML = '';
            setHeader(state.trigger);

            if (!state.items.length) {
                var empty = document.createElement('div');
                empty.className = 'wiki-link-autocomplete-empty';
                empty.textContent = state.trigger && state.trigger.query
                    ? '검색 결과가 없습니다.'
                    : '목록에서 선택하거나 검색어를 더 입력하세요.';
                list.appendChild(empty);
                dropdown.hidden = false;
                state.open = true;
                positionDropdown();
                return;
            }

            state.items.forEach(function (item, idx) {
                var row = document.createElement('button');
                row.type = 'button';
                row.className = 'wiki-link-autocomplete-item'
                    + (item.type === 'issue' ? ' is-issue' : ' is-wiki')
                    + (idx === state.activeIndex ? ' is-active' : '');
                row.textContent = (item.type === 'wiki' ? '위키' : '이슈') + ' · ' + item.label;
                row.addEventListener('mouseenter', function () {
                    state.activeIndex = idx;
                    renderDropdown();
                });
                row.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                });
                row.addEventListener('click', function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    applyItem(item);
                });
                list.appendChild(row);
            });

            dropdown.hidden = false;
            state.open = true;
            positionDropdown();

            var activeEl = list.querySelector('.is-active');
            if (activeEl && typeof activeEl.scrollIntoView === 'function') {
                activeEl.scrollIntoView({ block: 'nearest' });
            }
        }

        /**
         * 선택한 항목의 insert 문자열로 [[...]] 부분을 치환
         * replaceStart ~ 현재 커서(pos) 구간이 [[ 또는 [[# 로 시작한 미완성 입력
         */
        function applyItem(item) {
            var savedTrigger = state.trigger;
            if (!savedTrigger || !item) return;

            var ctx = getMarkdownContext(editor);
            var insertText = item.insert || '';
            if (!insertText) return;

            var newMd = ctx.md.substring(0, savedTrigger.replaceStart)
                + insertText
                + ctx.md.substring(ctx.pos);

            state.suppressUntil = Date.now() + 400;
            state.pointerInside = false;
            editor.setMarkdown(newMd);
            hideDropdown();

            if (typeof window.syncWikiContent === 'function') {
                window.syncWikiContent();
            }
        }

        /** ↑↓ Enter Esc — 에디터 기본 동작(줄바꿈 등)보다 먼저 처리 (capture) */
        function handleAutocompleteKeydown(e) {
            if (!state.open) return false;

            if (e.key === 'ArrowDown' && state.items.length) {
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();
                state.activeIndex = (state.activeIndex + 1) % state.items.length;
                renderDropdown();
                return true;
            }
            if (e.key === 'ArrowUp' && state.items.length) {
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();
                state.activeIndex = (state.activeIndex - 1 + state.items.length) % state.items.length;
                renderDropdown();
                return true;
            }
            if (e.key === 'Enter') {
                if (state.items.length && state.activeIndex < 0) {
                    state.activeIndex = 0;
                }
                if (state.activeIndex >= 0 && state.items.length) {
                    e.preventDefault();
                    e.stopPropagation();
                    e.stopImmediatePropagation();
                    applyItem(state.items[state.activeIndex]);
                    return true;
                }
            }
            if (e.key === 'Escape') {
                e.preventDefault();
                e.stopPropagation();
                state.pointerInside = false;
                hideDropdown();
                return true;
            }
            return false;
        }

        /** 입력 변화 감지 → 트리거 있으면 API 재검색 */
        function refreshSuggestions() {
            if (Date.now() < state.suppressUntil) return;
            if (state.pointerInside) return;

            var ctx = getMarkdownContext(editor);
            var trigger = detectTrigger(ctx.before);
            if (!trigger) {
                if (state.open) hideDropdown();
                return;
            }

            var sameTrigger = triggersEqual(state.trigger, trigger);
            state.trigger = trigger;

            if (!sameTrigger) {
                state.activeIndex = -1;
            }

            var seq = ++state.fetchSeq;
            fetchSuggestions(trigger.type, trigger.query).then(function (items) {
                if (seq !== state.fetchSeq) return;
                if (!state.trigger || !triggersEqual(state.trigger, trigger)) return;

                state.items = items || [];
                if (state.activeIndex < 0 && state.items.length) {
                    state.activeIndex = 0;
                } else if (state.activeIndex >= state.items.length) {
                    state.activeIndex = state.items.length ? state.items.length - 1 : -1;
                }
                renderDropdown();
            });
        }

        function onEditorInput() {
            if (Date.now() < state.suppressUntil) return;
            clearTimeout(state.debounceTimer);
            state.debounceTimer = setTimeout(refreshSuggestions, 120);
        }

        function isNavigationKey(key) {
            return key === 'ArrowUp' || key === 'ArrowDown' || key === 'Enter'
                || key === 'Escape' || key === 'Tab';
        }

        editor.on('change', onEditorInput);

        var editorRoot = anchorSelector ? document.querySelector(anchorSelector) : null;
        if (editorRoot) {
            editorRoot.addEventListener('keyup', function (e) {
                if (isNavigationKey(e.key)) return;
                onEditorInput();
            });
        }

        dropdown.addEventListener('mousedown', function (e) {
            e.preventDefault();
            e.stopPropagation();
        });
        dropdown.addEventListener('mouseenter', function () {
            state.pointerInside = true;
        });
        dropdown.addEventListener('mouseleave', function () {
            state.pointerInside = false;
        });

        document.addEventListener('keydown', handleAutocompleteKeydown, true);

        if (editorRoot) {
            editorRoot.addEventListener('keydown', handleAutocompleteKeydown, true);
            /** TOAST UI Editor 내부 textarea/ProseMirror — Enter가 에디터에서 먼저 삼켜지는 경우 대비 */
            function bindEditorInputs() {
                editorRoot.querySelectorAll('textarea, .ProseMirror, [contenteditable="true"]').forEach(function (el) {
                    el.addEventListener('keydown', handleAutocompleteKeydown, true);
                });
            }
            bindEditorInputs();
            setTimeout(bindEditorInputs, 300);
            setTimeout(bindEditorInputs, 1000);
        }

        document.addEventListener('mousedown', function (e) {
            if (!state.open) return;
            if (dropdown.contains(e.target)) return;
            if (editorRoot && editorRoot.contains(e.target)) return;
            state.pointerInside = false;
            hideDropdown();
        });

        window.addEventListener('resize', function () {
            if (state.open) positionDropdown();
        });
    }

    /** 마크다운 링크 라벨 안의 대괄호 이스케이프 */
    function escapeMarkdownLinkLabel(text) {
        return (text || '').replace(/\\/g, '\\\\').replace(/\[/g, '\\[').replace(/\]/g, '\\]');
    }

    /**
     * wikiView 렌더 직전: 저장된 내부 링크 문법 → 일반 마크다운 링크로 변환
     * 순서 중요: 이슈([[#n]]) 먼저, 위키([[제목]]) 다음, 레거시 #n 마지막
     */
    function expandInternalLinks(markdown) {
        if (!markdown) return markdown;
        var result = markdown;

        result = result.replace(/\[\[#(\d+)(?:\s+([^\]]+))?\]\]/g, function (_match, id, subject) {
            var url = '/project/issue/detail?id=' + id;
            var name = subject ? subject.trim() : '';
            var label = name ? name + ' (#' + id + ')' : '#' + id;
            return '[' + escapeMarkdownLinkLabel(label) + '](' + url + ')';
        });

        result = result.replace(/\[\[(?!#\d)([^\]]+)\]\]/g, function (_match, title) {
            var t = (title || '').trim();
            if (!t) return _match;
            var url = '/project/wiki/view/' + encodeURIComponent(t);
            return '[' + escapeMarkdownLinkLabel(t) + '](' + url + ')';
        });

        result = result.replace(/(^|[\s])(#(\d+))(?=$|[\s.,;:!?)])/gm, function (_match, prefix, _full, id) {
            var url = '/project/issue/detail?id=' + id;
            return prefix + '[#' + id + '](' + url + ')';
        });

        return result;
    }

    return {
        init: init,
        expandInternalLinks: expandInternalLinks
    };
})();
