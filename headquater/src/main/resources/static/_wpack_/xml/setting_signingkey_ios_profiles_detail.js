/*amd /xml/setting_signingkey_ios_profiles_detail.xml 34109 b9b40b1e76f448ee2464c31dea5a3a981a00a74bbc63d9dfacaf254257a5cd3a */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.checkProvisionFileYn = false;
			scwin.checkSigningkeyNameYn = false;
			scwin.signingkeyLastCnt = "";
			scwin.profileinfo_itemIdx = 0;

			scwin.onpageload = function () {
				// common.setScopeObj(scwin);

				gal_body_ios.show();
				var key_setting_mode = localStorage.getItem("_key_setting_mode_");
				var key_platform = localStorage.getItem("_platform_");

				if (key_setting_mode == "detailview") {
					if (key_platform == "iOS") {
						scwin.signingKeyiOSDetailView();
					}

				}


			};

			scwin.onpageunload = function () {
				localStorage.removeItem("_platform_");
			};

			// signingKey Setting 조회
			scwin.signingKeyAndroidDetailView = function () {

				var key_id = localStorage.getItem("_key_id_");

				var options = {};

				options.action = "/manager/mCert/android/search/profile/" + parseInt(key_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						var temp = {};

						signingkey_setting_name.setValue(data.key_name);
						setting_select_platform.setText(data.platform, false);
						setting_builder_list.addItem(data.builder_id, data.builder_name, false);
						setting_admin_list.addItem(data.admin_id, data.admin_name, false);
						setting_domain_list.addItem(data.domain_id, data.domain_name, false);

						// platform 기준으로 변경해서 처리 해야함..
						if (data.platform == "Android") {
							setting_android_key_password.setValue(data.android_key_password);
							setting_android_key_alias.setValue(data.android_key_alias);
							setting_android_store_password.setValue(data.android_key_store_password);

							setting_select_android_signingkey_type.setText(data.android_key_type, false);

							$("#wfm_main_signingkey_setting_android_input_file_path_tmp_inputFile").val(data.android_key_path);


						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};


			// signingKey Setting 조회
			scwin.signingKeyiOSDetailView = function () {

				var key_id = localStorage.getItem("_key_id_");

				var options = {};

				options.action = "/manager/mCert/iOS/search/profile/" + parseInt(key_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						// platform 기준으로 변경해서 처리 해야함..
						if (data.platform == "iOS") {


							// test data check...
							signingkey_main_ios_key_id.setValue(key_id);
							var ios_profiles_array = JSON.parse(data.ios_profiles_json);

							for (var i = 0; i < ios_profiles_array.length; i++) {
								var profilesPath = ios_profiles_array[i].profiles_path;
								var profilesBuildType = ios_profiles_array[i].profiles_build_type;
								var profilesKeyName = ios_profiles_array[i].profiles_key_name;

								if (i == 0) {
									setting_ios_profile_name_input.setValue(profilesKeyName);
									setting_ios_select_release_type.setValue(profilesBuildType);
									$("#mf_wfm_main_setting_signgingkey_tap_contents_tab_Profiles_body_signingkey_setting_ios_release_provisioning_input_file_path_inputFile").val(profilesPath);


								} else {
									var paramData = $p.getParameter("tabParam");

									// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
									var group_ul1 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_ul_" + scwin.profileinfo_itemIdx, "group", {tagname: "ul"}, group_ios_profile_info_main);

									var group_li1 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_1_" + scwin.profileinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

									var append_output_name = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_profile_name_" + scwin.profileinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li1);
									var name = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_profileName");
									append_output_name.setLabel(name);
									var append_output_profile_name_inputbox = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_profile_name_input_" + scwin.profileinfo_itemIdx, "input", {}, group_li1);

									var group_li2 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_2_" + scwin.profileinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

									var append_output_app_id_input = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_select_release_type_" + scwin.profileinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li2);
									var type = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_deployType");
									append_output_app_id_input.setLabel(type);
									// var append_output_ios_select_release_type_select = WebSquare.util.dynamicCreate("tap_"+paramData.tap_num+"myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx, "select1", {appearance:"full"}, group_li2);

									var sel1 = '<xf:select1 xmlns:w2="http://www.inswave.com/websquare" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" ' +
											'renderType="native" disabledClass="w2selectbox_disabled" appearance="minimal" chooseOption="false" displayMode="label" allOption="" submenuSize="auto" direction="auto" id="' + "'tap_" + paramData.tap_num + "myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx + '"' +
											' style="" disabled="false" ref=""> <xf:choices></xf:choices> </xf:select1>';

									var append_output_ios_select_release_type_select = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx, "select1", sel1, group_li2);
									// var append_output_ios_select_release_type_select = WebSquare.util.getComponentById("tap_"+paramData.tap_num+"myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx);

									var message_dev = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_development");
									var message_appstore = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_app_store");
									var message_enterprise = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_enterprise");
									append_output_ios_select_release_type_select.addItem(message_dev, message_dev);
									append_output_ios_select_release_type_select.addItem(message_appstore, message_appstore);
									append_output_ios_select_release_type_select.addItem(message_enterprise, message_enterprise);

									var group_li3 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_3_" + scwin.profileinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

									var append_output_server_url = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_release_provisioning_" + scwin.profileinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li3);
									var provision = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_provisioning");
									append_output_server_url.setLabel(provision);
									var group_ipt_box3_1 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_ipt_box_3_" + scwin.profileinfo_itemIdx, "group", {className: "ipt_box"}, group_li3);
									var append_output_server_url_input = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_release_provisioning_input_file_path_" + scwin.profileinfo_itemIdx, "upload", {
										inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word",
										style: "position: relative;width: 250px;height: 23px;",
										imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word",
										disabled: "false"
									}, group_ipt_box3_1);
									var append_output_server_url_trigger = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_profile_group_trigger_" + scwin.profileinfo_itemIdx, "trigger", {className: "btn_cm"}, group_ipt_box3_1);
									var dash = common.getLabel("lbl_dash");
									append_output_server_url_trigger.setLabel(dash);
									// server info
									// trigger, onclick 기능 추가 -> 삭제 기능
									append_output_server_url_trigger.bind("onclick", function (e) {
										var save_group_ul1 = group_ul1;

										save_group_ul1.remove();

									});

									append_output_profile_name_inputbox.setValue(profilesKeyName);
									append_output_ios_select_release_type_select.setValue(profilesBuildType);
									$("#tap_" + paramData.tap_num + "myid_ios_release_provisioning_input_file_path_" + scwin.profileinfo_itemIdx + "_inputFile").val(profilesPath);

									// itemIdx++ 형식으로 id 채번 하기
									scwin.profileinfo_itemIdx++;
									input_profile_config_cnt.setValue(scwin.profileinfo_itemIdx);


								}

							}
						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};

			scwin.getDomainByIDView = function () {


			};

			scwin.getAdminByIDView = function () {


			};

			scwin.saveSigningKeySettingData = function () {

				var signingKeyfile = "";
				var debug_profile_path = "";
				var release_profile_path = "";

				if (common.isEmptyStr(signingkey_setting_name.getValue())) {
					var message = common.getLabel("lbl_check_key_cert_name");
					alert(message);
					return false;
				}

				if (!scwin.checkSigningkeyNameYn) {
					var message = common.getLabel("lbl_check_dup_key");
					alert(message)
					return false;
				}

				// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",signingkey_setting_name.getValue())){
				// 	alert("한글은 입력할 수 없습니다.");
				// 	return false;
				// }

				if (setting_select_platform.getValue() == "Android") {

					signingKeyfile = signingkey_setting_android_input_file_path_tmp.dom.fakeinput;
					// signingKeyfile = document.getElementById("signingkey_setting_android_input_file_path");
					if (checkChar(signingKeyfile.files[0])) {
						var message = common.getLabel("lbl_key_form_kor");
						alert(message);
						return false;
					}

				} else if (setting_select_platform.getValue() == "iOS") {

					var ios_signingkey_type = setting_ios_select_signingkey_type.getValue(); // signingkey type value 동적 처리

					if (ios_signingkey_type == "build") {
						debug_profile_path = signingkey_setting_ios_debug_provisioning_input_file_path.dom.fakeinput;
						release_profile_path = signingkey_setting_ios_release_provisioning_input_file_path.dom.fakeinput;
						signingKeyfile = signingkey_setting_ios_signingkey_input_file_path.dom.fakeinput;

						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", signingKeyfile.files[0].name)) {
							// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
							var message = common.getLabel("lbl_key_form_kor");
							alert(message);
							return false;
						}

						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", debug_profile_path.files[0].name)) {
							// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
							var message = common.getLabel("lbl_key_form_kor");
							alert(message);
							return false;
						}

						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", release_profile_path.files[0].name)) {
							// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
							var message = common.getLabel("lbl_key_form_kor");
							alert(message);
							return false;
						}

					} else if (ios_signingkey_type == "deploy") {

					}


				}

				// 특수문자 한글 입력 제외 시키기
				var formData = new FormData();

				// formData.append("hqKey", whive_session.user_login_id);
				formData.append("key_name", signingkey_setting_name.getValue());
				formData.append("platform", setting_select_platform.getValue());
				// android 기준
				// file path 도 같이 파싱해야함.
				if (setting_select_platform.getValue() == "Android") {
					// signing key android, ios 분할해서 처리

					var android_signingkey_type = setting_select_android_signingkey_type.getValue(); // signingkey type value 동적 처리
					// console.log(signingkey_setting_android_input_file_path_tmp.dom.fakeinput.files[0]); // dom
					// console.log(signingKeyfile.files[0]); // dom
					//signingKeyfile = document.getElementById("signingkey_setting_android_input_file_path");
					signingKeyfile = signingkey_setting_android_input_file_path_tmp.dom.fakeinput;

					formData.append("file", signingKeyfile.files[0]);
					formData.append("key_type", android_signingkey_type);
					formData.append("key_password", setting_android_key_password.getValue());
					formData.append("key_alias", setting_android_key_alias.getValue());
					formData.append("store_key_password", setting_android_store_password.getValue());
					// formData.append("build_type",setting_select_android_build_type.getValue());
					formData.append("builder_id", setting_builder_list.getValue());

					// if(whive_session.user_role == "SUPERADMIN"){
					//formData.append("domain_id",setting_domain_list.getValue());
					//formData.append("admin_id",setting_admin_list.getValue());

					// } else if (whive_session.user_role == "ADMIN"){
					/*
						//formData.append("domain_id",whive_session.domain_id);
						//formData.append("admin_id",whive_session.id);

						컨트롤러 단에서처리해야함.

					 */

					//}

					scwin.setKeySettingAndroidInsert(formData);

				} else {
					var ios_signingkey_type = setting_ios_select_signingkey_type.getValue(); // signingkey type value 동적 처리
					var ios_release_type = setting_ios_select_release_type.getValue();

					signingKeyfile = signingkey_setting_ios_signingkey_input_file_path.dom.fakeinput;
					debug_profile_path = signingkey_setting_ios_debug_provisioning_input_file_path.dom.fakeinput;
					release_profile_path = signingkey_setting_ios_release_provisioning_input_file_path.dom.fakeinput;


					formData.append("key_type", ios_signingkey_type);
					formData.append("key_password", setting_ios_store_password.getValue());
					formData.append("builder_id", setting_builder_list.getValue());

					// if(whive_session.user_role == "SUPERADMIN"){
					// formData.append("domain_id",setting_domain_list.getValue());
					// formData.append("admin_id",setting_admin_list.getValue());

					// } else if (whive_session.user_role == "ADMIN"){
					// formData.append("domain_id",whive_session.domain_id);
					// formData.append("admin_id",whive_session.id);

					// }


					if (ios_signingkey_type == "build") {
						var unlock_keychain_password = setting_ios_unlock_keychain_password.getValue();
						formData.append("keyfile", signingKeyfile.files[0]);
						formData.append("release_type", ios_release_type);
						formData.append("debugprofile", debug_profile_path.files[0]);
						formData.append("releaseprofile", release_profile_path.files[0]);
						formData.append("unlock_keychain_password", unlock_keychain_password);


						scwin.setKeySettingiOSInsert(formData);
					} else {
						formData.append("keyfile", signingKeyfile.files[0]);
						formData.append("ios_issuer_id", setting_ios_issuer_id.getValue());
						formData.append("ios_key_id", setting_ios_key_id.getValue());

						scwin.setKeySettingDeployiOSInsert(formData);
					}


				}

			};

			scwin.setKeySettingAndroidInsert = function (formData) {

				// formdata 방식으로 바꿔서 file 객체 만 받는 구조로 바꾸기...
				$.ajax({
					url: "/manager/mCert/android/create",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					// data: formData,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						var data = r;

						//console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;
							// alert("Build Key Setting 생성 완료");


							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});

			};

			scwin.setKeySettingiOSInsert = function (formData) {

				// formdata 방식으로 바꿔서 file 객체 만 받는 구조로 바꾸기...
				$.ajax({
					url: "/manager/mCert/iOS/create",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					// data: formData,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						var data = r;

						//console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;
							// alert("Build Key Setting 생성 완료");
							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});

			};

			//
			scwin.setKeySettingDeployiOSInsert = function (formData) {

				// formdata 방식으로 바꿔서 file 객체 만 받는 구조로 바꾸기...
				$.ajax({
					url: "/manager/mCert/iOS/deploy/create",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					// data: formData,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						var data = r;

						//console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;
							// alert("Deploy Key Setting 생성 완료");

							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});

			};

			scwin.btn_create_signingkey_setting_onclick = function (e) {
				scwin.saveSigningKeySettingData();


			};

			scwin.setting_key_name_check_onclick = function () {

				var key_name = signingkey_setting_name.getValue();

				if (common.isEmptyStr(key_name)) {
					var message = common.getLabel("lbl_check_key_name");
					alert(message);
					return false;
				}

				scwin.select_check_key_name(key_name);

			};

			scwin.setting_select_ios_export_profile_change = function () {

				var release_profile_val = setting_ios_select_release_type.getValue();

				if (release_profile_val == "app-store") {


				} else if (release_profile_val == "enterprise") {


				}


			};

			// websocket 으로 결과 받아서 message -> ajax 로 update 할 값을 전송한다.
			scwin.webSocketCallback = function (obj) {
				// msg type 추가
				//console.log(" key file create websocket ");
				switch (obj.MsgType) {
					case "BIN_FILE_PROFILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
						// scwin.setBuilderKeyFileCreateStatus(obj);
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData", "signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

						break;
					case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
						// scwin.setBuilderiOSKeyFileCreateStatus(obj);
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData", "signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

						break;
					case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO_FROM_HEADQUATER" :
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData", "signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");
						// scwin.setBuilderiOSDeployKeyFileCreateStatus(obj);
						break;
					default :
						break;
				}
			};

			// android/ios 분리해서 처리하기
			scwin.setBuilderKeyFileCreateStatus = function (msg) {

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
						$p.parent().wfm_main.setUserData("settingsData", "signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						// alert("Signing Key Setting 생성 실패");
					}
				};

				options.error = function (e) {
					alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");

				};

				$p.ajax(options);


			};

			scwin.setBuilderiOSKeyFileCreateStatus = function (msg) {

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
						$p.parent().wfm_main.setUserData("settingsData", "signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						// alert("Signing Key Setting 생성 실패");
					}
				};

				options.error = function (e) {
					alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");

				};

				$p.ajax(options);


			};

			scwin.setBuilderiOSDeployKeyFileCreateStatus = function (msg) {

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
						$p.parent().wfm_main.setUserData("settingsData", "signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						// alert("Signing Key Setting 생성 실패");
					}
				};

				options.error = function (e) {
					alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");

				};

				$p.ajax(options);


			};

			// file name 한글 체크 함수
			function checkChar(obj) {

				var chrTemp;
				var strTemp = obj.name;
				var strLen = strTemp.length;
				var numeric = false;
				var alpha = false;
				var korean = false;

				if (strLen > 0) {

					for (var i = 0; i < strTemp.length; i++) {

						chrTemp = strTemp.charCodeAt(i);

						if (chrTemp >= 1 && chrTemp <= 57) {
							numeric = true;
						} else if (chrTemp >= 65 && chrTemp <= 122) {
							alpha = true;
						} else {
							korean = true;
							// alert("한글, 영문, 숫자만 입력하세요.");
							return true;
						}


					}
				}

			};

			// function changeToolTipContentSigningkeyDetail (componentId) {
			// 	let signingkeyType = setting_ios_select_signingkey_type.getValue();
			// 	switch (signingkeyType) {
			// 		case "build":
			//             return "애플 개발자 인증서를 PKCS#12 형식으로 변환한 파일"
			// 		case "deploy":
			//             return "Apple에서 제공하는 Rest API 서비스 사용시 사용자 인증을 위한 키"
			// 		default:
			//             return ""
			// 	}
			// };

			scwin.btn_append_input_ios_profile_id_onclick = function (e) {

				var paramData = $p.getParameter("tabParam");

				// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
				var group_ul1 = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_group_ul_" + scwin.profileinfo_itemIdx, "group", {tagname: "ul"}, group_ios_profile_info_main);

				var group_li1 = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_1_" + scwin.profileinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_name = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_profile_name_" + scwin.profileinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li1);
				var name = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_profileName");
				append_output_name.setLabel(name);
				var append_output_profile_name_inputbox = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_profile_name_input_" + scwin.profileinfo_itemIdx, "input", {}, group_li1);

				var group_li2 = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_2_" + scwin.profileinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_app_id_input = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_select_release_type_" + scwin.profileinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li2);
				var type = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_deployType");
				append_output_app_id_input.setLabel(type);
				// var append_output_ios_select_release_type_select = WebSquare.util.dynamicCreate("tap_"+paramData.tap_num+"myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx, "select1", {appearance:"full"}, group_li2);

				var sel1 = '<xf:select1 xmlns:w2="http://www.inswave.com/websquare" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" ' +
						'renderType="native" disabledClass="w2selectbox_disabled" appearance="minimal" chooseOption="true" displayMode="label" allOption="" submenuSize="auto" direction="auto" id="' + "tap_" + paramData.tap_num + "myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx + '"' +
						' style="" disabled="false" ref=""> <xf:choices></xf:choices> </xf:select1>';

				var append_output_ios_select_release_type_select = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx, "select1", sel1, group_li2);
				// var append_output_ios_select_release_type_select = WebSquare.util.getComponentById("tap_"+paramData.tap_num+"myid_ios_select_release_type_select_box_" + scwin.profileinfo_itemIdx);

				var message_dev = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_development");
				var message_appstore = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_app_store");
				var message_enterprise = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_enterprise");
				append_output_ios_select_release_type_select.addItem(message_dev, message_dev);
				append_output_ios_select_release_type_select.addItem(message_appstore, message_appstore);
				append_output_ios_select_release_type_select.addItem(message_enterprise, message_enterprise);

				var group_li3 = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_3_" + scwin.profileinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_server_url = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_release_provisioning_" + scwin.profileinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li3);
				var provision = common.getLabel("lbl_setting_signingkey_ios_profiles_detail_provisioning");
				append_output_server_url.setLabel(provision);
				var group_ipt_box3_1 = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_group_ipt_box_3_" + scwin.profileinfo_itemIdx, "group", {className: "ipt_box"}, group_li3);
				var append_output_server_url_input = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_ios_release_provisioning_input_file_path_" + scwin.profileinfo_itemIdx, "upload", {
					inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word",
					style: "position: relative;width: 250px;height: 23px;",
					imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word",
					disabled: "false"
				}, group_ipt_box3_1);
				var append_output_server_url_trigger = $p.dynamicCreate("tap_" + paramData.tap_num + "myid_profile_group_trigger_" + scwin.profileinfo_itemIdx, "trigger", {className: "btn_cm"}, group_ipt_box3_1);
				var dash = common.getLabel("lbl_dash");
				append_output_server_url_trigger.setLabel(dash);
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_server_url_trigger.bind("onclick", function (e) {
					var save_group_ul1 = group_ul1;

					save_group_ul1.remove();

				});

				// itemIdx++ 형식으로 id 채번 하기
				scwin.profileinfo_itemIdx++;
				input_profile_config_cnt.setValue(scwin.profileinfo_itemIdx);

			};
			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'ftp_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_key_cert_regist_setting'}}]}]},{T:1,N:'xf:group',A:{class:'gal_body type2 mt30',id:'gal_body_ios',style:''},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_profile_config_cnt',style:'visibility:hidden;',initValue:''}},{T:1,N:'xf:input',A:{id:'signingkey_main_ios_key_id',style:'visibility: hidden'}},{T:1,N:'xf:group',A:{id:'group_ios_profile_info_main',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'ios_signingkey_password_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'setting_ios_profile_name_text',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_ios_profiles_detail_profileName'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',tooltipLocaleRef:'lbl_setting_signingkey_ios_profiles_detail_profileName',toolTipDisplay:'true',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'setting_ios_profile_name_input',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_ios_profiles_detail_deployType'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',tooltipLocaleRef:'lbl_setting_signingkey_ios_profiles_detail_tooltip_type',toolTipDisplay:'true',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_ios_select_release_type',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.setting_select_ios_export_profile_change'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'development'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'development'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'app-store'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'app-store'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'enterprise'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'enterprise'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'ios_release_profile_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'signingkey_setting_ios_release_provisioning_input_file_path_text',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_ios_profiles_detail_provisioning'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_ios_profiles_detail_profile'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'signingkey_setting_ios_release_provisioning_input_file_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_append_input_ios_profile_id_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]})