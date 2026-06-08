/**
 * 날짜 입력 — Flatpickr 자동 초기화 (프로젝트 생성 페이지와 동일 UI)
 */
(function () {
	var DEFAULT_OPTS = {
		locale: 'ko',
		dateFormat: 'Y-m-d',
		allowInput: false,
		disableMobile: true
	};

	var scanTimer;

	function wrapDateInput(input) {
		if (input.closest('.date-input-wrap')) {
			return input.closest('.date-input-wrap');
		}
		var wrap = document.createElement('div');
		wrap.className = 'date-input-wrap';
		input.parentNode.insertBefore(wrap, input);
		wrap.appendChild(input);
		return wrap;
	}

	function buildOptions(input) {
		var opts = Object.assign({}, DEFAULT_OPTS);
		var value = (input.value || '').trim();
		var min = input.getAttribute('min') || input.dataset.minDate || '';
		var max = input.getAttribute('max') || input.dataset.maxDate || '';

		if (value) {
			opts.defaultDate = value;
		}
		if (min) {
			opts.minDate = min;
		}
		if (max) {
			opts.maxDate = max;
		}
		if (input.dataset.fpDefault) {
			opts.defaultDate = input.dataset.fpDefault;
		}
		return opts;
	}

	function initFlatpickr(input) {
		if (typeof flatpickr === 'undefined') {
			return;
		}
		if (!input || input.dataset.fpManual === 'true') {
			return;
		}
		if (input._flatpickr || input.classList.contains('flatpickr-input')) {
			return;
		}

		wrapDateInput(input);

		if (!input.placeholder) {
			input.placeholder = '날짜 선택';
		}
		if (input.type === 'date') {
			input.type = 'text';
			input.setAttribute('autocomplete', 'off');
			input.readOnly = true;
		}

		var picker = flatpickr(input, buildOptions(input));
		input.dataset.fpInit = 'true';
		return picker;
	}

	function scan(root) {
		var scope = root || document;
		scope.querySelectorAll('input[type="date"]:not([data-fp-manual])').forEach(initFlatpickr);
		scope.querySelectorAll('.date-input-wrap > input[type="text"]:not([data-fp-manual])').forEach(function (input) {
			if (!input._flatpickr && !input.classList.contains('flatpickr-input')) {
				initFlatpickr(input);
			}
		});
	}

	function scheduleScan(root) {
		clearTimeout(scanTimer);
		scanTimer = setTimeout(function () {
			scan(root);
		}, 30);
	}

	function init() {
		scan();
		if (typeof MutationObserver !== 'undefined' && document.body) {
			var observer = new MutationObserver(function () {
				scheduleScan();
			});
			observer.observe(document.body, { childList: true, subtree: true });
		}
	}

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', init);
	} else {
		init();
	}

	window.initAppDateInputs = scan;
	window.wrapDateInputs = scan;
})();
