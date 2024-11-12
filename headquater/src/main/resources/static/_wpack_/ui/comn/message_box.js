/*amd /cm/ui/comn/message_box.xml 5677 4d5e8639d3b536df271948625a4f51d2246942967076d170e1041ea3e857e536 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dlt_elMenuGroupVoList',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'idx',name:'name1',dataType:'text'}},{T:1,N:'w2:column',A:{id:'menuGroupNo',name:'name2',dataType:'text'}},{T:1,N:'w2:column',A:{id:'menuGroupNm',name:'name3',dataType:'text'}},{T:1,N:'w2:column',A:{id:'sortSeq',name:'name4',dataType:'text'}},{T:1,N:'w2:column',A:{id:'level',name:'name5',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'style',E:[{T:3,text:'\n			.w2window_restored {\n				top:50% !important;\n				left:50% !important;\n				transform: translateX(-50%) translateY(-50%);\n				z-index: 9999;\n			}\n		'}]},{T:1,N:'script',A:{lazy:'false',type:'javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.popupId = "";
						scwin.closeCallbackFncName = null;
						const param = $p.getParameter("param");

						scwin.onpageload = function () {
							scwin.popupId = param.popupId;
							scwin.closeCallbackFncName = param.callbackFn;
							txb_message.setValue(param.message);

							switch (param.messageType) {
								case "alert":
									btn_cancel.setStyle("display", "none");
									input_confirm.setStyle("display", "none");
									break;
								case "confirm":
									btn_cancel.setStyle("display", "inline-block");
									input_confirm.setStyle("display", "none");
									break;
								case "prompt":
									btn_cancel.setStyle("display", "inline-block");

									let message;
									switch (param.opts.do) {
										case "deleteProject":
											const project_name = param.opts.project_name;
											message = common.getLabel("lbl_workspace_android_count_str");
											message = common.getFormatStr(message, project_name);
											break;
										case "deleteWorkspace":
											const workspace_name = param.opts.workspace_name;
											message = common.getLabel("lbl_userManager_workspace_role_detail_workspace_name");
											message = common.getFormatStr(message, workspace_name);
											break;
										case "cancelProfessionalTierProject":
											const cancel_value = common.getLabel("lbl_userManager_pricing_cancel");
											message = common.getLabel("lbl_userManager_admin_detail_tier_professional_cancel_propt_detail");
											message = common.getFormatStr(message, cancel_value);
											break;
									}
									txb_message.setValue(message);
									break;
							}
						};

						scwin.btn_confirm_onclick = function () {
							if (scwin.closeCallbackFncName) {
								switch (param.messageType) {
									case "prompt":
										let target_name;
										const input_name = input_confirm.getValue();
										const removeData = {};

										switch (param.opts.do) {
											case 'deleteProject':
												target_name = param.opts.project_name;
												removeData.project_id = param.opts.project_id;
												removeData.project_name = param.opts.project_name;
												break;
											case 'deleteWorkspace':
												target_name = param.opts.workspace_name;
												removeData.workspace_id = param.opts.workspace_id;
												removeData.workspace_name = param.opts.workspace_name;
												break;
											case 'cancelProfessionalTierProject':
												target_name = "구독취소";
												removeData.regDate = param.opts.regDate;
												removeData.regNextDate = param.opts.regNextDate;
												break;
										}

										const chk = (target_name === input_name);

										if (!chk) {
											let message = common.getLabel("lbl_workspace_delete_message");
											message = common.getFormatStr(message, project_name);
											txb_message.setValue(message);
										} else {
											common.win.closePopup(scwin.popupId);
											eval(scwin.closeCallbackFncName + "(" + JSON.stringify(removeData) + ")");
										}
										break;
									default:
										common.win.closePopup(scwin.popupId);
										eval(scwin.closeCallbackFncName + "(" + true + ")");
										break;
								}
							}
						};

						scwin.btn_cancel_onclick = function (e) {
							common.win.closePopup(scwin.popupId);
							if (scwin.closeCallbackFncName) {
								eval(scwin.closeCallbackFncName + "(" + false + ")");
								//scwin.closeCallbackFncName(false);
							}
						};

					}}}]},{T:1,N:'style',A:{type:'text/css'}}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{id:'',class:'sub_contents'},E:[{T:1,N:'xf:group',A:{id:'',class:''},E:[{T:1,N:'xf:group',A:{style:'height:90px;margin-left:12px;',id:''},E:[{T:1,N:'w2:textbox',A:{style:'margin-bottom: 10px;',label:'MESSAGE',id:'txb_message',class:'tit'}},{T:1,N:'xf:input',A:{id:'input_confirm',style:'width:100%;'}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:'text-align: right;'},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm active','ev:onclick':'scwin.btn_confirm_onclick',id:'btn_confirm',style:'margin-right: 5px;',type:'button',useLocale:'true',localeRef:'lbl_confirm'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:trigger',A:{class:'btn_cm active','ev:onclick':'scwin.btn_cancel_onclick',id:'btn_cancel',type:'button',useLocale:'true',localeRef:'lbl_cancel'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]})