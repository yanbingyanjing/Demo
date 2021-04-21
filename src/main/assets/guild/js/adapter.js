;(function (doc, win) {
    var resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize'

    function calc () {
        var dEl = document.documentElement,
            clientWidth = dEl.clientWidth
            
        dEl.style.fontSize = 100 * (clientWidth / 750) + 'px'
    }

    win.addEventListener(resizeEvt, calc);
    doc.addEventListener('DOMContentLoaded', calc);
})(document, window)
;