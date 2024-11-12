/*amd /xml/userManager_user_role_list.xml 6639 68e515dbaa3fb12465722114192dbb5a213f118173b1d0b9e6b4b0a6d8a4d1c1 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'__role_list_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'role_id',name:'role_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'role_name',name:'role_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'created_date',name:'created_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'updated_date',name:'updated_date',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){

				scwin.onpageload = function() {

					scwin.getRoleListInfo();
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

			scwin.getRoleListInfo = async () => {
				let url = common.uri.getUesrRoleListAll;
				let method = "GET";
				let headers = {"Content-Type": "application/json"};

				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				scwin.userRoleListSetting(data);
			};

            scwin.userRoleListSetting = function(data){

				var domainArr = [];

				for (var row in data) {
					var temp = {};

					temp["role_id"] = data[row].role_id;
					temp["role_name"] = data[row].role_name;
					temp["created_date"]= data[row].created_date;
					// temp["member_role"] = data[row].member_role;
					// temp["member_name"]	= data[row].member_name;

					// temp["updated_date"]= data[row].updated_date;
					// temp["last_login_date"]= data[row].last_login_date;
					//
					domainArr.push(temp);

				}

				let distict = common.unique(domainArr, 'role_id');
				__role_list_data__.setJSON(distict);
				let role_manager = __role_list_data__.getAllJSON();

				let count = 0;

				for(let idx in role_manager){

					let buildbtnInx = generator_user_role_setting_list.insertChild();

					let txt_role_name = generator_user_role_setting_list.getChild(count,"user_role_name");
					let roleDetailadd = generator_user_role_setting_list.getChild(count,"role_detail_by_roleID");
					let roleDetailSettingAdd = generator_user_role_setting_list.getChild(count,"btn_role_detail_setting");

					// workspace detail view : workspace_id, workspace_name, role_code_name param 전달
					roleDetailadd.setUserData("role_id",role_manager[idx].role_id);
					roleDetailadd.setUserData("role_name",role_manager[idx].role_name);
					// domainDetailadd.setUserData("member_role",domain_manager[idx].status); // member role

					roleDetailSettingAdd.setUserData("role_id",role_manager[idx].role_id);
					roleDetailSettingAdd.setUserData("role_name",role_manager[idx].role_name);
					// domainDetailSettingAdd.setUserData("member_role",domain_manager[idx].status); // member role

					txt_role_name.setValue(role_manager[idx].role_name);
					count++;

				}

			};

			scwin.create_UserRoleDetail_onclick = function(){
				let data = {};
				data.member_setting_mode = "detailcreate";

				const menuKey = $p.top().scwin.convertMenuCodeToMenuKey("m0403010000");
				$p.top().scwin.add_tab(menuKey, null, data);

			};

				scwin.grp_list_role_detail_by_roleID_onclick = function(e){

					let role_id = this.getUserData("role_id");
					let role_name = this.getUserData("role_name");

					let data = {};
					data.role_id = role_id;
					data.role_name = role_name;
					data.member_setting_mode = "detailview";

					const menuKey = $p.top().scwin.convertMenuCodeToMenuKey("m0403010000");
					$p.top().scwin.add_tab(menuKey, null, data);

				};





}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_userManager_role'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',class:'acdbox',tagname:'li'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_userManager_permission'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'',type:'button',class:'btn_cm add','ev:onclick':'scwin.create_UserRoleDetail_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'생성'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'ectgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'ectbox'},E:[{T:1,N:'w2:span',A:{style:'',id:'',label:'android',class:'ico_android'}},{T:1,N:'w2:span',A:{class:'item_cnt_txt',id:'',label:'5',style:''}}]},{T:1,N:'xf:group',A:{class:'ectbox',id:'',style:''},E:[{T:1,N:'w2:span',A:{class:'ico_ios',id:'',label:'IOS',style:''}},{T:1,N:'w2:span',A:{class:'item_cnt_txt',id:'',label:'5',style:''}}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_user_role_setting_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'role_detail_by_roleID',class:'acd_txtbox','ev:onclick':'scwin.grp_list_role_detail_by_roleID_onclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'user_role_name',label:'item txt',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'',style:'',id:'btn_role_detail_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.grp_list_role_detail_by_roleID_onclick',useLocale:'true',localeRef:'lbl_setting'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})