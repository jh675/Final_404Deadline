(function () {
            const cfg = document.getElementById("memPageConfig");
            if (!cfg) return;

            const prjId = Number(cfg.dataset.prjId || 0);
            const pageUserId = cfg.dataset.userId
              ? Number(cfg.dataset.userId)
              : null;
            const pageOldGrpId = cfg.dataset.oldGrpId
              ? Number(cfg.dataset.oldGrpId)
              : null;
            const hireYmd = String(cfg.dataset.hireDate || "");
            const prjOpenYmd = String(cfg.dataset.prjOpen || "");
            const prjClosedYmd = String(cfg.dataset.prjClosed || "");

            function mountPickerOverlay(el) {
              if (el && el.parentElement !== document.body) {
                document.body.appendChild(el);
              }
            }

            function attachPickerOpen(inputEl, openFn) {
              if (!inputEl || typeof openFn !== "function") return;
              inputEl.addEventListener("click", function (e) {
                e.preventDefault();
                openFn();
              });
            }

            const prjStartInput = document.getElementById("detailPrjStart");
            const prjEndInput = document.getElementById("detailPrjEnd");
            const grpInput = document.getElementById("detailGrpName");
            const grpIdInput = document.getElementById("detailSelectedGrpId");
            let groupOverlay = document.getElementById("memGroupPickOverlay");
            const groupPickList = document.getElementById("memGroupPickList");
            const groupPickCancel = document.getElementById("btnGroupPickCancel");

            mountPickerOverlay(groupOverlay);
            const groupSearchType = document.getElementById("memGroupSearchType");
            const groupSearchKeyword = document.getElementById(
              "memGroupSearchKeyword",
            );
            let allGroups = [];

            function includesKeyword(value, keyword) {
              if (!keyword) return true;
              const text =
                value == null ? "" : String(value).trim().toLowerCase();
              return text.indexOf(keyword) !== -1;
            }

            function filterGroups(rows) {
              const type = groupSearchType ? groupSearchType.value : "grpName";
              const keyword = groupSearchKeyword
                ? groupSearchKeyword.value.trim().toLowerCase()
                : "";
              if (!keyword) return rows;
              return rows.filter(function (r) {
                if (type === "id") {
                  return includesKeyword(r.id, keyword);
                }
                return includesKeyword(r.grpName, keyword);
              });
            }

            function resetGroupSearch() {
              if (groupSearchType) groupSearchType.value = "grpName";
              if (groupSearchKeyword) groupSearchKeyword.value = "";
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

            const initialPrjStartYmd = normalizeYmd(cfg.dataset.initialStart || "");
            const initialPrjEndYmd = normalizeYmd(cfg.dataset.initialEnd || "");
            const initialGrpId = cfg.dataset.initialGrpId
              ? Number(cfg.dataset.initialGrpId)
              : null;

            function maxYmd(a, b) {
              if (!a) return b || "";
              if (!b) return a;
              return a >= b ? a : b;
            }

            function minStartYmd() {
              return maxYmd(hireYmd, prjOpenYmd);
            }

            function syncPeriodConstraints() {
              const minStart = minStartYmd();
              if (prjStartInput) {
                if (minStart) {
                  prjStartInput.min = minStart;
                } else {
                  prjStartInput.removeAttribute("min");
                }
                if (prjClosedYmd) {
                  prjStartInput.max = prjClosedYmd;
                } else {
                  prjStartInput.removeAttribute("max");
                }
                let start = normalizeYmd(prjStartInput.value);
                if (start && minStart && start < minStart) {
                  start = minStart;
                  prjStartInput.value = start;
                }
                if (start && prjClosedYmd && start > prjClosedYmd) {
                  prjStartInput.value = prjClosedYmd;
                  start = prjClosedYmd;
                }
              }

              if (!prjEndInput) return;
              const start = prjStartInput
                ? normalizeYmd(prjStartInput.value)
                : initialPrjStartYmd;
              if (start) {
                prjEndInput.min = start;
              } else if (minStart) {
                prjEndInput.min = minStart;
              } else {
                prjEndInput.removeAttribute("min");
              }
              if (prjClosedYmd) {
                prjEndInput.max = prjClosedYmd;
              } else {
                prjEndInput.removeAttribute("max");
              }
              let end = normalizeYmd(prjEndInput.value);
              if (start && end && end < start) {
                end = start;
                prjEndInput.value = end;
              }
              if (prjClosedYmd && end && end > prjClosedYmd) {
                prjEndInput.value = prjClosedYmd;
              }
            }

            function initDefaultEndDate() {
              if (!prjEndInput) return;
              let end = normalizeYmd(prjEndInput.value);
              if (!end && prjClosedYmd) {
                prjEndInput.value = prjClosedYmd;
              }
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

            function applyGroup(row) {
              if (!row) return;
              const gid = resolveGroupId(row);
              if (grpInput) {
                grpInput.value = row.grpName != null ? String(row.grpName) : "";
              }
              if (grpIdInput) {
                grpIdInput.value = gid != null ? String(gid) : "";
              }
            }

            /* ----- 프로젝트 내 그룹 선택 ----- */
            function closeGroupPicker() {
              if (!groupOverlay) return;
              groupOverlay.classList.remove("is-open");
              groupOverlay.setAttribute("aria-hidden", "true");
            }

            function renderGroupList(rows, emptyMessage) {
              if (!groupPickList) return;
              groupPickList.innerHTML = "";
              if (!rows.length) {
                const p = document.createElement("p");
                p.className = "mem-picker__empty";
                p.textContent = emptyMessage || "선택할 그룹이 없습니다.";
                groupPickList.appendChild(p);
                return;
              }
              let rendered = 0;
              rows.forEach(function (r) {
                const gid = resolveGroupId(r);
                if (gid == null) return;
                rendered += 1;
                const btn = document.createElement("button");
                btn.type = "button";
                btn.className = "mem-picker__item";
                btn.dataset.grpId = String(gid);
                btn.dataset.grpName = r.grpName || "";
                const main = document.createElement("span");
                main.textContent = r.grpName || "(이름 없음)";
                btn.appendChild(main);
                const sub = document.createElement("span");
                sub.className = "mem-picker__item-sub";
                sub.textContent = "그룹 ID: " + String(gid);
                btn.appendChild(sub);
                btn.addEventListener("click", function () {
                  applyGroup(r);
                  closeGroupPicker();
                });
                groupPickList.appendChild(btn);
              });
              if (rendered === 0) {
                const p = document.createElement("p");
                p.className = "mem-picker__empty";
                p.textContent = "표시할 그룹 정보가 없습니다.";
                groupPickList.appendChild(p);
              }
            }

            function refreshGroupListView() {
              const filtered = filterGroups(allGroups);
              renderGroupList(
                filtered,
                allGroups.length
                  ? "검색 결과가 없습니다."
                  : "선택할 그룹이 없습니다.",
              );
            }

            async function openGroupPicker() {
			 
              if (!groupOverlay || !groupPickList) return;
              const pid = Number(prjId);
              if (isNaN(pid)) return;
              resetGroupSearch();
              groupPickList.innerHTML = "불러오는 중…";
              groupOverlay.classList.add("is-open");
              groupOverlay.setAttribute("aria-hidden", "false");
              try {
                const res = await fetch(
                  "/project/member/projectGroups",
                  { credentials: "same-origin" },
                );
                const data = await res.json().catch(function () {
                  return {};
                });
                allGroups =
                  data && Array.isArray(data.content) ? data.content : [];
                refreshGroupListView();
              } catch (e) {
                allGroups = [];
                groupPickList.innerHTML = "";
                const p = document.createElement("p");
                p.className = "mem-picker__empty";
                p.textContent = "그룹 목록을 불러오지 못했습니다.";
                groupPickList.appendChild(p);
              }
            }

            if (groupSearchType) {
              groupSearchType.addEventListener("change", refreshGroupListView);
            }
            if (groupSearchKeyword) {
              groupSearchKeyword.addEventListener("input", refreshGroupListView);
            }

            if (groupPickList) {
              attachPickerOpen(grpInput, openGroupPicker);
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

            if (prjStartInput) {
              prjStartInput.addEventListener("change", syncPeriodConstraints);
              prjStartInput.addEventListener("input", syncPeriodConstraints);
            }
            if (prjEndInput) {
              prjEndInput.addEventListener("change", syncPeriodConstraints);
              prjEndInput.addEventListener("input", syncPeriodConstraints);
            }
            initDefaultEndDate();
            syncPeriodConstraints();

            function buildCsrfHeaders() {
              const headers = { "Content-Type": "application/json" };
              const tokenMeta = document.querySelector('meta[name="_csrf"]');
              const headerMeta = document.querySelector(
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

            function alertMsg(msg, title) {
              if (window.MemberQuestionModal && window.MemberQuestionModal.alert) {
                return window.MemberQuestionModal.alert(msg, title || "알림");
              }
              return Promise.resolve();
            }

            function confirmMsg(msg, title) {
              if (window.MemberQuestionModal && window.MemberQuestionModal.confirm) {
                return window.MemberQuestionModal.confirm(msg, title || "확인");
              }
              return Promise.resolve(false);
            }

            function isEditStartChanged(currentStartYmd) {
              return (
                normalizeYmd(currentStartYmd) !== normalizeYmd(initialPrjStartYmd)
              );
            }

            function isEditEndChanged(currentEndYmd) {
              return normalizeYmd(currentEndYmd) !== normalizeYmd(initialPrjEndYmd);
            }

            function isEditGrpChanged(currentGrpId) {
              const init =
                initialGrpId != null && initialGrpId !== ""
                  ? Number(initialGrpId)
                  : NaN;
              const cur = Number(currentGrpId);
              if (isNaN(init) || isNaN(cur)) {
                return false;
              }
              return init !== cur;
            }

            function buildEditConfirmMessage(
              startYmd,
              endYmd,
              grpName,
              startChanged,
              endChanged,
              grpChanged,
            ) {
              const lines = ["변경사항은 다음과 같습니다."];
              if (startChanged) {
                lines.push("- 투입 시작일: " + (startYmd || "(없음)"));
              }
              if (endChanged) {
                const display = endYmd ? endYmd : "(없음)";
                lines.push("- 종료일자: " + display);
              }
              if (grpChanged) {
                const name =
                  grpName && String(grpName).trim()
                    ? String(grpName).trim()
                    : "(이름 없음)";
                lines.push("- 소속그룹: " + name);
              }
              return lines.join("\n");
            }

            const saveBtn = document.getElementById("btnMemberSave");
            if (saveBtn) {
              saveBtn.addEventListener("click", async function () {
                const pid = Number(prjId);
                const uid = Number(pageUserId);
                const oldGid = Number(pageOldGrpId);
                const gidRaw = grpIdInput && grpIdInput.value
                  ? grpIdInput.value.trim()
                  : "";
                const gid = gidRaw ? Number(gidRaw) : NaN;
                const start = prjStartInput
                  ? normalizeYmd(prjStartInput.value)
                  : "";
                const end = prjEndInput
                  ? normalizeYmd(prjEndInput.value)
                  : "";

                if (isNaN(pid)) {
                  await alertMsg("프로젝트 ID가 올바르지 않습니다.", "알림");
                  return;
                }
                if (isNaN(uid)) {
                  await alertMsg("구성원 정보가 올바르지 않습니다.", "알림");
                  return;
                }
                if (isNaN(oldGid)) {
                  await alertMsg("기존 그룹 정보가 올바르지 않습니다.", "알림");
                  return;
                }
                if (!gidRaw || isNaN(gid)) {
                  await alertMsg("소속 그룹을 선택하세요.", "알림");
                  return;
                }
                if (!start) {
                  await alertMsg("프로젝트 투입 시작일을 입력하세요.", "알림");
                  return;
                }
                const minStart = minStartYmd();
                if (minStart && start < minStart) {
                  await alertMsg(
                    "프로젝트 투입일은 " + minStart + " 이후로만 지정할 수 있습니다.",
                    "알림",
                  );
                  return;
                }
                if (prjClosedYmd && start > prjClosedYmd) {
                  await alertMsg(
                    "프로젝트 투입 시작일은 프로젝트 종료일(" +
                      prjClosedYmd +
                      ") 이전이어야 합니다.",
                    "알림",
                  );
                  return;
                }
                if (end && end < start) {
                  await alertMsg(
                    "투입 종료일은 투입 시작일 이후여야 합니다.",
                    "알림",
                  );
                  return;
                }
                if (prjClosedYmd && end && end > prjClosedYmd) {
                  await alertMsg(
                    "투입 종료일은 프로젝트 종료일(" +
                      prjClosedYmd +
                      ") 이후로 지정할 수 없습니다.",
                    "알림",
                  );
                  return;
                }

                const grpName = grpInput
                  ? String(grpInput.value || "").trim()
                  : "";
                const startChanged = isEditStartChanged(start);
                const endChanged = isEditEndChanged(end);
                const grpChanged = isEditGrpChanged(gid);
                if (!startChanged && !endChanged && !grpChanged) {
                  await alertMsg("변경된 내용이 없습니다.", "알림");
                  return;
                }

                const confirmed = await confirmMsg(
                  buildEditConfirmMessage(
                    start,
                    end,
                    grpName,
                    startChanged,
                    endChanged,
                    grpChanged,
                  ),
                  "확인",
                );
                if (!confirmed) {
                  return;
                }

                const payload = {
                  userId: uid,
                  oldGrpId: oldGid,
                  grpId: gid,
                  prjStartDate: start,
                  prjEndDate: end || null,
                };

                saveBtn.disabled = true;
                try {
                  const res = await fetch("/project/member/updateMember", {
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
                        : "구성원 수정 중 오류가 발생했습니다.");
                    await alertMsg(errText, "오류");
                    return;
                  }
                  await alertMsg("구성원 정보가 수정되었습니다.", "알림");
                  window.location.href =
                    "/project/member/info?userId=" +
                    encodeURIComponent(String(uid)) +
                    "&grpId=" +
                    encodeURIComponent(String(gid));
                } catch (e) {
                  await alertMsg("구성원 수정 중 오류가 발생했습니다.", "오류");
                } finally {
                  saveBtn.disabled = false;
                }
              });
            }
          })();
