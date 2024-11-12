/*amd /cm/ui/works/project_deploy_task_setting_step02.xml 31090 4b208fe59cbcbdf1726336db8fb3e7e4f443edee03410cd5a085a958d2af08d5 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.platformType = "";
						scwin.onpageload = function() {

							const deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							scwin.platformType = deployTaskData[0].platform;

							if(scwin.platformType == "Android"){
								grp_android.setStyle("display", "table");
								grp_iOS.setStyle("display", "none");

							}else if(scwin.platformType == "iOS"){
								grp_iOS.setStyle("display", "table");
								grp_android.setStyle("display", "none");

							}

							scwin.initPage();

						};

						scwin.onpageunload = function() {

						};

						scwin.initPage = function(){
							// var build_project_json = $p.parent().dtl_build_setting_step1.getAllJSON();
							// 환경설정 List 조회

							// TODO : test deploy task api
							scwin.searchStep1Data();


						};

						scwin.selectBuildProject = function(build_project_json){
							step1_input_projectname.setValue(build_project_json[0].project_name);
							step1_select_platform.setText(build_project_json[0].platform, false);
							step1_txtarea_desc.setValue(build_project_json[0].description);

							//
							$p.parent().scwin.txt_project_all_step_platform = build_project_json[0].platform;

							// project setting localStorage data set!
							// localStorage.setItem("buildProjectName",build_project_json[0].project_name);
							// localStorage.setItem("buildPlatform",build_project_json[0].platform);
							// localStorage.setItem("projectsetting_pid",build_project_json[0].project_id);
							// localStorage.setItem("_platform_",step1_select_platform.getValue());

						};


						scwin.searchStep1Data = function(){

							const obj = $p.parent().scwin.getResultDeployConfigData;

							if(scwin.platformType == "Android"){

								step2_android_input_short_description.setValue(obj.short_description);
								step2_android_input_full_description.setValue(obj.full_description);
								step2_android_input_title.setValue(obj.title);
								step2_android_input_video.setValue(obj.video);

							}else if(scwin.platformType == "iOS"){

								step2_ios_input_apple_tv_privacy_policy.setValue(obj.apple_tv_privacy_policy);
								step2_ios_input_copyright.setValue(obj.copyright);
								step2_ios_input_demo_password.setValue(obj.demo_password);
								step2_ios_input_demo_user.setValue(obj.demo_user);
								step2_ios_input_description.setValue(obj.description);
								step2_ios_input_email_address.setValue(obj.email_address);
								step2_ios_input_first_name.setValue(obj.first_name);
								step2_ios_input_keywords.setValue(obj.keywords);
								step2_ios_input_last_name.setValue(obj.last_name);
								step2_ios_input_marketing_url.setValue(obj.marketing_url);
								step2_ios_input_name.setValue(obj.name);
								step2_ios_input_notes.setValue(obj.notes);
								step2_ios_input_phone_number.setValue(obj.phone_number);
								step2_ios_input_primary_category.setValue(obj.primary_category);
								step2_ios_input_primary_first_sub_category.setValue(obj.primary_first_sub_category);
								step2_ios_input_primary_second_sub_category.setValue(obj.primary_second_sub_category);
								step2_ios_input_privacy_url.setValue(obj.privacy_url);
								step2_ios_input_promotional_text.setValue(obj.promotional_text);
								step2_ios_input_release_notes.setValue(obj.release_notes);
								step2_ios_input_secondary_category.setValue(obj.secondary_category);
								step2_ios_input_secondary_first_sub_category.setValue(obj.secondary_first_sub_category);
								step2_ios_input_secondary_second_sub_category.setValue(obj.secondary_second_sub_category);
								step2_ios_input_subtitle.setValue(obj.subtitle);
								step2_ios_input_support_url.setValue(obj.support_url);


							}

						};


						scwin.saveStep1Data = async function(){

							const deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							let project_name = "";//step1_input_projectname.getValue();
							let platform = "";//step1_select_platform.getValue();
							// var build_server_url = step1_input_buildserver.getValue();

							let description = "";//step1_txtarea_desc.getValue();

							// workspace id 값 세팅
							const local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
							const local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

							let deployMetadataSetJson = {};



							if(scwin.platformType == "Android"){

								deployMetadataSetJson.full_description = step2_android_input_full_description.getValue();
								deployMetadataSetJson.short_description = step2_android_input_short_description.getValue();
								deployMetadataSetJson.title = step2_android_input_title.getValue();
								deployMetadataSetJson.video = step2_android_input_video.getValue();


							}else if(scwin.platformType == "iOS"){

								deployMetadataSetJson.primary_category = step2_ios_input_primary_category.getValue();
								deployMetadataSetJson.copyright = step2_ios_input_copyright.getValue();
								deployMetadataSetJson.primary_first_sub_category = step2_ios_input_primary_first_sub_category.getValue();
								deployMetadataSetJson.primary_second_sub_category = step2_ios_input_primary_second_sub_category.getValue();
								deployMetadataSetJson.secondary_category = step2_ios_input_secondary_category.getValue();
								deployMetadataSetJson.secondary_first_sub_category = step2_ios_input_primary_first_sub_category.getValue();
								deployMetadataSetJson.secondary_second_sub_category = step2_ios_input_secondary_second_sub_category.getValue();
								deployMetadataSetJson.name = step2_ios_input_name.getValue();
								deployMetadataSetJson.keywords = step2_ios_input_keywords.getValue();
								deployMetadataSetJson.apple_tv_privacy_policy = step2_ios_input_apple_tv_privacy_policy.getValue();
								deployMetadataSetJson.description = step2_ios_input_description.getValue();
								deployMetadataSetJson.marketing_url = step2_ios_input_marketing_url.getValue();
								deployMetadataSetJson.privacy_url = step2_ios_input_privacy_url.getValue();
								deployMetadataSetJson.promotional_text = step2_ios_input_promotional_text.getValue();
								deployMetadataSetJson.release_notes = step2_ios_input_release_notes.getValue();
								deployMetadataSetJson.subtitle = step2_ios_input_subtitle.getValue();
								deployMetadataSetJson.support_url = step2_ios_input_support_url.getValue();
								deployMetadataSetJson.demo_password = step2_ios_input_demo_password.getValue();
								deployMetadataSetJson.demo_user = step2_ios_input_demo_user.getValue();
								deployMetadataSetJson.email_address = step2_ios_input_email_address.getValue();
								deployMetadataSetJson.first_name = step2_ios_input_first_name.getValue();
								deployMetadataSetJson.last_name = step2_ios_input_last_name.getValue();
								deployMetadataSetJson.notes = step2_ios_input_notes.getValue();
								deployMetadataSetJson.phone_number = step2_ios_input_phone_number.getValue();


							}

							let data = {};
							data.project_id = parseInt(local_buildproject_id);
							data.workspace_id = parseInt(local_workspace_id);
							data.project_name = project_name;
							data.platform = platform;
							data.description = description;
							data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.
							data.deployMetadataSetJson = deployMetadataSetJson;

							const url = common.uri.deploySettingMetadataUpdate;
							const headers = {"Content-Type": "application/json; charset=UTF-8"};
							const body = data;

							await common.http.fetchPost(url,headers, body)
								.then(async(res) => {

								}).catch((err)=>{
									common.win.alert("error status:"+err.status+", message:"+err.message);
								});

						};

						scwin.btn_ios_prev_onclick = function(e){
							$p.parent().scwin.selected_step(1);
						};

						scwin.btn_android_prev_onclick = function(e){
							$p.parent().scwin.selected_step(1);
						};

						scwin.btn_ios_next_onclick = function(e){
							// 저장 플레그 추가
							scwin.saveStep1Data();
						};

						scwin.btn_android_next_onclick = function(e){
							// 저장 플레그 추가
							scwin.saveStep1Data();
						};

						scwin.btn_next_onclick = function(e){
							// 저장 플레그 추가
							scwin.saveStep1Data();


						};

						scwin.getDeployTaskDataSearchStatus = function(obj){

							switch (obj.status) {
								case "readdata":
									const message = common.getLabel("lbl_app_config_list");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "CONFIGUPDATE" :

									break;
								case "DONE" :

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

						scwin.setDeployMatadataUpdateStatus = async function(obj){
							let message;
							switch (obj.status) {
								case "setMetadata":
									message = common.getLabel("lbl_app_config_list");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "DONE" :
									WebSquare.layer.hideProcessMessage();
									message = common.getLabel("lbl_changed_project_deploy_meta_config");
									if(await common.win.alert(message)){
										$p.parent().scwin.selected_step(3);
									}
									break;
								case "SUCCESSFUL":

									break;
								case "FAILED":
									break;
								default :
									break;
							}

						};


					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_setting_deploy_metadata',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_iOS',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:190px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'apple tv privacy_policy',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'apple_tv_privacy_policy',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_apple_tv_privacy_policy'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_apple_tv_privacy_policy',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'copyright',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'copyright',userData2:'',tooltipLocaleRef:'lbl_deploy_tooltip_copyright',useLocale:'true'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_copyright',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'demo_password',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'demo_password',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_demo_password'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_demo_password',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_deploy_demo_user'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'demo_user',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_demo_user'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_demo_user',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_description'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'description',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_description'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_description',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_email_address'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'email_address',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_email_address'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_email_address',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_first_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'first_name',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_first_name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_first_name',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_keywords'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'keywords',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_keywords'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_keywords',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_last_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'last_name',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_last_name'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_last_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_marketing_url'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'marketing_url',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_marketing_url'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_marketing_url',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'ios_name',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_name',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_notes'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'notes',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_notes'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_notes',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_phone_number'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'phone_number',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_phone_number'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_phone_number',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_primary_category'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'primary_category',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_primary_category'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_primary_category',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_primary_first_sub_category'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'primary_first_sub_category',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_primary_first_sub_category'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_primary_first_sub_category',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_primary_second_sub_category'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'primary_second_sub_category',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_primary_second_sub_category'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_primary_second_sub_category',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_privacy_url'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'privacy_url',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_privacy_url'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_privacy_url',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_promotional_text'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'promotional_text',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_promotional_text'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_promotional_text',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_release_notes'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'release_notes',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_release_notes'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_release_notes',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_secondary_category'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'secondary_category',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_secondary_category'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_secondary_category',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_secondary_first_sub_category'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'secondary_first_sub_category',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_secondary_first_sub_category'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_secondary_first_sub_category',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_secondary_second_sub_category'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'secondary_second_sub_category',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_secondary_second_sub_category'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_secondary_second_sub_category',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_subtitle'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'subtitle',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_subtitle'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_subtitle',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'max-width: 160px;',userData2:'',useLocale:'true',localeRef:'lbl_deploy_ios_support_url'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'support_url',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_support_url'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_support_url',style:'width:100%;'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'grp_android',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:190px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'short_description',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'short_description',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_short_description'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_short_description',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'full_description',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'full_description',userData2:'',tooltipLocaleRef:'lbl_deploy_tooltip_full_description',useLocale:'true'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_full_description',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'title',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'Deploy title',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_title'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_title',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'video',ref:'',style:'max-width: 160px;',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'video',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_deploy_tooltip_video'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_video',style:'width:100%;'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_prev',id:'',style:'',type:'button','ev:onclick':'scwin.btn_ios_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm step_next',id:'',style:'',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})