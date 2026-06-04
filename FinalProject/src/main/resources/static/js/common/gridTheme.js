document.addEventListener('DOMContentLoaded', function() {
	if (typeof tui === 'undefined' || !tui.Grid) return;

	var root = getComputedStyle(document.documentElement);
	function cssVar(name, fallback) {
		var v = root.getPropertyValue(name).trim();
		return v || fallback;
	}

	tui.Grid.applyTheme('default', {
		grid: {
			border: cssVar('--app-border-light', 'rgba(71, 85, 105, 0.2)'),
			text: cssVar('--app-text', '#334155')
		},
		cell: {
			normal: {
				background: cssVar('--app-surface', '#fff'),
				border: cssVar('--app-border-light', 'rgba(71, 85, 105, 0.2)'),
				showVerticalBorder: false
			},
			header: {
				background: cssVar('--app-surface-muted', '#f4f8fc'),
				text: cssVar('--app-heading', '#1e293b'),
				border: cssVar('--app-border-light', 'rgba(71, 85, 105, 0.2)')
			},
			rowHeader: {
				background: cssVar('--app-surface-muted', '#f4f8fc'),
				border: cssVar('--app-border-light', 'rgba(71, 85, 105, 0.2)')
			},
			selectedHeader: {
				background: cssVar('--app-hover', '#eef4f9')
			}
		},
		row: {
			hover: {
				background: cssVar('--app-hover', '#eef4f9')
			}
		}
	});
});
