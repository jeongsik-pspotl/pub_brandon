/*amd /xml/setting_branch_detail.xml 15038 feb3928845f97cbed440e62183912dbcb45355e4dca85fed07735ec30962e292 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'__branch_setting_detail_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'builder_id',name:'builder_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'role_code_id',name:'role_code_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'builder_user_id',name:'builder_user_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'builder_name',name:'builder_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'session_status',name:'session_status',dataType:'text'}},{T:1,N:'w2:column',A:{id:'session_type',name:'session_type',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'create_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_date',name:'update_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'builder_url',name:'builder_url',dataType:'text'}},{T:1,N:'w2:column',A:{id:'builder_password',name:'builder_password',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.check_branch_nameYN = false;
			 scwin.check_branch_user_idYN = false;
			 scwin.onpageload = function() {
				// 화면 구성 변경
				var branch_setting_mode = localStorage.getItem("_branch_setting_mode_");

				if (branch_setting_mode == "detailview"){
					var view = common.getLabel("lbl_setting_branch_detail_detailView");
					var save = common.getLabel("lbl_save");

					branch_setting_title.setLabel(view);
					branch_create_or_save_btn.setLabel(save);
					// setDisabled
					branch_setting_role_id.setDisabled(true);
					branch_setting_user_id.setDisabled(true);
					branch_setting_name.setDisabled(true);
					branch_setting_url.setDisabled(true);
					scwin.branchDetailView();
				}else {
					var view = common.getLabel("lbl_setting_branch_detail_createSetting");
					var create = common.getLabel("lbl_create");

					branch_setting_title.setLabel(view);
					branch_create_or_save_btn.setLabel(create);
					branch_setting_role_id.setDisabled(false);
					branch_setting_user_id.setDisabled(false);
					branch_setting_name.setDisabled(false);
					branch_setting_url.setDisabled(false);
				}

				localStorage.setItem("_role_code_id_",branch_setting_role_id.getValue());

			};
	
	scwin.onpageunload = function() {

	};

	scwin.checkDataBranchName = function(){

		var branch_name = branch_setting_name.getValue();


		if(common.isEmptyStr(branch_name)){
			var message = common.getLabel("lbl_setting_branch_detail_inputName");
			alert(message);
			return false;
		}

		return true;
	};

	scwin.checkDataBranchUserId = function(){

	    var branch_user_id = branch_setting_user_id.getValue();

	    if(common.isEmptyStr(branch_user_id)){
			var message = common.getLabel("lbl_setting_branch_detail_inputId_branch");
	        alert(message);
	        return false;
	    }

	    return true;
	};

	scwin.checkAllBranch = function(){

		var branch_name = branch_setting_name.getValue();
		var branch_user_id = branch_setting_user_id.getValue();

		if(common.isEmptyStr(branch_name)){
			var message = common.getLabel("lbl_setting_branch_detail_inputBuilder");
			alert(message);
			return false;
		}

		if(!scwin.check_branch_nameYN){
			var message = common.getLabel("lbl_setting_branch_detail_duplicate_builder");
			alert(message);
			return false;
		}

		if(common.isEmptyStr(branch_user_id)){
			var message = common.getLabel("lbl_setting_branch_detail_inputId_builder");
			alert(message);
			return false;
		}

		if(!scwin.check_branch_user_idYN){
			var message = common.getLabel("lbl_setting_branch_detail_duplicateId");
			alert(message);
			return false;
		}

		return true;

	};

	// brnach_setting 조회
	scwin.branchDetailView = function(){

		var branch_id = localStorage.getItem("_branch_id_");

		var options = {};

		options.action = "/manager/branchSetting/selectByBranchId/" + parseInt(branch_id);
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {

				var settingBuilderDetail = [];

				var temp = {};

				// temp["branch_id"] = data.branch_id;
				branch_setting_role_id.setValue(data.role_code_id, false);
				branch_setting_user_id.setValue(data.builder_user_id);
				branch_setting_name.setValue(data.builder_name);
				branch_setting_url.setValue(data.builder_url);

                temp["builder_id"] = data.builder_id;
				temp["role_code_id"] = data.role_code_id;
				temp["branch_user_id"] = data.branch_user_id;
				temp["branch_name"]	= data.branch_name;
				temp["session_status"] = data.session_status;
				temp["session_type"] = data.session_type;
				temp["branch_url"] = data.branch_url;
				temp["created_date"]= data.created_date;
				temp["updated_date"]= data.updated_date;

				settingBuilderDetail.push(temp);

				var distict = common.unique(settingBuilderDetail, 'builder_id');
				// __workspace_data__.setJSON(distict);
				__branch_setting_detail_data__.setJSON(distict);

			} else {
				// alert("가져오기 화면 이동 실패");
			}
		};

		options.error = function (e) {
			alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");

		};

		$p.ajax( options );


	};

	scwin.saveBranchSettingData = function(){
		//var project_dir_path = step1_input_project_dir_path.getValue();
		var role_code_id = localStorage.getItem("_role_code_id_");
		var branch_user_id = branch_setting_user_id.getValue();
		var branch_name = branch_setting_name.getValue();
		var session_status = "";
		var session_type = "";
		var branch_url = branch_setting_url.getValue();
        var builder_password = branch_setting_password.getValue();


		var data = {};
		data.role_code_id = role_code_id;
		data.builder_user_id = branch_user_id;
		data.builder_name = branch_name;
		data.session_status = "N";
		data.session_type = "BRANCH";
		data.builder_url = branch_url;
        data.builder_password = builder_password;

		__branch_setting_detail_data__.setJSON([data]);

		var branch_setting_data = __branch_setting_detail_data__.getRowJSON(0);

		if(scwin.checkAllBranch()){
			scwin.setBranchSettingAndInsert(branch_setting_data);
		}

	};

	scwin.select_check_branch_name = function(branch_name){
		var data = {};
		data.branch_name = branch_name;
		var options = {};

		options.action = "/manager/branchSetting/checkName";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(data);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
				if (data != null) {
					var message = common.getLabel("lbl_exist_name");
					alert(message);
					scwin.check_branch_nameYN = false;
				}
			}

		};

		options.error = function (e) {

			if(e.responseStatusCode === 500){
				var message = common.getLabel("lbl_can_use_name");
				alert(message);
				scwin.check_branch_nameYN = true;

			}else {
				var message = common.getLabel("lbl_can_use_name");
				alert(message);
				scwin.check_branch_nameYN = true;
			}

		};

		$p.ajax( options );

	};

	scwin.select_check_branch_user_id = function(branch_user_id){
	    var data = {};
	    data.branch_user_id = branch_user_id;
	    var options = {};

	    options.action = "/manager/branchSetting/checkUserId";
	    options.mode = "asynchronous";
	    options.mediatype = "application/json";
	    options.requestData = JSON.stringify(data);
	    options.method = "POST";

	    options.success = function (e) {
	        var data = e.responseJSON;
	        if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
	            if (data != null) {
					var message = common.getLabel("lbl_exist_name");
					alert(message);
	                scwin.check_branch_user_idYN = false;
	            }
	        }

	    };

	    options.error = function (e) {
	        if(e.responseStatusCode === 500){
				var message = common.getLabel("lbl_available_id");
	            alert(message);
	            scwin.check_branch_user_idYN = true;

	        }else {
				var message = common.getLabel("lbl_available_id");
				alert(message);
				scwin.check_branch_user_idYN = true;
	            // alert( "message:"+e.responseText+"\n" );
	        }

	    };

	    $p.ajax( options );
	};

	scwin.setBranchSettingAndInsert = function (branch_setting_data) {

		// 아래의 내용 수정하기... $p.ajax();
		var branch_setting_json = {};

		branch_setting_json = branch_setting_data;

		var options = {};
		options.action = "/manager/branchSetting/create";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(branch_setting_json);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {
				var message = common.getLabel("lbl_setting_branch_detail_createInfo");
				alert(message);

				$p.parent().wfm_main.setSrc("/xml/settings.xml");

			} else {
				var message = common.getLabel("lbl_setting_branch_detail_fail");
				alert(message);
			}
		};

		options.error = function (e) {
			alert( "message:"+e.responseText+"\n" );
			//$p.url("/login.xml");
		};

		$p.ajax( options );

	};

    scwin.setBuilderSettingAndUpdate = function(){

		var builder_password = branch_setting_password.getValue();

		if(common.isEmptyStr(builder_password)){
			var message = common.getLabel("lbl_setting_branch_detail_inputPwd");
			alert(message);
			return false;
		}

		var branch_setting_data = __branch_setting_detail_data__.getRowJSON(0);
		branch_setting_data.builder_password = builder_password;

		var branch_setting_json = {};

		branch_setting_json = branch_setting_data;

		var options = {};
		options.action = "/manager/branchSetting/update";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(branch_setting_json);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {
				var message = common.getLabel("lbl_setting_branch_detail_changeOk");
				alert(message);

			} else {
				var message = common.getLabel("lbl_setting_branch_detail_changeFail");
				alert(message);
			}
		};

		options.error = function (e) {
			alert( "message:"+e.responseText+"\n" );
			//$p.url("/login.xml");
		};

		$p.ajax( options );

	};

	scwin.btn_create_branch_setting_onclick = function(e){

		var branch_setting_mode = localStorage.getItem("_branch_setting_mode_");
        if(branch_setting_mode == "detailview"){
			scwin.setBuilderSettingAndUpdate();
		}else {
			scwin.saveBranchSettingData();
		}



	};

	scwin.step1_select_platform_onchange = function(e){
		var platform = this.getValue(this.getSelectedIndex);
		localStorage.setItem("_role_code_id_",platform);
	};

	scwin.step1_btn_check_branch_name_onclick = function(e){

		var branch_name = branch_setting_name.getValue();

		if(scwin.checkDataBranchName()){
			scwin.select_check_branch_name(branch_name);
		}

	};

	scwin.step1_btn_check_branch_user_id_onclick = function(e){

	    var branch_user_id = branch_setting_user_id.getValue();

	    if(scwin.checkDataBranchUserId()){
	        scwin.select_check_branch_user_id(branch_user_id);
	    }

	};
	
	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'branch_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_setting_branch_detail_setting'}}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm type1 fl',id:'branch_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_create_branch_setting_onclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_branch_detail_name'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'branch_setting_name',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_branch_name_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_branch_detail_id'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'branch_setting_user_id',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_branch_user_id_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_branch_detail_permission'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'branch_setting_role_id',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_platform_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'build'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'1'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'deploy'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'2'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'All'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'3'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_branch_detail_url'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'branch_setting_url',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_branch_detail_password'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'branch_setting_password',style:'',adjustMaxLength:'false'}}]}]}]}]}]}]}]}]}]})