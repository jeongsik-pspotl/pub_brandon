/*amd /xml/login.xml 9463 b1ad755e7815b8f17fae94bb1e5d3fa3d33fe28b8b650f69fda1b5c3b5154441 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'meta',A:{name:'viewport',content:'width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'}},{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.onpageload = function () {

				// google recapcha lib 설정
				let headObj = document.getElementsByTagName("head")[0];
				let script = document.createElement('script');
				script.type = 'text/javascript';
				script.src = "https://www.google.com/recaptcha/api.js?render=6LccytwkAAAAALN6G3ljf0SNqxDkTx8l-EOoAmV7";
				headObj.appendChild(script);
				// 다국어설정
				let lang = common.getLocale() ? common.getLocale() : "ko";
				console.log(lang);
				ui_lang.setValue(lang);
				// ui_lang.hide(); // 현재 한국어만 다국어가 가능하기 때문에 임시로 숨겨놓는다.

				// 쿠키 설정 값에 따라 자동 email 값 설정 기능 구현
				let checkbox = $p.getComponentById("select_check_email_yn");
				let globalEmailCheck = WebSquare.cookie.getCookie("global_email_check");
				if (globalEmailCheck == "true") {
					checkbox.getCheckboxList()[0].checked = true;
					scwin.autoSetEmail();
				} else {
					checkbox.getCheckboxList()[0].checked = false;
				}

				// const whive_session = sessionStorage.getItem("accessToken");
				// if (whive_session) {
				// 	if (g_config.PROFILES == "service") {
				// 		$p.url("/index.xml");
				// 	} else {
				// 		$p.url("/index.xml");
				// 	}
				// }
			};

			scwin.saveIdOnchange = function() {
				let checkbox = $p.getComponentById("select_check_email_yn");
				let checked = checkbox.getCheckboxList()[0].checked;
				if (checked) {
					WebSquare.cookie.setCookie("global_email", $p.getComponentById("ipt_userID").getValue());
					WebSquare.cookie.setCookie("global_email_check", "true");
				} else {
					WebSquare.cookie.setCookie("global_email_check", "false");
					WebSquare.cookie.delCookie("global_email");
				}
			};

			scwin.login = async function () {
				const SiteKey = '6LccytwkAAAAALN6G3ljf0SNqxDkTx8l-EOoAmV7';
				grecaptcha.ready (function() {
					grecaptcha.execute(SiteKey, {action: 'submit'}).then(function(token) {

						const user_login_id = ipt_userID.getValue();
						const password = ipt_userPW.getValue();

						if(common.isEmptyStr(user_login_id) || common.isEmptyStr(password)) {
							const message = common.getLabel("lbl_check_id_password");
							common.win.alert(message);
                            return false;
						}

						let param = {};
						param.user_login_id = user_login_id;
						param.password = password;
						param.token = token;

						const url = common.uri.login;
						const method = "POST";
						const headers = {"Content-Type": "application/json; charset=utf-8"};
						const body = param;

						common.http.fetch(url, method, headers, body)
								.then(res => {
									sessionStorage.setItem("accessToken", res[0].accessToken);

									if (Array.isArray(res) && res[0].recaptcha_yn) {
										$p.url("/index.xml");
									} else {
										let message = common.getLabel("lbl_login_violation");
										common.win.alert(message);
									}
								})
								.catch(() => {
									let message = common.getLabel("lbl_check_id_password");
									common.win.alert(message);
								});

					});
				});

				scwin.saveIdOnchange();
			};

			scwin.autoSetEmail = function () {
				var email = WebSquare.cookie.getCookie("global_email");
				ipt_userID.setValue(email);
			};

			scwin.btn_login_onclick = function (e) {
				scwin.login();
			};

			scwin.btn_login_onkeypress = function (e) {
                // enterkey == 13
				if (e.keyCode === 13) {
					scwin.login();
				}
			};

			scwin.ui_lang_onviewchange = function () {
				common.setLocale(ui_lang.getValue());
			};

			scwin.btn_register_onclick = function () {
				if (g_config.PROFILES == "service") {
					$p.url("/ui/comn/register.xml");
				} else {
					$p.url("/ui/comn/register.xml");
				}
			};

			scwin.btn_password_reset_onclick = function () {
				if (g_config.PROFILES == "service") {
					$p.url("/ui/comn/user_password_send_email.xml");
				} else {
					$p.url("/ui/comn/user_password_send_email.xml");
				}
			};

			scwin.btn_find_id_view_onclick = function () {
				if (g_config.PROFILES == "service") {
					$p.url("/ui/comn/user_login_id_send_email.xml");
				} else {
					$p.url("/ui/comn/user_login_id_send_email.xml");
				}
			};
			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_main_wrap whive',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'ui_lang',ref:'',style:'position: absolute;right: 40px;top:40px;z-index: 100;width: 100px;margin-left:10px;margin:0 0 0 auto;font-size:12px;',submenuSize:'auto','ev:onviewchange':'scwin.ui_lang_onviewchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Korean'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'ko'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Japanese'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'ja'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Chinese'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'ch'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'English'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'en'}]}]}]}]},{T:1,N:'xf:group',A:{class:'deco1',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'ani1',id:'',style:''}},{T:1,N:'xf:group',A:{class:'ani2',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_container',adaptive:'layout',adaptiveThreshold:'1024'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_contents',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'login_header',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'logo',id:'',style:'',tabIndex:'',tagname:'h1'},E:[{T:1,N:'w2:anchor',A:{id:'',localeRef:'CMMN_LOGIN_LABEL_00002',outerDiv:'false',style:'',tabIndex:'',toolTip:'',userData2:'',userData3:''},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_info'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_item user'},E:[{T:1,N:'w2:textbox',A:{label:'Your email',style:'',id:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_id'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'ipt_userID',style:'',class:'cont_user',customModelFormatter:'',type:'',useMonthYearFormat:'',useVerticalAlign:'',dataType:'',displayFormatter:'',disabled:'',applyFormat:'',autocomplete:'',autoFocus:'',customModelUnformatter:''}},{T:1,N:'xf:trigger',A:{type:'anchor',style:'',id:'',disabled:'',class:'btn_login_close hidden',centerOffImageClass:'',anchorWithGroupClass:'',rightOffImageClass:'',leftOnImageClass:''},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'login_item pwd',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'Password',style:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_password'}},{T:1,N:'xf:secret',A:{id:'ipt_userPW',style:'',class:'cont_pwd',ref:'',nextTabID:'','ev:onkeypress':'scwin.btn_login_onkeypress'}},{T:1,N:'xf:trigger',A:{anchorWithGroupClass:'',centerOffImageClass:'',class:'btn_login_pw',disabled:'',id:'',leftOnImageClass:'',rightOffImageClass:'',style:'',type:'button','ev:onkeypress':'scwin.btn_login_onkeypress'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',class:'btnbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'xf:select',A:{selectedindex:'-1',id:'select_check_email_yn',appearance:'full',style:'',cols:'',rows:'',ref:'',renderType:'checkboxgroup',class:'btn_id_chk',useLocale:'true',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_login_save_id'}]},{T:1,N:'xf:value'}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'rt'},E:[{T:1,N:'w2:anchor',A:{class:'',href:'',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_find_id','ev:onclick':'scwin.btn_find_id_view_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',href:'',id:'',class:'',useLocale:'true',localeRef:'lbl_change_password','ev:onclick':'scwin.btn_password_reset_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'',class:'btn_login',href:'',useLocale:'true',localeRef:'lbl_login','ev:onclick':'scwin.btn_login_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{id:'',class:'joinbox'},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',style:'',tagname:'span',tooltipDisplay:'',useLocale:'',localeRef:''}},{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',href:'',id:'',class:'',useLocale:'true',localeRef:'lbl_login_join','ev:onclick':'scwin.btn_register_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'deco2',id:'',style:''}}]}]}]}]}]}]})