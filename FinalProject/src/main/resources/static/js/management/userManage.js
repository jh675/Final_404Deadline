document.addEventListener('DOMContentLoaded', async function() {
    // Grid 생성
    const grid = new tui.Grid({
        el: document.getElementById('grid'),

        data: userData,

        scrollX: true,
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
				width: 260,
                name: 'email',
				align: 'center',
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
				            class="btn btn-sm btn-outline-primary edit-btn"
				            data-id="${row.id}">
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

	// 수정 함수 불러오기 위한 연결
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('edit-btn')) {
            const id = e.target.dataset.id;
            openUpdateModal(id);
        }
    });

	await loadCompanyList();
	// 기업 목록 불러오는 함수
	async function loadCompanyList() {
	    const response = await csrfFetch('/admin/companyList');
	    const companyList = await response.json();
	    const select = document.getElementById('bizNo');
	    select.innerHTML = `
	        <option value="">선택</option>
	    `;
	    companyList.forEach(company => {
	        select.innerHTML += `
	            <option value="${company.bizNo}">
	                ${company.companyName}
	            </option>
	        `;
	    });
	}
	
	// 회원권한 변경 이벤트
	document.getElementById('adminCd').addEventListener('change', function () {

	    changeRoleArea(this.value);

	});
    
	// 저장 버튼 공통 처리
    document.getElementById('saveBtn').addEventListener('click', async function() {
		// 등록, 수정 판별
        const mode = this.dataset.mode;

        const body = {
			id: document.getElementById('userId').value,
		    bizNo: document.getElementById('bizNo').value,
		    login: document.getElementById('login').value,
		    name: document.getElementById('name').value,
		    email: document.getElementById('email').value,
		    tel: document.getElementById('tel').value,
		    adminCd: document.getElementById('adminCd').value,
		    statusCd: document.querySelector('input[name="statusCd"]:checked').value,
		    prjManagerCd: document.querySelector('input[name="prjManagerCd"]:checked').value,
			hireDate: document.getElementById('hireDate').value,
			genderCd: document.querySelector('input[name="genderCd"]:checked').value
        };

        let url = '';
        let method = '';

        if (mode === 'insert') {
            url = '/admin/userInsert';
            method = 'POST';
        } else {
            url = '/admin/userUpdate';
            method = 'PUT';
        }

        const response = await csrfFetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

		const result = await response.json();

		if (result.result === 'SUCCESS') {
		    alert(mode === 'insert'
		        ? '회원 등록이 완료되었습니다.'
		        : '회원 수정이 완료되었습니다.');

		    location.reload();

		} else {
		    alert('처리 중 오류가 발생했습니다.');
		}
    });

});

document.getElementById('uploadBtn').addEventListener('click', function () {
    document.getElementById('profileImage').click();
});

document.getElementById('profileImage').addEventListener('change', async function () {

	const file = this.files[0];
	   if (!file) return;

	   const userId = document.getElementById("userId").value; // 핵심

	   const formData = new FormData();
	   formData.append("userId", userId);
	   formData.append("file", file);

	   const res = await csrfFetch("/admin/user/profile", {
	       method: "POST",
	       body: formData
	   });

	   if (res.ok) {
	       alert("프로필 등록 완료");
	       location.reload();
	   }
});

document.getElementById('deleteImageBtn').addEventListener('click', async function () {

    const userId = document.getElementById("userId").value;

    const res = await csrfFetch(`/admin/user/profile/${userId}`, {
        method: "DELETE"
    });

    if (res.ok) {
        alert("삭제 완료");
        location.reload();
    }
});

const img = document.getElementById("profilePreview");

csrfFetch(`/admin/user/profile/${userId}`)
    .then(res => res.json())
    .then(data => {
        if (data) {
            img.src = `/download/${data.id}`;
            img.classList.remove("d-none");
            document.getElementById("emptyImageText").style.display = "none";
        }
    });
	
document.getElementById('bizNo').addEventListener('change', function() {
    document.getElementById('bizNoView').value = this.value;
});

// 등록 모달 함수
window.openInsertModal = function() {
    document.getElementById('modalTitle').textContent = '회원 정보 등록';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('login').readOnly = false;
    document.getElementById('saveBtn').dataset.mode = 'insert';
    document.getElementById('saveBtn').textContent = '등록';
	changeRoleArea(document.getElementById('adminCd').value);
    const modal = new bootstrap.Modal(document.getElementById('userInsert'));
    modal.show();
}

// 수정 모달 함수
window.openUpdateModal = function(id) {
    const row = userData.find(user => Number(user.id) === Number(id));

    if (!row) {
        alert('사용자 정보를 찾을 수 없습니다.');
        return;
    }

    // 제목 변경
    document.getElementById('modalTitle').textContent = '회원 정보 수정';

    // hidden id
    document.getElementById('userId').value = row.id ?? '';

/*	?? 문법은 아래 의미	
	if (row.id === null || row.id === undefined) {
	    document.getElementById('userId').value = '';
	} else {
	    document.getElementById('userId').value = row.id;
	}
*/  
  
	// 아이디
    document.getElementById('login').value = row.login ?? '';

    // 회원권한
    document.getElementById('adminCd').value = row.adminCd ?? '';
	changeRoleArea(row.adminCd);
	
    // 소속기업
	document.getElementById('bizNo').value = row.bizNo ?? '';
	document.getElementById('bizNoView').value = row.bizNo ?? '';

    // 이름
    document.getElementById('name').value = row.name ?? '';

    // 이메일
    document.getElementById('email').value = row.email ?? '';

    // 전화번호
    document.getElementById('tel').value = row.tel ?? '';
	
	// 등록일자
	document.getElementById('hireDate').value = row.hireDate ?? '';

	// 성별
	if (row.genderCd === '02GENDER') {
	    document.getElementById('female').checked = true;
	} else {
	    document.getElementById('male').checked = true;
	}
	
    // 역할 radio
    if (row.prjManagerNm === '활성') {
        document.getElementById('projectManager').checked = true;
    } else {
        document.getElementById('teamMember').checked = true;
    }

    // 계정상태 radio
    if (row.statusNm === '비활성') {
        document.getElementById('statusInactive').checked = true;
    } else {
        document.getElementById('statusActive').checked = true;
    }

    // 수정 모드에서는 readonly
    document.getElementById('login').readOnly = true;

    // 기업번호는 항상 readonly
    document.getElementById('bizNoView').readOnly = true;

    // 저장버튼 mode
    document.getElementById('saveBtn').dataset.mode = 'update';

    // 저장 버튼 텍스트 변경
    document.getElementById('saveBtn').textContent = '수정';

    // modal open
    const modal = new bootstrap.Modal(document.getElementById('userInsert'));
    modal.show();
}

// 기업관리자, 시스템 관리자 프로젝트 매니저 역할 고정
function changeRoleArea(adminCd) {
    const roleArea = document.getElementById('prjManagerArea');

    // 시스템관리자 / 기업관리자
    if (adminCd === '01ROLE' || adminCd === '02ROLE') {
        // 프로젝트 매니저 강제 선택
        document.getElementById('projectManager').checked = true;
        // 역할 영역 숨김
        roleArea.style.display = 'none';
    } else {
        // 역할 영역 표시
		document.getElementById('teamMember').checked = true;
        roleArea.style.display = '';
    }
}

// 검색 조건 없이 검색하려고 할 때
const searchForm = document.getElementById('searchForm');

searchForm.addEventListener('submit', function(e) {
    const searchType = document.getElementById('searchType').value;
    const keyword = document.getElementById('keyword').value.trim();
    const warning = document.getElementById('searchWarning');

    // 검색어는 있는데 검색조건이 없는 경우
    if (keyword !== '' && searchType === '') {
        e.preventDefault();
		document.getElementById('searchType').classList.add('is-invalid');
        warning.classList.remove('d-none');
        return;
    }
	
	// 검색조건은 있는데 검색어가 없는 경우
	if (searchType !== '' && keyword === '') {
	    e.preventDefault();
		document.getElementById('searchType').classList.remove('is-invalid');
	    warning.innerText = '검색어를 입력해주세요.';
	    warning.classList.remove('d-none');

	    return;
	}

    // 정상일 경우 경고 숨김
	document.getElementById('searchType').classList.remove('is-invalid');
    warning.classList.add('d-none');
});