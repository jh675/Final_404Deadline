let myPendingProfileFile = null;
let myIsProfileDeleted = false;
let myCropper = null;

// 이메일 인증 상태 플래그
let isEmailVerified = true; 

document.addEventListener('DOMContentLoaded', function() {

    const editInfoModalEl = document.getElementById('mypageEditModal');
    
    // ?모달이 열릴 때 기존 프로필 이미지 조회
    if(editInfoModalEl) {
        editInfoModalEl.addEventListener('show.bs.modal', function () {
            loadMyProfileImage();
            
            // 비밀번호 칸 초기화
            document.getElementById('currentPassword').value = '';
            document.getElementById('newPassword').value = '';
            document.getElementById('newPasswordConfirm').value = '';
            
            // 모든 에러 CSS 초기화
            document.querySelectorAll('#mypageForm .is-invalid').forEach(el => el.classList.remove('is-invalid'));
            document.getElementById('emailFeedback').textContent = '';
            document.getElementById('verifyCodeArea').classList.add('d-none');
            document.getElementById('verifyCodeInput').value = '';
        });
    }

    // 💡 2. 이메일 변경 감지 로직
    const emailInput = document.getElementById('myEmail');
    emailInput.addEventListener('input', function() {
        const originalEmail = this.getAttribute('data-original');
        
        if (this.value.trim() === originalEmail) {
            // 원래 이메일로 되돌린 경우 인증 불필요
            isEmailVerified = true;
            document.getElementById('emailFeedback').textContent = '';
            this.classList.remove('is-valid', 'is-invalid');
            document.getElementById('verifyCodeArea').classList.add('d-none');
        } else {
            // 이메일이 한 글자라도 바뀐 경우 인증 필수
            isEmailVerified = false;
            this.classList.remove('is-valid');
            document.getElementById('emailFeedback').className = 'small mt-1 text-danger';
            document.getElementById('emailFeedback').textContent = '이메일이 변경되었습니다. 인증을 진행해주세요.';
        }
    });

    // 💡 3. 이메일 인증 발송 (/email/send)
    document.getElementById('btnSendVerify').addEventListener('click', async function() {
        const email = emailInput.value.trim();
        const loginId = document.getElementById('myLoginId').value;
        const bizNo = document.getElementById('myBizNo').value;

        if (!email) {
            emailInput.classList.add('is-invalid');
            return;
        }

        this.disabled = true;
        this.textContent = '발송중...';

        try {
            const res = await csrfFetch('/email/send', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, login: loginId, bizNo: bizNo })
            });
            const resultText = await res.text();

            if (resultText === 'success') {
                document.getElementById('emailFeedback').className = 'small mt-1 text-success';
                document.getElementById('emailFeedback').textContent = '인증번호가 발송되었습니다. 3분 안에 입력해주세요.';
                document.getElementById('verifyCodeArea').classList.remove('d-none');
            } else {
                document.getElementById('emailFeedback').className = 'small mt-1 text-danger';
                document.getElementById('emailFeedback').textContent = '인증번호 발송에 실패했습니다.';
            }
        } catch (e) {
            console.error(e);
            alert("서버 통신 오류가 발생했습니다.");
        } finally {
            this.disabled = false;
            this.textContent = '인증발송';
        }
    });

    // 💡 4. 이메일 인증 확인 (/email/verify)
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
                verifyInputEl.classList.add('is-valid');
                document.getElementById('verifyCodeArea').classList.add('d-none');
                
                emailInput.classList.add('is-valid');
                document.getElementById('emailFeedback').className = 'small mt-1 text-success fw-bold';
                document.getElementById('emailFeedback').textContent = '이메일 인증이 완료되었습니다.';
                
                isEmailVerified = true;
            } else {
                verifyInputEl.classList.add('is-invalid');
            }
        } catch (e) {
            console.error(e);
        }
    });

    // 💡 5. 저장(수정) 버튼 클릭 검증 로직
    document.getElementById('btnSaveMyInfo').addEventListener('click', async function() {
        // 기존 에러 지우기
        document.querySelectorAll('#mypageForm .is-invalid').forEach(el => el.classList.remove('is-invalid'));
        let isValid = true;

        const userId = document.getElementById('myUserId').value;
        const loginId = document.getElementById('myLoginId');
        const name = document.getElementById('myName');
        const tel = document.getElementById('myTel');
        const email = document.getElementById('myEmail');
        
        const currentPwd = document.getElementById('currentPassword');
        const newPwd = document.getElementById('newPassword');
        const newPwdConfirm = document.getElementById('newPasswordConfirm');

        // 빈값 체크
        if (!loginId.value.trim()) { loginId.classList.add('is-invalid'); isValid = false; }
        if (!name.value.trim()) { name.classList.add('is-invalid'); isValid = false; }
        if (!email.value.trim()) { email.classList.add('is-invalid'); isValid = false; }

        // 이메일 인증 체크
        if (!isEmailVerified) {
            email.classList.add('is-invalid');
            document.getElementById('emailFeedback').className = 'small mt-1 text-danger';
            document.getElementById('emailFeedback').textContent = '이메일 인증을 먼저 완료해주세요.';
            isValid = false;
        }

        // 비밀번호 변경 체크 (하나라도 입력했다면 검증 수행)
        if (currentPwd.value || newPwd.value || newPwdConfirm.value) {
            if (!currentPwd.value) { currentPwd.classList.add('is-invalid'); isValid = false; }
            if (!newPwd.value) { newPwd.classList.add('is-invalid'); isValid = false; }
            if (newPwd.value !== newPwdConfirm.value) {
                newPwdConfirm.classList.add('is-invalid');
                isValid = false;
            }
        }

        if (!isValid) return;

        // 서버로 보낼 Payload
        const payload = {
            id: userId,
            login: loginId.value.trim(),
            name: name.value.trim(),
            email: email.value.trim(),
            tel: tel.value.trim()
        };
        
        if (currentPwd.value) {
            payload.currentPassword = currentPwd.value;
            payload.newPassword = newPwd.value;
        }

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
                return;
            } else if (result.status === 'DUPLICATE_ID') {
                loginId.classList.add('is-invalid');
                document.getElementById('myLoginIdError').textContent = '이미 사용 중인 아이디입니다.';
                return;
            } else if (result.status === 'SUCCESS') {
                // 정보 저장이 성공하면 프로필 이미지 처리
                await handleMyProfileUpload();
                alert("내 정보가 성공적으로 수정되었습니다.");
                location.reload();
            } else {
                alert("수정 중 오류가 발생했습니다.");
            }
        } catch (e) {
            console.error(e);
            alert("서버 통신 중 오류가 발생했습니다.");
        }
    });

    // ==========================================
    // 💡 6. 프로필 이미지 로직 (Cropper.js 연동)
    // ==========================================
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

// 정보 저장 후 이미지 서버 전송 로직 (/admin/user/profile 재활용 가능)
async function handleMyProfileUpload() {
    if (myIsProfileDeleted) {
        await csrfFetch(`/mypage/profile`, { method: "DELETE" });
    } else if (myPendingProfileFile) {
        const formData = new FormData();
        formData.append("userId", userId);
        formData.append("file", myPendingProfileFile);
        await csrfFetch("/mypage/profile", { method: "POST", body: formData });
    }
}