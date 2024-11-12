/*amd /xml/setting_vcs_detail.xml 18103 78041f20eafc936675055450c07b3e1d9429b26cdfa47886785ea8dac7873926 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'},E:[{T:1,N:'w2:dataList',A:{id:'__vcs_setting_detail_data__',saveRemovedData:'true'},E:[{T:1,N:'w2:columnInfo',E:[{T:1,N:'w2:column',A:{id:'vcs_id',name:'vcs_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'role_code_id',name:'role_code_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_type',name:'vcs_type',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_name',name:'vcs_name',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_url',name:'vcs_url',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_user_id',name:'vcs_user_id',dataType:'text'}},{T:1,N:'w2:column',A:{id:'vcs_user_pwd',name:'vcs_user_pwd',dataType:'text'}},{T:1,N:'w2:column',A:{id:'create_date',name:'create_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'update_date',name:'update_date',dataType:'text'}},{T:1,N:'w2:column',A:{id:'admin_id',name:'admin_id',dataType:'text'}}]}]}]},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
			scwin.check_vcs_nameYN = false;
			scwin.onpageload = function() {
				// 화면 구성 변경
				var vcs_setting_mode = localStorage.getItem("_vcs_setting_mode_");

				if (vcs_setting_mode == "detailview"){
					var view = common.getLabel("lbl_setting_vcs_detail_detailView");
					var save = common.getLabel("lbl_save");
					vcs_setting_title.setLabel(view);
					vcs_create_or_save_btn.setLabel(save);
					vcs_setting_name.setDisabled(true);
					vcs_setting_url.setDisabled(false);
					scwin.vcsDetailView();

					// if(whive_session.user_role == "SUPERADMIN"){
                    //
                    //
					// }else {
						domain_id_grp.hide();
						admin_id_grp.hide();
						//setting_admin_list.setDisabled(true);
						// setting_domain_list.setDisabled(true);
					// }

				}else {
					var create = common.getLabel("lbl_setting_vcs_detail_tool");
					var regist = common.getLabel("lbl_regist");
					vcs_setting_title.setLabel(create);
					vcs_create_or_save_btn.setLabel(regist);
					vcs_setting_name.setDisabled(false);
					vcs_setting_url.setDisabled(false);



					// if(whive_session.user_role == "SUPERADMIN"){
						// scwin.select_domain_list();
						// setting_admin_list.addItem("", "선택안함");

					// }else {
						domain_id_grp.hide();
						admin_id_grp.hide();

						//setting_admin_list.setDisabled(true);
						// setting_domain_list.setDisabled(true);
					//}

				}

			};

			scwin.onpageunload = function() {

			};

			scwin.checkDataVCSName = function(){

				var vcs_name = vcs_setting_name.getValue();

				if(common.isEmptyStr(vcs_name)){
					var message = common.getLabel("lbl_setting_vcs_detail_vcsName");
					alert(message);
					return false;
				}

				return true;
			};

			scwin.checkAllVCS = function(){

				var vcs_name = vcs_setting_name.getValue();
				var vcs_type = vcs_setting_vcs_type_code.getValue();

				if(common.isEmptyStr(vcs_name)){
					var message = common.getLabel("lbl_setting_vcs_detail_vcsName");
					alert(message);
					return false;
				}

				if(!scwin.check_vcs_nameYN){
					var message = common.getLabel("lbl_setting_vcs_detail_vcsDuplicate");
					alert(message);
					return false;
				}

				if(vcs_type == "git" && vcs_setting_user_id.getValue() == ""){
					var message = common.getLabel("lbl_setting_vcs_detail_gitId");
					alert(message);
					return false;
				}

				if(vcs_type == "git" && vcs_setting_user_pwd.getValue() == ""){
					var message = common.getLabel("lbl_setting_vcs_detail_gitPwd");
					alert(message);
					return false;
				}

				if(vcs_type == "git" && vcs_setting_url.getValue() == ""){
					var message = common.getLabel("lbl_setting_vcs_detail_gitUrl");
					alert(message);
					return false;
				}

				// if(vcs_type != "localgit" && vcs_setting_url.getValue() == ""){
				// 	alert("내부 저장소 type은 Url을 압력하세요.");
				// 	return false;
				// }

				return true;

			};

			scwin.vcsDetailView = function(){

				var vcs_id = localStorage.getItem("_vcs_id_");

				var options = {};

				options.action = "/manager/vcs/search/profile/" + parseInt(vcs_id);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {

						var settingvcs = [];
						var temp = {};


						vcs_setting_vcs_type_code.setValue(data.vcs_type);
						vcs_setting_user_id.setValue(data.vcs_user_id);
						vcs_setting_user_pwd.setValue(data.vcs_user_pwd);
						vcs_setting_name.setValue(data.vcs_name);

                        if(data.vcs_type == "localgit"){

						}else {
							vcs_setting_url.setValue(data.vcs_url);
						}



						var temp = {};

						temp.vcs_id = data.vcs_id;
						temp.vcs_type = data.vcs_type;
						temp.vcs_user_id = data.vcs_user_id;
						temp.vcs_user_pwd = data.vcs_user_pwd;
						temp.vcs_name = data.vcs_name;
						temp.vcs_url = data.vcs_url;
						temp.created_date = data.created_date;
						temp.updated_date = data.updated_date;

						__vcs_setting_detail_data__.setJSON([data]);
					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );


			};

			scwin.saveVCSSettingData = function(){

				var vcs_setting_mode = localStorage.getItem("_vcs_setting_mode_");

				var vcs_setting_data = __vcs_setting_detail_data__.getRowJSON(0);

				var vcs_type = vcs_setting_vcs_type_code.getValue();
				var vcs_user_id = vcs_setting_user_id.getValue();
				var vcs_user_pwd = vcs_setting_user_pwd.getValue();
				var vcs_name = vcs_setting_name.getValue();
				var vcs_url = vcs_setting_url.getValue();

				var data = {};
				data.vcs_id = vcs_setting_data.vcs_id;
				data.vcs_type = vcs_type;
				data.vcs_user_id = vcs_user_id;
				data.vcs_user_pwd = vcs_user_pwd;
				data.vcs_name = vcs_name;
				data.vcs_url = vcs_url;

				// if(whive_session.user_role == "SUPERADMIN"){

					// data.admin_id = setting_admin_list.getValue();


				// } else if (whive_session.user_role == "ADMIN"){

				/* admin id 컨트롤러 단에서 처리해야함.
					data.admin_id = whive_session.id; // 수정해야함...

				 */


				// }


				__vcs_setting_detail_data__.removeAll();
				__vcs_setting_detail_data__.setJSON([data]);

				vcs_setting_data = __vcs_setting_detail_data__.getRowJSON(0);

				if (vcs_setting_mode == "detailview"){
					scwin.setVCSSettingAndUpdate(vcs_setting_data);
				}else {
				    if(scwin.checkAllVCS()){
						scwin.setVCSSettingAndInsert(vcs_setting_data);
					}

				}


			};

			scwin.select_check_vcs_name = function(vcs_name){
				var data = {};
				data.vcs_name = vcs_name;
				var options = {};

				options.action = "/manager/vcs/search/checkProfileName";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(data);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if(e.responseStatusCode === 200 || e.responseStatusCode === 201){
						if (data != null) {
							if(data[0].vcs_name_not_found == "no"){
								var message = common.getLabel("lbl_can_use_name");
								alert(message);
								scwin.check_vcs_nameYN = true;

							}else if(data[0].vcs_name_not_found == "yes"){
								var message = common.getLabel("lbl_exist_name");
								alert(message);
								scwin.check_vcs_nameYN = false;
							}

						}
					}

				};

				options.error = function (e) {
					if(e.responseStatusCode === 500){


					}else {

					}

				};

				$p.ajax( options );

			};

			scwin.setVCSSettingAndInsert = function (vcs_setting_data) {

				var vcs_setting_json = {};

				vcs_setting_json = vcs_setting_data;

				var options = {};
				options.action = "/manager/vcs/create";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(vcs_setting_json);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {
						var message = common.getLabel("lbl_setting_vcs_detail_info");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","vcs");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						var message = common.getLabel("lbl_setting_vcs_detail_fail");
						alert(message);
					}
				};

				options.error = function (e) {
					alert( "message:"+e.responseText+"\n" );

				};

				$p.ajax( options );

			};

			scwin.setVCSSettingAndUpdate = function (vcs_setting_data) {

				var vcs_setting_json = {};

				vcs_setting_json = vcs_setting_data;

				var options = {};
				options.action = "/manager/vcs/update";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.requestData = JSON.stringify(vcs_setting_json);
				options.method = "POST";

				options.success = function (e) {
					var data = e.responseJSON;
					if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {
						var message = common.getLabel("lbl_setting_vcs_detail_infoSetting");
						alert(message);
						$p.parent().wfm_main.setUserData("settingsData","vcs");
						$p.parent().wfm_main.setSrc("/xml/settings.xml");

					} else {
						var message = common.getLabel("lbl_setting_vcs_detail_infoFail");
						alert(message);
					}
				};

				options.error = function (e) {
					alert( "message:"+e.responseText+"\n" );

				};

				$p.ajax( options );

			};

			scwin.btn_prev_onclick = function(e){

			};

			scwin.btn_next_onclick = function(e){
				if(scwin.checkData()){

				}
			};

			scwin.btn_create_vcs_setting_onclick = function(e){
				scwin.saveVCSSettingData();


			};

			scwin.step1_select_role_code_onchange = function(e){
				var platform = this.getValue(this.getSelectedIndex);
				localStorage.setItem("_role_code_id_",platform);
			};

			scwin.step1_btn_check_project_name_onclick = function(e){
				var vcs_name = vcs_setting_name.getValue();

				if(scwin.checkDataVCSName()){
					scwin.select_check_vcs_name(vcs_name);
				}

			};

			scwin.setting_select_domain_onchange = function(e){

				var domainID = setting_domain_list.getValue();

				if(domainID == ""){
					setting_admin_list.removeAll(false);
                    var message = common.getLabel("lbl_select");
					setting_admin_list.addItem("",message); //선택

				}else {
					scwin.select_admin_list(domainID);
				}

			};

			scwin.select_admin_list = function(domainID){

				var options = {};

				options.action = "/manager/member/search/userInfoForSelectBox/" + parseInt(domainID);
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
						setting_admin_list.removeAll(false);

                        var message = common.getLabel("lbl_select");
						setting_admin_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

							setting_admin_list.addItem(data[row].user_id, data[row].user_name);

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n");

				};

				$p.ajax( options );

			};

			// domain 리스트 전체 조회
			scwin.select_domain_list = function(){

				var options = {};

				options.action = "/manager/domain/search/domainList";
				options.mode = "asynchronous";
				options.mediatype = "application/json";
				options.method = "GET";

				options.success = function (e) {
					var data = e.responseJSON;
					if (data != null) {
                        var message = common.getLabel("lbl_select");
						setting_domain_list.addItem("",message); //선택
						for (var row in data) {
							var temp = {};

							setting_domain_list.addItem(data[row].domain_id, data[row].domain_name);

						}

					} else {

					}
				};

				options.error = function (e) {
					alert("code:"+e.status+"\n"+"message:"+e.responseText+"\n");

				};

				$p.ajax( options );

			};

			
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'gallery_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'fl'},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'vcs_setting_title',label:'',style:'',useLocale:'true',localeRef:'lbl_setting_vcs_detail_tool'}}]},{T:1,N:'xf:group',A:{class:'fr',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm type1 fl',id:'vcs_create_or_save_btn',style:'',type:'button','ev:onclick':'scwin.btn_create_vcs_setting_onclick',useLocale:'true',localeRef:'lbl_regist'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_version_manage_tool_profile'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',tooltipLocaleRef:'lbl_setting_vcs_detail_infoSeparator',toolTipDisplay:'true',useLocale:'true'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'xf:input',A:{id:'vcs_setting_name',style:'',adjustMaxLength:'false'}},{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.step1_btn_check_project_name_onclick',useLocale:'true',localeRef:'lbl_dup_check'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{id:'domain_id_grp',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_domain_id'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_domain_list',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.setting_select_domain_onchange'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'admin_id_grp',style:'',class:'',tagname:'li'},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_admin_id'}},{T:1,N:'xf:select1',A:{renderType:'native',id:'setting_admin_list',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.setting_select_admin_onchange'},E:[{T:1,N:'xf:choices'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_vcs_detail_toolType'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_vcs_detail_tools'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:select1',A:{renderType:'native',id:'vcs_setting_vcs_type_code',disabledClass:'w2selectbox_disabled',ref:'',appearance:'minimal',style:'',direction:'auto',chooseOption:'',allOption:'',displayMode:'label',disabled:'false',submenuSize:'auto','ev:onchange':'scwin.step1_select_role_code_onchange'},E:[{T:1,N:'xf:choices',E:[{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'내부 저장소 git'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'localgit'}]}]},{T:1,N:'xf:item',E:[{T:1,N:'xf:label',E:[{T:4,cdata:'Github'}]},{T:1,N:'xf:value',E:[{T:4,cdata:'git'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_vcs_detail_id'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_vcs_detail_necessaryId'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'vcs_setting_user_id',style:'',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_vcs_detail_password'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_vcs_detail_necessaryPw'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:input',A:{id:'vcs_setting_user_pwd',style:'',dataType:'password',type:'password',adjustMaxLength:'false'}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'tooltip_box',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'',style:'',class:'form_name',label:'',useLocale:'true',localeRef:'lbl_setting_vcs_detail_address'}},{T:1,N:'w2:anchor',A:{class:'ico_tip',id:'',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_setting_vcs_detail_serverAddress'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box'},E:[{T:1,N:'xf:input',A:{id:'vcs_setting_url',style:'',adjustMaxLength:'false'}}]}]}]}]}]}]}]}]}]})