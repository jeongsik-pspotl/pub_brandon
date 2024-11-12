/*amd /ui/manager/userManager_domain_detail.xml 10097 debeb633a321b70c8981da7c21e91fa37361414eaec5b028cd6a860f87ce1e0f */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.domainCheckYn = false;
						scwin.onpageload = function() {
							let paramData = $p.getParameter("tabParam");
							let domain_setting_mode = paramData.domain_setting_mode;

							if (domain_setting_mode === "detailview") {
								const view = common.getLabel("lbl_userManager_domain_detail_domainView");
								const save = common.getLabel("lbl_save");
								domain_setting_title.setLabel(view);
								domain_create_or_save_btn.setLabel(save);
								domain_created_date.setDisabled(true);
								domain_updated_date.setDisabled(true);
								btn_dup_check.setDisabled(true);

								scwin.domainDetailView();
							} else {
								let view = common.getLabel("lbl_userManager_domain_detail_domainView");
								let create = common.getLabel("lbl_create");
								domain_setting_title.setLabel(view);
								domain_create_or_save_btn.setLabel(create);
								domain_created_date.setDisabled(true);
								domain_updated_date.setDisabled(true);
							}
						};

						scwin.checkData = function() {
							const domain_name = manager_domain_name.getValue();

							if (common.isEmptyStr(domain_name)) {
								let message = common.getLabel("lbl_userManager_domain_detail_blank");
								common.win.alert(message);
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", domain_name)) {
								let message = common.getLabel("lbl_can_not_special_char");
								common.win.alert(message);
								return false;
							}

							if(!scwin.domainCheckYn){
								let message = common.getLabel("lbl_userManager_domain_detail_buttonClick");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						scwin.domainDetailView = async function () {
							const paramData = $p.getParameter("tabParam");
							const domain_id = paramData.domain_id;

							const url = common.uri.getDomainDetail(domain_id);
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.domainDetailViewSetting(data);
						};

						scwin.domainDetailViewSetting = function(data) {
							let create_date = "";
							let updated_date = "";

							if (data.create_date != null) {
								create_date = data.create_date.replace(/T/g,' ');
							}

							if (data.updated_date != null) {
								updated_date = data.updated_date.replace(/T/g,' ');
							}

							manager_domain_name.setValue(data.domain_name);
							domain_created_date.setValue(create_date);
							domain_updated_date.setValue(updated_date);
						};

						scwin.select_check_domain_name = async(domain_name) =>{
							const url = common.uri.checktDomainName(domain_name);
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							if (data[0].domain_name_not_found === "no") {
								let message = common.getLabel("lbl_exist_name");
								common.win.alert(message);
								scwin.domainCheckYn = false;
							} else if(data[0].domain_name_not_found === "yes") {
								let message = common.getLabel("lbl_can_use_name");
								common.win.alert(message);
								scwin.domainCheckYn = true;
							}
						};

						scwin.setDomainCreateAndInsert = function (domain_detail_data) {
							let domain_detail_json = {};
							domain_detail_json = domain_detail_data;

							const url = common.uri.setDomainCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, domain_detail_json)
								.then(res => {
									let message;
									if (res[0].result === "success" && res != null) {
										message = common.getLabel("lbl_userManager_domain_detail_success");
									} else {
										message = common.getLabel("lbl_userManager_domain_detail_fail");
									}
									common.win.alert(message);
								}).catch(() => {
								const message = common.getLabel("lbl_userManager_domain_detail_fail");
								common.win.alert(message);
							});
						};

						scwin.setDomainNameUpdate = function (domain_id, domain_name) {
							let workspace_update_data = {};
							let param = {};

							workspace_update_data.domain_id = domain_id;
							workspace_update_data.domain_name = domain_name;

							param.domain_id = domain_id;
							param.domain_name = domain_name;

							const url = common.uri.setDomainUpdate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};
							const body = param;

							common.http.fetch(url, method, headers, body)
								.then(res => {
									let message;
									if (res[0].result === "success" && res != null) {
										message = common.getLabel("lbl_userManager_domain_detail_modifiedSuccess");
									} else {
										message = common.getLabel("lbl_userManager_domain_detail_modifiedFail");
									}
									common.win.alert(message);
								}).catch(() => {
								const message = common.getLabel("lbl_check_id_password");
								common.win.alert(message);
							});
						};

						scwin.btnUpdateWorkspaceDetailOnclick = function() {
							const paramData = $p.getParameter("tabParam");
							const viewType = paramData.domain_setting_mode;

							if (viewType === "detailview") {
								let data = {};
								data.domain_name = manager_domain_name.getValue();
								const domain_id = paramData.domain_id;

								scwin.setDomainNameUpdate(domain_id, data.domain_name);
							} else {
								if(scwin.checkData()){
									let data = {};
									data.domain_name = manager_domain_name.getValue();

									scwin.setDomainCreateAndInsert(data);
								}
							}
						};

						scwin.step1_select_platform_onchange = function() {
							$p.top().scwin._role_code_id_ = this.getValue(this.getSelectedIndex);
						};

						scwin.step1_btn_check_domain_onclick = function(){
							const domain_name = manager_domain_name.getValue();

							if (common.isEmptyStr(domain_name)) {
								const message = common.getLabel("lbl_userManager_domain_detail_blank");
								common.win.alert(message);
								return false;
							}

							if(common.checkAllInputText("CHECK_INPUT_TYPE_SPC", domain_name)){
								const message = common.getLabel("lbl_can_not_special_char");
								common.win.alert(message);
								return false;
							}

							scwin.select_check_domain_name(domain_name);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{tagname:'h2',useLocale:'true',localeRef:'lbl_userManager_domainSetting'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'domain_setting_title',label:'',class:'',useLocale:'true',localeRef:'lbl_userManager_domain_detail_domainView'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'domain_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btnUpdateWorkspaceDetailOnclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{useLocale:'true',localeRef:'lbl_userManager_domain_detail_domainName'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'manager_domain_name',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_dup_check',type:'button',class:'btn_cm pt','ev:onclick':'scwin.step1_btn_check_domain_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_created_date'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'domain_created_date',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_modified_date'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{class:'',id:'domain_updated_date',style:'width:100%;'}}]}]}]}]}]}]}]}]}]})