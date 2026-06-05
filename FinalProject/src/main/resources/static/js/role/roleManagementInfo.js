window.RolePermToggle = (function () {
          var SECTION_OFF = "전체 선택";
          var SECTION_ON = "전체 해제";
          var GLOBAL_OFF = "전체 구역 선택";
          var GLOBAL_ON = "전체 구역 해제";

          function getSectionCheckboxes(section) {
            var list = [];
            section.querySelectorAll(".perm-menu-cb").forEach(function (cb) {
              if (!cb.disabled) {
                list.push(cb);
              }
            });
            return list;
          }

          function isSectionFullySelected(section) {
            var cbs = getSectionCheckboxes(section);
            if (!cbs.length) {
              return false;
            }
            return cbs.every(function (cb) {
              return cb.checked;
            });
          }

          function setToggleBtnLabel(btn, allOn, offText, onText) {
            if (!btn) {
              return;
            }
            btn.textContent = allOn ? onText : offText;
            btn.setAttribute("aria-pressed", allOn ? "true" : "false");
          }

          function updateSectionToggleBtn(section) {
            var btn = section.querySelector(".perm-toggle-btn--section");
            var allOn = isSectionFullySelected(section);
            setToggleBtnLabel(btn, allOn, SECTION_OFF, SECTION_ON);
          }

          function updateGlobalToggleBtn() {
            var globalBtn = document.getElementById("permToggleGlobal");
            if (!globalBtn) {
              return;
            }
            var sections = document.querySelectorAll(".perm-sub");
            var allOn = sections.length > 0;
            sections.forEach(function (section) {
              if (!isSectionFullySelected(section)) {
                allOn = false;
              }
            });
            if (!sections.length) {
              allOn = false;
            }
            setToggleBtnLabel(globalBtn, allOn, GLOBAL_OFF, GLOBAL_ON);
          }

          function setSectionAll(section, checked) {
            getSectionCheckboxes(section).forEach(function (cb) {
              cb.checked = checked;
            });
            updateSectionToggleBtn(section);
          }

          function toggleSection(section) {
            setSectionAll(section, !isSectionFullySelected(section));
            updateGlobalToggleBtn();
          }

          function toggleAllSections() {
            var sections = document.querySelectorAll(".perm-sub");
            var allOn = sections.length > 0;
            sections.forEach(function (section) {
              if (!isSectionFullySelected(section)) {
                allOn = false;
              }
            });
            if (!sections.length) {
              allOn = false;
            }
            var next = !allOn;
            sections.forEach(function (section) {
              setSectionAll(section, next);
            });
            updateGlobalToggleBtn();
          }

          function refreshAllToggleBtns() {
            document.querySelectorAll(".perm-sub").forEach(function (section) {
              updateSectionToggleBtn(section);
            });
            updateGlobalToggleBtn();
          }

          return {
            init: function (opts) {
              opts = opts || {};
              var canInteract =
                opts.canInteract ||
                function () {
                  return true;
                };

              document
                .querySelectorAll(".perm-toggle-btn--section")
                .forEach(function (btn) {
                  btn.addEventListener("click", function () {
                    if (!canInteract() || btn.disabled) {
                      return;
                    }
                    var section = btn.closest(".perm-sub");
                    if (section) {
                      toggleSection(section);
                    }
                  });
                });

              var globalBtn = document.getElementById("permToggleGlobal");
              if (globalBtn) {
                globalBtn.addEventListener("click", function () {
                  if (!canInteract() || globalBtn.disabled) {
                    return;
                  }
                  toggleAllSections();
                });
              }

              document.querySelectorAll(".perm-menu-cb").forEach(function (cb) {
                cb.addEventListener("change", function () {
                  if (!canInteract()) {
                    return;
                  }
                  var section = cb.closest(".perm-sub");
                  if (section) {
                    updateSectionToggleBtn(section);
                  }
                  updateGlobalToggleBtn();
                });
              });

              refreshAllToggleBtns();
            },
            setToggleButtonsEnabled: function (enabled) {
              document.querySelectorAll(".perm-toggle-btn").forEach(function (btn) {
                btn.disabled = !enabled;
              });
            },
            setToggleButtonsVisible: function (visible) {
              document.querySelectorAll(".perm-toggle-btn").forEach(function (btn) {
                btn.classList.toggle("is-hidden", !visible);
              });
            },
            refreshAllToggleBtns: refreshAllToggleBtns,
          };
        })();

document.addEventListener("DOMContentLoaded", function () {
          var cfg = window.roleManagementInfoPageConfig || {};
          if (!cfg.registerMode) return;
          var prjId = cfg.prjId;
          var listUrl =
            "/project/role/list";

          function confirmModal(message, title) {
            if (window.RoleQuestionModal && window.RoleQuestionModal.confirm) {
              return window.RoleQuestionModal.confirm(message, title || "확인");
            }
            return Promise.resolve(false);
          }

          function alertModal(message, title) {
            if (window.RoleQuestionModal && window.RoleQuestionModal.alert) {
              return window.RoleQuestionModal.alert(message, title || "알림");
            }
            return Promise.resolve();
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

          function collectMenuRoleIds() {
            var ids = [];
            document
              .querySelectorAll(".perm-menu-cb:checked[data-role-id]")
              .forEach(function (cb) {
                var id = (cb.getAttribute("data-role-id") || "").trim();
                if (id && ids.indexOf(id) === -1) {
                  ids.push(id);
                }
              });
            return ids;
          }

          function collectRegisterGrpIds() {
            var ref = window.__rolePendingGroupsRef;
            if (!ref || !ref.length) return [];
            var ids = [];
            ref.forEach(function (g) {
              var id = g && g.id != null ? Number(g.id) : NaN;
              if (!isNaN(id) && ids.indexOf(id) === -1) ids.push(id);
            });
            return ids;
          }

          if (window.RolePermToggle) {
            window.RolePermToggle.init({
              canInteract: function () {
                return true;
              },
            });
            window.RolePermToggle.setToggleButtonsEnabled(true);
          }

          var cancelBtn = document.getElementById("btnRoleCancel");
          if (cancelBtn) {
            cancelBtn.addEventListener("click", async function () {
              var ok = await confirmModal(
                "입력한 내용을 저장하지 않고 목록으로 돌아가시겠습니까?",
                "취소 확인",
              );
              if (ok) window.location.href = listUrl;
            });
          }

          var saveBtn = document.getElementById("btnRoleSave");
          if (saveBtn) {
            saveBtn.addEventListener("click", async function () {
              var nameEl = document.getElementById("detailRoleName");
              var name = nameEl ? nameEl.value.trim() : "";
              if (!name) {
                await alertModal("권한명을 입력하세요.", "알림");
                return;
              }

              var pid = Number(prjId);
              if (isNaN(pid)) {
                await alertModal("프로젝트 ID가 올바르지 않습니다.", "알림");
                return;
              }

              var menuRoleIds = collectMenuRoleIds();
              saveBtn.disabled = true;
              try {
                var res = await fetch("/project/role/registerRole", {
                  method: "POST",
                  headers: buildCsrfHeaders(),
                  credentials: "same-origin",
                body: JSON.stringify({
                  roleName: name,
                  menuRoleIds: menuRoleIds,
                  grpIds: collectRegisterGrpIds(),
                }),
                });
                var data = await res.json().catch(function () {
                  return {};
                });
                var status =
                  data && data.resultStatus ? String(data.resultStatus) : "";
                var msg =
                  data && data.resultMessage
                    ? String(data.resultMessage)
                    : data && data.message
                      ? String(data.message)
                      : "권한 등록 처리 중 오류가 발생했습니다.";
                var isOk = status.toUpperCase() === "OK";

                await alertModal(
                  msg,
                  isOk ? "권한 등록 완료" : "권한 등록 실패",
                );

                if (isOk) {
                  window.location.href = listUrl;
                }
              } catch (e) {
                await alertModal(
                  "권한 등록 중 오류가 발생했습니다.",
                  "권한 등록 실패",
                );
              } finally {
                saveBtn.disabled = false;
              }
            });
          }
        });

document.addEventListener("DOMContentLoaded", function () {
          var cfg = window.roleManagementInfoPageConfig || {};
          var isRegisterMode = !!cfg.registerMode;
          var prjId = cfg.prjId;
          var roleCd = cfg.roleCd;
          var roleEditMode = false;
          var editBtn = document.getElementById("btnRoleEdit");
          var cancelEditBtn = document.getElementById("btnRoleCancelEdit");
          var groupToolbar = document.getElementById("roleGroupToolbar");
          var manageGroupsBtn = document.getElementById("btnManageRoleGroups");
          var pickOverlay = document.getElementById("roleGroupPickOverlay");
          var pickGridEl = document.getElementById("roleGroupPickGrid");
          var pickSearchType = document.getElementById("roleGroupSearchType");
          var pickSearchKeyword = document.getElementById(
            "roleGroupSearchKeyword",
          );
          var pickCreatedFrom = document.getElementById("roleGroupCreatedFrom");
          var pickCreatedTo = document.getElementById("roleGroupCreatedTo");
          var pickCancel = document.getElementById("btnRoleGroupPickCancel");
          var pickOk = document.getElementById("btnRoleGroupPickOk");
          var editLabel =
            editBtn && editBtn.getAttribute("data-label-edit")
              ? editBtn.getAttribute("data-label-edit")
              : "수정";
          var saveLabel =
            editBtn && editBtn.getAttribute("data-label-save")
              ? editBtn.getAttribute("data-label-save")
              : "저장";
          var pid = String(prjId);
          var rc =
            roleCd != null &&
            roleCd !== "" &&
            !isNaN(Number(roleCd))
              ? String(roleCd)
              : "";

          function setPermCheckboxesEditable(editable) {
            document.querySelectorAll(".perm-menu-cb").forEach(function (cb) {
              cb.disabled = !editable;
            });
          }

          if (!isRegisterMode && window.RolePermToggle) {
            window.RolePermToggle.init({
              canInteract: function () {
                return roleEditMode;
              },
            });
            window.RolePermToggle.setToggleButtonsEnabled(false);
          }

          function enterRoleEditMode() {
            roleEditMode = true;
            setPermCheckboxesEditable(true);
            if (window.RolePermToggle) {
              window.RolePermToggle.setToggleButtonsVisible(true);
              window.RolePermToggle.setToggleButtonsEnabled(true);
              window.RolePermToggle.refreshAllToggleBtns();
            }
            if (editBtn) {
              editBtn.textContent = saveLabel;
            }
            if (cancelEditBtn) {
              cancelEditBtn.classList.remove("is-hidden");
            }
            setRoleGroupToolbarVisible(true);
            createRoleGroupsGrid();
          }

          function setRoleGroupToolbarVisible(visible) {
            if (!groupToolbar) return;
            if (isRegisterMode) {
              if (visible) {
                groupToolbar.classList.remove("is-hidden");
              }
              return;
            }
            groupToolbar.classList.toggle("is-hidden", !visible);
          }

          function confirmModal(message, title) {
            if (window.RoleQuestionModal && window.RoleQuestionModal.confirm) {
              return window.RoleQuestionModal.confirm(message, title || "확인");
            }
            return Promise.resolve(false);
          }

          function alertModal(message, title) {
            if (window.RoleQuestionModal && window.RoleQuestionModal.alert) {
              return window.RoleQuestionModal.alert(message, title || "알림");
            }
            return Promise.resolve();
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

          function collectMenuRoleIds() {
            var ids = [];
            document
              .querySelectorAll(".perm-menu-cb:checked[data-role-id]")
              .forEach(function (cb) {
                var id = (cb.getAttribute("data-role-id") || "").trim();
                if (id && ids.indexOf(id) === -1) {
                  ids.push(id);
                }
              });
            return ids;
          }

          function collectPendingGrpIds() {
            var ids = [];
            pendingGroups.forEach(function (g) {
              var id = g && g.id != null ? Number(g.id) : NaN;
              if (!isNaN(id) && ids.indexOf(id) === -1) {
                ids.push(id);
              }
            });
            return ids;
          }

          async function saveRoleMenus() {
            var pidNum = Number(prjId);
            var roleCdNum = rc ? Number(rc) : NaN;
            if (isNaN(pidNum) || isNaN(roleCdNum)) {
              await alertModal("프로젝트 또는 권한 코드가 올바르지 않습니다.", "알림");
              return;
            }

            var menuRoleIds = collectMenuRoleIds();
            var grpIds = collectPendingGrpIds();
            if (editBtn) {
              editBtn.disabled = true;
            }
            try {
              var res = await fetch("/project/role/updateRole", {
                method: "POST",
                headers: buildCsrfHeaders(),
                credentials: "same-origin",
                body: JSON.stringify({
                  roleCd: roleCdNum,
                  menuRoleIds: menuRoleIds,
                  grpIds: grpIds,
                }),
              });
              var data = await res.json().catch(function () {
                return {};
              });
              var status =
                data && data.resultStatus ? String(data.resultStatus) : "";
              var msg =
                data && data.resultMessage
                  ? String(data.resultMessage)
                  : data && data.message
                    ? String(data.message)
                    : "권한 수정 처리 중 오류가 발생했습니다.";
              var isOk = status.toUpperCase() === "OK";

              await alertModal(msg, isOk ? "수정 완료" : "수정 실패");

              if (isOk) {
                window.location.reload();
              }
            } catch (e) {
              await alertModal(
                "권한 수정 중 오류가 발생했습니다.",
                "수정 실패",
              );
            } finally {
              if (editBtn) {
                editBtn.disabled = false;
              }
            }
          }

          if (editBtn) {
            editBtn.addEventListener("click", async function () {
              if (!roleEditMode) {
                enterRoleEditMode();
                return;
              }
              await saveRoleMenus();
            });
          }

          if (cancelEditBtn) {
            cancelEditBtn.addEventListener("click", async function () {
              var ok = await confirmModal(
                "변경 내용을 저장하지 않고 되돌리시겠습니까?",
                "취소 확인",
              );
              if (ok) {
                window.location.reload();
              }
            });
          }

          var host = document.getElementById("roleGroupsGrid");
          var pendingGroups = [];
          window.__rolePendingGroupsRef = pendingGroups;
          var grpRoleCount = 0;
          var roleRevokeInProgress = false;
          var roleGroupsGrid = null;
          var allPickGroups = [];
          var pickGrid = null;
          var isSyncingPickGrid = false;
          var modalCheckedGrpIds = new Set();

          if (!host || typeof tui === "undefined" || !tui.Grid) {
            return;
          }

          if (!host.getAttribute("data-prj-id")) {
            host.setAttribute("data-prj-id", pid);
          }

          function formatDate(value) {
            if (value == null || value === "") return "";
            var d = new Date(value);
            if (isNaN(d.getTime())) return String(value);
            var pad = function (n) {
              return String(n).padStart(2, "0");
            };
            return (
              d.getFullYear() +
              "-" +
              pad(d.getMonth() + 1) +
              "-" +
              pad(d.getDate())
            );
          }

          function pendingToGridRows() {
            return pendingGroups.map(function (g) {
              return {
                id: g.id,
                grpName: g.grpName || "",
                grpMemberCnt:
                  g.grpMemberCnt != null ? Number(g.grpMemberCnt) : 0,
                createdOn: g.createdOn,
              };
            });
          }

          function initPendingFromRows(rows) {
            pendingGroups.length = 0;
            (rows || []).forEach(function (row) {
              if (row == null || row.id == null) return;
              pendingGroups.push({
                id: Number(row.id),
                grpName: row.grpName || "",
                grpMemberCnt:
                  row.grpMemberCnt != null ? Number(row.grpMemberCnt) : 0,
                createdOn: row.createdOn,
              });
            });
            grpRoleCount = pendingGroups.length;
          }

          function rowToPendingGroup(row) {
            return {
              id: Number(row.id),
              grpName: row.grpName || "",
              grpMemberCnt:
                row.cntMem != null
                  ? Number(row.cntMem)
                  : row.grpMemberCnt != null
                    ? Number(row.grpMemberCnt)
                    : 0,
              createdOn: row.createdOn,
            };
          }

          function getRoleGroupsColumns(showRevoke) {
            var cols = [
              {
                header: "그룹아이디",
                name: "id",
                width: 90,
                align: "center",
                sortable: true,
              },
              {
                header: "그룹명",
                name: "grpName",
                align: "center",
                sortable: true,
                escapeHTML: false,
                formatter: function (ctx) {
                  var row = ctx.row;
                  var text =
                    ctx.value == null || ctx.value === ""
                      ? ""
                      : String(ctx.value);
                  var gid =
                    row && row.id != null && row.id !== "" ? row.id : null;
                  if (gid == null || !text) {
                    return text || "-";
                  }
                  var q = "grpId=" + encodeURIComponent(String(gid));
                  return (
                    '<a href="/project/group/info?' +
                    q +
                    '">' +
                    text +
                    "</a>"
                  );
                },
              },
              {
                header: "그룹인원",
                name: "grpMemberCnt",
                width: 90,
                align: "center",
                sortable: true,
                formatter: function (ctx) {
                  var v = ctx.value;
                  return v == null ? "0" : String(v);
                },
              },
              {
                header: "그룹 생성일",
                name: "createdOn",
                width: 120,
                align: "center",
                sortable: true,
                formatter: function (ctx) {
                  return formatDate(ctx.value);
                },
              },
            ];
            if (showRevoke) {
              cols.push({
                header: "권한 회수",
                name: "revoke",
                width: 100,
                align: "center",
                sortable: false,
                formatter: function () {
                  return '<button type="button" class="btn-revoke">회수</button>';
                },
              });
            }
            return cols;
          }

          function createRoleGroupsGrid() {
            if (!host) return;
            if (roleGroupsGrid) {
              roleGroupsGrid.destroy();
              roleGroupsGrid = null;
            }
            roleGroupsGrid = new tui.Grid({
              el: host,
              data: pendingToGridRows(),
              rowHeaders: ["rowNum"],
              scrollX: false,
              scrollY: false,
              bodyHeight: "auto",
              rowHeight: 36,
              minBodyHeight: 100,
              columns: getRoleGroupsColumns(!roleEditMode && !isRegisterMode),
              pageOptions: {
                useClient: true,
                perPage: 10,
              },
            });
            roleGroupsGrid.on("click", onRoleGroupsGridClick);
          }

          function refreshRoleGroupsTable() {
            if (!host) return;
            if (roleGroupsGrid && typeof roleGroupsGrid.resetData === "function") {
              roleGroupsGrid.resetData(pendingToGridRows());
              return;
            }
            createRoleGroupsGrid();
          }

          function reloadRoleGroupsFromServer() {
            if (isRegisterMode) {
              return Promise.resolve();
            }
            if (!pid || !rc) return Promise.resolve();
            var url =
              "/project/role/roleGroups?roleCd=" + encodeURIComponent(rc);
            return fetch(url, { credentials: "same-origin" })
              .then(function (r) {
                return r.json();
              })
              .then(function (body) {
                var rows = body && body.content ? body.content : [];
                grpRoleCount =
                  body && body.grpRoleCount != null
                    ? Number(body.grpRoleCount)
                    : rows.length;
                initPendingFromRows(rows);
                refreshRoleGroupsTable();
              });
          }

          function parseRowDateYmd(value) {
            if (value == null || value === "") return "";
            if (Array.isArray(value) && value.length >= 3) {
              var y = value[0];
              var m = String(value[1]).padStart(2, "0");
              var d = String(value[2]).padStart(2, "0");
              return y + "-" + m + "-" + d;
            }
            var dt = new Date(value);
            if (isNaN(dt.getTime())) {
              var s = String(value);
              return s.length >= 10 ? s.substring(0, 10) : s;
            }
            var pad = function (n) {
              return String(n).padStart(2, "0");
            };
            return (
              dt.getFullYear() +
              "-" +
              pad(dt.getMonth() + 1) +
              "-" +
              pad(dt.getDate())
            );
          }

          function includesKeyword(val, keyword) {
            if (val == null) return false;
            return String(val).toLowerCase().includes(keyword);
          }

          function initModalDraftFromPending() {
            modalCheckedGrpIds = new Set(
              pendingGroups
                .map(function (g) {
                  return Number(g.id);
                })
                .filter(function (id) {
                  return !isNaN(id);
                }),
            );
          }

          function isModalDraftChecked(grpId) {
            return modalCheckedGrpIds.has(Number(grpId));
          }

          function filterPickGroups(rows) {
            var type = pickSearchType ? pickSearchType.value : "grpName";
            var keyword = pickSearchKeyword
              ? pickSearchKeyword.value.trim().toLowerCase()
              : "";
            var from = pickCreatedFrom ? pickCreatedFrom.value : "";
            var to = pickCreatedTo ? pickCreatedTo.value : "";
            return rows.filter(function (r) {
              if (keyword) {
                if (type === "id") {
                  if (!includesKeyword(r.id, keyword)) return false;
                } else if (!includesKeyword(r.grpName, keyword)) {
                  return false;
                }
              }
              if (from || to) {
                var ymd = parseRowDateYmd(r.createdOn);
                if (!ymd) return false;
                if (from && ymd < from) return false;
                if (to && ymd > to) return false;
              }
              return true;
            });
          }

          function getPickGridRows() {
            return filterPickGroups(
              allPickGroups.filter(function (r) {
                return r.id != null;
              }),
            );
          }

          function resetPickSearch() {
            if (pickSearchType) pickSearchType.value = "grpName";
            if (pickSearchKeyword) pickSearchKeyword.value = "";
            if (pickCreatedFrom) pickCreatedFrom.value = "";
            if (pickCreatedTo) pickCreatedTo.value = "";
          }

          function applyPickGridChecks() {
            if (!pickGrid || typeof pickGrid.getData !== "function") return;
            isSyncingPickGrid = true;
            try {
              pickGrid.getData().forEach(function (row) {
                if (row.rowKey == null) return;
                if (isModalDraftChecked(row.id)) {
                  pickGrid.check(row.rowKey);
                } else if (typeof pickGrid.uncheck === "function") {
                  pickGrid.uncheck(row.rowKey);
                }
              });
            } finally {
              isSyncingPickGrid = false;
            }
          }

          function bindPickGridSyncEvents() {
            if (!pickGrid) return;
            pickGrid.on("check", function (ev) {
              if (isSyncingPickGrid) return;
              var row = pickGrid.getRow(ev.rowKey);
              if (row && row.id != null) {
                modalCheckedGrpIds.add(Number(row.id));
              }
            });
            pickGrid.on("uncheck", function (ev) {
              if (isSyncingPickGrid) return;
              var row = pickGrid.getRow(ev.rowKey);
              if (row && row.id != null) {
                modalCheckedGrpIds.delete(Number(row.id));
              }
            });
            pickGrid.on("checkAll", function () {
              if (isSyncingPickGrid) return;
              pickGrid.getData().forEach(function (row) {
                if (row.id != null) {
                  modalCheckedGrpIds.add(Number(row.id));
                }
              });
            });
            pickGrid.on("uncheckAll", function () {
              if (isSyncingPickGrid) return;
              pickGrid.getData().forEach(function (row) {
                if (row.id != null) {
                  modalCheckedGrpIds.delete(Number(row.id));
                }
              });
            });
          }

          function recreatePickGrid() {
            if (!pickGridEl || typeof tui === "undefined" || !tui.Grid) {
              return;
            }
            if (pickGrid) {
              pickGrid.destroy();
              pickGrid = null;
            }
            pickGrid = new tui.Grid({
              el: pickGridEl,
              data: getPickGridRows(),
              rowHeaders: [{ type: "checkbox", header: "선택" }],
              scrollX: false,
              scrollY: true,
              bodyHeight: 280,
              rowHeight: 36,
              minBodyHeight: 100,
              columns: [
                {
                  header: "그룹아이디",
                  name: "id",
                  width: 90,
                  align: "center",
                  sortable: true,
                },
                {
                  header: "그룹명",
                  name: "grpName",
                  align: "center",
                  sortable: true,
                },
                {
                  header: "그룹 생성일",
                  name: "createdOn",
                  width: 120,
                  align: "center",
                  sortable: true,
                  formatter: function (ctx) {
                    return formatDate(ctx.value);
                  },
                },
              ],
              pageOptions: {
                useClient: true,
                perPage: 8,
              },
            });
            bindPickGridSyncEvents();
            applyPickGridChecks();
          }

          function commitModalSelection() {
            pendingGroups.length = 0;
            allPickGroups.forEach(function (row) {
              var gid = Number(row.id);
              if (!isNaN(gid) && modalCheckedGrpIds.has(gid)) {
                pendingGroups.push(rowToPendingGroup(row));
              }
            });
            grpRoleCount = pendingGroups.length;
            refreshRoleGroupsTable();
          }

          async function openGroupPicker() {
            if (!isRegisterMode && !roleEditMode) return;
            var pidNum = Number(prjId);
            if (isNaN(pidNum) || !pickOverlay || !pickGridEl) return;
            resetPickSearch();
            if (pickGrid) {
              pickGrid.destroy();
              pickGrid = null;
            }
            pickGridEl.innerHTML =
              '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">불러오는 중…</p>';
            pickOverlay.classList.add("is-open");
            pickOverlay.setAttribute("aria-hidden", "false");
            try {
              var res = await fetch("/project/role/roleGroupPickList", {
                credentials: "same-origin",
              });
              var data = await res.json().catch(function () {
                return {};
              });
              allPickGroups =
                data && Array.isArray(data.content) ? data.content : [];
              initModalDraftFromPending();
              pickGridEl.innerHTML = "";
              if (!allPickGroups.length) {
                pickGridEl.innerHTML =
                  '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">선택할 그룹이 없습니다.</p>';
                return;
              }
              recreatePickGrid();
            } catch (e) {
              allPickGroups = [];
              pickGridEl.innerHTML =
                '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">그룹 목록을 불러오지 못했습니다.</p>';
            }
          }

          function closeGroupPicker() {
            if (!pickOverlay) return;
            if (pickGrid) {
              pickGrid.destroy();
              pickGrid = null;
            }
            pickOverlay.classList.remove("is-open");
            pickOverlay.setAttribute("aria-hidden", "true");
          }

          if (manageGroupsBtn) {
            manageGroupsBtn.addEventListener("click", openGroupPicker);
          }
          if (pickCancel) {
            pickCancel.addEventListener("click", closeGroupPicker);
          }
          if (pickOverlay) {
            pickOverlay.addEventListener("click", function (e) {
              if (e.target === pickOverlay) {
                closeGroupPicker();
              }
            });
          }
          if (pickOk) {
            pickOk.addEventListener("click", function () {
              commitModalSelection();
              closeGroupPicker();
            });
          }
          if (pickSearchType) {
            pickSearchType.addEventListener("change", function () {
              if (!pickOverlay || !pickOverlay.classList.contains("is-open")) {
                return;
              }
              recreatePickGrid();
            });
          }
          if (pickSearchKeyword) {
            pickSearchKeyword.addEventListener("input", function () {
              if (!pickOverlay || !pickOverlay.classList.contains("is-open")) {
                return;
              }
              recreatePickGrid();
            });
          }
          if (pickCreatedFrom) {
            pickCreatedFrom.addEventListener("change", function () {
              if (!pickOverlay || !pickOverlay.classList.contains("is-open")) {
                return;
              }
              recreatePickGrid();
            });
          }
          if (pickCreatedTo) {
            pickCreatedTo.addEventListener("change", function () {
              if (!pickOverlay || !pickOverlay.classList.contains("is-open")) {
                return;
              }
              recreatePickGrid();
            });
          }

          async function onRoleGroupsGridClick(ev) {
            if (isRegisterMode) return;
            if (roleEditMode) return;
            if (!roleGroupsGrid) return;
            if (roleRevokeInProgress) return;
            if (ev.columnName === "grpName") {
              var navRow = roleGroupsGrid.getRow(ev.rowKey);
              var navGid =
                navRow && navRow.id != null && navRow.id !== ""
                  ? navRow.id
                  : null;
              if (navGid != null) {
                location.href =
                  "/project/group/info?grpId=" +
                  encodeURIComponent(String(navGid));
              }
              return;
            }
            if (ev.columnName !== "revoke") return;
            if (ev.nativeEvent && ev.nativeEvent.stopPropagation) {
              ev.nativeEvent.stopPropagation();
            }
            var row = roleGroupsGrid.getRow(ev.rowKey);
            if (!row || row.id == null) return;

            roleRevokeInProgress = true;
            var grpId = Number(row.id);
            var apiRoleCd = Number(rc);
            var grpName = row.grpName || "해당 그룹";
            if (isNaN(grpId) || isNaN(apiRoleCd)) {
              roleRevokeInProgress = false;
              return;
            }

            try {
              var firstOk = await confirmModal(
                '"' +
                  grpName +
                  '" 그룹에서 이 권한을 회수하시겠습니까?',
                "권한 회수 확인",
              );
              if (!firstOk) return;

              var deleteRoleIfUnused = false;
              if (grpRoleCount <= 1) {
                deleteRoleIfUnused = await confirmModal(
                  "이 권한을 사용하는 그룹이 더 이상 없습니다.\n권한 자체를 삭제하시겠습니까?",
                  "권한 삭제 확인",
                );
              }

              var res = await fetch("/project/role/revokeRoleFromGroup", {
                method: "POST",
                headers: buildCsrfHeaders(),
                credentials: "same-origin",
                body: JSON.stringify({
                  roleCd: apiRoleCd,
                  grpId: grpId,
                  deleteRoleIfUnused: deleteRoleIfUnused,
                }),
              });
              var data = await res.json().catch(function () {
                return {};
              });
              var status =
                data && data.resultStatus ? String(data.resultStatus) : "";
              var msg =
                data && data.resultMessage
                  ? String(data.resultMessage)
                  : data && data.message
                    ? String(data.message)
                    : "권한 회수 처리 중 오류가 발생했습니다.";
              var isOk = status.toUpperCase() === "OK";

              await alertModal(
                msg,
                isOk ? "권한 회수 완료" : "권한 회수 실패",
              );

              if (isOk) {
                if (deleteRoleIfUnused && grpRoleCount <= 1) {
                  window.location.href = "/project/role/list";
                  return;
                }
                await reloadRoleGroupsFromServer();
              }
            } catch (e) {
              await alertModal(
                "권한 회수 중 오류가 발생했습니다.",
                "권한 회수 실패",
              );
            } finally {
              roleRevokeInProgress = false;
            }
          }

          if (!pid || (!isRegisterMode && !rc)) {
            host.innerHTML =
              '<p class="perm-empty" style="padding:1rem">프로젝트 또는 권한 코드가 없어 그룹을 조회할 수 없습니다.</p>';
            return;
          }

          if (isRegisterMode) {
            grpRoleCount = 0;
            setRoleGroupToolbarVisible(true);
            createRoleGroupsGrid();
          } else {
            reloadRoleGroupsFromServer()
              .then(function () {
                createRoleGroupsGrid();
              })
              .catch(function () {
                host.innerHTML =
                  '<p class="perm-empty" style="padding:1rem">그룹 목록을 불러오지 못했습니다.</p>';
              });
          }
        });
