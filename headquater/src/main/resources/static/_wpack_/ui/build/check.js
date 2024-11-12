/*amd /ui/build/check.xml 4389 4ed9700cf0bdcc02c161a8d51f63020f9e0ce9b8ef025baa75e2b4fcf9a0498a */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'meta',A:{name:'viewport',content:'width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'}},{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
		scwin.onpageload = function(){
			scwin.qrcodeid = $p.getParameter("qrcodeID");
			const userID = $p.getParameter("userID");
			$p.getComponentById("ipt_userID").setValue(userID);
		};
		
		scwin.reLogin = function(){
			const id = $p.getComponentById("ipt_userID").getValue();
			const pw = $p.getComponentById("ipt_userPW").getValue();

			const params = {
				user_login_id: id,
				password: pw
			};

			common.http.fetchPost("/manager/member/qrcodeAuthCheckDetail",{"Content-Type":"application/json"},params).then((res)=>{
				return res.json();
			}).then((data)=>{
				 if(data[0].auth_check_result){
					// install url 요청
					 common.http.fetchGet("/builder/build/history/getInstallUrl/"+scwin.qrcodeid,"GET",{"Content-Type":"application/json"}).then((res)=>{
						 return res.json();
					 }).then((data)=>{
						 if(data!=null && Array.isArray(data)){
							 if(navigator.userAgent.includes('Mac')){
								location.href="itms-services://?action=download-manifest&url="+data[0].url;
							 } else {
								 location.href=data[0].url;
							 }
						 }
					 }).catch((err)=>{
						 common.win.alert("error status:"+err.status+", message:"+err.message);
					 });
				 }
			}).catch((err)=>{
				common.win.alert("로그인 오류: error status "+err.status);
			});
		};
		
			
scwin.btnLogin_onclick = function () {
	scwin.reLogin();
};

}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_main_wrap whive',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'deco1',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'ani1',id:'',style:''}},{T:1,N:'xf:group',A:{class:'ani2',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_container',adaptive:'layout',adaptiveThreshold:'1024'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_contents',adaptive:'layout',adaptiveThreshold:'1024',tagname:''},E:[{T:1,N:'xf:group',A:{class:'login_header',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'logo',id:'',style:'',tabIndex:'',tagname:'h1'},E:[{T:1,N:'w2:anchor',A:{id:'',localeRef:'CMMN_LOGIN_LABEL_00002',outerDiv:'false',style:'',tabIndex:'',toolTip:'',userData2:'',userData3:''},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'login_info'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'login_item user'},E:[{T:1,N:'w2:textbox',A:{label:'Your email',style:'',id:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_id'}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'ipt_userID',style:'',class:'cont_user',customModelFormatter:'',type:'',useMonthYearFormat:'',useVerticalAlign:'',dataType:'',displayFormatter:'',disabled:'',applyFormat:'',autocomplete:'',autoFocus:'',customModelUnformatter:''}},{T:1,N:'xf:trigger',A:{type:'anchor',style:'',id:'',disabled:'',class:'btn_login_close hidden',centerOffImageClass:'',anchorWithGroupClass:'',rightOffImageClass:'',leftOnImageClass:''},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'login_item pwd',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',label:'Password',style:'',tagname:'span',tooltipDisplay:'',useLocale:'true',localeRef:'lbl_password'}},{T:1,N:'xf:secret',A:{id:'ipt_userPW',style:'',class:'cont_pwd',ref:'',nextTabID:'','ev:onkeypress':'scwin.btn_login_onkeypress'}},{T:1,N:'xf:trigger',A:{anchorWithGroupClass:'',centerOffImageClass:'',class:'btn_login_pw',disabled:'',id:'',leftOnImageClass:'',rightOffImageClass:'',style:'',type:'button','ev:onkeypress':'scwin.btn_login_onkeypress'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'btnLogin',class:'btn_login',href:'',useLocale:'true',localeRef:'lbl_login','ev:onclick':'scwin.btnLogin_onclick'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'deco2',id:'',style:''}}]}]}]}]}]}]})