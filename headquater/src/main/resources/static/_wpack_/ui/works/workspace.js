/*amd /xml/workspace.xml 26888 06ca29715978c4a1236526fe8a273cbfb37440c504146c54eb50f63553a4729a */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.workspace_name_check_yn = false;
			scwin.onpageload = function () {

				const workspace_json = $p.parent().__workspace_data__.getAllJSON();
				const buildproj_json = $p.parent().__buildproject_data__.getAllJSON();

				scwin.setWorkspace(workspace_json, buildproj_json);
			};

			scwin.grp_workspace_onclick = function (e) {
				const acdbox = $('#'+this.render.id);
				acdbox.toggleClass('on');
				if (acdbox.hasClass("on")) {
					acdbox.find('.acd_congrp').slideDown();
				} else {
					acdbox.find('.acd_congrp').slideUp();
				}
			};

			scwin.setWorkspace = function (workspace_json, buildproj_json) {
				// workspace generator 초기화
				generator_workspace_list.removeAll();

				for (const [idx, workspace_data] of workspace_json.entries()) {
					if (workspace_data.delete_yn != "1") {
						const buildbtnInx = generator_workspace_list.insertChild();
						let workspace_select = generator_workspace_list.getChild(idx, "txt_workspace_lbl");
						workspace_select.setValue(workspace_data.workspace_name);

						let buildprojectadd = generator_workspace_list.getChild(buildbtnInx, 'btn_create_project');
						buildprojectadd.setUserData('member_id', workspace_data.member_id);
						buildprojectadd.setUserData('workspace_name', workspace_data.workspace_name);
						buildprojectadd.setUserData('workspace_group_role_id', workspace_data.workspace_group_role_id);
						buildprojectadd.setUserData('projectsampleYn', "N");


						let buildprojectimportAdd = generator_workspace_list.getChild(buildbtnInx, "btn_import_project");
						buildprojectimportAdd.setUserData('member_id', workspace_data.member_id);
						buildprojectimportAdd.setUserData('workspace_name', workspace_data.workspace_name);
						buildprojectimportAdd.setUserData('workspace_group_role_id', workspace_data.workspace_group_role_id);
						buildprojectimportAdd.setUserData('projectsampleYn', "N");

						let temp_generator = generator_workspace_list.getChild(idx, "list_buildproj_generator");

						let count = 0;
						let android_cnt = 0;
						let ios_cnt = 0;

						for (const [idx2, proj_data] of buildproj_json.entries()) {
							if (proj_data.workspace_id === workspace_data.id) {
								if (proj_data.read_yn === "1") {
									const genId = temp_generator.insertChild();
									const generatedId = temp_generator.getGeneratedIndex();

									//set icon
									const span_icon = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_buildproj_platform");
									const txt_proj_name = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_txt_buildproj_project_name");
									const btn_build = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_btn_build");
									const btn_delete = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_btn_delete");
									const btn_setting = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_btn_setting");
									const btn_deploy = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_btn_deploy");
									const grp_buildproj_btn = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_grp_list_buildproj");
									const btn_export = temp_generator.getChild(genId, "generator_workspace_list_" + generatedId + "_btn_export");

									btn_export.setUserData("platform", proj_data.platform);
									btn_export.setUserData("projectName", proj_data.project_name);
									btn_export.setUserData("build_id", proj_data.project_id);
									btn_export.setUserData("project_dir_path", proj_data.project_dir_path);
									btn_export.setUserData("workspace_name", proj_data.workspace_name);

									btn_build.setUserData("platform", proj_data.platform);
									btn_build.setUserData("projectName", proj_data.project_name);
									btn_build.setUserData("project_pkid", proj_data.project_id);
									btn_build.setUserData("workspace_pkid", proj_data.workspace_id);
									btn_build.setUserData("product_type", proj_data.product_type);

									btn_deploy.setUserData("platform", proj_data.platform);
									btn_deploy.setUserData("projectName", proj_data.project_name);
									btn_deploy.setUserData("project_pkid", proj_data.project_id);
									btn_deploy.setUserData("workspace_pkid", proj_data.workspace_id);

									btn_delete.setUserData("projectName", proj_data.project_name);
									btn_delete.setUserData("project_pkid", proj_data.project_id);

									grp_buildproj_btn.setUserData("platform", proj_data.platform);
									grp_buildproj_btn.setUserData("projectName", proj_data.project_name);
									grp_buildproj_btn.setUserData("project_pkid", proj_data.project_id);
									grp_buildproj_btn.setUserData("workspace_pkid", proj_data.workspace_id);
									grp_buildproj_btn.setUserData("workspace_name", workspace_data.workspace_name);

									btn_setting.setUserData("platform", proj_data.platform);
									btn_setting.setUserData("projectName", proj_data.project_name);
									btn_setting.setUserData("project_pkid", proj_data.project_id);
									btn_setting.setUserData("workspace_pkid", proj_data.workspace_id);
									btn_setting.setUserData("workspace_name", workspace_data.workspace_name);

									// project group 조건에 따라서 show hide 처리 하기
									if (proj_data.read_yn !== "1") {
										btn_setting.hide();
									}

									if (proj_data.update_yn === "1") {
										// 수정 기능 활성화 되어있으면
										// localstorage.setItem 값 설정 하고
										// 이후 project setting 화면에서 제어하기
										btn_setting.setUserData("update_yn", proj_data.update_yn);
										grp_buildproj_btn.setUserData("update_yn", proj_data.update_yn);
									}

									if (proj_data.delete_yn === "1") {
										$(".btn_role_cm").click(function () {
											$(".layer_pop").css("display", "block");
											$("body").append("<div class='dim'></div>");
										});
										$(".btn_pop_close").click(function () {
											$(".layer_pop").css("display", "none");
											$("div").remove(".dim");
										});
									} else {
										btn_delete.hide();
									}

									if (proj_data.build_yn != "1") {
										btn_build.hide();
									}

									if (proj_data.deploy_yn != "1") {
										btn_deploy.hide();
									}

									if (proj_data.export_yn != "1") {
										btn_export.hide();
									}

									count++;

									if (proj_data.platform.toLowerCase() === "android") {
										const android = common.getLabel("lbl_android");
										span_icon.addClass("ico_and");
										span_icon.setValue(android);
										android_cnt++;
									} else {
										const ios = common.getLabel("lbl_ios");
										span_icon.addClass("ico_ios");
										span_icon.setValue(ios);
										ios_cnt++;
									}
									txt_proj_name.setValue(proj_data.project_name);
								}
							}
						}

						let span_android = generator_workspace_list.getChild(idx, "count_android");
						let androidCnt = common.getLabel("lbl_android_s");
						androidCnt = common.getFormatStr("%s", android_cnt);
						let iosCnt = common.getLabel("lbl_ios_s");
						iosCnt = common.getFormatStr("%s", ios_cnt);

						span_android.setValue(androidCnt);
						let span_ios = generator_workspace_list.getChild(idx, "count_ios");
						span_ios.setValue(iosCnt);
					}
				}
			};

			scwin.btn_create_project_onclick = function () {
				const setmember_id = this.getUserData("member_id");
				const setworkspace_name = this.getUserData("workspace_name");
				const setworkspace_group_role_id = this.getUserData("workspace_group_role_id");
				const setsampleProjectYn = this.getUserData("projectsampleYn");

				$p.top().scwin.__workspace_name__ = setworkspace_name;
				$p.top().scwin.__workspace_group_role_id__ = setworkspace_group_role_id;
				$p.top().scwin.__sample_project_yn__ = setsampleProjectYn;

				// workspace key 값 조회
				scwin.select_workspace(setmember_id, setworkspace_name);
			};

			// project delete
			scwin.delete_build_project_pid = function (build_pid, project_name, del_project_name) {
				let data = {};
				data.id = parseInt(!!build_pid ? build_pid : 0);
				data.project_name = project_name;

				const url = common.uri.deleteBuildProject;
				const method = "POST";
				const headers = {"Content-Type": "application/json"};

				common.http.fetch(url, method, headers, data)
						.then(res => {
							if (Array.isArray(res) && res[0].project_name_found_check == "yes") {
								$p.top().scwin.updateWorkspaceData();
							}
						})
						.catch(err => {
							common.win.alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText);
						});
			};

			scwin.getWorkSpaceReSelect = function (member_id, domain_id) {
				const url = common.uri.getWorkSpaceReSelect(member_id, domain_id);
				const method = "GET"
				const headers = {"Content-Type": "application/json; charset=UTF-8"};

				common.http.fetch(url, method, headers)
						.then(data => {
							let workspace = [];

							for (const [row, workspace_data] of data.entries()) {
								let temp = {};

								temp["workspace_name"] = workspace_data.workspace_name;
								temp["id"] = workspace_data.id;
								temp["member_id"] = workspace_data.member_id;
								temp["favorite_flag"] = workspace_data.favorite_flag;

								workspace.push(temp);
							}

							$p.parent().__workspace_data__.removeAll();
							const distinct = common.unique(workspace, 'id');
							$p.parent().__workspace_data__.setJSON(distinct, true);

							scwin.getBuildProjectInfoReSelect();
						})
						.catch(err => {
							console.log("code:" + err.status + "\n" + "message:" + err.responseText + "\n" + "error:" + err);
						});
			};

			// workspace 재조회
			scwin.getBuildProjectInfoReSelect = function () {
				const url = common.uri.buildProjects;
				const method = "GET";
				const headers = {"Content-Type": "application/json; charset=UTF-8"};

				common.http.fetchGet(url, method, headers)
						.then(data => {
							let buildproj = [];

							for (const [row, proj_data] of data.entries()) {
								var temp = {};

								temp["id"] = proj_data.id;
								temp["workspace_id"] = proj_data.workspace_id;
								temp["project_name"] = proj_data.project_name;
								temp["platform"] = proj_data.platform;
								temp["description"] = proj_data.description;
								temp["status"] = proj_data.status;
								temp["template_version"] = proj_data.template_version;
								temp["project_dir_path"] = proj_data.project_dir_path;
								temp["created_date"] = proj_data.created_date;
								temp["updated_date"] = proj_data.updated_date;

								buildproj.push(temp);
							}

							$p.parent().__buildproject_data__.removeAll();
							var distinct = common.unique(buildproj, 'id');
							$p.parent().__buildproject_data__.setJSON(distinct, true);
						})
						.catch(err => {
							common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n" + "error:" + err);
						});
			};

			scwin.setProjectExportDownload = function (msg) {

                //

				let data = {};
				data = msg;

				const url = common.uri.exportDownloadA3;
				const method = 'POST';
				const headers = {'Content-Type': 'application/json'};
				const option = {type: 'application/octet-stream'};

				common.http.fileDownload(url, method, headers, data, option);
				// window.open(common.uri.exportDownload(msg));


			};

			scwin.btn_import_project_onclick = function () {
				const setmember_id = this.getUserData("member_id");
				const setworkspace_name = this.getUserData("workspace_name");
				const setsampleProjectYn = this.getUserData("projectsampleYn");

				$p.top().scwin.__member_id__ = setmember_id;
				$p.top().scwin.__workspace_name__ = setworkspace_name;
				$p.top().scwin.__sample_project_yn__ = setsampleProjectYn;

				scwin.select_workspace_ajax_to(setmember_id, setworkspace_name);
			};

			scwin.btn_create_project_onclick = function () {
				const setmember_id = this.getUserData("member_id");
				const setworkspace_name = this.getUserData("workspace_name");
				const setworkspace_group_role_id = this.getUserData("workspace_group_role_id");
				const setsampleProjectYn = this.getUserData("projectsampleYn");

				$p.top().scwin.__workspace_name__ = setworkspace_name;
				$p.top().scwin.__workspace_group_role_id__ = setworkspace_group_role_id;
				$p.top().scwin.__sample_project_yn__ = setsampleProjectYn;

				// workspace key 값 조회
				scwin.select_workspace(setmember_id, setworkspace_name);
			};

			scwin.select_workspace_ajax_to = function (setmember_id, setworkspace_name) {
				const member_id = setmember_id;
				const workspace_name = setworkspace_name;

				let data = {};
				data.member_id = parseInt(member_id);
				data.workspace_name = workspace_name;

				const url = common.uri.searchWorkspaceID(data);
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				common.http.fetchGet(url, method, headers).then((res)=>{
					return res.json();
				}).then((data) => {
					scwin.import_build_project(data);
				})
						.catch(() => {
							const message = common.getLabel("lbl_workspace_moveFail");
							common.win.alert(message);
						});
			};

			scwin.select_workspace = function (setmember_id, setworkspace_name) {
				const member_id = setmember_id;
				const workspace_name = setworkspace_name;

				let data = {};
				data.member_id = parseInt(member_id);
				data.workspace_name = workspace_name;

				const url = common.uri.searchWorkspaceID(data);
				const method = "GET";
				const headers = {"Content-Type": "application/json"};

				common.http.fetchGet(url, method, headers)
						.then(async (res) => {
							const data = await res.json();
							$p.top().scwin.__workspace_id__ = data.workspace_id;
							scwin.create_build_project(data);
						})
						.catch(() => {
							const message = common.getLabel("lbl_workspace_projectMoveFail");
							common.win.alert(message);
						});
			};

			scwin.select_check_workspace_name = function (workspace_name) {
				const url = common.uri.checkWorkspaceName(workspace_name);
				const method = "GET"
				const headers = {"Content-Type": "application/json"};

				common.http.fetchGet(url, method, headers)
						.then(data => {
							if (data != null) {
								const message = common.getLabel("lbl_exist_name");
								common.win.alert(message);
							}
						})
						.catch(err => {
							if (err.responseStatusCode === 500) {
								const message = common.getLabel("lbl_can_use_name");
								common.win.alert(message);
								scwin.workspace_name_check_yn = true;
							} else {
								alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText + "\n" + "error:" + err.requestBody);
							}
						});
			};

			scwin.grp_list_link_onclick = function (e) {
				const controlList = this.parentControl.parentControl.childControlList;
				for (const idx in controlList) {
					if (controlList[idx] === this.parentControl) {
						this.parentControl.toggleClass("on");
					} else {
						controlList[idx].removeClass("on");
					}
				}
			};

			scwin.grp_list_buildproj_onclick = function () {
				const project_name = this.getUserData("projectName");
				const project_pkid = this.getUserData("project_pkid");
				const workspace_name = this.getUserData("workspace_name");
				const platform = this.getUserData("platform");
				const workspace_pkid = this.getUserData("workspace_pkid");
				const update_yn = this.getUserData("update_yn");

				// build localStorage data set
				$p.top().scwin.buildPlatform = platform;
				$p.top().scwin.buildProjectId = project_pkid;
				$p.top().scwin.workspaceId = workspace_pkid;
				$p.top().scwin.buildProjectName = project_name;
				$p.top().scwin.buildProjecPkId = project_pkid;
				$p.top().scwin.__workspace_name__ = workspace_name;
				$p.top().scwin.__update_yn__ = update_yn;

				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0100020100");
				$p.top().scwin.add_tab(menu_key);
			};

			scwin.create_deploy_project = function () {
				//TODO
				//$p.parent().wfm_main.setSrc("/xml/project_add.xml");
			};

			scwin.import_deploy_project = function () {
				//TODO
				//$p.parent().wfm_main.setSrc("/xml/project_import.xml");
			};

			scwin.create_build_project = function (data) {
				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0100010000");
				$p.top().scwin.add_tab(menu_key, null, data);
			};

			scwin.import_build_project = function (data) {
				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0100040000");
				$p.top().scwin.add_tab(menu_key, null, data);
			};

			scwin.btn_deploy_onclick = function () {
				const platform = this.getUserData("platform");
				const deployProjectName = this.getUserData("projectName");
				const deploy_project_id = this.getUserData("project_pkid");
				const workspace_id = this.getUserData("workspace_pkid");

				let deployTask = [];

				let data = {};
				data.platform = platform;
				data.projectName = deployProjectName;
				data.project_pkid = deploy_project_id;
				data.workspace_pkid = workspace_id;

				deployTask.push(data);

				$p.parent().$p.parent().__deploytask_data__.removeAll();
				$p.parent().__deploytask_data__.setJSON(deployTask);

				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0100030000");
				$p.top().scwin.add_tab(menu_key);
				WebSquare.getBody().hideToolTip();
			};

			scwin.btn_export_onclick = async function () {
				const project_name = this.getUserData("projectName");
				const build_id = this.getUserData("build_id");
				const workspace_name = this.getUserData("workspace_name");
				const project_dir_path = this.getUserData("project_dir_path");

				let data = {};
				data.build_id = build_id;
				data.workspace_name = workspace_name;
				data.project_name = project_name;
				data.project_dir_name = project_dir_path;

				const url = common.uri.projectExportStart;
				const method = "POST";
				const headers = {"Content-Type": "application/json"};

				await common.http.fetch(url, method, headers, data)
						.catch(err => {
							alert("code:" + err.responseStatusCode + "\n" + "message:" + err.responseText + "\n" + "error:" + err.requestBody);
						})
			};

			scwin.btn_build_onclick = function () {
				const platform = this.getUserData("platform");
				const buildProjectName = this.getUserData("projectName");
				const build_project_id = this.getUserData("project_pkid");
				const workspace_id = this.getUserData("workspace_pkid");
				const product_type = this.getUserData("product_type");

				let buildAction = [];

				let data = {};
				data.platform = platform;
				data.projectName = buildProjectName;
				data.project_pkid = build_project_id;
				data.workspace_pkid = workspace_id;
				data.product_type = product_type;

				buildAction.push(data);

				$p.parent().__buildaction_data__.setJSON(buildAction);

				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0101000000");
				$p.top().scwin.add_tab(menu_key);
				WebSquare.getBody().hideToolTip();
			};

			scwin.btn_setting_onclick = function () {
				// build setting 페이지로 이동 화면 구현.
				const project_name = this.getUserData("projectName");
				const project_pkid = this.getUserData("project_pkid");
				const workspace_name = this.getUserData("workspace_name");
				const platform = this.getUserData("platform");
				const workspace_pkid = this.getUserData("workspace_pkid");
				const update_yn = this.getUserData("update_yn");

				// build localStorage data set
				$p.top().scwin.buildPlatform = platform;
				$p.top().scwin.buildProjectId = project_pkid;
				$p.top().scwin.workspaceId = workspace_pkid;
				$p.top().scwin.buildProjectName = project_name;
				$p.top().scwin.buildProjecPkId = project_pkid;
				$p.top().scwin.__workspace_name__ = workspace_name;
				$p.top().scwin.__update_yn__ = update_yn;

				const paramData = {
					buildPlatform: platform,
					buildProjectId: project_pkid,
					workspaceId: workspace_pkid,
					buildProjectName: project_name,
					buildProjecPkId: project_pkid,
					workspaceName: workspace_name,
					updateYN: update_yn
				};

				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0100020000")
				$p.top().scwin.add_tab(menu_key, null, paramData);
				WebSquare.getBody().hideToolTip();
			};

			scwin.btn_delete_onclick = async function () {
				const message = common.getLabel("lbl_workspace_removeConfirm");
				if (await common.win.confirm(message)) {
					const build_project_id = this.getUserData("project_pkid");
					const project_name = this.getUserData("projectName");

					const opts = {
						'do': 'deleteProject',
						'project_id': build_project_id,
						'project_name': project_name
					}

					await common.win.prompt("", null, opts);
				}
			};

			scwin.grp_list_buildproj_onclick = function() {
				// build setting 페이지로 이동 화면 구현.
				const project_name = this.getUserData("projectName");
				const project_pkid = this.getUserData("project_pkid");
				const workspace_name = this.getUserData("workspace_name");
				const platform = this.getUserData("platform");
				const workspace_pkid = this.getUserData("workspace_pkid");
				const update_yn = this.getUserData("update_yn");

				// build localStorage data set
				$p.top().scwin.buildPlatform = platform;
				$p.top().scwin.buildProjectId = project_pkid;
				$p.top().scwin.workspaceId = workspace_pkid;
				$p.top().scwin.buildProjectName = project_name;
				$p.top().scwin.buildProjecPkId = project_pkid;
				$p.top().scwin.__workspace_name__ = workspace_name;
				$p.top().scwin.__update_yn__ = update_yn;

				const paramData = {
					buildPlatform: platform,
					buildProjectId: project_pkid,
					workspaceId: workspace_pkid,
					buildProjectName: project_name,
					buildProjecPkId: project_pkid,
					workspaceName: workspace_name,
					updateYN: update_yn
				};

				// /ui/works/project_setting.xml
				const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0100020000");
				$p.top().scwin.add_tab(menu_key, null, paramData);
				WebSquare.getBody().hideToolTip();
			};
			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_workspace'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:generator',A:{id:'generator_workspace_list',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_workspace',class:'acdbox',tagname:'li','ev:onclick':'scwin.grp_workspace_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'txt_workspace_lbl',label:'acd tit',class:'acd_tit'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'btn_create_project',type:'button',class:'btn_cm add',useLocale:'true',localeRef:'lbl_create','ev:onclick':'scwin.btn_create_project_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{id:'btn_import_project',useLocale:'true',localeRef:'lbl_import',style:'',type:'button',class:'btn_cm add','ev:onclick':'scwin.btn_import_project_onclick'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'ectgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'ectbox'},E:[{T:1,N:'w2:span',A:{style:'',id:'',label:'',class:'ico_android',useLocale:'true',localeRef:'lbl_android'}},{T:1,N:'w2:span',A:{class:'item_cnt_txt',id:'count_android',label:'0',style:''}}]},{T:1,N:'xf:group',A:{class:'ectbox',id:'',style:''},E:[{T:1,N:'w2:span',A:{class:'ico_ios',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios'}},{T:1,N:'w2:span',A:{class:'item_cnt_txt',id:'count_ios',label:'5',style:''}}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'xf:generator',A:{id:'list_buildproj_generator',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'grp_list_buildproj',class:'acd_txtbox','ev:onclick':'scwin.grp_list_buildproj_onclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'android',id:'buildproj_platform',class:'ico_android'}},{T:1,N:'w2:textbox',A:{style:'',id:'txt_buildproj_project_name',label:'item txt',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_build',class:'btn_cm icon btn_i_play',useLocale:'true',localeRef:'lbl_build',tooltipLocaleRef:'lbl_build',toolTipDisplay:'true','ev:onclick':'scwin.btn_build_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_setting',class:'btn_cm icon btn_i_setting',useLocale:'true',localeRef:'lbl_setting',tooltipLocaleRef:'lbl_workspace_move',toolTipDisplay:'true','ev:onclick':'scwin.btn_setting_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_deploy',class:'btn_cm icon btn_i_module01',useLocale:'true',localeRef:'lbl_project_deploy_task',tooltipLocaleRef:'lbl_project_deploy_task',toolTipDisplay:'true','ev:onclick':'scwin.btn_deploy_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_delete',class:'btn_cm icon btn_i_reset',useLocale:'true',localeRef:'lbl_delete',tooltipLocaleRef:'lbl_delete',toolTipDisplay:'true','ev:onclick':'scwin.btn_delete_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_export',class:'btn_cm icon btn_i_download',useLocale:'true',localeRef:'lbl_export',tooltipLocaleRef:'lbl_export',toolTipDisplay:'true','ev:onclick':'scwin.btn_export_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})