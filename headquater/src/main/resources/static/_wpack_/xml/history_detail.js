/*amd /xml/history_detail.xml 13435 dbed461c8b0317e5d7b1a2453808c2085459b0267a11c5dcd36329516a2f8879 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',src:'/js/lib/qrcode.min.js'}},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
	scwin.onpageload = function() {
		common.setScopeObj(scwin);
		scwin.initPage();
	};

	scwin.onpageunload = function() {

	};

	scwin.initPage = function(){
		var historyPlatform = localStorage.getItem("historyPlatform");
		var historyProjectName = localStorage.getItem("historyProjectName");
		var historyProjectId = localStorage.getItem("historyProjectId");

        var message = common.getLabel("lbl_project_history"); // Project 기록

		txt_project_history_name.setValue(historyProjectName + " " + message);

		var options = {};
		options.action = "/manager/build/history/" + parseInt(historyProjectId) + "/" + historyPlatform + "/" + historyProjectName;
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.method = "GET";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {

				scwin.selecthistorydetail(data);

				// $(".qrcode").click(function(){
				// 	$(".layer_pop").css("display","block").css("width","180px").css("height","220px");
				// 	$("body").append("<div class='dim'></div>");
				// });
				// $(".btn_pop_close").click(function(){
				// 	$(".layer_pop").css("display","none");
				// 	$("div").remove(".dim");
				// });

			} else {
                var message = common.getLabel("lbl_failed_history"); // 히스토리 화면 조회 실패
				alert(message);
			}
		};

		options.error = function (e) {
			alert("message:"+e.responseText+"\n");
		};
		$p.ajax(options);
	};

	scwin.webSocketCallback = function(obj) {
		switch (obj.MsgType) {
			case "HV_MSG_APP_DOWNLOAD_STATUS_INFO_FROM_HEADQUATER" :
				scwin.setBuilderAppDownloadHref(obj);
				break;
			case "HV_MSG_APP_DOWNLOAD_STATUS_INFO" :
				scwin.setBranchAppDownloadStatus(obj);
				break;
			default :
				break;
		}
	};

	scwin.setBranchAppDownloadStatus = function ( data ) {
		switch (data.message) {
			case "SUCCESSFUL" :
				//scwin.setAppDownloadURL(data);

				break;
			case "FAILED" :

				break;
			default :
				break;
		}

	};

	scwin.setBuilderAppDownloadHref =  function (data){
		// app file 다운로드
		window.location = "/manager/build/history/downloadSetupFile/" + data.filename;
	}

	scwin.selecthistorydetail = function(data){
		var historyPlatform = localStorage.getItem("historyPlatform");
		var historyWorkspaceId = localStorage.getItem("historyWorkspaceId"); // historyWorkspaceId

		for (var idx in data) {
			list_buildhistory_detail_generator.insertChild();

			var txt_proj_history_list = list_buildhistory_detail_generator.getChild(idx, "txt_proj_history");
			var txt_build_start_date_list = list_buildhistory_detail_generator.getChild(idx, "txt_build_start_date");
			var txt_build_end_date_list = list_buildhistory_detail_generator.getChild(idx, "txt_build_end_date");
			var txt_history_build_project_name_list = list_buildhistory_detail_generator.getChild(idx, "txt_history_build_project_name");
			var txt_history_app_download_url_list = list_buildhistory_detail_generator.getChild(idx, "history_appfile_url");
			var txt_history_logfile_download_url_list = list_buildhistory_detail_generator.getChild(idx, "history_logfile_url");
			var txt_history_qrcode_download_url_list = list_buildhistory_detail_generator.getChild(idx, "history_qrcode_url");
			var btn_deploy_list = list_buildhistory_detail_generator.getChild(idx, "btn_deploy");

			var history_id = parseInt(data[idx].project_history_id);
			var build_id = parseInt(data[idx].project_id);
			var id_idx = parseInt(idx);

			// txt_name success
			if (data[idx].result == "SUCCESSFUL"){
				txt_proj_history_list.changeClass("txt_name fail","txt_name success");

				txt_history_app_download_url_list.setUserData("historyid",data[idx].project_history_id);

				txt_history_qrcode_download_url_list.setUserData("history_qrcode",data[idx].project_history_id);
				// txt_history_qrcode_download_url_list.setUserData("history_qrcode",data[idx].qrcode);

				btn_deploy_list.setUserData("platform",historyPlatform);
				btn_deploy_list.setUserData("projectName",data[idx].project_name);
				btn_deploy_list.setUserData("project_pkid",data[idx].project_id);
				btn_deploy_list.setUserData("project_history_id",data[idx].project_history_id);
				btn_deploy_list.setUserData("workspace_id",historyWorkspaceId); // historyWorkspaceId

			}else {
				txt_proj_history_list.changeClass("txt_name success","txt_name fail");

				txt_history_app_download_url_list.hide();
				txt_history_qrcode_download_url_list.hide();
				btn_deploy_list.hide();
			}

			if(data[idx].history_started_date != null){
				data[idx].history_started_date = data[idx].history_started_date.replace(/T/g,' ');
			}

			if(data[idx].history_ended_date != null){
				data[idx].history_ended_date = data[idx].history_ended_date.replace(/T/g,' ');
			}

            var message_state = common.getLabel("lbl_state");
            var message_history_started_date = common.getLabel("lbl_history_detail_js_history_started_date");
            var message_history_ended_date = common.getLabel("lbl_history_detail_js_history_ended_date");
            var message_project_name = common.getLabel("lbl_project_name");

			var status_log = "lbl_" + data[idx].status_log.toLowerCase();
			txt_proj_history_list.setValue(message_state + " : " + (data[idx].result != common.getLabel("lbl_successful_eng") ? common.getLabel("lbl_failed") : common.getLabel("lbl_successful")));
			txt_build_start_date_list.setValue(message_history_started_date + " : " + data[idx].history_started_date);
			txt_build_end_date_list.setValue(message_history_ended_date + " : " + data[idx].history_ended_date);
			txt_history_build_project_name_list.setValue(message_project_name + " : " + data[idx].project_name);

			// txt_history_app_download_url_list.setHref("/api/buildhistory/fileDownload/"+data[idx].id); //
			// txt_history_app_download_url_list.setHref("/api/buildhistory/fileDownload/"+data[idx].id); //

			// history id bind
			txt_history_logfile_download_url_list.setUserData("historyid",data[idx].project_history_id);

			// btn_deploy_list

			// txt_history_logfile_download_url_list.setHref("/api/buildhistory/logfileDownload/"+data[idx].id); //
			// txt_history_logfile_download_url_list.setHref("/api/buildhistory/logfileDownloadWebsocket/"+data[idx].id); //
		}
	};

	scwin.gethistoryfileurl_onclick = function(e){
	    var history_id = this.getUserData("historyid");

	    var data = {};
		var options = {};

		data.history_id = history_id;

		// options.action = "/api/buildhistory/logfileDownloadWebsocket/"+ parseInt(history_id);
		options.action = "/manager/build/history/downloadLogFile";
		options.mode = "asynchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(data);
		options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if (data != null) {

			} else {

			}
		};

		options.error = function (e) {
			alert("message:"+e.responseText+"\n");
		};

		$p.ajax( options );
	};

	/*
		Build History의 App Download 버튼을 누르면,
		웹서버에서 해당 apk or ipa 파일을 다운 받는다.
		@soorink
	 */
	scwin.gethistoryappfile_onclick = function(){
		var history_id = this.getUserData("historyid");

		(async () => {
			try {
				const response = await fetch('/manager/build/history/startFileDownload', {
					method: 'POST',
					headers: {
					'Content-Type': 'application/json',
					},
					body: JSON.stringify({
             			'history_id': history_id,
					}),
				});

				const result = await response.blob();
				const headers = await response.headers;
				const blob = new Blob([result], {type: 'application/octet-stream'});
				const url = URL.createObjectURL(blob);
				const link = document.createElement('a');
				link.href = url;
				link.download = headers.get('filename');
				link.click();
				URL.revokeObjectURL(url);
				delete a;
			} catch (error) {
				console.log('Error: ', error);
			}
		})();
	};

	scwin.getHistoryQRCodeUrl_onclick = function(){
		var history_qrcode = this.getUserData("history_qrcode");

		// scwin.buildAfterQrcodeCreate(history_qrcode);
		var data = {};
        data.historyCnt = history_qrcode;

		scwin.buildAfterQrcodeCreateByID(data);
	};

	scwin.btn_deploy_onclick = function(e){

		var platform = this.getUserData("platform");
		var deployProjectName = this.getUserData("projectName");
		var deploy_project_id = this.getUserData("project_pkid");
		var project_history_id = this.getUserData("project_history_id");
		var workspace_id = this.getUserData("workspace_id");
		// workspace_id
		// deploy id ...

		var deployAction = [];

		var data = {};
		data.platform = platform;
		data.projectName = deployProjectName;
		data.project_pkid = deploy_project_id;
		data.project_history_id = project_history_id;
		data.workspace_pkid = workspace_id;

		deployAction.push(data);

		$p.parent().__deployaction_data__.setJSON(deployAction);

		$p.parent().wfm_main.setSrc("/xml/deploy.xml");
		WebSquare.getBody().hideToolTip();
	};

	scwin.buildAfterQrcodeCreate = function(qrcodeUrl){

		$("#history_qrcode").empty();

		var qrcode = new QRCode(document.getElementById("history_qrcode"),{
			text: qrcodeUrl,
			width: 150,
			height: 150,
			colorDark : "#000000",
			colorLight : "#ffffff",
			correctLevel : QRCode.CorrectLevel.H
		});
	};

	scwin.buildAfterQrcodeCreateByID = function(data){
		// console.log(g_config.HTTPSERVER_URL);
		// $("#history_qrcode").empty();
        //
		// var qrcode = new QRCode(document.getElementById("history_qrcode"),{
		// 	text: g_config.HTTPSERVER_URL + "/builder/build/history/CheckAuthPopup/"+parseInt(qrcodeID),
		// 	width: 150,
		// 	height: 150,
		// 	colorDark : "#000000",
		// 	colorLight : "#ffffff",
		// 	correctLevel : QRCode.CorrectLevel.H
		// });

		requires("uiplugin.popup");

		var rowJSON = {
			"data" : data
		};

		var dataObject = {
			"type" : "json",
			"name" : "param",
			"data" : rowJSON
		};

		var opts = {
			"id" : "popup_window_qrcode",
			"type" : "litewindow",
			"width" : 350 + "px",
			"height" : 350 + "px",
			"popupName" : " ",
			"modal" : false,
			"useIFrame" : false,
			"title" : "",
			"useATagBtn" : "true",
			"frameMode" : "wframe",
			"dataObject" : dataObject
		};
		WebSquare.util.openPopup("/xml/QRCode.xml", opts);
	};

    scwin.buildAfterQrcodePopupByID = function(qrcodeID){

	};

	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'white_board'},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'pgtbox'},E:[{T:1,N:'w2:textbox',A:{id:'txt_project_history_name',style:'',class:'pgt_tit fl',label:'',useLocale:'true',localeRef:'lbl_history2'}}]},{T:1,N:'xf:group',A:{class:'',id:'',style:''},E:[{T:1,N:'w2:generator',A:{style:'',id:'list_buildhistory_detail_generator',class:'record_box02'},E:[{T:1,N:'xf:group',A:{class:'item',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rec_tit',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'txt_name success',id:'txt_proj_history',label:'',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_whether_build',tooltipLocaleRef:'lbl_whether_build'}},{T:1,N:'xf:group',A:{class:'recbox'},E:[{T:1,N:'w2:textbox',A:{style:'',id:'txt_build_start_date',label:'',class:'txt_name'}},{T:1,N:'w2:textbox',A:{style:'',id:'txt_build_end_date',label:'',class:'txt_name'}},{T:1,N:'w2:textbox',A:{style:'',id:'txt_history_build_project_name',label:'',class:'txt_name'}}]}]},{T:1,N:'xf:group',A:{class:'rec_link',id:'',style:''},E:[{T:1,N:'w2:anchor',A:{class:'btn_down',id:'history_appfile_url',outerDiv:'false',style:'',href:'','ev:onclick':'scwin.gethistoryappfile_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_app_download',tooltipLocaleRef:'lbl_history_detail_tooltip_app_file_url'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'btn_down',id:'history_logfile_url',outerDiv:'false',style:'',href:'','ev:onclick':'scwin.gethistoryfileurl_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_log_download',tooltipLocaleRef:'lbl_history_detail_tooltip_log_file_url'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'qrcode','ev:onclick':'scwin.getHistoryQRCodeUrl_onclick',href:'',id:'history_qrcode_url',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_history_detail_tooltip_history_qrcode'},E:[{T:1,N:'xf:label'}]},{T:1,N:'w2:anchor',A:{class:'btn_i_down','ev:onclick':'scwin.btn_deploy_onclick',href:'',id:'btn_deploy',outerDiv:'false',style:'',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',tooltipLocaleRef:'lbl_history_detail_tooltip_deploy'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'layer_pop',id:'',style:'display:none;'},E:[{T:1,N:'xf:group',A:{class:'ly_head',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'title',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_qrcode'}},{T:1,N:'w2:anchor',A:{class:'btn_pop_close',id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_close'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{class:'ly_cont',id:'',style:''},E:[{T:1,N:'div',A:{id:'history_qrcode',style:'width: 290px;height:330px;'}}]}]}]}]}]})