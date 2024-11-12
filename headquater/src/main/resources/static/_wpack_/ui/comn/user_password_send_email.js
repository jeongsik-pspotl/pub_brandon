/*amd /ui/comn/user_password_send_email.xml 5187 d0db0e59f6a546c38c73ea5b487e0ce0c2f06a2ebc1b6048643cf964ef8b8e6c */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'meta',A:{name:'viewport',content:'width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'}},{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
                        scwin.btn_back_to_login_onclick = function() {
                            $p.url("/login.xml");
                        };

                        scwin.btn_check_id_email_onclick = function() {
                            const email = register_email.getValue();
                            const user_login_id = register_user_id.getValue();

                            if (common.isEmptyStr(user_login_id)) {
                                const message = common.getLabel("lbl_check_blank_id");
                                common.win.alert(message);
                                return false;
                            }

                            if (common.isEmptyStr(email)) {
                                const message = common.getLabel("lbl_email_whitespace");
                                common.win.alert(message);
                                return false;
                            }

                            if (!common.checkAllInputText("CHECK_INPUT_TYPE_EMAIL", email)) {
                                const message = common.getLabel("lbl_register_checkEmail");
                                common.win.alert(message);
                                return false;
                            }
                            scwin.select_user_password_reset_email(email, user_login_id);
                        };

                        scwin.select_user_password_reset_email = function(user_email, user_login_id) {
                            let data = {};
                            data.user_login_id = user_login_id;
                            data.email = user_email;

                            const url = common.uri.resetPassword;
                            const method = "POST";
                            const headers = {"Content-Type": "application/json"};

                            common.http.fetch(url, method, headers, data)
                                .then(res => {
                                    if (Array.isArray(res)) {
                                        let message;
                                        switch (res[0].checkPassword) {
                                            case "success_email":
                                                message = common.getLabel("lbl_user_password_send_email_temporary");
                                                break;
                                            case "fail_id":
                                                message = common.getLabel("lbl_user_password_send_email_notExist_id");
                                                break;
                                            case "fail_email":
                                                message = common.getLabel("lbl_not_exist_whive_email");
                                                break;
                                        }
                                        common.win.alert(message);
                                    }
                                })
                                .catch(err => {
                                    common.win.alert("message:" + err.responseText);
                                });
                        };

                    }}}]}]},{T:1,N:'body',E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_main_wrap whive',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'deco1',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'ani1',id:'',style:''}},{T:1,N:'xf:group',A:{class:'ani2',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_container register',adaptive:'layout',adaptiveThreshold:'1024'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_contents ',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'login_header',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'logo',id:'',style:'',tabIndex:'',tagname:'h1'},E:[{T:1,N:'w2:anchor',A:{id:'',localeRef:'',outerDiv:'false'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',useLocale:'true',localeRef:'lbl_change_password'}}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_info'},E:[{T:1,N:'xf:group',A:{class:'login_item user',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',useLocale:'true',localeRef:'lbl_id'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',class:'cont_user',id:'register_user_id'}},{T:1,N:'xf:trigger',A:{class:'btn_login_close hidden',id:'',type:'anchor'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'',label:'출력텍스트입니다.',style:'display: none;',tagname:'span'}},{T:1,N:'xf:group',A:{class:'login_item user',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_email'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',class:'cont_user',id:'register_email'}},{T:1,N:'xf:trigger',A:{class:'btn_login_close hidden',type:'anchor'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:textbox',A:{class:'warning_txt',id:'',label:'출력텍스트입니다.',style:'display: none;',tagname:'span'}},{T:1,N:'xf:group',A:{id:'',class:'btnbox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',class:'btn_login',useLocale:'true',localeRef:'lbl_do_send','ev:onclick':'scwin.btn_check_id_email_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',class:'btn_login btn_cancel',useLocale:'true',localeRef:'lbl_cancel','ev:onclick':'scwin.btn_back_to_login_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]})