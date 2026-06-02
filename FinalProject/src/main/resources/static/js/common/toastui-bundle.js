/**
 * TOAST UI Viewer 번들 지연 로드
 */
var ToastUiBundle = (function () {
    var VIEWER_SRC = 'https://uicdn.toast.com/editor/3.2.2/toastui-editor-viewer.min.js';
    var viewerLoaded = false;
    var loadingPromise = null;

    function loadViewer() {
        if (viewerLoaded) {
            return Promise.resolve();
        }
        var existing = document.querySelector('script[data-toastui-bundle="viewer"]');
        if (existing && existing.getAttribute('data-loaded') === 'true') {
            viewerLoaded = true;
            return Promise.resolve();
        }
        if (loadingPromise) {
            return loadingPromise;
        }
        loadingPromise = new Promise(function (resolve, reject) {
            if (existing) {
                existing.addEventListener('load', function () {
                    viewerLoaded = true;
                    resolve();
                }, { once: true });
                existing.addEventListener('error', reject, { once: true });
                return;
            }
            var script = document.createElement('script');
            script.src = VIEWER_SRC;
            script.setAttribute('data-toastui-bundle', 'viewer');
            script.onload = function () {
                script.setAttribute('data-loaded', 'true');
                viewerLoaded = true;
                resolve();
            };
            script.onerror = function () {
                reject(new Error('toastui viewer load failed'));
            };
            document.head.appendChild(script);
        }).finally(function () {
            loadingPromise = null;
        });
        return loadingPromise;
    }

    function requireViewer(callback) {
        loadViewer()
            .then(function () {
                if (window.toastui && window.toastui.Editor) {
                    callback();
                }
            })
            .catch(function () {
                if (typeof callback === 'function') {
                    callback();
                }
            });
    }

    return {
        requireViewer: requireViewer
    };
})();
