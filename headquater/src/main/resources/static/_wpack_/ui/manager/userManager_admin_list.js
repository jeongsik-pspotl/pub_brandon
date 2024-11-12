/*amd /ui/manager/userManager_admin_list.xml 7432 395d852c8bb79457df1dadd19a39d153b67074c5c6ba79bbfc289e190d12bbf6 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dlt_member_manger_List',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'user_id',name:'user_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'email',name:'email',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_role',name:'user_role',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_name',name:'user_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'domain_id',name:'domain_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'create_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_date',name:'update_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'last_login_date',name:'last_login_date',dataType:'text'}}]},{T:1,N:'w2:dataList',A:{id:'__member_manager_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'user_id',name:'user_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'email',name:'email',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_role',name:'user_role',dataType:'text'}},{T:1,N:'w2:column',A:{id:'user_name',name:'user_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'domain_id',name:'domain_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'create_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_date',name:'update_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'last_login_date',name:'last_login_date',dataType:'text'}}]}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function() {
							$('.acd_tit').click(function() {
								let acdbox = $(this).closest('.acdbox');

								acdbox.toggleClass('on');
								if(acdbox.hasClass("on")){
									acdbox.find('.acd_congrp').slideDown();
								}else {
									acdbox.find('.acd_congrp').slideUp();
								}
							});
							scwin.getMemberListInfo();
						};

						scwin.getMemberListInfo = async function () {
							const url = common.uri.getUserListAll;
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.getMemberSettingList(data);
						};

						scwin.getMemberSettingList = function(data) {
							let managerAdmin = [];

							for (const [row, user_data] of data.entries()) {
								let temp = {};

								temp["user_id"] = user_data.user_id;
								temp["email"] = user_data.email;
								temp["domain_id"] = user_data.domain_id;
								temp["user_role"] = user_data.user_role;
								temp["user_name"] = user_data.user_name;
								temp["created_date"] = user_data.created_date;
								temp["updated_date"] = user_data.updated_date;
								temp["last_login_date"] = user_data.last_login_date;

								managerAdmin.push(temp);
							}

							const distinct = common.unique(managerAdmin, 'user_id');
							dlt_member_manger_List.setJSON(distinct);
							const member_manager = dlt_member_manger_List.getAllJSON();

							let count = 0;

							for (const [idx, member_manager_data] of member_manager.entries()) {
								generator_member_manager_list.insertChild();

								const txt_member_name = generator_member_manager_list.getChild(count,"member_setting_name");
								const memberDetailadd = generator_member_manager_list.getChild(count,"member_detail_byMemberID");
								const memberDetailSettingAdd = generator_member_manager_list.getChild(count,"btn_member_detail_setting");

								memberDetailadd.setUserData("user_id", member_manager_data.user_id);
								memberDetailadd.setUserData("domain_id", member_manager_data.domain_id);

								memberDetailSettingAdd.setUserData("user_id", member_manager_data.user_id);
								memberDetailSettingAdd.setUserData("domain_id", member_manager_data.domain_id);

								txt_member_name.setValue(member_manager_data.user_name);
								count++;
							}
						};

						scwin.create_memberSetting_onclick = function() {
							const admin_setting_mode = "detailcreate";

							let data = {};
							data.admin_setting_mode = admin_setting_mode;

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0404010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

						scwin.grp_list_memberdetail_byMemberID_onclick = function() {
							const user_id = this.getUserData("user_id");
							const domain_id = this.getUserData("domain_id");
							const admin_setting_mode = "detailview";

							let data = {};
							data.user_id = user_id;
							data.domain_id = domain_id;
							data.admin_setting_mode = admin_setting_mode;

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0404010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_userManager_userList'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',class:'acdbox',tagname:'li'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_userManager_userList'}},{T:1,N:'xf:group',A:{style:'',id:'create_userSetting_btn_id',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'',type:'button',class:'btn_cm add',useLocale:'true',localeRef:'lbl_create','ev:onclick':'scwin.create_memberSetting_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_member_manager_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'member_detail_byMemberID',class:'acd_txtbox','ev:onclick':'scwin.grp_list_memberdetail_byMemberID_onclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'member_setting_name',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_member_detail_setting',tooltipDisplay:'true',tooltipLocaleRef:'lbl_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.grp_list_memberdetail_byMemberID_onclick',useLocale:'true',localeRef:'lbl_setting'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})