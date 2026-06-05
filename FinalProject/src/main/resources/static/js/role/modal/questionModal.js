window.RoleQuestionModal = (function () {
  var modalEl, titleEl, msgEl, btnCancel, btnConfirm, btnClose;
  var modalQueue = Promise.resolve();

  function cacheElements() {
    if (!modalEl) {
      modalEl = document.getElementById("roleQuestionModal");
      if (!modalEl) return false;
      titleEl = document.getElementById("roleQuestionModalTitle");
      msgEl = document.getElementById("roleQuestionModalMessage");
      btnCancel = document.getElementById("roleQuestionModalCancelBtn");
      btnConfirm = document.getElementById("roleQuestionModalConfirmBtn");
      btnClose = document.getElementById("roleQuestionModalCloseBtn");
    }
    return true;
  }

  function hide() {
    if (!modalEl) return;
    modalEl.classList.remove("is-open");
    modalEl.hidden = true;
    modalEl.setAttribute("aria-hidden", "true");
  }

  function show() {
    if (!modalEl) return;
    modalEl.hidden = false;
    modalEl.classList.add("is-open");
    modalEl.setAttribute("aria-hidden", "false");
  }

  function isModalVisible() {
    return modalEl && modalEl.classList.contains("is-open");
  }

  function waitUntilFullyHidden() {
    if (!isModalVisible()) {
      return Promise.resolve();
    }
    return new Promise(function (resolve) {
      var attempts = 0;
      (function poll() {
        if (!isModalVisible() || attempts > 20) {
          resolve();
          return;
        }
        attempts += 1;
        requestAnimationFrame(poll);
      })();
    });
  }

  function presentModal(opts, mode) {
    return new Promise(function (resolve) {
      if (!cacheElements() || !titleEl || !msgEl) {
        resolve(mode === "confirm" ? false : undefined);
        return;
      }

      var settled = false;
      function settle(value) {
        if (settled) return;
        settled = true;
        hide();
        resolve(value);
      }

      titleEl.textContent =
        opts.title != null && opts.title !== ""
          ? opts.title
          : mode === "confirm"
            ? "확인"
            : "알림";
      msgEl.textContent = opts.message != null ? String(opts.message) : "";

      if (mode === "alert") btnCancel.hidden = true;
      else btnCancel.hidden = false;

      btnCancel.onclick = function () {
        if (mode === "confirm") settle(false);
        else settle(undefined);
      };
      btnConfirm.onclick = function () {
        if (mode === "confirm") settle(true);
        else settle(undefined);
      };
      if (btnClose) {
        btnClose.onclick = function () {
          if (mode === "confirm") settle(false);
          else settle(undefined);
        };
      }

      modalEl.onclick = function (e) {
        if (e.target === modalEl) {
          if (mode === "confirm") settle(false);
          else settle(undefined);
        }
      };

      show();
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
