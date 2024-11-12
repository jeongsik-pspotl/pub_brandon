/*amd /cm/ui/settings/setting_vcs_detail.xml 13039 0069835d395ca494c919c0ea43ac590f249c4cf63749be051d9c914c0203b895 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.check_vcs_nameYN = false;
						scwin.onpageload = function () {
							const paramData = $p.getParameter("tabParam");
							const vcs_setting_mode = paramData.vcs_setting_mode;

							if (vcs_setting_mode === "detailview") {
								const view = common.getLabel("lbl_vcs_setting");
								const save = common.getLabel("lbl_save");
								vcs_setting_title.setLabel(view);
								vcs_create_or_save_btn.setLabel(save);
								vcs_setting_name.setDisabled(true);
								vcs_setting_url.setDisabled(false);
								btm_vcs_name_dup_check.setDisabled(true);

								scwin.vcsDetailView();
							} else {
								const create = common.getLabel("lbl_vcs_setting");
								const regist = common.getLabel("lbl_regist");
								vcs_setting_title.setLabel(create);
								vcs_create_or_save_btn.setLabel(regist);
								vcs_setting_name.setDisabled(false);
								vcs_setting_url.setDisabled(false);
							}
						};

						scwin.checkDataVCSName = function () {
							const vcs_name = vcs_setting_name.getValue();

							if (common.isEmptyStr(vcs_name)) {
								const message = common.getLabel("lbl_vcs_setting_input_name");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						scwin.checkAllVCS = function () {
							const vcs_name = vcs_setting_name.getValue();
							const vcs_type = vcs_setting_vcs_type_code.getValue();

							if (common.isEmptyStr(vcs_name)) {
								var message = common.getLabel("lbl_vcs_setting_input_name");
								common.win.alert(message);
								return false;
							}

							if (!scwin.check_vcs_nameYN) {
								const message = common.getLabel("lbl_vcs_setting_check_duplicate");
								common.win.alert(message);
								return false;
							}

							if (vcs_type === "git" && vcs_setting_user_id.getValue() === "") {
								const message = common.getLabel("lbl_vcs_setting_input_git_id");
								common.win.alert(message);
								return false;
							}

							if (vcs_type === "git" && vcs_setting_user_pwd.getValue() === "") {
								const message = common.getLabel("lbl_vcs_setting_input_git_password");
								common.win.alert(message);
								return false;
							}

							if (vcs_type === "git" && vcs_setting_url.getValue() === "") {
								const message = common.getLabel("lbl_vcs_setting_input_git_url");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						/**
						 * vcs setting 조회 api
						 */
						scwin.vcsDetailView = async function () {
							const paramData = $p.getParameter("tabParam");
							const vcs_id = paramData.vcs_id;

							const url = common.uri.getVCSSettingDetailByID(parseInt(vcs_id));
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.setVCSSettingData(data);
						};

						scwin.setVCSSettingData = function (vcsData) {
							if (vcsData != null) {
								vcs_setting_vcs_type_code.setValue(vcsData.vcs_type);
								vcs_setting_user_id.setValue(vcsData.vcs_user_id);
								vcs_setting_user_pwd.setValue(vcsData.vcs_user_pwd);
								vcs_setting_name.setValue(vcsData.vcs_name);

								if (vcsData.vcs_type !== "localgit") {
									vcs_setting_url.setValue(vcsData.vcs_url);
								}
							}
						};

						scwin.saveVCSSettingData = function () {
							const paramData = $p.getParameter("tabParam");
							const vcs_setting_mode = paramData.vcs_setting_mode;

							let data = {};
							data.vcs_id = paramData.vcs_id;
							data.vcs_type = vcs_setting_vcs_type_code.getValue();
							data.vcs_user_id = vcs_setting_user_id.getValue();
							data.vcs_user_pwd = vcs_setting_user_pwd.getValue();
							data.vcs_name = vcs_setting_name.getValue();
							data.vcs_url = vcs_setting_url.getValue();

							if (vcs_setting_mode === "detailview") {
								scwin.setVCSSettingAndUpdate(data);
							} else {
								if (scwin.checkAllVCS()) {
									scwin.setVCSSettingAndInsert(data);
								}
							}
						};

						scwin.selectCheckVcsName = function (vcs_name) {
							let data = {};
							data.vcs_name = vcs_name;

							const url = common.uri.getVCSSettingCheckProfileName;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].vcs_name_not_found == "no") {
											message = common.getLabel("lbl_can_use_name");
											scwin.check_vcs_nameYN = true;

										} else if (res[0].vcs_name_not_found == "yes") {
											message = common.getLabel("lbl_exist_name");
											scwin.check_vcs_nameYN = false;
										}
										common.win.alert(message);
									}
								}).catch(() => {
								const message = common.getLabel("lbl_setting_vcs_detail_fail");
								common.win.alert(message);
							});
						};

						scwin.setVCSSettingAndInsert = function (vcs_setting_data) {
							const url = common.uri.setVCSSettingCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, vcs_setting_data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].result === "success" && res != null) {
											message = common.getLabel("lbl_vcs_setting_regist_success");

											let vcsTab = $p.top().tabc_layout.getTabInfo().filter((tab) => tab.id == "vcs");
											let vcsTabIndex;

											if (vcsTab.length > 0) {
												vcsTabIndex = vcsTab[0].currentTabIndex;

												let vcsTabWindow = $p.top().tabc_layout.getWindow(vcsTabIndex);
												vcsTabWindow.scwin.getVCSListInfo();
											}
										} else {
											message = common.getLabel("lbl_vcs_setting_regist_fail");
										}
										vcs_create_or_save_btn.setDisabled(true);
										common.win.alert(message);
									}
								}).catch(() => {
								let message = common.getLabel("lbl_vcs_setting_regist_fail");
								common.win.alert(message);
							});
						};

						scwin.setVCSSettingAndUpdate = function (vcs_setting_data) {
							const url = common.uri.setVCSSettingUpdate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, vcs_setting_data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].result === "success" && res != null) {
											message = common.getLabel("lbl_vcs_setting_update_success");
										} else {
											message = common.getLabel("lbl_vcs_setting_update_fail");
										}
										common.win.alert(message);
									}
								}).catch(() => {
								let message = common.getLabel("lbl_vcs_setting_update_fail");
								common.win.alert(message);
							});
						};

						scwin.vcsNameDupCheck = function () {
							const vcs_name = vcs_setting_name.getValue();

							if (scwin.checkDataVCSName()) {
								scwin.selectCheckVcsName(vcs_name);
							}
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_vcs_setting_detail'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'vcs_setting_title',label:'',class:'',useLocale:'true',localeRef:'lbl_vcs_setting_detail'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'vcs_create_or_save_btn',style:'',type:'button',useLocale:'true',localeRef:'lbl_save','ev:onclick':'scwin.saveVCSSettingData'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_version_manage_tool_profile'}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',style:'',userData2:'',id:'',label:'',class:'ico_tip ',useLocale:'true',tooltipLocaleRef:'lbl_vcs_setting_separator'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'vcs_setting_name',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btm_vcs_name_dup_check',type:'button',class:'btn_cm pt','ev:onclick':'scwin.vcsNameDupCheck',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_vcs_setting_tool_type'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_vcs_setting_tools_detail'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'vcs_setting_vcs_type_code',ref:'',style:'width: 100%;',submenuSize:'auto',useLocale:'true',useItemLocale:'true'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_local_git'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'localgit'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_github'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'git'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_vcs_setting_id'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_vcs_setting_id_detail'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'vcs_setting_user_id',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_vcs_setting_password'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',tagname:'span',tooltip:'tooltip',tooltipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_vcs_setting_password_detail'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'vcs_setting_user_pwd',dataType:'password',type:'password',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_vcs_setting_server_url'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',tagname:'span',tooltip:'tooltip',tooltipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_vcs_setting_server_url_detail'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{class:'',id:'vcs_setting_url',style:'width:100%;'}}]}]}]}]}]}]}]}]}]})