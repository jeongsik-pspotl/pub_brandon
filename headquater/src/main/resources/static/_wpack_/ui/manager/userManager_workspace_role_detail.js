/*amd /ui/manager/userManager_workspace_role_detail.xml 15281 221521e5afc912e3398a111005bde92d90f18ac1e74d7946a8f6d8dab7d17ba5 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'__workspace_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'member_id',name:'member_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'delete_yn',name:'delete_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'status',name:'status',dataType:'text'}},{T:1,N:'w2:column',A:{id:'created_date',name:'created_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'updated_date',name:'updated_date',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'},{T:1,N:'xf:submission',A:{id:'sub_getUserXmlMapData',ref:'',target:'data:json,{"id":"__workspace_list_data__","key":"data"}',action:'/manager/workspace/search/workspaceAll',method:'get',mediatype:'application/json',encoding:'UTF-8',instance:'',replace:'',errorHandler:'',customHandler:'',mode:'asynchronous',processMsg:'','ev:submit':'','ev:submitdone':'scwin.callback','ev:submiterror':'',abortTrigger:''}}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function () {
							scwin.select_admin_workspace_list();
						};

						scwin.callback = function (e) {
							var data = e.responseJSON;
							__workspace_list_data__.setJSON(data);

							var workspaceGridArr = workspaceGridView.getAllDisplayJSON();

							for (let i = 0; i < workspaceGridArr.length; i++) {
								const delete_yn = workspaceGridArr[i].delete_yn;
								const stastus_yn = workspaceGridArr[i].status;

								if (delete_yn === "1") {
									workspaceGridView.setCellChecked(i, "delete_yn", true);
									workspaceGridView.setRowVisible(i, false);
								} else if (delete_yn === "N") {
									workspaceGridView.setCellChecked(i, "delete_yn", false);
								}

								if (stastus_yn === "1") {
									workspaceGridView.setCellChecked(i, "status", true);
								} else if (stastus_yn === "N") {
									workspaceGridView.setCellChecked(i, "status", false);
								}
							}

							workspaceGridView.setColumnVisible("workspace_id", false);
							workspaceGridView.setColumnVisible("delete_yn", false);
						};

						/* admin workspace 조회  */
						scwin.select_admin_workspace_list = async function () {
							const url = common.uri.getAdminWorkspaceListInfo;
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.selectAdminWorkspaceListSetting(data);
						};

						scwin.selectAdminWorkspaceListSetting = function (data) {
							if (data != null) {
								let workspace = [];

								for (let [row, workspaceData] of data.entries()) {
									var temp = {};

									temp["workspace_id"] = workspaceData.workspace_id;
									temp["workspace_name"] = workspaceData.workspace_name;
									temp["member_id"] = workspaceData.member_id;
									temp["delete_yn"] = workspaceData.delete_yn;
									temp["status"] = workspaceData.status;
									temp["created_date"] = workspaceData.created_date.replace(/T/g, ' ');
									temp["updated_date"] = workspaceData.updated_date.replace(/T/g, ' ');

									workspace.push(temp);
								}
								__workspace_list_data__.removeAll();
								let distinct = common.unique(workspace, 'workspace_id');
								__workspace_list_data__.setJSON(distinct, true);

								const workspaceGridArr = workspaceGridView.getAllVisibleJSON();

								for (let i = 0; i < workspaceGridArr.length; i++) {
									const delete_yn = workspaceGridArr[i].delete_yn;
									const stastus_yn = workspaceGridArr[i].status;

									if (delete_yn === "1") {
										workspaceGridView.setCellChecked(i, "delete_yn", true);
										workspaceGridView.setRowVisible(i, false);
									} else if (delete_yn === "N") {
										workspaceGridView.setCellChecked(i, "delete_yn", false);
									}

									if (stastus_yn === "1") {
										workspaceGridView.setCellChecked(i, "status", true);
									} else if (stastus_yn === "N") {
										workspaceGridView.setCellChecked(i, "status", false);
									}
								}

								workspaceGridView.setColumnVisible("workspace_id", false);
								workspaceGridView.setColumnVisible("delete_yn", false);
							}
						};

						/* workspace 생성  */
						scwin.btn_workspace_create_onclick = function () {
							const pattern = /\s/g;
							const workspace_name = input_workpspace_name.getValue();

							if (workspace_name.match(pattern)) {
								const message = common.getLabel("lbl_userManager_workspace_role_detail_blank");
								common.win.alert(message);
								return false;
							}

							if (common.isEmptyStr(workspace_name)) {
								const message = common.getLabel("lbl_userManager_workspace_role_detail_workspace_input");
								common.win.alert(message);
								return false;
							}

							// workspace 자릿수 제한
							if (common.getCheckInputLength(workspace_name, workspace_name.length, 50)) {
								return false;
							}

							if (common.checkAllInputText("CHECK_INPUT_TYPE_SPC", workspace_name)) {
								const message = common.getLabel("lbl_can_not_special_char");
								common.win.alert(message);
								return false;
							}

							if (workspace_name) {
								// save datalist
								var params = {};
								params.workspace_name = workspace_name;
								scwin.create_workspace(params);

								//workspace name form init
								input_workpspace_name.setValue("");
							}
						};

						scwin.create_workspace = function (params) {
							let data = {};
							data.workspace_name = params.workspace_name;
							data.status = "1";
							data.favorite_flag = "Y";
							data.delete_yn = "0"

							const url = common.uri.setWorkspaceCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									const workspaceCheck = res[0].workspaceCheck;
									if (workspaceCheck === "yes") {
										const message = common.getLabel("lbl_exist_name")
										common.win.alert(message);
									} else {
										// 바로 조회 할수 있게 구현
										scwin.select_admin_workspace_list();
										// workspace 생성 후 workspace 화면 갱신
										$p.top().scwin.updateWorkspaceData();
									}
								})
								.catch(() => {
									const message = common.getLabel("lbl_userManager_admin_detail_modifiedFail");
									common.win.alert(message);
								});
						};

						scwin.workspaceGridViewDeleteBtnOnCellClick = async function (row, col) {
							const columnID = workspaceGridView.getColumnID(col);

							if (columnID === "delete_btn") {
								const message = common.getLabel("lbl_userManager_workspace_role_detail_delete_message");

								if (await common.win.confirm(message)) {
									const workspaceGridRow = workspaceGridView.getRealRowIndex(row);
									const workSpaceJsonRow = __workspace_list_data__.getRowJSON(workspaceGridRow);

									const opts = {
										'do': 'deleteWorkspace',
										'workspace_id': workSpaceJsonRow.workspace_id,
										'workspace_name': workSpaceJsonRow.workspace_name
									}

									await common.win.prompt("", null, opts);
								}
							}
						};

						scwin.btn_update_workspace_detail_onclick = function () {
							const data = workspaceGridView.getAllDisplayJSON();

							const url = common.uri.setWorkspaceUpdate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if (res[0].result === "success" && res != null) {
										scwin.select_admin_workspace_list();
									}
								}).catch(err => {
								common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");
							});
						};

						scwin.delete_workspace_name_onclick = async function (workspace_id, workspace_name) {
							let data = {};
							data.workspace_id = workspace_id;
							data.workspace_name = workspace_name;

							const url = common.uri.setWorkspaceDelete;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									if (res[0].workspace_name_found_check === "yes") {
										scwin.select_admin_workspace_list();
										// workspace 삭제후 workspace 화면 갱신
										$p.top().scwin.updateWorkspaceData();
									} else if (res[0].workspace_name_found_check === "no") {
										let label = common.getLabel("lbl_userManager_workspace_role_detail_delete_fail");
										label = common.getFormatStr(label, workspace_name_temp);
										lbl_workspace_delete_name_check.setLabel(label);
									}
								}).catch(err => {
								common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");
							});
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',tagname:'h2',useLocale:'true',localeRef:'lbl_workspace'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_userManager_workspace_manage'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button','ev:onclick':'scwin.btn_update_workspace_detail_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_userManager_workspace_detail_workspaceName'}}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'input_workpspace_name',style:'width:100%;',useLocale:'true',localeRef:'lbl_userManager_workspace_role_detail_workspaceName'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button','ev:onclick':'scwin.btn_workspace_create_onclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_userManager_workspace_role_detail_list'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''}}]}]},{T:1,N:'xf:group',A:{adaptiveThreshold:'',class:'gvwbox',id:'',style:''},E:[{T:1,N:'w2:gridView',A:{autoFit:'allColumn',autoFitMinWidth:'',class:'gvw',contextMenu:'true',dataList:'data:__workspace_list_data__',defaultCellHeight:'20',editModeEvent:'ondblclick','ev:oncellclick':'scwin.workspaceGridViewDeleteBtnOnCellClick',focusMode:'row',focusMove:'',id:'workspaceGridView',keepDefaultColumnWidth:'true',keyMoveEditMode:'true',rowNumVisible:'true',rowNumWidth:'50',scrollByColumn:'false',scrollByColumnAdaptive:'false',style:'height:100px;',visibleRowNum:'20',visibleRowNumFix:''},E:[{T:1,N:'w2:caption',A:{id:'caption1',style:'',value:''}},{T:1,N:'w2:header',A:{id:'header1',style:''},E:[{T:1,N:'w2:row',A:{id:'row1',style:''},E:[{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column1',inputType:'text',useLocale:'true',localeRef:'lbl_workspace',removeBorderStyle:'false',style:'height:30px;',toolTip:'tooltip',tooltipHeader:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_userManager_workspace_role_detail_tooltip_grid_space',label:'',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column3',inputType:'text',tooltipDisplay:'true',localeRef:'lbl_userManager_workspace_role_detail_grid_userStatus',removeBorderStyle:'false',style:'height:30px;',useLocale:'true',toolTip:'tooltip',tooltipHeader:'true',tooltipLocaleRef:'lbl_userManager_workspace_role_detail_tooltip_grid_useStatus',label:'',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column2',inputType:'text',localeRef:'lbl_delete',removeBorderStyle:'false',style:'height:30px;',value:'',label:'',useLocale:'true',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column4',inputType:'text',useLocale:'true',localeRef:'lbl_created_date',removeBorderStyle:'false',style:'height:30px;',label:'',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column5',inputType:'text',useLocale:'true',localeRef:'lbl_created_date',removeBorderStyle:'false',style:'height:30px;',value:'',label:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column6',inputType:'text',localeRef:'lbl_modified_date',removeBorderStyle:'false',style:'height:30px;',useLocale:'true',value:'',label:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column7',inputType:'text',localeRef:'lbl_workspace_id',removeBorderStyle:'false',style:'height:30px;',useLocale:'true',label:'',value:'',width:'70'}}]}]},{T:1,N:'w2:gBody',A:{id:'gBody1',style:''},E:[{T:1,N:'w2:row',A:{id:'row2',style:''},E:[{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_name',inputType:'text',removeBorderStyle:'false',style:'height:30px;',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'status',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{displayMode:'label',id:'delete_btn',inputType:'button',style:'height:20px;',textAlign:'center',value:'delete',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'delete_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'created_date',inputType:'',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'updated_date',inputType:'',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}}]}]}]}]}]}]}]}]}]})