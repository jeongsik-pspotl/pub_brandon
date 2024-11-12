/*amd /xml/userManager_admin_detail.xml 40048 7b294b1f6522387276ab89935b3ba21d37f07bec8174f87ba49bdb2cc57cb5f4 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dlt_rolecode_list_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'__member_create_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'member_id',name:'member_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_login_id',name:'user_login_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'email',name:'email',dataType:'text'}},{T:1,N:'w2:column',A:{id:'password',name:'password',dataType:'text'}},{T:1,N:'w2:column',A:{id:'confirmPassword',name:'confirmPassword',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_role',name:'member_role',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_name',name:'member_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'create_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_date',name:'update_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'last_login_date',name:'last_login_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'domain_id',name:'domain_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'role_id',name:'role_id',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_role_list',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_id',name:'role_id'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_name',name:'role_name'}},{T:1,N:'w2:column',A:{dataType:'text',id:'select_yn',name:'select_yn'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.check_user_emailYN = false;
			 scwin.user_role_id_temp_data = 0;
			 scwin.onpageload = function() {
				// 화면 구성 변경
				 common.setScopeObj(scwin);
				// var member_setting_mode = localStorage.getItem("_member_setting_mode_");
				var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");


				 if (member_setting_mode == "detailview"){
                     var view = common.getLabel("lbl_userManager_admin_detail_adminView");
					 var save = common.getLabel("lbl_save");
					branch_setting_title.setLabel(view);
					branch_create_or_save_btn.setLabel(save);
					// setDisabled
					manager_user_id.setDisabled(true);
					manager_user_email.setDisabled(true);
					manager_member_name.setDisabled(true);
					//member_role.setDisabled(true);
					user_created_date.setDisabled(true);
					user_updated_date.setDisabled(true);
					user_last_login_date.setDisabled(true);
					manager_role_name.setDisabled(true);

					btn_manager_user_id_check.hide();
					btn_manager_user_email_check.hide();
					manager_user_password.hide();
					manager_user_confirm_password.hide();
					user_password_label.hide();
					user_confirm_password_label.hide();
					user_password_group.hide();
					user_confirm_password_group.hide();

					 if (g_config.PROFILES == "service") {
						 change_password_body.show();
						 secession_body.show();
					 }else {

						 change_password_body.show();
						 secession_body.show();

						 // change_password_body.hide();
						 // secession_body.hide();
						 // manager_user_email_grp.hide();
					 }

					 scwin.getRoleCodeListInfo();
					 scwin.getRoleNameListInfo();
					 scwin.getDomainListInfo();


					gridView1.setColumnVisible("role_id",false);

				}else if(member_setting_mode == "detailviewuser"){

					 var user = common.getLabel("lbl_userManager_admin_detail_userView");
					 branch_setting_title.setLabel(user);
					 // setDisabled
					 manager_user_id.setDisabled(true);
					 manager_user_email.setDisabled(true);
					 manager_member_name.setDisabled(true);
					 //member_role.setDisabled(true);
					 user_select_rolecode_settingbox.setDisabled(true);
					 user_created_date.setDisabled(true);
					 user_updated_date.setDisabled(true);
					 user_last_login_date.setDisabled(true);
					 manager_role_name.setDisabled(true);

					 role_btn.hide();
					 btn_manager_user_id_check.hide();
					 btn_manager_user_email_check.hide();
					 branch_create_or_save_btn.hide();
					 manager_user_password.hide();
					 manager_user_confirm_password.hide();
					 user_password_label.hide();
					 user_confirm_password_label.hide();
					 user_password_group.hide();
					 user_confirm_password_group.hide();

					 if (g_config.PROFILES == "service") {
						 change_password_body.show();
						 secession_body.show();
						 user_domain_setting_group.hide();
						 user_manager_role_group.hide();
					 }else {
						 change_password_body.show();
						 user_domain_setting_group.show();
						 user_manager_role_group.show();
						 secession_body.show();
						 manager_user_email_grp.hide();
					 }

					 scwin.getRoleCodeListInfo();
					 scwin.getRoleNameListInfo();
					 scwin.getDomainListInfo();

				}else {
					 var admin = common.getLabel("lbl_userManager_admin_detail_adminCreate");
					 var create = common.getLabel("lbl_create");

					branch_setting_title.setLabel(admin);
					branch_create_or_save_btn.setLabel(create);

					// manager_user_email.setDisabled(false);
					 if (g_config.PROFILES == "service") {
						 manager_user_email.setDisabled(true);
					 }else {
						 manager_user_email.setDisabled(false);
						 manager_user_email_grp.hide();
					 }

					manager_member_name.setDisabled(false);
					//member_role.setDisabled(false);
					user_created_date.setDisabled(false);
					user_updated_date.setDisabled(false);
					user_last_login_date.setDisabled(false);
					manager_role_name.setDisabled(true);

					grp_user_craete_date.hide();
					grp_user_update_date.hide();
					grp_user_last_login_date.hide();

				    change_password_body.hide();
				    secession_body.hide();

					scwin.getRoleCodeListInfo();

					scwin.getRoleNameListInfo();

					scwin.getDomainListInfo();

					gridView1.setColumnVisible("role_id",false);

				}

			};

	scwin.onpageunload = function() {
		// localStorage.removeItem("_domain_id_");
	};

	scwin.getRoleNameListInfo = function () {

		var options = {};

		options.action = "/manager/userRole/search/profileListAll";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {

			    var roleNameList = [];

				for (var row in data) {
					var temp = {};
					temp["role_id"] = data[row].role_id;
					temp["role_name"] = data[row].role_name;

					roleNameList.push(temp);

				}

				var distict = common.unique(roleNameList, 'role_id');
				dlt_role_list.setJSON(distict);

				$(".btn_role_cm").click(function(){
					$(".layer_pop").css("display","block").css("width","400px");
					$("body").append("<div class='dim'></div>");


				});
				$(".btn_pop_close").click(function(){
					$(".layer_pop").css("display","none");
					// w , h
					$("div").remove(".dim");
				});

			} else {

			}
		};

		options.error = function (e) {
			alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

		};

		$p.ajax( options );

	};

	scwin.checkDataCraete = function(){

	    var user_login_id = manager_user_id.getValue();
	    var user_email = manager_user_email.getValue();
		var user_password = manager_user_password.getValue();
		var user_confirm_password = manager_user_confirm_password.getValue();
		var member_name = manager_member_name.getValue();
		var user_rolecode = user_select_rolecode_settingbox.getText();

	    // 이메일 중복 조회 체크

		if(common.isEmptyStr(user_login_id)){
			var message = common.getLabel("lbl_check_blank_id");
			alert(message);
			scwin.check_user_emailYN = false;
			return false;
		}


		//
		if (g_config.PROFILES == "service") {
			if(common.isEmptyStr(user_email)){
				var message = common.getLabel("lbl_userManager_admin_detail_blankEmail");
				alert(message);
				scwin.check_user_emailYN = false;
				return false;
			}

			if(!scwin.check_user_emailYN){
				var message = common.getLabel("lbl_userManager_admin_detail_duplicate");
				alert(message);
				return false;
			}

		}else {

		}

	    if(common.isEmptyStr(user_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_pwdInput");
			alert(message);
			return false;
		}

		if(common.isEmptyStr(user_confirm_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_pwdCheck");
			alert(message);
			return false;
		}

		if(user_password != user_confirm_password){
			var message = common.getLabel("lbl_userManager_admin_detail_samePwd");
			alert(message);
			return false;
		}

		if(common.isEmptyStr(member_name)){
			var message = common.getLabel("lbl_input_username");
			alert(message);
			return false;
		}

		if(common.isEmptyStr(user_rolecode)){
			var message = common.getLabel("lbl_userManager_admin_detail_permission");
			alert(message);
			return false;
		}

		return true;
	};

	// mmeber Detail view 조회
	scwin.memberDetailView = function(){

		var member_id = localStorage.getItem("_member_id_");

		// /manager/branchSetting/selectByBranchId/

		var options = {};

        // userDetail
		options.action = "/manager/member/search/userListDetail"; // + parseInt(member_id);
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;

			if (data != null) {

				var create_date = "";
				var updated_date = "";

				if(data.created_date != null){
					create_date = data.created_date.replace(/T/g,' ');
				}

				if (data.updated_date != null){
					updated_date = data.updated_date.replace(/T/g,' ');

				}

				var label = common.getLabel("lbl_userManager_admin_detail_secession_detail");
				label = common.getFormatStr(label,data.email);

				manager_user_id.setValue(data.user_login_id);
				manager_user_email.setValue(data.email);
				secession_email_label.setLabel(label);
				manager_member_name.setValue(data.user_name);
				user_select_rolecode_settingbox.setText(data.user_role, false);
				user_created_date.setValue(create_date);
				user_updated_date.setValue(updated_date);
				user_last_login_date.setValue(data.last_login_date);
				manager_role_name.setValue(data.role_name);

				// __member_create_data__
				__member_create_data__.setJSON([data]);


			} else {

			}
		};

		options.error = function (e) {
			alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");

		};

		$p.ajax( options );


	};

    scwin.memberOneDetailView = function(){

		 var member_id = localStorage.getItem("_member_id_");
		 var options = {};

		 // userDetail
		// /manager/member/search/userOneDetail/{user_id}
		 options.action = "/manager/member/search/userOneDetail/"+ parseInt(member_id); //
		 options.mode = "synchronous";
		 options.mediatype = "application/json";
		 options.method = "GET";

		 options.success = function (e) {
		 var data = e.responseJSON;

		 if (data != null) {

			 var create_date = "";
			 var updated_date = "";
			 var label = common.getLabel("lbl_userManager_admin_detail_secession_detail");
			 label = common.getFormatStr(label,data.email);

			 if(data.created_date != null){
				 create_date = data.created_date.replace(/T/g,' ');
			 }

			 if (data.updated_date != null){
				 updated_date = data.updated_date.replace(/T/g,' ');

			 }

			 manager_user_id.setValue(data.user_login_id);
			 manager_user_email.setValue(data.email);
			 secession_email_label.setLabel(label);
			 manager_member_name.setValue(data.user_name);
			 user_select_rolecode_settingbox.setText(data.user_role, false);
			 user_created_date.setValue(create_date);
			 user_updated_date.setValue(updated_date);
			 user_last_login_date.setValue(data.last_login_date);
			 manager_role_name.setValue(data.role_name);

			 // __member_create_data__
			 __member_create_data__.setJSON([data]);


			 } else {

			 }
		 };

		 options.error = function (e) {
			 alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");

		 };

		 $p.ajax( options );


	 };




	scwin.saveMembereDetailData = function(){
		// var member_setting_mode = localStorage.getItem("_member_setting_mode_");
		var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

		var user_login_id = manager_user_id.getValue();
		var user_email = manager_user_email.getValue();
		var user_password = manager_user_password.getValue();
		var user_confirm_password = manager_user_confirm_password.getValue();
		var member_name = manager_member_name.getValue();
		var domain_id = user_select_domain_settingbox.getValue(); //domain id select box 추가하기

		var user_save_data_list =  __member_create_data__.getRowJSON(0);
		//console.log(user_save_data_list);

		var data = {};
		data.user_login_id = user_login_id;
		data.email = user_email;
		data.password = user_password;
		data.confirmPassword = user_confirm_password;
		data.user_role = user_select_rolecode_settingbox.getText();
		data.user_name = member_name;
		data.domain_id = domain_id;

		if(member_setting_mode == "detailview"){
			data.role_id = scwin.user_role_id_temp_data;
		}else{
			data.role_id = scwin.user_role_id_temp_data;
		}


		__member_create_data__.removeAll();

		__member_create_data__.setJSON([data]);

		var member_craete_data = __member_create_data__.getRowJSON(0);

		//console.log(member_craete_data);
		if (member_setting_mode == "detailview" || member_setting_mode == "detailviewuser"){
			scwin.setMemberUserUpdate(member_craete_data);
		}else {
			if(scwin.checkDataCraete()){
				scwin.setMemberUserCreateAndInsert(member_craete_data);
			}

		}

	};

	scwin.select_check_user_email = function(user_email){

	    var data = {};

	    data.email = user_email;

		var options = {};

		// check userid controller 추가
		options.action = "/manager/member/search/checkEmail";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(data);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if(data != null){
				if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
					if (data != null) {

						if(data[0].user_name_not_found == "no"){
							var message = common.getLabel("lbl_exist_email");
							alert(message);
							scwin.check_user_emailYN = false;
						}else if(data[0].user_name_not_found == "yes"){
							var message = common.getLabel("lbl_userManager_admin_detail_available");
							alert(message);
							scwin.check_user_emailYN = true;
						}

						//alert(" 해당 이메일이 존재 합니다.");

					}
				}
			}

		};

		options.error = function (e) {
			if(e.responseStatusCode === 500){
				var message = common.getLabel("lbl_userManager_admin_detail_available");
				alert(message);
				// scwin.build_project_name_yn = true;
				scwin.check_user_emailYN = true;
			}else {
				alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
			}

		};

		$p.ajax( options );

	};

	scwin.select_check_user_login_id = function(user_login_id){
	    var data = {};
	    data.user_login_id = user_login_id;

	    var options = {};
	    // check userid controller 추가
		options.action = "/manager/member/search/checkUserId";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(data);
		options.method = "POST";

		options.success = function (e) {
		    var data = e.responseJSON;

		    if(data != null){
		    	if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
					if (data != null) {

						if(data[0].user_name_not_found == "no"){
							var message = common.getLabel("lbl_exist_id");
							alert(message);
							scwin.check_user_emailYN = false;
						}else if(data[0].user_name_not_found == "yes"){
							var message = common.getLabel("lbl_available_id");
							alert(message);
							scwin.check_user_emailYN = true;
						}

						//alert(" 해당 이메일이 존재 합니다.");

					}
		    	}
			}
		};

		options.error = function (e) {

		    if(e.responseStatusCode === 500){
				var message = common.getLabel("lbl_userManager_admin_detail_available");
				alert(message);
				// scwin.build_project_name_yn = true;
				scwin.check_user_emailYN = true;
			}else {
				alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n");
			}

		};

		$p.ajax( options );
	};

	// detail update
	scwin.setMemberUserUpdate = function (member_detail_data) {

		var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");
		var member_detail_json = {};

		member_detail_json = member_detail_data;

		var options = {};
		options.action = "/manager/member/update";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(member_detail_json);
		options.method = "POST";

		options.success = function (e) {
		    var data = e.responseJSON;
		    if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {
				var message = common.getLabel("lbl_userManager_admin_detail_modifiedSuccess");
		        alert(message);
				if(member_setting_mode == "detailview"){
					$p.parent().wfm_main.setSrc("/xml/userManager.xml");

				}else if(member_setting_mode == "detailviewuser"){
					$p.url("/index.xml");
				}

		    } else {
				var message = common.getLabel("lbl_userManager_admin_detail_modifiedFail");
		        alert(message);
		    }
		};

		options.error = function (e) {
		    alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);

		};

		$p.ajax( options );


	};

	scwin.setMemberUserCreateAndInsert = function (member_detail_data) {

		var member_detail_json = {};

		member_detail_json = member_detail_data;

		var options = {};
		options.action = "/manager/member/create";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(member_detail_json);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {
				var message = common.getLabel("lbl_userManager_admin_detail_createUser");
				alert(message);
				$p.parent().wfm_main.setSrc("/xml/userManager.xml");


			} else {
				var message = common.getLabel("lbl_userManager_admin_detail_failUser");
				alert(message);
			}
		};

		options.error = function (e) {
			alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);

		};

		$p.ajax( options );


	};

	 scwin.getRoleCodeListInfo = function () {

		 var codeType = "member";

		 // var member_setting_mode = localStorage.getItem("_member_setting_mode_");
		 var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

		 $.ajax({
			 url : "/manager/role/search/codeList/" + codeType,
			 type : "get",
			 accept : "application/json",
			 contentType : "application/json; charset=utf-8",
			 // data : JSON.stringify(data),
			 dataType : "json",
			 success : function(r,status) {
				 // alert(r);
				 // alert(status);
				 if(status === "success"){
				     var resultData = r;
                     var message = common.getLabel("lbl_select");
					 user_select_rolecode_settingbox.addItem("",message); //선택
					 for (var row in resultData) {
						 var temp = {};

						 user_select_rolecode_settingbox.addItem(resultData[row].role_code_id, resultData[row].role_code_name);
					 }

					 if(member_setting_mode == "detailviewuser"){
						 scwin.memberDetailView();

					 }else if(member_setting_mode == "detailview"){
						 scwin.memberOneDetailView();

					 }


				 }

			 },
			 error:function(request,status,error){
				 alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			 }

		 });

	 };

	 scwin.getDomainListInfo = function(){


		 var options = {};

		 options.action = "/manager/domain/search/domainList";
		 options.mode = "asynchronous";
		 options.mediatype = "application/json";
		 options.method = "GET";

		 options.success = function (e) {
			 var data = e.responseJSON;
			 if (data != null) {
                 var message = common.getLabel("lbl_select");
				 user_select_domain_settingbox.addItem("",message); //선택
				 for (var row in data) {
					 var temp = {};

					 user_select_domain_settingbox.addItem(data[row].domain_id, data[row].domain_name);

				 }

				 scwin.getDomainListByIDInfo();

			 } else {

			 }
		 };

		 options.error = function (e) {
			 alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

		 };

		 $p.ajax( options );

	 };

	 scwin.getDomainListByIDInfo = function(){

		var domain_id = localStorage.getItem("_domain_id_");

		 var options = {};

		 options.action = "/manager/domain/search/domain/"+parseInt(domain_id);
		 options.mode = "asynchronous";
		 options.mediatype = "application/json";
		 options.method = "GET";

		 options.success = function (e) {
			 var data = e.responseJSON;
			 if (data != null) {
				 // user_select_domain_settingbox.addItem("","선택");
				 // user_select_domain_settingbox.addItem(data.domain_id, data.domain_name);
				 user_select_domain_settingbox.setSelectedIndex( data.domain_id );

			 } else {

			 }
		 };

		 options.error = function (e) {
			 alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");

		 };

		 if(domain_id == null){

		 }else {
			 $p.ajax( options );
		 }


	 };

	 scwin.userPasswordReset = function(){

		 var data = {};

		 data.user_login_id = manager_user_id.getValue();
		 data.email = manager_user_email.getValue();
		 data.old_password = change_old_password.getValue();
		 data.new_password = change_user_password.getValue();
		 data.confirm_password = change_user_confirm_password.getValue();

		 var options = {};

		 // check userid controller 추가
		 options.action = "/manager/member/update/userAuthToken";
		 options.mode = "asynchronous";
		 options.mediatype = "application/json";
		 options.requestData = JSON.stringify(data);
		 options.method = "POST";

		 options.success = function (e) {
			 var data = e.responseJSON;

			 if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
				 if (data != null) {
					 var message = common.getLabel("lbl_userManager_admin_detail_newPwd_confirm");
				     alert(message);

					 //alert(" 해당 이메일이 존재 합니다.");
				 }
			 }


		 };

		 options.error = function (e) {
			 if(e.responseStatusCode === 500){
				 // alert(" 해당 이메일은 시용 가능 합니다.");
				 alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
				 // scwin.build_project_name_yn = true;
				 scwin.check_user_emailYN = true;
			 }else {
				 alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
			 }

		 };

		 $p.ajax( options );

	 };

	 scwin.secssion_check_send_email = function(){

		 var data = {};

		 data.email = input_secession_email.getValue();

		 var options = {};

		 // check userid controller 추가
		 options.action = "/manager/account/resign/sendEmail";
		 options.mode = "asynchronous";
		 options.mediatype = "application/json";
		 options.requestData = JSON.stringify(data);
		 options.method = "POST";

		 options.success = function (e) {
			 var data = e.responseJSON;

			 if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
				 if (data != null) {

					 if(data[0].user_name_not_found == "no"){
						 var message = common.getLabel("lbl_userManager_admin_detail_notExist");
						 alert(message);
					 }else if(data[0].user_name_not_found == "yes"){
						 var message = common.getLabel("lbl_userManager_admin_detail_sendEmail");
						 alert(message);

					 }

					 //alert(" 해당 이메일이 존재 합니다.");
				 }
			 }


		 };

		 options.error = function (e) {
			 if(e.responseStatusCode === 500){
				 // alert(" 해당 이메일은 시용 가능 합니다.");
				 alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
				 // scwin.build_project_name_yn = true;
				 scwin.check_user_emailYN = true;
			 }else {
				 alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
			 }

		 };

		 $p.ajax( options );


	 };


	scwin.btn_create_member_detail_onclick = function(e){
		scwin.saveMembereDetailData();


	};

	scwin.step1_select_platform_onchange = function(e){
		var platform = this.getValue(this.getSelectedIndex);
		localStorage.setItem("_role_code_id_",platform);
	};

	scwin.step1_btn_check_email_onclick = function(e){

		var email = manager_user_email.getValue();

		// if(scwin.checkDataProjectName()){

		if(common.isEmptyStr(email)){
			var message = common.getLabel("lbl_email_whitespace");
			alert(message);
			return false;
		}

		scwin.select_check_user_email(email);
		// }

	};

	scwin.step1_btn_check_id_onclick = function(){

	    var user_login_id = manager_user_id.getValue();

		if(common.isEmptyStr(user_login_id)){
			var message = common.getLabel("lbl_userManager_admin_detail_idBlank");
			alert(message);
			return false;
		}

		scwin.select_check_user_login_id(user_login_id);



	};

	scwin.btn_update_user_role_onclick = function(e){

		var role_grid_check = gridView1.getCheckedJSON("select_yn");

		manager_role_name.setValue( role_grid_check[0].role_name);

		__member_create_data__.setCellData(0, "role_id", role_grid_check[0].role_id);
		scwin.user_role_id_temp_data = role_grid_check[0].role_id;

		$(".layer_pop").css("display","none");
		$("div").remove(".dim");

	};

	scwin.btn_update_user_role_cancel_onclick = function(e){

		$(".layer_pop").css("display","none");
		$("div").remove(".dim");

	};

	scwin.btn_update_password_reset_onclick = function(){

		var email = manager_user_email.getValue();
		var old_password = change_old_password.getValue();
		var new_password = change_user_password.getValue();
		var confirm_password = change_user_confirm_password.getValue();

		// if(common.isEmptyStr(email)){
		// 	alert("E-Mail에 빈칸은 들어갈 수 없습니다.");
		// 	return false;
		// }

		if(common.isEmptyStr(old_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_pwdOLD");
			alert(message);
			return false;
		}

		// if(!common.checkAllInputText("CHECK_INPUT_TYPE_KOR_ENG_NUM",old_password)){
		// 	alert(" OLD 패스워드 확인 형식에 맞지 않습니다.\n ex) 문자, 숫자, 특수 문자를 포함한 8자리 이상의 문자열 ");
		// 	return false;
		// }

		if(common.isEmptyStr(new_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_newPwd");
			alert(message);
			return false;
		}

		if(!common.checkAllInputText("CHECK_INPUT_TYPE_PW",new_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_notice");
			alert(message);
			return false;
		}

        if(old_password == new_password){
			var message = common.getLabel("lbl_userManager_admin_detail_errorNotice");
			alert(message);
            return false;
		}

		if(common.isEmptyStr(confirm_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_confirmPwd");
			alert(message);
			return false;
		}

		if(!common.checkAllInputText("CHECK_INPUT_TYPE_PW",confirm_password)){
			var message = common.getLabel("lbl_userManager_admin_detail_formatError");
			alert(message);
			return false;
		}

		if(new_password != confirm_password){
			var message = common.getLabel("lbl_userManager_admin_detail_samePwd");
			alert(message);
			return false;
		}

		scwin.userPasswordReset();

	};

	scwin.secssion_start_onclick = function(){

		var secssion_email = input_secession_email.getValue();
        var user_email = manager_user_email.getValue();

		if(common.isEmptyStr(secssion_email)){
			var message = common.getLabel("lbl_email_whitespace");
			alert(message);
			return false;
		}

        if(secssion_email != user_email){
			var message = common.getLabel("lbl_userManager_admin_detail_errorEmail");
			alert(message);
			return false;
		}

		scwin.secssion_check_send_email();


	};

	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'branch_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_createBranch'}}]},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm type1 fl',id:'branch_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_create_member_detail_onclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_id'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'manager_user_id',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'btn_manager_user_id_check',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_id_onclick',useLocale:'true',localeRef:'lbl_userManager_admin_detail_duplicateId'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'manager_user_email_grp',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_email'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'manager_user_email',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'btn_manager_user_email_check',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_email_onclick',useLocale:'true',localeRef:'lbl_userManager_admin_detail_duplicateEmail'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'user_password_group',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'user_password_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_password'}},{T:1,N:'xf:input',A:{id:'manager_user_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'user_confirm_password_group',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'user_confirm_password_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_confirm'}},{T:1,N:'xf:input',A:{id:'manager_user_confirm_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_division'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'user_select_rolecode_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_name'}},{T:1,N:'xf:input',A:{id:'manager_member_name',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'user_domain_setting_group',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_domain'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'user_select_domain_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'user_manager_role_group',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_permissionName'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'manager_role_name',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'role_btn',style:'',class:'btn_role_cm',type:'button','ev:onclick':'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_reference'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'grp_user_craete_date',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_created_date'}},{T:1,N:'xf:input',A:{id:'user_created_date',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'grp_user_update_date',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_modified_date'}},{T:1,N:'xf:input',A:{id:'user_updated_date',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'grp_user_last_login_date',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_finalLogin'}},{T:1,N:'xf:input',A:{id:'user_last_login_date',style:'',adjustMaxLength:'false'}}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box',id:'change_password_body',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'change_password_title',label:'',style:'',useLocale:'true',localeRef:'lbl_change_password'}}]},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm type1 fl',id:'change_password_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_update_password_reset_onclick',useLocale:'true',localeRef:'lbl_userManager_admin_detail_modifyPw'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'change_old_password_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_oldPw'}},{T:1,N:'xf:input',A:{id:'change_old_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'change_password_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_newPw'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_userManager_admin_detail_pwRule'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'change_user_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'change_user_confirm_password_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_PwCheck'}},{T:1,N:'xf:input',A:{id:'change_user_confirm_password',dataType:'password',type:'password',style:'',adjustMaxLength:'false'}}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box',id:'secession_body',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'secession_title',label:'',style:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_secession'}}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'secession_email_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_mailNotice'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'input_secession_email',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.secssion_start_onclick',useLocale:'true',localeRef:'lbl_userManager_admin_detail_mailSend'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_userManager_admin_detail_user_permission'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox tac',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm',id:'',style:'',type:'button','ev:onclick':'scwin.btn_update_user_role_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm type1',id:'',style:'',type:'button','ev:onclick':'scwin.btn_update_user_role_cancel_onclick',useLocale:'true',localeRef:'lbl_cancel'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'form_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:gridView',A:{id:'gridView1',style:'width: 90%;height: 55%;position: absolute;',scrollByColumn:'false',defaultCellHeight:'5',scrollByColumnAdaptive:'false',dataList:'data:dlt_role_list',visibleRowNum:'5',rowNumVisible:'true',autoFit:'allColumn',contextMenu:'true',autoFitMinWidth:'0'},E:[{T:1,N:'w2:caption',A:{style:'',id:'caption1',value:''}},{T:1,N:'w2:header',A:{style:'',id:'header1'},E:[{T:1,N:'w2:row',A:{style:'',id:'row1'},E:[{T:1,N:'w2:column',A:{removeBorderStyle:'false',blockSelect:'false',width:'70',inputType:'text',style:'height:20px',id:'column2',value:'선택',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'column1',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_admin_detail_user_permission'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'column3',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_admin_detail_header1_role_id'}}]}]},{T:1,N:'w2:gBody',A:{style:'',id:'gBody1'},E:[{T:1,N:'w2:row',A:{style:'',id:'row2'},E:[{T:1,N:'w2:column',A:{removeBorderStyle:'false',blockSelect:'false',width:'70',inputType:'radio',style:'height:20px;',id:'select_yn',value:'',displayMode:'label',radioLabelColumn:'',textAlign:'center'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px',id:'role_name',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px',id:'role_id',value:'',blockSelect:'false',displayMode:'label'}}]}]}]}]}]}]}]}]}]}]}]})