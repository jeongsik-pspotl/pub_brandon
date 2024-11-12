/*amd /ui/settings/setting_ftp_detail.xml 12400 3589104a630d2c8978a8a43f1f1d274aaa4118adc4b8c91058611ee4b20755c8 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.check_FTP_name_yn = false;
						scwin.onpageload = function() {
							const paramData = $p.getParameter("tabParam");
							const ftp_setting_mode = paramData.ftp_setting_mode;

							if (ftp_setting_mode === "detailview"){
								const view = common.getLabel("lbl_ftp_setting_detail");
								const save = common.getLabel("lbl_save");

								ftp_setting_title.setLabel(view);
								ftp_create_or_save_btn.setLabel(save);

								ftp_setting_name.setDisabled(true);
								ftp_setting_url.setDisabled(false);
								ftp_setting_ip.setDisabled(false);
								ftp_setting_port.setDisabled(false);
								ftp_setting_user_id.setDisabled(false);
								ftp_setting_user_pwd.setDisabled(false);
								btn_ftp_name_dup_check.setDisabled(true);

								scwin.ftpDetailView();
							} else {
								const view = common.getLabel("lbl_ftp_setting_detail");
								const create = common.getLabel("lbl_create");

								ftp_setting_title.setLabel(view);
								ftp_create_or_save_btn.setLabel(create);

								ftp_setting_name.setDisabled(false);
								ftp_setting_url.setDisabled(false);
								ftp_setting_ip.setDisabled(false);
								ftp_setting_port.setDisabled(false);
								ftp_setting_user_id.setDisabled(false);
								ftp_setting_user_pwd.setDisabled(false);
							}
						};

						scwin.checkData = function() {
							const ftp_name = ftp_setting_name.getValue();
							const ftp_server_url = ftp_setting_url.getValue();
							const ftp_server_ip = ftp_setting_ip.getValue();
							const ftp_server_port = ftp_setting_port.getValue();
							const ftp_user_id = ftp_setting_user_id.getValue();
							const ftp_user_pwd = ftp_setting_user_pwd.getValue();

							if (common.isEmptyStr(ftp_name)) {
								const message = common.getLabel("lbl_ftp_setting_input_name");
								common.win.alert(message);
								return false;
							}

							const paramData = $p.getParameter("tabParam");
							const ftp_setting_mode = paramData.ftp_setting_mode;

							if (!(ftp_setting_mode === "detailview")) {
								if (!scwin.check_FTP_name_yn ) {
									const message = common.getLabel("lbl_ftp_setting_name_dup_check");
									common.win.alert(message);
									return false;
								}
							}

							if (ftp_server_url === "") {
								const message = common.getLabel("lbl_ftp_setting_input_url");
								common.win.alert(message);
								return false;
							}

							if (ftp_server_ip === "") {
								const message = common.getLabel("lbl_ftp_setting_input_ip");
								common.win.alert(message);
								return false;
							}

							if (ftp_server_port === "") {
								const message = common.getLabel("lbl_ftp_setting_input_port");
								common.win.alert(message);
								return false;
							}

							if (ftp_user_id === "") {
								const message = common.getLabel("lbl_ftp_setting_input_id");
								common.win.alert(message);
								return false;
							}

							if (ftp_user_pwd === "") {
								const message = common.getLabel("lbl_ftp_setting_input_password");
								common.win.alert(message);
								return false;
							}
							return true;
						};

						/**
						 * ftp setting 조회 api
						 */
						scwin.ftpDetailView = async function () {
							const paramData = $p.getParameter("tabParam");
							const ftp_id = paramData.ftp_id;

							const url = common.uri.getFTPSettingDetailByID(parseInt(ftp_id));
							const method = "GET";
							const headers = {"Content-Type": "application/json"};

							const response = await common.http.fetchGet(url, method, headers, {});
							const ftpData = await response.json();

							ftp_setting_name.setValue(ftpData.ftp_name);
							ftp_setting_url.setValue(ftpData.ftp_url);
							ftp_setting_ip.setValue(ftpData.ftp_ip);
							ftp_setting_port.setValue(ftpData.ftp_port);
							ftp_setting_user_id.setValue(ftpData.ftp_user_id);
							ftp_setting_user_pwd.setValue(ftpData.ftp_user_pwd);
						};

						scwin.saveFtpSettingData = function() {
							const paramData = $p.getParameter("tabParam");
							const ftp_setting_mode =paramData.ftp_setting_mode;

							let data = {};

							if (ftp_setting_mode == "detailview") {
								data.ftp_id = paramData.ftp_id;
							}

							data.ftp_name = ftp_setting_name.getValue();
							data.ftp_url = ftp_setting_url.getValue();
							data.ftp_ip = ftp_setting_ip.getValue();
							data.ftp_port = ftp_setting_port.getValue();
							data.ftp_user_id = ftp_setting_user_id.getValue();
							data.ftp_user_pwd = ftp_setting_user_pwd.getValue();

							if(ftp_setting_mode == "detailview"){
								scwin.setFtpSettingAndUpdate(data);
							} else {
								scwin.setFtpSettingAndInsert(data);
							}
						};

						scwin.checkFTPName = function() {
							let data = {};
							data.ftp_name = ftp_setting_name.getValue();

							if (common.isEmptyStr(data.ftp_name)) {
								const message = common.getLabel("lbl_ftp_setting_input_ftp_server_name");
								common.win.alert(message);
								return false;
							}

							const url = common.uri.checkFTPName;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, data)
								.then(res => {
									let message;
									if (res != null) {
										message = common.getLabel("lbl_exist_name");
										scwin.check_FTP_name_yn = false;
									} else {
										message = common.getLabel("lbl_can_use_name");
										scwin.check_FTP_name_yn = true;
									}
									common.win.alert(message);
								})
								.catch(() => {
									const message = common.getLabel("lbl_can_use_name");
									common.win.alert(message);
									scwin.check_FTP_name_yn = true;
								});
						};

						scwin.setFtpSettingAndInsert = function (ftp_setting_data) {
							const url = common.uri.setFTPSettingCreate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, ftp_setting_data)
								.then(res => {
									if(Array.isArray(res)) {
										let message;
										if (res != null && res[0].result === "success") {
											message = common.getLabel("lbl_ftp_setting_created");
										} else {
											message = common.getLabel("lbl_ftp_setting_fail");
										}
										common.win.alert(message);
									}
								})
								.catch(() => {
									const message = common.getLabel("lbl_ftp_setting_fail");
									common.win.alert(message);
								});
						};

						scwin.setFtpSettingAndUpdate = function (ftp_setting_data) {
							const url = common.uri.setFTPSettingUpdate;
							const method = "POST";
							const headers = {"Content-Type": "application/json; charset=utf-8"};

							common.http.fetch(url, method, headers, ftp_setting_data)
								.then(res => {
									let message;
									if (res != null && res[0].result === "success") {
										message = common.getLabel("lbl_ftp_setting_updated_info");
									} else {
										message = common.getLabel("lbl_ftp_setting_update_fail");
									}
									common.win.alert(message);
								})
								.catch(() => {
									const message = common.getLabel("lbl_ftp_setting_update_fail");
									common.win.alert(message);
								});
						};

						scwin.createFtpSettingOnclick = function() {
							if (scwin.checkData()) {
								scwin.saveFtpSettingData();
							}

						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_ftp_setting'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'ftp_setting_title',label:'',class:'',useLocale:'true',localeRef:'lbl_ftp_setting_detail'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'ftp_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.createFtpSettingOnclick'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'tblbox'},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',tagname:'table',style:'',id:'',class:'w2tb tbl'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:180px;'}},{T:1,N:'xf:group',A:{tagname:'col',style:''}}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th req'},E:[{T:1,N:'xf:group',A:{id:'',class:'tooltipbox'},E:[{T:1,N:'w2:textbox',A:{ref:'',style:'',userData2:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_ftp_setting_name'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',style:'',class:'w2tb_td'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'xf:input',A:{style:'width:100%;',id:'ftp_setting_name',class:''}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_ftp_name_dup_check',type:'button',class:'btn_cm pt','ev:onclick':'scwin.checkFTPName',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_ftp_setting_detail_url'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:input',A:{class:'',id:'ftp_setting_url',style:'width:100%;'}}]}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'FTP Server IP',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_ftp_setting_ip'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'ftp_setting_ip',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_ftp_setting_port'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'ftp_setting_port',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr',style:''},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_ftp_setting_id'}}]}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'xf:input',A:{class:'',id:'ftp_setting_user_id',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',class:'w2tb_th '},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',ref:'',style:'',userData2:'',useLocale:'true',localeRef:'lbl_ftp_setting_password'}}]},{T:1,N:'xf:group',A:{tagname:'td',class:'w2tb_td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'ftp_setting_user_pwd',dataType:'password',type:'password',style:'width:100%;'}}]}]}]}]}]}]}]}]}]})