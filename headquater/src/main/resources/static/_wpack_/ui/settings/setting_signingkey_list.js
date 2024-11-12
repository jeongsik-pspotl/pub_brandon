/*amd /cm/ui/settings/setting_signingkey_list.xml 6117 5b6314874a1623d921c97da1f7a20247f9f8d7571653861083e77d49b54e3152 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function() {
							scwin.getKeySettingInfo();
							// $('.acd_tit').click(function() {
							// 	let acdbox = $(this).closest('.acdbox');
							//
							//     acdbox.toggleClass('on');
							//     if(acdbox.hasClass("on")){
							//         acdbox.find('.acd_congrp').slideDown();
							//     }else {
							//         acdbox.find('.acd_congrp').slideUp();
							//     }
							// });
						};

						scwin.grp_signingkey_onclick = function(e){
							const acdbox = $('#'+this.render.id);
							acdbox.toggleClass('on');
							if (acdbox.hasClass("on")) {
								acdbox.find('.acd_congrp').slideDown();
							} else {
								acdbox.find('.acd_congrp').slideUp();
							}
						};

						scwin.getKeySettingInfo = function () {
							const url = common.uri.getKeySettingAll;
							const method = "POST";
							const headers = {"Content-Type": "application/json"};

							common.http.fetch(url, method, headers)
								.then( res => {
									scwin.keyListSetting(res);
								});
						};

						scwin.keyListSetting = function(keySettingData){
							let count = 0;
							generator_signingkey_setting_list.removeAll();

							for(const [idx, key_data] of keySettingData.entries()){
								generator_signingkey_setting_list.insertChild();

								let keySettingName = generator_signingkey_setting_list.getChild(count,"key_setting_name");
								let keySettingPlatform = generator_signingkey_setting_list.getChild(count,"key_setting_platfrom");
								let keySettingDetailAdd = generator_signingkey_setting_list.getChild(count,"detail_key_id");
								let keySettingType = generator_signingkey_setting_list.getChild(count,"key_setting_type");
								let keySettingDetailIcon = generator_signingkey_setting_list.getChild(count, "detail_key_id_setting");

								keySettingDetailAdd.setUserData("key_id", key_data.key_id);
								keySettingDetailAdd.setUserData("platform", key_data.platform);
								keySettingDetailIcon.setUserData("key_id", key_data.key_id);
								keySettingDetailIcon.setUserData("platform", key_data.platform);

								if (key_data.platform.toLowerCase() === "android") {
									keySettingPlatform.addClass("ico_android");
								} else if (key_data.platform.toLowerCase() === "ios") {
									keySettingPlatform.addClass("ico_ios");
								}

								if (key_data.key_type == "build") {
									const label = common.getLabel("lbl_settings_dev");
									keySettingType.setValue(label);

								} else if (key_data.key_type == "deploy") {
									const label = common.getLabel("lbl_operate");
									keySettingType.setValue(label);
								}
								keySettingName.setValue(key_data.key_name);
								count++;
							}
						};

						scwin.signingKeyCreateOnclick = function () {
							let data = {};
							data.key_setting_mode = "detailcreate";

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						}

						scwin.signingKeyDetailOnclick = function() {
							let data = {};
							data.key_id = this.getUserData("key_id");
							data.key_platform = this.getUserData("platform");
							data.key_setting_mode = "detailview";

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0304010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_signingkey_setting'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_signingkey',class:'acdbox',tagname:'li','ev:onclick':'scwin.grp_signingkey_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_signingkey_setting_list'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'',type:'button',class:'btn_cm add',useLocale:'true',localeRef:'lbl_create','ev:onclick':'scwin.signingKeyCreateOnclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_signingkey_setting_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'detail_key_id',class:'acd_txtbox','ev:onclick':'scwin.signingKeyDetailOnclick'},E:[{T:1,N:'w2:span',A:{style:'',id:'key_setting_platfrom',label:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'key_setting_name',label:'',class:'acd_itemtxt'}},{T:1,N:'w2:textbox',A:{style:'',id:'key_setting_type',label:'',class:'txt_name'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',style:'',id:'detail_key_id_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.signingKeyDetailOnclick',useLocale:'true',localeRef:'lbl_setting',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_detail'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})