/**
 * @type common
 * Do not remove this line for Code Assist.
 */
let common = {};

common.unique = function (arr, comp) {
    const unique = arr
        .map(e => e[comp])
        .map((e, i, final) => final.indexOf(e) === i && i)
        .filter(e => arr[e]).map(e => arr[e]);
    return unique;
};

common.isEmptyStr = function(args){
    if(args == "" || args == null){
        return true;
    } else {
        return false;
    }
};

common.setScopeObj = function (scopObj) {
    // 23.02.15 changok
    // 기존 sp4에서 scwin은 최상위 부모페이지에 scwin이였다면 sp5에서는 common.setScopeObj가 호출되는 시점에 페이지에 scwin이라 비정상동작
    // 의도대로 최상위 부모페이지에 scwin 객체에 screenScopeObj를 저장하여 수정
    let topScreenObj = $p.top().scwin;
    topScreenObj.screenScopeObj = scopObj;
    //scwin.screenScopeObj = scopObj;
};

// 유효성 검사 체크 function
common.checkAllInputText = function(type, str){
    let checkYn = "";
    const check_num = /[0-9]/; // 숫자
    const check_eng = /[a-zA-Z]/; // 문자
    const check_spc = /[~!@#$%^&*()+|<>?:{}\[\]ㅤ]/; // 특수문자
    const check_kor = /[가-힣]/; // 한글체크

    const check_spc_workspace = /[_-]/;
    const check_eng_kor_num = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$/; // 한글, 영어, 숫자 정규식 체크 추가

    const check_project_name = /^(?=.*?[-ㄱ-ㅎㅏ-ㅣ가-힣A-Za-z0-9_.]).{4,40}$/;
    const check_app_id_dot = /^(?=.*?[-a-z0-9_.]).{1,20}$/;

    const check_id = /^[-A-Za-z0-9_]*$/;
    const check_email = /^[-A-Za-z0-9_]+[-A-Za-z0-9_.]*[@]{1}[-A-Za-z0-9_]+[-A-Za-z0-9_.]*[.]{1}[A-Za-z]{1,5}$/;
    const check_email_id = /^[-A-Za-z0-9_]+[-A-Za-z0-9_.]*$/;
    const check_email_domain = /^[-A-Za-z0-9_]+[-A-Za-z0-9_.]*[.]{1}[A-Za-z]{1,5}$/;
    const check_pw = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    const check_ip_port = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]){1}([:][0-9][0-9][0-9][0-9][0-9]?)$/;
    const check_domain_port = /^(http(s)?:\/\/)([a-z0-9\w]+\.*)+[a-z0-9]{2,4}$/gi;

    const check_app_version_1 = /^[0-9]*[.]{1}[0-9]{1,6}$/;
    const check_app_version_2 = /^[0-9]*[.]{1}[0-9]*[.]{1}[0-9]{1,6}$/;
    const check_app_version_3 = /^[0-9]*[.]{1}[0-9]*[.]{1}[0-9]*[.]{1}[0-9]{1,6}$/;
    const check_app_version_4 = /^[0-9]*[.]{1}[0-9]*[.]{1}[0-9]*[.]{1}[0-9]*[.]{1}[0-9]{1,6}$/;
    const check_app_version_code = /^[0-9]{1,5}$/;
    const check_app_id_dot2 = /^[a-z]\.|[a-z]\.|[a-z]$/;
    const check_app_id_dot3 = /^[a-z]|.|[a-z].|[a-z].|[a-z]$/;
    const check_app_id_dot4 = /^[a-z]|.|[a-z].|[a-z].|[a-z].|[a-z]$/;

    // type 종류로 체크, return ;
    switch (type) {
        case "CHECK_INPUT_TYPE_NUM" :
            checkYn = check_num.test(str);
            if(!checkYn){
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_ENG" :
            checkYn = check_eng.test(str);
            if(!checkYn){
                 return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_SPC" :
            checkYn = check_spc.test(str);
            if(!checkYn){
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_KOR" :
            checkYn = check_kor.test(str);
            if(!checkYn){
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_ID" :
            checkYn = check_id.test(str);
            if (!checkYn) {
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_EMAIL" :
            checkYn = check_email.test(str);
            if(!checkYn){
                //alert('이메일 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_EMAIL_ID" :
            checkYn = check_email_id.test(str);
            if(!checkYn) {
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_EMAIL_DOMAIN" :
            checkYn = check_email_domain.test(str);
            if(!checkYn) {
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_PW" :
            checkYn = check_pw.test(str);
            if(!checkYn){
                //alert('비밀번호는 8자 이상이어야 하며, 숫자/소문자/특수문자를 모두 포함해야 합니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_IP_PORT" :
            checkYn = check_ip_port.test(str);
            if(!checkYn){
                // alert('IP, PORT 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_WORKSPACE" :
            // 세부 체크 기능 필수
            checkYn = check_eng.test(str);
            if(!checkYn){
                // alert('WORKSPACE 생성 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_PROJECT_NAME" :
            // 세부 체크 기능 필수
            if(!check_project_name.test(str)){
                //alert('프로젝트 이름 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_APP_VERSION" :
            // 세부 체크 기능 필수
            if(!check_app_version_1.test(str) && !check_app_version_2.test(str) && !check_app_version_3.test(str) && !check_app_version_4.test(str)){
                //alert('앱 버전 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_APP_VERSION_CODE" :
            // 세부 체크 기능 필수
            checkYn = check_app_version_code.test(str);
            if(!checkYn){
               return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_APP_ID2" :
            // 세부 체크 기능 필수
            checkYn = check_app_id_dot.test(str);
            if(!checkYn){
                //alert('앱 아이디 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_SERVER_DOMAIN" :
            checkYn = check_domain_port.test(str);
            if(!checkYn){
                //alert('앱 아이디 형식에 맞지 않습니다.');
                return false;
            }
            return true;
            break;
        case "CHECK_INPUT_TYPE_KOR_ENG_NUM" :
            checkYn = check_eng_kor_num.test(str);
            if(!checkYn){
                return false;
            }
            return true;
            break;
        default :
            break;
    }

};

// 공통화 작업을 위해서 임시로 소스 코드 넣어둠
common.checkChar = function(obj) {
      var chrTemp;
      var strTemp    = obj.name;
      var strLen     = strTemp.length;
      var chkAlpha   = false;
      var resString  = '';
      var testTemp = '';

      if (strLen > 0) {

        for (var i=0; i<strTemp.length; i++)
        {

          chrTemp = strTemp.charCodeAt(i);
          testTemp = String.fromCharCode(chrTemp)
          // console.log(testTemp);
          if (((chrTemp > 44031 && chrTemp < 55203) || (chrTemp > 12592 && chrTemp < 12644))) {
            chkAlpha = true;

          } else {
            resString = resString + String.fromCharCode(chrTemp);

          }
        }
      }

      if (chkAlpha == true) {
        var message = common.getLabel("lbl_commonjs_check_char");
        alert(message); //한글, 영문, 숫자만 입력하세요
        obj.value = resString;
        obj.focus();

        return false;
      }

};

common.getCheckInputLength = function(obj_value, obj_length, max_length){
    var str = obj_value;
    var str_length = obj_length; // 전체길이
    var i = 0;
    var ko_byte = 0;
    var one_char = ""; // 한글자씩 검사한다

    for (i = 0; i < str_length; i++) {
        one_char = str.charAt(i); // 한글자추출
        ko_byte++;
    }

    if (ko_byte > max_length) {
        var message = common.getLabel("lbl_commonjs_check_input_length");
    	alert(max_length + " " + message); //글자 이상 입력할 수 없습니다
        return true;
    }else {
        return false;
    }

};

common.calBytes = function(str){
  var tcount = 0;

  var tmpStr = new String(str);
  var temp = tmpStr.length;

  var onechar;
  for ( k=0; k<temp; k++ ) {
    onechar = tmpStr.charAt(k);
    if (escape(onechar).length > 4)
    {
      tcount += 2;
    }
    else
    {
      tcount += 1;
    }
  }

  return tcount;
};

common.getLabel = function(key) {
    return WebSquare.WebSquareLang[key];
};

common.getFormatStr = function(label,value){
    if(label == undefined){
        var message = common.getLabel("lbl_commonjs_global_undefined");
        return message; // global_undefined
    }

    var result = "";
    result = label.replace("%s",String(value));
    return result;
}

common.setLocale = function(locale) {
    WebSquare.cookie.setCookie( "system_language" , locale );
    $p.reinitialize( true );
};

common.getLocale = function() {
    return WebSquare.cookie.getCookie("system_language") ? WebSquare.cookie.getCookie("system_language"):"ko";
};

/**
 * executeAjaxToken 함수 호출 시 createPricingToken wspay 에서 발급하는 토큰 값을 가져오고 세팅한다.
 * 다음으로 접근하려는 url 에 호출한다.
 * url 호출시 token 값을 세팅해야하는게 전제다.
 * @param url
 * @param object
 * @param callback
 * @returns {Promise<void>}
 */
let executeAjaxToken = async (url, object, callback) =>{

            let uri = common.uri.createPricingToken;
            let method = "GET";
            let headers = {"Content-Type": "application/json"};
            const response = await common.http.fetchGet(uri, method, headers, {})
            let data = await response.json();
            let authorization = data[0].Authorization;
            executeAjax(url, authorization, object, callback);

}

/**
 * pay.inswave.com 도메인내 url을 호출 하는 기능이다.
 * 해당 스크립트는 w쉐어링 팀에서 제공해줬다.
 * @param url
 * @param auth
 * @param object
 * @param callback
 */
let executeAjax = (url, auth, object, callback) => {
    var xhr = new XMLHttpRequest();
    var sendUrl = "https://pay.inswave.com" + url;
    xhr.open("POST", sendUrl, true);
    xhr.setRequestHeader("Content-Type", "application/json; charset=utf-8");
    xhr.setRequestHeader("Proworks-Body", "Y");
    xhr.setRequestHeader("Proworks-Lang", "ko");
    xhr.setRequestHeader("Authorization", auth);
    xhr.setRequestHeader("AuthInfo", "WHive");

    xhr.onload = function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            var data = JSON.parse(xhr.responseText);
            callback(data);
        } else {
            var data = {
                "code": xhr.status,
                "message": xhr.responseText,
                "error": "Error occurred"
            };
            alert("[ HTTP 상태 ] " + data.code + " 에러가 발생했습니다.");
        }
    };

    xhr.onerror = function() {
        var data = {
            "code": xhr.status,
            "message": "Network error occurred",
            "error": "Error occurred"
        };
        alert("[ HTTP 상태 ] " + data.code + " 에러가 발생했습니다.");
    };

    var jsonData = JSON.stringify(object);
    xhr.send(jsonData);
}


common.http = {};

/**
 * Fetch with return
 * @param url String, request url
 * @param method String, GET, POST, PUT, DELETE 등
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param body json, ex: {'history_id': history_id}
 * @param options: json
 * @returns {Promise<any>}
 */
common.http.fetch = async (url, method, headers, body, options) => {
    const response = await fetch(url, {
        method: method,
        headers: headers,
        body: JSON.stringify(body)
    });
    const responseJson = await response.json();
    
    return responseJson;
};

/**
 * Fetch with return
 * @param url String, request url
 * @param method String, GET, POST, PUT, DELETE 등
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param options: json
 * @returns {Promise<any>}
 */
common.http.fetchGet = async (url, method, headers, options) => {
    let response = await fetch(url, {
        method: method,
        headers: headers
    });

    return response;
};

/**
 * Fetch with return
 * @param url String, request url
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param options: json
 * @returns {Promise<any>}
 */
common.http.fetchPost = (url, headers, body, options) => {
    const response = fetch(url, {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body)
    });

    return response;
};

/**
 * Just fetch
 * @param url String, request url
 * @param method String, GET, POST, PUT, DELETE 등
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param body json, ex: {'history_id': history_id}
 * @param options: json
 * @returns {Promise<void>}
 */
common.http.justFetch = async (url, method, headers, body, options) => {
    try {
        await fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(body)
        })
    } catch (e) {
        console.log(e);
    }
}

/**
 * Fetch with callback
 * @param url String, request url
 * @param method String, GET, POST, PUT, DELETE 등
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param body json, ex: {'history_id': history_id}
 * @param options: json
 * @param callback: callback
 * @returns {Promise<void>}
 */
common.http.fetchWithCallback = async (url, method, headers, body, options, callback) => {
    try {
        await fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(body)
        })
        .then(response => response.json())
        .then(data => {
            callback(data, options);
        })
        .catch(e => {
            callback({"error":e});
        });
    } catch (e) {
        console.log(e);
        callback({"error":e});
    }
};

/**
 * 해당 url의 파일을 다운로드 받는다
 * @param url String, request url
 * @param method String, GET, POST, PUT, DELETE 등
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param body json, ex: {'history_id': history_id}
 * @param options: json ex: {type: 'application/octet-stream}, {type: 'text/plain; charset=utf-8;'}, {type: 'binary mime-type'} 등
 * @returns {Promise<void>}
 */
common.http.fileDownload = async (url, method, headers, body, options) => {
    try {
        const response = await fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(body)
        });

        const result = await response.blob();
        const responseHeader = await response.headers;
        const blob = new Blob([result], options);
        const blobUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = responseHeader.get('filename');
        link.click();
        URL.revokeObjectURL(url);
        delete a;
    } catch (e) {
        console.log(e);
    }
};

/**
 * 해당 url의 파일을 다운로드 받는다
 * @param blobData: blob 데이터
 * @param options: json ex: {type: 'application/octet-stream}, {type: 'text/plain; charset=utf-8;'}, {type: 'binary mime-type'} 등
 * @returns {Promise<void>}
 */
common.http.fileDownloadWithBlob = (blobData, fileName, options) => {
    try {
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(dataBlob, fileName)
        } else {
            const blob = new Blob(["\ufeff" + blobData], options);
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = fileName;
            link.click();
            window.URL.revokeObjectURL(url);
            delete a;
        }
    } catch (e) {
        console.log(e);
    }
};

/**
 * 해당 url의 파일을 다운로드 받는다. 단, blob이 아닌 url 링크로 처리한다.
 * @param url String, request url
 * @param method String, GET, POST, PUT, DELETE 등
 * @param headers json, ex: {'Content-Type': 'application/json'}
 * @param body json, ex: {'history_id': history_id}
 * @param options: json ex: {type: 'application/octet-stream}, {type: 'text/plain; charset=utf-8;'}, {type: 'binary mime-type'} 등
 * @returns {Promise<void>}
 */
common.http.fileDownloadWithURL = async (url, method, headers, body, options) => {
    try {
        // const message = common.getLabel("lbl_file_downloading");
        // WebSquare.layer.showProcessMessage(message);
        const response = await fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(body)
        });

        const responseHeader = await response.headers;
        const downloadURL = responseHeader.get("downloadURL");
        const link = document.createElement('a');
        link.href = downloadURL;
        link.click();
        delete a;
        // WebSquare.layer.hideProcessMessage();
    } catch (e) {
        // WebSquare.layer.hideProcessMessage();
        console.error(e);
        common.win.alert("파일 다운로드에 실패했습니다.");
    }
};

common.http.fetchFileUpload = async (uri, method, data) => {
    const response= fetch(uri, {
        method: method,
        headers: {},
        body: data
    });
    return response;
};

common.http.fetchUpload = async (uri, method, header, data) => {
    const response= fetch(uri, {
        method: method,
        headers: header,
        body: data
    });
    return response;
};

common.win = {};

common.win.alertHandler = function(r){};

// window alert 대체
// ex) common.win.alert("message");
common.win.alert = function (message, callbackFunc, opts) {
    return new Promise(function (resolve, reject) {
        let options = common.win.getPopupOpt();
        common.win.alertHandler = function () {
            resolve(true);
        };

        options.dataObject = {
            "type" : "json",
            "name" : "param",
            "data" : {
                message: message,
                messageType: "alert",
                popupId: options.id,
                callbackFn: "common.win.alertHandler",
            }
        };
        $p.openPopup("/ui/comn/message_box.xml", options);
    });
};

// 확인/취소 버튼 눌렀을때 반환하기위한 handler함수
common.win.confirmHandler = function(rtn){};

// window confirm 대체
// ex) await common.win.confirm("message");
common.win.confirm = function (message, callbackFunc, opts) {
    return new Promise(function (resolve, reject) {
        common.win.confirmHandler = function (rtn){
            resolve(rtn);
        };

        let options = common.win.getPopupOpt();
        options.dataObject = {
            "type" : "json",
            "name" : "param",
            "data" : {
                message: message,
                callbackFn: "common.win.confirmHandler",
                messageType: "confirm",
                popupId: options.id
            }
        };
        $p.openPopup("/ui/comn/message_box.xml", options);
    });
};

// 확인/취소 버튼 눌렀을때 반환하기 위한 handler 함수
common.win.promptHandler = function(rtn){};

// textbox가 달린 confirm
// ex) await common.win.prompt("message");
common.win.prompt = function(message, callbackFunc, opts) {
        return new Promise(function(resolve, reject) {
            common.win.promptHandler = function (rtn) {
                resolve(rtn);

                const tabIdx = $p.top().tabc_layout.selectedIndex;
                const tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                const tabInfo = $p.top().tabc_layout.getTabInfo();

                if (tabInfo.length > 0) {
                    switch (tabInfo[tabIdx].label.toLowerCase()) {
                        case "workspace":
                            tabWindow.scwin.delete_build_project_pid(rtn.project_id, rtn.project_name);
                            break;
                        case 'workspace 관리':
                            tabWindow.scwin.delete_workspace_name_onclick(rtn.workspace_id, rtn.workspace_name);
                            break;
                        case '사용자 상세':
                            tabWindow.scwin.pricing_cancel_action_api(rtn.regDate,rtn.regNextDate);
                            break;
                    }
                }
            };

        let options = common.win.getPopupOpt();
        options.dataObject = {
            "type": "json",
            "name": "param",
            "data": {
                message: message,
                callbackFn: "common.win.promptHandler",
                messageType: "prompt",
                popupId: options.id,
                opts: opts
            }
        };
        $p.openPopup("/ui/comn/message_box.xml", options);
    });
};

common.win.getPopupOpt = function(){
    const popUpId = "popup"+ (new Date()).getTime();

    const popupNameVal = common.getLabel("lbl_noti");

    let opt = {
        id : popUpId,
        popupName : popupNameVal,
        type : "wframePopup",
        width : "320px",
        height : "200px",
        modal : true,
        alwaysOnTop : false,
        useModalStack : true,
        resizable : true,
        useMaximize : false,
        className : "",
        scrollbars : false,
        windowDragMove : true,
        frameMode : "wframe",
        disableCloseButton : false
    };

    return opt;
}

common.win.closePopup = function(popupId){
    try {
        $p.closePopup(popupId);
    } catch(e){
        console.log(e);
    }
};

common.ws = {};
common.ws.socket = null;
common.ws.interval = null;

common.ws.connect = (userData)=>{
    common.ws.socket = new WebSocket(g_config.WEBSOCKETSERVER_URL + "/whivebranch");

    common.ws.socket.onopen = () => {
        const name = userData.user_name + "\n" + userData.domain_name;
        const member_role = userData.user_role;
        const user_login_id = userData.user_login_id;

        common.ws.send(JSON.stringify({
            MsgType: "HV_MSG_USER_LOGIN_SESSION_INFO",
            sessType: "HEADQUATER",
            name: name,
            member_role: member_role,
            userId: user_login_id
        }));

        common.ws.interval = setInterval(function () {
            common.ws.send(JSON.stringify({MsgType: "HV_MSG_HEADQUATER_SESSID_INFO", sessId: "head_main","hqKey":user_login_id}));
        }, 30000);

        common.util.addEventVisibility(common.ws.visibilityListenr);
    };

    common.ws.socket.onclose = async function () {
        console.log("ws onclose");

        // socket이 끊어졌을때 healthCheck interval제거
        clearInterval(common.ws.interval);
        common.ws.interval = null;

        // 화면이 보이고 websocket에 상태가 closed일때
        if(common.util.isVisible() && common.ws.socket.readyState == 3){
            console.log("isVisible: true & ws closed")
            // session 만료체크후 reconnect요청
            common.ws.reconnect();
        }
    };

    common.ws.socket.onerror = function (e) {
        console.error("WebSocket error observed:", e);
    };

    common.ws.socket.onmessage = function (e) {
        common.message.wsBinMessage(e);
    };
};

common.ws.reconnect = function() {
    console.log("reconnect");
    common.http.fetch(g_config.HTTPSERVER_URL + common.uri.loginCheck, "POST", {"Content-Type":"application/json"}, {})
        .then(async (res) => {
            if (Array.isArray(res)) {
                if (res[0].is_login === "no") {
                    if(await common.win.alert("세션이 끊겼습니다. 다시 로그인 해주세요.")){
                        $p.url(common.uri.logout);
                    }
                } else {
                    const userData = $p.top().__account_info__.getRowJSON(0);
                    if(userData!=null){
                        common.ws.connect(userData);
                    } else {
                        if(await common.win.alert("세션이 끊겼습니다. 다시 로그인 해주세요.")){
                            $p.url(common.uri.logout);
                        }
                    }
                }
            } else {
                if(await common.win.alert("세션이 끊겼습니다. 다시 로그인 해주세요.")){
                    $p.url(common.uri.logout);
                }
            }
        }).catch(async (e)=>{
            if(await common.win.alert("세션이 끊겼습니다. 다시 로그인 해주세요.")){
                $p.url(common.uri.logout);
            }
        });
};

common.ws.send = (data)=>{
    common.ws.socket.send(data);
};

common.ws.close = () => {
    if(!!common.ws.socket){
        common.ws.socket.close();
    }
}

common.ws.visibilityListenr = (e) => {

    const state = common.util.isVisible();
    if(state){
        console.log('visible');
        // closed state
        if(common.ws.socket.readyState == 3){
            common.ws.reconnect();
        }
    } else {
        console.log('hidden');
        common.ws.close();
    }
}



common.message = {};

common.message.binTypeOnMessage = (binType, resultBlob) => {
    let tabIdx;
    let tabWindow;
    let deployTaskStep3;
    let wframeWindow;

    switch (binType) {
        case "HV_BIN_APP_ICON_READ":
            //scwin.websocketBinaryCallback(binType, resultBlob);
            break;
        case "HV_BIN_DEPLOY_METADATA_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployMetadataImageRead(binType, resultBlob);
                }
            }
            break;
        case "HV_BIN_DEPLOY_METADATA_PHONE_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployMetadataPhoneImageRead(binType, resultBlob);
                }
            }
            break;
        case "HV_BIN_DEPLOY_METADATA_TABLET_SMALL_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployMetadataTabletSmallImageRead(binType, resultBlob);
                }
            }
            break;
        case "HV_BIN_DEPLOY_METADATA_TABLET_LARGE_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployMetadataTabletLargeImageRead(binType, resultBlob);
                }
            }
            break;
        case "HV_BIN_DEPLOY_ANDROID_METADATA_PHONE_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployAndroidMetadataPhoneImageRead(binType, resultBlob);
                }
            }
            break;
        case "HV_BIN_DEPLOY_ANDROID_METADATA_TABLET_7INCH_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployAndroidMetadataTablet7InchImageRead(binType, resultBlob);
                }
            }
            break;
        case "HV_BIN_DEPLOY_ANDROID_METADATA_TABLET_10INCH_IMAGE_READ": // project_deploy_task_setting_step03
            tabIdx = $p.top().tabc_layout.selectedIndex;
            tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
            // /ui/works/project_deploy_task_setting_step1.xml
            deployTaskStep3 = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
            if(!!deployTaskStep3){
                if(deployTaskStep3.render.style.getPropertyValue("display") != "none") {
                    wframeWindow = deployTaskStep3.getWindow();
                    wframeWindow.scwin.getBinDeployAndroidMetadataTablet10InchImageRead(binType, resultBlob);
                }
            }
            break;
        default:
            break;
    }
};

common.message.wsBinMessage = function (ev) {
    if (ev.data instanceof Blob) {
        function getStringUTF8(dataview, offset, length) {
            let s = '';
            for (let i = 0, c; i < length;) {
                c = dataview.getUint8(offset + i++); // 해당 구간 에러
                s += String.fromCharCode(
                    c > 0xdf && c < 0xf0 && i < length - 1
                        ? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
                        | dataview.getUint8(offset + i++) & 0x3f
                        : c > 0x7f && i < length
                            ? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
                            : c
                );
            }
            return s;
        }

        let reader = new FileReader();
        reader.addEventListener("loadend", function () {
            /// Parse binary
            let dataView = new DataView(reader.result);

            let offset = 0;
            let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

            let adminIdLength = dataView.getInt32(offset);
            offset = DEFAULT_READ_BYTES;

            let adminID = getStringUTF8(dataView, offset, adminIdLength);
            offset += adminIdLength;

            let hqKeyLength = dataView.getInt32(offset);
            offset += DEFAULT_READ_BYTES;

            let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
            offset += hqKeyLength;

            let binTypeLength = dataView.getInt32(offset);
            offset += DEFAULT_READ_BYTES;

            let binType = getStringUTF8(dataView, offset, binTypeLength);
            offset += binTypeLength;

            common.message.binTypeOnMessage(binType, ev.data);

            let fileNameLength = dataView.getInt32(offset);
            offset += DEFAULT_READ_BYTES;

            let fileName = getStringUTF8(dataView, offset, fileNameLength);
            offset += fileNameLength;

            let dataLength = dataView.getInt32(offset);
            offset += DEFAULT_READ_BYTES;

            let a = null,
                pureData = null,
                dataBlob = null,
                downloadURL = null;

            if (common.util.endsWith(fileName, 'txt')) {
                pureData = getStringUTF8(dataView, offset, dataLength);
                common.http.fileDownloadWithBlob(pureData, fileName, {type: 'text/plain; charset=utf-8;'});
            } else if (common.util.endsWith(fileName, 'apk')) {
                pureData = getStringUTF8(dataView, offset, dataLength);
                common.http.fileDownloadWithBlob(pureData, fileName, {type: 'binary mime-type'});
            } else if (common.util.endsWith(fileName, 'ipa')) {
                pureData = getStringUTF8(dataView, offset, dataLength);
                common.http.fileDownloadWithBlob(pureData, fileName, {type: 'binary mime-type'});
            }
        });
        reader.readAsArrayBuffer(ev.data);
    } else {
        common.message.onMessage(ev);
    }
};


common.message.onMessage = (e) => {
    const received_msg = JSON.parse(e.data);
    let message;
    let tabIdx;
    let tabWindow;
    let wframeWindow;
    let step1Wfrm;
    let step2Wfrm;
    let step3Wfrm;
    let settingStep1Wfrm;
    let settingStep2Wfrm;
    let settingStep2ConfigTabC;
    let importStep1Wfrm;
    let deployBody;

    if (received_msg) {
        switch (received_msg.MsgType) {

            // 강제 로그아웃 기능 추가
            case "HV_MSG_USER_LOGIN_SESSION_INFO":
            case "HV_MSG_LOG_INFO":
            case "HV_MSG_DEBUG_BUILD":
            case "HV_MSG_LOG_INFO_FROM_BRANCH":
            case "HV_MSG_BRANCH_SESSID_INFO":
            case "HV_MSG_HEADQUATER_SESSID_INFO":
            case "HV_MSG_SEND_FILE_INFO":
            case "HV_MSG_FORCE_LOGOUT":
                $p.top().scwin.logoutUpdateLastLoginDate();
                break;

            case "HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER":
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setBranchBuildStatus(received_msg);
                break;

            case "HV_MSG_PLUGIN_LIST_INFO_FROM_HEADQUATER":
                // /ui/work/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step03.xml
                step3Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step3");
                if(!!step3Wfrm){
                    if (step3Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step3Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderPluginListStatusNoBuildPage(received_msg);
                        break;
                    }
                }

                // /ui/works/project_setting_step03.xml
                settingstep3Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step3");
                if(!!settingstep3Wfrm){
                    if (settingstep3Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingstep3Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderPluginListStatusNoBuildPage(received_msg);
                        break;
                    }
                }
                break;
            case "HV_MSG_PLUGIN_ADD_LIST_INFO_FROM_HEADQUATER":
            case "HV_MSG_PLUGIN_REMOVE_LIST_INFO_FROM_HEADQUATER":
                // /ui/work/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step03.xml
                step3Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step3");
                if(!!step3Wfrm){
                    if (step3Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step3Wfrm.getWindow();
                        wframeWindow.scwin.setBranchPluginListStatus(received_msg);
                        break;
                    }
                }

                // /ui/works/project_setting_step03.xml
                settingstep3Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step3");
                if(!!settingstep3Wfrm){
                    if (settingstep3Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingstep3Wfrm.getWindow();
                        wframeWindow.scwin.setBranchPluginListStatus(received_msg);
                        break;
                    }
                }
                break;
            case "HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO_FROM_HEADQUATER":
                // /ui/work/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step03.xml
                step3Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step3");
                if(!!step3Wfrm){
                    if (step3Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step3Wfrm.getWindow();
                        wframeWindow.scwin.setBranchGitPullStatus(received_msg);
                    }
                }

                // /ui/works/project_setting_step03.xml
                settingstep3Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step3");
                if(!!settingstep3Wfrm){
                    if (settingstep3Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingstep3Wfrm.getWindow();
                        wframeWindow.scwin.setBranchGitPullStatus(received_msg);
                        break;
                    }
                }
                break;
            case "MV_MSG_SIGNIN_KEY_ADD_INFO_FROM_HEADQUATER":
                // proejct_setting.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // proejct_setting_step02.xml
                settingStep2Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step2");
                // project_setting_step02_app_config.xml
                settingStep2ConfigTabC = settingStep2Wfrm.$p.getComponentById("project_setting_step2_tap");
                const step2ConfigTabIdx = settingStep2ConfigTabC.selectedIndex;
                const step2ConfigTabWindow = settingStep2ConfigTabC.getWindow(step2ConfigTabIdx);
                step2ConfigTabWindow.setBranchPluginListStatus(received_msg);
                break;
            case "HV_MSG_CONNETCION_CHECK_INFO":
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step01.xml
                step1Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step1");
                if(!!step1Wfrm){
                    if (step1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step1Wfrm.getWindow();
                        wframeWindow.scwin.setBranchConnectionStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO_FROM_HEADQUATER":
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step01.xml
                step1Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step1");
                if(!!step1Wfrm){
                    if (step1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step1Wfrm.getWindow();
                        wframeWindow.scwin.setGeneralAppProjectCreateSyncStatus(received_msg);
                    }
                }
                break;
            case "MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO_HEADQUATER":
                WebSquare.layer.hideProcessMessage();
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setProjectExportDownload(received_msg);
                break;
            case "MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO_FROM_HEADQUATER":
                WebSquare.layer.hideProcessMessage();
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setProjectExportDownload(received_msg);
                break;
            case "HV_MSG_PROJECT_EXPORT_STATUS_INFO_FROM_HEADQUATER":
                message = common.getLabel("lbl_workspace_project_export");
                WebSquare.layer.showProcessMessage(message);
                break;
            case "HV_MSG_PROJECT_TEMPLATE_LIST_INFO_FROM_HEADQUATER":
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step01.xml
                step1Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step1");
                if(!!step1Wfrm){
                    if (step1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step1Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderToTemplateVersionSetting(received_msg);
                    }
                }

                // /ui/works/project_import_step01.xml

                break;
            case "BIN_FILE_PROFILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER":
                message = common.getLabel("lbl_key_setting_complete");
                common.win.alert(message);
                break;
            case "HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO_FROM_HEADQUATER" :
                // $p.top().scwin.setStatus(received_msg);
                break;
            case "HV_MSG_PROJECT_SERVER_CONFIG_UPDATE_STATUS_INFO_FROM_HEADQUATER" :

                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                settingStep2Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step2");
                if(!!settingStep2Wfrm){
                    if(settingStep2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep2Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderTemplateMultiProfileConfigStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_DEPLOY_STATUS_INFO_FROM_HEADQUATER":
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setBranchDeployStatus(received_msg);
                break;
            case "HV_MSG_CONNETCION_CHECK_INFO_FROM_HEADQUATER":
            case "HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_HEADQUATER":
            case "HV_MSG_PROJECT_TEMPLATE_STATUS_INFO_FROM_HEADQUATER":
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step01.xml
                step1Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step1");
                if(!!step1Wfrm){
                    if (step1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step1Wfrm.getWindow();
                        wframeWindow.scwin.setBranchProjectCreateSyncStatus(received_msg);
                        break;
                    }
                }

                // /ui/works/project_add_step02.xml
                step2Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step2");
                if(!!step2Wfrm){
                    if (step2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step2Wfrm.getWindow();
                        wframeWindow.scwin.setBranchProjectCreateSyncStatus(received_msg);
                        break;
                    }
                }

                // /ui/works/project_import_step01.xml
                importStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_import_step1");
                if(!!importStep1Wfrm){
                    if (importStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = importStep1Wfrm.getWindow();
                        wframeWindow.scwin.setBranchProjectCreateSyncStatus(received_msg);
                        break;
                    }
                }

                break;
            case "HV_MSG_DEPLOY_SETTING_STATUS_INFO_FROM_HEADQUATER":
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);

                // /ui/works/project_add_step02.xml
                step2Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step2");
                if (!!step2Wfrm){
                    if (step2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow =  step2Wfrm.getWindow()
                        wframeWindow.scwin.setBuilderDeploySettingStatus(received_msg);
                    }
                }

                // /ui/works/project_import_step01.xml
                importStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_import_step1");
                if (!!importStep1Wfrm){
                    if(importStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = importStep1Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderDeploySettingStatus(received_msg);
                    }
                }

                settingStep2Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step2");
                if(!!settingStep2Wfrm){
                    if(settingStep2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep2Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderDeploySettingStatus(received_msg);
                    }
                }

                // /ui/deploy/deploy.xml
                // 배포시에도 배포키 확인 및 deployinit을 한다
                deployBody = tabWindow.$p.getComponentById("mf_tabc_layout_contents_deploy_body");
                if(!!deployBody) {
                    wframeWindow = deployBody.getWindow();
                    wframeWindow.scwin.setBuilderDeploySettingStatus(received_msg);
                }
                break;
            case "BIN_FILE_APP_ICON_APPEND_SEND_INFO_FROM_HEADQUATER" :
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_add_step02.xml
                step2Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step2");
                if(!!step2Wfrm){
                    if(step2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step2Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderAppIconUploadStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_APP_DOWNLOAD_STATUS_INFO_FROM_HEADQUATER":
                // /ui/works/project_add.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_add_step02.xml
                step2Wfrm = tabWindow.$p.getComponentById("wfm_project_add_step2");
                if(!!step2Wfrm){
                    if(step2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = step2Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderAppDownloadHref(received_msg);
                    }
                }
                // /ui/history/history_detail.xml
                // /ui/history/deploy_history_detail.xml
                const history_detail = tabWindow.scwin.setBuilderAppDownloadHref;
                if (!!history_detail) {
                    history_detail(received_msg);
                }

                break;
            case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
                message = common.getLabel("lbl_key_setting_complete");
                common.win.alert(message);
                break;
            case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO_FROM_HEADQUATER" :
                message = common.getLabel("lbl_key_setting_complete");
                common.win.alert(message);
                break;
            case "HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO_FROM_HEADQUATER" :
                $p.top().scwin.setStatus(received_msg);
                break;
            case "HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO_FROM_HEADQUATER" :
            case "HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO_FROM_HEADQUATER" :
                // /ui/works/project_setting.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_setting_step01.xml
                settingStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step1");
                if(!!settingStep1Wfrm){
                    if(settingStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep1Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderAllAppConfigStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO_FROM_HEADQUATER" :
                // /ui/build/build.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setMultiProfileListStatus(received_msg);
                break;
            case "HV_MSG_BUILD_SET_ACTIVE_PROFILE_INFO_FROM_HEADQUATER" :
                // /ui/build/build.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setActiveProfileStatus(received_msg);
                break;
            case "HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO_FROM_HEADQUATER" :
                // /ui/build/build.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.setMultiProfileAppConfigDataStatus(received_msg);
                break;
            case "HV_MSG_BUILD_GENERAL_APP_INFO_FROM_HEADQUATER":
                // /ui/build/build.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                tabWindow.scwin.getGeneralIOSAPPInfo(received_msg);
                break;
            case "BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO_FROM_HEADQUATER" :
                message = common.getLabel("lbl_key_setting_complete");
                common.win.alert(message);
                break;
            case "BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO_FROM_HEADQUATER" :
                message = common.getLabel("lbl_signingkey_setting_key_modify");
                common.win.alert(message);
                break;
            case "BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO_FROM_HEADQUATER" :
                message = common.getLabel("lbl_signingkey_setting_key_modify");
                common.win.alert(message);
                break;
            case "HV_MSG_PROJECT_IMPORT_STATUS_INFO_FROM_HEADQUATER" :
                // /ui/works/project_import.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_import_step01.xml
                importStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_import_step1");
                if(!!importStep1Wfrm){
                    if(importStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = importStep1Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderToProjectImportStastus(received_msg);
                    }
                }
                break;
            case "HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO_FROM_HEADQUATER":
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_deploy_task_setting_step1.xml
                settingStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step1");
                if(!!settingStep1Wfrm){
                    if(settingStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep1Wfrm.getWindow();
                        wframeWindow.scwin.getDeployTaskDataSearchStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO_FROM_HEADQUATER":
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_deploy_task_setting_step1.xml
                settingStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step1");
                if(!!settingStep1Wfrm){
                    if(settingStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep1Wfrm.getWindow();
                        wframeWindow.scwin.setDeployTaskDataUpdateStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO_FROM_HEADQUATER":
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_deploy_task_setting_step1.xml
                settingStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step2");
                if(!!settingStep1Wfrm){
                    if(settingStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep1Wfrm.getWindow();
                        wframeWindow.scwin.setDeployMatadataUpdateStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_DEPLOY_METADATA_IMAGE_STATUS_INFO_FROM_HEADQUATER":
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_deploy_task_setting_step1.xml
                settingStep1Wfrm = tabWindow.$p.getComponentById("wfm_project_deploy_task_setting_step3");
                if(!!settingStep1Wfrm){
                    if(settingStep1Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep1Wfrm.getWindow();
                        wframeWindow.scwin.getDeployImageMeatadataStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO_FROM_HEADQUATER" :
                // /ui/works/project_setting.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_setting_step02.xml
                settingStep2Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step2");
                if(!!settingStep2Wfrm){
                    if(settingStep2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep2Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderVCSMultiProfileConfigStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO_FROM_HEADQUATER" :
                // /ui/works/project_setting.xml
                tabIdx = $p.top().tabc_layout.selectedIndex;
                tabWindow = $p.top().tabc_layout.getWindow(tabIdx);
                // /ui/works/project_setting_step02.xml
                settingStep2Wfrm = tabWindow.$p.getComponentById("wfm_project_setting_step2");
                if(!!settingStep2Wfrm){
                    if(settingStep2Wfrm.render.style.getPropertyValue("display") != "none") {
                        wframeWindow = settingStep2Wfrm.getWindow();
                        wframeWindow.scwin.setBuilderTemplateMultiProfileConfigStatus(received_msg);
                    }
                }
                break;
            case "HV_MSG_SEND_FILE_INFO_FROM_BRANCH":
            case "HV_MSG_DISCONNECT":
            case "HV_MSG_DISCONNECT_FROM_BRANCH":
            case "HV_MSG_SEND_FILE_BLOB":
            case "HV_MSG_WINDOWS_CONFIG_LIST_INFO_FROM_HEADQUATER":
            case "HV_MSG_PROJECT_APP_CONFIG_IMAGE_LIST_INFO_FROM_HEADQUATER":
                break;
            default:
                console.log("received_msg:"+JSON.stringify(received_msg));
                break;
        }
    }
};

common.util = {};

common.util.isVisible = () => {
    let hidden = "hidden";
    if (typeof document.hidden !== "undefined") {
        hidden = "hidden";
    } else if (typeof document.mozHidden !== "undefined") {
        hidden = "mozHidden";
    } else if (typeof document.msHidden !== "undefined") {
        hidden = "msHidden";
    } else if (typeof document.webkitHidden !== "undefined") {
        hidden = "webkitHidden";
    }
    return !document[hidden];
};

common.util.removeEventVisibility = function(listner){
    let visibilityChange;
    if (typeof document.hidden !== "undefined") {
        visibilityChange = "visibilitychange";
    } else if (typeof document.mozHidden !== "undefined") {
        visibilityChange = "mozvisibilitychange";
    } else if (typeof document.msHidden !== "undefined") {
        visibilityChange = "msvisibilitychange";
    } else if (typeof document.webkitHidden !== "undefined") {
        visibilityChange = "webkitvisibilitychange";
    } else {
        console.log("visiblitychange not support");
        return;
    }
    document.removeEventListener(visibilityChange,listner);
}

common.util.addEventVisibility = function(listner){
    let hidden, visibilityChange;
    if (typeof document.hidden !== "undefined") {
        hidden = "hidden";
        visibilityChange = "visibilitychange";
    } else if (typeof document.mozHidden !== "undefined") {
        hidden = "mozHidden";
        visibilityChange = "mozvisibilitychange";
    } else if (typeof document.msHidden !== "undefined") {
        hidden = "msHidden";
        visibilityChange = "msvisibilitychange";
    } else if (typeof document.webkitHidden !== "undefined") {
        hidden = "webkitHidden";
        visibilityChange = "webkitvisibilitychange";
    } else {
        console.log("visiblitychange not support");
        return;
    }

    if (!!visibilityChange && !!hidden){
        document.addEventListener(visibilityChange,listner);
    }
};

common.util.endsWith = (string, key) => {
    let len = string.length;
    let keyLen = key.length;

    if (len < keyLen) return false;

    return string.substring(len - keyLen, len) === key;
};

// 변수 empty 여부확인
common.util.isEmpty = function(value) {
    if ((typeof value === "undefined") || (value === null) || (value === "")) {
        return true;
    } else {
        return false;
    }
};

// 스트링 경로에서 파일명 가져오기
common.util.findFileName = function (path) {
    if (typeof path === "string" && path.length > 0) {
        const pathSplit = path.split(/\\|\//);
        const filename = pathSplit[pathSplit.length - 1];

        return filename;
    } else {
        return "";
    }
};

// input type=file 객체 앞에 있는 textbox에 값 filename 전달
common.util.inputFileChange = function (_this) {
    const filepath = _this.value;
    _this.previousElementSibling.innerHTML = common.util.findFileName(filepath);
};

// 호출 URI
common.uri = {};

//GET
/// index.xml
common.uri.userInfo = "/manager/member/search/userInfo";
common.uri.getWorkspaceInfo = "/manager/workspace/search/workspaceListByMemberRole";
common.uri.logout = "/manager/member/logout";

/// workspace.xml
common.uri.getWorkSpaceReSelect = (member_id, domain_id) => { return `/api/workspaces/memberid/${member_id}/${domain_id}`; }
common.uri.buildProjects = "/api/buildprojects";
common.uri.exportDownload = (data) => { return `/manager/build/project/export/download/${data.filename}`; }
common.uri.exportDownloadA3=  '/manager/build/project/export/download';
common.uri.searchWorkspaceID = (data) => { return `/manager/workspace/search/workspaceId/${data.member_id}/${data.workspace_name}`; }
common.uri.checkWorkspaceName = (data) => { return `/api/workspace/namecheck/${workspace_name}`;}
/// project_add_step01.xml
common.uri.builderList = "/manager/builderSetting/selectBySelectBoxList";
common.uri.ftpList = "/manager/ftp/setting/search/selectBoxListAll";
common.uri.programLanguageList = (platform)=>{return `/manager/build/project/search/programLanguageList/${platform}`; };
common.uri.checkProjectName = (name)=>{ return `/manager/build/project/search/checkProjectName/${name}`; };
common.uri.vscList = (vcs_type)=>{ return `/manager/vcs/search/profileTypeAndAdminBySelectBox/${vcs_type}/N`; };
common.uri.platformList = "/manager/build/project/search/platformSelect";

// project_add_step04.xml
common.uri.storeDeployKey = (idx) => { return `/manager/mCert/common/search/storeDeployKey/${idx}`; };
common.uri.storeDeployKeyByPlatform = (platform) => { return `/manager/mCert/common/search/storeDeployKeyByPlatform/${platform}`; };
common.uri.storeDeployKeyType = "/manager/mCert/common/search/storeDeployKeyType";
common.uri.deploySettingCreate = "/manager/deploy/setting/create";

// project_setting.xml
common.uri.settingGetBuildId = (buildID) => { return `/manager/deploy/setting/search/getBuildId/${buildID}`; };
common.uri.settingProjectConfig = (buildID) => { return `/manager/build/project/search/projectConfig/${buildID}`; };
common.uri.settingProjectDetail = (projectID) => { return `/manager/build/setting/search/projectDetail/${projectID}`; };

// project_setting_step1.xml
common.uri.vcsSearchProfileId = (vcs_id) => { return `/manager/vcs/search/profileIdBySelectBox/${vcs_id}`; };
common.uri.ftpSetting = (ftp_id) => { return `/manager/ftp/setting/search/selectBoxList/${ftp_id}`; };
common.uri.androidSearchProflie = (signingkey_id) => { return `/manager/mCert/android/search/profile/${signingkey_id}`; };
common.uri.iosSearchProfile = (key_id) => { return `/manager/mCert/iOS/search/profile/${key_id}`; };
common.uri.getAndoridAllGetconfig = "/manager/build/setting/search/getAndroidAllGetConfig";
common.uri.getiOSGetInformation = "/manager/build/setting/search/getiOSGetInformation";
common.uri.updateProjSetting ="/manager/build/project/update/projectSetting";
common.uri.projectSerachProgramLanguage = (role_code_id)=>{ return `/manager/build/project/search/programLanguage/${role_code_id}`; };
common.uri.branchSetting = (builder_id)=>{ return `/manager/branchSetting/selectBySelectBoxListBranchId/${builder_id}`; };

// project_import_step01.xml
common.uri.importMultiProfileTemplateProject = "/manager/project/import/multiProfileTemplateProject";
common.uri.projectImportVcsMultiProfiles = "/manager/build/project/import/vcsMultiProfiles";

// settings
common.uri.getBuilderList = "/manager/builderSetting/selectBySelectBoxList";
common.uri.getAndroidCert = (key_id) => { return `/manager/mCert/android/search/profile/${key_id}`; };
common.uri.getIosCert = (key_id) => { return `/manager/mCert/iOS/search/profile/${key_id}`; };



//POST
// index.html
common.uri.loginCheck = "/manager/member/loginCheck"
common.uri.contactUs = "/manager/service/contactus"

/// login.xml
common.uri.login = "/manager/member/login";
/// index.xml
common.uri.menuList = "/manager/menu/list";
common.uri.buildProjectInfo = "/manager/build/project/search/projectRoles";
common.uri.getHistoryInfo = "/manager/build/history/getAll";
/// register.xml
common.uri.createUser = "/manager/account/signUp/resultEmail";
common.uri.checkCodeValue = "/manager/account/signUp/checkCodeValue";
common.uri.checkUserEmail = "/manager/account/signUp/readyEmail";
common.uri.checkUserId = "/manager/member/search/checkUserId";
/// user_login_id_send_email.xml
common.uri.checkUserIdSendEmail = "/manager/account/signUp/checkUserId";
/// user_password_send_email.xml
common.uri.resetPassword = "/manager/account/signUp/resetPassword";
/// workspace.xml
common.uri.deleteBuildProject = "/manager/build/project/delete";
common.uri.projectExportStart = "/manager/build/project/export/start";
/// build.xml
common.uri.getInfo = "/manager/general/history/getInfo";
common.uri.allMultiProfileList = "/manager/build/history/allMultiProfileList";
common.uri.buildHistoryCreate = "/manager/build/history/create";
common.uri.generalBuildHistoryCreate = "/manager/general/build/history/create";
common.uri.qrcode = (qrcodeID) => { return g_config.HTTPSERVER_URL + `/builder/build/history/CheckAuthPopup/${qrcodeID}`; }
common.uri.appDownload = "/manager/build/history/startFileDownload";
common.uri.logDonwload = "/manager/build/history/downloadLogFile";
common.uri.appDownloadHref = (data) => { return `/manager/build/history/downloadSetupFile/${data.filename}`; }
common.uri.getMultiProfileAppConfig = "/manager/build/history/getMultiProfileAppConfig";

/// deploy.xml
common.uri.deployHistoryCreate = "/manager/deploy/history/create";
common.uri.getBuildHistoryData = (projet_history_id) => { return `/manager/build/history/${projet_history_id}`; }
common.uri.getProductType = (project_id) => { return `/manager/deploy/project/getProductType/${project_id}`; }

/// project_add_step01.xml
common.uri.signingProfile = "/manager/mCert/common/selectAllByAdmin";
common.uri.templateList = "/manager/build/project/search/templateList";
common.uri.createGeneralProj = "/manager/general/build/project/create";
common.uri.userPayCheck = "/manager/member/payCheck";

// project_add_step02.xml
common.uri.iosProfilesKeyName = "/manager/mCert/common/selectiOSProfilesKeyName";
common.uri.vcsMultiProfile = "/manager/build/project/create/vcsMultiProfiles";
common.uri.createMultiProfileTemplateProject = "/manager/build/project/create/multiProfileTemplateProject";
common.uri.uploadIcon = "/manager/build/setting/upload/icon";

//iOS
common.uri.signingkeyfile = "/api/buildsetting/signingkeyfile";
//Android
common.uri.signingkeyupload = "/api/buildsetting/signingkeyupload";
common.uri.checkLicense = "/manager/build/project/checkLicense";

// proejct_add_step3.xml
common.uri.pluginlist = "/api/buildproject/pluginlist";
common.uri.searchPluginList = "/manager/build/project/search/pluginList"
common.uri.updatePlugin = "/manager/build/project/update/plugin";

// project_setting_step2.xml
common.uri.updateVcsMultiProfiles = "/manager/build/project/update/multiProfileConfig";

// project_setting_step04.xml

common.uri.searchStoreDeployKey = (id) => { return`/manager/mCert/common/search/storeDeployKey/${id}`};

//build history
common.uri.buildHistoryDetailList  = (historyProjectId, historyPlatform, historyProjectName) => {return `/manager/build/history/${historyProjectId}/${historyPlatform}/${historyProjectName}`};
common.uri.buildHistoryLogFileDownload = "/manager/build/history/downloadLogFile";
common.uri.buildHistoryAppFileDownload = "/manager/build/history/startFileDownload";

// project_deploy_setting_step1.xml

common.uri.searchDeploySettingTaskList =  "/manager/deploy/setting/search/task";
common.uri.DeploySettingTaskDataUpdate =  "/manager/deploy/setting/update/task";

//project_deploy_setting_step2..xml
common.uri.deploySettingMetadataUpdate =  "/manager/deploy/setting/update/metadata";

//project_deploy_setting_step3.xml
common.uri.deploySettingMetadataImageList = "/manager/deploy/setting/search/metadata/image";
common.uri.deploySettingMetadataiOSImageUpdate = "/manager/deploy/setting/update/metadata/image";
common.uri.deploySettingMetadataAndroidImageUpdate = "/manager/deploy/setting/update/metadata/android/image";

//deploy history
common.uri.deployHistoryDetailList = "/manager/deploy/history/search/historyList";
common.uri.deployHistoryLogFileDownload = "/manager/deploy/history/download/logFileFromWebSocket";

// settings
common.uri.getBranchSettingAll = "/manager/branchSetting/getAll";
common.uri.getBranchSettingDetailByID = (builder_id) => {return `/manager/branchSetting/selectByBranchId/${builder_id}`}
common.uri.setBranchSettingUpdate = "/manager/branchSetting/update";
common.uri.setBranchSettingCheckName = "/manager/branchSetting/checkName";
common.uri.setBranchSettingCheckUserID = "/manager/branchSetting/checkUserId";
common.uri.setBranchSettingCreate = "/manager/branchSetting/create";
common.uri.getFTPSettingAll = "/manager/ftp/setting/searchAll";
common.uri.getFTPSettingDetailByID = (ftp_id) => {return `/manager/ftp/setting/search/${ftp_id}`}
common.uri.checkFTPName = "/manager/ftp/setting/search/checkName";
common.uri.setFTPSettingCreate = "/manager/ftp/setting/create";
common.uri.setFTPSettingUpdate = "/manager/ftp/setting/update";
common.uri.getKeySettingAll = "/manager/mCert/common/selectAll";
common.uri.getVcsSettingAll = "/manager/vcs/search/profileListAll";
common.uri.getVCSSettingDetailByID = (vcs_id) => {return `/manager/vcs/search/profile/${vcs_id}`};
common.uri.getVCSSettingCheckProfileName = "/manager/vcs/search/checkProfileName";
common.uri.setVCSSettingUpdate = "/manager/vcs/update";
common.uri.setVCSSettingCreate = "/manager/vcs/create";
common.uri.checkProfileName = "/manager/mCert/common/search/checkProfileName";
common.uri.androidCertCreate = "/manager/mCert/android/create";
common.uri.androidCertUpdate = "/manager/mCert/android/update";
common.uri.iosCertCreate = "/manager/mCert/iOS/AllCreate";
common.uri.iosCertUpdate = "/manager/mCert/iOS/AllUpdate";

// managers
common.uri.setUserCreate = "/manager/member/create";
common.uri.setUserUpdate = "/manager/member/update";
common.uri.getUserListAll = "/manager/member/search/userListAll";
common.uri.getUserCheckName = "/manager/member/search/checkUserId";
common.uri.getUserEmailCheck = "/manager/member/search/checkEmail"
common.uri.setUserUpdateAuthToken = "/manager/member/update/userAuthToken"
common.uri.getUserOneDetail = (member_id) => {return `/manager/member/search/userOneDetail/${member_id}`};
common.uri.getUserListDetail = "/manager/member/search/userListDetail";
common.uri.getCodeAllList = (codeType) => {return `/manager/role/search/codeList/${codeType}`};
common.uri.getRoleProfileListAll = "/manager/userRole/search/profileListAll";
common.uri.getDomainList = "/manager/domain/search/domainList";
common.uri.getDomainListAll = "/manager/domain/search/domainListAll";
common.uri.getDomainDetail = (domain_id) => {return `/manager/domain/search/domain/${domain_id}`};
common.uri.checktDomainName = (domain_name) => {return ` /manager/domain/search/checkName/${domain_name}`};
common.uri.setDomainUpdate = "/manager/domain/update";
common.uri.setDomainCreate = "/manager/domain/create";
common.uri.getAdminWorkspaceListInfo = "/manager/workspace/search/workspaceListByMemberId";
common.uri.setWorkspaceCreate = "/manager/workspace/create";
common.uri.setWorkspaceUpdate = "/manager/workspace/update/useYN";
common.uri.setWorkspaceDelete = "/manager/workspace/delete";
common.uri.getUesrRoleListAll = "/manager/userRole/search/profileListAll";
common.uri.setUserRoleUpdate = "/manager/userRole/update";
common.uri.setUserRoleCreate = "/manager/userRole/create";
common.uri.getWorkspaceRoleListAll = "/manager/workspace/search/roleListAll";
common.uri.getUserRoleFindByWorkspaceGroup = (role_id) => {return `/manager/userRole/search/workspaceGroup/${role_id}`};
common.uri.getUserRoleFindByProfileGroupRoleID = (role_id) => {return `/manager/userRole/search/profileGroup/${role_id}`};
common.uri.getUserRoleFindByProjectListinWorkspaceID = (workspace_id) => {return `/manager/workspace/search/projectList/${workspace_id}`};
common.uri.getUserRoleFindByProjectGroupinWorkspacegrpRoleID = (workspace_group_role_id) => {return `/manager/workspace/search/projectGroup/${workspace_group_role_id}`};
common.uri.gettUserRoleFIndByProjectListAll = "/manager/workspace/search/projectListAll";
common.uri.getUserRoleFindBuRoleIDListInProject = (role_id) => {return `/manager/workspace/search/roleIdListInProject/${role_id}`};
common.uri.getUserRoleFindByProjectListWithGroupRole = (workspace_id, workspace_group_role_id) => {return `/manager/workspace/search/projectListWithGroupRole/${workspace_id}/${workspace_group_role_id}`};
common.uri.getUesrRoleFindbyRoleListInWorkspaceGroup = (role_id) => {return `/manager/workspace/search/roleIdListInWorkspaceGroup/${role_id}`};
common.uri.getUserRoleCheckRoleName = "/manager/userRole/search/checkRoleName";
common.uri.resign = "/manager/account/resign/sendEmail";
common.uri.setUserAppIDUpdate = "/manager/member/appID/Update";

// pricing
common.uri.setUserInfoPricingCreate = "/manager/pricing/search/userinfo";
common.uri.setUserInfoPricingCancel = "/manager/pricing/cancel/id";
common.uri.createPricingToken = "/manager/pricing/create/token";

// secssion
common.uri.resignResult = "/manager/account/resign/result";
