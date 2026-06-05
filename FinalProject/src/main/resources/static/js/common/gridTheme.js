document.addEventListener('DOMContentLoaded', function() {
	if (typeof tui === 'undefined' || !tui.Grid) return;

	var root = getComputedStyle(document.documentElement);
	function cssVar(name, fallback) {
		var v = root.getPropertyValue(name).trim();
		return v || fallback;
	}

	tui.Grid.applyTheme('default', {
		grid: {
			border: cssVar('--app-border-light', '#cbd5e1'), // 전체 테두리를 살짝 더 선명하게
			text: cssVar('--app-text', '#334155')
		},
		cell: {
			normal: {
				background: cssVar('--app-surface', '#ffffff'),
				border: cssVar('--app-border-light', '#cbd5e1'), // 가로 구분선을 조금 더 또렷하게
				showVerticalBorder: true
			},
			header: {
				background: cssVar('--app-surface-muted', '#e8f0fe'), // 헤더에 은은한 푸른빛(소프트 블루) 부여
				text: cssVar('--app-heading', '#1e293b'),
				border: cssVar('--app-border-light', '#cbd5e1'),
				showVerticalBorder: true
			},
			rowHeader: {
				background: cssVar('--app-surface-muted', '#e8f0fe'), // 체크박스/번호 열 헤더도 톤 통일
				border: cssVar('--app-border-light', '#cbd5e1'),
				showVerticalBorder: true
			},
			selectedHeader: {
				background: cssVar('--app-hover', '#d2e3fc') // 선택 시 살짝 더 진해지도록
			}
		},
		row: {
			hover: {
				background: cssVar('--app-hover', '#f1f5f9') // 마우스 올렸을 때 눈이 편안한 밝은 회색
			}
		}
	});
});