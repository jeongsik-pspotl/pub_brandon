/*amd /ui/build/QRCode.xml 3458 6572dbbd6fecf0c3ec3b94bd2803627c54e94772d2ba0faf4b69f1f3865926c0 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
scwin.onpageload = function() {
	const paramData = $p.getParameter("param");

	const qrcodeID = paramData.data.historyCnt;

	scwin.buildAfterQrcodeCreateByID(qrcodeID);
};

scwin.btn_close_onclick = function(e) {
	$p.getComponentById($p.getPopupId("popup_window_qrcode")).close();
};

scwin.buildAfterQrcodeCreateByID = function(qrcodeID){
	// console.log(g_config.HTTPSERVER_URL);
	// const qrcode = new QRCode(document.getElementById("build_qrcode"),{
	// 	text: g_config.HTTPSERVER_URL + "/builder/build/history/CheckAuthPopup/"+parseInt(qrcodeID),
	// 	width: 120,
	// 	height: 120,
	// 	colorDark : "#000000",
	// 	colorLight : "#ffffff",
	// 	correctLevel : QRCode.CorrectLevel.H
	// });
	
	const userID = $p.top().__account_info__.getRowJSON(0).user_login_id;
	const qrcode = new QRCode(document.getElementById("build_qrcode"),{
		text: g_config.HTTPSERVER_URL + "/websquare/websquare.html?w2xPath=/ui/build/check.xml&qrcodeID="+qrcodeID+"&userID="+userID,
		width: 120,
		height: 120,
		colorDark : "#000000",
		colorLight : "#ffffff",
		correctLevel : QRCode.CorrectLevel.H
	});
};


}}}]},{T:1,N:'style',E:[{T:3,text:'\n\n			.w2window_header { display:none; }\n\n			.w2window_body { top: 0px; }\n\n			.w2window {\n			position: absolute;\n			background-color: #fff;\n			border: 0px;\n			}\n\n			.w2window_restored {\n			top:50% !important;\n			left:50% !important;\n			transform: translateX(-50%) translateY(-50%);\n			}\n\n			.w2tb .w2tb_th {\n			text-align: center;\n			background: #FFFFFF;\n			border: 0px;\n			}\n\n			.w2tb .w2tb_td {\n			margin: 0;\n			border: 0px;\n			vertical-align: bottom;\n			}\n\n			.confirm_btn {\n			display: inline-block;\n			height: 28px;\n			padding: 0 12px;\n			line-height: 26px;\n			font-size: 12px;\n			color: #ffffff;\n			background: #8851da;\n			vertical-align: top;\n			border:0px;\n			border-radius: 3px;\n			}\n		'}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{id:'',style:'padding:20px;'},E:[{T:1,N:'xf:group',A:{tagname:'table',style:'width:100%;',id:'',class:'w2tb'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'caption'}},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{tagname:'col',style:'width:100.00%'}}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'col'}]}]},{T:1,N:'div',A:{id:'build_qrcode',style:'width: 120px;height:120px;margin: auto;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'col'}]}]},{T:1,N:'w2:textbox',A:{id:'',label:'',style:'height: 23px; margin-top:30px;',useLocale:'true',localeRef:'lbl_QRCode_notice'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{tagname:'th',style:'',class:'w2tb_th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'col'}]}]},{T:1,N:'xf:trigger',A:{type:'button',style:'width: 100%;height: 23px; margin-top:30px;',id:'btn_close','ev:onclick':'scwin.btn_close_onclick',class:'confirm_btn',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]})