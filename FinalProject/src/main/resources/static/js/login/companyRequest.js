document.addEventListener('DOMContentLoaded', function() {
    const reqBizNoInput = document.getElementById('reqBizNo');
    const btnCheckBizNo = document.getElementById('btnCheckBizNo');
    const bizNoFeedback = document.getElementById('bizNoFeedback');
    const companyRequestForm = document.getElementById('companyRequestForm');
    
    let isBizNoVerified = false; // 중복확인 통과 여부 플래그

    // 사업자번호 자동 포맷팅 (XXX-XX-XXXXX)
    reqBizNoInput.addEventListener('input', function(e) {
        // 숫자가 아닌 모든 문자 제거
        let val = e.target.value.replace(/[^0-9]/g, '');
        
        // 길이에 맞춰 하이픈(-) 삽입
        if(val.length > 3 && val.length <= 5) {
            val = val.replace(/(\d{3})(\d+)/, '$1-$2');
        } else if (val.length > 5) {
            val = val.replace(/(\d{3})(\d{2})(\d+)/, '$1-$2-$3');
        }
        
        e.target.value = val;
        
        // 사용자가 다시 타이핑하면 중복확인 초기화
        isBizNoVerified = false;
        reqBizNoInput.classList.remove('is-valid', 'is-invalid');
        bizNoFeedback.textContent = '';
    });

    // 중복 확인 요청
    btnCheckBizNo.addEventListener('click', async function() {
        const bizNo = reqBizNoInput.value;
        
        if (bizNo.length !== 12) {
            reqBizNoInput.classList.add('is-invalid');
            bizNoFeedback.textContent = '사업자번호 10자리를 정확히 입력해주세요.';
            bizNoFeedback.className = 'small mt-1 text-danger';
            return;
        }

        try {
            // Controller에 만들어둘 API로 검사 요청
            const response = await fetch(`/login/company/check-bizno?bizNo=${bizNo}`);
            const result = await response.json();

            reqBizNoInput.classList.remove('is-valid', 'is-invalid');

            if (result.exists) {
                // 존재할 경우 (상태에 따라 메시지 출력)
                reqBizNoInput.classList.add('is-invalid');
                bizNoFeedback.className = 'small mt-1 text-danger';
                
                let statusText = '';
                if (result.statusCd === '01ACTIVE') statusText = '활성';
                else if (result.statusCd === '02ACTIVE') statusText = '비활성';
                else if (result.statusCd === '03ACTIVE') statusText = '승인대기';

                bizNoFeedback.textContent = `이미 '${statusText}' 상태로 등록된 기업입니다.`;
                isBizNoVerified = false;
            } else {
                // 존재하지 않을 경우
                reqBizNoInput.classList.add('is-valid');
                bizNoFeedback.className = 'small mt-1 text-success';
                bizNoFeedback.textContent = '등록 가능한 사업자번호입니다.';
                isBizNoVerified = true;
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 통신 중 오류가 발생했습니다.');
        }
    });

	// 폼 제출 시 비동기 처리 및 1.5초 대기 로직
    companyRequestForm.addEventListener('submit', async function(e) {
        e.preventDefault(); 

        if (!isBizNoVerified) {
            // 사업자 번호 중복체크를 하지 않은 경우
            reqBizNoInput.classList.remove('is-valid');
            reqBizNoInput.classList.add('is-invalid');
            bizNoFeedback.className = 'small mt-1 text-danger';
            bizNoFeedback.textContent = '사업자번호 중복확인을 먼저 진행해주세요.';
            reqBizNoInput.focus();
            return;
        }

        const submitBtn = document.getElementById('requestSubmitBtn');
        const feedback = document.getElementById('requestFeedback');
        
        // 이중 클릭 방지
        submitBtn.disabled = true;
        submitBtn.textContent = '요청 중...';
        feedback.textContent = '';

        try {
            // 폼 데이터를 모아서 fetch로 백엔드에 전송 (Thymeleaf CSRF 토큰 자동 포함)
            const formData = new FormData(companyRequestForm);
            const response = await fetch(companyRequestForm.action, {
                method: 'POST',
                body: formData 
            });

            if (response.ok) {
                // 통신 성공 시
                feedback.className = 'small fw-bold text-success';
                feedback.textContent = '기업 등록 요청이 완료되었습니다. 잠시 후 창이 닫힙니다.';
                
                submitBtn.className = 'btn btn-success px-5 py-2 fw-semibold';
                submitBtn.textContent = '요청 완료';
                
                // 1.5초 대기 후 새로고침하여 모달 닫기
                setTimeout(() => {
                    location.reload(); 
                }, 1500);
                
            } else {
                feedback.className = 'small fw-bold text-danger';
                feedback.textContent = '요청 처리 중 오류가 발생했습니다.';
                submitBtn.disabled = false;
                submitBtn.textContent = '등록 요청';
            }
        } catch (error) {
            console.error('Error:', error);
            feedback.className = 'small fw-bold text-danger';
            feedback.textContent = '서버 통신 중 오류가 발생했습니다.';
            submitBtn.disabled = false;
            submitBtn.textContent = '등록 요청';
        }
    });
});