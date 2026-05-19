document.addEventListener('DOMContentLoaded', function() {
    // Grid 생성
    const grid = new tui.Grid({
        el: document.getElementById('grid'),

        data: userData,

        scrollX: false,
        scrollY: false,

        bodyHeight: 'auto',
        rowHeight: 40,
        minBodyHeight: 200,

        columns: [

            {
                header: 'No',
                name: 'id',
                width: 80,
                align: 'center',
                sortable: true
            },

            {
                header: '아이디',
                name: 'login',
                width: 150,
                align: 'center',
            },

            {
                header: '회원 권한',
                name: 'adminNm',
                width: 120,
                align: 'center',
                sortable: true
            },

            {
                header: '소속기업',
                name: 'compNm',
                width: 180,
                align: 'center',
                sortable: true
            },

            {
                header: '이름',
                name: 'name',
                width: 120,
                align: 'center',
            },

            {
                header: '이메일',
                name: 'email',
            },

            {
                header: '전화번호',
                name: 'tel',
                width: 150,
                align: 'center',
            },

            {
                header: '활성여부',
                name: 'statusNm',
                width: 120,
                align: 'center',
                sortable: true
            },

            {
                header: '수정',
                name: 'edit',
                width: 100,
                align: 'center',

                formatter: ({ row }) => {
                    return `
									<button 
										type="button"
										class="btn btn-sm btn-outline-primary"
										onclick="location.href='/admin/userManage/update/${row.id}'">
										수정
									</button>
								`;
                }
            }
        ],

        // 페이징
        pageOptions: {
            useClient: true,
            perPage: 10
        }
    });

});

