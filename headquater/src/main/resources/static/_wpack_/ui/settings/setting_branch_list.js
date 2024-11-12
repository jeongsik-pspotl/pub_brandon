/*amd /ui/settings/setting_branch_list.xml 5114 400826174c5d0629d7c6d7d8a5476b04235c7a0db37db4587b94c763fa7329cd */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function () {
							scwin.getDomainListInfo();
							// $('.acd_tit').click(function () {
							// 	var acdbox = $(this).closest('.acdbox');
							//
							// 	acdbox.toggleClass('on');
							// 	if (acdbox.hasClass("on")) {
							// 		acdbox.find('.acd_congrp').slideDown();
							// 	} else {
							// 		acdbox.find('.acd_congrp').slideUp();
							// 	}
							// });
						};

						scwin.grp_builder_onclick = function(e){
							const acdbox = $('#'+this.render.id);
							acdbox.toggleClass('on');
							if (acdbox.hasClass("on")) {
								acdbox.find('.acd_congrp').slideDown();
							} else {
								acdbox.find('.acd_congrp').slideUp();
							}
						};

						scwin.getDomainListInfo = async function () {
							let url = common.uri.getBranchSettingAll;
							let method = "GET";
							let headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.builderListSetting(data);
						};

						scwin.builderListSetting = function (builderData) {
							let count = 0;

							for (const [idx, data] of builderData.entries()) {
								generator_builder_setting_list.insertChild();

								let builderName = generator_builder_setting_list.getChild(count, "builder_setting_name");
								let builderDetailadd = generator_builder_setting_list.getChild(count, "builder_detail_bybuilderID");
								let builderDetailSettingAdd = generator_builder_setting_list.getChild(count, "btn_builder_detail_setting");

								// workspace detail view : workspace_id, workspace_name, role_code_name param 전달
								builderDetailadd.setUserData("builder_id", data.builder_id);
								builderDetailadd.setUserData("builder_name", data.builder_name);

								builderDetailSettingAdd.setUserData("builder_id", data.builder_id);
								builderDetailSettingAdd.setUserData("builder_name", data.builder_name);

								builderName.setValue(data.builder_name);
								count++;
							}
						};

						scwin.create_branchSetting_onclick = function () {
							const builder_setting_mode = "detailcreate";

							let data = {};
							data.builder_setting_mode = builder_setting_mode;

							// Builder Create
							const menuKey = $p.top().scwin.convertMenuCodeToMenuKey("m0301010000");
							$p.top().scwin.add_tab(menuKey, null, data);
						};

						scwin.builder_detail_onclick = function () {
							const builder_id = this.getUserData("builder_id");
							const builder_name = this.getUserData("builder_name");
							const builder_setting_mode = "detailview";

							let data = {};
							data.builder_id = builder_id;
							data.builder_name = builder_name;
							data.builder_setting_mode = builder_setting_mode;

							// Builder Detail
							const menuKey = $p.top().scwin.convertMenuCodeToMenuKey("m0301010000");
							$p.top().scwin.add_tab(menuKey, null, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_builder_setting'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_builder',class:'acdbox',tagname:'li','ev:onclick':'scwin.grp_builder_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_builder_list'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'',type:'button',class:'btn_cm add',useLocale:'true',localeRef:'lbl_create','ev:onclick':'scwin.create_branchSetting_onclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'w2:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_builder_setting_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'builder_detail_bybuilderID',class:'acd_txtbox','ev:onclick':'scwin.builder_detail_onclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'builder_setting_name',label:'item txt',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',id:'btn_builder_detail_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.builder_detail_onclick',useLocale:'true',localeRef:'lbl_setting',tooltipDisplay:'true',tooltipLocaleRef:'lbl_builder_detail'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})