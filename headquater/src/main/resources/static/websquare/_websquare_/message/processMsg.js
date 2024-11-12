/*amd /websquare_resources/processMsg.xml 3998 80b893d68a8980cd9de68eff750c93b90080344ce4ec3a0edc89d369bdf47722 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'style',E:[{T:3,text:'\r\n            .w2_proc {\r\n                position : absolute;\r\n                z-index : 10001;\r\n            }\r\n            .w2_proc_group_wrapper {\r\n                position : relative;\r\n                padding : 20px 0 0 13px; \r\n                width : 280px; \r\n                height : 81px; \r\n                border : 1px solid #b3b3b3; \r\n                background : url("../_websquare_/message/images/bg_ly.gif") left top repeat-x;\r\n            }\r\n            .w2_proc_text_msg {\r\n                width : 200px;\r\n                height : 20px;\r\n                font-size : 13px;\r\n                color : #3f3f67;\r\n                font-weight : bold;\r\n                overflow : hidden;\r\n                white-space:nowrap;\r\n                text-overflow:ellipsis;\r\n            }\r\n            .w2_proc_image_bar {\r\n                width : 267px;\r\n                height : 23px;\r\n            }\r\n            .w2_proc_btn_hide {\r\n                position:absolute;\r\n                top:20px;\r\n                right:14px;\r\n                height:20px;\r\n                text-align:center;\r\n                word-wrap:break-word;\r\n                padding:3px;\r\n                display:none;\r\n            }\r\n            .w2_proc_btn_cancel {\r\n                position:absolute;\r\n                top:20px;\r\n                right:14px;\r\n                height:20px;\r\n                text-align:center;\r\n                word-wrap:break-word;\r\n                padding:3px;\r\n                display:none;\r\n            }\r\n            .w2_proc_modal {\r\n                position : absolute;\r\n                left : 0;\r\n                top : 0;\r\n                width : 100%;\r\n                height : 100%;\r\n                background-color : #cccccc;\r\n                filter : alpha(opacity=30);\r\n                opacity : 0.3;\r\n                z-index: 10000;\r\n            }\r\n        '}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
        
    scwin.onpageload = function() {
        image_bar.setSrc(WebSquare.baseURI + "/_websquare_/message/images/progressingbar.gif");
        btn_hide.dom.input.value = WebSquare.language.getMessage("Window_close");
        btn_cancel.dom.input.value = WebSquare.language.getMessage("Window_cancel");
    };
    
    scwin.onpageunload = function() {
        
    };
    
    scwin.btn_hide_onclick = function() {
        $p._getProcessMsgTargetFrame().hideProcessMessage();
    };
    
    scwin.btn_cancel_onclick = function() {
        var submissionList = $p._getProcessMsgTargetFrame()._processMsgQueue;
        for(var i = 0; i < submissionList.length; i++){
            WebSquare.ModelUtil.abort(submissionList[i].org_id, submissionList[i].scope_id);
        }
    };
    
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'w2_proc_group_wrapper',id:'group_wrapper',style:''},E:[{T:1,N:'w2:textbox',A:{class:'w2_proc_text_msg',label:'',id:'text_msg',style:''}},{T:1,N:'xf:image',A:{class:'w2_proc_image_bar',id:'image_bar',style:'',src:'',alt:'로딩중'}},{T:1,N:'xf:trigger',A:{type:'button',class:'w2_proc_btn_hide',id:'btn_hide',style:'','ev:onclick':'scwin.btn_hide_onclick'}},{T:1,N:'xf:trigger',A:{type:'button',class:'w2_proc_btn_cancel',id:'btn_cancel',style:'','ev:onclick':'scwin.btn_cancel_onclick'}}]}]}]}]})