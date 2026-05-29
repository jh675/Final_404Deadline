(function () {
          const cfg = window.groupManagementInfoPageConfig || {};
          const prjId = cfg.prjId;
          const registerMode = cfg.registerMode;
          const grpId = cfg.grpId;
          const serverMembers = cfg.serverMembers || [];
          const serverRoles = cfg.serverRoles || [];
          let groupEditMode = registerMode;
          const grpNameInput = document.getElementById("detailGrpName");
          const editBtn = document.getElementById("btnGroupEdit");
          const saveBtn = document.getElementById("btnGroupSave");
          const cancelBtn = document.getElementById("btnGroupCancel");
          const addMemberBtn = document.getElementById("btnAddMember");
          const manageRolesBtn = document.getElementById("btnManageRoles");
          const memberToolbar = document.getElementById("grpMemberToolbar");
          const roleToolbar = document.getElementById("grpRoleToolbar");
          const membersGridEl = document.getElementById("grpMembersGrid");
          const rolesGridEl = document.getElementById("grpRolesGrid");
          const pickOverlay = document.getElementById("grpMemberPickOverlay");
          const pickGridEl = document.getElementById("grpMemberPickGrid");
          const pickSearchType = document.getElementById("grpMemberSearchType");
          const pickSearchKeyword = document.getElementById("grpMemberSearchKeyword");
          const pickCancel = document.getElementById("btnMemberPickCancel");
          const pickOk = document.getElementById("btnMemberPickOk");
          const roleMenusOverlay = document.getElementById("grpRoleMenusOverlay");
          const btnGrpRoleMenusClose = document.getElementById("btnGrpRoleMenusClose");
          const grpRoleMenusTable = document.getElementById("grpRoleMenusTable");
          const grpRoleMenusTableBody = document.getElementById("grpRoleMenusTableBody");
          const grpRoleMenusEmpty = document.getElementById("grpRoleMenusEmpty");
          const grpRoleMenusLoading = document.getElementById("grpRoleMenusLoading");
          const grpRoleMenusRoleCaption = document.getElementById("grpRoleMenusRoleCaption");
          const rolePickOverlay = document.getElementById("grpRolePickOverlay");
          const rolePickGridEl = document.getElementById("grpRolePickGrid");
          const rolePickSearchType = document.getElementById("grpRoleSearchType");
          const rolePickSearchKeyword = document.getElementById("grpRoleSearchKeyword");
          const rolePickCancel = document.getElementById("btnRolePickCancel");
          const rolePickOk = document.getElementById("btnRolePickOk");
          if (!grpNameInput) return;

          const pendingMembers = [];
          const pendingRoles = [];
          let serverMemberIds = [];
          let serverRoleCds = [];

          function escapeHtmlText(text) {
            if (text == null) return "";
            return String(text)
              .replace(/&/g, "&amp;")
              .replace(/</g, "&lt;")
              .replace(/>/g, "&gt;")
              .replace(/"/g, "&quot;")
              .replace(/'/g, "&#39;");
          }

          function setMemberToolbarVisible(visible) {
            if (!memberToolbar) return;
            memberToolbar.classList.toggle("is-hidden", !visible);
          }

          function setRoleToolbarVisible(visible) {
            if (!roleToolbar) return;
            roleToolbar.classList.toggle("is-hidden", !visible);
          }

          function sortedIdList(ids) {
            return ids
              .filter(function (id) { return id != null && !isNaN(Number(id)); })
              .map(function (id) { return Number(id); })
              .sort(function (a, b) { return a - b; });
          }

          function membersChanged() {
            const current = sortedIdList(
              pendingMembers.map(function (m) { return m.userId; }),
            );
            return ( JSON.stringify(current) !== JSON.stringify(serverMemberIds) );
          }

          function rolesChanged() {
            const current = sortedIdList(
              pendingRoles.map(function (r) { return r.roleCd; }),
            );
            return JSON.stringify(current) !== JSON.stringify(serverRoleCds);
          }

          function setEditActionButtonsVisible(editing) {
            if (editBtn) editBtn.classList.toggle("is-hidden", editing);
            if (saveBtn && !registerMode) saveBtn.classList.toggle("is-hidden", !editing);
            if (cancelBtn) cancelBtn.classList.toggle("is-hidden", !editing);
          }

          function enterEditMode() {
            groupEditMode = true;
            setMemberToolbarVisible(true);
            setRoleToolbarVisible(true);
            setEditActionButtonsVisible(true);
          }

          function exitEditMode() {
            groupEditMode = false;
            initPendingFromServer();
            initPendingRolesFromServer();
            refreshMembersTable();
            refreshRolesTable();
            setMemberToolbarVisible(false);
            setRoleToolbarVisible(false);
            setEditActionButtonsVisible(false);
          }

          let membersGrid = null;
          let rolesGrid = null;

          function formatGridDate(value) {
            if (value == null || value === "") return "-";
            if (Array.isArray(value) && value.length >= 3) {
              const y = value[0];
              const m = String(value[1]).padStart(2, "0");
              const d = String(value[2]).padStart(2, "0");
              return y + "-" + m + "-" + d;
            }
            const dt = new Date(value);
            if (isNaN(dt.getTime())) return String(value);
            const pad = function (n) {
              return String(n).padStart(2, "0");
            };
            return ( dt.getFullYear() + "-" + pad(dt.getMonth() + 1) + "-" + pad(dt.getDate()) )
          }

          function pendingToGridRows() {
            return pendingMembers.map(function (m) { return {
                userId: m.userId,
                userName: m.userName || "-",
                tel: m.tel || "-",
                email: m.email || "-",
                grpName: m.grpName || "-",
              };
            });
          }

          function initPendingFromServer() {
            if (registerMode) return;
            pendingMembers.length = 0;
            (serverMembers || []).forEach(function (m) {
              if (m == null || m.userId == null) return;
              pendingMembers.push({
                userId: Number(m.userId),
                userName: m.userName || "-",
                tel: m.tel || "-",
                email: m.email || "-",
                grpName: m.grpName || "-",
              });
            });
            serverMemberIds = sortedIdList(
              pendingMembers.map(function (m) {
                return m.userId;
              }),
            );
          }

          function initPendingRolesFromServer() {
            if (registerMode) return;
            pendingRoles.length = 0;
            (serverRoles || []).forEach(function (r) {
              if (r == null || r.roleCd == null) return;
              pendingRoles.push({
                roleCd: Number(r.roleCd),
                roleName: r.roleName || "-",
                createdOn: r.createdOn,
                remark: r.remark || "-",
              });
            });
            serverRoleCds = sortedIdList(
              pendingRoles.map(function (r) {
                return r.roleCd;
              }),
            );
          }

          function pendingRolesToGridRows() {
            return pendingRoles.map(function (r) {
              return {
                roleCd: r.roleCd,
                roleName: r.roleName || "-",
                createdOn: r.createdOn,
                remark: r.remark || "-",
              };
            });
          }

          function refreshRolesTable() {
            if (!rolesGridEl) return;
            if (rolesGrid && typeof rolesGrid.resetData === "function") {
              rolesGrid.resetData(pendingRolesToGridRows());
              return;
            }
            createRolesGrid();
          }

          function createMembersGrid() {
            if (!membersGridEl || typeof tui === "undefined" || !tui.Grid) {
              return;
            }
            if (membersGrid) {
              membersGrid.destroy();
              membersGrid = null;
            }
            membersGrid = new tui.Grid({
              el: membersGridEl,
              data: pendingToGridRows(),
              rowHeaders: ["rowNum"],
              scrollX: false,
              scrollY: false,
              bodyHeight: "auto",
              rowHeight: 36,
              minBodyHeight: 80,
              columns: [
                {
                  header: "이름",
                  name: "userName",
                  align: "center",
                  width: 300,
                  sortable: true,
                },
                {
                  header: "연락처",
                  name: "tel",
                  width: 120,
                  align: "center",
                  sortable: false,
                },
                {
                  header: "이메일",
                  name: "email",
                  align: "center",
                  sortable: true,
                },
              ],
              pageOptions: {
                useClient: true,
                perPage: 10,
              },
            });
          }

          function closeGroupRoleMenusModal() {
            if (!roleMenusOverlay) return;
            roleMenusOverlay.classList.remove("is-open");
            roleMenusOverlay.setAttribute("aria-hidden", "true");
            if (grpRoleMenusLoading) grpRoleMenusLoading.hidden = true;
            if (grpRoleMenusTable) grpRoleMenusTable.hidden = true;
            if (grpRoleMenusTableBody) grpRoleMenusTableBody.innerHTML = "";
          }

          function renderGrpRoleMenusMatrix(sections) {
            if (!grpRoleMenusTable || !grpRoleMenusTableBody) return false;
            grpRoleMenusTableBody.innerHTML = "";
            const safeSections = Array.isArray(sections) ? sections : [];
            const rows = safeSections.filter(function (sec) {
              if (!sec) return false;
              const crud = Array.isArray(sec.crudSlots) ? sec.crudSlots : [];
              const extras = Array.isArray(sec.extraSlots) ? sec.extraSlots : [];
              const fulls = Array.isArray(sec.fullControlSlots) ? sec.fullControlSlots : [];
              return (
                crud.some(function (c) { return c && c.checked; }) ||
                extras.some(function (e) { return e && e.checked; }) ||
                fulls.some(function (f) { return f && f.checked; })
              );
            });
            if (!rows.length) {
              grpRoleMenusTable.hidden = true;
              return false;
            }
            const labelToKey = { "조회": "view", "등록": "create", "수정": "update", "삭제": "delete" };
            rows.forEach(function (sec) {
              const tr = document.createElement("tr");
              const tdName = document.createElement("td");
              tdName.textContent = sec.sectionTitle || "-";
              tr.appendChild(tdName);
              const flags = { view: false, create: false, update: false, "delete": false };
              const crud = Array.isArray(sec.crudSlots) ? sec.crudSlots : [];
              crud.forEach(function (c) {
                if (!c) return;
                const k = labelToKey[c.label];
                if (k) flags[k] = !!c.checked;
              });
              ["view", "create", "update", "delete"].forEach(function (key) {
                const td = document.createElement("td");
                if (flags[key]) {
                  td.textContent = "✓";
                  td.className = "grp-role-menus-cell--check";
                } else {
                  td.textContent = "-";
                  td.className = "grp-role-menus-cell--dash";
                }
                tr.appendChild(td);
              });
              grpRoleMenusTableBody.appendChild(tr);
            });
            grpRoleMenusTable.hidden = false;
            return true;
          }

          async function openGroupRoleMenusModal(fallbackRoleName, pid, gid, rc) {
            if (
              !roleMenusOverlay ||
              !grpRoleMenusTable ||
              !grpRoleMenusTableBody ||
              !grpRoleMenusEmpty ||
              !grpRoleMenusLoading ||
              !grpRoleMenusRoleCaption
            ) {
              return;
            }
            grpRoleMenusRoleCaption.textContent =
              fallbackRoleName && fallbackRoleName !== "-"
                ? "권한: " + fallbackRoleName
                : "권한 코드: " + rc;
            grpRoleMenusTable.hidden = true;
            grpRoleMenusTableBody.innerHTML = "";
            grpRoleMenusEmpty.hidden = true;
            grpRoleMenusLoading.hidden = false;
            roleMenusOverlay.classList.add("is-open");
            roleMenusOverlay.setAttribute("aria-hidden", "false");
            try {
              var url =
                "/project/group/groupRoleMenus?grpId=" +
                encodeURIComponent(String(gid)) +
                "&roleCd=" +
                encodeURIComponent(String(rc));
              var res = await fetch(url, { credentials: "same-origin" });
              var data = await res.json().catch(function () {
                return {};
              });
              grpRoleMenusLoading.hidden = true;
              if (!res.ok || !data || data.ok !== true) {
                var msg =
                  (data && data.message) ||
                  "메뉴 권한을 불러오지 못했습니다.";
                await alertMsg(msg, "오류");
                closeGroupRoleMenusModal();
                return;
              }
              var name = data.roleName || fallbackRoleName || "";
              if (name && name !== "-") {
                grpRoleMenusRoleCaption.textContent = "권한: " + name;
              }
              const rendered = renderGrpRoleMenusMatrix(data.menuSections);
              if (!rendered) {
                grpRoleMenusEmpty.hidden = false;
              }
            } catch (err) {
              grpRoleMenusLoading.hidden = true;
              await alertMsg("메뉴 권한을 불러오지 못했습니다.", "오류");
              closeGroupRoleMenusModal();
            }
          }

          function createRolesGrid() {
            if (!rolesGridEl || typeof tui === "undefined" || !tui.Grid) return;
            if (rolesGrid) rolesGrid.destroy(), rolesGrid = null;
            rolesGrid = new tui.Grid({
              el: rolesGridEl,
              data: pendingRolesToGridRows(),
              rowHeaders: ["rowNum"],
              scrollX: false,
              scrollY: false,
              bodyHeight: "auto",
              rowHeight: 36,
              minBodyHeight: 80,
              columns: [
                {
                  header: "권한명",
                  name: "roleName",
                  align: "center",
                  sortable: true,
                  escapeHTML: false,
                  formatter: function (ctx) {
                    var raw =
                      ctx.value == null || ctx.value === ""
                        ? "-"
                        : String(ctx.value);
                    var v = raw === "-" ? raw : escapeHtmlText(raw);
                    return ( '<span class="grp-role-name-link" tabindex="0">' + v + "</span>" ) 
                  },
                },
                {
                  header: "권한 생성일",
                  name: "createdOn",
                  width: 200,
                  align: "center",
                  sortable: true,
                  formatter: function (ctx) {
                    return formatGridDate(ctx.value);
                  },
                },
                {
                  header: "비고",
                  name: "remark",
                  width: 100,
                  align: "center",
                  sortable: false,
                },
              ],
              pageOptions: {
                useClient: true,
                perPage: 10,
              },
            });
            rolesGrid.on("click", function (ev) {
              if (!rolesGrid || groupEditMode || ev.columnName !== "roleName") {
                return;
              }
              var row = rolesGrid.getRow(ev.rowKey);
              if (!row || row.roleCd == null) return;
              var pid = Number(prjId);
              var gid = Number(grpId);
              var rc = Number(row.roleCd);
              if (isNaN(pid) || isNaN(gid) || isNaN(rc)) return;
              var label = row.roleName || "";
              openGroupRoleMenusModal(label, pid, gid, rc);
            });
          }
          let allPickMembers = [];
          let pickGrid = null;
          let isSyncingPickGrid = false;
          /** 모달 내 체크 상태 (저장 전까지 구성원 정보 테이블과 분리) */
          let modalCheckedUserIds = new Set();

          function buildCsrfHeaders() {
            const headers = { "Content-Type": "application/json" };
            const tokenMeta = document.querySelector('meta[name="_csrf"]');
            const headerMeta = document.querySelector('meta[name="_csrf_header"]');
            if ( tokenMeta && headerMeta && tokenMeta.content && headerMeta.content)
              headers[headerMeta.content] = tokenMeta.content;
            return headers;
          }

          function alertMsg(msg, title) {
            if (window.GroupQuestionModal && window.GroupQuestionModal.alert) {
              return window.GroupQuestionModal.alert(msg, title || "알림");
            }
            return Promise.resolve();
          }

          function refreshMembersTable() {
            if (!membersGridEl) return;
            if (membersGrid && typeof membersGrid.resetData === "function") {
              membersGrid.resetData(pendingToGridRows());
              return;
            }
            createMembersGrid();
          }

          function rowToPendingMember(row) {
            return {
              userId: Number(row.userId),
              memberId: row.memberId,
              userName: row.userName || "",
              tel: row.tel || "",
              email: row.email || "",
              grpName: row.grpName || "",
            };
          }

          function initModalDraftFromPending() {
            modalCheckedUserIds = new Set(
              pendingMembers
                .map(function (m) {
                  return Number(m.userId);
                })
                .filter(function (id) {
                  return !isNaN(id);
                }),
            );
          }

          function isModalDraftChecked(uid) {
            return modalCheckedUserIds.has(Number(uid));
          }

          function getCheckedPickRows() {
            if (!pickGrid) return [];
            if (typeof pickGrid.getCheckedRowKeys === "function") {
              const keys = pickGrid.getCheckedRowKeys();
              if (Array.isArray(keys) && typeof pickGrid.getRow === "function") {
                return keys
                  .map(function (k) {
                    return pickGrid.getRow(k);
                  })
                  .filter(Boolean);
              }
            }
            if (typeof pickGrid.getCheckedRows === "function") {
              const arr = pickGrid.getCheckedRows();
              return Array.isArray(arr) ? arr : [];
            }
            return [];
          }

          function captureModalDraftFromGrid() {
            if (!pickGrid || typeof pickGrid.getData !== "function") return;
            const checkedKeys = new Set();
            if (typeof pickGrid.getCheckedRowKeys === "function") {
              pickGrid.getCheckedRowKeys().forEach(function (k) {
                checkedKeys.add(k);
              });
            }
            pickGrid.getData().forEach(function (row) {
              const uid = Number(row.userId);
              if (isNaN(uid)) return;
              if (checkedKeys.has(row.rowKey)) {
                modalCheckedUserIds.add(uid);
              } else {
                modalCheckedUserIds.delete(uid);
              }
            });
          }

          function commitModalSelection() {
            pendingMembers.length = 0;
            allPickMembers.forEach(function (row) {
              const uid = Number(row.userId);
              if (!isNaN(uid) && modalCheckedUserIds.has(uid)) {
                pendingMembers.push(rowToPendingMember(row));
              }
            });
            refreshMembersTable();
          }

          function includesKeyword(val, keyword) {
            if (val == null) return false;
            return String(val).toLowerCase().includes(keyword);
          }

          function filterPickMembers(rows) {
            const type = pickSearchType ? pickSearchType.value : "userName";
            const keyword = pickSearchKeyword
              ? pickSearchKeyword.value.trim().toLowerCase()
              : "";
            if (!keyword) return rows;
            return rows.filter(function (r) {
              if (type === "memberId") {
                return includesKeyword(r.memberId, keyword);
              }
              return includesKeyword(r.userName, keyword);
            });
          }

          function getPickGridRows() {
            return filterPickMembers(
              allPickMembers.filter(function (r) {
                return r.userId != null;
              }),
            );
          }

          function resetPickSearch() {
            if (pickSearchType) pickSearchType.value = "userName";
            if (pickSearchKeyword) pickSearchKeyword.value = "";
          }

          function applyPickGridChecks() {
            if (!pickGrid || typeof pickGrid.getData !== "function") return;
            isSyncingPickGrid = true;
            try {
              pickGrid.getData().forEach(function (row) {
                if (row.rowKey == null) return;
                if (isModalDraftChecked(row.userId)) {
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
              const row = pickGrid.getRow(ev.rowKey);
              if (row && row.userId != null) {
                modalCheckedUserIds.add(Number(row.userId));
              }
            });
            pickGrid.on("uncheck", function (ev) {
              if (isSyncingPickGrid) return;
              const row = pickGrid.getRow(ev.rowKey);
              if (row && row.userId != null) {
                modalCheckedUserIds.delete(Number(row.userId));
              }
            });
            pickGrid.on("checkAll", function () {
              if (isSyncingPickGrid) return;
              if (typeof pickGrid.getData !== "function") return;
              pickGrid.getData().forEach(function (row) {
                if (row.userId != null) {
                  modalCheckedUserIds.add(Number(row.userId));
                }
              });
            });
            pickGrid.on("uncheckAll", function () {
              if (isSyncingPickGrid) return;
              if (typeof pickGrid.getData !== "function") return;
              pickGrid.getData().forEach(function (row) {
                if (row.userId != null) {
                  modalCheckedUserIds.delete(Number(row.userId));
                }
              });
            });
          }

          function recreatePickGrid() {
            if (!pickGridEl || typeof tui === "undefined" || !tui.Grid) {
              return;
            }
            if (pickGrid) {
              captureModalDraftFromGrid();
              pickGrid.destroy();
              pickGrid = null;
            }
            const data = getPickGridRows();
            pickGrid = new tui.Grid({
              el: pickGridEl,
              data: data,
              rowHeaders: [{ type: "checkbox", header: "선택" }],
              scrollX: false,
              scrollY: true,
              bodyHeight: 280,
              rowHeight: 36,
              minBodyHeight: 100,
              columns: [
                {
                  header: "사번",
                  name: "memberId",
                  width: 90,
                  align: "center",
                  sortable: true,
                },
                {
                  header: "이름",
                  name: "userName",
                  width: 120,
                  align: "center",
                  sortable: true,
                },
                {
                  header: "그룹명",
                  name: "grpName",
                  align: "center",
                  sortable: true,
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

          setMemberToolbarVisible(registerMode);
          setRoleToolbarVisible(false);
          setEditActionButtonsVisible(false);
          if (!registerMode) {
            initPendingFromServer();
            initPendingRolesFromServer();
          }
          createMembersGrid();
          if (!registerMode) {
            createRolesGrid();
          }

          let allPickRoles = [];
          let rolePickGrid = null;
          let isSyncingRolePickGrid = false;
          let modalCheckedRoleCds = new Set();

          function initRoleModalDraftFromPending() {
            modalCheckedRoleCds = new Set(
              pendingRoles
                .map(function (r) {
                  return Number(r.roleCd);
                })
                .filter(function (id) {
                  return !isNaN(id);
                }),
            );
          }

          function isRoleModalDraftChecked(roleCd) {
            return modalCheckedRoleCds.has(Number(roleCd));
          }

          function filterPickRoles(rows) {
            const type = rolePickSearchType ? rolePickSearchType.value : "roleName";
            const keyword = rolePickSearchKeyword
              ? rolePickSearchKeyword.value.trim().toLowerCase()
              : "";
            if (!keyword) return rows;
            return rows.filter(function (r) {
              if (type === "roleCd") {
                return includesKeyword(r.roleCd, keyword);
              }
              return includesKeyword(r.roleName, keyword);
            });
          }

          function getRolePickGridRows() {
            return filterPickRoles(
              allPickRoles.filter(function (r) {
                return r.roleCd != null;
              }),
            );
          }

          function resetRolePickSearch() {
            if (rolePickSearchType) rolePickSearchType.value = "roleName";
            if (rolePickSearchKeyword) rolePickSearchKeyword.value = "";
          }

          function applyRolePickGridChecks() {
            if (!rolePickGrid || typeof rolePickGrid.getData !== "function") {
              return;
            }
            isSyncingRolePickGrid = true;
            try {
              rolePickGrid.getData().forEach(function (row) {
                if (row.rowKey == null) return;
                if (isRoleModalDraftChecked(row.roleCd)) {
                  rolePickGrid.check(row.rowKey);
                } else if (typeof rolePickGrid.uncheck === "function") {
                  rolePickGrid.uncheck(row.rowKey);
                }
              });
            } finally {
              isSyncingRolePickGrid = false;
            }
          }

          function bindRolePickGridSyncEvents() {
            if (!rolePickGrid) return;
            rolePickGrid.on("check", function (ev) {
              if (isSyncingRolePickGrid) return;
              const row = rolePickGrid.getRow(ev.rowKey);
              if (row && row.roleCd != null) {
                modalCheckedRoleCds.add(Number(row.roleCd));
              }
            });
            rolePickGrid.on("uncheck", function (ev) {
              if (isSyncingRolePickGrid) return;
              const row = rolePickGrid.getRow(ev.rowKey);
              if (row && row.roleCd != null) {
                modalCheckedRoleCds.delete(Number(row.roleCd));
              }
            });
            rolePickGrid.on("checkAll", function () {
              if (isSyncingRolePickGrid) return;
              if (typeof rolePickGrid.getData !== "function") return;
              rolePickGrid.getData().forEach(function (row) {
                if (row.roleCd != null) {
                  modalCheckedRoleCds.add(Number(row.roleCd));
                }
              });
            });
            rolePickGrid.on("uncheckAll", function () {
              if (isSyncingRolePickGrid) return;
              if (typeof rolePickGrid.getData !== "function") return;
              rolePickGrid.getData().forEach(function (row) {
                if (row.roleCd != null) {
                  modalCheckedRoleCds.delete(Number(row.roleCd));
                }
              });
            });
          }

          function recreateRolePickGrid() {
            if (!rolePickGridEl || typeof tui === "undefined" || !tui.Grid) {
              return;
            }
            if (rolePickGrid) {
              rolePickGrid.destroy();
              rolePickGrid = null;
            }
            rolePickGrid = new tui.Grid({
              el: rolePickGridEl,
              data: getRolePickGridRows(),
              rowHeaders: [{ type: "checkbox", header: "선택" }],
              scrollX: false,
              scrollY: true,
              bodyHeight: 280,
              rowHeight: 36,
              minBodyHeight: 100,
              columns: [
                {
                  header: "권한코드",
                  name: "roleCd",
                  width: 100,
                  align: "center",
                  sortable: true,
                },
                {
                  header: "권한명",
                  name: "roleName",
                  align: "center",
                  sortable: true,
                },
                {
                  header: "권한 생성일",
                  name: "createdOn",
                  width: 120,
                  align: "center",
                  sortable: true,
                  formatter: function (ctx) {
                    return formatGridDate(ctx.value);
                  },
                },
              ],
              pageOptions: {
                useClient: true,
                perPage: 8,
              },
            });
            bindRolePickGridSyncEvents();
            applyRolePickGridChecks();
          }

          function captureRoleModalDraftFromGrid() {
            if (!rolePickGrid || typeof rolePickGrid.getData !== "function") return;
            const checkedKeys = new Set();
            if (typeof rolePickGrid.getCheckedRowKeys === "function") {
              rolePickGrid.getCheckedRowKeys().forEach(function (k) {
                checkedKeys.add(k);
              });
            }
            rolePickGrid.getData().forEach(function (row) {
              const rc = Number(row.roleCd);
              if (isNaN(rc)) return;
              if (checkedKeys.has(row.rowKey)) {
                modalCheckedRoleCds.add(rc);
              } else {
                modalCheckedRoleCds.delete(rc);
              }
            });
          }

          function commitRoleModalSelection() {
            pendingRoles.length = 0;
            allPickRoles.forEach(function (row) {
              const rc = Number(row.roleCd);
              if (!isNaN(rc) && modalCheckedRoleCds.has(rc)) {
                pendingRoles.push({
                  roleCd: rc,
                  roleName: row.roleName || "-",
                  createdOn: row.createdOn,
                  remark: row.remark || "-",
                });
              }
            });
            refreshRolesTable();
          }

          async function openRolePicker() {
            if (!groupEditMode) return;
            if (!rolePickOverlay || !rolePickGridEl) return;
            resetRolePickSearch();
            if (rolePickGrid) {
              rolePickGrid.destroy();
              rolePickGrid = null;
            }
            rolePickGridEl.innerHTML =
              '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">불러오는 중…</p>';
            rolePickOverlay.classList.add("is-open");
            rolePickOverlay.setAttribute("aria-hidden", "false");
            try {
              const res = await fetch("/project/group/groupRolePickList", {
                credentials: "same-origin",
              });
              const data = await res.json().catch(function () {
                return {};
              });
              allPickRoles =
                data && Array.isArray(data.content) ? data.content : [];
              initRoleModalDraftFromPending();
              rolePickGridEl.innerHTML = "";
              if (!allPickRoles.length) {
                rolePickGridEl.innerHTML =
                  '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">선택할 권한이 없습니다.</p>';
                return;
              }
              recreateRolePickGrid();
            } catch (e) {
              allPickRoles = [];
              rolePickGridEl.innerHTML =
                '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">권한 목록을 불러오지 못했습니다.</p>';
            }
          }

          function closeRolePicker() {
            if (!rolePickOverlay) return;
            rolePickOverlay.classList.remove("is-open");
            rolePickOverlay.setAttribute("aria-hidden", "true");
            if (rolePickGrid) {
              try {
                rolePickGrid.destroy();
              } catch (e) {
                /* ignore */
              }
              rolePickGrid = null;
            }
          }

          async function openMemberPicker() {
            if (!groupEditMode) return;
            const pid = Number(prjId);
            if (isNaN(pid) || !pickOverlay || !pickGridEl) return;
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
              const res = await fetch("/project/group/groupMemberPickList", {
                credentials: "same-origin",
              });
              const data = await res.json().catch(function () {
                return {};
              });
              allPickMembers =
                data && Array.isArray(data.content) ? data.content : [];
              initModalDraftFromPending();
              pickGridEl.innerHTML = "";
              if (!allPickMembers.length) {
                pickGridEl.innerHTML =
                  '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">선택할 구성원이 없습니다.</p>';
                return;
              }
              recreatePickGrid();
            } catch (e) {
              allPickMembers = [];
              pickGridEl.innerHTML =
                '<p style="margin:0.75rem 1rem;font-size:0.8125rem;color:#777">구성원 목록을 불러오지 못했습니다.</p>';
            }
          }

          function closeMemberPicker() {
            if (!pickOverlay) return;
            pickOverlay.classList.remove("is-open");
            pickOverlay.setAttribute("aria-hidden", "true");
            if (pickGrid) {
              try {
                pickGrid.destroy();
              } catch (e) {
                /* ignore */
              }
              pickGrid = null;
            }
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

          if (addMemberBtn) {
            addMemberBtn.addEventListener("click", openMemberPicker);
          }
          if (pickCancel) {
            pickCancel.addEventListener("click", closeMemberPicker);
          }
          if (pickOverlay) {
            pickOverlay.addEventListener("click", function (e) {
              if (e.target === pickOverlay) {
                closeMemberPicker();
              }
            });
          }
          if (pickOk) {
            pickOk.addEventListener("click", function () {
              try {
                if (pickGrid) {
                  captureModalDraftFromGrid();
                }
                commitModalSelection();
              } finally {
                closeMemberPicker();
              }
            });
          }

          if (btnGrpRoleMenusClose) {
            btnGrpRoleMenusClose.addEventListener(
              "click",
              closeGroupRoleMenusModal,
            );
          }
          if (roleMenusOverlay) {
            roleMenusOverlay.addEventListener("click", function (e) {
              if (e.target === roleMenusOverlay) {
                closeGroupRoleMenusModal();
              }
            });
          }

          if (editBtn) {
            editBtn.addEventListener("click", function () {
              enterEditMode();
            });
          }
          if (cancelBtn) {
            cancelBtn.addEventListener("click", function () {
              exitEditMode();
            });
          }
          if (manageRolesBtn) {
            manageRolesBtn.addEventListener("click", openRolePicker);
          }
          if (rolePickCancel) {
            rolePickCancel.addEventListener("click", closeRolePicker);
          }
          if (rolePickOk) {
            rolePickOk.addEventListener("click", function () {
              try {
                if (rolePickGrid) {
                  captureRoleModalDraftFromGrid();
                }
                commitRoleModalSelection();
              } finally {
                closeRolePicker();
              }
            });
          }
          if (rolePickOverlay) {
            rolePickOverlay.addEventListener("click", function (e) {
              if (e.target === rolePickOverlay) {
                closeRolePicker();
              }
            });
          }
          if (rolePickSearchType) {
            rolePickSearchType.addEventListener("change", function () {
              if (
                !rolePickOverlay ||
                !rolePickOverlay.classList.contains("is-open")
              ) {
                return;
              }
              recreateRolePickGrid();
            });
          }
          if (rolePickSearchKeyword) {
            rolePickSearchKeyword.addEventListener("input", function () {
              if (
                !rolePickOverlay ||
                !rolePickOverlay.classList.contains("is-open")
              ) {
                return;
              }
              recreateRolePickGrid();
            });
          }

          if (saveBtn) {
            saveBtn.addEventListener("click", async function () {
              if (!registerMode) {
                const pid = Number(prjId);
                const gid = Number(grpId);
                if (!groupEditMode) {
                  return;
                }
                if (isNaN(pid) || isNaN(gid)) {
                  await alertMsg("그룹 정보가 올바르지 않습니다.", "알림");
                  return;
                }
                const memberDirty = membersChanged();
                const roleDirty = rolesChanged();
                if (!memberDirty && !roleDirty) {
                  await alertMsg("변경된 내용이 없습니다.", "알림");
                  return;
                }

                saveBtn.disabled = true;
                try {
                  if (memberDirty) {
                    const memberPayload = {
                      grpId: gid,
                      userIds: pendingMembers.map(function (m) {
                        return m.userId;
                      }),
                    };
                    const memberRes = await fetch("/project/group/updateGroup", {
                      method: "POST",
                      headers: buildCsrfHeaders(),
                      credentials: "same-origin",
                      body: JSON.stringify(memberPayload),
                    });
                    const memberData = await memberRes.json().catch(function () {
                      return {};
                    });
                    if (!memberRes.ok || !memberData || memberData.ok !== true) {
                      const errText =
                        (memberData && memberData.message) ||
                        (memberRes.status === 403
                          ? "권한이 없거나 보안 토큰이 만료되었습니다. 페이지를 새로고침한 뒤 다시 시도해 주세요."
                          : "그룹 구성원 수정 중 오류가 발생했습니다.");
                      await alertMsg(errText, "오류");
                      return;
                    }
                  }
                  if (roleDirty) {
                    const rolePayload = {
                      grpId: gid,
                      roleCds: pendingRoles.map(function (r) {
                        return r.roleCd;
                      }),
                    };
                    const roleRes = await fetch("/project/group/updateGroupRoles", {
                      method: "POST",
                      headers: buildCsrfHeaders(),
                      credentials: "same-origin",
                      body: JSON.stringify(rolePayload),
                    });
                    const roleData = await roleRes.json().catch(function () {
                      return {};
                    });
                    if (!roleRes.ok || !roleData || roleData.ok !== true) {
                      const errText =
                        (roleData && roleData.message) ||
                        (roleRes.status === 403
                          ? "권한이 없거나 보안 토큰이 만료되었습니다. 페이지를 새로고침한 뒤 다시 시도해 주세요."
                          : "그룹 권한 수정 중 오류가 발생했습니다.");
                      await alertMsg(errText, "오류");
                      return;
                    }
                  }

                  const okMsg =
                    memberDirty && roleDirty ? "그룹 구성원과 권한이 수정되었습니다." : memberDirty ? "그룹 구성원이 수정되었습니다." : "그룹 권한이 수정되었습니다.";
                  await alertMsg(okMsg, "알림");
                  window.location.href =
                    "/project/group/info?grpId=" +
                    encodeURIComponent(String(gid));
                } catch (e) {
                  await alertMsg("그룹 수정 중 오류가 발생했습니다.", "오류");
                } finally {
                  saveBtn.disabled = false;
                }
                return;
              }

              const pid = Number(prjId);
              const name = grpNameInput.value.trim();
              if (isNaN(pid)) {
                await alertMsg("프로젝트 ID가 올바르지 않습니다.", "알림");
                return;
              }
              if (!name) {
                await alertMsg("그룹명을 입력하세요.", "알림");
                return;
              }

              const payload = { grpName: name };
              if (pendingMembers.length > 0) {
                payload.userIds = pendingMembers.map(function (m) {
                  return m.userId;
                });
              }

              saveBtn.disabled = true;
              try {
                const res = await fetch("/project/group/registerGroup", {
                  method: "POST",
                  headers: buildCsrfHeaders(),
                  credentials: "same-origin",
                  body: JSON.stringify(payload),
                });
                const data = await res.json().catch(function () {
                  return {};
                });
                if (!res.ok || !data || data.ok !== true) {
                  const errText =
                    (data && data.message) ||
                    (res.status === 403
                      ? "권한이 없거나 보안 토큰이 만료되었습니다. 페이지를 새로고침한 뒤 다시 시도해 주세요."
                      : "그룹 등록 중 오류가 발생했습니다.");
                  await alertMsg(errText, "오류");
                  return;
                }
                await alertMsg("그룹이 등록되었습니다.", "알림");
                window.location.href = "/project/group/list";
              } catch (e) {
                await alertMsg("그룹 등록 중 오류가 발생했습니다.", "오류");
              } finally {
                saveBtn.disabled = false;
              }
            });
          }
        })();
