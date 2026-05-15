document.addEventListener('DOMContentLoaded', () => {

    //   csrf
    const csrfMeta = document.querySelector('meta[name="_csrf"]');

    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    if (!csrfMeta || !csrfHeaderMeta) {
        return;
    }

    const csrfToken = csrfMeta.content;
    const csrfHeader = csrfHeaderMeta.content;

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
            const response = await fetch('/email/send', {
				                    method: 'POST',
				                    headers: {
				                        'Content-Type': 'application/json',
				                        [csrfHeader]: csrfToken
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
        } finally {
            sendBtn.disabled = false;
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
                verifyBtn.disabled = true;
            } else {
                showMessage( '인증번호가 올바르지 않거나 만료되었습니다.', 'danger');
            }
        } catch (error) {
            console.error(error);
            showMessage(
                '서버 통신 중 오류가 발생했습니다.',
                'danger'
            );
        } finally {
            verifyBtn.disabled = false;
        }
    });

	// 입력창에 입력되면 경고문구 지우기
    [bizNo, login, email, verifyNum].forEach(e=> {
        e.addEventListener('input', () => {
            clearMessage();
        });
    });
});