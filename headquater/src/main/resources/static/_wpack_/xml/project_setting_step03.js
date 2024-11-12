/*amd /xml/project_setting_step03.xml 28458 2023ab79be86051fc4808fc48c1fdff4fec3d621c748a4b2cd44c0c2a5d4b54e */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
    scwin.platformType = "";
	serverinfo_itemIdx = 0;
	scwin.onpageload = function() {
		common.setScopeObj(scwin);
		// ToolTip
		$(".tooltip_box .ico_tip").click(function(){
			$(this).siblings(".tip_desc").toggleClass("on").parent().parent().siblings().children().children(".tip_desc").removeClass("on");
		});
		scwin.initPage();
	};
	
	scwin.onpageunload = function() {
		
	};

	scwin.initPage = function(){

	    var data = {};
		var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();
		var buildsetting_json = $p.parent().dtl_build_setting_step2.getAllJSON();

		var update_yn = localStorage.getItem("__update_yn__");

		if(update_yn == "1"){

		}else {
			step3_fr_grp_proj_save.hide();
		}

		// var whive_session = sessionStorage.getItem("__whybrid_session__");
		// whive_session = JSON.parse(whive_session);

		// scwin.setBuildSetting(buildproj_json, buildsetting_json);
		scwin.platformType = buildproj_json[0].platform;
		// input_serverurl.setValue(buildsetting_json[0].server_URL);

		// ajax data set
		// data.hqKey = whive_session.user_login_id;
		// data.domainID = whive_session.domain_id;
		// data.userID = whive_session.id;
		data.build_id = buildproj_json[0].project_id;
		data.workspace_id = buildproj_json[0].workspace_id;
		data.project_name = buildproj_json[0].project_name;
		data.project_dir_path = buildproj_json[0].project_dir_path;
        data.product_type = buildproj_json[0].product_type;

		if (scwin.platformType == "Android"){

			// android  getserverconfig 조회 기능 적용
			data.platform = scwin.platformType;
			form_wrap_ios.hide();
			if($p.parent().scwin.getResultAppConfigData === undefined){
				scwin.getHybridServerConfig(data);

			}else {
				scwin.setServerConfigData($p.parent().scwin.getResultAppConfigData);
			}

		}else if(scwin.platformType == "iOS"){

			// ios getserverconfig 조회 기능 적용
			data.platform = scwin.platformType;
			form_wrap_android.hide();

			if($p.parent().scwin.getResultAppConfigData === undefined){
				scwin.getHybridServerConfig(data);

			}else {
				scwin.setServerConfigData($p.parent().scwin.getResultAppConfigData);
			}
		}



	};

	scwin.btn_prev_onclick = function(e) {
		$p.parent().scwin.selected_step(2);
	};

	scwin.btn_next_onclick = function(e) {
		$p.parent().scwin.selected_step(4);
	};

	scwin.setBuildSetting = function(buildproj_json, buildsetting_json){



	};

	scwin.webSocketCallback = function(obj) {
		// 추가 개발시 msg type 추가
		switch (obj.MsgType) {
			case "HV_MSG_CONNETCION_CHECK_INFO_FROM_HEADQUATER" :
				scwin.setBranchProjectCreateSyncStatus(obj);
				break;
			case "HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_HEADQUATER" :
				scwin.setBranchProjectCreateSyncStatus(obj);
				break;
			case "HV_MSG_WINDOWS_CONFIG_LIST_INFO_FROM_HEADQUATER" :
				// scwin.setWindowsConfigListStatus(obj);
				break;
			case "HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO_FROM_HEADQUATER" :
			    scwin.setBuilderTemplateServerConfigStatus(obj);
			    break;
			case "HV_MSG_PROJECT_SERVER_CONFIG_UPDATE_STATUS_INFO_FROM_HEADQUATER" :
				scwin.setBuilderTemplateServerConfigStatus(obj);
			    break;
			default :
				break;
		}
	};

	scwin.setBranchProjectCreateSyncStatus = function(obj){
		// git sync return
		switch (obj.gitStatus) {
			case "GITCLONE":
				var msg = common.getLabel("lbl_git_clone");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "SVNCHECKOUT":
				var msg = common.getLabel("lbl_svn_checkout");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "APPICONUNZIP":
				var msg = common.getLabel("lbl_app_icon_uploading");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "CONFIG":
				var msg = common.getLabel("lbl_app_config_setting");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "GITCOMMIT":
				var msg = common.getLabel("lbl_git_commit");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "REMOTESET":
				var msg = common.getLabel("lbl_project_setting_step03_remote");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "GITADD":
				var msg = common.getLabel("lbl_git_add");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "GITPUSH":
				var msg = common.getLabel("lbl_git_push");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "SVNADD":
				var msg = common.getLabel("lbl_svn_add");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "SVNCOMMIT":
				var msg = common.getLabel("lbl_svn_commit");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "DONE":
				WebSquare.layer.hideProcessMessage();
				var message = common.getLabel("lbl_project_setting_step03_successSetting");
				alert(message);
				break;
			default :
				break;
		}
	};

	scwin.setBuilderTemplateServerConfigStatus = function(obj) {

		switch (obj.message) {
			case "SEARCHING":
				var msg = common.getLabel("lbl_project_setting_step03_configList");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "CONFIGUPDATE" :
				var msg = common.getLabel("lbl_project_setting_step03_configUpdate");
				WebSquare.layer.showProcessMessage(msg);
			    break;
			case "DONE" :
				WebSquare.layer.hideProcessMessage();
				// alert("W-Hybrid Server 설정 수정 완료");
			    break;
			case "SUCCESSFUL":
			    scwin.setServerConfigData(obj);
				WebSquare.layer.hideProcessMessage();
				break;
			default :
				break;
		}
	};

	scwin.setServerConfigData = function(obj){

		var buildsetting_json = $p.parent().dtl_build_setting_step2.getAllJSON();

		if(scwin.platformType == "Android"){

			var ServerConfigArr = obj.resultServerConfigListObj.Server;

			for (var i = 0 ; i < ServerConfigArr.length; i++){

			    if(i == 0){
					input_serverurl_1.setValue(ServerConfigArr[i].ServerURL);
					input_appid_1.setValue(ServerConfigArr[i].AppID);
					input_appname_1.setValue(ServerConfigArr[i].Name);

				}else {

					var group_ul1 = WebSquare.util.dynamicCreate("myid_group_ul_"+i,"group",{tagname : "ul"},group_server_info_main);

					var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_1_"+i,"group",{tagname : "li"},group_ul1);

					var append_output_name = WebSquare.util.dynamicCreate("myid_name_"+i,"textbox",{className : "w2textbox form_name" },group_li1);

					var name = common.getLabel("lbl_name");
					append_output_name.setLabel(name);

					var append_output_name_inputbox = WebSquare.util.dynamicCreate("myid_name_input_"+i,"input",{ },group_li1);

					var group_li2 = WebSquare.util.dynamicCreate("myid_group_li_2_"+i,"group",{tagname : "li"},group_ul1);

					var append_output_app_id_input = WebSquare.util.dynamicCreate("myid_app_id_"+i,"textbox",{ className : "w2textbox form_name" },group_li2);

					var appID = common.getLabel("lbl_appid");
					append_output_app_id_input.setLabel(appID);

					var append_output_app_id = WebSquare.util.dynamicCreate("myid_app_id_input_"+i,"input",{ },group_li2);

					var group_li3 = WebSquare.util.dynamicCreate("myid_group_li_3_"+i,"group",{tagname : "li"},group_ul1);

					var append_output_server_url = WebSquare.util.dynamicCreate("myid_server_url_"+i,"textbox",{className : "w2textbox form_name"  },group_li3);

					var url = common.getLabel("lbl_serverurl");
					append_output_server_url.setLabel(url);

					var group_ipt_box3_1 = WebSquare.util.dynamicCreate("myid_group_ipt_box_3_"+i,"group",{className : "ipt_box"},group_li3);
					var append_output_server_url_input = WebSquare.util.dynamicCreate("myid_server_url_input_"+i,"input",{ },group_ipt_box3_1);
					var append_output_server_url_trigger = WebSquare.util.dynamicCreate("myid_server_url_trigger_"+i,"trigger",{ className : "btn_cm"},group_ipt_box3_1);

					var dash = common.getLabel("lbl_dash");
					append_output_server_url_trigger.setLabel(dash);
					// server info
					// trigger, onclick 기능 추가 -> 삭제 기능
					append_output_server_url_trigger.bind("onclick",function(e){
						var save_group_ul1 = group_ul1;

						save_group_ul1.remove();

					});


					var server_name = $p.getComponentById("myid_name_input_"+i );
					var server_app_id =  $p.getComponentById("myid_app_id_input_"+i );
					var server_url = $p.getComponentById("myid_server_url_input_"+i );

					server_name.setValue(ServerConfigArr[i].Name);
					server_app_id.setValue(ServerConfigArr[i].AppID);
					server_url.setValue(ServerConfigArr[i].ServerURL);
				}

			}

			serverinfo_itemIdx = ServerConfigArr.length;

			input_packagename.setValue(buildsetting_json[0].package_name);
		}else if(scwin.platformType == "iOS"){

			var ServerConfigArr = obj.serverconfig.server;

			for (var i = 0 ; i < ServerConfigArr.length; i++){

				if(i == 0){
					input_serverurl_1.setValue(ServerConfigArr[i].ServerURL);
					input_appid_1.setValue(ServerConfigArr[i].AppID);
					input_appname_1.setValue(ServerConfigArr[i].Name);

				}else {

					var group_ul1 = WebSquare.util.dynamicCreate("myid_group_ul_"+i,"group",{tagname : "ul"},group_server_info_main);

					var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_1_"+i,"group",{tagname : "li"},group_ul1);

					var append_output_name = WebSquare.util.dynamicCreate("myid_name_"+i,"textbox",{className : "w2textbox form_name" },group_li1);

					var name = common.getLabel("lbl_name");
					append_output_name.setLabel(name);

					var append_output_name_inputbox = WebSquare.util.dynamicCreate("myid_name_input_"+i,"input",{ },group_li1);

					var group_li2 = WebSquare.util.dynamicCreate("myid_group_li_2_"+i,"group",{tagname : "li"},group_ul1);

					var append_output_app_id_input = WebSquare.util.dynamicCreate("myid_app_id_"+i,"textbox",{ className : "w2textbox form_name" },group_li2);

					var appID = common.getLabel("lbl_appid");
					append_output_app_id_input.setLabel(appID)

					var append_output_app_id = WebSquare.util.dynamicCreate("myid_app_id_input_"+i,"input",{ },group_li2);

					var group_li3 = WebSquare.util.dynamicCreate("myid_group_li_3_"+i,"group",{tagname : "li"},group_ul1);

					var append_output_server_url = WebSquare.util.dynamicCreate("myid_server_url_"+i,"textbox",{className : "w2textbox form_name"  },group_li3);

					var url = common.getLabel("lbl_serverurl");
					append_output_server_url.setLabel(url);

					var group_ipt_box3_1 = WebSquare.util.dynamicCreate("myid_group_ipt_box_3_"+i,"group",{className : "ipt_box"},group_li3);
					var append_output_server_url_input = WebSquare.util.dynamicCreate("myid_server_url_input_"+i,"input",{ },group_ipt_box3_1);
					var append_output_server_url_trigger = WebSquare.util.dynamicCreate("myid_server_url_trigger_"+i,"trigger",{ className : "btn_cm"},group_ipt_box3_1);

					var dash = common.getLabel("lbl_dash");
					append_output_server_url_trigger.setLabel(dash);
					// server info
					// trigger, onclick 기능 추가 -> 삭제 기능
					append_output_server_url_trigger.bind("onclick",function(e){
						var save_group_ul1 = group_ul1;

						save_group_ul1.remove();

					});


					var server_name = $p.getComponentById("myid_name_input_"+i );
					var server_app_id =  $p.getComponentById("myid_app_id_input_"+i );
					var server_url = $p.getComponentById("myid_server_url_input_"+i );

					server_name.setValue(ServerConfigArr[i].Name);
					server_app_id.setValue(ServerConfigArr[i].AppID);
					server_url.setValue(ServerConfigArr[i].ServerURL);
				}

			}

			serverinfo_itemIdx = ServerConfigArr.length;

			input_projectname.setValue(buildsetting_json[0].package_name);
		}

	};


	// 해당 호출 ajax 처리 function step 2 xml로 이동해야함.
	scwin.getHybridServerConfig = function (data){

		var options = {};
		options.action = "/manager/build/setting/search/serverConfig";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(data);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {


			} else {

			}
		};

		options.error = function (e) {
			alert("message:"+e.responseText+"\n");
		};

		$p.ajax( options );

	};

	scwin.checkData = function () {

		var packageName = "";

		var server_name_1 = input_appname_1.getValue();

		var app_id1 = input_appid_1.getValue();

		var server_URL1 = input_serverurl_1.getValue();

		var appIDCheck1 = "";
		var appIDCheckAll = "";
		var appIDCheck3 = "";

		if(scwin.platformType == "Android"){
			packageName = input_packagename.getValue();
		}else if(scwin.platformType == "iOS"){
			packageName = input_projectname.getValue();
		}

		// name, server, appid 정보
		if (common.isEmptyStr(packageName) && scwin.platformType == "Android") {
			var message = common.getLabel("lbl_check_package_name");
			alert(message);
			return false;
		}

		if (common.isEmptyStr(packageName) && scwin.platformType == "iOS") {
			var message = common.getLabel("lbl_check_project_name");
			alert(message);
			return false;
		}

		// name, server, appid 정보
		// server name 빈칸
		if (common.isEmptyStr(server_name_1) ) {
			var message = common.getLabel("lbl_check_server_name");
			alert(message);
			return false;
		}

		// app id 빈칸
		if (common.isEmptyStr(app_id1) ) {
			var message = common.getLabel("lbl_input_appid");
			alert(message);
			return false;
		}

		// server url 빈칸
		if (common.isEmptyStr(server_URL1) ) {
			var message = common.getLabel("lbl_project_setting_step03_input_serverURL");
			alert(message);
			return false;
		}

		// package name 한글 입력
		if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",packageName) && scwin.platformType == "Android"){
			var message = common.getLabel("lbl_package_name_kor_rule");
			alert(packageName + message);
			return false;
		}

		// project name 한글 입력
		if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",packageName) && scwin.platformType == "iOS"){
			var message = common.getLabel("lbl_proejct_name_kor_rule");
			alert(packageName + message);
			return false;
		}

		// server name 한글 입력
		if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",server_name_1) ){
			var message = common.getLabel("lbl_project_setting_step03_name_kor_123");
			alert(server_name_1 + message);
			return false;
		}

		// server url 빈칸
		if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",server_URL1) ){
			var message = common.getLabel("lbl_project_setting_step03_url_kor_123");
			alert(server_URL1 + message);
			return false;
		}

		if(common.checkAllInputText("CHECK_INPUT_TYPE_SPC",packageName) && scwin.platformType == "Android"){
			var message = common.getLabel("lbl_package_name_special_char_rule");
			alert(packageName + message);
			return false;
		}

		if(common.checkAllInputText("CHECK_INPUT_TYPE_SPC",packageName) && scwin.platformType == "iOS"){
			var message = common.getLabel("lbl_project_name_special_char_rule");
			alert(packageName + message);
			return false;
		}

		if(common.checkAllInputText("CHECK_INPUT_TYPE_SPC",server_name_1) ){
			var message = common.getLabel("lbl_project_setting_step03_123_specialCharacter");
			alert(server_name_1 + message);
			return false;
		}


		appIDCheck1 = app_id1.split("\.");

		// appID 1 check
		if(appIDCheck1.length >= 3 || appIDCheck1.length >= 4){

			for(var i = 0; i < appIDCheck1.length; i++){
				var strAppID = appIDCheck1[i];

				var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2",strAppID);

				if(!check_app_id_str){
					var message = common.getLabel("lbl_appid_check_form");
					alert(app_id + message);
					return false;
				}
			}

		}else {
			var message = common.getLabel("lbl_appid_check_form");
			alert(app_id1 + message);
			return false;
		}

		for (var i = 1 ; i < serverinfo_itemIdx; i++){

			var servertempJson = {};

			var server_name = $p.getComponentById("myid_name_input_"+i );
			var server_app_id =  $p.getComponentById("myid_app_id_input_"+i );
			var server_url = $p.getComponentById("myid_server_url_input_"+i );

			if(server_name != null){
				servertempJson.Name = server_name.getValue();

				// server name 빈칸
				if (common.isEmptyStr(servertempJson.Name) ) {
					var message = common.getLabel("lbl_check_server_name");
					alert(message);
					return false;
				}

				// server name 한글 입력
				if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",servertempJson.Name)){
					var message = common.getLabel("lbl_server_name_kor_rule");
					alert(servertempJson.Name + message);
					return false;
				}

				if(common.checkAllInputText("CHECK_INPUT_TYPE_SPC",servertempJson.Name)){
					var message = common.getLabel("lbl_server_name_special_char_rule");
					alert(servertempJson.Name + message);
					return false;
				}

			}


			if(server_app_id != null){
				servertempJson.AppID = server_app_id.getValue();

				// app id 빈칸
				if (common.isEmptyStr(servertempJson.AppID) ) {
					var message = common.getLabel("lbl_input_appid");
					alert(message);
					return false;
				}

				appIDCheckAll = servertempJson.AppID.split("\.");

				if(appIDCheckAll.length >= 3 || appIDCheckAll.length >= 4){
					for(var app = 0; app < appIDCheckAll.length; app++){
						var strAppID = appIDCheckAll[app];

						var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2",strAppID);

						if(!check_app_id_str){
							var message = common.getLabel("lbl_appid_check_form");
							alert(servertempJson.AppID + message);
							return false;
						}
					}

				}else {
					var message = common.getLabel("lbl_appid_check_form");
					alert(servertempJson.AppID + message);
					return false;
				}

			}

			if(server_url != null){
				servertempJson.ServerURL = server_url.getValue();

				// server url 빈칸
				if (common.isEmptyStr(servertempJson.ServerURL) ) {
					var message = common.getLabel("lbl_project_setting_step03_inputUrl");
					alert(message);
					return false;
				}

				if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",servertempJson.ServerURL)){
					var message = common.getLabel("lbl_project_setting_step03_url_kor");
					alert(servertempJson.ServerURL + message);
					return false;
				}

			}


		}

		return true;

	};

	scwin.updateServerConfig = function() {

		var buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);
		var buildsetting_json = $p.parent().dtl_build_setting_step2.getRowJSON(0);

		var templateCheck = buildproj_json.template_version;

		var serverConfigArray = [];
		var server1Json = {};

		server1Json.Name = input_appname_1.getValue();
		server1Json.AppID = input_appid_1.getValue();
		server1Json.ServerURL = input_serverurl_1.getValue();

		serverConfigArray.push(server1Json);

		for (var i = 1 ; i < serverinfo_itemIdx; i++){

			var servertempJson = {};

			var server_name = $p.getComponentById("myid_name_input_"+i );
			var server_app_id =  $p.getComponentById("myid_app_id_input_"+i );
			var server_url = $p.getComponentById("myid_server_url_input_"+i );

			if(server_name != null){
				servertempJson.Name = server_name.getValue();
			}

			if(server_app_id != null){
				servertempJson.AppID = server_app_id.getValue();
			}

			if(server_url != null){
				servertempJson.ServerURL = server_url.getValue();
				serverConfigArray.push(servertempJson);
			}

		}


		//임시 주석 처리
		if(templateCheck == ""){
			scwin.setBuildSetting(buildproj_json, buildsetting_json);
		}else {
			scwin.setServerConfigController(buildproj_json, buildsetting_json, serverConfigArray);

		}

	};

	scwin.setServerConfigController = function(buildproj_json, buildsetting_json, serverConfigArray){

		// var whive_session = sessionStorage.getItem("__whybrid_session__");
		// whive_session = JSON.parse(whive_session);


		var build_all_in_json = {};

		// build_all_in_json.hqKey = whive_session.user_login_id;
		// build_all_in_json.domainID = whive_session.domain_id;
		// build_all_in_json.userID = whive_session.id;
		if(scwin.platformType == "Android"){
			build_all_in_json.packageName = input_packagename.getValue();
		}else if(scwin.platformType == "iOS"){
			build_all_in_json.packageName = input_projectname.getValue();
		}

		build_all_in_json.buildProject = buildproj_json;
		build_all_in_json.buildSetting = buildsetting_json;
		build_all_in_json.serverConfig = serverConfigArray;

		var options = {};
		options.action = "/manager/build/project/update/templateProject";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(build_all_in_json);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {

				var build_id = data[0].build_id;
				// $p.parent().dtl_build_project_step1.setCellData(0, "id", build_id);
				// localStorage.setItem("__create_build_id__",build_id);
				btn_update_project_trigger.setDisabled(true);

			} else {
				var message = common.getLabel("lbl_project_setting_step03_saveFail");
				alert(message);
			}
		};

		options.error = function (e) {
			alert("message:"+e.responseText+"\n");
			//$p.url("/login.xml");
		};

		$p.ajax( options );

	};

	scwin.btn_setserverconfig_setting_onclick = function(){

		if (scwin.checkData()) {
			var message = common.getLabel("lbl_project_setting_step03_serverSetting");
			if (confirm(message))
			{
				scwin.updateServerConfig();
			}
			else
			{

			}

		}

	};

	// build setting onclick 세팅..
	scwin.btn_append_input_bundle_id_onclick = function(e){

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_ul1 = WebSquare.util.dynamicCreate("myid_group_ul_"+serverinfo_itemIdx,"group",{tagname : "ul"},group_server_info_main);

		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_1_"+serverinfo_itemIdx,"group",{tagname : "li"},group_ul1);

		var append_output_name = WebSquare.util.dynamicCreate("myid_name_"+serverinfo_itemIdx,"textbox",{className : "w2textbox form_name" },group_li1);

		var name = common.getLabel("lbl_name");
		append_output_name.setLabel(name);

		var append_output_name_inputbox = WebSquare.util.dynamicCreate("myid_name_input_"+serverinfo_itemIdx,"input",{ },group_li1);

		var group_li2 = WebSquare.util.dynamicCreate("myid_group_li_2_"+serverinfo_itemIdx,"group",{tagname : "li"},group_ul1);

		var append_output_app_id_input = WebSquare.util.dynamicCreate("myid_app_id_"+serverinfo_itemIdx,"textbox",{ className : "w2textbox form_name" },group_li2);

		var appID = common.getLabel("lbl_appid");
		append_output_app_id_input.setLabel(appID)

		var append_output_app_id = WebSquare.util.dynamicCreate("myid_app_id_input_"+serverinfo_itemIdx,"input",{ },group_li2);


		var group_li3 = WebSquare.util.dynamicCreate("myid_group_li_3_"+serverinfo_itemIdx,"group",{tagname : "li"},group_ul1);

		var append_output_server_url = WebSquare.util.dynamicCreate("myid_server_url_"+serverinfo_itemIdx,"textbox",{className : "w2textbox form_name"  },group_li3);

		var url = common.getLabel("lbl_serverurl");
		append_output_server_url.setLabel(url);

		var group_ipt_box3_1 = WebSquare.util.dynamicCreate("myid_group_ipt_box_3_"+serverinfo_itemIdx,"group",{className : "ipt_box"},group_li3);
		var append_output_server_url_input = WebSquare.util.dynamicCreate("myid_server_url_input_"+serverinfo_itemIdx,"input",{ },group_ipt_box3_1);
		var append_output_server_url_trigger = WebSquare.util.dynamicCreate("myid_server_url_trigger_"+serverinfo_itemIdx,"trigger",{ className : "btn_cm"},group_ipt_box3_1);

		var dash = common.getLabel("lbl_dash");
		append_output_server_url_trigger.setLabel(dash);
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_server_url_trigger.bind("onclick",function(e){
			var save_group_ul1 = group_ul1;

			save_group_ul1.remove();

		});

		// itemIdx++ 형식으로 id 채번 하기
		serverinfo_itemIdx++;

	};

	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step03_reference'}},{T:1,N:'xf:group',A:{class:'fr',id:'step3_fr_grp_proj_save'},E:[{T:1,N:'w2:textbox',A:{class:'txt_chk',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step03_saveNotice'}},{T:1,N:'xf:trigger',A:{class:'btn_cm type1',id:'btn_update_project_trigger',style:'',type:'button','ev:onclick':'scwin.btn_setserverconfig_setting_onclick',useLocale:'true',localeRef:'lbl_project_setting_step03_save'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'form_wrap_android',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_package_name'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_packagename',style:'',initValue:''}}]}]}]},{T:1,N:'xf:group',A:{id:'form_wrap_ios',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_name'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_projectname',style:'',initValue:''}}]}]}]},{T:1,N:'xf:group',A:{id:'group_server_info_main',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_appname_1',style:'',initValue:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_used_appid'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_appid_1',style:'',initValue:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_server_address'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_address'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'input_serverurl_1',style:''}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_append_input_bundle_id_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'+'}]}]}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})