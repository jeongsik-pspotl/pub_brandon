/*amd /xml/WsUsrPricePopup.xml 15126 7f85473861b9512c08297dee6ff3d724b2b6d0c9f89132b9e9ac9662d5c10161 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/admin.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',A:{},E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/common.js'}},{T:1,N:'script',A:{type:'test/javascript',src:'https://cdn.iamport.kr/v1/iamport.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.productSeq = 0;
scwin.onpageload = function() {
    var payTypeCd = $p.getParameter('data').payTypeCd;
    if(payTypeCd == '1') product_plan.setValue("(매월)")
    else product_plan.setValue("(매년)")
    
    // 상품정보 얻기 위한 함수 호출
    scwin.btn_pg_onclick();
};

scwin.pgType = "";
scwin.pgMonthOrDay = "";
scwin.btn_pg_onclick = function(e) {

	let data = {
		"elData" : {
			"teamId" : "WHive"
		}
	};

	executeAjaxToken("/product/merchant/find/now.pwkjson", data, scwin.productList);

};

scwin.productList = function(e){
	// 각 상품의 seq : e.payProductVoList.productSeq
    let jsonResult =  e.response;

	var payTypeCd = $p.getParameter('data').payTypeCd;

	for(var i = 0; i < jsonResult.length; i++){

		// if(jsonResult[i].productPeriod == "1" && payTypeCd == "1"){
        if(jsonResult[i].productPeriod == "30" && payTypeCd == "1"){
			price.setValue(addComma(jsonResult[i].priceKRW));
			product_name.setValue(jsonResult[i].productName);
			scwin.pgMonthOrDay = "day";
			scwin.productSeq =  jsonResult[i].productSeq;

		}else if(jsonResult[i].productPeriod == "12" && payTypeCd == "2"){
			price.setValue(addComma(jsonResult[i].priceKRW));
			product_name.setValue(jsonResult[i].productName);
			scwin.pgMonthOrDay = "month";
			scwin.productSeq =  jsonResult[i].productSeq;

		}
	}


};

scwin.prodOneOnclick = function(e){

	const checkboxes = document.querySelectorAll('input[type="checkbox"]');
	let num = 0;

	checkboxes.forEach((checkbox) => {
		if(checkbox.checked) num ++;
	})
	if(checkboxes.length -1 > num) {
		openPopup({
			type: 'confirm',
			title:'Information',
			message:'이용약관 동의를 해주세요.',
			callback: () => {
				$p.$(".loadingBox").css("display","block");
			},
			loadCallback: (popup) => {
				popup.querySelector('.alertbox').style.zIndex = 6101;
				popup.querySelector('.dim').style.zIndex = 6100;
			}
		})
		return false;

	}

	let data = {
		"elData" : {
				"teamId":"WHive",
				"productSeq": parseInt(scwin.productSeq ),
				"pgProvider" : 'toss'
				}
		}

	executeAjaxToken("/payment/prepare.pwkjson", data, scwin.onSuccess);
};

scwin.onSuccess = function(e){
	// 상품조회 성공 후처리 추가 (결제창 연결)
	scwin.btn_toss_onclick(e);
}

			scwin.btn_toss_onclick = function(e) {
				var USER_ID = $p.getParameter('data').USER_ID;
				var EMAIL = $p.getParameter('data').EMAIL;
				var payTypeCd = $p.getParameter('data').payTypeCd;
				var jsonResponse = e.response;
				IMP.init('imp67046332'); // 가맹점 식별 코드
				IMP.request_pay(
						{
							// ------data 예시--------
							// pg: 'tosspayments.iamporttest_4',
							// merchant_uid: '20230818016896290431',
							// name: '월간 정기결제',
							// escrow: false,
							// amount: 89000,
							// notice_url: 'http://43.201.134.221/notice/first/toss.do',
							// customer_id: 'customer_id',
							// customer_uid: 'QJ3bg5hauS',
							// buyer_email: 'test@inswave.com'
							// ----------------------




							// pg: elData.pgProvider, // pg사.상점mid
							// merchant_uid: elData.payOrderVo.orderId, // 주문번호 - pk
							// name: elData.payOrderVo.productName, // 상품명
							// escrow: false, // 고정값
							// amount: elData.payOrderVo.productPrice, // 상품가격
							// notice_url: elData.noticeUrl, // 웹훅 url
							// customer_id: 'pspotl87', // 사용자 아이디 - 특수문자 포함하면 안됨
							// customer_uid: elData.payOrderVo.customerUid, // 빌링키 - pk, 정기결제시 필수값이다.
							// buyer_email: "pspotl87@inswave.com" // 사용자 이메일 주소

							pg: jsonResponse.pgProvider, // pg사.상점mid
							merchant_uid: jsonResponse.orderId, // 주문번호 - pk
							name: jsonResponse.productName, // 상품명
							escrow: false, // 고정값
							amount: jsonResponse.productPrice, // 상품가격
							notice_url: jsonResponse.noticeUrl + "?teamId=WHive", // 웹훅 url
							customer_id: USER_ID, // 사용자 아이디 - 특수문자 포함하면 안됨
							customer_uid: jsonResponse.customerUid, // 빌링키 - pk, 정기결제시 필수값이다.
							buyer_email: EMAIL, // 사용자 이메일 주소,
							currency : "KRW",
							m_redirect_url: g_config.HTTPSERVER_URL

						},
						function (rsp) { // callback
							if (rsp.success) {
								// 빌링키 발급성공
								// console.log(rsp);

								let data = {
									"elData" : {
											"teamId" : "WHive",
											"impUid": rsp.imp_uid, // 포트원 imp_uid
											"customerUid": jsonResponse.customerUid, // 빌링키
											"productSeq": scwin.productSeq ,
											"productPeriod": jsonResponse.productPeriod, // 상품개월수
											"productPeriodUnit":scwin.pgMonthOrDay,
											"productPrice": jsonResponse.priceKRW, // 상품가격
											// "orderId": rsp.merchant_uid, // 주문번호
											"productName": jsonResponse.productName, // 상품명
											// "regDate": rsp.paid_at, // 결제시각
											"userId": EMAIL, // 사용자 이메일 주소
											// "payTypeCd": payTypeCd, // 상품분류값 (1개월 '1' or 1년 '2),
											"currency" : "KRW",
											"pgProvider" : "toss" // pg사 string값 ('toss' or 'paypal')
									}
								}


								executeAjaxToken("/payment/subscribe/payment.pwkjson", data, scwin.onSuccess_toss)
							} else {
								console.log("IMP.requestpay fail",rsp)
							}
						}
				);
			};

			scwin.onSuccess_toss = async function(e){
				console.log('toss success', e);
				let success_toss_data = e.response;

				var payTypeCd = $p.getParameter('data').payTypeCd;

				if(e.code == 0){
					// e.code 0일 때 success
					let pricing_in_json = {};

					pricing_in_json.order_id = success_toss_data.payments.merchant_uid;
					pricing_in_json.customer_uid = success_toss_data.payments.customer_uid;
					pricing_in_json.imp_uid = success_toss_data.payments.imp_uid;
					pricing_in_json.pay_type_cd = payTypeCd;

					const url = common.uri.setUserInfoPricingCreate;
					const method = "POST";
					const headers = {"Content-Type": "application/json; charset=utf-8"};

					await common.http.fetch(url, method, headers, pricing_in_json)
							.then(res => {
								if(Array.isArray(res)) {
									if (res != null && res[0].result === "success") {
										let options = {
											id : "otpPopup2",
											type : "wframePopup",
											// width: "400px",
											// height: "500px",
											top: "0px",
											left: "0px",
											className: "otpPopup2",
											dataObject: {
												type: "json",
												name: "data",
												data: {
													res : e,
													payTypeCd : payTypeCd,
													parentPopupID : $p.getFrame().paramObj.popupID
												}
											}
										};

										$p.openPopup("/pricing/WsUsrPriceCompletePopup.xml",  options );
										let id = document.querySelector('.otpPopup2').id;
										document.querySelector(`#${id}_header`).remove();
										document.querySelector(`#${id}_content`).style.background = 'none';
										document.querySelector(`#${id}`).style.background = 'none';
										document.querySelector(`#${id}`).style.width = '100%';
										document.querySelector(`#${id}`).style.height = '100%';

									} else {

									}

								}
							})
							.catch(() => {

							});


				}else {

				}
			};

scwin.closeBtn_onclick = function(e) {
	$p.closePopup($p.getFrame().paramObj.popupID);
};

scwin.btn_next_onclick = function(e) {
    var payTypeCd = $p.getParameter('data').payTypeCd;
    var USER_ID = $p.getParameter('data').USER_ID;

    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    let num = 0;
    
    checkboxes.forEach((checkbox) => {
        if(checkbox.checked) num ++;
    })

    if(checkboxes.length -1 > num) {
        confirm("이용약관 동의를 해주세요.");
        // openPopup({
        //     type: 'confirm',
        //     title:'Information',
        //     message:'이용약관 동의를 해주세요.',
        //     callback: () => {
        //         $p.$(".loadingBox").css("display","block");
        //     },
        //     loadCallback: (popup) => {
        //         popup.querySelector('.alertbox').style.zIndex = 6101;
        //         popup.querySelector('.dim').style.zIndex = 6100;
        //     }
        // })
        return false;
    }

    $p.closePopup($p.getFrame().paramObj.popupID);
    let options = {  
        id : "otpPopup", 
        type : "wframePopup", 
        top: "0px",
        left: "0px",
        className: "otpPopup",
        dataObject: {
            type: "json",
            name: "data",
            data: {
                USER_ID : USER_ID,
                payTypeCd : payTypeCd
            }
        }
    }; 
    
    $p.openPopup("/xml/usr/price/WsUsrPricePopup2.xml",  options );
    
    let id = document.querySelector('.otpPopup').id;
    document.querySelector(`#${id}_header`).remove();
    document.querySelector(`#${id}_content`).style.background = 'none';
    document.querySelector(`#${id}`).style.background = 'none';
    document.querySelector(`#${id}`).style.width = '100%';
    document.querySelector(`#${id}`).style.height = '100%';	
};

scwin.checkall_onviewchange = function(e) {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    checkboxes.forEach((checkbox) => {
        checkbox.checked = e.checked
    })
};

scwin.term_popup_onclick = function(e) {
    var options = {  
	    id : "privatyPopup", 
	    type : "wframePopup", 
	    width: "400px", 
	    height: "500px",
	    top: "0px",
	    left: "0px",
	    className: "privatyPopup",
        dataObject: {
            type: "json",
            name: "data",
            data: {
                e : e
            }
        }
	}; 
	$p.openPopup("/pricing/WsUsrRegPrivatyPolicy.xml",  options );
	
	var id = document.querySelector('.privatyPopup').id;
    document.querySelector(`#${id}_header`).remove();
    document.querySelector(`#${id}_content`).style.background = 'none';
    document.querySelector(`#${id}`).style.background = 'none';
    document.querySelector(`#${id}`).style.width = '100%';
    document.querySelector(`#${id}`).style.height = '100%';
    document.querySelector(`#${id}_body`).style.top = '0';
    id = '';
};

			function addComma (value){
				value = value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
				return value;
			};



}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'layer type04',id:'',style:'width:500px;'},E:[{T:1,N:'xf:group',A:{class:'btnclosebox',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_close',id:'closeBtn',style:'',type:'button','ev:onclick':'scwin.closeBtn_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'닫기'}]}]}]},{T:1,N:'w2:textbox',A:{class:'pop_tit',id:'',label:'결제',style:''}},{T:1,N:'xf:group',A:{id:'',class:'box_info',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'textbox1',label:'결제 금액',class:'tit','ev:onclick':'scwin.textbox1_onclick'}},{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'w2:textbox',A:{style:'',id:'price',label:'',class:'num',tagname:'span'}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'원',style:'',tagname:'span'}},{T:1,N:'w2:textbox',A:{class:'sub',id:'product_plan',label:'',style:'',tagname:'span'}}]}]},{T:1,N:'xf:group',A:{id:'',tagname:'li',style:''},E:[{T:1,N:'w2:textbox',A:{class:'tit',id:'',label:'상품명',style:''}},{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'product_name',label:'',style:'',tagname:'span'}}]}]}]},{T:1,N:'w2:textbox',A:{class:'tit_sub',id:'',label:'이용약관',style:''}},{T:1,N:'xf:group',A:{id:'',class:'box_agree'},E:[{T:1,N:'xf:select',A:{ref:'',appearance:'full',style:'',id:'checkall',rows:'',selectedindex:'-1',class:'allchk',cols:'',renderType:'checkboxgroup','ev:onchange':'scwin.checkall_onchange','ev:onviewchange':'scwin.checkall_onviewchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'이용약관에 전체 동의합니다.'}]},{T:1,N:'xf:value'}]}]}]},{T:1,N:'xf:group',A:{class:'grp_agree',id:'',style:''},E:[{T:1,N:'xf:select',A:{appearance:'full',class:'',cols:'',id:'allagreeCheckid',ref:'',rows:'',selectedindex:'-1',style:'',renderType:'checkboxgroup'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'개인정보처리방침 동의'}]},{T:1,N:'xf:value'}]}]}]},{T:1,N:'w2:anchor',A:{class:'detail',id:'privacy_popup',outerDiv:'false',style:'','ev:onclick':'scwin.term_popup_onclick(\'1\')'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'내용보기'}]}]}]},{T:1,N:'xf:group',A:{class:'grp_agree',id:'',style:''},E:[{T:1,N:'xf:select',A:{appearance:'full',class:'',cols:'',id:'',ref:'',rows:'',selectedindex:'-1',style:'',renderType:'checkboxgroup'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'이용약관 동의'}]},{T:1,N:'xf:value'}]}]}]},{T:1,N:'w2:anchor',A:{class:'detail',id:'policy_popoup',outerDiv:'false',style:'','ev:onclick':'scwin.term_popup_onclick(\'2\')'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'내용보기'}]}]}]}]},{T:1,N:'xf:group',A:{class:'form_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',class:'btnbox',style:''},E:[{T:1,N:'xf:trigger',A:{style:'',id:'',type:'button',class:'btn_org_line','ev:onclick':'scwin.closeBtn_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'취소'}]}]},{T:1,N:'xf:trigger',A:{class:'btn_org',id:'btn_next',style:'width: 140px;',type:'button','ev:onclick':'scwin.prodOneOnclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'결제'}]}]}]}]},{T:1,N:'xf:group',A:{class:'pop_footer',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'logo',id:'',label:'(주)인스웨이브시스템즈',style:''}},{T:1,N:'w2:textbox',A:{class:'',id:'',label:'서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>Tel : 02-2082-1400',style:'',escape:'false'}}]}]}]}]}]})