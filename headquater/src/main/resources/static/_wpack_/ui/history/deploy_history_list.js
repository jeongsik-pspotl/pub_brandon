/*amd /ui/history/deploy_history_list.xml 7551 d27d2dbc00f43781fbe24d2d1904a84349df1070e65fb7c9b9402b4828c9cca8 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function () {
							const workspace_json = $p.parent().__workspace_data__.getAllJSON();
							const buildproj_json = $p.parent().__buildproject_data__.getAllJSON();

							scwin.setWorkspace(workspace_json, buildproj_json);
						};

						scwin.grp_deploy_history_onclick = function(e){
							const acdbox = $('#'+this.render.id);
							acdbox.toggleClass('on');
							if (acdbox.hasClass("on")) {
								acdbox.find('.acd_congrp').slideDown();
							} else {
								acdbox.find('.acd_congrp').slideUp();
							}
						}

						/* workspace setting param workspace_json, buildproj_json */
						scwin.setWorkspace = function (workspace_json, buildproj_json) {
							for (const [idx, value] of workspace_json.entries()) {
								generator_deploy_history_workspace_list.insertChild();
								let workspace_select = generator_deploy_history_workspace_list.getChild(idx, "select_deploy_history_workpsace_title");
								workspace_select.setValue(value.workspace_name);

								let temp_generator = generator_deploy_history_workspace_list.getChild(idx, "list_deploy_history_buildproj_generator");

								let count = 0;
								let android_cnt = 0;
								let ios_cnt = 0;

								for (const [idx2, value2] of buildproj_json.entries()) {
									if (value2.workspace_id === value.id) {
										if (value2.deploy_yn === "1") {
											temp_generator.insertChild();

											//set icon
											let span_icon = temp_generator.getChild(count, "buildproj_platform");
											let txt_proj_name = temp_generator.getChild(count, "txt_buildproj_project_name");
											let btn_history = temp_generator.getChild(count, "btn_history");

											let grp_historyproj_btn = temp_generator.getChild(count, "grp_list_historyproj");
											grp_historyproj_btn.setUserData("platform", value2.platform);
											grp_historyproj_btn.setUserData("projectName", value2.project_name);
											grp_historyproj_btn.setUserData("project_pkid", value2.project_id);
											grp_historyproj_btn.setUserData("workspace_pkid", value.id);

											btn_history.setUserData("platform", value2.platform);
											btn_history.setUserData("projectName", value2.project_name);
											btn_history.setUserData("project_pkid", value2.project_id);
											btn_history.setUserData("workspace_pkid", value.id);

											count++;

											let message;
											if (value2.platform.toLowerCase() == "android") {
												message = common.getLabel("lbl_android")
												span_icon.addClass("ico_android");
												android_cnt++;
											} else {
												message = common.getLabel("lbl_ios");
												span_icon.addClass("ico_ios");
												ios_cnt++;
											}
											span_icon.setValue(message);
											txt_proj_name.setValue(buildproj_json[idx2].project_name);
										}
									}
								}

								let label_android = common.getLabel("lbl_android_s");
								label_android = common.getFormatStr("%s", android_cnt);
								let label_ios = common.getLabel("lbl_ios_s");
								label_ios = common.getFormatStr("%s", ios_cnt);

								let span_android = generator_deploy_history_workspace_list.getChild(idx, "count_android");
								span_android.setValue(label_android);
								let span_ios = generator_deploy_history_workspace_list.getChild(idx, "count_ios");
								span_ios.setValue(label_ios);
							}
						};

						scwin.grp_list_historyproj_onclick = function () {
							const platform = this.getUserData("platform");
							const build_project_id = this.getUserData("project_pkid");
							const buildProjectName = this.getUserData("projectName");

							$p.top().scwin.deployHistoryPlatform = platform;
							$p.top().scwin.deployHistoryProjectName = buildProjectName;
							$p.top().scwin.deployHistoryProjectId = build_project_id;

							// m0202010000
							const menuKey = $p.top().scwin.convertMenuCodeToMenuKey("m0202010000");
							$p.top().scwin.add_tab(menuKey);
						};

						scwin.btn_history_onclick = function () {
							const platform = this.getUserData("platform");
							const buildProjectName = this.getUserData("projectName");
							const build_project_id = this.getUserData("project_pkid");

							$p.top().scwin.deployHistoryPlatform = platform;
							$p.top().scwin.deployHistoryProjectName = buildProjectName;
							$p.top().scwin.deployHistoryProjectId = build_project_id;

							const menuKey = $p.top().scwin.convertMenuCodeToMenuKey("m0202010000");
							$p.top().scwin.add_tab(menuKey);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_deploy_history_list_title'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'w2:generator',A:{tagname:'ul',style:'',id:'generator_deploy_history_workspace_list',class:'acd_list'},E:[{T:1,N:'xf:group',A:{id:'grp_deploy_history',class:'acdbox',tagname:'li','ev:onclick':'scwin.grp_deploy_history_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'select_deploy_history_workpsace_title',label:'',class:'acd_tit'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'ectgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'ectbox'},E:[{T:1,N:'w2:span',A:{style:'',id:'',label:'',class:'ico_android',useLocale:'true',localeRef:'lbl_android'}},{T:1,N:'w2:span',A:{class:'item_cnt_txt',id:'count_android',label:'',style:''}}]},{T:1,N:'xf:group',A:{class:'ectbox',id:'',style:''},E:[{T:1,N:'w2:span',A:{class:'ico_ios',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios'}},{T:1,N:'w2:span',A:{class:'item_cnt_txt',id:'count_ios',label:'',style:''}}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'list_deploy_history_buildproj_generator',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'grp_list_historyproj',class:'acd_txtbox','ev:onclick':'scwin.grp_list_historyproj_onclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'android',id:'buildproj_platform',class:'ico_android'}},{T:1,N:'w2:textbox',A:{style:'',id:'txt_buildproj_project_name',label:'item txt',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'',style:'',id:'btn_history',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.btn_history_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_history',tooltipLocaleRef:'lbl_deploy_history_list_tooltip_history'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})