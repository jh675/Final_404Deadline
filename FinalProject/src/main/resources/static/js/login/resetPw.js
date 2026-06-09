document.addEventListener('DOMContentLoaded', () => {

    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    if (!csrfMeta || !csrfHeaderMeta) {
        return;
    }

    // 화면 타겟 설정
    const resetBizNo = document.querySelector('#resetBizNo');
    const resetCompanyNameInput = document.querySelector('#resetCompanyNameInput');
    const resetCompanyDropdown = document.querySelector('#resetCompanyDropdown');
    
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
    const resultMsg = document.querySelector('#resultMsg');
	
	const resetPwTimerDisplay = document.querySelector('#resetPwTimerDisplay'); // 타이머 UI 변수
    let pwVerifyTimer = null; // 타이머 변수

    // 타이머 함수
    function startPwVerifyTimer(durationInSeconds) {
        if (pwVerifyTimer) clearInterval(pwVerifyTimer);
        
        resetPwTimerDisplay.classList.remove('d-none');
        verifyNum.disabled = false;

        const endTime = Date.now() + (durationInSeconds * 1000);

        function updateTimer() {
            const timeLeft = Math.max(0, endTime - Date.now());
            const totalSeconds = Math.ceil(timeLeft / 1000);

            const m = Math.floor(totalSeconds / 60);
            const s = totalSeconds % 60;
            resetPwTimerDisplay.textContent = `${m}:${s.toString().padStart(2, '0')}`;

            if (timeLeft <= 0) {
                clearInterval(pwVerifyTimer);
                
                // 타임아웃 UI 처리
                verifyNum.disabled = true;
                verifyBtn.disabled = true;
                sendBtn.disabled = false;
                sendBtn.textContent = '재인증 요청';
                
                showMessage('인증 시간이 만료되었습니다. 다시 요청해주세요.', 'danger');
            }
        }
        updateTimer();
        pwVerifyTimer = setInterval(updateTimer, 500); 
    }

    // 기업 검색 및 자동완성 로직
    let resetDebounceTimer;

    resetCompanyNameInput.addEventListener('input', function(e) {
        const keyword = e.target.value.trim();
        resetBizNo.value = ''; // 타이핑 수정 시 hidden 기업번호 초기화

        if (keyword.length === 0) {
            resetCompanyDropdown.style.display = 'none';
            resetCompanyDropdown.innerHTML = '';
            return;
        }

        clearTimeout(resetDebounceTimer);
        resetDebounceTimer = setTimeout(() => {
            fetch(`/login/companies/search?keyword=${encodeURIComponent(keyword)}`)
                .then(response => response.json())
                .then(data => {
                    resetCompanyDropdown.innerHTML = '';
                    if (data.length > 0) {
                        data.forEach(company => {
                            const li = document.createElement('li');
                            li.className = 'list-group-item list-group-item-action cursor-pointer';
                            li.style.cursor = 'pointer';
                            li.textContent = company.companyName;

                            li.addEventListener('mousedown', function() {
                                resetCompanyNameInput.value = company.companyName;
                                resetBizNo.value = company.bizNo;
                                resetCompanyDropdown.style.display = 'none';
                            });
                            resetCompanyDropdown.appendChild(li);
                        });
                        resetCompanyDropdown.style.display = 'block';
                    } else {
                        resetCompanyDropdown.innerHTML = '<li class="list-group-item text-muted">검색 결과가 없습니다.</li>';
                        resetCompanyDropdown.style.display = 'block';
                    }
                })
                .catch(error => console.error('Error:', error));
        }, 300);
    });
	
	resetCompanyNameInput.addEventListener('blur', function() {
	    // 포커스를 잃는 순간, 검색 0.3초 대기 타이머를 강제로 취소
	    clearTimeout(resetDebounceTimer);
	    
	    // 드롭다운 닫기
	    resetCompanyDropdown.style.display = 'none';
	});

    function showMessage(message, type) {
        messageArea.className = `alert alert-${type}`;
        messageArea.textContent = message;
    }

    function clearMessage() {
        messageArea.className = 'alert d-none';
        messageArea.textContent = '';
    }

    function validateSendForm() {
        clearMessage();
        
        // 유효성 검사
        if (!resetBizNo.value.trim()) {
            showMessage('기업을 검색하여 선택해주세요.', 'danger');
            resetCompanyNameInput.focus();
            return false;
        }

        if (!login.value.trim()) {
            showMessage('아이디를 입력하세요.', 'danger');
            login.focus();
            return false;
        }

        if (!email.value.trim()) {
            showMessage('이메일을 입력하세요.', 'danger');
            email.focus();
            return false;
        }

        return true;
    }

    sendBtn.addEventListener('click', async () => {
        try {
            if (!validateSendForm()) {
                return;
            }

            const response = await csrfFetch('/email/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    bizNo: resetBizNo.value, // ⭐ 수정됨
                    login: login.value,
                    email: email.value
                })
            });

            if (!response.ok) {
                throw new Error('서버 오류');
            }

            const result = await response.text();

            if (result === 'success') {
                sendBtn.disabled = true;
                showMessage('인증번호가 발송되었습니다.', 'success');
                verifyArea.classList.remove('d-none');
                verifyBtn.disabled = false;
				startPwVerifyTimer(180); // 타이머 시작
            } else if (result === 'no_user') {
                showMessage('회원정보가 일치하지 않습니다.', 'danger');
            } else {
                showMessage('인증 요청 중 오류가 발생했습니다.', 'danger');
            }
        } catch (error) {
            console.error(error);
            showMessage('서버 통신 중 오류가 발생했습니다.', 'danger');
        }
    });

    verifyBtn.addEventListener('click', async () => {
        try {
            if (!verifyNum.value.trim()) {
                showMessage('인증번호를 입력하세요.', 'danger');
                verifyNum.focus();
                return;
            }

            verifyBtn.disabled = true;
            const response = await csrfFetch('/email/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
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
            
            if (result === 'success') {
				//타이머 종료
				clearInterval(pwVerifyTimer);
                resetPwTimerDisplay.classList.add('d-none');
                verifyNum.disabled = true; // 성공한 인증번호 변경 불가 처리
				
                passwordArea.classList.remove('d-none');
                confirmBtn.classList.remove('d-none');
                verifyBtn.textContent = '인증완료';
                verifyBtn.disabled = true;
            } else {
                showMessage('인증번호가 올바르지 않거나 만료되었습니다.', 'danger');
				clearInterval(pwVerifyTimer);
                resetPwTimerDisplay.classList.add('d-none');
                verifyBtn.disabled = true;
                sendBtn.disabled = false;
                sendBtn.textContent = '재인증 요청';
                verifyNum.value = '';
                verifyNum.focus();
            }
        } catch (error) {
            console.error(error);
            showMessage('서버 통신 중 오류가 발생했습니다.', 'danger');
        }
    });

    // 경고문구 지우기 
    [resetCompanyNameInput, login, email, verifyNum].forEach(e => {
        e.addEventListener('input', () => {
            clearMessage();
        });
    });

    function showPasswordError(message) {
        passwordError.textContent = message;
        passwordError.classList.remove('d-none');
    }

    function showResultmsg(message) {
        resultMsg.textContent = message;
        resultMsg.classList.remove('d-none');
    }
    
    function clearPasswordMsg() {
        passwordError.textContent = '';
        resultMsg.textContent = '';
        passwordError.classList.add('d-none');
        resultMsg.classList.add('d-none');
    }

    confirmBtn.addEventListener('click', async () => {
        try {
            clearPasswordMsg();

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
            if (newPassword.value !== newPasswordCheck.value) {
                showPasswordError('비밀번호가 일치하지 않습니다.');
                newPasswordCheck.focus();
                return;
            }
            
            confirmBtn.disabled = true;

            const response = await csrfFetch('/email/resetPw', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    bizNo: resetBizNo.value,
                    login: login.value,
                    password: newPassword.value
                })
            });

            if (!response.ok) {
                throw new Error('서버 오류');
            }

            const result = await response.text();

			if (result === 'success') {
                showResultmsg('비밀번호가 성공적으로 변경되었습니다. 잠시 후 창이 닫힙니다.');
                confirmBtn.disabled = true;
                
                // 1초 대기 후 화면 새로고침하여 모달 닫기 및 초기화
                setTimeout(() => {
                    location.reload();
                }, 1000);
            } else {
                showResultmsg('비밀번호 변경에 실패했습니다.');
                confirmBtn.disabled = false;
            }

        } catch (error) {
            console.error(error);
            showMessage('서버 통신 중 오류가 발생했습니다.', 'danger');
            confirmBtn.disabled = false;
        }
    });

    // 모달 초기화
    const resetPwModal = document.querySelector('#resetPwModal');
    resetPwModal.addEventListener('hidden.bs.modal', () => {
		// 타이머 초기화
		if (pwVerifyTimer) clearInterval(pwVerifyTimer);
        resetPwTimerDisplay.classList.add('d-none');
        verifyNum.disabled = false;
        
		// 새로 추가된 인풋들 및 드롭다운 초기화
        resetCompanyNameInput.value = '';
        resetBizNo.value = '';
        resetCompanyDropdown.innerHTML = '';
        resetCompanyDropdown.style.display = 'none';
        
        login.value = '';
        email.value = '';
        verifyNum.value = '';

        newPassword.value = '';
        newPasswordCheck.value = '';

        clearMessage();
        verifyArea.classList.add('d-none');
        passwordArea.classList.add('d-none');
        confirmBtn.classList.add('d-none');
        clearPasswordMsg();
        
        sendBtn.disabled = false;
        verifyBtn.disabled = false;
        confirmBtn.disabled = false;
        
        sendBtn.textContent = '인증요청';
        verifyBtn.textContent = '인증';
    });
});