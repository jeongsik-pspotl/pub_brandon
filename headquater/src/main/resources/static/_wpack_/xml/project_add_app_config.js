/*amd /xml/project_add_app_config.xml 72312 56b9ff575fd67ea63037a97087446b5e9991be3b445ca72cb1096f5c7c1f5828 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/jszip.min.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/jszip-utils.min.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.app_icon_upload_check_yn = false;
			scwin.app_id_license_check_yn = false;
			scwin.app_id_temp_save = "";
			serverinfo_itemIdx = 0;
			scwin.onpageload = function () {
				// 현재 나의 scope 객체를 전역 변수에 저장한다.
				// common.setScopeObj(scwin);
				var type = $p.parent().$p.parent().scwin.txt_project_all_step_platform;

				var paramData = $p.getParameter("tabParam");

				//$p.parent().btn_proejct_step03.setDisabled(true);
				//$p.parent().btn_proejct_step04.setDisabled(true);
				//$p.parent().btn_proejct_step05.setDisabled(true);

				// console.log(paramData.profileList);

				if (type == "Android") {

					if (paramData.tap_num == "0") {
						grp_android_app_icon_id.show();

					} else {
						grp_android_app_icon_id.hide();

					}

					grp_iOS.hide();
					grp_Windows.hide();
					input_tap_config_cnt.setValue(paramData.tap_num);
				} else if (type == "Windows") {
					grp_iOS.hide();
					grp_Android.hide();
				} else {

					if (paramData.tap_num == "0") {
						grp_ios_app_icon_id.show();

					} else {
						grp_ios_app_icon_id.hide();

					}

					var profileList = paramData.profileList;
					var profileDebugList = paramData.profileDebugList;
					grp_Android.hide();
					grp_Windows.hide();

					var message = common.getLabel("lbl_select"); // 선택

					step2_select_profile_key_name.addItem("", message);
					step2_select_debug_profile_key_name.addItem("", message);
					// profile release
					for (var row in profileList) {
						step2_select_profile_key_name.addItem(profileList[row], profileList[row]);
					}

					for (var rowdebug in profileDebugList) {
						step2_select_debug_profile_key_name.addItem(profileDebugList[rowdebug], profileDebugList[rowdebug]);
					}

					// profile develope

					input_ios_tap_config_cnt.setValue(paramData.tap_num);
					input_ios_build_type_text.setValue(paramData.buildType);
				}
				// 초기 멀티도메인 컴포넌트 추가
				// scwin.generator_fixed_server_group_list(type);
				// scwin.getMultiDomainInfo();
				// scwin.makeStartServerGroupSelectList();

				//document.getElementById("step2_android_input_icons_path").addEventListener("change", function (event) {
				$p.getComponentById("step2_android_input_icons_path").render.addEventListener("change", function (event) {
					var imgExt = ['png', 'jpg', 'jpeg'];
					var files = event.target.files;
					var pro = arrangeZip(files[0]);

					pro.then(function (value) {

						for (var i = 0; i < value.length; i++) {
							if (value[i].type == 'image') {

								var blob = b64toBlob(value[i].cont, 'image/png');
								var appIconName = value[i].name;
								var reader = new FileReader();
								reader.readAsDataURL(blob);
								reader.fileName = appIconName;
								reader.platform = localStorage.getItem("_platform_");
								reader.onload = function (e) {
									// platform 기준 분리
									// 이미지 조회 기능 개선하기
									if (this.platform == 'Android') {

										icon_xhdpi.setSrc(this.result);

										// if (/-xhdpi/.test(this.fileName)) {
										// 	icon_xhdpi.setSrc(this.result);
										//
										// } else if (/-mdpi/.test(this.fileName)) {
										// 	icon_mdpi.setSrc(this.result);
										//
										// } else if (/-hdpi/.test(this.fileName)) {
										// 	icon_hdpi.setSrc(this.result);
										// }

									}

								}

							}

						}

					}, function (err) {
					}); //  step2_android_input_icons_path ends

				}, false);

				// ios app icon view
				// document.getElementById("step2_ios_input_icons_path").addEventListener("change", function (event) {
				//
				// 	var imgExt = ['png', 'jpg', 'jpeg'];
				// 	var promise = [];
				// 	var files = event.target.files;
				//
				// 	var pro = arrangeZip(files[0]);
				//
				// 	pro.then(function (value) {
				//
				// 		for (var i = 0; i < value.length; i++) {
				// 			if (value[i].type == 'image') {
				//
				// 				var blob = b64toBlob(value[i].cont, 'image/png');
				// 				var appIconName = value[i].name;
				// 				var reader = new FileReader();
				// 				reader.readAsDataURL(blob);
				// 				reader.fileName = appIconName;
				// 				reader.platform = localStorage.getItem("_platform_");
				// 				reader.onload = function (e) {
				//
				// 					if (this.platform == 'iOS') {
				// 						// platform 기준 분리
				// 						if (/^29/.test(this.fileName)) {
				// 							icon29.setSrc(this.result);
				//
				// 						} else if (/^40/.test(this.fileName)) {
				// 							icon40.setSrc(this.result);
				//
				// 						} else if (/^57/.test(this.fileName)) {
				// 							// icon57.setSrc(this.result);
				//
				// 						} else if (/^58/.test(this.fileName)) {
				// 							icon58.setSrc(this.result);
				//
				// 						} else if (/^60/.test(this.fileName)) {
				// 							icon60.setSrc(this.result);
				//
				// 						} else if (/^80/.test(this.fileName)) {
				// 							icon80.setSrc(this.result);
				//
				// 						} else if (/^87/.test(this.fileName)) {
				// 							icon87.setSrc(this.result);
				//
				// 						} else if (/^114/.test(this.fileName)) {
				// 							//icon114.setSrc(this.result);
				//
				// 						} else if (/^120/.test(this.fileName)) {
				// 							icon120.setSrc(this.result);
				//
				// 						} else if (/^180/.test(this.fileName)) {
				// 							icon180.setSrc(this.result);
				//
				// 						} else if (/^1024/.test(this.fileName)) {
				// 							icon1024.setSrc(this.result);
				// 						}
				// 					}
				//
				// 				}
				//
				// 			}
				//
				// 		}
				//
				// 	}, function (err) {
				// 	}); // step2_ios_input_icons_path end
				//
				// }, false);

			};

			scwin.onpageunload = function () {
				scwin.app_id_license_check_yn = false;
			};

			scwin.stringToUint = function (str) {
				return new Uint8Array([].map.call(str, function (x) {
					return x.charCodeAt(0)
				}));
			};

			scwin.intToBytes = function (x) {
				var i = 8, bytes = [];

				do {
					bytes[--i] = x & (255);
					x = x >> 8;
				} while (i);

				return bytes;
			};

			scwin.webSocketCallback = function (obj) {
				console.log(" add app config page : " + obj.MsgType);
				switch (obj.MsgType) {
					case "MV_MSG_SIGNIN_KEY_ADD_INFO_FROM_HEADQUATER" :
						scwin.setBranchPluginListStatus(obj);
						break;
					case "HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO" :
						consoel.log("bbb");
						break;
					default :
						break;
				}
			};

			scwin.setBranchPluginListStatus = function (data) {
				if (data.message == "SEARCHING") {
					var message = common.getLabel("lbl_uploading"); // Uploading
					WebSquare.layer.showProcessMessage(message);
					//console.log("검색중..");
				} else if (data.message == "SUCCESSFUL") {
					WebSquare.layer.hideProcessMessage();
					//console.log("처리완료");
				} else if (data.message == "FAILED") {
					//console.log("실패");
				}

			};

			scwin.checkData = function () {
				var platformType = localStorage.getItem("_platform_");
				var app_id = "";
				var app_name = "";
				var app_version = "";
				var app_version_code = "";
				var profile_debug = "";
				var profile_release = "";
				var appIDCheck = "";
				var packageName = "";


				var tempDataList = $p.parent().dtl_build_project_step2.getRowJSON(0);

				if (platformType == 'Android') {
					app_id = step2_android_input_app_id.getValue();
					app_name = step2_android_input_app_name.getValue();
					app_version = step2_android_input_app_version.getValue();
					app_version_code = step2_android_input_app_version_code.getValue();

				} else if (platformType == 'Windows') {
					app_id = step2_windows_input_app_id.getValue();
					app_name = step2_windows_input_app_name.getValue();
					app_version = step2_windows_input_app_version.getValue();
					app_version_code = step2_windows_input_app_version_code.getValue();

				} else {
					app_id = step2_ios_input_app_id.getValue();
					app_name = step2_ios_input_app_name.getValue();
					app_version = step2_ios_input_app_version.getValue();
					app_version_code = step2_ios_input_app_version_code.getValue();

				}

				var check_app_version_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION", app_version);
				var check_app_version_code_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION_CODE", app_version_code);

				var message_input_appid = common.getLabel("lbl_input_appid");
				var message_check_appid = common.getLabel("lbl_ask_appid_check");

				if (common.isEmptyStr(app_id)) {
					alert(message_input_appid);
					scwin.app_id_license_check_yn = false;
					return false;
				}

				if (tempDataList.app_id != "") {
					if (tempDataList.app_id != app_id) {
						alert(message_check_appid);
						scwin.app_id_license_check_yn = false;
						return false;
					}
				} else if (scwin.app_id_temp_save != "") {
					if (scwin.app_id_temp_save != app_id) {
						alert(message_check_appid);
						scwin.app_id_license_check_yn = false;
						return false;

					}
				}

				// 앱 라이센스 체크를 하세요.
				if (!scwin.app_id_license_check_yn) {
					alert(message_check_appid);
					return false;
				}

				appIDCheck = app_id.split("\.");

				var message_check_appid_form = common.getLabel("lbl_appid_check_form");
				var message_check_app_name = common.getLabel("lbl_check_app_name");

				// app id 체크
				if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
					for (var i = 0; i < appIDCheck.length; i++) {
						var strAppID = appIDCheck[i];

						var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

						if (!check_app_id_str) {
							alert(app_id + " : " + message_check_appid_form);
							return false;
						}
					}
				} else {
					alert(app_id + " : " + message_check_appid_form);
					return false;
				}

				scwin.app_id_temp_save = app_id;

				if (common.isEmptyStr(app_name)) {
					alert(message_check_app_name);
					return false;
				}

				// app name 자릿수 제한
				if (common.getCheckInputLength(app_name, app_name.length, 200)) {

					return false;
				}

				if (common.isEmptyStr(app_version)) {
					var message = common.getLabel("lbl_check_app_version");
					alert(message);
					return false;
				}

				if (!check_app_version_str) {
					var message = common.getLabel("lbl_app_version_form");
					alert(message);
					return false;
				}

				if (common.isEmptyStr(app_version_code)) {
					var message = common.getLabel("lbl_check_app_version_code");
					alert(message);
					return false;
				}

				if ((!check_app_version_code_str) && (platformType != 'Windows')) {
					var message = common.getLabel("lbl_app_version_code_form");
					alert(message);
					return false;
				}

				if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", app_version_code)) {
					var message = common.getLabel("lbl_app_version_code_kor");
					alert(message);
					return false;
				}

				if (common.checkAllInputText("CHECK_INPUT_TYPE_ENG", app_version_code)) {
					var message = common.getLabel("lbl_app_version_code_eng");
					alert(message);
					return false;
				}


				if (platformType == "Android") {
					packageName = step2_input_packagename.getValue();
				} else if (platformType == "iOS") {
					packageName = step2_ios_input_projectname.getValue();
				}

				// name, server, appid 정보
				if (common.isEmptyStr(packageName) && platformType == "Android") {
					var message = common.getLabel("lbl_check_package_name");
					alert(message);
					return false;
				}

				if (common.isEmptyStr(packageName) && platformType == "iOS") {
					var message = common.getLabel("lbl_check_package_name");
					alert(message);
					return false;
				}

				// packageName 자릿수 제한
				if (common.getCheckInputLength(packageName, packageName.length, 200)) {

					return false;
				}


				// 기존 step 3 화면 내 소스 코드 붙이기 ...
				var server_name_1 = input_appname_1.getValue();

				var app_id1 = input_appid_1.getValue();

				var server_URL1 = input_serverurl_1.getValue();

				var appIDCheck1 = "";
				var appIDCheckAll = "";


				// name, server, appid 정보
				// server name 빈칸
				if (common.isEmptyStr(server_name_1)) {
					var message = common.getLabel("lbl_check_server_name");
					alert(message);
					return false;
				}

				// app id 빈칸
				if (common.isEmptyStr(app_id1)) {
					var message = common.getLabel("lbl_input_appid");
					alert(message); //APPID를 입력하세요
					return false;
				}

				// server url 빈칸
				if (common.isEmptyStr(server_URL1)) {
					common.getLabel("lbl_project_add_app_config_js_check_server_url");
					alert(message); //server URL 입력하세요
					return false;
				}

				// server url 자릿수 제한
				if (common.getCheckInputLength(server_URL1, server_URL1.length, 200)) {

					return false;
				}

				// package name 한글 입력
				if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "Android") {
					var message = common.getLabel("lbl_package_name_kor_rule");
					alert(packageName + " : " + message);
					return false;
				}

				// project name 한글 입력
				if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "iOS") {
					var message = common.getLabel("lbl_package_name_kor_rule");
					alert(packageName + " : " + message);
					return false;
				}

				// server name 한글 입력
				if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", server_name_1)) {
					var message = common.getLabel("lbl_server_name_kor_rule");
					alert(server_name_1 + " : " + message);
					return false;
				}

				// server url 빈칸
				if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", server_URL1)) {
					var message = common.getLabel("lbl_project_add_app_config_js_check_server_url_kor");
					alert(server_URL1 + " : " + message); // server URL 형식에 한글은 입력할 수 없습니다
					return false;
				}

				if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "Android") {

					var message = common.getLabel("lbl_package_name_special_char_rule");
					alert(packageName + " : " + message);
					return false;
				}

				if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "iOS") {
					var message = common.getLabel("lbl_package_name_special_char_rule");
					alert(packageName + " : " + message);
					return false;
				}

				if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", server_name_1)) {
					var message = common.getLabel("lbl_server_name_special_char_rule");
					alert(server_name_1 + " : " + message);
					return false;
				}

				appIDCheck1 = app_id1.split("\.");

				// appID 1 check
				if (appIDCheck1.length >= 3 || appIDCheck1.length >= 4) {
					for (var i = 0; i < appIDCheck1.length; i++) {
						var strAppID = appIDCheck1[i];

						var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

						if (!check_app_id_str) {
							alert(app_id + " : " + message_check_appid_form);
							return false;
						}
					}

				} else {
					alert(app_id1 + " : " + message_check_appid_form);
					return false;
				}

				for (var i = 0; i < serverinfo_itemIdx; i++) {

					var servertempJson = {};

					var server_name = $p.getComponentById("myid_name_input_" + i);
					var server_app_id = $p.getComponentById("myid_app_id_input_" + i);
					var server_url = $p.getComponentById("myid_server_url_input_" + i);

					if (server_name != null) {
						servertempJson.Name = server_name.getValue();
					}

					if (server_app_id != null) {
						servertempJson.AppID = server_app_id.getValue();
					}

					if (server_url != null) {
						servertempJson.ServerURL = server_url.getValue();
					}

					// server name 한글 입력
					if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", servertempJson.Name)) {
						var message = common.getLabel("lbl_server_name_kor_rule");
						alert(servertempJson.Name + " : " + message); //server Name 형식에 한글은 입력할 수 없습니다
						return false;
					}

					// server url 빈칸
					if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", servertempJson.ServerURL)) {
						var message = common.getLabel("lbl_project_add_app_config_js_check_server_url_kor");
						alert(servertempJson.ServerURL + " : " + message); //server URL 형식에 한글은 입력할 수 없습니다.
						return false;
					}

					if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", servertempJson.Name)) {
						var message = common.getLabel("lbl_server_name_special_char_rule");
						alert(servertempJson.Name + " : " + message);
						return false;
					}

					if (server_app_id != null) {
						appIDCheckAll = servertempJson.AppID.split("\.");

						if (appIDCheckAll.length >= 3 || appIDCheckAll.length >= 4) {
							for (var i = 0; i < appIDCheckAll.length; i++) {
								var strAppID = appIDCheckAll[i];

								var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

								if (!check_app_id_str) {
									alert(servertempJson.AppID + " : " + message_check_appid_form); // APPID 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다.
									return false;
								}
							}
						} else {
							alert(servertempJson.AppID + " : " + message_check_appid_form); // APPID 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다.
							return false;
						}
					}
				}
				return true;
			};

			scwin.saveStep2Data = function () {
				var platformType = localStorage.getItem("_platform_");

				var app_id;
				var app_name;
				var app_version;
				var app_version_code;


				var data = {};

				if (platformType == 'Android') {
					app_id = step2_android_input_app_id.getValue();
					app_name = step2_android_input_app_name.getValue();
					app_version = step2_android_input_app_version.getValue();
					app_version_code = step2_android_input_app_version_code.getValue();

					data.platform_type = platformType;
					data.target = "server";
					data.app_id = app_id;
					data.app_name = app_name;
					data.app_version = app_version;
					data.app_version_code = app_version_code;
					data.server_URL = "";
					data.package_name = step2_input_packagename.getValue();
					data.min_target_version = step2_select_minsdk_version.getValue();
					data.test_url = "";

					//data.zip_file_name = document.getElementById("step2_android_input_icons_path").files[0] ? document.getElementById("step2_android_input_icons_path").files[0].name : "" ; //document.getElementById("step2_android_input_icons_path").files[0].name;
					var uploader = $p.getComponentById("step2_android_input_icons_path").render;
					data.zip_file_name = uploader.files[0] ? uploader.files[0] : "";

				} else if (platformType == 'Windows') {
					data.platform_type = platformType;
					data.target = "server";
					data.app_id = step2_windows_input_app_id.getValue();
					data.app_name = step2_windows_input_app_name.getValue();
					data.app_version = step2_windows_input_app_version.getValue();
					data.app_version_code = step2_windows_input_app_version_code.getValue();
					data.server_URL = "";
					data.package_name = step2_windows_input_app_version_code.getValue();

				} else {

					data.platform_type = platformType;
					data.target = "server";
					data.app_id = step2_ios_input_app_id.getValue();
					data.app_name = step2_ios_input_app_name.getValue();
					data.app_version = step2_ios_input_app_version.getValue();
					data.app_version_code = step2_ios_input_app_version_code.getValue();
					data.server_URL = "";
					data.package_name = step2_ios_input_projectname.getValue();
					data.min_target_version = step2_select_target_version.getValue();

					var uploader = $p.getComponentById("step2_android_input_icons_path").render;
					data.zip_file_name = uploader.files[0] ? uploader.files[0] : "";
					//data.zip_file_name = document.getElementById("step2_ios_input_icons_path").files[0] ? document.getElementById("step2_ios_input_icons_path").files[0].name : "" ;

					data.test_url = "";

				}

				$p.parent().dtl_build_project_step2.setJSON([data]);

			};

			// ios는 인증서 생성 이후 data list 로 저장한다.
			scwin.btn_ios_signing_cli_onclick = function (e) {
				if (scwin.checkData()) {

					var platform = localStorage.getItem("_platform_");
					// 하나의 list 로 정리해야함..
					var step1DataList = $p.parent().dtl_build_project_step1.getAllJSON();

					var data = {};
					data.app_id = step2_ios_input_app_id.getValue();
					data.app_name = step2_ios_input_app_name.getValue();

					var beforeCertificates = document.getElementById("step2_ios_input_app_store_certificates");

					if (beforeCertificates.files.length === 0) {
						return;
					}

					var formData = new FormData();

					formData.append("file", beforeCertificates.files[0]);
					formData.append("app_id", data.app_id);
					formData.append("target_server", step1DataList[0].target_server);
					formData.append("app_name", data.app_name);
					formData.append("platform", platform);
					formData.append("signing_path", data.signing_path);
					formData.append("signing_issuer_id", data.signing_issuer_id);
					formData.append("signing_key_id", data.signing_key_id);
					formData.append("provisioning_profiles_name", data.provisioning_profiles_name);

					$.ajax({
						url: "/api/buildsetting/signingkeyfile",
						type: "POST",
						enctype: 'multipart/form-data',
						processData: false,
						contentType: false,
						data: formData,
						dataType: 'json',
						cache: false,
						success: function (r, status) {
							if (status === "success") {

							}

						}
						, error: function (request, status, error) {
							alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
						}
					});
				}
				;

			};

			scwin.getStringUTF8 = function (dataview, offset, length) {
				var s = '';

				for (var i = 0, c; i < length;) {
					c = dataview.getUint8(offset + i++);
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
			};

			// android keystore file send
			scwin.android_keystorefile_send = function () {
				var provisioningProfile = document.getElementById("step2_android_input_keystore_path");

				var step1DataList = $p.parent().dtl_build_project_step1.getAllJSON();

				var formData = new FormData();

				formData.append("file", provisioningProfile.files[0]);
				formData.append("target_server", step1DataList[0].target_server);
				formData.append("platform", step1DataList[0].platform);

				var data = {};
				data.MsgType = "HV_BIN_MSG_PROJECT_PROVIONING_PROFILE_UPLOAD_INFO";
				data.sessType = "BRANCH";
				data.filename = "";

				data.platform = localStorage.getItem("_platform_");
				data.target_server = step1DataList[0].target_server;

				$.ajax({
					url: "/api/buildsetting/signingkeyupload",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						if (status === "success") {

						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");
					}
				});

			};

			// 앱아이콘 업로드
			scwin.icons_zip_upload_send = function () {

				var platform = localStorage.getItem("_platform_");
				var formData = new FormData();
				var step1DataList = $p.parent().dtl_build_project_step1.getAllJSON();
				var ZipIconfile = "";

				if (platform == "Android") {
					//ZipIconfile = document.getElementById("step2_android_input_icons_path");
					ZipIconfile = $p.getComponentById("step2_android_input_icons_path").render;
					formData.append("file", ZipIconfile.files[0]);

				} else {
					//ZipIconfile = document.getElementById("step2_ios_input_icons_path");
					ZipIconfile = $p.getComponentById("step2_android_input_icons_path").render;
					formData.append("file", ZipIconfile.files[0]);
				}

				// branch_id
				// domain id
				// user id
				// workspace id
				// project id
				formData.append("branch_id", step1DataList[0].builder_id);
				formData.append("workspacename", localStorage.getItem("__workspace_name__"));
				formData.append("projectname", step1DataList[0].project_name);
				formData.append("projectDirName", step1DataList[0].project_dir_path);
				formData.append("platform", platform);
				formData.append("target_server", step1DataList[0].target_server);

				// project create 처리시 수정 필요.
				var data = {};
				data.MsgType = "HV_BIN_MSG_PROJECT_PROVIONING_PROFILE_UPLOAD_INFO";
				data.sessType = "BRANCH";
				data.filename = "";

				data.platform = platform;
				data.target_server = step1DataList[0].target_server;

				var message_uploaded_image = common.getLabel("lbl_img_upload_complete");


				$.ajax({
					url: "/manager/build/setting/upload/icon",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						//console.log(r);
						if (status === "Created") {
							//console.log("status success ");
							alert(message_uploaded_image); //이미지 업로드 완료 되었습니다.
							scwin.app_icon_upload_check_yn = true;

						}

					}
					, error: function (request, status, error) {
						if (request.status == 200) {
							alert(message_uploaded_image); //이미지 업로드 완료 되었습니다.
							scwin.app_icon_upload_check_yn = true;
						}
					}
				});
			};

			// ios provisioning send text blob type
			scwin.ios_json_send = function () {

				var platform = localStorage.getItem("_platform_");
				var provisioningProfile = document.getElementById("step2_ios_input_provisioning_profile_path");

				var step1DataList = $p.parent().dtl_build_project_step1.getAllJSON();

				var formData = new FormData();

				formData.append("file", provisioningProfile.files[0]);
				formData.append("target_server", step1DataList[0].target_server);
				formData.append("platform", platform);

				// project create 처리시 수정 필요.
				var data = {};
				data.MsgType = "HV_BIN_MSG_PROJECT_PROVIONING_PROFILE_UPLOAD_INFO";
				data.sessType = "BRANCH";
				data.filename = "";

				data.platform = localStorage.getItem("_platform_");
				data.target_server = step1DataList[0].target_server;

				$.ajax({
					url: "/api/buildsetting/signingkeyupload",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						if (status === "success") {

						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");
					}
				});

			};

			scwin.select_check_app_id = function (obj) {
				var options = {};

				options.action = "/manager/build/project/checkLicense";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(obj);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if (e.responseStatusCode === 200 || e.responseStatusCode === 201) {
						if (data != null) {
							var message = common.getLabel("lbl_can_use_appid");
							alert(obj.appID + " : " + message); //APPID는 사용 가능 합니다.

							// flag 처리
							scwin.app_id_license_check_yn = true;
                            $p.parent().scwin.app_id_license_check_yn = true;
							scwin.app_id_temp_save = obj.appID;
						}
					}
				};

				options.error = function (e) {
					if (e.responseStatusCode === 500) {

						var errorObj = JSON.parse(e.responseBody.replace("text/", ""));

						if (errorObj[0]) {
							alert(errorObj[0].error);
						} else {
							var message = common.getLabel("lbl_appid_license");
							alert(obj.appID + " : " + message);
						}
						scwin.app_id_license_check_yn = false;
					} else {

						var message = e.responseText;

						alert("code:" + e.responseStatusCode + "\n" + "message:" + message.result + "\n" + "error:" + e.requestBody);
					}

				};

				$p.ajax(options);

			};

			scwin.btn_Android_prev_onclick = function (e) {
				$p.parent().scwin.selected_step(1);
			};

			scwin.btn_Android_next_onclick = function (e) {
				if (scwin.checkData()) {
					scwin.saveStep2Data();
					scwin.btn_project_step3_checkYn = true;
					$p.parent().scwin.selected_step(3);
				}
			};

			scwin.btn_iOS_prev_onclick = function (e) {
				$p.parent().scwin.selected_step(1);
			};

			scwin.btn_iOS_next_onclick = function (e) {
				if (scwin.checkData()) {
					scwin.saveStep2Data();
					$p.parent().scwin.selected_step(3);
				}
			};

			scwin.btn_Windows_prev_onclick = function (e) {
				$p.parent().scwin.selected_step(1);
			};

			scwin.btn_Windows_next_onclick = function (e) {
				if (scwin.checkData()) {
					scwin.saveStep2Data();
					$p.parent().scwin.selected_step(3);
				}
			};

			scwin.btn_keychain_upload_onclick = function (e) {
				//input_keychain.setValue("");
				var message_process = common.getLabel("lbl_uploading");
				var message_success_upload = common.getLabel("lbl_upload_success");
				WebSquare.layer.showProcessMessage(message_process); // uploading
				setTimeout(function () {
					alert(message_success_upload); //upload success
					WebSquare.layer.hideProcessMessage();
				}, 1000);
			};

			function arrangeZip(path) {
				var promise = [];
				// var zip = new JSZip();
				var imgExt = ['png', 'jpg', 'jpeg'];
				var htmlExt = ['xhtml', 'html'];
				var MACdir = ['__MACOSX/'];
				return JSZip.loadAsync(path).then(function (data) {
					//return Promise.all(
					//console.log(data);
					data.forEach(function (relpath, file) {
						//console.log(file);
						var ext = file.name.split('.');
						ext = ext[ext.length - 1];
						//console.log("ext ?? " + ext);
						var contentType = '';
						var dataType = '';
						if (!imgExt.indexOf(ext) && !/^__MACOSX/.test(file.name)) {
							dataType = 'base64';
							contentType = 'image/' + ext;
							promise.push(file.async(dataType).then(function (cont) {
								return new Promise(function (resolve, reject) {
									resolve({cont: cont, name: file.name, type: 'image'});
								});
							}, function (err) {
								reject(err);
							}));
						}

					});
					return Promise.all(promise);
				});
			};

			function b64toBlob(b64Data, contentType, sliceSize) {
				contentType = contentType || '';
				sliceSize = sliceSize || 512;

				var byteCharacters = atob(b64Data);
				var byteArrays = [];

				for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
					var slice = byteCharacters.slice(offset, offset + sliceSize);

					var byteNumbers = new Array(slice.length);
					for (var i = 0; i < slice.length; i++) {
						byteNumbers[i] = slice.charCodeAt(i);
					}

					var byteArray = new Uint8Array(byteNumbers);

					byteArrays.push(byteArray);
				}

				var blob = new Blob(byteArrays, {type: contentType});
				return blob;
			};

			scwin.btn_android_icons_upload_onclick = function (e) {
				var message_process = common.getLabel("lbl_uploading");
				var message_success_upload = common.getLabel("lbl_upload_success");

				WebSquare.layer.showProcessMessage(message_process); // uploading
				setTimeout(function () {
					alert(message_success_upload); //upload success
					WebSquare.layer.hideProcessMessage();
					// icon_ldpi.setSrc("/images/temp/mipmap-ldpi/logo.png");
					icon_mdpi.setSrc("/images/temp/mipmap-mdpi/logo.png");
					icon_hdpi.setSrc("/images/temp/mipmap-hdpi/logo.png");
					icon_xhdpi.setSrc("/images/temp/mipmap-xhdpi/logo.png");
					// icon_xxhdpi.setSrc("/images/temp/mipmap-xxxhdpi/logo.png");
					// icon_xxxhdpi.setSrc("/images/temp/mipmap-xxxhdpi/logo.png");
				}, 1500);

			};

			scwin.btn_ios_provisioning_onclick = function (e) {
				var message_process = common.getLabel("lbl_uploading");
				WebSquare.layer.showProcessMessage(message_process); // uploading

				scwin.ios_json_send();
				setTimeout(function () {

					WebSquare.layer.hideProcessMessage();
				}, 1000);
			};

			scwin.btn_create_keystore_upload_onclick = function (e) {
				var message_process = common.getLabel("lbl_uploading");
				WebSquare.layer.showProcessMessage(message_process); // uploading

				scwin.android_keystorefile_send();

				setTimeout(function () {

					WebSquare.layer.hideProcessMessage();
				}, 1000);


			};

			scwin.btn_ios_icon_upload_onclick = function (e) {
				var message_process = common.getLabel("lbl_uploading");
				var message_success_upload = common.getLabel("lbl_upload_success");
				WebSquare.layer.showProcessMessage(message_process); // uploading

				setTimeout(function () {
					alert(message_success_upload); //upload success
					WebSquare.layer.hideProcessMessage();
					icon29.setSrc("/images/temp2/29.png");
					icon40.setSrc("/images/temp2/40.png");
					// icon57.setSrc("/images/temp2/57.png");
					icon58.setSrc("/images/temp2/58.png");
					icon60.setSrc("/images/temp2/60.png");
					icon80.setSrc("/images/temp2/80.png");
					icon87.setSrc("/images/temp2/87.png");
					// icon114.setSrc("/images/temp2/114.png");
					icon120.setSrc("/images/temp2/120.png");
					icon180.setSrc("/images/temp2/180.png");
					icon1024.setSrc("/images/temp2/1024.png");
				}, 2500);
			};

			scwin.btn_check_app_id_onclick = function (e) {

				var obj = {};
				var appID = "";
				var appIDCheck = "";


				var platform = localStorage.getItem("_platform_");
				if (platform == "Android") {
					appID = step2_android_input_app_id.getValue();
				} else if (platform == "iOS") {
					appID = step2_ios_input_app_id.getValue();
				}

				if (common.isEmptyStr(appID)) {
					var message = common.getLabel("lbl_input_appid");
					alert(message); //APP ID를 입력하세요
					scwin.app_id_license_check_yn = false;
					return false;
				}

				appIDCheck = appID.split("\.");

				var message_appid_form = common.getLabel("lbl_appid_check_form");

				if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
					for (var i = 0; i < appIDCheck.length; i++) {
						var strAppID = appIDCheck[i];

						var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

						if (!check_app_id_str) {
							alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
							return false;
						}
					}

				} else {
					alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
					return false;
				}

				obj.appID = appID;
				obj.platform = platform;

				scwin.select_check_app_id(obj);

			};

			function changeToolTipContentAddStep2(componentId, label) {
				var message_tooltip_android_appid = common.getLabel("lbl_android_appid");
				var message_tooltip_android_app_name = common.getLabel("lbl_app_name");
				var message_tooltip_android_app_version = common.getLabel("lbl_app_version");
				var message_tooltip_android_app_version_code = common.getLabel("lbl_app_version_code");
				var message_tooltip_android_min_sdk = common.getLabel("lbl_min_os_version");
				var message_tooltip_ios_appid = common.getLabel("lbl_ios_appid");
				var message_tooltip_ios_app_name = common.getLabel("lbl_ios_app_name");
				var message_tooltip_ios_app_version = common.getLabel("lbl_ios_app_version");
				var message_tooltip_ios_app_version_code = common.getLabel("lbl_ios_app_version_code");
				var message_tooltip_ios_min_sdk = common.getLabel("lbl_ios_min_os_version");

				switch (componentId) {
					case "wfm_main_wfm_project_add_step2_android_appid_tooltip":
						return message_tooltip_android_appid //앱을 구분하기 위한 ID <br>Android : Application ID
					case "wfm_main_wfm_project_add_step2_android_appname_tooltip":
						return message_tooltip_android_app_name //기기에 표시될 앱 이름
					case "wfm_main_wfm_project_add_step2_android_appversion_tooltip":
						return message_tooltip_android_app_version //앱 버전명
					case "wfm_main_wfm_project_add_step2_android_appversioncode_tooltip":
						return message_tooltip_android_app_version_code //앱 버전 코드
					case "wfm_main_wfm_project_add_step2_android_minsdk_tooltip":
						return message_tooltip_android_min_sdk //최소 지원 OS버전 <br>21 - Android 5.0 (Lollipop)<br>22 - Android 5.1 (Lollipop)<br>23 - Android 6.0 (Marshmallow)<br>24 - Android 7.0 (Nougat)<br>25 - Android 7.1.1 (Nougat)<br>26 - Android 8.0 (Oreo)<br>27 - Android 8.1 (Oreo)<br>28 - Android 9.0 (Pie)<br>29 - Android 10.0 (Q)<br>30 - Android 11.0 (R)<br>31 - Android 12.0 (S)
					case "wfm_main_wfm_project_add_step2_ios_appid_tooltip":
						return message_tooltip_ios_appid //앱을 구분하기 위한 ID <br>iOS : Bundle Identifier
					case "wfm_main_wfm_project_add_step2_ios_appname_tooltip":
						return message_tooltip_ios_app_name //기기에 표시될 앱 이름 <br>iOS : Display Name(Product Name)
					case "wfm_main_wfm_project_add_step2_ios_appversion_tooltip":
						return message_tooltip_ios_app_version //앱 버전명 <br>iOS : Version(Market Version)
					case "wfm_main_wfm_project_add_step2_ios_appversioncode_tooltip":
						return message_tooltip_ios_app_version_code //앱 버전 코드<br>iOS : Build(Current Project Version)
					case "wfm_main_wfm_project_add_step2_ios_minsdk_tooltip":
						return message_tooltip_ios_min_sdk //최소 지원 OS버전<br>12.0 ~ lastest<br>13.0 ~ lastest<br>14.0 ~ lastest<br>15.0 ~ lastest
					default:
						return ""
				}
			};

			scwin.btn_append_input_android_bundle_id_onclick = function (e) {

				var paramData = $p.getParameter("tabParam");

				// alert(paramData.tap_num);

				// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
				var group_ul1 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_ul_" + serverinfo_itemIdx, "group", {tagname: "ul"}, group_server_info_main);

				var group_li1 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_1_" + serverinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_name = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_name_" + serverinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li1);
				var message_name = common.getLabel("lbl_name");
				append_output_name.setLabel(message_name + " "); // Name
				var append_output_name_inputbox = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_name_input_" + serverinfo_itemIdx, "input", {}, group_li1);

				var group_li2 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_2_" + serverinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_app_id_input = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_app_id_" + serverinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li2);
				var message_appid = common.getLabel("lbl_appid");
				append_output_app_id_input.setLabel(message_appid + " "); // APP ID
				var append_output_app_id = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_app_id_input_" + serverinfo_itemIdx, "input", {}, group_li2);

				var group_li3 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_li_3_" + serverinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_server_url = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_server_url_" + serverinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li3);
				var message_server_url = common.getLabel("lbl_serverurl");
				append_output_server_url.setLabel(message_server_url + " "); // Server URL
				var group_ipt_box3_1 = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_group_ipt_box_3_" + serverinfo_itemIdx, "group", {className: "ipt_box"}, group_li3);
				var append_output_server_url_input = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_server_url_input_" + serverinfo_itemIdx, "input", {}, group_ipt_box3_1);

				var append_output_server_url_trigger = WebSquare.util.dynamicCreate("tap_" + paramData.tap_num + "myid_server_url_trigger_" + serverinfo_itemIdx, "trigger", {className: "btn_cm"}, group_ipt_box3_1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_server_url_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_server_url_trigger.bind("onclick", function (e) {
					var save_group_ul1 = group_ul1;

					save_group_ul1.remove();

				});

				// itemIdx++ 형식으로 id 채번 하기
				serverinfo_itemIdx++;
				input_server_config_cnt.setValue(serverinfo_itemIdx);

			};


			scwin.btn_ios_append_input_android_bundle_id_onclick = function (e) {

				var paramData = $p.getParameter("tabParam");

				// alert(paramData.tap_num);

				// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
				var group_ul1 = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_group_ul_" + serverinfo_itemIdx, "group", {tagname: "ul"}, group_ios_server_info_main);

				var group_li1 = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_group_li_1_" + serverinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_name = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_name_" + serverinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li1);
				var message_name = common.getLabel("lbl_name");
				append_output_name.setLabel(message_name + " ");
				var append_output_name_inputbox = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_name_input_" + serverinfo_itemIdx, "input", {}, group_li1);

				var group_li2 = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_group_li_2_" + serverinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_app_id_input = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_app_id_" + serverinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li2);
				var message_appid = common.getLabel("lbl_appid");
				append_output_app_id_input.setLabel(message_appid + " "); // APP ID
				var append_output_app_id = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_app_id_input_" + serverinfo_itemIdx, "input", {}, group_li2);


				var group_li3 = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_group_li_3_" + serverinfo_itemIdx, "group", {tagname: "li"}, group_ul1);

				var append_output_server_url = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_server_url_" + serverinfo_itemIdx, "textbox", {className: "w2textbox form_name"}, group_li3);
				var message_server_url = common.getLabel("lbl_serverurl");
				append_output_server_url.setLabel(message_server_url + " "); // Server URL
				var group_ipt_box3_1 = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_group_ipt_box_3_" + serverinfo_itemIdx, "group", {className: "ipt_box"}, group_li3);
				var append_output_server_url_input = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_server_url_input_" + serverinfo_itemIdx, "input", {}, group_ipt_box3_1);
				var append_output_server_url_trigger = WebSquare.util.dynamicCreate("ios_tap_" + paramData.tap_num + "myid_server_url_trigger_" + serverinfo_itemIdx, "trigger", {className: "btn_cm"}, group_ipt_box3_1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_server_url_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_server_url_trigger.bind("onclick", function (e) {
					var save_group_ul1 = group_ul1;

					save_group_ul1.remove();

				});

				// itemIdx++ 형식으로 id 채번 하기
				serverinfo_itemIdx++;
				input_ios_server_config_cnt.setValue(serverinfo_itemIdx);

			};

			// 멀티 도메인 관련 자바스크립트
			scwin.generator_fixed_server_group_list = function (type) {
				var gen_idx;

				if (type == 'Android') {
					gen_idx = fixed_android_server_group_list.insertChild();
					fixed_android_server_group_list.getChild(gen_idx, "fixed_android_server_info").insertChild();
				} else {
					gen_idx = fixed_ios_server_group_list.insertChild();
					fixed_ios_server_group_list.getChild(gen_idx, "fixed_ios_server_info").insertChild();
				}
			}

			scwin.btn_fixed_server_info_add_onclick = function () {
				let platform = $p.parent().$p.parent().scwin.txt_project_all_step_platform;
				if (platform == 'Android') {
					fixed_android_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "fixed_android_server_info").insertChild(this.getGeneratedIndex() + 1);
				} else {
					fixed_ios_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "fixed_ios_server_info").insertChild(this.getGeneratedIndex() + 1);
				}
			}

			scwin.btn_fixed_server_info_remove_onclick = function () {
				let cmpGenerator = this.getGenerator();
				if (cmpGenerator.getLength() <= 1) {
					alert("서버 정보는 최소 1개 이상 있어야 합니다.");
					return;
				}
				let numIndex = this.getGeneratedIndex();
				cmpGenerator.removeChild(numIndex);
			}

			scwin.btn_server_info_add_onclick = function () {
				let platform = $p.parent().$p.parent().scwin.txt_project_all_step_platform;
				if (platform == 'Android') {
					android_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "android_server_info").insertChild(this.getGeneratedIndex() + 1);
				} else {
					ios_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "ios_server_info").insertChild(this.getGeneratedIndex() + 1);
				}
			};

			scwin.generator_server_group_list = function () {
				let platform = $p.parent().$p.parent().scwin.txt_project_all_step_platform;

				var gen_idx;
				var gen_cond;

				if (platform == 'Android'){
					gen_idx = android_server_group_list.insertChild();
					gen_cond = android_server_group_list.getChild(gen_idx, "android_server_info").insertChild();
				} else {
					gen_idx = ios_server_group_list.insertChild();
					gen_cond = ios_server_group_list.getChild(gen_idx, "ios_server_info").insertChild();
				}
				scwin.makeStartServerGroupSelectList();
			};

			scwin.makeStartServerGroupSelectList = function () {
				var platform = $p.parent().$p.parent().scwin.txt_project_all_step_platform;
				step2_ios_select_start_server_group.removeAll();
				step2_android_select_start_server_group.removeAll();

				var first_server_group = (platform == 'Android') ? fixed_android_server_group_list.getChild(0, 'fixed_input_android_server_group_name').getValue() : fixed_ios_server_group_list.getChild(0, 'fixed_input_ios_server_group_name').getValue();
				(platform == 'Android') ? step2_android_select_start_server_group.addItem(first_server_group, first_server_group) : step2_ios_select_start_server_group.addItem(first_server_group, first_server_group);

				var list = (platform == 'Android') ? android_server_group_list : ios_server_group_list;

				for (var i = 0; i < list.getLength(); i++) {
					var value = (platform == 'Android') ? android_server_group_list.getChild(i, 'input_android_server_group_name').getValue() : ios_server_group_list.getChild(i, 'input_ios_server_group_name').getValue();

					// first_server_group을 처음에 추가했으므로 i+1을 해준다.
					(platform == 'Android') ? step2_android_select_start_server_group.addItem(value, value) : step2_ios_select_start_server_group.addItem(value, value);
				}
			};

			scwin.remove_server_group_list = function () {
				this.getGenerator().removeChild(this.getGeneratedIndex());
				scwin.makeStartServerGroupSelectList();
			};

			scwin.btn_server_info_remove_onclick = function (e) {
				// Generator 컴포넌트를 반환합니다. Generator의 ID를 알고 있는 경우 직접 접근할 수 있습니다. - server_info
				let cmpGenerator = this.getGenerator();

				if (cmpGenerator.getLength() <= 1) {
					alert("서버 정보는 최소 1개 이상 있어야 합니다.");
					return;
				}
				// 자신의 반복부 index를 반환합니다.
				let numIndex = this.getGeneratedIndex();
				// index에 해당하는 반복부 아이템을 삭제합니다.
				cmpGenerator.removeChild(numIndex);
			};
			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box android',id:'grp_Android',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_android_app_default_info'}},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_profile_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_profilename_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_profile_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step2_android_input_profile_name',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appid_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_appid'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{id:'step2_android_input_app_id',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_check_app_id_onclick',useLocale:'true',localeRef:'lbl_appid_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appname_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step2_android_input_app_name',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_app_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appversion_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step2_android_input_app_version',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appversioncode_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version_code'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'step2_android_input_app_version_code',style:'',adjustMaxLength:'false'}}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_package_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_use_package_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_android_input_packagename',style:'',initValue:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_minsdk_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_min_os_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step2_select_minsdk_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'21'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'21'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'22'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'22'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'23'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'23'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'24'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'24'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'25'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'25'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'26'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'26'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'27'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'27'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'28'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'28'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'29'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'29'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'30'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'30'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'31'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'31'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'group_server_info_main',style:'',class:'form_wrap'},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_server_config_cnt',style:'visibility:hidden;',initValue:''}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_tap_config_cnt',style:'visibility:hidden;',initValue:''}}]},{T:1,N:'xf:group',A:{class:'icon_box',id:'grp_android_app_icon_id',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_icon'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_android_input_icons_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}}]},{T:1,N:'xf:group',A:{class:'viewer',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w48',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon_mdpi',src:'/images/contents/img_view_48x48.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w48'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w72',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon_hdpi',src:'/images/contents/img_view_72x72.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w72'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w96',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon_xhdpi',src:'/images/contents/img_view_96x96.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w96'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box windows',id:'grp_Windows',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_windows_app_default_info'}},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_name'}},{T:1,N:'xf:input',A:{id:'step2_windows_input_app_id',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project'}},{T:1,N:'xf:input',A:{id:'step2_windows_input_app_name',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_version'}},{T:1,N:'xf:input',A:{id:'step2_windows_input_app_version',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_op_mode'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'step2_windows_input_app_version_code',style:'',adjustMaxLength:'false'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'icon_box',id:'',style:''}},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_windows_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_Windows_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_windows_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_Windows_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box iOS',id:'grp_iOS',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_app_default_info'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_ios_build_type_text',style:'visibility:hidden;',initValue:''}},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'form_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_dev_profile_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_debug_profilename_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'false',tooltipLocaleRef:'lbl_dev_profile_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step2_select_debug_profile_key_name',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_rel_profile_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_profilename_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'false',tooltipLocaleRef:'lbl_rel_profile_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step2_select_profile_key_name',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':''}}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appid_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_appid'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_id',style:''}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_check_app_id_onclick',useLocale:'true',localeRef:'lbl_appid_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appname_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_name',style:''}}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appversion_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_version',style:''}}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appversioncode_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version_code'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_version_code',style:''}}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'tooltip_box',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_xcode_project_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_ios_project_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_projectname',style:'',initValue:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_minsdk_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep2',useLocale:'false',tooltipLocaleRef:'lbl_min_os_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step2_select_target_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'12.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'12.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'13.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'13.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'14.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'14.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'15.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'15.0'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'group_ios_server_info_main',style:'',class:'form_wrap'},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_ios_server_config_cnt',style:'visibility:hidden;',initValue:''}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_ios_tap_config_cnt',style:'visibility:hidden;',initValue:''}}]},{T:1,N:'xf:group',A:{class:'icon_box',id:'grp_ios_app_icon_id',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_icon'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_ios_input_icons_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}}]},{T:1,N:'xf:group',A:{class:'viewer',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w36',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon29',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w29'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w48',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon40',src:'/images/contents/img_view_48x48.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w40'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w72',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon58',src:'/images/contents/img_view_72x72.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w58'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w72',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon60',src:'/images/contents/img_view_72x72.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w60'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w96',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon80',src:'/images/contents/img_view_96x96.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w80'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w96',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon87',src:'/images/contents/img_view_96x96.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w87'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w144',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon120',src:'/images/contents/img_view_144x144.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w120'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w144',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon180',src:'/images/contents/img_view_144x144.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w180'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w192',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon1024',src:'/images/contents/img_view_192x192.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w1024'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]})