/*amd /xml/deploy.xml 14683 cd4aac434092a8d3de0d024fe4b3717ac7bf48b1c57fdbd2cd2ac2ed65715551 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/btn.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/sockjs.min.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.logData = "";
			var interval;
			var classBtnYn = false;
			var isStop = false;
			scwin.onpageload = function() {
				// 현재 나의 scope 객체를 전역 변수에 저장한다.
				common.setScopeObj(scwin);
				//console.log("test deploy");
				var deployActionData = $p.parent().__deployaction_data__.getAllJSON();


				var deployProjectName = deployActionData[0].projectName;
				var platform = deployActionData[0].platform;

                var message_select_default = common.getLabel("lbl_select");
				var message_deploy = common.getLabel("lbl_deploy");
                var message_alpha_eng = common.getLabel("lbl_deploy_js_alpha_eng");
                var message_alpha = common.getLabel("lbl_deploy_js_alpha");
                var message_beta_eng = common.getLabel("lbl_deploy_js_beta_eng");
                var message_beta = common.getLabel("lbl_deploy_js_beta");
                var message_operate = common.getLabel("lbl_operate");
                var message_test = common.getLabel("lbl_deploy_js_test");


				txt_deploy_project_name.setValue(deployProjectName + " " + message_deploy);

				if(platform == "Android"){
					deploy_select_deploytype.addItem("", message_select_default);
					deploy_select_deploytype.addItem(message_alpha_eng, message_alpha); // alpha
					deploy_select_deploytype.addItem(message_beta_eng, message_beta); // Beta
					deploy_select_deploytype.addItem(message_deploy, message_operate); // Deploy
				}else {
					deploy_select_deploytype.addItem("", message_select_default);
					deploy_select_deploytype.addItem(message_beta_eng, message_test); // TestFileget
					deploy_select_deploytype.addItem(message_deploy, message_operate); // Deplop
				}
			};

			scwin.start_deploy_project = function (e) {
					var deployActionData = $p.parent().__deployaction_data__.getAllJSON();

					var sendDBdata = {};
					// 데이터 동적 처리
					sendDBdata.workspaceID = deployActionData[0].workspace_pkid;
					sendDBdata.projectID = deployActionData[0].project_pkid;
					sendDBdata.status = common.getLabel("lbl_deploy_js_start_distribute");
					sendDBdata.log_path = "";
					sendDBdata.message = "";
					sendDBdata.project_history_id = deployActionData[0].project_history_id;
					sendDBdata.platform = deployActionData[0].platform;

					scwin.setBranchDeployInsert(sendDBdata);
			};

			scwin.initPage = function(){

			};

			scwin.webSocketCallback = function(obj) {
				//console.log(obj);
				switch (obj.MsgType) {
					case "HV_MSG_DEPLOY_STATUS_INFO_FROM_HEADQUATER" :
						scwin.setBranchDeployStatus(obj);
						break;
					default :
						break;
				}
			};

			function intervalYnfunc() {
				interval = setInterval(function() {
					if (!isStop) {
						if(classBtnYn){
							project_build_build_result.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
							classBtnYn = false;
						}else {
							project_build_build_result.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
							classBtnYn = true;
						}
					} else {

						clearInterval(interval);
						// 밖에서 선언한 interval을 안에서 중지시킬 수 있음
					}
				}, 500)

			};

			scwin.setBranchDeployStatus = function ( data ) {
				switch (data.message) {
					case "STOP" :
					    alert(data.logValue);
						break;
					case "DEPLOYING" :
					    // 깜박임
						// btn_cm type2 icon stop
						// project_build_clean_build.changeClass("txt_name fail","txt_name success");
						// project_build_clean_build.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						// project_build_status.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						var message = common.getLabel("lbl_deploy_js_distributing");
						project_build_build_result.setLabel(message);

                        // deploy start
						var logData = data.logValue;
						if(!(btn4.hasClass("active")) && logData == "deploy start"){
							btn2_progress_img.changeClass("progress-0","progress-50");
                            // console.log("check deploy");

							document.getElementById("deploy_status_text_id").value = message;

							intervalYnfunc();
						}

						scwin.setBranchDeployLog(data);
						break;
					case "FILEUPLOADING" :
						project_build_building.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						//project_build_ftp_upload.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						var message = common.getLabel("lbl_app_uploading");
						WebSquare.layer.showProcessMessage(message);
						break;
					case "SUCCESSFUL" :
                        var message_complete = common.getLabel("lbl_deploy_js_complete_distribute");
                        var message_completed = common.getLabel("lbl_deploy_js_completed_distribute");
						project_build_build_result.setLabel(message_complete);
						isStop = true;
						//project_build_ftp_upload.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						//project_build_building.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						project_build_build_result.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						btn2_progress_img.changeClass("progress-50","progress-100");

						// scwin.setBranchBuildUpdate(data);
						//console.log(data);
						if(data.message == "SUCCESSFUL"){
							// alert(data.buildHistoryObj.buildProjectName + " 배포, 완료 되었습니다.");
							alert(message_completed);
						}else if(data.message == "FAILED"){
							// alert(data.buildHistoryObj.buildProjectName + " 배포, 실패 되었습니다.");
						}

						btn_log_download.setUserData("history_id",data.history_id);
						btn_log_download.show("");

						scwin.logData = "";

						project_build_start.setDisabled(true);
						// resultLayer.show();
						scwin.logData = "";

						localStorage.setItem("_buildFromProjSetting_","stop");

						var platform = localStorage.getItem("buildPlatform");
						// console.log("platform : " + platform);
						if (platform == "Windows"){

						}else {

							// scwin.buildAfterQrcodeCreate(data.qrCode);
						}

						break;
					case "FAILED" :
						isStop = true;
						// project_build_ftp_upload.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						project_build_build_result.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						// project_build_building.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						var message = common.getLabel("lbl_deploy_js_fail_distribute");
						project_build_build_result.setLabel(message);
						btn2_progress_img.changeClass("progress-50","progress-100");

						btn_log_download.setUserData("history_id",data.history_id);
						btn_log_download.show("");

						scwin.logData = "";

						project_build_start.setDisabled(true);
						// resultLayer.show();
						scwin.logData = "";

						localStorage.setItem("_buildFromProjSetting_","stop");

						// scwin.setBranchBuildUpdate(data);
						break;
					case "CLEANBUILING" :
						// project_build_clean_build.changeClass("btn_cm type2 icon stop","btn_cm type3 icon stop");
						// project_build_status.changeClass("btn_cm type3 icon stop","btn_cm type2 icon stop");
						scwin.setBranchDeployLog(data);
						// scwin.setBranchBuildUpdate(data);
						break;
					default :
						break;
				}

			};

			// build log view
			scwin.setBranchDeployLog = function (data){
				// txtarea_log
				var logData = data.logValue;
				var txtarea_log = document.getElementById("txtarea_log");
				txtarea_log.focus();
				logData = logData.replace(/\u001b[^m]*?m/g,"");
				scwin.logData += logData + "\n";

				txtarea_log.value = scwin.logData + "\n";
				txtarea_log.scrollTop = txtarea_log.scrollHeight - txtarea_log.clientHeight;

			};

			// db insert 처리 구간을 -> handler 내부에서 service 로 받아서 처리 방식 수정
			scwin.setBranchDeployInsert = function(data){
				var sendData = {};
				var deployHistory = {};

				// var whive_session = sessionStorage.getItem("__whybrid_session__");
				// whive_session = JSON.parse(whive_session);

				deployHistory.project_id = data.projectID;
				deployHistory.platform_type = data.platform;// platform type  수정하기
				// sendData.msgType = "HV_MSG_RELEASE_BUILD";
				// sendData.sessType = "BRANCH";
				sendData.workspace_id = data.workspaceID;
				sendData.deploy_type = deploy_select_deploytype.getValue();
				sendData.project_history_id = data.project_history_id;
				// sendData.hqKey = whive_session.user_login_id; // hqKey 세션 아이디
				// sendData.user_id = whive_session.id;
				// sendData.domain_id = whive_session.domain_id;
				sendData.deployHistory = deployHistory;

				$.ajax({
					url : "/manager/deploy/history/create",
					type : "post",
					accept : "application/json",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(sendData),
					dataType : "json",
					success : function(r,status) {
						if(status === "success"){
							// build 버튼 disabled
							project_build_start.setDisabled(true);

							localStorage.setItem("_buildStatusFlag_", "deploying"); // true
						}
					},
					error:function(request,status,error){
						var message = request.responseJSON;
						alert("message:"+message.result+"\n");
					}
				});
			};

			scwin.success_android_callback = function(response){

			};

			scwin.success_ios_callback = function(response){

			};

			scwin.error_callback = function(XMLHttpRequest, textStatus, errorThrown){
				alert("Error: " + errorThrown);
			};

			scwin.onpageunload = function() {
				scwin.logData = "";
				localStorage.setItem("_deployStatusFlag_", "deploy_back");
				localStorage.setItem("_deployFromProjSetting_","stop");
				$p.parent().__deployaction_data__.removeAll();
			};

			scwin.btn_qrcode_onclick = function(){
				// resultLayer.show();
			};

			scwin.btn_app_download_onclick = function(e){
				//href click test
				var history_id = this.getUserData("history_id");

				var options = {};

				options.action = "/api/buildhistory/fileDownload/"+ parseInt(history_id);
				options.mode = "asynchronous";
				// application/octet-stream;
				options.mediatype = "application/octet-stream";
				options.method = "";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

					} else {
						// alert("가져오기 화면 이동 실패");
					}
				};

				options.error = function (e) {

					if (request.status == 200){
						window.location = "/api/buildhistory/fileDownload/"+ parseInt(history_id);
					}
				};
				$p.ajax( options );
			};

			scwin.resultLayer_onclose = function() {
				//resultImg.setSrc("");
				resultLayer.hide();
			};

			scwin.start_log_download_onclick = function(e){

				var history_id = this.getUserData("history_id");

				var data = {};
				var options = {};


				data.history_id = history_id;

				options.action = "/manager/deploy/history/download/logFileFromWebSocket";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				// options.method = "GET";
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						var buildproj = [];

					} else {

					}
				};

				options.error = function (e) {
					alert("message:"+e.responseText+"\n");

				};

				$p.ajax( options );

			};

			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'white_board',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'work',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'pgt_tit fl',id:'txt_deploy_project_name',label:'',style:'',useLocale:'true',localeRef:'lbl_deploy_project_name'}},{T:1,N:'br'},{T:1,N:'br'},{T:1,N:'xf:group',A:{class:'fl',id:'',style:''},E:[{T:1,N:'xf:group',A:{style:'',class:'progress-btn',id:'btn4','ev:onclick':'scwin.btn4_onclick'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:data-progress-style',E:[{T:3,text:'fill-back'}]}]},{T:1,N:'xf:group',A:{style:'',id:'btn_progress_text',class:'btn'}},{T:1,N:'xf:group',A:{style:'',id:'btn2_progress_img',class:'progress-0'}}]},{T:1,N:'input',A:{id:'deploy_status_text_id',type:'hidden'}},{T:1,N:'xf:trigger',A:{class:'btn_cm type2 icon stop',id:'project_build_build_result',style:'',type:'button',useLocale:'true',localeRef:'lbl_ready'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'xf:select1',A:{renderType:'native',id:'deploy_select_deploytype',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'height: 22px;',direction:'auto',chooseOption:'',displayMode:'label',allOption:'',submenuSize:'auto',disabled:'false'},E:[{T:1,N:'xf:choices'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm type1 icon redo',id:'project_build_start',style:'',type:'button','ev:onclick':'scwin.start_deploy_project',useLocale:'true',localeRef:'lbl_deploy_project_build_start'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:anchor',A:{id:'btn_log_download',style:'display:none;',class:'btn_cm type2 type4',outerDiv:'false',href:'',type:'button','ev:onclick':'scwin.start_log_download_onclick',useLocale:'true',localeRef:'lbl_log_download'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'textarea',A:{id:'txtarea_log',class:'console_box',style:'height: 600px;font-family: Monospace;',placeholder:'',readOnly:'true'}},{T:1,N:'xf:group',A:{id:'',style:'',class:'pgtbox'}}]}]}]},{T:1,N:'w2:floatingLayer',A:{id:'resultLayer',title:'',style:'position:absolute; display:none;width:300px; height:300px; z-index:9999;','ev:onclose':'scwin.resultLayer_onclose',useModal:'false',useLocale:'true',localeRef:'lbl_deploy_result_layer_title'},E:[{T:1,N:'div',A:{id:'qrcode',style:'height:300px;'}}]}]}]}]})