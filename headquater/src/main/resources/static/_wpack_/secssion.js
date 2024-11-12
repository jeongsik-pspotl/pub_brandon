/*amd /cm/ui/comn/secssion.xml 3957 ac314c769d98b67fe19edcf12386b7ce2484cadf639f25ea1f1d3a8e15c70668 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function() {
							scwin.check_user_emailYN = false;
							scwin.oParams =  scwin.getUrlParams();

							secssion_label_id.setLabel("W-Hive 탈퇴 이후에는 " + oParams.email + " 계정 아이디로 더 이상 해당 서비스를 진행하실 수 없습니다.");
						};

						scwin.btn_secssion_user_onclick = function() {
							let data = {};
							data.email = scwin.oParams.email;

							scwin.secssionUser(data);
						};

						scwin.btn_back_to_login_onclick = function() {
							$p.url("/login.xml");
						};

						scwin.secssionUser = function(data){
							const url = common.uri.resignResult;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if(Array.isArray(res)) {
										if(res[0].secssionResult === "success") {
											const message = common.getLabel("lbl_secssion_success");
											common.win.alert(message);
											$p.url("/manager/member/logout");
										} else {
											const message = common.getLabel("lbl_secssion_fail");
											common.win.alert(message);
										}
									}
								})
								.catch(err => {
									common.win.alert("code:" + err.responseStatusCode + "\nmessage:" + err.responseText + "\nerror:" + err.requestBody);
								});
						};

						scwin.getUrlParams = function () {
							let params = {};
							window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(str, key, value) {
								params[key] = value;
							});
							return params;
						};

						scwin.deleteCookie = function(name) {
							document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
						};


					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_main_wrap whive',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'deco1',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'ani1',id:'',style:''}},{T:1,N:'xf:group',A:{class:'ani2',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_container register delete ',adaptive:'layout',adaptiveThreshold:'1024'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_contents ',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'login_header',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'logo',id:'',style:'',tabIndex:'',tagname:'h1'},E:[{T:1,N:'w2:anchor',A:{id:'',localeRef:'CMMN_LOGIN_LABEL_00002',outerDiv:'false',style:'',tabIndex:'',toolTip:''},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:textbox',A:{label:'',style:'background:none;color:#000;border:0;border-radius:0;',tagname:'span',useLocale:'true',localeRef:'lbl_secssion_title'}}]}]},{T:1,N:'xf:group',A:{class:'login_info'},E:[{T:1,N:'xf:group',A:{class:'login_item',style:'text-align: center;'},E:[{T:1,N:'w2:textbox',A:{id:'secssion_label_id',label:'',tagname:'span',escape:'false'}},{T:1,N:'w2:textbox',A:{escape:'false',label:'',tagname:'p',class:'subtxt',useLocale:'true',localeRef:'lbl_secssion_message2'}}]},{T:1,N:'xf:group',A:{id:'',class:'btnbox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',class:'btn_login btn_cancel',useLocale:'true',localeRef:'lbl_cancel','ev:onclick':'scwin.btn_back_to_login_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',class:'btn_login',useLocale:'true',localeRef:'lbl_secssion_secssion','ev:onclick':'scwin.btn_secssion_user_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]})