/*amd /cm/ui/deploy/deploy.xml 14312 99e9c03be3f37ce4e802a59f5d96a9f6313831f2e7018122059eded7da22bd83 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.logData = "";
						let interval;
						let classBtnYn = false;
						let isStop = false;
						let deployActionData = $p.parent().__deployaction_data__.getAllJSON();
						let appConfigJSONData = {};
						let product_type;
						let ios_builded_target;

						scwin.onpageload = async function () {
							let deployProjectName = deployActionData[0].projectName;
							let platform = deployActionData[0].platform;

							let message_select_default = common.getLabel("lbl_select");
							let message_deploy = common.getLabel("lbl_deploy");
							let message_alpha_eng = common.getLabel("lbl_deploy_js_alpha_eng");
							let message_alpha = common.getLabel("lbl_deploy_js_alpha");
							let message_beta_eng = common.getLabel("lbl_deploy_js_beta_eng");
							let message_beta = common.getLabel("lbl_deploy_js_beta");
							let message_operate = common.getLabel("lbl_operate");
							let message_test = common.getLabel("lbl_deploy_js_test");

							txt_deploy_project_name.setValue(deployProjectName + " " + message_deploy);

							if (platform == "Android") {
								deploy_select_deploytype.addItem("", message_select_default);
								deploy_select_deploytype.addItem(message_alpha_eng, message_alpha); // alpha
								deploy_select_deploytype.addItem(message_beta_eng, message_beta); // Beta
								deploy_select_deploytype.addItem(message_deploy, message_operate); // Deploy
							} else {
								deploy_select_deploytype.addItem("", message_select_default);
								deploy_select_deploytype.addItem(message_beta_eng, message_test); // TestFileget
								deploy_select_deploytype.addItem(message_deploy, message_operate); // Deplop

								// iOS의 경우, project_history 테이블에서 빌드한 Target 이름 또는 bundle ID를 가져온다
								let url = common.uri.getBuildHistoryData(deployActionData[0].project_history_id);
								const method = "POST";
								const headers = {"Content-Type": "application/json"};

								await common.http.fetch(url, method, headers)
									.then(res => {
										if (!!(res.ios_builded_target_or_bundle_id)) {
											ios_builded_target = res.ios_builded_target_or_bundle_id;
										}
									});

								url = common.uri.getProductType(deployActionData[0].project_pkid);

								await common.http.fetch(url, method, headers)
									.then(res => {
										if(!!res[0].product_type) {
											product_type = res[0].product_type;
										}
									});

								if (product_type.toLowerCase() == "wmatrix") {
									await scwin.ios_get_config();
								} else {
									// what?
								}
							}
						};

						scwin.start_deploy_project = function (e) {
							let deployActionData = $p.parent().__deployaction_data__.getAllJSON();

							let sendDBdata = {};
							// 데이터 동적 처리
							sendDBdata.workspaceID = deployActionData[0].workspace_pkid;
							sendDBdata.projectID = deployActionData[0].project_pkid;
							sendDBdata.status = common.getLabel("lbl_deploy_js_start_distribute");
							sendDBdata.log_path = "";
							sendDBdata.message = "";
							sendDBdata.project_history_id = deployActionData[0].project_history_id;
							sendDBdata.platform = deployActionData[0].platform;

							scwin.setBranchDeployInsert(sendDBdata);
						};

						scwin.intervalYnfunc = function () {
							interval = setInterval(function () {
								if (!isStop) {
									if (classBtnYn) {
										project_deploy_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
										classBtnYn = false;
									} else {
										project_deploy_build_result.changeClass("btn_cm type3 stop", "btn_cm type2 stop");
										classBtnYn = true;
									}
								} else {

									clearInterval(interval);
									// 밖에서 선언한 interval을 안에서 중지시킬 수 있음
								}
							}, 500)
						};

						scwin.ios_get_config = async function (e) {
							const platform = deployActionData[0].platform;
							const project_id = deployActionData[0].project_pkid;
							const workspace_id = deployActionData[0].workspace_pkid;

							let data = {};
							data.platform = platform;
							data.projectID = parseInt(project_id);
							data.workspaceID = parseInt(workspace_id);

							if (platform.toLowerCase() == "ios") {
								data.profileType = ios_builded_target;
							}

							const url = common.uri.getMultiProfileAppConfig;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.justFetch(url, method, headers, data);
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
									const platform = deployActionData[0].platform;
									const resultMultiProfileAppConfig = data.resultMultiProfileConfigListObj;

									if (platform.toLowerCase() == "ios") {
										const tempResultOneProfile = resultMultiProfileAppConfig.targets;
										const profileType = ios_builded_target;
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

						scwin.setBranchDeployStatus = function (data) {
							switch (data.message) {
								case "STOP" :
									break;
								case "DEPLOYING" :
									const message_deploying = common.getLabel("lbl_deploy_js_distributing");
									project_deploy_build_result.setLabel(message_deploying);
									// deploy start
									let logData = data.logValue;
									if (!(btn4.hasClass("active")) && logData == "deploy start") {
										btn2_progress_img.changeClass("progress-0", "progress-50");
										// console.log("check deploy");
										document.getElementById("deploy_status_text_id").value = message_deploying;

										scwin.intervalYnfunc();
									}

									scwin.setBranchDeployLog(data);
									break;
								case "SUCCESSFUL" :
									const message_complete = common.getLabel("lbl_deploy_js_complete_distribute");
									const message_completed = common.getLabel("lbl_deploy_js_completed_distribute");
									project_deploy_build_result.setLabel(message_complete);
									isStop = true;

									btn2_progress_img.changeClass("progress-50", "progress-100");

									if (data.message == "SUCCESSFUL") {
										common.win.alert(message_completed);
									} else if (data.message == "FAILED") {

									}

									btn_log_download.setUserData("history_id", data.history_id);
									btn_log_download.show("");

									project_build_start.setDisabled(true);
									// resultLayer.show();
									scwin.logData = "";

									break;
								case "FAILED" :
									isStop = true;
									project_deploy_build_result.changeClass("btn_cm type2 stop", "btn_cm type3 stop");
									const message_deploy_fail = common.getLabel("lbl_deploy_js_fail_distribute");
									project_deploy_build_result.setLabel(message_deploy_fail);
									btn2_progress_img.changeClass("progress-50", "progress-100");

									btn_log_download.setUserData("history_id", data.history_id);
									btn_log_download.show("");

									project_build_start.setDisabled(true);
									// resultLayer.show();
									scwin.logData = "";

									break;
								case "CLEANBUILING" :
									scwin.setBranchDeployLog(data);
									break;
								default :
									break;
							}

						};

						// build log view
						scwin.setBranchDeployLog = function (data) {
							// txtarea_log
							let logData = data.logValue;
							let txtarea_log = document.getElementById("deploy_txtarea_log");
							txtarea_log.focus();
							logData = logData.replace(/\u001b[^m]*?m/g, "");
							scwin.logData += logData + "\n";

							txtarea_log.value = scwin.logData + "\n";
							txtarea_log.scrollTop = txtarea_log.scrollHeight - txtarea_log.clientHeight;

						};

						// db insert 처리 구간을 -> handler 내부에서 service 로 받아서 처리 방식 수정
						scwin.setBranchDeployInsert = function (data) {
							let sendData = {};
							let deployHistory = {};

							deployHistory.project_id = data.projectID;
							deployHistory.platform_type = data.platform;// platform type  수정하기
							sendData.workspace_id = data.workspaceID;
							sendData.deploy_type = deploy_select_deploytype.getValue();
							sendData.project_history_id = data.project_history_id;

							if (product_type.toLowerCase() == "generalapp") {
								sendData.ios_app_bundleID = ios_builded_target;
							}

							sendData.deployHistory = deployHistory;

							// TODO 공통 api 호출 기능 전환
							const url = common.uri.deployHistoryCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=UTF-8"};

							common.http.fetch(url, method, headers, sendData)
								.then((res) => {
									if (Array.isArray(res)) {
										if (res[0].has_deploy_key == "no") {
											let message = common.getLabel("lbl_signingkey_has_not_deploy_key");
											common.win.alert(message);
										} else {
											project_build_start.setDisabled(true);
										}
									}
								})
								.catch(err => {
									let message = request.responseJSON;
									common.win.alert("message:" + message.result + "\n");
								});
						};

						scwin.onpageunload = function () {
							scwin.logData = "";
							$p.parent().__deployaction_data__.removeAll();
						};

						scwin.resultLayer_onclose = function () {
							resultLayer.hide();
						};

						scwin.start_log_download_onclick = function (e) {

							let history_id = this.getUserData("history_id");

							let data = {};
							data.history_id = history_id;

							const url = common.uri.deployHistoryLogFileDownload;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=UTF-8"};

							common.http.justFetch(url, method, headers, data);

						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'txt_deploy_project_name',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_deploy_project_name'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'progress-btn','ev:onclick':'scwin.btn4_onclick',id:'btn4',style:'width: 100%;max-width: 450px;'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:data-progress-style',E:[{T:3,text:'indefinite'}]}]},{T:1,N:'xf:group',A:{class:'btn',id:'btn_progress_text',style:''}},{T:1,N:'xf:group',A:{class:'progress-0',id:'btn2_progress_img',style:''}}]},{T:1,N:'input',A:{id:'deploy_status_text_id',type:'hidden'}},{T:1,N:'xf:trigger',A:{class:'btn_cm type2 stop',id:'project_deploy_build_result',useLocale:'true',localeRef:'lbl_ready',style:'margin-left: 10px;',type:'button'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'nowrap',id:'build_select_buildtype_label',label:'',style:'text-wrap:nowrap;',tagname:'span',useLocale:'true',localeRef:'lbl_build_build_type'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'deploy_select_deploytype',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.selectedBuildInfo'},E:[{T:1,N:'xf:choices'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm type1 redo',id:'project_build_start',useLocale:'true',localeRef:'lbl_deploy_project_build_start',style:'',type:'button','ev:onclick':'scwin.start_deploy_project'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{id:'btn_log_download',style:'display:none;',class:'btn_cm type2 type4',outerDiv:'false',href:'',type:'button','ev:onclick':'scwin.start_log_download_onclick',useLocale:'true',localeRef:'lbl_log_download'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'textarea',A:{class:'',id:'deploy_txtarea_log',placeholder:'',style:'width:100%;height: 600px;'}}]}]},{T:1,N:'w2:floatingLayer',A:{id:'resultLayer',title:'',style:'position:absolute; display:none;width:190px; height:185px; z-index:9999;','ev:onclose':'scwin.resultLayer_onclose',useModal:'false',useLocale:'true',localeRef:'lbl_build_result_layer_title'},E:[{T:1,N:'div',A:{id:'qrcode',style:'height:150px;'}}]}]}]}]})