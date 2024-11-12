/*amd /xml/userManager_domain_detail.xml 9940 f90511419889a30c368a890c5912950b5c69c90220ab5dc56eeefd322a079a21 */
define({declaration:{A:{version:"1.0",encoding:"UTF-8"}},E:[{T:1,N:"html",A:{xmlns:"http://www.w3.org/1999/xhtml","xmlns:ev":"http://www.w3.org/2001/xml-events","xmlns:w2":"http://www.inswave.com/websquare","xmlns:xf":"http://www.w3.org/2002/xforms"},E:[{T:1,N:"head",E:[{T:1,N:"w2:type",E:[{T:3,text:"DEFAULT"}]},{T:1,N:"w2:buildDate"},{T:1,N:"xf:model",E:[{T:1,N:"w2:dataCollection",A:{baseNode:"map"},E:[{T:1,N:"w2:dataList",A:{id:"dlt_rolecode_list_selectbox",saveRemovedData:"true",style:""},E:[{T:1,N:"w2:columnInfo",E:[{T:1,N:"w2:column",A:{dataType:"text",id:"role_code_id",name:"name1"}},{T:1,N:"w2:column",A:{dataType:"text",id:"role_code_name",name:"name2"}}]}]},{T:1,N:"w2:dataList",A:{id:"__workspace_save_setting_data__",saveRemovedData:"true"},E:[{T:1,N:"w2:columnInfo",E:[{T:1,N:"w2:column",A:{id:"domain_id",name:"domain_id",dataType:"text"}},{T:1,N:"w2:column",A:{id:"domain_name",name:"domain_name",dataType:"text"}}]}]}]},{T:1,N:"w2:workflowCollection"}]},{T:1,N:"script",A:{type:"text/javascript",lazy:"false"},E:[{T:4,cdata:function(scopeObj){with(scopeObj)scwin.domainCheckYn=!1,scwin.onpageload=function(){var view,save;"detailview"==localStorage.getItem("_domain_setting_mode_")?(view=common.getLabel("lbl_userManager_domain_detail_domainView"),save=common.getLabel("lbl_save"),domain_setting_title.setLabel(view),domain_create_or_save_btn.setLabel(save),domain_created_date.setDisabled(!0),domain_updated_date.setDisabled(!0),scwin.domainDetailView()):(view=common.getLabel("lbl_userManager_domain_detail_domainView"),save=common.getLabel("lbl_create"),domain_setting_title.setLabel(view),domain_create_or_save_btn.setLabel(save),domain_created_date.setDisabled(!0),domain_updated_date.setDisabled(!0))},scwin.onpageunload=function(){},scwin.checkData=function(){var message,domain_name=manager_domain_name.getValue();return common.isEmptyStr(domain_name)?(message=common.getLabel("lbl_userManager_domain_detail_blank"),alert(message),!1):common.checkAllInputText("CHECK_INPUT_TYPE_SPC",domain_name)?(message=common.getLabel("lbl_can_not_special_char"),alert(message),!1):!!scwin.domainCheckYn||(message=common.getLabel("lbl_userManager_domain_detail_buttonClick"),alert(message),!1)},scwin.domainDetailView=function(){var domain_id=localStorage.getItem("_domain_id_"),options={};options.action="/manager/domain/search/domain/"+parseInt(domain_id),options.mode="asynchronous",options.mediatype="application/json",options.method="GET",options.success=function(e){var create_date,updated_date,e=e.responseJSON;null!=e&&(updated_date=create_date="",null!=e.create_date&&(create_date=e.create_date.replace(/T/g," ")),null!=e.updated_date&&(updated_date=e.updated_date.replace(/T/g," ")),manager_domain_name.setValue(e.domain_name),domain_created_date.setValue(create_date),domain_updated_date.setValue(updated_date))},options.error=function(e){alert("code:"+request.status+"\nmessage:"+request.responseText+"\n")},$p.ajax(options)},scwin.select_check_domain_name=function(domain_name){var options={};options.action="/manager/domain/search/checkName/"+domain_name,options.mode="asynchronous",options.mediatype="application/json",options.method="GET",options.success=function(e){var message,data=e.responseJSON;200!==e.responseStatusCode&&201!==e.responseStatusCode||("no"==data[0].domain_name_not_found?(message=common.getLabel("lbl_exist_name"),alert(message)):"yes"==data[0].domain_name_not_found&&(message=common.getLabel("lbl_can_use_name"),alert(message),scwin.domainCheckYn=!0))},options.error=function(e){500===e.responseStatusCode||alert("message:"+e.responseText+"\n")},$p.ajax(options)},scwin.setDomainCreateAndInsert=function(domain_detail_data){var options={action:"/manager/domain/create",mode:"asynchronous",mediatype:"application/json"};options.requestData=JSON.stringify(domain_detail_data),options.method="POST",options.success=function(e){var message,data=e.responseJSON;200!==e.responseStatusCode&&201!==e.responseStatusCode||null==data?(message=common.getLabel("lbl_userManager_domain_detail_fail"),alert(message)):(message=common.getLabel("lbl_userManager_domain_detail_success"),alert(message),$p.parent().wfm_main.setSrc("/xml/userManager.xml"))},options.error=function(e){alert("message:"+e.responseText+"\n")},$p.ajax(options)},scwin.setDomainNameUpdate=function(domain_id,domain_name){var data={},domain_id=(data.domain_id=domain_id,data.domain_name=domain_name,{});domain_id.action="/manager/domain/update",domain_id.mode="asynchronous",domain_id.mediatype="application/json",domain_id.method="POST",domain_id.requestData=JSON.stringify(data),domain_id.success=function(e){var message,data=e.responseJSON;200!==e.responseStatusCode&&201!==e.responseStatusCode||null==data?(message=common.getLabel("lbl_userManager_domain_detail_modifiedFail"),alert(message)):(message=common.getLabel("lbl_userManager_domain_detail_modifiedSuccess"),alert(message),$p.parent().wfm_main.setSrc("/xml/userManager.xml"))},domain_id.error=function(e){alert("message:"+e.responseText+"\n")},$p.ajax(domain_id)},scwin.btn_update_workspace_detail_onclick=function(e){var domain_id,data;"detailview"==localStorage.getItem("_domain_setting_mode_")?((data={}).domain_name=manager_domain_name.getValue(),domain_id=localStorage.getItem("_domain_id_"),scwin.setDomainNameUpdate(domain_id,data.domain_name)):scwin.checkData()&&((data={}).domain_name=manager_domain_name.getValue(),scwin.setDomainCreateAndInsert(data))},scwin.step1_select_platform_onchange=function(e){var platform=this.getValue(this.getSelectedIndex);localStorage.setItem("_role_code_id_",platform)},scwin.step1_btn_check_domain_onclick=function(e){var message,domain_name=manager_domain_name.getValue();return common.isEmptyStr(domain_name)?(message=common.getLabel("lbl_userManager_domain_detail_blank"),alert(message),!1):common.checkAllInputText("CHECK_INPUT_TYPE_SPC",domain_name)?(message=common.getLabel("lbl_can_not_special_char"),alert(message),!1):(scwin.select_check_domain_name(domain_name),void 0)}}}]}]},{T:1,N:"body",A:{"ev:onpageload":"scwin.onpageload","ev:onpageunload":"scwin.onpageunload"},E:[{T:1,N:"xf:group",A:{class:"gallery_box",id:"",style:""},E:[{T:1,N:"xf:group",A:{class:"dfbox",id:"",style:""},E:[{T:1,N:"xf:group",A:{class:"fl"},E:[{T:1,N:"w2:textbox",A:{class:"gal_tit fl",id:"domain_setting_title",label:"",style:"",useLocale:"true",localeRef:"lbl_userManager_domain_detail_domainView"}}]},{T:1,N:"xf:group",A:{class:"fr"},E:[{T:1,N:"xf:trigger",A:{class:"btn_cm type1 fl",id:"domain_create_or_save_btn",style:"",type:"button","ev:onclick":"scwin.btn_update_workspace_detail_onclick",useLocale:"true",localeRef:"lbl_modify"},E:[{T:1,N:"xf:label"}]}]}]},{T:1,N:"xf:group",A:{class:"gal_body",id:"",style:""},E:[{T:1,N:"xf:group",A:{id:"",style:"",class:"form_wrap"},E:[{T:1,N:"xf:group",A:{id:"",style:"",class:"",tagname:"ul"},E:[{T:1,N:"xf:group",A:{id:"",style:"",class:"",tagname:"li"},E:[{T:1,N:"w2:textbox",A:{id:"",style:"",class:"form_name",label:"",useLocale:"true",localeRef:"lbl_userManager_domain_detail_domainName"}},{T:1,N:"xf:group",A:{class:"ipt_box",id:"",style:""},E:[{T:1,N:"xf:input",A:{id:"manager_domain_name",style:"",adjustMaxLength:"false"}},{T:1,N:"xf:trigger",A:{id:"",style:"",class:"btn_cm",type:"button","ev:onclick":"scwin.step1_btn_check_domain_onclick",useLocale:"true",localeRef:"lbl_dup_check"},E:[{T:1,N:"xf:label"}]}]}]},{T:1,N:"xf:group",A:{id:"grp_user_craete_date",style:"",class:"",tagname:"li"},E:[{T:1,N:"w2:textbox",A:{id:"",style:"",class:"form_name",label:"",useLocale:"true",localeRef:"lbl_created_date"}},{T:1,N:"xf:input",A:{id:"domain_created_date",style:"",adjustMaxLength:"false"}}]},{T:1,N:"xf:group",A:{id:"grp_user_update_date",style:"",class:"",tagname:"li"},E:[{T:1,N:"w2:textbox",A:{id:"",style:"",class:"form_name",label:"",useLocale:"true",localeRef:"lbl_modified_date"}},{T:1,N:"xf:input",A:{id:"domain_updated_date",style:"",adjustMaxLength:"false"}}]}]}]}]}]}]}]}]});
