/*amd /ui/manager/userManager_domain_detail.xml 14744 aaef64747eb8d6fa2f12d8f6341ab949892f40fbbd637f8e4ffd86ec00fc1b39 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.check_branch_nameYN = false;
						scwin.check_branch_user_idYN = false;
						scwin.role_code_value = "";

						scwin.onpageload = function () {
							const paramData = $p.getParameter("tabParam");
							const branch_setting_mode = paramData.builder_setting_mode;

							// 빌더 세팅 수정 뷰
							if (branch_setting_mode === "detailview") {
								let view = common.getLabel("lbl_builder_detail");
								let save = common.getLabel("lbl_save");

								builder_setting_title.setLabel(view);
								builder_create_or_save_btn.setLabel(save);
								// setDisabled
								builder_setting_role_id.setDisabled(true);
								builder_setting_user_id.setDisabled(true);
								builder_setting_name.setDisabled(true);
								builder_setting_url.setDisabled(true);
								btn_name_dup_check.setDisabled(true);
								btn_id_dup_check.setDisabled(true);

								scwin.branchDetailView();
							}
							// 빌더 세팅 생성 뷰
							else {
								let view = common.getLabel("lbl_builder_create_setting");
								let create = common.getLabel("lbl_create");

								builder_setting_title.setLabel(view);
								builder_create_or_save_btn.setLabel(create);
								builder_setting_role_id.setDisabled(false);
								builder_setting_user_id.setDisabled(false);
								builder_setting_name.setDisabled(false);
								builder_setting_url.setDisabled(false);
							}
							scwin.role_code_value = builder_setting_role_id.getValue();
						};

						scwin.checkDataBranchName = function () {
							const branch_name = builder_setting_name.getValue();

							if (common.isEmptyStr(branch_name)) {
								const message = common.getLabel("lbl_builder_input_name");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						scwin.checkDataBranchUserId = function () {
							const branch_user_id = builder_setting_user_id.getValue();

							if (common.isEmptyStr(branch_user_id)) {
								let message = common.getLabel("lbl_builder_input_id");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						scwin.checkAllBranch = function () {
							const branch_name = builder_setting_name.getValue();
							const branch_user_id = builder_setting_user_id.getValue();

							if (common.isEmptyStr(branch_name)) {
								const message = common.getLabel("lbl_builder_input_name");
								common.win.alert(message);
								return false;
							}

							if (!scwin.check_branch_nameYN) {
								const message = common.getLabel("lbl_builder_check_duplicate_id");
								common.win.alert(message);
								return false;
							}

							if (common.isEmptyStr(branch_user_id)) {
								const message = common.getLabel("lbl_builder_input_id");
								common.win.alert(message);
								return false;
							}

							if (!scwin.check_branch_user_idYN) {
								const message = common.getLabel("lbl_builder_check_duplicate_user_id");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						/**
						 *  builder setting 조회 api
						 */
						scwin.branchDetailView = async function () {
							const paramData = $p.getParameter("tabParam");
							const builder_id = paramData.builder_id;

							const url = common.uri.getBranchSettingDetailByID(parseInt(builder_id));
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.builderDataSetting(data);
						};

						/**
						 *  builder setting 조회 결과 값 data setting
						 */
						scwin.builderDataSetting = function (builderData) {
							if (builderData != null) {
								builder_setting_role_id.setValue(builderData.role_code_id, false);
								builder_setting_user_id.setValue(builderData.builder_user_id);
								builder_setting_name.setValue(builderData.builder_name);
								builder_setting_url.setValue(builderData.builder_url);
							}
						};

						/**
						 * builder 저장 및 등록 전체 함수
						 */
						scwin.saveBranchSettingData = function () {
							let data = {};
							data.role_code_id = scwin.role_code_value;
							data.builder_user_id = builder_setting_user_id.getValue();
							data.builder_name = builder_setting_name.getValue();
							data.session_status = "N";
							data.session_type = "BRANCH";
							data.builder_url = builder_setting_url.getValue();
							data.builder_password = branch_setting_password.getValue();

							if (scwin.checkAllBranch()) {
								scwin.setBranchSettingAndInsert(data);
							}
						};

						scwin.selectCheckBuilderName = function (branch_name) {
							let data = {};
							data.branch_name = branch_name;

							const url = common.uri.setBranchSettingCheckName;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].builder_name_can_use === "yes") {
											message = common.getLabel("lbl_can_use_name");
											scwin.check_branch_nameYN = true;
										} else {
											message = common.getLabel("lbl_exist_name");
											scwin.check_branch_nameYN = false;
										}
										common.win.alert(message);
									}
								}).catch(() => {
								const message = common.getLabel("lbl_can_use_name");
								common.win.alert(message);
								scwin.check_branch_nameYN = true;
							});
						};

						scwin.selectCheckBuilderUserID = function (branch_user_id) {
							let data = {};
							data.branch_user_id = branch_user_id;

							let url = common.uri.setBranchSettingCheckUserID;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].builder_userid_can_use === "yes") {
											message = common.getLabel("lbl_available_id");
											scwin.check_branch_user_idYN = true;

										} else {
											message = common.getLabel("lbl_exist_name");
											scwin.check_branch_user_idYN = false;
										}
										common.win.alert(message);
									}
								})
								.catch(() => {
									const message = common.getLabel("lbl_available_id");
									common.win.alert(message);
									scwin.check_branch_user_idYN = true;
								});
						};

						/**
						 * builder 등록
						 * @param branch_setting_data
						 */
						scwin.setBranchSettingAndInsert = function (branch_setting_data) {
							const url = common.uri.setBranchSettingCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, branch_setting_data)
								.then(res => {
									if (Array.isArray(res)) {
										let message;
										if (res[0].result === "success" && res != null) {
											message = common.getLabel("lbl_builder_created");
										} else {
											message = common.getLabel("lbl_builder_create_fail");
										}
										common.win.alert(message);
									}
								})
								.catch(() => {
									const message = common.getLabel("lbl_builder_create_fail");
									common.win.alert(message);
								});
						};

						scwin.setBuilderSettingAndUpdate = function () {
							const builder_password = branch_setting_password.getValue();

							if (common.isEmptyStr(builder_password)) {
								const message = common.getLabel("lbl_builder_input_password");
								common.win.alert(message);
								return false;
							}

							const url = common.uri.setBranchSettingUpdate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, builder_password)
								.then(res => {
									if (Array.isArray(res)) {
										if (res[0].result === "success" && res != null) {
											const message = common.getLabel("lbl_builder_password_changed");
											common.win.alert(message);
										} else {
											const message = common.getLabel("lbl_builder_password_change_fail");
											common.win.alert(message);
										}
									}
								})
								.catch(() => {
									const message = common.getLabel("lbl_setting_branch_detail_changeFail");
									common.win.alert(message);
								});
						};

						scwin.btn_create_branch_setting_onclick = function () {
							const paramData = $p.getParameter("tabParam");
							const branch_setting_mode = paramData.builder_setting_mode;

							if (branch_setting_mode == "detailview") {
								scwin.setBuilderSettingAndUpdate();
							} else {
								scwin.saveBranchSettingData();
							}
						};

						scwin.step1_select_platform_onchange = function () {
							const roleCodeValue = this.getValue(this.getSelectedIndex);
							scwin.role_code_value = roleCodeValue;
						};

						scwin.step1_btn_check_branch_name_onclick = function () {
							const branch_name = builder_setting_name.getValue();

							if (scwin.checkDataBranchName()) {
								scwin.selectCheckBuilderName(branch_name);
							}
						};

						scwin.step1_btn_check_branch_user_id_onclick = function () {
							const branch_user_id = builder_setting_user_id.getValue();

							if (scwin.checkDataBranchUserId()) {
								scwin.selectCheckBuilderUserID(branch_user_id);
							}
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_builder_detail'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'builder_setting_title',label:'',class:'',useLocale:'true',localeRef:'lbl_builder_detail'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'builder_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_create_branch_setting_onclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_builder_name'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'builder_setting_name',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_name_dup_check',type:'button',class:'btn_cm pt','ev:onclick':'scwin.step1_btn_check_branch_name_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_builder_user_id'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'builder_setting_user_id',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_id_dup_check',style:'',type:'button','ev:onclick':'scwin.step1_btn_check_branch_user_id_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_builder_permission'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{id:'builder_setting_role_id',chooseOption:'',style:'width: 100%;',submenuSize:'auto',allOption:'',disabled:'false',direction:'auto',appearance:'minimal',disabledClass:'w2selectbox_disabled',ref:'','ev:onchange':'scwin.step1_select_platform_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'build'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'1'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'deploy'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'2'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'All'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'3'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_builder_server_url'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'builder_setting_url',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_builder_password'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{class:'',id:'branch_setting_password',style:'width:100%;'}}]}]}]}]}]}]}]}]}]})