document.addEventListener("DOMContentLoaded", function () {
          var pageData = window.memberListPageData || {};
          var prjId = pageData.prjId;
          var projectMemberRows = Array.isArray(pageData.rows) ? pageData.rows : [];

          function formatDate(value) {
            if (value == null || value === "") return "";
            if ( typeof value === "string" && /^\d{4}-\d{2}-\d{2}$/.test(value) ) return value;
            const d = new Date(value);
            if (isNaN(d.getTime())) return String(value);
            const pad = (n) => String(n).padStart(2, "0");
            return ( d.getFullYear() + "-" + pad(d.getMonth() + 1) + "-" + pad(d.getDate()) );
          }

          function buildCsrfHeaders() {
            var headers = { "Content-Type": "application/json" };
            var tokenMeta = document.querySelector('meta[name="_csrf"]');
            var headerMeta = document.querySelector(
              'meta[name="_csrf_header"]',
            );
            if ( tokenMeta && headerMeta && tokenMeta.content && headerMeta.content ) {
              headers[headerMeta.content] = tokenMeta.content;
            }
            return headers;
          }

          let selectedMemberKey = null;
          const memberListSplit = document.querySelector(".role-list-split");
          const detailAside = document.getElementById("memberDetailAside");
          const detailFrame = document.getElementById("memberDetailPanelFrame");
          const detailCloseBtn = document.getElementById("memberDetailCloseBtn");

          function refreshListGridLayout() {
            if (grid && typeof grid.refreshLayout === "function") {
              grid.refreshLayout();
            }
          }

          function buildPanelUrl(userId, grpId) {
            const panelBase =
              (window.memberListPageData && window.memberListPageData.panelUrl) ||
              "/project/member/panel";
            const join = panelBase.indexOf("?") >= 0 ? "&" : "?";
            return (
              panelBase +
              join +
              "userId=" +
              encodeURIComponent(String(userId)) +
              "&grpId=" +
              encodeURIComponent(String(grpId))
            );
          }

          function showMemberDetailPanel(userId, grpId) {
            if (userId == null || grpId == null) return;
            selectedMemberKey = String(userId) + "_" + String(grpId);
            if (memberListSplit) {
              memberListSplit.classList.add("is-detail-open");
            }
            if (detailAside) {
              detailAside.classList.remove("is-hidden");
            }
            if (detailFrame) {
              detailFrame.classList.remove("is-hidden");
              detailFrame.removeAttribute("srcdoc");
              detailFrame.src = buildPanelUrl(userId, grpId);
            }
            refreshListGridLayout();
          }

          function clearSelectedRows() {
            if (!grid) return;
            grid.getData().forEach(function (row) {
              if (row.rowKey != null) {
                grid.removeRowClassName(row.rowKey, "role-grid-row--selected");
              }
            });
          }

          function clearMemberDetailPanel() {
            selectedMemberKey = null;
            if (memberListSplit) {
              memberListSplit.classList.remove("is-detail-open");
            }
            if (detailAside) {
              detailAside.classList.add("is-hidden");
            }
            if (detailFrame) {
              detailFrame.removeAttribute("srcdoc");
              detailFrame.src = "about:blank";
              detailFrame.classList.add("is-hidden");
            }
            clearSelectedRows();
            refreshListGridLayout();
          }

          if (detailCloseBtn) {
            detailCloseBtn.addEventListener("click", function () {
              clearMemberDetailPanel();
            });
          }

          function markSelectedRow(userId, grpId) {
            if (!grid || userId == null || grpId == null) return;
            const target = String(userId) + "_" + String(grpId);
            grid.getData().forEach(function (row) {
              if (row.rowKey == null) return;
              const key = rowMemberKey(row);
              if (key === target) {
                grid.addRowClassName(row.rowKey, "role-grid-row--selected");
              } else {
                grid.removeRowClassName(row.rowKey, "role-grid-row--selected");
              }
            });
          }

          function rowMemberKey(r) {
            if (!r) return null;
            var u = r.userId != null ? String(r.userId) : "";
            var g = r.grpId != null ? String(r.grpId) : "";
            if (!u || !g) return null;
            return u + "_" + g;
          }

          function tryOpenMemberFromQuery() {
            var params = new URLSearchParams(window.location.search);
            var uid = params.get("userId");
            var gid = params.get("grpId");
            if (!uid || !gid) return;
            var uidNum = Number(uid);
            var gidNum = Number(gid);
            if (isNaN(uidNum) || isNaN(gidNum)) return;
            showMemberDetailPanel(uidNum, gidNum);
            markSelectedRow(uidNum, gidNum);
          }

          function memberKeyPayload(r) {
            if (!r) return null;
            var uid =
              r.userId != null && r.userId !== "" ? Number(r.userId) : NaN;
            var gid = r.grpId != null && r.grpId !== "" ? Number(r.grpId) : NaN;
            if (isNaN(uid) || isNaN(gid)) return null;
            return { userId: uid, grpId: gid };
          }

          let gridData = Array.isArray(projectMemberRows)
            ? projectMemberRows.map(function (r) {
                return typeof r === "object" && r !== null
                  ? Object.assign({}, r) : r;
              })
            : [];
          let deleteMode = false;
          let grid = null;
          let toolbarMountElement = null;

          function getToolbarMount() {
            if (!toolbarMountElement) {
              toolbarMountElement = document.createElement("div");
              toolbarMountElement.id = "memberGridToolbarMount";
              toolbarMountElement.className = "role-grid-toolbar";
              toolbarMountElement.setAttribute("role", "toolbar");
              toolbarMountElement.setAttribute("aria-label", "구성원 목록 도구");
            }
            return toolbarMountElement;
          }

          function detachToolbarFromGrid() {
            const host = document.getElementById("memberGrid");
            const mount = getToolbarMount();
            if (host && mount.parentNode === host) {
              host.removeChild(mount);
            }
          }

          function placeToolbarBetweenTableAndPagination() {
            const host = document.getElementById("memberGrid");
            const mount = getToolbarMount();
            if (!host || !mount) return false;
            const pag = host.querySelector(".tui-pagination");
            if (pag && pag.parentNode) {
              pag.parentNode.insertBefore(mount, pag);
              return true;
            }
            return false;
          }

          function schedulePlaceToolbar() {
            var tries = 0;
            function attempt() {
              if (placeToolbarBetweenTableAndPagination()) return;
              tries += 1;
              if (tries < 15) {
                setTimeout(attempt, 30);
                return;
              }
              var host = document.getElementById("memberGrid");
              var mount = getToolbarMount();
              if (host && mount && !host.contains(mount)) {
                host.appendChild(mount);
              }
            }
            setTimeout(attempt, 0);
          }

          function getCheckedRowsForRemove() {
            if (!grid) return [];
            if (typeof grid.getCheckedRowKeys === "function") {
              const keys = grid.getCheckedRowKeys();
              if (
                Array.isArray(keys) &&
                keys.length &&
                typeof grid.getRow === "function"
              ) {
                return keys.map((k) => grid.getRow(k)).filter(Boolean);
              }
            }
            if (typeof grid.getCheckedRows === "function") {
              const arr = grid.getCheckedRows();
              if (Array.isArray(arr)) return arr;
            }
            return [];
          }

          function renderToolbar() {
            const mount = getToolbarMount();
            if (!mount) return;
            let html = "";
            if (!deleteMode) {
              html +=
                '<button type="button" class="btn btn-primary" id="memberBtnRegister">등록</button>';
              html +=
                '<button type="button" class="btn btn-danger" id="memberBtnDelete">삭제</button>';
            } else {
              const rows = grid ? getCheckedRowsForRemove() : [];
              const n = rows.length;
              if (n === 0) {
                html += '<button type="button" class="btn btn-secondary" id="memberBtnDeleteCancel">삭제 취소</button>';
              } else {
                html += '<button type="button" class="btn btn-danger" id="memberBtnRemove">제거</button>';
              }
            }
            mount.innerHTML = html;
          }

          function bindToolbarMountOnce() {
            const mount = getToolbarMount();
            if (mount.dataset.bound === "1") return;
            mount.dataset.bound = "1";
            mount.addEventListener("click", function (e) {
              const btn = e.target.closest("button");
              if (!btn || !btn.id) return;
              if (btn.id === "memberBtnRegister") {
                location.href = "/project/member/join";
                return;
              }
              if (btn.id === "memberBtnDelete") {
                deleteMode = true;
                recreateGrid();
                return;
              }
              if (btn.id === "memberBtnDeleteCancel") {
                deleteMode = false;
                recreateGrid();
                return;
              }
              if (btn.id === "memberBtnRemove") {
                void onRemoveClicked();
              }
            });
          }

          async function onRemoveClicked() {
            const rows = getCheckedRowsForRemove();
            if (!rows.length) {
              return;
            }
            const names = rows.map(function (r) {
              return r.userName != null && r.userName !== ""
                ? String(r.userName) : "(이름 없음)";
            });
            const msg = "선택한 구성원을 프로젝트에서 제거하시겠습니까?\n\n" + names.join("\n");
            var confirmed = await window.MemberQuestionModal.confirm(
              msg, "구성원 제거" );
            if (!confirmed) {
              return;
            }
            const members = rows
              .map(function (r) {
                return memberKeyPayload(r);
              })
              .filter(Boolean);
            if (!members.length) {
              await window.MemberQuestionModal.alert( "사용자·그룹 정보를 확인할 수 없습니다.", "알림" );
              return;
            }
            var pid = Number(prjId);
            if (isNaN(pid)) {
              await window.MemberQuestionModal.alert( "프로젝트 ID가 올바르지 않습니다.", "알림" );
              return;
            }
            try {
              var res = await fetch("/project/member/deleteMembers", {
                method: "POST",
                headers: buildCsrfHeaders(),
                credentials: "same-origin",
                body: JSON.stringify({ members: members }),
              });
              var data = {};
              try {
                data = await res.json();
              } catch (ignore) {}
              if (!res.ok || !data || data.ok !== true) {
                var errText =
                  (data && data.message) ||
                  (res.status === 403
                    ? "권한이 없거나 보안 토큰이 만료되었습니다. 페이지를 새로고침한 뒤 다시 시도해 주세요."
                    : "제거 중 오류가 발생하였습니다.");
                await window.MemberQuestionModal.alert(errText, "오류");
                return;
              }
            } catch (e) {
              await window.MemberQuestionModal.alert( "제거 중 오류가 발생하였습니다.", "오류" );
              return;
            }
            const labelPart =
              names.length === 1
                ? "「" + names[0] + "」"
                : "「" + names.join("」, 「") + "」";
            const okMsg =
              names.length === 1
                ? labelPart + "이(가) 정상적으로 제거되었습니다."
                : labelPart + " 구성원이 정상적으로 제거되었습니다.";
            await window.MemberQuestionModal.alert(okMsg, "알림");

            const removeSet = {};
            rows.forEach(function (r) {
              const k = rowMemberKey(r);
              if (k) removeSet[k] = true;
            });
            gridData = gridData.filter(function (r) {
              const k = rowMemberKey(r);
              return !k || !removeSet[k];
            });
            if (selectedMemberKey && removeSet[selectedMemberKey]) {
              clearMemberDetailPanel();
            }
            deleteMode = false;
            recreateGrid();
          }

          function recreateGrid() {
            detachToolbarFromGrid();
            if (grid) {
              grid.destroy();
              grid = null;
            }
            const el = document.getElementById("memberGrid");
            if (!el) return;

            bindToolbarMountOnce();

            const rowHeaders = deleteMode
              ? [{ type: "checkbox", header: "선택" }]
              : [{ type: "rowNum", header: "번호" }];

            grid = new tui.Grid({
              el: el,
              data: gridData,
              rowHeaders: rowHeaders,
              scrollX: true,
              scrollY: false,
              bodyHeight: "auto",
              rowHeight: 36,
              minBodyHeight: 100,
              columns: [
                {
                  header: "이름",
                  name: "userName",
                  width: 120,
                  align: "center",
                  sortable: true,
                  formatter: ({ row, value }) => {
                    const uid =
                      row.userId != null && row.userId !== ""
                        ? row.userId
                        : null;
                    const gid =
                      row.grpId != null && row.grpId !== ""
                        ? row.grpId
                        : null;
                    const text = value == null ? "" : String(value);
                    if (uid == null || gid == null || !text) {
                      return text || "-";
                    }
                    return (
                      '<button type="button" class="role-name-link" data-user-id="' +
                      encodeURIComponent(String(uid)) +
                      '" data-grp-id="' +
                      encodeURIComponent(String(gid)) +
                      '">' +
                      text +
                      "</button>"
                    );
                  },
                },
                {
                  header: "연락처",
                  name: "tel",
                  width: 130,
                  align: "center",
                  sortable: false,
                },
                {
                  header: "이메일",
                  name: "email",
                  width: 220,
                  align: "center",
                  sortable: false,
                },
                {
                  header: "소속 그룹명",
                  name: "grpName",
                  width: 180,
                  align: "center",
                  sortable: false,
                },
              ],
              pageOptions: {
                useClient: true,
                perPage: 10,
              },
            });

            grid.on("check", function () {
              renderToolbar();
            });
            grid.on("uncheck", function () {
              renderToolbar();
            });
            grid.on("checkAll", function () {
              renderToolbar();
            });
            grid.on("uncheckAll", function () {
              renderToolbar();
            });

            grid.on("click", function (ev) {
              if (deleteMode || ev.rowKey == null) return;
              if (ev.columnName !== "userName") return;
              const row = grid.getRow(ev.rowKey);
              const uid =
                row && row.userId != null && row.userId !== ""
                  ? row.userId
                  : null;
              const gid =
                row && row.grpId != null && row.grpId !== ""
                  ? row.grpId
                  : null;
              if (uid != null && gid != null) {
                showMemberDetailPanel(uid, gid);
                markSelectedRow(uid, gid);
              }
            });

            renderToolbar();
            schedulePlaceToolbar();
          }

          recreateGrid();
          tryOpenMemberFromQuery();
        });
