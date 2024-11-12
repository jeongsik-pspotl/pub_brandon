/*amd /xml/project_import_step01.xml 37376 ccd55c9cf98d42a13213526e2d8ff5027f8cd3633587fe6348192aacfc264fbf */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.build_project_name_yn = false;
			 scwin.platformType = "";
			 scwin.onpageload = function () {
				 // 현재 나의 scope 객체를 전역 변수에 저장한다.
				 common.setScopeObj(scwin);
				 $p.parent().scwin.selected_step(1);

				 step1_select_product_type.setSelectedIndex(0);
				 step1_select_platform.setSelectedIndex(0);
				 step1_select_templatev.setSelectedIndex(0);
				 step1_select_templatev.addItem("", "선택안함");
				 step1_select_signingKey_settingbox.setSelectedIndex(0);
				 step1_select_signingKey_settingbox.addItem("", "선택");
				 localStorage.setItem("_platform_", step1_select_platform.getValue());
				 $p.parent().scwin.txt_project_all_step_platform = step1_select_platform.getValue();
				 scwin.platformType = step1_select_platform.getValue();

				 // 디바이스, 언어 선택
				 scwin.getProgrammingInfo(step1_select_platform.getValue());

				 // 환경설정 List 조회
				 scwin.getBranchSettingInfo();

				 scwin.getVCSSettingFindAdminAndTypeInfo("git,localsvn");

				 scwin.getFTPSettingInfo();

				 scwin.getSigningKeySettingInfoToAdmin(step1_select_platform.getValue(), "");
			 };

			 /**
			  * scwin.getProgrammingInfo (platform)
			  * platform : Android/iOS
			  *
			  * 플랫폼 별 사용 언어 조회 기능
			  *
			  */
			 scwin.getProgrammingInfo = function (platform) {

				 step1_select_platform_language_settingbox.removeAll(true);
				 var options = {};

				 options.action = "/manager/build/project/search/programLanguageList/"+platform;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 var sampleProjectYn = localStorage.getItem("__sample_project_yn__");
						 if(sampleProjectYn == "N"){
							 step1_select_platform_language_settingbox.addItem("","선택");
						 }

						 for (var row in data) {
							 step1_select_platform_language_settingbox.addItem(data[row].role_code_id, data[row].role_code_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("message:"+e.responseJSON+"\n");
				 };
				 $p.ajax( options );
			 };

			 /**
			  *  scwin.getBranchSettingInfo
			  *  Builder Profile 리스트 조회 function
			  */
			 scwin.getBranchSettingInfo = function () {

				 step1_select_builder_settingbox.removeAll(false);
				 var options = {};

				 options.action = "/manager/builderSetting/selectBySelectBoxList";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 var sampleProjectYn = localStorage.getItem("__sample_project_yn__");
						 if(sampleProjectYn == "N"){
							 step1_select_builder_settingbox.addItem("","선택");
						 }

						 for (var row in data) {
							 step1_select_builder_settingbox.addItem(data[row].builder_id, data[row].builder_name);
						 }

						 if(sampleProjectYn == "Y"){
							 step1_select_builder_settingbox.setSelectedIndex(0);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("message:"+e.responseJSON+"\n");
				 };
				 $p.ajax( options );
			 };

			 /**
			  *  scwin.getVCSSettingFindAdminAndTypeInfo
			  *  VCS Profile 리스트 조회 function
			  */
			 scwin.getVCSSettingFindAdminAndTypeInfo = function(vcs_type){

				 var sampleProjectYn = localStorage.getItem("__sample_project_yn__");
				 var options = {};

				 options.action = "/manager/vcs/search/profileTypeAndAdminBySelectBox/" + vcs_type +"/"+sampleProjectYn;
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_vcs_settingbox.removeAll(false);

						 if(sampleProjectYn == "Y" && vcs_type == "localgit"){

						 }else {
							 step1_select_vcs_settingbox.addItem("","선택 안함");
						 }

						 for (var row in data) {
							 step1_select_vcs_settingbox.addItem(data[row].vcs_id, data[row].vcs_name);
						 }

						 if(sampleProjectYn == "Y" && vcs_type == "localgit"){
							 step1_select_vcs_settingbox.setSelectedIndex(0);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("message:"+e.responseJSON+"\n");
				 };
				 $p.ajax( options );
			 };

			 /**
			  *  scwin.getFTPSettingInfo
			  *  내부 배포 서버  Profile 리스트 조회 function
			  */
			 scwin.getFTPSettingInfo = function () {
				 var options = {};

				 options.action = "/manager/ftp/setting/search/selectBoxListAll";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_ftp_settingbox.removeAll(false);
						 var sampleProjectYn = localStorage.getItem("__sample_project_yn__");
						 if(sampleProjectYn == "N"){
							 step1_select_ftp_settingbox.addItem("","선택");
						 }

						 for (var row in data) {
							 step1_select_ftp_settingbox.addItem(data[row].ftp_id, data[row].ftp_name);
						 }

						 if(sampleProjectYn == "Y"){
							 step1_select_ftp_settingbox.setSelectedIndex(0);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("message:"+e.responseJSON+"\n");
				 };

				 $p.ajax( options );

			 };

			 /**
			  *  scwin.getSigningKeySettingInfoToAdmin
			  *  인증서 Profile 리스트 조회 function
			  */
			 scwin.getSigningKeySettingInfoToAdmin = function (platform, admin_id) {

				 var sampleProjectYn = localStorage.getItem("__sample_project_yn__");
				 var datareq = {};

				 datareq.platform = platform;
				 datareq.sampleProjectYn = sampleProjectYn;

				 var options = {};

				 options.action = "/manager/mCert/common/selectAllByAdmin";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "POST";
				 options.requestData = JSON.stringify(datareq);

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 //console.log(data);
						 step1_select_signingKey_settingbox.removeAll(false);
						 if(sampleProjectYn == "N"){
							 step1_select_signingKey_settingbox.addItem("", "선택");
						 }

						 for (var row in data) {
							 if (data[row].platform == "Android" && platform == "Android") {
								 step1_select_signingKey_settingbox.addItem(data[row].key_id, data[row].key_name);
							 } else if (data[row].platform == "iOS" && platform == "iOS") {
								 step1_select_signingKey_settingbox.addItem(data[row].key_id, data[row].key_name);
							 }
						 }

						 if(sampleProjectYn == "Y"){
							 step1_select_signingKey_settingbox.setSelectedIndex(0);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("message:" + e.responseJSON + "\n");
				 };

                 $p.ajax(options);
			 };

			 scwin.send_to_builder_template_version_action = function(builder_id, platform, product_type){
				 common.setScopeObj(scwin);

				 var data = {};
				 var options = {};

				 data.branch_id = builder_id;
				 data.platform = platform;
				 data.product_type = product_type;

				 // ajax 호출
				 options.action = "/manager/build/project/search/templateList";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "POST";
				 options.requestData = JSON.stringify(data);

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

					 }
				 };

				 options.error = function (e) {
					 alert("message:"+e.responseJSON+"\n");
				 };

				 $p.ajax( options );
			 };

			 scwin.checkData = function () {
				 var project_name = step1_import_input_projectname.getValue();
				 var platform = step1_import_select_platform.getValue();
				 //var template_version = step1_select_templatev.getValue();
				 var build_server_url = step1_import_input_buildserver.getValue();
				 var description = step1_import_txtarea_desc.getValue();

				 var project_name_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_PROJECT_NAME", project_name);
				 var build_server_url_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_IP_PORT", build_server_url);

				 if (common.isEmptyStr(project_name)) {
					 alert("프로젝트 이름을 입력하세요.");
					 return false;
				 }

				 if (!project_name_check_str) {
					 alert('프로젝트 이름 형식에 맞지 않습니다.');
					 return false;
				 }

				 if (!build_server_url_check_str) {
					 alert('IP, PORT 형식에 맞지 않습니다.');
					 return false;
				 }

				 if (common.isEmptyStr(platform)) {
					 alert("플렛폼 타입을 선택하세요.");
					 return false;
				 }

				 if (common.isEmptyStr(build_server_url)) {
					 alert("빌드 서버 URL을 입력하세요.");
					 return false;
				 }

				 if (common.isEmptyStr(description)) {
					 alert("프로젝트 설명을 입력하세요.");
					 return false;
				 }

				 return true;
			 };

			 scwin.saveStep1Data = function () {
				 var project_name = step1_input_projectname.getValue();
				 var product_type = step1_select_product_type.getValue();
				 var platform = step1_select_platform.getValue();
				 var platform_language = step1_select_platform_language_settingbox.getText();
				 var template_version = step1_select_templatev.getValue();;
				 var project_dir_path = "";
				 var description = step1_txtarea_desc.getValue();
				 var builder_id = step1_select_builder_settingbox.getValue();
				 var vcs_id = step1_select_vcs_settingbox.getValue();
				 var ftp_id =  step1_select_ftp_settingbox.getValue();
				 var key_id = step1_select_signingKey_settingbox.getValue();
				 // workspace id 값 세팅
				 var local_workspace_id = localStorage.getItem("__workspace_id__");

				 var templateCheck = step1_select_templatev.getValue();

				 var data = {};
				 data.workspace_id = local_workspace_id;
				 data.project_name = project_name;
				 data.product_type = product_type;
				 data.platform = platform;
                 data.template_version = template_version;
				 data.platform_language = platform_language;
				 data.project_dir_path = project_dir_path;
				 data.description = description;
				 data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.
				 data.builder_id = builder_id;
				 data.vcs_id = vcs_id;
				 data.ftp_id = ftp_id;
				 data.key_id = key_id;

				 localStorage.setItem("__project_name__", data.project_name);

				 $p.parent().dtl_build_import_project_step1.setJSON([data]);

                 scwin.setTemplateProjectCopy(data, data);
			 };

			 scwin.webSocketCallback = function (obj) {
				 // msg type 추가
				 switch (obj.MsgType) {
					 case "HV_MSG_PROJECT_TEMPLATE_LIST_INFO_FROM_HEADQUATER":
						 scwin.setBuilderToTemplateVersionSetting(obj);
						 break;
					 case "HV_MSG_PROJECT_IMPORT_STATUS_INFO_FROM_HEADQUATER" :
						 scwin.setBuilderToProjectImportStastus(obj);
                         break;
					 case "HV_MSG_DEPLOY_SETTING_STATUS_INFO_FROM_HEADQUATER" :
						 scwin.setBuilderDeploySettingStatus(obj);
						 break;
					 case "HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_HEADQUATER" :
						 scwin.setBranchProjectCreateSyncStatus(obj);
                         break;
						 // case 추가.. builder to  template version
					 default :
						 break;
				 }
			 };

			 scwin.setBuilderToTemplateVersionSetting = function(data) {

				 switch (data.message) {
					 case "SUCCESSFUL" : // 정상 처리
						 var versionList = data.templateVersionList.templateList;

						 	 step1_select_templatev.removeAll(false);
							 step1_select_templatev.addItem("", "선택안함");
							 for(var i = 0 ; i <versionList.length; i++){
								 step1_select_templatev.addItem(i+1, versionList[i]);
							 }
						 break;
					 case "FAILD" : // 못찾을때 처리
						 alert("Connection FAILD");
						 break;
					 default :
						 break;
				 }
			 };

             scwin.setBuilderToProjectImportStastus = function(data) {
                  switch (data.gitStatus) {
					  case "DONE" :
						  WebSquare.layer.hideProcessMessage();
						  alert("프로젝트 가져오기 완료 헸습니다.");
                          break;
					  case "FAILD" :
						  WebSquare.layer.hideProcessMessage();
						  alert("프로젝트 가져오기 실패 헸습니다.");
                          break;
					  default :
						  WebSquare.layer.showProcessMessage("Project Import ...");
                          break;
				  }
			 };

			 scwin.setBuilderDeploySettingStatus = function (obj) {
				 switch (obj.status) {
					 case "DONE":
						 WebSquare.layer.hideProcessMessage();
						 break;
					 default :
						 WebSquare.layer.showProcessMessage("Project Import ...");
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
						 // scwin.saveStep2Data();
						 break;
					 case "DONE":
						 WebSquare.layer.hideProcessMessage();
						 // alert("프로젝트 생성 완료");

						 var message = common.getLabel("lbl_project_import_js_confirm");
						 if (confirm(message)){ // 프로젝트 생성 완료 되었습니다. \n빌드 화면으로 이동 하시겠습니까?

							 var buildproj_json = $p.parent().dtl_build_import_project_step1.getRowJSON(0);

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
                             data.product_type = product_type

							 buildAction.push(data);

							 $p.parent().$p.parent().__buildaction_data__.setJSON(buildAction);
							 $p.parent().$p.parent().wfm_main.setSrc("/xml/build.xml");

						 } else {
							 // flag yn 처리
							 scwin.btn_project_step4_checkYn = true;
							 scwin.createProjectToFlag_yn = true;
							 btn_next.show();
						 }
						 break;
					 default :
						 WebSquare.layer.showProcessMessage("Project Import ...");
						 break;
				 }
			 };

			 scwin.setBuildSetting = function (buildproj_json, buildsetting_json) {

				 var build_all_in_json = {};

				 build_all_in_json.workspace_group_role_id = localStorage.getItem("__workspace_group_role_id__"); //

				 build_all_in_json.buildProject = buildproj_json;
				 build_all_in_json.buildSetting = buildsetting_json;

				 var options = {};
				 options.action = "/manager/build/project/import/vcsMultiProfiles"; // url 변경 해야함.
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
						 alert("프로젝트 가져오기 실패");
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);
				 };
				 $p.ajax(options);
			 };

			 scwin.setTemplateProjectCopy = function (buildproj_json, buildsetting_json) {

                 var product_type = step1_select_product_type.getValue();
				 var import_all_in_json = {};

				 import_all_in_json.workspace_group_role_id = localStorage.getItem("__workspace_group_role_id__"); // localstorage 방식 제거 해야함.

				 if (scwin.platformType == "Android") {
					 import_all_in_json.packageName = step1_input_packagename.getValue();
				 } else if (scwin.platformType == "iOS") {
					 import_all_in_json.packageName = step1_input_packagename.getValue(); // input_projectname.getValue();
				 }

				 import_all_in_json.buildProject = buildproj_json;
				 import_all_in_json.buildSetting = "";

				 var importProjectZipFile = step1_input_projectfile.dom.fakeinput.files[0];

				 var formData = new FormData();
                 // TODO : form data 형식의 데이터 구조 쌓기..
				 formData.append("zipFile", importProjectZipFile);
				 formData.append('projectImportJson', JSON.stringify(import_all_in_json));

				 $.ajax({
					 url: "/manager/project/import/multiProfileTemplateProject",
					 type: "POST",
					 enctype: 'multipart/form-data',
					 processData: false,
					 contentType: false,
					 data: formData,
					 dataType: 'json',
					 cache: false,
					 success: function (r, status) {
						 var data = r;
						 if (status === "success") {
							 scwin.signingkeyLastCnt = data[0].signingkeyCnt;
						 }
					 }
					 , error: function (request, status, error) {
						 alert("message:" + request.responseText + "\n");
					 }
				 });
			 };

			 scwin.step1_select_Template_checkyn_onchange = function(e){
				 var select_template = step1_select_templatev.getValue();
				 var product = step1_select_product_type.getValue();
                 if (product == "WMatrix") {
					 if(select_template == "") {
						 scwin.getVCSSettingFindAdminAndTypeInfo("git");
					 } else {
						 scwin.getVCSSettingFindAdminAndTypeInfo("localgit,localsvn");
					 }
                 } else {
                     scwin.getVCSSettingFindAdminAndTypeInfo("git,localgit");
				 }
			 };

             scwin.step1_select_product_onchange = function(e){
                 var product = step1_select_product_type.getValue();
				 var platform = $p.parent().scwin.txt_project_all_step_platform;

                 if (product == "WMatrix") {
					 scwin.getProgrammingInfo(platform);
					 scwin.getBranchSettingInfo();
					 scwin.getFTPSettingInfo();

					 step1_select_templatev_group.show();
					 step1_select_platform_language_settingbox_group.show();

					 if (step1_select_builder_settingbox.getValue() != null) {
						 // product type 추가
						 scwin.send_to_builder_template_version_action(step1_select_builder_settingbox.getValue(), step1_select_platform.getValue(), step1_select_product_type.getValue());

						 scwin.getSigningKeySettingInfoToAdmin(platform, "");

						 scwin.getVCSSettingFindAdminAndTypeInfo("git");

					 } else if(step1_select_templatev.getValue() == ""){
						 scwin.getVCSSettingFindAdminAndTypeInfo("git");
					 } else {
						 step1_select_templatev.removeAll(false);
						 step1_select_signingKey_settingbox.removeAll(false);

						 scwin.getSigningKeySettingInfoToAdmin(platform, "");

					 	 step1_select_vcs_settingbox.removeAll(false);
						 step1_select_templatev.addItem("", "선택안함");
						 step1_select_signingKey_settingbox.addItem("", "선택");

						 scwin.getVCSSettingFindAdminAndTypeInfo("git,localsvn");
					 }
				 }
                 // 일반 앱인 경우
				 else {
					 step1_select_templatev_group.hide();
					 step1_select_platform_language_settingbox_group.hide();

                     // 일반 앱 가져오기는 git, localgit만 지원한다. (SVN 지원 X)
					 scwin.getVCSSettingFindAdminAndTypeInfo("git,localgit");
				 }
			 };

			 scwin.step1_select_platform_onchange = function (e) {

				 var platform = this.getValue(this.getSelectedIndex);
				 localStorage.setItem("_platform_", platform);
				 $p.parent().scwin.txt_project_all_step_platform = platform;
				 scwin.getProgrammingInfo(platform);
				 scwin.getBranchSettingInfo();
				 scwin.getFTPSettingInfo();

                 if (platform == "Android") {
					 step1_app_id_label.setValue("Bundle ID");
				 } else {
                     step1_app_id_label.setValue(".xcodeproj Name")
				 }

				 if (step1_select_builder_settingbox.getValue() != null) {
					 // product type 추가
					 scwin.send_to_builder_template_version_action(step1_select_builder_settingbox.getValue(), step1_select_platform.getValue(), step1_select_product_type.getValue());

					 scwin.getSigningKeySettingInfoToAdmin(platform, "");

					 scwin.getVCSSettingFindAdminAndTypeInfo("git");

				 } else if(step1_select_templatev.getValue() == ""){
					 scwin.getVCSSettingFindAdminAndTypeInfo("git");
				 } else {
					 step1_select_templatev.removeAll(false);
					 step1_select_signingKey_settingbox.removeAll(false);

					 scwin.getSigningKeySettingInfoToAdmin(platform, "");

					 step1_select_vcs_settingbox.removeAll(false);
					 step1_select_templatev.addItem("", "선택안함");
					 step1_select_signingKey_settingbox.addItem("", "선택");

					 scwin.getVCSSettingFindAdminAndTypeInfo("git,localsvn");
				 }
			 };

			 scwin.step1_select_builder_onchange = function (e) {
				 var platform = localStorage.getItem("_platform_");
				 if (step1_select_builder_settingbox.getValue() != "") {
					 // product type 추가
					 scwin.send_to_builder_template_version_action(step1_select_builder_settingbox.getValue(), step1_select_platform.getValue(), step1_select_product_type.getValue());
					 scwin.getSigningKeySettingInfoToAdmin(platform, "");
				 } else {
					 step1_select_templatev.removeAll(false);
					 step1_select_signingKey_settingbox.removeAll(false);
					 step1_select_templatev.addItem("", "선택안함");
					 step1_select_signingKey_settingbox.addItem("", "선택");

					 scwin.getVCSSettingFindAdminAndTypeInfo("git,localsvn");
				 }
			 };

			 scwin.step1_btn_check_project_name_onclick = function (e) {

				 var project_name = step1_input_projectname.getValue();

				 if (scwin.checkDataProjectName()) {
					 scwin.select_check_project_name(project_name);
				 }
			 };

			 scwin.checkDataProjectName = function () {

				 var project_name = step1_input_projectname.getValue();
				 var project_name_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_PROJECT_NAME", project_name);

				 if (common.isEmptyStr(project_name)) {
					 alert("프로젝트 이름을 입력하세요.");
					 return false;
				 }

				 if(common.checkAllInputText("CHECK_INPUT_TYPE_SPC",project_name)){
					 alert("특수문자는 입력할수 없습니다.");
					 return false;
				 }

				 if (!project_name_check_str) {
					 alert('프로젝트 이름은 영문자 5자 이상 부터 200자 이하 내로  입력해야 합니다.');
					 return false;
				 }

				 scwin.txt_project_name_save = project_name;

				 return true;
			 };

			 scwin.select_check_project_name = function (project_name) {

				 var options = {};

				 options.action = "/manager/build/project/search/checkProjectName/" + project_name;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (e.responseStatusCode === 200 || e.responseStatusCode === 201) {

						 if(data[0].projectname_not_found == "no"){
							 alert(" 해당 이름이 존재 합니다.");
						 }else if(data[0].projectname_not_found == "yes"){
							 alert(" 해당 이름은 사용 가능 합니다.");
							 scwin.build_project_name_yn = true;
						 }
					 }
				 };

				 options.error = function (e) {
					 if (e.responseStatusCode === 500) {
						 alert("message:" + e.responseText + "\n");
					 } else {
						 alert("message:" + e.responseText + "\n");
					 }
				 };

				 $p.ajax(options);
			 };

			 scwin.btn_complete_onclick = function (e) {
				 if (confirm('프로젝트 가져오기 하시겠습니까?')) {
					 // app icon upload file yn 확인
					 // app icon 있을 경우 png file uplaod 실행
					 scwin.saveStep1Data();
				 }
			 };
			 
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_import'}}]},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'w2:textbox',A:{class:'txt_chk',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_do_project_import'}},{T:1,N:'xf:trigger',A:{type:'button',id:'btn_complete',style:'',class:'btn_cm type1','ev:onclick':'scwin.btn_complete_onclick',useLocale:'true',localeRef:'lbl_import'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'Upload',style:''}},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step1_input_projectfile',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{id:'step1_input_projectname',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_project_name_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'step1_app_id_label',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_bundle_id'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_package_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{id:'step1_input_packagename',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step01_product'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_add_step01_tooltip_product'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_product_type',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_product_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'W-Matrix'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'WMatrix'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'일반 앱'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'generalApp'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_os'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_osSetting'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_platform',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_platform_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Android'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'Android'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'iOS'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'iOS'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'step1_select_platform_language_settingbox_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step01_lang'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_app_language'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_platform_language_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_build_server_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_build_server_profile_tip'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Description'}]}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_builder_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.step1_select_builder_onchange'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'step1_select_templatev_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_wmatrix_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_version_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_templatev',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.step1_select_Template_checkyn_onchange'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_version_manage_tool_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_version_manage_tool_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_vcs_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_inner_deploy_server_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_inner_deploy_server_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_ftp_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_sign_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'signing_profile_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep1',useLocale:'true',tooltipLocaleRef:'lbl_sign_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_signingKey_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_explain'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_descript_project'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:textarea',A:{id:'step1_txtarea_desc',style:'width:100%;height:100px;'}}]}]}]}]}]}]}]}]})