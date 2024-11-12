/*amd /cm/pricing/WsUsrPrice.xml 12200 ed4fecf7cfd7b9abc10d623310dc2aa205f0d6cb583b039659f1132f57f7e045 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/admin.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/base.css" type="text/css"'},{T:7,N:'xml-stylesheet',instruction:'href="/pricing/css/contents.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{baseNode:'list',repeatNode:'map',id:'dataList1',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'col1',name:'name1',dataType:'text'}},{T:1,N:'w2:column',A:{id:'col2',name:'name2',dataType:'text'}},{T:1,N:'w2:column',A:{id:'col3',name:'name3',dataType:'text'}}]},{T:1,N:'w2:data',A:{use:'true'},E:[{T:1,N:'w2:row'},{T:1,N:'w2:row'},{T:1,N:'w2:row'}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/swiper.min.js'}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/config.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){scwin.onpageload = function() {
			scwin.USER_ID = "";
    var menuButton = document.querySelector('.btn_scrolling');
    var topMove = function () {
        swiper.slideTo(1, 1000, true);
    };

    var swiper = new Swiper('.swiper-container', {
        direction: 'vertical',
        speed: 900,
        slidesPerView: 1,
        spaceBetween: 0,
        mousewheel: false,
        on: {
            touchStart: function () {
                menuButton.addEventListener('click', topMove, true);
            },
            slideChangeTransitionStart: function () { 
                if(swiper.activeIndex > 0){ 
                    menuButton.classList.add('off');
                    menuButton.classList.remove('on');
                }else{
                    menuButton.classList.add('on');
                    menuButton.classList.remove('off');
                }
            }
        }
    });

			scwin.getUserInfo();
    // $p.ajax({
	// 	action:'/manager/member/search/userInfo',
	// 	method:'GET',
	// 	mediatype:'application/json',
	// 	mode:"synchronous",
	// 	success:function(e){
    //
	// 		var data = e.responseJSON;
    //
	// 		if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
	// 			if (data != null) {
    //
	// 				btn_createAccount.hide();
	// 				btn_createAccount2.hide();
	// 				btn_monthly.show();
	// 				btn_yearly.show();
	// 				btn_free.show()
	// 				scwin.USER_ID = data.user_login_id;
    //                 scwin.EMAIL = data.email;
    //
	// 				return true;
	// 			}
    //
	// 		}
	// 	},
	// 	error: function(e){ console.error(e);}
	// });

};

		scwin.getUserInfo = async function () {
			const url = common.uri.userInfo;
			const method = "GET";
			const headers = {"Content-Type": "application/json"};

			try {
				const response = await common.http.fetchGet(url, method, headers, {});
				const data = await response.json();

				if (response.status >= 200 && response.status < 300) {
					btn_createAccount.hide();
					btn_createAccount2.hide();
					btn_free.show()
					btn_free.show()

                    if(data.pay_change_yn == "Y"){
						btn_pricing_using.show();
						btn_monthly.hide();
						btn_yearly.hide();
					}else if(data.pay_change_yn == "N"){

						btn_pricing_using.hide();
						btn_monthly.show();
						btn_yearly.show();
					}


					scwin.USER_ID = data.user_login_id;
					scwin.EMAIL = data.email;

				}

			}catch (err){

			}

		};

function hideScrollBtn(obj){
    obj.css("display", "none");
}
scwin.contactUs_onclick = function(e) {
	var options = {
		id : "contUsPopup",
		type : "wframePopup",
		width: "1000px",
		height: "600px",
		top: "0px",
		left: "0px",
		className: "contUsPopup"
	};
	$p.openPopup("/pricing/WsUsrContUs.xml",  options );

    var id = document.querySelector('.contUsPopup').id;
    document.querySelector(`#${id}_header`).remove();
    document.querySelector(`#${id}_content`).style.background = 'none';
    document.querySelector(`#${id}`).style.background = 'none';
    document.querySelector(`#${id}`).style.width = '100%';
    document.querySelector(`#${id}`).style.height = '100%';
    document.querySelector(`#${id}_body`).style.top = '0';
};

scwin.btn_createAccount_onclick = function(e) {

	top.location.href = g_config.HTTPSERVER_URL+"/websquare/websquare.html?w2xPath=/login.xml";

};

scwin.btn_price_onclick = function(e){
    var options = {
        id : "regPopup",
        type : "wframePopup",
        width: "400px",
        height: "500px",
        top: "0px",
        left: "0px",
        className: "regPopup",
        dataObject: {
            type: "json",
            name: "data",
            data: {
                USER_ID : scwin.USER_ID,
				EMAIL : scwin.EMAIL,
                payTypeCd : e
            }
        }
    };
	/**
	 * 2023.11.29 pg 사 url 변경 완료 이후 주석 해제
	 */
	// common.win.alert("유료 결제는 추후 오픈 예정");

	$p.openPopup("/pricing/WsUsrPricePopup.xml",  options );
    var id = document.querySelector('.regPopup').id;
    document.querySelector(`#${id}_header`).remove();
    document.querySelector(`#${id}_content`).style.background = 'none';
    document.querySelector(`#${id}`).style.background = 'none';
    document.querySelector(`#${id}`).style.width = '100%';
    document.querySelector(`#${id}`).style.height = '100%';
    document.querySelector(`#${id}_body`).style.top = '0';

    id = '';
}

scwin.btn_free_onclick = function(e) {
	top.location.href = g_config.HTTPSERVER_URL+"/websquare/websquare.html?w2xPath=/index.xml";
};

}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'page',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'sub_contents w_feature',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'info_cont banner_cont',id:''},E:[{T:1,N:'xf:group',A:{class:'center_area',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'sub_visualbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'txt1',escape:'false',id:'',label:'Pricing',style:''}},{T:1,N:'w2:textbox',A:{class:'txt2',escape:'false',id:'',label:'합리적인 가격의 W-Hive',style:'font-weight:normal;'}},{T:1,N:'w2:textbox',A:{class:'txt3',escape:'false',id:'',label:'합리적인 가격의 W-Hive을 만나보세요. 기업, 회사 및 가격 상담을 원하시면 저희에게 언제든 문의주세요.',style:''}}]},{T:1,N:'xf:group',A:{class:'banner_wrap',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'bannerbox type3',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'banner_top',id:''},E:[{T:1,N:'xf:group',A:{class:'banner_tit',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'tit',escape:'false',id:'',label:'Free',style:''}}]},{T:1,N:'xf:group',A:{class:'banner_price',id:''},E:[{T:1,N:'xf:group',A:{class:'price_info',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'price',escape:'false',id:'',label:'0',style:''}},{T:1,N:'w2:textbox',A:{class:'won',escape:'false',id:'',label:'원',style:''}}]},{T:1,N:'w2:textbox',A:{class:'price',escape:'false',id:'',label:'!~SCR_WsUsrPrice_6~!',style:'display: none;'}}]},{T:1,N:'xf:group',A:{class:'banner_btn',id:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_step',id:'btn_createAccount',style:'',type:'button','ev:onclick':'scwin.btn_createAccount_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Create Account'}]}]},{T:1,N:'xf:trigger',A:{class:'btn_step','ev:onclick':'scwin.btn_free_onclick',id:'btn_free',style:'display: none;',type:'button'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Use Now'}]}]}]}]},{T:1,N:'xf:group',A:{class:'banner_btm',id:''},E:[{T:1,N:'xf:group',A:{class:'banner_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'App 빌드',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'내부 배포서버 지원',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'공유 Builder',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'Android 1개 App ID',style:'',tagname:'li'}}]}]}]},{T:1,N:'xf:group',A:{class:'bannerbox type3',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'banner_top',id:''},E:[{T:1,N:'xf:group',A:{class:'banner_tit',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'tit',escape:'false',id:'',label:'PROFESSIONAL',style:''}}]},{T:1,N:'xf:group',A:{class:'banner_price',id:''},E:[{T:1,N:'xf:group',A:{class:'price_info',id:''},E:[{T:1,N:'w2:textbox',A:{class:'price',escape:'false',id:'',label:'300,000',style:''}},{T:1,N:'w2:textbox',A:{class:'won',escape:'false',id:'',label:'원',style:''}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'/ 1개월',style:''}}]},{T:1,N:'w2:textbox',A:{class:'price_sale',escape:'false',id:'',label:'12개월 구매시 17%할인',style:''}},{T:1,N:'xf:group',A:{class:'price_info',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'price',escape:'false',id:'',label:'3,000,000',style:''}},{T:1,N:'w2:textbox',A:{class:'won',escape:'false',id:'',label:'원',style:''}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'/ 12개월',style:''}}]}]},{T:1,N:'xf:group',A:{class:'banner_btn',id:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_step',id:'btn_createAccount2',style:'',type:'button','ev:onclick':'scwin.btn_createAccount_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Create Account'}]}]},{T:1,N:'xf:trigger',A:{class:'btn_step',id:'btn_monthly',style:'display:none;',type:'button','ev:onclick':'scwin.btn_price_onclick(\'1\')'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'1개월 무료'}]}]},{T:1,N:'xf:trigger',A:{class:'btn_step',id:'btn_yearly',style:'display:none;',type:'button','ev:onclick':'scwin.btn_price_onclick(\'2\')'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'12개월 신청'}]}]},{T:1,N:'xf:trigger',A:{class:'btn_step','ev:onclick':'scwin.btn_free_onclick',id:'btn_pricing_using',style:'display: none;',type:'button'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Use Now'}]}]}]}]},{T:1,N:'xf:group',A:{class:'banner_btm',id:''},E:[{T:1,N:'xf:group',A:{class:'banner_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'w2:textbox',A:{class:'pt',escape:'false',id:'',label:'+ 모든 FREE 기능',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'Android, iOS 4개 App ID',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'스토어배포',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'외부 VCS 지원',style:'',tagname:'li'}}]}]}]},{T:1,N:'xf:group',A:{class:'bannerbox type3',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'banner_top',id:''},E:[{T:1,N:'xf:group',A:{class:'banner_tit',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'tit',escape:'false',id:'',label:'Enterprise',style:''}}]},{T:1,N:'xf:group',A:{class:'banner_price',id:''},E:[{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'w2:textbox',A:{class:'price',escape:'false',id:'',label:'문의하기',style:''}}]}]},{T:1,N:'xf:group',A:{class:'banner_btn',id:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_step',id:'contactUs',style:'',type:'button','ev:onclick':'scwin.contactUs_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Contact US'}]}]}]}]},{T:1,N:'xf:group',A:{class:'banner_btm',id:''},E:[{T:1,N:'xf:group',A:{class:'banner_list',id:'',style:'',tagname:'ul'},E:[{T:1,N:'w2:textbox',A:{class:'pt',escape:'false',id:'',label:'+ 모든 PROFESSIONAL 기능',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'전용 Builder',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'Android, iOS 무제한 App ID',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'외부 배포서버 지원',style:'',tagname:'li'}},{T:1,N:'w2:textbox',A:{class:'',escape:'false',id:'',label:'운영 지원',style:'',tagname:'li'}}]}]}]}]}]}]}]}]}]}]}]})