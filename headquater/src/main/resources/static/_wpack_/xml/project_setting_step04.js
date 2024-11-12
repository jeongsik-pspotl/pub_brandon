/*amd /xml/project_setting_step04.xml 39434 a72999a2f01fba22605578e2695cc1d808542fa87aa2a40fe0cd6fdaf77a0e14 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.pluginArray = [];
			 var installed_plugin_json = [];
			 var available_plugin_json = [];
             var available_basic_plugin_json = [];
	scwin.onpageload = function() {
		// 현재 나의 scope 객체를 전역 변수에 저장한다.

		common.setScopeObj(scwin);

        var getResultPluginData = $p.parent().scwin.getResultAppConfigData;
        var platform = $p.parent().scwin.txt_project_all_step_platform;
		// set parameter

		if(platform == "Android"){

            scwin.setpluginlisttag(getResultPluginData);

			installed_plugin_json = $p.parent().dtl_plugin_setting_installed_step4.getAllJSON();
			available_plugin_json = $p.parent().dtl_plugin_setting_available_step4.getAllJSON();
			available_basic_plugin_json = $p.parent().dtl_plugin_setting_available_basic_step4.getAllJSON();

			scwin.setPluginListSettingDataList(installed_plugin_json,available_plugin_json, available_basic_plugin_json);
			scwin.pluginArray = [];

		}else if(platform == "Windows"){

			var data = {};

			data.build_id = local_buildproject_id;
			data.platform = platform;
			data.workspace_name = workspace_name;
			data.project_name = project_name;
			// data.hqKey = whive_session.user_login_id; // hq key 추가
			data.project_dir_name = project_dir_name;

			// plugin 조회시 ajax call -> service send message 처리
			scwin.getProjectToPluginList(data);
		} else {

			scwin.setpluginlisttag(getResultPluginData);

			installed_plugin_json = $p.parent().dtl_plugin_setting_installed_step4.getAllJSON();
			available_plugin_json = $p.parent().dtl_plugin_setting_available_step4.getAllJSON();
			available_basic_plugin_json = $p.parent().dtl_plugin_setting_available_basic_step4.getAllJSON();

			scwin.setPluginListSettingDataList(installed_plugin_json,available_plugin_json, available_basic_plugin_json);
			scwin.pluginArray = [];

		}

	};
	
	scwin.onpageunload = function() {
		
	};

	scwin.getProjectToPluginList = function(data){

		$.ajax({
			url : "/api/buildproject/pluginlist",
			type : "post",
			accept : "application/json",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(data),
			dataType : "json",
			success : function(r,status) {

				if(status === "success"){

				}

			},
			error:function(request,status,error){
				alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			}

		});

	};

	scwin.setProjectToPluginUpdate = function(data){

		$.ajax({
			url : "/api/buildproject/pluginupdate",
			type : "post",
			accept : "application/json",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(data),
			dataType : "json",
			success : function(r,status) {

				if(status === "success"){

				}

			},
			error:function(request,status,error){
				alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			}

		});

	};

	scwin.getTemplateProjectToPluginList = function(data){
		$.ajax({
			url : "/manager/build/project/search/pluginList",
			type : "post",
			accept : "application/json",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(data),
			dataType : "json",
			success : function(r,status) {

				if(status === "success"){

				}

			},
			error:function(request,status,error){
				alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			}

		});

	};

	scwin.setTemplateProjectPluginUpdate = function(data){

		$.ajax({
			url : "/manager/build/project/update/plugin",
			type : "post",
			accept : "application/json",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(data),
			dataType : "json",
			success : function(r,status) {

				if(status === "success"){

				}

			},
			error:function(request,status,error){
				alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			}

		});

	};

	scwin.webSocketCallback = function(obj) {

		switch (obj.MsgType) {
			case "HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO_FROM_HEADQUATER":
				scwin.setBranchPluginListStatus(obj);
		    	break;
			case "HV_MSG_PLUGIN_LIST_INFO_FROM_HEADQUATER" :
				scwin.setBuilderPluginListStatusNoBuildPage(obj);
				break;
			case "HV_MSG_PLUGIN_ADD_LIST_INFO_FROM_HEADQUATER" :
			    scwin.setBranchPluginListStatus(obj);
				break;
			case "HV_MSG_PLUGIN_REMOVE_LIST_INFO_FROM_HEADQUATER" :
				scwin.setBranchPluginListStatus(obj);
				break;
			default :
				break;
		}
	};

	scwin.setBuilderPluginListStatusNoBuildPage = function (data) {
		 if (data.message == "SEARCHING") {
			 // project_build_status.setLabel("빌드중");
			 var msg = common.getLabel("lbl_project_setting_step04_list");
			 WebSquare.layer.showProcessMessage(msg);

		 }else if(data.message == "ADDLOADING"){
			 var msg = common.getLabel("lbl_plugin_add");
			 WebSquare.layer.showProcessMessage(msg);

		 }else if(data.message == "REMOVELOADING"){
			 var msg = common.getLabel("lbl_plugin_remove");
			 WebSquare.layer.showProcessMessage(msg);

		 } else if (data.message == "SUCCESSFUL") {

			 scwin.setpluginlisttag(data);
			 WebSquare.layer.hideProcessMessage();
			 // plugin list data setting
			 installed_plugin_json = $p.parent().dtl_plugin_add_installed_step4.getAllJSON();
			 available_plugin_json = $p.parent().dtl_plugin_add_available_step4.getAllJSON();
			 available_basic_plugin_json = $p.parent().dtl_plugin_add_available_basic_step4.getAllJSON();

			 scwin.setPluginListSettingDataList(installed_plugin_json, available_plugin_json, available_basic_plugin_json);
			 //alert("플러그인 적용이
			 // 되었습니다.");

			 scwin.pluginArray = [];

		 } else if (data.message == "FAILED") {
			 WebSquare.layer.hideProcessMessage();

		 }
	};

	scwin.setBranchPluginListStatus = function ( data ) {

		if(data.message == "SEARCHING"){
			var msg = common.getLabel("lbl_project_setting_step04_list");
			WebSquare.layer.showProcessMessage(msg);

		}else if(data.gitStatus == "GITPULL" || data.gitStatus == "SVNUPDATE"){
			var msg = common.getLabel("lbl_synchronous");
			WebSquare.layer.showProcessMessage(msg);
		}else if(data.message == "ADDLOADING"){
			var msg = common.getLabel("lbl_plugin_add");
			WebSquare.layer.showProcessMessage(msg);

		}else if(data.message == "REMOVELOADING"){
			var msg = common.getLabel("lbl_plugin_remove");
			WebSquare.layer.showProcessMessage(msg);

		}else if(data.message == "SUCCESSFUL"){

			WebSquare.layer.hideProcessMessage();
			var message = common.getLabel("lbl_move_build");
드
			if(confirm(message)){

				var buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);

				var platform = buildproj_json.platform;
				var buildProjectName = buildproj_json.project_name;
				var build_project_id = buildproj_json.project_id;
				var workspace_id = buildproj_json.workspace_id;
                var product_type = buildproj_json.product_type;
				/// var target_server_id = this.getUserData("target_server_id");

				var buildAction = [];

				var data = {};
				data.platform = platform;
				data.projectName = buildProjectName;
				data.project_pkid = build_project_id;
				data.workspace_pkid = workspace_id;
                data.product_type = product_type;

				buildAction.push(data);

				$p.parent().$p.parent().__buildaction_data__.setJSON(buildAction);

				$p.parent().$p.parent().wfm_main.setSrc("/xml/build.xml");

			}else {

			}

			scwin.setpluginlisttag(data.resultPluginListObj);
			// plugin list data setting
			installed_plugin_json = $p.parent().dtl_plugin_setting_installed_step4.getAllJSON();
			available_plugin_json = $p.parent().dtl_plugin_setting_available_step4.getAllJSON();
			available_basic_plugin_json = $p.parent().dtl_plugin_setting_available_basic_step4.getAllJSON();

			scwin.setPluginListSettingDataList(installed_plugin_json,available_plugin_json, available_basic_plugin_json);
			scwin.pluginArray = [];




		}else if(data.message == "FAILED"){
			WebSquare.layer.hideProcessMessage();

		}

	};

	scwin.setBranchPluginAddStatus = function(data){
		// plugin add status ...


	};

	/* plugin list 조회해서 string 변환 및 데이터 조합. */
	scwin.setpluginlisttag = function (data){
		console.log("=== setpluginlisttag start ===");
		console.log(data);
		var installedPluginvar;
		var availablePluginvar;

		var platform  = localStorage.getItem("_platform_");
		if(platform == "Android"){
			installedPluginvar = data.installedPlugin;
			availablePluginvar = data.availablePlugin;

		}else if(platform == "iOS"){
            // resultPluginListObj
			installedPluginvar = data.resultAppConfigListObj.plugin.installedPlugin;
			availablePluginvar = data.resultAppConfigListObj.plugin.availablePlugin;
		}

		var pluginAddInstalld = [];
		var pluginAddAvailable = [];
        var pluginAddAvailableBasic = [];

		// list for 문 수행
		for(var idx in installedPluginvar){

			var temp = {};

				temp["installed_plugin_module"] = installedPluginvar[idx].module;
				temp["installed_plugin_version"] = installedPluginvar[idx].version;
				temp["installed_plugin_name"] = installedPluginvar[idx].name;
				temp["installed_plugin_type"] = installedPluginvar[idx].type;

				pluginAddInstalld.push(temp);

		}

		for(var idx in availablePluginvar){

			var temp = {};

			temp["available_plugin_module"] = availablePluginvar[idx].module;
			temp["available_plugin_version"] = availablePluginvar[idx].version;
			temp["available_plugin_name"] = availablePluginvar[idx].name;
			temp["available_plugin_type"] = availablePluginvar[idx].type;
			if(availablePluginvar[idx].type == "basic"){
				pluginAddAvailable.push(temp);
			}else if(availablePluginvar[idx].type == "premium"){
				pluginAddAvailableBasic.push(temp);
			}else {
				pluginAddAvailable.push(temp);
			}

		}

		$p.parent().dtl_plugin_setting_installed_step4.removeAll();
		$p.parent().dtl_plugin_setting_available_step4.removeAll();
        $p.parent().dtl_plugin_setting_available_basic_step4.removeAll();

		$p.parent().dtl_plugin_setting_installed_step4.setJSON(pluginAddInstalld,true);
		$p.parent().dtl_plugin_setting_available_step4.setJSON(pluginAddAvailable,true);
		$p.parent().dtl_plugin_setting_available_basic_step4.setJSON(pluginAddAvailableBasic,true);

	};

	// setPluginListSettingDataList function 생성
	scwin.setPluginListSettingDataList = function(installed_plugin_json, available_plugin_json, available_basic_plugin_json){

		var platform  = localStorage.getItem("_platform_");
		generator_installed_plugin_list.removeAll();
		generator_available_plugin_list.removeAll();

		for(var installed_idx in installed_plugin_json){

			if(installed_plugin_json[installed_idx].installed_plugin_module == "cordova-plugin-whitelist" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "cordova-plugin-camera" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "cordova-plugin-file" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "cordova-plugin-network-information" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "cordova-plugin-wkwebview-engine" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "whybrid-plugin-IAU" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "wmatrix-plugin-network" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "wmatrix-plugin-network-information" ||
					installed_plugin_json[installed_idx].installed_plugin_module == "wmatrix-plugin-camera"
			){


			} else {
				var removebtnInx = generator_installed_plugin_list.insertChild();

                // plugin_type 추가
				var li_plugin_type = generator_installed_plugin_list.getChild(removebtnInx,"plugin_type");

				var span_plugin_id = generator_installed_plugin_list.getChild(removebtnInx,"plugin_id");

				var txt_plugin_id = generator_installed_plugin_list.getChild(removebtnInx,"plugin_text_id");
				txt_plugin_id.setValue(installed_plugin_json[installed_idx].installed_plugin_name);
				var txt_plugin_version_id = generator_installed_plugin_list.getChild(removebtnInx,"plugin_version_id");

                var message = common.getLabel("lbl_by_inswave_ver");
				txt_plugin_version_id.setValue(message + " " + installed_plugin_json[installed_idx].installed_plugin_version);
				var btn_plugin_remove_btn_id = generator_installed_plugin_list.getChild(removebtnInx,"plugin_remove_btn_id");

				if(installed_plugin_json[installed_idx].installed_plugin_version_list == undefined){
					btn_plugin_remove_btn_id.setUserData("setModuleVersion",installed_plugin_json[installed_idx].installed_plugin_version);
				}else {

					btn_plugin_remove_btn_id.setUserData("setModuleVersion",installed_plugin_json[installed_idx].installed_plugin_version);
				}

				// plugin image 동적으로 변경하기
				switch (installed_plugin_json[installed_idx].installed_plugin_name.toLowerCase()){
					case "app" :
						span_plugin_id.changeClass("ico05","ico01");
						break;
					case "misc" :
						span_plugin_id.changeClass("ico05","ico016");
						break;
					case "device" :
						span_plugin_id.changeClass("ico05","ico08");
						break;
					case "websquare" :
						span_plugin_id.changeClass("ico05","ico08");
						break;
					case "view" :
						span_plugin_id.changeClass("ico05","ico08");
						break;
					case "contents" :
						span_plugin_id.changeClass("ico05","ico07");
						break;
					case "license" :
						span_plugin_id.changeClass("ico05","ico09");
						break;
					case "file" :
						span_plugin_id.changeClass("ico05","ico09");
						break;
					case "paint" :
						span_plugin_id.changeClass("ico05","ico09");
						break;
					case "websocket" :
						span_plugin_id.changeClass("ico05","ico09");
						break;
					case "audiorecorder" :
						span_plugin_id.changeClass("ico05","ico09");
						break;
					case "biometric" :
						span_plugin_id.changeClass("ico05","ico09");
						break;
					default :
						span_plugin_id.changeClass("ico05","ico09");
						break;
				}


				// type change css class pt_li_basic || pt_li_premium
				if(installed_plugin_json[installed_idx].installed_plugin_type == "basic"){
					li_plugin_type.changeClass("pt_li_basic","pt_li_basic");
				}else if(installed_plugin_json[installed_idx].installed_plugin_type == "premium"){
					li_plugin_type.changeClass("pt_li_basic","pt_li_premium");
				}else {
					li_plugin_type.changeClass("pt_li_basic","pt_li_basic");
				}

				// plugin_remove_btn_id

				btn_plugin_remove_btn_id.setUserData("platform",platform);
				btn_plugin_remove_btn_id.setUserData("setModule",installed_plugin_json[installed_idx].installed_plugin_module);



				btn_plugin_remove_btn_id.setUserData("setModuleName",installed_plugin_json[installed_idx].installed_plugin_name);
				btn_plugin_remove_btn_id.setUserData("setModuleVersionList",installed_plugin_json[installed_idx].installed_plugin_version_list);
                btn_plugin_remove_btn_id.setUserData("setModuleType",installed_plugin_json[installed_idx].installed_plugin_type);
			}

		}


		for(var available_idx in available_plugin_json){
			var addbtnInx = generator_available_plugin_list.insertChild();

            // avaliable_plugin_type
			var li_available_plugin_type = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_type");

			// available_plugin_id
			var span_available_plugin_id = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_id");

			var txt_available_plugin_id = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_text_id");
			txt_available_plugin_id.setValue(available_plugin_json[available_idx].available_plugin_name);

			var txt_available_plugin_version_id = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_version_id");
			// var btn_plugin_add_btn_id = generator_available_plugin_list.getChild(addbtnInx,"plugin_add_btn_id");
			var pluginVersionList = "";

			if(available_plugin_json[available_idx].available_plugin_version_list == undefined){
				pluginVersionList = available_plugin_json[available_idx].available_plugin_version;
                var message = common.getLabel("lbl_not_select");
				txt_available_plugin_version_id.addItem("", message); //선택안함
				for(var i = 0 ; i <pluginVersionList.length; i++){
					// txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i],false);
					// txt_available_plugin_version_id.addItem(1, pluginVersionList[i]);
					txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i]);

				}


			}else {
				pluginVersionList = available_plugin_json[available_idx].available_plugin_version_list;
				// txt_available_plugin_version_id.removeAll(false);
				var message = common.getLabel("lbl_not_select");
				txt_available_plugin_version_id.addItem("", message); //선택안함
				for(var i = 0 ; i <pluginVersionList.length; i++){
					// txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i],false);
					txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i]);

				}

			}

			// plugin image 동적으로 변경하기
			switch (available_plugin_json[available_idx].available_plugin_name.toLowerCase()){
				case "app" :
					span_available_plugin_id.changeClass("ico05","ico01");
					break;
				case "misc" :
					span_available_plugin_id.changeClass("ico05","ico016");
					break;
				case "device" :
					span_available_plugin_id.changeClass("ico05","ico08");
					break;
				case "websquare" :
					span_available_plugin_id.changeClass("ico05","ico08");
					break;
				case "view" :
					span_available_plugin_id.changeClass("ico05","ico08");
					break;
				case "contents" :
					span_available_plugin_id.changeClass("ico05","ico07");
					break;
				case "license" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "file" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "paint" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "websocket" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "audiorecorder" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "biometric" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				default :
					span_available_plugin_id.changeClass("ico05","ico09");
				    break;
			}

			// type change css class pt_li_basic || pt_li_premium
			if(available_plugin_json[available_idx].available_plugin_type == "basic"){
				// pt_li_basic
				li_available_plugin_type.changeClass("pt_li_basic","pt_li_basic");
			}else if(available_plugin_json[available_idx].available_plugin_type == "premium"){
				// pt_li_premium
				li_available_plugin_type.changeClass("pt_li_basic","pt_li_premium");
			}else {
				li_available_plugin_type.changeClass("pt_li_basic","pt_li_basic");
			}

			// plugin_add_btn_id
			var btn_plugin_add_btn_id = generator_available_plugin_list.getChild(addbtnInx,"plugin_add_btn_id");
			btn_plugin_add_btn_id.setUserData("platform",platform);
			btn_plugin_add_btn_id.setUserData("setModule",available_plugin_json[available_idx].available_plugin_module);

			var component_available_plugin_version_id = $p.getComponentById(txt_available_plugin_version_id.id);

			txt_available_plugin_version_id.bind("onchange",function(e){
				btn_plugin_add_btn_id.setUserData("setModuleVersion",component_available_plugin_version_id);

			});

			btn_plugin_add_btn_id.setUserData("setModuleVersion",component_available_plugin_version_id);
			btn_plugin_add_btn_id.setUserData("setModuleVersionList",pluginVersionList);
			btn_plugin_add_btn_id.setUserData("setModuleName", available_plugin_json[available_idx].available_plugin_name);
            btn_plugin_add_btn_id.setUserData("setModuleType", available_plugin_json[available_idx].available_plugin_type);

		}


		for(var available_basic_idx in available_basic_plugin_json){
			var addbtnInx = generator_available_plugin_list.insertChild();

			// avaliable_plugin_type
			var li_available_plugin_type = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_type");

			// available_plugin_id
			var span_available_plugin_id = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_id");

			var txt_available_plugin_id = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_text_id");
			txt_available_plugin_id.setValue(available_basic_plugin_json[available_basic_idx].available_plugin_name);

			var txt_available_plugin_version_id = generator_available_plugin_list.getChild(addbtnInx,"available_plugin_version_id");
			// var btn_plugin_add_btn_id = generator_available_plugin_list.getChild(addbtnInx,"plugin_add_btn_id");
			var pluginVersionList = "";

			if(available_basic_plugin_json[available_basic_idx].available_plugin_version_list == undefined){
				pluginVersionList = available_basic_plugin_json[available_basic_idx].available_plugin_version;
                var message = common.getLabel("lbl_not_select");
				txt_available_plugin_version_id.addItem("", message); //선택안함
				for(var i = 0 ; i <pluginVersionList.length; i++){
					// txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i],false);
					// txt_available_plugin_version_id.addItem(1, pluginVersionList[i]);
					txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i]);

				}


			}else {
				pluginVersionList = available_basic_plugin_json[available_basic_idx].available_plugin_version_list;
				// txt_available_plugin_version_id.removeAll(false);
				var message = common.getLabel("lbl_not_select");
				txt_available_plugin_version_id.addItem("", message); //선택안함
				for(var i = 0 ; i <pluginVersionList.length; i++){
					// txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i],false);
					txt_available_plugin_version_id.addItem(i+1, pluginVersionList[i]);

				}

			}

			// plugin image 동적으로 변경하기
			switch (available_basic_plugin_json[available_basic_idx].available_plugin_name.toLowerCase()){
				case "app" :
					span_available_plugin_id.changeClass("ico05","ico01");
					break;
				case "misc" :
					span_available_plugin_id.changeClass("ico05","ico016");
					break;
				case "device" :
					span_available_plugin_id.changeClass("ico05","ico08");
					break;
				case "websquare" :
					span_available_plugin_id.changeClass("ico05","ico08");
					break;
				case "view" :
					span_available_plugin_id.changeClass("ico05","ico08");
					break;
				case "contents" :
					span_available_plugin_id.changeClass("ico05","ico07");
					break;
				case "license" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "file" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "paint" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "websocket" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "audiorecorder" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				case "biometric" :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
				default :
					span_available_plugin_id.changeClass("ico05","ico09");
					break;
			}

			// type change css class pt_li_basic || pt_li_premium
			if(available_basic_plugin_json[available_basic_idx].available_plugin_type == "basic"){
				// pt_li_basic
				li_available_plugin_type.changeClass("pt_li_basic","pt_li_basic");
			}else if(available_basic_plugin_json[available_basic_idx].available_plugin_type == "premium"){
				// pt_li_premium
				li_available_plugin_type.changeClass("pt_li_basic","pt_li_premium");
			}else {
				li_available_plugin_type.changeClass("pt_li_basic","pt_li_basic");
			}
			// plugin_add_btn_id
			var btn_plugin_add_btn_id = generator_available_plugin_list.getChild(addbtnInx,"plugin_add_btn_id");
			btn_plugin_add_btn_id.setUserData("platform",platform);
			btn_plugin_add_btn_id.setUserData("setModule",available_basic_plugin_json[available_basic_idx].available_plugin_module);

			var component_available_plugin_version_id = $p.getComponentById(txt_available_plugin_version_id.id);

			txt_available_plugin_version_id.bind("onchange",function(e){
				btn_plugin_add_btn_id.setUserData("setModuleVersion",component_available_plugin_version_id);

			});

			btn_plugin_add_btn_id.setUserData("setModuleVersion",component_available_plugin_version_id);
			btn_plugin_add_btn_id.setUserData("setModuleVersionList",pluginVersionList);
			btn_plugin_add_btn_id.setUserData("setModuleName", available_basic_plugin_json[available_basic_idx].available_plugin_name);
			btn_plugin_add_btn_id.setUserData("setModuleType", available_basic_plugin_json[available_basic_idx].available_plugin_type);

		}

	};

	scwin.start_plugin_list_onclick = function(){


		var build_project_json = $p.parent().dtl_build_setting_step1.getAllJSON();

	    // plugin add remove 적용 하는 구간 추가

		var local_buildproject_id = build_project_json[0].project_id;
        var product_type = build_project_json[0].product_type;
		var platform = build_project_json[0].platform;
		var workspace_name = localStorage.getItem("__workspace_name__");
		var project_name = build_project_json[0].project_name;
		var project_dir_name = build_project_json[0].project_dir_path;

		if(platform == "Android"){

			// plugin add
			var data = {};

			data.build_id = local_buildproject_id; // project id
			data.product_type = product_type;
			data.platform = platform;
			data.moduleList = scwin.pluginArray; // data.moduleList 로 변경
			data.workspace_name = workspace_name;
			data.project_dir_name = project_dir_name;
			data.project_name = project_name;

			// vcs_id 가 없을 경우 vcs 처리 없이 동작하는 plugins update 실행
			// 2021/10/18 scwin.setTemplateProjectPluginUpdate(data); 하나의 function에 다 처리 하는걸로
			if(build_project_json.vcs_id == null || build_project_json.vcs_id == ""){
				scwin.setTemplateProjectPluginUpdate(data);
			}else {
				scwin.setTemplateProjectPluginUpdate(data);
			}

		} else {

			// plugin add
			var data = {};

			data.build_id = local_buildproject_id;
			data.product_type = product_type;
			data.platform = platform;
			data.moduleList = scwin.pluginArray; // data.moduleList 로 변경
			data.workspace_name = workspace_name;
			data.project_dir_name = project_dir_name;
			data.project_name = project_name;

			// vcs_id 가 없을 경우 vcs 처리 없이 동작하는 plugins update 실행
			if(build_project_json.vcs_id == null || build_project_json.vcs_id == ""){
				scwin.setTemplateProjectPluginUpdate(data);
			}else {
				scwin.setTemplateProjectPluginUpdate(data);
			}

		}

	};

	scwin.add_plugin_onclick = function(e){
		var getPlatform = this.getUserData("platform");
		var getModule = this.getUserData("setModule");
		var getModuleVersion = this.getUserData("setModuleVersion");
		var getModuleName = this.getUserData("setModuleName");
		var setModuleVersionList = this.getUserData("setModuleVersionList");
        var getModuleType = this.getUserData("setModuleType");

		var addPluginTemp = {};
		var installedPlugin = {};

		if(getModuleVersion.getText() == null){
			var message = common.getLabel("lbl_project_setting_step04_pluginVersion");
		    alert(message);
		    return false;

		}

		addPluginTemp.module = getModule;
		addPluginTemp.moduleName = getModuleName;
		addPluginTemp.moduleVersion = getModuleVersion.getText();

		// plugin_add_btn_id, 여기는 단건으로 처리하기 위한 방법
		if(getModuleType == "basic"){

			$.each(available_plugin_json, function (){
				if(this["available_plugin_module"] == getModule){
					addPluginTemp = this;
					addPluginTemp.pluginMode = "ADD";
					addPluginTemp.available_plugin_version = getModuleVersion.getText();
					// data format 추가 정의 및 기존 정의 붙이기..
					var findPluginItem = scwin.pluginArray.findIndex(function(pluginItem){
						return pluginItem.installed_plugin_module == getModule;
					});

					if(findPluginItem === -1){
						scwin.pluginArray.push(addPluginTemp);
					}else {
						scwin.pluginArray.splice(findPluginItem,1);
					}
				}
			});

			// 설치 가능한 플러그인 선택한 모듈 채번
			var findItem = available_plugin_json.findIndex(function(item){
				return item.available_plugin_module == getModule;
			});

			// 선택한 플러그인 모듈 삭제
			available_plugin_json.splice(findItem,1);

		}else if(getModuleType == "premium"){

			$.each(available_basic_plugin_json, function (){
				if(this["available_plugin_module"] == getModule){
					addPluginTemp = this;
					addPluginTemp.pluginMode = "ADD";
					addPluginTemp.available_plugin_version = getModuleVersion.getText();
					// data format 추가 정의 및 기존 정의 붙이기..
					var findPluginItem = scwin.pluginArray.findIndex(function(pluginItem){
						return pluginItem.installed_plugin_module == getModule;
					});

					if(findPluginItem === -1){
						scwin.pluginArray.push(addPluginTemp);
					}else {
						scwin.pluginArray.splice(findPluginItem,1);
					}
				}
			});

			// 설치 가능한 플러그인 선택한 모듈 채번
			var findItem = available_basic_plugin_json.findIndex(function(item){
				return item.available_plugin_module == getModule;
			});

			// 선택한 플러그인 모듈 삭제
			available_basic_plugin_json.splice(findItem,1);

		}else {

		}


		installedPlugin.installed_plugin_module = getModule;
		installedPlugin.installed_plugin_version = getModuleVersion.getText();
		installedPlugin.installed_plugin_name = getModuleName;
        installedPlugin.installed_plugin_type = getModuleType;
		installedPlugin.installed_plugin_version_list = setModuleVersionList;
		// installedPlugin.installed_plugin_version_list = getModuleName;

		// 다시 조회 할때는 선택한 add plugin 은 remove plugin push 한다.
		// 설치 가능한 플러그인으로 이동
		installed_plugin_json.push(installedPlugin);

		scwin.setPluginListSettingDataList(installed_plugin_json, available_plugin_json, available_basic_plugin_json);

	};

	scwin.remove_plugin_onclick = function(e){
		var getPlatform = this.getUserData("platform");
		var getModule = this.getUserData("setModule");
		var getModuleVersion = this.getUserData("setModuleVersion");
		var getModuleName = this.getUserData("setModuleName");
		var getModuleVersionList = this.getUserData("setModuleVersionList");
		var getModuleType = this.getUserData("setModuleType");

		var removePlugin = {};
		var availablePlugin = {};



		$.each(installed_plugin_json, function (){
			if(this["installed_plugin_module"] == getModule){
				removePlugin = this;
				removePlugin.pluginMode = "REMOVE";
				removePlugin.available_plugin_version = getModuleVersion;

				var findPluginItem = scwin.pluginArray.findIndex(function(pluginItem){
					return pluginItem.available_plugin_module == getModule;
				});

				if(findPluginItem === -1){
					scwin.pluginArray.push(removePlugin);
				}else {
					scwin.pluginArray.splice(findPluginItem,1);
				}


			}
		});

		var findItem = installed_plugin_json.findIndex(function(item){
			return item.installed_plugin_module == getModule;
		});

		availablePlugin.available_plugin_module = getModule;
		availablePlugin.available_plugin_version = getModuleVersion; // 여기가 문제... getModuleVersion.getText()
		availablePlugin.available_plugin_name = getModuleName;
		availablePlugin.available_plugin_version_list = getModuleVersionList;
        availablePlugin.available_plugin_type = getModuleType;

		installed_plugin_json.splice(findItem,1);
		// 다시 조회 할때는 선택한 add plugin 은 remove plugin push 한다.
		if(getModuleType == "basic"){
			available_plugin_json.push(availablePlugin);
		}else if(getModuleType == "premium"){
            available_basic_plugin_json.push(availablePlugin);
		}else{

		}

		scwin.setPluginListSettingDataList(installed_plugin_json, available_plugin_json, available_basic_plugin_json);

	};

	scwin.btn_prev_onclick = function(e) {
		$p.parent().scwin.selected_step(2);
	};

	scwin.btn_next_onclick = function(e) {
		$p.parent().scwin.selected_step(5);
	};
	
	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'dfbox'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'gal_tit fl',label:'',useLocale:'true',localeRef:'lbl_plugin_setting'}},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'txt_chk',useLocale:'true',localeRef:'lbl_confirm_plugin_setting'}},{T:1,N:'xf:trigger',A:{type:'button',id:'',style:'',class:'btn_cm type1','ev:onclick':'scwin.start_plugin_list_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_apply',tooltipLocaleRef:'lbl_plugin_apply'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'photo_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'pt_tit',useLocale:'true',localeRef:'lbl_applied_plugin'}},{T:1,N:'xf:group',A:{class:'pt_area',id:'',style:'',tagname:'ul'},E:[{T:1,N:'w2:generator',A:{tagname:'ul',style:'',id:'generator_installed_plugin_list',class:''},E:[{T:1,N:'xf:group',A:{class:'pt_li_basic',id:'plugin_type',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'pt_top',id:'',style:''},E:[{T:1,N:'w2:span',A:{label:'',id:'plugin_id',style:'',class:'ico05',useLocale:'true',localeRef:'lbl_camera'}},{T:1,N:'w2:textbox',A:{label:'',id:'plugin_text_id',style:'',class:'txt',useLocale:'true',localeRef:'lbl_camera'}}]},{T:1,N:'xf:group',A:{class:'pt_bot',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl',id:'',style:''},E:[{T:1,N:'w2:span',A:{label:'',id:'plugin_version_id',style:'',escape:'false',useLocale:'true',localeRef:'lbl_by_inswave_ver'}}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'w2:anchor',A:{id:'plugin_remove_btn_id',style:'',outerDiv:'false',class:'btn_ic_minus','ev:onclick':'scwin.remove_plugin_onclick',useLocale:'true',localeRef:'lbl_insert_text'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'photo_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'dfbox'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'pt_tit fl',label:'',useLocale:'true',localeRef:'lbl_applicable_plugin'}}]},{T:1,N:'xf:group',A:{class:'pt_area',id:'',style:'',tagname:'ul'},E:[{T:1,N:'w2:generator',A:{tagname:'ul',style:'',id:'generator_available_plugin_list',class:''},E:[{T:1,N:'xf:group',A:{class:'pt_li_basic',id:'available_plugin_type',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'pt_top',id:'',style:''},E:[{T:1,N:'w2:span',A:{class:'ico05',id:'available_plugin_id',label:'',style:'',useLocale:'true',localeRef:'lbl_geolocation'}},{T:1,N:'w2:textbox',A:{class:'txt',id:'available_plugin_text_id',label:'',style:'',useLocale:'true',localeRef:'lbl_geolocation'}}]},{T:1,N:'xf:group',A:{class:'pt_bot',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl',id:'',style:''},E:[{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_version'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'available_plugin_version_id',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':''},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'w2:anchor',A:{class:'btn_ic_plus',id:'plugin_add_btn_id',outerDiv:'false',style:'','ev:onclick':'scwin.add_plugin_onclick',useLocale:'true',localeRef:'lbl_insert_text'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'title',useLocale:'true',localeRef:'lbl_plugin_setting'}},{T:1,N:'w2:anchor',A:{id:'',style:'',outerDiv:'false',class:'btn_pop_close',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'form_wrap type2',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:span',A:{label:'',id:'',style:'',class:'t_head',useLocale:'true',localeRef:'lbl_solution'}},{T:1,N:'w2:span',A:{id:'',label:'',style:'',class:'t_data',useLocale:'true',localeRef:'lbl_mtranskey'}}]},{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:span',A:{class:'t_head',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step04_description'}},{T:1,N:'w2:span',A:{class:'t_data',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_secure_keyboard_plugin'}}]},{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:span',A:{class:'t_head',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_version'}},{T:1,N:'w2:span',A:{class:'t_data',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step04_versionNum'}}]}]}]},{T:1,N:'xf:group',A:{class:'form_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_module'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{type:'button',id:'',style:'',class:'btn_cm',useLocale:'true',localeRef:'lbl_upload'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_license'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'',style:''}},{T:1,N:'xf:trigger',A:{class:'btn_cm',id:'',style:'',type:'button',useLocale:'true',localeRef:'lbl_upload'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox tac',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{type:'button',id:'',style:'',class:'btn_cm type1',useLocale:'true',localeRef:'lbl_confirm'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]})