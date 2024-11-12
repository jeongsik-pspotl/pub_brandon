/*amd /xml/project_import.xml 4310 c5b8803029ef68d76e3cb42ace66aec6c3d14bdc6d8afcd6474ef444e640a22c */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'dtl_import_steps',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'INDEX',name:'INDEX',dataType:'text'}},{T:1,N:'w2:column',A:{id:'FRAMEID',name:'FRAMEID',dataType:'text'}},{T:1,N:'w2:column',A:{id:'FRAMESRC',name:'FRAMESRC',dataType:'text'}},{T:1,N:'w2:column',A:{id:'MENUID',name:'MENUID',dataType:'text'}}]},{T:1,N:'w2:data',A:{use:'true'},E:[{T:1,N:'w2:row',E:[{T:1,N:'INDEX',E:[{T:4,cdata:'1'}]},{T:1,N:'FRAMEID',E:[{T:4,cdata:'wfm_project_import_step1'}]},{T:1,N:'FRAMESRC',E:[{T:4,cdata:'/xml/project_import_step01.xml'}]},{T:1,N:'MENUID',E:[{T:4,cdata:'grp_step1'}]}]}]}]},{T:1,N:'w2:dataList',A:{id:'dtl_build_import_project_step1',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'project_id',name:'project_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'workspace_id',name:'workspace_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'favorite',name:'favorite',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_name',name:'project_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'product_type',name:'product_type',dataType:'text'}},{T:1,N:'w2:column',A:{id:'platform',name:'platform',dataType:'text'}},{T:1,N:'w2:column',A:{id:'platform_language',name:'platform_language',dataType:'text'}},{T:1,N:'w2:column',A:{id:'status',name:'status',dataType:'text'}},{T:1,N:'w2:column',A:{id:'description',name:'description',dataType:'text'}},{T:1,N:'w2:column',A:{id:'template_version',name:'template_version',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_dir_path',name:'project_dir_path',dataType:'text'}},{T:1,N:'w2:column',A:{id:'created_date',name:'created_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'updated_date',name:'updated_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'project_etc1',name:'project_etc1',dataType:'text'}},{T:1,N:'w2:column',A:{id:'builder_id',name:'builder_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_id',name:'vcs_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'ftp_id',name:'ftp_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'key_id',name:'key_id',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.txt_project_all_step_platform = "";
			scwin.onpageload = function() {
				common.setScopeObj(scwin);
			};

			scwin.selected_step = function(selected){
				var jsonData = dtl_import_steps.getAllJSON();
				for(var obj in jsonData){
					var row = jsonData[obj];
					if(row.INDEX == selected){
						var temp = $p.getComponentById(row.FRAMEID);
						if(temp.getSrc() == ""){
							temp.setSrc(row.FRAMESRC);
						}
						$p.getComponentById(row.FRAMEID).show();
						$p.getComponentById(row.MENUID).addClass("on");
					} else {
						$p.getComponentById(row.FRAMEID).hide();
						$p.getComponentById(row.MENUID).removeClass("on");
					}
				}
			};

			scwin.btn_import_step01_onclick = function(){
				scwin.selected_step(1);
			};
			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'sub_contents'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'white_board'},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'pgt_tit fl',id:'',label:'Project 가져오기',style:''}}]},{T:1,N:'xf:group',A:{class:'step_box',id:'',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'checked on',id:'grp_step1',style:'',tagname:'li'},E:[{T:1,N:'w2:span',A:{id:'',label:'1',style:''}},{T:1,N:'w2:anchor',A:{id:'btn_proejct_import_step01',outerDiv:'false',style:'','ev:onclick':'scwin.btn_import_step01_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'프로젝트 가져오기'}]}]}]}]},{T:1,N:'w2:wframe',A:{id:'wfm_project_import_step1',style:'display:none;',src:'/xml/project_import_step01.xml',scope:'true'}}]}]}]}]}]})