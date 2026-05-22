const companyNameInput = document.getElementById('companyNameInput');
const bizNoInput = document.getElementById('bizNo');
const companyDropdown = document.getElementById('companyDropdown');
let debounceTimer;

// 1. 기업명 입력 시 자동완성 검색
companyNameInput.addEventListener('input', function(e) {
    const keyword = e.target.value.trim();
    
    // 사용자가 직접 타이핑을 수정하면 기존에 선택된 사업자번호 초기화
    bizNoInput.value = ''; 

    if (keyword.length === 0) {
        companyDropdown.style.display = 'none';
        companyDropdown.innerHTML = '';
        return;
    }

    // 서버 부하를 막기 위한 타이핑 후 0.3초 뒤에 검색
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetch(`/login/companies/search?keyword=${encodeURIComponent(keyword)}`)
            .then(response => response.json())
            .then(data => {
                companyDropdown.innerHTML = '';
                if (data.length > 0) {
                    data.forEach(company => {
                        const li = document.createElement('li');
                        li.className = 'list-group-item list-group-item-action cursor-pointer';
                        li.style.cursor = 'pointer';
                        li.textContent = company.companyName; // 기업명 표시
                        
                        // 클릭 시 선택 로직
                        li.addEventListener('click', function() {
                            companyNameInput.value = company.companyName; // 화면엔 기업명
                            bizNoInput.value = company.bizNo;             // hidden엔 사업자번호
                            companyDropdown.style.display = 'none';       // 드롭다운 닫기
                        });
                        companyDropdown.appendChild(li);
                    });
                    companyDropdown.style.display = 'block';
                } else {
                    companyDropdown.innerHTML = '<li class="list-group-item text-muted">검색 결과가 없습니다.</li>';
                    companyDropdown.style.display = 'block';
                }
            })
            .catch(error => console.error('Error:', error));
    }, 300);
});

// 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', function(e) {
    if (!companyNameInput.contains(e.target) && !companyDropdown.contains(e.target)) {
        companyDropdown.style.display = 'none';
    }
});
// 폼 제출 시 검증 및 자동 매칭 로직
document.getElementById('loginForm').addEventListener('submit', function(e) {
    // 폼 기본 제출 동작 막기
    e.preventDefault();

    const companyNameInput = document.getElementById('companyNameInput');
    const bizNoInput = document.getElementById('bizNo');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const feedbackDiv = companyNameInput.parentElement.querySelector('.invalid-feedback');

    // 기존 오류 디자인 제거
    [companyNameInput, username, password].forEach(input => {
        input.classList.remove('is-invalid');
    });

    // 기초 유효성 검사 (빈칸 체크)
    let isValid = true;
    if (!companyNameInput.value.trim()) {
        companyNameInput.classList.add('is-invalid');
        feedbackDiv.textContent = "기업명을 입력해주세요.";
        isValid = false;
    }
    if (!username.value.trim()) { 
		username.classList.add('is-invalid'); 
		isValid = false; 
	}
    if (!password.value.trim()) { 
		password.classList.add('is-invalid');
		isValid = false;
	}

    if (!isValid) return; // 빈칸이 있으면 여기서 중단

    // bizNo가 채워져 있는 경우 
    if (bizNoInput.value.trim()) {
        this.submit(); // this.submit()은 이벤트를 다시 발생시키지 않고 순수하게 폼만 제출합니다.
        return;
    }

    // 기업이름을 선택 안 하고, 이름만 치고 엔터/로그인 누른 경우 -> 자동 매칭 시도
    const keyword = companyNameInput.value.trim();
    
    fetch(`/login/companies/search?keyword=${encodeURIComponent(keyword)}`)
        .then(response => response.json())
        .then(data => {
            // 입력한 텍스트와 같은 이름의 기업 찾기
            const exactMatch = data.find(c => c.companyName === keyword);

            if (exactMatch) {
                // 정확히 일치하는 기업이 있으면 자동 세팅 후 제출
                bizNoInput.value = exactMatch.bizNo;
                companyNameInput.value = exactMatch.companyName;
                this.submit();
            } 
            else if (data.length === 1) {
                // 검색 결과가 딱 1개뿐이라면 (예: '예'만 쳤는데 '예담' 하나만 나올 때) 그걸로 제출
                bizNoInput.value = data[0].bizNo;
                companyNameInput.value = data[0].companyName;
                this.submit();
            } 
            else if (data.length > 1) {
                // 비슷한 이름의 기업이 여러 개일 경우 (예: 예담, 예담IT)
                companyNameInput.classList.add('is-invalid');
                feedbackDiv.textContent = "검색된 기업이 여러 개입니다. 정확한 기업을 선택해주세요.";
                // 사용자가 선택할 수 있게 드롭다운을 강제로 열어줍니다
                companyNameInput.dispatchEvent(new Event('input')); 
            } 
            else {
                // 검색 결과가 없을 경우
                companyNameInput.classList.add('is-invalid');
                feedbackDiv.textContent = "존재하지 않거나 비활성화된 기업입니다.";
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("기업 정보를 확인하는 중 오류가 발생했습니다.");
        });
});

document.addEventListener("DOMContentLoaded", function() {
    // 주소창에 파라미터(?error=... 또는 ?logout)가 있는지 확인
    if (window.location.search.includes('error') || window.location.search.includes('logout')) {
        
        // 브라우저가 History API를 지원하는 경우
        if (window.history.replaceState) {
            // 파라미터를 제외한 깨끗한 원본 URL만 추출 (/login)
            const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
            
            // 페이지 이동 없이 주소창의 URL만 깨끗한 URL로 덮어쓰기
            window.history.replaceState({ path: cleanUrl }, '', cleanUrl);
        }
    }
});