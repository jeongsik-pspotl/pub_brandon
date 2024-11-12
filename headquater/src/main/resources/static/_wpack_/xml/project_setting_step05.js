/*amd /xml/project_setting_step05.xml 11352 7188670d9623bd67384642e650db39b68b75edf5cfa984364dc0ef9a4e7446b3 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){

	scwin.onpageload = function() {
		common.setScopeObj(scwin);
		var platform = localStorage.getItem("_platform_");
		if(platform == "Android"){
			var setting = common.getLabel("lbl_project_setting_step05_distribution_setting");
			var playstore = common.getLabel("lbl_project_setting_step05_playstore_key");

			txt_platoform_store.setValue(setting);
			txt_platform_signingkeyfile_name.setValue(playstore);
			//grp_ios_apple_key_id.hide();
			//grp_ios_apple_issuer_id.hide();

		} else {
			var setting = common.getLabel("lbl_project_setting_step05_distribution_ios");
			var appstore = common.getLabel("lbl_app_store_deploy_key");

			txt_platoform_store.setValue(setting);
			txt_platform_signingkeyfile_name.setValue(appstore);
			//grp_ios_apple_key_id.show();
			//grp_ios_apple_issuer_id.show();
		}

		scwin.init();

	};

	scwin.init = function(){

		var whive_session = sessionStorage.getItem("__whybrid_session__");
		whive_session = JSON.parse(whive_session);

		var platform = localStorage.getItem("_platform_");
		var buildproj_json = $p.parent().dtl_build_setting_step1.getAllJSON();

		var deploySetting_json = $p.parent().dtl_deploy_setting_step5.getAllJSON();
		// deploySetting_json[0].all_signingkey_id;

		if(platform == "Android"){
		    // android 는 받을 값이 없음

		}else {
		    // ios 는 두가지 키 값을 받음
			//step2_ios_input_key_app_id.setValue(deploySetting_json[0].apple_key_id);
			//step2_ios_input_apple_issuer_id.setValue(deploySetting_json[0].apple_issuer_id);

		}

		// deploy setting data 기반으로 signingkey 상세 조회 기능 구현

		if(deploySetting_json[0].all_signingkey_id == ""){
			var buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);

            // 수정 ...
			if(g_config.PROFILES == "service"){
				scwin.getSigningProfileDeployService(platform);
			}else {
				scwin.getSigningKeySettingInfoToDeploy(buildproj_json.platform, buildproj_json.builder_id);
			}


		}else {
			scwin.getSigningKeySettingInfoToDeployKey(deploySetting_json[0].all_signingkey_id);
		}





	};

	scwin.getSigningKeySettingInfoToDeployKey = function(signingkey_id){
		var options = {};

		options.action = "/manager/mCert/common/search/storeDeployKey/"+parseInt(signingkey_id);
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {


				step5_select_signingKey_settingbox.addItem(data.key_id, data.key_name);

                // 해당 기능 실행 이후 분기 처리
				//
				btn_complete.setDisabled(true);
				step5_select_signingKey_settingbox.setDisabled(true);

			} else {

			}
		};

		options.error = function (e) {
			//console.log(e.responseJSON);
			alert("message:"+e.responseJSON+"\n");

		};

		$p.ajax( options );

	};

	scwin.onpageunload = function() {

	};

	scwin.btn_prev_onclick = function(e) {
		$p.parent().scwin.selected_step(4);
	};

	scwin.webSocketCallback = function(obj) {
		// msg type 추가
		switch (obj.MsgType) {
				// 1. msg deploy init start
			case "HV_MSG_DEPLOY_SETTING_STATUS_INFO_FROM_HEADQUATER" :
				scwin.setBuilderDeploySettingStatus(obj);
				break;
			case "HV_MSG_PROJECT_TEMPLATE_STATUS_INFO_FROM_HEADQUATER":
				// 2. msg deploy init
				break;
			default :
				break;
		}
	};

	scwin.setBuilderDeploySettingStatus = function (obj) {

		switch (obj.status) {
			case "FASTLANEINIT":
				var msg = common.getLabel("lbl_project_setting_step05_init");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "FASTENV":
				var msg = common.getLabel("lbl_project_setting_step05_env");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "FASTFILE":
				var msg = common.getLabel("lbl_project_setting_step05_fastfile");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "APPFILE":
				var msg = common.getLabel("lbl_project_setting_step05_appfile");
				WebSquare.layer.showProcessMessage(msg);
				break;
			case "DONE":
				var msg = common.getLabel();
				WebSquare.layer.hideProcessMessage();
				var message = common.getLabel("lbl_project_setting_step05_complete");
				alert(message);
				$p.parent().$p.parent().scwin.btnWorkspace_onclick();
				//wfm_main.setSrc("/xml/workspace.xml");

				break;
			default :
				break;
		}

	};

	scwin.btn_complete_onclick = function(e) {

	    // fastlane init 값 설정 하는 ajax 통신 구현
		var buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);
		var buildsetting_json = $p.parent().dtl_build_setting_step2.getRowJSON(0);

		// var whive_session = sessionStorage.getItem("__whybrid_session__");
		// whive_session = JSON.parse(whive_session);

		var platform = localStorage.getItem("_platform_");

		var deploy_all_in_json = {};

		// deploy_all_in_json.hqKey = whive_session.user_login_id;
		// deploy_all_in_json.domain_id = whive_session.domain_id;
		// deploy_all_in_json.user_id = whive_session.id;
		// deploy_all_in_json.role_id = whive_session.role_id;
		deploy_all_in_json.all_signingkey_id = step5_select_signingKey_settingbox.getValue();
		deploy_all_in_json.all_package_name = buildsetting_json.package_name;
		deploy_all_in_json.workspace_id = localStorage.getItem("__workspace_id__");
		deploy_all_in_json.project_name = buildproj_json.project_name;
		if(platform == "Android"){
			deploy_all_in_json.apple_key_id = "";
			deploy_all_in_json.apple_issuer_id = "";

		} else {
			//deploy_all_in_json.apple_key_id = step2_ios_input_key_app_id.getValue();
			//deploy_all_in_json.apple_issuer_id = step2_ios_input_apple_issuer_id.getValue();

		}

		var options = {};
		options.action = "/manager/deploy/setting/create";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(deploy_all_in_json);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {


			} else {
				// alert("Deploy Setting 생성 실패");
			}
		};

		options.error = function (e) {
			alert("message:"+e.responseText+"\n");
			//$p.url("/login.xml");
		};

		$p.ajax( options );


		// wfm_main.setSrc("/xml/workspace.xml");
	};

	scwin.getSigningKeySettingInfoToDeploy = function(platform, builder_id){


		var options = {};

		options.action = "/manager/mCert/common/search/storeDeployKeyType"; // + parseInt(whive_session.role_id); // role_id 값으로 처리하기
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {
				step5_select_signingKey_settingbox.removeAll(false);

                var message = common.getLabel("lbl_select");
				step5_select_signingKey_settingbox.addItem("",message); //선택
				for (var row in data) {
					if(platform == "Android") {
						step5_select_signingKey_settingbox.addItem(data[row].key_deploy_android_id, data[row].android_key_name);
					}else {
						step5_select_signingKey_settingbox.addItem(data[row].key_deploy_ios_id, data[row].ios_key_name);
					}

				}

			} else {

			}
		};

		options.error = function (e) {
			//console.log(e.responseJSON);
			alert("message:"+e.responseJSON+"\n");

		};

		$p.ajax( options );

	};

	scwin.getSigningProfileDeployService = function(platform){

		var options = {};

		options.action = "/manager/mCert/common/search/storeDeployKeyByPlatform/" + platform; // role_id 값으로 처리하기
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {
				step5_select_signingKey_settingbox.removeAll(false);

                var message = common.getLabel("lbl_select");
				step5_select_signingKey_settingbox.addItem("",message); //선택
				for (var row in data) {
					if(data[row].platform == "Android" && platform == "Android") {
						step5_select_signingKey_settingbox.addItem(data[row].key_id, data[row].key_name);
					}else if(data[row].platform == "iOS" && platform == "iOS"){
						step5_select_signingKey_settingbox.addItem(data[row].key_id, data[row].key_name);
					}

				}

			} else {

			}
		};

		options.error = function (e) {
			//console.log(e.responseJSON);
			alert("message:"+e.responseJSON+"\n");

		};

		$p.ajax( options );

	};

	function changeToolTipContentSettingStep5 (componentId, label) {
		let platform = localStorage.getItem("_platform_");
		switch (platform) {
			case "Android":
				return "플레이 스토어 배포를 위한 API JSON Key"
			case "iOS":
				return "앱 스토어 배포를 위한 App Store Connect API Key (p8)"
			default:
				return ""
		}
	};

	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'txt_platoform_store',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step05_notice'}},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'w2:textbox',A:{class:'txt_chk',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_setting_step05_deploySetting'}},{T:1,N:'xf:trigger',A:{type:'button',id:'btn_complete',style:'',class:'btn_cm type1','ev:onclick':'scwin.btn_complete_onclick',useLocale:'true',localeRef:'lbl_complete'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'txt_platform_signingkeyfile_name',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_setting_step05_storeKey'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentSettingStep5',useLocale:'true',tooltipLocaleRef:'lbl_project_setting_step05_key'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step5_select_signingKey_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]})