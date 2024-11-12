/*amd /cm/ui/works/project_setting_step02.xml 42553 76e60e942b6e2e21df102c1986b2ee74a1c59d9c0a55e0031c2d6c27f4bef05c */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.updateProjectToFlag_yn = false;
						scwin.app_icon_upload_check_yn = false;
						scwin.step01Data = $p.parent().dtl_build_setting_step1.getAllJSON();
						scwin.workspace_group_role_id = "";
						scwin.platformType = "";

						scwin.originValues = [];

						scwin.onpageload = function () {
							scwin.init();
						};

						scwin.init = async function () {
							scwin.platformType = scwin.step01Data[0].platform;
							scwin.workspace_group_role_id = scwin.step01Data[0].workspace_group_role_id ? scwin.step01Data[0].workspace_group_role_id : "";
							const getAllConfigData = $p.parent().scwin.getResultAppConfigData;

							if (scwin.platformType == "Android") {
								const profiles = getAllConfigData.getConfig.wmatrix.profiles;
								let cnt = 0;
								for(const [key,value] of Object.entries(profiles)){
									scwin.originValues.push(value);
									const rowJSON = {
										"profile" : value,
										"step01Data" : scwin.step01Data[0]
									};

									const tabOpt = {
										label : key,
										closable : true
									};

									const contOpt = {
										frameMode : "wframe",
										src : "/ui/works/project_setting_step02_app_config.xml",
										title : key,
										alwaysDraw : "true",
										scope: "true",
										dataObject: {
											"type" : "json",
											"name" : "tabParam",
											"data" : rowJSON
										}
									};

									project_setting_step2_tap.addTab('project0'+cnt, tabOpt, contOpt);
									cnt++;
								}

							} else if (scwin.platformType == "iOS") {
								let profilesDebugList = [];
								let profilesList = [];
								let datareq = {};
								datareq.key_id = scwin.step01Data[0].key_id;
								const uri = common.uri.iosProfilesKeyName;
								const resData = await common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, datareq);
								if(resData != null){
									for (let row in resData) {
										if (resData[row].profiles_build_type == "development") {
											profilesDebugList.push(resData[row].profiles_key_name);
										} else {
											profilesList.push(resData[row].profiles_key_name);
										}
									}
								}

								const targets = getAllConfigData.resultAppConfigListObj.targets;
								let cnt = 0;
								for(const [key,value] of Object.entries(targets)){
									scwin.originValues.push(value);
									const rowJSON = {
										"target" : value,
										"step01Data" : scwin.step01Data[0],
										"profilesList": profilesList,
										"profilesDebugList": profilesDebugList
									};

									const tabOpt = {
										label : key,
										closable : true
									};

									const contOpt = {
										frameMode : "wframe",
										src : "/ui/works/project_setting_step02_app_config.xml",
										title : key,
										alwaysDraw : "true",
										scope: "true",
										dataObject: {
											"type" : "json",
											"name" : "tabParam",
											"data" : rowJSON
										}
									};
									project_setting_step2_tap.addTab('project0'+cnt, tabOpt, contOpt);
									cnt++;
								}
							}
						};

						// step1에서 선택된 platform을 기준으로 앱 정보 입력 창을 tab으로 추가
						scwin.addTab = function (tabId, title, dataObj) {
							const tabOpt = {
								label: title,
								closable: true,
							};

							const contOpt = {
								frameMode: "wframe",
								src: "/ui/works/project_add_step02_app_config.xml",
								title: title,
								alwaysDraw: "true",
								scope: "true",
								dataObject: {
									"type": "json",
									"name": "tabParam",
									"data": dataObj
								}
							};

							project_setting_step2_tap.addTab(tabId, tabOpt, contOpt);
						};

						scwin.setBuilderAppIconUploadStatus = function (obj) {
							switch (obj.status) {
								case "APPICONUPLOAD":
									const message = common.getLabel("lbl_app_icon_uploading");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "APPICONUPLOADDONE":
									// start project create
									WebSquare.layer.hideProcessMessage();
									scwin.saveStep2Data();
									break;
								default :
									break;
							}
						};

						// 종료 구간
						scwin.setBuilderTemplateMultiProfileConfigStatus = async function(obj) {
							let message = "";
							switch (obj.message) {
								case "CONFIGSEARCHING":
									message = common.getLabel("lbl_project_setting_step02_js_multi_profile_config_list");
									WebSquare.layer.showProcessMessage(message); //MultiProfile Config List
									break;
								case "CONFIGUPDATE" :
									message = common.getLabel("lbl_project_setting_step02_js_multi_profile_config_update");
									WebSquare.layer.showProcessMessage(message); //MultiProfile Config Update
									break;
								case "DONE" :
									WebSquare.layer.hideProcessMessage();
									// alert("W-Hybrid Server 설정 수정 완료");
									message = common.getLabel("lbl_project_setting_step02_js_updated_project");
									if(await common.win.confirm(message)){ //프로젝트 정보 수정이 완료 되었습니다. \n 빌드 화면으로 이동 하시겠습니까?

										let buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);

										let platform = buildproj_json.platform;
										let buildProjectName = buildproj_json.project_name;
										let build_project_id = buildproj_json.project_id;
										let workspace_id = buildproj_json.workspace_id;
										let product_type = buildproj_json.product_type;
										/// var target_server_id = this.getUserData("target_server_id");

										let buildAction = [];

										let data = {};
										data.platform = platform;
										data.projectName = buildProjectName;
										data.project_pkid = build_project_id;
										data.workspace_pkid = workspace_id;
										data.product_type = product_type;

										buildAction.push(data);

										$p.parent().$p.parent().__buildaction_data__.setJSON(buildAction);

										const tabId = $p.top().scwin.convertMenuCodeToMenuKey("m0101000000");
										$p.top().scwin.add_tab(tabId);

									}else {
										// flag yn 처리
										// scwin.btn_project_step4_checkYn = true;
										// scwin.createProjectToFlag_yn = true;
										// // btn_create_project_trigger.setDisabled(true);
										// btn_next.show();

									}

									break;
								case "SUCCESSFUL":
									// scwin.setServerConfigData(obj);
									WebSquare.layer.hideProcessMessage();

									message = common.getLabel("lbl_project_setting_step02_js_updated_project");
									if(await common.win.confirm(message)){ //프로젝트 정보 수정이 완료 되었습니다. \n 빌드 화면으로 이동 하시겠습니까?

										let buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);

										let platform = buildproj_json.platform;
										let buildProjectName = buildproj_json.project_name;
										let build_project_id = buildproj_json.project_id;
										let workspace_id = buildproj_json.workspace_id;
										let product_type = buildproj_json.product_type;
										/// var target_server_id = this.getUserData("target_server_id");

										let buildAction = [];

										let data = {};
										data.platform = platform;
										data.projectName = buildProjectName;
										data.project_pkid = build_project_id;
										data.workspace_pkid = workspace_id;
										data.product_type = product_type;

										buildAction.push(data);

										$p.parent().__buildaction_data__.setJSON(buildAction);

										const tabId = $p.top().scwin.convertMenuCodeToMenuKey("m0101000000");
										$p.top().scwin.add_tab(tabId);

									}else {
										// flag yn 처리
										// scwin.btn_project_step4_checkYn = true;
										// scwin.createProjectToFlag_yn = true;
										// // btn_create_project_trigger.setDisabled(true);
										// btn_next.show();

									}

									break;
								default :
									break;
							}
						};

						scwin.setBranchProjectCreateSyncStatus = async function (obj) {
							let message = "";
							// git sync return
							switch (obj.gitStatus) {
								case "GITCLONE":
									message = common.getLabel("lbl_git_clone");
									WebSquare.layer.showProcessMessage(message); //Git Clone
									break;
								case "SVNCHECKOUT":
									message = common.getLabel("lbl_svn_checkout");
									WebSquare.layer.showProcessMessage(message); //Svn Checkout
									break;
								case "SVNCREATE":
									message = common.getLabel("lbl_svn_create");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "APPICONUNZIP":
									message = common.getLabel("lbl_app_icon_uploading");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "FILEUPLOAD":
									message = common.getLabel("lbl_signingkey_uploading");
									WebSquare.layer.showProcessMessage(message); //SigningKey Uploading
									// scwin.sendBranchSigningKeyAPI(obj);
									break;
								case "CONFIG":
									message = common.getLabel("lbl_app_config_setting");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "GITCOMMIT":
									message = common.getLabel("lbl_git_commit");
									WebSquare.layer.showProcessMessage(message); //Git Commit
									break;
								case "GITADD":
									message = common.getLabel("lbl_git_add");
									WebSquare.layer.showProcessMessage(message); //Git Add
									break;
								case "GITPUSH":
									message = common.getLabel("lbl_git_push");
									WebSquare.layer.showProcessMessage(message); // Git Push
									break;
								case "SVNADD":
									message = common.getLabel("lbl_svn_add");
									WebSquare.layer.showProcessMessage(message); //Svn Add
									break;
								case "SVNCOMMIT":
									message = common.getLabel("lbl_svn_commit");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "MKDIR":
									message = common.getLabel("lbl_project_dir_create");
									WebSquare.layer.showProcessMessage(message); //Project Directory Create
									break;
								case "GITBARE":
									message = common.getLabel("lbl_git_bare_init");
									WebSquare.layer.showProcessMessage(message); //Git Bare init
									break;
								case "PROJCOPY":
									message = common.getLabel("lbl_project_zip_copy");
									WebSquare.layer.showProcessMessage(message); //Project Zip File Copy
									break;
								case "APPICONUPLOAD":
									message = common.getLabel("lbl_app_icon_uploading");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "APPICONUPLOADDONE":
									// start project create
									WebSquare.layer.hideProcessMessage();
									scwin.saveStep2Data();
									break;
								case "DONE":
									WebSquare.layer.hideProcessMessage();
									message = common.getLabel("lbl_project_setting_step02_js_updated_project");
									if (await common.win.confirm(message)) {
										const buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);
										let buildAction = [];
										let data = {};
										data.platform = buildproj_json.platform;
										data.projectName =  buildproj_json.project_name;
										data.project_pkid = buildproj_json.project_id;
										data.workspace_pkid =  buildproj_json.workspace_id;;
										data.product_type = buildproj_json.product_type;;
										buildAction.push(data);

										//index.xml에 datalist에 데이터저장
										$p.top().__buildaction_data__.setJSON(buildAction);

										//"/ui/build/build.xml" open
										const tabId = $p.top().scwin.convertMenuCodeToMenuKey("m0101000000");
										$p.top().scwin.add_tab(tabId);
									}
									break;
								default :
									break;
							}

						};

						scwin.setBuilderDeploySettingStatus = function (obj) {
							let message = "";
							switch (obj.status) {
								case "FASTLANEINIT":
									message = common.getLabel("lbl_project_add_step02_js_fastlane_init");
									WebSquare.layer.showProcessMessage(message); //Deploy init Create
									break;
								case "FASTENV":
									message = common.getLabel("lbl_project_add_step02_js_fastlane_env");
									WebSquare.layer.showProcessMessage(message); //Deploy Env setting
									break;
								case "FASTFILE":
									message = common.getLabel("lbl_project_add_step02_js_fastlane_file");
									WebSquare.layer.showProcessMessage(message); //Deploy FastFile Create
									break;
								case "APPFILE":
									message = common.getLabel("lbl_project_add_step02_js_fastlane_app_file");
									WebSquare.layer.showProcessMessage(message); //Deploy AppFile Create
									break;
								case "DONE":
									WebSquare.layer.hideProcessMessage();
									break;
								default :
									break;
							}
						};

						scwin.btn_complete_onclick = async function (e) {
							if (scwin.checkData()) {
								const message = common.getLabel("lbl_project_setting_step02_editInfo");
								if (await common.win.confirm(message)) {
									// app icon upload file yn 확인
									// app icon 있을 경우 png file uplaod 실행
									if (scwin.checkAppIconFileYn()) {
										scwin.checkAppIconFileAndUpload();
									} else {
										// 없을 경우 project create 실행
										scwin.saveStep2Data();
									}
								}
							}
						};

						scwin.checkData = function () {
							let packageName = "";
							const platformType = scwin.platformType;
							for (let tap_cnt = 0; tap_cnt < project_setting_step2_tap.getTabCount(); tap_cnt++) {
								const tableFrame = project_setting_step2_tap.getFrame(tap_cnt);
								let append_tap_app_config_data = {};
								let message = "";

								if (!tableFrame) {
									let profile_name = "";
									let app_id = "";
									let app_name = "";
									let app_version = "";
									let app_version_code = "";
									let appIDCheck = "";

									// android
									if (platformType == "Android") {
										profile_name = tableFrame.getObj("step2_android_input_profile_name").getValue();
										app_id = tableFrame.getObj("step2_android_input_app_id").getValue();
										app_name = tableFrame.getObj("step2_android_input_app_name").getValue();
										app_version = tableFrame.getObj("step2_android_input_app_version").getValue();
										app_version_code = tableFrame.getObj("step2_android_input_app_version_code").getValue();
										packageName = tableFrame.getObj("step2_android_input_packagename").getValue();
										append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_minsdk_version").getValue();
									} else if (platformType == "iOS") {
										app_id = tableFrame.getObj("step2_ios_input_app_id").getValue();
										app_name = tableFrame.getObj("step2_ios_input_app_name").getValue();
										app_version = tableFrame.getObj("step2_ios_input_app_version").getValue();
										app_version_code = tableFrame.getObj("step2_ios_input_app_version_code").getValue();
										xcode_proj_name = tableFrame.getObj("step2_ios_input_projectname").getValue();
										append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_target_version").getValue();
									}

									const check_app_version_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION", app_version);
									const check_app_version_code_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION_CODE", app_version_code);

									if (common.isEmptyStr(profile_name) && (platformType == 'Android')) {
										message = common.getLabel("lbl_project_add_step02_js_input_profile_name");
										common.win.alert(message);
										scwin.app_id_license_check_yn = false;
										return false;
									}

									if (common.isEmptyStr(app_id)) {
										message = common.getLabel("lbl_input_appid");
										common.win.alert(message);
										scwin.app_id_license_check_yn = false;
										return false;
									}

									appIDCheck = app_id.split("\.");

									// app id 체크
									if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
										for (let i = 0; i < appIDCheck.length; i++) {
											let strAppID = appIDCheck[i];
											let check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

											if (!check_app_id_str) {
												message = common.getLabel("lbl_appid_check_form");
												common.win.alert(app_id + " : " + message);
												return false;
											}
										}
									} else {
										message = common.getLabel("lbl_appid_check_form");
										alert(app_id + " : " + message);
										return false;
									}

									scwin.app_id_temp_save = app_id;

									if (common.isEmptyStr(app_name)) {
										message = common.getLabel("lbl_check_app_name");
										common.win.alert(message);
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

									if (common.isEmptyStr(packageName) && (platformType == 'Android')) {
										message = common.getLabel("lbl_check_package_name");
										common.win.alert(message);
										return false;
									}

									if (common.isEmptyStr(xcode_proj_name) && (platformType == 'iOS')) {
										message = common.getLabel("");
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

									// ios
									let appIDCheck1 = "";
									let appIDCheckAll = "";

									let serverConfigArray = [];
									let server1Json = {};

									// package name 한글 입력
									if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "Android") {
										message = common.getLabel("lbl_package_name_kor_rule");
										common.win.alert(packageName + " : " + message);
										return false;
									}

									// project name 한글 입력
									if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", xcode_proj_name) && scwin.platformType == "iOS") {
										message = common.getLabel("lbl_proejct_name_kor_rule");
										common.win.alert(xcode_proj_name + " : " + message);
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
										message = common.getLabel("lbl_project_add_step02_js_server_url_kor");
										common.win.alert(server_URL1 + " : " + message); //server URL 형식에 한글은 입력할 수 없습니다
										return false;
									}

									if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "Android") {
										message = common.getLabel("lbl_package_name_special_char_rule");
										common.win.alert(packageName + " : " + message);
										return false;
									}

									if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", xcode_proj_name) && scwin.platformType == "iOS") {
										message = common.getLabel("lbl_project_name_special_char_rule");
										common.win.alert(xcode_proj_name + " : " + message);
										return false;
									}


									if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", server_name_1)) {
										message = common.getLabel("lbl_server_name_special_char_rule");
										cmmon.win.alert(server_name_1 + " : " + message);
										return false;
									}

									appIDCheck1 = app_id1.split("\.");

									// appID 1 check
									if (appIDCheck1.length >= 3 || appIDCheck1.length >= 4) {
										for (let i = 0; i < appIDCheck1.length; i++) {
											const strAppID = appIDCheck1[i];

											const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

											if (!check_app_id_str) {
												message = common.getLabel("lbl_appid_check_form");
												common.win.alert(app_id1 + " : " + message);
												return false;
											}
										}
									} else {
										message = common.getLabel("lbl_appid_check_form");
										common.win.alert(app_id1 + " : " + message);
										return false;
									}

								}
							}
							return true;
						};

						// step2 데이터 저장
						scwin.saveStep2Data = function () {
							const platformType = scwin.platformType;
							let message = "";
							let build_all_in_json = {};
							let tap_all_app_configArray = [];
							let tabArr = project_setting_step2_tap.tabArr;
							for (let tap_cnt = 0; tap_cnt < tabArr.length; tap_cnt++) {
								const tabId = tabArr[tap_cnt].userTabID;
								const tableFrame = project_setting_step2_tap.getFrame(tabId);
								let append_tap_app_config_data = {};

								if (tableFrame) {
									// android
									if (platformType == "Android") {
										append_tap_app_config_data.profile_name = tabArr[tap_cnt].title;
										append_tap_app_config_data.app_id = tableFrame.getObj("step2_android_input_app_id").getValue();
										append_tap_app_config_data.app_name = tableFrame.getObj("step2_android_input_app_name").getValue();
										append_tap_app_config_data.app_version = tableFrame.getObj("step2_android_input_app_version").getValue();
										append_tap_app_config_data.app_version_code = tableFrame.getObj("step2_android_input_app_version_code").getValue();
										append_tap_app_config_data.package_name = tableFrame.getObj("step2_input_packagename").getValue();
										append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_minsdk_version").getValue();

										if (tap_cnt == 0) {
											if (document.getElementById("step2_android_input_icons_path").files[0] === "undefined") {
												append_tap_app_config_data.icon_image_path = "";
											} else if (document.getElementById("step2_android_input_icons_path").files[0] != undefined) {
												append_tap_app_config_data.icon_image_path = document.getElementById("step2_android_input_icons_path").files[0].name;
											}
										} else {
											append_tap_app_config_data.icon_image_path = "";
										}

										//서버 입력정보 없애면서 주석처리 시작
										let serverConfigArray = [];
										let server1Json = {};

										server1Json.name = tableFrame.getObj("input_servername_1").getValue();
										server1Json.appId = tableFrame.getObj("input_appid_1").getValue();
										server1Json.url = tableFrame.getObj("input_serverurl_1").getValue();

										serverConfigArray.push(server1Json);

										const serverInfoGenerator = tableFrame.getObj("serverInfoGen");
										const serverInfoLength = serverInfoGenerator.getLength();
										for(let idx = 0; idx < serverInfoLength; idx++){
											let servertempJson = {};
											let input_servername = serverInfoGenerator.getChild(idx,"ibx_servername");
											let input_appid = serverInfoGenerator.getChild(idx,"ibx_appid");
											let input_serverurl = serverInfoGenerator.getChild(idx,"ibx_serverurl");
											if(input_servername != null){
												servertempJson.name = input_servername.getValue();
											}
											if(input_appid != null){
												servertempJson.appId = input_appid.getValue();
											}
											if(input_serverurl != null){
												servertempJson.url = input_serverurl.getValue();
											}
											serverConfigArray.push(servertempJson);
										}
										append_tap_app_config_data.server = serverConfigArray;
										tap_all_app_configArray.push(append_tap_app_config_data);
									} else if (platformType == "iOS") {
										append_tap_app_config_data.target_name = tabArr[tap_cnt].title;
										append_tap_app_config_data.app_id = tableFrame.getObj("step2_ios_input_app_id").getValue();
										append_tap_app_config_data.app_name = tableFrame.getObj("step2_ios_input_app_name").getValue();
										append_tap_app_config_data.app_version = tableFrame.getObj("step2_ios_input_app_version").getValue();
										append_tap_app_config_data.app_version_code = tableFrame.getObj("step2_ios_input_app_version_code").getValue();
										append_tap_app_config_data.xcode_proj_name = tableFrame.getObj("step2_ios_input_projectname").getValue();
										append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_target_version").getValue();
										append_tap_app_config_data.build_type = tableFrame.getObj("step2_select_profile_key_name").getValue();
										append_tap_app_config_data.debug_type = tableFrame.getObj("step2_select_debug_profile_key_name").getValue();

										if (tap_cnt == 0) {
											if (document.getElementById("step2_ios_input_icons_path").files[0] === "undefined") {
												append_tap_app_config_data.icon_image_path = "";
											} else if (document.getElementById("step2_ios_input_icons_path").files[0] != undefined) {
												append_tap_app_config_data.icon_image_path = document.getElementById("step2_ios_input_icons_path").files[0].name;
											}
										} else {
											append_tap_app_config_data.icon_image_path = "";
										}

										let build_type = tableFrame.getObj("step2_select_profile_key_name").getValue();
										if (build_type == undefined || build_type == null) {
											common.win.alert(profile_name + " 탭에 해당 Release Type 을 선택해주세요.");
											return false;
										} else {
											append_tap_app_config_data.build_type = build_type;
										}

										//서버 입력정보 없애면서 주석처리 시작
										let serverConfigArray = [];
										let server1Json = {};

										server1Json.name = tableFrame.getObj("input_servername_1").getValue();
										server1Json.appId = tableFrame.getObj("input_appid_1").getValue();
										server1Json.url = tableFrame.getObj("input_serverurl_1").getValue();

										serverConfigArray.push(server1Json);

										const serverInfoGenerator = tableFrame.getObj("serverInfoGen");
										const serverInfoLength = serverInfoGenerator.getLength();
										for(let idx = 0; idx < serverInfoLength; idx++){
											let servertempJson = {};
											let input_servername = serverInfoGenerator.getChild(idx,"ibx_servername");
											let input_appid = serverInfoGenerator.getChild(idx,"ibx_appid");
											let input_serverurl = serverInfoGenerator.getChild(idx,"ibx_serverurl");
											if(input_servername != null){
												servertempJson.name = input_servername.getValue();
											}
											if(input_appid != null){
												servertempJson.appId = input_appid.getValue();
											}
											if(input_serverurl != null){
												servertempJson.url = input_serverurl.getValue();
											}
											serverConfigArray.push(servertempJson);
										}

										append_tap_app_config_data.server = serverConfigArray;
										tap_all_app_configArray.push(append_tap_app_config_data);
									}
								}
							}

							// tap_all_app_configArray build_settings
							// build_project
							const buildproj_json = scwin.step01Data[0];
							const templateCheck = buildproj_json.template_version;

							// localStorage __workspace_group_role_id__ 값 대신 전역 변수 값을 세팅하기 ...
							// append_tap_app_config_data 내 데이터를 serverConfig array 같이 넣어주고
							build_all_in_json.workspace_group_role_id = scwin.workspace_group_role_id;
							build_all_in_json.buildProject = buildproj_json;
							build_all_in_json.buildSetting = tap_all_app_configArray;

							message = common.getLabel("lbl_select");
							if (templateCheck == "" || templateCheck == message) { //선택안함
								scwin.setBuildSetting(buildproj_json, tap_all_app_configArray); // server Config Array 값 추가
							} else {
								scwin.setTemplateProjectUpdate(buildproj_json, tap_all_app_configArray);
							}
						};

						scwin.setBuildSetting = async function (buildproj_json, buildsetting_json) {
							let build_all_in_json = {};
							build_all_in_json.workspace_group_role_id = scwin.workspace_group_role_id;
							build_all_in_json.buildProject = buildproj_json;
							build_all_in_json.buildSetting = buildsetting_json;

							const uri = common.uri.vcsMultiProfile;
							const resData = await common.http.fetch(uri,"POST",{"Content-Type":"application/json"},build_all_in_json);
							if(resData != null){
								const build_id = resData[0].build_id;
								$p.parent().dtl_build_project_step1.setCellData(0, "project_id", build_id);
								btn_complete.setDisabled(true);
							} else {
								const message = common.getLabel("lbl_failed_update_project");
								common.win.alert(message);
							}
						};

						// app config 탭을 추가한다.
						scwin.btn_tab_add_app_config_onclick = async function (e) {
							let profilesList = [];
							const platformType = scwin.platformType;
							const buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);

							const label = ibx_app_config_name.getValue();

							if (!common.isEmptyStr(label)) {
								if (platformType == "iOS") {
									let datareq = {};
									datareq.key_id = buildproj_json.key_id;
									const uri = common.uri.iosProfilesKeyName;
									const resData = await common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, datareq);
									if (resData != null) {
										for (let row in resData) {
											profilesList.push(resData[row].profiles_key_name);
										}
										const tab_num = project_setting_step2_tap.getTabCount();
										let rowJSON = {
											"tap_num": tab_num,
											"profileList": profilesList
										}
										scwin.addTab('project0' + tab_num, label, rowJSON);
									}
								} else if (platformType == "Android") {
									const tab_num = project_setting_step2_tap.getTabCount();
									const rowJSON = {
										"tap_num": tab_num,
										"profileList": profilesList
									};
									scwin.addTab('project0' + tab_num, label, rowJSON);
								}
								ibx_app_config_name.setValue("");
							} else {
								const message = common.getLabel("lbl_project_add_step02_js_input_build_config_name");
								common.win.alert(message); //buildConfig 이름을 입력하세요
							}
						};

						scwin.btn_tab_delete_app_config_onclick = async function (e) {
							let message = "";
							const label = ibx_app_config_name.getValue();

							if (label == "Debug" || label == "Release") {
								ibx_app_config_name.setValue("");
								//는 삭제할 수 없습니다
								const message = common.getLabel("lbl_project_add_step02_js_can_not_delete");
								common.win.alert(label + message);
								return;
							}

							const tabInfo = project_setting_step2_tap.getTabInfo();
							let tabId = "";

							// 입력된 라벨과 tab에 label과 일치하는 tabId를 찾는다.
							for (let idx in tabInfo) {
								if (tabInfo[idx].label == label) {
									tabId = tabInfo[idx].id
								}
							}

							if (tabId != "" && tabId != null) {
								const idx = project_setting_step2_tap.getTabIndex(tabId);

								//을(를) 삭제 하시겠습니까?
								message = common.getLabel("lbl_project_add_step02_js_confirm_delete");
								if (await common.win.confirm(label + " " + message)) {
									project_setting_step2_tap.deleteTab(idx);
									ibx_app_config_name.setValue("");
								}
							} else {
								//삭제할 이름을 입력하세요
								message = common.getLabel("lbl_project_add_step02_js_input_delete_name");
								common.win.alert(message);
							}
						};

						// 업로드될 appIcon 확장자 확인
						scwin.checkAppIconFileYn = function () {
							let message = "";
							const platform = scwin.platformType;
							let tap_cnt = 0;
							const tableFrame = project_setting_step2_tap.getFrame(tap_cnt);
							let AppIconfile = null;

							if (platform == "Android") {
								AppIconfile = document.getElementById("step2_android_input_icons_path");
								if (AppIconfile.files[0] == "" || AppIconfile.files[0] == null || AppIconfile.files[0] === "undefined") {
									return false;
								}

								const filename = AppIconfile.files[0].name; // 해당 값에서 png 파일 타입이 아닌 경우 수정 해야함 ...
								if (common.isEmptyStr(filename)) {
									return false;
								}

								const filepngtype = filename.split("\.");

								if (filepngtype[1] != "png") {
									message = common.getLabel("lbl_check_img_form");
									common.win.alert(message);
									fileObj.value = "";
									return false;
								} else if (filepngtype[1] == "png") {
									return true;
								}
							} else {
								AppIconfile = document.getElementById("step2_ios_input_icons_path");
								if (AppIconfile.files[0] == "" || AppIconfile.files[0] == null || AppIconfile.files[0] === "undefined") {
									return false;
								}

								const filename = AppIconfile.files[0].name; // 해당 값에서 png 파일 타입이 아닌 경우 수정 해야함 ...
								if (common.isEmptyStr(filename)) {
									return false;
								}

								const filepngtype = filename.split("\.");
								if (filepngtype[1] != "png") {
									message = common.getLabel("lbl_check_img_form");
									common.win.alert(message);
									fileObj.value = "";
									return false;
								} else if (filepngtype[1] == "png") {
									return true;
								}
							}
						};

						// 앱 아이콘 업로드 기능 및 앱 아이콘 파일 체크 기능
						scwin.checkAppIconFileAndUpload = function () {
							// png 파일 기반으로 업로드를 해야함
							// 이후에 파일 체크 기능 구현하고
							// 있으면 app icon 업로드
							// 없으면 project create 기능 수행 ...
							const workspace_name = "";
							const platform = scwin.platformType;
							const step1DataList = $p.parent().dtl_build_project_step1.getAllJSON();
							const step1Row = step1DataList[0];

							let formData = new FormData();
							let AppIconfile = "";
							let tap_cnt = 0;

							/// tap num = 0 값으로 처리해야함...
							const tableFrame = project_setting_step2_tap.getFrame(tap_cnt);

							if (platform == "Android") {
								// ZipIconfile = document.getElementById("step2_android_input_icons_path");
								// step2_android_input_icons_path
								AppIconfile = document.getElementById("step2_android_input_icons_path");

								formData.append("file", AppIconfile.files[0]);

							} else {
								// ZipIconfile = document.getElementById("step2_ios_input_icons_path");
								AppIconfile = document.getElementById("step2_ios_input_icons_path");

								formData.append("file", AppIconfile.files[0]);
							}

							formData.append("branch_id", step1Row.builder_id);
							formData.append("workspacename", workspace_name);
							formData.append("projectname", step1Row.project_name);
							formData.append("projectDirName", step1Row.project_dir_path);
							formData.append("platform", platform);
							formData.append("target_server", step1Row.target_server);

							const uri = common.uri.uploadIcon;
							common.http.fetchFileUpload(uri, "POST", formData).then((res) => {
								scwin.app_icon_upload_check_yn = true;
								scwin.saveStep2Data();
							}).catch((err) => {
								scwin.app_icon_upload_check_yn = false;
								const message = common.getLabel("lbl_check_img_form");
								common.win.alert(message + ", error:" + err);
							});
						};

						// project setting update
						scwin.setTemplateProjectUpdate = function (buildproj_json, buildsetting_json) {
							let build_all_in_json = {};
							build_all_in_json.workspace_group_role_id = scwin.workspace_group_role_id;
							if (scwin.platformType == "Android") {
								build_all_in_json.packageName = "";
							} else if (scwin.platformType == "iOS") {
								build_all_in_json.packageName = "";
							}
							build_all_in_json.buildProject = buildproj_json;
							build_all_in_json.buildSetting = buildsetting_json;

							const uri = common.uri.updateVcsMultiProfiles;
							common.http.fetchPost(uri,{"Content-Type":"application/json"},build_all_in_json).then((res)=>{
								return res.json();
							}).then((data)=>{
								if(data != null){
									btn_complete.setDisabled(true);
								}
							}).catch((e)=>{
								const message = common.getLabel("lbl_project_setting_step02_js_failed_update_project");
								common.win.alert(message+" status:"+e.status+" message:"+e.message);
							});
						};

						// input에 값에 변경유무만 체크
						scwin.checkUpdateData = function(){
							const platformType = scwin.platformType;
							let checkUpdateValue = false
							for (let tap_cnt = 0; tap_cnt < project_setting_step2_tap.getTabCount(); tap_cnt++) {
								const tableFrame = project_setting_step2_tap.getFrame(tap_cnt);
								if (tableFrame) {
									let app_id = "";
									let app_name = "";
									let app_version = "";
									let app_version_code = "";
									let min_target_version = "";
									let packageName = "";

									const item = scwin.originValues[tap_cnt];

									// android
									if (platformType == "Android") {
										app_id = tableFrame.getObj("step2_android_input_app_id").getValue();
										app_name = tableFrame.getObj("step2_android_input_app_name").getValue();
										app_version = tableFrame.getObj("step2_android_input_app_version").getValue();
										app_version_code = tableFrame.getObj("step2_android_input_app_version_code").getValue();
										packageName = tableFrame.getObj("step2_input_packagename").getValue();
										min_target_version = tableFrame.getObj("step2_select_minsdk_version").getValue();

										if (!(item.applicationId === app_id && item.appName === app_name && item.appVersion === app_version && item.appVersionCode === Number(app_version_code) && item.applicationId === packageName && item.minSdkVersion === Number(min_target_version))){
											checkUpdateValue = true;
										}
									} else if (platformType == "iOS") {
										app_id = tableFrame.getObj("step2_ios_input_app_id").getValue();
										app_name = tableFrame.getObj("step2_ios_input_app_name").getValue();
										app_version = tableFrame.getObj("step2_ios_input_app_version").getValue();
										app_version_code = tableFrame.getObj("step2_ios_input_app_version_code").getValue();
										packageName = tableFrame.getObj("step2_input_packagename").getValue();
										min_target_version = tableFrame.getObj("step2_select_target_version").getValue();
										if (!(item.Release.applicationID === app_id && item.Release.appName === app_name && item.Release.version === app_version && item.Release.versionCode === app_version_code && item.Release.deploymentTarget == min_target_version)){
											checkUpdateValue = true;
										}
									}
								}
							}
							return checkUpdateValue;
						}

						scwin.btn_step2_prev_onclick = function(e){
							$p.parent().scwin.selected_step(1);
						};

						scwin.btn_step2_next_onclick = function(e){
							if(scwin.updateProjectToFlag_yn == true){
								$p.parent().scwin.selected_step(3);
								return
							} else {
								// 값이 변경된게 있는지 체크
								if(scwin.checkUpdateData()){
									// 값이 유효한 값인지 체크
									if (scwin.checkData()) {
										scwin.saveStep2Data();
										$p.parent().scwin.selected_step(3);
									}
								} else {
									$p.parent().scwin.selected_step(3);
								}
							}
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_input_project_setting'}}]},{T:1,N:'xf:group',A:{id:'',class:'step_bar',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',tagname:'li',class:'prev'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'',useLocale:'true',localeRef:'lbl_project_setting_step01_project_setting'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',tagname:'li',style:'',class:'on'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_app_default_info_change'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',tagname:'li',style:''},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_project_add_step03_title'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_app_default_info'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_confirm_create_project'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_complete',style:'',type:'button','ev:onclick':'scwin.btn_complete_onclick',useLocale:'true',localeRef:'lbl_complete'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''}},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'ibx_app_config_name',style:'width:100%;',allowChar:'0-9, aA-zZ',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{class:'btn_cm row_add',id:'',style:'',type:'button','ev:onclick':'scwin.btn_tab_add_app_config_onclick',useLocale:'true',localeRef:'lbl_add'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm row_del',id:'btn_tab_delete_app_config',style:'',type:'button',useLocale:'true',localeRef:'lbl_del','ev:onclick':'scwin.btn_tab_delete_app_config_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'tbcbox',id:'',style:''},E:[{T:1,N:'w2:tabControl',A:{alwaysDraw:'false',class:'tbc',confirmFalseAction:'new',confirmTrueAction:'exist',id:'project_setting_step2_tap',style:'',tabScroll:'',useConfirmMessage:'false',useMoveNextTabFocus:'false',useTabKeyOnly:'true'}}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_prev',id:'btn_step2_prev',style:'',type:'button','ev:onclick':'scwin.btn_step2_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm step_next',id:'btn_step2_next',style:'',type:'button','ev:onclick':'scwin.btn_step2_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]})