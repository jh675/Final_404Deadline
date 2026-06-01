document.addEventListener('DOMContentLoaded', function() {
    // 사용자가 다시 타이핑을 시작하면 에러(빨간 테두리) 표시를 지워주는 편의성 로직 추가
    ['newPassword', 'confirmPassword'].forEach(id => {
        document.getElementById(id).addEventListener('input', function() {
            this.classList.remove('is-invalid');
            document.getElementById('generalFeedback').textContent = '';
        });
    });
});

function changePassword() {
	const newPasswordInput = document.getElementById('newPassword');
	const confirmPasswordInput = document.getElementById('confirmPassword');
	const newPassword = newPasswordInput.value;
	const confirmPassword = confirmPasswordInput.value;
	const generalFeedback = document.getElementById('generalFeedback');

	// 초기화 (기존에 띄워둔 에러 메세지 지우기)
	newPasswordInput.classList.remove('is-invalid');
	confirmPasswordInput.classList.remove('is-invalid');
	generalFeedback.textContent = '';

	let isValid = true;

	// 1. 새 비밀번호 빈칸 검사
	if (!newPassword || newPassword.trim() === '') {
		newPasswordInput.classList.add('is-invalid');
		isValid = false;
	}

	// 2. 비밀번호 일치 검사
	if (newPassword && newPassword !== confirmPassword) {
		confirmPasswordInput.classList.add('is-invalid');
		isValid = false;
	}

	// 유효성 검사를 통과하지 못하면 서버로 전송하지 않고 중단
	if (!isValid) return;

	// 서버로 새 비밀번호 전송
	csrfFetch('/login/password-reset', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({
			'newPassword': newPassword
		})
	})
	.then(response => response.text())
	.then(result => {
		if (result === 'success') {
			// 성공 알림은 확인 버튼을 눌러야 넘어가도록 alert 유지 
			alert('비밀번호가 성공적으로 변경되었습니다.');
			window.location.href = '/'; // 메인 페이지로 이동
		} else {
			// 실패 시 화면 하단에 텍스트로 에러 표시
			generalFeedback.textContent = '비밀번호 변경에 실패했습니다. 다시 시도해주세요.';
		}
	})
	.catch(error => {
		console.error('Error:', error);
		// 서버 연결 실패 알림 (요청하신 대로 유지)
		alert('서버와 통신 중 오류가 발생했습니다.');
	});
}