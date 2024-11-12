/*amd /cm/ui/works/project_deploy_task_setting_step01.xml 34288 14d633edd5c87423011dd710d87b2a51706b3f75aadf80a36b766ec43367570d */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.platformType = "";
						scwin.onpageload = function() {
							$p.parent().scwin.selected_step(1);

							let deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							scwin.platformType = deployTaskData[0].platform;

							if(scwin.platformType == "Android"){
								grp_android.show();
								grp_iOS.hide();

							}else if(scwin.platformType == "iOS"){
								grp_iOS.show();
								grp_android.hide();

							}

							scwin.initPage();

						};

						scwin.onpageunload = function() {

						};

						scwin.initPage = function(){
							// let build_project_json = $p.parent().dtl_build_setting_step1.getAllJSON();
							// 환경설정 List 조회

							scwin.searchStep1Data();


						};

						scwin.selectBuildProject = function(build_project_json){
							step1_input_projectname.setValue(build_project_json[0].project_name);
							step1_select_platform.setText(build_project_json[0].platform, false);
							step1_txtarea_desc.setValue(build_project_json[0].description);

							//
							$p.parent().scwin.txt_project_all_step_platform = build_project_json[0].platform;

							// project setting localStorage data set!
							localStorage.setItem("buildProjectName",build_project_json[0].project_name);
							localStorage.setItem("buildPlatform",build_project_json[0].platform);
							localStorage.setItem("projectsetting_pid",build_project_json[0].project_id);
							localStorage.setItem("_platform_",step1_select_platform.getValue());

						};


						scwin.searchStep1Data = async function(){

							let deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							let project_name = ""; //step1_input_projectname.getValue();
							scwin.platformType = deployTaskData[0].platform; //step1_select_platform.getValue();

							let description = "";//step1_txtarea_desc.getValue();

							// workspace id 값 세팅
							let local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
							let local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

							let data = {};
							data.project_id = parseInt(local_buildproject_id);
							data.workspace_id = parseInt(local_workspace_id);
							data.project_name = project_name;
							data.platform = scwin.platformType;
							data.description = description;
							data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.

							const url = common.uri.searchDeploySettingTaskList;
							const headers = {"Content-Type": "application/json; charset=UTF-8"};
							const body = data;

							await common.http.fetchPost(url, headers, body)
								.then(async(res) => {

								}).catch((err)=>{
									common.win.alert("error status:"+err.status+", message:"+err.message);
								});

						};


						scwin.saveStep1Data = async function(){

							let deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							let project_name = "";//step1_input_projectname.getValue();
							let platform = "";//step1_select_platform.getValue();

							let description = "";//step1_txtarea_desc.getValue();

							// workspace id 값 세팅
							let local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
							let local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

							let deployTaskSetJson = {};

							if(scwin.platformType == "Android"){

								let skip_upload_aab = step1_input_android_skip_upload_aab.getValue();
								let skip_upload_metadata = step1_input_android_skip_upload_metadata.getValue();
								let skip_upload_changelogs = step1_input_android_skip_upload_changelogs.getValue();
								let skip_upload_images = step1_input_android_skip_upload_images.getValue();
								let skip_upload_screenshots = step1_input_android_skip_upload_screenshots.getValue();
								let validate_only = step1_input_android_validate_only.getValue();
								let changes_not_sent_for_review = step1_input_android_changes_not_sent_for_review.getValue();
								let rescue_changes_not_sent_for_review = step1_input_android_rescue_changes_not_sent_for_review.getValue();
								let ack_bundle_installation_warning = step1_input_android_ack_bundle_installation_warning.getValue();

								deployTaskSetJson.skip_upload_aab = skip_upload_aab;
								deployTaskSetJson.skip_upload_metadata = skip_upload_metadata;
								deployTaskSetJson.skip_upload_changelogs = skip_upload_changelogs;
								deployTaskSetJson.skip_upload_images = skip_upload_images;
								deployTaskSetJson.skip_upload_screenshots = skip_upload_screenshots;
								deployTaskSetJson.validate_only = validate_only;
								deployTaskSetJson.changes_not_sent_for_review = changes_not_sent_for_review;
								deployTaskSetJson.rescue_changes_not_sent_for_review = rescue_changes_not_sent_for_review;
								deployTaskSetJson.ack_bundle_installation_warning = ack_bundle_installation_warning;

							}else if(scwin.platformType == "iOS"){

								let skip_submission = step1_input_skip_submission.getValue();
								let skip_waiting_for_build_processing = step1_input_skip_waiting_for_build_processing.getValue();
								let distribute_only =  step1_input_distribute_only.getValue();
								let uses_non_exempt_encryption = step1_input_uses_non_exempt_encryption.getValue();
								let distribute_external = step1_input_distribute_external.getValue();
								let expire_previous_builds = step1_input_expire_previous_builds.getValue();
								let reject_build_waiting_for_review = step1_input_reject_build_waiting_for_review.getValue();
								let submit_beta_review = step1_input_submit_beta_review.getValue();

								deployTaskSetJson.skip_submission = skip_submission;
								deployTaskSetJson.skip_waiting_for_build_processing = skip_waiting_for_build_processing;
								deployTaskSetJson.distribute_only = distribute_only;
								deployTaskSetJson.uses_non_exempt_encryption = uses_non_exempt_encryption;
								deployTaskSetJson.distribute_external = distribute_external;
								deployTaskSetJson.expire_previous_builds = expire_previous_builds;
								deployTaskSetJson.reject_build_waiting_for_review = reject_build_waiting_for_review;
								deployTaskSetJson.submit_beta_review = submit_beta_review;
							}



							let data = {};
							data.project_id = parseInt(local_buildproject_id);
							data.workspace_id = parseInt(local_workspace_id);
							data.project_name = project_name;
							data.platform = platform;
							data.description = description;
							data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.
							data.deployTaskSetJson = deployTaskSetJson;

							// localStorage.setItem("__project_name__",data.project_name);
							// localStorage.setItem("__project_id__",data.project_id);
							// localStorage.setItem("__workspace_id__",data.workspace_id);

							const url = common.uri.DeploySettingTaskDataUpdate;
							const headers = {"Content-Type": "application/json; charset=UTF-8"};
							const body = data;

							await common.http.fetchPost(url, headers, body)
								.then(async(res) => {

								}).catch((err)=>{
									common.win.alert("error status:"+err.status+", message:"+err.message);
								});

						};

						scwin.btn_prev_onclick = function(e){

						};

						scwin.btn_next_onclick = function(e){
							// 저장 플레그 추가
							scwin.saveStep1Data();

						};

						scwin.getDeployTaskDataSearchStatus = function(obj){

							switch (obj.status) {
								case "readdata":
									let message = common.getLabel("lbl_app_config_list");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "CONFIGUPDATE" :

									break;
								case "DONE" :

									if(scwin.platformType == "Android"){
										step1_input_android_skip_upload_aab.setValue(obj.jsonDeploy.SKIP_UPLOAD_AAB, false);
										step1_input_android_skip_upload_metadata.setValue(obj.jsonDeploy.SKIP_UPLOAD_METADATA, false);
										step1_input_android_skip_upload_changelogs.setValue(obj.jsonDeploy.SKIP_UPLOAD_CHANGELOGS, false);
										step1_input_android_skip_upload_images.setValue(obj.jsonDeploy.SKIP_UPLOAD_IMAGES, false);
										step1_input_android_skip_upload_screenshots.setValue(obj.jsonDeploy.SKIP_UPLOAD_SCREENSHOTS, false);
										step1_input_android_validate_only.setValue(obj.jsonDeploy.VALIDATE_ONLY, false);
										step1_input_android_changes_not_sent_for_review.setValue(obj.jsonDeploy.CHANGES_NOT_SENT_FOR_REVIEW, false);
										step1_input_android_rescue_changes_not_sent_for_review.setValue(obj.jsonDeploy.RESCUE_CHANGES_NOT_SENT_FOR_REVIEW, false);
										step1_input_android_ack_bundle_installation_warning.setValue(obj.jsonDeploy.ACK_BUNDLE_INSTALLATION_WARNING, false);
										$p.parent().scwin.getResultDeployConfigData = obj.jsonDeployMetaData;


									}else if(scwin.platformType == "iOS"){
										step1_input_skip_submission.setValue(obj.jsonDeploy.SKIP_SUBMISSION, false);
										step1_input_skip_waiting_for_build_processing.setValue(obj.jsonDeploy.SKIP_WAITING_FOR_BUILD_PROCESSING, false);
										step1_input_distribute_only.setValue(obj.jsonDeploy.DISTRIBUTE_ONLY, false);
										step1_input_uses_non_exempt_encryption.setValue(obj.jsonDeploy.USES_NON_EXEMPT_ENCRYPTION, false);
										step1_input_distribute_external.setValue(obj.jsonDeploy.DISTRIBUTE_EXTERNAL, false);
										step1_input_expire_previous_builds.setValue(obj.jsonDeploy.EXPIRE_PREVIOUS_BUILDS, false);
										step1_input_reject_build_waiting_for_review.setValue(obj.jsonDeploy.REJECT_BUILD_WAITING_FOR_REVIEW, false);
										step1_input_submit_beta_review.setValue(obj.jsonDeploy.SUBMIT_BETA_REVIEW, false);
										$p.parent().scwin.getResultDeployConfigData = obj.jsonDeployMetaData;

									}

									// alert("Deplay Task Config 조회 했습니다.");
									WebSquare.layer.hideProcessMessage();
									break;
								case "SUCCESSFUL":

									break;
								case "FAILED":
									break;
								default :
									break;
							}

						};

						scwin.setDeployTaskDataUpdateStatus = async function(obj){

							switch (obj.status) {
								case "setEnv":
									let message = common.getLabel("lbl_app_config_list");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "DONE" :

									if(scwin.platformType == "Android"){
										step1_input_android_skip_upload_aab.setValue(obj.jsonDeploy.SKIP_UPLOAD_AAB, false);
										step1_input_android_skip_upload_metadata.setValue(obj.jsonDeploy.SKIP_UPLOAD_METADATA, false);
										step1_input_android_skip_upload_changelogs.setValue(obj.jsonDeploy.SKIP_UPLOAD_CHANGELOGS, false);
										step1_input_android_skip_upload_images.setValue(obj.jsonDeploy.SKIP_UPLOAD_IMAGES, false);
										step1_input_android_skip_upload_screenshots.setValue(obj.jsonDeploy.SKIP_UPLOAD_SCREENSHOTS, false);
										step1_input_android_validate_only.setValue(obj.jsonDeploy.VALIDATE_ONLY, false);
										step1_input_android_changes_not_sent_for_review.setValue(obj.jsonDeploy.CHANGES_NOT_SENT_FOR_REVIEW, false);
										step1_input_android_rescue_changes_not_sent_for_review.setValue(obj.jsonDeploy.RESCUE_CHANGES_NOT_SENT_FOR_REVIEW, false);
										step1_input_android_ack_bundle_installation_warning.setValue(obj.jsonDeploy.ACK_BUNDLE_INSTALLATION_WARNING, false);


									}else if(scwin.platformType == "iOS"){
										step1_input_skip_submission.setValue(obj.jsonDeploy.SKIP_SUBMISSION, false);
										step1_input_skip_waiting_for_build_processing.setValue(obj.jsonDeploy.SKIP_WAITING_FOR_BUILD_PROCESSING, false);
										step1_input_distribute_only.setValue(obj.jsonDeploy.DISTRIBUTE_ONLY, false);
										step1_input_uses_non_exempt_encryption.setValue(obj.jsonDeploy.USES_NON_EXEMPT_ENCRYPTION, false);
										step1_input_distribute_external.setValue(obj.jsonDeploy.DISTRIBUTE_EXTERNAL, false);
										step1_input_expire_previous_builds.setValue(obj.jsonDeploy.EXPIRE_PREVIOUS_BUILDS, false);
										step1_input_reject_build_waiting_for_review.setValue(obj.jsonDeploy.REJECT_BUILD_WAITING_FOR_REVIEW, false);
										step1_input_submit_beta_review.setValue(obj.jsonDeploy.SUBMIT_BETA_REVIEW, false);

									}

									WebSquare.layer.hideProcessMessage();
									message = common.getLabel("lbl_changed_project_deploy_task_config");
									if(await common.win.alert(message)){
										$p.parent().scwin.selected_step(2);
									};
									break;
								case "SUCCESSFUL":

									break;
								case "FAILED":
									break;
								default :
									break;
							}

						};

						function changeToolTipContentSettingStep1 (componentId, label) {
							let platform = scwin.platformType;
							switch (platform) {
								case "Android":
									let message_android_signprofile = common.getLabel("lbl_sign_profile_tip");
									return message_android_signprofile
								case "iOS":
									let message_ios_signprofile = common.getLabel("lbl_ios_sign_profile");
									return message_ios_signprofile
								default:
									return ""
							}

						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_project_deploy_task_title'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_iOS',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'skip submission',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'skip_submission',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_skip_submission'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_skip_submission',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'skip waiting for build processing',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'skip waiting for build processing',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_skip_waiting_for_build_processing'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_skip_waiting_for_build_processing',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'distribute only',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy distribute_only',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_distribute_only',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_distribute_only',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'uses non exempt encryption',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy uses non exempt_encryption',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_uses_non_exempt_encryption'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_uses_non_exempt_encryption',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'distribute externa',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy distribute_externa',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_distribute_externa'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_distribute_external',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'expire previous builds',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy expire_previous_builds',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_expire_previous_builds'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_expire_previous_builds',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'reject build waiting for review',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy submit_beta_review',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_reject_build_waiting_for_review'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_reject_build_waiting_for_review',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_deploy_submit_beta_review'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy submit_beta_review',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_submit_beta_review',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_submit_beta_review',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_android',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_deploy_task_skip_submission'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy skip_submission',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_skip_submission'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_skip_upload_aab',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',useLocale:'true',localeRef:'lbl_deploy_skip_waiting_for_build_processing',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy skip_waiting_for_build_processing',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_skip_waiting_for_build_processing'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_skip_upload_metadata',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_deploy_distribute_only'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy distribute_only',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_distribute_only'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_skip_upload_changelogs',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_deploy_uses_non_exempt_encryption',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy uses non exempt_encryption',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_uses_non_exempt_encryption'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_skip_upload_images',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_deploy_distribute_external',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy distribute_externa',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_distribute_externa'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_skip_upload_screenshots',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_deploy_expire_previous_builds',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy expire_previous_builds',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_expire_previous_builds'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_validate_only',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_deploy_reject_build_waiting_for_review',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'reject build waiting for review',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_reject_build_waiting_for_review'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_changes_not_sent_for_review',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_rescue_changes_not_sent_for_review'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'rescue_changes_not_sent_for_review',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_rescue_changes_not_sent_for_review'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_rescue_changes_not_sent_for_review',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',useLocale:'true',userData2:'',localeRef:'lbl_deploy_submit_beta_review'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy submit_beta_review',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_submit_beta_review'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_input_android_ack_bundle_installation_warning',ref:'',style:'width: 100%;',submenuSize:'auto',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_yes'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'true'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_no'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'false'}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_next',id:'',style:'',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})