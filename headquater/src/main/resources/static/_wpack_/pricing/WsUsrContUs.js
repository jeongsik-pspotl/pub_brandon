/*amd /cm/pricing/WsUsrContUs.xml 9203 c12e80d29175eb1ae2c49d0d42aa996376fb0dde528183fc72450b8cfce8f1e1 */
define({E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataMap',A:{baseNode:'map',id:'contactUsMap'},E:[{T:1,N:'w2:keyInfo',E:[{T:1,N:'w2:key',A:{id:'EMAIL',name:'name1',dataType:'text'}},{T:1,N:'w2:key',A:{id:'NAME',name:'name2',dataType:'text'}},{T:1,N:'w2:key',A:{id:'PHONE',name:'name3',dataType:'text'}},{T:1,N:'w2:key',A:{id:'CONTENT',name:'name4',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{type:'text/javascript',src:'/js/swiper.min.js'}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){var data = {
            type: 'alert',
            title: 'CONTACT US',
            message: ''
        }

        scwin.onpageload = function() {
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
        };

        function hideScrollBtn(obj){
            obj.css("display", "none");
        }
        scwin.contUsCancel_onclick = function(e) {
            $p.closePopup($p.getFrame().paramObj.popupID);
        };

        scwin.popupCloseBtn_onclick = function(e) {
            $p.closePopup($p.getFrame().paramObj.popupID);
        };

        scwin.contUsRegist_onclick = function(e) {
            var inputEmail = document.getElementById(contactUsEmail.getID()).value;
            var inputPhone = document.getElementById(contactUsPhone.getID()).value;
            var emailRegex = /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
            var phoneRegex = /^\+[0-9]{1,3}(?:[- ]?[0-9]){10,15}$/;

            var inputEmailResult = contactUsEmail.validate();
            var inputNameResult = contactUsName.validate();
            var inputPhoneResult = contactUsPhone.validate();
            var inputContentResult = contactUsContent.validate();

            data.loadCallback = function() {
                document.querySelector('.alertbox').style.zIndex = 6101;
                document.querySelector('.alertbox').nextSibling.style.zIndex = 6100;
                isDualPopup = true;
            }

            if(!inputEmailResult){
                data.message = 'Please Fill Out Your E-mail';
                common.win.alert(data.message);
                contactUsEmail.focus();
                return;
            }

            if(!emailRegex.test(inputEmail)){
                data.message = 'Please Check Your E-mail Format\n(ex: your@example.com)';
                common.win.alert(data.message);
                contactUsEmail.focus();
                return;
            }

            if(!inputNameResult){
                data.message = 'Please Fill Out Your Name';
                common.win.alert(data.message);
                contactUsName.focus();
                return;
            }

            if(!inputPhoneResult){
                data.message = 'Please Fill Out Your Phone';
                common.win.alert(data.message);
                contactUsPhone.focus();
                return;
            }

            if(!phoneRegex.test(inputPhone)){
                data.message = 'Please Check Your Phone Format\n(ex: +XX XXX XXXX XXXX)';
                common.win.alert(data.message);
                contactUsPhone.focus();
                return;
            }


            if(!inputContentResult){
                data.message = 'Please Fill Out Content';
                common.win.alert(data.message);
                contactUsContent.focus;
                return;
            }

            let contactUsData = {};
            contactUsData.name = contactUsName.realValue;
            contactUsData.email = contactUsEmail.realValue;
            contactUsData.phone = contactUsPhone.realValue;
            contactUsData.content = contactUsContent.realValue;

            const url = common.uri.contactUs;
            const method = "POST";
            const headers = {"Content-Type": "application/json; charset=utf-8"};

            common.http.fetch(url, method, headers, contactUsData)
                    .then(res => {
                        if (res.result == "success") {
                            common.win.alert("문의가 접수되었습니다.");
                        } else {
                            common.win.alert("문의 접수에 실패했습니다.");
                        }
                    })

        };

            
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'layer pop_price',id:'',style:'width:810px;'},E:[{T:1,N:'xf:group',A:{class:'btnclosebox',id:'',style:'top: 40px;right: 35px;'},E:[{T:1,N:'xf:trigger',A:{class:'btn_close_sm',id:'popupCloseBtn',style:'',type:'button','ev:onclick':'scwin.popupCloseBtn_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'닫기'}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'pop_price_cont'},E:[{T:1,N:'xf:group',A:{id:'',class:'custombox'},E:[{T:1,N:'xf:group',A:{id:'',class:'custom_tit'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'ENTERPRISE',class:'df_tit'}},{T:1,N:'w2:textbox',A:{style:'',id:'',label:'Custom Pricing',class:'df_tit'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'custom_info'},E:[{T:1,N:'xf:group',A:{tagname:'ul',style:'',id:'',class:''},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:''},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'Sales',class:'',escape:'false'}},{T:1,N:'w2:textbox',A:{style:'',id:'',label:'+82 02-2082-1400',class:'',escape:'false'}}]},{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:''},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'E-mail',class:'',escape:'false'}},{T:1,N:'w2:textbox',A:{style:'',id:'',label:'whivehelp@inswave.com',class:'',escape:'false'}}]}]},{T:1,N:'w2:textbox',A:{style:'',id:'',label:'Contact us for a consulatation.',class:'',escape:'false'}}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'contactbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'dfbox'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'',label:'Contact US',class:'df_tit'}}]},{T:1,N:'xf:group',A:{class:'form_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'txt_tit',id:'',label:'Your Email',style:''}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'contactUsEmail',placeholder:'your@example.com',style:'',ref:'data:contactUsMap.EMAIL',mandatory:'true',maxlength:'100'}},{T:1,N:'w2:textbox',A:{class:'txt_tit',id:'',label:'Your Name',style:''}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'contactUsName',placeholder:'your name',style:'',ref:'data:contactUsMap.NAME',mandatory:'true',maxlength:'100'}},{T:1,N:'w2:textbox',A:{class:'txt_tit',id:'',label:'Your Phone Number',style:''}},{T:1,N:'xf:input',A:{adjustMaxLength:'false',id:'contactUsPhone',placeholder:'+XX XXX XXXX XXXX',style:'',ref:'data:contactUsMap.PHONE',mandatory:'true',maxlength:'30'}},{T:1,N:'w2:textbox',A:{class:'txt_tit',id:'',label:'Content',style:''}},{T:1,N:'xf:textarea',A:{id:'contactUsContent',style:'',ref:'data:contactUsMap.CONTENT',mandatory:'true',maxlength:'2000'}}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'form_box'},E:[{T:1,N:'xf:group',A:{id:'',class:'fr'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'btnbox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'contUsCancel',type:'button',class:'btn_org_line','ev:onclick':'scwin.contUsCancel_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Cancel'}]}]},{T:1,N:'xf:trigger',A:{style:'',id:'contUsRegist',type:'button',class:'btn_org','ev:onclick':'scwin.contUsRegist_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Regist'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'dim on',id:'',style:''}},{T:1,N:'xf:group',A:{class:'loadingBox',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'spinner',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'',id:'',style:''}},{T:1,N:'xf:group',A:{class:'rect2',id:'',style:''}},{T:1,N:'xf:group',A:{class:'rect3',id:'',style:''}},{T:1,N:'xf:group',A:{class:'rect4',id:'',style:''}},{T:1,N:'xf:group',A:{class:'rect5',id:'',style:''}}]}]}]}]}]})