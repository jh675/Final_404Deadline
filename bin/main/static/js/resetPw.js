document.addEventListener('DOMContentLoaded', () => {

    console.log('resetPw.js loaded');

    /*
        csrf
    */
    const csrfMeta =
        document.querySelector('meta[name="_csrf"]');

    const csrfHeaderMeta =
        document.querySelector('meta[name="_csrf_header"]');

    /*
        csrf 확인
    */
    if (!csrfMeta || !csrfHeaderMeta) {

        console.error('CSRF meta 태그 없음');

        return;
    }

    const csrfToken =
        csrfMeta.content;

    const csrfHeader =
        csrfHeaderMeta.content;

    /*
        요소 확인
    */
    const sendBtn =
        document.querySelector('#sendCodeBtn');

    const verifyBtn =
        document.querySelector('#verifyBtn');

    /*
        버튼 확인
    */
    if (!sendBtn || !verifyBtn) {

        console.error('버튼 요소를 찾을 수 없습니다.');

        return;
    }

    console.log('버튼 연결 완료');

    /*
        인증번호 발송
    */
    sendBtn.addEventListener('click', async () => {

        console.log('send button click');

    });

    /*
        인증번호 확인
    */
    verifyBtn.addEventListener('click', async () => {

        console.log('verify button click');

    });

});