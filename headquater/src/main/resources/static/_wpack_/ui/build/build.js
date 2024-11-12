/*amd /cm/ui/build/build.xml 33915 69097f51f41a82ab06f2704e0b2fd20c0592723e585776ba5a61c8e41462b98a */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.logData = "";
						let interval;
						let classBtnYn = false;
						let isStop = false;
						let appConfigJSONData = {};
						let iosSchemeInfo = {};
						let selectedGeneralIosInfo = {};
						scwin.platformType = "";
						scwin.iosProfileData = {};

						scwin.onpageload = function () {
							$p.top().scwin._buildStatusFlag_ = "build_view";
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							const buildProjectName = buildActionData[0].projectName;
							const platform = buildActionData[0].platform;
							const product_type = buildActionData[0].product_type;
							scwin.platformType = platform;

							txt_build_project_name.setValue(buildProjectName + " " + common.getLabel("lbl_build"));
							btn_app_download.hide();
							btn_log_download.hide();
							btn_qrcode.hide();

							const message_select_default = common.getLabel("lbl_select");
							const message_select_debug = common.getLabel("lbl_build_js_select_debug");
							const message_select_release = common.getLabel("lbl_build_js_select_release");
							const message_select_apk = common.getLabel("lbl_build_js_select_apk");
							const message_select_aab = common.getLabel("lbl_build_js_select_aab");
							const message_select_aabdebug = common.getLabel("lbl_build_js_select_aabdebug");
							const message_select_aabrelease = common.getLabel("lbl_build_js_select_aabrelease");

							if (product_type.toLowerCase() == "wmatrix") {
								if (platform == "Android") {
									// profile list 조회 기능 호출 하는 구간
									scwin.startGetAllProfileList();
								} else {
									scwin.getiOSSigningProfile();
								}
								build_select_profiletype_group.show("");
								general_ios_scheme_selectbox_group.hide();
								general_ios_export_method_selectbox_group.hide();
							} else {
								scwin.generalAppGetInfo();
								build_select_profiletype_group.hide();

								if (platform.toLowerCase() == "ios") {
									general_ios_scheme_selectbox_group.show("");
									general_ios_export_method_selectbox_group.show("");
								} else {
									general_ios_scheme_selectbox_group.hide();
									general_ios_export_method_selectbox_group.hide();
								}
							}
							// platform : android, ios 기준으로 build type
							if (platform.toLowerCase() == "android") {
								build_select_buildtype.addItem("", message_select_default);
								build_select_buildtype.addItem(message_select_debug, message_select_apk + " " + message_select_debug);
								build_select_buildtype.addItem(message_select_release, message_select_apk + " " + message_select_release);
								build_select_buildtype.addItem(message_select_aabdebug, message_select_aab + " " + message_select_debug);
								build_select_buildtype.addItem(message_select_aabrelease, message_select_aab + " " + message_select_release);
							} else {
								build_select_buildtype.addItem("", message_select_default);
								//iOS는 target 조회후에 설정됨. 이름이 반대로 되어있음.
								// build_select_buildtype => 타겟명
								// build_select_profiletype => iOS signing profile 명
							}

							const buildFromProjSetting = $p.top().scwin._buildFromProjSetting_;
							if (buildFromProjSetting.toLowerCase() == "start") {
								scwin.start_build_project();
							}

							$(".btn_i_option").click(function () {
								$(".layer_pop").css("display", "block").css("width", "180px").css("height", "220px");
								$("body").append("<div class='dim'></div>");
							});
							$(".btn_pop_close").click(function () {
								$(".layer_pop").css("display", "none");
								$("div").remove(".dim");
							});
						};

						scwin.generalAppGetInfo = function () {
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							const projectId = buildActionData[0].project_pkid;

							const url = common.uri.getInfo;
							const method = 'POST';
							const headers = {'Content-Type': 'application/json'};
							const body = {"projectID": projectId};

							common.http.justFetch(url, method, headers, body);
						};

						scwin.getiOSSigningProfile = function () {
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							const projectId = buildActionData[0].project_pkid;
							common.http.fetchGet(common.uri.settingProjectConfig(projectId), "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								if (data !== null) {
									let datareq = {};
									datareq.key_id = data.key_id;
									const uri = common.uri.iosProfilesKeyName;
									common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, datareq).then((data) => {
										scwin.iosProfileData = data;
										const message = common.getLabel("lbl_select");

										build_select_profiletype.addItem("", message);
										for (let idx in data) {
											build_select_profiletype.addItem(data[idx].profiles_key_name, data[idx].profiles_key_name);
										}
									});
								}
							});
							// iOS target list call
							scwin.startGetAllProfileList();
						};

						scwin.startGetAllProfileList = function () {
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							const platform = buildActionData[0].platform;
							const projectId = buildActionData[0].project_pkid;
							const workspaceId = buildActionData[0].workspace_pkid;

							let data = {};
							data.platform = platform;
							data.projectID = parseInt(projectId);
							data.workspaceID = parseInt(workspaceId);

							const url = common.uri.allMultiProfileList;
							const method = "POST";
							const headers = {'Content-Type': 'application/json'};

							common.http.justFetch(url, method, headers, data);
						};

						scwin.start_build_project = function () {
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							const productType = buildActionData[0].product_type;
							const platform = buildActionData[0].platform;
							const projectId = buildActionData[0].project_pkid;
							const workspaceId = buildActionData[0].workspace_pkid;

							let sendDBdata = {};
							sendDBdata.workspaceID = parseInt(workspaceId);
							sendDBdata.projectID = parseInt(projectId);
							sendDBdata.status = common.getLabel("lbl_build_js_start_build");
							sendDBdata.log_path = "";
							sendDBdata.message = "";

							if (productType.toLowerCase() == "wmatrix") {
								if (build_select_buildtype.getValue() == "" || build_select_buildtype.getValue() == null) {
									const message = common.getLabel("lbl_build_js_choose_build_mode")
									common.win.alert(message);
									return false;
								}
								scwin.setBranchBuildInsert(sendDBdata);
							}
							// 일반 앱 빌드 시작
							else {
								scwin.setBranchBuildInsertGeneralApp(sendDBdata);
							}
						};

						scwin.start_build_project_onclick = function () {
							scwin.start_build_project();
						};

						scwin.setMultiProfileAppConfigDataStatus = function (data) {
							switch (data.message) {
								case "SEARCHING":
									const message = common.getLabel("lbl_build_js_creating_multi_profile");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "APPLOADING", "REMOVELOADING":
									break;
								case "FAILED":
									WebSquare.layer.hideProcessMessage();
									break;
								case "SUCCESSFUL":
									WebSquare.layer.hideProcessMessage();
									const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
									const platform = buildActionData[0].platform;
									const resultMultiProfileAppConfig = data.resultMultiProfileConfigListObj;
									let profileType = build_select_profiletype.getValue();

									if (platform.toLowerCase() == "android") {
										const tempResultOneProfile = resultMultiProfileAppConfig.getConfig.wmatrix.profiles;

										const oneProfileKey = Object.keys(tempResultOneProfile);
										for (let i = 0; i < oneProfileKey.length; i++) {
											let key = oneProfileKey[i];

											if (key == profileType.toLowerCase()) {
												const profileValueJson = Object.values(tempResultOneProfile);
												appConfigJSONData = profileValueJson[i];
											}
										}
									}

									if (platform.toLowerCase() == "ios") {
										const tempResultOneProfile = resultMultiProfileAppConfig.targets;
										profileType = build_select_buildtype.getValue();
										const oneProfileKey = Object.keys(tempResultOneProfile);
										for (let i = 0; i < oneProfileKey.length; i++) {
											let key = oneProfileKey[i];

											if (key.toLowerCase() == profileType.toLowerCase()) {
												let profileValueJson = Object.values(tempResultOneProfile);
												appConfigJSONData = profileValueJson[i].Release;
											}
										}
									}
									break;
								default:
									break;
							}
						};

						scwin.setMultiProfileListStatus = function (data) {
							switch (data.message) {
								case "SEARCHING":
									const message = common.getLabel("lbl_build_js_checking_multi_profile");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "ADDLOADING", "REMOVELOADING":
									break;
								case "FAILED":
									WebSquare.layer.hideProcessMessage();
									break;
								case "SUCCESSFUL":
									WebSquare.layer.hideProcessMessage();
									if (scwin.platformType == "iOS") {
										const resultMultiProfileArr = data.resultMultiProfileList;
										for (let i = 0; i < resultMultiProfileArr.length; i++) {
											build_select_buildtype.addItem(resultMultiProfileArr[i], resultMultiProfileArr[i]);
										}
									} else {
										const messageSelectDefault = common.getLabel("lbl_select");
										build_select_profiletype.addItem("", messageSelectDefault);

										const resultMultiProfileArr = data.resultMultiProfileList;

										for (let i = 0; i < resultMultiProfileArr.length; i++) {
											build_select_profiletype.addItem(resultMultiProfileArr[i], resultMultiProfileArr[i]);
										}
									}
									break;
								default:
									break;
							}
						};

						scwin.setActiveProfileStatus = function (data) {
							switch (data.message) {
								case "SETTINGS":
									const message = common.getLabel("lbl_build_js_setting_multi_profile");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "FAILED":
									WebSquare.layer.hideProcessMessage();
									break;
								default:
									break;
							}
						};

						scwin.setBranchBuildStatus = async function (data) {
							switch (data.message) {
								case "STOP" :
									break;
								case "BUILDING" :
									WebSquare.layer.hideProcessMessage();
									btn2_progress_img.changeClass("progress-30", "progress-45");

									const message_building = common.getLabel("lbl_building");
									project_build_build_result.setLabel(message_building);

									if (document.getElementById("build_status_text_id").value != message_building) {
										project_build_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
										document.getElementById("build_status_text_id").value = message_building;
									}

									scwin.setBranchBuildLog(data);
									break;
								case "FILEUPLOADING" :
									const message_uploading = common.getLabel("lbl_uploading");
									project_build_build_result.setLabel(message_uploading);
									project_build_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
									document.getElementById("build_status_text_id").value = message_uploading;

									btn2_progress_img.changeClass("progress-45", "progress-60");

									const message_app_uploading = common.getLabel("lbl_app_uploading");
									WebSquare.layer.showProcessMessage(message_app_uploading);
									break;
								case "APPCONFIG" :
									const message_app_info_setting = common.getLabel("lbl_app_info_setting");
									btn2_progress_img.changeClass("progress-60", "progress-75");
									project_build_build_result.setLabel(message_app_info_setting);
									document.getElementById("build_status_text_id").value = message_app_info_setting;

									project_build_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");

									WebSquare.layer.showProcessMessage(message_app_info_setting);
									break;
								case "GITPULL" :
									const message_git_pull = common.getLabel("lbl_git_pull");
									if (!(btn4.hasClass("active"))) {
										btn4.addClass("active");

										btn2_progress_img.changeClass("progress-0", "progress-15");
										project_build_build_result.setLabel(message_git_pull);
										project_build_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
										document.getElementById("build_status_text_id").value = message_git_pull;
										scwin.intervalYnfunc();
									}
									WebSquare.layer.showProcessMessage(message_git_pull);
									break;
								case "READAPPCONFIG" :
									const message_app_info_reading = common.getLabel("lbl_app_info_reading");
									WebSquare.layer.showProcessMessage(message_app_info_reading);
									project_build_build_result.setLabel(message_app_info_reading);
									project_build_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
									document.getElementById("build_status_text_id").value = message_app_info_reading;

									btn2_progress_img.changeClass("progress-15", "progress-30");
									break;
								case "GITPUSH" :
									const message_git_push = common.getLabel("lbl_git_push");
									project_build_build_result.setLabel(message_git_push);
									btn2_progress_img.changeClass("progress-75", "progress-90");
									document.getElementById("build_status_text_id").value = message_git_push;

									WebSquare.layer.showProcessMessage(message_git_push);
									break;
								case "SUCCESSFUL" :
									const message = common.getLabel("lbl_build_complete");
									isStop = true;
									project_build_build_result.setLabel(message);
									project_build_build_result.changeClass("btn_cm type3 stop", "btn_cm type2 stop");
									document.getElementById("build_status_text_id").value = message;

									btn2_progress_img.changeClass("progress-90", "progress-100");

									WebSquare.layer.hideProcessMessage();

									if (data.message == "SUCCESSFUL") {
										const message = common.getLabel("lbl_build_js_build_completed");
										await common.win.alert(data.buildHistoryObj.buildProjectName + " " + message);
									}

									const historyCntSuccess = data.history_id;
									project_build_start.setDisabled(true);
									btn_log_download.setUserData("history_id", historyCntSuccess);
									btn_log_download.show("");
									btn_app_download.setUserData("history_id", historyCntSuccess);
									btn_app_download.show("");

									scwin.logData = "";

									$p.top().scwin._buildFromProjSetting_ = "stop";

									const platform = $p.top().scwin.buildPlatform;
									if (platform != "Windows") {
										scwin.btn_qrcode_popup(data);
									}

									break;
								case "FAILED" :
									isStop = true;

									project_build_build_result.changeClass("btn_cm type3 stop", "btn_cm type2 stop");

									const message_fail = common.getLabel("lbl_build_failed");
									project_build_build_result.setLabel(message_fail);
									document.getElementById("build_status_text_id").value = message_fail;

									btn2_progress_img.changeClass("progress-90", "progress-100");

									WebSquare.layer.hideProcessMessage();
									const historyCntFailed = data.historyCnt;

									project_build_start.setDisabled(true);
									btn_log_download.setUserData("history_id", historyCntFailed);
									btn_log_download.show("");

									if (data.message == "FAILED") {
										const message_failed = common.getLabel("lbl_build_js_build_failed");
										common.win.alert(data.buildHistoryObj.buildProjectName + " " + message_failed);
									}

									scwin.logData = "";
									$p.top().scwin._buildFromProjSetting_ = "stop";
									break;
								case "CLEANBUILING" :
									scwin.setBranchBuildLog(data);
									break;
								default :
									break;
							}
						};

						scwin.setBranchBuildLog = function (data) {
							// txtarea_log
							let logData = data.logValue;
							let txtarea_log = document.getElementById("txtarea_log");
							txtarea_log.focus();
							scwin.logData += logData + "\n";

							txtarea_log.value = scwin.logData + "\n";
							txtarea_log.scrollTop = txtarea_log.scrollHeight - txtarea_log.clientHeight;
						};

						scwin.intervalYnfunc = function () {
							interval = setInterval(function () {
								if (!isStop) {
									if (classBtnYn) {
										project_build_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
										classBtnYn = false;
									} else {
										project_build_build_result.changeClass("btn_cm type3 stop", "btn_cm type2 stop");
										classBtnYn = true;
									}
								} else {
									clearInterval(interval);
									// 밖에서 선언한 interval을 안에서 중지시킬 수 있음
								}
							}, 500)
						};

						scwin.setBranchBuildInsert = function (data) {
							let sendData = {};
							let buildHistory = {};
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							const workspace_id = data.workspaceID;
							sendData.MsgType = "HV_MSG_RELEASE_BUILD";
							sendData.sessType = "BRANCH";
							sendData.workspace_id = workspace_id;
							sendData.productType = buildActionData[0].product_type;
							sendData.buildType = build_select_buildtype.getValue();
							if (buildActionData[0].platform == "iOS") {
								// project와매핑된 signing key profile name 추가
								sendData.profileKeyName = build_select_profiletype.getValue();
							}

							if (buildActionData[0].product_type.toLowerCase() == "wmatrix") {
								if (buildActionData[0].platform == "Android") {
									sendData.profileType = build_select_profiletype.getValue().toLowerCase();
								} else if (buildActionData[0].platform == "iOS") {
									sendData.profileType = build_select_buildtype.getValue();
									let profileKeyName = build_select_profiletype.getValue();
									const profiles = scwin.iosProfileData.filter((item) => item.profiles_key_name == profileKeyName);
									if (profiles.length > 0 && profiles[0].profiles_build_type == "development") {
										sendData.buildType = "Debug";
									} else {
										sendData.buildType = "Release";
									}
								}
							}

							sendData.appConfig = appConfigJSONData;

							buildHistory.project_id = data.projectID;
							buildHistory.platform = buildActionData[0].platform;
							buildHistory.target_server_id = "";
							buildHistory.project_name = buildActionData[0].projectName;
							buildHistory.status = data.status;
							buildHistory.status_log = common.getLabel("lbl_sending_eng");
							buildHistory.log_path = data.log_path;
							buildHistory.result = data.message;
							buildHistory.platform_build_file_path = "";
							buildHistory.platform_build_file_name = "";
							buildHistory.qrcode = "http://...";

							if (buildActionData[0].platform.toLowerCase() == 'ios') {
								buildHistory.ios_builded_target_or_bundle_id = build_select_buildtype.getValue();
							}

							sendData.buildHistory = buildHistory;

							const url = common.uri.buildHistoryCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=UTF-8"};

							common.http.fetch(url, method, headers, sendData)
								.then(res => {
									if (Array.isArray(res)) {
										if (res[0].result === "success") {
											scwin.setBuildingScreen();
										} else if (res[0].result == "server_error") {
											common.win.alert(res[0].error);
										}
									}

								})
								.catch(err => {
									console.log(err);
								});
						};

						scwin.setBranchBuildInsertGeneralApp = function (data) {
							let sendData = {};
							let buildHistory = {};
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();

							sendData.MsgType = "HV_MSG_GENERAL_APP_RELEASE_BUILD";
							sendData.sessType = "BRANCH";
							sendData.workspace_id = data.workspaceID;
							sendData.productType = buildActionData[0].product_type;
							sendData.buildType = build_select_buildtype.getValue();
							sendData.appConfig = selectedGeneralIosInfo;

							buildHistory.project_id = data.projectID;
							buildHistory.platform = buildActionData[0].platform;
							buildHistory.target_server_id = "";
							buildHistory.project_name = buildActionData[0].projectName;
							buildHistory.status = data.status;
							buildHistory.status_log = common.getLabel("lbl_sending_eng");
							buildHistory.log_path = data.log_path;
							buildHistory.result = data.message;
							buildHistory.platform_build_file_path = "";
							buildHistory.platform_build_file_name = "";
							buildHistory.qrcode = "http://...";

							if (buildActionData[0].platform.toLowerCase() == 'ios') {
								buildHistory.ios_builded_target_or_bundle_id = selectedGeneralIosInfo.appId;
							}

							sendData.buildHistory = buildHistory;

							const url = common.uri.generalBuildHistoryCreate
							const method = 'POST';
							const headers = {'Content-Type': 'application/json'};

							common.http.fetch(url, method, headers, sendData)
								.then(res => {
									if (Array.isArray(res)) {
										if (res[0].result === "success") {
											scwin.setBuildingScreen();
										} else if (res[0].result == "server_error") {
											common.win.alert(res[0].error);
										}
									}
								})
								.catch(err => {
									console.log(err);
								});
						};

						scwin.setBuildingScreen = function () {
							// build 버튼 disabled
							project_build_start.setDisabled(true);
							build_select_buildtype.setDisabled(true);
							build_select_profiletype.setDisabled(true);
							general_ios_scheme_selectbox.setDisabled(true);
							general_ios_export_method_selectbox.setDisabled(true);

							project_build_start.changeClass("btn_cm type1 redo", "btn_cm type2 stop");

							$p.top().scwin._buildStatusFlag_ = "building";
						}

						// 빌드 완료, 빌드 실패시 db update 수행
						scwin.buildAfterQrcodeCreate = function (qrcodeUrl) {
							const qrcode = new QRCode(document.getElementById("build_qrcode"), {
								text: qrcodeUrl,
								width: 150,
								height: 150,
								colorDark: "#000000",
								colorLight: "#ffffff",
								correctLevel: QRCode.CorrectLevel.H
							});
						};

						scwin.buildAfterQrcodeCreateByID = (qrcodeID) => {
							const qrcode = new QRCode(document.getElementById("build_qrcode"), {
								text: common.uri.qrcode(qrcodeID),
								width: 150,
								height: 150,
								colorDark: "#000000",
								colorLight: "#ffffff",
								correctLevel: QRCode.CorrectLevel.H
							});
						}

						scwin.onpageunload = function () {
							scwin.logData = "";

							$p.top().scwin._buildStatusFlag_ = "build_back";
							$p.top().scwin._buildFromProjSetting_ = "stop";
						};

						/*
                            빌드완료 후, App Download 버튼을 누르면,
                            웹서버에서 해당 apk or ipa 파일을 다운 받는다.
                         */
						scwin.btn_app_download_onclick = function () {
							const history_id = this.getUserData("history_id");

							const url = common.uri.appDownload;
							const method = 'POST';
							const headers = {'Content-Type': 'application/json'};
							const body = {'history_id': history_id};

							common.http.fileDownload(url, method, headers, body);
						}

						scwin.getGeneralIOSAPPInfo = function (data) {
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();

							switch (data.message) {
								case "GETTING INFO":
									const message = common.getLabel("lbl_getting_info");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "DONE":
									WebSquare.layer.hideProcessMessage();
									if (buildActionData[0].platform.toLowerCase() == "ios") {
										iosSchemeInfo = JSON.parse(data.data);
										scwin.makeiOSBuildSelectBox(iosSchemeInfo);
									}
									break;
								default:
									break;
							}
						};

						scwin.makeiOSBuildSelectBox = function (data) {
							general_ios_scheme_selectbox.removeAll();
							const message_select_default = common.getLabel("lbl_select");
							general_ios_scheme_selectbox.addItem("", message_select_default);
							for (let i = 0; i < data.length; i++) {
								general_ios_scheme_selectbox.addItem(data[i].scheme, data[i].scheme);
							}
						};

						scwin.general_ios_scheme_selectbox_onchange = function (data) {
							build_select_buildtype.removeAll();

							const message_select_default = common.getLabel("lbl_select");
							build_select_buildtype.addItem("", message_select_default);

							for (let i = 0; i < iosSchemeInfo.length; i++) {
								if (iosSchemeInfo[i].scheme == data.newValue) {
									const configData = iosSchemeInfo[i].configuration;
									for (let j = 0; j < configData.length; j++) {
										build_select_buildtype.addItem(configData[j].name, configData[j].name);
									}
								}
							}
						};

						scwin.build_select_buildtype_onchange = function () {
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();
							selectedGeneralIosInfo.buildType = build_select_buildtype.getValue();

							if (buildActionData[0].platform.toLowerCase() == 'ios') {
								selectedGeneralIosInfo.exportMethod = general_ios_export_method_selectbox.getValue();
								selectedGeneralIosInfo.scheme = general_ios_scheme_selectbox.getValue();
								for (let i = 0; i < iosSchemeInfo.length; i++) {
									if (iosSchemeInfo[i].scheme == selectedGeneralIosInfo.scheme) {
										const configData = iosSchemeInfo[i].configuration;
										for (let j = 0; j < configData.length; j++) {
											if (configData[j].name == selectedGeneralIosInfo.buildType) {
												selectedGeneralIosInfo.appId = configData[j].applicationID;
												selectedGeneralIosInfo.version = configData[j].version;
												selectedGeneralIosInfo.versionCode = configData[j].versionCode;
											}
										}
									}
								}
							}
						};

						scwin.start_log_download_onclick = function () {
							const history_id = this.getUserData("history_id");

							let data = {};
							data.history_id = history_id;

							const url = common.uri.logDonwload;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.justFetch(url, method, headers, data);
						};

						scwin.setBuilderAppDownloadHref = function (data) {
							// app file 다운로드
							window.open(common.uri.appDownloadHref(data));
						};

						scwin.build_select_profiletype_onchange = async function (e) {
							const profileStr = build_select_profiletype.getValue();
							const buildActionData = $p.parent().__buildaction_data__.getAllJSON();

							const platform = buildActionData[0].platform;
							const project_id = buildActionData[0].project_pkid;
							const workspace_id = buildActionData[0].workspace_pkid;

							let data = {};
							data.platform = platform;
							data.projectID = parseInt(project_id);
							data.workspaceID = parseInt(workspace_id);
							if (platform == "Android") {
								data.profileType = profileStr;
							} else {
								let targetName = build_select_buildtype.getValue();
								if (!!targetName) {
									data.profileType = targetName;
								} else {
									if (await common.win.alert("build type을 먼저 선택해야 합니다.")) {
										return false;
									}
								}
							}

							const url = common.uri.getMultiProfileAppConfig;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.justFetch(url, method, headers, data);
						};

						scwin.btn_qrcode_popup = function (data) {
							const rowJSON = {"data": data};

							const dataObject = {
								"type": "json",
								"name": "param",
								"data": rowJSON
							};

							const opts = {
								"id": "popup_window_qrcode",
								"type": "litewindow",
								"width": 350 + "px",
								"height": 350 + "px",
								"popupName": " ",
								"modal": true,
								"useIFrame": false,
								"title": "",
								"useATagBtn": "true",
								"frameMode": "wframe",
								"dataObject": dataObject
							};

							$p.openPopup("/ui/build/QRCode.xml", opts);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'txt_build_project_name',label:'Project Build',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_build_project_name'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{class:'titbox type2',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',class:'flex',style:'flex-flow:row;width:350px;'},E:[{T:1,N:'xf:group',A:{'ev:onclick':'scwin.btn4_onclick',style:'width: 100%;max-width: 300px;',id:'btn4',class:'progress-btn'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:data-progress-style',E:[{T:3,text:'indefinite'}]}]},{T:1,N:'xf:group',A:{class:'btn',id:'btn_progress_text',style:''}},{T:1,N:'xf:group',A:{class:'progress-0',id:'btn2_progress_img',style:''}}]},{T:1,N:'input',A:{id:'build_status_text_id',type:'hidden'}},{T:1,N:'xf:trigger',A:{class:'btn_cm type2 stop',id:'project_build_build_result',useLocale:'true',localeRef:'lbl_ready',style:'margin-left: 10px;',type:'button'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt buildconsol_box'},E:[{T:1,N:'xf:group',A:{id:'general_ios_scheme_selectbox_group',class:'flex'},E:[{T:1,N:'w2:textbox',A:{tagname:'span',localeRef:'lbl_ios_scheme',style:'text-wrap:nowrap;',id:'txt_general_ios_scheme_selectbox',label:'',class:'nowrap',useLocale:'true'}},{T:1,N:'xf:select1',A:{submenuSize:'auto','ev:onchange':'scwin.general_ios_scheme_selectbox_onchange',labelWidthAuto:'true',chooseOption:'',allOption:'',displayMode:'label',ref:'',appearance:'minimal',disabledClass:'w2selectbox_disabled',disabled:'false',style:'width: 100px;',id:'general_ios_scheme_selectbox',renderType:'native',direction:'auto'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'build_select_buildtype_group',class:'flex'},E:[{T:1,N:'w2:textbox',A:{tagname:'span',localeRef:'lbl_build_build_type',style:'text-wrap:nowrap;',id:'txt_build_select_buildtype',label:'',class:'nowrap',useLocale:'true'}},{T:1,N:'xf:select1',A:{submenuSize:'auto','ev:onchange':'scwin.build_select_buildtype_onchange',labelWidthAuto:'true',chooseOption:'',allOption:'',displayMode:'label',ref:'',appearance:'minimal',disabledClass:'w2selectbox_disabled',disabled:'false',style:'width: 100px;',id:'build_select_buildtype',renderType:'native',direction:'auto'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'build_select_profiletype_group',class:'flex'},E:[{T:1,N:'w2:textbox',A:{tagname:'span',localeRef:'lbl_build_profile_type',style:'text-wrap:nowrap;',id:'txt_build_select_profiletype',label:'',class:'nowrap',useLocale:'true'}},{T:1,N:'xf:select1',A:{submenuSize:'auto',labelWidthAuto:'true','ev:onchange':'scwin.build_select_profiletype_onchange',chooseOption:'',allOption:'',displayMode:'label',ref:'',appearance:'minimal',disabledClass:'w2selectbox_disabled',disabled:'false',style:'width: 100px;',id:'build_select_profiletype',renderType:'native',direction:'auto'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'general_ios_export_method_selectbox_group',class:'flex'},E:[{T:1,N:'w2:textbox',A:{tagname:'span',localeRef:'lbl_distribution_setting',style:'text-wrap:nowrap;',id:'txt_general_ios_export_method_selectbox',label:'',class:'nowrap',useLocale:'true'}},{T:1,N:'xf:select1',A:{submenuSize:'auto',useItemLocale:'true',labelWidthAuto:'true',chooseOption:'true',allOption:'',displayMode:'label',ref:'',appearance:'minimal',disabledClass:'w2selectbox_disabled',chooseOptionLabelLocaleRef:'lbl_select',disabled:'false',style:'width: 100px;',id:'general_ios_export_method_selectbox',renderType:'native',direction:'auto',useLocale:'true','ev:onchange':'scwin.build_select_buildtype_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_development'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'development'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_appstore'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'app-store'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_enterprise'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'enterprise'}]}]}]}]}]},{T:1,N:'xf:group',E:[{T:1,N:'xf:trigger',A:{'ev:onclick':'scwin.start_build_project_onclick',localeRef:'lbl_build',style:'',id:'project_build_start',type:'button',class:'btn_cm type1 redo',useLocale:'true'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{outerDiv:'false','ev:onclick':'scwin.start_log_download_onclick',localeRef:'lbl_log_download',style:'display:none;',id:'btn_log_download',href:'',type:'button',class:'btn_cm type2 type4',useLocale:'true'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{'ev:onclick':'scwin.btn_app_download_onclick',localeRef:'lbl_app_download',style:'',id:'btn_app_download',type:'button',class:'btn_cm',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'textarea',A:{class:'',id:'txtarea_log',placeholder:'',style:'width:100%;height: 600px;',readonly:'true'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'pgtbox'},E:[{T:1,N:'xf:trigger',A:{id:'btn_qrcode',style:'',class:'btn_cm icon qrcode',type:'button','ev:onclick':'scwin.btn_qrcode_onclick',useLocale:'true',localeRef:'lbl_build_qrcode'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'w2:floatingLayer',A:{id:'resultLayer',title:'',style:'position:absolute; display:none;width:190px; height:185px; z-index:9999;','ev:onclose':'scwin.resultLayer_onclose',useModal:'false',useLocale:'true',localeRef:'lbl_build_result_layer_title'},E:[{T:1,N:'div',A:{id:'qrcode',style:'height:150px;'}}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_build_qrcode'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocal:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''}}]}]}]}]})