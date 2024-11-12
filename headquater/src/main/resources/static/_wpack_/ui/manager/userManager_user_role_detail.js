/*amd /cm/ui/manager/userManager_user_role_detail.xml 54452 33d45cb965956273ffd6f444cbba2ee43eb9d94725265a11debaf93e9becbeb6 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dlt_rolecode_list_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'__workspace_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_group_role_id',name:'workspace_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'__key_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'key_id',name:'key_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'platform',name:'platform',dataType:'text'}},{T:1,N:'w2:column',A:{id:'key_name',name:'key_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'key_type',name:'key_type',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'__project_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'project_group_role_id',name:'project_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_group_role_id',name:'workspace_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_id',name:'project_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_name',name:'project_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'all_yn',name:'all_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'read_yn',name:'read_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'delete_yn',name:'delete_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_yn',name:'update_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'build_yn',name:'build_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'deploy_yn',name:'deploy_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'export_yn',name:'export_yn',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'__project_list_data_real__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'project_group_role_id',name:'project_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_group_role_id',name:'workspace_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_id',name:'project_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_name',name:'project_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'all_yn',name:'all_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'read_yn',name:'read_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'delete_yn',name:'delete_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_yn',name:'update_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'build_yn',name:'build_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'deploy_yn',name:'deploy_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'export_yn',name:'export_yn',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'},{T:1,N:'xf:submission',A:{id:'sub_getUserXmlMapData',ref:'',target:'data:json,{"id":"__workspace_list_data__","key":"data"}',action:'/manager/workspace/search/roleListAll',method:'get',mediatype:'application/json',encoding:'UTF-8',instance:'',replace:'',errorHandler:'',customHandler:'',mode:'synchronous',processMsg:'','ev:submit':'','ev:submitdone':'scwin.callback','ev:submiterror':'',abortTrigger:''}}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.check_role_nameYN = false;
						scwin.onpageload = function () {
							let paramData = $p.getParameter("tabParam");
							var member_setting_mode = paramData.member_setting_mode;

							if (member_setting_mode == "detailview") {

								// $p.executeSubmission("sub_getUserXmlMapData")
								scwin.getWorkspaceRoleListAll();

								scwin.getKeySettingInfo();

								var role_name = paramData.role_name;
								txt_role_name.setValue(role_name);

								// scwin.getAllProjectInfo();

								// scwin.getAllProjectInfoByRoleID();
								//
								// scwin.getAllCheckedProjectInfoList();
								//
								// scwin.setGridColumVisible();

							} else {
								var view = common.getLabel("lbl_userManager_user_role_detail_createView");

								var create = common.getLabel("lbl_create");

								workspace_setting_title.setLabel(view);
								branch_create_or_save_btn.setLabel(create);

								// $p.executeSubmission("sub_getUserXmlMapData");
								scwin.getWorkspaceRoleListAll();

								scwin.getKeySettingInfo();

								scwin.getAllProjectInfo();

								scwin.setGridColumVisible();
							}

						};

						scwin.onpageunload = function () {

						};

						scwin.getWorkspaceRoleListAll = async  function(){

							/// scwin.getWorkspaceRoleListAll
							let url = common.uri.getWorkspaceRoleListAll;
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.callback(data);

						};

						scwin.callback = function (data) {
							console.log(data);
							__workspace_list_data__.setJSON(data);

							let paramData = $p.getParameter("tabParam");
							var member_setting_mode = paramData.member_setting_mode;

							if (member_setting_mode == "detailview") {
								scwin.getWorkspaceGroupIdList();
							}

						};

						scwin.checkDataProjectName = function () {


							return true;
						};

						scwin.setGridColumVisible = function () {

							gridView1.setColumnVisible("workspace_id", false);
							gridView1.setColumnVisible("workspace_group_role_id", false);
							// gridView1.setColumnVisible("select_yn", false);
							gridView2.setColumnVisible("key_id", false);
							gridView3.setColumnVisible("project_id", false);
							gridView3.setColumnVisible("workspace_id", false);
							gridView3.setColumnVisible("project_group_role_id", false);
							gridView3.setColumnVisible("workspace_group_role_id", false);


						};

						scwin.getKeySettingInfo = async function () {

							var data = {};

							// POST
							let url = common.uri.getKeySettingAll;
							let method = "POST";
							let headers = {"Content-Type": "application/json"};

							await common.http.fetch(url, method, headers, {}, {}).then( res => {
								scwin.keySettingListSetting(res);
							});

						};

						scwin.keySettingListSetting = function(data){

							let paramData = $p.getParameter("tabParam");
							var member_setting_mode = paramData.member_setting_mode;

							var keySettingFileList = [];

							for (var row in data) {
								var temp = {};

								temp["key_id"] = data[row].key_id;
								temp["key_name"] = data[row].key_name;
								temp["key_type"] = data[row].key_type;
								temp["platform"] = data[row].platform;

								keySettingFileList.push(temp);

							}

							var distict = common.unique(keySettingFileList, 'key_id');
							__key_list_data__.setJSON(distict);

							// key role detail
							if (member_setting_mode == "detailview") {
								scwin.getKeyRoleDetail();
							} else {

							}

						};

						scwin.btn_update_workspace_detail_onclick = async function (e) {

							let paramData = $p.getParameter("tabParam");
							var member_setting_mode = paramData.member_setting_mode;

							if(common.isEmptyStr(txt_role_name.getValue())){
								var message = common.getLabel("lbl_userManager_user_role_detail_blank");
								common.win.alert(message);
								scwin.check_role_nameYN = false;
								return false;
							}

							if (member_setting_mode == "detailview") {

								var workspaceGridArr = gridView1.getAllDisplayJSON();
								var keyGridArr = gridView2.getAllDisplayJSON();
								var keyListArr = __key_list_data__.getAllJSON();
								var projectGridArr = __project_list_data_real__.getAllJSON(); // 수정 해야함.
								var projectGridArr_test = __project_list_data__.getAllJSON();

								var data = {};

								let paramData = $p.getParameter("tabParam");
								data.role_id = paramData.role_id;
								data.role_name = txt_role_name.getValue();
								// workspace id, list 로 관리해서 처리하기 ...
								var workspaceIDList = [];
								for (var i = 0; i < workspaceGridArr.length; i++) {

									var workspace_data = {};
									if (workspaceGridArr[i].select_yn == 1) {
										workspace_data.workspace_group_role_id = workspaceGridArr[i].workspace_group_role_id;
										workspace_data.workspace_id = workspaceGridArr[i].workspace_id;
										workspace_data.role_id = paramData.role_id;
										workspace_data.select_yn = workspaceGridArr[i].select_yn;
										workspaceIDList[i] = workspace_data;

									}else {
										workspace_data.workspace_group_role_id = workspaceGridArr[i].workspace_group_role_id;
										workspace_data.workspace_id = workspaceGridArr[i].workspace_id;
										workspace_data.role_id = paramData.role_id;
										workspace_data.select_yn = workspaceGridArr[i].select_yn;
										workspaceIDList[i] = workspace_data;
									}

								}


								data.workspaceGroupRole = workspaceIDList;

								var keyIDList = [];
								var count = 0;
								for (var k = 0; k < keyGridArr.length; k++) {

									if (keyGridArr[k].select_yn == 1) {

										var key_data = {};
										for(var kList = 0; kList < keyListArr.length; kList++){

											if((keyGridArr[k].key_type == "build" && keyGridArr[k].platform == "Android") && (keyListArr[kList].key_type == "build" && keyListArr[kList].platform == "Android")){

												if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}


											}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "Android") && (keyListArr[kList].key_type == "deploy" && keyListArr[kList].platform == "Android")){
												// console.log(keyGridArr[k]);
												if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}
											}

											if((keyGridArr[k].key_type == "build" && keyGridArr[k].platform == "iOS") && (keyListArr[kList].key_type == "build" && keyListArr[kList].platform == "iOS")){

												if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}


											}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "iOS") && (keyListArr[kList].key_type == "deploy" && keyListArr[kList].platform == "iOS")){
												// console.log(keyGridArr[k]);
												if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}
											}

										}

										key_data.key_id = keyGridArr[k].key_id;
										key_data.role_id = paramData.role_id;
										key_data.select_yn = keyGridArr[k].select_yn;
										keyIDList[count] = key_data;

										count++;
									}else {

										var key_data = {};
										key_data.key_id = keyGridArr[k].key_id;
										key_data.role_id = paramData.role_id;
										key_data.select_yn = keyGridArr[k].select_yn;
										keyIDList[count] = key_data;

										count++;
									}

								}


								data.keyGroupRole = keyIDList;

								var projectIDList = [];
								for (var p = 0; p < projectGridArr.length; p++) {

									var project_data = {};

									project_data.project_group_role_id = projectGridArr[p].project_group_role_id;
									project_data.workspace_group_role_id = projectGridArr[p].workspace_group_role_id;
									project_data.project_id = projectGridArr[p].project_id;
									project_data.workspace_id = projectGridArr[p].workspace_id;
									project_data.read_yn = projectGridArr[p].read_yn;
									project_data.update_yn = projectGridArr[p].update_yn;
									project_data.delete_yn = projectGridArr[p].delete_yn;
									project_data.build_yn = projectGridArr[p].build_yn;
									project_data.deploy_yn = projectGridArr[p].deploy_yn;
									project_data.export_yn = projectGridArr[p].export_yn;

									projectIDList[p] = project_data;

								}

								data.projectGroupRole = projectIDList;

								// POST
								const url = common.uri.setUserRoleUpdate;
								const method = "POST";
								const headers = {"Content-Type": "application/json; charset=utf-8"};
								const body = data;


								await common.http.fetch(url, method, headers, body).then(res => {

									if (res[0].result == "success" && res != null) {
										let message = common.getLabel("lbl_userManager_user_role_detail_success");
										common.win.alert(message);

									}

								}).catch(err => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");

								});

							} else {
								// create View

								if(!scwin.check_role_nameYN){
									var message = common.getLabel("lbl_userManager_user_role_detail_duplicate_message");
									common.win.alert(message);
									return false;
								}


								var workspaceGridArr = gridView1.getAllDisplayJSON();
								var keyGridArr = gridView2.getCheckedJSON("select_yn");
								var keyGridArr_checkTemp = gridView2.getCheckedJSON("select_yn");
								var projectGridArr = __project_list_data_real__.getAllJSON();

								var data = {};

								data.role_name = txt_role_name.getValue();
								// workspace id, list 로 관리해서 처리하기 ...
								var workspaceIDList = [];
								for (var i = 0; i < workspaceGridArr.length; i++) {

									var workspace_data = {};
									// 2021/11/02 select_yn
									if (workspaceGridArr[i].select_yn == "1" && workspaceGridArr[i].select_yn != "") {
										workspace_data.workspace_id = workspaceGridArr[i].workspace_id;
										workspaceIDList.push(workspace_data);
										// console.log(workspaceIDList[i]);
									}

									// if (workspaceGridArr[i].workspace_id != "") {
									// 	workspace_data.workspace_id = workspaceGridArr[i].workspace_id;
									// 	workspaceIDList[i] = workspace_data;
									//
									// }
								}

								data.workspaceGroupRole = workspaceIDList;

								var keyIDList = [];
								for (var k = 0; k < keyGridArr.length; k++) {

									if (keyGridArr[k].select_yn == 1) {
										var key_data = {};

										for(var kList = 0; kList < keyGridArr_checkTemp.length; kList++){

											if((keyGridArr[k].key_type == "build" && keyGridArr[k].platform == "Android") && (keyGridArr_checkTemp[kList].key_type == "build" && keyGridArr_checkTemp[kList].platform == "Android")){

												if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}


											}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "Android") && (keyGridArr_checkTemp[kList].key_type == "deploy" && keyGridArr_checkTemp[kList].platform == "Android")){
												// console.log(keyGridArr[k]);
												if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}
											}

											if((keyGridArr[k].key_type == "build" && keyGridArr[k].platform == "iOS") && (keyGridArr_checkTemp[kList].key_type == "build" && keyGridArr_checkTemp[kList].platform == "iOS")){

												if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}


											}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "iOS") && (keyGridArr_checkTemp[kList].key_type == "deploy" && keyGridArr_checkTemp[kList].platform == "iOS")){
												// console.log(keyGridArr[k]);
												if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

													var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
													common.win.alert(message);
													return false;
												}
											}

										}

										key_data.key_id = keyGridArr[k].key_id;
										keyIDList[k] = key_data;
									}

								}


								data.keyGroupRole = keyIDList;

								// 수정 필요
								var projectIDList = [];
								for (var p = 0; p < projectGridArr.length; p++) {

									var project_data = {};

									project_data.project_id = projectGridArr[p].project_id;
									project_data.workspace_id = projectGridArr[p].workspace_id;
									project_data.read_yn = projectGridArr[p].read_yn;
									project_data.update_yn = projectGridArr[p].update_yn;
									project_data.delete_yn = projectGridArr[p].delete_yn;
									project_data.build_yn = projectGridArr[p].build_yn;
									project_data.deploy_yn = projectGridArr[p].deploy_yn;
									project_data.export_yn = projectGridArr[p].export_yn;

									projectIDList[p] = project_data;

								}

								data.projectGroupRole = projectIDList;


								// POST
								const url = common.uri.setUserRoleCreate;
								const method = "POST";
								const headers = {"Content-Type": "application/json; charset=utf-8"};
								const body = data;

								await common.http.fetch(url, method, headers, body).then(res => {

									if (res[0].result == "success" && res != null) {
										let label = common.getLabel("lbl_userManager_user_role_detail_makeSuccess");
										label = common.getFormatStr(label);
										common.win.alert(label);


									} else {
										let label = common.getLabel("lbl_userManager_user_role_detail_makeFail");
										label = common.getFormatStr(label);
										common.win.alert(label);
									}

								}).catch(err => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");

								});


							}


						};

						// 수정 필요
						scwin.getWorkspaceRoleDetailList = async function () {

							let paramData = $p.getParameter("tabParam");
							var role_id =paramData.role_id;

							//  GET
							let url = common.uri.getUserRoleFindByWorkspaceGroup(parseInt(role_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getWorkspaceRoleDetailListSetting(data);

						};

						scwin.getWorkspaceRoleDetailListSetting = function(data) {
							if (data != null) {

								for (var i = 0; i < data.length; i++) {
									// console.log(data);
									var workspace_id = data[i].workspace_id;

									if (workspace_id != "") {
										gridView1.setCellChecked(i, "select_yn", true);
									} else {
										gridView1.setCellChecked(i, "select_yn", false);
									}

									if (data[i].workspace_group_role_id != "") {

										//scwin.getProjectGroupByIDList(data[i].workspace_group_role_id); // workspace_group_role_id 값 가지고 처리하기

									} else {
										__project_list_data__.setJSON([]);
									}
								}


							} else {

							}
						};

						scwin.getKeyRoleDetail = async function () {

							let paramData = $p.getParameter("tabParam");
							console.log(paramData);
							var role_id =paramData.role_id;

							// GET
							let url = common.uri.getUserRoleFindByProfileGroupRoleID( parseInt(role_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getKeyRoleDetailSetting(data);
						};

						scwin.getKeyRoleDetailSetting = function(data){

							if (data != null) {

								var keyGridArr = gridView2.getAllVisibleJSON();
								for (var i = 0; i < keyGridArr.length; i++) {
									if (data[0].key_build_android_id != "" && (data[0].key_build_android_id == keyGridArr[i].key_id)) {
										gridView2.setCellChecked(i, "select_yn", true);
									} else if (data[0].key_build_ios_id != "" && (data[0].key_build_ios_id == keyGridArr[i].key_id)) {
										gridView2.setCellChecked(i, "select_yn", true);
									} else if (data[0].key_deploy_android_id != "" && (data[0].key_deploy_android_id == keyGridArr[i].key_id)) {
										gridView2.setCellChecked(i, "select_yn", true);
									} else if (data[0].key_deploy_ios_id != "" && (data[0].key_deploy_ios_id == keyGridArr[i].key_id)) {
										gridView2.setCellChecked(i, "select_yn", true);
									} else {
										gridView2.setCellChecked(i, "select_yn", false);
									}
								}
							}


						};

						scwin.getProjectByIDList = async function (workspace_id, projectListReal) {

							// GET
							let url = common.uri.getUserRoleFindByProjectListinWorkspaceID(parseInt(workspace_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getProjectByIDListSetting(data, projectListReal);

						};

						scwin.getProjectByIDListSetting = function(data, projectListReal){

							if (data != null) {
								var projectGroupList = [];
								var workspace_id_save = "";

								if (data.length == 0) {

								} else {
									for (var row in data) {
										var temp = {};

										temp["project_id"] = data[row].project_id;
										temp["workspace_id"] = data[row].workspace_id;
										temp["project_name"] = data[row].project_name;
										temp["workspace_name"] = data[row].workspace_name;
										workspace_id_save = data[row].workspace_id;
										projectGroupList.push(temp);

									}

									var distict = common.unique(projectGroupList, 'project_id');
									__project_list_data__.setJSON(distict, true);

									var projectListData = __project_list_data__.getAllJSON();
									// workspace | project 중복 체크
									//console.log("projectListData");
									//console.log(projectListData);
									for (var rowList in projectListData) {

										for (var rowReal in projectListReal) {

											if ((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id)) {

												__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
											} else if ((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id) && (projectListReal[rowReal].workspace_group_role_id == "") && (projectListReal[rowReal].project_group_role_id == "")){
												__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
											}

											if ((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id)
												&& (projectListReal[rowReal].read_yn == 1 && projectListReal[rowReal].update_yn == 1 && projectListReal[rowReal].delete_yn == 1 && projectListReal[rowReal].build_yn == 1
													&& projectListReal[rowReal].deploy_yn == 1 && projectListReal[rowReal].export_yn == 1) ){
												__project_list_data__.setCellData(rowList, "all_yn",1);
											}
										}
									}
								}
							}

						};

						scwin.getProjectGroupByIDList = async function (workspace_group_role_id) {

							// GET
							let url = common.uri.getUserRoleFindByProjectGroupinWorkspacegrpRoleID(parseInt(workspace_group_role_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getProjectGroupByIDLIstsetting(data);
						};

						scwin.getProjectGroupByIDLIstsetting = function(data){
							if (data != null) {
								var projectGroupList = [];

								if (data.length == 0) {

								} else {
									for (var row in data) {
										var temp = {};

										temp["project_group_role_id"] = data[row].project_group_role_id;
										temp["workspace_group_role_id"] = data[row].workspace_group_role_id;
										temp["project_id"] = data[row].project_id;
										temp["workspace_id"] = data[row].workspace_id;
										temp["workspace_name"] = data[row].workspace_name;
										temp["project_name"] = data[row].project_name;
										temp["read_yn"] = data[row].read_yn;
										temp["update_yn"] = data[row].update_yn;
										temp["delete_yn"] = data[row].delete_yn;
										temp["build_yn"] = data[row].build_yn;
										temp["deploy_yn"] = data[row].deploy_yn;
										temp["export_yn"] = data[row].export_yn;

										projectGroupList.push(temp);

									}

									var distict = common.unique(projectGroupList, 'project_group_role_id');
									__project_list_data__.setJSON(distict, true);

								}


							} else {

							}

						};

						scwin.getAllProjectInfo = async function () {

							// GET
							let url = common.uri.gettUserRoleFIndByProjectListAll;
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getAllProjectInfoSetting(data);

						};

						scwin.getAllProjectInfoSetting = function(data){

							if (data != null) {
								var projectGroupList = [];

								if (data.length == 0) {

								} else {
									for (var row in data) {
										var temp = {};

										temp["project_id"] = data[row].project_id;
										temp["workspace_id"] = data[row].workspace_id;
										temp["project_name"] = data[row].project_name;
										temp["workspace_name"] = data[row].workspace_name;

										projectGroupList.push(temp);
										// workspace | project 중복 체크

									}

									var distict = common.unique(projectGroupList, 'project_id');
									__project_list_data_real__.setJSON(distict, false);

									console.log(__project_list_data_real__.getAllJSON());

									let paramData = $p.getParameter("tabParam");

									if(paramData.member_setting_mode == "detailview"){
										scwin.getAllProjectInfoByRoleID();
									}




								}


							} else {

							}

						};

						scwin.getAllProjectInfoByRoleID = async function () {

							let paramData = $p.getParameter("tabParam");
							var role_id =paramData.role_id;
							// GET
							let url = common.uri.getUserRoleFindBuRoleIDListInProject(parseInt(role_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getAllProjectInfoByRoleIDSetting(data);

						};

						scwin.getAllProjectInfoByRoleIDSetting = function(data){
							if (data != null) {
								var projectGroupList = [];

								if (data.length == 0) {

								} else {
									var projectGridView3 = gridView3.getAllVisibleJSON();
									var projectListArr = __project_list_data_real__.getAllJSON();

									for (var row in data) {
										var temp = {};

										for(var i = 0; i< projectGridView3.length; i++){

											if(projectGridView3[i].project_id == data[row].project_id){

											}else {

											}
										}

										for(var idx = 0; idx < projectListArr.length; idx++){

											if(projectListArr[idx].project_id == data[row].project_id){
												__project_list_data_real__.setCellData(idx, "project_group_role_id", data[row].project_group_role_id);
												__project_list_data_real__.setCellData(idx, "workspace_group_role_id", data[row].workspace_group_role_id);
												__project_list_data_real__.setCellData(idx, "read_yn", data[row].read_yn);
												__project_list_data_real__.setCellData(idx, "update_yn", data[row].update_yn);
												__project_list_data_real__.setCellData(idx, "delete_yn", data[row].delete_yn);
												__project_list_data_real__.setCellData(idx, "build_yn", data[row].build_yn);
												__project_list_data_real__.setCellData(idx, "deploy_yn", data[row].deploy_yn);
												__project_list_data_real__.setCellData(idx, "export_yn", data[row].export_yn);

											}else {

											}

										}

									}

									scwin.getAllCheckedProjectInfoList();



								}

							} else {

							}


						};

						// 수정 해야함.
						// 더블 클릭시 project list 조회 기능 으로 전환하기
						scwin.gridView1_projectID_oncellclick = function (row, col) {

							// var realRow = gridView1.getRealRowIndex( row );
							// var data = __workspace_list_data__.getRowJSON(realRow);
							var columnID = gridView1.getColumnID(col);
							let paramData = $p.getParameter("tabParam");
							var member_setting_mode = paramData.member_setting_mode;

							if (columnID == "select_yn" && gridView1.getCellChecked(row ,col)) {

								var workspaceGridRow = gridView1.getRealRowIndex(row);
								var workSpaceJsonRow = __workspace_list_data__.getRowJSON(workspaceGridRow);
								var projectListReal = __project_list_data_real__.getAllJSON();

								__project_list_data__.removeAll();

								if (member_setting_mode == "detailview") {

									//console.log("workSpaceJsonRow.workspace_group_role_id + " + workSpaceJsonRow.workspace_group_role_id);
									if(workSpaceJsonRow.workspace_group_role_id == ""){
										scwin.getProjectByIDList(workSpaceJsonRow.workspace_id, projectListReal);
									} else if(workSpaceJsonRow.workspace_group_role_id != "" && workSpaceJsonRow.select_yn == 1){
										scwin.getProjectByIDList(workSpaceJsonRow.workspace_id, projectListReal);
									} else {
										scwin.getWorkspaceCheckedProjectIDList(workSpaceJsonRow.workspace_id, workSpaceJsonRow.workspace_group_role_id, projectListReal);
									}

								} else {
									scwin.getProjectByIDList(workSpaceJsonRow.workspace_id, projectListReal);

								}


								//console.log("__project_list_data_real__.getAllJSON() .... ");
								//console.log(__project_list_data_real__.getAllJSON());

							}

							if (columnID == "workspace_name") {

								var workspaceGridRow = gridView1.getRealRowIndex(row);
								var workSpaceJsonRow = __workspace_list_data__.getRowJSON(workspaceGridRow);
								var projectListReal = __project_list_data_real__.getAllJSON();

								var test_workspace = __workspace_list_data__.getAllJSON();
								//console.log(test_workspace);

								__project_list_data__.removeAll();

								if (member_setting_mode == "detailview") {
									console.log("workSpaceJsonRow.workspace_group_role_id + " + workSpaceJsonRow.workspace_group_role_id);
									if(workSpaceJsonRow.workspace_group_role_id == ""){
										scwin.getProjectByIDList(workSpaceJsonRow.workspace_id, projectListReal);
									}else {
										scwin.getWorkspaceCheckedProjectIDList(workSpaceJsonRow.workspace_id, workSpaceJsonRow.workspace_group_role_id, projectListReal);
									}

								} else {
									scwin.getProjectByIDList(workSpaceJsonRow.workspace_id, projectListReal);

								}


								//console.log("__project_list_data_real__.getAllJSON() .... ");
								//console.log(__project_list_data_real__.getAllJSON());

							}

						};

						//
						scwin.gridView2_key_setting_oncellclick = function(row, col){

							var columnID = gridView2.getColumnID(col);

							if (columnID == "select_yn" && gridView2.getCellChecked(row,col)) {


							}

						};

						scwin.gridView3_project_group_role_oncellclick = function (row, col) {


							var columnID = gridView3.getColumnID(col);


							if (columnID == "all_yn" && gridView3.getCellChecked(row, "all_yn")) {

								gridView3.setCellChecked(row, "read_yn", true);
								gridView3.setCellChecked(row, "update_yn", true);
								gridView3.setCellChecked(row, "delete_yn", true);
								gridView3.setCellChecked(row, "build_yn", true);
								gridView3.setCellChecked(row, "deploy_yn", true);
								gridView3.setCellChecked(row, "export_yn", true);

							} else if (columnID == "all_yn" && !gridView3.getCellChecked(row, "all_yn")) {
								gridView3.setCellChecked(row, "read_yn", false);
								gridView3.setCellChecked(row, "update_yn", false);
								gridView3.setCellChecked(row, "delete_yn", false);
								gridView3.setCellChecked(row, "build_yn", false);
								gridView3.setCellChecked(row, "deploy_yn", false);
								gridView3.setCellChecked(row, "export_yn", false);
							}
							// 추가 조건 all yn 이 아닐 경우 다른 하나를 선택 해제 할 경우에 대한 조건추가 넣기 ..

							if(gridView3.getCellChecked(row, "read_yn") && gridView3.getCellChecked(row, "update_yn") && gridView3.getCellChecked(row, "delete_yn")
								&& gridView3.getCellChecked(row, "build_yn") && gridView3.getCellChecked(row, "deploy_yn") && gridView3.getCellChecked(row, "export_yn")){
								gridView3.setCellChecked(row, "all_yn", true);
							}else {
								gridView3.setCellChecked(row, "all_yn", false);
							}

							// 최종??? 체크 박스 선택시
							var focusRowCellChecked = gridView3.getFocusedRowIndex()
							var data = __project_list_data__.getRowJSON(focusRowCellChecked);

							var projectDataListReal = __project_list_data_real__.getAllJSON();
							for (var realRow = 0; realRow < projectDataListReal.length; realRow++) {

								if (data.project_id == projectDataListReal[realRow].project_id) {


									__project_list_data_real__.setRowJSON(realRow, data, true);

								}

							}
						};

						scwin.getAllCheckedProjectInfoList = function () {

							var workSpaceJsonRow = __workspace_list_data__.getRowJSON(0);
							var projectListReal = __project_list_data_real__.getAllJSON();
							console.log(__workspace_list_data__.getAllJSON());
							console.log(workSpaceJsonRow);
							scwin.getWorkspaceCheckedProjectIDList(workSpaceJsonRow.workspace_id, workSpaceJsonRow.workspace_group_role_id, projectListReal);

						};

						scwin.getWorkspaceCheckedProjectIDList = async function (workspace_id, workspace_group_role_id, projectListReal) {
							// GET

							console.log(workspace_group_role_id);
							let url = common.uri.getUserRoleFindByProjectListWithGroupRole(parseInt(workspace_id), parseInt(workspace_group_role_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getWorkspaceCheckedProjectIDListSetting(data, projectListReal);

						};

						scwin.getWorkspaceCheckedProjectIDListSetting = function(data, projectListReal){
							if (data != null) {
								var projectGroupList = [];
								var workspace_id_save = "";

								if (data.length == 0) {
									scwin.getProjectByIDList(workspace_id, projectListReal);
								} else {

									var projectGridView3 = gridView3.getAllVisibleJSON();

									for (var row in data) {
										var temp = {};

										temp["project_group_role_id"] = data[row].project_group_role_id;
										temp["workspace_group_role_id"] = data[row].workspace_group_role_id;
										temp["project_id"] = data[row].project_id;
										temp["workspace_id"] = data[row].workspace_id;
										temp["workspace_name"] = data[row].workspace_name;
										temp["project_name"] = data[row].project_name;
										temp["read_yn"] = data[row].read_yn;
										temp["update_yn"] = data[row].update_yn;
										temp["delete_yn"] = data[row].delete_yn;
										temp["build_yn"] = data[row].build_yn;
										temp["deploy_yn"] = data[row].deploy_yn;
										temp["export_yn"] = data[row].export_yn;

										for(var i = 0; i< projectGridView3.length; i++){

											if(projectGridView3[i].project_id == data[row].project_id){

											}else {

											}
										}

										projectGroupList.push(temp);

									}


									var distict = common.unique(projectGroupList, 'project_id');
									__project_list_data__.setJSON(distict, true);

									var projectListData = __project_list_data__.getAllJSON();

									for (var rowList in projectListData) {

										for (var rowReal in projectListReal) {

											if ((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id)) {

												__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);

											} else {

											}

											if((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id) && (projectListData[rowList].workspace_group_role_id == "")){
												__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
											}

											if((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id) && (projectListData[rowList].project_group_role_id == "")){
												__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
											}

											if((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id)
												&& (projectListData[rowList].read_yn == 1 && projectListData[rowList].update_yn == 1 && projectListData[rowList].delete_yn == 1 && projectListData[rowList].build_yn == 1
													&& projectListData[rowList].deploy_yn == 1 && projectListData[rowList].export_yn == 1) ){
												__project_list_data__.setCellData(rowList, "all_yn",1);
											}

										}
									}

									scwin.setGridColumVisible();
								}
							} else {

							}
						};


						scwin.getWorkspaceGroupIdList = async function() {

							let paramData = $p.getParameter("tabParam");
							var role_id = paramData.role_id;

							// GET
							let url = common.uri.getUesrRoleFindbyRoleListInWorkspaceGroup(parseInt(role_id));
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getWorkspaceGroupIdListSetting(data);

						};

						scwin.getWorkspaceGroupIdListSetting = function(data){
							if (data != null) {
								var workspaceGroupList = [];

								if (data.length == 0) {

								} else {

									var workspaceGridArr = gridView1.getAllVisibleJSON();
									var workspaceDataListArr = __workspace_list_data__.getAllJSON();

									for (var row in data) {
										var temp = {};

										for(var i = 0; i< workspaceGridArr.length; i++){

											if(workspaceGridArr[i].workspace_id == data[row].workspace_id){
												gridView1.setCellChecked(i, "select_yn", true);
											}else {

											}
										}

										for(var idx = 0; idx < workspaceDataListArr.length; idx++){

											if(workspaceDataListArr[idx].workspace_id == data[row].workspace_id){
												__workspace_list_data__.setCellData(idx, "workspace_group_role_id", data[row].workspace_group_role_id);
											}else {

											}

										}

									}

									scwin.getAllProjectInfo();  // TODO project list 조회

								}

							} else {

							}

						};


						scwin.select_check_role_name = async function(){

							if(common.isEmptyStr(txt_role_name.getValue())){
								var message = common.getLabel("lbl_userManager_user_role_detail_blank");
								common.win.alert(message);
								return false;
							}

							var data = {};

							data.role_name = txt_role_name.getValue();

							// POST
							const url = common.uri.getUserRoleCheckRoleName;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};
							const body = data;


							await common.http.fetch(url, method, headers, body).then(res => {

								if (res[0].role_name_not_found == "yes") {
									let message = common.getLabel("lbl_userManager_user_role_detail_available");
									common.win.alert(message);
									scwin.check_role_nameYN = true;

								} else if (res[0].role_name_not_found == "no") {
									var message = common.getLabel("lbl_userManager_user_role_detail_unavailable");
									common.win.alert(message);
									scwin.check_role_nameYN = false
								}

							}).catch(err => {
								common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n");

							});

						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'Role Manager',style:'',tagname:'h2'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'workspace_setting_title',label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_permission'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'branch_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_update_workspace_detail_onclick',useLocale:'true',localeRef:'lbl_modify'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{class:'lybox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptiveThreshold:'',class:'ly_column col_5',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_name'}}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'txt_role_name',style:'width:100%;'}},{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button','ev:onclick':'scwin.select_check_role_name',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'ly_column col_5',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'Your viewer domain',style:'',tagname:'h3'}}]},{T:1,N:'xf:group',A:{adaptiveThreshold:'',class:'gvwbox',id:'',style:''},E:[{T:1,N:'w2:gridView',A:{autoFit:'allColumn',class:'gvw',columnMove:'',contextMenu:'true',dataList:'data:__workspace_list_data__',defaultCellHeight:'20','ev:oncellclick':'scwin.gridView1_projectID_oncellclick','ev:oncelldblclick':'scwin.gridView1_oncelldblclick',id:'gridView1',keepDefaultColumnWidth:'true',rowNumVisible:'true',rowNumWidth:'50',scrollByColumn:'false',scrollByColumnAdaptive:'false',style:'height:100px;',visibleRowNum:'10',autoFitMinWidth:'430'},E:[{T:1,N:'w2:caption',A:{id:'caption1',style:'',value:''}},{T:1,N:'w2:header',A:{id:'header1',style:''},E:[{T:1,N:'w2:row',A:{id:'row1',style:''},E:[{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',fixColumnWidth:'true',id:'column10',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'',width:'50'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column9',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Workspace',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column11',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Workspace',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'column11',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Workspace',width:'70'}}]}]},{T:1,N:'w2:gBody',A:{id:'gBody1',style:''},E:[{T:1,N:'w2:row',A:{id:'row2',style:''},E:[{T:1,N:'w2:column',A:{checkboxLabel:'Y',displayMode:'label',heckboxLabelColumn:' ',id:'select_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'50'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_name',inputType:'text',removeBorderStyle:'false',style:'height:30px;',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_group_role_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',tagname:'h3',useLocale:'true',localeRef:'lbl_certification'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''}}]}]},{T:1,N:'xf:group',A:{adaptiveThreshold:'',class:'gvwbox',id:'',style:''},E:[{T:1,N:'w2:gridView',A:{autoFit:'allColumn',autoFitMinWidth:'780',class:'gvw',columnMove:'',contextMenu:'true',dataList:'data:__key_list_data__',defaultCellHeight:'4','ev:oncellclick':'scwin.gridView2_key_setting_oncellclick','ev:oncelldblclick':'scwin.gridView1_oncelldblclick',id:'gridView2',keepDefaultColumnWidth:'true',rowNumVisible:'true',rowNumWidth:'50',scrollByColumn:'false',scrollByColumnAdaptive:'false',style:'height:100px;',visibleRowNum:'10'},E:[{T:1,N:'w2:caption',A:{id:'caption2',style:'',value:''}},{T:1,N:'w2:header',A:{id:'header2',style:''},E:[{T:1,N:'w2:row',A:{id:'row11',style:''},E:[{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',fixColumnWidth:'true',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'',width:'50'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Platform',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Key Type',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Key',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Key ID',width:'70'}}]}]},{T:1,N:'w2:gBody',A:{id:'gBody2',style:''},E:[{T:1,N:'w2:row',A:{id:'row22',style:''},E:[{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'select_yn',inputType:'checkbox',style:'height:30px;',textAlign:'center',value:'',width:'50'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'platform',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'key_type',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'key_name',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'key_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}}]}]}]}]},{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'project List',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''}}]}]},{T:1,N:'xf:group',A:{adaptiveThreshold:'',class:'gvwbox',id:'',style:''},E:[{T:1,N:'w2:gridView',A:{autoFit:'allColumn',autoFitMinWidth:'780',class:'gvw',contextMenu:'true',dataList:'data:__project_list_data__',defaultCellHeight:'15','ev:oncellclick':'scwin.gridView3_project_group_role_oncellclick',id:'gridView3',keepDefaultColumnWidth:'true',rowNumVisible:'true',rowNumWidth:'50',scrollByColumn:'false',scrollByColumnAdaptive:'false',style:'height: 100px;',visibleRowNum:'10'},E:[{T:1,N:'w2:header',A:{id:'header3',style:''},E:[{T:1,N:'w2:row',A:{id:'row111',style:''},E:[{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'workspace',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'project',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'project id',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'workspace id',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'project_group_role',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'workspace_group_role id',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'All',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Read',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Update',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Delete',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Build/History',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Deploy',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'',inputType:'text',localeRef:'',removeBorderStyle:'false',style:'height:30px;',value:'Export',width:'70'}}]}]},{T:1,N:'w2:gBody',A:{id:'gBody3',style:''},E:[{T:1,N:'w2:row',A:{id:'row222',style:''},E:[{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_name',inputType:'text',removeBorderStyle:'false',style:'height:30px;',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'project_name',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'project',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'project_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:' id',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'project_group_role_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{blockSelect:'false',displayMode:'label',id:'workspace_group_role_id',inputType:'text',removeBorderStyle:'false',style:'height:20px',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'all_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'read_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'update_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'delete_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'build_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'deploy_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}},{T:1,N:'w2:column',A:{checkboxLabel:'Y',checkboxLabelColumn:' ',displayMode:'label',id:'export_yn',inputType:'checkbox',style:'height:20px;',textAlign:'center',value:'',width:'70'}}]}]}]}]}]}]}]}]}]})