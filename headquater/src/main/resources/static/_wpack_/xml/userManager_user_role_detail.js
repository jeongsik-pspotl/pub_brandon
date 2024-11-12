/*amd /xml/userManager_user_role_detail.xml 57461 94690bb4ced9bb56cdc3da3f768ebdd3b6adc441caf2befe259e7053a787c20e */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dlt_rolecode_list_selectbox',saveRemovedData:'true',style:''},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_id',name:'name1'}},{T:1,N:'w2:column',A:{dataType:'text',id:'role_code_name',name:'name2'}}]}]},{T:1,N:'w2:dataList',A:{id:'__workspace_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_group_role_id',name:'workspace_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'__key_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'key_id',name:'key_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'platform',name:'platform',dataType:'text'}},{T:1,N:'w2:column',A:{id:'key_name',name:'key_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'key_type',name:'key_type',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'__project_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'project_group_role_id',name:'project_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_group_role_id',name:'workspace_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_id',name:'project_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_name',name:'project_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'all_yn',name:'all_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'read_yn',name:'read_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'delete_yn',name:'delete_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_yn',name:'update_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'build_yn',name:'build_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'deploy_yn',name:'deploy_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'export_yn',name:'export_yn',dataType:'text'}}]}]},{T:1,N:'w2:dataList',A:{id:'__project_list_data_real__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'project_group_role_id',name:'project_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_group_role_id',name:'workspace_group_role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_id',name:'project_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_name',name:'workspace_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_name',name:'project_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'select_yn',name:'select_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'all_yn',name:'all_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'read_yn',name:'read_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'delete_yn',name:'delete_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_yn',name:'update_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'build_yn',name:'build_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'deploy_yn',name:'deploy_yn',dataType:'text'}},{T:1,N:'w2:column',A:{id:'export_yn',name:'export_yn',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'},{T:1,N:'xf:submission',A:{id:'sub_getUserXmlMapData',ref:'',target:'data:json,{"id":"__workspace_list_data__","key":"data"}',action:'/manager/workspace/search/roleListAll',method:'get',mediatype:'application/json',encoding:'UTF-8',instance:'',replace:'',errorHandler:'',customHandler:'',mode:'synchronous',processMsg:'','ev:submit':'','ev:submitdone':'scwin.callback','ev:submiterror':'',abortTrigger:''}}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.check_role_nameYN = false;
			scwin.onpageload = function () {
				var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

				if (member_setting_mode == "detailview") {


					// $p.executeSubmission("sub_getUserXmlMapData");
					// var role_id = localStorage.getItem("_role_id_");

					$p.executeSubmission("sub_getUserXmlMapData");

					scwin.getKeySettingInfo();

					// role_id 값으로 상세 조회 기능 구현
					// workspace role detail
					// scwin.getWorkspaceRoleDetailList();

					var role_name = localStorage.getItem("_role_name_");
					txt_role_name.setValue(role_name);

					scwin.getAllProjectInfo();

					scwin.getAllProjectInfoByRoleID();

					scwin.getAllCheckedProjectInfoList();

					scwin.setGridColumVisible();

				} else {
					var view = common.getLabel("lbl_userManager_user_role_detail_createView");

					var create = common.getLabel("lbl_create");

					workspace_setting_title.setLabel(view);
					branch_create_or_save_btn.setLabel(create);

					$p.executeSubmission("sub_getUserXmlMapData");

					scwin.getKeySettingInfo();

					scwin.getAllProjectInfo();

					scwin.setGridColumVisible();
				}

			};

			scwin.onpageunload = function () {

			};

			scwin.callback = function (e) {

				var data = e.responseJSON;
				__workspace_list_data__.setJSON(data);

				var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

				if (member_setting_mode == "detailview") {
					scwin.getWorkspaceGroupIdList();
				}

			};

			scwin.checkData = function () {

				return true;
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

			scwin.memberDetailView = function () {

				var workspace_name = localStorage.getItem("_workspace_name_");
				var member_role = localStorage.getItem("_member_role_");

				var options = {};

				options.action = "/api/workspace/menager/" + member_role + "/" + workspace_name;
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						var create_date = "";
						var updated_date = "";


					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);


			};

			scwin.getKeySettingInfo = function () {

				var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

				// role code 가 admin 일 경우
				var data = {};
				var options = {};

				options.action = "/manager/mCert/common/selectAll";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

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


					} else {
						// alert("가져오기 화면 이동 실패");
					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};

			scwin.btn_update_workspace_detail_onclick = function (e) {

				var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

				if(common.isEmptyStr(txt_role_name.getValue())){
					var message = common.getLabel("lbl_userManager_user_role_detail_blank");
					alert(message);
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
					var options = {};

					data.role_id = localStorage.getItem("_role_id_");
					data.role_name = txt_role_name.getValue();
					// workspace id, list 로 관리해서 처리하기 ...
					var workspaceIDList = [];
					for (var i = 0; i < workspaceGridArr.length; i++) {

						var workspace_data = {};
						if (workspaceGridArr[i].select_yn == 1) {
							workspace_data.workspace_group_role_id = workspaceGridArr[i].workspace_group_role_id;
							workspace_data.workspace_id = workspaceGridArr[i].workspace_id;
							workspace_data.role_id = localStorage.getItem("_role_id_");
							workspace_data.select_yn = workspaceGridArr[i].select_yn;
							workspaceIDList[i] = workspace_data;

						}else {
							workspace_data.workspace_group_role_id = workspaceGridArr[i].workspace_group_role_id;
							workspace_data.workspace_id = workspaceGridArr[i].workspace_id;
							workspace_data.role_id = localStorage.getItem("_role_id_");
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
										alert(message);
										return false;
									}


								}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "Android") && (keyListArr[kList].key_type == "deploy" && keyListArr[kList].platform == "Android")){
									// console.log(keyGridArr[k]);
									if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
										var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
										alert(message);
										return false;
									}
								}

								if((keyGridArr[k].key_type == "build" && keyGridArr[k].platform == "iOS") && (keyListArr[kList].key_type == "build" && keyListArr[kList].platform == "iOS")){

									if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
										var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
										alert(message);
										return false;
									}


								}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "iOS") && (keyListArr[kList].key_type == "deploy" && keyListArr[kList].platform == "iOS")){
									// console.log(keyGridArr[k]);
									if((keyGridArr[k].key_id != keyListArr[kList].key_id) && (keyGridArr[k].select_yn == keyListArr[kList].select_yn)){
										var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
										alert(message);
										return false;
									}
								}

							}

							key_data.key_id = keyGridArr[k].key_id;
							key_data.role_id = localStorage.getItem("_role_id_");
							key_data.select_yn = keyGridArr[k].select_yn;
							keyIDList[count] = key_data;

							count++;
						}else {

							var key_data = {};
							key_data.key_id = keyGridArr[k].key_id;
							key_data.role_id = localStorage.getItem("_role_id_");
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


					// role id 생성 컨트롤러 이동..
					options.action = "/manager/userRole/update";
					options.mode = "asynchronous";
					options.mediatype = "application/json";
					options.requestData = JSON.stringify(data);
					options.method = "POST";

					options.success = function (e) {

						// reuturn 처리 안되어 있음
						var data = e.responseJSON;

						if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {
							var message = common.getLabel("lbl_userManager_user_role_detail_success");
							alert(message);
							$p.parent().wfm_main.setSrc("/xml/userManager.xml");


						} else {
							var message = common.getLabel("lbl_userManager_user_role_detail_fail");
							alert(message);
						}

					};

					options.error = function (e) {
						alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);

					};

					$p.ajax(options);

				} else {
					// create View

					if(!scwin.check_role_nameYN){
						var message = common.getLabel("lbl_userManager_user_role_detail_duplicate_message");
						alert(message);
						return false;
					}


					var workspaceGridArr = gridView1.getAllDisplayJSON();
					var keyGridArr = gridView2.getCheckedJSON("select_yn");
					var keyGridArr_checkTemp = gridView2.getCheckedJSON("select_yn");
					var projectGridArr = __project_list_data_real__.getAllJSON();

					var data = {};
					var options = {};

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
										alert(message);
										return false;
									}


								}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "Android") && (keyGridArr_checkTemp[kList].key_type == "deploy" && keyGridArr_checkTemp[kList].platform == "Android")){
									// console.log(keyGridArr[k]);
									if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

										var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
										alert(message);
										return false;
									}
								}

								if((keyGridArr[k].key_type == "build" && keyGridArr[k].platform == "iOS") && (keyGridArr_checkTemp[kList].key_type == "build" && keyGridArr_checkTemp[kList].platform == "iOS")){

									if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

										var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
										alert(message);
										return false;
									}


								}else if((keyGridArr[k].key_type == "deploy" && keyGridArr[k].platform == "iOS") && (keyGridArr_checkTemp[kList].key_type == "deploy" && keyGridArr_checkTemp[kList].platform == "iOS")){
									// console.log(keyGridArr[k]);
									if((keyGridArr[k].key_id != keyGridArr_checkTemp[kList].key_id) && (keyGridArr[k].select_yn == keyGridArr_checkTemp[kList].select_yn)){

										var message = common.getLabel("lbl_userManager_user_role_detail_sameKey_warning");
										alert(message);
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


					// role id 생성 컨트롤러 이동..
					options.action = "/manager/userRole/create";
					options.mode = "asynchronous";
					options.mediatype = "application/json";
					options.requestData = JSON.stringify(data);
					options.method = "POST";

					options.success = function (e) {

						// reuturn 처리 안되어 있음
						var data = e.responseJSON;

						if ((e.responseStatusCode === 200 || e.responseStatusCode === 201) && data != null) {
							var message = common.getLabel("lbl_userManager_user_role_detail_makeSuccess");
							alert(message);
							// $p.parent().wfm_main.setSrc("/xml/userManager.xml");


						} else {
							var message = common.getLabel("lbl_userManager_user_role_detail_makeFail");
							alert(message);
						}

					};

					options.error = function (e) {
						alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);

					};

					$p.ajax(options);


				}


			};

			// 수정 필요
			scwin.getWorkspaceRoleDetailList = function () {

				var role_id = localStorage.getItem("_role_id_");

				var options = {};

				options.action = "/manager/userRole/search/workspaceGroup/" + parseInt(role_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};

			scwin.getKeyRoleDetail = function () {

				var role_id = localStorage.getItem("_role_id_");

				var options = {};

				options.action = "/manager/userRole/search/profileGroup/" + parseInt(role_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");
				};

				$p.ajax(options);
			};

			scwin.getProjectByIDList = function (workspace_id, projectListReal) {

				var options = {};

				options.action = "/manager/workspace/search/projectList/" + parseInt(workspace_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");
				};

				$p.ajax(options);
            };

			scwin.getProjectGroupByIDList = function (workspace_group_role_id) {

				var options = {};

				options.action = "/manager/workspace/search/projectGroup/" + parseInt(workspace_group_role_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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

				}

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);
			};

			scwin.getAllProjectInfo = function () {

				var options = {};

				options.action = "/manager/workspace/search/projectListAll";
				options.mode = "synchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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
						}


					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};

			scwin.getAllProjectInfoByRoleID = function () {

				var options = {};
				var role_id = localStorage.getItem("_role_id_");

				options.action = "/manager/workspace/search/roleIdListInProject/" + parseInt(role_id);
				options.mode = "synchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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
										// gridView3.setCellChecked(i, "all_yn", true); // 추후에 수정하기
									}else {
										// gridView1.setCellChecked(i, "select_yn", false);
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

							//var distict = common.unique(projectGroupList, 'project_id');
							// __project_list_data_real__.setJSON(distict, false);


						}


					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};

			// 수정 해야함.
			// 더블 클릭시 project list 조회 기능 으로 전환하기
			scwin.gridView1_projectID_oncellclick = function (row, col) {

				// var realRow = gridView1.getRealRowIndex( row );
				// var data = __workspace_list_data__.getRowJSON(realRow);
				var columnID = gridView1.getColumnID(col);
				var member_setting_mode = $p.parent().wfm_main.getUserData("_member_setting_mode_");

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

				// all_yn, read_yn, update_yn, delete_yn, build_yn, deploy_yn, export_yn
				// if (gridView3.getCellChecked(row, columnID)) {
				// 	gridView3.setCellChecked(row, columnID, false);
                //
				// } else if (!gridView3.getCellChecked(row, columnID)) {
				// 	gridView3.setCellChecked(row, columnID, true);
				// }


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
				// data.project_id; 조건에 맞게 row 값 찾기
				// 추가로 뭔가가 필요할거 같은데....
				// 아래 real data list row 값을 정확하게 가져 올수 있는 방법이????
				// for 문 돌려서 i 값 으로 setRowJSON 값을 세팅한다..
				console.log(data);

				var projectDataListReal = __project_list_data_real__.getAllJSON();
				for (var realRow = 0; realRow < projectDataListReal.length; realRow++) {

					if (data.project_id == projectDataListReal[realRow].project_id) {


						__project_list_data_real__.setRowJSON(realRow, data, true);
						// console.log(__project_list_data_real__.getRowJSON(realRow));
					}

				}
			};

			scwin.getAllCheckedProjectInfoList = function () {

				var workSpaceJsonRow = __workspace_list_data__.getRowJSON(0);
				var projectListReal = __project_list_data_real__.getAllJSON();

				scwin.getWorkspaceCheckedProjectIDList(workSpaceJsonRow.workspace_id, workSpaceJsonRow.workspace_group_role_id, projectListReal);

			};

			scwin.getWorkspaceCheckedProjectIDList = function (workspace_id, workspace_group_role_id, projectListReal) {

				var options = {};

				options.action = "/manager/workspace/search/projectListWithGroupRole/" + parseInt(workspace_id) + "/" + parseInt(workspace_group_role_id);
				options.mode = "synchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
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
										// gridView3.setCellChecked(i, "all_yn", true); // 추후에 수정하기
									}else {
										// gridView1.setCellChecked(i, "select_yn", false);
									}
								}

								projectGroupList.push(temp);

							}


							var distict = common.unique(projectGroupList, 'project_id');
							__project_list_data__.setJSON(distict, true);

							var projectListData = __project_list_data__.getAllJSON();
							// workspace | project 중복 체크
							//console.log("projectListData");
							//console.log(projectListData);
							//console.log(projectListReal);
							for (var rowList in projectListData) {

								for (var rowReal in projectListReal) {

									if ((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id)) {

										__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
										//__project_list_data_real__.setRowJSON(rowReal, projectListData[rowList], true);
									} else {

									}

									if((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id) && (projectListData[rowList].workspace_group_role_id == "")){
										__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
									}

									if((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id) && (projectListData[rowList].project_group_role_id == "")){
										__project_list_data__.setRowJSON(rowList, projectListReal[rowReal], true);
									}

									// all_yn 테스트
									if((projectListReal[rowReal].workspace_id == projectListData[rowList].workspace_id) && (projectListReal[rowReal].project_id == projectListData[rowList].project_id)
											&& (projectListData[rowList].read_yn == 1 && projectListData[rowList].update_yn == 1 && projectListData[rowList].delete_yn == 1 && projectListData[rowList].build_yn == 1
													&& projectListData[rowList].deploy_yn == 1 && projectListData[rowList].export_yn == 1) ){
										__project_list_data__.setCellData(rowList, "all_yn",1);
									}
									// else {
									// 	__project_list_data__.setCellData(rowList, "all_yn",0);
									// }



								}
							}


						}


					} else {

					}
				}

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};


			scwin.getWorkspaceGroupIdList = function() {

				var role_id = localStorage.getItem("_role_id_");

				var options = {};

				options.action = "/manager/workspace/search/roleIdListInWorkspaceGroup/" + parseInt(role_id);
				options.mode = "synchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
						var workspaceGroupList = [];

						if (data.length == 0) {

						} else {

							var workspaceGridArr = gridView1.getAllVisibleJSON();
							var workspaceDataListArr = __workspace_list_data__.getAllJSON();

							for (var row in data) {
								var temp = {};

								console.log("workspaceGroupID /.... ");
								console.log(data);

								for(var i = 0; i< workspaceGridArr.length; i++){

									if(workspaceGridArr[i].workspace_id == data[row].workspace_id){
										gridView1.setCellChecked(i, "select_yn", true);
									}else {
										// gridView1.setCellChecked(i, "select_yn", false);
									}
								}

								for(var idx = 0; idx < workspaceDataListArr.length; idx++){

									if(workspaceDataListArr[idx].workspace_id == data[row].workspace_id){
										__workspace_list_data__.setCellData(idx, "workspace_group_role_id", data[row].workspace_group_role_id);
									}else {

									}

								}

								// temp["workspace_id"] = data[row].workspace_id;
								// temp["workspace_name"] = data[row].workspace_name;
								// temp["workspace_group_role_id"] = data[row].workspace_group_role_id;
								// temp["select_yn"] = "";


								// workspaceGroupList.push(temp);
								// workspace | project 중복 체크

							}

							// var distict = common.unique(workspaceGroupList, 'workspace_id');
							// __workspace_list_data__.setJSON(distict, false);


						}


					} else {

					}
				};

				options.error = function (e) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n");

				};

				$p.ajax(options);

			};


			scwin.select_check_role_name = function(){

				if(common.isEmptyStr(txt_role_name.getValue())){
					var message = common.getLabel("lbl_userManager_user_role_detail_blank");
					alert(message);
					return false;
				}

				var data = {};

				data.role_name = txt_role_name.getValue();

				var options = {};

				// check userid controller 추가
				options.action = "/manager/userRole/search/checkRoleName";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					console.log(data);
					if(data != null){
						if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
							if (data != null) {

								if(data[0].role_name_not_found == "no"){
									var message = common.getLabel("lbl_userManager_user_role_detail_unavailable");
									alert(message);
									scwin.check_role_nameYN = false;
								}else if(data[0].role_name_not_found == "yes"){
									var message = common.getLabel("lbl_userManager_user_role_detail_available");
									alert(message);
									scwin.check_role_nameYN = true;
								}

								//alert(" 해당 이메일이 존재 합니다.");

							}
						}
					}

				};

				options.error = function (e) {
					if(e.responseStatusCode === 500){
						var message = common.getLabel("lbl_userManager_user_role_detail_available");
						alert(message);
						// scwin.build_project_name_yn = true;
						scwin.check_role_nameYN = true;
					}else {
						alert( "message:"+e.responseText+"\n" );
					}

				};

				$p.ajax( options );

			};



			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'workspace_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_permission'}}]},{T:1,N:'xf:group',A:{class:'fr'},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm type1 fl',id:'branch_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_update_workspace_detail_onclick',useLocale:'true',localeRef:'lbl_modify'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'form_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_name'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'txt_role_name',style:''}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.select_check_role_name',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_workspace'}},{T:1,N:'xf:group',A:{id:'',class:'wq_gvw'},E:[{T:1,N:'w2:gridView',A:{'ev:oncellclick':'scwin.gridView1_projectID_oncellclick',scrollByColumnAdaptive:'false',rowNumVisible:'true',scrollByColumn:'false',defaultCellHeight:'20',contextMenu:'true',dataList:'data:__workspace_list_data__',style:'height:100px;',autoFit:'allColumn','ev:oncelldblclick':'scwin.gridView1_oncelldblclick',id:'gridView1',visibleRowNum:'10',class:'gvwbox mt0',autoFitMinWidth:'0',columnMove:'',rowNumWidth:'50',keepDefaultColumnWidth:'true'},E:[{T:1,N:'w2:caption',A:{style:'',id:'caption1',value:''}},{T:1,N:'w2:header',A:{style:'',id:'header1'},E:[{T:1,N:'w2:row',A:{style:'',id:'row1'},E:[{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'50',inputType:'text',style:'height:30px;',id:'column10',value:'',blockSelect:'false',displayMode:'label',fixColumnWidth:'true',useLocale:'true',localeRef:'lbl_select'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px;',id:'column9',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_workspace'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px;',id:'column11',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_workspace_id'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px;',id:'column11',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_workspace_group_role_id'}}]}]},{T:1,N:'w2:gBody',A:{style:'',id:'gBody1'},E:[{T:1,N:'w2:row',A:{style:'',id:'row2'},E:[{T:1,N:'w2:column',A:{textAlign:'center',width:'50',checkboxLabel:'Y',inputType:'checkbox',style:'height:20px;',id:'select_yn',value:'',displayMode:'label',heckboxLabelColumn:' '}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px;',id:'workspace_name',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'workspace_id',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'workspace_group_role_id',value:'',blockSelect:'false',displayMode:'label'}}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_certification'}},{T:1,N:'xf:group',A:{id:'',class:'wq_gvw'},E:[{T:1,N:'w2:gridView',A:{scrollByColumnAdaptive:'false',rowNumVisible:'true',scrollByColumn:'false',defaultCellHeight:'4',contextMenu:'true',dataList:'data:__key_list_data__',style:'height:100px;',autoFit:'allColumn','ev:oncelldblclick':'scwin.gridView1_oncelldblclick','ev:oncellclick':'scwin.gridView2_key_setting_oncellclick',id:'gridView2',visibleRowNum:'10',class:'gvwbox mt0',autoFitMinWidth:'0',columnMove:'',rowNumWidth:'50',keepDefaultColumnWidth:'true'},E:[{T:1,N:'w2:caption',A:{style:'',id:'caption2',value:''}},{T:1,N:'w2:header',A:{style:'',id:'header2'},E:[{T:1,N:'w2:row',A:{style:'',id:'row11'},E:[{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'50',inputType:'text',style:'height:30px;',id:'',value:'',blockSelect:'false',displayMode:'label',fixColumnWidth:'true',useLocale:'true',localeRef:'lbl_select'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header2_platform'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header2_key_type'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header2_key'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_key_id'}}]}]},{T:1,N:'w2:gBody',A:{style:'',id:'gBody2'},E:[{T:1,N:'w2:row',A:{style:'',id:'row22'},E:[{T:1,N:'w2:column',A:{textAlign:'center',width:'70',checkboxLabel:'Y',checkboxLabelColumn:' ',inputType:'checkbox',style:'height:30px;',id:'select_yn',value:'',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'platform',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'key_type',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'key_name',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'key_id',value:'',blockSelect:'false',displayMode:'label'}}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'dfbox mt20',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'df_tit fl',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_project_list'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'gvwbox',tagname:''},E:[{T:1,N:'w2:gridView',A:{id:'gridView3',style:'height: 100px;',scrollByColumn:'false',defaultCellHeight:'15',scrollByColumnAdaptive:'false',dataList:'data:__project_list_data__',visibleRowNum:'10',rowNumVisible:'true',autoFit:'allColumn',contextMenu:'true','ev:oncellclick':'scwin.gridView3_project_group_role_oncellclick',autoFitMinWidth:'0',class:'wq_gvw',rowNumWidth:'50',keepDefaultColumnWidth:'true'},E:[{T:1,N:'w2:header',A:{style:'',id:'header3'},E:[{T:1,N:'w2:row',A:{style:'',id:'row111'},E:[{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px;',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_workspace'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_project'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_project_id'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_workspace_id'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_project_group_role_id'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_workspace_group_role_id'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_all'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_read'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_update'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_delete'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_build_history'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_deploy'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'',value:'',blockSelect:'false',displayMode:'label',useLocale:'true',localeRef:'lbl_userManager_user_role_detail_header3_export'}}]}]},{T:1,N:'w2:gBody',A:{style:'',id:'gBody3'},E:[{T:1,N:'w2:row',A:{style:'',id:'row222'},E:[{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:30px;',id:'workspace_name',value:'workspace',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'project_name',value:'project',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'project_id',value:'project id',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'workspace_id',value:'workspace id',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'project_group_role_id',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{removeBorderStyle:'false',width:'70',inputType:'text',style:'height:20px',id:'workspace_group_role_id',value:'',blockSelect:'false',displayMode:'label'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'all_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'read_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'update_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'delete_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'build_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'deploy_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}},{T:1,N:'w2:column',A:{width:'70',inputType:'checkbox',style:'height:20px;',id:'export_yn',value:'',displayMode:'label',textAlign:'center',checkboxLabelColumn:' ',checkboxLabel:'Y'}}]}]}]}]}]}]}]}]})