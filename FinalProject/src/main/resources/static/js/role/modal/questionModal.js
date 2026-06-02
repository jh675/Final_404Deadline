window.RoleQuestionModal = (function () {
          var modalEl, titleEl, msgEl, btnCancel, btnConfirm;
          var modalQueue = Promise.resolve();

          function cacheElements() {
            if (!modalEl) {
              modalEl = document.getElementById("roleQuestionModal");
              if (!modalEl) return false;
              titleEl = document.getElementById("roleQuestionModalTitle");
              msgEl = document.getElementById("roleQuestionModalMessage");
              btnCancel = document.getElementById("roleQuestionModalCancelBtn");
              btnConfirm = document.getElementById("roleQuestionModalConfirmBtn");
            }
            return true;
          }

          function getInstance() {
            if (!cacheElements() || typeof bootstrap === "undefined" || !bootstrap.Modal) {
              return null;
            }
            return bootstrap.Modal.getOrCreateInstance(modalEl);
          }

          /** show 클래스는 hide 직후 사라지므로 backdrop(body.modal-open)까지 함께 확인 */
          function isModalVisible() {
            if (!modalEl) return false;
            return (
              modalEl.classList.contains("show") ||
              document.body.classList.contains("modal-open")
            );
          }

          function waitUntilFullyHidden() {
            if (!cacheElements() || !isModalVisible()) {
              return Promise.resolve();
            }
            return new Promise(function (resolve) {
              modalEl.addEventListener(
                "hidden.bs.modal",
                function handler() {
                  modalEl.removeEventListener("hidden.bs.modal", handler);
                  resolve();
                },
              );
            });
          }

          function presentModal(opts, mode) {
            return new Promise(function (resolve) {
              var inst = getInstance();
              if (!inst || !titleEl || !msgEl) {
                resolve(mode === "confirm" ? false : undefined);
                return;
              }
              var settled = false;
              function settle(value) {
                if (settled) return;
                settled = true;
                modalEl.removeEventListener("hidden.bs.modal", onDismiss);
                resolve(value);
              }
              function onDismiss() {
                if (settled) return;
                if (mode === "confirm") settle(false);
                else settle(undefined);
              }
              titleEl.textContent =
                opts.title != null && opts.title !== ""
                  ? opts.title
                  : mode === "confirm"
                    ? "확인"
                    : "알림";
              msgEl.textContent = opts.message != null ? String(opts.message) : "";
              if (mode === "alert") btnCancel.classList.add("d-none");
              else btnCancel.classList.remove("d-none");
              modalEl.addEventListener("hidden.bs.modal", onDismiss);
              btnCancel.onclick = function () {
                if (mode === "confirm") settle(false);
                inst.hide();
              };
              btnConfirm.onclick = function () {
                if (mode === "confirm") settle(true);
                else settle(undefined);
                inst.hide();
              };
              inst.show();
            });
          }

          function open(opts) {
            var mode = opts.mode || "alert";
            var operation = modalQueue
              .then(function () {
                return waitUntilFullyHidden();
              })
              .then(function () {
                return presentModal(opts, mode);
              })
              .then(function (result) {
                return waitUntilFullyHidden().then(function () {
                  return result;
                });
              });
            modalQueue = operation.catch(function () {});
            return operation;
          }

          return {
            alert: function (message, title) {
              return open({ message: message, title: title, mode: "alert" });
            },
            confirm: function (message, title) {
              return open({ message: message, title: title, mode: "confirm" });
            },
          };
        })();
