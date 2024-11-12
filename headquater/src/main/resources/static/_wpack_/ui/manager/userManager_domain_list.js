/*amd /ui/manager/userManager_domain_list.xml 5819 c26a8cc410db77755da4448df43200c91038f1163f13788957619c8e19487eb7 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dlt_domain_List',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'domain_id',name:'name1',dataType:'text'}},{T:1,N:'w2:column',A:{id:'domain_name',name:'name2',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'name3',dataType:'text'}},{T:1,N:'w2:column',A:{id:'updated_date',name:'name4',dataType:'text'}}]},{T:1,N:'w2:data',A:{use:'true'}}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function() {
							scwin.getDomainListInfo();

							$('.acd_tit').click(function() {
								var acdbox = $(this).closest('.acdbox');

								acdbox.toggleClass('on');
								if(acdbox.hasClass("on")){
									acdbox.find('.acd_congrp').slideDown();
								}else {
									acdbox.find('.acd_congrp').slideUp();
								}
							});
						};

						scwin.getDomainListInfo = async function () {
							const url = common.uri.getDomainListAll;
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.domainListSetting(data);
						};

						scwin.domainListSetting = function(domainData) {
							let domainArr = [];

							for (const [row, data] of domainData.entries()) {
								let temp = {};

								temp["domain_id"] = data.domain_id;
								temp["domain_name"] = data.domain_name;
								temp["created_date"]= data.create_date;

								domainArr.push(temp);
							}

							let distinct = common.unique(domainArr, 'domain_id');
							dlt_domain_List.setJSON(distinct);

							let domain_manager =  dlt_domain_List.getAllJSON();
							let count = 0;

							for (const [idx, managerData] of domain_manager.entries()){
								generator_domain_setting_list.insertChild();

								let txt_domain_name = generator_domain_setting_list.getChild(count, "domain_setting_name");
								let domainDetailadd = generator_domain_setting_list.getChild(count, "domain_detail_bydomainID");
								let domainDetailSettingAdd = generator_domain_setting_list.getChild(count, "btn_domain_detail_setting");

								// workspace detail view : workspace_id, workspace_name, role_code_name param 전달
								domainDetailadd.setUserData("domain_id", managerData.domain_id);
								domainDetailadd.setUserData("domain_name", managerData.domain_name);

								domainDetailSettingAdd.setUserData("domain_id", managerData.domain_id);
								domainDetailSettingAdd.setUserData("domain_name", managerData.domain_name);

								txt_domain_name.setValue(managerData.domain_name);
								count++;
							}
						};

						scwin.createDomainSettingOnclick = function() {
							let data = {};
							data.domain_setting_mode = "detailcreate";

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0401010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

						scwin.detailDomainSettingOnclick = function() {
							var domain_id = this.getUserData("domain_id");

							let data = {};
							data.domain_id = domain_id;
							data.domain_setting_mode = "detailview";

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0401010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{tagname:'h2',useLocale:'true',localeRef:'lbl_userManager_admin_detail_domain'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',class:'acdbox',tagname:'li'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_userManager_domainSetting'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{type:'button',class:'btn_cm add','ev:onclick':'scwin.createDomainSettingOnclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_domain_setting_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'domain_detail_bydomainID',class:'acd_txtbox','ev:onclick':'scwin.detailDomainSettingOnclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'domain_setting_name',label:'item txt',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_domain_detail_setting',tooltipDisplay:'true',tooltipLocaleRef:'lbl_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.detailDomainSettingOnclick',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})