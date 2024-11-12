/*amd /cm/ui/history/history_detail.xml 11588 e0c34248114a7326b0a14b638d7396a5c10ec6a4b2873249c5e0c761d0137cb5 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = async function () {
							const historyPlatform = $p.top().scwin.historyPlatform;
							const historyProjectName = $p.top().scwin.historyProjectName;
							const historyProjectId = $p.top().scwin.historyProjectId;

							const message = common.getLabel("lbl_project_history"); // Project 기록

							txt_project_history_name.setValue(historyProjectName + " " + message);

							const url = common.uri.buildHistoryDetailList(parseInt(historyProjectId), historyPlatform, historyProjectName);
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.selecthistorydetail(data);
						};

						scwin.setBuilderAppDownloadHref = function (data) {
							// app file 다운로드
							window.location = "/manager/build/history/downloadSetupFile/" + data.filename;
						}

						scwin.selecthistorydetail = function (data) {
							let historyPlatform = $p.top().scwin.historyPlatform;
							let historyWorkspaceId = $p.top().scwin.historyWorkspaceId;

							for (const [idx, value] of data.entries()) {
								list_build_history_detail_generator.insertChild();

								let txt_proj_history_list = list_build_history_detail_generator.getChild(idx, "txt_proj_history");
								let txt_build_start_date_list = list_build_history_detail_generator.getChild(idx, "txt_build_start_date");
								let txt_build_end_date_list = list_build_history_detail_generator.getChild(idx, "txt_build_end_date");
								let txt_history_build_project_name_list = list_build_history_detail_generator.getChild(idx, "txt_history_build_project_name");
								let txt_history_app_download_url_list = list_build_history_detail_generator.getChild(idx, "history_appfile_url");
								let txt_history_logfile_download_url_list = list_build_history_detail_generator.getChild(idx, "history_logfile_url");
								let txt_history_qrcode_download_url_list = list_build_history_detail_generator.getChild(idx, "history_qrcode_url");
								let btn_deploy_list = list_build_history_detail_generator.getChild(idx, "btn_deploy");

								// txt_name success
								if (value.result === "SUCCESSFUL") {
									txt_proj_history_list.changeClass("txt_name fail", "txt_name success");

									txt_history_app_download_url_list.setUserData("historyid", value.project_history_id); //

									txt_history_qrcode_download_url_list.setUserData("history_qrcode", value.project_history_id);

									btn_deploy_list.setUserData("platform", historyPlatform);
									btn_deploy_list.setUserData("projectName", value.project_name);
									btn_deploy_list.setUserData("project_pkid", value.project_id);
									btn_deploy_list.setUserData("project_history_id", value.project_history_id);
									btn_deploy_list.setUserData("workspace_id", historyWorkspaceId);
								} else {
									txt_proj_history_list.changeClass("txt_name success", "txt_name fail");

									txt_history_app_download_url_list.hide();
									txt_history_qrcode_download_url_list.hide();
									btn_deploy_list.hide();
								}

								if (value.history_started_date != null) {
									value.history_started_date = value.history_started_date.replace(/T/g, ' ');
								}

								if (value.history_ended_date != null) {
									value.history_ended_date = value.history_ended_date.replace(/T/g, ' ');
								}

								const message_state = common.getLabel("lbl_state");
								const message_history_started_date = common.getLabel("lbl_history_detail_js_history_started_date");
								const message_history_ended_date = common.getLabel("lbl_history_detail_js_history_ended_date");
								const message_project_name = common.getLabel("lbl_project_name");

								txt_proj_history_list.setValue(message_state + " : " + (value.result != common.getLabel("lbl_successful_eng") ? common.getLabel("lbl_failed") : common.getLabel("lbl_successful")));
								txt_build_start_date_list.setValue(message_history_started_date + " : " + value.history_started_date);
								txt_build_end_date_list.setValue(message_history_ended_date + " : " + value.history_ended_date);
								txt_history_build_project_name_list.setValue(message_project_name + " : " + value.project_name);

								// history id bind
								txt_history_logfile_download_url_list.setUserData("historyid", value.project_history_id);
							}
						};

						scwin.gethistoryfileurl_onclick = function () {
							const history_id = this.getUserData("historyid");

							let data = {};
							data.history_id = history_id;

							const url = common.uri.buildHistoryLogFileDownload;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.fetch(url, method, headers, data)
						};

						/*
                            Build History의 App Download 버튼을 누르면,
                            웹서버에서 해당 apk or ipa 파일을 다운 받는다.
                            @soorink
                         */
						scwin.gethistoryappfile_onclick = function () {
							const history_id = this.getUserData("historyid");

							let data = {};
							data.history_id = history_id;

							const url = common.uri.buildHistoryAppFileDownload;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=UTF-8"};
							const option = {type: 'application/octet-stream'};

							common.http.fileDownload(url, method, headers, data, option);
						};

						scwin.getHistoryQRCodeUrl_onclick = function () {
							const history_qrcode = this.getUserData("history_qrcode");

							let data = {};
							data.historyCnt = history_qrcode;

							scwin.buildAfterQrcodeCreateByID(data);
						};

						scwin.btn_deploy_onclick = function () {
							const platform = this.getUserData("platform");
							const deployProjectName = this.getUserData("projectName");
							const deploy_project_id = this.getUserData("project_pkid");
							const project_history_id = this.getUserData("project_history_id");
							const workspace_id = this.getUserData("workspace_id");

							let deployAction = [];

							let data = {};
							data.platform = platform;
							data.projectName = deployProjectName;
							data.project_pkid = deploy_project_id;
							data.project_history_id = project_history_id;
							data.workspace_pkid = workspace_id;

							deployAction.push(data);

							$p.parent().__deployaction_data__.setJSON(deployAction);

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0102000000");
							$p.top().scwin.add_tab(menu_key);
							WebSquare.getBody().hideToolTip();

							// $p.parent().wfm_main.setSrc("/xml/deploy.xml");
							// WebSquare.getBody().hideToolTip();
						};

						scwin.buildAfterQrcodeCreate = function (qrcodeUrl) {
							$("#history_qrcode").empty();

							new QRCode(document.getElementById("history_qrcode"), {
								text: qrcodeUrl,
								width: 150,
								height: 150,
								colorDark: "#000000",
								colorLight: "#ffffff",
								correctLevel: QRCode.CorrectLevel.H
							});
						};

						scwin.buildAfterQrcodeCreateByID = function (data) {
							requires("uiplugin.popup");

							let rowJSON = {
								"data": data
							};

							let dataObject = {
								"type": "json",
								"name": "param",
								"data": rowJSON
							};

							let opts = {
								"id": "popup_window_qrcode",
								"type": "litewindow",
								"width": 350 + "px",
								"height": 350 + "px",
								"popupName": " ",
								"modal": true,
								"useIFrame": false,
								"title": "",
								"useATagBtn": "true",
								"frameMode": "wframe",
								"dataObject": dataObject
							};

							$p.openPopup("/ui/build/QRCode.xml", opts);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_history_list_title'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'txt_project_history_name',label:'Project 기록',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_whether_build'}}]},{T:1,N:'xf:group',A:{class:'acd_congrp historygrp',id:'',style:''},E:[{T:1,N:'w2:generator',A:{style:'',id:'list_build_history_detail_generator',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{class:'acd_itembox',id:''},E:[{T:1,N:'xf:group',A:{class:'acd_item',id:''},E:[{T:1,N:'xf:group',A:{class:'acd_txtbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'acd_itemtxt success',id:'txt_proj_history',label:'',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_whether_build',tooltipLocaleRef:'lbl_whether_build'}},{T:1,N:'xf:group',A:{id:'',class:'recbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_build_start_date',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_build_end_date',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_history_build_project_name',label:'',class:'acd_itemtxt'}}]}]}]},{T:1,N:'xf:group',A:{class:'acd_icobox',id:''},E:[{T:1,N:'w2:anchor',A:{class:'btn_cm icon text btn_i_download',id:'history_appfile_url',outerDiv:'false',style:'','ev:onclick':'scwin.gethistoryappfile_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_app_download',tooltipLocaleRef:'lbl_history_detail_tooltip_app_file_url'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'btn_cm icon text btn_i_download',id:'history_logfile_url',outerDiv:'false',style:'','ev:onclick':'scwin.gethistoryfileurl_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_log_download',tooltipLocaleRef:'lbl_history_detail_tooltip_log_file_url'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'btn_cm icon btn_i_qrcode',id:'history_qrcode_url',outerDiv:'false',style:'','ev:onclick':'scwin.getHistoryQRCodeUrl_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_history_detail_tooltip_history_qrcode'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'btn_cm icon btn_i_module01',id:'btn_deploy',outerDiv:'false',style:'','ev:onclick':'scwin.btn_deploy_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_history_detail_tooltip_deploy'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_qrcode'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''},E:[{T:1,N:'div',A:{id:'history_qrcode',style:'width: 290px;height:330px;'}}]}]}]}]}]})