/*amd /cm/ui/works/project_deploy_task_setting.xml 8895 c0b19fdfdab1f5b351f835fe510236273dedd7c9c63b6c4e21fd362bf08676ad */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dtl_steps',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'INDEX',name:'INDEX',dataType:'text'}},{T:1,N:'w2:column',A:{id:'FRAMEID',name:'FRAMEID',dataType:'text'}},{T:1,N:'w2:column',A:{id:'FRAMESRC',name:'FRAMESRC',dataType:'text'}},{T:1,N:'w2:column',A:{id:'MENUID',name:'MENUID',dataType:'text'}}]},{T:1,N:'w2:data',A:{use:'true'},E:[{T:1,N:'w2:row',E:[{T:1,N:'INDEX',E:[{T:4,cdata:'1'}]},{T:1,N:'FRAMEID',E:[{T:4,cdata:'wfm_project_deploy_task_setting_step1'}]},{T:1,N:'FRAMESRC',E:[{T:4,cdata:'/ui/works/project_deploy_task_setting_step01.xml'}]},{T:1,N:'MENUID',E:[{T:4,cdata:'grp_step1'}]}]},{T:1,N:'w2:row',E:[{T:1,N:'INDEX',E:[{T:4,cdata:'2'}]},{T:1,N:'FRAMEID',E:[{T:4,cdata:'wfm_project_deploy_task_setting_step2'}]},{T:1,N:'FRAMESRC',E:[{T:4,cdata:'/ui/works/project_deploy_task_setting_step02.xml'}]},{T:1,N:'MENUID',E:[{T:4,cdata:'grp_step2'}]}]},{T:1,N:'w2:row',E:[{T:1,N:'INDEX',E:[{T:4,cdata:'3'}]},{T:1,N:'FRAMEID',E:[{T:4,cdata:'wfm_project_deploy_task_setting_step3'}]},{T:1,N:'FRAMESRC',E:[{T:4,cdata:'/ui/works/project_deploy_task_setting_step03.xml'}]},{T:1,N:'MENUID',E:[{T:4,cdata:'grp_step3'}]}]}]}]},{T:1,N:'w2:dataList',A:{id:'dtl_build_setting_step1',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'project_id',name:'project_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_name',name:'project_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'product_type',name:'product_type',dataType:'text'}},{T:1,N:'w2:column',A:{id:'platform',name:'platform',dataType:'text'}},{T:1,N:'w2:column',A:{id:'platform_language',name:'platform_language',dataType:'text'}},{T:1,N:'w2:column',A:{id:'target_server',name:'target_server',dataType:'text'}},{T:1,N:'w2:column',A:{id:'description',name:'description',dataType:'text'}},{T:1,N:'w2:column',A:{id:'status',name:'status',dataType:'text'}},{T:1,N:'w2:column',A:{id:'template_version',name:'template_version',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_dir_path',name:'project_dir_path',dataType:'text'}},{T:1,N:'w2:column',A:{id:'created_date',name:'created_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'updated_date',name:'updated_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'builder_id',name:'builder_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_id',name:'vcs_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'ftp_id',name:'ftp_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'key_id',name:'key_id',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.getResultDeployConfigData;
						scwin.txt_project_all_step_platform = "";
						scwin.onpageload = function() {

							let deployTaskData = $p.parent().__deploytask_data__.getAllJSON();

							let project_id = deployTaskData[0].project_pkid;


							scwin.select_build_project_id(project_id);
							// scwin.select_deploy_setting_id(project_id);
						};

						scwin.onpageunload = function() {

						};

						scwin.selected_step = function(selected){
							const jsonData = dtl_steps.getAllJSON();
							for(let obj in jsonData){
								const row = jsonData[obj];
								if(row.INDEX == selected){
									let temp = $p.getComponentById(row.FRAMEID);
									if(temp.getSrc() == ""){
										const obj = {
											"dataObject": {
												"type" : "json",
												"name" : "wframeParam",
												"data" : scwin.paramData
											}
										};
										temp.setSrc(row.FRAMESRC, obj);
									}
									$p.getComponentById(row.FRAMEID).show();
								} else {
									$p.getComponentById(row.FRAMEID).hide();
								}
							}
							if(selected == 1) {
								$p.getComponentById("deployTaskGrp1").addClass("on");
								$p.getComponentById("deployTaskGrp2").removeClass("on");
								$p.getComponentById("deployTaskGrp3").removeClass("on");
							} else if (selected == 2){
								$p.getComponentById("deployTaskGrp2").addClass("on");
								$p.getComponentById("deployTaskGrp1").removeClass("on");
								$p.getComponentById("deployTaskGrp3").removeClass("on");
							} else if (selected == 3){
								$p.getComponentById("deployTaskGrp3").addClass("on");
								$p.getComponentById("deployTaskGrp2").removeClass("on");
								$p.getComponentById("deployTaskGrp1").removeClass("on");
							}
						};

						scwin.select_build_project_id = function(build_id){

							const uri = common.uri.settingProjectConfig(build_id);
							common.http.fetchGet(uri,"GET",{"Content-Type":"application/json"}).then((res)=>{
								return res.json();
							}).then((data)=>{
								if(data !== null){
									let buildproj = [];
									buildproj.push(data);

									let distict = common.unique(buildproj, 'project_id');
									dtl_build_setting_step1.setJSON(distict);

									const path = $p.top().scwin.convertMenuCodeToPath("m0100030100");
									if(path != null){
										wfm_project_deploy_task_setting_step1.setSrc(path);
									}
								}
							}).catch((err)=>{
								common.win.alert("error:"+err);
							});

						};

						scwin.select_deploy_setting_id =  function(build_id){
							// deploy setting cotroller 구현하기
							$.ajax({
								url : "/manager/deploy/setting/search/getBuildId/"+parseInt(build_id) ,
								type : "get",
								async : false,
								accept : "application/json",
								contentType : "application/json; charset=utf-8",
								success : function(e) {
									let data = e;
									//console.log("builprojects success r ");
									//console.log(data);
									if(data !== null){
										let deploysetting = [];
										let temp = {};

										temp["deploy_id"]	= data.deploy_id;
										temp["build_id"] = data.build_id;
										temp["all_package_name"] = data.all_package_name;
										temp["all_signingkey_id"] = data.all_signingkey_id;
										temp["apple_issuer_id"] = data.apple_issuer_id;
										temp["apple_key_id"] = data.apple_key_id;
										temp["created_date"] = data.created_date;
										temp["updated_date"] = data.updated_date;

										deploysetting.push(temp);
										localStorage.setItem("deploy_setting_pid",data.deploy_id);
										let distict = common.unique(deploysetting, 'deploy_id');
										wfm_project_deploy_task_setting_step1.setJSON(distict);

									}

								}
								, error:function(request,status,error){
									common.win.alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
								}
							});

						};

						scwin.btn_proejct_deploy_setting_task01_onclick = function(e) {
							scwin.selected_step(1);
						};

						scwin.btn_proejct_deploy_setting_task02_onclick = function(e) {
							scwin.selected_step(2);
						};

						scwin.btn_proejct_deploy_setting_task03_onclick = function(e) {
							scwin.selected_step(3);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_project_deploy',style:'',tagname:'h2'}}]},{T:1,N:'xf:group',A:{class:'step_bar',id:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'on',id:'deployTaskGrp1',tagname:'li'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_proejct_deploy_setting_task01_onclick',useLocale:'true',localeRef:'lbl_setting_deploy'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'',id:'deployTaskGrp2',style:'',tagname:'li'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_proejct_deploy_setting_task02_onclick',useLocale:'true',localeRef:'lbl_setting_deploy_metadata'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'deployTaskGrp3',style:'',tagname:'li'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'','ev:onclick':'scwin.btn_proejct_deploy_setting_task03_onclick',useLocale:'true',localeRef:'lbl_setting_deploy_metadata'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'w2:wframe',A:{id:'wfm_project_deploy_task_setting_step1',style:'display:block;width:100%;height:100%;',src:'',scope:'true'}},{T:1,N:'w2:wframe',A:{id:'wfm_project_deploy_task_setting_step2',style:'display:none;',src:'',scope:'true'}},{T:1,N:'w2:wframe',A:{id:'wfm_project_deploy_task_setting_step3',style:'display:none;',src:'',scope:'true'}}]}]}]}]}]})