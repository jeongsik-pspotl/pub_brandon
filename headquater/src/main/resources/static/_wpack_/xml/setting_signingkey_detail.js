/*amd /xml/setting_signingkey_detail.xml 53066 bc74df5e9fcdb531548c17a34852927fa17fbe77c493ac09148fe4a2430895de */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.checkProvisionFileYn = false;
			scwin.checkSigningkeyNameYn = false;
			scwin.signingkeyLastCnt = "";

            var androidTarget = {"AndroidTemplate":["Build","Deploy"]};
            var androidSigningkeyConfig = androidTarget["AndroidTemplate"];

			var target = {"Template":["Profiles","certificate","Deploy"]};
			var buildConfig = target["Template"];

			scwin.onpageload = function() {
				common.setScopeObj(scwin);
				scwin.init();
				// 화면 구성 변경
				var key_setting_mode = localStorage.getItem("_key_setting_mode_");
				var key_platform = localStorage.getItem("_platform_");

				if (key_setting_mode == "detailview"){
					var view = common.getLabel("lbl_key_setting_detail_view");
					var save = common.getLabel("lbl_save");

					ftp_setting_title.setLabel(view);
					signingkey_setting_name.setDisabled(true);
					signingkey_setting_button.setLabel(save);


					domain_id_grp.hide();
					admin_id_grp.hide();

					scwin.select_builder_list();

					if(key_platform == "Android"){
						scwin.signingKeyAndroidDetailView();
					}else {
						scwin.signingKeyiOSDetailView();
					}

				}else {
					var create = common.getLabel("lbl_key_setting_create");
					var save = common.getLabel("lbl_regist");

				    ftp_setting_title.setLabel(create);
					signingkey_setting_name.setDisabled(false);
					signingkey_setting_button.setLabel(save);

					domain_id_grp.hide();
					admin_id_grp.hide();




					scwin.select_builder_list();

				}

				var platform = setting_select_platform.getValue();

				if(platform == "Android"){
					// android body show
					gal_body_android.show();
					gal_body_ios.hide();

				}else if(platform == "iOS"){
					// ios body show
					gal_body_android.hide();
					gal_body_ios.show();

				}


			};

			scwin.init = function(){


                for(var a = 0;a<androidSigningkeyConfig.length;a++){

                    var label = androidSigningkeyConfig[a];
                    var id = "tab_" + label;
                    var contOpt = {};

                    var rowJSON = {
                        "tap_num" : a
					};

					var tabOpt = {
						"label" : label,
						"closable" : false,
						"tabWidth" : "100px"
					};

					if(label == "Build"){
						contOpt = {
							"wframe" : true,
							"src" : "/xml/setting_signingkey_android_build_detail.xml",
							"alwaysDraw" : "false",
							"scope" : "true",
							"dataObject": {
								"type" : "json",
								"name" : "tabParam",
								"data" : rowJSON
							}

						};

					}

					if(label == "Deploy"){
						contOpt = {
							"wframe" : true,
							"src" : "/xml/setting_signingkey_android_deploy_detail.xml",
							"alwaysDraw" : "false",
							"scope" : "true",
							"dataObject": {
								"type" : "json",
								"name" : "tabParam",
								"data" : rowJSON
							}

						};

					}


					setting_signgingkey_android_tap.addTab(id, tabOpt, contOpt);

				}


				for(var i=0;i<buildConfig.length;i++){

					var closable = true;
					var label = buildConfig[i];
					var id = "tab_"+label;
					var contOpt = {};

					var rowJSON = {
						"tap_num" : i
					};

					var tabOpt = {
						"label" : label,
						"closable" : false,
						"tabWidth" : "100px"
					};

                    if(label == "Profiles"){
						contOpt = {
							"wframe" : true,
							"src" : "/xml/setting_signingkey_ios_profiles_detail.xml",
							"alwaysDraw" : "false",
							"scope" : "true",
							"dataObject": {
								"type" : "json",
								"name" : "tabParam",
								"data" : rowJSON
							}
							// setting_signingkey_ios_profiles_detail.xml
						};

					}else if(label == "certificate"){
						contOpt = {
							"wframe" : true,
							"src" : "/xml/setting_signingkey_ios_certificate_detail.xml",
							"alwaysDraw" : "false",
							"scope" : "true",
							"dataObject": {
								"type" : "json",
								"name" : "tabParam",
								"data" : rowJSON
							}
							// setting_signingkey_ios_profiles_detail.xml
						};

					}else if(label == "Deploy"){
						contOpt = {
							"wframe" : true,
							"src" : "/xml/setting_signingkey_ios_deploy_detail.xml",
							"alwaysDraw" : "false",
							"scope" : "true",
							"dataObject": {
								"type" : "json",
								"name" : "tabParam",
								"data" : rowJSON
							}
							// setting_signingkey_ios_profiles_detail.xml
						};

					}


					setting_signgingkey_tap.addTab(id, tabOpt, contOpt);
				}
			};

			scwin.onpageunload = function() {
				localStorage.removeItem("_platform_");
			};

			// signingKey Setting 조회
			scwin.signingKeyAndroidDetailView = function(){

				var key_id = localStorage.getItem("_key_id_");

				var options = {};

				options.action = "/manager/mCert/android/search/profile/" + parseInt(key_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
						// console.log(data);
						var temp = {};

						signingkey_setting_name.setValue(data.key_name);
						setting_select_platform.setText(data.platform, false);
						setting_builder_list.setText(data.builder_name, false);
						setting_admin_list.addItem(data.admin_id, data.admin_name, false);
						setting_domain_list.addItem(data.domain_id, data.domain_name, false);

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};


			// signingKey Setting 조회
			scwin.signingKeyiOSDetailView = function(){

				var key_id = localStorage.getItem("_key_id_");

				var options = {};

				options.action = "/manager/mCert/iOS/search/profile/" + parseInt(key_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						signingkey_setting_name.setValue(data.key_name);
						setting_select_platform.setText(data.platform, false);
						setting_builder_list.setText(data.builder_name, false);
						setting_admin_list.addItem(data.admin_id, data.admin_name, false);
						setting_domain_list.addItem(data.domain_id, data.domain_name, false);

						// platform 기준으로 변경해서 처리 해야함..
						// if(data.platform == "iOS"){
						// 	setting_ios_select_signingkey_type.setText(data.ios_key_type, false);
						// 	setting_ios_select_release_type.setText(data.ios_release_type, false);
						// 	setting_ios_store_password.setValue(data.ios_key_password);
						// 	setting_ios_unlock_keychain_password.setValue(data.ios_unlock_keychain_password);
                        //
						// 	if(data.ios_key_type == "build"){
                        //
						// 		$("#wfm_main_signingkey_setting_ios_debug_provisioning_input_file_path_inputFile").val(data.ios_debug_profile_path);
						// 		$("#wfm_main_signingkey_setting_ios_release_provisioning_input_file_path_inputFile").val(data.ios_release_profile_path);
						// 		$("#wfm_main_signingkey_setting_ios_signingkey_input_file_path_inputFile").val(data.ios_key_path);
                        //
						// 	}else{
						// 		$("#wfm_main_signingkey_setting_ios_signingkey_input_file_path_inputFile").val(data.ios_key_path);
                        //
						// 		setting_ios_issuer_id.setValue(data.ios_issuer_id);
						// 		setting_ios_key_id.setValue(data.ios_key_id);
                        //
                        //
						// 	}
						// }

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			scwin.getDomainByIDView = function(){


			};

			scwin.getAdminByIDView = function(){


			};

			// signingkey name 중복 조회 ajax
			scwin.select_check_key_name = function(key_name){
				var data = {};
				data.key_name = key_name;
				var options = {};

				options.action = "/manager/mCert/common/search/checkProfileName";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
						if (data != null) {

						    if(data[0].keyNameCheck == "no"){
								var message = common.getLabel("lbl_exist_key_cert_name");
								alert(message);
								scwin.checkSigningkeyNameYn = false;

							}else {
								var message = common.getLabel("lbl_can_use_key_cert_name");
								alert(message);
								scwin.checkSigningkeyNameYn = true;

							}

						}
					}

				};

				options.error = function (e) {
					if(e.responseStatusCode === 500){
						var message = common.getLabel("lbl_can_use_key_cert_name");
						alert(message);
						scwin.checkSigningkeyNameYn = true;

					}else {

					}

				};

				$p.ajax( options );

			};

			scwin.select_builder_list = function(){

				var options = {};

				options.action = "/manager/builderSetting/selectBySelectBoxList";
				options.mode = "synchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
                        var message = common.getLabel("lbl_select");
						setting_builder_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

							setting_builder_list.addItem(data[row].builder_id, data[row].builder_name);
							// 다음으로 진행할 기능 template 버전 조회
							// -> builder id 데이터 호출


						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			// domain 리스트 전체 조회
			scwin.select_domain_list = function(){

				var options = {};

				options.action = "/manager/domain/search/domainList";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
                        var message = common.getLabel("lbl_select");
						setting_domain_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

							setting_domain_list.addItem(data[row].domain_id, data[row].domain_name);

						}

					} else {

					}
				};// domain 리스트 전체 조회
				scwin.select_domain_list = function(){

					var options = {};

					options.action = "/manager/domain/search/domainList";
					options.mode = "asynchronous";
					options.mediatype = "application/json";
					options.method = "GET";

					options.success = function (e) {
						var data = e.responseJSON;
						if (data != null) {
                            var message = common.getLabel("lbl_select");
							setting_domain_list.addItem("",message); //선택
							for (var row in data) {
								var temp = {};

								setting_domain_list.addItem(data[row].domain_id, data[row].domain_name);

							}

						} else {

						}
					};

					options.error = function (e) {
						alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

					};

					$p.ajax( options );

				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			scwin.select_admin_list = function(domainID){

				var options = {};

				options.action = "/manager/member/search/userInfoForSelectBox/" + parseInt(domainID);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
						setting_admin_list.removeAll(false);

                        var message = common.getLabel("lbl_select");
						setting_admin_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

							setting_admin_list.addItem(data[row].user_id, data[row].user_name);

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			scwin.saveSigningKeySettingData = function(){

				var signingKeyfile = "";
				var debug_profile_path = "";
				var release_profile_path = "";
				var append_deploy_config_data = {};
				var tap_all_certificate_config = [];
				var tap_all_profile_config = [];
                var tap_file_profile_config = [];
                var tap_file_certificate_config = [];

				if (common.isEmptyStr(signingkey_setting_name.getValue())) {
					var message = common.getLabel("lbl_check_key_cert_name");
					alert(message);
					return false;
				}

				if(!scwin.checkSigningkeyNameYn){
					var message = common.getLabel("lbl_check_dup_key");
					alert(message)
					return false;
				}

				if(setting_select_platform.getValue() == "Android"){

					for(var tap_cnt = 0 ; tap_cnt < androidSigningkeyConfig.length;tap_cnt++){

						var tableFrame = setting_signgingkey_android_tap.getFrame(tap_cnt);
						// var tap_all_deploy_config = [];

						// android build 인증서 값 세팅
						if(tap_cnt == 0){
							if(tableFrame.getObj("setting_android_key_alias") === undefined){

							}else {
								append_deploy_config_data.build_android_key_alias = tableFrame.getObj("setting_android_key_alias").getValue();
							}


							if(tableFrame.getObj("setting_android_key_password") === undefined){

							}else {
								append_deploy_config_data.build_android_key_password = tableFrame.getObj("setting_android_key_password").getValue();
							}

							if(tableFrame.getObj("setting_android_store_password") === undefined){

							}else {
								append_deploy_config_data.build_android_store_password = tableFrame.getObj("setting_android_store_password").getValue();
							}

							if(tableFrame.getObj("signingkey_setting_android_input_file_path_tmp") === undefined){

							}else{
								append_deploy_config_data.buildfile_path = tableFrame.getObj("signingkey_setting_android_input_file_path_tmp").dom.fakeinput;
								console.log(append_deploy_config_data.buildfile_path);
								if(append_deploy_config_data.buildfile_path.files[0] === undefined){


								}else if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_deploy_config_data.buildfile_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_key_form_kor");
									alert(message);
									return false;
								}
							}

                            if(tableFrame.getObj("signingkey_android_key_id") === undefined) {

							}else {
                                append_deploy_config_data.build_android_key_id = tableFrame.getObj("signingkey_android_key_id").getValue();

							}

						}

						if(tap_cnt == 1){

							if(tableFrame.getObj("signingkey_setting_android_deploy_input_file_path_tmp") === undefined){

							}else{
								append_deploy_config_data.deployfile_path = tableFrame.getObj("signingkey_setting_android_deploy_input_file_path_tmp").dom.fakeinput;
								console.log(append_deploy_config_data.deployfile_path);
								if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_deploy_config_data.deployfile_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_setting_signingkey_detail_jsonError");
									alert(message);
									return false;
								}
							}

						}

					}



				}else if(setting_select_platform.getValue() == "iOS"){

					for(var tap_cnt = 0 ; tap_cnt < buildConfig.length;tap_cnt++){
						var tableFrame = setting_signgingkey_tap.getFrame(tap_cnt);
						var append_tap_app_config_data = {};

						if(tableFrame == undefined){

						}else {
                            // tap_cnt profiles 인 경우다.
                            if(tap_cnt == 0){

                                var append_profile_config_data = {};

								append_profile_config_data.profile_name = tableFrame.getObj("setting_ios_profile_name_input").getValue();
								append_profile_config_data.profile_path = tableFrame.getObj("signingkey_setting_ios_release_provisioning_input_file_path").dom.fakeinput;
								append_profile_config_data.profile_type = tableFrame.getObj("setting_ios_select_release_type").getValue();

								if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_profile_config_data.profile_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_key_form_kor");
									alert(message);
									return false;
								}


								tap_all_profile_config.push(new Blob([ JSON.stringify(append_profile_config_data) ], {type : "application/json"}));
								tap_file_profile_config.push(append_profile_config_data);


                                // 다음으로는 for 문 처리 하는 방식으로 동적 처리하기
								var profile_config_cnt = tableFrame.getObj("input_profile_config_cnt").getValue(); // cnt 값 호출하기
								if(profile_config_cnt == 0){

								}else {
                                    for(var profile_cnt = 0; profile_cnt < profile_config_cnt; profile_cnt++){
										var append_profile_config_data_sample = {};

                                        // console.log($p.getComponentById("tap_0myid_ios_select_release_type_select_box_0_input_0").getValue());
										append_profile_config_data_sample.profile_name = $p.getComponentById("tap_"+tap_cnt+"myid_profile_name_input_" + profile_cnt).getValue();
										append_profile_config_data_sample.profile_type = $p.getComponentById("tap_"+tap_cnt+"myid_ios_select_release_type_select_box_" + profile_cnt).getValue();
										append_profile_config_data_sample.profile_path = $p.getComponentById("tap_"+tap_cnt+"myid_ios_release_provisioning_input_file_path_" + profile_cnt).dom.fakeinput;

										if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_profile_config_data_sample.profile_path.files[0].name)){
											// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
											var message = common.getLabel("lbl_key_form_kor");
											alert(message);
											return false;
										}
										// new Blob([ JSON.stringify(append_profile_config_data_sample) ], {type : "application/json"})
										tap_all_profile_config.push(new Blob([ JSON.stringify(append_profile_config_data_sample) ], {type : "application/json"}));
										tap_file_profile_config.push(append_profile_config_data_sample);

									}
								}

							}

							if(tap_cnt == 1){

								var append_certificate_config_data = {};

								append_certificate_config_data.certificate_name = tableFrame.getObj("setting_ios_certificate_name_input").getValue();
								append_certificate_config_data.certificate_path = tableFrame.getObj("signingkey_setting_ios_signingkey_input_file_path").dom.fakeinput;
								append_certificate_config_data.certificate_password = tableFrame.getObj("setting_ios_certificate_password").getValue();

								if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_certificate_config_data.certificate_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_key_form_kor");
									alert(message);
									return false;
								}

								tap_all_certificate_config.push(new Blob([ JSON.stringify(append_certificate_config_data) ], {type : "application/json"}));
								tap_file_certificate_config.push(append_certificate_config_data);

								var certiftcate_config_cnt = tableFrame.getObj("input_certiftcate_config_cnt").getValue(); // cnt 값 호출하기

								if(certiftcate_config_cnt == 0){

								}else {
									for(var certificate_cnt = 0; certificate_cnt < certiftcate_config_cnt; certificate_cnt++){
										var append_certificate_config_data_sample = {};

										append_certificate_config_data_sample.certificate_name = $p.getComponentById("tap_"+tap_cnt+"myid_certificate_name_input_" + certificate_cnt).getValue();
										append_certificate_config_data_sample.certificate_path = $p.getComponentById("tap_"+tap_cnt+"myid_ios_certificate_input_file_path_" + certificate_cnt).dom.fakeinput;
										append_certificate_config_data_sample.certificate_password = $p.getComponentById("tap_"+tap_cnt+"myid_certificate_password_input_" + certificate_cnt).getValue();

										if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_certificate_config_data_sample.certificate_path.files[0].name)){
											// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
											var message = common.getLabel("lbl_key_form_kor");
											alert(message);
											return false;
										}

										tap_all_certificate_config.push(new Blob([ JSON.stringify(append_certificate_config_data_sample) ], {type : "application/json"}));
										tap_file_certificate_config.push(append_certificate_config_data_sample);

									}
								}
							}

                            if(tap_cnt == 2){

								// var tap_all_deploy_config = [];
                                if(tableFrame.getObj("setting_ios_key_id") === undefined){

								}else {
									append_deploy_config_data.deploy_ios_key_id = tableFrame.getObj("setting_ios_key_id").getValue();
								}

                                if(tableFrame.getObj("signingkey_setting_ios_signingkey_input_file_path") === undefined){

								}else{
									append_deploy_config_data.deploy_path = tableFrame.getObj("signingkey_setting_ios_signingkey_input_file_path").dom.fakeinput;

									if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_deploy_config_data.deploy_path.files[0].name)){
										// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
										var message = common.getLabel("lbl_key_form_kor");
										alert(message);
										return false;
									}
								}

                                if(tableFrame.getObj("setting_ios_issuer_id") === undefined){

								}else {
									append_deploy_config_data.deploy_ios_issuer_id = tableFrame.getObj("setting_ios_issuer_id").getValue();
								}

							}

						}

					}

				}

				// 특수문자 한글 입력 제외 시키기
				var formData = new FormData();

				// formData.append("hqKey", whive_session.user_login_id);
				formData.append("key_name", signingkey_setting_name.getValue());
				formData.append("platform", setting_select_platform.getValue());
				// android 기준
				// file path 도 같이 파싱해야함.
				if(setting_select_platform.getValue() == "Android"){
					// signing key android, ios 분할해서 처리

					formData.append("file", append_deploy_config_data.buildfile_path.files[0]);
					formData.append("key_type","all");


					formData.append("key_password",append_deploy_config_data.build_android_key_password);
					formData.append("key_alias",append_deploy_config_data.build_android_key_alias);
					formData.append("store_key_password",append_deploy_config_data.build_android_store_password);
					formData.append("deployFile", append_deploy_config_data.deployfile_path.files[0]);


					// formData.append("build_type",setting_select_android_build_type.getValue());
					formData.append("builder_id",setting_builder_list.getValue());

					// if(whive_session.user_role == "SUPERADMIN"){
						//formData.append("domain_id",setting_domain_list.getValue());
						//formData.append("admin_id",setting_admin_list.getValue());

					// } else if (whive_session.user_role == "ADMIN"){
					/*
						//formData.append("domain_id",whive_session.domain_id);
						//formData.append("admin_id",whive_session.id);

						컨트롤러 단에서처리해야함.

					 */

					//}
					signingkey_setting_button.setDisabled(true);
					scwin.setKeySettingAndroidInsert(formData);

				}else {
					// 여기서 부터 서버단으로 데이터를 보내해야할 값들을 세팅 해야하는 구간이다.

					formData.append("builder_id",setting_builder_list.getValue());


                    for(var i = 0; i < tap_file_profile_config.length ; i++){

                        var profile_configJson = tap_file_profile_config[i];

						formData.append("profiles",profile_configJson.profile_path.files[0]);


					}

                    formData.append("jsonProfiles[]",JSON.stringify(tap_file_profile_config));

                    for(var j = 0; j < tap_file_certificate_config.length; j++) {

                        var certificate_configJson = tap_file_certificate_config[j];
                        // console.log(certificate_configJson);
						formData.append("certificates",certificate_configJson.certificate_path.files[0]);

					}

					formData.append("jsonCertificates[]",JSON.stringify(tap_file_certificate_config));
					// if(whive_session.user_role == "SUPERADMIN"){
						// formData.append("domain_id",setting_domain_list.getValue());
						// formData.append("admin_id",setting_admin_list.getValue());

					// } else if (whive_session.user_role == "ADMIN"){
						// formData.append("domain_id",whive_session.domain_id);
						// formData.append("admin_id",whive_session.id);

					// }
					if(append_deploy_config_data.deploy_path === undefined){
						formData.append("deployFile", null);
					}else{
						formData.append("deployFile", append_deploy_config_data.deploy_path.files[0]);
					}

                    if(append_deploy_config_data.deploy_ios_issuer_id === undefined){
						formData.append("ios_issuer_id", "");
					}else{
						formData.append("ios_issuer_id", append_deploy_config_data.deploy_ios_issuer_id);
					}

                    if(append_deploy_config_data.deploy_ios_key_id === undefined){
						formData.append("ios_key_id", "");
					}else {
						formData.append("ios_key_id", append_deploy_config_data.deploy_ios_key_id);
					}

                    // ajax 로 보내고 서버에서 어떻게 값이 들어오는지 확인 해봐야 할거 같다.
					signingkey_setting_button.setDisabled(true);
					scwin.setKeySettingAlliOSInsert(formData);


				}

			};


			scwin.updateSigningKeySettingData = function(){

				var signingKeyfile = "";
				var debug_profile_path = "";
				var release_profile_path = "";
				var append_deploy_config_data = {};
				var tap_all_certificate_config = [];
				var tap_all_profile_config = [];
				var tap_file_profile_config = [];
				var tap_file_certificate_config = [];

				if (common.isEmptyStr(signingkey_setting_name.getValue())) {
					var message = common.getLabel("lbl_check_key_cert_name");
					alert(message);
					return false;
				}

				if(setting_select_platform.getValue() == "Android"){


					for(var tap_cnt = 0 ; tap_cnt < androidSigningkeyConfig.length;tap_cnt++){

						var tableFrame = setting_signgingkey_android_tap.getFrame(tap_cnt);
						// var tap_all_deploy_config = [];

						// android build 인증서 값 세팅
						if(tap_cnt == 0){
							if(tableFrame.getObj("setting_android_key_alias") === undefined){

							}else {
								append_deploy_config_data.build_android_key_alias = tableFrame.getObj("setting_android_key_alias").getValue();
							}


							if(tableFrame.getObj("setting_android_key_password") === undefined){

							}else {
								append_deploy_config_data.build_android_key_password = tableFrame.getObj("setting_android_key_password").getValue();
							}

							if(tableFrame.getObj("setting_android_store_password") === undefined){

							}else {
								append_deploy_config_data.build_android_store_password = tableFrame.getObj("setting_android_store_password").getValue();
							}

							if(tableFrame.getObj("signingkey_setting_android_input_file_path_tmp") === undefined){

							}else{
								append_deploy_config_data.buildfile_path = tableFrame.getObj("signingkey_setting_android_input_file_path_tmp").dom.fakeinput;
								console.log(append_deploy_config_data.buildfile_path);
								if(append_deploy_config_data.buildfile_path.files[0] === undefined){


								}else if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_deploy_config_data.buildfile_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_key_form_kor");
									alert(message);
									return false;
								}
							}



							if(tableFrame.getObj("signingkey_android_key_id") === undefined) {

							}else {
								append_deploy_config_data.build_android_key_id = tableFrame.getObj("signingkey_android_key_id").getValue();

							}

						}

                        if(tap_cnt == 1){

							if(tableFrame.getObj("signingkey_setting_android_deploy_input_file_path_tmp") === undefined){

							}else{
								append_deploy_config_data.deployfile_path = tableFrame.getObj("signingkey_setting_android_deploy_input_file_path_tmp").dom.fakeinput;
								console.log(append_deploy_config_data.deployfile_path);
								if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_deploy_config_data.deployfile_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_setting_signingkey_detail_jsonError");
									alert(message);
									return false;
								}
							}

						}

					}



				}else if(setting_select_platform.getValue() == "iOS"){

					for(var tap_cnt = 0 ; tap_cnt < buildConfig.length;tap_cnt++){
						var tableFrame = setting_signgingkey_tap.getFrame(tap_cnt);
						var append_tap_app_config_data = {};

						if(tableFrame == undefined){

						}else {
							// tap_cnt profiles 인 경우다.
							if(tap_cnt == 0){

								var append_profile_config_data = {};

								append_profile_config_data.profile_name = tableFrame.getObj("setting_ios_profile_name_input").getValue();
								append_profile_config_data.profile_path = tableFrame.getObj("signingkey_setting_ios_release_provisioning_input_file_path").dom.fakeinput;
								append_profile_config_data.profile_type = tableFrame.getObj("setting_ios_select_release_type").getValue();

								if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_profile_config_data.profile_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_key_form_kor");
									alert(message);
									return false;
								}


								tap_all_profile_config.push(new Blob([ JSON.stringify(append_profile_config_data) ], {type : "application/json"}));
								tap_file_profile_config.push(append_profile_config_data);


								// 다음으로는 for 문 처리 하는 방식으로 동적 처리하기
								var profile_config_cnt = tableFrame.getObj("input_profile_config_cnt").getValue(); // cnt 값 호출하기
								if(profile_config_cnt == 0){

								}else {
									for(var profile_cnt = 0; profile_cnt < profile_config_cnt; profile_cnt++){
										var append_profile_config_data_sample = {};
										append_profile_config_data_sample.profile_name = $p.getComponentById("tap_"+tap_cnt+"myid_profile_name_input_" + profile_cnt).getValue();
										append_profile_config_data_sample.profile_path = $p.getComponentById("tap_"+tap_cnt+"myid_ios_release_provisioning_input_file_path_" + profile_cnt).dom.fakeinput;
										append_profile_config_data_sample.profile_type = $p.getComponentById("tap_"+tap_cnt+"myid_ios_select_release_type_select_box_" + profile_cnt).getValue();

										if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_profile_config_data_sample.profile_path.files[0].name)){
											// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
											var message = common.getLabel("lbl_key_form_kor");
											alert(message);
											return false;
										}
										// new Blob([ JSON.stringify(append_profile_config_data_sample) ], {type : "application/json"})
										tap_all_profile_config.push(new Blob([ JSON.stringify(append_profile_config_data_sample) ], {type : "application/json"}));
										tap_file_profile_config.push(append_profile_config_data_sample);

									}
								}

								if(tableFrame.getObj("signingkey_main_ios_key_id") === undefined) {

								}else {
									append_deploy_config_data.build_ios_key_id = tableFrame.getObj("signingkey_main_ios_key_id").getValue();

								}

							}

							if(tap_cnt == 1){

								var append_certificate_config_data = {};

								append_certificate_config_data.certificate_name = tableFrame.getObj("setting_ios_certificate_name_input").getValue();
								append_certificate_config_data.certificate_path = tableFrame.getObj("signingkey_setting_ios_signingkey_input_file_path").dom.fakeinput;
								append_certificate_config_data.certificate_password = tableFrame.getObj("setting_ios_certificate_password").getValue();

								if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_certificate_config_data.certificate_path.files[0].name)){
									// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
									var message = common.getLabel("lbl_key_form_kor");
									alert(message);
									return false;
								}

								tap_all_certificate_config.push(new Blob([ JSON.stringify(append_certificate_config_data) ], {type : "application/json"}));
								tap_file_certificate_config.push(append_certificate_config_data);

								var certiftcate_config_cnt = tableFrame.getObj("input_certiftcate_config_cnt").getValue(); // cnt 값 호출하기

								if(certiftcate_config_cnt == 0){

								}else {
									for(var certificate_cnt = 0; certificate_cnt < certiftcate_config_cnt; certificate_cnt++){
										var append_certificate_config_data_sample = {};

										append_certificate_config_data_sample.certificate_name = $p.getComponentById("tap_"+tap_cnt+"myid_certificate_name_input_" + certificate_cnt).getValue();
										append_certificate_config_data_sample.certificate_path = $p.getComponentById("tap_"+tap_cnt+"myid_ios_certificate_input_file_path_" + certificate_cnt).dom.fakeinput;
										append_certificate_config_data_sample.certificate_password = $p.getComponentById("tap_"+tap_cnt+"myid_certificate_password_input_" + certificate_cnt).getValue();

										if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_certificate_config_data_sample.certificate_path.files[0].name)){
											// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
											var message = common.getLabel("lbl_key_form_kor");
											alert(message);
											return false;
										}

										tap_all_certificate_config.push(new Blob([ JSON.stringify(append_certificate_config_data_sample) ], {type : "application/json"}));
										tap_file_certificate_config.push(append_certificate_config_data_sample);

									}
								}
							}

							if(tap_cnt == 2){

								// var tap_all_deploy_config = [];
								if(tableFrame.getObj("setting_ios_key_id") === undefined){

								}else {
									append_deploy_config_data.deploy_ios_key_id = tableFrame.getObj("setting_ios_key_id").getValue();
								}

								if(tableFrame.getObj("signingkey_setting_ios_signingkey_input_file_path") === undefined){

								}else{
									append_deploy_config_data.deploy_path = tableFrame.getObj("signingkey_setting_ios_signingkey_input_file_path").dom.fakeinput;

									if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR", append_deploy_config_data.deploy_path.files[0].name)){
										// if(common.checkAllInputText("CHECK_INPUT_TYPE_KOR",profile_name)){
										var message = common.getLabel("lbl_key_form_kor");
										alert(message);
										return false;
									}
								}

								if(tableFrame.getObj("setting_ios_issuer_id") === undefined){

								}else {
									append_deploy_config_data.deploy_ios_issuer_id = tableFrame.getObj("setting_ios_issuer_id").getValue();
								}

							}

						}

					}

				}

				// 특수문자 한글 입력 제외 시키기
				var formData = new FormData();

				// formData.append("hqKey", whive_session.user_login_id);
				formData.append("key_name", signingkey_setting_name.getValue());
				formData.append("platform", setting_select_platform.getValue());
				// android 기준
				// file path 도 같이 파싱해야함.
				if(setting_select_platform.getValue() == "Android"){

					formData.append("file", append_deploy_config_data.buildfile_path.files[0]);
					formData.append("key_type","all");
					formData.append("key_id",append_deploy_config_data.build_android_key_id);
					// console.log(append_deploy_config_data.deployfile_path.files[0]);

					formData.append("key_password",append_deploy_config_data.build_android_key_password);
					formData.append("key_alias",append_deploy_config_data.build_android_key_alias);
					formData.append("store_key_password",append_deploy_config_data.build_android_store_password);

					if(append_deploy_config_data.deployfile_path === undefined){
						//formData.append("deployFile", null);
					}else if(append_deploy_config_data.deployfile_path.files[0] === undefined){

					} else{
						formData.append("deployFile", append_deploy_config_data.deployfile_path.files[0]);
					}

					// formData.append("build_type",setting_select_android_build_type.getValue());
					formData.append("builder_id",setting_builder_list.getValue());

					// if(whive_session.user_role == "SUPERADMIN"){
					//formData.append("domain_id",setting_domain_list.getValue());
					//formData.append("admin_id",setting_admin_list.getValue());

					// } else if (whive_session.user_role == "ADMIN"){
					/*
						//formData.append("domain_id",whive_session.domain_id);
						//formData.append("admin_id",whive_session.id);

						컨트롤러 단에서처리해야함.

					 */

					//}
					scwin.setKeySettingAndroidUpdate(formData);

				}else {

					formData.append("builder_id",setting_builder_list.getValue());
					formData.append("key_id",append_deploy_config_data.build_ios_key_id);


					for(var i = 0; i < tap_file_profile_config.length ; i++){

						var profile_configJson = tap_file_profile_config[i];

						formData.append("profiles",profile_configJson.profile_path.files[0]);


					}

					formData.append("jsonProfiles[]",JSON.stringify(tap_file_profile_config));

					for(var j = 0; j < tap_file_certificate_config.length; j++) {

						var certificate_configJson = tap_file_certificate_config[j];
						// console.log(certificate_configJson);
						formData.append("certificates",certificate_configJson.certificate_path.files[0]);

					}

					formData.append("jsonCertificates[]",JSON.stringify(tap_file_certificate_config));
					// if(whive_session.user_role == "SUPERADMIN"){
					// formData.append("domain_id",setting_domain_list.getValue());
					// formData.append("admin_id",setting_admin_list.getValue());

					// } else if (whive_session.user_role == "ADMIN"){
					// formData.append("domain_id",whive_session.domain_id);
					// formData.append("admin_id",whive_session.id);

					// }
					if(append_deploy_config_data.deploy_path === undefined){
						formData.append("deployFile", null);
					}else{
						formData.append("deployFile", append_deploy_config_data.deploy_path.files[0]);
					}

					if(append_deploy_config_data.deploy_ios_issuer_id === undefined){
						formData.append("ios_issuer_id", "");
					}else{
						formData.append("ios_issuer_id", append_deploy_config_data.deploy_ios_issuer_id);
					}

					if(append_deploy_config_data.deploy_ios_key_id === undefined){
						formData.append("ios_key_id", "");
					}else {
						formData.append("ios_key_id", append_deploy_config_data.deploy_ios_key_id);
					}

					// ajax 로 보내고 서버에서 어떻게 값이 들어오는지 확인 해봐야 할거 같다.
					// scwin.setKeySettingAlliOSInsert(formData);
					scwin.setKeysettingAlliOSUpdate(formData);


				}

			};


			scwin.setKeySettingAndroidInsert = function (formData) {

			    // formdata 방식으로 바꿔서 file 객체 만 받는 구조로 바꾸기...
				$.ajax({
					url: "/manager/mCert/android/create",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					// data: formData,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
					    var data = r;

					    //console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;



							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});

			};

            scwin.setKeySettingAlliOSInsert = function(formData){

				$.ajax({
					url: "/manager/mCert/iOS/AllCreate",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					// data: formData,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						var data = r;

						//console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;
							// alert("Build Key Setting 생성 완료");
							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});


			};

            scwin.setKeySettingAndroidUpdate = function(formData) {

				$.ajax({
					url: "/manager/mCert/android/update",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						var data = r;

						//console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;
							if(data[0].signingKeyUpdate == "Y") {
								var message = common.getLabel("lbl_key_setting_update_complete");
								alert(message);

							}else if(data[0].signingKeyUpdate == "N"){

							}
							// alert("Build Key Setting 생성 완료");
							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});


			};

            scwin.setKeysettingAlliOSUpdate = function(formData) {

				$.ajax({
					url: "/manager/mCert/iOS/AllUpdate",
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					data: formData,
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						var data = r;

						//console.log(data);
						if (status === "success") {
							scwin.signingkeyLastCnt = data[0].signingkeyCnt;
							// alert("Build Key Setting 생성 완료");
							// console.log(scwin.signingkeyLastCnt);


						}

					}
					, error: function (request, status, error) {
						alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					}
				});


			};

			scwin.btn_create_signingkey_setting_onclick = function(e){

				var key_setting_mode = localStorage.getItem("_key_setting_mode_");
                if(key_setting_mode == "detailview"){
					scwin.updateSigningKeySettingData();
				}else {
					scwin.saveSigningKeySettingData();
				}


			};

			scwin.setting_select_platform_onchange = function(e){

			    var platform = setting_select_platform.getValue();
				if(platform == "Android"){
					// android body show
					gal_body_android.show();
					gal_body_ios.hide();

				}else if(platform == "iOS"){
					// ios body show
					gal_body_android.hide();
					gal_body_ios.show();

				}

			};


			scwin.setting_key_name_check_onclick = function(){

				var key_name = signingkey_setting_name.getValue();

				if(common.isEmptyStr(key_name)){
					var message = common.getLabel("lbl_check_key_name");
					alert(message);
					return false;
				}

				scwin.select_check_key_name(key_name);

			};

			// websocket 으로 결과 받아서 message -> ajax 로 update 할 값을 전송한다.
			scwin.webSocketCallback = function(obj) {
				// msg type 추가
				switch (obj.MsgType) {
					case "BIN_FILE_PROFILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
						// scwin.setBuilderKeyFileCreateStatus(obj);
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

						break;
					case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER" :
						// scwin.setBuilderiOSKeyFileCreateStatus(obj);
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					    break;
					case "BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO_FROM_HEADQUATER" :
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");
						// scwin.setBuilderiOSDeployKeyFileCreateStatus(obj);
						break;
					case "BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO_FROM_HEADQUATER" :
						var message = common.getLabel("lbl_key_setting_complete");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");
                        break;
					case "BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO_FROM_HEADQUATER" :
						var message = common.getLabel("lbl_setting_signingkey_detail_keyModify");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");
						break;
					case "BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO_FROM_HEADQUATER" :
						var message = common.getLabel("lbl_setting_signingkey_detail_keyModify");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");
						break;
					default :
						break;
				}
			};

			scwin.setting_select_domain_onchange = function(e){

			    var domainID = setting_domain_list.getValue();

			    if(domainID == ""){
					setting_admin_list.removeAll(false);

                    var message = common.getLabel("lbl_select");
					setting_admin_list.addItem("",message); //선택

				}else {
					scwin.select_admin_list(domainID);
				}

			};

			// file name 한글 체크 함수
			function checkChar(obj) {

				var chrTemp;
				var strTemp = obj.name;
				var strLen = strTemp.length;
				var numeric = false;
				var alpha = false;
				var korean = false;

				if (strLen > 0) {

					for (var i=0; i<strTemp.length; i++)
					{

						chrTemp = strTemp.charCodeAt(i);

						if(chrTemp >= 1 && chrTemp <= 57) {
							numeric = true;
						} else if(chrTemp >= 65 && chrTemp <= 122) {
							alpha = true;
						} else {
							korean = true;
							// alert("한글, 영문, 숫자만 입력하세요.");
							return true;
						}


					}
				}

			};

			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'ftp_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_key_cert_regist_setting'}}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm type1 fl',id:'signingkey_setting_button',style:'',type:'button','ev:onclick':'scwin.btn_create_signingkey_setting_onclick',useLocale:'true',localeRef:'lbl_regist'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body type2',id:'gal_signingkey_main',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_detail_id'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_detail_id'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'signingkey_setting_name',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.setting_key_name_check_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_signingkey_detail_buildServerProfileId'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_detail_separator'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_builder_list',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_os'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_signingkey_detail_osType'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_select_platform',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.setting_select_platform_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Android'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'Android'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'iOS'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'iOS'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'domain_id_grp',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_domain_id'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_domain_list',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.setting_select_domain_onchange'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'admin_id_grp',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_admin_id'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_admin_list',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.setting_select_admin_onchange'},E:[{T:1,N:'xf:choices'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body type2 mt30',id:'gal_body_android',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'w2:tabControl',A:{frameModal:'false',confirmTrueAction:'exist',useTabKeyOnly:'true',confirmFalseAction:'new',useMoveNextTabFocus:'false',tabScroll:'false',useATagBtn:'true',closable:'false',useConfirmMessage:'false',alwaysDraw:'false',style:'width: 100%;height: 600px;',id:'setting_signgingkey_android_tap'}}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body type2 mt30',id:'gal_body_ios',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'w2:tabControl',A:{frameModal:'false',confirmTrueAction:'exist',useTabKeyOnly:'true',confirmFalseAction:'new',useMoveNextTabFocus:'false',tabScroll:'false',useATagBtn:'true',closable:'false',useConfirmMessage:'false',alwaysDraw:'false',style:'width: 100%;height: 600px;',id:'setting_signgingkey_tap'}}]}]}]}]}]}]}]})