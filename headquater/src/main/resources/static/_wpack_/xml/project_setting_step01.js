/*amd /xml/project_setting_step01.xml 25552 b3e9c9070ee10bbc9a8bab5f50bae78a02ea1820d40d61c42c848910c9a9a3ee */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/stomp.min.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/sockjs.min.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.onpageload = function () {
				 common.setScopeObj(scwin);
				 $p.parent().scwin.selected_step(1);

				 scwin.initPage();

			 };

			 scwin.onpageunload = function () {

			 };

			 scwin.initPage = function () {
				 var build_project_json = $p.parent().dtl_build_setting_step1.getAllJSON();
				 // 환경설정 List 조회

				 step1_input_projectname.setDisabled(true);
				 step1_select_platform.setDisabled(true);
				 step1_select_template.setDisabled(true);
				 step1_select_branch_settingbox.setDisabled(true);
				 step1_select_vcs_settingbox.setDisabled(true);
				 step1_select_ftp_settingbox.setDisabled(true);
				 step1_select_platformlanguage_settingbox.setDisabled(true);
				 step1_input_project_dir_path_name.setDisabled(true);
				 step1_select_signingKey_settingbox.setDisabled(true);
				 step1_input_project_create_date.setDisabled(true);
				 step1_input_project_updated_date.setDisabled(true);

				 var update_yn = localStorage.getItem("__update_yn__");

				 if (update_yn == "1") {

				 } else {
					 fr_grp_proj_save.hide();
				 }

				 // btn_vcspull_1 해당 버튼 visible 처리
				 scwin.selectBuildProject(build_project_json);
			 };

			 scwin.checkData = function () {

				 var description = step1_txtarea_desc.getValue(); // 필수

				 if (common.isEmptyStr(description)) {
					 return false;
				 }

				 return true;
			 };

			 scwin.selectBuildProject = function (build_project_json) {
				 step1_input_projectname.setValue(build_project_json[0].project_name);
				 step1_select_platform.setText(build_project_json[0].platform, false);
				 step1_select_template.addItem(0, build_project_json[0].template_version);
				 step1_input_project_dir_path_name.setValue(build_project_json[0].project_dir_path);
				 step1_txtarea_desc.setValue(build_project_json[0].description);
				 step1_select_ftp_settingbox.setValue(build_project_json[0].ftp_id, false);
				 step1_select_platformlanguage_settingbox.addItem(0, build_project_json[0].platform_language);
				 step1_input_project_create_date.setValue(build_project_json[0].created_date.replace(/T/g, ' '));
				 step1_input_project_updated_date.setValue(build_project_json[0].updated_date.replace(/T/g, ' '));
				 scwin.getBranchSettingInfo(build_project_json[0].builder_id);

				 //
				 $p.parent().scwin.txt_project_all_step_platform = build_project_json[0].platform;

				 if (build_project_json[0].vcs_id == "") {

				 } else {
					 scwin.getVCSSettingInfo(build_project_json[0].vcs_id);
				 }

				 scwin.getFTPSettingInfo(build_project_json[0].ftp_id);

				 if (build_project_json[0].platform == "Android") {
					 scwin.getSigningKeySettingInfo(build_project_json[0].key_id);
				 } else {
					 scwin.getiOSKeySettingInfo(build_project_json[0].key_id);
				 }

				 // project setting localStorage data set!
				 localStorage.setItem("buildProjectName", build_project_json[0].project_name);
				 localStorage.setItem("buildPlatform", build_project_json[0].platform);
				 localStorage.setItem("projectsetting_pid", build_project_json[0].project_id);
				 localStorage.setItem("_platform_", step1_select_platform.getValue());

				 // step 2 app config, step 3 server config list set
				 if (build_project_json[0].product_type == "WMatrix") {
					 if (build_project_json[0].platform == "Android") {
						 scwin.getAndroidConfig();
					 } else {
						 scwin.getiOSAllGetInformation();
					 }
				 } else {
					 $p.parent().grp_step5.hide();
					 $p.parent().grp_step4.hide();
					 $p.parent().grp_step2.hide();

					 tep1_select_template_group.hide();
					 step1_select_platformlanguage_settingbox_group.hide();
					 btn_next.hide();
				 }
			 };

			 scwin.saveStep1Data = function () {
				 var project_name = step1_input_projectname.getValue();
				 var platform = step1_select_platform.getValue();
				 var template_version = step1_select_template.getValue();
				 // var build_server_url = step1_input_buildserver.getValue()a

				 var project_dir_path = step1_input_project_dir_path_name.getValue();
				 var description = step1_txtarea_desc.getValue();

				 // workspace id 값 세팅
				 var local_workspace_id = localStorage.getItem("__workspace_id__"); // change to data list
				 var local_buildproject_id = localStorage.getItem("projectsetting_pid"); // change to data list

				 var data = {};
				 data.project_id = parseInt(local_buildproject_id);
				 data.workspace_id = parseInt(local_workspace_id);
				 data.project_name = project_name;
				 data.platform = platform;
				 data.template_version = template_version;
				 data.project_dir_path = project_dir_path;
				 data.description = description;
				 data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.

				 localStorage.setItem("__project_name__", data.project_name);
				 localStorage.setItem("__project_id__", data.project_id);
				 localStorage.setItem("__workspace_id__", data.workspace_id);

				 $p.parent().dtl_build_setting_step1.setJSON([data]);

				 $.ajax({
					 url: "/manager/build/project/update/projectSetting",
					 type: "POST",
					 accept: "application/json",
					 contentType: "application/json; charset=utf-8",
					 data: JSON.stringify(data),
					 dataType: "json",
					 success: function (r, status) {
						 if (status === "success") {
							 //console.log("status success ");
							 alert("저장 완료 되었습니다.");
							 // project_name localStorage setting
						 }

					 }
					 , error: function (request, status, error) {
						 alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					 }
				 });

			 };

			 scwin.getPlatformProgramLanguageInfo = function (role_code_id) {

				 var options = {};

				 options.action = "/manager/build/project/search/programLanguage/" + role_code_id;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 step1_select_platformlanguage_settingbox.addItem(data.role_code_id, data.role_code_name);
					 } else {

					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 // dlt_branch_setting_selectbox set select box
			 scwin.getBranchSettingInfo = function (builder_id) {

				 var options = {};

				 options.action = "/manager/branchSetting/selectBySelectBoxListBranchId/" + parseInt(builder_id);
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 step1_select_branch_settingbox.addItem(data.builder_id, data.builder_name);
					 } else {

					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 // step1_select_vcs_settingbox set select box
			 scwin.getVCSSettingInfo = function (vcs_id) {

				 var options = {};

				 options.action = "/manager/vcs/search/profileIdBySelectBox/" + parseInt(vcs_id);
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 step1_select_vcs_settingbox.addItem(data.vcs_id, data.vcs_name);


					 } else {

					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 // step1_select_ftp_settingbox set select box
			 scwin.getFTPSettingInfo = function (ftp_id) {

				 var options = {};

				 options.action = "/manager/ftp/setting/search/selectBoxList/" + parseInt(ftp_id);
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 step1_select_ftp_settingbox.addItem(data.ftp_id, data.ftp_name);

					 } else {

					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 // android key id 기준 조회
			 scwin.getSigningKeySettingInfo = function (signingkey_id) {
				 var options = {};

				 options.action = "/manager/mCert/android/search/profile/" + parseInt(signingkey_id);
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 step1_select_signingKey_settingbox.addItem(data.key_id, data.key_name);


					 } else {

					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 // ios key id 기준 조회
			 scwin.getiOSKeySettingInfo = function (key_id) {
				 var options = {};

				 options.action = "/manager/mCert/iOS/search/profile/" + parseInt(key_id);
				 options.mode = "synchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {

						 step1_select_signingKey_settingbox.addItem(data.key_id, data.key_name);


					 } else {

					 }
				 };

				 options.error = function (e) {
					 alert("code:" + e.status + "\n" + "message:" + e.responseJSON + "\n");

				 };

				 $p.ajax(options);

			 };

			 scwin.getAndroidConfig = function () {

				 var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();
				 var data = {};

				 // var whive_session = sessionStorage.getItem("__whybrid_session__");
				 // whive_session = JSON.parse(whive_session);

				 // ajax data set
				 // data.hqKey = whive_session.user_login_id;
				 // data.domainID = whive_session.domain_id;
				 // data.userID = whive_session.id;
				 data.build_id = buildproj_json[0].project_id;
				 data.workspace_id = buildproj_json[0].workspace_id;
				 data.project_name = buildproj_json[0].project_name;
				 data.project_dir_path = buildproj_json[0].project_dir_path;

				 data.product_type = buildproj_json[0].product_type;
				 data.platform = step1_select_platform.getValue();

				 var options = {};
				 options.action = "/manager/build/setting/search/getAndroidAllGetConfig";
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
					 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");
				 };

				 $p.ajax(options);


			 };

			 scwin.getiOSAllGetInformation = function () {

				 // getinformation 조회 호출 전 파라미터 값 설정
				 var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();
				 var data = {};

				 // var whive_session = sessionStorage.getItem("__whybrid_session__");
				 // whive_session = JSON.parse(whive_session);

				 // ajax data set
				 // data.hqKey = whive_session.user_login_id;
				 // data.domainID = whive_session.domain_id;
				 // data.userID = whive_session.id;
				 data.build_id = buildproj_json[0].project_id;
				 data.workspace_id = buildproj_json[0].workspace_id;
				 data.project_name = buildproj_json[0].project_name;
				 data.project_dir_path = buildproj_json[0].project_dir_path;

				 data.product_type = buildproj_json[0].product_type;
				 data.platform = step1_select_platform.getValue();

				 var options = {};
				 options.action = "/manager/build/setting/search/getiOSGetInformation";
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
					 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");
				 };

				 $p.ajax(options);

			 };

			 scwin.btn_prev_onclick = function (e) {

			 };

			 scwin.btn_next_onclick = function (e) {
				 // 저장 플레그 추가
				 var textareaVal = step1_txtarea_desc.getValue();
				 if (textareaVal == "") {

				 } else {
					 scwin.saveStep1Data();
				 }
				 var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();

				 if (buildproj_json[0].product_type == 'WMatrix') {
					 $p.parent().scwin.selected_step(2);
				 }
			 };

			 scwin.webSocketCallback = function (obj) {

				 switch (obj.MsgType) {
					 case "HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO_FROM_HEADQUATER" :
						 scwin.setBuilderAllAppConfigStatus(obj);
						 break;
					 case "HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO_FROM_HEADQUATER" :
						 scwin.setBuilderAllAppConfigStatus(obj);
						 break;
					 default :
						 break;
				 }
			 };

			 scwin.setBuilderAllAppConfigStatus = function (obj) {

				 switch (obj.message) {
					 case "SEARCHING":
						 var message = common.getLabel("lbl_app_config_list");
						 WebSquare.layer.showProcessMessage(message);
						 break;
					 case "CONFIGUPDATE" :
						 var message = common.getLabel("lbl_project_setting_step01_js_server_config_update");
						 WebSquare.layer.showProcessMessage(message); //Server Config Update
						 break;
					 case "DONE" :
						 WebSquare.layer.hideProcessMessage();
						 break;
					 case "SUCCESSFUL":
						 WebSquare.layer.hideProcessMessage();
						 scwin.selectBuildAppConfigListData(step1_select_platform.getValue(), obj);

						 break;
					 default :
						 break;
				 }

			 };

			 scwin.setBuilderAppConfigStatus = function (obj) {

				 switch (obj.message) {
					 case "SEARCHING":
						 var message = common.getLabel("lbl_app_config_list");
						 WebSquare.layer.showProcessMessage(message);
						 break;
					 case "CONFIGUPDATE" :
						 var message = common.getLabel("lbl_project_setting_step01_js_server_config_update");
						 WebSquare.layer.showProcessMessage(message); //Server Config Update
						 break;
					 case "DONE" :
						 WebSquare.layer.hideProcessMessage();
						 break;
					 case "SUCCESSFUL":
						 WebSquare.layer.hideProcessMessage();
						 scwin.selectBuildAppConfigListData(step1_select_platform.getValue(), obj);

						 break;
					 default :
						 break;
				 }
			 };

			 scwin.selectBuildAppConfigListData = function (type, obj) {
				 if (type == 'iOS') {

					 $p.parent().scwin.getResultAppConfigData = obj;
					 // console.log(obj.resultAppConfigListObj);

				 } else if (type == 'Android') {
					 console.log(obj);
					 $p.parent().scwin.getResultAppConfigData = obj.resultMultiProfileConfigListObj;

				 }

			 };

			 function changeToolTipContentSettingStep1(componentId, label) {
				 let platform = localStorage.getItem("_platform_");
				 switch (platform) {
					 case "Android":
						 var message = common.getLabel("lbl_sign_profile_tip");
						 return message
					 case "iOS":
						 var message = common.getLabel("lbl_ios_sign_profile");
						 return message
					 default:
						 return ""
				 }

			 };
			 
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_input_project'}},{T:1,N:'xf:group',A:{class:'fr',id:'fr_grp_proj_save',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'txt_chk',useLocale:'true',localeRef:'lbl_project_setting_step01_saveQuestion'}},{T:1,N:'xf:trigger',A:{id:'btn_vcspull_1',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'step1_input_projectname',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_os'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step01_osSetting'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_platform',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Android'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'Android'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'iOS'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'iOS'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'tep1_select_template_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_wmatrix_version'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_version_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_template',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'step1_select_platformlanguage_settingbox_group',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_setting_step01_developLanguage'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_app_language'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_platformlanguage_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_branch_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_setting_step01_pathName'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipLocaleRef:'lbl_project_setting_step01_pathName',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:input',A:{id:'step1_input_project_dir_path_name',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_build_server_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_build_server_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_branch_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_branch_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_version_manage_tool_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_version_manage_tool_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_vcs_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_inner_deploy_server_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_inner_deploy_server_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_ftp_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_sign_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'signing_profile_tooltip',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep1',useLocale:'true',tooltipLocaleRef:'lbl_sign_profile_tip'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step1_select_signingKey_settingbox',disabledClass:'w2selectbox_disabled',ref:'data:dlt_vcs_setting_selectbox',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_setting_step01_createDate'}},{T:1,N:'xf:input',A:{id:'step1_input_project_create_date',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_setting_step01_modifyDate'}},{T:1,N:'xf:input',A:{id:'step1_input_project_updated_date',style:'',adjustMaxLength:'false'}}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_explain'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_descript_project'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:textarea',A:{id:'step1_txtarea_desc',style:'width:100%;height:100px;'}}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_next',id:'btn_next',outerDiv:'false',style:'','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_next'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})