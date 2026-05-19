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

const bizNoInput = document.getElementById('bizNo');

bizNoInput.addEventListener('input', function(e) {

    let value = e.target.value;

    // 숫자만 추출
    value = value.replace(/[^0-9]/g, '');

    // 최대 10자리
    value = value.substring(0, 10);

    // 하이픈 자동 추가
    if (value.length > 5) {
        value = value.replace(/^(\d{3})(\d{2})(\d{0,5})$/, '$1-$2-$3');
    } else if (value.length > 3) {
        value = value.replace(/^(\d{3})(\d{0,2})$/, '$1-$2');
    }

    e.target.value = value;
});
