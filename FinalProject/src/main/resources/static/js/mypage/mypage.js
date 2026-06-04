let myPendingProfileFile = null;
let myIsProfileDeleted = false;
let myCropper = null;

// 이메일 인증 상태 플래그
let isEmailVerified = true;
// 타이머 변수
let mypageTimer = null;

// 타이머 함수
function startMypageTimer(durationInSeconds) {
    const timerDisplay = document.getElementById('mypageTimerDisplay');
    const verifyInput = document.getElementById('verifyCodeInput');
    const btnConfirmVerify = document.getElementById('btnConfirmVerify');
    const btnEmailAction = document.getElementById('btnEmailAction');
    const emailFeedback = document.getElementById('emailFeedback');

    if (mypageTimer) clearInterval(mypageTimer);
    
    timerDisplay.classList.remove('d-none');
    verifyInput.disabled = false;
    btnConfirmVerify.disabled = false;

    const endTime = Date.now() + (durationInSeconds * 1000);

    function updateTimer() {
        const timeLeft = Math.max(0, endTime - Date.now());
        const totalSeconds = Math.ceil(timeLeft / 1000);

        const m = Math.floor(totalSeconds / 60);
        const s = totalSeconds % 60;
        timerDisplay.textContent = `${m}:${s.toString().padStart(2, '0')}`;

        if (timeLeft <= 0) {
            clearInterval(mypageTimer);
            verifyInput.disabled = true;
            btnConfirmVerify.disabled = true;
            
            emailFeedback.className = 'small mt-1 text-danger fw-bold';
            emailFeedback.textContent = '인증 시간이 만료되었습니다. 재인증을 진행해주세요.';
            
            btnEmailAction.disabled = false;
            btnEmailAction.textContent = '재인증';
            btnEmailAction.className = 'btn btn-outline-primary';
        }
    }

    updateTimer(); 
    mypageTimer = setInterval(updateTimer, 500); 
}

document.addEventListener('DOMContentLoaded', function() {

    const editInfoModalEl = document.getElementById('mypageEditModal');
    const emailInput = document.getElementById('myEmail');
    const btnEmailAction = document.getElementById('btnEmailAction');
    const generalFeedback = document.getElementById('generalFeedback');
    
    // 모달 초기화
    if(editInfoModalEl) {
        editInfoModalEl.addEventListener('show.bs.modal', function () {
            loadMyProfileImage();
            
            // 이메일 영역 완전 초기화 
            emailInput.value = emailInput.getAttribute('data-original');
            emailInput.setAttribute('readonly', true);
            emailInput.classList.add('bg-light');
			btnEmailAction.disabled = false;
            btnEmailAction.textContent = '수정';
            btnEmailAction.className = 'btn btn-outline-secondary';
			document.getElementById('btnConfirmVerify').disabled = false;
            isEmailVerified = true;
            
            // 비밀번호 영역 UI 초기화
            const currentPwd = document.getElementById('currentPassword');
            const btnCheckCurrentPwd = document.getElementById('btnCheckCurrentPwd');
            const newPasswordArea = document.getElementById('newPasswordArea');
            
            currentPwd.value = '';
            currentPwd.readOnly = false;
            currentPwd.classList.remove('is-valid');
            document.getElementById('newPassword').value = '';
            document.getElementById('newPasswordConfirm').value = '';
            
            if(btnCheckCurrentPwd) {
                btnCheckCurrentPwd.disabled = false;
                btnCheckCurrentPwd.textContent = '확인';
                btnCheckCurrentPwd.className = 'btn btn-outline-secondary';
            }
            if(newPasswordArea) {
                newPasswordArea.classList.add('d-none');
            }
            document.getElementById('currentPwdSuccess').classList.add('d-none');

            document.querySelectorAll('#mypageForm .is-invalid').forEach(el => el.classList.remove('is-invalid'));
            document.getElementById('emailFeedback').textContent = '';
            document.getElementById('verifyCodeArea').classList.add('d-none');
            document.getElementById('verifyCodeInput').value = '';
            generalFeedback.textContent = '';
			
			if (mypageTimer) clearInterval(mypageTimer); 
            document.getElementById('mypageTimerDisplay').classList.add('d-none');
            document.getElementById('verifyCodeInput').disabled = false;
        });
    }

    // 현재 비밀번호 확인
    const btnCheckCurrentPwd = document.getElementById('btnCheckCurrentPwd');
    if(btnCheckCurrentPwd) {
        btnCheckCurrentPwd.addEventListener('click', async function() {
            const currentPwd = document.getElementById('currentPassword');
            const pwdVal = currentPwd.value;
            
            if (!pwdVal) {
                currentPwd.classList.add('is-invalid');
                document.getElementById('currentPasswordError').textContent = '현재 비밀번호를 입력해주세요.';
                return;
            }

            this.disabled = true;
            this.textContent = '확인중...';

            try {
                // 컨트롤러에 새로 추가한 API 호출
                const res = await csrfFetch('/mypage/check-password', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ password: pwdVal })
                });
                const result = await res.json(); 

                if (result.valid) {
                    currentPwd.classList.remove('is-invalid');
                    currentPwd.classList.add('is-valid');
                    currentPwd.readOnly = true; // 확인 완료 후 수정 못하게 잠금
                    this.textContent = '확인완료';
                    this.classList.replace('btn-outline-secondary', 'btn-success');
                    
                    document.getElementById('currentPwdSuccess').classList.remove('d-none');
                    
                    // 새 비밀번호 창 열고 부드럽게 스크롤
                    const newPasswordArea = document.getElementById('newPasswordArea');
                    newPasswordArea.classList.remove('d-none');
                    newPasswordArea.scrollIntoView({ behavior: 'smooth', block: 'center' });
                } else {
                    currentPwd.classList.add('is-invalid');
                    document.getElementById('currentPasswordError').textContent = '현재 비밀번호가 일치하지 않습니다.';
                    this.disabled = false;
                    this.textContent = '확인';
                }
            } catch (e) {
                currentPwd.classList.add('is-invalid');
                document.getElementById('currentPasswordError').textContent = '서버 오류가 발생했습니다.';
                this.disabled = false;
                this.textContent = '확인';
            }
        });
    }

    // 이메일 토글 버튼 이벤트
    btnEmailAction.addEventListener('click', async function() {
        if (emailInput.hasAttribute('readonly')) {
            emailInput.removeAttribute('readonly');
            emailInput.classList.remove('bg-light');
            emailInput.focus();
            
            this.textContent = '인증발송';
            this.className = 'btn btn-outline-primary';
			this.disabled = false;
			
            document.getElementById('emailFeedback').className = 'small mt-1 text-primary';
            document.getElementById('emailFeedback').textContent = '변경할 이메일을 입력 후 인증발송을 눌러주세요.';
            isEmailVerified = false;
        } 
        else {
            const email = emailInput.value.trim();
            const originalEmail = emailInput.getAttribute('data-original');

            if (email === originalEmail) {
                emailInput.setAttribute('readonly', true);
                emailInput.classList.add('bg-light');
                this.textContent = '수정';
                this.className = 'btn btn-outline-secondary';
                document.getElementById('emailFeedback').textContent = '';
                isEmailVerified = true;
                return;
            }

            if (!email) {
                emailInput.classList.add('is-invalid');
                return;
            }

            this.disabled = true;
            this.textContent = '발송중...';

            try {
                const loginId = document.getElementById('myLoginId').value;
                const bizNo = document.getElementById('myBizNo').value;
                
                const res = await csrfFetch('/email/send-mypage', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email, login: loginId, bizNo: bizNo })
                });
                const resultText = await res.text();

                if (resultText === 'success') {
	                this.textContent = '발송완료';
	                this.className = 'btn btn-secondary';
                    document.getElementById('emailFeedback').className = 'small mt-1 text-success';
                    document.getElementById('emailFeedback').textContent = '인증번호가 발송되었습니다. 3분 안에 입력해주세요.';
					document.getElementById('verifyCodeArea').classList.remove('d-none');
	                document.getElementById('btnConfirmVerify').disabled = false;
	                document.getElementById('verifyCodeInput').value = '';
	                document.getElementById('verifyCodeInput').classList.remove('is-invalid');
					startMypageTimer(180);
                } else {
	                this.disabled = false;
	                this.textContent = '재인증';
	                this.className = 'btn btn-outline-primary';
                    document.getElementById('emailFeedback').className = 'small mt-1 text-danger';
                    document.getElementById('emailFeedback').textContent = '인증번호 발송에 실패했습니다.';
                }
            } catch (e) {
				this.disabled = false;
	            this.textContent = '재인증';
	            document.getElementById('emailFeedback').className = 'small mt-1 text-danger';
	            document.getElementById('emailFeedback').textContent = '서버 통신 오류가 발생했습니다.';
            }
        }
    });

    // 이메일 인증 확인
    document.getElementById('btnConfirmVerify').addEventListener('click', async function() {
        const email = emailInput.value.trim();
        const verifyNum = document.getElementById('verifyCodeInput').value.trim();
        const verifyInputEl = document.getElementById('verifyCodeInput');

        if (!verifyNum) {
            verifyInputEl.classList.add('is-invalid');
            return;
        }

        try {
            const res = await csrfFetch('/email/verify', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, verifyNum: verifyNum })
            });
            const resultText = await res.text();

            if (resultText === 'success') {
                clearInterval(mypageTimer);
                document.getElementById('mypageTimerDisplay').classList.add('d-none');
                
                verifyInputEl.classList.remove('is-invalid');
                document.getElementById('verifyCodeArea').classList.add('d-none');
                
                emailInput.classList.remove('is-invalid');
                document.getElementById('emailFeedback').className = 'small mt-1 text-success fw-bold';
                document.getElementById('emailFeedback').textContent = '이메일 인증이 완료되었습니다.';

                isEmailVerified = true;
                
                emailInput.setAttribute('readonly', true);
                emailInput.classList.add('bg-light');
                emailInput.setAttribute('data-original', email);
				btnEmailAction.disabled = false;
                btnEmailAction.textContent = '수정';
                btnEmailAction.className = 'btn btn-outline-secondary';
                
            } else {
	            verifyInputEl.classList.add('is-invalid');
	            document.getElementById('verifyCodeError').textContent = '인증번호가 일치하지 않거나 3분이 초과되었습니다.';
	            
	            this.disabled = true; 
	            
	            btnEmailAction.disabled = false;
	            btnEmailAction.textContent = '재인증';
	            btnEmailAction.className = 'btn btn-outline-primary';
	            
	            document.getElementById('emailFeedback').className = 'small mt-1 text-danger fw-bold';
	            document.getElementById('emailFeedback').textContent = '인증에 실패했습니다. 재인증을 진행해주세요.';
            }
        } catch (e) {
            console.error(e);
        }
    });

    // 최종 저장 버튼 로직
    document.getElementById('btnSaveMyInfo').addEventListener('click', async function() {
        document.querySelectorAll('#mypageForm .is-invalid').forEach(el => el.classList.remove('is-invalid'));
        generalFeedback.textContent = '';
        let isValid = true;

        const loginId = document.getElementById('myLoginId');
        const name = document.getElementById('myName');
        const tel = document.getElementById('myTel');
        
        const currentPwd = document.getElementById('currentPassword');
        const newPwd = document.getElementById('newPassword');
        const newPwdConfirm = document.getElementById('newPasswordConfirm');
        const newPasswordArea = document.getElementById('newPasswordArea');

        // 기본 정보 유효성 검사
        if (!loginId.value.trim()) { loginId.classList.add('is-invalid'); isValid = false; }
        if (!name.value.trim()) { name.classList.add('is-invalid'); isValid = false; }
        if (!emailInput.value.trim()) { emailInput.classList.add('is-invalid'); isValid = false; }

        const currentEmailVal = emailInput.value.trim();
        const originalEmailVal = emailInput.getAttribute('data-original');
        
        if (currentEmailVal !== originalEmailVal || !isEmailVerified) {
            emailInput.classList.add('is-invalid');
            document.getElementById('emailFeedback').className = 'small mt-1 text-danger fw-bold';
            document.getElementById('emailFeedback').textContent = '변경된 이메일에 대한 인증을 완료해주세요.';
            isValid = false;
        }

        // 새 비밀번호 영역이 열려있는 경우에만 패스워드 검증
        const isPwdChanging = newPasswordArea && !newPasswordArea.classList.contains('d-none');

        if (isPwdChanging) {
            if (!newPwd.value) { 
                newPwd.classList.add('is-invalid'); 
                isValid = false; 
            }
            if (newPwd.value !== newPwdConfirm.value) {
                newPwdConfirm.classList.add('is-invalid');
                // 기존 HTML에 있던 newPasswordConfirmError 영역에 메시지 표시
                const confirmError = document.getElementById('newPasswordConfirmError');
                if(confirmError) confirmError.textContent = '비밀번호가 일치하지 않습니다.';
                isValid = false;
            }
        }

        if (!isValid) return;

        const payload = {
            login: loginId.value.trim(),
            name: name.value.trim(),
            email: currentEmailVal,
            tel: tel.value.trim()
        };

        // 비밀번호를 변경하는 중이라면 payload에 추가 
        if (isPwdChanging) {
            payload.currentPassword = currentPwd.value;
            payload.newPassword = newPwd.value;
        }

        this.disabled = true;
        this.textContent = '저장 중...';

        try {
            const res = await csrfFetch('/mypage/update', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const result = await res.json();

            if (result.status === 'PWD_ERROR') {
                currentPwd.classList.add('is-invalid');
                document.getElementById('currentPasswordError').textContent = '현재 비밀번호가 일치하지 않습니다.';
            } else if (result.status === 'DUPLICATE_LOGIN') {
                loginId.classList.add('is-invalid');
                document.getElementById('myLoginIdError').textContent = '해당 기업에 이미 사용 중인 아이디입니다.';
            } else if (result.status === 'SUCCESS') {
                await handleMyProfileUpload(); 
                
                generalFeedback.className = 'small fw-bold text-success';
                generalFeedback.textContent = '내 정보가 성공적으로 수정되었습니다.';
                
                setTimeout(() => location.reload(), 1000);
                return; 
            } else {
                generalFeedback.className = 'small fw-bold text-danger';
                generalFeedback.textContent = '수정 중 오류가 발생했습니다.';
            }
        } catch (e) {
            generalFeedback.className = 'small fw-bold text-danger';
            generalFeedback.textContent = '서버 통신 중 오류가 발생했습니다.';
        }
        
        this.disabled = false;
        this.textContent = '저장';
    });

    document.getElementById('btnUploadProfile').addEventListener('click', () => {
        document.getElementById('myProfileImage').click();
    });

    document.getElementById('myProfileImage').addEventListener('change', function(e) {
        const file = this.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(event) {
            document.getElementById('myImageToCrop').src = event.target.result;
            new bootstrap.Modal(document.getElementById('myCropModal')).show();
        };
        reader.readAsDataURL(file);
        this.value = '';
    });

    document.getElementById('myCropModal').addEventListener('shown.bs.modal', function() {
        const image = document.getElementById('myImageToCrop');
        if (myCropper) myCropper.destroy();

        myCropper = new Cropper(image, {
            aspectRatio: 3 / 4,
            viewMode: 1,
            dragMode: 'move',
            autoCropArea: 0.8,
        });
    });

    document.getElementById('btnApplyCrop').addEventListener('click', function() {
        if (!myCropper) return;
        const canvas = myCropper.getCroppedCanvas({ width: 300, height: 400 });

        const dataUrl = canvas.toDataURL('image/jpeg', 0.9);
        const previewImg = document.getElementById("myProfilePreview");
        previewImg.src = dataUrl;
        previewImg.classList.remove("d-none");
        document.getElementById("myEmptyImageText").style.display = "none";

        canvas.toBlob(function(blob) {
            myPendingProfileFile = new File([blob], 'profile.jpg', { type: 'image/jpeg' });
            myIsProfileDeleted = false;
            bootstrap.Modal.getInstance(document.getElementById('myCropModal')).hide();
        }, 'image/jpeg', 0.9);
    });

    document.getElementById('btnDeleteProfile').addEventListener('click', function() {
        myPendingProfileFile = null;
        myIsProfileDeleted = true;
        document.getElementById('myProfilePreview').src = "";
        document.getElementById('myProfilePreview').classList.add("d-none");
        document.getElementById('myEmptyImageText').style.display = "";
    });

    document.querySelectorAll('#mypageForm input').forEach(input => {
        input.addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    });
});

async function loadMyProfileImage() {
    const img = document.getElementById("myProfilePreview");
    const emptyText = document.getElementById("myEmptyImageText");

    myPendingProfileFile = null;
    myIsProfileDeleted = false;

    try {
        const res = await csrfFetch(`/mypage/profile`);
        if (res.ok) {
            const text = await res.text();
            if (text) {
                const data = JSON.parse(text);
                if (data && data.id) {
                    img.src = `/download/${data.id}?t=${new Date().getTime()}`;
                    img.classList.remove("d-none");
                    emptyText.style.display = "none";
                    return;
                }
            }
        }
    } catch (e) {}

    img.src = "";
    img.classList.add("d-none");
    emptyText.style.display = "";
}

async function handleMyProfileUpload() {
    if (myIsProfileDeleted) {
        await csrfFetch(`/mypage/profile`, { method: "DELETE" });
    } else if (myPendingProfileFile) {
        const formData = new FormData();
        formData.append("file", myPendingProfileFile);
        await csrfFetch("/mypage/profile", { method: "POST", body: formData });
    }
}