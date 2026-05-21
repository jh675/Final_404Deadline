// 프로필 이미지 임시 저장할 전역 변수
let pendingProfileFile = null;
let isProfileDeleted = false;

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
				align: 'center' 
			},
			{ 
				header: '계정권한',
				name: 'adminNm', 
				width: 120, 
				align: 'center', 
				sortable: true,
			},
            { 
				header: '역할',
				name: 'prjManagerNm', 
				width: 120, 
				align: 'center', 
				sortable: true,
				
				formatter: ({ value }) => {
				    const roleMap = {
				        '활성': '프로젝트매니저',
				        '비활성': '사원'
				    };

				    return roleMap[value] || value;
				}
			},
            { 
				header: '이름', 
				name: 'name', 
				width: 120, 
				align: 'center' 
			},
            { 
				header: '전화번호', 
				name: 'tel', 
				width: 150, 
				align: 'center' 
			}, 
            { 
				header: '이메일', 
				width: 260, 
				name: 'email', 
				align: 'center' 
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
                    return `<button type="button" class="btn btn-sm btn-outline-primary edit-btn" data-id="${row.id}">수정</button>`;
                }
            }
        ],
        pageOptions: { useClient: true, perPage: 10 }
    });

    // 수정 버튼 이벤트
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('edit-btn')) {
            openUpdateModal(e.target.dataset.id);
        }
    });

    // 저장 버튼 공통 처리
    document.getElementById('saveBtn').addEventListener('click', async function() {
        const mode = this.dataset.mode;

		// 이전 에러 상태 초기화 (아이디 중복 메시지도 기본 메시지로 원상복구)
        const inputs = ['login', 'name', 'hireDate'];
        inputs.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.remove('is-invalid');
        });
        const loginError = document.getElementById('loginError');
        if (loginError) loginError.textContent = '아이디를 입력해주세요.';

        // 필수 항목 유효성 검사 (빈칸 체크)
        let isValid = true;
		
        // 필수 항목 유효성 검사 (빈칸 체크)
        const loginInput = document.getElementById('login');
        const nameInput = document.getElementById('name');
        const hireDateInput = document.getElementById('hireDate');

        // 아이디 검사
        if (!loginInput.value.trim()) {
			loginInput.classList.add('is-invalid');
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
		
        // adminCd(권한)와 bizNo(기업번호)는 백엔드에서 강제로 세팅하므로 제외
        const body = {
            id: document.getElementById('userId').value,
            login: document.getElementById('login').value,
            name: document.getElementById('name').value,
            email: document.getElementById('email').value,
            tel: document.getElementById('tel').value,
            statusCd: document.querySelector('input[name="statusCd"]:checked').value,
            prjManagerCd: document.querySelector('input[name="prjManagerCd"]:checked').value,
            hireDate: document.getElementById('hireDate').value,
            genderCd: document.querySelector('input[name="genderCd"]:checked').value,
			mcpCd: document.querySelector('input[name="mcpCd"]:checked').value
        };

        const url = mode === 'insert' ? '/cadmin/userInsert' : '/cadmin/userUpdate';
        const method = mode === 'insert' ? 'POST' : 'PUT';

        const response = await csrfFetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
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
            try {
                if (isProfileDeleted && finalUserId) {
                    await csrfFetch(`/cadmin/user/profile/${finalUserId}`, { method: "DELETE" });
                } else if (pendingProfileFile && finalUserId) {
                    const formData = new FormData();
                    formData.append("userId", finalUserId);
                    formData.append("file", pendingProfileFile);
                    await csrfFetch("/cadmin/user/profile", { method: "POST", body: formData });
                }
                alert(mode === 'insert' ? '회원 등록이 완료되었습니다.' : '회원 수정이 완료되었습니다.');
                location.reload();
            } catch (error) {
                console.error(error);
                alert("회원 정보는 저장되었으나, 프로필 이미지 처리에 실패했습니다.");
                location.reload();
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
}); // DOMContentLoaded 닫기

// 프로필 이미지 로드
async function loadProfileImage(userId) {
    resetProfileImageUI();
    const img = document.getElementById("profilePreview");
    const emptyText = document.getElementById("emptyImageText");

    try {
        const res = await csrfFetch(`/cadmin/user/profile/${userId}`);
        if (res.ok) {
            const text = await res.text();
            if (text) {
                const data = JSON.parse(text);
                if (data && data.id) {
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

// 파일 선택 버튼 클릭
document.getElementById('uploadBtn').addEventListener('click', () => {
    document.getElementById('profileImage').click();
});

// 파일 업로드 처리 (비율 검증)
document.getElementById('profileImage').addEventListener('change', async function() {
    const file = this.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function(e) {
        const imgObj = new Image();
        imgObj.src = e.target.result;
        imgObj.onload = function() {
            const ratio = Math.round((imgObj.width / imgObj.height) * 100) / 100;
            if (ratio < 0.7 || ratio > 0.8) {
                document.getElementById('wrongImageSize').innerHTML = `사진 비율이 맞지 않습니다.<br>현재 이미지 크기: ${imgObj.width}px x ${imgObj.height}px<br>비율: ${ratio}`;
                document.getElementById('profileImage').value = '';
                return;
            }
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

// 이미지 삭제 버튼
document.getElementById('deleteImageBtn').addEventListener('click', function() {
    pendingProfileFile = null;
    isProfileDeleted = true;
    document.getElementById('profileImage').value = '';
    resetProfileImageUI();
});

function resetProfileImageUI() {
    const img = document.getElementById("profilePreview");
    const emptyText = document.getElementById("emptyImageText");
    img.src = "";
    img.classList.add("d-none");
    emptyText.style.display = "";
}

// 등록 모달 열기
window.openInsertModal = function() {
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

    // 신규 등록 시 기업번호 칸을 백엔드에서 전달받은 변수(또는 공백)로 채움
    document.getElementById('bizNoView').value = currentCaBizNo;

    document.getElementById('login').readOnly = false;
    document.getElementById('saveBtn').dataset.mode = 'insert';
    document.getElementById('saveBtn').textContent = '등록';
	document.getElementById('mcpActive').checked = true;

    const modal = new bootstrap.Modal(document.getElementById('userInsert'));
    modal.show();
}

// 수정 모달 열기
window.openUpdateModal = function(id) {
    const row = userData.find(user => Number(user.id) === Number(id));
    if (!row) {
        alert('사용자 정보를 찾을 수 없습니다.');
        return;
    }

    pendingProfileFile = null;
    isProfileDeleted = false;
    resetProfileImageUI();

	// 모든 에러 상태(빨간 테두리) 지우기
    ['login', 'name', 'hireDate', 'adminCd', 'bizNo'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.remove('is-invalid');
    });
	
    document.getElementById('modalTitle').textContent = '회원 정보 수정';
    document.getElementById('userId').value = row.id ?? '';
    document.getElementById('login').value = row.login ?? '';
    document.getElementById('bizNoView').value = row.bizNo ?? currentCaBizNo;
    document.getElementById('name').value = row.name ?? '';
    document.getElementById('email').value = row.email ?? '';
    document.getElementById('tel').value = row.tel ?? '';
    document.getElementById('hireDate').value = row.hireDate ?? '';

    if (row.genderCd === '02GENDER') {
        document.getElementById('female').checked = true;
    } else {
        document.getElementById('male').checked = true;
    }

    if (row.prjManagerNm === '프로젝트 매니저') {
        document.getElementById('projectManager').checked = true;
    } else {
        document.getElementById('teamMember').checked = true;
    }

    if (row.statusNm === '비활성') {
        document.getElementById('statusInactive').checked = true;
    } else {
        document.getElementById('statusActive').checked = true;
    }
	
	if (row.mcpCd === '02ACTIVE') {
	    document.getElementById('mcpInactive').checked = true;
	} else {
	    document.getElementById('mcpActive').checked = true;
	}

    document.getElementById('login').readOnly = true;
    document.getElementById('saveBtn').dataset.mode = 'update';
    document.getElementById('saveBtn').textContent = '수정';

    loadProfileImage(row.id);

    const modal = new bootstrap.Modal(document.getElementById('userInsert'));
    modal.show();
}

// 검색 조건 검증
const searchForm = document.getElementById('searchForm');
searchForm.addEventListener('submit', function(e) {
    const searchType = document.getElementById('searchType').value;
    const keyword = document.getElementById('keyword').value.trim();
    const warning = document.getElementById('searchWarning');

    if (keyword !== '' && searchType === '') {
        e.preventDefault();
        document.getElementById('searchType').classList.add('is-invalid');
        warning.classList.remove('d-none');
        return;
    }

    if (searchType !== '' && keyword === '') {
        e.preventDefault();
        document.getElementById('searchType').classList.remove('is-invalid');
        warning.innerText = '검색어를 입력해주세요.';
        warning.classList.remove('d-none');
        return;
    }

    document.getElementById('searchType').classList.remove('is-invalid');
    warning.classList.add('d-none');
});