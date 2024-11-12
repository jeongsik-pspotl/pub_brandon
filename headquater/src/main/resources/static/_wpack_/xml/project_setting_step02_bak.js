/*amd /xml/project_setting_step02_bak.xml 53954 a090ee628285e8bffc6c75a34a5f9ddd446ddc4e7a9713a8dbea09903dccfe59 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/jszip.min.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/jszip-utils.min.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.platformType = "";
			 scwin.onpageload = function () {
				 common.setScopeObj(scwin);
				 scwin.initPage();

				 var build_setting_json = $p.parent().dtl_build_setting_step2.getAllJSON();
				 var build_setting1_json = $p.parent().dtl_build_setting_step1.getAllJSON();

				 // ios app icon view
				 document.getElementById("step2_ios_input_icons_path").addEventListener("change", function (event) {

					 var imgExt = ['png', 'jpg', 'jpeg'];
					 var promise = [];
					 var files = event.target.files;


					 var pro = arrangeZip(files[0]);


					 pro.then(function (value) {

						 // 해당 소스 고민 해보기...
						 for (var i = 0; i < value.length; i++) {
							 if (value[i].type == 'image') {

								 var blob = b64toBlob(value[i].cont, 'image/png');
								 var appIconName = value[i].name;
								 var reader = new FileReader();
								 reader.readAsDataURL(blob);
								 reader.fileName = appIconName;
								 reader.platform = build_setting1_json[0].platform; // localStorage.getItem("_platform_");
								 reader.onload = function (e) {

									 if (this.platform == 'iOS') {
										 // platform 기준 분리
										 if (/^29/.test(this.fileName)) {
											 icon29.setSrc(this.result);

										 } else if (/^40/.test(this.fileName)) {
											 icon40.setSrc(this.result);

										 } else if (/^57/.test(this.fileName)) {
											 // icon57.setSrc(this.result);

										 } else if (/^58/.test(this.fileName)) {
											 icon58.setSrc(this.result);

										 } else if (/^60/.test(this.fileName)) {
											 icon60.setSrc(this.result);

										 } else if (/^80/.test(this.fileName)) {
											 icon80.setSrc(this.result);

										 } else if (/^87/.test(this.fileName)) {
											 icon87.setSrc(this.result);

										 } else if (/^114/.test(this.fileName)) {
											 // icon114.setSrc(this.result);

										 } else if (/^120/.test(this.fileName)) {
											 icon120.setSrc(this.result);

										 } else if (/^180/.test(this.fileName)) {
											 icon180.setSrc(this.result);

										 } else if (/^1024/.test(this.fileName)) {
											 icon1024.setSrc(this.result);
										 }
									 }

								 }

							 }

						 }

					 }, function (err) {
					 }); // step2_ios_input_icons_path end

				 }, false);

				 document.getElementById("step2_android_input_icons_path").addEventListener("change", function (event) {

					 var imgExt = ['png', 'jpg', 'jpeg'];
					 var promise = [];
					 var files = event.target.files;

					 var pro = arrangeZip(files[0]);

					 pro.then(function (value) {
						 for (var i = 0; i < value.length; i++) {
							 if (value[i].type == 'image') {

								 var blob = b64toBlob(value[i].cont, 'image/png');
								 var appIconName = value[i].name;
								 var reader = new FileReader();
								 reader.readAsDataURL(blob);
								 reader.fileName = appIconName;
								 reader.platform = build_setting1_json[0].platform;  //localStorage.getItem("_platform_");
								 reader.onload = function (e) {

									 // platform 기준 분리
									 if (this.platform == 'Android') {
										 icon_xhdpi.setSrc(this.result);

									 }

								 }

							 }

						 }

					 }, function (err) {
					 }); //  step2_android_input_icons_path ends

				 }, false);

			 };

			 scwin.onpageunload = function () {

			 };

			 scwin.initPage = function () {
				 var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();
				 var build_setting_json = $p.parent().dtl_build_setting_step2.getAllJSON();
				 // platform_type

				 scwin.platformType = localStorage.getItem("_platform_");

				 // app config 조회 호출 전 파라미터 값 설정
				 var data = {};

				 data.build_id = buildproj_json[0].project_id;
				 data.workspace_id = buildproj_json[0].workspace_id;
				 data.project_name = buildproj_json[0].project_name;
				 data.project_dir_path = buildproj_json[0].project_dir_path;
                 data.product_type = buildproj_json[0].product_type;

				 if (scwin.platformType == "Android") {
					 grp_iOS.hide();
					 grp_Windows.hide();

					 scwin.appIconImageLoad();

				 } else if (scwin.platformType == "Windows") {
					 grp_Android.hide();
					 grp_iOS.hide();

					 step2_windows_input_app_id.setDisabled(true);
					 step2_windows_input_app_version.setDisabled(true);
					 step2_windows_input_app_version_code.setDisabled(true);

				 } else {
					 grp_Android.hide();
					 grp_Windows.hide();

					 // 앱 아이콘 조회 기능 바로 호출
					 // workspace_id, project_id
					 scwin.appIconImageLoad();
				 }

				 if (scwin.platformType == "Android"){

					 // android  getserverconfig 조회 기능 적용
					 data.platform = scwin.platformType;
					 // form_wrap_ios.hide();
					 scwin.getHybridServerConfig(data);

				 }else if(scwin.platformType == "iOS"){

					 // ios getserverconfig 조회 기능 적용
					 data.platform = scwin.platformType;
					 // form_wrap_android.hide();
					 if($p.parent().scwin.getResultAppConfigData === undefined){
						 scwin.getHybridServerConfig(data);
					 }else {
						 scwin.selectiOSBuildAppConfigListData($p.parent().scwin.getResultAppConfigData);
					 }

				 }

				 var update_yn = localStorage.getItem("__update_yn__");

				 if(update_yn == "1"){
					 if (scwin.platformType == "Android"){
						 step2_fr_grp_proj_save_android.show();
					 }else if(scwin.platformType == "iOS"){
						 step2_fr_grp_proj_save_ios.show();
					 }

				 }else {
					 step2_fr_grp_proj_save_android.hide();
					 step2_fr_grp_proj_save_ios.hide();
				 }

			 };

			 scwin.checkData = function () {
				 var platformType = localStorage.getItem("_platform_");
				 var app_id = "";
				 var app_name = "";
				 var app_version = "";
				 var app_version_code = "";

				 var buildsetting_json = $p.parent().dtl_build_setting_step2.getAllJSON();


				 if (platformType == 'Android') {
					 app_id = step2_android_input_app_id.getValue();
					 app_name = step2_android_input_app_name.getValue();
					 app_version = step2_android_input_app_version.getValue();
					 app_version_code = step2_android_input_app_version_code.getValue();


				 } else if (platformType == 'Windows') {
					 app_id = step2_windows_input_app_id.getValue();
					 app_name = step2_windows_input_app_name.getValue();
					 app_version = step2_windows_input_app_version.getValue();
					 app_version_code = step2_windows_input_app_version_code.getValue();

				 } else {
					 app_id = step2_ios_input_app_id.getValue();
					 app_name = step2_ios_input_app_name.getValue();
					 app_version = step2_ios_input_app_version.getValue();
					 app_version_code = step2_ios_input_app_version_code.getValue();

				 }

				 var check_app_version_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION", app_version);
				 var check_app_version_code_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_VERSION_CODE", app_version_code);
				 // var check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID",app_id);

				 if (common.isEmptyStr(app_id)) {
                     var message = common.getLabel("lbl_input_appid");
					 alert(message); //앱 아이디를 입력하세요
					 scwin.app_id_license_check_yn = false;
					 return false;
				 }

				 // 앱 라이센스 체크를 하세요.
				 // if (!scwin.app_id_license_check_yn) {
				 //  alert("APPID 체크를 하세요.");
				 //  return false;
				 // }

				 if (common.isEmptyStr(app_name)) {
                     var message = common.getLabel("lbl_check_app_name");
					 alert(message);
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

				 if ((!check_app_version_code_str) && (platformType != 'Windows')) {
                     var message = common.getLabel("lbl_app_version_code_form");
					 alert(message);
					 return false;
				 }

				 if (common.checkAllInputText("CHECK_INPUT_TYPE_KOR", app_version_code)) {
                     var message = common.getLabel("lbl_app_version_code_kor");
					 alert(message);
					 return false;
				 }

				 if (common.checkAllInputText("CHECK_INPUT_TYPE_ENG", app_version_code)) {
                     var message = common.getLabel("lbl_app_version_code_eng");
					 alert(message);
					 return false;
				 }


				 if (platformType == 'Android') {

				 } else {

				 }

				 return true;
			 };

			 scwin.webSocketCallback = function (obj) {

				 switch (obj.MsgType) {
					 case "HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO_FROM_HEADQUATER" :
						 scwin.setBranchGitPullStatus(obj);
						 break;
					 case "HV_MSG_PROJECT_APP_CONFIG_IMAGE_LIST_INFO_FROM_HEADQUATER" :
						 scwin.setImageBase64CallBack(obj);
						 break;
					 case "HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO_FROM_HEADQUATER" :
						 scwin.setBuilderTemplateServerConfigStatus(obj);
						 break;
					 case "HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO_FROM_HEADQUATER" :
						 scwin.setBuilderAppConfigStatus(obj);
						 break;
					 default :
						 break;
				 }
			 };

			 scwin.setBranchGitPullStatus = function (obj) {

				 switch (obj.gitStatus) {
					 case "GITPULL":
                         var message = common.getLabel("lbl_git_pull");
						 WebSquare.layer.showProcessMessage(message); //Git Pull
						 break;
					 case "DONE":
						 WebSquare.layer.hideProcessMessage();
                         var message = common.getLabel("lbl_project_setting_step02_bak_js_git_done");
						 alert(message); //프로젝트 업데이트 완료
						 break;
					 default :
						 break;
				 }

			 };

			 scwin.setBuilderTemplateServerConfigStatus = function(obj) {

				 switch (obj.message) {
					 case "SEARCHING":
						var message = common.getLabel("lbl_app_config_list");
						 WebSquare.layer.showProcessMessage(message);
						 break;
					 case "CONFIGUPDATE" :
                         var message = common.getLabel("lbl_project_setting_step02_bak_js_server_config_update");
						 WebSquare.layer.showProcessMessage(message); //Server Config Update
						 break;
					 case "DONE" :
						 WebSquare.layer.hideProcessMessage();
                         var message = common.getLabel("lbl_project_setting_step02_bak_js_updated_hybrid_server");
						 alert(message); //W-Hybrid Server 설정 수정 완료
						 break;
					 case "SUCCESSFUL":
						 WebSquare.layer.hideProcessMessage();
						 scwin.selectBuildSettingData(scwin.platformType, obj);
						 break;
					 default :
						 break;
				 }
			 };

			 scwin.setBuilderAppConfigStatus = function(obj) {

				 switch (obj.message) {
					 case "SEARCHING":
                         var message = common.getLabel("lbl_app_config_list");
						 WebSquare.layer.showProcessMessage(message);
						 break;
					 case "CONFIGUPDATE" :
                         var message = common.getLabel("lbl_project_setting_step02_bak_js_server_config_update");
						 WebSquare.layer.showProcessMessage(message); //Server Config Update
						 break;
					 case "DONE" :
						 WebSquare.layer.hideProcessMessage();
						 break;
					 case "SUCCESSFUL":
						 WebSquare.layer.hideProcessMessage();
						 scwin.selectBuildAppConfigListData(scwin.platformType, obj);

						 break;
					 default :
						 break;
				 }
			 };

			 scwin.selectBuildSettingData = function (type, obj) {
				 var build_setting_json = $p.parent().dtl_build_setting_step2.getAllJSON();

				 $p.parent().scwin.getResultAppConfigData = obj;
				 if (type == 'Android') {

					 step2_android_input_app_id.setValue(obj.resultServerConfigListObj.ApplicationID);
					 step2_android_input_app_name.setValue(obj.resultServerConfigListObj.AppName);
					 step2_android_input_app_version.setValue(obj.resultServerConfigListObj.AppVersion);
					 step2_android_input_app_version_code.setValue(obj.resultServerConfigListObj.AppVersionCode);
					 step2_input_packagename.setValue(build_setting_json[0].package_name);
				 } else if (type == "Windows") {
					 step2_windows_input_app_id.setValue(buildsetting[0].app_id);
					 step2_windows_input_app_name.setValue(buildsetting[0].app_name);
					 step2_windows_input_app_version.setValue(buildsetting[0].app_version);
					 step2_windows_input_app_version_code.setValue(buildsetting[0].app_version_code);
				 } else {

					 step2_input_projectname.setValue(build_setting_json[0].package_name);

					 var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();
					 var build_setting_json = $p.parent().dtl_build_setting_step2.getAllJSON();
					 // platform_type

					 scwin.platformType = localStorage.getItem("_platform_");

					 // app config 조회 호출 전 파라미터 값 설정
					 var data = {};

					 data.build_id = buildproj_json[0].project_id;
					 data.workspace_id = buildproj_json[0].workspace_id;
					 data.project_name = buildproj_json[0].project_name;
					 data.project_dir_path = buildproj_json[0].project_dir_path;

					 data.platform = type;


				 }

			 };

			 scwin.selectBuildAppConfigListData = function (type, obj) {
				 if (type == 'iOS') {
					 step2_ios_input_app_id.setValue(obj.resultAppConfigListObj.Release.bundleIdentifier);
					 step2_ios_input_app_name.setValue(obj.resultAppConfigListObj.Release.productName);
					 step2_ios_input_app_version.setValue(obj.resultAppConfigListObj.Release.version);
					 step2_ios_input_app_version_code.setValue(obj.resultAppConfigListObj.Release.build);
				 }

			 };

			 scwin.selectiOSBuildAppConfigListData = function (obj) {

				var build_setting_json = $p.parent().dtl_build_setting_step2.getAllJSON();

			    step2_input_projectname.setValue(build_setting_json[0].package_name);

				step2_ios_input_app_id.setValue(obj.appconfig.Release.bundleIdentifier);
				step2_ios_input_app_name.setValue(obj.appconfig.Release.productName);
				step2_ios_input_app_version.setValue(obj.appconfig.Release.version);
				step2_ios_input_app_version_code.setValue(obj.appconfig.Release.build);


			 };

			 scwin.saveStep2Data = function () {

				 var saveStep2DataVal = $p.parent().dtl_build_setting_step2.getAllJSON();
				 var saveSteop2DataTemp = $p.parent().dtl_build_setting_step2_temp.getAllJSON();

				 var platformType = localStorage.getItem("_platform_");

				 var id;
				 var project_id;
				 var app_id;
				 var app_name;
				 var app_version;
				 var app_version_code;
				 var min_target_version;
				 var package_name;
				 var server_URL;
				 var all_keyfile_path;
				 var all_keyfile_password;
				 var provisioning_profile_path;
				 var key_alias;
				 var key_password;

				 var data = {};

				 if (platformType == 'Android') {
					 id = saveStep2DataVal[0].project_setting_id; // localStorage.getItem("build_setting2_pid");
					 project_id = saveStep2DataVal[0].project_id;  // localStorage.getItem("__buildproject_id__");
					 app_id = step2_android_input_app_id.getValue();
					 app_name = step2_android_input_app_name.getValue();
					 app_version = step2_android_input_app_version.getValue();
					 app_version_code = step2_android_input_app_version_code.getValue();
					 min_target_version = step2_select_minsdk_version.getValue();
					 package_name = step2_input_packagename.getValue();
					 server_URL = saveStep2DataVal[0].server_URL;

					 data.project_setting_id = parseInt(id);
					 data.project_id = parseInt(project_id); // build project insert 이후 id 값 조회....
					 //data.platform_type = platformType; // 화면단에 platform 값 호출
					 //data.target = "server";
					 data.app_id = app_id;
					 data.app_name = app_name;
					 data.app_version = app_version;
					 data.app_version_code = app_version_code;
					 data.min_target_version = min_target_version;
					 data.package_name = package_name;
					 data.server_URL = server_URL;
					 data.all_keyfile_path = all_keyfile_path;
					 data.all_keyfile_password = all_keyfile_password;
					 data.key_alias = key_alias;
					 data.key_password = key_password;
					 data.test_url = server_URL;

				 } else {
					 id = saveStep2DataVal[0].project_setting_id; // localStorage.getItem("build_setting2_pid");
					 project_id = saveStep2DataVal[0].project_id; // localStorage.getItem("__buildproject_id__");
					 app_id = step2_ios_input_app_id.getValue();
					 app_name = step2_ios_input_app_name.getValue();
					 app_version = step2_ios_input_app_version.getValue();
					 app_version_code = step2_ios_input_app_version_code.getValue();
					 min_target_version = step2_select_target_version.getValue();
					 package_name = step2_input_projectname.getValue();
					 server_URL = saveStep2DataVal[0].server_URL;
					 // provisioning_profile_path = step2_ios_input_provisioning_profile_path.getValue();

					 data.project_setting_id = parseInt(id);
					 data.project_id = parseInt(project_id); // build project insert 이후 id 값 조회....
					 //data.platform_type = platformType;
					 //data.target = "server";
					 data.app_id = app_id;
					 data.app_name = app_name;
					 data.app_version = app_version;
					 data.app_version_code = app_version_code;
					 data.min_target_version = min_target_version;
					 data.package_name = package_name;
					 data.server_URL = server_URL;
					 data.provisioning_profile_path = provisioning_profile_path;
					 data.test_url = server_URL;

				 }

				 if (saveSteop2DataTemp[0].app_name == app_name && saveSteop2DataTemp[0].app_id == app_id && saveSteop2DataTemp[0].app_version == app_version
						 && saveSteop2DataTemp[0].app_version_code == app_version_code && saveSteop2DataTemp[0].min_target_version == min_target_version && saveSteop2DataTemp[0].package_name == package_name) {  // 같을경우 ajax 처리 하지 않기

				 } else {
					 $p.parent().dtl_build_setting_step2.setJSON([data]);

					 $.ajax({
						 url: "/manager/build/setting/update/projectDetail",
						 type: "POST",
						 accept: "application/json",
						 contentType: "application/json; charset=utf-8",
						 data: JSON.stringify(data),
						 dataType: "json",
						 success: function (r, status) {
							 if (status === "success") {
								 //console.log("update success ");

							 }

						 }
						 , error: function (request, status, error) {
							 alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");
						 }
					 });
				 }


			 };

			 scwin.setImageBase64CallBack = function (obj) {

				 // 29, 40, 57, 58, 60, 76, 80, 114, 120, 152, 167, 180, 1024
				 var base64SetVar = "data:image/jpeg;base64,";
				 var platform = obj.imageList.platform;

				 if (platform == "Android") {
					 var iconlogo_ImageList = obj.imageList.imageurl_logo;

					 icon_xhdpi.setSrc(base64SetVar + iconlogo_ImageList);

				 } else if (platform == "iOS") {
					 var icon29_ImageList = obj.imageList.imageurl_29;
					 var icon40_ImageList = obj.imageList.imageurl_40;
					 var icon57_ImageList = obj.imageList.imageurl_57;
					 var icon58_ImageList = obj.imageList.imageurl_58;
					 var icon60_ImageList = obj.imageList.imageurl_60;
					 var icon80_ImageList = obj.imageList.imageurl_80;
					 var icon87_ImageList = obj.imageList.imageurl_87;
					 var icon114_ImageList = obj.imageList.imageurl_114;
					 var icon120_ImageList = obj.imageList.imageurl_120;
					 var icon180_ImageList = obj.imageList.imageurl_180;
					 var icon1024_ImageList = obj.imageList.imageurl_1024;

					 icon29.setSrc(base64SetVar + icon29_ImageList);
					 icon40.setSrc(base64SetVar + icon40_ImageList);
					 //icon57.setSrc(base64SetVar + icon57_ImageList);
					 icon58.setSrc(base64SetVar + icon58_ImageList);
					 icon60.setSrc(base64SetVar + icon60_ImageList);
					 icon80.setSrc(base64SetVar + icon80_ImageList);
					 icon87.setSrc(base64SetVar + icon87_ImageList);
					 //icon114.setSrc(base64SetVar + icon114_ImageList);
					 icon120.setSrc(base64SetVar + icon120_ImageList);
					 icon180.setSrc(base64SetVar + icon180_ImageList);
					 icon1024.setSrc(base64SetVar + icon1024_ImageList);
				 }


			 };

			 scwin.appIconImageLoad = function () {

				 // start 전송 방식
				 var saveStep2DataVal = $p.parent().dtl_build_setting_step2.getAllJSON();

				 var project_id = saveStep2DataVal[0].project_id; // localStorage.getItem("__project_id__");
				 var workspace_id = localStorage.getItem("__workspace_id__");

				 var data = {};
				 data.project_id = project_id;
				 data.workspace_id = workspace_id;

				 var options = {};
				 // post 로 변경하기
				 options.action = "/manager/build/setting/search/appIcon";
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.requestData = JSON.stringify(data);
				 options.method = "POST";

				 options.success = function (e) {
					 if (e.responseStatusCode === 200 || e.responseStatusCode === 201) {
						 if (data != null) {

						 }
					 }

				 };

				 options.error = function (e) {
					 if (e.responseStatusCode === 500) {

						 // scwin.build_project_name_yn = true;
					 } else {
						 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);
					 }

				 };

				 $p.ajax(options);

			 };

			 function arrangeZip(path) {

				 var promise = [];
				 // var zip = new JSZip();
				 var imgExt = ['png', 'jpg', 'jpeg'];
				 var htmlExt = ['xhtml', 'html'];
				 var MACdir = ['__MACOSX/'];

				 return JSZip.loadAsync(path).then(function (data) {

					 data.forEach(function (relpath, file) {
						 var ext = file.name.split('.');
						 ext = ext[ext.length - 1];

						 var contentType = '';
						 var dataType = '';

						 if (!imgExt.indexOf(ext) && !/^__MACOSX/.test(file.name)) {
							 //console.log(" zip into png?? ");
							 dataType = 'base64';
							 contentType = 'image/' + ext;
							 promise.push(file.async(dataType).then(function (cont) {
								 return new Promise(function (resolve, reject) {
									 resolve({cont: cont, name: file.name, type: 'image'});
								 });
							 }, function (err) {
								 reject(err);
							 }));
						 }

					 });
					 return Promise.all(promise);
				 });
			 };

			 function b64toBlob(b64Data, contentType, sliceSize) {
				 contentType = contentType || '';
				 sliceSize = sliceSize || 512;

				 var byteCharacters = atob(b64Data);
				 var byteArrays = [];

				 for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
					 var slice = byteCharacters.slice(offset, offset + sliceSize);

					 var byteNumbers = new Array(slice.length);
					 for (var i = 0; i < slice.length; i++) {
						 byteNumbers[i] = slice.charCodeAt(i);
					 }

					 var byteArray = new Uint8Array(byteNumbers);

					 byteArrays.push(byteArray);
				 }

				 var blob = new Blob(byteArrays, {type: contentType});
				 return blob;
			 };

			 // 앱아이콘 업로드
			 scwin.icons_zip_upload_send = function () {


				 var platform = localStorage.getItem("_platform_");
				 var formData = new FormData();
				 var step1DataList = $p.parent().dtl_build_setting_step1.getAllJSON();
				 var ZipIconfile = "";

				 if (platform == "Android") {
					 ZipIconfile = document.getElementById("step2_android_input_icons_path");
					 formData.append("file", ZipIconfile.files[0]);

				 } else {
					 ZipIconfile = document.getElementById("step2_ios_input_icons_path");
					 formData.append("file", ZipIconfile.files[0]);
				 }

				 formData.append("branch_id", step1DataList[0].builder_id);
				 formData.append("build_id", step1DataList[0].project_id);
				 formData.append("workspaceID", step1DataList[0].workspace_id);
				 formData.append("projectID", step1DataList[0].project_id);
				 formData.append("projectname", step1DataList[0].project_name);
				 formData.append("projectDirName", step1DataList[0].project_dir_path);
				 formData.append("platform", platform);

				 $.ajax({
					 url: "/manager/build/setting/upload/updateIcon",
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
							 var message = common.getLabel("lbl_img_upload_complete");
							 alert(message); //이미지 업로드 완료 되었습니다
							 scwin.app_icon_upload_check_yn = true;

						 }

					 }
					 , error: function (request, status, error) {
						 if (request.status == 200) {
                             var message = common.getLabel("lbl_img_upload_complete");
							 alert(message); //이미지 업로드 완료 되었습니다
							 scwin.app_icon_upload_check_yn = true;
						 }

						 // alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");
					 }
				 });


			 };

			 scwin.btn_all_save_next_onclick = function(e){
				 if (scwin.checkData()) {
					 scwin.saveStep2Data();
					 $p.parent().scwin.selected_step(3);
				 }
			 };

			 scwin.btn_Android_prev_onclick = function (e) {
				 $p.parent().scwin.selected_step(1);
			 };

			 scwin.btn_Android_next_onclick = function (e) {
				 // if (confirm('현재 변경사항 저장하시겠습니까?')) {
					 if (scwin.checkData()) {
						 // scwin.saveStep2Data();
						 $p.parent().scwin.selected_step(3);
					 }
				 // } else {
					//  $p.parent().scwin.selected_step(3);
				 // }

			 };

			 scwin.btn_Windows_prev_onclick = function (e) {
				 $p.parent().scwin.selected_step(1);
			 };

			 scwin.btn_Windows_next_onclick = function (e) {
				 $p.parent().scwin.selected_step(3);
			 };

			 scwin.btn_iOS_prev_onclick = function (e) {
				 $p.parent().scwin.selected_step(1);
			 };

			 scwin.btn_iOS_next_onclick = function (e) {
				 // if (confirm('현재 변경사항 저장하시겠습니까?')) {
					 if (scwin.checkData()) {
						 // scwin.saveStep2Data();
						 $p.parent().scwin.selected_step(3);
					 }
				 // } else {
					//  $p.parent().scwin.selected_step(3);
				 // }
			 };

			 scwin.btn_keychain_upload_onclick = function (e) {
				 //input_keychain.setValue("");
				 var message_uploading = common.getLabel("lbl_uploading");
				 WebSquare.layer.showProcessMessage(message_uploading); //uploading
				 setTimeout(function () {
                     var message_success = common.getLabel("lbl_upload_success");
					 alert(message_success); //upload success
					 WebSquare.layer.hideProcessMessage();
				 }, 1000);
			 };

			 scwin.btn_icons_upload_onclick = function (e) {
				 //input_icons.setValue("");
				 var message_uploading = common.getLabel("lbl_uploading");
				 WebSquare.layer.showProcessMessage(message_uploading); //uploading

				 scwin.icons_zip_upload_send();
				 setTimeout(function () {
					 // alert("upload success");
					 WebSquare.layer.hideProcessMessage();

				 }, 1500);

			 };

			 scwin.btn_ios_provisioning_onclick = function (e) {
				 var message_uploading = common.getLabel("lbl_uploading");
				 WebSquare.layer.showProcessMessage(message_uploading); //uploading

				 setTimeout(function () {
					 var message_success = common.getLabel("lbl_upload_success");
					 alert(message_success); //upload success
					 WebSquare.layer.hideProcessMessage();
				 }, 1000);
			 };

			 scwin.btn_ios_icon_upload_onclick = function (e) {
				 var message_uploading = common.getLabel("lbl_uploading");
				 WebSquare.layer.showProcessMessage(message_uploading); //uploading
				 setTimeout(function () {
					 var message_success = common.getLabel("lbl_upload_success");
					 alert(message_success); //upload success
					 WebSquare.layer.hideProcessMessage();
					 icon29.setSrc("/images/temp2/29.png");
					 icon40.setSrc("/images/temp2/40.png");
					 //icon57.setSrc("/images/temp2/57.png");
					 icon58.setSrc("/images/temp2/58.png");
					 icon60.setSrc("/images/temp2/60.png");
					 icon80.setSrc("/images/temp2/80.png");
					 icon87.setSrc("/images/temp2/87.png");
					 //icon114.setSrc("/images/temp2/114.png");
					 icon120.setSrc("/images/temp2/120.png");
					 icon180.setSrc("/images/temp2/180.png");
					 icon1024.setSrc("/images/temp2/1024.png");
				 }, 2500);
			 };

			 scwin.btn_btn_vcspull_onclick = function (e) {

				 var step1DataList = $p.parent().dtl_build_setting_step1.getAllJSON();


				 var data = {};
				 data.build_id = step1DataList[0].id;
				 data.platform = step1DataList[0].platform;   // localStorage.getItem("_platform_");
				 data.workspace_name = localStorage.getItem("__workspace_name__");
				 data.project_name = step1DataList[0].project_name;


				 $.ajax({
					 url: "/api/buildsetting/gitpull",
					 type: "post",
					 accept: "application/json",
					 contentType: "application/json; charset=utf-8",
					 data: JSON.stringify(data),
					 dataType: "json",
					 success: function (r, status) {
						 // alert(r);
						 // alert(status);
						 if (status === "success") {
							 // console.log("response[0].id " +response[0].historyCnt);
						 }

					 },
					 error: function (request, status, error) {
						 alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					 }

				 });

			 };

			 scwin.getSigningKeySettingInfoToDebugProfile = function (signingkey_id) {
				 var options = {};

				 options.action = "/api/signingkeysetting/selectBySigningKey_id/" + parseInt(signingkey_id);
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {


						 step2_select_ios_debug_profile_settingbox.addItem(data.signingkey_id, data.signingkey_name);


					 } else {

					 }
				 };

				 options.error = function (e) {
					 // console.log(e.responseJSON);
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 scwin.getSigningKeySettingInfoToReleaseProfile = function (signingkey_id) {
				 var options = {};

				 options.action = "/api/signingkeysetting/selectBySigningKey_id/" + parseInt(signingkey_id);
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {


						 step2_select_ios_release_profile_settingbox.addItem(data.signingkey_id, data.signingkey_name);


					 } else {

					 }
				 };

				 options.error = function (e) {
					 // console.log(e.responseJSON);
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 scwin.getHybridServerConfig = function (data){

				 var options = {};
				 options.action = "/manager/build/setting/search/serverConfig";
				 options.mode = "synchronous";
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
					 alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n");
					 //$p.url("/login.xml");
				 };

				 $p.ajax( options );

			 };

			 // 현재 사용하지 않는 메소드
			 scwin.getiOSAppConfigInfo = function (data) {
				 var options = {};
				 options.action = "/manager/build/setting/search/getiOSAppConfigInfo";
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
					 alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n");
					 //$p.url("/login.xml");
				 };

				 $p.ajax( options );
			 };

			 function changeToolTipContentSettingStep2 (componentId, label) {
				 switch (componentId) {
					 case "wfm_main_wfm_project_setting_step2_android_appid_tooltip":
                         var message = common.getLabel("lbl_android_appid");
						 return message
					 case "wfm_main_wfm_project_setting_step2_android_appname_tooltip":
                         var message = common.getLabel("lbl_displayed_app_name");
						 return message
					 case "wfm_main_wfm_project_setting_step2_android_appversion_tooltip":
                         var message = common.getLabel("lbl_app_version");
						 return message
					 case "wfm_main_wfm_project_setting_step2_android_appversioncode_tooltip":
                         var message = common.getLabel("lbl_app_version_code");
						 return message
					 case "wfm_main_wfm_project_setting_step2_android_minsdk_tooltip":
                         var message = common.getLabel("lbl_android_min_os_version");
						 return message
					 case "wfm_main_wfm_project_setting_step2_ios_appid_tooltip":
                         var message = common.getLabel("lbl_ios_appid");
						 return message
					 case "wfm_main_wfm_project_setting_step2_ios_appname_tooltip":
                         var message = common.getLabel("lbl_ios_app_name");
						 return message
					 case "wfm_main_wfm_project_setting_step2_ios_appversion_tooltip":
                         var message = common.getLabel("lbl_ios_app_version");
						 return message
					 case "wfm_main_wfm_project_setting_step2_ios_appversioncode_tooltip":
                         var message = common.getLabel("lbl_ios_app_version_code");
						 return message
					 case "wfm_main_wfm_project_setting_step2_ios_minsdk_tooltip":
                         var message = common.getLabel("lbl_ios_min_os_version");
						 return message
					 default:
						 return ""
				 }
			 };


			 
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box android',id:'grp_Android',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'dfbox'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'step2_fr_grp_proj_save_android',label:'',style:'',useLocale:'true',localeRef:'lbl_android_app_default_info'},E:[{T:3,text:'\n				>'}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'txt_chk',useLocale:'true',localeRef:'lbl_project_setting_step02_bak_saveQuestion'}},{T:1,N:'xf:trigger',A:{id:'btn_vcspull_1',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_all_save_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appid_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_appid'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step2_android_input_app_id',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appname_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step2_android_input_app_name',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_setting_step02_bak_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appversion_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step2_android_input_app_version',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_appversioncode_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version_code'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'step2_android_input_app_version_code',style:'',adjustMaxLength:'false'}}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_package_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_use_package_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_input_packagename',style:'',initValue:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'android_minsdk_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_min_os_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step2_select_minsdk_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'21'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'21'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'icon_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step02_bak_icon'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'input',A:{id:'step2_android_input_icons_path',style:'',type:'file'}},{T:1,N:'xf:trigger',A:{class:'btn_cm',id:'',style:'',type:'button','ev:onclick':'scwin.btn_icons_upload_onclick',useLocale:'true',localeRef:'lbl_upload'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'viewer',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w48',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon_mdpi',src:'/images/contents/img_view_48x48.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w48'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w72',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon_hdpi',src:'/images/contents/img_view_72x72.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w72'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w96',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon_xhdpi',src:'/images/contents/img_view_96x96.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w96'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_Android_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_Android_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_Android_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_Android_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box windows',id:'grp_Windows',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_windows_app_default_info'}},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_name'}},{T:1,N:'xf:input',A:{id:'step2_windows_input_app_id',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project'}},{T:1,N:'xf:input',A:{id:'step2_windows_input_app_name',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_version'}},{T:1,N:'xf:input',A:{id:'step2_windows_input_app_version',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_op_mode'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'step2_windows_input_app_version_code',style:'',adjustMaxLength:'false'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'icon_box',id:'',style:''}},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_windows_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_Windows_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_windows_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_Windows_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box iOS',id:'grp_iOS',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_app_default_info'}},{T:1,N:'xf:group',A:{class:'fr',id:'step2_fr_grp_proj_save_ios',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'txt_chk',useLocale:'true',localeRef:'lbl_project_setting_step02_bak_saveQuestion'}},{T:1,N:'xf:trigger',A:{id:'btn_vcspull_2',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_all_save_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'form_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appid_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_appid'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_id',style:''}}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appname_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_name',style:''}}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appversion_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_version',style:''}}]},{T:1,N:'xf:group',A:{class:'tooltip_box',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_appversioncode_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_app_version_code'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_ios_input_app_version_code',style:''}}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_xcode_project_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_ios_project_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'step2_input_projectname',style:'',initValue:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'ios_minsdk_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep2',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step02_bak_authPw'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step2_select_target_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:3,text:'\n								tooltip_box								'},{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'9.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'9.0'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'icon_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step02_bak_icon'}},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'input',A:{id:'step2_ios_input_icons_path',style:'',type:'file'}},{T:1,N:'xf:trigger',A:{class:'btn_cm',id:'btn_ios_icon_upload',style:'',type:'button','ev:onclick':'scwin.btn_icons_upload_onclick',useLocale:'true',localeRef:'lbl_upload'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'viewer',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w36',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon29',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w29'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w48',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon40',src:'/images/contents/img_view_48x48.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w40'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w72',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon58',src:'/images/contents/img_view_72x72.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w58'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w72',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon60',src:'/images/contents/img_view_72x72.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w60'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w96',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon80',src:'/images/contents/img_view_96x96.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w80'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w96',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon87',src:'/images/contents/img_view_96x96.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w87'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w144',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon120',src:'/images/contents/img_view_144x144.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w120'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w144',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon180',src:'/images/contents/img_view_144x144.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w180'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'w192',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{id:'icon1024',src:'/images/contents/img_view_192x192.png',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w1024'}},{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_iOS_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_iOS_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_iOS_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_iOS_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})