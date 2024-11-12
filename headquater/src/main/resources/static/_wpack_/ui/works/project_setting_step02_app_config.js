/*amd /cm/ui/works/project_setting_step02_app_config.xml 39462 1d537ed5097e2295b67ce2a7aa1610228f7bd154fc9087c41534420f4a7b968a */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dtl_ios_profile',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'name',name:'name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'value',name:'value',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dtl_ios_debug_profile',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'name',name:'name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'value',name:'value',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.app_icon_upload_check_yn = false;
						scwin.app_id_license_check_yn = false;
						scwin.app_id_temp_save = "";
						serverinfo_itemIdx = 0;

						scwin.platformType = "";

						scwin.onpageload = function () {
							document.getElementById("step2_android_input_icons_path").addEventListener("change",scwin.iconAndroidChange,false);
							document.getElementById("step2_ios_input_icons_path").addEventListener("change",scwin.iconiOSChange,false);
							let message = ""

							const paramData = $p.getParameter("tabParam");
							scwin.platformType = $p.parent().scwin.step01Data[0].platform;

							if (scwin.platformType == "Android") {
								grp_iOS.hide();
								scwin.setAndroidConfigData(paramData.profile);
							} else if(scwin.platformType == "iOS") {
								grp_Android.hide();
								scwin.setiOSConfigData(paramData.target);

								// profile
								let profileArr = paramData.profilesList.map((item)=>{
									return {"name":item,"value":item};
								});
								dtl_ios_profile.setJSON(profileArr);
								step2_select_profile_key_name.setSelectedIndex(1);
								// debug profile
								let debugProfileArr = paramData.profilesDebugList.map((item)=>{
									return {"name":item,"value":item};
								});
								dtl_ios_debug_profile.setJSON(debugProfileArr);
								step2_select_debug_profile_key_name.setSelectedIndex(1);
							} else {
								grp_iOS.hide();
								grp_Android.hide();
								common.win.alert("platformType not found");
							}
						};

						scwin.iconiOSChange = function(e) {
							const file = e.target.files[0];
							let reader = new FileReader();

							reader.addEventListener("load",function () {
								step2_icon_ios.setSrc(this.result);
							},false);

							if (file) {
								reader.readAsDataURL(file);
							}
						};

						scwin.iconAndroidChange = function(e){
							const file = e.target.files[0];
							let reader = new FileReader();

							reader.addEventListener("load",function () {
								step2_icon_android.setSrc(this.result);
							},false);

							if (file) {
								reader.readAsDataURL(file);
							}
						};

						// 안드로이드 프로젝트 데이터 설정
						scwin.setAndroidConfigData = function(data){
							// appID
							step2_android_input_app_id.setValue(data.applicationId)
							// appName
							step2_android_input_app_name.setValue(data.appName);
							// appVersion
							step2_android_input_app_version.setValue(data.appVersion);
							// appVersionCode
							step2_android_input_app_version_code.setValue(data.appVersionCode);
							// packageName
							step2_input_packagename.setValue(data.applicationId);
							// min sdk
							step2_select_minsdk_version.setValue(data.minSdkVersion);
							// icon 이미지
							// step2_icon_android.setSrc();
							// 서버정보 설정
							scwin.setAndroidServerInfo(data.server[0].data);
						};

						// iOS 프로젝트 데이터 설정
						scwin.setiOSConfigData = function(data) {
							// target아래에 configuration이 release 와 debug가 기존적으로 존재한다. 같은내용을 세팅하기 때문에 release를 사용한다.

							// appID
							step2_ios_input_app_id.setValue(data.Release.applicationID);

							// appName
							step2_ios_input_app_name.setValue(data.Release.appName);

							// appVersion
							step2_ios_input_app_version.setValue(data.Release.version);

							// appVersionCode
							step2_ios_input_app_version_code.setValue(data.Release.versionCode);

							// xcode project name
							const xcodeprojName = $p.parent().$p.parent().scwin.getResultAppConfigData.resultAppConfigListObj.XCodeProjectName;
							step2_ios_input_projectname.setValue(xcodeprojName);

							// iOS target version
							step2_select_target_version.setValue(data.Release.deploymentTarget);


							// icon 이미지
							// step2_icon_ios.setSrc();

							// 서버정보 설정
							let serverGroupName = data.Release.startServerGroupName;
							let serverArr = Object.keys(data.Release.serverGroups).filter(key => key == serverGroupName);
							if(Array.isArray(serverArr) && serverArr.length > 0){
								scwin.setiOSServerInfo(data.Release.serverGroups[serverArr[0]]);
							}
						};

						scwin.setAndroidServerInfo = function(data){
							const cnt = data.length;
							for(let idx=0;idx<cnt;idx++){
								if (idx == 0){
									input_servername_1.setValue(data[idx].name);
									input_appid_1.setValue(data[idx].appId);
									input_serverurl_1.setValue(data[idx].url);
								} else {
									serverInfoGen.insertChild(idx-1);
									let ibx_servername = serverInfoGen.getChild(idx-1,"ibx_servername");
									let ibx_appid = serverInfoGen.getChild(idx-1,"ibx_appid");
									let ibx_serverurl = serverInfoGen.getChild(idx-1,"ibx_serverurl");
									ibx_servername.setValue(data[idx].name);
									ibx_appid.setValue(data[idx].appId);
									ibx_serverurl.setValue(data[idx].url);
								}
							}
						};



						scwin.setiOSServerInfo = function(data){
							const cnt = data.length;
							for(let idx=0;idx<cnt;idx++){
								if (idx == 0){
									input_servername_1.setValue(data[idx].name);
									input_appid_1.setValue(data[idx].appID);
									input_serverurl_1.setValue(data[idx].url);
								} else {
									serverInfoGen.insertChild(idx-1);
									let ibx_servername = serverInfoGen.getChild(idx-1,"ibx_servername");
									let ibx_appid = serverInfoGen.getChild(idx-1,"ibx_appid");
									let ibx_serverurl = serverInfoGen.getChild(idx-1,"ibx_serverurl");
									ibx_servername.setValue(data[idx].name);
									ibx_appid.setValue(data[idx].appID);
									ibx_serverurl.setValue(data[idx].url);
								}
							}
						};

						scwin.setBranchPluginListStatus = function (data) {
							if (data.message == "SEARCHING") {
								const message = common.getLabel("lbl_uploading"); // Uploading
								WebSquare.layer.showProcessMessage(message);
								//console.log("검색중..");
							} else if (data.message == "SUCCESSFUL") {
								WebSquare.layer.hideProcessMessage();
								//console.log("처리완료");
							} else if (data.message == "FAILED") {
								//console.log("실패");
							}
						};

						scwin.getMultiDomainInfo = async function () {
							const uri = common.uri.getInfo;
							const resData = await common.http.fetch(uri,"POST",{"Content-Type":"application/json"},JSON.stringify({"projectID": project_id}));
							if(resData != null && resData.length > 0){
								selectedGeneralIosInfo.appId = data[0].ApplicationID;
							} else {
								common.win.alert("error: multiDomainInfo");
							}
						};

						// license check
						scwin.select_check_app_id = async function (obj) {
							let uri = common.uri.checkLicense;
							common.http.fetch(uri,"POST",{"Content-Type":"application/json"},obj).then((resData)=>{
								if(resData != null){
									if(Array.isArray(resData) && resData[0].result == "success") {
										const message = common.getLabel("lbl_can_use_appid");
										common.win.alert(obj.appID + " : " + message); //APPID는 사용 가능 합니다.

										// flag 처리
										scwin.app_id_license_check_yn = true;
										$p.parent().$p.parent().scwin.app_id_license_check_yn = true;
										scwin.app_id_temp_save = obj.appID;
									} else {
										common.win.alert(resData[0].error);
									}
								} else {
									common.win.alert("checkLicense error: resData is null");
								}
							}).catch((err)=>{
								common.win.alert("checkLicense error: "+err[0].result);
							});

						};

						// AppID Check
						scwin.btn_check_android_app_id_onclick = function(e) {
							let obj = {};
							let appID = step2_android_input_app_id.getValue();
							let appIDCheck = "";

							if (common.isEmptyStr(appID)) {
								const message = common.getLabel("lbl_input_appid");
								common.win.alert(message); //APP ID를 입력하세요
								scwin.app_id_license_check_yn = false;
								return false;
							}

							appIDCheck = appID.split("\.");
							const message_appid_form = common.getLabel("lbl_appid_check_form");

							if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
								for (let i = 0; i < appIDCheck.length; i++) {
									const strAppID = appIDCheck[i];
									const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);
									if (!check_app_id_str) {
										common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
										return false;
									}
								}
							} else {
								common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
								return false;
							}

							obj.appID = appID;
							obj.platform = "Android";
							scwin.select_check_app_id(obj);
						};

						// AppID Check
						scwin.btn_check_ios_app_id_onclick = function(e) {
							let obj = {};
							let appID = step2_ios_input_app_id.getValue();
							let appIDCheck = "";
							if (common.isEmptyStr(appID)) {
								const message = common.getLabel("lbl_input_appid");
								common.win.alert(message); //APP ID를 입력하세요
								scwin.app_id_license_check_yn = false;
								return false;
							}
							appIDCheck = appID.split("\.");
							const message_appid_form = common.getLabel("lbl_appid_check_form");

							if (appIDCheck.length >= 3 || appIDCheck.length >= 4) {
								for (let i = 0; i < appIDCheck.length; i++) {
									const strAppID = appIDCheck[i];

									const check_app_id_str = common.checkAllInputText("CHECK_INPUT_TYPE_APP_ID2", strAppID);

									if (!check_app_id_str) {
										common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
										return false;
									}
								}

							} else {
								common.win.alert(appID + ": " + message_appid_form); //앱 구분자 형식에 맞지 않습니다. \n com.inswave.whybrid 이거나 kr.co.inswave.whybrid 형식으로 입력해야합니다
								return false;
							}

							obj.appID = appID;
							obj.platform = "iOS";

							scwin.select_check_app_id(obj);
						};

						scwin.changeToolTipContentAddStep2 = function(componentId, label) {
							const message_tooltip_android_appid = common.getLabel("lbl_android_appid");
							const message_tooltip_android_app_name = common.getLabel("lbl_app_name");
							const message_tooltip_android_app_version = common.getLabel("lbl_app_version");
							const message_tooltip_android_app_version_code = common.getLabel("lbl_app_version_code");
							const message_tooltip_android_min_sdk = common.getLabel("lbl_min_os_version");
							const message_tooltip_ios_appid = common.getLabel("lbl_ios_appid");
							const message_tooltip_ios_app_name = common.getLabel("lbl_ios_app_name");
							const message_tooltip_ios_app_version = common.getLabel("lbl_ios_app_version");
							const message_tooltip_ios_app_version_code = common.getLabel("lbl_ios_app_version_code");
							const message_tooltip_ios_min_sdk = common.getLabel("lbl_ios_min_os_version");

							switch (componentId) {
								case "wfm_main_wfm_project_add_step2_android_appid_tooltip":
									return message_tooltip_android_appid //앱을 구분하기 위한 ID <br>Android : Application ID
								case "wfm_main_wfm_project_add_step2_android_appname_tooltip":
									return message_tooltip_android_app_name //기기에 표시될 앱 이름
								case "wfm_main_wfm_project_add_step2_android_appversion_tooltip":
									return message_tooltip_android_app_version //앱 버전명
								case "wfm_main_wfm_project_add_step2_android_appversioncode_tooltip":
									return message_tooltip_android_app_version_code //앱 버전 코드
								case "wfm_main_wfm_project_add_step2_android_minsdk_tooltip":
									return message_tooltip_android_min_sdk //최소 지원 OS버전 <br>21 - Android 5.0 (Lollipop)<br>22 - Android 5.1 (Lollipop)<br>23 - Android 6.0 (Marshmallow)<br>24 - Android 7.0 (Nougat)<br>25 - Android 7.1.1 (Nougat)<br>26 - Android 8.0 (Oreo)<br>27 - Android 8.1 (Oreo)<br>28 - Android 9.0 (Pie)<br>29 - Android 10.0 (Q)<br>30 - Android 11.0 (R)<br>31 - Android 12.0 (S)
								case "wfm_main_wfm_project_add_step2_ios_appid_tooltip":
									return message_tooltip_ios_appid //앱을 구분하기 위한 ID <br>iOS : Bundle Identifier
								case "wfm_main_wfm_project_add_step2_ios_appname_tooltip":
									return message_tooltip_ios_app_name //기기에 표시될 앱 이름 <br>iOS : Display Name(Product Name)
								case "wfm_main_wfm_project_add_step2_ios_appversion_tooltip":
									return message_tooltip_ios_app_version //앱 버전명 <br>iOS : Version(Market Version)
								case "wfm_main_wfm_project_add_step2_ios_appversioncode_tooltip":
									return message_tooltip_ios_app_version_code //앱 버전 코드<br>iOS : Build(Current Project Version)
								case "wfm_main_wfm_project_add_step2_ios_minsdk_tooltip":
									return message_tooltip_ios_min_sdk //최소 지원 OS버전<br>12.0 ~ lastest<br>13.0 ~ lastest<br>14.0 ~ lastest<br>15.0 ~ lastest
								default:
									return ""
							}
						};

						// server form 추가
						scwin.btn_serverInfo_add_onclick = function(e){
							serverInfoGen.insertChild();
						};

						// server form 삭제
						scwin.btn_serverinfo_remove_onclick = function (e) {
							const idx = this.getGeneratedIndex();
							serverInfoGen.removeChild(idx);
						};


					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'grp_Android',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_android_app_default_info_change'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_appid',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_android_appid',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_id',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_check_android_app_id',style:'',type:'button','ev:onclick':'scwin.btn_check_android_app_id_onclick',useLocale:'true',localeRef:'lbl_appid_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_name',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_displayed_app_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_version',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_app_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_version',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_app_version_code',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_android_input_app_version_code',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_package_name',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_use_package_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_input_packagename',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_min_os_version',ref:'',style:'',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_android_min_os_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{renderType:'auto',id:'step2_select_minsdk_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'21'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'21'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'22'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'22'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'23'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'23'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'24'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'24'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'25'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'25'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'26'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'26'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'27'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'27'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'28'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'28'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'29'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'29'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'30'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'30'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'31'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'31'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_icon',ref:'',style:'',userData2:''}}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'input',A:{type:'file',id:'step2_android_input_icons_path',class:'w2upload_input',accept:'image/png'}},{T:1,N:'xf:group',A:{class:'appiconbox',id:'grp_android_app_icon_id',style:''},E:[{T:1,N:'xf:group',A:{class:'appicon_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'appicon_item w192',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{alt:'appicon이미지',id:'step2_icon_android',src:'/cm/images/contents/appicon_default_bg.svg',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_img_w1024'}},{T:1,N:'w2:anchor',A:{class:'btn_cm icon btn_i_reset2',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'dfbox',id:'grp_iOS',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_ios_app_default_info_change',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_appid',userData2:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_ios_appid'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_id',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'btn_check_ios_app_id',style:'',type:'button','ev:onclick':'scwin.btn_check_ios_app_id_onclick',useLocale:'true',localeRef:'lbl_appid_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_name',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_version',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_version_code'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_app_version_code',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_app_version_code',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_xcode_project_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_project_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'step2_ios_input_projectname',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_min_os_version'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_ios_min_os_version',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:select1',A:{renderType:'auto',id:'step2_select_target_version',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step2_select_target_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'12.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'12.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'13.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'13.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'14.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'14.0'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'15.0'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'15.0'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_rel_profile_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_rel_profile_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{id:'step2_select_profile_key_name',chooseOption:'true',style:'',submenuSize:'auto',allOption:'',disabled:'false',direction:'auto',appearance:'minimal',disabledClass:'w2selectbox_disabled',renderType:'auto',ref:'',chooseOptionLabel:'선택'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dtl_ios_profile'},E:[{T:1,N:'xf:label',A:{ref:'name'}},{T:1,N:'xf:value',A:{ref:'value'}}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_dev_profile_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',escape:'false',id:'',label:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_dev_profile_name',userData2:''}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:select1',A:{allOption:'',appearance:'minimal',chooseOption:'true',chooseOptionLabel:'선택',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'step2_select_debug_profile_key_name',ref:'',renderType:'auto',style:'',submenuSize:'auto'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:itemset',A:{nodeset:'data:dtl_ios_debug_profile'},E:[{T:1,N:'xf:label',A:{ref:'name'}},{T:1,N:'xf:value',A:{ref:'value'}}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_app_icon'}}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'input',A:{type:'file',id:'step2_ios_input_icons_path',class:'',style:'width:100%;',onchange:'iconChange',accept:'image/png'}},{T:1,N:'xf:group',A:{class:'appiconbox',id:'grp_ios_app_icon_id',style:''},E:[{T:1,N:'xf:group',A:{class:'appicon_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'appicon_item w192',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'xf:image',A:{alt:'appicon이미지',id:'step2_icon_ios',src:'/cm/images/contents/appicon_default_bg.svg',style:''}}]},{T:1,N:'w2:span',A:{id:'',label:'',useLocale:'true',localeRef:'lbl_img_w1024',style:''}},{T:1,N:'w2:anchor',A:{class:'btn_cm icon btn_i_reset2',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_reset'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'dfbox'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project_setting_btn_project_step03',class:''}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_server_name',class:''}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',style:'',userData2:'',id:'',label:'',class:'ico_tip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_name'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_servername_1',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_used_appid',id:'',label:'',class:'ico_tip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_appid_1',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_server_address',class:''}},{T:1,N:'w2:textbox',A:{ref:'',tagname:'span',tooltip:'tooltip',style:'',userData2:'',id:'',label:'',class:'ico_tip',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_address'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'input_serverurl_1',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_serverInfo_add',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_serverInfo_add_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'genbox'},E:[{T:1,N:'w2:generator',A:{tagname:'ul',style:'',id:'serverInfoGen',class:''},E:[{T:1,N:'xf:group',A:{tagname:'li',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}},{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_server_name',class:''}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_name'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'ibx_servername',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_appid'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_used_appid'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{style:'width:100%;',id:'ibx_appid',class:''}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',useLocale:'true',localeRef:'lbl_server_address'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',id:'',label:'',ref:'',style:'',tagname:'span',tooltip:'tooltip',userData2:'',useLocale:'true',tooltipLocaleRef:'lbl_wmatrix_server_address'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'3'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'ibx_serverurl',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_serverinfo_remove',type:'button',class:'btn_cm icon btn_i_minus','ev:onclick':'scwin.btn_serverinfo_remove_onclick',useLocale:'true',localeRef:'lbl_del_info'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'dfbox',id:'grp_Windows',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_windows_app_default_info',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}},{T:1,N:'xf:group',A:{style:'width:150px;',tagname:'col'}},{T:1,N:'xf:group',A:{tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_version'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_op_mode'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'',style:'width:100%;'}}]}]}]}]}]}]}]}]})