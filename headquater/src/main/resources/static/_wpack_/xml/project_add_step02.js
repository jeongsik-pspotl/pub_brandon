/*amd /xml/project_add_step02.xml 44180 72c55ca6fc6a3e8b6b2e6e870ac23af61e4739ecfd99f737a5a7fab5d787fa99 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			var i = 1;
			scwin.createProjectToFlag_yn = false;
			scwin.platformType = "";
			scwin.app_icon_upload_check_yn = false;

			scwin.onpageload = function() {
				$p.parent().scwin.selected_step(2);
				common.setScopeObj(scwin);
				// project_add_step2_tap.addTab('project02', tabOpt, contOpt); // >> + 버튼으로 동적 할당 하는 기능 추가해야함...

				scwin.init();
			};

			scwin.onpageunload = function() {

			};

			scwin.init = function(){

                var profilesList = [];
                var profileDeubugList = [];
				var platformType = $p.parent().scwin.txt_project_all_step_platform;
                scwin.platformType = $p.parent().scwin.txt_project_all_step_platform;
				var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);

                if(platformType == "iOS"){

					var datareq = {};

					// datareq.platform = platform;
                    datareq.key_id = buildproj_json.key_id;

					var options = {};

					options.action = "/manager/mCert/common/selectiOSProfilesKeyName";
					options.mode = "asynchronous";
					options.mediatype = "application/json";
					options.method = "POST";
					options.requestData = JSON.stringify(datareq);

					options.success = function (e) {
						var data = e.responseJSON;
						if (data != null) {

                            for(var row in data){

                                if(data[row].profiles_build_type == "development"){
									profileDeubugList.push(data[row].profiles_key_name);
								}else {
									profilesList.push(data[row].profiles_key_name);
								}

							}

							var rowJSON = {
								"tap_num" : 0,
								"profileList": profilesList,
								"profileDebugList": profileDeubugList,
								"buildType": "Debug"
							};

							var tabOpt = {
								label : "Debug",
								closable : true,
							};
							var contOpt = {
								frameMode : "wframe",
								src : "/xml/project_add_app_config.xml",
								title : "Debug",
								alwaysDraw : "true",
								scope: "true",
								dataObject: {
									"type" : "json",
									"name" : "tabParam",
									"data" : rowJSON
								}
							};

							project_add_step2_tap.addTab('project00', tabOpt, contOpt);

							var rowJSON = {
								"tap_num" : 1,
								"profileList": profilesList,
								"profileDebugList": profileDeubugList,
								"buildType": "Release"
							};

							var tabOpt = {
								label : "Release",
								closable : true,
							};
							var contOpt = {
								frameMode : "wframe",
								src : "/xml/project_add_app_config.xml",
								title : "Release",
								alwaysDraw : "true",
								scope: "true",
								dataObject: {
									"type" : "json",
									"name" : "tabParam",
									"data" : rowJSON
								}
							};

							project_add_step2_tap.addTab('project01', tabOpt, contOpt);

                            i = 2;

						}
					};

					options.error = function (e) {

						alert("message:" + e.responseJSON + "\n");

					};

					$p.ajax(options);



				}else if(platformType == "Android"){

					var rowJSON = {
						"tap_num" : 0,
						"profileList": profilesList,
						"buildType": "Debug"
					};

					var tabOpt = {
						label : "debug",
						closable : true,
					};
					var contOpt = {
						frameMode : "wframe",
						src : "/xml/project_add_app_config.xml",
						title : "debug",
						alwaysDraw : "false",
						scope: "true",
						dataObject: {
							"type" : "json",
							"name" : "tabParam",
							"data" : rowJSON
						}
					};

					project_add_step2_tap.addTab('project00', tabOpt, contOpt);

				}


				btn_next.hide();

			};

			scwin.webSocketCallback = function(obj) {
				// msg type 추가
				console.log(" add step 2 page : "+obj.MsgType);
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
					case "HV_MSG_PROJECT_TEMPLATE_STATUS_INFO_FROM_HEADQUATER":
						scwin.setBranchProjectCreateSyncStatus(obj);
						break;
					case "HV_MSG_DEPLOY_SETTING_STATUS_INFO_FROM_HEADQUATER" :
						scwin.setBuilderDeploySettingStatus(obj);
						break;
					case "BIN_FILE_APP_ICON_APPEND_SEND_INFO_FROM_HEADQUATER" :
						scwin.setBuilderAppIconUploadStatus(obj);
                        break;
					default :
						break;
				}
			};

			scwin.setBuilderAppIconUploadStatus = function(obj){

                switch (obj.status) {
					case "APPICONUPLOAD":
                        var message = common.getLabel("lbl_app_icon_uploading");
						WebSquare.layer.showProcessMessage(message);
						break;
					case "APPICONUPLOADDONE":
						// start project create
						WebSquare.layer.hideProcessMessage();
						scwin.saveStep2Data();
						break;
					default :
						break;
				}

			};

			scwin.setBranchProjectCreateSyncStatus = function (obj) {
				// git sync return
				switch (obj.gitStatus) {
					case "GITCLONE":
                        var message = common.getLabel("lbl_git_clone");
						WebSquare.layer.showProcessMessage(message); //Git Clone
						break;
					case "SVNCHECKOUT":
                        var message = common.getLabel("lbl_svn_checkout");
						WebSquare.layer.showProcessMessage(message); //Svn Checkout
						break;
					case "SVNCREATE":
                        var message = common.getLabel("lbl_svn_create");
						WebSquare.layer.showProcessMessage(message);
                        break;
					case "APPICONUNZIP":
                        var message = common.getLabel("lbl_app_icon_uploading");
						WebSquare.layer.showProcessMessage(message);
						break;
					case "FILEUPLOAD":
                        var message = common.getLabel("lbl_signingkey_uploading");
						WebSquare.layer.showProcessMessage(message); //SigningKey Uploading
						// scwin.sendBranchSigningKeyAPI(obj);
						break;
					case "CONFIG":
                        var message = common.getLabel("lbl_app_config_setting");
						WebSquare.layer.showProcessMessage(message);
						break;
					case "GITCOMMIT":
                        var message = common.getLabel("lbl_git_commit");
						WebSquare.layer.showProcessMessage(message); //Git Commit
						break;
					case "GITADD":
                        var message = common.getLabel("lbl_git_add");
						WebSquare.layer.showProcessMessage(message); //Git Add
						break;
					case "GITPUSH":
                        var message = common.getLabel("lbl_git_push");
						WebSquare.layer.showProcessMessage(message); // Git Push
						break;
					case "SVNADD":
						var message = common.getLabel("lbl_svn_add");
						WebSquare.layer.showProcessMessage(message); //Svn Add
						break;
					case "SVNCOMMIT":
                        var message = common.getLabel("lbl_svn_commit");
                        WebSquare.layer.showProcessMessage(message);
                        break;
					case "MKDIR":
						var message = common.getLabel("lbl_project_dir_create");
						WebSquare.layer.showProcessMessage(message); //Project Directory Create
						break;
					case "GITBARE":
						var message = common.getLabel("lbl_git_bare_init");
						WebSquare.layer.showProcessMessage(message); //Git Bare init
						break;
					case "PROJCOPY":
						var message = common.getLabel("lbl_project_zip_copy");
						WebSquare.layer.showProcessMessage(message); //Project Zip File Copy
						break;
					case "APPICONUPLOAD":
						var message = common.getLabel("lbl_app_icon_uploading");
						WebSquare.layer.showProcessMessage(message);
                        break;
					case "APPICONUPLOADDONE":
                        // start project create
						WebSquare.layer.hideProcessMessage();
						scwin.saveStep2Data();
						break;
					case "DONE":
						WebSquare.layer.hideProcessMessage();
						// alert("프로젝트 생성 완료");
						if (obj.projectDirPath == null) {

						} else {
							$p.parent().dtl_build_project_step1.setCellData(0, "project_dir_path", obj.projectDirPath);
						}

                        var message = common.getLabel("lbl_project_add_step02_js_confirm");
						if(confirm(message)){ // 프로젝트 생성 완료 되었습니다. \n빌드 화면으로 이동 하시겠습니까?

							var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);

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
							// flag yn 처리
							scwin.btn_project_step4_checkYn = true;
							scwin.createProjectToFlag_yn = true;
							// btn_create_project_trigger.setDisabled(true);
							btn_next.show();

						}


						// flag yn 처리
						// scwin.btn_project_step4_checkYn = true;
						// scwin.createProjectToFlag_yn = true;
						// // btn_create_project_trigger.setDisabled(true);
						// btn_next.show();

						break;
					default :
						break;
				}

			};

			scwin.setBuilderDeploySettingStatus = function (obj) {

				switch (obj.status) {
					case "FASTLANEINIT":
                        var message = common.getLabel("lbl_project_add_step02_js_fastlane_init");
						WebSquare.layer.showProcessMessage(message); //Deploy init Create
						break;
					case "FASTENV":
						var message = common.getLabel("lbl_project_add_step02_js_fastlane_env");
						WebSquare.layer.showProcessMessage(message); //Deploy Env setting
						break;
					case "FASTFILE":
						var message = common.getLabel("lbl_project_add_step02_js_fastlane_file");
						WebSquare.layer.showProcessMessage(message); //Deploy FastFile Create
						break;
					case "APPFILE":
						var message = common.getLabel("lbl_project_add_step02_js_fastlane_app_file");
						WebSquare.layer.showProcessMessage(message); //Deploy AppFile Create
						break;
					case "DONE":
						WebSquare.layer.hideProcessMessage();

						break;
					default :
						break;
				}
			};

			scwin.btn_prev_onclick = function(e) {
				$p.parent().scwin.selected_step(1);
			};

			scwin.btn_complete_onclick = function(e) {
                if(scwin.checkData()){
					var message = common.getLabel("lbl_confirm_create_project");
					if (confirm(message)) {

                        // app icon upload file yn 확인
						// app icon 있을 경우 png file uplaod 실행
						if (scwin.checkAppIconFileYn()) {
							scwin.checkAppIconFileAndUpload();
						} else {
                            // 없을 경우 project create 실행
							scwin.saveStep2Data();
						}
                    }
				}
			};

            scwin.checkData = function(){

				var packageName = "";
                var platformType = scwin.platformType;

                // 서버정보 입력 부분을 없애면서, 주석처리
				// var server_name_1 = input_appname_1.getValue();
				// var app_id1 = input_appid_1.getValue();
				// var server_URL1 = input_serverurl_1.getValue();

				for(var tap_cnt = 0 ; tap_cnt < project_add_step2_tap.getTabCount();tap_cnt++){
					var tableFrame = project_add_step2_tap.getFrame(tap_cnt);
					var append_tap_app_config_data = {};

					if(tableFrame == undefined){

					} else {
						var profile_name = "";
						var app_id = "";
						var app_name = "";
						var app_version = "";
						var app_version_code = "";
						var appIDCheck = "";
						var packageName = "";

						// android
						if (platformType == "Android") {
							profile_name = tableFrame.getObj("step2_android_input_profile_name").getValue();
							app_id = tableFrame.getObj("step2_android_input_app_id").getValue();
							app_name = tableFrame.getObj("step2_android_input_app_name").getValue();
							app_version = tableFrame.getObj("step2_android_input_app_version").getValue();
							app_version_code = tableFrame.getObj("step2_android_input_app_version_code").getValue();
							packageName = tableFrame.getObj("step2_android_input_packagename").getValue();
							append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_minsdk_version").getValue();
						} else if (platformType == "iOS") {
							app_id = tableFrame.getObj("step2_ios_input_app_id").getValue();
							app_name = tableFrame.getObj("step2_ios_input_app_name").getValue();
							app_version = tableFrame.getObj("step2_ios_input_app_version").getValue();
							app_version_code = tableFrame.getObj("step2_ios_input_app_version_code").getValue();
							packageName = tableFrame.getObj("step2_ios_input_projectname").getValue();
							append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_target_version").getValue();
						}

						var check_app_version_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION", app_version);
						var check_app_version_code_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION_CODE", app_version_code);

                        if (common.isEmptyStr(profile_name) && (platformType == 'Android')) {
							var message = common.getLabel("lbl_project_add_step02_js_input_profile_name");
                            alert(message);
                            scwin.app_id_license_check_yn = false;
                            return false;
						}

						if (common.isEmptyStr(app_id)) {
                            var message = common.getLabel("lbl_input_appid");
							alert(message);
							scwin.app_id_license_check_yn = false;
							return false;
						}

						// 앱 라이센스 체크를 하세요.
						// if (!scwin.app_id_license_check_yn) {
						// 	var message = common.getLabel("lbl_ask_appid_check");
						// 	alert(message); //APPID 체크를 하세요
						// 	return false;
						// }

						appIDCheck = app_id.split("\.");

						// app id 체크
						if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
							for (var i = 0; i < appIDCheck.length; i++) {
								var strAppID = appIDCheck[i];

								var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2",strAppID);

								if(!check_app_id_str) {
                                    var message = common.getLabel("lbl_appid_check_form");
									alert(app_id + " : " + message);
									return false;
								}
							}
						} else {
							var message = common.getLabel("lbl_appid_check_form");
							alert(app_id + " : " + message);
							return false;
						}

						scwin.app_id_temp_save = app_id;

						if (common.isEmptyStr(app_name)) {
                            var message = common.getLabel("lbl_check_app_name");
							alert(message);
							return false;
						}

						// app name 자릿수 제한
						if (common.getCheckInputLength(app_name, app_name.length, 200)) {
							return false;
						}

						if (common.isEmptyStr(app_version)) {
							var message = common.getLabel("lbl_check_app_version");
							alert(message);
							return false;
						}

						if (!check_app_version_str) {
							var message = common.getLabel("lbl_app_version_form");
							alert(message);
							return false;
						}

						if (common.isEmptyStr(app_version_code)) {
							var message = common.getLabel("lbl_check_app_version_code");
							alert(message);
							return false;
						}

                        if (common.isEmptyStr(packageName) && (platformType == 'Android')) {
                            var message = common.getLabel("lbl_check_package_name");
                            alert(message);
                            return false;
						}

                        if (common.isEmptyStr(packageName) && (platformType == 'iOS')) {
                            var message = common.getLabel("");
                            alert(message);
                            return false;
						}

						if ((!check_app_version_code_str) && (platformType != 'Windows')) {
							var message = common.getLabel("lbl_app_version_code_form");
							alert(message);
							return false;
						}

						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR",app_version_code)) {
							var message = common.getLabel("lbl_app_version_code_kor");
                            alert(message);
							return false;
						}

						if (common.checkAllInputText("CHECK_INPUT_TYPE_ENG",app_version_code)) {
							var message = common.getLabel("lbl_app_version_code_eng");
							alert(message);
							return false;
						}

						// ios
						var appIDCheck1 = "";
						var appIDCheckAll = "";

						var serverConfigArray = [];
						var server1Json = {};

						// package name 한글 입력
						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "Android") {
							var message = common.getLabel("lbl_package_name_kor_rule");
							alert(packageName + " : " + message);
							return false;
						}

						// project name 한글 입력
						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", packageName) && scwin.platformType == "iOS") {
							var message = common.getLabel("lbl_proejct_name_kor_rule");
							alert(packageName + " : " + message);
							return false;
						}

                        /*
						// server name 한글 입력
						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", server_name_1)) {
                            var message = common.getLabel("lbl_server_name_kor_rule");
							alert(server_name_1 + " : " + message);
							return false;
						}

						// server url 빈칸
						if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", server_URL1)) {
                            var message = common.getLabel("lbl_project_add_step02_js_server_url_kor");
							alert(server_URL1 + " : " + message); //server URL 형식에 한글은 입력할 수 없습니다
							return false;
						}
						*/
						if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "Android") {
                            var message = common.getLabel("lbl_package_name_special_char_rule");
							alert(packageName + " : " + message);
							return false;
						}

						if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", packageName) && scwin.platformType == "iOS") {
                            var message = common.getLabel("lbl_project_name_special_char_rule");
							alert(packageName + " : " + message);
							return false;
						}

                        /*
						if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", server_name_1)) {
                            var message = common.getLabel("lbl_server_name_special_char_rule");
							alert(server_name_1 + " : " + message);
							return false;
						}

						appIDCheck1 = app_id1.split("\.");

						// appID 1 check
						if (appIDCheck1.length >= 3 || appIDCheck1.length >= 4) {
							for (var i = 0; i < appIDCheck1.length; i++) {
								var strAppID = appIDCheck1[i];

								var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

								if (!check_app_id_str) {
                                    var message = common.getLabel("lbl_appid_check_form");
									alert(app_id1 + " : " + message);
									return false;
								}
							}
                        } else {
							var message = common.getLabel("lbl_appid_check_form");
							alert(app_id1 + " : " + message);
							return false;
						}
                        */
					}
				}
                return true;
			};

			scwin.saveStep2Data = function () {
				var platformType = $p.parent().scwin.txt_project_all_step_platform;

				var build_all_in_json = {};

				var tap_all_app_configArray = [];

                for (var tap_cnt = 0; tap_cnt < project_add_step2_tap.getTabCount(); tap_cnt++) {
					var tableFrame = project_add_step2_tap.getFrame(tap_cnt);
                    var append_tap_app_config_data = {};

                    if(tableFrame == undefined){

					} else {
                        // android
						if (platformType == "Android") {
                            append_tap_app_config_data.profile_name = tableFrame.getObj("step2_android_input_profile_name").getValue();
							append_tap_app_config_data.app_id = tableFrame.getObj("step2_android_input_app_id").getValue();
							append_tap_app_config_data.app_name = tableFrame.getObj("step2_android_input_app_name").getValue();
							append_tap_app_config_data.app_version = tableFrame.getObj("step2_android_input_app_version").getValue();
							append_tap_app_config_data.app_version_code = tableFrame.getObj("step2_android_input_app_version_code").getValue();
							append_tap_app_config_data.package_name = tableFrame.getObj("step2_android_input_packagename").getValue();
							append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_minsdk_version").getValue();

                            if (tap_cnt == 0) {
                                if (tableFrame.getObj("step2_android_input_icons_path").dom.fakeinput.files[0] === "undefined") {
									append_tap_app_config_data.icon_image_path = "";
								} else if (tableFrame.getObj("step2_android_input_icons_path").dom.fakeinput.files[0] != undefined) {
									append_tap_app_config_data.icon_image_path = tableFrame.getObj("step2_android_input_icons_path").dom.fakeinput.files[0].name;
								}
							} else {
								append_tap_app_config_data.icon_image_path = "";
							}

                            /*** 서버 입력정보 없애면서 주석처리 시작 -->
							var serverConfigArray = [];
							var server1Json = {};

							server1Json.Name = tableFrame.getObj("input_appname_1").getValue();
							server1Json.AppID = tableFrame.getObj("input_appid_1").getValue();
							server1Json.ServerURL = tableFrame.getObj("input_serverurl_1").getValue();

							serverConfigArray.push(server1Json);

							var serverconfig_cnt = tableFrame.getObj("input_server_config_cnt").getValue();
							var tap_config_cnt = tableFrame.getObj("input_tap_config_cnt").getValue();

							for(var server_tap_cnt = 0; server_tap_cnt < serverconfig_cnt; server_tap_cnt++ ){

								var servertempJson = {};

								// var server_name = tableFrame.getObj("myid_name_input_" + server_tap_cnt);
								// var server_app_id = tableFrame.getObj("myid_app_id_input_" + server_tap_cnt);
								// var server_url = tableFrame.getObj("myid_server_url_input_" + server_tap_cnt);
								var server_name = $p.getComponentById("tap_"+tap_config_cnt+"myid_name_input_" + server_tap_cnt);
								var server_app_id = $p.getComponentById("tap_"+tap_config_cnt+"myid_app_id_input_" + server_tap_cnt);
								var server_url = $p.getComponentById("tap_"+tap_config_cnt+"myid_server_url_input_" + server_tap_cnt);

								if (server_name != null) {
									servertempJson.Name = server_name.getValue();
								}

								if (server_app_id != null) {
									servertempJson.AppID = server_app_id.getValue();
								}

								if (server_url != null) {
									servertempJson.ServerURL = server_url.getValue();
									serverConfigArray.push(servertempJson);
								}

							}

							append_tap_app_config_data.server = serverConfigArray;
							주석처리 끝 ***/

							tap_all_app_configArray.push(append_tap_app_config_data);
						} else if(platformType == "iOS") {
							var profile_name = project_add_step2_tap.getLabelText(tap_cnt);

							append_tap_app_config_data.profile_name = project_add_step2_tap.getLabelText(tap_cnt); // step2_ios_input_profile_name tableFrame.getObj("step2_select_profile_key_name").getValue();
							append_tap_app_config_data.app_id = tableFrame.getObj("step2_ios_input_app_id").getValue();
							append_tap_app_config_data.app_name = tableFrame.getObj("step2_ios_input_app_name").getValue();
							append_tap_app_config_data.app_version = tableFrame.getObj("step2_ios_input_app_version").getValue();
							append_tap_app_config_data.app_version_code = tableFrame.getObj("step2_ios_input_app_version_code").getValue();
							append_tap_app_config_data.package_name = tableFrame.getObj("step2_ios_input_projectname").getValue();
							append_tap_app_config_data.min_target_version = tableFrame.getObj("step2_select_target_version").getValue();
							append_tap_app_config_data.build_type = tableFrame.getObj("step2_select_profile_key_name").getValue();
                            append_tap_app_config_data.debug_type = tableFrame.getObj("step2_select_debug_profile_key_name").getValue();

							if (tap_cnt == 0) {
								if (tableFrame.getObj("step2_ios_input_icons_path").dom.fakeinput.files[0] === "undefined") {
									append_tap_app_config_data.icon_image_path = "";
								} else if (tableFrame.getObj("step2_ios_input_icons_path").dom.fakeinput.files[0] != undefined) {
									append_tap_app_config_data.icon_image_path = tableFrame.getObj("step2_ios_input_icons_path").dom.fakeinput.files[0].name;
								}
							} else {
								append_tap_app_config_data.icon_image_path = "";
							}

							var build_type = tableFrame.getObj("step2_select_profile_key_name").getValue();
							if(build_type == undefined || build_type == null){
								alert(profile_name +" 탭에 해당 Release Type 을 선택해주세요.");
								return false;
							}else {
								append_tap_app_config_data.build_type = build_type;
							}

                            /*** 서버 입력정보 없애면서 주석처리 시작 -->
							var serverConfigArray = [];
							var server1Json = {};

							server1Json.Name = tableFrame.getObj("input_ios_appname_1").getValue();
							server1Json.AppID = tableFrame.getObj("input_ios_appid_1").getValue();
							server1Json.ServerURL = tableFrame.getObj("input_ios_serverurl_1").getValue();

							serverConfigArray.push(server1Json);

							var serverconfig_cnt = tableFrame.getObj("input_ios_server_config_cnt").getValue();
							var tap_config_cnt = tableFrame.getObj("input_ios_tap_config_cnt").getValue();

							for(var server_tap_cnt = 0; server_tap_cnt < serverconfig_cnt; server_tap_cnt++ ){

								var servertempJson = {};

								// var server_name = tableFrame.getObj("myid_name_input_" + server_tap_cnt);
								// var server_app_id = tableFrame.getObj("myid_app_id_input_" + server_tap_cnt);
								// var server_url = tableFrame.getObj("myid_server_url_input_" + server_tap_cnt);
								var server_name = $p.getComponentById("ios_tap_"+tap_config_cnt+"myid_name_input_" + server_tap_cnt);
								var server_app_id = $p.getComponentById("ios_tap_"+tap_config_cnt+"myid_app_id_input_" + server_tap_cnt);
								var server_url = $p.getComponentById("ios_tap_"+tap_config_cnt+"myid_server_url_input_" + server_tap_cnt);

								if (server_name != null) {
									servertempJson.Name = server_name.getValue();
								}

								if (server_app_id != null) {
									servertempJson.AppID = server_app_id.getValue();
								}

								if (server_url != null) {
									servertempJson.ServerURL = server_url.getValue();
									serverConfigArray.push(servertempJson);
								}

							}

							append_tap_app_config_data.server = serverConfigArray;
							<-- 주석처리 끝 ***/

							tap_all_app_configArray.push(append_tap_app_config_data);
						}
                        // server config android, ios 분리해서 처리하기 ..
					}
				}

				// tap_all_app_configArray build_settings
				// build_project
				var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);

				var templateCheck = buildproj_json.template_version;

                // localStorage __workspace_group_role_id__ 값 대신 전역 변수 값을 세팅하기 ...
				// append_tap_app_config_data 내 데이터를 serverConfig array 같이 넣어주고
				build_all_in_json.workspace_group_role_id = localStorage.getItem("__workspace_group_role_id__");
                build_all_in_json.buildProject = buildproj_json;
                build_all_in_json.buildSetting = tap_all_app_configArray;

                var message = common.getLabel("lbl_not_select");
				if (templateCheck == "" || templateCheck == message) { //선택안함
					scwin.setBuildSetting(buildproj_json, tap_all_app_configArray); // server Config Array 값 추가
				} else {
					scwin.setTemplateProjectCopy(buildproj_json, tap_all_app_configArray);
				}
			};

			function changeToolTipContentAddStep5 (componentId, label) {
				let platform = localStorage.getItem("_platform_");
				switch (platform) {
					case "Android":
                        var message = common.getLabel("lbl_app_store_connect_api_key_tip");
						return message
					case "iOS":
                        var message = common.getLabel("lbl_app_store_connect_api_key_tip");
						return message
					default:
						return ""
				}
			};

			scwin.setBuildSetting = function (buildproj_json, buildsetting_json) {
				var build_all_in_json = {};

				build_all_in_json.workspace_group_role_id = localStorage.getItem("__workspace_group_role_id__"); //

				build_all_in_json.buildProject = buildproj_json;
				build_all_in_json.buildSetting = buildsetting_json;

				var options = {};
				options.action = "/manager/build/project/create/vcsMultiProfiles"; // url 변경 해야함.
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(build_all_in_json);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {

						var build_id = data[0].build_id;
						$p.parent().dtl_build_project_step1.setCellData(0, "project_id", build_id);
						localStorage.setItem("__create_build_id__", build_id);
						btn_complete.setDisabled(true);

					} else {
                        var message = common.getLabel("lbl_failed_create_project");
						alert(message); //프로젝트 생성 실패
					}
				};

				options.error = function (e) {
					alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);
					//$p.url("/login.xml");
				};

				$p.ajax(options);

			};

			scwin.setTemplateProjectCopy = function (buildproj_json, buildsetting_json) {
				var build_all_in_json = {};

				build_all_in_json.workspace_group_role_id = localStorage.getItem("__workspace_group_role_id__"); // localstorage 방식 제거 해야함.

				if (scwin.platformType == "Android") {
					build_all_in_json.packageName = buildsetting_json[0].package_name;
					//$p.parent().dtl_build_project_step2.setCellData(0, "all_package_name", input_packagename.getValue());
				} else if (scwin.platformType == "iOS") {
					build_all_in_json.packageName = ""; // input_projectname.getValue();
					//$p.parent().dtl_build_project_step2.setCellData(0, "all_package_name", input_projectname.getValue());
				}

				build_all_in_json.buildProject = buildproj_json;
				build_all_in_json.buildSetting = buildsetting_json;

				var options = {};
				options.action = "/manager/build/project/create/multiProfileTemplateProject";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(build_all_in_json);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {

						var build_id = data[0].build_id;
						$p.parent().dtl_build_project_step1.setCellData(0, "project_id", build_id);
						localStorage.setItem("__create_build_id__", build_id);
						btn_complete.setDisabled(true);

					} else {
						var message = common.getLabel("lbl_failed_create_project");
						alert(message); //프로젝트 생성 실패
					}
				};

				options.error = function (e) {
					alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");
					//$p.url("/login.xml");
				};
				$p.ajax(options);
			};

			scwin.btn_tab_allclose_onclick = function() {
				var tabCnt = project_add_step2_tap.getTabCount();
				for( var i = tabCnt ; i > 0 ; i-- ){
					project_add_step2_tap.deleteTab(i);
				}
			};

			scwin.btn_tab_add_app_config_onclick = function(){
				var rowJSON = {
					"tap_num" : i
				};

				var tabOpt = {
					label : "App Config",
					closable : true,
				};

				var contOpt = {
					frameMode : "wframe",
					src : "/xml/project_add_app_config.xml",
					title : "App Config",
					alwaysDraw : "false",
					scope: "true",
					dataObject: {
						"type" : "json",
						"name" : "tabParam",
						"data" : rowJSON
					}
				};

				project_add_step2_tap.addTab('project0'+i, tabOpt, contOpt); // >> + 버튼으로 동적 할당 하는 기능 추가해야함...
				i++;
			};

			 scwin.btn_next_onclick = function (e) {
				 if (scwin.createProjectToFlag_yn) {
					 $p.parent().scwin.selected_step(4);
				 } else {
                     var message = common.getLabel("lbl_project_add_step02_js_can_move_plugin_page");
					 alert(message); //프로젝트 생성되어야 플러그인 설정 화면에 이동 할 수 있습니다
				 }
			 };

			scwin.trigger1_onclick = function(e) {

                var profilesList = [];
				var platformType = $p.parent().scwin.txt_project_all_step_platform;
				var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);
				var label = input_tabName.getValue();
				var id = "tab_"+label;

				if(label != "" && label != null){
					if(platformType == "iOS"){

						var datareq = {};

						// datareq.platform = platform;
						datareq.key_id = buildproj_json.key_id;

						var options = {};

						options.action = "/manager/mCert/common/selectiOSProfilesKeyName";
						options.mode = "asynchronous";
						options.mediatype = "application/json";
						options.method = "POST";
						options.requestData = JSON.stringify(datareq);

						options.success = function (e) {
							var data = e.responseJSON;
							if (data != null) {

								for(var row in data){
									profilesList.push(data[row].profiles_key_name);
								}

								var rowJSON = {
									"tap_num" : i,
									"profileList": profilesList
								};

								var tabOpt = {
									label : label,
									closable : false,
									tabWidth : "100px"
								};

								var contOpt = {
									frameMode : "wframe",
									src : "/xml/project_add_app_config.xml",
									title : label,
									alwaysDraw : "false",
									scope: "true",
									dataObject: {
										"type" : "json",
										"name" : "tabParam",
										"data" : rowJSON
									}
								};

								project_add_step2_tap.addTab('project0'+i, tabOpt, contOpt);
								i++;
							}
						};

						options.error = function (e) {
							alert("message:" + e.responseJSON + "\n");
						};

						$p.ajax(options);

					} else if(platformType == "Android") {
						var rowJSON = {
							"tap_num" : i,
							"profileList": profilesList
						};

						var tabOpt = {
							label : label,
							closable : false,
							tabWidth : "100px"
						};

						var contOpt = {
							frameMode : "wframe",
							src : "/xml/project_add_app_config.xml",
							title : label,
							alwaysDraw : "false",
							scope: "true",
							dataObject: {
								"type" : "json",
								"name" : "tabParam",
								"data" : rowJSON
							}
						};

						project_add_step2_tap.addTab('project0'+i, tabOpt, contOpt);

						i++;
					}

					input_tabName.setValue("");

				} else {
                    var message = common.getLabel("lbl_project_add_step02_js_input_build_config_name");
					alert(message); //buildConfig 이름을 입력하세요
				}


			};

			scwin.btn_delete_onclick = function(e) {
				var label = input_tabName.getValue();

				if(label == "Debug" || label == "Release"){
					input_tabName.setValue("");
                    var message = common.getLabel("lbl_project_add_step02_js_can_not_delete");
					alert(label + message); //는 삭제할 수 없습니다
					return;
				}

				var tabId = "tab_"+label;
				if(label != "" && label != null){
					var idx = project_add_step2_tap.getTabIndex(tabId);
                    var message = common.getLabel("lbl_project_add_step02_js_confirm_delete");
					if(confirm(label + " " + message)){ //을(를) 삭제 하시겠습니까?
						project_add_step2_tap.deleteTab(idx);
						input_tabName.setValue("");
					}
				} else {
                    var message = common.getLabel("lbl_project_add_step02_js_input_delete_name");
					alert(message); //삭제할 이름을 입력하세요
				}
			};

			scwin.checkAppIconFileYn = function(){

                var platform = $p.parent().scwin.txt_project_all_step_platform;
				var tap_cnt = 0;
				/// tap num = 0 값으로 처리해야함...
				var tableFrame = project_add_step2_tap.getFrame(tap_cnt);
                var AppIconfile;

				if (platform == "Android") {
					// ZipIconfile = document.getElementById("step2_android_input_icons_path");
					AppIconfile = tableFrame.getObj("step2_android_input_icons_path").dom.fakeinput;
					// console.log(AppIconfile.files[0]);
					if(AppIconfile.files[0] == "" || AppIconfile.files[0] == null || AppIconfile.files[0] === "undefined"){
						return false;
					}

					var filename = AppIconfile.files[0].name; // 해당 값에서 png 파일 타입이 아닌 경우 수정 해야함 ...

					if(filename == "" || filename == null){
                        return false;
					}

					var filepngtype = filename.split("\.");

					if(filepngtype[1] != "png"){
                        var message = common.getLabel("lbl_check_img_form");
						alert(message);
						fileObj.value = "";
						return false;
					} else if(filepngtype[1] == "png") {
						return true;
					}
				} else {
					// ZipIconfile = document.getElementById("step2_ios_input_icons_path");
					AppIconfile = tableFrame.getObj("step2_ios_input_icons_path").dom.fakeinput;

					if(AppIconfile.files[0] == "" || AppIconfile.files[0] == null || AppIconfile.files[0] === "undefined"){
						return false;
					}

					var filename = AppIconfile.files[0].name; // 해당 값에서 png 파일 타입이 아닌 경우 수정 해야함 ...

					if(filename == "" || filename == null){
						return false;
					}

					var filepngtype = filename.split("\.");

					if (filepngtype[1] != "png") {
						var message = common.getLabel("lbl_check_img_form");
						alert(message);
						fileObj.value = "";
						return false;
					} else if(filepngtype[1] == "png"){
						return true;
					}
				}
			};

            // 앱 아이콘 업로드 기능 및 앱 아이콘 파일 체크 기능
            scwin.checkAppIconFileAndUpload = function() {

                // png 파일 기반으로 업로드를 해야함
				// 이후에 파일 체크 기능 구현하고
				// 있으면 app icon 업로드
				// 없으면 project create 기능 수행 ...

				var platform = localStorage.getItem("_platform_");
				var formData = new FormData();
				var step1DataList = $p.parent().dtl_build_project_step1.getAllJSON();
				var AppIconfile = "";
                var tap_cnt = 0;

                /// tap num = 0 값으로 처리해야함...
				var tableFrame = project_add_step2_tap.getFrame(tap_cnt);

				if (platform == "Android") {
					// ZipIconfile = document.getElementById("step2_android_input_icons_path");
					// step2_android_input_icons_path
					AppIconfile = tableFrame.getObj("step2_android_input_icons_path").dom.fakeinput;

					formData.append("file", AppIconfile.files[0]);

				} else {
					// ZipIconfile = document.getElementById("step2_ios_input_icons_path");
					AppIconfile = tableFrame.getObj("step2_ios_input_icons_path").dom.fakeinput;

					formData.append("file", AppIconfile.files[0]);
				}

				// branch_id
				// domain id
				// user id
				// workspace id
				// project id
				formData.append("branch_id", step1DataList[0].builder_id);
				formData.append("workspacename", localStorage.getItem("__workspace_name__"));
				formData.append("projectname", step1DataList[0].project_name);
				formData.append("projectDirName", step1DataList[0].project_dir_path);
				formData.append("platform", platform);
				formData.append("target_server", step1DataList[0].target_server);

				$.ajax({
					url: "/manager/build/setting/upload/icon",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						//console.log(r);
						if (status === "Created") {
							//console.log("status success ");
							// alert("이미지 업로드 완료 되었습니다.");
							scwin.app_icon_upload_check_yn = true;
						}
					}
					, error: function (request, status, error) {
						if (request.status == 200){
							// alert("이미지 업로드 완료 되었습니다.");
							scwin.app_icon_upload_check_yn = true;
						}
					}
				});
			};

	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'txt_platform_store',label:'',style:'',useLocale:'true',localeRef:'lbl_app_default_info'}}]},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'w2:textbox',A:{class:'txt_chk',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_confirm_create_project'}},{T:1,N:'xf:trigger',A:{type:'button',id:'btn_complete',style:'',class:'btn_cm type1','ev:onclick':'scwin.btn_complete_onclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body_tap',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'float:right;padding:10px;'},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',style:'width: 144px;height: 21px;',id:'input_tabName',allowChar:'0-9, aA-zZ'}},{T:1,N:'xf:trigger',A:{style:'width: 80px;height: 23px;',id:'trigger1',type:'button','ev:onclick':'scwin.trigger1_onclick',useLocale:'true',localeRef:'lbl_add'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{type:'button',style:'width: 80px;height: 23px;',id:'btn_delete','ev:onclick':'scwin.btn_delete_onclick',useLocale:'true',localeRef:'lbl_del'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:tabControl',A:{frameModal:'false',confirmTrueAction:'exist',useTabKeyOnly:'true',confirmFalseAction:'new',useMoveNextTabFocus:'false',tabScroll:'false',useATagBtn:'true',closable:'false',useConfirmMessage:'false',alwaysDraw:'false',style:'width: 100%;height: 95%;',id:'project_add_step2_tap'}},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''}}]}]}]}]})