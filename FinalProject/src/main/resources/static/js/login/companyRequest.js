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

    // 폼 제출 시 최종 방어
    companyRequestForm.addEventListener('submit', function(e) {
        if (!isBizNoVerified) {
			// 사업자 번호 중복체크를 하지 않은 경우
            e.preventDefault(); // 제출 차단
			reqBizNoInput.classList.remove('is-valid');
            reqBizNoInput.classList.add('is-invalid');
            bizNoFeedback.className = 'small mt-1 text-danger';
            bizNoFeedback.textContent = '사업자번호 중복확인을 먼저 진행해주세요.';
            reqBizNoInput.focus();
        }
    });
});