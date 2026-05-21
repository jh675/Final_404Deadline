// 프로필 이미지 임시 저장할 전역 변수
let pendingProfileFile = null; // 업로드 대기 중인 파일 객체
let isProfileDeleted = false;  // 기존 이미지 삭제 대기 플래그

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

	// 수정 함수 호출을 위한 연결
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('edit-btn')) {
            const id = e.target.dataset.id;
            openUpdateModal(id);
        }
    });
	
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
	
	await loadCompanyList();
	
	// 회원권한 변경 이벤트
	document.getElementById('adminCd').addEventListener('change', function () {
	    changeRoleArea(this.value);
	});
	
	// 소속기업 변경 시 기업번호 input에 값 세팅
		document.getElementById('bizNo').addEventListener('change', function() {
		    document.getElementById('bizNoView').value = this.value;
		});
		
	// 저장 버튼 공통 처리
    document.getElementById('saveBtn').addEventListener('click', async function() {
		// 등록, 수정 판별
        const mode = this.dataset.mode;
		
		// 이전 에러 상태 초기화 (아이디 중복 메시지도 기본 메시지로 원상복구)
        const inputs = ['login', 'name', 'hireDate', 'adminCd', 'bizNo'];
        inputs.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.remove('is-invalid');
        });
        const loginError = document.getElementById('loginError');
        if (loginError) loginError.textContent = '아이디를 입력해주세요.';

        // 필수 항목 유효성 검사 (빈칸 체크)
        let isValid = true;
        
        const loginInput = document.getElementById('login');
        const nameInput = document.getElementById('name');
        const hireDateInput = document.getElementById('hireDate');
        const adminCdSelect = document.getElementById('adminCd'); 
        const bizNoSelect = document.getElementById('bizNo'); 

        // 아이디 검사
        if (!loginInput.value.trim()) {
            loginInput.classList.add('is-invalid');
            isValid = false;
        }
        // 회원 권한 검사 (존재할 경우)
        if (adminCdSelect && !adminCdSelect.value) {
            adminCdSelect.classList.add('is-invalid');
            isValid = false;
        }
        // 소속기업 검사 (select 태그로 존재할 경우)
        if (bizNoSelect && bizNoSelect.tagName === 'SELECT' && !bizNoSelect.value) {
            bizNoSelect.classList.add('is-invalid');
            isValid = false;
        }
        // 이름 검사
        if (!nameInput.value.trim()) {
            nameInput.classList.add('is-invalid');
            isValid = false;
        }
        // 고용일자 검사
        if (!hireDateInput.value) {
            hireDateInput.classList.add('is-invalid');
            isValid = false;
        }

        // 하나라도 비어있다면 폼 제출 중단
        if (!isValid) return;
		
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
			genderCd: document.querySelector('input[name="genderCd"]:checked').value,
			mcpCd: document.querySelector('input[name="mcpCd"]:checked').value
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
		
		let isSuccess = false;
		let finalUserId = document.getElementById('userId').value;

		if (mode === 'insert') {
            if (result.result === 'DUPLICATE') {
                loginInput.classList.add('is-invalid');
                if (loginError) loginError.textContent = '해당 기업에 이미 사용 중인 아이디입니다.';
                loginInput.focus();
                return; 
            }
			if (result.result === 'SUCCESS' && result.id) { 
		    	isSuccess = true;
		        finalUserId = result.id; 
			}
		} else { // mode === 'update' 인 경우
		    if (result.result === 'SUCCESS') {
		        isSuccess = true;
			}
		}

		if (isSuccess) {
	        // 프로필 이미지 동기화
	        try {
	            if (isProfileDeleted && finalUserId) {
	                // 삭제 대기 중이면 기존 이미지 지우기
	                await csrfFetch(`/admin/user/profile/${finalUserId}`, { method: "DELETE" });
	                
	            } else if (pendingProfileFile && finalUserId) {
	                // 업로드 대기 중인 새 파일이 있으면 업로드하기
	                const formData = new FormData();
	                formData.append("userId", finalUserId);
	                formData.append("file", pendingProfileFile);
	                await csrfFetch("/admin/user/profile", { method: "POST", body: formData });
	            }
	            
	            // 모든 작업 완료
	            alert(mode === 'insert' ? '회원 등록이 완료되었습니다.' : '회원 수정이 완료되었습니다.');
	            location.reload();
	            
	        } catch (error) {
	            console.error(error);
	            alert("회원 정보는 저장되었으나, 프로필 이미지 처리에 실패했습니다.");
	            location.reload(); // 일단 정보는 저장되었으므로 리로드
	        }
	
	    } else {
	        alert('처리 중 오류가 발생했습니다.');
	    }
	});

	// 입력창에 값을 입력하면 빨간 경고 테두리와 메시지를 지워줌
    ['login', 'name', 'hireDate', 'adminCd', 'bizNo'].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('input', function() {
                this.classList.remove('is-invalid');
            });
            el.addEventListener('change', function() {
                this.classList.remove('is-invalid');
            });
        }
    });
}); // grid와 페이징 생성

// 특정 유저의 프로필 이미지 불러오기
async function loadProfileImage(userId) {
    resetProfileImageUI(); // 일단 비우기
    const img = document.getElementById("profilePreview");
    const emptyText = document.getElementById("emptyImageText");

    try {
        const res = await csrfFetch(`/admin/user/profile/${userId}`);
        if (res.ok) {
            // 응답이 존재하면 JSON 파싱
            const text = await res.text();
            if(text) {
                const data = JSON.parse(text);
                if (data && data.id) {
                    // 브라우저 캐시 방지를 위해 파라미터(t) 추가
                    img.src = `/download/${data.id}?t=${new Date().getTime()}`; 
                    img.classList.remove("d-none");
                    emptyText.style.display = "none";
                }
            }
        }
    } catch (e) {
        console.error("프로필 이미지 로드 실패", e);
    }
}

// 파일 선택 버튼 클릭 (유효성 검사 추가)
document.getElementById('uploadBtn').addEventListener('click', function () {
    document.getElementById('profileImage').click();
});

// 파일 업로드 처리
document.getElementById('profileImage').addEventListener('change', async function () {
    const file = this.files[0];
    if (!file) return;

	// 이미지 가로/세로 비율 검사 (3:4 비율 체크)
    const reader = new FileReader();
    reader.onload = function(e) {
        // 임시 이미지 객체를 만들어 해상도를 체크.
        const imgObj = new Image();
        imgObj.src = e.target.result;
        
        imgObj.onload = function() {
            const width = imgObj.width;
            const height = imgObj.height;
            
            // 비율 계산 
            const ratio = Math.round((width / height) * 100) / 100;
            // 3:4 비율은 0.75 (0.7 ~ 0.8 사이면 허용)
            if (ratio < 0.7 || ratio > 0.8) {
                document.getElementById('wrongImageSize').innerHTML = `사진 비율이 맞지 않습니다.<br>
																	   현재 이미지 크기: ${width}px x ${height}px<br>
																	   비율: ${ratio}`;
                document.getElementById('profileImage').value = ''; // 선택 취소
                return;
            }

            // 검증 통과 프로필 이미지 등록
			document.getElementById('wrongImageSize').innerHTML = '';
            pendingProfileFile = file;
            isProfileDeleted = false; 

            const previewImg = document.getElementById("profilePreview");
            previewImg.src = e.target.result;
            previewImg.classList.remove("d-none");
            document.getElementById("emptyImageText").style.display = "none";
        };
    };
    reader.readAsDataURL(file);
});

// [삭제 버튼] 클릭 시 (서버 삭제 X, 로컬 UI만 지우고 변수에 표시)
document.getElementById('deleteImageBtn').addEventListener('click', function () {
    // 1. 대기열 비우기 및 삭제 플래그 켜기
    pendingProfileFile = null;
    isProfileDeleted = true;
    
    // 2. UI 및 input 초기화
    document.getElementById('profileImage').value = ''; 
    resetProfileImageUI(); 
});

// 프로필 UI 초기화 (이미지 숨기고 텍스트 표시)
function resetProfileImageUI() {
    const img = document.getElementById("profilePreview");
    const emptyText = document.getElementById("emptyImageText");
    img.src = "";
    img.classList.add("d-none");
    emptyText.style.display = "";
}


// 등록 모달 함수
window.openInsertModal = function() {
	
	// 신규 등록 시 이미지 상태 초기화
	pendingProfileFile = null;
	isProfileDeleted = false;
	resetProfileImageUI();
	
	// 모든 에러 상태(빨간 테두리) 지우기
    ['login', 'name', 'hireDate', 'adminCd', 'bizNo'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.remove('is-invalid');
    });
		
    document.getElementById('modalTitle').textContent = '회원 정보 등록';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('login').readOnly = false;
    document.getElementById('bizNoView').readOnly = true; // 기업번호는 항상 readonly
    document.getElementById('saveBtn').dataset.mode = 'insert';
    document.getElementById('saveBtn').textContent = '등록';
    changeRoleArea(document.getElementById('adminCd').value);
	document.getElementById('mcpActive').checked = true;
    
    // 신규 등록 시 프로필 이미지 초기화 (초기화면 유지)
    resetProfileImageUI();
    
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
	
	// 신규 등록 시 이미지 상태 초기화
	pendingProfileFile = null;
	isProfileDeleted = false;
	resetProfileImageUI();
	
	// 모든 에러 상태(빨간 테두리) 지우기
	['login', 'name', 'hireDate', 'adminCd', 'bizNo'].forEach(id => {
	    const el = document.getElementById(id);
	    if (el) el.classList.remove('is-invalid');
	});	
    
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
	
	// 비밀번호 초기화 (mcpCd) 라디오
	if (row.mcpCd === '02ACTIVE') {
	    document.getElementById('mcpInactive').checked = true;
	} else {
	    document.getElementById('mcpActive').checked = true;
	}

    // 수정 모드에서는 readonly
    document.getElementById('login').readOnly = true;

    // 기업번호는 항상 readonly
    document.getElementById('bizNoView').readOnly = true;

    // 저장버튼 mode
    document.getElementById('saveBtn').dataset.mode = 'update';

    // 저장 버튼 텍스트 변경
    document.getElementById('saveBtn').textContent = '수정';

	// 해당 유저의 프로필 이미지 불러오기
    loadProfileImage(row.id);

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