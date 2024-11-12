/*amd /xml/userManager_admin_detail.xml 48793 182497438bb76995ab13b68f6c42d7e90d6359113486d3dd2a15df5be2d62a24 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'__member_create_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'member_id',name:'member_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_login_id',name:'user_login_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'email',name:'email',dataType:'text'}},{T:1,N:'w2:column',A:{id:'password',name:'password',dataType:'text'}},{T:1,N:'w2:column',A:{id:'confirmPassword',name:'confirmPassword',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_role',name:'member_role',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_name',name:'member_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'create_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_date',name:'update_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'last_login_date',name:'last_login_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'domain_id',name:'domain_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'role_id',name:'role_id',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_rolecode_list_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_role_list',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_id',name:'role_id'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_name',name:'role_name'}},{T:1,N:'w2:column',A:{dataType:'text',id:'select_yn',name:'select_yn'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.check_user_idYN = false;
			scwin.check_user_emailYN = false;
			scwin.user_role_id_temp_data = 0;

			scwin.onpageload = function () {
				const paramData = $p.getParameter("tabParam");
				const member_setting_mode = paramData.admin_setting_mode;

				if (member_setting_mode === "detailview") {
					let view = common.getLabel("lbl_userManager_admin_detail_adminView");
					let save = common.getLabel("lbl_save");
					// branch_setting_title.setLabel(view);
					branch_create_or_save_btn.setLabel(save);
					// setDisabled
					manager_user_id.setDisabled(true);
					manager_user_email.setDisabled(true);
					manager_member_name.setDisabled(true);

					user_created_date.setDisabled(true);
					user_updated_date.setDisabled(true);
					user_last_login_date.setDisabled(true);

					btn_manager_user_id_check.hide();
					btn_manager_user_email_check.hide();

					grp_user_pricing_title.hide();
					grp_user_pricing_body.hide();

					user_password_group.hide();
					user_confirm_password_group.hide();

					if (g_config.PROFILES == "service") {
						grp_change_password_title.show();
						grp_change_password_body.show();
						grp_secession_title.show();
						grp_secession_body.show();
						grp_domain_setting.hide();
						grp_permission_setting.hide();
					} else {
						grp_change_password_title.show();
						grp_change_password_body.show();
						grp_secession_title.hide();
						grp_secession_body.hide();
						grp_domain_setting.show();
						grp_permission_setting.show();
					}

					scwin.getRoleCodeListInfo();
					scwin.getRoleNameListInfo();
					scwin.getDomainListInfo();
				} else if (member_setting_mode === "detailviewuser") {
					let user = common.getLabel("lbl_userManager_admin_detail_userView");
					// branch_setting_title.setLabel(user);
					// setDisabled
					manager_user_id.setDisabled(true);
					manager_user_email.setDisabled(true);
					manager_member_name.setDisabled(true);

					user_select_rolecode_settingbox.setDisabled(true);
					user_created_date.setDisabled(true);
					user_updated_date.setDisabled(true);
					user_last_login_date.setDisabled(true);

					grp_user_pricing_title.show();
					grp_user_pricing_body.show();

					user_pricing_billing_account_name.setDisabled(true);
					user_pricing_billing_reg_date.setDisabled(true);
					user_pricing_billing_reg_next_date.setDisabled(true);
					user_pricing_billing_amount.setDisabled(true);

					role_btn.hide();
					btn_manager_user_id_check.hide();
					btn_manager_user_email_check.hide();
					branch_create_or_save_btn.hide();

					user_password_group.hide();
					user_confirm_password_group.hide();

					if (g_config.PROFILES == "service") {
						grp_change_password_title.show("");
						grp_change_password_body.show("");
						grp_secession_title.show("");
						grp_secession_body.show("");
						grp_domain_setting.hide();
						grp_permission_setting.hide();
					} else {
						grp_change_password_title.show();
						grp_change_password_body.show();
						grp_secession_title.hide();
						grp_secession_body.hide();
						grp_domain_setting.show("");
						grp_permission_setting.show("");
					}
					scwin.getRoleCodeListInfo();
					scwin.getRoleNameListInfo();
					scwin.getDomainListInfo();
				}
				// detailcreate
				else {
					let admin = common.getLabel("lbl_userManager_admin_detail_adminCreate");
					let create = common.getLabel("lbl_create");

					// 결제 내역 및 구독 취소  내용 숨김
					grp_user_pricing_title.hide();
					grp_user_pricing_body.hide();

					//비밀번호 재설정 관련 내용 숨김
					grp_change_password_title.hide();
					grp_change_password_body.hide();

					//탈퇴 관련 내용 숨김
					grp_secession_title.hide();
					grp_secession_body.hide();

					// branch_setting_title.setLabel(admin);
					branch_create_or_save_btn.setLabel(create);

					if (g_config.PROFILES == "service") {
						manager_user_email.setDisabled(false);
						manager_user_email_grp.show("");
						grp_domain_setting.hide();
						grp_permission_setting.hide();
					} else {
						manager_user_email.setDisabled(false);
						manager_user_email_grp.show("");
						grp_domain_setting.show("");
						grp_permission_setting.show("");
					}

					manager_member_name.setDisabled(false);
					user_created_date.setDisabled(false);
					user_updated_date.setDisabled(false);
					user_last_login_date.setDisabled(false);

					grp_user_created_date.hide();
					grp_user_updated_date.hide();
					grp_user_last_login_date.hide();

					grp_change_password_body.hide();
					grp_secession_body.hide();

					scwin.getRoleCodeListInfo();

					scwin.getRoleNameListInfo();

					scwin.getDomainListInfo();
				}
			};

			scwin.getRoleNameListInfo = async function () {
				const url = common.uri.getRoleProfileListAll;
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				scwin.roleNameListInfoSettingView(data);
			};

			scwin.roleNameListInfoSettingView = function (data) {
				let roleNameList = [];

				for (const [row, role] of data.entries()) {
					let temp = {};
					temp["role_id"] = role.role_id;
					temp["role_name"] = role.role_name;

					roleNameList.push(temp);
				}

				const distinct = common.unique(roleNameList, 'role_id');
				dlt_role_list.setJSON(distinct);

				$(".btn_role_cm").click(function () {
					$(".layer_pop").css("display", "block").css("width", "400px");
					$("body").append("<div class='dim'></div>");
				});

				$(".btn_pop_close").click(function () {
					$(".layer_pop").css("display", "none");
					$("div").remove(".dim");
				});
			};

			scwin.checkDataCreate = function () {
				const user_login_id = manager_user_id.getValue();
				const user_email = manager_user_email.getValue();
				const user_password = manager_user_password.getValue();
				const user_confirm_password = manager_user_confirm_password.getValue();
				const member_name = manager_member_name.getValue();
				const user_rolecode = user_select_rolecode_settingbox.getText();

				// 이메일 중복 조회 체크
				if (common.isEmptyStr(user_login_id)) {
					const message = common.getLabel("lbl_check_blank_id");
					common.win.alert(message);
					scwin.check_user_idYN = false;
					return false;
				}

				if (g_config.PROFILES === "service" && user_rolecode === "USER") {
					if (common.isEmptyStr(user_email)) {
						const message = common.getLabel("lbl_userManager_admin_detail_blankEmail");
						common.win.alert(message);
						scwin.check_user_emailYN = false;
						return false;
					}

					if (common.isEmptyStr(user_email)) {
						const message = common.getLabel("lbl_userManager_admin_detail_blankEmail");
						common.win.alert(message);
						check_user_emailYN = false;
						return false;
					}

					if (!common.checkAllInputText("CHECK_INPUT_TYPE_EMAIL", user_email)) {
						const message = common.getLabel("lbl_register_checkEmail");
						common.win.alert(message);
						check_user_emailYN = false;
						return false;
					}

					if (!scwin.check_user_emailYN) {
						const message = common.getLabel("lbl_userManager_admin_detail_duplicate");
						common.win.alert(message);
						check_user_emailYN = false;
						return false;
					}
				}

				if (!scwin.check_user_idYN) {
					const message = common.getLabel("lbl_userManager_admin_detail_duplicate");
					common.win.alert(message);
					return false;
				}

				if (common.isEmptyStr(user_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_pwdInput");
					common.win.alert(message);
					return false;
				}

				if (common.isEmptyStr(user_confirm_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_pwdCheck");
					common.win.alert(message);
					return false;
				}

				if (user_password != user_confirm_password) {
					const message = common.getLabel("lbl_userManager_admin_detail_samePwd");
					common.win.alert(message);
					return false;
				}

				if (common.isEmptyStr(member_name)) {
					const message = common.getLabel("lbl_input_username");
					common.win.alert(message);
					return false;
				}

				if (common.isEmptyStr(user_rolecode)) {
					const message = common.getLabel("lbl_userManager_admin_detail_permission");
					common.win.alert(message);
					alert(message);
					return false;
				}

				return true;
			};

			// mmeber Detail view 조회
			scwin.memberDetailView = async function () {
				const url = common.uri.getUserListDetail;
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				scwin.memberDetailViewSetting(data);
			};

			scwin.memberDetailViewSetting = function (data) {
				let create_date = "";
				let updated_date = "";
				if (data.created_date != null) {
					create_date = data.created_date.replace(/T/g, ' ');
				}

				if (data.updated_date != null) {
					updated_date = data.updated_date.replace(/T/g, ' ');
				}

				let label = common.getLabel("lbl_userManager_admin_detail_secession_detail");
				label = common.getFormatStr(label, data.email);

				manager_user_id.setValue(data.user_login_id);
				manager_user_email.setValue(data.email);
				secession_email_label.setLabel(label);
				manager_member_name.setValue(data.user_name);
				user_select_rolecode_settingbox.setText(data.user_role, false);
				user_created_date.setValue(create_date);
				user_updated_date.setValue(updated_date);
				user_last_login_date.setValue(data.last_login_date);

				if (data.pricing === undefined ||  data.pricing == null || data.pricing.pay_change_yn === undefined  || data.pricing.pay_change_yn == "N") {
					const cancelOrPricingMsg = common.getLabel("lbl_userManager_pricing_ok");
					user_cancel_pricing.setLabel(cancelOrPricingMsg);
					android_appid_tr2.hide();
                    ios_appid_tr1.hide();
                    ios_appid_tr2.hide();
					if(data.appid_json === undefined || data.appid_json == null){

					}else {
						let appIDJson =  JSON.parse(data.appid_json);
                        user_appid_android_1_name.setValue(appIDJson.androidAppID1);
					}

				} else {

					if(data.appid_json === undefined || data.appid_json == null){

					}else {
						let appIDJson =  JSON.parse(data.appid_json);
						user_appid_android_1_name.setValue(appIDJson.androidAppID1);
                        user_appid_android_2_name.setValue(appIDJson.androidAppID2);
                        user_appid_ios_1_name.setValue(appIDJson.iOSAppID1);
                        user_appid_ios_2_name.setValue(appIDJson.iOSAppID2);
					}

					// let custom_data = JSON.parse(data.pricing.pricingObj.custom_data);
					user_pricing_billing_account_name.setValue(data.pricing.pricingObj.productName);
					user_pricing_billing_reg_date.setValue(data.pricing.pricingObj.regDate);
					user_pricing_billing_reg_next_date.setValue(data.pricing.pricingObj.regNextDate);
					user_pricing_billing_amount.setValue(data.pricing.pricingObj.productPrice);
				}




				// __member_create_data__
				__member_create_data__.setJSON([data]);
			};

			scwin.memberOneDetailView = async function () {
				const paramData = $p.getParameter("tabParam");
				const member_id = paramData.user_id;

				const url = common.uri.getUserOneDetail(member_id);
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				scwin.memberOneDetailViewSetting(data);
			};

			scwin.memberOneDetailViewSetting = function (data) {
				let create_date = "";
				let updated_date = "";
				let label = common.getLabel("lbl_userManager_admin_detail_secession_detail");
				label = common.getFormatStr(label, data.email);

				if (data.created_date != null) {
					create_date = data.created_date.replace(/T/g, ' ');
				}

				if (data.updated_date != null) {
					updated_date = data.updated_date.replace(/T/g, ' ');
				}

				manager_user_id.setValue(data.user_login_id);
				manager_user_email.setValue(data.email);
				secession_email_label.setLabel(label);
				manager_member_name.setValue(data.user_name);
				user_select_rolecode_settingbox.setText(data.user_role, false);
				user_created_date.setValue(create_date);
				user_updated_date.setValue(updated_date);
				user_last_login_date.setValue(data.last_login_date);

				// __member_create_data__
				__member_create_data__.setJSON([data]);
			};

			scwin.saveMembereDetailData = function () {
				const paramData = $p.getParameter("tabParam");
				const member_setting_mode = paramData.admin_setting_mode;

				const user_login_id = manager_user_id.getValue();
				const user_email = manager_user_email.getValue();
				const user_password = manager_user_password.getValue();
				const user_confirm_password = manager_user_confirm_password.getValue();
				const member_name = manager_member_name.getValue();
				let domain_id;
				let user_role;

				// 서비스는 회원가입 시, domain이 1로 셋팅된다.
				if (g_config.PROFILES === "service") {
					domain_id = 1;
				} else {
					domain_id = user_select_domain_settingbox.getValue();
				}

				let data = {};
				data.user_login_id = user_login_id;
				data.email = user_email;
				data.password = user_password;
				data.confirmPassword = user_confirm_password;
				data.user_role = user_select_rolecode_settingbox.getText();
				data.user_name = member_name;
				data.domain_id = domain_id;

				if (member_setting_mode === "detailview") {
					data.role_id = scwin.user_role_id_temp_data;
				} else {
					data.role_id = scwin.user_role_id_temp_data;
				}

				__member_create_data__.removeAll();
				__member_create_data__.setJSON([data]);

				let member_create_data = __member_create_data__.getRowJSON(0);

				if (member_setting_mode === "detailview" || member_setting_mode === "detailviewuser") {
					scwin.setMemberUserUpdate(member_create_data);
				} else {
					if (scwin.checkDataCreate()) {
						scwin.setMemberUserCreateAndInsert(member_create_data);
					}
				}
			};

			scwin.selectCheckUserLoginId = function (user_login_id) {
				let data = {};
				data.user_login_id = user_login_id;

				const url = common.uri.getUserCheckName;
				const method = "POST";
				const headers = {"Content-Type": "application/json; charset=utf-8"};

				common.http.fetch(url, method, headers, data).then(res => {
					if (res != null) {
						let message;
						if (res[0].user_name_not_found === "no") {
							message = common.getLabel("lbl_exist_id");
							scwin.check_user_idYN = false;
						} else if (res[0].user_name_not_found === "yes") {
							message = common.getLabel("lbl_available_id");
							scwin.check_user_idYN = true;
						}
						common.win.alert(message);
					} else {
						const message = common.getLabel("lbl_available_id");
						common.win.alert(message);
						scwin.check_user_idYN = true;
					}
				}).catch(() => {
					const message = common.getLabel("lbl_available_id");
					common.win.alert(message);
					scwin.check_user_idYN = true;
				});
			};

			// email 중복확인
			scwin.selectCheckUserEmail = function (user_email) {
				let data = {};
				data.email = user_email;

				const url = common.uri.getUserEmailCheck;
				const method = "POST";
				const headers = {"Content-Type": "application/json"};

				common.http.fetch(url, method, headers, data)
						.then(res => {
							if (Array.isArray(res)) {
								let message;
								const chk = res[0].user_email_not_found;
								if (chk == "yes") {
									message = common.getLabel("lbl_userManager_admin_detail_available");
									scwin.check_user_emailYN = true;
								} else {
									message = common.getLabel("lbl_exist_email");
									scwin.check_user_emailYN = false;
								}
								common.win.alert(message);
							}
						})
						.catch(err => {
							common.win.alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText + "\n")
						});
			};

			// detail update
			scwin.setMemberUserUpdate = function (member_detail_data) {
				const url = common.uri.setUserUpdate;
				const method = "POST";
				const headers = {"Content-Type": "application/json; charset=utf-8"};

				common.http.fetch(url, method, headers, member_detail_data).then(res => {
					let message;
					if (res != null && res[0].result === "success") {
						message = common.getLabel("lbl_userManager_admin_detail_modifiedSuccess");
					} else {
						message = common.getLabel("lbl_setting_ftp_detail_fail");
					}
					common.win.alert(message);
				}).catch(() => {
					const message = common.getLabel("lbl_userManager_admin_detail_modifiedFail");
					common.win.alert(message);
				});
			};

			scwin.setMemberUserCreateAndInsert = function (member_detail_data) {
				const url = common.uri.setUserCreate;
				const method = "POST";
				const headers = {"Content-Type": "application/json; charset=utf-8"};

				common.http.fetch(url, method, headers, member_detail_data).then(res => {
					let message;
					if (res != null && res[0].result === "success") {
						message = common.getLabel("lbl_userManager_admin_detail_createUser");
					} else {
						message = common.getLabel("lbl_userManager_admin_detail_failUser");
					}
					common.win.alert(message);
				}).catch(() => {
					const message = common.getLabel("lbl_userManager_admin_detail_failUser");
					common.win.alert(message);
				});
			};

			scwin.getRoleCodeListInfo = async function () {
				const codeType = "member";

				const url = common.uri.getCodeAllList(codeType);
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				scwin.RoleCodeListSetting(data);
			};

			scwin.RoleCodeListSetting = function (data) {
				const paramData = $p.getParameter("tabParam");
				const member_setting_mode = paramData.admin_setting_mode;

				for (const [row, role] of data.entries()) {
					user_select_rolecode_settingbox.addItem(role.role_code_id, role.role_code_name);
				}

				// 생성될 USER의 권한 기본값을 USER로 셋팅
				user_select_rolecode_settingbox.setValue("12");
				scwin.userRoleChange();

				if (member_setting_mode === "detailviewuser") {
					scwin.memberDetailView();
				} else if (member_setting_mode === "detailview") {
					scwin.memberOneDetailView();
				}
			};

			scwin.userRoleChange = function () {
				const role = user_select_rolecode_settingbox.getValue();

				// role code 11은 ADMIN, 12는 USER
				if (role.toLowerCase() === "11") {
					manager_user_email_grp.hide();
				} else {
					manager_user_email_grp.show("");
				}
			};

			scwin.getDomainListInfo = async function () {
				const url = common.uri.getDomainList;
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				scwin.domainListInfoSettingView(data);
			};

			scwin.domainListInfoSettingView = function (data) {
				const message = common.getLabel("lbl_select");
				user_select_domain_settingbox.addItem("", message);

				for (const [row, domain] of data.entries()) {
					user_select_domain_settingbox.addItem(domain.domain_id, domain.domain_name);
				}

				scwin.getDomainListByIDInfo();
			};

			scwin.getDomainListByIDInfo = async function () {
				const paramData = $p.getParameter("tabParam");
				const domain_id = paramData.domain_id;

				const url = common.uri.getDomainDetail(domain_id);
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				if (!!domain_id) {
					const response = await common.http.fetchGet(url, method, headers, {});
					const data = await response.json();

					user_select_domain_settingbox.setSelectedIndex(data.domain_id);
				}
			};

			scwin.userPasswordReset = function () {
				let data = {};
				data.user_login_id = manager_user_id.getValue();
				data.email = manager_user_email.getValue();
				data.old_password = change_old_password.getValue();
				data.new_password = change_user_password.getValue();
				data.confirm_password = change_user_confirm_password.getValue();

				const url = common.uri.setUserUpdateAuthToken;
				const method = "POST";
				const headers = {"Content-Type": "application/json; charset=utf-8"};

				common.http.fetch(url, method, headers, data).then(res => {
					if (Array.isArray(res) && !!res) {
						const message = common.getLabel("lbl_userManager_admin_detail_newPwd_confirm");
						common.win.alert(message);
					}
				});
			};

			scwin.userIdDupCheck = function () {
				const user_login_id = manager_user_id.getValue();

				if (common.isEmptyStr(user_login_id)) {
					const message = common.getLabel("lbl_userManager_admin_detail_idBlank");
					common.win.alert(message);
					return false;
				}

				scwin.selectCheckUserLoginId(user_login_id);
			};

			scwin.userEmailDupCheck = function () {
				const user_email = manager_user_email.getValue();

				if (common.isEmptyStr(user_email)) {
					const message = common.getLabel("lbl_userManager_admin_detail_blankEmail");
					common.win.alert(message);
					return false;
				}

				if (!common.checkAllInputText("CHECK_INPUT_TYPE_EMAIL", user_email)) {
					const message = common.getLabel("lbl_register_checkEmail");
					common.win.alert(message);
					check_user_emailYN = false;
					return false;
				}

				scwin.selectCheckUserEmail(user_email);
			};

			scwin.btn_update_password_reset_onclick = function () {
				const old_password = change_old_password.getValue();
				const new_password = change_user_password.getValue();
				const confirm_password = change_user_confirm_password.getValue();

				if (common.isEmptyStr(old_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_pwdOLD");
					common.win.alert(message);
					return false;
				}

				if (common.isEmptyStr(new_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_newPwd");
					common.win.alert(message);
					return false;
				}

				if (!common.checkAllInputText("CHECK_INPUT_TYPE_PW", new_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_notice");
					common.win.alert(message);
					return false;
				}

				if (old_password == new_password) {
					const message = common.getLabel("lbl_userManager_admin_detail_errorNotice");
					common.win.alert(message);
					return false;
				}

				if (common.isEmptyStr(confirm_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_confirmPwd");
					common.win.alert(message);
					return false;
				}

				if (!common.checkAllInputText("CHECK_INPUT_TYPE_PW", confirm_password)) {
					const message = common.getLabel("lbl_userManager_admin_detail_formatError");
					common.win.alert(message);
					return false;
				}

				if (new_password != confirm_password) {
					const message = common.getLabel("lbl_userManager_admin_detail_samePwd");
					common.win.alert(message);
					return false;
				}

				scwin.userPasswordReset();
			};

			scwin.doSecssion = function () {
				const secssionEmail = input_secession_email.getValue();
				const userEmail = manager_user_email.getValue();

				if (common.isEmptyStr(secssionEmail)) {
					const message = common.getLabel("lbl_userManager_admin_detail_blankEmail");
					common.win.alert(message);
					return false;
				}

				if (secssionEmail !== userEmail) {
					const message = common.getLabel("lbl_userManager_admin_detail_errorEmail");
					common.win.alert(message);
					return false;
				}


				const url = common.uri.resign;
				const method = "POST";
				const headers = {"Content-Type": "application/json"};

				common.http.fetch(url, method, headers, {"email": secssionEmail})
						.then(res => {
							if (Array.isArray(res)) {
								let message;
								if (res[0].user_name_not_found == "yes") {
									// message = common.getLabel("lbl_userManager_admin_detail_sendEmail");
									// common.win.alert(message);
									$p.url("/manager/member/logout");
								} else {
									message = common.getLabel("lbl_userManager_admin_detail_notExist");
									common.win.alert(message);
								}

							}
						})
						.catch(err => {

						});
			};

			scwin.pricing_cancel_onclick = async function () {
				let msgUserCancel = user_cancel_pricing.getLabel();
				let PricingMsg = common.getLabel("lbl_userManager_pricing_ok");
				let cancelMsg = common.getLabel("lbl_userManager_pricing_cancel");


				if (msgUserCancel == PricingMsg) {
                    $p.url("/pricing.html");

				} else if (msgUserCancel == cancelMsg) {


                    // TODO 구독 취소 하기 전에 알림 창 추가
					const message = common.getLabel("lbl_userManager_admin_detail_tier_professional_cancel_confirm");
					if (await common.win.confirm(message)) {

						const opts = {
							'do': 'cancelProfessionalTierProject',
							'regDate': user_pricing_billing_reg_date.getValue(),
							'regNextDate': user_pricing_billing_reg_next_date.getValue()
						}

						await common.win.prompt("", null, opts);
					}

					//TODO 해당 작업 전에 공통 소스 코드 호출해서 처리 해야함...
					// scwin.pricing_cancel_action_api();

				}

			};

			scwin.pricing_cancel_action_api = async function(regDate, regNextDate){
				let PricingMsg = common.getLabel("lbl_userManager_pricing_ok");

				const url = common.uri.setUserInfoPricingCancel;
				const headers = {"Content-Type": "application/json; charset=UTF-8"};
				const body = {};
				body.regDate = regDate;
				body.regNextDate = regNextDate;

				const message = common.getLabel("lbl_userManager_admin_detail_tier_professional_cancel_progress");
				WebSquare.layer.showProcessMessage(message);

				await common.http.fetchPost(url, headers, body)
						.then(async (res) => {
							let message;
							WebSquare.layer.hideProcessMessage();
							if (res.status == 200) {
								user_pricing_billing_account_name.setValue("");
								user_pricing_billing_reg_date.setValue("");
								user_pricing_billing_reg_next_date.setValue("");
								user_pricing_billing_amount.setValue("");
								message = common.getLabel("lbl_userManager_admin_detail_tier_professional_cancel_detail");
								common.win.alert(message);

								user_cancel_pricing.setLabel(PricingMsg);


							}
						}).catch((err) => {
							common.win.alert("error status:" + err.status + ", message:" + err.message);
						});
			};

			/**
			 * user detail appID update api 호출 function
			 */
			scwin.user_appid_update_onclick = function () {


				const androidAppID1 = user_appid_android_1_name.getValue();
				const androidAppID2 = user_appid_android_2_name.getValue();
				const iOSAppID1 = user_appid_ios_1_name.getValue();
				const iOSAppID2 = user_appid_ios_2_name.getValue();
				const userEmail = manager_user_email.getValue();

				//TODO validation 처리 기능 고민해보기


				const url = common.uri.setUserAppIDUpdate;
				const method = "POST";
				const headers = {"Content-Type": "application/json"};
                const body = {};

                body.androidAppID1 = androidAppID1;
                body.androidAppID2 = androidAppID2;
                body.iOSAppID1 = iOSAppID1;
                body.iOSAppID2 = iOSAppID2;

				common.http.fetch(url, method, headers, body)
						.then(res => {
                            let message;
							if (Array.isArray(res)) {
								if (res[0].appid_update_ok == "yes") {
									message = common.getLabel("lbl_userManager_admin_detail_AppID_Update_success");
								} else {
									message = common.getLabel("lbl_userManager_admin_detail_AppID_Update_fail");
								}
								common.win.alert(message);
							}

						}).catch(err => {

						});
			};


			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_userManager_admin_detail_userView'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_userView'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'branch_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.saveMembereDetailData',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_id'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'manager_user_id',class:''}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_manager_user_id_check',style:'',type:'button','ev:onclick':'scwin.userIdDupCheck',useLocale:'true',localeRef:'lbl_userManager_admin_detail_duplicateId'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:'',id:'manager_user_email_grp'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{useLocale:'true',localeRef:'lbl_email'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'manager_user_email',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_manager_user_email_check',style:'',type:'button','ev:onclick':'scwin.userEmailDupCheck',useLocale:'true',localeRef:'lbl_userManager_admin_detail_duplicateEmail'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_division'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{appearance:'minimal',direction:'auto',disabled:'false','ev:onchange':'scwin.userRoleChange',disabledClass:'w2selectbox_disabled',id:'user_select_rolecode_settingbox',style:'width: 100%;',submenuSize:'auto'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_name'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'manager_member_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'user_password_group'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_password'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'manager_user_password',dataType:'password',type:'password',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'user_confirm_password_group'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{useLocale:'true',localeRef:'lbl_userManager_admin_detail_confirm'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'manager_user_confirm_password',dataType:'password',type:'password',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{id:'grp_domain_setting',tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_domain'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'user_select_domain_settingbox',ref:'',style:'width: 100%;',submenuSize:'auto'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{id:'grp_permission_setting',tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_permissionName'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'manager_role_name',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'role_btn',style:'',type:'button',useLocale:'true',localeRef:'lbl_userManager_admin_detail_reference'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'grp_user_created_date'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_created_date'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'user_created_date',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'grp_user_updated_date'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_modified_date'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'user_updated_date',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',id:'grp_user_last_login_date'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_finalLogin'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{id:'user_last_login_date',style:'width:100%;'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'grp_user_appid_list_title',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_userManager_admin_detail_AppIDAddAndUpdate'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'user_appid_update',style:'',type:'button','ev:onclick':'scwin.user_appid_update_onclick',useLocale:'true',localeRef:'lbl_userManager_admin_detail_AppIDAddAndUpdate_OnClick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_user_appid_body',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_AppID_Android_Name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{id:'user_appid_android_1_name',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr',id:'android_appid_tr2'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_AppID_Android_Name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'user_appid_android_2_name',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr',id:'ios_appid_tr1'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_AppID_iOS_Name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{id:'user_appid_ios_1_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr',id:'ios_appid_tr2'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_AppID_iOS_Name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{id:'user_appid_ios_2_name',style:'width:100%;'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'grp_user_pricing_title',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_userManager_admin_detail_PricingDetail'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'user_cancel_pricing',style:'',type:'button','ev:onclick':'scwin.pricing_cancel_onclick',useLocale:'true',localeRef:'lbl_userManager_pricing_cancel'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_user_pricing_body',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_UserAccountName'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{id:'user_pricing_billing_account_name',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_UserRegDate'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'user_pricing_billing_reg_date',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_UserRegNextDate'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{id:'user_pricing_billing_reg_next_date',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_UserAmount'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{id:'user_pricing_billing_amount',style:'width:100%;'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'grp_change_password_title',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{id:'change_password_title',label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_change_password'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'change_password_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_update_password_reset_onclick',useLocale:'true',localeRef:'lbl_userManager_admin_detail_modifyPw'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_change_password_body',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_oldPw'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:secret',A:{class:'',id:'change_old_password',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_newPw'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_userManager_admin_detail_pwRule'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:secret',A:{class:'',id:'change_user_password',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_PwCheck'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:secret',A:{class:'',id:'change_user_confirm_password',style:'width:100%;'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'grp_secession_title',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_secssion_secssion'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''}}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'grp_secession_body',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'secession_title',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_secession'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'input_secession_email',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button',useLocale:'true',localeRef:'lbl_userManager_admin_detail_mailSend','ev:onclick':'scwin.doSecssion'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:textbox',A:{id:'secession_email_label',label:'',style:'',tagname:'span',useLocale:'true',localeRef:'lbl_userManager_admin_detail_mailNotice'}}]}]}]}]}]}]}]}]}]})