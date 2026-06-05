window.GroupQuestionModal = (function () {
  var modalEl, titleEl, msgEl, btnCancel, btnConfirm, btnClose;

  function cacheElements() {
    if (!modalEl) {
      modalEl = document.getElementById("groupQuestionModal");
      if (!modalEl) return false;
      titleEl = document.getElementById("groupQuestionModalTitle");
      msgEl = document.getElementById("groupQuestionModalMessage");
      btnCancel = document.getElementById("groupQuestionModalCancelBtn");
      btnConfirm = document.getElementById("groupQuestionModalConfirmBtn");
      btnClose = document.getElementById("groupQuestionModalCloseBtn");
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

  function open(opts) {
    var mode = opts.mode || "alert";
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

  return {
    alert: function (message, title) {
      return open({ message: message, title: title, mode: "alert" });
    },
    confirm: function (message, title) {
      return open({ message: message, title: title, mode: "confirm" });
    },
  };
})();
