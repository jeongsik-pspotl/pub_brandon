/*amd /ui/settings/setting_ftp_list.xml 4623 5aa1799961599b8f18a4098220fdc7e0d452333c646c26420353f5ae9da21fba */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.onpageload = function () {
							scwin.getFTPListInfo();
							// $('.acd_tit').click(function () {
							// 	let acdbox = $(this).closest('.acdbox');
							//
							// 	acdbox.toggleClass('on');
							// 	if (acdbox.hasClass("on")) {
							// 		acdbox.find('.acd_congrp').slideDown();
							// 	} else {
							// 		acdbox.find('.acd_congrp').slideUp();
							// 	}
							// });
						};

						scwin.grp_ftp_onclick = function(e){
							const acdbox = $('#'+this.render.id);
							acdbox.toggleClass('on');
							if (acdbox.hasClass("on")) {
								acdbox.find('.acd_congrp').slideDown();
							} else {
								acdbox.find('.acd_congrp').slideUp();
							}
						};

						scwin.getFTPListInfo = async function () {
							const url = common.uri.getFTPSettingAll;
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const data = await response.json();

							scwin.FTPListSetting(data);
						};

						scwin.FTPListSetting = function (data) {
							let count = 0;

							for (const [idx, ftpData] of data.entries()) {
								generator_ftp_setting_list.insertChild();

								let ftpSettingName = generator_ftp_setting_list.getChild(count, "ftp_setting_name");
								let ftpSettingDetailAdd = generator_ftp_setting_list.getChild(count, "grp_ftp_detail");
								let ftpSettingDetailBtn = generator_ftp_setting_list.getChild(count, "btn_ftp_detail_setting");

								ftpSettingDetailAdd.setUserData("ftp_id", ftpData.ftp_id);
								ftpSettingDetailBtn.setUserData("ftp_id", ftpData.ftp_id);

								ftpSettingName.setValue(ftpData.ftp_name);
								count++;
							}
						};

						scwin.createFtpSettingOnclick = function () {
							const ftp_setting_mode = "detailcreate";

							let data = {};
							data.ftp_setting_mode = ftp_setting_mode;

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0303010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

						scwin.detailFtpSettingOnClick = function () {
							let ftp_id = this.getUserData("ftp_id");
							let ftp_setting_mode = "detailview";

							let data = {};

							data.ftp_id = ftp_id;
							data.ftp_setting_mode = ftp_setting_mode;

							const menu_key = $p.top().scwin.convertMenuCodeToMenuKey("m0303010000");
							$p.top().scwin.add_tab(menu_key, null, data);
						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_ftp_setting'}}]}]}]},{T:1,N:'xf:group',A:{class:'contents_inner bottom nosch',id:''},E:[{T:1,N:'xf:group',A:{id:'',class:'acdgrp'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_list',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'grp_ftp',class:'acdbox',tagname:'li','ev:onclick':'scwin.grp_ftp_onclick'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_titgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtitbox'},E:[{T:1,N:'w2:textbox',A:{tagname:'p',style:'',id:'',label:'',class:'acd_tit',useLocale:'true',localeRef:'lbl_ftp_setting_list'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'acdtit_subbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'',type:'button',class:'btn_cm add',useLocale:'true',localeRef:'lbl_create','ev:onclick':'scwin.createFtpSettingOnclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_congrp'},E:[{T:1,N:'w2:generator',A:{style:'',id:'generator_ftp_setting_list',class:'acd_itemgrp'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'acd_itembox'},E:[{T:1,N:'xf:group',A:{id:'',class:'acd_item'},E:[{T:1,N:'xf:group',A:{style:'',id:'grp_ftp_detail',class:'acd_txtbox','ev:onclick':'scwin.detailFtpSettingOnClick'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',class:''}},{T:1,N:'w2:textbox',A:{style:'',id:'ftp_setting_name',label:'',class:'acd_itemtxt'}}]},{T:1,N:'xf:group',A:{id:'',class:'acd_icobox'},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',tooltip:'tooltip',id:'btn_ftp_detail_setting',class:'btn_cm icon btn_i_setting','ev:onclick':'scwin.detailFtpSettingOnClick',useLocale:'true',localeRef:'lbl_setting',tooltipDisplay:'true',tooltipLocaleRef:'lbl_ftp_setting_detail'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]}]}]}]}]}]}]})