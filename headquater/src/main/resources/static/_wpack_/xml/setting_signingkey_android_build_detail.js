/*amd /xml/setting_signingkey_android_build_detail.xml 15967 5e8d89607d32b35801a9a220c99361550f75101c668df0714d7f81417687a263 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.checkProvisionFileYn = false;
			scwin.checkSigningkeyNameYn = false;
			scwin.signingkeyLastCnt = "";
			scwin.onpageload = function() {
				// 화면 구성 변경
				var key_setting_mode = localStorage.getItem("_key_setting_mode_");
				var key_platform = localStorage.getItem("_platform_");

				if (key_setting_mode == "detailview"){
					var label = common.getLabel("lbl_key_setting_detail_view");
					ftp_setting_title.setLabel(label);

					if(key_platform == "Android"){
						scwin.signingKeyAndroidDetailView();
					}else {
						scwin.signingKeyiOSDetailView();
					}

				}else {
					var label = common.getLabel("lbl_key_setting_create");
				    ftp_setting_title.setLabel(label);

					scwin.select_builder_list();

				}


			};

			scwin.onpageunload = function() {
				localStorage.removeItem("_platform_");
			};

			// signingKey Setting 조회
			scwin.signingKeyAndroidDetailView = function(){

				var key_id = localStorage.getItem("_key_id_");

				var options = {};

				options.action = "/manager/mCert/android/search/profile/" + parseInt(key_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						// platform 기준으로 변경해서 처리 해야함..
						if(data.platform == "Android"){
							signingkey_android_key_id.setValue(key_id);
							setting_android_key_password.setValue(data.android_key_password);
							setting_android_key_alias.setValue(data.android_key_alias);
							setting_android_store_password.setValue(data.android_key_store_password);

							$("#wfm_main_setting_signgingkey_android_tap_contents_tab_Build_body_signingkey_setting_android_input_file_path_tmp_inputFile").val(data.android_key_path);

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};


			// signingKey Setting 조회
			scwin.signingKeyiOSDetailView = function(){

				var key_id = localStorage.getItem("_key_id_");

				var options = {};

				options.action = "/manager/mCert/iOS/search/profile/" + parseInt(key_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			scwin.getDomainByIDView = function(){


			};

			scwin.getAdminByIDView = function(){


			};

			// signingkey name 중복 조회 ajax
			scwin.select_check_key_name = function(key_name){
				var data = {};
				data.key_name = key_name;
				var options = {};

				options.action = "/manager/mCert/common/search/checkProfileName";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
						if (data != null) {

						    if(data[0].keyNameCheck == "no"){
								var message = common.getLabel("lbl_exist_key_cert_name");
								alert(message);
								scwin.checkSigningkeyNameYn = false;

							}else {
								var message = common.getLabel("lbl_can_use_key_cert_name");
								alert(message);
								scwin.checkSigningkeyNameYn = true;

							}

						}
					}

				};

				options.error = function (e) {
					if(e.responseStatusCode === 500){
						var message = common.getLabel("lbl_can_use_key_cert_name");
						alert(message);
						scwin.checkSigningkeyNameYn = true;

					}else {

					}

				};

				$p.ajax( options );

			};

			scwin.select_builder_list = function(){

				var options = {};

				options.action = "/manager/builderSetting/selectBySelectBoxList";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
                        var message = common.getLabel("lbl_select");
						// setting_builder_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			// domain 리스트 전체 조회
			scwin.select_domain_list = function(){

				var options = {};

				options.action = "/manager/domain/search/domainList";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
                        var message = common.getLabel("lbl_select");
						setting_domain_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			scwin.select_admin_list = function(domainID){

				var options = {};

				options.action = "/manager/member/search/userInfoForSelectBox/" + parseInt(domainID);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
						setting_admin_list.removeAll(false);

                        var message = common.getLabel("lbl_select");
						setting_admin_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

							setting_admin_list.addItem(data[row].user_id, data[row].user_name);

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			// websocket 으로 결과 받아서 message -> ajax 로 update 할 값을 전송한다.
			scwin.webSocketCallback = function(obj) {
				// msg type 추가
				//console.log(" key file create websocket ");
				switch (obj.MsgType) {
					case "BIN_FILE_PROFILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
						// scwin.setBuilderKeyFileCreateStatus(obj);
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

						break;
					case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
						// scwin.setBuilderiOSKeyFileCreateStatus(obj);
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					    break;
					case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO_FROM_HEADQUATER" :
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");
						// scwin.setBuilderiOSDeployKeyFileCreateStatus(obj);
						break;
					default :
						break;
				}
			};

			// android/ios 분리해서 처리하기
			scwin.setBuilderKeyFileCreateStatus = function(msg){

				var data = {};
				data.signingkey_id = scwin.signingkeyLastCnt;
				data.signingkey_path = msg.filePath;

				var options = {};
				options.action = "/api/signingkeysetting/updatekeyfile";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "PUT";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {
						// alert("Signing Key Setting 생성 완료");
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						// alert("Signing Key Setting 생성 실패");
					}
				};

				options.error = function (e) {
					alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n");

				};

				$p.ajax( options );


			};

			scwin.setBuilderiOSKeyFileCreateStatus = function(msg){

				var data = {};
				data.key_id = scwin.signingkeyLastCnt;
				data.ios_key_path = msg.keyfilePath;
				data.ios_debug_profile_path = msg.debugProfilePath;
				data.ios_release_profile_path = msg.releaseProfilePath;


				var options = {};
				options.action = "/api/keysetting/ios/updatekeyfile";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "PUT";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {
						// alert("Signing Key Setting 생성 완료");
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						// alert("Signing Key Setting 생성 실패");
					}
				};

				options.error = function (e) {
					alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n");

				};

				$p.ajax( options );


			};

			scwin.setBuilderiOSDeployKeyFileCreateStatus = function(msg){

				var data = {};
				data.key_id = scwin.signingkeyLastCnt;
				data.ios_key_path = msg.keyfilePath;


				var options = {};
				options.action = "/api/keysetting/ios/deploy/updatekeyfile";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "PUT";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {
						// alert("Signing Key Setting 생성 완료");
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						// alert("Signing Key Setting 생성 실패");
					}
				};

				options.error = function (e) {
					alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n");

				};

				$p.ajax( options );


			};

			scwin.setting_select_domain_onchange = function(e){

			    var domainID = setting_domain_list.getValue();

			    if(domainID == ""){
					setting_admin_list.removeAll(false);

                    var message = common.getLabel("lbl_select");
					setting_admin_list.addItem("",message); //선택

				}else {
					scwin.select_admin_list(domainID);
				}

			};

            function changeToolTipContentSigningkeyDetail (componentId) {
				let signingkeyType = setting_ios_select_signingkey_type.getValue();
				switch (signingkeyType) {
					case "build":
                        return "애플 개발자 인증서를 PKCS#12 형식으로 변환한 파일"
					case "deploy":
                        return "Apple에서 제공하는 Rest API 서비스 사용시 사용자 인증을 위한 키"
					default:
                        return ""
				}
			};

			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'ftp_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_key_cert_regist_setting'}}]}]},{T:1,N:'xf:group',A:{class:'gal_body type2 mt30',id:'gal_body_ios',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'android_key_type_build_1',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'setting_android_key_alias_text',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_android_build_detail_key'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_android_build_detail_descript'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'setting_android_key_alias',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'android_key_type_build_2',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'setting_android_key_password_text',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_android_build_detail_password'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_android_build_detail_keyUsePw'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'setting_android_key_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'android_key_type_build_3',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'setting_android_store_password_text',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_android_build_detail_storePw'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_android_build_detail_securityPw'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'setting_android_store_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false',useLocale:'true',localeRef:''}}]}]},{T:1,N:'xf:group',A:{id:'android_key_type_all_4',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'signingkey_setting_android_input_file_path_text',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_android_build_detail_storeFile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_android_build_detail_tooltip_storeFile'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'signingkey_setting_android_input_file_path_tmp',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}}]}]},{T:1,N:'xf:input',A:{id:'signingkey_android_key_id',style:'visibility: hidden'}}]}]}]}]}]}]}]})