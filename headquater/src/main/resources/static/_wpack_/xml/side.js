/*amd /xml/side.xml 15577 3f049d596cdf2851e3154952263b5c283d64fe66eb4300d018df684305cca404 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dlt_side_user_List',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'text_label',name:'name1',dataType:'text'}},{T:1,N:'w2:column',A:{id:'menu_key',name:'name2',dataType:'text'}},{T:1,N:'w2:column',A:{id:'depth',name:'name3',dataType:'text'}},{T:1,N:'w2:column',A:{id:'icon',name:'name4',dataType:'text'}}]},{T:1,N:'w2:data',A:{use:'true'}}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/common.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			var g_user_role = "";
			scwin.onpageload = function() {
				common.setScopeObj(scwin);

				var options = {};

				options.action = "/manager/member/search/userInfo";
				options.mode = "synchronous";
				options.mediatype = "application/json";
				options.method = "GET";
				options.success = function (e) {

					var data = e.responseJSON;
					if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
						if (data != null) {

							var loginData = {};

							// loginData.id = data.user_id;
							// loginData.email = data.email;
							loginData.build_yn = data.build_yn;
							loginData.created_date = data.created_date;
							loginData.last_login_date = data.last_login_date;
							loginData.user_name = data.user_name;
							loginData.user_role = data.user_role;
							loginData.updated_date = data.updated_date;
							loginData.domain_id = data.domain_id;
							loginData.domain_name = data.domain_name;
							loginData.role_id = data.role_id;
							loginData.user_login_id = data.user_login_id;
							g_user_role = data.user_role;
							scwin.tw_menu_nodeListSettingfunc(g_user_role);

							return true;

						} else {
							// $p.url("/login.xml");
						}
					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");
				};

				$p.ajax( options );

				var treeview_group = $(".w2treeview_group");

				// console.log(treeview_group[i]);

				// wq_uuid_33_tw_menu_id_group_1
				// console.log(data);

				$(".w2treeview_row_depth1 > td .w2treeview_icon_label").click(function(){
					// 버튼 비활성화
					$(".tw_menu").find("*").removeClass("on");

					// 트리뷰 접기
					$(".w2treeview_child").hide();
					$(".w2treeview_table_node").removeClass("w2treeview_open_child").addClass("w2treeview_close_child").attr("opened", "false");
					$(".w2treeview_row_depth1").removeClass("on");

					// 메뉴 활성화
					$(this).parents(".w2treeview_row_depth1").toggleClass("on");

				});

				$(".w2treeview_row_depth1 > td .w2treeview_label").click(function(){
					// 버튼 비활성화
					$(".tw_menu").find("*").removeClass("on");

					// 트리뷰 접기
					$(".w2treeview_child").hide();
					$(".w2treeview_table_node").removeClass("w2treeview_open_child").addClass("w2treeview_close_child").attr("opened", "false");
					$(".w2treeview_row_depth1").removeClass("on");

					// 메뉴 활성화
					$(this).parents(".w2treeview_row_depth1").toggleClass("on");

				});
			};

			scwin.onpageunload = function() {

			};

			scwin.tw_menu_nodeListSettingfunc = function(user_role){
				// menu 테이블에서, user_role (USER, ADMIN, SUPERADMIN)과 PROFILES에 따라서, 해당되는 메뉴를 가져온다.
				$.ajax({
					url: "/manager/menu/list",
					type:  "POST",
					data: JSON.stringify({
						"menu_role_type": user_role,
						"menu_profile_type": g_config.PROFILES,
						"menu_lang_type": common.getLocale()
					}),
					contentType: "application/json; charset=utf-8",
					dataType: 'json',
					cache: false,
					success: function (r, status) {
						menuSetting(r);
					},
					error: function (e) {
						alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");
					}
				});

				// 가져온 메뉴를 화면에 표시한다.
				function menuSetting(menuData) {
					dlt_side_user_List.removeAll();
					dlt_side_user_List.setJSON(menuData, true);
				}
			};

			scwin.tw_menu_onlabelclick = function(e,node,menu){

				var menuValue = e;

				if (g_config.PROFILES == "service" && g_user_role == "ADMIN"){
					if(menu == "1"){ // workspace
						$p.parent().scwin.allRefrashSelect();
						$p.parent().wfm_main.setSrc("/xml/workspace.xml");

					}else if(menu == "2"){ // history



					}else if(menu == "3"){ //
						// $p.parent().wfm_main.setSrc("/xml/settings.xml");

						$p.parent().wfm_main.setSrc("/xml/history_list.xml");
					}else if(menu == "4"){ //
						// $p.parent().wfm_main.setSrc("/xml/settings.xml");

						$p.parent().wfm_main.setSrc("/xml/deploy_history_list.xml");
					}else if(menu == "5"){ //
						// $p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "6"){
						$p.parent().wfm_main.setUserData("settingsData","vcs");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "7"){
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "8"){


					}else if(menu == "9"){
						$p.parent().wfm_main.setUserData("roleViewData","workspaceRoleView");
						$p.parent().wfm_main.setSrc("/xml/userManager_workspace_role_detail.xml");

					} else if(menu == "10"){

						var winWid = $(window).width();
						var winHei = $(window).height();
						var popWid = 780;
						var popHei = 800;

						var popupX = (window.screen.width/2) - (popWid / 2);
						var popupY= (window.screen.height/2) - (popHei / 2);

						var url = "/menual.html";
						var name = "W-Hive 사용설명서";
						var option = "width = 780, height = 800, top = "+popupY+", left = "+popupX+", status = no";
						// var option = "width = 780, height = 800, top = "+popupX+", left = "+popupY+", screenX="+ popupX + ", screenY= "+ popupY +", status = no";
						window.open(url, name, option);
					}

				}else {
					if(menu == "1"){ // workspace
						$p.parent().scwin.allRefrashSelect();
						$p.parent().wfm_main.setSrc("/xml/workspace.xml");

					}else if(menu == "2"){ // history



					}else if(menu == "3"){ //
						// $p.parent().wfm_main.setSrc("/xml/settings.xml");

						$p.parent().wfm_main.setSrc("/xml/history_list.xml");
					}else if(menu == "4"){ //
						// $p.parent().wfm_main.setSrc("/xml/settings.xml");

						$p.parent().wfm_main.setSrc("/xml/deploy_history_list.xml");
					}else if(menu == "5"){ //
						// $p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "6"){
						$p.parent().wfm_main.setUserData("settingsData","builder");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "7"){
						$p.parent().wfm_main.setUserData("settingsData","vcs");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "8"){
						$p.parent().wfm_main.setUserData("settingsData","FTP");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "9"){
						$p.parent().wfm_main.setUserData("settingsData","signingkey");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					}else if(menu == "10"){
						// $p.parent().wfm_main.setSrc("/xml/userManager.xml");

					}else if(menu == "11"){
						$p.parent().wfm_main.setUserData("roleViewData","domain");
						$p.parent().wfm_main.setSrc("/xml/userManager.xml");

					}else if(menu == "12"){
						$p.parent().wfm_main.setUserData("roleViewData","workspaceRoleView");
						$p.parent().wfm_main.setSrc("/xml/userManager_workspace_role_detail.xml");

					}else if(menu == "13"){
						$p.parent().wfm_main.setUserData("roleViewData","roleView");
						$p.parent().wfm_main.setSrc("/xml/userManager.xml");

					}else if(menu == "14"){
						$p.parent().wfm_main.setUserData("roleViewData","userListView");
						$p.parent().wfm_main.setSrc("/xml/userManager.xml");
					}else if(menu == "15"){
						var popWid = 780;
						var popHei = 800;

						var popupX = (window.screen.width/2) - (popWid / 2);
						var popupY= (window.screen.height/2) - (popHei / 2);

						var url = "/menual.html";
						var name = "W-Hive 사용설명서";
						var option = "width = 780, height = 800, top = "+popupY+", left = "+popupX+", status = no";
						// var option = "width = 780, height = 800, top = "+popupX+", left = "+popupY+", screenX="+ popupX + ", screenY= "+ popupY +", status = no";
						window.open(url, name, option);

					}

				}



			};

			scwin.webSocketCallback = function( obj ) {

				switch (obj.MsgType) {
					case "HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER" :
						// console.log("side.xml data ");
						// console.log(obj);
						// 빌드 화면에 나왔을시  flag 처리 경우의 수 추가
						var buildFlag = localStorage.getItem("_buildStatusFlag_");

						if (buildFlag == "build_view"){
							// scwin.screenScopeObj.webSocketCallback(obj);
							scwin.buildStatusFromHeadquarter(obj);
						}else if(buildFlag == "building"){

							// index.xml build status 수행
							// build status 처리 구간

							// obj.msgType : HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATE
							// 플랫폼 : android, ios, windows
							// 상태 처리 data.message : BUILDING, CLEANBUILING, FILEUPLOADING, SUCCESSFUL, FAILED
							// 빌드 완료 이후 :update 처리 setBranchBuildUpdate
							// scwin.screenScopeObj.webSocketCallback(obj);
							scwin.buildStatusFromHeadquarter(obj);
						} else if(obj.status == "web_build") {
							// index.xml build status 수행
							// build status 처리 구간

							// obj.msgType : HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATE
							// 플랫폼 : android, ios, windows
							// 상태 처리 data.message : BUILDING, CLEANBUILING, FILEUPLOADING, SUCCESSFUL, FAILED
							// 빌드 완료 이후 :update 처리 setBranchBuildUpdate
							scwin.buildStatusFromHeadquarter(obj);

						}else {
							//scwin.screenScopeObj.webSocketCallback(obj);
						}

						break;
					default :
						// console.log(obj);
						break;
				}

			};

			scwin.buildStatusFromHeadquarter = function (obj){

				// 빌드 화면에 나왔을시  flag 처리 경우의 수 추가
				var buildFlag = localStorage.getItem("_buildStatusFlag_");
				var buildActionData = $p.parent().__buildaction_data__.getAllJSON();

				var platform = buildActionData[0].platform;


				if(obj.msgType == "HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER"){
					// console.log(" buildFlagYn, HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER ");

					switch (obj.message) {
						case "BUILDING" :
							// project_build_status.setLabel("빌드중");
							// scwin.setBranchBuildLog(data);

							// txt_build_status_lbl, txt_build_platform_lbl
							if(platform == "Android"){

								build_status_android.changeClass("state  clean","state  build");
								build_status_android.setLabel("빌드중");

							}else if(platform == "iOS"){
								build_status_ios.changeClass("state  clean","state  build");
								build_status_ios.setLabel("빌드중");

							}else {
								// build_status_windows.changeClass("state  clean","state  build");
								// build_status_windows.setLabel("빌드중");

							}
							// class 설정 수정 및 리스트 정리

							break;
						case "FILEUPLOADING" :
							// project_build_status.setLabel("업로드중");
							// WebSquare.layer.showProcessMessage("FTP 업로드중...");
							if(platform == "Android"){
								build_status_android.changeClass("state  build","state  upload");
								build_status_android.setLabel("업로드");

							}else if(platform == "iOS"){
								build_status_ios.changeClass("state  build","state  upload");
								build_status_ios.setLabel("업로드");

							}else {
								// build_status_windows.changeClass("state  build","state  upload");
								// build_status_windows.setLabel("업로드");

							}

							break;
						case "SUCCESSFUL" :
							//project_build_status.setLabel("빌드완료");
							// WebSquare.layer.hideProcessMessage();
							if(platform == "Android"){
								build_status_android.setLabel("빌드완료");
								build_status_android.changeClass("state  upload","state  finish");

							}else if(platform == "iOS"){
								build_status_ios.setLabel("빌드완료");
								build_status_ios.changeClass("state  upload","state  finish");

							}else {
								// build_status_windows.setLabel("빌드완료");
								// build_status_windows.changeClass("state  upload","state  finish");

							}

							$p.parent().__buildaction_data__.removeAll();

							if (buildFlag == "building") {

							}else if(buildFlag == "build_back"){
								// scwin.setIndexBranchBuildUpdate(obj);
							}

							break;
						case "FAILED" :
							if(platform == "Android"){
								build_status_android.changeClass("state  build","state  finish");
								build_status_android.setLabel("빌드실패");

							}else if(platform == "iOS"){
								build_status_ios.changeClass("state  build","state  finish");
								build_status_ios.setLabel("빌드실패");

							}else {
								// build_status_windows.changeClass("state  build","state  ready");
								// build_status_windows.setLabel("빌드실패");

							}

							$p.parent().__buildaction_data__.removeAll();


							if (buildFlag == "building") {

							}else if(buildFlag == "build_back"){
								// scwin.setIndexBranchBuildUpdate(obj);
							}

							break;
						case "CLEANBUILING" :
							// project_build_status.setLabel("클린빌드");
							// scwin.setBranchBuildLog(data);
							// scwin.setBranchBuildUpdate(data);
							if(platform == "Android"){
								build_status_android.changeClass("state  ready","state  clean");
								build_status_android.setLabel("클린중");

							}else if(platform == "iOS"){
								build_status_ios.changeClass("state  ready","state  clean");
								build_status_ios.setLabel("클린중");

							}else {
								// build_status_windows.changeClass("state  ready","state  clean");
								// build_status_windows.setLabel("클린중");

							}

							break;
						default :
							break;
					}
				}

			};

			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'grp_tab',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'tab_con',id:'',style:''},E:[{T:1,N:'w2:treeview',A:{class:'tw_menu',dataType:'listed','ev:onlabelclick':'scwin.tw_menu_onlabelclick','ev:onimageclick':'scwin.tw_menu_onlabelclick',id:'tw_menu_id',lineShow:'false',renderType:'table',showTreeDepth:'0',style:'',toggleEvent:'onclick',tooltipGroupClass:'false'},E:[{T:1,N:'w2:itemset',A:{nodeset:'data:dlt_side_user_List'},E:[{T:1,N:'w2:label',A:{ref:'text_label'}},{T:1,N:'w2:value',A:{ref:'menu_key'}},{T:1,N:'w2:depth',A:{ref:'depth'}},{T:1,N:'w2:folder',A:{ref:''}},{T:1,N:'w2:checkbox',A:{ref:''}},{T:1,N:'w2:checkboxDisabled',A:{ref:''}},{T:1,N:'w2:image',A:{ref:''}},{T:1,N:'w2:iconImage',A:{ref:'icon'}},{T:1,N:'w2:selectedImage',A:{ref:''}},{T:1,N:'w2:expandedImage',A:{ref:''}},{T:1,N:'w2:leafImage',A:{ref:''}}]}]}]}]}]}]}]})