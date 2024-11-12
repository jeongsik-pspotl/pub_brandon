/*amd /xml/build.xml 39069 e3cd4c6ae9541ff355fd0c43898665f98d6e66eaca42be6b224c87d6da47fa33 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/btn.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.logData = "";
			var interval;
			var classBtnYn = false;
			var isStop = false;
			var appConfigJSONData = {};
			var iosSchemeInfo = {};
			var selectedGeneralIosInfo = {};
			scwin.onpageload = function () {
				// 현재 나의 scope 객체를 전역 변수에 저장한다.
				common.setScopeObj(scwin);
				localStorage.setItem("_buildStatusFlag_", "build_view"); // true
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();
				//console.log(buildActionData);
				var buildProjectName = buildActionData[0].projectName;
				var platform = buildActionData[0].platform;
				var product_type = buildActionData[0].product_type;

				txt_build_project_name.setValue(buildProjectName + " " + common.getLabel("lbl_build"));
				btn_app_download.hide();
				btn_log_download.hide();
				btn_qrcode.hide();

				// project_build_status.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");

				var message_select_default = common.getLabel("lbl_select");
				var message_select_debug = common.getLabel("lbl_build_js_select_debug");
				var message_select_release = common.getLabel("lbl_build_js_select_release");
				var message_select_apk = common.getLabel("lbl_build_js_select_apk");
				var message_select_aab = common.getLabel("lbl_build_js_select_aab");
				var message_select_aabdebug = common.getLabel("lbl_build_js_select_aabdebug");
				var message_select_aabrelease = common.getLabel("lbl_build_js_select_aabrelease");

				if (product_type.toLowerCase() == "wmatrix") {
					// profile list 조회 기능 호출 하는 구간
					scwin.start_get_all_profile_list();
					build_select_profiletype.show();
					build_select_profiletype_txtbox.show();
					general_ios_scheme_selectbox.hide();
					general_ios_scheme_selectbox_label.hide();
					general_ios_export_method_selectbox.hide();
					general_ios_export_method_selectbox_label.hide();
				} else {
					scwin.generalAppGetInfo();
					build_select_profiletype.hide();
					build_select_profiletype_txtbox.hide();

					if (platform.toLowerCase() == "ios") {
						general_ios_scheme_selectbox.show();
						general_ios_scheme_selectbox_label.show();
						general_ios_export_method_selectbox.show();
                        general_ios_export_method_selectbox_label.show();
						general_ios_export_method_selectbox.show();
						general_ios_export_method_selectbox_label.show();
					} else {
						general_ios_scheme_selectbox.hide();
						general_ios_scheme_selectbox_label.hide();
						general_ios_export_method_selectbox.hide();
						general_ios_export_method_selectbox_label.hide();
						general_ios_export_method_selectbox.hide();
						general_ios_export_method_selectbox_label.hide();
					}
				}
				// platform : android, ios 기준으로 build type
				if (platform == "Android") {
					build_select_buildtype.addItem("", message_select_default);
					build_select_buildtype.addItem(message_select_debug, message_select_apk + " " + message_select_debug);
					build_select_buildtype.addItem(message_select_release, message_select_apk + " " + message_select_release);
					build_select_buildtype.addItem(message_select_aabdebug, message_select_aab + " " + message_select_debug);
					build_select_buildtype.addItem(message_select_aabrelease, message_select_aab + " " + message_select_release);
				} else {
					// build_select_buildtype.hide();
					build_select_buildtype.addItem("", message_select_default);
					build_select_buildtype.addItem(message_select_debug, message_select_debug);
					build_select_buildtype.addItem(message_select_release, message_select_release);
				}

				var buildFromProjSetting = localStorage.getItem("_buildFromProjSetting_");
				if (buildFromProjSetting == "start") {
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
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();
				var project_id = buildActionData[0].project_pkid;

				(async () => {
					try {
						await fetch('/manager/general/history/getInfo', {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
							body: JSON.stringify({"projectID": project_id})
						})
						.then(response => response.json())
						.then(data => {
                            if (data.length > 0) {
                            	selectedGeneralIosInfo.appId = data[0].ApplicationID;
                            }
						});
					} catch (e) {
						console.log("Error: ", e);
					}
				})();
			};

			scwin.start_get_all_profile_list = function () {

				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();

				var platform = buildActionData[0].platform;
				var project_id = buildActionData[0].project_pkid; // parseInt
				var workspace_id = buildActionData[0].workspace_pkid; // parseInt

				var data = {};

				data.platform = platform;
				data.projectID = parseInt(project_id);
				data.workspaceID = parseInt(workspace_id);

				var options = {};

				options.action = "/manager/build/history/allMultiProfileList";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "POST";

				options.success = function (e) {

				};

				options.error = function (e) {
					if (request.status == 200) {

					}
				};
				$p.ajax(options);
			};

			scwin.start_build_project = function (e) {
				// workspace_id, build_id
				// var platform = localStorage.getItem("buildPlatform");
				// var project_id = localStorage.getItem("buildProjectId"); // parseInt
				// var workspace_id = localStorage.getItem("workspaceId"); // parseInt

				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();
				var product_type = buildActionData[0].product_type;
				var platform = buildActionData[0].platform;
				var project_id = buildActionData[0].project_pkid; // parseInt
				var workspace_id = buildActionData[0].workspace_pkid; // parseInt

				var sendDBdata = {};

				sendDBdata.workspaceID = parseInt(workspace_id);
				sendDBdata.projectID = parseInt(project_id);
				sendDBdata.status = common.getLabel("lbl_build_js_start_build");
				sendDBdata.log_path = "";
				sendDBdata.message = "";

				if (product_type.toLowerCase() == "wmatrix") {
					// console.log("platform : " + platform);
					if (build_select_buildtype.getValue() == "" || build_select_buildtype.getValue() == null) {
						var message = common.getLabel("lbl_build_js_choose_build_mode")
						alert(message);
						return false;
					}
					scwin.setBranchBuildInsert(sendDBdata);
				}
				// 일반 앱 빌드 시작
				else {
					scwin.setBranchBuildInsertGeneralApp(sendDBdata);
				}
			};

			scwin.initPage = function () {

			};

			scwin.webSocketCallback = function (obj) {
				switch (obj.MsgType) {
					case "HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER":
						scwin.setBranchBuildStatus(obj);
						break;
					case "HV_MSG_APP_DOWNLOAD_STATUS_INFO_FROM_HEADQUATER":
						scwin.setBuilderAppDownloadHref(obj);
						break;
					case "HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO_FROM_HEADQUATER":
						scwin.setMultiProfileListStatus(obj);
						break;
					case "HV_MSG_BUILD_SET_ACTIVE_PROFILE_INFO_FROM_HEADQUATER":
						scwin.setActiveProfileStatus(obj);
						break;
					case "HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO_FROM_HEADQUATER":
						scwin.setMultiProfileAppConfigDataStatus(obj);
						break;
					case "HV_MSG_BUILD_GENERAL_APP_INFO_FROM_HEADQUATER":
						scwin.getGeneralIOSAPPInfo(obj);
					default :
						break;
				}
			};

			scwin.setMultiProfileAppConfigDataStatus = function (data) {
				if (data.message == "SEARCHING") {
					// project_build_status.setLabel("빌드중");
					var message = common.getLabel("lbl_build_js_creating_multi_profile");
					WebSquare.layer.showProcessMessage(message);

				} else if (data.message == "ADDLOADING") {

				} else if (data.message == "REMOVELOADING") {

				} else if (data.message == "SUCCESSFUL") {
					//console.log(data);
					//console.log(data.resultMultiProfileList);
					WebSquare.layer.hideProcessMessage();
					var buildActionData = $p.parent().__buildaction_data__.getAllJSON();

					var platform = buildActionData[0].platform;

					var resultMultiProfileAppConfig = data.resultMultiProfileConfigListObj;
					var profileType = build_select_profiletype.getValue();
					if (platform == "Android") {
						var tempResultOneProfile = resultMultiProfileAppConfig.getConfig.wmatrix.profiles;
						console.log(tempResultOneProfile);
						var oneProfileKey = Object.keys(tempResultOneProfile);
						for (var i = 0; i < oneProfileKey.length; i++) {
							var key = oneProfileKey[i];

							if (key == profileType.toLowerCase()) {
								var profileValueJson = Object.values(tempResultOneProfile);
								appConfigJSONData = profileValueJson[i];

							} else {

							}
						}
					} else if (platform == "iOS") {
						var tempResultOneProfile = resultMultiProfileAppConfig.targets;

						var oneProfileKey = Object.keys(tempResultOneProfile);
						for (var i = 0; i < oneProfileKey.length; i++) {
							var key = oneProfileKey[i];

							if (key == profileType) {
								var profileValueJson = Object.values(tempResultOneProfile);
								appConfigJSONData = profileValueJson[i].Release;

							} else {

							}
						}
					}
				} else if (data.message == "FAILED") {
					WebSquare.layer.hideProcessMessage();
				}
			};

			scwin.setMultiProfileListStatus = function (data) {

				if (data.message == "SEARCHING") {
					// project_build_status.setLabel("빌드중");
					var message = common.getLabel("lbl_build_js_checking_multi_profile");
					WebSquare.layer.showProcessMessage(message);

				} else if (data.message == "ADDLOADING") {

				} else if (data.message == "REMOVELOADING") {

				} else if (data.message == "SUCCESSFUL") {
					//console.log(data);
					//console.log(data.resultMultiProfileList);
					WebSquare.layer.hideProcessMessage();

					var message_select_default = common.getLabel("lbl_select");
					build_select_profiletype.addItem("", message_select_default);

					var resultMultiProfileArr = data.resultMultiProfileList;

					for (var i = 0; i < resultMultiProfileArr.length; i++) {
						build_select_profiletype.addItem(resultMultiProfileArr[i], resultMultiProfileArr[i]);
					}
				} else if (data.message == "FAILED") {
					WebSquare.layer.hideProcessMessage();
				}
			};

			scwin.setActiveProfileStatus = function (data) {
				if (data.message == "SETTINGS") {
					// project_build_status.setLabel("빌드중");
					var message = common.getLabel("lbl_build_js_setting_multi_profile");
					WebSquare.layer.showProcessMessage(message);

				} else if (data.message == "SUCCESSFUL") {

				} else if (data.message == "FAILED") {
					WebSquare.layer.hideProcessMessage();
				}
			}

			scwin.setBranchBuildStatus = function (data) {
				switch (data.message) {
					case "STOP" :
						alert(data.logValue);
						break;
					case "BUILDING" :
						WebSquare.layer.hideProcessMessage();
						// 깜박임

						btn2_progress_img.changeClass("progress-30", "progress-45");
						// _this.removeClass("active");

						// btn_cm type2 icon stop
						// project_build_clean_build.changeClass("txt_name fail","txt_name success");
						//project_build_building.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						//project_build_clean_build.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						//project_build_status.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");

						var message_building = common.getLabel("lbl_building");
						project_build_build_result.setLabel(message_building);

						if (document.getElementById("build_status_text_id").value == message_building) {

						} else {
							project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
							document.getElementById("build_status_text_id").value = message_building;
						}

						scwin.setBranchBuildLog(data);
						break;
					case "FILEUPLOADING" :
						//project_build_building.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						//project_build_ftp_upload.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						var message_uploading = common.getLabel("lbl_uploading");
						project_build_build_result.setLabel(message_uploading);
						project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
						document.getElementById("build_status_text_id").value = message_uploading;

						btn2_progress_img.changeClass("progress-45", "progress-60");
						// _this.removeClass("active");

						var message_app_uploading = common.getLabel("lbl_app_uploading");
						WebSquare.layer.showProcessMessage(message_app_uploading);
						break;
						//
					case "APPCONFIG" :
						var message_app_info_setting = common.getLabel("lbl_app_info_setting");
						btn2_progress_img.changeClass("progress-60", "progress-75");
						project_build_build_result.setLabel(message_app_info_setting);
						document.getElementById("build_status_text_id").value = message_app_info_setting;

						project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");

						// _this.removeClass("active");

						WebSquare.layer.showProcessMessage(message_app_info_setting);
						break;
					case "GITPULL" :
						var message = common.getLabel("lbl_git_pull");
						if (!(btn4.hasClass("active"))) {
							btn4.addClass("active");

							btn2_progress_img.changeClass("progress-0", "progress-15");
							project_build_build_result.setLabel(message);
							project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
							document.getElementById("build_status_text_id").value = message;
							intervalYnfunc();
							// _this.removeClass("active");
						}
						WebSquare.layer.showProcessMessage(message);
						break;
					case "READAPPCONFIG" :
						var message = common.getLabel("lbl_app_info_reading");
						WebSquare.layer.showProcessMessage(message);
						project_build_build_result.setLabel(message);
						project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
						document.getElementById("build_status_text_id").value = message;

						btn2_progress_img.changeClass("progress-15", "progress-30");
						// _this.removeClass("active");

						break;
					case "GITPUSH" :
						var message = common.getLabel("lbl_git_push");
						project_build_build_result.setLabel(message);
						btn2_progress_img.changeClass("progress-75", "progress-90");
						document.getElementById("build_status_text_id").value = message;
						// _this.removeClass("active");

						WebSquare.layer.showProcessMessage(message);
						break;
					case "SUCCESSFUL" :
						var message = common.getLabel("lbl_build_complete");
						isStop = true;
						project_build_build_result.setLabel(message);
						// project_build_ftp_upload.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						project_build_build_result.changeClass("btn_cm type3 icon stop", "btn_cm type2 icon stop");
						project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
						document.getElementById("build_status_text_id").value = message;

						btn2_progress_img.changeClass("progress-90", "progress-100");
						// _this.removeClass("active");

						WebSquare.layer.hideProcessMessage();
						// scwin.setBranchBuildUpdate(data);

						if (data.message == "SUCCESSFUL") {
							var message = common.getLabel("lbl_build_js_build_completed");
							alert(data.buildHistoryObj.buildProjectName + " " + message);

							// $(".layer_pop").css("display","block").css("width","180px").css("height","220px");
							// $("body").append("<div class='dim'></div>");

						} else if (data.message == "FAILED") {
							var message = common.getLabel("lbl_build_js_build_failed");
							alert(data.buildHistoryObj.buildProjectName + " " + message);
						}

						var historyCnt = data.history_id;

						project_build_start.setDisabled(true);
						btn_log_download.setUserData("history_id", historyCnt);
						btn_log_download.show("");
						// btn_app_download.setUserData("history_id",data.historyCnt);
						// btn_app_download.setHref("/api/buildhistory/fileDownload/" + parseInt(historyCnt));
						btn_app_download.setUserData("history_id", historyCnt);
						btn_app_download.show("");
						//resultLayer.show();
						scwin.logData = "";

						localStorage.setItem("_buildFromProjSetting_", "stop");

						var platform = localStorage.getItem("buildPlatform");
						// console.log("platform : " + platform);
						if (platform == "Windows") {

						} else {
							// scwin.buildAfterQrcodeCreate(data.qrCode);
							scwin.btn_qrcode_popup(data);
							// scwin.buildAfterQrcodeCreateByID(historyCnt);
						}

						break;
					case "FAILED" :
						isStop = true;
						//project_build_ftp_upload.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						project_build_build_result.changeClass("btn_cm type3 icon stop", "btn_cm type2 icon stop");
						project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
						//project_build_building.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");

						var message_fail = common.getLabel("lbl_build_failed");
						project_build_build_result.setLabel(message_fail);
						document.getElementById("build_status_text_id").value = message_fail;

						btn2_progress_img.changeClass("progress-90", "progress-100");

						WebSquare.layer.hideProcessMessage();
						var historyCnt = data.historyCnt;

						project_build_start.setDisabled(true);
						btn_log_download.setUserData("history_id", historyCnt);
						btn_log_download.show("");

						if (data.message == "FAILED") {
							var message_failed = common.getLabel("lbl_build_js_build_failed");
							alert(data.buildHistoryObj.buildProjectName + " " + message_failed);
						}

						scwin.logData = "";

						localStorage.setItem("_buildFromProjSetting_", "stop");
						// scwin.setBranchBuildUpdate(data);
						break;
					case "CLEANBUILING" :
						//project_build_clean_build.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						//project_build_status.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						scwin.setBranchBuildLog(data);
						// scwin.setBranchBuildUpdate(data);
						break;
					default :
						break;
				}
			};

			// build log view
			scwin.setBranchBuildLog = function (data) {
				// txtarea_log
				var logData = data.logValue;
				var txtarea_log = document.getElementById("txtarea_log");
				txtarea_log.focus();
				scwin.logData += logData + "\n";

				txtarea_log.value = scwin.logData + "\n";
				txtarea_log.scrollTop = txtarea_log.scrollHeight - txtarea_log.clientHeight;

			};

			function intervalYnfunc() {
				interval = setInterval(function () {
					if (!isStop) {
						if (classBtnYn) {
							project_build_build_result.changeClass("btn_cm type2 icon stop", "btn_cm type3 icon stop");
							classBtnYn = false;
						} else {
							project_build_build_result.changeClass("btn_cm type3 icon stop", "btn_cm type2 icon stop");
							classBtnYn = true;
						}
					} else {
						clearInterval(interval);
						// 밖에서 선언한 interval을 안에서 중지시킬 수 있음
					}
				}, 500)
			};

			// db insert 처리 구간을 -> handler 내부에서 service 로 받아서 처리 방식 수정
			scwin.setBranchBuildInsert = function (data) {
				var sendData = {};
				var buildHistory = {};
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();
				// sendData insert setting
				// var workspace_id = localStorage.getItem("workspaceId"); workspaceID // parseInt
				var workspace_id = data.workspaceID // parseInt
				// var whive_session = sessionStorage.getItem("__whybrid_session__");
				// whive_session = JSON.parse(whive_session);

				sendData.MsgType = "HV_MSG_RELEASE_BUILD";
				sendData.sessType = "BRANCH";
				sendData.workspace_id = workspace_id;
				sendData.productType = buildActionData[0].product_type;

				if (buildActionData[0].platform == "Android") {
					sendData.buildType = build_select_buildtype.getValue(); // buildTyep 추가
				} else if (buildActionData[0].platform == "iOS") {
					sendData.buildType = build_select_buildtype.getValue();
				}

				if (buildActionData[0].product_type.toLowerCase() == "wmatrix") {
					if (buildActionData[0].platform == "Android") {
						sendData.profileType = build_select_profiletype.getValue().toLowerCase();
					} else if (buildActionData[0].platform == "iOS") {
						sendData.profileType = build_select_profiletype.getValue();
					}
				}

				sendData.appConfig = appConfigJSONData;

				// buildHistory.build_id = localStorage.getItem("buildProjectId");
				buildHistory.project_id = data.projectID;
				buildHistory.platform = buildActionData[0].platform;
				// buildHistory.target_server_id = localStorage.getItem("targetServerId");
				buildHistory.target_server_id = "";
				buildHistory.project_name = buildActionData[0].projectName;
				buildHistory.status = data.status;
				buildHistory.status_log = common.getLabel("lbl_sending_eng");
				buildHistory.log_path = data.log_path;
				buildHistory.result = data.message;
				//sendData.platform_build_file_path = data.BuildFileObj.platform_build_file_path;
				buildHistory.platform_build_file_path = "";
				buildHistory.platform_build_file_name = "";
				buildHistory.qrcode = "http://...";

				sendData.buildHistory = buildHistory;

				$.ajax({
					url: "/manager/build/history/create",
					type: "post",
					accept: "application/json",
					contentType: "application/json; charset=utf-8",
					data: JSON.stringify(sendData),
					dataType: "json",
					success: function (r, status) {
						if (status === "success") {
							setBuildingScreen();
						}
					},
					error: function (request, status, error) {
						var message = request.responseJSON;
						alert("error:" + message[0].error + "\n");
					}
				});
			};

			scwin.setBranchBuildInsertGeneralApp = function (data) {
				var sendData = {};
				var buildHistory = {};
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();

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

				sendData.buildHistory = buildHistory;

				(async () => {
					try {
						await fetch('/manager/general/build/history/create', {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
							body: JSON.stringify(sendData)
						})
								.then(response => {
									if (response.status == 200) {
										setBuildingScreen();
									} else {
										alert(response);
									}
								})
					} catch (e) {
						console.log("Error: ", e);
					}
				})();
			};

			function setBuildingScreen() {
				// build 버튼 disabled
				project_build_start.setDisabled(true);
				build_select_buildtype.setDisabled(true);
				build_select_profiletype.setDisabled(true);
				general_ios_scheme_selectbox.setDisabled(true);
				general_ios_export_method_selectbox.setDisabled(true);

				project_build_start.changeClass("btn_cm type1 icon redo", "btn_cm type2 icon stop");
				// btn_cm type1 icon redo

				localStorage.setItem("_buildStatusFlag_", "building");
			}

			// 빌드 완료, 빌드 실패시 db update 수행
			scwin.buildAfterQrcodeCreate = function (qrcodeUrl) {
				var qrcode = new QRCode(document.getElementById("build_qrcode"), {
					text: qrcodeUrl,
					width: 150,
					height: 150,
					colorDark: "#000000",
					colorLight: "#ffffff",
					correctLevel: QRCode.CorrectLevel.H
				});
			};

			scwin.buildAfterQrcodeCreateByID = function (qrcodeID) {
				var qrcode = new QRCode(document.getElementById("build_qrcode"), {
					text: g_config.HTTPSERVER_URL + "/builder/build/history/CheckAuthPopup/" + parseInt(qrcodeID),
					width: 150,
					height: 150,
					colorDark: "#000000",
					colorLight: "#ffffff",
					correctLevel: QRCode.CorrectLevel.H
				});
			}

			scwin.success_android_callback = function (response) {

			};

			scwin.success_ios_callback = function (response) {

			};

			scwin.error_callback = function (XMLHttpRequest, textStatus, errorThrown) {
				// console.log("Status: " + textStatus);
				alert("Error: " + errorThrown);
			};

			scwin.onpageunload = function () {
				scwin.logData = "";
				// window.location = "";
				localStorage.setItem("_buildStatusFlag_", "build_back");
				localStorage.setItem("_buildFromProjSetting_", "stop");
			};

			scwin.btn_qrcode_onclick = function () {
				// resultLayer.show();
			};

			/*
                빌드완료 후, App Download 버튼을 누르면,
                웹서버에서 해당 apk or ipa 파일을 다운 받는다.
                @soorink
             */
			scwin.btn_app_download_onclick = function (e) {
				var history_id = this.getUserData("history_id");

				(async () => {
					try {
						const response = await fetch('/manager/build/history/startFileDownload', {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json',
							},
							body: JSON.stringify({
								'history_id': history_id,
							}),
						});

						const result = await response.blob();
						const headers = await response.headers;
						const blob = new Blob([result], {type: 'application/octet-stream'});
						const url = URL.createObjectURL(blob);
						const link = document.createElement('a');
						link.href = url;
						link.download = headers.get('filename');
						link.click();
						URL.revokeObjectURL(url);
						delete a;
					} catch (error) {
						console.log('Error: ', error);
					}
				})();
			};


			scwin.getGeneralIOSAPPInfo = function (data) {
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();

				switch (data.message) {
					case "GETTING INFO":
						var message = common.getLabel("lbl_getting_info");
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
				var message_select_default = common.getLabel("lbl_select");
				general_ios_scheme_selectbox.addItem("", message_select_default);
				for (i = 0; i < data.length; i++) {
					general_ios_scheme_selectbox.addItem(data[i].scheme, data[i].scheme);
				}
			};

			scwin.makeSelectForScheme = function (data) {
				build_select_buildtype.removeAll();

				var message_select_default = common.getLabel("lbl_select");
				build_select_buildtype.addItem("", message_select_default);

				for (i = 0; i < iosSchemeInfo.length; i++) {
					if (iosSchemeInfo[i].scheme == data.newValue) {
						var configData = iosSchemeInfo[i].configuration;
						for (j = 0; j < configData.length; j++) {
							build_select_buildtype.addItem(configData[j].name, configData[j].name);
						}
					}
				}
			};

			scwin.selectedBuildInfo = function () {
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();
				selectedGeneralIosInfo.buildType = build_select_buildtype.getValue();

				if (buildActionData[0].platform.toLowerCase() == 'ios') {
					selectedGeneralIosInfo.exportMethod = general_ios_export_method_selectbox.getValue();
					selectedGeneralIosInfo.scheme = general_ios_scheme_selectbox.getValue();
					for (i = 0; i < iosSchemeInfo.length; i++) {
						if (iosSchemeInfo[i].scheme == selectedGeneralIosInfo.scheme) {
							var configData = iosSchemeInfo[i].configuration;
							for (j = 0; j < configData.length; j++) {
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

			scwin.resultLayer_onclose = function () {
				//resultImg.setSrc("");
				// resultLayer.hide();
			};

			scwin.start_log_download_onclick = function (e) {

				var history_id = this.getUserData("history_id");
				// var whive_session = sessionStorage.getItem("__whybrid_session__");
				// whive_session = JSON.parse(whive_session);

				var data = {};
				var options = {};

				data.history_id = history_id;
				// data.hqKey = whive_session.user_login_id; // hqKey 아이디

				options.action = "/manager/build/history/downloadLogFile";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				// options.method = "GET";
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + e + "\n" + "message:" + e.responseText + "\n");

				};
				$p.ajax(options);
			};

			scwin.setBuilderAppDownloadHref = function (data) {
				// app file 다운로드
				window.location = "/manager/build/history/downloadSetupFile/" + data.filename;
			};

			scwin.build_select_profile_onchange = function (data) {
				var profileStr = build_select_profiletype.getValue();

				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();

				var platform = buildActionData[0].platform;
				var project_id = buildActionData[0].project_pkid; // parseInt
				var workspace_id = buildActionData[0].workspace_pkid; // parseInt

				if (platform == "Android") {
					var data = {};

					data.platform = platform;
					data.projectID = parseInt(project_id);
					data.workspaceID = parseInt(workspace_id);
					data.profileType = profileStr;

					var options = {};

					options.action = "/manager/build/history/getMultiProfileAppConfig";
					options.mode = "asynchronous";
					options.mediatype = "application/json";
					options.requestData = JSON.stringify(data);
					options.method = "POST";

					options.success = function (e) {

					};

					options.error = function (e) {

						if (request.status == 200) {

						}
					};

					$p.ajax(options);
				} else if (platform == "iOS") {

					var data = {};

					data.platform = platform;
					data.projectID = parseInt(project_id);
					data.workspaceID = parseInt(workspace_id);
					data.profileType = profileStr;

					var options = {};

					options.action = "/manager/build/history/getMultiProfileAppConfig";
					options.mode = "asynchronous";
					options.mediatype = "application/json";
					options.requestData = JSON.stringify(data);
					options.method = "POST";

					options.success = function (e) {

					};

					options.error = function (e) {

						if (request.status == 200) {

						}
					};
					$p.ajax(options);
				}
			};

			scwin.btn_qrcode_popup = function (data) {

				requires("uiplugin.popup");

				var rowJSON = {
					"data": data
				};

				var dataObject = {
					"type": "json",
					"name": "param",
					"data": rowJSON
				};

				var opts = {
					"id": "popup_window_qrcode",
					"type": "litewindow",
					"width": 350 + "px",
					"height": 350 + "px",
					"popupName": " ",
					"modal": false,
					"useIFrame": false,
					"title": "",
					"useATagBtn": "true",
					"frameMode": "wframe",
					"dataObject": dataObject
				};

				WebSquare.util.openPopup("/xml/QRCode.xml", opts);

			};
			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'white_board',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'work',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'pgt_tit fl',id:'txt_build_project_name',label:'',style:'',useLocale:'true',localeRef:'lbl_build_project_name'}},{T:1,N:'br'},{T:1,N:'br'},{T:1,N:'xf:group',A:{class:'fl',id:'',style:''},E:[{T:1,N:'xf:group',A:{style:'',class:'progress-btn',id:'btn4','ev:onclick':'scwin.btn4_onclick'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:data-progress-style',E:[{T:3,text:'indefinite'}]}]},{T:1,N:'xf:group',A:{style:'',id:'btn_progress_text',class:'btn'}},{T:1,N:'xf:group',A:{style:'',id:'btn2_progress_img',class:'progress-0'}}]},{T:1,N:'input',A:{id:'build_status_text_id',type:'hidden'}},{T:1,N:'xf:trigger',A:{class:'btn_cm type2 icon stop',id:'project_build_build_result',style:'',type:'button',useLocale:'true',localeRef:'lbl_ready'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'build_select_profiletype_txtbox',label:'',style:'',useLocale:'true',localeRef:'lbl_build_profile_type'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'build_select_profiletype',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'height: 22px;',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.build_select_profile_onchange'},E:[{T:1,N:'xf:choices'}]},{T:1,N:'w2:textbox',A:{class:'title',id:'general_ios_scheme_selectbox_label',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_scheme'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'general_ios_scheme_selectbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'height: 22px;',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.makeSelectForScheme'},E:[{T:1,N:'xf:choices'}]},{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_build_build_type'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'build_select_buildtype',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'height: 22px;',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.selectedBuildInfo'},E:[{T:1,N:'xf:choices'}]},{T:1,N:'w2:textbox',A:{class:'title',id:'general_ios_export_method_selectbox_label',label:'',style:'',useLocale:'true',localeRef:'lbl_distribution_setting'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'general_ios_export_method_selectbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'height: 22px;',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false',useLocale:'true',useItemLocale:'true','ev:onchange':'scwin.selectedBuildInfo'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_select'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'""'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_development'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'development'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_appstore'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'app-store'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_adhoc'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'ad-hoc'}]}]}]}]},{T:1,N:'xf:trigger',A:{class:'btn_cm type1 icon redo',id:'project_build_start',style:'',type:'button','ev:onclick':'scwin.start_build_project',useLocale:'true',localeRef:'lbl_build'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:anchor',A:{id:'btn_log_download',style:'display:none;',class:'btn_cm type2 type4',outerDiv:'false',href:'',type:'button','ev:onclick':'scwin.start_log_download_onclick',useLocale:'true',localeRef:'lbl_log_download'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:anchor',A:{id:'btn_app_download',style:'',class:'btn_cm type2 type4',outerDiv:'false',href:'',type:'button','ev:onclick':'scwin.btn_app_download_onclick',useLocale:'true',localeRef:'lbl_app_download'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'textarea',A:{id:'txtarea_log',class:'console_box',style:'height: 600px;font-family: Monospace;',placeholder:'',readOnly:'true'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'pgtbox'},E:[{T:1,N:'xf:trigger',A:{id:'btn_qrcode',style:'',class:'btn_cm icon qrcode',type:'button','ev:onclick':'scwin.btn_qrcode_onclick',useLocale:'true',localeRef:'lbl_build_qrcode'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'w2:floatingLayer',A:{id:'resultLayer',title:'',style:'position:absolute; display:none;width:190px; height:185px; z-index:9999;','ev:onclose':'scwin.resultLayer_onclose',useModal:'false',useLocale:'true',localeRef:'lbl_build_result_layer_title'},E:[{T:1,N:'div',A:{id:'qrcode',style:'height:150px;'}}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_build_qrcode'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocal:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''}}]}]}]}]})