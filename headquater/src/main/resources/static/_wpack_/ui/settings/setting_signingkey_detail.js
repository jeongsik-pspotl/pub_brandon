/*amd /cm/ui/settings/setting_signingkey_detail.xml 31589 ae0e7b4b245e58b31a399ed91e93d06a4f10376f2f3362fb8224c07b3d730dac */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dlt_builder_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'builder_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'builder_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_platform_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_type',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_key_id',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'key_id',name:'name1'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.checkProvisionFileYn = false;
						scwin.checkSigningkeyNameYn = false;
						scwin.signingkeyLastCnt = "";

						scwin.onpageload = function () {
							scwin.signingKeyData = $p.getParameter("tabParam");

							let uri = common.uri.platformList;
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then(async (res) => {
								const data = await res.json()
								dlt_platform_selectbox.setJSON(data);

								// builder list 정보를 불러와서 dlt_builder_setting_selectbox에 저장한다
								scwin.getBuilderList(scwin.modeSetting);
							}).catch(e => {
								common.win.alert("platform code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
							})
						};

						scwin.getBuilderList = function (callback) {
							const url = common.uri.getBuilderList;
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							common.http.fetchGet(url, method, headers)
								.then(res => res.json())
								.then(data => {
									if (Array.isArray(data)) {
										dlt_builder_setting_selectbox.setJSON(data);
										callback();
									}
								})
								.catch(err => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");
								});
						};

						scwin.modeSetting = function () {
							let message;
							if (scwin.signingKeyData.key_setting_mode.toLowerCase() === "detailcreate") {
								// 생성 시, 초기 탭 보임
								android_keys.show();
								ios_keys.hide();
								scwin.drawTab("android", null);

								message = common.getLabel("lbl_create");

								input_profile_name.setDisabled(false);
								input_build_server_profile.setDisabled(false);
								input_platform.setDisabled(false);
							} else {
								// android, ios 타입에 따른 화면 변경
								if (scwin.signingKeyData.key_platform.toLowerCase() === "android") {
									android_keys.show();
									ios_keys.hide();

									scwin.getAndroidKeySettingData();
								} else {
									android_keys.hide();
									ios_keys.show();

									scwin.getIosKeysettingData();
								}
								message = common.getLabel("lbl_save");

								input_profile_name.setDisabled(true);
								input_build_server_profile.setDisabled(true);
								input_platform.setDisabled(true);
								btn_profile_name_dup_check.setDisabled(true);
							}
							key_create_or_save_btn.setLabel(message);
						};

						scwin.getAndroidKeySettingData = function () {
							const url = common.uri.getAndroidCert(scwin.signingKeyData.key_id);
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							common.http.fetchGet(url, method, headers)
								.then(res => res.json())
								.then(data => {
									scwin.drawTab('android', data);
								})
								.catch(err => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");
								});
						};

						scwin.getIosKeysettingData = function () {
							const url = common.uri.getIosCert(scwin.signingKeyData.key_id);
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							common.http.fetchGet(url, method, headers)
								.then(res => res.json())
								.then(data => {
									scwin.drawTab("ios", data);
								})
								.catch(err => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");
								});
						};

						scwin.add_tab = function (tab_id_input, tab_control, dataObj, tab_src) {
							const tab_id = tab_id_input;
							const tab_opt = {
								'label': tab_id,
							}

							const tab_cont = {
								'src': tab_src,
								'wframe': true,
								'scope': true,
								'dataObject': {
									'type': 'json',
									'name': 'keyParam',
									'data': dataObj ? dataObj : null
								}
							}

							if (tab_src != '') {
								tab_control.addTab(tab_id, tab_opt, tab_cont);
							}
						};

						scwin.drawTab = function (platform, data) {
							if (platform === "android") {
								if (!!data) {
									input_profile_name.setValue(data.key_name);
									input_build_server_profile.setValue(data.builder_id);
									input_platform.setValue(data.platform);

									dlt_key_id.setJSON([{"key_id": data.key_id}]);

									let androidKeyBuildData = {};
									androidKeyBuildData.android_key_alias = data.android_key_alias;
									androidKeyBuildData.android_key_password = data.android_key_password;
									androidKeyBuildData.android_key_store_password = data.android_key_store_password;
									androidKeyBuildData.android_key_path = data.android_key_path;

									let menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010100");
									let tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010100");
									scwin.add_tab(menu_key, tab_android_key_detail, androidKeyBuildData, tab_src);

									menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010200");
									tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010200");
									scwin.add_tab(menu_key, tab_android_key_detail, {'android_deploy_key_path': data.android_deploy_key_path}, tab_src);
								} else {
									let menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010100");
									let tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010100");
									scwin.add_tab(menu_key, tab_android_key_detail, null, tab_src);

									menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010200");
									tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010200");
									scwin.add_tab(menu_key, tab_android_key_detail, null, tab_src);
								}
							}
							// iOS
							else {
								if (!!data) {
									input_profile_name.setValue(data.key_name);
									input_build_server_profile.setValue(data.builder_id);
									input_platform.setValue(data.platform);

									dlt_key_id.setJSON([{"key_id": data.key_id}]);

									let menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010300");
									let tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010300");
									scwin.add_tab(menu_key, tab_ios_key_detail, {'ios_profiles_json': data.ios_profiles_json}, tab_src);

									menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010400");
									tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010400");
									scwin.add_tab(menu_key, tab_ios_key_detail, {'ios_certificates_json': data.ios_certificates_json}, tab_src);

									let deployData = {};
									deployData.ios_key_path = data.ios_key_path;
									deployData.ios_issuer_id = data.ios_issuer_id;
									deployData.ios_key_id = data.ios_key_id;

									menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010500");
									tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010500");
									scwin.add_tab(menu_key, tab_ios_key_detail, deployData, tab_src);
								} else {
									let menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010300");
									let tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010300");
									scwin.add_tab(menu_key, tab_ios_key_detail, null, tab_src);

									menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010400");
									tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010400");
									scwin.add_tab(menu_key, tab_ios_key_detail, null, tab_src);

									menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010500");
									tab_src = $p.top().scwin.convertMenuCodeToPath("m0304010500");
									scwin.add_tab(menu_key, tab_ios_key_detail, null, tab_src);
								}
							}
						};

						scwin.selectCheckKeyName = function () {
							const key_name = input_profile_name.getValue();

							if (common.isEmptyStr(key_name)) {
								const message = common.getLabel("lbl_check_key_name");
								common.win.alert(message);
								return false;
							}

							let data = {};
							data.key_name = key_name;

							const url = common.uri.checkProfileName;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].keyNameCheck === "no") {
											message = common.getLabel("lbl_exist_key_cert_name");
											scwin.checkSigningkeyNameYn = false;
										} else {
											message = common.getLabel("lbl_can_use_key_cert_name");
											scwin.checkSigningkeyNameYn = true;
										}
										common.win.alert(message);
									}
								});
						};

						scwin.changePlatform = function () {
							const platform_type = input_platform.getValue();

							if (platform_type.toLowerCase() === "android") {
								android_keys.show();
								ios_keys.hide();
								scwin.drawTab("android", null);
							} else {
								android_keys.hide();
								ios_keys.show();
								scwin.drawTab("ios", null);
							}
						};

						scwin.saveSigningKeySettingData = async function (mode) {
							let append_deploy_config_data = {};
							let tap_all_certificate_config = [];
							let tap_all_profile_config = [];
							let tap_file_profile_config = [];
							let tap_file_certificate_config = [];

							if (mode === "detailcreate") {
								if (common.isEmptyStr(input_profile_name.getValue())) {
									const message = common.getLabel("lbl_check_key_cert_name");
									common.win.alert(message);
									return false;
								}

								if (!scwin.checkSigningkeyNameYn) {
									const message = common.getLabel("lbl_check_dup_key");
									common.win.alert(message)
									return false;
								}
							}

							if (input_platform.getValue().toLowerCase() === "android") {
								for (let tab_cnt = 0; tab_cnt < tab_android_key_detail.getTabCount(true); tab_cnt++) {
									await tab_android_key_detail.setSelectedTabIndex(tab_cnt);
									let tabFrame = tab_android_key_detail.getFrame(tab_cnt);
									tab_android_key_detail.activateTab(tab_cnt);

									// android build tab
									if (!!tabFrame.getObj("android_key_alias")) {
										append_deploy_config_data.build_android_key_alias = tabFrame.getObj("android_key_alias").getValue();
										append_deploy_config_data.build_android_key_password = tabFrame.getObj("android_alias_password").getValue();
										append_deploy_config_data.build_android_store_password = tabFrame.getObj("android_key_store_password").getValue();

										append_deploy_config_data.build_android_key_id = dlt_key_id.getRowJSON(0).key_id;

										const fileId = tabFrame.getObj("android_keystore_file").id;
										const files = document.querySelector("#" + fileId).files;
										const fileLength = files.length;

										if (fileLength > 0) {
											const file = files[0];
											append_deploy_config_data.buildfile_path = file;

											if (!!file && common.checkAllInputText("CHECK_INPUT_TYPE_KOR", file.name)) {
												const message = common.getLabel("lbl_key_form_kor");
												common.win.alert(message);
												return false;
											}
										} else {
											append_deploy_config_data.buildfile_path = "";

											//validate
											const message = common.getLabel("lbl_signingkey_setting_input_store_key_file");
											common.win.alert(message);
											return false;
										}

										// validate
										if (common.isEmptyStr(append_deploy_config_data.build_android_key_alias)) {
											const message = common.getLabel("lbl_signingkey_setting_input_key_alias");
											common.win.alert(message);
											return false;
										}

										if (common.isEmptyStr(append_deploy_config_data.build_android_key_password)) {
											const message = common.getLabel("lbl_signingkey_setting_input_android_key_password");
											common.win.alert(message);
											return false;
										}

										if (common.isEmptyStr(append_deploy_config_data.build_android_store_password)) {
											const message = common.getLabel("lbl_signingkey_setting_input_android_store_password");
											common.win.alert(message);
											return false;
										}
									}

									// android deploy tab
									if (!!tabFrame.getObj("android_json_key_file")) {
										const fileId = tabFrame.getObj("android_json_key_file").id;
										const files = document.querySelector("#" + fileId).files;
										const fileLength = files.length;

										if (fileLength > 0) {
											const file = files[0];
											append_deploy_config_data.deployfile_path = file;

											if (!!file && common.checkAllInputText("CHECK_INPUT_TYPE_KOR", file.name)) {
												const message = common.getLabel("lbl_signingkey_setting_json_key_error");
												common.win.alert(message);
												return false;
											}
										}
										// 안드로이드 json 키는 플레이스토어 배포용이기 때문에, 배포를 하지 않으면 필수가 아니다.
										// 따라서, 해당 키를 넣지 않아도 정상적으로 인증서 등록이 되어야 한다.
										// else {
										// 	append_deploy_config_data.deployfile_path = "";
										//
										//     //validate
										// 	const message = common.getLabel("lbl_signingkey_setting_input_json_key_file");
										//     common.win.alert(message);
										//     return false;
										// }
									}
								}
							} else if (input_platform.getValue().toLowerCase() === "ios") {
								for (let tab_cnt = 0; tab_cnt < tab_ios_key_detail.getTabCount(true); tab_cnt++) {
									await tab_ios_key_detail.setSelectedTabIndex(tab_cnt);
									let tabFrame = tab_ios_key_detail.getFrame(tab_cnt);
									tab_ios_key_detail.activateTab(tab_cnt);

									// ios profiles tab
									if (!!tabFrame.getObj("ios_profile_first")) {
										let append_profile_config_data = {};

										append_profile_config_data.profile_name = tabFrame.getObj("ios_profile_first").getValue();
										append_profile_config_data.profile_type = tabFrame.getObj("ios_distribution_type_first").getValue();

										const fileId = tabFrame.getObj("ios_provisioning_profile_first").id;
										const files = document.querySelector("#" + fileId).files;
										const fileLength = files.length;

										if (fileLength > 0) {
											const file = files[0];
											append_profile_config_data.profile_path = file;

											if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", file.name)) {
												const message = common.getLabel("lbl_key_form_kor");
												common.win.alert(message);
												return false;
											}
										} else {
											append_profile_config_data.profile_path = "";

											//validate
											const message = common.getLabel("lbl_signingkey_setting_input_profile");
											common.win.alert(message);
											return false;
										}

										// validate
										if (common.isEmptyStr(append_profile_config_data.profile_name)) {
											const message = common.getLabel("lbl_signingkey_setting_input_profile_name");
											common.win.alert(message);
											return false;
										}

										if (common.isEmptyStr(append_profile_config_data.profile_type)) {
											const message = common.getLabel("lbl_signingkey_setting_input_profile_type");
											common.win.alert(message);
											return false;
										}

										tap_all_profile_config.push(new Blob([JSON.stringify(append_profile_config_data)], {type: "application/json"}));
										tap_file_profile_config.push(append_profile_config_data);

										const gen_ios_profile = $p.getComponentById("tab_ios_key_detail").getWindow(0).gen_ios_profile;

										if (gen_ios_profile.getLength() > 0) {
											append_profile_config_data = {};

											for (let pf = 0; pf < gen_ios_profile.getLength(); pf++) {
												append_profile_config_data.profile_name = gen_ios_profile.getChild(pf, "ios_profile").getValue();
												append_profile_config_data.profile_type = gen_ios_profile.getChild(pf, "ios_distribution_type").getValue();

												const fileId = "#gen_ios_profile_" + pf + "_ios_provisioning_profile";
												const files = document.querySelector(fileId).files;
												const fileLength = files.length;

												if (fileLength > 0) {
													const file = files[0];
													append_profile_config_data.profile_path = file;

													if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", file.name)) {
														const message = common.getLabel("lbl_key_form_kor");
														common.win.alert(message);
														return false;
													}
												} else {
													append_profile_config_data.profile_path = "";

													//validate
													const message = common.getLabel("lbl_signingkey_setting_input_profile");
													common.win.alert(message);
													return false;
												}

												// validate
												if (common.isEmptyStr(append_profile_config_data.profile_name)) {
													const message = common.getLabel("lbl_signingkey_setting_input_profile_name");
													common.win.alert(message);
													return false;
												}

												if (common.isEmptyStr(append_profile_config_data.profile_type)) {
													const message = common.getLabel("lbl_signingkey_setting_input_profile_type");
													common.win.alert(message);
													return false;
												}

												tap_all_profile_config.push(new Blob([JSON.stringify(append_profile_config_data)], {type: "application/json"}));
												tap_file_profile_config.push(append_profile_config_data);
											}
										}
									}

									// Certificate Tab
									if (!!tabFrame.getObj("ios_certificate_name_first")) {
										let append_certificate_config_data = {};

										append_certificate_config_data.certificate_name = tabFrame.getObj("ios_certificate_name_first").getValue();
										append_certificate_config_data.certificate_password = tabFrame.getObj("ios_certificate_password_first").getValue();

										const fileId = tabFrame.getObj("ios_certificate_file_first").id;
										const files = document.querySelector("#" + fileId).files;
										const fileLength = files.length;

										if (fileLength > 0) {
											const file = files[0];
											append_certificate_config_data.certificate_path = file;

											if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", file.name)) {
												const message = common.getLabel("lbl_key_form_kor");
												common.win.alert(message);
												return false;
											}
										} else {
											append_certificate_config_data.certificate_path = "";

											const message = common.getLabel("lbl_signingkey_setting_input_certificate");
											common.win.alert(message);
											return false;
										}

										// validate
										if (common.isEmptyStr(append_certificate_config_data.certificate_name)) {
											const message = common.getLabel("lbl_signingkey_setting_input_certificate_name");
											common.win.alert(message);
											return false;
										}

										if (common.isEmptyStr(append_certificate_config_data.certificate_password)) {
											const message = common.getLabel("lbl_signingkey_setting_input_certificate_password");
											common.win.alert(message);
											return false;
										}

										tap_all_certificate_config.push(new Blob([JSON.stringify(append_certificate_config_data)], {type: "application/json"}));
										tap_file_certificate_config.push(append_certificate_config_data);
									}

									// Deploy Tab
									if (!!tabFrame.getObj("ios_appstore_connect_key_id")) {
										append_deploy_config_data.deploy_ios_issuer_id = tabFrame.getObj("ios_appstore_connect_issuer_id").getValue();
										append_deploy_config_data.deploy_ios_key_id = tabFrame.getObj("ios_appstore_connect_key_id").getValue();

										append_deploy_config_data.build_ios_key_id = dlt_key_id.getRowJSON(0).key_id;

										const fileId = tabFrame.getObj("ios_appstore_connect_file").id;
										const files = document.querySelector("#" + fileId).files;
										const fileLength = files.length;

										// Deploy 인증서는 스토어 배포할때 뺴고 필요하지 않으므로, 필수가 아니다.
										// 따라서, 인증서 등록시에는 Deploy 탭에 값을 입력하거나 파일을 업로드 한 경우가 아니라면
										// 따로 벨리데이션 체크를 하지 않는다.
										if (!common.isEmptyStr(append_deploy_config_data.deploy_ios_issuer_id)
											|| !common.isEmptyStr(append_deploy_config_data.deploy_ios_key_id)
											|| fileLength > 0) {

											if (fileLength > 0) {
												const file = files[0];
												append_deploy_config_data.deploy_path = file;

												if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", file.name)) {
													const message = common.getLabel("lbl_key_form_kor");
													common.win.alert(message);
													return false;
												}
											} else {
												append_deploy_config_data.deploy_path = "";

												const message = common.getLabel("lbl_signingkey_setting_input_appstore_connect_key_file");
												common.win.alert(message);
												return false;
											}

											// validate
											if (common.isEmptyStr(append_deploy_config_data.deploy_ios_issuer_id)) {
												const message = common.getLabel("lbl_signingkey_setting_input_appstore_connect_key_key_id");
												common.win.alert(message);
												return false;
											}

											if (common.isEmptyStr(append_deploy_config_data.deploy_ios_key_id)) {
												const message = common.getLabel("lbl_signingkey_setting_input_appstore_connect_key_file");
												common.win.alert(message);
												return false;
											}
										}
									}
								}
							}

							let formData = new FormData();

							formData.append("key_name", input_profile_name.getValue());
							formData.append("platform", input_platform.getValue());

							if (input_platform.getValue().toLowerCase() === "android") {
								formData.append("file", append_deploy_config_data.buildfile_path);
								formData.append("key_type", "all");
								formData.append("key_password", append_deploy_config_data.build_android_key_password);
								formData.append("key_alias", append_deploy_config_data.build_android_key_alias);
								formData.append("store_key_password", append_deploy_config_data.build_android_store_password);
								formData.append("deployFile", append_deploy_config_data.deployfile_path);
								formData.append("builder_id", input_build_server_profile.getValue());

								if (mode === "detailview") {
									formData.append("key_id", append_deploy_config_data.build_android_key_id);
								}

								key_create_or_save_btn.setDisabled(true);
								scwin.setKeySettingAndroidInsert(mode, formData);

							}
							// iOS
							else {
								formData.append("builder_id", input_build_server_profile.getValue());

								if (mode === "detailview") {
									formData.append("key_id", append_deploy_config_data.build_ios_key_id);
								}

								for (let i = 0; i < tap_file_profile_config.length; i++) {
									let profile_configJson = tap_file_profile_config[i];
									formData.append("profiles", profile_configJson.profile_path);
								}

								formData.append("jsonProfiles[]", JSON.stringify(tap_file_profile_config));

								for (let j = 0; j < tap_file_certificate_config.length; j++) {
									let certificate_configJson = tap_file_certificate_config[j];
									formData.append("certificates", certificate_configJson.certificate_path);
								}

								formData.append("jsonCertificates[]", JSON.stringify(tap_file_certificate_config));

								if (!!append_deploy_config_data.deploy_path) {
									formData.append("deployFile", append_deploy_config_data.deploy_path);
								} else {
									formData.append("deployFile", null);
								}

								if (!!append_deploy_config_data.deploy_ios_issuer_id) {
									formData.append("ios_issuer_id", append_deploy_config_data.deploy_ios_issuer_id);
								} else {
									formData.append("ios_issuer_id", "");
								}

								if (!!append_deploy_config_data.deploy_ios_key_id) {
									formData.append("ios_key_id", append_deploy_config_data.deploy_ios_key_id);
								} else {
									formData.append("ios_key_id", "");
								}

								key_create_or_save_btn.setDisabled(true);
								scwin.setKeySettingAlliOSInsert(mode, formData);
							}
						};

						scwin.setKeySettingAndroidInsert = async function (mode, formData) {

							const url = (mode === "detailcreate") ? common.uri.androidCertCreate : common.uri.androidCertUpdate;
							const method = "POST";

							await common.http.fetchFileUpload(url, method, formData);

							scwin.signingKeyListUpdate();
						};

						scwin.setKeySettingAlliOSInsert = async function (mode, formData) {
							const url = (mode === "detailcreate") ? common.uri.iosCertCreate : common.uri.iosCertUpdate;
							const method = "POST";

							await common.http.fetchFileUpload(url, method, formData);

							scwin.signingKeyListUpdate();
						};

						scwin.signingKeyListUpdate = function () {
							let signingKeyTab = $p.top().tabc_layout.getTabInfo().filter((tab) => tab.id == "certificate");
							let signingKeyTabIndex;

							if (signingKeyTab.length > 0) {
								signingKeyTabIndex = signingKeyTab[0].currentTabIndex;

								let signingKeyTabWindow = $p.top().tabc_layout.getWindow(signingKeyTabIndex);
								signingKeyTabWindow.scwin.getKeySettingInfo();
							}
						};

						scwin.btnSigningKeySettingOnclick = function () {
							const mode = scwin.signingKeyData.key_setting_mode.toLowerCase();

							if (scwin.checkData()) {
								scwin.saveSigningKeySettingData(mode);
							}
						};

						scwin.checkData = function () {
							const mode = scwin.signingKeyData.key_setting_mode.toLowerCase();

							if (mode === "detailcreate") {
								if (common.isEmptyStr(input_profile_name.getValue())) {
									const message = common.getLabel("lbl_check_key_cert_name");
									common.win.alert(message);
									return false;
								}
								;

								if (common.isEmptyStr(input_build_server_profile.getValue())) {
									const message = common.getLabel("lbl_signingkey_setting_set_builder");
									common.win.alert(message);
									return false;
								}

								if (common.isEmptyStr(input_platform.getValue())) {
									const message = common.getLabel("lbl_signingkey_setting_set_platform");
									common.win.alert(message);
									return false
								}
							}

							return true;
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_signingkey_setting'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',label:'',useLocale:'true',localeRef:'lbl_signingkey_setting_detail'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'key_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btnSigningKeySettingOnclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_signingkey_setting_detail_name'}},{T:1,N:'w2:textbox',A:{tagname:'span',tooltip:'tooltip',class:'ico_tip',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_profile_name'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_profile_name',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_profile_name_dup_check',type:'button',class:'btn_cm pt',useLocale:'true',localeRef:'lbl_dup_check','ev:onclick':'scwin.selectCheckKeyName'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',useLocale:'true',localeRef:'lbl_signingkey_setting_build_server_profile_id'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_separator'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'true',direction:'auto',disabled:'false',chooseOptionLabelLocaleRef:'lbl_select',disabledClass:'w2selectbox_disabled',id:'input_build_server_profile',style:'width: 100%;',submenuSize:'auto'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_builder_setting_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'builder_name'}},{T:1,N:'xf:value',A:{ref:'builder_id'}}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_os'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',useLocale:'true',tooltip:'tooltip',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_os_type'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'input_platform',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true','ev:onviewchange':'scwin.changePlatform'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dlt_platform_selectbox'},E:[{T:1,N:'xf:label',A:{ref:'role_code_name'}},{T:1,N:'xf:value',A:{ref:'role_code_name'}}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'tbcbox',id:'android_keys',style:''},E:[{T:1,N:'w2:tabControl',A:{alwaysDraw:'false',class:'tbc',confirmFalseAction:'new',confirmTrueAction:'exist',id:'tab_android_key_detail',style:'',tabScroll:'',useConfirmMessage:'false',useMoveNextTabFocus:'false',useTabKeyOnly:'true'}}]},{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'tbcbox',id:'ios_keys',style:''},E:[{T:1,N:'w2:tabControl',A:{alwaysDraw:'false',class:'tbc',confirmFalseAction:'new',confirmTrueAction:'exist',id:'tab_ios_key_detail',style:'',tabScroll:'',useConfirmMessage:'false',useMoveNextTabFocus:'false',useTabKeyOnly:'true'}}]}]}]}]}]}]})