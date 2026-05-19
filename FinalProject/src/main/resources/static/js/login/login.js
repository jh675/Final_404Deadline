document.getElementById('loginForm').addEventListener('submit', function(e) {

	let isValid = true;

	const bizNo = document.getElementById('bizNo');
	const username = document.getElementById('username');
	const password = document.getElementById('password');

	// 기존 오류 제거
	[bizNo, username, password].forEach(input => {
		input.classList.remove('is-invalid');
	});

	// 기업번호 체크
	if (!bizNo.value.trim()) {
		bizNo.classList.add('is-invalid');
		isValid = false;
	}

	// 아이디 체크
	if (!username.value.trim()) {
		username.classList.add('is-invalid');
		isValid = false;
	}

	// 비밀번호 체크
	if (!password.value.trim()) {
		password.classList.add('is-invalid');
		isValid = false;
	}

	// submit 막기
	if (!isValid) {
		e.preventDefault();
	}

});
