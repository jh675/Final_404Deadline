document.addEventListener('DOMContentLoaded', function() {
    // tui 객체가 존재하는지 안전하게 체크한 후 테마 적용
    if (typeof tui !== 'undefined' && tui.Grid) {
        tui.Grid.applyTheme('default', {
            grid: {
                border: '#dee2e6',
                text: '#495057'
            },
            cell: {
                normal: {
                    background: '#fff',
                    border: '#dee2e6',
                    showVerticalBorder: false
                },
                header: {
                    background: '#f8f9fa',
                    text: '#212529',
                    border: '#dee2e6'
                },
                rowHeader: {
                    background: '#f8f9fa',
                    border: '#dee2e6'
                },
                selectedHeader: {
                    background: '#e9ecef'
                }
            },
            row: {
                hover: {
                    background: '#f1f3f5'
                }
            }
        });
    }
});