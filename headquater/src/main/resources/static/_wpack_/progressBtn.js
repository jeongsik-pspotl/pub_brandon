/*amd /xml/progressBtn.xml 3133 1e8a1d90a5f93190ff23a07b5993f7a708767133621f037696c7285297fdd6bd */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:7,N:'xml-stylesheet',instruction:'href="/cm/css/btn.css" type="text/css"'},{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){

        	/**
        		참고사이트
        		https://codepen.io/teamturret/pen/KwyVQx

        		dom구조가 복잡하여 보류
        		https://tympanus.net/Development/ProgressButtonStyles/
        	*/

			scwin.onpageload = function() {
	
			};
		
			scwin.btn1_onclick = function(e) {
                var _this = this;
			    if(!(_this.hasClass("active"))){
                    _this.addClass("active");

			        setTimeout(function(){
                        // _this.removeClass("active");
                        // _this.removeClass("active");
                        //_this.addClass("active");
                        btn1_progress.changeClass("progress-0","progress-50");
			        },10000);

			    }
			};
			
			scwin.btn2_onclick = function(e) {
			    if(!(this.hasClass("active"))){
			        this.addClass("active");
			        var _this = this;
			        setTimeout(function(){
			            _this.removeClass("active");
			        },10000);
			    }
			};

			scwin.btn3_onclick = function(e) {
			    if(!(this.hasClass("active"))){
			        this.addClass("active");
			        var _this = this;
			        setTimeout(function(){
			            _this.removeClass("active");
			        },10000);
			    }
			};

			scwin.btn4_onclick = function(e){
				if(!(btn4.hasClass("active"))){
                    btn4.addClass("active");
			        var _this = this;
			        setTimeout(function(){
                        btn2_progress.changeClass("progress-0","progress-100");
			            // _this.removeClass("active");
			        },10000);
			    }
			};



}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'row',id:''},E:[{T:1,N:'xf:group',A:{style:'',class:'progress-btn',id:'btn1','ev:onclick':'scwin.btn1_onclick'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:data-progress-style',E:[{T:3,text:'fill-back'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'btn',text:'',useLocale:'true',localeRef:'lbl_building'}},{T:1,N:'xf:group',A:{style:'',id:'btn1_progress',class:'progress-0'}}]},{T:1,N:'xf:group',A:{style:'',class:'progress-btn',id:'btn4','ev:onclick':'scwin.btn4_onclick'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:data-progress-style',E:[{T:3,text:'indefinite'}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'btn',text:'',useLocale:'true',localeRef:'lbl_build_complete'}},{T:1,N:'xf:group',A:{style:'',id:'btn2_progress',class:'progress-0'}}]}]}]}]}]})