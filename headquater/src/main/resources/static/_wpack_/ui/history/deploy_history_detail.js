/*amd /ui/history/deploy_history_detail.xml 6849 37ac478411bd8351514caeb070e1fcb14e0454033f9458518ce2c8cd608da0d9 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function () {
							const historyProjectId = $p.top().scwin.deployHistoryProjectId;

							let data = {};
							data.projectID = historyProjectId;

							const url = common.uri.deployHistoryDetailList;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=UTF-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									scwin.selecthistorydetail(res);

									$(".qrcode").click(function () {
										$(".layer_pop").css("display", "block").css("width", "180px").css("height", "220px");
										$("body").append("<div class='dim'></div>");
									});
									$(".btn_pop_close").click(function () {
										$(".layer_pop").css("display", "none");
										$("div").remove(".dim");
									});
								});
						};

						scwin.setBuilderAppDownloadHref = function (data) {
							// app file 다운로드
							window.location = "/manager/build/history/downloadSetupFile/" + data.filename;
						}

						scwin.selecthistorydetail = function (data) {
							const historyProjectName = $p.top().scwin.deployHistoryProjectName;

							const message = common.getLabel("lbl_project_history");
							txt_project_history_name.setValue(historyProjectName + " " + message); // Project 기록

							for (const [idx, value] of data.entries()) {
								list_deploy_history_detail_generator.insertChild();

								let txt_proj_history_list = list_deploy_history_detail_generator.getChild(idx, "txt_proj_history");
								let txt_build_start_date_list = list_deploy_history_detail_generator.getChild(idx, "txt_build_start_date");
								let txt_build_end_date_list = list_deploy_history_detail_generator.getChild(idx, "txt_build_end_date");
								let txt_history_build_project_name_list = list_deploy_history_detail_generator.getChild(idx, "txt_history_build_project_name");
								let txt_history_logfile_download_url_list = list_deploy_history_detail_generator.getChild(idx, "history_logfile_url");

								if (value.result === "SUCCESSFUL") {
									txt_proj_history_list.changeClass("txt_name fail", "txt_name success");
								} else {
									txt_proj_history_list.changeClass("txt_name success", "txt_name fail");
								}

								if (value.deploy_start_date != null) {
									value.deploy_start_date = value.deploy_start_date.replace(/T/g, ' ');
								}

								if (value.deploy_end_date != null) {
									value.deploy_end_date = value.deploy_end_date.replace(/T/g, ' ');
								}

								const message_status = common.getLabel("lbl_state");
								const message_dist_start_time = common.getLabel("lbl_deploy_history_detail_js_distribute_start_time");
								const message_dist_end_time = common.getLabel("lbl_deploy_history_detail_js_distribute_end_time");
								const message_project_name = common.getLabel("lbl_project_name");

								txt_proj_history_list.setValue(message_status + " : " + value.result);
								txt_build_start_date_list.setValue(message_dist_start_time + " : " + value.deploy_start_date);
								txt_build_end_date_list.setValue(message_dist_end_time + " : " + value.deploy_end_date);
								txt_history_build_project_name_list.setValue(message_project_name + " : " + historyProjectName);

								// history id bind
								txt_history_logfile_download_url_list.setUserData("historyid", value.deploy_history_id);
							}
						};

						scwin.getHistoryFileUrl_onclick = function () {
							const history_id = this.getUserData("historyid");

							let data = {};
							data.history_id = history_id;

							const url = common.uri.deployHistoryLogFileDownload;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=UTF-8"};

							common.http.justFetch(url, method, headers, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_deploy_history_list_title'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'txt_project_history_name',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_history'}}]},{T:1,N:'xf:group',A:{class:'acd_congrp historygrp',id:'',style:''},E:[{T:1,N:'w2:generator',A:{style:'',id:'list_deploy_history_detail_generator',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{class:'acd_itembox',id:''},E:[{T:1,N:'xf:group',A:{class:'acd_item',id:''},E:[{T:1,N:'xf:group',A:{class:'acd_txtbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'acd_itemtxt success',id:'txt_proj_history',label:'',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_whether_build'}},{T:1,N:'xf:group',A:{id:'',class:'recbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_build_start_date',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_build_end_date',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_history_build_project_name',label:'',class:'acd_itemtxt'}}]}]}]},{T:1,N:'xf:group',A:{class:'acd_icobox',id:''},E:[{T:1,N:'w2:anchor',A:{class:'btn_cm icon text btn_i_download',id:'history_logfile_url',outerDiv:'false',style:'',tooltip:'','ev:onclick':'scwin.getHistoryFileUrl_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_log_download',tooltipLocaleRef:'lbl_deploy_history_detail_tooltip_history_logfile_url'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_qrcode'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''},E:[{T:1,N:'div',A:{id:'history_qrcode',style:'width: 290px;height:330px;'}}]}]}]}]}]})