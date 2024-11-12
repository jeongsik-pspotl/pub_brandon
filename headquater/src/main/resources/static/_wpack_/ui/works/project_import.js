/*amd /ui/works/project_import.xml 1790 1ddd85ccf0320f7e997ddf0e4dd44ea57ff20854d44e5815827c20cb1acf6f30 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
	scwin.onpageload = function() {
		scwin.paramData = $p.getParameter("tabParam");
		const obj = {
			"dataObject": {
				"type" : "json",
				"name" : "wframeParam",
				"data" : scwin.paramData
			}
		};
        const path = $p.parent().scwin.convertMenuCodeToPath("m0100040100");
		wfm_project_import_step1.setSrc(path,obj);
	};

}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',localeRef:'lbl_project_import',useLocale:'true'}}]},{T:1,N:'xf:group',A:{class:'step_bar',id:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'on',id:'',tagname:'li'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',localeRef:'lbl_project_import',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'w2:wframe',A:{src:'',style:'',id:'wfm_project_import_step1',scope:'true'}}]}]}]}]}]})