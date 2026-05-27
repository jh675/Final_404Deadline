// 프로필 이미지 임시 저장할 전역 변수
let pendingProfileFile = null; // 업로드 대기 중인 파일 객체
let isProfileDeleted = false;  // 기존 이미지 삭제 대기 플래그

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
                header: '이름',
                name: 'name',
                align: 'center',
            },
            {
                header: '아이디',
                name: 'login',
                align: 'center',
            },
            {
                header: '회원 권한',
                name: 'adminNm',
                align: 'center',
                sortable: true
            },
            {
                header: '계정상태',
                name: 'statusNm',
                align: 'center',
                sortable: true
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
                header: '소속기업',
                name: 'compNm',
                align: 'center',
                sortable: true
            },
            {
                header: '이메일',
                name: 'email',
                align: 'center',
            },
            {
                header: '전화번호',
                name: 'tel',
                align: 'center',
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
        rowHeaders: ['checkbox', 'rowNum'],

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

    // 로딩 직후 체크박스 컬럼 숨기기.
    grid.hideColumn('_checked');

    // 일괄작업 버튼 누르면
    document.getElementById('toggleBulkModeBtn').addEventListener('click', function() {
        this.classList.add('d-none'); // 일괄작업 버튼 숨기기
        document.getElementById('bulkControls').classList.remove('d-none'); // 적용 컨트롤 보이기
        grid.showColumn('_checked'); // 🌟 그리드 체크박스 나타나기!
    });

    // 일괄작업 취소 버튼
    document.getElementById('cancelBulkModeBtn').addEventListener('click', function() {
        document.getElementById('bulkControls').classList.add('d-none');
        document.getElementById('toggleBulkModeBtn').classList.remove('d-none');
        grid.uncheckAll(); // 체크된 것 모두 해제
        grid.hideColumn('_checked'); // 🌟 그리드 체크박스 숨기기!
    });

    // 일괄 처리 적용 버튼 이벤트
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

        // 관리자의 PM 권한을 해제하려고 할 때 차단
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
            const response = await csrfFetch('/admin/users/bulk-update', {
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

    await loadCompanyList();

    // 회원권한 변경 이벤트
    document.getElementById('adminCd').addEventListener('change', function() {
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
	        // 아이디 중복 에러
	        if (result.result === 'DUPLICATE') {
	            loginInput.classList.add('is-invalid');
	            if (loginError) loginError.textContent = '해당 기업에 이미 사용 중인 아이디입니다.';
	            loginInput.focus();
	            return; 
	        }
	        
	        // 존재하지 않는 기업번호 에러
	        if (result.result === 'INVALID_BIZNO') {
	            if (bizNoSelect) {
	                bizNoSelect.classList.add('is-invalid');
	                document.getElementById('bizNoError').textContent = '존재하지 않은 기업번호입니다.';
	                bizNoSelect.focus();
	            }
	            return;
	        }
	        
			if (result.result === 'SUCCESS' && result.id) { 
		    	isSuccess = true;
		        finalUserId = result.id; 
			}
		} else { 
	        // 존재하지 않는 기업번호 에러 (수정)
	        if (result.result === 'INVALID_BIZNO') {
	            if (bizNoSelect) {
	                bizNoSelect.classList.add('is-invalid');
	                document.getElementById('bizNoError').textContent = '존재하지 않은 기업번호입니다.';
	                bizNoSelect.focus();
	            }
	            return;
	        }
			// 소속기업 아이디 중복 체크
			if (result.result === 'DUPLICATE_LOGIN') {
		        loginInput.classList.add('is-invalid');
		        if (loginError) {
		            loginError.textContent = '해당 기업에 이미 사용 중인 아이디입니다.';
		        }
		        loginInput.focus();
		        return; 
		    }
			
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

	// 입력창 변경 시 에러 초기화 구문 안쪽에 추가
    const bizNoSelectElement = document.getElementById('bizNo');
    if (bizNoSelectElement) {
        bizNoSelectElement.addEventListener('change', function() {
            const errorDiv = document.getElementById('bizNoError');
            if (errorDiv) errorDiv.textContent = '소속기업을 선택해주세요.'; 
        });
    }
	
    // 💡 4. 모달창: '비활성' 체크 시 자동으로 '비밀번호 초기화 필요' 체크
    const statusInactive = document.getElementById('statusInactive');
    const mcpActive = document.getElementById('mcpActive'); // 필요(01ACTIVE) 라디오 버튼

    if (statusInactive && mcpActive) {
        statusInactive.addEventListener('change', function() {
            if (this.checked) {
                mcpActive.checked = true; // 자동으로 '필요'로 변경!
            }
        });
    }
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
            if (text) {
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
document.getElementById('uploadBtn').addEventListener('click', function() {
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
document.getElementById('cropModal').addEventListener('shown.bs.modal', function() {
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

// 삭제 버튼 클릭 시 (서버 삭제 X, 로컬 UI만 지우고 변수에 표시)
document.getElementById('deleteImageBtn').addEventListener('click', function() {
    // 대기열 비우기 및 삭제 플래그 켜기
    pendingProfileFile = null;
    isProfileDeleted = true;

    // UI 및 input 초기화
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
    document.getElementById('profileImage').value = '';

    // 모든 에러 상태(빨간 테두리) 지우기
    ['login', 'name', 'hireDate', 'adminCd', 'bizNo'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.remove('is-invalid');
    });

    document.getElementById('modalTitle').textContent = '회원 정보 등록';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('bizNoView').disabled = true; // 기업번호는 항상 readonly
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
    document.getElementById('profileImage').value = '';

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

    // 기업번호는 항상 readonly
    document.getElementById('bizNoView').disabled = true;

    // 저장버튼 mode
    document.getElementById('saveBtn').dataset.mode = 'update';

    // 저장 버튼 텍스트 변경
    document.getElementById('saveBtn').textContent = '수정';

    // 해당 유저의 프로필 이미지 불러오기
    loadProfileImage(row.id);

	// 시스템관리자라면 소속기업 선택을 막기.
	if (row.adminCd === '01ROLE') {
	    document.getElementById('bizNo').disabled = true;
	} else {
	    document.getElementById('bizNo').disabled = false;
	}
	
    // modal open
    const modal = new bootstrap.Modal(document.getElementById('userInsert'));
    modal.show();
}

// 기업관리자, 시스템 관리자 프로젝트 매니저 역할 고정
function changeRoleArea(adminCd) {
    const roleArea = document.getElementById('prjManagerArea');
    const bizNoSelect = document.getElementById('bizNo');     // 소속기업 select
    const bizNoView = document.getElementById('bizNoView');     // 기업번호 input
    // 시스템관리자(01ROLE)를 선택한 경우
    if (adminCd === '01ROLE') {
        // 자동으로 본사 기업번호 매핑
        if (bizNoSelect) {
            bizNoSelect.value = '';        
            bizNoSelect.disabled = true;  
        }
        if (bizNoView) {
            bizNoView.value = '124-87-03358'; 
        }

        // 역할은 프로젝트 매니저 강제 선택 및 영역 숨김
        document.getElementById('projectManager').checked = true;
        roleArea.style.display = 'none';
    }
    // 기업관리자(02ROLE) 또는 사원(03ROLE)을 선택한 경우
    else {
        if (bizNoSelect) {
            bizNoSelect.disabled = false; // 소속기업을 고를 수 있도록 활성화
			
			// 다른 권한으로 돌아왔을 때, 기업번호 변경
			if (bizNoView) {
			    bizNoView.value = bizNoSelect.value; 
			}
        }

        // 역할 제어 
        if (adminCd === '02ROLE') {
            document.getElementById('projectManager').checked = true;
            roleArea.style.display = 'none';
        } else {
            document.getElementById('teamMember').checked = true;
            roleArea.style.display = '';
        }
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