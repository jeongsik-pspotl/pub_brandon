/*amd /cm/ui/settings/setting_vcs_list.xml 5730 6885a7ae6788469fad1b3d04904bd40797790a2656dfba73905653a20dcc681f */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function() {
							scwin.getVCSListInfo();
							// $('.acd_tit').click(function() {
							// 	var acdbox = $(this).closest('.acdbox');
							//
							// 	acdbox.toggleClass('on');
							// 	if(acdbox.hasClass("on")){
							// 		acdbox.find('.acd_congrp').slideDown();
							// 	}else {
							// 		acdbox.find('.acd_congrp').slideUp();
							// 	}
							// });
						};

						scwin.grp_vcs_onclick = function(e){
							const acdbox = $('#'+this.render.id);
							acdbox.toggleClass('on');
							if (acdbox.hasClass("on")) {
								acdbox.find('.acd_congrp').slideDown();
							} else {
								acdbox.find('.acd_congrp').slideUp();
							}
						};

						scwin.getVCSListInfo = async function () {
							const url = common.uri.getVcsSettingAll;
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.vcsListSetting(data);
						};

						scwin.vcsListSetting = function(vcsData){
							let count = 0;
							generator_vcs_setting_list.removeAll();

							for (const [idx, vcs_data] of vcsData.entries()){
								generator_vcs_setting_list.insertChild();

								let vcsSettingName = generator_vcs_setting_list.getChild(count, "vcs_setting_name");
								let vcsSettingDetailAdd = generator_vcs_setting_list.getChild(count, "grp_vcs_detail");
								let vcsSettingType = generator_vcs_setting_list.getChild(count, "vcs_setting_type");
								let vcsDetailSetting = generator_vcs_setting_list.getChild(count, "btn_vcs_detail_setting");

								vcsSettingDetailAdd.setUserData("vcs_id", vcs_data.vcs_id);
								vcsDetailSetting.setUserData("vcs_id", vcs_data.vcs_id);

								if (vcs_data.vcs_type === "git") {
									const label = common.getLabel("lbl_git");
									vcsSettingType.setValue(label);
								} else if(vcs_data.vcs_type === "localgit") {
									const label = common.getLabel("lbl_settings_repo");
									vcsSettingType.setValue(label);
								} else if(vcs_data.vcs_type === "svn") {
									const label = common.getLabel("lbl_settings_svn");
									vcsSettingType.setValue(label);
								}

								vcsSettingName.setValue(vcs_data.vcs_name);
								count++;
							}
						};

						scwin.createVcsSettingOnclick = function() {
							const vcs_setting_mode = "detailcreate";

							let data = {};
							data.vcs_setting_mode = vcs_setting_mode;

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0302010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

						scwin.grpVcsDetailOnclick = function() {
							const vcs_id = this.getUserData("vcs_id");
							const vcs_setting_mode = "detailview";

							let data = {};
							data.vcs_id = vcs_id;
							data.vcs_setting_mode = vcs_setting_mode;

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0302010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_vcs_setting'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_vcs',class:'acdbox',tagname:'li','ev:onclick':'scwin.grp_vcs_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_vcs_list'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'btn_vcs_add',type:'button',class:'btn_cm add','ev:onclick':'scwin.createVcsSettingOnclick',useLocale:'true',localeRef:'lbl_create'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_vcs_setting_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'grp_vcs_detail',class:'acd_txtbox','ev:onclick':'scwin.grpVcsDetailOnclick'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'vcs_setting_name',label:'',class:'acd_itemtxt'}},{T:1,N:'w2:textbox',A:{style:'',id:'vcs_setting_type',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'btn_vcs_detail_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.grpVcsDetailOnclick',useLocale:'true',localeRef:'lbl_setting',tooltipDisplay:'true',tooltipLocaleRef:'lbl_vcs_setting_detail'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})