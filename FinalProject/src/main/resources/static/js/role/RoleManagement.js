document.addEventListener("DOMContentLoaded", function () {
          (function roleSearchDates() {
            const form = document.getElementById("roleSearchForm");
            const start = document.getElementById("createdFrom");
            const end = document.getElementById("createdTo");
            if (!form || !start || !end) return;

            const pad = (n) => String(n).padStart(2, "0");
            const todayStr = () => {
              const d = new Date();
              return (
                d.getFullYear() +
                "-" +
                pad(d.getMonth() + 1) +
                "-" +
                pad(d.getDate())
              );
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
              if (sv) {
                end.min = sv;
              } else {
                end.removeAttribute("min");
              }

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
              if (sv && ev && compare(sv, ev) > 0) {
                end.value = sv;
              }
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
              if (sv && ev && compare(ev, sv) < 0) {
                start.value = ev;
              }
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
                await window.RoleQuestionModal.alert(
                  "시작일은 오늘 이후로 지정할 수 없습니다.",
                  "알림"
                );
                return;
              }
              if (ev && compare(ev, today) > 0) {
                e.preventDefault();
                await window.RoleQuestionModal.alert(
                  "종료일은 오늘 이후로 지정할 수 없습니다.",
                  "알림"
                );
                return;
              }
              if (sv && ev && compare(sv, ev) > 0) {
                e.preventDefault();
                await window.RoleQuestionModal.alert(
                  "시작일은 종료일보다 늦을 수 없습니다.",
                  "알림"
                );
                return;
              }
            });
          })();

          function formatDate(value) {
            if (!value) return "";
            const d = new Date(value);
            if (isNaN(d.getTime())) return value;
            const pad = (n) => String(n).padStart(2, "0");
            return (
              d.getFullYear() +
              "-" +
              pad(d.getMonth() + 1) +
              "-" +
              pad(d.getDate())
            );
          }

          function collectRoleCdForApi(r) {
            if (!r) return null;
            var v =
              r.roleCd != null && r.roleCd !== ""
                ? r.roleCd
                : r.id != null && r.id !== ""
                  ? r.id
                  : null;
            if (v == null || v === "") return null;
            var n = Number(v);
            return isNaN(n) ? null : n;
          }

          function buildCsrfHeaders() {
            var headers = { "Content-Type": "application/json" };
            var tokenMeta = document.querySelector('meta[name="_csrf"]');
            var headerMeta = document.querySelector('meta[name="_csrf_header"]');
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

          let gridData = Array.isArray(projectRoleRows)
            ? projectRoleRows.map(function (r) {
                return typeof r === "object" && r !== null
                  ? Object.assign({}, r)
                  : r;
              })
            : [];
          let deleteMode = false;
          let grid = null;
          /** 그리드 destroy 시 같이 지워지지 않도록 보관하는 툴바 루트 */
          let toolbarMountElement = null;

          function getToolbarMount() {
            if (!toolbarMountElement) {
              toolbarMountElement = document.createElement("div");
              toolbarMountElement.id = "roleGridToolbarMount";
              toolbarMountElement.className = "role-grid-toolbar";
              toolbarMountElement.setAttribute("role", "toolbar");
              toolbarMountElement.setAttribute("aria-label", "역할 목록 도구");
            }
            return toolbarMountElement;
          }

          function detachToolbarFromGrid() {
            const host = document.getElementById("menuRoleGrid");
            const mount = getToolbarMount();
            if (host && mount.parentNode === host) {
              host.removeChild(mount);
            }
          }

          function placeToolbarBetweenTableAndPagination() {
            const host = document.getElementById("menuRoleGrid");
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
              var host = document.getElementById("menuRoleGrid");
              var mount = getToolbarMount();
              if (host && mount && !host.contains(mount)) {
                host.appendChild(mount);
              }
            }
            setTimeout(attempt, 0);
          }

          function rowBizId(r) {
            if (!r) return null;
            if (r.roleCd != null && r.roleCd !== "") return String(r.roleCd);
            if (r.id != null && r.id !== "") return String(r.id);
            return null;
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
            let html =
              '<button type="button" class="role-grid-toolbar__btn role-grid-toolbar__btn--register" id="roleBtnRegister">등록</button>';
            if (!deleteMode) {
              html +=
                '<button type="button" class="role-grid-toolbar__btn role-grid-toolbar__btn--delete" id="roleBtnDelete">삭제</button>';
            } else {
              const rows = grid ? getCheckedRowsForRemove() : [];
              const n = rows.length;
              if (n === 0) {
                html +=
                  '<button type="button" class="role-grid-toolbar__btn role-grid-toolbar__btn--cancel-delete" id="roleBtnDeleteCancel">삭제 취소</button>';
              } else {
                html +=
                  '<button type="button" class="role-grid-toolbar__btn role-grid-toolbar__btn--remove" id="roleBtnRemove">제거</button>';
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
              if (btn.id === "roleBtnRegister") {
                location.href =
                  "/project/role/info";
                return;
              }
              if (btn.id === "roleBtnDelete") {
                deleteMode = true;
                recreateGrid();
                return;
              }
              if (btn.id === "roleBtnDeleteCancel") {
                deleteMode = false;
                recreateGrid();
                return;
              }
              if (btn.id === "roleBtnRemove") {
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
              return r.roleName != null && r.roleName !== ""
                ? String(r.roleName)
                : "(역할명 없음)";
            });
            const msg =
              "선택한 역할을 삭제하시겠습니까?\n\n" + names.join("\n");
            var confirmed = await window.RoleQuestionModal.confirm(
              msg,
              "역할 삭제"
            );
            if (!confirmed) {
              return;
            }
            const roleCds = rows
              .map(function (r) {
                return collectRoleCdForApi(r);
              })
              .filter(function (n) {
                return n != null;
              });
            if (!roleCds.length) {
              await window.RoleQuestionModal.alert(
                "역할 코드를 확인할 수 없습니다.",
                "알림"
              );
              return;
            }
            var pid = Number(prjId);
            if (isNaN(pid)) {
              await window.RoleQuestionModal.alert(
                "프로젝트 ID가 올바르지 않습니다.",
                "알림"
              );
              return;
            }
            try {
              var res = await fetch(
                "/project/role/deleteRoles",
                {
                  method: "POST",
                  headers: buildCsrfHeaders(),
                  credentials: "same-origin",
                  body: JSON.stringify({ roleCds: roleCds }),
                }
              );
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
                await window.RoleQuestionModal.alert(errText, "오류");
                return;
              }
            } catch (e) {
              await window.RoleQuestionModal.alert(
                "제거 중 오류가 발생하였습니다.",
                "오류"
              );
              return;
            }
            const labelPart =
              names.length === 1
                ? "「" + names[0] + "」"
                : "「" + names.join("」, 「") + "」";
            const okMsg =
              names.length === 1
                ? labelPart + "이(가) 정상적으로 제거되었습니다."
                : labelPart + " 역할이 정상적으로 제거되었습니다.";
            await window.RoleQuestionModal.alert(okMsg, "알림");

            const removeSet = {};
            rows.forEach(function (r) {
              const id = rowBizId(r);
              if (id) removeSet[id] = true;
            });
            gridData = gridData.filter(function (r) {
              const id = rowBizId(r);
              return !id || !removeSet[id];
            });
            deleteMode = false;
            recreateGrid();
          }

          function recreateGrid() {
            detachToolbarFromGrid();
            if (grid) {
              grid.destroy();
              grid = null;
            }
            const el = document.getElementById("menuRoleGrid");
            if (!el) return;

            bindToolbarMountOnce();

            const rowHeaders = deleteMode
              ? [{ type: "checkbox", header: "선택" }]
              : [{ type: "rowNum", header: "번호" }];

            grid = new tui.Grid({
              el: el,
              data: gridData,
              rowHeaders: rowHeaders,
              scrollX: false,
              scrollY: false,
              bodyHeight: "auto",
              rowHeight: 36,
              minBodyHeight: 100,
              columns: [
                {
                  header: "역할코드",
                  name: "roleCd",
                  width: 110,
                  align: "center",
                  sortable: true,
                  formatter: ({ value }) =>
                    value == null || value === "" ? "" : String(value),
                },
                {
                  header: "역할명",
                  name: "roleName",
                  width: 220,
                  align: "center",
                  sortable: true,
                  formatter: ({ row, value }) => {
                    const rc =
                      row.roleCd != null && row.roleCd !== ""
                        ? row.roleCd
                        : row.id;
                    const text = value == null ? "" : value;
                    const q =
                      "roleCd=" +
                      encodeURIComponent(rc == null ? "" : String(rc));
                    return (
                      '<a href="/project/role/info?' +
                      q +
                      '">' +
                      text +
                      "</a>"
                    );
                  },
                },
                {
                  header: "보유 그룹 수",
                  name: "grpCnt",
                  width: 120,
                  align: "center",
                  sortable: true,
                },
                {
                  header: "권한 생성일",
                  name: "createdOn",
                  width: 120,
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
              if (deleteMode) {
                return;
              }
              if (ev.columnName === "roleName" || ev.rowKey == null) return;
              const row = grid.getRow(ev.rowKey);
              const rc =
                row && row.roleCd != null && row.roleCd !== ""
                  ? row.roleCd
                  : row && row.id;
              if (rc != null && rc !== "") {
                location.href =
                  "/project/role/info?roleCd=" +
                  encodeURIComponent(String(rc));
              }
            });

            renderToolbar();
            schedulePlaceToolbar();
          }

          recreateGrid();
        });
