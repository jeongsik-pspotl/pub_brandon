/*amd /xml/project_add_step05.xml 10593 efd825ab6e4f0afd517a78c3a5f8d8ef8862751475dbb696e098072cbe699d72 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
        
	scwin.onpageload = function() {
		common.setScopeObj(scwin);
		var platform = localStorage.getItem("_platform_");
		if(platform == "Android"){
            var message_setting = common.getLabel("lbl_project_add_step05_js_deploy_setting_playstore");
            var message_key = common.getLabel("lbl_project_add_step05_js_deploy_playstore_key");
			txt_platform_store.setValue(message_setting); //PlayStore 배포설정
			txt_platform_signingkeyfile_name.setValue(message_key); //플레이 스토어 배포 키
			grp_ios_apple_key_id.hide();
			grp_ios_apple_issuer_id.hide();


		} else {
            var message_setting = common.getLabel("lbl_project_add_step05_js_deploy_setting_test_flight");
            var message_key = common.getLabel("lbl_app_store_deploy_key");
			txt_platform_store.setValue(message_setting); //Testflight(iOS) 배포설정
			txt_platform_signingkeyfile_name.setValue(message_key);
			grp_ios_apple_key_id.hide();
			grp_ios_apple_issuer_id.hide();
		}

		scwin.init();
	};
	
	scwin.onpageunload = function() {
		
	};

	scwin.init = function(){


		var buildproj_json = $p.parent().dtl_build_project_step1.getRowJSON(0);

		//if(g_config.PROFILES == "service" && whive_session.user_role == "ADMIN"){
			scwin.getSigningProfileDeployService(buildproj_json.platform);

		//}else {
			//scwin.getSigningKeySettingInfoToDeploy(buildproj_json.platform, buildproj_json.builder_id);
		//}



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
                var message = common.getLabel("lbl_project_add_step05_js_fastlane_init");
				WebSquare.layer.showProcessMessage(message); //Deploy init Create
				break;
			case "FASTENV":
                var message = common.getLabel("lbl_project_add_step05_js_fastlane_env");
				WebSquare.layer.showProcessMessage(message); //Deploy Env setting
				break;
			case "FASTFILE":
                var message = common.getLabel("lbl_project_add_step05_js_fastlane_file");
				WebSquare.layer.showProcessMessage(message); //Deploy FastFile Create
				break;
			case "APPFILE":
                var message = common.getLabel("lbl_project_add_step05_js_fastlane_app_file");
				WebSquare.layer.showProcessMessage(message); //Deploy AppFile Create
				break;
			case "DONE":
				WebSquare.layer.hideProcessMessage();
                var message = common.getLabel("lbl_project_add_step05_js_complete_deploy_setting");
				alert(message); //Deploy Setting 생성 완료
				$p.parent().$p.parent().scwin.btnWorkspace_onclick();
				//wfm_main.setSrc("/xml/workspace.xml");

				break;
			default :
				break;
		}

	};

	scwin.btn_prev_onclick = function(e) {
		$p.parent().scwin.selected_step(4);
	};

	scwin.btn_complete_onclick = function(e) {

		var buildsetting_json = $p.parent().dtl_build_project_step2.getRowJSON(0);


		var platform = localStorage.getItem("_platform_");

		var deploy_all_in_json = {};

		deploy_all_in_json.all_signingkey_id = step5_select_signingKey_settingbox.getValue();
		deploy_all_in_json.all_package_name = buildsetting_json.package_name;
		deploy_all_in_json.workspace_id = localStorage.getItem("__workspace_id__");
		deploy_all_in_json.project_name = localStorage.getItem("__project_name__");
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
			alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
			//$p.url("/login.xml");
		};

		$p.ajax( options );

	};

	scwin.getSigningKeySettingInfoToDeploy = function(platform, builder_id){

		var whive_session = sessionStorage.getItem("__whybrid_session__");
		whive_session = JSON.parse(whive_session);


		var options = {};

		options.action = "/api/signingkeysetting/SelectListDeployKeyType/" + parseInt(whive_session.role_id); // role_id 값으로 처리하기
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
			alert("code:"+e.status+"\n"+"message:"+e.responseJSON+"\n");

		};

		$p.ajax( options );

	};


	scwin.getSigningProfileDeployService = function(platform){

		var options = {};

		options.action = "/manager/mCert/common/search/storeDeployKeyByPlatform/"+platform; // role_id 값으로 처리하기
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
			alert("code:"+e.status+"\n"+"message:"+e.responseJSON+"\n");

		};

		$p.ajax( options );

	};

	function changeToolTipContentAddStep5 (componentId, label) {
		let platform = localStorage.getItem("_platform_");
		switch (platform) {
			case "Android":
                var message = common.getLabel("lbl_play_store_api_json_key");
				return message
			case "iOS":
                var message = common.getLabel("lbl_app_store_connect_api_key_tip");
				return message
			default:
				return ""
		}
	};

	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit',id:'txt_platform_store',label:'',style:'',useLocale:'true',localeRef:'lbl_distribution_setting'}}]},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'w2:textbox',A:{class:'txt_chk',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_project_add_step05_chk_txt'}},{T:1,N:'xf:trigger',A:{type:'button',id:'btn_complete type1',style:'',class:'btn_cm','ev:onclick':'scwin.btn_complete_onclick',useLocale:'true',localeRef:'lbl_complete'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_ios_apple_key_id',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'txt_ios_apple_key_id_name',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step05_ios_apple_key_id'}},{T:1,N:'xf:input',A:{id:'step2_ios_input_key_app_id',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'grp_ios_apple_issuer_id',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'txt_ios_apple_issuer_id_name',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step05_ios_apple_issuer_id'}},{T:1,N:'xf:input',A:{id:'step2_ios_input_apple_issuer_id',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'txt_platform_signingkeyfile_name',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_project_add_step05_platform_signingkey_file_name'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',tooltipFormatter:'changeToolTipContentAddStep5',useLocale:'true',tooltipLocaleRef:'lbl_project_add_step05_tooltip_platform_signingkey_file_name'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'step5_select_signingKey_settingbox',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''}}]}]}]}]})