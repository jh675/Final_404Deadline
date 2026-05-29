let myPendingProfileFile = null;
let myIsProfileDeleted = false;
let myCropper = null;

let isEmailVerified = true; 

document.addEventListener('DOMContentLoaded', function() {

    const editInfoModalEl = document.getElementById('mypageEditModal');
    const emailInput = document.getElementById('myEmail');
    const btnEmailAction = document.getElementById('btnEmailAction');
    const generalFeedback = document.getElementById('generalFeedback');
    
    // 모달 초기화
    if(editInfoModalEl) {
        editInfoModalEl.addEventListener('show.bs.modal', function () {
            loadMyProfileImage();
            
            // 이메일 영역 완전 초기화 (잠금 상태로 복구)
            emailInput.value = emailInput.getAttribute('data-original');
            emailInput.setAttribute('readonly', true);
            emailInput.classList.add('bg-light');
			btnEmailAction.disabled = false;
            btnEmailAction.textContent = '수정';
            btnEmailAction.className = 'btn btn-outline-secondary';
			document.getElementById('btnConfirmVerify').disabled = false;
			
            isEmailVerified = true;
            
            document.getElementById('currentPassword').value = '';
            document.getElementById('newPassword').value = '';
            document.getElementById('newPasswordConfirm').value = '';
            
            document.querySelectorAll('#mypageForm .is-invalid').forEach(el => el.classList.remove('is-invalid'));
            document.getElementById('emailFeedback').textContent = '';
            document.getElementById('verifyCodeArea').classList.add('d-none');
            document.getElementById('verifyCodeInput').value = '';
            generalFeedback.textContent = '';
        });
    }

    // 토글 버튼 이벤트
    btnEmailAction.addEventListener('click', async function() {
        // '수정' 버튼 상태인 경우 -> 수정 모드로 개방
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
        // '인증발송' 버튼 상태인 경우 -> 발송 로직 처리
        else {
            const email = emailInput.value.trim();
            const originalEmail = emailInput.getAttribute('data-original');

            // 변경사항이 없이 원래 이메일과 똑같다면 원상복구
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

            // 인증 메일 발송 API 호출
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
					// 발송 성공시
	                this.textContent = '발송완료';
	                this.className = 'btn btn-secondary';
					
                    document.getElementById('emailFeedback').className = 'small mt-1 text-success';
                    document.getElementById('emailFeedback').textContent = '인증번호가 발송되었습니다. 3분 안에 입력해주세요.';
					document.getElementById('verifyCodeArea').classList.remove('d-none');
	                document.getElementById('btnConfirmVerify').disabled = false;
	                document.getElementById('verifyCodeInput').value = '';
	                document.getElementById('verifyCodeInput').classList.remove('is-invalid');
                } else {
					// 발송 실패 시 다시 누를 수 있도록 복구
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
				
                verifyInputEl.classList.remove('is-invalid');
                document.getElementById('verifyCodeArea').classList.add('d-none');
                
                emailInput.classList.remove('is-invalid');
                document.getElementById('emailFeedback').className = 'small mt-1 text-success fw-bold';
                document.getElementById('emailFeedback').textContent = '이메일 인증이 완료되었습니다.';
                
                isEmailVerified = true;
                
                // 인증 완료 후 다시 안전하게 잠금 (수정하려면 다시 수정버튼을 누르게 유도)
                emailInput.setAttribute('readonly', true);
                emailInput.classList.add('bg-light');
                // 인증 성공한 이메일을 새로운 오리지널로 임시 지정하여 저장 시 통과되도록 함
                emailInput.setAttribute('data-original', email);
				btnEmailAction.disabled = false;
                btnEmailAction.textContent = '수정';
                btnEmailAction.className = 'btn btn-outline-secondary';
                
            } else {
				// 실패 처리 
	            verifyInputEl.classList.add('is-invalid');
	            document.getElementById('verifyCodeError').textContent = '인증번호가 일치하지 않거나 3분이 초과되었습니다.';
	            
	            // 현재 확인 버튼 잠금 & 재인증 유도
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

    // 저장 버튼 검증
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

        if (!loginId.value.trim()) { loginId.classList.add('is-invalid'); isValid = false; }
        if (!name.value.trim()) { name.classList.add('is-invalid'); isValid = false; }
        if (!emailInput.value.trim()) { emailInput.classList.add('is-invalid'); isValid = false; }

        const currentEmailVal = emailInput.value.trim();
        const originalEmailVal = emailInput.getAttribute('data-original');
        
        // input의 값이 변경되었는데 isEmailVerified가 false인 경우 
        if (currentEmailVal !== originalEmailVal || !isEmailVerified) {
            emailInput.classList.add('is-invalid');
            document.getElementById('emailFeedback').className = 'small mt-1 text-danger fw-bold';
            document.getElementById('emailFeedback').textContent = '변경된 이메일에 대한 인증을 완료해주세요.';
            isValid = false;
        }

        if (currentPwd.value || newPwd.value || newPwdConfirm.value) {
            if (!currentPwd.value) { currentPwd.classList.add('is-invalid'); isValid = false; }
            if (!newPwd.value) { newPwd.classList.add('is-invalid'); isValid = false; }
            if (newPwd.value !== newPwdConfirm.value) {
                newPwdConfirm.classList.add('is-invalid');
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
        if (currentPwd.value) {
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
                
                // 1.5초 뒤 페이지 새로고침하여 적용된 정보 보여주기
                setTimeout(() => location.reload(), 1500);
                return; // 성공 시 여기서 멈춤 (버튼 활성화 복구 안함)
            } else {
                generalFeedback.className = 'small fw-bold text-danger';
                generalFeedback.textContent = '수정 중 오류가 발생했습니다.';
            }
        } catch (e) {
            generalFeedback.className = 'small fw-bold text-danger';
            generalFeedback.textContent = '서버 통신 중 오류가 발생했습니다.';
        }
        
        // 에러 발생 시 버튼 원상복구
        this.disabled = false;
        this.textContent = '저장';
    });

    // 프로필 이미지 로직 (Cropper.js 연동)
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

    document.getElementById('myCropModal').addEventListener('shown.bs.modal', function () {
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
    
    // 입력창 타이핑 시 빨간 에러 CSS 삭제
    document.querySelectorAll('#mypageForm input').forEach(input => {
        input.addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    });
});

// 프로필 이미지 서버에서 불러오기
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
    
    // 실패 시 초기화
    img.src = "";
    img.classList.add("d-none");
    emptyText.style.display = "";
}

// 정보 저장 후 이미지 서버 전송 로직 
async function handleMyProfileUpload() {
    if (myIsProfileDeleted) {
        await csrfFetch(`/mypage/profile`, { method: "DELETE" });
    } else if (myPendingProfileFile) {
        const formData = new FormData();
        formData.append("file", myPendingProfileFile);
        await csrfFetch("/mypage/profile", { method: "POST", body: formData });
    }
}