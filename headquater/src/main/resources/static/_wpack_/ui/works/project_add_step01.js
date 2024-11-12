/*amd /cm/ui/works/project_add_step01.xml 43807 4fff54a395fc5f56ca22b9ce47230685362e4663d6c9bf5303e4ea9f3d111c8e */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dlt_builder_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'builder_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'builder_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_wmatrix_version_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'wmatrix_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'wmatrix_version',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_language_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_type',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_platform_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_type',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_vcs_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'vcs_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'vcs_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_ftp_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'ftp_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'ftp_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_signing_profile_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'number',id:'admin_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'number',id:'builder_id',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'create_date',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'number',id:'key_id',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'key_name',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'key_type',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'platform',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'update_date',name:'name2'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.build_project_name_yn = false;
						scwin.connect_build_server_yn = false;
						scwin.btn_check_project_step = false;
						scwin.txt_project_name_save = "";

						scwin.platform_name = "Android";

						scwin.workspace_group_role_id = "";

						scwin.workspaceData = {};

						scwin.onpageload = function () {
							scwin.workspaceData = $p.getParameter("wframeParam");
							//const dataObj = $p.getParamert("workspaceInfo");
							scwin.workspace_group_role_id = scwin.workspaceData.__workspace_group_role_id__ ? scwin.workspaceData.__workspace_group_role_id__ : "";

							step1_select_product_type.setSelectedIndex(0);
							step1_select_templatev.setSelectedIndex(0);
							step1_select_signingKey_settingbox.setSelectedIndex(0);

							let uri = common.uri.platformList;
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json()
								dlt_platform_selectbox.setJSON(data);

								step1_select_platform.setSelectedIndex(1);
								const platform = step1_select_platform.getValue();

								// parent 페이지에 platform 설정
								$p.parent().scwin.txt_project_all_step_platform = platform;
								scwin.platform_name = platform;

								// 디바이스, 언어 선택
								scwin.getProgrammingInfo(platform);

								// builder List 조회
								scwin.getBuilderInfo();

								// vcs 조회
								scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");

								// ftp 조회
								scwin.getFTPSettingInfo();

								// signing 조회
								scwin.getSigningKeySettingInfoToAdmin(platform);

								// 일반앱은 외부 vcs 사용이 필 수 이므로, pro 유저에게만 허용된다.
								scwin.getUserIsPayed(scwin.setGeneralAppSetting);

							}).catch(e => {
								common.win.alert("platform code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							})


						};

						// platform 조회
						scwin.getPlatformInfo = async function (platform) {
							step1_select_platform.removeAll(true);
							const uri = common.uri.platformList;
							await common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json()
								dlt_platform_selectbox.setJSON(data);
							}).catch(e => {
								common.win.alert("platform code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							})
						};

						// 신규 vcs profile 조회 조건 : (admin_id, vcs_type) 기준으로 추가 조회 하는 기능 추가
						scwin.getVCSSettingFindAdminAndTypeInfo = async function (vcs_type) {
							const uri = common.uri.vscList(vcs_type);
							const resData = await common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								dlt_vcs_setting_selectbox.setJSON(await res.json());
							}).catch(e => {
								common.win.alert("vcsList code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							});
						};

						// checkData와 saveStep1Data 합침. data check완료뒤에 datalist에 저장.
						// 입력데이터검증 및 데이터리스트에 데이터저장
						scwin.saveStep1Data = function () {
							const product_type = step1_select_product_type.getValue();
							const project_name = step1_input_projectname.getValue();
							const platform = step1_select_platform.getValue();
							const template_version = step1_select_templatev.getValue();
							const platform_language = step1_select_platform_language_settingbox.getValue();
							const branch_select_box = step1_select_builder_settingbox.getValue();
							const vcs_select_box = step1_select_vcs_settingbox.getValue();
							const ftp_select_box = step1_select_ftp_settingbox.getValue();
							const signingkey_box = step1_select_signingKey_settingbox.getValue();
							const android_app_id = step1_android_bundle_id.getValue();
							const description = step1_txtarea_desc.getValue();

							const builder_id = step1_select_builder_settingbox.getValue();
							const vcs_id = step1_select_vcs_settingbox.getValue();
							const ftp_id = step1_select_ftp_settingbox.getValue();
							const key_id = step1_select_signingKey_settingbox.getValue();

							const project_name_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_PROJECT_NAME", project_name);

							const tempDataList = $p.parent().dtl_build_project_step1.getRowJSON(0);
							let message = "";

							if (common.isEmptyStr(project_name)) {
								message = common.getLabel("lbl_check_project_name");
								common.win.alert(message); // 프로젝트 이름을 입력하세요.
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", project_name)) {
								message = common.getLabel("lbl_project_name_special_char_rule");
								common.win.alert(message);
								// 특수 문자 넣지 말아야할 것 알아보기
								return false;
							}

							if (!project_name_check_str) {
								message = common.getLabel("lbl_project_add_step01_js_check_project_name");
								common.win.alert(message); // 프로젝트 이름은 영문자 5자 이상 입력해야 합니다
								return false;
							}

							// project name 자릿수 제한
							if (common.getCheckInputLength(project_name, project_name.length, 200)) {
								return false;
							}

							if (!scwin.build_project_name_yn) {
								message = common.getLabel("lbl_project_add_step01_js_check_project_name_duplicate");
								common.win.alert(message); //프로젝트 이름 중복 확인이 필요합니다
								return false;
							}

							if (tempDataList.project_name != "") {
								if (tempDataList.project_name != project_name) {
									message = common.getLabel("lbl_project_add_step01_js_check_project_name_duplicate");
									common.win.alert(message); //프로젝트 이름 중복 확인이 필요합니다
									scwin.build_project_name_yn = false;
									return false;
								}
							} else if (scwin.txt_project_name_save != "") {
								if (scwin.txt_project_name_save != project_name) {
									message = common.getLabel("lbl_project_add_step01_js_check_project_name_duplicate");
									common.win.alert(message); //프로젝트 이름 중복 확인이 필요합니다
									scwin.build_project_name_yn = false;
									return false;
								}
							}

							scwin.txt_project_name_save = project_name;

							if (common.isEmptyStr(platform)) {
								message = common.getLabel("lbl_project_add_step01_js_select_platform_type");
								common.win.alert(message); // 플렛폼 타입을 선택하세요
								return false;
							}

							if (common.isEmptyStr(platform_language) && product_type == "WMatrix") {
								message = common.getLabel("lbl_project_add_step01_js_select_platform_language");
								common.win.alert(message); // 플렛폼 언어를 선택하세요
								return false;
							}

							if (common.isEmptyStr(branch_select_box) && product_type == "WMatrix") {
								message = common.getLabel("lbl_project_add_step01_js_select_builder_setting");
								common.win.alert(message); //Builder 설정을 선택하세요
								return false;
							}

							if (common.isEmptyStr(android_app_id) && platform == "Android" && product_type == "generalApp") {
								message = common.getLabel("lbl_project_add_step01_js_input_android_bundle_id");
								common.win.alert(message);
								return false;
							}

							if (common.isEmptyStr(vcs_select_box)) {
								message = common.getLabel("lbl_project_add_step01_js_select_vcs");
								common.win.alert(message); //VCS를 선택하세요
								return false;
							}

							if (common.isEmptyStr(vcs_select_box) && !common.isEmptyStr(template_version)) {
								message = common.getLabel("lbl_project_add_step01_js_select_template_or_vcs");
								common.win.alert(message); // Template Version, VCS 둘중 하나의 설정을 선택하세요
								return false;
							}


							if (common.isEmptyStr(vcs_select_box) && !common.isEmptyStr(template_version)) {
								message = common.getLabel("lbl_project_add_step01_js_select_template_and_vcs");
								common.win.alert(message); //Template Version 선택시 VCS는 내부 저장소 방식으로 선택하세요
								return false;
							}

							if (common.isEmptyStr(ftp_select_box)) {
								message = common.getLabel("lbl_project_add_step01_js_select_ftp_setting");
								common.win.alert(message); //FTP 설정을 선택하세요
								return false;
							}

							if (common.isEmptyStr(signingkey_box) && platform == "Android") {
								message = common.getLabel("lbl_project_add_step01_js_select_signing_key");
								common.win.alert(message); //SigningKey 설정을 선택하세요
								return false;
							}

							let data = {};
							data.workspace_id = scwin.workspaceData.workspace_id;
							data.project_name = project_name;
							data.product_type = product_type;
							data.platform = platform;
							data.platform_language = platform_language;
							data.template_version = template_version;
							data.project_dir_path = "";
							data.description = description;
							data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.
							data.builder_id = builder_id;
							data.vcs_id = vcs_id;
							data.ftp_id = ftp_id;
							data.key_id = key_id;

							// 부모페이지에 project name 저장하여 형제페이지에 프로젝트이름 공유
							$p.parent().scwin.txt_project_all_step_project_name = project_name;

							// 부모페이지에 step1 데이터 저장
							$p.parent().dtl_build_project_step1.setJSON([data]);
							return true;
						};

						// 다음버튼 이벤트
						scwin.btn_next_onclick = function (e) {
							if (scwin.saveStep1Data()) {
								scwin.btn_project_step2_checkYn = true;
								const product_type = step1_select_product_type.getValue().toLowerCase();
								if (product_type == "wmatrix") {
									// step2 이동
									$p.parent().scwin.selected_step(2);
								} else {
									// 일반 프로젝트 생성
									const buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);
									const android_app_id = step1_android_bundle_id.getValue();
									scwin.createGeneralAppProject(buildproj_json, android_app_id);
								}
							}
						};

						// 일반앱 프로젝트 생성
						scwin.btn_create_general_onclick = function (e) {
							if (scwin.saveStep1Data()) {
								scwin.btn_project_step2_checkYn = true;
								const buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);
								const android_app_id = step1_android_bundle_id.getValue();
								scwin.createGeneralAppProject(buildproj_json, android_app_id);
							}
						}

						// 일반앱 안드로이드 일때 앱 아이디 row 추가제거
						scwin.android_bundle_id_group_show_and_hide = function () {
							const product = step1_select_product_type.getValue().toLowerCase();
							const platform = step1_select_platform.getValue().toLowerCase();
							if (product == "generalapp" && platform == "android") {
								if (navigator.userAgent.includes('iPhone') || navigator.userAgent.includes('iPad')) {
									// webkit 브라우저에서 table-row 이상동작
									step1_android_bundle_id_group.setStyle("display", "block");
								} else {
									step1_android_bundle_id_group.setStyle("display", "table-row");
								}
							} else {
								step1_android_bundle_id_group.setStyle("display", "none");
							}
						};

						// Builder 정보조회
						scwin.getBuilderInfo = async function () {
							const uri = common.uri.builderList;
							await common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json();
								dlt_builder_setting_selectbox.setJSON(data);
							}).catch(e => {
								common.win.alert("BuilderList code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							});
						};

						// platform language조회
						scwin.getProgrammingInfo = async function (platform) {
							step1_select_platform_language_settingbox.removeAll(true);
							const uri = common.uri.programLanguageList(platform);
							await common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json()
								dlt_language_selectbox.setJSON(data);
							}).catch(e => {
								common.win.alert("Language code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							})
						};

						// FTP정보조회
						scwin.getFTPSettingInfo = async function () {
							const uri = common.uri.ftpList;
							await common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json();
								dlt_ftp_setting_selectbox.setJSON(data);
							}).catch(err => {
								common.win.alert("ftpList code:" + err.status + "\n" + "message:" + err.responseJSON + "\n");
							});
						};

						// signning profile 조회
						scwin.getSigningKeySettingInfoToAdmin = async function (platform) {
							const uri = common.uri.signingProfile;
							let reqData = {};
							reqData.platform = platform;
							const resData = await common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, reqData);
							dlt_signing_profile_selectbox.setJSON(resData);
						};


						// template version 조회
						scwin.send_to_builder_template_version_action = async function (builder_id, platform, product_type) {

							const uri = common.uri.templateList;
							let reqData = {};
							reqData.branch_id = builder_id;
							reqData.platform = platform;
							reqData.product_type = product_type;
							await common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, reqData);
							// respData가 websocket messageHandler로 전달됨.
						};


						// 프로젝트이름 정합성체크(특수문자,길이 등)
						scwin.checkDataProjectName = function (project_name) {
							const project_name_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_PROJECT_NAME", project_name);

							if (common.isEmptyStr(project_name)) {
								const message = common.getLabel("lbl_check_project_name"); // 프로젝트 이름을 입력하세요
								common.win.alert(message);
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", project_name)) {
								const message = common.getLabel("lbl_project_name_special_char_rule"); //Project 이름에 특수문자는 입력할 수 없습니다
								common.win.alert(message);
								return false;
							}

							if (!project_name_check_str) {
								const message = common.getLabel("lbl_project_add_step01_js_check_project_name"); //프로젝트 이름은 영문자 5자 이상, 200자 이하로 입력해야 합니다
								common.win.alert(message);
								return false;
							}

							scwin.txt_project_name_save = project_name;

							return true;
						};

						// 프로젝트이름 중복조회
						scwin.select_check_project_name = async function (project_name) {
							const uri = common.uri.checkProjectName(project_name);
							await common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json();
								const message_exist_project_name = common.getLabel("lbl_exist_name"); //해당 이름이 존재 합니다.
								const message_can_use_project_name = common.getLabel("lbl_can_use_name"); //해당 이름은 사용 가능 합니다.

								if (data[0].projectname_not_found == "no") {
									common.win.alert(message_exist_project_name);
								} else if (data[0].projectname_not_found == "yes") {
									common.win.alert(message_can_use_project_name);
									scwin.build_project_name_yn = true;
								}
							}).catch(e => {
								common.win.alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							});
						};


						// 프로젝트이름 체크 1.문자열체크 2.중복체크
						scwin.step1_btn_check_project_name_onclick = function (e) {
							const prject_name = step1_input_projectname.getValue();
							//프로젝트 이름 validate체크
							if (scwin.checkDataProjectName(prject_name)) {
								// 프로젝트 이름 중복체크
								scwin.select_check_project_name(prject_name);
							}
						};


						// 서명프로필 툴팁 handler
						scwin.changeToolTipContentAddStep1 = function (componentId, label) {
							const message_android = common.getLabel("lbl_sign_profile_tip");
							const message_ios = common.getLabel("lbl_ios_sign_profile");

							switch (scwin.platform_name) {
								case "Android":
									return message_android //미리 생성한 서명 프로필
								case "iOS":
									return message_ios //미리 생성한 서명 프로필 <br> (앱을 기기에 설치하거나 App Store에 제출시 필요한 인증서 서명 정보)
								default:
									return ""
							}
						};

						// 일반 앱 프로젝트 생성
						scwin.createGeneralAppProject = async function (buildproj_json, android_app_id) {
							let reqData = {};
							reqData.workspace_group_role_id = scwin.workspace_group_role_id;
							reqData.buildProject = buildproj_json;
							reqData.buildSetting = [{"app_id": android_app_id}];

							const uri = common.uri.createGeneralProj;
							const resData = await common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, reqData);
							if (Array.isArray(resData)) {
								$p.parent().dtl_build_project_step1.setCellData(0, 'project_id', resData[0].build_id);
							} else if (typeof resData == "object") {
								common.win.alert("error status:" + resData.status + ", message:" + resData.message);
								$p.getComponentById("btn_create_general").setDisabled(true);
							} else {
								const msg = common.getLabel("lbl_generalapp_create_failed_msg");
								common.win.alert(msg);
								$p.getComponentById("btn_create_general").setDisabled(true);
							}
						}

						scwin.setBuilderDeploySettingStatus = function (obj) {
							let msg = "";
							switch (obj.status) {
								case "FASTLANEINIT":
									msg = common.getLabel("lbl_project_setting_step05_init");
									WebSquare.layer.showProcessMessage(msg);
									break;
								case "FASTENV":
									msg = common.getLabel("lbl_project_setting_step05_env");
									WebSquare.layer.showProcessMessage(msg);
									break;
								case "FASTFILE":
									msg = common.getLabel("lbl_project_setting_step05_fastfile");
									WebSquare.layer.showProcessMessage(msg);
									break;
								case "APPFILE":
									msg = common.getLabel("lbl_project_setting_step05_appfile");
									WebSquare.layer.showProcessMessage(msg);
									break;
								case "DONE":
									msg = common.getLabel();
									WebSquare.layer.hideProcessMessage();
									const message = common.getLabel("lbl_project_setting_step05_complete");
									common.win.alert(message);

									break;
								default :
									break;
							}
						};

						// template version 조회후 messageHandler
						scwin.setBuilderToTemplateVersionSetting = function (data) {
							switch (data.message) {
								case "SUCCESSFUL":
									const versionList = data.templateVersionList.templateList;
									let list = [];
									for (let idx in versionList) {
										const data = {"wmatrix_id": versionList[idx], "wmatrix_version": versionList[idx]};
										list.push(data)
									}
									dlt_wmatrix_version_selectbox.setJSON(list);
									break;
								case "FAILD":
									const message = common.getLabel("lbl_connection_failed");
									common.win.alert(message); // Connection FAILD
									break;
								default:
									break;
							}
						};

						// Builder상태체크?
						scwin.setBranchConnectionStatus = function (data) {
							switch (data.message) {
								case "SUCCESSFUL" :
									const message_successful = common.getLabel("lbl_successful");
									const message_connect_successful = common.getLabel("lbl_connection_successful");

									step1_btn_testconnection.setLabel(message_successful); //SUCCESSFUL
									step1_btn_testconnection.setDisabled(true);
									scwin.connect_build_server_yn = true;
									common.win.alert(message_connect_successful); //Connection SUCCESSFUL
									break;
								case "FAILD" :
									const message_failed = common.getLabel("lbl_failed");
									const message_connect_failed = common.getLabel("lbl_connection_failed");

									step1_btn_testconnection.setLabel(message_failed); // FAILED
									step1_btn_testconnection.setDisabled(false);
									scwin.connect_build_server_yn = false;
									common.win.alert(message_connect_failed); // Connection FAILED
									break;
								default :
									break;
							}
						};

						// 일반프로젝트 생성시 websocket 이벤트처리
						scwin.setGeneralAppProjectCreateSyncStatus = async function (data) {
							let message = "";
							switch (data.gitStatus) {
								case "MKDIR":
									message = common.getLabel("lbl_project_dir_create"); //Project Directory Create
									WebSquare.layer.showProcessMessage(message);
									break;
								case "GITBARE":
									message = common.getLabel("lbl_git_bare_init"); //Git Bare init
									WebSquare.layer.showProcessMessage(message);
									break;
								case "GITCLONE":
									message = common.getLabel("lbl_git_clone"); //Git Clone
									WebSquare.layer.showProcessMessage(message);
									break;
								case "DONE":
									WebSquare.layer.hideProcessMessage();

									if (data.projectDirPath != null) {
										$p.parent().dtl_build_project_step1.setCellData(0, "project_dir_path", data.projectDirPath);
									}
									// project 생성이후 workspace 화면 갱신
									$p.top().scwin.updateWorkspaceData();

									message = common.getLabel("lbl_project_add_step01_js_confirm"); // 프로젝트 생성 완료 되었습니다. \n빌드 화면으로 이동 하시겠습니까?

									if (await common.win.confirm(message)) {
										const buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);

										let data = {};
										data.platform = buildproj_json.platform;
										data.project_name = buildproj_json.project_name;
										data.project_pkid = buildproj_json.project_id;
										data.workspace_pkid = buildproj_json.workspace_id;
										data.product_type = buildproj_json.product_type;

										$p.top().__buildaction_data__.setJSON([data]);

										btn_create_general.setDisabled(false);

										// 현재탭 닫기
										const tabIdx = $p.top().tabc_layout.selectedIndex;
										$p.top().tabc_layout.deleteTab(tabIdx);

										// build 화면으로 이동
										const tabId = $p.top().scwin.convertMenuCodeToMenuKey("m0101000000");
										$p.top().scwin.add_tab(tabId);
									}
									break;
							}
						};
						// websocket 이벤트 처리 END

						scwin.setGeneralAppSetting = function (isPayed) {
							if (isPayed) {
								// 상태바 가리기
								grp_step2.setStyle("display", "none");
								grp_step3.setStyle("display", "none");

								// wmatrix version 숨김 개발언어 설정숨김
								step1_language_and_wmatrixversion_grp.setStyle("display", "none");
								// 다음버튼 숨김
								btn_next.hide();
								// 프로젝트저장버튼 추가
								fr_grp_proj_save.show();
								// 안드로이드 앱 아이디 로우 추가
								scwin.android_bundle_id_group_show_and_hide();
							} else {
								// 제품을 W-Matrix로 선택
								step1_select_product_type.setSelectedIndex(1);
								// 제품 리스트에서 일반앱 삭제
								step1_select_product_type.deleteItem(0, false);
							}
						}

						// 로그인한 user의 결제 여부에 따라서,
						// 일반앱 프로젝트를 생성할 수 있는지 결정한다.
						// 일반앱은 외부 vcs 사용이 필 수 이므로, pro 유저에게만 허용된다.
						scwin.getUserIsPayed = function (callback) {
							const uri = common.uri.userPayCheck;
							let res = common.http.fetchPost(uri, {"Content-Type": "application/json"}, {})
								.then(res => res.json())
								.then(res => {
									if (Array.isArray(res) && res[0].is_payed == "yes") {
										callback(true);
									} else {
										callback(false);
									}
								});
						};

						/// 이벤트 START

						// 제품타입 변경 이벤트
						// 제품타입 변경시 재조회 > 프로그램언어조회, signing profile, 템플릿버전
						scwin.step1_select_product_type_onchange = function (info) {
							const product = info.newValue.toLowerCase();
							const platform = step1_select_platform.getValue()

							if (product == "wmatrix") {
								// 상태바 보이기
								grp_step2.setStyle("display", "list-item");
								grp_step3.setStyle("display", "list-item");

								// language & wmatrix version 표시
								if (navigator.userAgent.includes('iPhone') || navigator.userAgent.includes('iPad')) {
									// webkit 브라우저에서 table-row 이상동작
									step1_language_and_wmatrixversion_grp.setStyle("display", "block");
								} else {
									step1_language_and_wmatrixversion_grp.setStyle("display", "table-row");
								}

								// 다음버튼 표시
								btn_next.show();

								// 프로젝트저장버튼 숨김
								fr_grp_proj_save.hide();

								// 안드로이드 앱 아이디 로우 제거
								scwin.android_bundle_id_group_show_and_hide();

								// 프로그램언어조회
								// scwin.getProgrammingInfo(platform);

								// signing profile 조회
								scwin.getSigningKeySettingInfoToAdmin(platform);

								//scwin.getBuilderInfo();
								//scwin.getFTPSettingInfo();

								//빌더ID
								const BuilderID = step1_select_builder_settingbox.getValue();

								if (!!BuilderID) {
									// 템플릿버전조회
									scwin.send_to_builder_template_version_action(BuilderID, platform, product);
								}

								// vcs profile 조회
								// scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");
							} else {
								scwin.getUserIsPayed(scwin.setGeneralAppSetting);
							}
						};

						// platform 변경시 이벤트
						// platform 변경시 재조회 > 프로그램언어조회,template 버전조회(wmatrix), signing profile조회
						scwin.step1_select_platform_onchange = function (info) {
							const platform = info.newValue;
							$p.parent().scwin.txt_project_all_step_platform = platform;

							const productType = step1_select_product_type.getValue();

							// 프로그램언어조회
							scwin.getProgrammingInfo(platform);

							if (productType == "WMatrix") {
								const BuilderID = step1_select_builder_settingbox.getValue();
								if (!!BuilderID) {
									// template version 조회
									scwin.send_to_builder_template_version_action(BuilderID, platform, productType);
								}
							} else {
								scwin.android_bundle_id_group_show_and_hide();
							}

							// signing profile 조회
							scwin.getSigningKeySettingInfoToAdmin(platform);


							// vcs profile 조회
							// scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");
						};

						// builder select 변경시 이벤트
						// builder 변경시 재조회 > 템플릿버전 재조회
						scwin.step1_select_builder_onchange = function (info) {
							const builderID = info.newValue;
							const platformName = step1_select_platform.getValue();
							const productType = step1_select_product_type.getValue();

							// template version 재조회
							if (productType == "WMatrix" && builderID != "") {
								// template 버전조회
								scwin.send_to_builder_template_version_action(builderID, platformName, productType);
							}

							// signingkey 재조회
							// scwin.getSigningKeySettingInfoToAdmin(platformName);
						};

						scwin.step1_select_templatev_onchange = function (info) {
							if (info.newValue == "") {
								scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");
							} else {
								scwin.getVCSSettingFindAdminAndTypeInfo("localgit,localsvn");
							}
						};
						/// 이벤트 END

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project_add_title',style:'',tagname:'h2'}}]},{T:1,N:'xf:group',A:{id:'',class:'step_bar',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_step1',tagname:'li',class:'on'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'',useLocale:'true',localeRef:'lbl_input_project'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'grp_step2',tagname:'li',style:'',class:''},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_app_default_info'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'grp_step3',tagname:'li',style:''},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_plugin_setting'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_input_project'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'display:none;',id:'fr_grp_proj_save',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_confirm_create_project'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_create_general',style:'',type:'button',useLocale:'true',localeRef:'lbl_create','ev:onclick':'scwin.btn_create_general_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_project_name'}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_name',style:'',userData2:'',id:'',label:'',class:'ico_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'step1_input_projectname',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'step1_btn_check_project_name',type:'button',class:'btn_cm pt',useLocale:'true',localeRef:'lbl_dup_check','ev:onclick':'scwin.step1_btn_check_project_name_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_add_step01_product'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_add_step01_tooltip_product',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_product_type',disabledClass:'w2selectbox_disabled',useLocale:'true',useItemLocale:'true',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'false',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_product_type_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_general_app'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'generalApp'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_wmatrix'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'WMatrix'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'OS',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_app_os_env',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_platform',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_platform_onchange',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_platform_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'role_code_name'}},{T:1,N:'xf:value',A:{ref:'role_code_name'}}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_build_server_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_build_server_profile_tip',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_builder_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.step1_select_builder_onchange',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_builder_setting_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'builder_name'}},{T:1,N:'xf:value',A:{ref:'builder_id'}}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:'display:none;',id:'step1_android_bundle_id_group'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_android_appid'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{id:'step1_android_bundle_id',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'step1_language_and_wmatrixversion_grp'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th ',id:'step1_select_platform_language_settingbox_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project_add_step01_lang',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_app_language',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td',id:'step1_select_platform_language_settingbox_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_platform_language_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_language_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'role_code_name'}},{T:1,N:'xf:value',A:{ref:'role_code_name'}}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th ',id:''},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project_setting_step01_wmatrix_version',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_wmatrix_version_tooltip',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td',id:''},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_templatev',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false',chooseOptionLabelLocaleRef:'lbl_select','ev:onchange':'scwin.step1_select_templatev_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_wmatrix_version_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'wmatrix_version'}},{T:1,N:'xf:value',A:{ref:'wmatrix_id'}}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_vcs_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_vcs_profile_tooltip',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_vcs_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_vcs_setting_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'vcs_name'}},{T:1,N:'xf:value',A:{ref:'vcs_id'}}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project_setting_step01_internal_dist_server_profile',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_internal_dist_server_profile_tooltip',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_ftp_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_ftp_setting_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'ftp_name'}},{T:1,N:'xf:value',A:{ref:'ftp_id'}}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_sign_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'signing_profile_tooltip',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'scwin.changeToolTipContentAddStep1',useLocale:'true',tooltipLocaleRef:'lbl_sign_profile_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{id:'step1_select_signingKey_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'true',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false',chooseOptionLabelLocaleRef:'lbl_select'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_signing_profile_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'key_name'}},{T:1,N:'xf:value',A:{ref:'key_id'}}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_explain',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_descript_project',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:textarea',A:{style:'width:100%;height: 82px;',id:'step1_txtarea_desc',placeholder:'',class:''}}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_next',id:'btn_next',useLocale:'true',localeRef:'lbl_next',style:'',type:'button','ev:onclick':'scwin.btn_next_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]})