/*amd /cm/ui/works/project_add_step02_app_config.xml 59478 538b50d10add759fcf4ddebd40fca908d554fb7249d0dd291893c77f6d38303c */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dtl_ios_profile',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'name',name:'name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'value',name:'value',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dtl_ios_debug_profile',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'name',name:'name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'value',name:'value',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.app_icon_upload_check_yn = false;
						scwin.app_id_license_check_yn = false;
						scwin.app_id_temp_save = "";
						serverinfo_itemIdx = 0;

						scwin.platformType = "";

						scwin.onpageload = function () {
							document.getElementById("step2_android_input_icons_path").addEventListener("change", scwin.AndroidIconChange, false);
							document.getElementById("step2_ios_input_icons_path").addEventListener("change", scwin.iosIconChange, false);
							let message = ""

							const paramData = $p.getParameter("tabParam");
							scwin.platformType = $p.parent().scwin.platformType;

							if (scwin.platformType == "Android") {
								grp_iOS.hide();
							} else if (scwin.platformType == "iOS") {
								grp_Android.hide();
								let profileList = paramData.profileList;
								let profileDebugList = paramData.profileDebugList;

								let profileArr = [];
								for (let row in profileList) {
									profileArr.push({"name": profileList[row], "value": profileList[row]});
								}
								dtl_ios_profile.setJSON(profileArr);

								let debugProfileArr = [];
								for (let row in profileDebugList) {
									debugProfileArr.push({"name": profileDebugList[row], "value": profileDebugList[row]});
								}
								dtl_ios_debug_profile.setJSON(debugProfileArr);

								scwin.set_xcode_project_name();
							} else {
								grp_iOS.hide();
								grp_Android.hide();
								common.win.alert("platformType not found");
							}
						};

						// tab을 추가하거나, 첫번째 tab에서 xcode project name을 수정하면
						// 가장 첫번째 tab에 있는 xcode project name으로 나머지 tab의 xcode project name을 세팅한다
						scwin.set_xcode_project_name = function () {
							let tabControl = $p.parent().project_add_step2_tap;

							if (tabControl.getTabCount() > 1) {
								for (let i = 1; i < tabControl.getTabCount(); i++) {
									let tabProjectNameTag = tabControl.getWindow(i).step2_ios_input_projectname;
									let xcodeProjectName = tabControl.getWindow(0).step2_ios_input_projectname.getValue();

									tabProjectNameTag.setDisabled("true");
									tabProjectNameTag.setValue(xcodeProjectName);
								}
							}
						}

						scwin.AndroidIconChange = function (e) {
							const file = e.target.files[0];
							let reader = new FileReader();
							reader.addEventListener("load", function () {
								$p.getComponentById("step2_icon_android").setSrc(this.result);
							}, false);
							if (file) {
								reader.readAsDataURL(file);
							}
						};

						scwin.iosIconChange = function (e) {
							const file = e.target.files[0];
							let reader = new FileReader();
							reader.addEventListener("load", function () {
								$p.getComponentById("step2_icon_ios").setSrc(this.result);
							}, false);
							if (file) {
								reader.readAsDataURL(file);
							}
						};

						scwin.stringToUint = function (str) {
							return new Uint8Array([].map.call(str, function (x) {
								return x.charCodeAt(0)
							}));
						};

						scwin.intToBytes = function (x) {
							let i = 8, bytes = [];

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
								const message = common.getLabel("lbl_uploading"); // Uploading
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
							const platformType = scwin.platformType;
							let app_id = "";
							let app_name = "";
							let app_version = "";
							let app_version_code = "";
							let profile_debug = "";
							let profile_release = "";
							let appIDCheck = "";
							let packageName = "";
							let message = "";

							const tempDataList = $p.parent().$p.parent().dtl_build_project_step2.getRowJSON(0);

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
								//iOS
								app_id = step2_ios_input_app_id.getValue();
								app_name = step2_ios_input_app_name.getValue();
								app_version = step2_ios_input_app_version.getValue();
								app_version_code = step2_ios_input_app_version_code.getValue();

							}

							const check_app_version_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION", app_version);
							const check_app_version_code_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION_CODE", app_version_code);

							const message_input_appid = common.getLabel("lbl_input_appid");
							const message_check_appid = common.getLabel("lbl_ask_appid_check");

							if (common.isEmptyStr(app_id)) {
								common.win.alert(message_input_appid);
								scwin.app_id_license_check_yn = false;
								return false;
							}

							if (tempDataList.app_id != "") {
								if (tempDataList.app_id != app_id) {
									common.win.alert(message_check_appid);
									scwin.app_id_license_check_yn = false;
									return false;
								}
							} else if (scwin.app_id_temp_save != "") {
								if (scwin.app_id_temp_save != app_id) {
									common.win.alert(message_check_appid);
									scwin.app_id_license_check_yn = false;
									return false;

								}
							}

							// 앱 라이센스 체크를 하세요.
							if (!scwin.app_id_license_check_yn) {
								common.win.alert(message_check_appid);
								return false;
							}

							appIDCheck = app_id.split("\.");

							const message_check_appid_form = common.getLabel("lbl_appid_check_form");
							const message_check_app_name = common.getLabel("lbl_check_app_name");

							// app id 체크
							if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
								for (let i = 0; i < appIDCheck.length; i++) {
									const strAppID = appIDCheck[i];

									const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

									if (!check_app_id_str) {
										common.win.alert(app_id + " : " + message_check_appid_form);
										return false;
									}
								}
							} else {
								common.win.alert(app_id + " : " + message_check_appid_form);
								return false;
							}

							scwin.app_id_temp_save = app_id;

							if (common.isEmptyStr(app_name)) {
								common.win.alert(message_check_app_name);
								return false;
							}

							// app name 자릿수 제한
							if (common.getCheckInputLength(app_name, app_name.length, 200)) {
								return false;
							}

							if (common.isEmptyStr(app_version)) {
								message = common.getLabel("lbl_check_app_version");
								common.win.alert(message);
								return false;
							}

							if (!check_app_version_str) {
								message = common.getLabel("lbl_app_version_form");
								common.win.alert(message);
								return false;
							}

							if (common.isEmptyStr(app_version_code)) {
								message = common.getLabel("lbl_check_app_version_code");
								common.win.alert(message);
								return false;
							}

							if ((!check_app_version_code_str) && (platformType != 'Windows')) {
								message = common.getLabel("lbl_app_version_code_form");
								common.win.alert(message);
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", app_version_code)) {
								message = common.getLabel("lbl_app_version_code_kor");
								common.win.alert(message);
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_ENG", app_version_code)) {
								message = common.getLabel("lbl_app_version_code_eng");
								common.win.alert(message);
								return false;
							}


							if (platformType == "Android") {
								packageName = step2_input_packagename.getValue();
							} else if (platformType == "iOS") {
								packageName = step2_ios_input_projectname.getValue();
							}

							// name, server, appid 정보
							if (common.isEmptyStr(packageName) && platformType == "Android") {
								message = common.getLabel("lbl_check_package_name");
								common.win.alert(message);
								return false;
							}

							if (common.isEmptyStr(packageName) && platformType == "iOS") {
								message = common.getLabel("lbl_check_package_name");
								common.win.alert(message);
								return false;
							}

							// packageName 자릿수 제한
							if (common.getCheckInputLength(packageName, packageName.length, 200)) {
								return false;
							}

							// 기존 step 3 화면 내 소스 코드 붙이기 ...
							const server_name_1 = input_servername_1.getValue();
							const app_id1 = input_appid_1.getValue();
							const server_URL1 = input_serverurl_1.getValue();

							let appIDCheck1 = "";
							let appIDCheckAll = "";


							// name, server, appid 정보
							// server name 빈칸
							if (common.isEmptyStr(server_name_1)) {
								message = common.getLabel("lbl_check_server_name");
								common.win.alert(message);
								return false;
							}

							// app id 빈칸
							if (common.isEmptyStr(app_id1)) {
								message = common.getLabel("lbl_input_appid");
								common.win.alert(message); //APPID를 입력하세요
								return false;
							}

							// server url 빈칸
							if (common.isEmptyStr(server_URL1)) {
								message = common.getLabel("lbl_project_add_app_config_js_check_server_url");
								common.win.alert(message); //server URL 입력하세요
								return false;
							}

							// server url 자릿수 제한
							if (common.getCheckInputLength(server_URL1, server_URL1.length, 200)) {

								return false;
							}

							// package name 한글 입력
							if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "Android") {
								message = common.getLabel("lbl_package_name_kor_rule");
								common.win.alert(packageName + " : " + message);
								return false;
							}

							// project name 한글 입력
							if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "iOS") {
								message = common.getLabel("lbl_package_name_kor_rule");
								common.win.alert(packageName + " : " + message);
								return false;
							}

							// server name 한글 입력
							if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", server_name_1)) {
								message = common.getLabel("lbl_server_name_kor_rule");
								common.win.alert(server_name_1 + " : " + message);
								return false;
							}

							// server url 빈칸
							if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", server_URL1)) {
								message = common.getLabel("lbl_project_add_app_config_js_check_server_url_kor");
								common.win.alert(server_URL1 + " : " + message); // server URL 형식에 한글은 입력할 수 없습니다
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "Android") {
								message = common.getLabel("lbl_package_name_special_char_rule");
								common.win.alert(packageName + " : " + message);
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "iOS") {
								message = common.getLabel("lbl_package_name_special_char_rule");
								common.win.alert(packageName + " : " + message);
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", server_name_1)) {
								message = common.getLabel("lbl_server_name_special_char_rule");
								common.win.alert(server_name_1 + " : " + message);
								return false;
							}

							appIDCheck1 = app_id1.split("\.");

							// appID 1 check
							if (appIDCheck1.length >= 3 || appIDCheck1.length >= 4) {
								for (let i = 0; i < appIDCheck1.length; i++) {
									const strAppID = appIDCheck1[i];

									const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

									if (!check_app_id_str) {
										common.win.alert(app_id + " : " + message_check_appid_form);
										return false;
									}
								}

							} else {
								common.win.alert(app_id1 + " : " + message_check_appid_form);
								return false;
							}

							for (let i = 0; i < serverinfo_itemIdx; i++) {

								let servertempJson = {};

								const server_name = $p.getComponentById("myid_name_input_" + i);
								const server_app_id = $p.getComponentById("myid_app_id_input_" + i);
								const server_url = $p.getComponentById("myid_server_url_input_" + i);

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
									message = common.getLabel("lbl_server_name_kor_rule");
									common.win.alert(servertempJson.Name + " : " + message); //server Name 형식에 한글은 입력할 수 없습니다
									return false;
								}

								// server url 빈칸
								if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", servertempJson.ServerURL)) {
									message = common.getLabel("lbl_project_add_app_config_js_check_server_url_kor");
									common.win.alert(servertempJson.ServerURL + " : " + message); //server URL 형식에 한글은 입력할 수 없습니다.
									return false;
								}

								if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", servertempJson.Name)) {
									message = common.getLabel("lbl_server_name_special_char_rule");
									common.win.alert(servertempJson.Name + " : " + message);
									return false;
								}

								if (server_app_id != null) {
									appIDCheckAll = servertempJson.AppID.split("\.");

									if (appIDCheckAll.length >= 3 || appIDCheckAll.length >= 4) {
										for (let i = 0; i < appIDCheckAll.length; i++) {
											const strAppID = appIDCheckAll[i];

											const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

											if (!check_app_id_str) {
												common.win.alert(servertempJson.AppID + " : " + message_check_appid_form); // APPID 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다.
												return false;
											}
										}
									} else {
										common.win.alert(servertempJson.AppID + " : " + message_check_appid_form); // APPID 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다.
										return false;
									}
								}
							}
							return true;
						};

						scwin.getMultiDomainInfo = async function () {
							const uri = common.uri.getInfo;
							const resData = await common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, JSON.stringify({"projectID": project_id}));
							if (resData != null && resData.length > 0) {
								selectedGeneralIosInfo.appId = data[0].ApplicationID;
							} else {
								common.win.alert("error: multiDomainInfo");
							}
						};

						scwin.saveStep2Data = function () {
							const platformType = scwin.platformType;

							let app_id;
							let app_name;
							let app_version;
							let app_version_code;


							let data = {};

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

								const uploader = document.getElementById("step2_android_input_icons_path");
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

								const uploader = document.getElementById("step2_ios_input_icons_path");
								data.zip_file_name = uploader.files[0] ? uploader.files[0] : "";

								data.test_url = "";

							}

							$p.parent().$p.parent().dtl_build_project_step2.setJSON([data]);

						};

						// ios는 인증서 생성 이후 data list 로 저장한다.
						scwin.btn_ios_signing_cli_onclick = function (e) {
							if (scwin.checkData()) {
								const platform = scwin.platformType;
								const step1DataList = $p.parent().$p.parent().dtl_build_project_step1.getAllJSON();

								let data = {};
								data.app_id = step2_ios_input_app_id.getValue();
								data.app_name = step2_ios_input_app_name.getValue();

								let beforeCertificates = document.getElementById("step2_ios_input_app_store_certificates");
								if (beforeCertificates.files.length === 0) {
									return;
								}

								let formData = new FormData();
								formData.append("file", beforeCertificates.files[0]);
								formData.append("app_id", data.app_id);
								formData.append("target_server", step1DataList[0].target_server);
								formData.append("app_name", data.app_name);
								formData.append("platform", platform);
								formData.append("signing_path", data.signing_path);
								formData.append("signing_issuer_id", data.signing_issuer_id);
								formData.append("signing_key_id", data.signing_key_id);
								formData.append("provisioning_profiles_name", data.provisioning_profiles_name);

								common.http.fetchFileUpload(common.uri.signingkeyfile, "POST", formData).then((res) => {
									common.win.alert("signing profile upload success");
								}).catch((err) => {
									common.win.alert("signing profile upload error:" + err);
								});
							}
							;
						};

						scwin.getStringUTF8 = function (dataview, offset, length) {
							let s = '';
							for (let i = 0, c; i < length;) {
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
							const provisioningProfile = document.getElementById("step2_android_input_keystore_path");
							const step1DataList = $p.parent().$p.parent().dtl_build_project_step1.getAllJSON();

							let formData = new FormData();
							formData.append("file", provisioningProfile.files[0]);
							formData.append("target_server", step1DataList[0].target_server);
							formData.append("platform", step1DataList[0].platform);

							let data = {};
							data.MsgType = "HV_BIN_MSG_PROJECT_PROVIONING_PROFILE_UPLOAD_INFO";
							data.sessType = "BRANCH";
							data.filename = "";

							data.platform = scwin.platformType;
							data.target_server = step1DataList[0].target_server;

							common.http.fetchFileUpload(common.uri.signingkeyupload, "POST", formData).then((res) => {
								common.win.alert("signing key upload success");
							}).catch((err) => {
								common.win.alert("signing key upload error:" + err);
							});
						};


						// license check
						scwin.select_check_app_id = async function (obj) {
							let uri = common.uri.checkLicense;
							common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, obj).then((resData) => {
								if (resData != null) {
									if (Array.isArray(resData) && resData[0].result == "success") {
										const message = common.getLabel("lbl_can_use_appid");
										common.win.alert(obj.appID + " : " + message); //APPID는 사용 가능 합니다.

										// flag 처리
										scwin.app_id_license_check_yn = true;
										$p.parent().$p.parent().scwin.app_id_license_check_yn = true;
										scwin.app_id_temp_save = obj.appID;
									} else {
										common.win.alert(resData[0].error);
									}
								} else {
									common.win.alert("checkLicense error: resData is null");
								}
							}).catch((err) => {
								common.win.alert("checkLicense error: " + err[0].result);
							});

						};

						scwin.btn_Android_prev_onclick = function (e) {
							$p.parent().$p.parent().scwin.selected_step(1);
						};

						scwin.btn_Android_next_onclick = function (e) {
							if (scwin.checkData()) {
								scwin.saveStep2Data();
								scwin.btn_project_step3_checkYn = true;
								$p.parent().$p.parent().scwin.selected_step(3);
							}
						};

						scwin.btn_iOS_prev_onclick = function (e) {
							$p.parent().$p.parent().scwin.selected_step(1);
						};

						scwin.btn_iOS_next_onclick = function (e) {
							if (scwin.checkData()) {
								scwin.saveStep2Data();
								scwin.btn_project_step3_checkYn = true;
								$p.parent().$p.parent().scwin.selected_step(3);
							}
						};

						scwin.btn_Windows_prev_onclick = function (e) {
							$p.parent().$p.parent().scwin.selected_step(1);
						};

						scwin.btn_Windows_next_onclick = function (e) {
							if (scwin.checkData()) {
								scwin.saveStep2Data();
								$p.parent().$p.parent().scwin.selected_step(3);
							}
						};

						scwin.btn_check_android_app_id_onclick = function (e) {
							let obj = {};
							let appID = step2_android_input_app_id.getValue();
							let appIDCheck = "";

							if (common.isEmptyStr(appID)) {
								const message = common.getLabel("lbl_input_appid");
								common.win.alert(message); //APP ID를 입력하세요
								scwin.app_id_license_check_yn = false;
								return false;
							}

							appIDCheck = appID.split("\.");
							const message_appid_form = common.getLabel("lbl_appid_check_form");

							if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
								for (let i = 0; i < appIDCheck.length; i++) {
									const strAppID = appIDCheck[i];

									const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

									if (!check_app_id_str) {
										common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
										return false;
									}
								}

							} else {
								common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
								return false;
							}

							obj.appID = appID;
							obj.platform = "Android";

							scwin.select_check_app_id(obj);
						};

						scwin.btn_check_ios_app_id_onclick = function (e) {
							let obj = {};
							let appID = step2_ios_input_app_id.getValue();
							let appIDCheck = "";
							if (common.isEmptyStr(appID)) {
								const message = common.getLabel("lbl_input_appid");
								common.win.alert(message); //APP ID를 입력하세요
								scwin.app_id_license_check_yn = false;
								return false;
							}
							appIDCheck = appID.split("\.");
							const message_appid_form = common.getLabel("lbl_appid_check_form");

							if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
								for (let i = 0; i < appIDCheck.length; i++) {
									const strAppID = appIDCheck[i];

									const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

									if (!check_app_id_str) {
										common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
										return false;
									}
								}

							} else {
								common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
								return false;
							}

							obj.appID = appID;
							obj.platform = "iOS";

							scwin.select_check_app_id(obj);
						};

						scwin.changeToolTipContentAddStep2 = function (componentId, label) {
							const message_tooltip_android_appid = common.getLabel("lbl_android_appid");
							const message_tooltip_android_app_name = common.getLabel("lbl_app_name");
							const message_tooltip_android_app_version = common.getLabel("lbl_app_version");
							const message_tooltip_android_app_version_code = common.getLabel("lbl_app_version_code");
							const message_tooltip_android_min_sdk = common.getLabel("lbl_min_os_version");
							const message_tooltip_ios_appid = common.getLabel("lbl_ios_appid");
							const message_tooltip_ios_app_name = common.getLabel("lbl_ios_app_name");
							const message_tooltip_ios_app_version = common.getLabel("lbl_ios_app_version");
							const message_tooltip_ios_app_version_code = common.getLabel("lbl_ios_app_version_code");
							const message_tooltip_ios_min_sdk = common.getLabel("lbl_ios_min_os_version");

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

						// 멀티 도메인 관련 자바스크립트
						scwin.generator_fixed_server_group_list = function (type) {
							let gen_idx;

							if (type == 'Android') {
								gen_idx = fixed_android_server_group_list.insertChild();
								fixed_android_server_group_list.getChild(gen_idx, "fixed_android_server_info").insertChild();
							} else {
								gen_idx = fixed_ios_server_group_list.insertChild();
								fixed_ios_server_group_list.getChild(gen_idx, "fixed_ios_server_info").insertChild();
							}
						}

						scwin.btn_fixed_server_info_add_onclick = function () {
							const platform = scwin.platformType;
							if (platform == 'Android') {
								fixed_android_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "fixed_android_server_info").insertChild(this.getGeneratedIndex() + 1);
							} else {
								fixed_ios_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "fixed_ios_server_info").insertChild(this.getGeneratedIndex() + 1);
							}
						}

						scwin.btn_fixed_server_info_remove_onclick = function () {
							const cmpGenerator = this.getGenerator();
							if (cmpGenerator.getLength() <= 1) {
								let message = common.getLabel("lbl_project_add_step02_app_config_minimum_server_info");
								common.win.alert(message);
								return;
							}
							const numIndex = this.getGeneratedIndex();
							cmpGenerator.removeChild(numIndex);
						}

						scwin.btn_server_info_add_onclick = function () {
							const platform = scwin.platformType;
							if (platform == 'Android') {
								android_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "android_server_info").insertChild(this.getGeneratedIndex() + 1);
							} else {
								ios_server_group_list.getChild(this.getGenerator().getGeneratedIndex(), "ios_server_info").insertChild(this.getGeneratedIndex() + 1);
							}
						};

						scwin.generator_server_group_list = function () {
							let platform = scwin.platformType;
							let gen_idx;
							let gen_cond;

							if (platform == 'Android') {
								gen_idx = android_server_group_list.insertChild();
								gen_cond = android_server_group_list.getChild(gen_idx, "android_server_info").insertChild();
							} else {
								gen_idx = ios_server_group_list.insertChild();
								gen_cond = ios_server_group_list.getChild(gen_idx, "ios_server_info").insertChild();
							}
							scwin.makeStartServerGroupSelectList();
						};

						scwin.makeStartServerGroupSelectList = function () {
							const platform = scwin.platformType;
							step2_ios_select_start_server_group.removeAll();
							step2_android_select_start_server_group.removeAll();

							let first_server_group = (platform == 'Android') ? fixed_android_server_group_list.getChild(0, 'fixed_input_android_server_group_name').getValue() : fixed_ios_server_group_list.getChild(0, 'fixed_input_ios_server_group_name').getValue();
							(platform == 'Android') ? step2_android_select_start_server_group.addItem(first_server_group, first_server_group) : step2_ios_select_start_server_group.addItem(first_server_group, first_server_group);

							const list = (platform == 'Android') ? android_server_group_list : ios_server_group_list;

							for (let i = 0; i < list.getLength(); i++) {
								const value = (platform == 'Android') ? android_server_group_list.getChild(i, 'input_android_server_group_name').getValue() : ios_server_group_list.getChild(i, 'input_ios_server_group_name').getValue();

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
								let message = common.getLabel("lbl_project_add_step02_app_config_minimum_server_info");
								common.win.alert(message);
								return;
							}
							// 자신의 반복부 index를 반환합니다.
							let numIndex = this.getGeneratedIndex();
							// index에 해당하는 반복부 아이템을 삭제합니다.
							cmpGenerator.removeChild(numIndex);
						};

						scwin.btn_serverInfo_add_onclick = function (e) {
							serverInfoGen.insertChild();
						};


						scwin.btn_serverinfo_remove_onclick = function (e) {
							const idx = this.getGeneratedIndex();
							serverInfoGen.removeChild(idx);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'grp_Android',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_android_app_default_info'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_appid',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_android_appid',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_id',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_check_android_app_id',style:'',type:'button',useLocale:'true',localeRef:'lbl_appid_check','ev:onclick':'scwin.btn_check_android_app_id_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_displayed_app_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_app_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_version',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_app_version_code',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_version_code',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_package_name',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_use_package_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_input_packagename',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_android_min_os_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step2_select_minsdk_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'21'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'21'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'22'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'22'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'23'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'23'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'24'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'24'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'25'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'25'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'26'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'26'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'27'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'27'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'28'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'28'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'29'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'29'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'30'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'30'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'31'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'31'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'APP Icon',ref:'',style:'',userData2:''}}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:group',A:{class:'upload_grp'},E:[{T:1,N:'input',A:{type:'file',id:'step2_android_input_icons_path',class:'w2upload_input',accept:'image/png'}},{T:1,N:'xf:group',A:{class:'appiconbox',id:'grp_android_app_icon_id',style:''},E:[{T:1,N:'xf:group',A:{class:'appicon_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'appicon_item w192',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{alt:'appicon이미지',id:'step2_icon_android',src:'/cm/images/contents/appicon_default_bg.svg',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w1024'}},{T:1,N:'w2:anchor',A:{class:'btn_cm icon btn_i_reset2',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'dfbox',id:'grp_iOS',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_ios_app_default_info'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_appid',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_id',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_check_ios_app_id',style:'',type:'button',useLocale:'true',localeRef:'lbl_appid_check','ev:onclick':'scwin.btn_check_ios_app_id_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_version',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_version_code',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_version_code',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_xcode_project_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_project_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_projectname',style:'width:100%;','ev:onchange':'scwin.set_xcode_project_name'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_min_os_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step2_select_target_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'12.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'12.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'13.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'13.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'14.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'14.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'15.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'15.0'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_rel_profile_name',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_rel_profile_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{id:'step2_select_profile_key_name',chooseOption:'true',style:'',submenuSize:'auto',allOption:'',disabled:'false',direction:'auto',appearance:'minimal',disabledClass:'w2selectbox_disabled',ref:'',chooseOptionLabel:'',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dtl_ios_profile'},E:[{T:1,N:'xf:label',A:{ref:'name'}},{T:1,N:'xf:value',A:{ref:'value'}}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_dev_profile_name',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_dev_profile_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'true',chooseOptionLabel:'',chooseOptionLabelLocaleRef:'lbl_select',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step2_select_debug_profile_key_name',ref:'',style:'',submenuSize:'auto'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dtl_ios_debug_profile'},E:[{T:1,N:'xf:label',A:{ref:'name'}},{T:1,N:'xf:value',A:{ref:'value'}}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_icon',ref:'',style:'',userData2:''}}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:group',A:{class:'upload_grp'},E:[{T:1,N:'input',A:{type:'file',id:'step2_ios_input_icons_path',class:'',style:'width:100%;',accept:'image/png'}},{T:1,N:'xf:group',A:{class:'appiconbox',id:'grp_ios_app_icon_id',style:''},E:[{T:1,N:'xf:group',A:{class:'appicon_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'appicon_item w192',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{alt:'appicon이미지',id:'step2_icon_ios',src:'/cm/images/contents/appicon_default_bg.svg',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',useLocale:'true',localeRef:'lbl_img_w1024',style:''}},{T:1,N:'w2:anchor',A:{class:'btn_cm icon btn_i_reset2',id:'',useLocale:'true',localeRef:'lbl_reset',outerDiv:'false',style:''},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'dfbox'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_project_add_step03_title'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_server_name',class:''}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_name',style:'',userData2:'',id:'',label:'',class:'ico_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_servername_1',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_appid',class:''}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_used_appid',style:'',userData2:'',id:'',label:'',class:'ico_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_appid_1',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_server_address',class:''}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_address',style:'',userData2:'',id:'',label:'',class:'ico_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_serverurl_1',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_serverInfo_add',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_serverInfo_add_onclick',useLocale:'true',localeRef:'lbl_add'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'genbox'},E:[{T:1,N:'w2:generator',A:{tagname:'ul',style:'',id:'serverInfoGen',class:''},E:[{T:1,N:'xf:group',A:{tagname:'li',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_server_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'ibx_servername',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_used_appid',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{style:'width:100%;',id:'ibx_appid',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_server_address'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_address',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'ibx_serverurl',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_serverinfo_remove',type:'button',class:'btn_cm icon btn_i_minus','ev:onclick':'scwin.btn_serverinfo_remove_onclick',useLocale:'true',localeRef:'lbl_del'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'dfbox',id:'grp_Windows',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_windows_app_default_info'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_version'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_op_mode'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]}]}]}]}]}]}]}]})