(function () {
          const prjId = Number(memJoinPrjId);
          const prjStartDateYmd = String(memJoinPrjStartDate || "");
          const prjClosedDateYmd = String(memJoinPrjClosedDate || "");

          const gridEl = document.getElementById("memJoinMemberGrid");
          const searchType = document.getElementById("memJoinSearchType");
          const searchKeyword = document.getElementById("memJoinSearchKeyword");
          const startInput = document.getElementById("memJoinPrjStart");
          const endInput = document.getElementById("memJoinPrjEnd");
          const periodHint = document.getElementById("memJoinPeriodHint");
          const grpInput = document.getElementById("memJoinGrpName");
          const grpIdInput = document.getElementById("memJoinSelectedGrpId");
          const groupOverlay = document.getElementById("memJoinGroupOverlay");
          const groupPickList = document.getElementById("memJoinGroupPickList");
          const groupSearchType = document.getElementById("memJoinGroupSearchType");
          const groupSearchKeyword = document.getElementById("memJoinGroupSearchKeyword");
          const groupPickCancel = document.getElementById("btnMemJoinGroupPickCancel");
          const registerBtn = document.getElementById("btnMemberJoinRegister");

          let allMembers = [];
          let allGroups = [];
          let grid = null;
          const selectedUserIds = new Set();
          let isSyncingCheck = false;

          function buildCsrfHeaders() {
            const headers = { "Content-Type": "application/json" };
            const tokenMeta = document.querySelector('meta[name="_csrf"]');
            const headerMeta = document.querySelector('meta[name="_csrf_header"]');
            if (tokenMeta && headerMeta && tokenMeta.content && headerMeta.content) {
              headers[headerMeta.content] = tokenMeta.content;
            }
            return headers;
          }

          function alertMsg(msg, title) {
            if (window.MemberQuestionModal && window.MemberQuestionModal.alert) {
              return window.MemberQuestionModal.alert(msg, title || "알림");
            }
            return Promise.resolve();
          }

          function todayYmd() {
            const d = new Date();
            const y = d.getFullYear();
            const m = String(d.getMonth() + 1).padStart(2, "0");
            const day = String(d.getDate()).padStart(2, "0");
            return y + "-" + m + "-" + day;
          }

          function normalizeYmd(value) {
            if (value == null) return "";
            const s = String(value).trim();
            if (!s) return "";
            if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return s;
            const d = new Date(s);
            if (isNaN(d.getTime())) return "";
            const y = d.getFullYear();
            const m = String(d.getMonth() + 1).padStart(2, "0");
            const day = String(d.getDate()).padStart(2, "0");
            return y + "-" + m + "-" + day;
          }

          function maxYmd(a, b) {
            if (!a) return b || "";
            if (!b) return a;
            return a >= b ? a : b;
          }

          function getSelectedMembers() {
            if (selectedUserIds.size === 0) return [];
            return allMembers.filter(function (m) {
              return selectedUserIds.has(Number(m.userId));
            });
          }

          /** 선택된 직원들 중 가장 늦은 입사일 (없으면 빈 문자열) */
          function getSelectedMaxHireYmd() {
            let max = "";
            getSelectedMembers().forEach(function (m) {
              const ymd = m && m.hireDate ? normalizeYmd(m.hireDate) : "";
              if (ymd && (!max || ymd > max)) {
                max = ymd;
              }
            });
            return max;
          }

          function renderPeriodHint() {
            if (!periodHint) return;
            const hireYmd = getSelectedMaxHireYmd();
            const minStart = maxYmd(hireYmd, prjStartDateYmd);
            const lines = [];
            const count = selectedUserIds.size;
            if (count === 0) {
              lines.push("직원을 선택하면 투입일 가능 범위가 갱신됩니다.");
            } else {
              if (count > 1) {
                lines.push("선택된 직원 " + count + "명을 일괄 등록합니다.");
              }
              if (minStart) {
                lines.push("투입일은 " + minStart + " 이후로 지정할 수 있습니다.");
              }
            }
            if (prjClosedDateYmd) {
              lines.push("종료일은 " + prjClosedDateYmd + " 이전이어야 합니다.");
            }
            periodHint.textContent = lines.join(" ");
            periodHint.classList.toggle("mem-join-hint--warn", count === 0);
          }

          function applyDateConstraints() {
            const hireYmd = getSelectedMaxHireYmd();
            const minStart = maxYmd(hireYmd, prjStartDateYmd);

            if (minStart) {
              startInput.min = minStart;
            } else {
              startInput.removeAttribute("min");
            }
            if (prjClosedDateYmd) {
              startInput.max = prjClosedDateYmd;
            } else {
              startInput.removeAttribute("max");
            }

            let curStart = normalizeYmd(startInput.value);
            if (curStart && minStart && curStart < minStart) {
              curStart = minStart;
              startInput.value = curStart;
            }
            if (curStart && prjClosedDateYmd && curStart > prjClosedDateYmd) {
              curStart = prjClosedDateYmd;
              startInput.value = curStart;
            }

            if (curStart) {
              endInput.min = curStart;
            } else if (minStart) {
              endInput.min = minStart;
            } else {
              endInput.removeAttribute("min");
            }
            if (prjClosedDateYmd) {
              endInput.max = prjClosedDateYmd;
            } else {
              endInput.removeAttribute("max");
            }

            let curEnd = normalizeYmd(endInput.value);
            if (curEnd && curStart && curEnd < curStart) {
              curEnd = curStart;
              endInput.value = curEnd;
            }
            if (curEnd && prjClosedDateYmd && curEnd > prjClosedDateYmd) {
              endInput.value = prjClosedDateYmd;
            }
          }

          function initDefaultStartDate() {
            const hireYmd = getSelectedMaxHireYmd();
            const minStart = maxYmd(hireYmd, prjStartDateYmd);
            const today = todayYmd();
            let initStart = startInput.value ? normalizeYmd(startInput.value) : "";
            if (!initStart) {
              initStart = maxYmd(minStart, today);
              if (prjClosedDateYmd && initStart > prjClosedDateYmd) {
                initStart = prjClosedDateYmd;
              }
              if (initStart) startInput.value = initStart;
            }
          }

          function initDefaultEndDate() {
            if (!endInput) return;
            let initEnd = normalizeYmd(endInput.value);
            if (!initEnd && prjClosedDateYmd) {
              endInput.value = prjClosedDateYmd;
            }
          }

          function includesKeyword(value, keyword) {
            if (!keyword) return true;
            const text = value == null ? "" : String(value).trim().toLowerCase();
            return text.indexOf(keyword) !== -1;
          }

          function filterMembers(rows) {
            const type = searchType ? searchType.value : "userName";
            const keyword = searchKeyword
              ? searchKeyword.value.trim().toLowerCase()
              : "";
            if (!keyword) return rows;
            return rows.filter(function (r) {
              if (type === "login") return includesKeyword(r.login, keyword);
              if (type === "tel") return includesKeyword(r.tel, keyword);
              if (type === "email") return includesKeyword(r.email, keyword);
              return includesKeyword(r.userName, keyword);
            });
          }

          function memberGenderText(code) {
            const c = (code || "").toUpperCase();
            if (c === "01GENDER") return "남";
            if (c === "02GENDER") return "여";
            return "-";
          }

          function memberToGridRow(m) {
            return {
              userId: m.userId,
              login: m.login || "-",
              userName: m.userName || "-",
              hireDate: m.hireDate || "-",
              gender: memberGenderText(m.genderCd),
              tel: m.tel || "-",
              email: m.email || "-",
            };
          }

          function refreshGridData() {
            if (!grid) return;
            const rows = filterMembers(allMembers).map(memberToGridRow);
            grid.resetData(rows);
            applyCheckedRowFromSelected();
          }

          function applyCheckedRowFromSelected() {
            if (!grid || typeof grid.getData !== "function") return;
            isSyncingCheck = true;
            try {
              grid.getData().forEach(function (row) {
                if (row.rowKey == null) return;
                const uid = Number(row.userId);
                if (!isNaN(uid) && selectedUserIds.has(uid)) {
                  grid.check(row.rowKey);
                } else if (typeof grid.uncheck === "function") {
                  grid.uncheck(row.rowKey);
                }
              });
            } finally {
              isSyncingCheck = false;
            }
          }

          function onSelectionChanged() {
            applyDateConstraints();
            renderPeriodHint();
          }

          function createGrid() {
            if (!gridEl || typeof tui === "undefined" || !tui.Grid) return;
            if (grid) {
              grid.destroy();
              grid = null;
            }
            grid = new tui.Grid({
              el: gridEl,
              data: filterMembers(allMembers).map(memberToGridRow),
              rowHeaders: [{ type: "checkbox", header: "선택" }],
              scrollX: false,
              scrollY: false,
              bodyHeight: "auto",
              rowHeight: 36,
              minBodyHeight: 100,
              columns: [
                { header: "아이디", name: "login", width: 120, align: "center", sortable: true },
                { header: "이름", name: "userName", width: 130, align: "center", sortable: true },
                { header: "입사일", name: "hireDate", width: 130, align: "center", sortable: true },
                { header: "성별", name: "gender", width: 70, align: "center", sortable: false },
                { header: "연락처", name: "tel", width: 140, align: "center", sortable: false },
                { header: "이메일", name: "email", align: "center", sortable: false },
              ],
              pageOptions: {
                useClient: true,
                perPage: 10,
              },
            });

            grid.on("check", function (ev) {
              if (isSyncingCheck) return;
              const row = grid.getRow(ev.rowKey);
              if (!row || row.userId == null) return;
              selectedUserIds.add(Number(row.userId));
              onSelectionChanged();
            });
            grid.on("uncheck", function (ev) {
              if (isSyncingCheck) return;
              const row = grid.getRow(ev.rowKey);
              if (!row || row.userId == null) return;
              selectedUserIds.delete(Number(row.userId));
              onSelectionChanged();
            });
            grid.on("checkAll", function () {
              if (isSyncingCheck) return;
              const data = grid.getData() || [];
              data.forEach(function (row) {
                if (row && row.userId != null) {
                  selectedUserIds.add(Number(row.userId));
                }
              });
              onSelectionChanged();
            });
            grid.on("uncheckAll", function () {
              if (isSyncingCheck) return;
              const data = grid.getData() || [];
              data.forEach(function (row) {
                if (row && row.userId != null) {
                  selectedUserIds.delete(Number(row.userId));
                }
              });
              onSelectionChanged();
            });
            applyCheckedRowFromSelected();
          }

          async function loadMembers() {
            try {
              const res = await fetch("/project/member/companyMembers?excludeRegistered=true", {
                credentials: "same-origin",
              });
              const data = await res.json().catch(function () {
                return {};
              });
              allMembers = data && Array.isArray(data.content) ? data.content : [];
            } catch (e) {
              allMembers = [];
            }
            createGrid();
            renderPeriodHint();
          }

          function resetGroupSearch() {
            if (groupSearchType) groupSearchType.value = "grpName";
            if (groupSearchKeyword) groupSearchKeyword.value = "";
          }

          function filterGroups(rows) {
            const type = groupSearchType ? groupSearchType.value : "grpName";
            const keyword = groupSearchKeyword
              ? groupSearchKeyword.value.trim().toLowerCase()
              : "";
            if (!keyword) return rows;
            return rows.filter(function (r) {
              if (type === "id") return includesKeyword(r.id, keyword);
              return includesKeyword(r.grpName, keyword);
            });
          }

          function resolveGroupId(row) {
            if (!row) return null;
            const raw =
              row.id != null && row.id !== ""
                ? row.id
                : row.grpId != null && row.grpId !== ""
                  ? row.grpId
                  : null;
            if (raw == null) return null;
            const n = Number(raw);
            return isNaN(n) ? null : n;
          }

          function renderGroupList(rows, emptyMessage) {
            if (!groupPickList) return;
            groupPickList.innerHTML = "";
            if (!rows.length) {
              const p = document.createElement("p");
              p.className = "mem-join-picker__empty";
              p.textContent = emptyMessage || "선택할 그룹이 없습니다.";
              groupPickList.appendChild(p);
              return;
            }
            rows.forEach(function (r) {
              const gid = resolveGroupId(r);
              if (gid == null) return;
              const btn = document.createElement("button");
              btn.type = "button";
              btn.className = "mem-join-picker__item";
              const main = document.createElement("span");
              main.textContent = r.grpName || "(이름 없음)";
              btn.appendChild(main);
              const sub = document.createElement("span");
              sub.className = "mem-join-picker__item-sub";
              sub.textContent = "그룹 ID: " + String(gid);
              btn.appendChild(sub);
              btn.addEventListener("click", function () {
                grpInput.value = r.grpName != null ? String(r.grpName) : "";
                grpIdInput.value = String(gid);
                closeGroupPicker();
              });
              groupPickList.appendChild(btn);
            });
          }

          function refreshGroupListView() {
            renderGroupList(
              filterGroups(allGroups),
              allGroups.length
                ? "검색 결과가 없습니다."
                : "선택할 그룹이 없습니다.",
            );
          }

          async function openGroupPicker() {
            if (!groupOverlay || !groupPickList) return;
            resetGroupSearch();
            groupPickList.innerHTML = "불러오는 중…";
            groupOverlay.classList.add("is-open");
            groupOverlay.setAttribute("aria-hidden", "false");
            try {
              const res = await fetch("/project/member/projectGroups", {
                credentials: "same-origin",
              });
              const data = await res.json().catch(function () {
                return {};
              });
              allGroups = data && Array.isArray(data.content) ? data.content : [];
              refreshGroupListView();
            } catch (e) {
              allGroups = [];
              groupPickList.innerHTML = "";
              const p = document.createElement("p");
              p.className = "mem-join-picker__empty";
              p.textContent = "그룹 목록을 불러오지 못했습니다.";
              groupPickList.appendChild(p);
            }
          }

          function closeGroupPicker() {
            if (!groupOverlay) return;
            groupOverlay.classList.remove("is-open");
            groupOverlay.setAttribute("aria-hidden", "true");
          }

          if (searchType) {
            searchType.addEventListener("change", refreshGridData);
          }
          if (searchKeyword) {
            searchKeyword.addEventListener("input", refreshGridData);
          }

          if (startInput) {
            startInput.addEventListener("change", applyDateConstraints);
            startInput.addEventListener("input", applyDateConstraints);
          }
          if (endInput) {
            endInput.addEventListener("change", applyDateConstraints);
            endInput.addEventListener("input", applyDateConstraints);
          }

          if (grpInput) {
            grpInput.addEventListener("click", openGroupPicker);
          }
          if (groupPickCancel) {
            groupPickCancel.addEventListener("click", closeGroupPicker);
          }
          if (groupOverlay) {
            groupOverlay.addEventListener("click", function (e) {
              if (e.target === groupOverlay) {
                closeGroupPicker();
              }
            });
          }
          if (groupSearchType) {
            groupSearchType.addEventListener("change", refreshGroupListView);
          }
          if (groupSearchKeyword) {
            groupSearchKeyword.addEventListener("input", refreshGroupListView);
          }

          if (registerBtn) {
            registerBtn.addEventListener("click", async function () {
              if (isNaN(prjId)) {
                await alertMsg("프로젝트 ID가 올바르지 않습니다.");
                return;
              }
              if (selectedUserIds.size === 0) {
                await alertMsg("등록할 직원을 선택하세요.");
                return;
              }
              const start = normalizeYmd(startInput.value);
              const end = normalizeYmd(endInput.value);
              if (!start) {
                await alertMsg("프로젝트 투입일을 입력하세요.");
                return;
              }

              const hireYmd = getSelectedMaxHireYmd();
              const minStart = maxYmd(hireYmd, prjStartDateYmd);
              if (minStart && start < minStart) {
                await alertMsg(
                  "프로젝트 투입일은 " + minStart + " 이후로만 지정할 수 있습니다.",
                );
                return;
              }
              if (prjClosedDateYmd && start > prjClosedDateYmd) {
                await alertMsg(
                  "프로젝트 투입 시작일은 프로젝트 종료일(" +
                    prjClosedDateYmd +
                    ") 이전이어야 합니다.",
                );
                return;
              }
              if (!end && prjClosedDateYmd) {
                end = prjClosedDateYmd;
                endInput.value = end;
              }
              if (prjClosedDateYmd && end && end > prjClosedDateYmd) {
                await alertMsg(
                  "투입 종료일은 프로젝트 종료일(" +
                    prjClosedDateYmd +
                    ") 이후로 지정할 수 없습니다.",
                );
                return;
              }
              if (end && start && end < start) {
                await alertMsg("투입 종료일은 투입 시작일 이후여야 합니다.");
                return;
              }

              const gidRaw = grpIdInput && grpIdInput.value ? grpIdInput.value.trim() : "";
              if (!gidRaw) {
                await alertMsg("소속 그룹을 선택하세요.");
                return;
              }
              const gid = Number(gidRaw);
              if (isNaN(gid)) {
                await alertMsg("소속 그룹 정보가 올바르지 않습니다.");
                return;
              }

              const userIds = Array.from(selectedUserIds);
              const payload = {
                userIds: userIds,
                grpId: gid,
                prjStartDate: start,
                prjEndDate: end || null,
              };

              registerBtn.disabled = true;
              try {
                const res = await fetch("/project/member/registerMembers", {
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
                      : "구성원 등록 중 오류가 발생했습니다.");
                  await alertMsg(errText, "오류");
                  return;
                }
                const okMsg = userIds.length === 1
                  ? "구성원이 등록되었습니다."
                  : "구성원 " + userIds.length + "명이 등록되었습니다.";
                await alertMsg(okMsg);
                window.location.href = "/project/member/list";
              } catch (e) {
                await alertMsg("구성원 등록 중 오류가 발생했습니다.", "오류");
              } finally {
                registerBtn.disabled = false;
              }
            });
          }

          initDefaultStartDate();
          initDefaultEndDate();
          applyDateConstraints();
          renderPeriodHint();
          loadMembers();
        })();
