function initPopup() {
    if (WebSquare.isWebSquarePopup === true) {
        return;
    }
    WebSquare.isWebSquarePopup = true;
    var parentobj = opener || parent;
    var popupInfo;
    if (parentobj.WebSquare && parentobj.WebSquare._popupInfo) {
        popupInfo = parentobj.JSON.parse(parentobj.WebSquare._popupInfo);
        if (popupInfo) {
            WebSquare.net._setParameter(popupInfo);
        }
        parentobj.WebSquare._popupInfo = "";
    }
}
(function() {
    initPopup();
    var promiseObj = WebSquare.startApplication();
    promiseObj.then(function(resolve, reject) {
        // to do
    });
})();