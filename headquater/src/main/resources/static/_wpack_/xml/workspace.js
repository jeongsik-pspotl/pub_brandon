/*amd /xml/workspace.xml 39072 a0e00c4d34d98fc21fe7d560cd73dd6c24ac7b6d535337d0c1155be55ac676bd */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',A:{},E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			 scwin.workspace_name_check_yn = false;
			 scwin.onpageload = function () {
				 common.setScopeObj(scwin);

				 scwin.initPage();
			 };

			 scwin.onpageunload = function () {

			 };

			 scwin.initPage = function () {


				 var workspace_json = $p.parent().__workspace_data__.getAllJSON();
				 var buildproj_json = $p.parent().__buildproject_data__.getAllJSON();

				 scwin.setWorkspace(workspace_json, buildproj_json);
			 };

			 // project delete
			 scwin.delete_build_project_pid = function (build_pid, project_name, del_project_name) {

				 var pid = build_pid;

				 var data = {};
				 data.id = parseInt(pid);
				 data.project_name = project_name;


				 var options = {};

				 options.action = "/manager/build/project/delete";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "POST";
                 options.requestData = JSON.stringify(data);

				 options.success = function (e) {
					 var data = e.responseJSON;

					 if (e.responseStatusCode === 200 || e.responseStatusCode === 201) {
						 if (data[0].project_name_found_check == "yes") {

							 $(".layer_pop").css("display", "none");
							 $("div").remove(".dim");
							 txt_project_delete_name.setValue("");
							 $p.parent().scwin.allRefrashSelect();

							 $p.parent().wfm_main.setSrc("/xml/workspace.xml");
						 } else if (data[0].project_name_found_check == "no") {
							 var label = common.getLabel("lbl_workspace_delete_message");
							 label = common.getFormatStr(label,del_project_name);
							 lbl_project_delete_name_check.setLabel(label);
							 txt_project_delete_name.setValue("");
							 // $p.parent().scwin.allRefrashSelect();
						 }



					 }

				 };

				 options.error = function (e) {
					 if (e.responseStatusCode === 204) {

					 } else if (e.responseStatusCode === 500) {

					 } else {
						 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n");
					 }


				 };

				 $p.ajax(options);
			 };


			 scwin.getWorkSpaceReSelect = function (member_id, domain_id) {

				 $.ajax({
					 url: "/api/workspaces/memberid/" + parseInt(member_id) + "/" + parseInt(domain_id),
					 type: "get",
					 accept: "application/json",
					 async: false,
					 contentType: "application/json; charset=utf-8",
					 success: function (e) {
						 var data = e;

						 if (data !== null) {

							 var workspace = [];

							 for (var row in data) {
								 var temp = {};

								 temp["workspace_name"] = data[row].workspace_name;
								 temp["id"] = data[row].id;
								 temp["member_id"] = data[row].member_id;
								 temp["favorite_flag"] = data[row].favorite_flag;

								 workspace.push(temp);

							 }

							 $p.parent().__workspace_data__.removeAll();
							 var distict = common.unique(workspace, 'id');
							 $p.parent().__workspace_data__.setJSON(distict, true);

							 // workspace 재조회 기능 추가...
							 scwin.getBuildProjectInfoReSelect();

						 }

					 }
					 , error: function (request, status, error) {
						 alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					 }
				 });
			 };

			 scwin.getBuildProjectInfoReSelect = function () {

				 $.ajax({
					 url: "/api/buildprojects",
					 type: "get",
					 accept: "application/json",
					 contentType: "application/json; charset=utf-8",
					 success: function (e) {
						 var data = e;

						 if (data !== null) {

							 var buildproj = [];

							 for (var row in data) {
								 var temp = {};

								 temp["id"] = data[row].id;
								 temp["workspace_id"] = data[row].workspace_id;
								 temp["project_name"] = data[row].project_name;
								 temp["platform"] = data[row].platform;
								 // temp["target_server"] = data[row].target_server;
								 temp["description"] = data[row].description;
								 temp["status"] = data[row].status;
								 temp["template_version"] = data[row].template_version;
								 temp["project_dir_path"] = data[row].project_dir_path;
								 temp["created_date"] = data[row].created_date;
								 temp["updated_date"] = data[row].updated_date;
								 //console.log("temp : "+ temp);
								 buildproj.push(temp);
							 }

							 $p.parent().__buildproject_data__.removeAll();
							 var distict = common.unique(buildproj, 'id');
							 $p.parent().__buildproject_data__.setJSON(distict, true);

							 $p.parent().wfm_main.setSrc("/xml/workspace.xml");

						 }

					 }
					 , error: function (request, status, error) {
						 alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
					 }
				 });
			 };

			 scwin.setWorkspace = function (workspace_json, buildproj_json) {

				 // var whive_session = sessionStorage.getItem("__whybrid_session__");
				 // whive_session = JSON.parse(whive_session);

				 for (var idx in workspace_json) {

					 if (workspace_json[idx].delete_yn == "1") {

					 } else {

						 var buildbtnInx = generator_workspace_list.insertChild();
						 var workspace_select = generator_workspace_list.getChild(idx, "txt_workspace_lbl");
						 workspace_select.setValue(workspace_json[idx].workspace_name);
						 //workspace_select.addItem('false', workspace_json[idx].workspace_name, 0);

						 var buildprojectadd = generator_workspace_list.getChild(buildbtnInx, 'btn_create_project');
						 buildprojectadd.setUserData('member_id', workspace_json[idx].member_id);
						 buildprojectadd.setUserData('workspace_name', workspace_json[idx].workspace_name);
						 buildprojectadd.setUserData('workspace_group_role_id', workspace_json[idx].workspace_group_role_id); // workspace_group_role_id 추가하기
						 buildprojectadd.setUserData('projectsampleYn',"N");
						 buildprojectadd.bind("onclick", function () {
							 scwin.btn_create_project_onclick(this);
						 });

                         var buildprojectimportAdd = generator_workspace_list.getChild(buildbtnInx, "btn_import_project");
						 buildprojectimportAdd.setUserData('member_id', workspace_json[idx].member_id);
						 buildprojectimportAdd.setUserData('workspace_name', workspace_json[idx].workspace_name);
						 buildprojectimportAdd.setUserData('workspace_group_role_id', workspace_json[idx].workspace_group_role_id); // workspace_group_role_id 추가하기
						 buildprojectimportAdd.setUserData('projectsampleYn',"N");
						 buildprojectimportAdd.bind("onclick", function () {
							 scwin.btn_import_project_onclick(this);
						 });

                         // var sampleBuildProjectAdd = generator_workspace_list.getChild(buildbtnInx, 'btn_sample_create_project');
						 // sampleBuildProjectAdd.setUserData('member_id', workspace_json[idx].member_id);
						 // sampleBuildProjectAdd.setUserData('workspace_name', workspace_json[idx].workspace_name);
						 // sampleBuildProjectAdd.setUserData('workspace_group_role_id', workspace_json[idx].workspace_group_role_id); // workspace_group_role_id 추가하기
						 // sampleBuildProjectAdd.setUserData('projectsampleYn',"Y");
						 // sampleBuildProjectAdd.bind("onclick", function () {
							//  scwin.btn_create_project_onclick(this);
						 // });

						 var temp_generator = generator_workspace_list.getChild(idx, "list_buildproj_generator");

						 var count = 0;
						 var android_cnt = 0;
						 var windows_cnt = 0;
						 var ios_cnt = 0;

						 for (var idx2 in buildproj_json) {
							 if (buildproj_json[idx2].workspace_id == workspace_json[idx].id) {

							// 	 if (whive_session.user_role == "SUPERADMIN") {
							// 		 temp_generator.insertChild();
                            //
							// 		 //set icon
							// 		 var span_icon = temp_generator.getChild(count, "buildproj_platform");
							// 		 var txt_proj_name = temp_generator.getChild(count, "txt_buildproj_project_name");
							// 		 var btn_build = temp_generator.getChild(count, "btn_build");
							// 		 // var btn_history = temp_generator.getChild(count, "btn_history");
							// 		 var btn_delete = temp_generator.getChild(count, "btn_delete");
							// 		 var btn_setting = temp_generator.getChild(count, "btn_setting");
							// 		 //var btn_deploy = temp_generator.getChild(count, "btn_deploy");
							// 		 var grp_buildproj_btn = temp_generator.getChild(count, "grp_list_buildproj");
							// 		 // var btn_export = temp_generator.getChild(count, "btn_export");
                            //
                            //
							// 		 btn_build.setUserData("platform", buildproj_json[idx2].platform);
							// 		 btn_build.setUserData("projectName", buildproj_json[idx2].project_name);
							// 		 btn_build.setUserData("project_pkid", buildproj_json[idx2].project_id);
							// 		 btn_build.setUserData("workspace_pkid", buildproj_json[idx2].workspace_id);
							// 		 // btn_build.setUserData("project_dir_path",buildproj_json[idx2].id); // path... project_dir_path
                            //
							// //btn_deploy.setUserData("platform",buildproj_json[idx2].platform);
							// //btn_deploy.setUserData("projectName",buildproj_json[idx2].project_name);
							// //btn_deploy.setUserData("project_pkid",buildproj_json[idx2].project_id);
							// //btn_deploy.setUserData("workspace_pkid",buildproj_json[idx2].workspace_id);
                            //
							// //btn_history.setUserData("platform",buildproj_json[idx2].platform);
							// //btn_history.setUserData("projectName",buildproj_json[idx2].project_name);
							// //btn_history.setUserData("project_pkid",buildproj_json[idx2].project_id);
                            //
							// btn_delete.setUserData("projectName",buildproj_json[idx2].project_name);
							// btn_delete.setUserData("project_pkid",buildproj_json[idx2].project_id);
                            //
							// 		 grp_buildproj_btn.setUserData("platform", buildproj_json[idx2].platform);
							// 		 grp_buildproj_btn.setUserData("projectName", buildproj_json[idx2].project_name);
							// 		 grp_buildproj_btn.setUserData("project_pkid", buildproj_json[idx2].project_id);
							// 		 grp_buildproj_btn.setUserData("workspace_pkid", buildproj_json[idx2].workspace_id);
							// 		 // grp_buildproj_btn.setUserData("target_server_id",buildproj_json[idx2].target_server);
							// 		 grp_buildproj_btn.setUserData("workspace_name", workspace_json[idx].workspace_name);
                            //
							// 		 btn_setting.setUserData("platform", buildproj_json[idx2].platform);
							// 		 btn_setting.setUserData("projectName", buildproj_json[idx2].project_name);
							// 		 btn_setting.setUserData("project_pkid", buildproj_json[idx2].project_id);
							// 		 btn_setting.setUserData("workspace_pkid", buildproj_json[idx2].workspace_id);
							// 		 // btn_setting.setUserData("target_server_id",buildproj_json[idx2].target_server);
							// 		 btn_setting.setUserData("workspace_name", workspace_json[idx].workspace_name);
                            //
							// 		 count++;
                            //
							// 		 if (buildproj_json[idx2].platform == "Android") {
							// 			 span_icon.addClass("ico_and");
							// 			 span_icon.setValue("Android");
							// 			 android_cnt++;
							// 		 } else if (buildproj_json[idx2].platform == "Windows") {
							// 			 // span_icon.addClass("ico_win");
							// 			 // span_icon.setValue("Windows");
							// 			 // windows_cnt++;
							// 		 } else {
							// 			 span_icon.addClass("ico_ios");
							// 			 span_icon.setValue("iOS");
							// 			 ios_cnt++;
							// 		 }
							// 		 txt_proj_name.setValue(buildproj_json[idx2].project_name);
                            //
							// 		 $(".btn_role_cm").click(function () {
							// 			 $(".layer_pop").css("display", "block");
							// 			 $("body").append("<div class='dim'></div>");
                            //
                            //
							// 		 });
							// 		 $(".btn_pop_close").click(function () {
							// 			 $(".layer_pop").css("display", "none");
							// 			 $("div").remove(".dim");
							// 		 });
                            //
							// 	 } else
                                 if (buildproj_json[idx2].read_yn == "1") {
									 temp_generator.insertChild();

									 //set icon
									 var span_icon = temp_generator.getChild(count, "buildproj_platform");
									 var txt_proj_name = temp_generator.getChild(count, "txt_buildproj_project_name");
									 var btn_build = temp_generator.getChild(count, "btn_build");
									 //var btn_history = temp_generator.getChild(count, "btn_history");
									 var btn_delete = temp_generator.getChild(count, "btn_delete");
									 var btn_setting = temp_generator.getChild(count, "btn_setting");
									 var btn_deploy = temp_generator.getChild(count, "btn_deploy"); // TODO btn_deploy 체크
									 var grp_buildproj_btn = temp_generator.getChild(count, "grp_list_buildproj");
									 var btn_export = temp_generator.getChild(count, "btn_export");

									 btn_export.setUserData("platform", buildproj_json[idx2].platform);
									 btn_export.setUserData("projectName", buildproj_json[idx2].project_name);
									 btn_export.setUserData("build_id", buildproj_json[idx2].project_id);
									 btn_export.setUserData("project_dir_path", buildproj_json[idx2].project_dir_path);
									 btn_export.setUserData("workspace_name", workspace_json[idx].workspace_name);


									 btn_build.setUserData("platform", buildproj_json[idx2].platform);
									 btn_build.setUserData("projectName", buildproj_json[idx2].project_name);
									 // btn_build.setUserData("target_server_id",buildproj_json[idx2].target_server);
									 btn_build.setUserData("project_pkid", buildproj_json[idx2].project_id);
									 btn_build.setUserData("workspace_pkid", buildproj_json[idx2].workspace_id);
                                     btn_build.setUserData("product_type", buildproj_json[idx2].product_type);
									 // btn_build.setUserData("project_dir_path",buildproj_json[idx2].id); // path... project_dir_path

									btn_deploy.setUserData("platform",buildproj_json[idx2].platform);
									btn_deploy.setUserData("projectName",buildproj_json[idx2].project_name);
									btn_deploy.setUserData("project_pkid",buildproj_json[idx2].project_id);
									btn_deploy.setUserData("workspace_pkid",buildproj_json[idx2].workspace_id);

							//btn_history.setUserData("platform",buildproj_json[idx2].platform);
							//btn_history.setUserData("projectName",buildproj_json[idx2].project_name);
							// btn_history.setUserData("project_pkid",buildproj_json[idx2].project_id);

									 btn_delete.setUserData("projectName",buildproj_json[idx2].project_name);
									 btn_delete.setUserData("project_pkid",buildproj_json[idx2].project_id);

									 grp_buildproj_btn.setUserData("platform", buildproj_json[idx2].platform);
									 grp_buildproj_btn.setUserData("projectName", buildproj_json[idx2].project_name);
									 grp_buildproj_btn.setUserData("project_pkid", buildproj_json[idx2].project_id);
									 grp_buildproj_btn.setUserData("workspace_pkid", buildproj_json[idx2].workspace_id);
									 // grp_buildproj_btn.setUserData("target_server_id",buildproj_json[idx2].target_server);
									 grp_buildproj_btn.setUserData("workspace_name", workspace_json[idx].workspace_name);

									 btn_setting.setUserData("platform", buildproj_json[idx2].platform);
									 btn_setting.setUserData("projectName", buildproj_json[idx2].project_name);
									 btn_setting.setUserData("project_pkid", buildproj_json[idx2].project_id);
									 btn_setting.setUserData("workspace_pkid", buildproj_json[idx2].workspace_id);
									 // btn_setting.setUserData("target_server_id",buildproj_json[idx2].target_server);
									 btn_setting.setUserData("workspace_name", workspace_json[idx].workspace_name);


									 // project group 조건에 따라서 show hide 처리 하기 ...

									 if (buildproj_json[idx2].read_yn == "1") {
										 //console.log("read_yn "+buildproj_json[idx2].read_yn);

									 } else {
										 btn_setting.hide();
									 }

									 if (buildproj_json[idx2].update_yn == "1") {
										 //console.log("update_yn "+buildproj_json[idx2].update_yn);
										 // 수정 기능 활성화 되어있으면
										 // localstorage.setItem 값 설정 하고
										 // 이후 project setting 화면에서 제어하기
										 btn_setting.setUserData("update_yn", buildproj_json[idx2].update_yn);
										 grp_buildproj_btn.setUserData("update_yn", buildproj_json[idx2].update_yn);
									 } else {

									 }

									 if (buildproj_json[idx2].delete_yn == "1") {
										 // console.log("delete_yn " + buildproj_json[idx2].delete_yn);

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

									 if (buildproj_json[idx2].build_yn == "1") {
										 // console.log("build_yn " + buildproj_json[idx2].build_yn);
										 // btn_build.show();
										 // btn_history.show();
									 } else {
										 btn_build.hide();
										 btn_history.hide();
									 }

									 if (buildproj_json[idx2].deploy_yn == "1") {
										 // console.log("deploy_yn " + buildproj_json[idx2].deploy_yn);
									 } else {
										 btn_deploy.hide();
									 }

									 if (buildproj_json[idx2].export_yn == "1") {
										 // console.log("export_yn " + buildproj_json[idx2].export_yn);
										 // btn_export.show();
									 } else {
										 btn_export.hide();
									 }


									 count++;

									 if (buildproj_json[idx2].platform == "Android") {
										 var android = common.getLabel("lbl_android");
										 span_icon.addClass("ico_and");
										 span_icon.setValue(android);
										 android_cnt++;
									 } else if (buildproj_json[idx2].platform == "Windows") {
										 // span_icon.addClass("ico_win");
										 // span_icon.setValue("Windows");
										 // windows_cnt++;
									 } else {
										 var ios = common.getLabel("lbl_ios");
										 span_icon.addClass("ico_ios");
										 span_icon.setValue(ios);
										 ios_cnt++;
									 }
									 txt_proj_name.setValue(buildproj_json[idx2].project_name);
								 } else {
									 // btn_setting.hide();
								 }
							 }
						 }

						 var span_android = generator_workspace_list.getChild(idx, "count_android");
						 var androidCnt = common.getLabel("lbl_android_s");
						 androidCnt = common.getFormatStr(androidCnt,android_cnt);
						 var iosCnt = common.getLabel("lbl_ios_s");
						 iosCnt = common.getFormatStr(iosCnt,ios_cnt);

						 span_android.setValue(androidCnt);
						 // var span_windows = generator_workspace_list.getChild(idx,"count_windows");
						 // span_windows.setValue("Windows<span>"+windows_cnt+"</span>");
						 var span_ios = generator_workspace_list.getChild(idx, "count_ios");
						 span_ios.setValue(iosCnt);

					 }
				 }  // workspcae end
			 };

			 // websocket handler 받는 구간
			 scwin.webSocketCallback = function (obj) {

				 // console.log("workspace.xml webSocketCallback()");
				 switch (obj.MsgType) {

					 case "HV_MSG_PROJECT_EXPORT_STATUS_INFO_FROM_HEADQUATER":
						 var msg = common.getLabel("lbl_workspace_project_export");
						 WebSquare.layer.showProcessMessage(msg);
						 break;

					 case "MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO_HEADQUATER" :
						 WebSquare.layer.hideProcessMessage();
						 scwin.setProjectExportDownload(obj);
						 break;
					 default :
						 break;
				 }
			 };

			 scwin.setProjectExportDownload = function (msg) {

				 // zip file 다운로드
				 window.location = "/manager/build/project/export/download/" + msg.filename;
			 };


			 scwin.btn_import_project_onclick = function (e) {

                 // buildprojectimportAdd.setUserData('member_id', workspace_json[idx].member_id);
				 // buildprojectimportAdd.setUserData('workspace_name', workspace_json[idx].workspace_name);
				 // buildprojectimportAdd.setUserData('workspace_group_role_id', workspace_json[idx].workspace_group_role_id); // workspace_group_role_id 추가하기
				 // buildprojectimportAdd.setUserData('projectsampleYn',"N");

				 var setmember_id = this.getUserData("member_id");
				 var setworkspace_name = this.getUserData("workspace_name");
				 var setsampleProjectYn = this.getUserData("projectsampleYn");

				 localStorage.setItem("__member_id__", setmember_id);
				 localStorage.setItem("__workspace_name__", setworkspace_name);
				 localStorage.setItem("__sample_project_yn__",setsampleProjectYn);

				 scwin.select_workspace_ajax_to(setmember_id, setworkspace_name);
				 // scwin.select_workspace(setmember_id, setworkspace_name);
			 };

			 scwin.btn_create_project_onclick = function (e) {
				 var setmember_id = this.getUserData("member_id");
				 var setworkspace_name = this.getUserData("workspace_name");
				 var setworkspace_group_role_id = this.getUserData("workspace_group_role_id");
				 var setsampleProjectYn = this.getUserData("projectsampleYn");
				 // localStorage.setItem("__member_id__", setmember_id);
				 localStorage.setItem("__workspace_name__", setworkspace_name);
				 localStorage.setItem("__workspace_group_role_id__", setworkspace_group_role_id);
                 localStorage.setItem("__sample_project_yn__",setsampleProjectYn);

				 // workspace key 값 조회
				 scwin.select_workspace(setmember_id, setworkspace_name);

			 };

			 scwin.select_workspace_ajax_to = function (setmember_id, setworkspace_name) {

				 var member_id = setmember_id;
				 var workspace_name = setworkspace_name;

				 var data = {};
				 data.member_id = parseInt(member_id);
				 data.workspace_name = workspace_name;
				 var options = {};

				 options.action = "/manager/workspace/search/workspaceId/" + data.member_id + "/" + data.workspace_name;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 //console.log(" select ajax workspace test ");
						 //console.log(data);

						 // localStorage.setItem("__workspace_name__",data.name);
						 //console.log("member_id to import project : " + member_id);
						 scwin.import_build_project();
					 } else {
                         var message = common.getLabel("lbl_workspace_moveFail");
						 alert(message);
					 }
				 };

				 options.error = function (e) {

					 // alert("session이 만료되었습니다. 로그인페이지로 이동합니다.");
					 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);
					 //$p.url("/login.xml");

				 };

				 $p.ajax(options);

			 };


			 scwin.select_workspace = function (setmember_id, setworkspace_name) {

				 var member_id = setmember_id;
				 var workspace_name = setworkspace_name;

				 var data = {};
				 data.member_id = parseInt(member_id);
				 data.workspace_name = workspace_name;

				 var options = {};
				 options.action = "/manager/workspace/search/workspaceId/" + data.member_id + "/" + data.workspace_name;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (data != null) {
						 localStorage.setItem("__workspace_id__", data.workspace_id);

						 scwin.create_build_project();
					 } else {
                         var message = common.getLabel("lbl_workspace_projectMoveFail");
						 alert(message);
					 }
				 };

				 options.error = function (e) {

					 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);


				 };

				 $p.ajax(options);

			 };

			 scwin.select_check_workspace_name = function (workspace_name) {

				 var data = {};
				 data.workspace_name = workspace_name;
				 var options = {};

				 options.action = "/api/workspace/namecheck/" + workspace_name;
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.method = "GET";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (e.responseStatusCode === 200 || e.responseStatusCode === 201) {
						 if (data != null) {
                             var message = common.getLabel("lbl_exist_name");
							 alert(message);
						 }
					 }

				 };

				 options.error = function (e) {
					 if (e.responseStatusCode === 500) {
						 var message = common.getLabel("lbl_can_use_name");
						 alert(message);
						 scwin.workspace_name_check_yn = true;
					 } else {
						 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);
					 }

				 };

				 $p.ajax(options);
			 };

			 scwin.grp_list_link_onclick = function (e) {
				 var controlList = this.parentControl.parentControl.childControlList;
				 for (var idx in controlList) {
					 if (controlList[idx] === this.parentControl) {
						 this.parentControl.toggleClass("on");
					 } else {
						 controlList[idx].removeClass("on");
					 }
				 }
			 };

			 scwin.grp_list_buildproj_onclick = function (e) {
				 var project_name = this.getUserData("projectName");
				 var project_pkid = this.getUserData("project_pkid");
				 var workspace_name = this.getUserData("workspace_name");
				 var platform = this.getUserData("platform");
				 var workspace_pkid = this.getUserData("workspace_pkid");
				 var update_yn = this.getUserData("update_yn");
				 // var target_server_id = this.getUserData("target_server_id");


				 // build localStorage data set
				 localStorage.setItem("buildPlatform", platform);
				 localStorage.setItem("buildProjectId", project_pkid);
				 localStorage.setItem("workspaceId", workspace_pkid);
				 // localStorage.setItem("targetServerId",target_server_id);

				 localStorage.setItem("buildProjectName", project_name);
				 localStorage.setItem("buildProjecPkId", project_pkid);
				 localStorage.setItem("__workspace_name__", workspace_name);
				 localStorage.setItem("__update_yn__", update_yn);

				 $p.parent().wfm_main.setSrc("/xml/project_setting.xml");

			 };

			 scwin.create_deploy_project = function () {
				 $p.parent().wfm_main.setSrc("/xml/project_add.xml");
			 };

			 scwin.import_deploy_project = function () {
				 $p.parent().wfm_main.setSrc("/xml/project_import.xml");
			 };

			 scwin.create_build_project = function () {
				 $p.parent().wfm_main.setSrc("/xml/project_add.xml");
			 };

			 scwin.import_build_project = function () {
				 $p.parent().wfm_main.setSrc("/xml/project_import.xml");
			 };

	scwin.btn_deploy_onclick = function(e){

		var platform = this.getUserData("platform");
		var deployProjectName = this.getUserData("projectName");
		var deploy_project_id = this.getUserData("project_pkid");
		var workspace_id = this.getUserData("workspace_pkid");
		// deploy id ...

	    var deployTask = [];

		var data = {};
		data.platform = platform;
		data.projectName = deployProjectName;
		data.project_pkid = deploy_project_id;
		data.workspace_pkid = workspace_id;

		deployTask.push(data);

		$p.parent().$p.parent().__deploytask_data__.removeAll();
		$p.parent().__deploytask_data__.setJSON(deployTask);

		$p.parent().wfm_main.setSrc("/xml/project_deploy_task_setting.xml");
	};

			 scwin.btn_export_onclick = function (e) {

				 var project_name = this.getUserData("projectName");
				 var build_id = this.getUserData("build_id");
				 var workspace_name = this.getUserData("workspace_name");
				 var project_dir_path = this.getUserData("project_dir_path");

				 // var whive_session = sessionStorage.getItem("__whybrid_session__");
				 // whive_session = JSON.parse(whive_session);

				 var data = {};
				 data.build_id = build_id;
				 // data.domainID = whive_session.domain_id;
				 // data.userID = whive_session.id;
				 data.workspace_name = workspace_name;
				 data.project_name = project_name;
				 data.project_dir_name = project_dir_path;
				 // data.hqKey = whive_session.user_login_id; // hqKey 추가

				 var options = {};

				 options.action = "/manager/build/project/export/start";
				 options.mode = "asynchronous";
				 options.mediatype = "application/json";
				 options.requestData = JSON.stringify(data);
				 options.method = "POST";

				 options.success = function (e) {
					 var data = e.responseJSON;
					 if (e.responseStatusCode === 200 || e.responseStatusCode === 201) {
						 if (data != null) {

						 }
					 }

				 };

				 options.error = function (e) {
					 if (e.responseStatusCode === 500) {

					 } else {
						 alert("code:" + e.responseStatusCode + "\n" + "message:" + e.responseText + "\n" + "error:" + e.requestBody);
					 }

				 };

				 $p.ajax(options);


			 };

			 scwin.btn_build_onclick = function (e) {
				 var platform = this.getUserData("platform");
				 var buildProjectName = this.getUserData("projectName");
				 var build_project_id = this.getUserData("project_pkid");
				 var workspace_id = this.getUserData("workspace_pkid");
                 var product_type = this.getUserData("product_type");
				 /// var target_server_id = this.getUserData("target_server_id");

				 var buildAction = [];

				 var data = {};
				 data.platform = platform;
				 data.projectName = buildProjectName;
				 data.project_pkid = build_project_id;
				 data.workspace_pkid = workspace_id;
                 data.product_type = product_type;

				 buildAction.push(data);

				 $p.parent().__buildaction_data__.setJSON(buildAction);

				 $p.parent().wfm_main.setSrc("/xml/build.xml");
				 WebSquare.getBody().hideToolTip();
			 };

			 scwin.btn_setting_onclick = function (e) {
				 // build setting 페이지로 이동 화면 구현.

				 var project_name = this.getUserData("projectName");
				 var project_pkid = this.getUserData("project_pkid");
				 var workspace_name = this.getUserData("workspace_name");
				 var platform = this.getUserData("platform");
				 var workspace_pkid = this.getUserData("workspace_pkid");
				 var update_yn = this.getUserData("update_yn");
				 // var target_server_id = this.getUserData("target_server_id");

				 // build localStorage data set
				 localStorage.setItem("buildPlatform", platform);
				 localStorage.setItem("buildProjectId", project_pkid);
				 localStorage.setItem("workspaceId", workspace_pkid);
				 // localStorage.setItem("targetServerId",target_server_id);

				 localStorage.setItem("buildProjectName", project_name);
				 localStorage.setItem("buildProjecPkId", project_pkid);
				 localStorage.setItem("__workspace_name__", workspace_name);
				 localStorage.setItem("__update_yn__", update_yn);
				 // project setting update yn 처리 기능 추가 ..

				 // localStorage.setItem("build_project_pk_id",platform);
				 $p.parent().wfm_main.setSrc("/xml/project_setting.xml");
				 WebSquare.getBody().hideToolTip();
			 };

			 scwin.btn_history_onclick = function (e) {
				 var platform = this.getUserData("platform");
				 var buildProjectName = this.getUserData("projectName");
				 var build_project_id = this.getUserData("project_pkid");

				 localStorage.setItem("historyPlatform", platform);
				 localStorage.setItem("historyProjectName", buildProjectName);
				 localStorage.setItem("historyProjectId", build_project_id);
				 $p.parent().wfm_main.setSrc("/xml/history_detail.xml");
			 };

			 scwin.btn_delete_onclick = function (e) {
				var message = common.getLabel("lbl_workspace_removeConfirm");
				 if (confirm(message)) {
					 var build_project_id = this.getUserData("project_pkid");
					 var project_name = this.getUserData("projectName");

					 var label = common.getLabel("lbl_workspace_android_count_str");
					 label = common.getFormatStr(label,project_name);

                     lbl_project_delete_name_check.setLabel(label);
					 btn_delete_project_data.setUserData("project_id", build_project_id);
					 btn_delete_project_data.setUserData("projectName", project_name);

					 $(".layer_pop").css("display", "block");
					 $("body").append("<div class='dim'></div>");

				 }


			 };

			 scwin.btn_delete_project_name_onclick = function(e){

				 var project_id = this.getUserData("project_id");
				 var del_project_name = this.getUserData("projectName");
				 var project_name = txt_project_delete_name.getValue();

                 var project_name_txt = txt_project_delete_name.getValue();

                 if(project_name_txt == "" || project_name_txt == null){
                     alert("프로젝트 이름을 입력하세요.");
                     return false;
				 }

                 if(project_name_txt != project_name){
					 alert(project_name + " 해당 프로젝트 이름하고 동일하게 입력하세요.");
					 return false;
				 }

				 scwin.delete_build_project_pid(project_id, project_name);

			 };

			 scwin.btn_cancel_project_name_onclick = function (e) {

				 $(".layer_pop").css("display", "none");
				 $("div").remove(".dim");

			 };

			 
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'white_board'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'work'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'pgtbox'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'',class:'pgt_tit fl',useLocale:'true',localeRef:'lbl_workspace'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'fr'}}]},{T:1,N:'xf:group',A:{class:'fav_list'},E:[{T:1,N:'w2:generator',A:{tagname:'ul',style:'',id:'generator_workspace_list',class:''},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:''},E:[{T:1,N:'xf:group',A:{tagname:'',style:'',id:'grp_list_link',class:'fav_link','ev:onclick':'scwin.grp_list_link_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'left'},E:[{T:1,N:'w2:textbox',A:{tagname:'span',style:'',id:'txt_workspace_lbl',label:'',class:'txt_date'}},{T:1,N:'xf:group',A:{style:'',id:'grp_btn_list_link',class:'fav_cont'},E:[{T:1,N:'xf:group',A:{style:'',id:'grp_btn_build_list_link',class:'grp_build'},E:[{T:1,N:'xf:trigger',A:{useLocale:'true',localeRef:'lbl_create',type:'button',style:'',id:'btn_create_project',class:'btn_cm','ev:onclick':'scwin.btn_create_project_onclick'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{id:'btn_import_project',useLocale:'true',localeRef:'lbl_import',style:'',type:'button',class:'btn_cm','ev:onclick':'scwin.btn_import_project_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'right'},E:[{T:1,N:'w2:anchor',A:{useLocale:'true',localeRef:'lbl_android_count',outerDiv:'false',style:'',id:'count_android',class:'btn_android'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{useLocale:'true',localeRef:'lbl_ios_count',outerDiv:'false',style:'',id:'count_ios',class:'btn_ios'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'w2:generator',A:{style:'',id:'list_buildproj_generator',class:'fav_cont'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'item'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'item_gp'},E:[{T:1,N:'xf:group',A:{style:'',id:'grp_list_buildproj',class:'dev_tit','ev:onclick':'scwin.btn_setting_onclick'},E:[{T:1,N:'w2:span',A:{style:'',id:'buildproj_platform',label:'',class:''}},{T:1,N:'w2:textbox',A:{style:'cursor:pointer;',id:'txt_buildproj_project_name',label:'',class:'txt_name'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'dev_link'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'grp_link'},E:[{T:1,N:'w2:anchor',A:{useLocale:'true',localeRef:'lbl_build',outerDiv:'false',style:'',id:'btn_build',class:'btn_i_play','ev:onclick':'scwin.btn_build_onclick',toolTip:'tooltip',tooltipLocaleRef:'lbl_build',toolTipDisplay:'true'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{useLocale:'true',localeRef:'lbl_setting',outerDiv:'false',style:'',id:'btn_setting',class:'btn_i_option','ev:onclick':'scwin.btn_setting_onclick',toolTip:'tooltip',tooltipLocaleRef:'lbl_workspace_move',toolTipDisplay:'true'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{useLocale:'true',localeRef:'lbl_deploy',outerDiv:'false',style:'',id:'btn_deploy',class:'btn_i_down','ev:onclick':'scwin.btn_deploy_onclick',toolTip:'tooltip',tooltipLocaleRef:'lbl_project_deploy_task',toolTipDisplay:'true'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{localeRef:'lbl_del',useLocale:'true',outerDiv:'false',style:'',id:'btn_delete',class:'btn_i_reset','ev:onclick':'scwin.btn_delete_onclick',toolTip:'tooltip',tooltipLocaleRef:'lbl_del',toolTipDisplay:'true'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{localeRef:'lbl_export',useLocale:'true',outerDiv:'false',style:'',id:'btn_export',class:'btn_i_log','ev:onclick':'scwin.btn_export_onclick',toolTip:'tooltip',tooltipLocaleRef:'lbl_export',toolTipDisplay:'true'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'export'}]}]}]}]}]}]}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_workspace_projectDelete'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'form_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'lbl_project_delete_name_check',label:'',style:'',useLocale:'true',localeRef:'lbl_workspace_settingName'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'txt_project_delete_name',style:''}}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox tac',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm',id:'btn_delete_project_data',style:'',type:'button','ev:onclick':'scwin.btn_delete_project_name_onclick',useLocale:'true',localeRef:'lbl_delete'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm type1',id:'',style:'',type:'button','ev:onclick':'scwin.btn_cancel_project_name_onclick',useLocale:'true',localeRef:'lbl_cancel'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]})