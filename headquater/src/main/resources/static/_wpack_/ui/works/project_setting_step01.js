/*amd /cm/ui/works/project_setting_step01.xml 25405 3642c5ca34eb9b5f50ec23feb4d94abe36c0a55225f593b32421c348b012cb35 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function () {
							scwin.initPage();
						};

						// project_setting > dtl_build_setting_step1에 데이터참조
						scwin.step01Data = $p.parent().dtl_build_setting_step1.getAllJSON();

						scwin.initPage = function () {
							scwin.selectBuildProject(scwin.step01Data[0]);

							step1_input_projectname.setDisabled(true);
							step1_select_platform.setDisabled(true);
							step1_select_product_type.setDisabled(true);
							step1_select_templatev.setDisabled(true);
							step1_select_builder.setDisabled(true);
							step1_select_vcs.setDisabled(true);
							step1_select_ftp.setDisabled(true);
							step1_select_platform_language.setDisabled(true);
							step1_input_project_dir_path_name.setDisabled(true);
							step1_select_signingKey.setDisabled(true);
							step1_input_project_create_date.setDisabled(true);
							step1_input_project_updated_date.setDisabled(true);

							const update_yn = $p.top().scwin.__update_yn__;

							if (update_yn != "1") {
								btn_save.hide();
							}
						};

						scwin.selectBuildProject = function (build_project_json) {
							step1_input_projectname.setValue(build_project_json.project_name);
							step1_select_platform.addItem(build_project_json.platform, build_project_json.platform);
							step1_select_templatev.addItem(build_project_json.template_version, build_project_json.template_version);
							step1_select_platform_language.addItem(build_project_json.platform_language, build_project_json.platform_language);
							step1_input_project_dir_path_name.setValue(build_project_json.project_dir_path);
							step1_txtarea_desc.setValue(build_project_json.description);
							step1_select_ftp.setValue(build_project_json.ftp_id, false);
							step1_input_project_create_date.setValue(build_project_json.created_date.replace(/T/g, ' '));
							step1_input_project_updated_date.setValue(build_project_json.updated_date.replace(/T/g, ' '));
							step1_select_product_type.addItem(build_project_json.product_type, build_project_json.product_type);
							scwin.getBranchSettingInfo(build_project_json.builder_id);

							$p.parent().scwin.txt_project_all_step_platform = build_project_json.platform;

							if (build_project_json.vcs_id != "") {
								scwin.getVCSSettingInfo(build_project_json.vcs_id);
							}

							scwin.getFTPSettingInfo(build_project_json.ftp_id);

							if (build_project_json.platform == "Android") {
								scwin.getSigningKeySettingInfo(build_project_json.key_id);
							} else {
								scwin.getiOSKeySettingInfo(build_project_json.key_id);
							}

							// step 2 app config, step 3 server config list set
							if (build_project_json.product_type == "WMatrix") {
								if (build_project_json.platform == "Android") {
									scwin.getAndroidConfig();
								} else {
									scwin.getiOSAllGetInformation();
								}
								step1_language_and_wmatrixversion_grp.setStyle("display", "table-row");
							} else {
								// 상태바 숨김 (step2,step3,step4)
								grp_step2.setStyle("display", "none");
								grp_step3.setStyle("display", "none");


								// language & wmatrix version 표시
								step1_language_and_wmatrixversion_grp.setStyle("display", "none");

								btn_next.hide();
							}
						};

						scwin.saveStep1Data = function () {
							if (Array.isArray(scwin.step01Data)) {
								const project_name = step1_input_projectname.getValue();
								const project_dir_path = step1_input_project_dir_path_name.getValue();
								const description = step1_txtarea_desc.getValue();
								const local_workspace_id = scwin.step01Data[0].workspace_id;
								const local_buildproject_id = scwin.step01Data[0].project_id;
								const platform = scwin.step01Data[0].platform;
								const template_version = scwin.step01Data[0].template_version;

								let data = {};
								data.project_id = parseInt(local_buildproject_id);
								data.workspace_id = parseInt(local_workspace_id);
								data.project_name = project_name;
								data.platform = platform;
								data.template_version = template_version;
								data.project_dir_path = project_dir_path;
								data.description = description;
								data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.
								$p.parent().dtl_build_setting_step1.setJSON([data]);

								const uri = common.uri.updateProjSetting;
								return common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, data);
							}
						};

						// 미사용
						scwin.getPlatformProgramLanguageInfo = function (role_code_id) {
							const uri = common.uri.projectSerachProgramLanguage(role_code_id);
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								step1_select_platform_language.addItem(data.role_code_name, data.role_code_name);
							}).catch((err) => {
								common.win.alert("code:" + err.status + ", message:" + err.message);
							});
						};

						// builder selectbox setting
						scwin.getBranchSettingInfo = function (builder_id) {
							const uri = common.uri.branchSetting(builder_id);
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								step1_select_builder.addItem(data.builder_id, data.builder_name);
							}).catch((err) => {
								common.win.alert("code:" + err.status + ", message:" + err.message);
							});
						};

						// vcs selectbox setting
						scwin.getVCSSettingInfo = function (vcs_id) {
							const uri = common.uri.vcsSearchProfileId(vcs_id);
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								if (data != null) {
									step1_select_vcs.addItem(data.vcs_id, data.vcs_name);
								}
							}).catch((err) => {
								common.win.alert("code:" + err.status + "\nmessage:" + err.message);
							});
						};

						// ftp selectbox setting
						scwin.getFTPSettingInfo = function (ftp_id) {
							const uri = common.uri.ftpSetting(ftp_id);
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								if (data != null) {
									step1_select_ftp.addItem(data.ftp_id, data.ftp_name);
								}
							}).catch((err) => {
								common.win.alert("code:" + err.status + "\nmessage:" + err.message);
							});
						};

						// android key id 기준 조회
						scwin.getSigningKeySettingInfo = function (signingkey_id) {
							const uri = common.uri.androidSearchProflie(signingkey_id);
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								if (data != null) {
									step1_select_signingKey.addItem(data.key_id, data.key_name);
								}
							}).catch((err) => {
								common.win.alert("code:" + err.status + "\nmessage:" + err.message);
							});
						};

						// ios key id 기준 조회
						scwin.getiOSKeySettingInfo = function (key_id) {
							const uri = common.uri.iosSearchProfile(key_id);
							common.http.fetchGet(uri, "GET", {"Content-Type": "application/json"}).then((res) => {
								return res.json();
							}).then((data) => {
								step1_select_signingKey.addItem(data.key_id, data.key_name);
							}).catch((err) => {
								common.win.alert("code:" + err.status + "\nmessage:" + err.message);
							});
						};

						// Android AppConfig 조회
						scwin.getAndroidConfig = function () {
							let data = {};
							data.build_id = scwin.step01Data[0].project_id;
							data.workspace_id = scwin.step01Data[0].workspace_id;
							data.project_name = scwin.step01Data[0].project_name;
							data.project_dir_path = scwin.step01Data[0].project_dir_path;
							data.product_type = scwin.step01Data[0].product_type;
							data.platform = scwin.step01Data[0].platform;

							const uri = common.uri.getAndoridAllGetconfig;
							common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, data).then((data) => {
								// websocket에서 데이터반환
								// scwin.setBuilderAllAppConfigStatus로 데이터와 함께 호출됨.
							}).catch((err) => {
								common.win.alert("code:" + err.status + "\nmessage:" + err.message);
							});
						};

						// iOS AppConfig 조회
						scwin.getiOSAllGetInformation = function () {

							// getinformation 조회 호출 전 파라미터 값 설정
							let data = {};
							data.build_id = scwin.step01Data[0].project_id;
							data.workspace_id = scwin.step01Data[0].workspace_id;
							data.project_name = scwin.step01Data[0].project_name;
							data.project_dir_path = scwin.step01Data[0].project_dir_path;
							data.product_type = scwin.step01Data[0].product_type;
							data.platform = scwin.step01Data[0].platform;

							const uri = common.uri.getiOSGetInformation;
							common.http.fetch(uri, "POST", {"Content-Type": "application/json"}, data).then((data) => {
								// websocket에서 데이터반환
								// scwin.setBuilderAllAppConfigStatus로 데이터와 함께 호출됨.
							}).catch((err) => {
								common.win.alert("code:" + err.status + "\nmessage:" + err.message);
							});
						};

						// websocket callback
						scwin.setBuilderAllAppConfigStatus = function (obj) {
							let message = ""
							switch (obj.message) {
								case "SEARCHING":
									message = common.getLabel("lbl_app_config_list");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "CONFIGUPDATE" :
									message = common.getLabel("lbl_project_setting_step01_js_server_config_update");
									WebSquare.layer.showProcessMessage(message); //Server Config Update
									break;
								case "DONE" :
									WebSquare.layer.hideProcessMessage();
									break;
								case "SUCCESSFUL":
									WebSquare.layer.hideProcessMessage();
									scwin.selectBuildAppConfigListData(scwin.step01Data[0].platform, obj);
									break;
								case "FAILED":
									WebSquare.layer.hideProcessMessage();
									message = common.getLabel("lbl_app_config_check_failed");
									common.win.alert(message);
									break;
								default :
									break;
							}
						};

						scwin.selectBuildAppConfigListData = function (type, obj) {
							if (type == 'iOS') {
								$p.parent().scwin.getResultAppConfigData = obj;
							} else if (type == 'Android') {
								$p.parent().scwin.getResultAppConfigData = obj.resultMultiProfileConfigListObj;
							}
						};

						scwin.changeToolTipContentSettingStep1 = function (componentId, label) {
							const platform = localStorage.getItem("_platform_");
							let message = "";
							switch (platform) {
								case "Android":
									message = common.getLabel("lbl_sign_profile_tip");
									return message
								case "iOS":
									message = common.getLabel("lbl_ios_sign_profile");
									return message
								default:
									return ""
							}
						};
						// 다음버튼 이벤트
						scwin.btn_next_onclick = async function (e) {
							// 저장 플레그 추가
							const newValue = step1_txtarea_desc.getValue();
							const oldValue = scwin.step01Data[0].description;

							if (oldValue != newValue) {
								const message = common.getLabel("lbl_save_changed");
								if (await common.win.alert(message)) {
									scwin.saveStep1Data().then((data) => {
										if (data != null && data[0].result == "success") {
											if (scwin.step01Data[0].product_type == 'WMatrix') {
												$p.parent().scwin.selected_step(2);
											}
										}
									}).catch((err) => {
										common.win.alert("code:" + err.status + ", message:" + err.message);
									});
								}
							} else {
								if (scwin.step01Data[0].product_type == 'WMatrix') {
									$p.parent().scwin.selected_step(2);
								}
							}

						};
						// 저장버튼 이벤트
						scwin.btn_save_onclick = function (e) {
							// 저장 플레그 추가
							const newValue = step1_txtarea_desc.getValue();
							const oldValue = scwin.step01Data[0].description;
							if (oldValue != newValue) {
								scwin.saveStep1Data().then((data) => {
									if (data != null && data[0].result == "success") {
										const message = common.getLabel("lbl_saved");
										common.win.alert(message);
									}
								}).catch((err) => {
									common.win.alert("code:" + err.status + ", message:" + err.message);
								});
							}
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_input_project_setting'}}]},{T:1,N:'xf:group',A:{id:'',class:'step_bar',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',tagname:'li',class:'on'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'',useLocale:'true',localeRef:'lbl_project_setting_step01_project_setting'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'grp_step2',tagname:'li',style:'',class:''},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_app_default_info'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'grp_step3',tagname:'li',style:''},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_project_add_step03_title'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_project_setting_step01_project_setting'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:''},E:[{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step01_saveQuestion'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_save',style:'',type:'button',useLocale:'true',localeRef:'lbl_save','ev:onclick':'scwin.btn_save_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_project_name'}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_name',style:'',userData2:'',id:'',label:'',class:'ico_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'step1_input_projectname',class:'',disabled:'true'}}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_add_step01_product'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',useLocale:'true',toolTip:'tooltip',tooltipLocaleRef:'lbl_project_add_step01_tooltip_product',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_product_type',renderType:'native',style:'',submenuSize:'auto'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_os'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_osSetting'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'step1_select_platform',chooseOption:'false',style:'',submenuSize:'auto',allOption:'false',disabled:'false',direction:'auto',appearance:'minimal',disabledClass:'w2selectbox_disabled',renderType:'native'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_build_server_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',useLocale:'true',toolTip:'tooltip',tooltipLocaleRef:'lbl_project_setting_step01_build_server_profile_tooltip',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_builder',renderType:'native',style:'',submenuSize:'auto'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'step1_language_and_wmatrixversion_grp'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_developLanguage'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_developLanguage_tooltip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_platform_language',renderType:'native',style:'',submenuSize:'auto'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_wmatrix_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',userData2:'',toolTip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_wmatrix_version_tooltip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_templatev',renderType:'native',style:'',submenuSize:'auto'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_vcs_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_vcs_profile_tooltip',userData2:''}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_vcs',style:'',submenuSize:'auto',renderType:'native'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_internal_dist_server_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_internal_dist_server_profile_tooltip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_ftp',style:'',submenuSize:'auto',renderType:'native'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_sign_profile'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'scwin.changeToolTipContentSettingStep1',useLocale:'true',tooltipLocaleRef:'lbl_sign_profile_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'false',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step1_select_signingKey',style:'',submenuSize:'auto',renderType:'native'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',localeRef:'lbl_project_setting_step01_project_path',useLocale:'true'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',userData2:'',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_project_path_tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{class:'',disabled:'true',id:'step1_input_project_dir_path_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_createDate'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',disabled:'true',id:'step1_input_project_create_date',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_project_setting_step01_modifyDate'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',disabled:'true',id:'step1_input_project_updated_date',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_explain'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_descript_project'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:textarea',A:{style:'width:100%;height: 82px;',id:'step1_txtarea_desc',placeholder:'',class:''}}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_next',id:'btn_next',style:'',type:'button',useLocale:'true',localeRef:'lbl_next','ev:onclick':'scwin.btn_next_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]})