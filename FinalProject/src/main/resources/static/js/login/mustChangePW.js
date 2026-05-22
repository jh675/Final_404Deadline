function changePassword() {
	const newPassword = document.getElementById('newPassword').value;
	const confirmPassword = document.getElementById('confirmPassword').value;

	if (!newPassword || newPassword.trim() === '') {
		alert('새 비밀번호를 입력해주세요.');
		return;
	}

	if (newPassword !== confirmPassword) {
		alert('비밀번호가 일치하지 않습니다.');
		return;
	}

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
			alert('비밀번호가 성공적으로 변경되었습니다.');
			window.location.href = '/'; // 메인 페이지로 이동
		} else {
			alert('비밀번호 변경에 실패했습니다. 다시 시도해주세요.');
		}
	})
	.catch(error => {
		console.error('Error:', error);
		alert('서버와 통신 중 오류가 발생했습니다.');
	});
}