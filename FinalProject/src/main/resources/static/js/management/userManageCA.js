// 프로필 이미지 임시 저장할 전역 변수
let pendingProfileFile = null;
let isProfileDeleted = false;

document.addEventListener('DOMContentLoaded', async function() {
    // Grid 생성
    const grid = new tui.Grid({
        el: document.getElementById('grid'),
        data: userData,
		autowidth: true,
        scrollX: false,
        scrollY: false,
        bodyHeight: 'auto',
        rowHeight: 40,
        minBodyHeight: 200,
        columns: [
            { 
				header: '아이디', 
				name: 'login', 
				align: 'center' 
			},
			{ 
				header: '계정권한',
				name: 'adminNm', 
				align: 'center', 
				sortable: true,
			},
            { 
				header: '역할',
				name: 'prjManagerNm', 
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
				align: 'center' 
			},
            { 
				header: '전화번호', 
				name: 'tel', 
				align: 'center' 
			}, 
            { 
				header: '이메일', 
				name: 'email', 
				align: 'center' 
			},
            { 
				header: '활성여부', 
				name: 'statusNm', 
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
		rowHeaders: ['checkbox', 'rowNum'],
        pageOptions: { useClient: true, perPage: 10 }
    });

    // 수정 버튼 이벤트
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('edit-btn')) {
            openUpdateModal(e.target.dataset.id);
        }
    });

	// 💡 1. 로딩 직후 체크박스 컬럼을 먼저 숨깁니다. (UX 기획 반영)
    grid.hideColumn('_checked');

    // 💡 2. 일괄작업 모드 켜기
    document.getElementById('toggleBulkModeBtn').addEventListener('click', function() {
        this.classList.add('d-none'); // 일괄작업 버튼 숨기기
        document.getElementById('bulkControls').classList.remove('d-none'); // 적용 컨트롤 보이기
        grid.showColumn('_checked'); // 🌟 그리드 체크박스 나타나기!
    });

    // 💡 3. 일괄작업 모드 취소
    document.getElementById('cancelBulkModeBtn').addEventListener('click', function() {
        document.getElementById('bulkControls').classList.add('d-none');
        document.getElementById('toggleBulkModeBtn').classList.remove('d-none');
        grid.uncheckAll(); // 체크된 것 모두 해제
        grid.hideColumn('_checked'); // 🌟 그리드 체크박스 숨기기!
    });
	
	// 💡 2. 일괄 처리 적용 버튼 이벤트
    document.getElementById('bulkApplyBtn').addEventListener('click', async function() {
        const actionVal = document.getElementById('bulkActionType').value;
        if (!actionVal) {
            alert('일괄 처리할 작업을 선택해주세요.');
            return;
        }

        // 체크된 행 데이터들 가져오기 (TUI Grid 내장 함수)
        const checkedRows = grid.getCheckedRows();
        if (checkedRows.length === 0) {
            alert('선택된 회원이 없습니다. 체크박스를 선택해주세요.');
            return;
        }

        // actionVal 분리 (예: 'status_01ACTIVE' -> type: 'status', value: '01ACTIVE')
        const [updateType, updateValue] = actionVal.split('_');

        // 🚨 UX 방어 로직: 관리자의 PM 권한을 해제하려고 할 때 차단
        if (updateType === 'prjManager' && updateValue === '02ACTIVE') {
            const hasAdmin = checkedRows.some(row => row.adminCd === '01ROLE' || row.adminCd === '02ROLE');
            if (hasAdmin) {
                alert('시스템/기업관리자의 프로젝트 매니저 권한은 해제할 수 없습니다.\n일반 사원만 선택해주세요.');
                return;
            }
        }

        if (!confirm(`선택한 ${checkedRows.length}명의 회원을 일괄 변경하시겠습니까?`)) {
            return;
        }

        // 체크된 회원들의 ID만 추출
        const userIds = checkedRows.map(row => row.id);

        // 서버로 보낼 Payload
        const payload = {
            ids: userIds,
            type: updateType, // 'status' 또는 'prjManager'
            value: updateValue // '01ACTIVE' 또는 '02ACTIVE'
        };

        try {
            const response = await csrfFetch('/cadmin/users/bulk-update', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const result = await response.json();
            if (result.result === 'SUCCESS') {
                alert('일괄 처리가 완료되었습니다.');
                location.reload();
            } else {
                alert('처리 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('Bulk Update Error:', error);
            alert('서버 통신 중 오류가 발생했습니다.');
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

let cropper = null;

// 이미지 자르기 모달 띄우기
document.getElementById('profileImage').addEventListener('change', function(e) {
    const file = this.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function(event) {
        // 원본 이미지를 자르기 모달로 전달
        document.getElementById('imageToCrop').src = event.target.result;
        
        // 자르기 모달 띄우기
        const cropModal = new bootstrap.Modal(document.getElementById('cropModal'));
        cropModal.show();
    };
    reader.readAsDataURL(file);
    
    // 같은 파일을 다시 선택해도 change 이벤트가 발생하도록 input 초기화
    this.value = ''; 
});

// 자르기 모달이 생성되면
document.getElementById('cropModal').addEventListener('shown.bs.modal', function () {
    const image = document.getElementById('imageToCrop');
    
    // 이전에 쓰던 자르기 데이터가 남아있다면 초기화
    if (cropper) {
        cropper.destroy();
    }
    
    // 3:4 비율로 세팅
    cropper = new Cropper(image, {
        aspectRatio: 3 / 4, // 비율 고정
        viewMode: 1,        // 자르기 범위가 캔버스 밖으로 나가지 않게
        dragMode: 'move',   // 마우스로 박스 대신 사진 자체를 움직이게 함
        autoCropArea: 0.8,  // 처음에 사진의 80% 크기로 박스 자동 생성
    });
});

// 잘라낸 결과물 저장 및 미리보기 갱신
document.getElementById('applyCropBtn').addEventListener('click', function() {
    if (!cropper) return;

    // 결과물을 300x400 해상도로 설정
    const canvas = cropper.getCroppedCanvas({
        width: 300,
        height: 400
    });

    // 등록 화면의 '미리보기' 업데이트
    const dataUrl = canvas.toDataURL('image/jpeg', 0.9);
    const previewImg = document.getElementById("profilePreview");
    previewImg.src = dataUrl;
    previewImg.classList.remove("d-none");
    document.getElementById("emptyImageText").style.display = "none";
    document.getElementById('wrongImageSize').innerHTML = ''; // 에러문구 제거

    // 자른 사진 데이터를 File 객체로 변환하여 저장
    canvas.toBlob(function(blob) {
        const croppedFile = new File([blob], 'profile.jpg', { type: 'image/jpeg' });
        pendingProfileFile = croppedFile; 
        isProfileDeleted = false;
        
        // 자르기 모달 닫기
        const cropModal = bootstrap.Modal.getInstance(document.getElementById('cropModal'));
        cropModal.hide();
        
    }, 'image/jpeg', 0.9);
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
	document.getElementById('profileImage').value = '';

	// 모든 에러 상태(빨간 테두리) 지우기
    ['login', 'name', 'hireDate', 'adminCd', 'bizNo'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.remove('is-invalid');
    });
	
	document.getElementById('prjManagerArea').classList.remove('d-none');
	document.getElementById('teamMember').checked = true;
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
    document.getElementById('profileImage').value = '';

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

	// 프로젝트 매니저 체크 제어
	const prjManagerArea = document.getElementById('prjManagerArea');
	
	if (row.adminCd === '02ROLE' || row.adminNm === '기업관리자') {
	    // 기업관리자일 경우 영역 숨기기
	    prjManagerArea.classList.add('d-none');
	    // 값은 '01ACTIVE'로 강제 고정 (폼 전송용)
	    document.getElementById('projectManager').checked = true;
	} else {
	    // 일반 유저일 경우 영역 보이기
	    prjManagerArea.classList.remove('d-none');
	    
	    // 기존 로직 유지
	    if (row.prjManagerNm === '프로젝트 매니저' || row.prjManagerCd === '01ACTIVE') {
	        document.getElementById('projectManager').checked = true;
	    } else {
	        document.getElementById('teamMember').checked = true;
	    }
	}
		
    if (row.genderCd === '02GENDER') {
        document.getElementById('female').checked = true;
    } else {
        document.getElementById('male').checked = true;
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