/*amd /ui/comn/register.xml 14397 37e5a99b3fb94cca047c646d617cf22c0bec1a22f162b3f54661744f0882726c */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="../../cm/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="../../cm/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'meta',A:{name:'viewport',content:'width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'}},{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
                        scwin.check_user_email = false;
                        scwin.check_user_id = false;

                        scwin.onpageload = function () {
                            // 이메일 인증 코드 부분 hide
                            user_code_auth.hide();
                            user_code_auth_warn.hide();
                        };

                        scwin.btn_create_user_onclick = function () {
                            let data = {};
                            data.user_login_id = register_user_id.getValue();
                            data.email = register_user_email_id.getValue() + "@" + register_user_email_domain.getValue();
                            data.user_name = register_user_name.getValue();
                            data.password = register_user_password.getValue();
                            data.confirmPassword = register_user_password_check.getValue();
                            data.domain_id = 1;

                            if (scwin.checkData(data)) {
                                scwin.createUser(data);
                            }
                        };

                        scwin.checkData = function (data) {
                            // 이메일, 이름, password, confirm password
                            const user_login_id = data.user_login_id;
                            const user_email = data.email;
                            const user_password = data.password
                            const user_confirm_password = data.confirmPassword;
                            const member_name = data.user_name

                            if (common.isEmptyStr(user_login_id)) {
                                const message = common.getLabel("lbl_check_blank_id");
                                common.win.alert(message);
                                scwin.check_user_id = false;
                                return false;
                            }

                            if (!scwin.check_user_id) {
                                const message = common.getLabel("lbl_register_duplicateId");
                                common.win.alert(message);
                                return false;
                            }

                            if (common.isEmptyStr(user_email)) {
                                const message = common.getLabel("lbl_register_blankEmail");
                                common.win.alert(message);
                                scwin.check_user_email = false;
                                return false;
                            }

                            if (!scwin.check_user_email) {
                                const message = common.getLabel("lbl_auth_email");
                                common.win.alert(message);
                                return false;
                            }

                            if (common.isEmptyStr(user_password)) {
                                const message = common.getLabel("lbl_register_inputPwd");
                                common.win.alert(message);
                                return false;
                            }

                            if (!common.checkAllInputText("CHECK_INPUT_TYPE_PW", user_password)) {
                                const message = common.getLabel("lbl_register_errorPwd");
                                common.win.alert(message);
                                return false;
                            }

                            if (common.isEmptyStr(user_confirm_password)) {
                                const message = common.getLabel("lbl_register_checkPwd");
                                common.win.alert(message);
                                return false;
                            }

                            if (!common.checkAllInputText("CHECK_INPUT_TYPE_PW", user_confirm_password)) {
                                const message = common.getLabel("lbl_register_errorPwd");
                                common.win.alert(message);
                                return false;
                            }

                            if (user_password != user_confirm_password) {
                                const message = common.getLabel("lbl_register_samePwd");
                                common.win.alert(message);
                                return false;
                            }

                            if (common.isEmptyStr(member_name)) {
                                const message = common.getLabel("lbl_input_username");
                                common.win.alert(message);
                                return false;
                            }

                            // 한글, 영문, 숫자 체크
                            if (!common.checkAllInputText("CHECK_INPUT_TYPE_KOR_ENG_NUM", member_name)) {
                                const message = common.getLabel("lbl_register_checkId");
                                common.win.alert(message);
                                return false;
                            }

                            // 글자 수 체크
                            if (common.calBytes(member_name) > 30) {
                                const message = common.getLabel("lbl_register_restrictId");
                                common.win.alert(message);
                                return false;
                            }

                            return true;
                        };

                        scwin.createUser = function (data) {
                            const url = common.uri.createUser;
                            const method = "POST";
                            const headers = {"Content-Type": "application/json"};

                            common.http.fetch(url, method, headers, data)
                                .then(async (res) => {
                                    const message = common.getLabel("lbl_register_createdAccount");
                                    if (await common.win.alert(message)) { //계정 생성이 완료되었습니다
                                        $p.url("/login.xml");
                                    }
                                })
                                .catch(err => {
                                    scwin.check_user_email = true;
                                    common.win.alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText + "\n" + "error:" + err.requestBody);
                                });
                        };

                        scwin.checkCodeValue = function () {
                            let data = {};
                            data.email = register_user_email_id.getValue() + "@" + register_user_email_domain.getValue();
                            data.keyCode = register_user_code.getValue();

                            const url = common.uri.checkCodeValue;
                            const method = "POST";
                            const headers = {"Content-Type": "application/json"};

                            common.http.fetch(url, method, headers, data)
                                .then(res => {
                                    if (Array.isArray(res)) {
                                        let message;
                                        switch (res[0].checkResult) {
                                            case "success":
                                                message = common.getLabel("lbl_register_authSuccess");
                                                scwin.check_user_email = true;
                                                break;
                                            default:
                                                message = common.getLabel("lbl_register_authFail");
                                                scwin.check_user_email = false;
                                                break;
                                        }
                                        common.win.alert(message);
                                    }
                                });
                        };

                        scwin.step1_btn_check_email_onclick = function () {
                            const email_id = register_user_email_id.getValue();
                            const email_domain = register_user_email_domain.getValue();

                            if (common.isEmptyStr(email_id) || common.isEmptyStr(email_domain)) {
                                const message = common.getLabel("lbl_register_blankEmail");
                                common.win.alert(message);
                                return false;
                            }

                            if (!common.checkAllInputText("CHECK_INPUT_TYPE_EMAIL_ID", email_id)) {
                                const message = common.getLabel("lbl_register_checkEmail");
                                common.win.alert(message);
                                return false;
                            }

                            if (!common.checkAllInputText("CHECK_INPUT_TYPE_EMAIL_DOMAIN", email_domain)) {
                                const message = common.getLabel("lbl_register_checkEmail");
                                common.win.alert(message);
                                return false;
                            }

                            scwin.select_check_user_email(email_id + "@" + email_domain);
                        };

                        scwin.select_check_user_email = function (user_email) {
                            let data = {};
                            data.email = user_email;

                            const url = common.uri.checkUserEmail;
                            const method = "POST";
                            const headers = {"Content-Type": "application/json"}

                            common.http.fetch(url, method, headers, data)
                                .then(res => {
                                    if (Array.isArray(res)) {
                                        let message;
                                        const chk = res[0].user_email_not_found;
                                        if (chk == "yes") {
                                            message = common.getLabel("lbl_register_authCode");
                                            user_code_auth.show();
                                            user_code_auth.setStyle("display", "");
                                            scwin.check_user_email = true;
                                        } else {
                                            message = common.getLabel("lbl_exist_email");
                                            scwin.check_user_email = false;
                                        }
                                        common.win.alert(message);
                                    }
                                })
                                .catch(err => {
                                    common.win.alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText + "\n" + "error:" + err.requestBody);
                                });
                        };

                        scwin.step1_btn_check_user_id_onclick = function () {
                            const user_id = register_user_id.getValue();

                            if (common.isEmptyStr(user_id)) {
                                const message = common.getLabel("lbl_check_blank_id");
                                common.win.alert(message);
                                return false;
                            }

                            scwin.checkUserId();

                        };

                        scwin.checkUserId = function () {
                            let data = {};
                            data.user_login_id = register_user_id.getValue();

                            const url = common.uri.checkUserId;
                            const method = "POST";
                            const headers = {"Content-Type": "application/json"};

                            common.http.fetch(url, method, headers, data)
                                .then(res => {
                                    if (Array.isArray(res)) {
                                        let message;
                                        let chk = res[0].user_name_not_found;
                                        if (chk == "yes") {
                                            message = common.getLabel("lbl_available_id");
                                            scwin.check_user_id = true;
                                        } else {
                                            message = common.getLabel("lbl_exist_id");
                                            scwin.check_user_id = false;
                                        }
                                        common.win.alert(message);
                                    }
                                })
                                .catch(err => {
                                    common.win.alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText + "\n");
                                });
                        };

                        scwin.btn_back_to_login_onclick = function() {
                            $p.url("/login.xml");
                        }

                    }}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_main_wrap whive',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'deco1',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'ani1',id:'',style:''}},{T:1,N:'xf:group',A:{class:'ani2',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_container register',adaptive:'layout',adaptiveThreshold:'1024'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_contents ',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'login_header',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'logo',id:'',style:'',tabIndex:'',tagname:'h1'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',tabIndex:'',toolTip:'',userData2:'',userData3:''},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',useLocale:'true',localeRef:'lbl_register_join'}}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_info'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_item user'},E:[{T:1,N:'w2:textbox',A:{label:'ID',style:'',id:'',tagname:'span'}},{T:1,N:'xf:group',A:{id:'',class:'flex',style:'width: 100%;'},E:[{T:1,N:'xf:group',A:{id:'',style:'position: relative;flex:1;margin-right: 5px;'},E:[{T:1,N:'xf:input',A:{id:'register_user_id',class:'cont_user'}},{T:1,N:'xf:trigger',A:{type:'anchor',class:'btn_login_close hidden'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',id:'btn_manager_user_id_check',class:'btn_cm pt',useLocale:'true',localeRef:'lbl_register_duplicate','ev:onclick':'scwin.step1_btn_check_user_id_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'register_user_id_warn',label:'출력텍스트입니다.',tagname:'span',style:'display: none;'}},{T:1,N:'xf:group',A:{class:'login_item user',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',useLocale:'true',localeRef:'lbl_email'}},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'register_user_email_id',placeholder:'',style:'width:100%;'}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'@',style:'',tagname:'span'}},{T:1,N:'xf:input',A:{class:'',id:'register_user_email_domain',placeholder:'',style:'width:100%;'}},{T:1,N:'w2:anchor',A:{class:'btn_cm pt',id:'btn_manager_user_email_check',outerDiv:'false',useLocale:'true',localeRef:'lbl_register_auth','ev:onclick':'scwin.step1_btn_check_email_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'register_user_email_warn',label:'출력텍스트입니다.',style:'display:none;',tagname:'span'}},{T:1,N:'xf:group',A:{style:'',id:'user_code_auth',class:'login_item user'},E:[{T:1,N:'w2:textbox',A:{label:'',style:'',id:'',tagname:'span',useLocale:'true',localeRef:'lbl_register_authNum'}},{T:1,N:'xf:group',A:{id:'',class:'flex',style:'width: 100%;'},E:[{T:1,N:'xf:group',A:{id:'',style:'position: relative;flex:1;margin-right: 5px;'},E:[{T:1,N:'xf:input',A:{id:'register_user_code',class:'cont_user'}},{T:1,N:'xf:trigger',A:{type:'anchor',class:'btn_login_close hidden'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',id:'btn_register_user_code_check',class:'btn_cm pt',useLocale:'true',localeRef:'lbl_confirm','ev:onclick':'scwin.checkCodeValue'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'login_item user',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',useLocale:'true',localeRef:'lbl_name'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',class:'cont_user',customModelFormatter:'',id:'register_user_name',style:''}},{T:1,N:'xf:trigger',A:{class:'btn_login_close hidden',type:'anchor'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'register_user_name_warn',label:'출력텍스트입니다.',style:'display:none;',tagname:'span'}},{T:1,N:'xf:group',A:{class:'login_item pwd',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_password'}},{T:1,N:'xf:secret',A:{id:'register_user_password',style:'',class:'cont_pwd',placeholder:'비밀번호 입력(문자,숫자,특수문자 포함 8~20자)'}},{T:1,N:'xf:trigger',A:{class:'btn_login_pw',type:'button'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'register_user_password_warn',label:'출력텍스트입니다.',style:'display:none;',tagname:'span'}},{T:1,N:'xf:group',A:{class:'login_item pwd',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_password_check'}},{T:1,N:'xf:secret',A:{class:'cont_pwd',id:'register_user_password_check',nextTabID:'',ref:'',style:'',placeholder:'비밀번호 재입력'}},{T:1,N:'xf:trigger',A:{class:'btn_login_pw',type:'button'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'register_user_password_check_warn',label:'출력텍스트입니다.',style:'display:none;',tagname:'span'}},{T:1,N:'xf:group',A:{id:'',class:'btnbox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'',href:'',class:'btn_login btn_cancel',useLocale:'true',localeRef:'lbl_cancel','ev:onclick':'scwin.btn_back_to_login_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',href:'',id:'',class:'btn_login',useLocale:'true',localeRef:'lbl_register_join','ev:onclick':'scwin.btn_create_user_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]})