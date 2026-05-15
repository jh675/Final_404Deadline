document.addEventListener('DOMContentLoaded', () => {

    //   csrf
    const csrfMeta = document.querySelector('meta[name="_csrf"]');

    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    if (!csrfMeta || !csrfHeaderMeta) {
        return;
    }

    /*const csrfToken = csrfMeta.content;
    const csrfHeader = csrfHeaderMeta.content;*/

    const bizNo = document.querySelector('#bizNo');
    const login = document.querySelector('#login');
    const email = document.querySelector('#email');
    const verifyNum = document.querySelector('#verifyNum');
    const sendBtn = document.querySelector('#sendCodeBtn');
    const verifyBtn = document.querySelector('#verifyBtn');
    const verifyArea = document.querySelector('#verifyArea');
    const passwordArea = document.querySelector('#passwordArea');
    const confirmBtn = document.querySelector('#confirmBtn');
    const messageArea = document.querySelector('#messageArea');
	const newPassword = document.querySelector('#newPassword');
	const newPasswordCheck = document.querySelector('#newPasswordCheck');
	const passwordError = document.querySelector('#passwordError');

    //메시지 출력
    function showMessage(message, type) {
        messageArea.className = `alert alert-${type}`;
        messageArea.textContent = message;
    }

    //	메시지 숨김
    function clearMessage() {
        messageArea.className = 'alert d-none';
        messageArea.textContent = '';
    }

    //	경고기능
    function validateSendForm() {
        clearMessage();
        if (!bizNo.value.trim()) {
            showMessage('기업번호를 입력하세요.', 'danger');
            bizNo.focus();
            return false;
        }

        if (!login.value.trim()) {
            showMessage('아이디를 입력하세요.', 'danger');
            login.focus();
            return false;
        }

        if (!email.value.trim()) {
            showMessage('이메일을 입력하세요.','danger');
            email.focus();
            return false;
        }

        return true;
    }

//		인증번호 발송
        sendBtn.addEventListener('click', async () => {
        try {
            if (!validateSendForm()) {
                return;
            }

            sendBtn.disabled = true;
            const response = await csrfFetch('/email/send', {
				                    method: 'POST',
				                    headers: {
				                        'Content-Type': 'application/json',
				                        /*[csrfHeader]: csrfToken*/
				                    },
				                    body: JSON.stringify({
				                        bizNo: bizNo.value,
				                        login: login.value,
				                        email: email.value
				                    })
				                });

            if (!response.ok) {
                throw new Error('서버 오류');
            }

            const result = await response.text();

            console.log(result);

            if (result === 'success') {
                showMessage( '인증번호가 발송되었습니다.','success');
                verifyArea.classList.remove('d-none');
				verifyBtn.disabled = false;
            } else if (result === 'no_user') {
                showMessage('회원정보가 일치하지 않습니다.','danger');
            } else {
                showMessage('인증 요청 중 오류가 발생했습니다.','danger');
            }
        } catch (error) {
            console.error(error);
            showMessage(
                '서버 통신 중 오류가 발생했습니다.',
                'danger'
            );
        }
    });

//		인증번호 확인
    verifyBtn.addEventListener('click', async () => {
        try {
            if (!verifyNum.value.trim()) {
                showMessage('인증번호를 입력하세요.','danger');
                verifyNum.focus();
                return;
            }

            verifyBtn.disabled = true;
            const response = await fetch('/email/verify', {
				                    method: 'POST',
				                    headers: {
				                        'Content-Type': 'application/json',
				                        [csrfHeader]: csrfToken
				                    },
				                    body: JSON.stringify({
				                        email: email.value,
				                        verifyNum: verifyNum.value
				                    })
				                });
            if (!response.ok) {
                throw new Error('서버 오류');
            }
            const result = await response.text();
            console.log(result);
            if (result === 'success') {
                showMessage('이메일 인증이 완료되었습니다.', 'success');
                passwordArea.classList.remove('d-none');
                confirmBtn.classList.remove('d-none');
				verifyBtn.textContent = '인증완료';
                verifyBtn.disabled = true;
            } else {
                showMessage( '인증번호가 올바르지 않거나 만료되었습니다.', 'danger');
	            verifyBtn.disabled = true;
				sendBtn.disabled = false;
				sendBtn.textContent = '재인증 요청';
				verifyNum.value = '';
				verifyNum.focus();
            }
        } catch (error) {
            console.error(error);
            showMessage(
                '서버 통신 중 오류가 발생했습니다.',
                'danger'
            );
        }
    });

	// 입력창에 입력되면 경고문구 지우기
    [bizNo, login, email, verifyNum].forEach(e=> {
        e.addEventListener('input', () => {
            clearMessage();
        });
    });
	
	function showPasswordError(message) {
	    passwordError.textContent = message;
	    passwordError.classList.remove('d-none');
	}

	function clearPasswordError() {
	    passwordError.textContent = '';
	    passwordError.classList.add('d-none');
	}
	
	confirmBtn.addEventListener('click', async () => {
	    try {
	        clearPasswordError();

	        // 빈값 체크
	        if (!newPassword.value.trim()) {
	            showPasswordError('새 비밀번호를 입력하세요.');
	            newPassword.focus();
	            return;
	        }
	        if (!newPasswordCheck.value.trim()) {
	            showPasswordError('비밀번호 확인을 입력하세요.');
	            newPasswordCheck.focus();
	            return;
	        }

	        // 비밀번호 비교
	        if (newPassword.value !== newPasswordCheck.value) {
	            showPasswordError('비밀번호가 일치하지 않습니다.');
	            newPasswordCheck.focus();
	            return;
	        }
	        confirmBtn.disabled = true;

	        const response = await fetch('/email/resetPw', {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
	                [csrfHeader]: csrfToken
	            },
	            body: JSON.stringify({
	                bizNo: bizNo.value,
	                login: login.value,
	                password: newPassword.value
	            })
	        });

	        if (!response.ok) {
	            throw new Error('서버 오류');
	        }

	        const result = await response.text();

	        if (result === 'success') {
	            showMessage(
	                '비밀번호가 변경되었습니다.',
	                'success'
	            );
	            confirmBtn.disabled = true;

	        } else {
	            showMessage(
	                '비밀번호 변경에 실패했습니다.',
	                'danger'
	            );
	            confirmBtn.disabled = false;
	        }

	    } catch (error) {
	        console.error(error);
	        showMessage('서버 통신 중 오류가 발생했습니다.', 'danger');
	        confirmBtn.disabled = false;
	    }
	});
	
});