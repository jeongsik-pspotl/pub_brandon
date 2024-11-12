/*amd /xml/WsUsrPricePopup2.xml 7435 112d21e9ac3f431d46456f6de408c084619bd125c359ffa181a6bb7a9cfdc105 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',A:{},E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'test/javascript',src:'https://cdn.iamport.kr/v1/iamport.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
scwin.onpageload = function() {
    btn_paypal.setAttribute('data-portone-ui-type','paypal-rt')
    scwin.btn_pg_onclick('paypal');
};

scwin.userCode = "imp06172607"; // 가맹점 식별 코드
scwin.pgType = "";
scwin.btn_pg_onclick = function(e) {
    // payTypeCd : '1' - month , '2' - year
    var payTypeCd = $p.getParameter('data').payTypeCd;
    scwin.pgType = e;

    let data = {
        "elData" : {
            "taOrderVo" :{"payTypeCd": payTypeCd},
            "pgProvider" : e,
            "teamId" : "WSharing"
        }  
    }
    executeAjax("/product/one.pwkjson", data, scwin.onSuccess) 
}

scwin.onSuccess = function(e){
    if(scwin.pgType == "toss") scwin.btn_toss_onclick(e);
    else scwin.getPaypal(e)
}

scwin.btn_toss_onclick = function(e) {
	var USER_ID = $p.getParameter('data').USER_ID;
    var customerId = "";
    if(USER_ID.indexOf('+')!=-1){
        customerId = USER_ID.split('+')[0];
    }else{
        customerId = USER_ID.split('@')[0];
    }

    var payTypeCd = $p.getParameter('data').payTypeCd;
    var elData = e.elData;

    IMP.init(scwin.userCode); // 가맹점 식별 코드
    IMP.request_pay(
        {
            pg: elData.pgProvider,
            merchant_uid: elData.taOrderVo.orderId,
            name: elData.taOrderVo.productName,
            escrow: false,
            amount: elData.taOrderVo.productPrice,
            notice_url: elData.noticeUrl + "?teamId=WSharing",
            customer_id: customerId, // customer_id는 특수문자를 포함하면 안된다..
            customer_uid: elData.taOrderVo.customerUid,
            buyer_email: USER_ID
        },
        function (rsp) { // callback
            if (rsp.success) {
                console.log(rsp)

                let data = {
                    "elData" : {
                        "taOrderVo" : {
                            "impUid": rsp.imp_uid,
                            "orderId": rsp.merchant_uid,
                            "productName": elData.taOrderVo.productName,
                            "productPrice": elData.taOrderVo.productPrice,
                            "regDate": rsp.paid_at,
                            "customerUid": elData.taOrderVo.customerUid,
                            "userId": USER_ID,
                            "payTypeCd": payTypeCd
                        },
                        "pgProvider" : scwin.pgType,
                        "teamId" : "WSharing"
                    }
                }

                executeAjax("/payment/schedule.pwkjson", data, scwin.onSuccess_toss)			
            } else {
                console.log("IMP.requestpay fail",rsp)
            }
        }
    );
};

scwin.onSuccess_toss = function(e){
    console.log('toss success', e)
    var payTypeCd = $p.getParameter('data').payTypeCd;
    // 결제 성공시
    if(e.code == 0){
        $p.closePopup($p.getFrame().paramObj.popupID);
        let options = {  
            id : "otpPopup2", 
            type : "wframePopup", 
            top: "0px",
            left: "0px",
            className: "otpPopup2",
            dataObject: {
                type: "json",
                name: "data",
                data: {
                    res : e,
                    payTypeCd : payTypeCd
                }
            }
        }; 
        
        $p.openPopup("/xml/usr/price/WsUsrPriceCompletePopup.xml",  options );
        
        let id = document.querySelector('.otpPopup2').id;
        document.querySelector(`#${id}_header`).remove();
        document.querySelector(`#${id}_content`).style.background = 'none';
        document.querySelector(`#${id}`).style.background = 'none';
        document.querySelector(`#${id}`).style.width = '100%';
        document.querySelector(`#${id}`).style.height = '100%';	
    }
}

// paypal
scwin.paypalReqData = {};
scwin.getPaypal = function(e) {
    var elData = e.elData;
    var payTypeCd = $p.getParameter('data').payTypeCd;
    var USER_ID = $p.getParameter('data').USER_ID;

    scwin.paypalReqData = {
        pg: elData.pgProvider,
        merchant_uid: elData.taOrderVo.orderId,
        name:  elData.taOrderVo.name,
        pay_method: "paypal",
        customer_uid: elData.taOrderVo.customerUid,
        notice_url : elData.noticeUrl + "?teamId=WSharing",
        currency: 'USD', //필수
        custom_data : {
            name: elData.taOrderVo.productName,
            amount: elData.taOrderVo.productPrice,
            payTypeCd :payTypeCd,
            userId :USER_ID,
            teamId : "WSharing"
        }
    };

    IMP.init(scwin.userCode); // 가맹점 식별 코드를 넣어 모듈을 초기화해주세요.
    IMP.loadUI('paypal-rt', scwin.paypalReqData, scwin.onChangeName());
}

scwin.onChangeName = function() {
    IMP.updateLoadUIRequest('paypal-rt', scwin.paypalReqData)
}

scwin.closeBtn_onclick = function(e) {
	$p.closePopup($p.getFrame().paramObj.popupID);
};

scwin.checkall_onviewchange = function(e) {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    checkboxes.forEach((checkbox) => {
        checkbox.checked = e.checked
    })
};
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'layer type04',id:'',style:'width:500px;'},E:[{T:1,N:'xf:group',A:{class:'btnclosebox',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_close',id:'closeBtn',style:'',type:'button','ev:onclick':'scwin.closeBtn_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'닫기'}]}]}]},{T:1,N:'w2:textbox',A:{class:'pop_tit',id:'',label:'결제 수단',style:''}},{T:1,N:'xf:group',A:{class:'btn_pay',id:'',style:''},E:[{T:1,N:'w2:anchor',A:{class:'toss',id:'btn_toss',outerDiv:'false',style:'','ev:onclick':'scwin.btn_pg_onclick(\'toss\')'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'토스페이'}]}]},{T:1,N:'w2:anchor',A:{class:'paypal portone-ui-container',id:'btn_paypal',outerDiv:'false',style:'','ev:onclick':'scwin.btn_pg_onclick(\'paypal\')'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'페이팔'}]}]}]},{T:1,N:'xf:group',A:{class:'form_box',id:'',style:''}},{T:1,N:'xf:group',A:{class:'pop_footer',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'logo',id:'',label:'(주)인스웨이브시스템즈',style:''}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>Tel : 02-2082-1400',style:'',escape:'false'}}]}]}]}]}]})