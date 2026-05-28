window.RoleQuestionModal = (function () {
          var modalEl, titleEl, msgEl, btnCancel, btnConfirm;
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
            if (!cacheElements() || typeof bootstrap === "undefined" || !bootstrap.Modal) return null;
            return bootstrap.Modal.getOrCreateInstance(modalEl);
          }
          function open(opts) {
            var mode = opts.mode || "alert";
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
                modalEl.removeEventListener("hidden.bs.modal", onModalHidden);
                resolve(value);
              }
              function onModalHidden() {
                if (mode === "confirm") settle(false);
                else settle(undefined);
              }
              titleEl.textContent =
                opts.title != null && opts.title !== ""
                  ? opts.title
                  : mode === "confirm" ? "확인" : "알림";
              msgEl.textContent = opts.message != null ? String(opts.message) : "";
              if (mode === "alert") btnCancel.classList.add("d-none");
              else btnCancel.classList.remove("d-none");
              modalEl.addEventListener("hidden.bs.modal", onModalHidden);
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
          return {
            alert: function (message, title) {
              return open({ message: message, title: title, mode: "alert" });
            },
            confirm: function (message, title) {
              return open({ message: message, title: title, mode: "confirm" });
            },
          };
        })();
