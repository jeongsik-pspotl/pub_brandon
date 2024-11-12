/*amd /xml/WsUsrPriceCompletePopup.xml 5395 9dffc94993c56944a848a6eddd59549bd39ba3cf04bff1642e2bb39fbb7e5acb */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/admin.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dataList1',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'col1',name:'name1',dataType:'text'}},{T:1,N:'w2:column',A:{id:'col2',name:'name2',dataType:'text'}},{T:1,N:'w2:column',A:{id:'col3',name:'name3',dataType:'text'}}]},{T:1,N:'w2:data',A:{use:'true'},E:[{T:1,N:'w2:row'},{T:1,N:'w2:row'},{T:1,N:'w2:row'}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/common.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/swiper.min.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
scwin.onpageload = function() {
	// console.log($p.getFrame().paramObj);
	var resdata = $p.getParameter('data').res.response;
	console.log(resdata);
	var payTypeCd = $p.getParameter('data').payTypeCd;
	if(payTypeCd == '1') payTypeCd = "1개월 무료"
	else payTypeCd = "1년"
	var productData = JSON.parse(resdata.payments.custom_data);

	product_price.setValue(addComma(resdata.payments.amount));
	product_name.setValue(resdata.payments.name);
	reg_period.setValue(productData.regDate + "~" + productData.regNextDate + "(" +payTypeCd +")")
	reg_next_date.setValue(productData.regNextDate);


};

scwin.closeBtn_onclick = function(e) {
    // parentPopupID
	$p.closePopup($p.getParameter('data').parentPopupID);
	WebSquare.util.reinitialize(true);
};

function addComma (value){
	value = value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	return value;
};


}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'layer type04',id:'',style:'width:600px;'},E:[{T:1,N:'xf:group',A:{class:'comp_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnclosebox',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_close','ev:onclick':'scwin.closeBtn_onclick',id:'closeBtn',style:'',type:'button'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'닫기'}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'comp_box'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'결제완료',class:'title',tagname:'p'}},{T:1,N:'w2:textbox',A:{class:'sub_txt',id:'',label:'결제가 성공적으로 완료되었습니다.',style:'',tagname:'p'}},{T:1,N:'xf:group',A:{id:'',class:'txtbox'}},{T:1,N:'xf:group',A:{class:'box_info',id:'',tagname:'ul',style:''},E:[{T:1,N:'xf:group',A:{id:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'tit',id:'',label:'결제 금액',style:''}},{T:1,N:'w2:textbox',A:{class:'',id:'product_price',label:'',style:'',tagname:'span',dataType:'number'}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'원',style:'',tagname:'span'}}]},{T:1,N:'xf:group',A:{id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'tit',id:'',label:'상품명',style:''}},{T:1,N:'w2:textbox',A:{class:'',id:'product_name',label:'',style:'',tagname:'span'}}]},{T:1,N:'xf:group',A:{id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'tit',id:'',label:'이용기간',style:''}},{T:1,N:'w2:textbox',A:{class:'',id:'reg_period',label:'',style:'',tagname:'span'}}]},{T:1,N:'xf:group',A:{id:'',style:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{class:'tit',id:'',label:'다음 자동 결제일',style:''}},{T:1,N:'w2:textbox',A:{class:'',id:'reg_next_date',label:'',style:'',tagname:'span'}}]}]},{T:1,N:'xf:group',A:{id:'',tagname:'p'},E:[{T:1,N:'w2:textbox',A:{tagname:'span',style:'',id:'',label:'자세한 결제 내역은',class:''}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'매인화면 > 사용자 정보',style:'margin-left: 4px;',tagname:'strong'}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'에서 조회하실 수 있습니다.',style:'',tagname:'span'}}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_org_line',id:'',style:'display: none;',type:'button'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Dashboard'}]}]},{T:1,N:'xf:trigger',A:{class:'btn_org',id:'',style:'',type:'button','ev:onclick':'scwin.closeBtn_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'확인'}]}]}]},{T:1,N:'xf:group',A:{class:'notibox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'tit',id:'',label:'유의사항',style:''}},{T:1,N:'w2:textbox',A:{class:'txt bold',id:'',label:'자동결제는 1개월(1년) 단위로 구매하신 날과 매월(매년) 동일한 날짜에 결제가 이루어지며 해당 일자가 없는 경우 해당 월의 말일에 결제가 이루어집니다.',style:''}},{T:1,N:'w2:textbox',A:{class:'txt',id:'',label:'문의사항은 02-2082-1400 또는 whivehelp@inswave.com으로 연락 부탁드립니다.',style:''}}]}]}]}]}]}]}]})