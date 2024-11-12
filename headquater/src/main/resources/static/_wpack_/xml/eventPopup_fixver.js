/*amd /xml/eventPopup_fixver.xml 8407 97119e80f2e4bda1e4eac0d861e616cba13e3f279c57a9b2107df85f58aa02a9 */
define({declaration:{A:{version:"1.0",encoding:"UTF-8"}},E:[{T:1,N:"html",A:{xmlns:"http://www.w3.org/1999/xhtml","xmlns:ev":"http://www.w3.org/2001/xml-events","xmlns:w2":"http://www.inswave.com/websquare","xmlns:xf":"http://www.w3.org/2002/xforms"},E:[{T:1,N:"head",E:[{T:1,N:"w2:type",E:[{T:3,text:"COMPONENT"}]},{T:1,N:"w2:buildDate"},{T:1,N:"w2:MSA"},{T:1,N:"xf:model",E:[{T:1,N:"w2:dataCollection",A:{baseNode:"map"}},{T:1,N:"w2:workflowCollection"}]},{T:1,N:"w2:layoutInfo"},{T:1,N:"w2:publicInfo",A:{method:""}},{T:1,N:"script",A:{type:"text/javascript",src:"/js/lib/qrcode.min.js"}},{T:1,N:"script",A:{lazy:"false",type:"text/javascript"},E:[{T:4,cdata:function(scopeObj){with(scopeObj){function getTextLength(str){for(var len=0,i=0;i<str.length;i++)6==escape(str.charAt(i)).length&&len++,len++}scwin.onpageload=function(){common.setScopeObj(scwin);var paramData=$p.getParameter("param"),qrcodeID=paramData.data.historyCnt,paramData=paramData.data.user_name;whive_event_text.setLabel("<b>"+paramData+"님 W-Hive 참여 이벤트 당첨</b>을 축하드립니다! 🥳<br/><br/>"),whive_event_longtext.setLabel("W-Hive에 관심을 갖고 앱을 생성해 주셔서 감사합니다.<br/>가입하실 때 기재하신 핸드폰 번호로 "+"<span style='background-color:#fff68e'>신세계 상품권 1만원권</span>을<br/> 9월 12일 이후 순차 증정할 예정입니다.<br/><br/>밑에 리뷰 작성 시, 추첨을 통해 <span style='background-color:#fff68e'>추가 상품</span>을 드리니 놓치지 마세요!"),scwin.buildAfterQrcodeCreateByID(qrcodeID)},scwin.btn_submit_onclick=function(e){var data,comment=build_event_textarea.getValue();return comment.length<20?(alert("최소 20글자 입력해야합니다."),!1):400<comment.length?(alert("최대 400글자까지 입력 가능합니다."),!1):((data={}).comment=comment,$.ajax({url:"/manager/build/history/whiveEvent",type:"post",accept:"application/json",contentType:"application/json; charset=utf-8",data:JSON.stringify(data),dataType:"json",success:function(r,status){"success"===status&&(popup_window_wframe.close(),common.win.alert("리뷰이벤트 등록이 완료되었습니다.",function(){},{id:"popup2",width:380,height:223,name:"알림"}))},error:function(request,status,error){request=request.responseJSON;alert("message:"+request[0].result+"\n"),popup_window_wframe.close()}}),void 0)},scwin.btn_event_textarea_keyup=function(e){var comment=this.getValue();current.setLabel(comment.length),20<comment.length&&current.setStyle("color","#000000"),400<comment.length&&(alert("최대 400자까지 가능합니다."),this.getValue(comment.substring(0,400)),current.setLabel("400"))},scwin.reviewLengthValidate=function(value){return value.length<20?current.setStyle("color","#ff0000"):current.setStyle("color","#000000"),value},scwin.btn_cancel_onclick=function(){popup_window_wframe.close()},scwin.buildAfterQrcodeCreateByID=function(qrcodeID){new QRCode(document.getElementById("build_event_qrcode"),{text:g_config.HTTPSERVER_URL+"/builder/build/history/CheckAuthPopup/"+parseInt(qrcodeID),width:120,height:120,colorDark:"#000000",colorLight:"#ffffff",correctLevel:QRCode.CorrectLevel.H})}}}}]},{T:1,N:"style",E:[{T:3,text:"\n\n\t.w2window_header { display:none; }\n\n\t.w2window_body { top: 0px; }\n\n\t.w2window {\n\tposition: absolute;\n\tbackground-color: #fff;\n\tborder: 0px;\n\t}\n\n\t.w2window_restored {\n\ttop:50% !important;\n\tleft:50% !important;\n\ttransform: translateX(-50%) translateY(-50%);\n\t}\n\n\t.flex_container {\n\tdisplay: flex;\n\talign-items: center;\n\tjustify-content: space-between;\n\t}\n\n\t.w2tb .w2tb_th {\n\ttext-align: center;\n\tbackground: #FFFFFF;\n\tborder: 0px;\n\t}\n\n\t.w2tb .w2tb_td {\n\tmargin: 0;\n\tborder: 0px;\n\tvertical-align: middle;\n\t}\n\n\t.w2popup_window .w2window_content {\n\ttop:0px;\n\tbottom:0px;\n\tleft:0px;\n\tright:0px;\n\tborder-top:0px;\n\tborder-left:0px;\n\tborder-right:0px;\n\tborder-bottom:0px;\n\t}\n\n\t.submit_btn {\n\tdisplay: inline-block;\n\theight: 28px;\n\tpadding: 0 12px;\n\tline-height: 26px;\n\tfont-size: 12px;\n\tcolor: #ffffff;\n\tbackground: #8851da;\n\tvertical-align: top;\n\tborder:0px;\n\tborder-radius: 3px;\n\t}\n\t.cancel_btn {\n\t\tdisplay: inline-block;\n\t\theight: 28px;\n\t\tpadding: 0 12px;\n\t\tline-height: 26px;\n\t\tfont-size: 12px;\n\t\tcolor: #ffffff;\n\t\tbackground: #a4a4a4;\n\t\tvertical-align: top;\n\t\tborder:0px;\n\t\tborder-radius: 3px;\n\t}\n"}]}]},{T:1,N:"body",A:{"ev:onpageload":"scwin.onpageload"},E:[{T:1,N:"xf:group",A:{id:"",style:"padding:24px 26px 0px 24px;"},E:[{T:1,N:"xf:group",A:{tagname:"table",style:"width:100%;",id:"",class:"w2tb"},E:[{T:1,N:"w2:attributes",E:[{T:1,N:"w2:summary"}]},{T:1,N:"xf:group",A:{tagname:"caption"}},{T:1,N:"xf:group",A:{tagname:"colgroup"},E:[{T:1,N:"xf:group",A:{tagname:"col",style:"width:30.00%"}},{T:1,N:"xf:group",A:{tagname:"col",style:"width:70.00%"}}]},{T:1,N:"xf:group",A:{tagname:"tr"},E:[{T:1,N:"xf:group",A:{tagname:"th",style:"vertical-align:top;padding: 8px 24px 8px 8px;",class:"w2tb_th"},E:[{T:1,N:"w2:attributes",E:[{T:1,N:"w2:scope",E:[{T:3,text:"row"}]}]},{T:1,N:"div",A:{id:"build_event_qrcode",style:"width: 120px;height:120px;margin: auto;"}}]},{T:1,N:"xf:group",A:{tagname:"td",style:"vertical-align:top;",class:"w2tb_td"},E:[{T:1,N:"w2:textbox",A:{id:"whive_event_text",label:"XX님 W-Hive 참여 이벤트 당첨을 축하드립니다! 🥳<br/> <br/>",style:"width: 100%; font-size: medium"}},{T:1,N:"w2:textbox",A:{style:"width: 100%;",id:"whive_event_longtext",label:"W-Hive에 관심을 갖고 앱을 생성해 주셔서 감사합니다.<br/>가입하실 때 기재하신 핸드폰 번호로 신세계 상품권 1만원권을<br/> 9월 12일 이후 순차 증정할 예정입니다.<br/><br/>밑에 리뷰 작성 시, 추첨을 통해 추가 상품을 드리니 놓치지 마세요!"}}]}]},{T:1,N:"xf:group",A:{tagname:"tr"},E:[{T:1,N:"xf:group",A:{tagname:"th",style:"vertical-align:top;padding: 8px 24px 0px 8px;",class:"w2tb_th"},E:[{T:1,N:"w2:attributes",E:[{T:1,N:"w2:scope",E:[{T:3,text:"row"}]}]},{T:1,N:"w2:textbox",A:{id:"",label:"QRCode",style:"width: 100%;height: 35px; font-weight:bold;font-size: x-large;padding: 8px 0px 8px;"}},{T:1,N:"w2:textbox",A:{id:"",label:"카메라앱으로 스캔하시면<br/>설치할 수 있습니다",style:"font-size: smaller;"}}]},{T:1,N:"xf:group",A:{tagname:"td",style:"",class:"w2tb_td"},E:[{T:1,N:"xf:textarea",A:{minlength:"20",maxlength:"400",style:"width: 100%;height: 100px;",id:"build_event_textarea",placeholder:"최소 20자 이상 입력해야합니다.",validator:"scwin.reviewLengthValidate","ev:onkeyup":"scwin.btn_event_textarea_keyup"}},{T:1,N:"xf:group",A:{style:"float:right;",id:"theCount"},E:[{T:1,N:"w2:span",A:{label:"0",style:"color:#ff0000;",id:"current"}},{T:1,N:"w2:span",A:{label:" / 400",style:"",id:"maximum"}}]}]}]},{T:1,N:"xf:group",A:{tagname:"tr"},E:[{T:1,N:"xf:group",A:{tagname:"th",style:"",class:"w2tb_th"},E:[{T:1,N:"w2:attributes",E:[{T:1,N:"w2:scope",E:[{T:3,text:"row"}]}]}]},{T:1,N:"xf:group",A:{tagname:"td",style:"",class:"w2tb_td"},E:[{T:1,N:"xf:group",A:{id:"",style:"",class:"flex_container"},E:[{T:1,N:"xf:trigger",A:{type:"button",style:"width: 80px;height: 23px; margin-top:35px;",id:"btn_submit",class:"submit_btn","ev:onclick":"scwin.btn_submit_onclick"},E:[{T:1,N:"xf:label",E:[{T:4,cdata:"리뷰등록"}]}]},{T:1,N:"xf:trigger",A:{type:"button",style:"width: 80px;height: 23px; margin-top:35px;",id:"btn_cancel",class:"cancel_btn","ev:onclick":"scwin.btn_cancel_onclick"},E:[{T:1,N:"xf:label",E:[{T:4,cdata:"닫기"}]}]}]}]}]}]}]}]}]}]});
