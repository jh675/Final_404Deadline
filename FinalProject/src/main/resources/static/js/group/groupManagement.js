document.addEventListener("DOMContentLoaded", function () {
          var pageData = window.groupListPageData || {};
          var prjId = pageData.prjId;
          var projectGroupRows = Array.isArray(pageData.rows) ? pageData.rows : [];

          (function groupSearchDates() {
            const form = document.getElementById("groupSearchForm");
            const start = document.getElementById("createdFrom");
            const end = document.getElementById("createdTo");
            if (!form || !start || !end) return;

            const pad = (n) => String(n).padStart(2, "0");
            const todayStr = () => {
              const d = new Date();
              return ( d.getFullYear() + "-" + pad(d.getMonth() + 1) + "-" + pad(d.getDate()) );
            };

            const compare = (a, b) => {
              if (!a || !b) return 0;
              return a < b ? -1 : a > b ? 1 : 0;
            };

            function minYmd(a, b) {
              if (!a) return b;
              if (!b) return a;
              return compare(a, b) <= 0 ? a : b;
            }

            function applyLimits() {
              const today = todayStr();
              const sv = start.value;
              const ev = end.value;

              end.max = today;
              if (sv) end.min = sv; else end.removeAttribute("min");

              start.max = ev ? minYmd(today, ev) : today;
            }

            function clampStart() {
              const today = todayStr();
              let sv = start.value;
              let ev = end.value;
              if (sv && compare(sv, today) > 0) {
                sv = today;
                start.value = sv;
              }
              if (sv && ev && compare(sv, ev) > 0) end.value = sv;
              applyLimits();
            }

            function clampEnd() {
              const today = todayStr();
              let sv = start.value;
              let ev = end.value;
              if (ev && compare(ev, today) > 0) {
                end.value = today;
                ev = today;
              }
              if (sv && ev && compare(ev, sv) < 0) start.value = ev;
              applyLimits();
            }

            clampStart();
            clampEnd();
            applyLimits();

            start.addEventListener("change", clampStart);
            start.addEventListener("input", clampStart);
            end.addEventListener("change", clampEnd);
            end.addEventListener("input", clampEnd);

            form.addEventListener("submit", async function (e) {
              clampStart();
              clampEnd();
              const today = todayStr();
              const sv = start.value;
              const ev = end.value;
              if (sv && compare(sv, today) > 0) {
                e.preventDefault();
                await window.GroupQuestionModal.alert( "시작일은 오늘 이후로 지정할 수 없습니다.", "알림" );
                return;
              }
              if (ev && compare(ev, today) > 0) {
                e.preventDefault();
                await window.GroupQuestionModal.alert( "종료일은 오늘 이후로 지정할 수 없습니다.", "알림" );
                return;
              }
              if (sv && ev && compare(sv, ev) > 0) {
                e.preventDefault();
                await window.GroupQuestionModal.alert( "시작일은 종료일보다 늦을 수 없습니다.", "알림" );
                return;
              }
            });
          })();

          function formatDate(value) {
            if (value == null || value === "") return "";
            if (typeof value === "string" && /^\d{4}-\d{2}-\d{2}$/.test(value)) return value;
            const d = new Date(value);
            if (isNaN(d.getTime())) return String(value);
            const pad = (n) => String(n).padStart(2, "0");
            return (
              d.getFullYear() +
              "-" +
              pad(d.getMonth() + 1) +
              "-" +
              pad(d.getDate())
            );
          }

          function buildCsrfHeaders() {
            var headers = { "Content-Type": "application/json" };
            var tokenMeta = document.querySelector('meta[name="_csrf"]');
            var headerMeta = document.querySelector(
              'meta[name="_csrf_header"]',
            );
            if (
              tokenMeta &&
              headerMeta &&
              tokenMeta.content &&
              headerMeta.content
            ) {
              headers[headerMeta.content] = tokenMeta.content;
            }
            return headers;
          }

          let selectedGrpId = null;
          const groupListSplit = document.querySelector(".role-list-split");
          const detailAside = document.getElementById("groupDetailAside");
          const detailFrame = document.getElementById("groupDetailPanelFrame");
          const detailCloseBtn = document.getElementById("groupDetailCloseBtn");

          function refreshListGridLayout() {
            if (grid && typeof grid.refreshLayout === "function") {
              grid.refreshLayout();
            }
          }

          function buildPanelUrl(grpId) {
            const panelBase =
              (window.groupListPageData && window.groupListPageData.panelUrl) ||
              "/project/group/panel";
            const join = panelBase.indexOf("?") >= 0 ? "&" : "?";
            return (
              panelBase + join + "grpId=" + encodeURIComponent(String(grpId))
            );
          }

          function showGroupDetailPanel(grpId) {
            if (grpId == null || grpId === "") return;
            selectedGrpId = String(grpId);
            if (groupListSplit) {
              groupListSplit.classList.add("is-detail-open");
            }
            if (detailAside) {
              detailAside.classList.remove("is-hidden");
            }
            if (detailFrame) {
              detailFrame.classList.remove("is-hidden");
              detailFrame.removeAttribute("srcdoc");
              detailFrame.src = buildPanelUrl(selectedGrpId);
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

          function clearGroupDetailPanel() {
            selectedGrpId = null;
            if (groupListSplit) {
              groupListSplit.classList.remove("is-detail-open");
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
              clearGroupDetailPanel();
            });
          }

          function markSelectedRow(grpId) {
            if (!grid || grpId == null) return;
            const target = String(grpId);
            grid.getData().forEach(function (row) {
              if (row.rowKey == null) return;
              const gid =
                row.id != null && row.id !== "" ? String(row.id) : "";
              if (gid === target) {
                grid.addRowClassName(row.rowKey, "role-grid-row--selected");
              } else {
                grid.removeRowClassName(row.rowKey, "role-grid-row--selected");
              }
            });
          }

          function tryOpenGroupFromQuery() {
            try {
              var params = new URLSearchParams(window.location.search);
              var gid = params.get("grpId");
              if (!gid) return;
              var gidNum = Number(gid);
              if (isNaN(gidNum)) return;
              showGroupDetailPanel(gidNum);
              markSelectedRow(gidNum);
            } catch (_) {
              // ignore
            }
          }

          function rowGrpId(r) {
            if (!r || r.id == null || r.id === "") return null;
            return String(r.id);
          }

          function collectGrpIdForApi(r) {
            if (!r || r.id == null || r.id === "") return null;
            var n = Number(r.id);
            return isNaN(n) ? null : n;
          }

          let gridData = Array.isArray(projectGroupRows)
            ? projectGroupRows.map(function (r) {
                return (typeof r === "object" && r !== null) ? Object.assign({}, r) : r;
              })
            : [];
          let deleteMode = false;
          let grid = null;
          let toolbarMountElement = null;

          function getToolbarMount() {
            if (!toolbarMountElement) {
              toolbarMountElement = document.createElement("div");
              toolbarMountElement.id = "groupGridToolbarMount";
              toolbarMountElement.className = "role-grid-toolbar";
              toolbarMountElement.setAttribute("role", "toolbar");
              toolbarMountElement.setAttribute("aria-label", "그룹 목록 도구");
            }
            return toolbarMountElement;
          }

          function detachToolbarFromGrid() {
            const host = document.getElementById("groupGrid");
            const mount = getToolbarMount();
            if (host && mount.parentNode === host) {
              host.removeChild(mount);
            }
          }

          function placeToolbarBetweenTableAndPagination() {
            const host = document.getElementById("groupGrid");
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
              var host = document.getElementById("groupGrid");
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
              if (Array.isArray(keys) && keys.length && typeof grid.getRow === "function")
                return keys.map((k) => grid.getRow(k)).filter(Boolean);
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
            let html =
              '<button type="button" class="btn btn-primary" id="groupBtnRegister">등록</button>';
            if (!deleteMode) {
              html +=
                '<button type="button" class="btn btn-danger" id="groupBtnDelete">삭제</button>';
            } else {
              const rows = grid ? getCheckedRowsForRemove() : [];
              const n = rows.length;
              if (n === 0) {
                html +=
                  '<button type="button" class="btn btn-secondary" id="groupBtnDeleteCancel">삭제 취소</button>';
              } else {
                html +=
                  '<button type="button" class="btn btn-danger" id="groupBtnRemove">제거</button>';
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
              if (btn.id === "groupBtnRegister") {
                var pid = Number(prjId);
                if (isNaN(pid)) {
                  void window.GroupQuestionModal.alert("프로젝트 ID가 올바르지 않습니다.", "알림");
                  return;
                }
                location.href = "/project/group/info";
                return;
              }
              if (btn.id === "groupBtnDelete") {
                deleteMode = true;
                recreateGrid();
                return;
              }
              if (btn.id === "groupBtnDeleteCancel") {
                deleteMode = false;
                recreateGrid();
                return;
              }
              if (btn.id === "groupBtnRemove") {
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
              return (r.grpName != null && r.grpName !== "") ? String(r.grpName) : "(그룹명 없음)";
            });
            const msg =
              "선택한 그룹을 삭제하시겠습니까?\n\n" + names.join("\n");
            var confirmed = await window.GroupQuestionModal.confirm(msg, "그룹 삭제");
            if (!confirmed) return;
            const grpIds = rows
              .map(function (r) {
                return collectGrpIdForApi(r);
              })
              .filter(Boolean);
            if (!grpIds.length) {
              await window.GroupQuestionModal.alert( "그룹 ID를 확인할 수 없습니다.", "알림");
              return;
            }
            var pid = Number(prjId);
            if (isNaN(pid)) {
              await window.GroupQuestionModal.alert( "프로젝트 ID가 올바르지 않습니다.", "알림");
              return;
            }
            try {
              var res = await fetch("/project/group/deleteGroups", {
                method: "POST",
                headers: buildCsrfHeaders(),
                credentials: "same-origin",
                body: JSON.stringify({ grpIds: grpIds }),
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
                    : "삭제 중 오류가 발생하였습니다.");
                await window.GroupQuestionModal.alert(errText, "오류");
                return;
              }
            } catch (e) {
              await window.GroupQuestionModal.alert( "삭제 중 오류가 발생하였습니다.", "오류");
              return;
            }
            const labelPart =
              names.length === 1
                ? "「" + names[0] + "」"
                : "「" + names.join("」, 「") + "」";
            const okMsg =
              names.length === 1
                ? labelPart + "이(가) 정상적으로 삭제되었습니다."
                : labelPart + " 그룹이 정상적으로 삭제되었습니다.";
            await window.GroupQuestionModal.alert(okMsg, "알림");

            const removeSet = {};
            rows.forEach(function (r) {
              const k = rowGrpId(r);
              if (k) removeSet[k] = true;
            });
            gridData = gridData.filter(function (r) {
              const k = rowGrpId(r);
              return !k || !removeSet[k];
            });
            if (selectedGrpId && removeSet[selectedGrpId]) {
              clearGroupDetailPanel();
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
            const el = document.getElementById("groupGrid");
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
                  header: "그룹명",
                  name: "grpName",
                  width: 220,
                  align: "center",
                  sortable: true,
                  formatter: ({ row, value }) => {
                    const gid =
                      row.id != null && row.id !== ""
                        ? row.id
                        : null;
                    const text = value == null ? "" : String(value);
                    if (gid == null || !text) {
                      return text || "-";
                    }
                    return (
                      '<button type="button" class="role-name-link" data-grp-id="' +
                      encodeURIComponent(String(gid)) +
                      '">' +
                      text +
                      "</button>"
                    );
                  },
                },
                {
                  header: "멤버 수",
                  name: "cntMem",
                  width: 110,
                  align: "center",
                  sortable: true,
                  formatter: ({ value }) =>
                    value == null || value === "" ? "0" : String(value),
                },
                {
                  header: "그룹 생성일",
                  name: "createdOn",
                  width: 130,
                  align: "center",
                  sortable: true,
                  formatter: ({ value }) => formatDate(value),
                },
              ],
              pageOptions: {
                useClient: true,
                perPage: 15,
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
              if (ev.columnName !== "grpName") return;
              const row = grid.getRow(ev.rowKey);
              const gid =
                row && row.id != null && row.id !== "" ? row.id : null;
              if (gid != null) {
                showGroupDetailPanel(gid);
                markSelectedRow(gid);
              }
            });

            renderToolbar();
            schedulePlaceToolbar();
          }

          recreateGrid();
          tryOpenGroupFromQuery();
        });
