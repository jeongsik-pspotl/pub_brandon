/*amd /xml/project_add_step01.xml 43067 c55f696abe3a138afcacb230f5990dffc34bdead0f8a3554460bd3fdc64f279e */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dlt_branch_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'branch_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'branch_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_vcs_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'vcs_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'vcs_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'dlt_ftp_setting_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'ftp_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'ftp_name',name:'name2'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.build_project_name_yn = false;
			 scwin.connect_build_server_yn = false;

			 scwin.btn_check_project_step = false;
			 scwin.txt_project_name_save = "";
			 scwin.onpageload = function () {

				 common.setScopeObj(scwin);
				 $p.parent().scwin.selected_step(1);

				 $p.parent().btn_proejct_step02.setDisabled(true);
				 // $p.parent().btn_proejct_step03.setDisabled(true);
				 $p.parent().btn_proejct_step04.setDisabled(true);
				 $p.parent().btn_proejct_step05.setDisabled(true);

				 fr_grp_proj_save.hide();
				 step1_android_bundle_id_group.hide();

				 var message_not_select = common.getLabel("lbl_not_select");
				 var message_select_default = common.getLabel("lbl_select");

				 step1_select_product_type.setSelectedIndex(0);
				 step1_select_platform.setSelectedIndex(0);
				 step1_select_templatev.setSelectedIndex(0);
				 step1_select_templatev.addItem("", message_not_select); //선택안함
				 step1_select_signingKey_settingbox.setSelectedIndex(0);
				 step1_select_signingKey_settingbox.addItem("", message_select_default); //선택
				 localStorage.setItem("_platform_", step1_select_platform.getValue());
				 $p.parent().scwin.txt_project_all_step_platform = step1_select_platform.getValue();

				 // 디바이스, 언어 선택
				 scwin.getProgrammingInfo(step1_select_platform.getValue());

				 // 환경설정 List 조회
				 scwin.getBranchSettingInfo();

				 scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");

				 scwin.getFTPSettingInfo();

				 scwin.getSigningKeySettingInfoToAdmin(step1_select_platform.getValue(), "");

			 };

			 scwin.onpageunload = function () {

			 };

			 scwin.checkData = function () {
				 var product_type = step1_select_product_type.getValue();
				 var project_name = step1_input_projectname.getValue();
				 var platform = step1_select_platform.getValue();
				 var template_version = step1_select_templatev.getValue();
				 var platform_language = step1_select_platform_language_settingbox.getValue();
				 var branch_select_box = step1_select_builder_settingbox.getValue();
				 var vcs_select_box = step1_select_vcs_settingbox.getValue();
				 var ftp_select_box = step1_select_ftp_settingbox.getValue();
				 var signingkey_box = step1_select_signingKey_settingbox.getValue();
				 var android_app_id = step1_android_bundle_id.getValue();

				 var project_name_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_PROJECT_NAME", project_name);

				 var tempDataList = $p.parent().dtl_build_project_step1.getRowJSON(0);

				 if (common.isEmptyStr(project_name)) {
					 var message = common.getLabel("lbl_check_project_name");
					 alert(message); // 프로젝트 이름을 입력하세요.
					 return false;
				 }

				 if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", project_name)) {
					 var message = common.getLabel("lbl_project_name_special_char_rule");
					 alert(message);
					 // 특수 문자 넣지 말아야할 것 알아보기
					 return false;
				 }

				 if (!project_name_check_str) {
					 var message = common.getLabel("lbl_project_add_step01_js_check_project_name");
					 alert(message); // 프로젝트 이름은 영문자 5자 이상 입력해야 합니다
					 return false;
				 }

				 // project name 자릿수 제한
				 if (common.getCheckInputLength(project_name, project_name.length, 200)) {
					 return false;
				 }

				 if (!scwin.build_project_name_yn) {
					 var message = common.getLabel("lbl_project_add_step01_js_check_project_name_duplicate");
					 alert(message); //프로젝트 이름 중복 확인이 필요합니다
					 return false;
				 }

				 if (tempDataList.project_name != "") {
					 if (tempDataList.project_name != project_name) {
						 var message = common.getLabel("lbl_project_add_step01_js_check_project_name_duplicate");
						 alert(message); //프로젝트 이름 중복 확인이 필요합니다
						 scwin.build_project_name_yn = false;
						 return false;
					 }
				 } else if (scwin.txt_project_name_save != "") {
					 if (scwin.txt_project_name_save != project_name) {
						 var message = common.getLabel("lbl_project_add_step01_js_check_project_name_duplicate");
						 alert(message); //프로젝트 이름 중복 확인이 필요합니다
						 scwin.build_project_name_yn = false;
						 return false;
					 }
				 }

				 scwin.txt_project_name_save = project_name;

				 if (common.isEmptyStr(platform)) {
					 var message = common.getLabel("lbl_project_add_step01_js_select_platform_type");
					 alert(message); // 플렛폼 타입을 선택하세요
					 return false;
				 }

				 if (common.isEmptyStr(platform_language) && product_type == "WMatrix") {
					 var message = common.getLabel("lbl_project_add_step01_js_select_platform_language");
					 alert(message); // 플렛폼 언어를 선택하세요
					 return false;
				 }

				 if (common.isEmptyStr(branch_select_box) && product_type == "WMatrix") {
					 var message = common.getLabel("lbl_project_add_step01_js_select_builder_setting");
					 alert(message); //Builder 설정을 선택하세요
					 return false;
				 }

				 if (common.isEmptyStr(android_app_id) && platform == "Android" && product_type == "generalApp") {
					 var message = common.getLabel("lbl_project_add_step01_js_input_android_bundle_id");
					 alert(message);
					 return false;
				 }

				 if (common.isEmptyStr(vcs_select_box)) {
					 var message = common.getLabel("lbl_project_add_step01_js_select_vcs");
					 alert(message); //VCS를 선택하세요
					 return false;
				 }

				 if (common.isEmptyStr(vcs_select_box) && !common.isEmptyStr(template_version)) {
					 var message = common.getLabel("lbl_project_add_step01_js_select_template_or_vcs");
					 alert(message); // Template Version, VCS 둘중 하나의 설정을 선택하세요
					 return false;
				 }


				 if (common.isEmptyStr(vcs_select_box) && !common.isEmptyStr(template_version)) {
					 var message = common.getLabel("lbl_project_add_step01_js_select_template_and_vcs");
					 alert(message); //Template Version 선택시 VCS는 내부 저장소 방식으로 선택하세요
					 return false;
				 }

				 if (common.isEmptyStr(ftp_select_box)) {
					 var message = common.getLabel("lbl_project_add_step01_js_select_ftp_setting");
					 alert(message); //FTP 설정을 선택하세요
					 return false;
				 }

				 if (common.isEmptyStr(signingkey_box) && platform == "Android") {
					 var message = common.getLabel("lbl_project_add_step01_js_select_signing_key");
					 alert(message); //SigningKey 설정을 선택하세요
					 return false;
				 }

				 return true;
			 };

			 scwin.checkDataProjectName = function () {

				 var project_name = step1_input_projectname.getValue();
				 var project_name_check_str = common.checkAllInputText("CHECK_INPUT_TYPE_PROJECT_NAME", project_name);

				 if (common.isEmptyStr(project_name)) {
					 var message = common.getLabel("lbl_check_project_name");
					 alert(message); // 프로젝트 이름을 입력하세요
					 return false;
				 }

				 if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", project_name)) {
					 var message = common.getLabel("lbl_project_name_special_char_rule");
					 alert(message);
					 return false;
				 }

				 if (!project_name_check_str) {
					 var message = common.getLabel("lbl_project_add_step01_js_check_project_name");
					 alert(message); //프로젝트 이름은 영문자 5자 이상, 200자 이하로 입력해야 합니다
					 return false;
				 }

				 scwin.txt_project_name_save = project_name;

				 return true;
			 };

			 // 2 on message
			 scwin.webSocketCallback = function (obj) {
				 // msg type 추가
				 switch (obj.MsgType) {
					 case "HV_MSG_CONNETCION_CHECK_INFO_FROM_HEADQUATER" :
						 scwin.setBranchConnectionStatus(obj);
						 break;
					 case "HV_MSG_CONNETCION_CHECK_INFO" :
						 scwin.setBranchConnectionStatus(obj);
						 break;
					 case "HV_MSG_PROJECT_TEMPLATE_LIST_INFO_FROM_HEADQUATER":
						 scwin.setBuilderToTemplateVersionSetting(obj);
						 break;
					 case "HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO_FROM_HEADQUATER":
						 scwin.setGeneralAppProjectCreateSyncStatus(obj);
						 break;
						 // case 추가.. builder to  template version
					 default :
						 break;
				 }
			 };

			 scwin.setBranchConnectionStatus = function (data) {
				 switch (data.message) {
					 case "SUCCESSFUL" :
						 var message_successful = common.getLabel("lbl_successful");
						 var message_connect_successful = common.getLabel("lbl_connection_successful");

						 step1_btn_testconnection.setLabel(message_successful); //SUCCESSFUL
						 step1_btn_testconnection.setDisabled(true);
						 scwin.connect_build_server_yn = true;
						 alert(message_connect_successful); //Connection SUCCESSFUL
						 break;
					 case "FAILD" :
						 var message_failed = common.getLabel("lbl_failed");
						 var message_connect_failed = common.getLabel("lbl_connection_failed");

						 step1_btn_testconnection.setLabel(message_failed); // FAILED
						 step1_btn_testconnection.setDisabled(false);
						 scwin.connect_build_server_yn = false;
						 alert(message_connect_failed); // Connection FAILED
						 break;
					 default :
						 break;
				 }
			 };

			 scwin.setBuilderToTemplateVersionSetting = function (data) {

				 switch (data.message) {
					 case "SUCCESSFUL" : // 정상 처리
						 var versionList = data.templateVersionList.templateList;

						 var message = common.getLabel("lbl_not_select");
						 step1_select_templatev.removeAll(false);
						 step1_select_templatev.addItem("", message); //선택안함
						 for (var i = 0; i < versionList.length; i++) {
							 step1_select_templatev.addItem(i + 1, versionList[i]);
						 }

						 break;
					 case "FAILD" : // 못찾을때 처리
						 var message = common.getLabel("lbl_connection_failed");
						 alert(message); // Connection FAILD
						 break;
					 default :
						 break;
				 }
			 };

			 scwin.setGeneralAppProjectCreateSyncStatus = function (data) {
				 switch (data.gitStatus) {
					 case "MKDIR":
						 var message = common.getLabel("lbl_project_dir_create");
						 WebSquare.layer.showProcessMessage(message); //Project Directory Create
						 break;
					 case "GITBARE":
						 var message = common.getLabel("lbl_git_bare_init");
						 WebSquare.layer.showProcessMessage(message); //Git Bare init
						 break;
					 case "GITCLONE":
						 var message = common.getLabel("lbl_git_clone");
						 WebSquare.layer.showProcessMessage(message); //Git Clone
						 break;
					 case "DONE":
						 WebSquare.layer.hideProcessMessage();

						 if (data.projectDirPath != null) {
							 $p.parent().dtl_build_project_step1.setCellData(0, "project_dir_path", data.projectDirPath);
						 }

						 var message = common.getLabel("lbl_project_add_step02_js_confirm");
						 if (confirm(message)) { // 프로젝트 생성 완료 되었습니다. \n빌드 화면으로 이동 하시겠습니까?
							 var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);

							 var data = {};
							 data.platform = buildproj_json.platform;
							 data.project_name = buildproj_json.project_name;
							 data.project_pkid = buildproj_json.project_id;
							 data.workspace_pkid = buildproj_json.workspace_id;
							 data.product_type = buildproj_json.product_type;

							 $p.parent().$p.parent().__buildaction_data__.setJSON([data]);
							 $p.parent().$p.parent().wfm_main.setSrc("/xml/build.xml");
						 }
						 break;
				 }
			 }

			 scwin.saveStep1Data = function () {
				 var project_name = step1_input_projectname.getValue();
				 var product_type = step1_select_product_type.getValue();
				 var platform = step1_select_platform.getValue();
				 var platform_language = step1_select_platform_language_settingbox.getText();
				 var template_version = step1_select_templatev.getText();
				 var project_dir_path = ""; //step1_input_project_dir_path_name.getValue();
				 var description = step1_txtarea_desc.getValue();
				 var builder_id = step1_select_builder_settingbox.getValue();
				 var vcs_id = step1_select_vcs_settingbox.getValue();
				 var ftp_id = step1_select_ftp_settingbox.getValue();
				 var key_id = step1_select_signingKey_settingbox.getValue();
				 // workspace id 값 세팅
				 var local_workspace_id = localStorage.getItem("__workspace_id__");

				 var data = {};
				 data.workspace_id = local_workspace_id;
				 data.project_name = project_name;
				 data.product_type = product_type;
				 data.platform = platform;
				 data.platform_language = platform_language;
				 data.template_version = template_version;
				 // data.target_server = build_server_url; // target_server -> build server 컬럼명 변경해야함.
				 data.project_dir_path = project_dir_path;
				 data.description = description;
				 data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.
				 data.builder_id = builder_id;
				 data.vcs_id = vcs_id;
				 data.ftp_id = ftp_id;
				 data.key_id = key_id;

				 localStorage.setItem("__project_name__", data.project_name);

				 $p.parent().dtl_build_project_step1.setJSON([data]);
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

						 var message_exist_project_name = common.getLabel("lbl_exist_name");
						 var message_can_use_project_name = common.getLabel("lbl_can_use_name");

						 if (data[0].projectname_not_found == "no") {
							 alert(" " + message_exist_project_name); //해당 이름이 존재 합니다.
						 } else if (data[0].projectname_not_found == "yes") {
							 alert(" " + message_can_use_project_name); //해당 이름은 사용 가능 합니다.
							 scwin.build_project_name_yn = true;
						 }
					 }
				 };

				 options.error = function (e) {
					 if (e.responseStatusCode === 500) {
						 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");
					 } else {
						 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");
					 }
				 };

				 $p.ajax(options);
			 };

			 // dlt_branch_setting_selectbox set select box
			 scwin.getProgrammingInfo = function (platform) {
				 step1_select_platform_language_settingbox.removeAll(true);
				 var options = {};

				 options.action = "/manager/build/project/search/programLanguageList/" + platform;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 var message = common.getLabel("lbl_select");
						 step1_select_platform_language_settingbox.addItem("", message); // 선택
						 for (var row in data) {
							 step1_select_platform_language_settingbox.addItem(data[row].role_code_id, data[row].role_code_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };
				 $p.ajax(options);
			 };

			 // dlt_branch_setting_selectbox set select box
			 scwin.getBranchSettingInfo = function () {

				 var options = {};

				 options.action = "/manager/builderSetting/selectBySelectBoxList";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 var message = common.getLabel("lbl_select");
						 step1_select_builder_settingbox.removeAll(false);
						 step1_select_builder_settingbox.addItem("", message); //선택
						 for (var row in data) {
							 var temp = {};

							 step1_select_builder_settingbox.addItem(data[row].builder_id, data[row].builder_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 //console.log(e.responseJSON);
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };
				 $p.ajax(options);
			 };

			 // step1_select_vcs_settingbox set select box
			 scwin.getVCSSettingInfo = function () {

				 var options = {};

				 options.action = "/manager/vcs/search/profileListAllBySelectBox";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_vcs_settingbox.removeAll(false);

						 var message = common.getLabel("lbl_not_select");
						 step1_select_vcs_settingbox.addItem("", message); //선택 안함
						 for (var row in data) {
							 var temp = {};

							 step1_select_vcs_settingbox.addItem(data[row].vcs_id, data[row].vcs_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };
				 $p.ajax(options);
			 };

			 scwin.getVCSSettingInfoByAdmin = function () {

				 var options = {};

				 options.action = "/manager/vcs/search/profileAdminBySelectBox"; // parseInt(adminID);
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_vcs_settingbox.removeAll(false);

						 var message = common.getLabel("lbl_not_select");
						 step1_select_vcs_settingbox.addItem("", message); //선택 안함
						 for (var row in data) {
							 var temp = {};

							 step1_select_vcs_settingbox.addItem(data[row].vcs_id, data[row].vcs_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };

				 $p.ajax(options);
			 };

			 scwin.getVCSSettingFindTypeInfo = function (vcs_type) {

				 var options = {};

				 options.action = "/manager/vcs/search/profileTypeBySelectBox/" + vcs_type;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_vcs_settingbox.removeAll(false);

						 var message = common.getLabel("lbl_not_select");
						 step1_select_vcs_settingbox.addItem("", message); //선택 안함

						 for (var row in data) {
							 var temp = {};

							 step1_select_vcs_settingbox.addItem(data[row].vcs_id, data[row].vcs_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };
				 $p.ajax(options);
			 };

			 // 신규 vcs profile 조회 조건 : (admin_id, vcs_type) 기준으로 추가 조회 하는 기능 추가
			 scwin.getVCSSettingFindAdminAndTypeInfo = function (vcs_type) {
				 var options = {};

				 options.action = "/manager/vcs/search/profileTypeAndAdminBySelectBox/" + vcs_type + "/N";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_vcs_settingbox.removeAll(false);

						 var message = common.getLabel("lbl_not_select");
						 step1_select_vcs_settingbox.addItem("", message); //선택 안함
						 for (var row in data) {
							 var temp = {};

							 step1_select_vcs_settingbox.addItem(data[row].vcs_id, data[row].vcs_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };
				 $p.ajax(options);
			 };

			 // step1_select_ftp_settingbox set select box
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

						 var message = common.getLabel("lbl_select");
						 step1_select_ftp_settingbox.addItem("", message); // 선택
						 for (var row in data) {
							 step1_select_ftp_settingbox.addItem(data[row].ftp_id, data[row].ftp_name);
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 scwin.getSigningKeySettingInfo = function (platform, builder_id) {

				 var options = {};

				 options.action = "/manager/mCert/common/search/profileList";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 step1_select_signingKey_settingbox.removeAll(false);
						 var message = common.getLabel("lbl_select");
						 step1_select_signingKey_settingbox.addItem("", message); // 선택
						 for (var row in data) {

							 // console.log(data);
							 if (platform == "Android") {
								 step1_select_signingKey_settingbox.addItem(data[row].key_build_android_id, data[row].android_key_name);
							 } else if (platform == "iOS") {
								 step1_select_signingKey_settingbox.addItem(data[row].key_build_ios_id, data[row].ios_key_name);
							 }
						 }
					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };

				 $p.ajax(options);

			 };

			 // scwin.send_to_builder_template_version_action = function(builder_id, platform){
			 scwin.send_to_builder_template_version_action = function (builder_id, platform, product_type) {

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
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };

				 $p.ajax(options);
			 };

			 scwin.getSigningKeySettingInfoToAdmin = function (platform, admin_id) {

				 var datareq = {};

				 datareq.platform = platform;

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
						 var message = common.getLabel("lbl_select");
						 step1_select_signingKey_settingbox.addItem("", message); // 선택
						 for (var row in data) {

							 if (data[row].platform == "Android" && platform == "Android") {
								 step1_select_signingKey_settingbox.addItem(data[row].key_id, data[row].key_name);

							 } else if (data[row].platform == "iOS" && platform == "iOS") {
								 step1_select_signingKey_settingbox.addItem(data[row].key_id, data[row].key_name);
							 }
						 }
					 }
				 };

				 options.error = function (e) {

					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");
				 };

				 $p.ajax(options);

			 };

			 scwin.btn_prev_onclick = function (e) {

			 };

			 scwin.btn_next_onclick = function (e) {
				 if (scwin.checkData()) {
					 scwin.saveStep1Data();
					 scwin.btn_project_step2_checkYn = true;

					 var product_type = step1_select_product_type.getValue();

					 if (product_type == "WMatrix") {
						 $p.parent().scwin.selected_step(2);
						 // $p.parent().wfm_project_add_step2.setSrc("");
						 $p.parent().wfm_project_add_step2.setSrc("/xml/project_add_step02.xml");
					 } else {
						 var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);
						 var android_app_id = $p.getComponentById("step1_android_bundle_id").getValue();

						 scwin.createGeneralAppProject(buildproj_json, android_app_id);
					 }
				 }
			 };

			 // 일반 앱 프로젝트 생성
			 scwin.createGeneralAppProject = function (buildproj_json, android_app_id) {
				 data = {};
				 data.workspace_group_role_id = localStorage.getItem("__workspace_group_role_id__");
				 data.buildProject = buildproj_json;
				 data.buildSetting = [{"app_id": android_app_id}];

				 (async () => {
					 try {
						 await fetch('/manager/general/build/project/create', {
							 method: 'POST',
							 headers: {
								 'Content-Type': 'application/json'
							 },
							 body: JSON.stringify(data)
						 })
						 .then(response => response.json())
						 .then(data => {
                             // 프로젝트 생성 직후, project_id 할당
                             $p.parent().dtl_build_project_step1.setCellData(0, 'project_id', data[0].build_id);
						 })
					 } catch (e) {
						 console.log("Error: ", e);
					 }
				 })();
			 }

			 function android_bundle_id_group_show_and_hide() {
				 var product = step1_select_product_type.getValue().toLowerCase();
				 var platform = $p.parent().scwin.txt_project_all_step_platform.toLowerCase();
				 if (product == "generalapp" && platform == "android") {
					 step1_android_bundle_id_group.show();
				 } else {
					 step1_android_bundle_id_group.hide();
				 }
			 }

			 scwin.step1_select_product_onchange = function (e) {
				 var product = step1_select_product_type.getValue();
				 var platform = $p.parent().scwin.txt_project_all_step_platform;

				 if (product == "WMatrix") {
					 // 진행 버튼 보이기
					 $p.parent().grp_step5.show();
					 $p.parent().grp_step4.show();
					 $p.parent().grp_step2.show();

					 // 일반앱으로 인해 사라졌던 컴포넌트 노출
					 step1_select_templatev_group.show();
					 step1_select_platform_language_settingbox_group.show();
					 btn_next.show();
					 fr_grp_proj_save.hide();

					 android_bundle_id_group_show_and_hide();

					 scwin.getProgrammingInfo(platform);
					 scwin.getBranchSettingInfo();
					 scwin.getFTPSettingInfo();
					 scwin.getSigningKeySettingInfoToAdmin(platform, "");

					 if (step1_select_builder_settingbox.getValue() != null) {
						 // product type 추가
						 scwin.send_to_builder_template_version_action(step1_select_builder_settingbox.getValue(), step1_select_platform.getValue(), step1_select_product_type.getValue());
					 } else {
						 step1_select_templatev.removeAll(false);
						 step1_select_signingKey_settingbox.removeAll(false);
						 step1_select_vcs_settingbox.removeAll(false);

						 var message_select = common.getLabel("lbl_select");
						 var message_not_select = common.getLabel("lbl_not_select");
						 step1_select_templatev.addItem("", message_not_select); // 선택안함
						 step1_select_signingKey_settingbox.addItem("", message_select); // 선택
					 }
					 scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");
				 }
				 // 일반 앱인 경우
				 else {
					 // 진행 버튼 가리기
					 $p.parent().grp_step5.hide();
					 $p.parent().grp_step4.hide();
					 $p.parent().grp_step2.hide();

					 // 일반앱 빌드시 필요없는 정보 가리기
					 step1_select_templatev_group.hide();
					 step1_select_platform_language_settingbox_group.hide();
					 btn_next.hide();
					 fr_grp_proj_save.show();

					 android_bundle_id_group_show_and_hide();
				 }
			 };

			 scwin.step1_select_platform_onchange = function (e) {
				 var platform = this.getValue(this.getSelectedIndex);
				 localStorage.setItem("_platform_", platform);
				 $p.parent().scwin.txt_project_all_step_platform = platform;
				 scwin.getProgrammingInfo(platform);
				 scwin.getBranchSettingInfo();
				 scwin.getFTPSettingInfo();

				 if (step1_select_builder_settingbox.getValue() != null) {
					 // product type 추가
					 scwin.send_to_builder_template_version_action(step1_select_builder_settingbox.getValue(), step1_select_platform.getValue(), step1_select_product_type.getValue());
					 scwin.getSigningKeySettingInfoToAdmin(platform, "");

				 } else {
					 step1_select_templatev.removeAll(false);
					 step1_select_signingKey_settingbox.removeAll(false);

					 scwin.getSigningKeySettingInfoToAdmin(platform, "");

					 step1_select_vcs_settingbox.removeAll(false);
					 var message_select = common.getLabel("lbl_select");
					 var message_not_select = common.getLabel("lbl_not_select");
					 step1_select_templatev.addItem("", message_not_select); // 선택안함
					 step1_select_signingKey_settingbox.addItem("", message_select); // 선택
				 }
				 scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");

				 android_bundle_id_group_show_and_hide();
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
					 var message_select = common.getLabel("lbl_select");
					 var message_not_select = common.getLabel("lbl_not_select");
					 step1_select_templatev.addItem("", message_not_select); // 선택안함
					 step1_select_signingKey_settingbox.addItem("", message_select); // 선택

					 scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");
				 }
			 };

			 scwin.step1_select_Template_checkyn_onchange = function (e) {

				 var select_template = step1_select_templatev.getValue();

				 if (select_template == "") {
					 scwin.getVCSSettingFindAdminAndTypeInfo("git,svn");
				 } else {
					 scwin.getVCSSettingFindAdminAndTypeInfo("localgit,localsvn");
				 }
			 };

			 scwin.step1_btn_check_project_name_onclick = function (e) {

				 var project_name = step1_input_projectname.getValue();

				 if (scwin.checkDataProjectName()) {
					 scwin.select_check_project_name(project_name);
				 }

			 };

			 function changeToolTipContentAddStep1(componentId, label) {
				 let platform = localStorage.getItem("_platform_");

				 var message_android = common.getLabel("lbl_sign_profile_tip");
				 var message_ios = common.getLabel("lbl_ios_sign_profile");
				 switch (platform) {
					 case "Android":
						 return message_android //미리 생성한 서명 프로필
					 case "iOS":
						 return message_ios //미리 생성한 서명 프로필 <br> (앱을 기기에 설치하거나 App Store에 제출시 필요한 인증서 서명 정보)
					 default:
						 return ""
				 }
			 };
			 
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_input_project'}},{T:1,N:'xf:group',A:{class:'fr',id:'fr_grp_proj_save',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'txt_chk',useLocale:'true',localeRef:'lbl_project_setting_step01_saveQuestion'}},{T:1,N:'xf:trigger',A:{id:'btn_vcspull_1',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{id:'step1_input_projectname',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_project_name_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step01_product'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_add_step01_tooltip_product'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_product_type',disabledClass:'w2selectbox_disabled',useLocale:'true',useItemLocale:'true',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_product_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_wmatrix'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'WMatrix'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_general_app'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'generalApp'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_os'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_app_os_env'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_platform',disabledClass:'w2selectbox_disabled',useLocale:'true',useItemLocale:'true',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_platform_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_android'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'Android'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'lbl_ios'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'iOS'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'step1_android_bundle_id_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_android_appid'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{id:'step1_android_bundle_id',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'step1_select_platform_language_settingbox_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step01_lang'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_app_language'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_platform_language_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_build_server_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_build_server_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_builder_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_branch_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.step1_select_builder_onchange'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'step1_select_templatev_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_wmatrix_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_version_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_templatev',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false','ev:onchange':'scwin.step1_select_Template_checkyn_onchange'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_version_manage_tool_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_version_manage_tool_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_vcs_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_inner_deploy_server_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_inner_deploy_server_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_ftp_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_sign_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'signing_profile_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep1',useLocale:'true',tooltipLocaleRef:'lbl_sign_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_signingKey_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_explain'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_descript_project'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:textarea',A:{id:'step1_txtarea_desc',style:'width:100%;height:100px;'}}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})