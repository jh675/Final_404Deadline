
//  csrf 정보

const csrfToken = document.querySelector('meta[name="_csrf"]').content;

const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

//  공통 fetch
async function csrfFetch(url, options = {}) {
    options.headers = {
        ...options.headers,
        [csrfHeader]: csrfToken
    };

    return fetch(url, options);
}