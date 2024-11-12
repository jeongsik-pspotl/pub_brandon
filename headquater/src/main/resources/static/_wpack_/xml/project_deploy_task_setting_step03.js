/*amd /xml/project_deploy_task_setting_step03.xml 86951 8b04b455d6a79e395b19f59109890774dd0fcc1c98757e3a1d043928c1f3e29a */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'DEFAULT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'script',A:{type:'text/javascript',lazy:'false'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
	scwin.platformType = "";
	scwin.app_scrennshot_ios_itemIdx = 0;
	scwin.app_scrennshot_ios_phone_itemIdx = 0;
    scwin.app_screenshot_ios_ipad_129_itemIdx = 0;
	scwin.app_screenshot_ios_ipad_129_3gen_itemIdx = 0;
	scwin.app_scrennshot_android_phone_itemIdx = 0;
	scwin.app_scrennshot_android_small_tablet_itemIdx = 0;
	scwin.app_scrennshot_android_large_tablet_itemIdx = 0;
	scwin.onpageload = function() {
		common.setScopeObj(scwin);

		var deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
		scwin.platformType = deployTaskData[0].platform;

        if(scwin.platformType == "Android"){
			grp_android.show();
            grp_iOS.hide();

		}else if(scwin.platformType == "iOS"){
			grp_iOS.show();
			grp_android.hide();

		}

		/*
                    1. dynamicCreate는 생성하려는 id가 존재하면, 컴포넌트 생성을 하지 않는다.
                    2. 해당 이유로, project_deploy_task_setting_step03 에서 dynamicCreate를 이용해서 생성하는 컴포넌트들은 첫번째 화면 로딩시에는 정상동작하고,
                       두번째 화면로딩 부터는 컴포넌트가 제대로 생성되지 않는 문제가 발생한다.
                    따라서, 아래의 destroy 명령어로 해당 컴포넌트들을 소멸 시켜주어야 정상 동작한다.
		*/
		Object.keys(WebSquare.idToUUID).filter( i => /^(myid_group_li_).*$/.test(i)).map( i => $p.getComponentById(i).destroy() );
		Object.keys(WebSquare.idToUUID).filter( i => /^(myid_group_li_).*(small_tablet_1_\d+)$/.test(i)).map( i => $p.getComponentById(i).destroy() );
		Object.keys(WebSquare.idToUUID).filter( i => /^(myid_group_li_).*(large_tablet_1_\d+)$/.test(i)).map( i => $p.getComponentById(i).destroy() );
		Object.keys(WebSquare.idToUUID).filter( i => /^(myid_group_li_).*(phone_1_\d+)$/.test(i)).map( i => $p.getComponentById(i).destroy() );
		// Object.keys(WebSquare.idToUUID).filter( i => /^(tap_\d+_ios_myid_).*(ul_\d+)$/.test(i)).map( i => $p.getComponentById(i).destroy() );

		scwin.initPage();

	};
	
	scwin.onpageunload = function() {

	};

	scwin.initPage = function(){
		// var build_project_json = $p.parent().dtl_build_setting_step1.getAllJSON();
		// 환경설정 List 조회

		// TODO : test deploy task api
		scwin.searchStep1Data();

		if(scwin.platformType == "Android"){

			$p.getComponentById("step2_android_input_app_screenshot1_path").render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						screen_shot_29.setSrc(base64data);
					}

			},false);


		}else if(scwin.platformType == "iOS"){

			$p.getComponentById("step2_ios_input_app_screenshot_iphone55_path").render.addEventListener("change", function (event) {
				var files = event.target.files[0];

				const reader = new FileReader();
				reader.readAsDataURL(files);
				reader.onloadend = () => {
					const base64data = reader.result;

					screen_shot_ios_iphone55.setSrc(base64data);
				}

			},false);


		}

	};


	scwin.searchStep1Data = function(){

		var deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
		var project_name = ""; //step1_input_projectname.getValue();
		scwin.platformType = deployTaskData[0].platform; //step1_select_platform.getValue();
		// var build_server_url = step1_input_buildserver.getValue();

		var description = "";//step1_txtarea_desc.getValue();

		// workspace id 값 세팅
		var local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
		var local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

		var data = {};
		data.project_id = parseInt(local_buildproject_id);
		data.workspace_id = parseInt(local_workspace_id);
		data.project_name = project_name;
		data.platform = scwin.platformType;
		data.description = description;
		data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.

		var options = {};
		options.action = "/manager/deploy/setting/search/metadata/image";
		options.mode = "synchronous";
		options.mediatype = "application/json";
		options.requestData = JSON.stringify(data),
				options.method = "POST";

		options.success = function (e) {
			var data = e.responseJSON;
			if ((e.responseStatusCode === 200 || e.responseStatusCode === 201)&& data != null) {


			} else {
				// alert("Deploy Setting 생성 실패");
			}
		};

		options.error = function (e) {
			alert("code:"+e.responseStatusCode+"\n"+"message:"+e.responseText+"\n"+"error:"+e.requestBody);
			//$p.url("/login.xml");
		};

		$p.ajax( options );


	};


	scwin.saveStep1Data = function(){

		var formData = new FormData();
		var deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
		var platform = scwin.platformType;//step1_select_platform.getValue();
		// var build_server_url = step1_input_buildserver.getValue();

		var description = "";//step1_txtarea_desc.getValue();

		// workspace id 값 세팅
		var local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
		var local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

		var deployJsonData = {};
		deployJsonData.project_id = parseInt(local_buildproject_id);
		deployJsonData.workspace_id = parseInt(local_workspace_id);
		deployJsonData.platform = platform;
		deployJsonData.description = description;

		formData.append("projectDeploytJson", JSON.stringify(deployJsonData));

        if(scwin.platformType == "Android"){


            // var screenshot1_img = screen_shot_30.getSrc();
            //
			var screenshot_android_phone_img_file = step2_android_input_app_screenshot_phone_path.dom.fakeinput.files[0];
			var screenshot_android_phone_img_file_blob = screenshot_android_phone_img_file.slice(0, screenshot_android_phone_img_file.size, 'image/png');
			screenshot_android_phone_img_file = new File([screenshot_android_phone_img_file_blob], '1_ko-KR.png',{type: screenshot_android_phone_img_file.type});
			formData.append("phoneImagefile", screenshot_android_phone_img_file);

			for(var iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_android_phone_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_android_input_app_screenshot_phone_"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_ko-KR.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("phoneImagefile", save_screen_shot_uploadfile);
				}


			}

            // formData.append("phoneImagefile", screenshot1_img_file);

			var screenshot_android_7inch_tablet_img_file = step2_android_input_app_screenshot_7inch_tablet_path.dom.fakeinput.files[0];
			var screenshot_android_7inch_tablet_img_file_blob = screenshot_android_7inch_tablet_img_file.slice(0, screenshot_android_7inch_tablet_img_file.size, 'image/png');
			screenshot_android_7inch_tablet_img_file = new File([screenshot_android_7inch_tablet_img_file_blob], '1_ko-KR.png',{type: screenshot_android_7inch_tablet_img_file.type});
			formData.append("sevenInchTabletImagefile", screenshot_android_7inch_tablet_img_file);

			for(var iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_android_small_tablet_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_android_input_app_screenshot_small_tablet_"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_ko-KR.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("sevenInchTabletImagefile", save_screen_shot_uploadfile);
				}


			}

			var screenshot_android_10inch_tablet_img_file = step2_android_input_app_screenshot_10inch_tablet_path.dom.fakeinput.files[0];
			var screenshot_android_10inch_tablet_img_file_blob = screenshot_android_10inch_tablet_img_file.slice(0, screenshot_android_10inch_tablet_img_file.size, 'image/png');
			screenshot_android_10inch_tablet_img_file = new File([screenshot_android_10inch_tablet_img_file_blob], '1_ko-KR.png',{type: screenshot_android_10inch_tablet_img_file.type});
			formData.append("tenInchTabletImagefile", screenshot_android_10inch_tablet_img_file);

			for(var iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_android_large_tablet_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_android_input_app_screenshot_large_tablet_"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_ko-KR.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("tenInchTabletImagefile", save_screen_shot_uploadfile);
				}


			}

			// TODO : Android image file upload 디바이스 별로 분류 해서 append 하기

			$.ajax({
				url: "/manager/deploy/setting/update/metadata/android/image",
				type: "POST",
				enctype: 'multipart/form-data',
				processData: false,
				contentType: false,
				data: formData,
				dataType: 'json',
				cache: false,
				success: function (r, status) {
					var data = r;
					//console.log(data);
					if (status === "success") {
						console.log("success");
					}

				}
				, error: function (request, status, error) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
				}
			});


        }else if(scwin.platformType == "iOS"){

			var screenshot_screen_shot_ios_iphone65_path = step2_ios_input_app_screenshot_iphone65_path.dom.fakeinput.files[0];
			var screenshot_screen_shot_ios_iphone65_path_blob = screenshot_screen_shot_ios_iphone65_path.slice(0, screenshot_screen_shot_ios_iphone65_path.size, 'image/png');
            screenshot_screen_shot_ios_iphone65_path = new File([screenshot_screen_shot_ios_iphone65_path_blob], '0_APP_IPHONE_65_0.png',{type: screenshot_screen_shot_ios_iphone65_path.type});
			formData.append("imagefile", screenshot_screen_shot_ios_iphone65_path);

            for(var iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_ios_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_ios_input_app_screenshot"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPHONE_65_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("imagefile", save_screen_shot_uploadfile);
				}


			}

			var screenshot_screen_shot_ios_iphone55 = step2_ios_input_app_screenshot_iphone55_path.dom.fakeinput.files[0];
			var screenshot_screen_shot_ios_iphone55_blob = screenshot_screen_shot_ios_iphone55.slice(0, screenshot_screen_shot_ios_iphone55.size, 'image/png');
			screenshot_screen_shot_ios_iphone55 = new File([screenshot_screen_shot_ios_iphone55_blob], '0_APP_IPHONE_55_0.png',{type: screenshot_screen_shot_ios_iphone55.type});
			formData.append("imagefile", screenshot_screen_shot_ios_iphone55);

			for(var iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_ios_phone_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_ios_input_app_screenshot_phone_"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPHONE_55_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("imagefile", save_screen_shot_uploadfile);
				}


			}

			var screenshot_screen_shot_ios_ipad129 = step2_ios_input_app_screenshot_ipad129_path.dom.fakeinput.files[0];
			var screenshot_screen_shot_ios_ipad129_blob = screenshot_screen_shot_ios_ipad129.slice(0, screenshot_screen_shot_ios_ipad129.size, 'image/png');
			screenshot_screen_shot_ios_ipad129 = new File([screenshot_screen_shot_ios_ipad129_blob], '0_APP_IPAD_PRO_129_0.png',{type: screenshot_screen_shot_ios_ipad129.type});
			formData.append("imagefile", screenshot_screen_shot_ios_ipad129);

			for(var iosImgCnt = 0; iosImgCnt < scwin.app_screenshot_ios_ipad_129_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_ios_input_app_screenshot_small_tablet_"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPAD_PRO_129_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("imagefile", save_screen_shot_uploadfile);
				}

			}

			var screenshot_screen_shot_ios_ipad129_3gen = step2_ios_input_app_screenshot_ipad129_3gen_path.dom.fakeinput.files[0];
			var screenshot_screen_shot_ios_ipad129_3gen_blob = screenshot_screen_shot_ios_ipad129_3gen.slice(0, screenshot_screen_shot_ios_ipad129_3gen.size, 'image/png');
			screenshot_screen_shot_ios_ipad129_3gen = new File([screenshot_screen_shot_ios_ipad129_3gen_blob], '0_APP_IPAD_PRO_3GEN_129_0.png',{type: screenshot_screen_shot_ios_ipad129_3gen.type});
			formData.append("imagefile", screenshot_screen_shot_ios_ipad129_3gen);

			for(var iosImgCnt = 0; iosImgCnt < scwin.app_screenshot_ios_ipad_129_3gen_itemIdx;iosImgCnt++){

				var screen_shot_uploadfile = $p.getComponentById("step3_ios_input_app_screenshot_large_tablet_"+ iosImgCnt).dom.fakeinput.files[0];
				var screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
				var save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPAD_PRO_3GEN_129_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
				if(save_screen_shot_uploadfile === undefined){

				}else {
					formData.append("imagefile", save_screen_shot_uploadfile);
				}

			}


			$.ajax({
				url: "/manager/deploy/setting/update/metadata/image",
				type: "POST",
				enctype: 'multipart/form-data',
				processData: false,
				contentType: false,
				data: formData,
				dataType: 'json',
				cache: false,
				success: function (r, status) {
					var data = r;
					//console.log(data);
					if (status === "success") {
						console.log("success");
					}

				}
				, error: function (request, status, error) {
					alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
				}
			});


		}








	};

	scwin.btn_ios_prev_onclick = function(e){
		$p.parent().scwin.selected_step(1);
	};

    scwin.btn_android_prev_onclick = function(e){
		$p.parent().scwin.selected_step(1);
	};

	scwin.btn_next_onclick = function(e){
	    // 저장 플레그 추가
		scwin.saveStep1Data();


	};

	/**
	 * 이미지 파일 바이너리 통신 구간
	 * @param binType
	 * @param resultBlob
	 */
	scwin.websocketBinaryCallback =function (binType, resultBlob){

        switch (binType){
			case "HV_BIN_DEPLOY_METADATA_IMAGE_READ":

				function getStringUTF8(dataview, offset, length) {
					var s = '';

					for (var i = 0, c; i < length;) {
						c = dataview.getUint8(offset + i++); // 해당 구간 에러
						s += String.fromCharCode(
								c > 0xdf && c < 0xf0 && i < length - 1
										? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
										| dataview.getUint8(offset + i++) & 0x3f
										: c > 0x7f && i < length
												? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
												: c
						);
					}

					return s;
				}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

                    var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
                    offset += DEFAULT_READ_BYTES;

                    var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
                    offset += imageFileListLength;


                    for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
                        offset += DEFAULT_READ_BYTES;

                        var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

                        var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

                        var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotImgBinaryData(imageFileListSize);

					// var fileNameLength = dataView.getInt32(offset);
					// offset += DEFAULT_READ_BYTES;
                    //
					// var fileName = getStringUTF8(dataView, offset, fileNameLength);
					// offset += fileNameLength;
					// //
					// var dataLength = dataView.getInt32(offset);
					// offset += DEFAULT_READ_BYTES;



					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

                    // if(endsWith(fileName, 'png')){
					if(scwin.platformType == "Android"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						screen_shot_30.setSrc("data:image/;base64,"+imageFileArr[0]);
						screen_shot_29.setSrc("data:image/;base64,"+imageFileArr[1]);

					}else if(scwin.platformType == "iOS"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						for(var isize = 0; isize < imageFileArr.length ; isize++){

							if (isize == 0){
								screen_shot_ios_iphone65_path.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

							if (isize >= 1){
								for (var shotsize = 0; shotsize < scwin.app_scrennshot_ios_itemIdx; shotsize++){
									if(isize == (shotsize+1)){
                                        console.log(shotsize);
										var screen_shot_imageset = $p.getComponentById("screen_shot_ios_iphone65"+ shotsize);
										screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
									}

								}


							}

						}

					}


					// }

				});
				reader.readAsArrayBuffer(resultBlob);

                break;
                // TODO : screen shot ios/android tablet
			case "HV_BIN_DEPLOY_METADATA_PHONE_IMAGE_READ":

				function getStringUTF8(dataview, offset, length) {
					var s = '';

					for (var i = 0, c; i < length;) {
						c = dataview.getUint8(offset + i++); // 해당 구간 에러
						s += String.fromCharCode(
								c > 0xdf && c < 0xf0 && i < length - 1
										? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
										| dataview.getUint8(offset + i++) & 0x3f
										: c > 0x7f && i < length
												? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
												: c
						);
					}

					return s;
				}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
					offset += imageFileListLength;


					for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

						var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotPhoneImgBinaryData(imageFileListSize);

					// var fileNameLength = dataView.getInt32(offset);
					// offset += DEFAULT_READ_BYTES;
					//
					// var fileName = getStringUTF8(dataView, offset, fileNameLength);
					// offset += fileNameLength;
					// //
					// var dataLength = dataView.getInt32(offset);
					// offset += DEFAULT_READ_BYTES;



					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

					// if(endsWith(fileName, 'png')){
					if(scwin.platformType == "Android"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						screen_shot_30.setSrc("data:image/;base64,"+imageFileArr[0]);
						screen_shot_29.setSrc("data:image/;base64,"+imageFileArr[1]);

					}else if(scwin.platformType == "iOS"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						for(var isize = 0; isize < imageFileArr.length ; isize++){

							if (isize == 0){
								screen_shot_ios_iphone55.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

							if (isize >= 1){
								for (var shotsize = 0; shotsize < scwin.app_scrennshot_ios_phone_itemIdx; shotsize++){
									if(isize == (shotsize + 1)){
										var screen_shot_imageset = $p.getComponentById("screen_shot_ios_phone55_"+ shotsize);
										screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
									}

								}


							}

						}

					}


					// }

				});
				reader.readAsArrayBuffer(resultBlob);

				break;
			case "HV_BIN_DEPLOY_METADATA_TABLET_SMALL_IMAGE_READ":

			function getStringUTF8(dataview, offset, length) {
				var s = '';

				for (var i = 0, c; i < length;) {
					c = dataview.getUint8(offset + i++); // 해당 구간 에러
					s += String.fromCharCode(
							c > 0xdf && c < 0xf0 && i < length - 1
									? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
									| dataview.getUint8(offset + i++) & 0x3f
									: c > 0x7f && i < length
											? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
											: c
					);
				}

				return s;
			}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
					offset += imageFileListLength;


					for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

						var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotSmallTabletImgBinaryData(imageFileListSize);



					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

					// if(endsWith(fileName, 'png')){
					if(scwin.platformType == "Android"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						screen_shot_30.setSrc("data:image/;base64,"+imageFileArr[0]);
						screen_shot_29.setSrc("data:image/;base64,"+imageFileArr[1]);

					}else if(scwin.platformType == "iOS"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						for(var isize = 0; isize < imageFileArr.length ; isize++){

							if (isize == 0){
								screen_shot_ios_ipad129.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

							if (isize >= 1){
								for (var shotsize = 0; shotsize < scwin.app_screenshot_ios_ipad_129_itemIdx; shotsize++){
									if(isize == (shotsize + 1)){
										var screen_shot_imageset = $p.getComponentById("screen_shot_ios_30_small_tablet_"+ shotsize);
										screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
									}

								}


							}

						}

					}


					// }

				});
				reader.readAsArrayBuffer(resultBlob);

				break;
			case "HV_BIN_DEPLOY_METADATA_TABLET_LARGE_IMAGE_READ":

			function getStringUTF8(dataview, offset, length) {
				var s = '';

				for (var i = 0, c; i < length;) {
					c = dataview.getUint8(offset + i++); // 해당 구간 에러
					s += String.fromCharCode(
							c > 0xdf && c < 0xf0 && i < length - 1
									? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
									| dataview.getUint8(offset + i++) & 0x3f
									: c > 0x7f && i < length
											? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
											: c
					);
				}

				return s;
			}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
					offset += imageFileListLength;


					for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

						var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotLargeTabletImgBinaryData(imageFileListSize);

					// var fileNameLength = dataView.getInt32(offset);
					// offset += DEFAULT_READ_BYTES;
					//
					// var fileName = getStringUTF8(dataView, offset, fileNameLength);
					// offset += fileNameLength;
					// //
					// var dataLength = dataView.getInt32(offset);
					// offset += DEFAULT_READ_BYTES;



					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

					// if(endsWith(fileName, 'png')){
					if(scwin.platformType == "Android"){

					}else if(scwin.platformType == "iOS"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						for(var isize = 0; isize < imageFileArr.length ; isize++){

							if (isize == 0){
								screen_shot_ios_ipad129_3gen.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

								for (var shotsize = 0; shotsize < scwin.app_screenshot_ios_ipad_129_3gen_itemIdx; shotsize++){
									if(isize == (shotsize+1)){
										var screen_shot_imageset = $p.getComponentById("screen_shot_ios_30_large_tablet_"+ shotsize);
										screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
									}

								}

						}

					}

				});
				reader.readAsArrayBuffer(resultBlob);

				break;
			case "HV_BIN_DEPLOY_ANDROID_METADATA_PHONE_IMAGE_READ":
				function getStringUTF8(dataview, offset, length) {
					var s = '';

					for (var i = 0, c; i < length;) {
						c = dataview.getUint8(offset + i++); // 해당 구간 에러
						s += String.fromCharCode(
								c > 0xdf && c < 0xf0 && i < length - 1
										? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
										| dataview.getUint8(offset + i++) & 0x3f
										: c > 0x7f && i < length
												? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
												: c
						);
					}

					return s;
				}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
					offset += imageFileListLength;


					for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

						var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotPhoneImgBinaryData(imageFileListSize);

					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

					if(scwin.platformType == "Android"){
						// // TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						// screen_shot_android_30.setSrc("data:image/;base64,"+imageFileArr[0]);
						for(var isize = 0; isize < imageFileArr.length ; isize++){

                            if(isize == 0){
								screen_shot_android_phone.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

							for (var shotsize = 0; shotsize < scwin.app_scrennshot_android_phone_itemIdx; shotsize++){
								if(isize == (shotsize+1)){
									var screen_shot_imageset = $p.getComponentById("screen_shot_android_30_phone_"+ shotsize);
									screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
								}

							}

						}



					}

				});
				reader.readAsArrayBuffer(resultBlob);

                break;
			case "HV_BIN_DEPLOY_ANDROID_METADATA_TABLET_7INCH_IMAGE_READ":
				function getStringUTF8(dataview, offset, length) {
					var s = '';

					for (var i = 0, c; i < length;) {
						c = dataview.getUint8(offset + i++); // 해당 구간 에러
						s += String.fromCharCode(
								c > 0xdf && c < 0xf0 && i < length - 1
										? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
										| dataview.getUint8(offset + i++) & 0x3f
										: c > 0x7f && i < length
												? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
												: c
						);
					}

					return s;
				}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
					offset += imageFileListLength;


					for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

						var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotSmallTabletImgBinaryData(imageFileListSize);

					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

					if(scwin.platformType == "Android"){
						for(var isize = 0; isize < imageFileArr.length ; isize++){

                            if(isize == 0){
								screen_shot_android_7inch_tablet.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

							for (var shotsize = 0; shotsize < scwin.app_scrennshot_android_small_tablet_itemIdx; shotsize++){
								if(isize == (shotsize+1)){

									var screen_shot_imageset = $p.getComponentById("screen_shot_android_30_small_tablet_"+ shotsize);
									screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
								}
							}
						}

					}

				});
				reader.readAsArrayBuffer(resultBlob);

                break;
			case "HV_BIN_DEPLOY_ANDROID_METADATA_TABLET_10INCH_IMAGE_READ":
				function getStringUTF8(dataview, offset, length) {
					var s = '';

					for (var i = 0, c; i < length;) {
						c = dataview.getUint8(offset + i++); // 해당 구간 에러
						s += String.fromCharCode(
								c > 0xdf && c < 0xf0 && i < length - 1
										? (c & 0xf) << 12 | (dataview.getUint8(offset + i++) & 0x3f) << 6
										| dataview.getUint8(offset + i++) & 0x3f
										: c > 0x7f && i < length
												? (c & 0x1f) << 6 | dataview.getUint8(offset + i++) & 0x3f
												: c
						);
					}

					return s;
				}

				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					var imageFileArr = [];
					var dataView = new DataView(reader.result);

					var offset = 0;
					var DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

					var adminIdLength = dataView.getInt32(offset);
					offset = DEFAULT_READ_BYTES;

					var adminID = getStringUTF8(dataView, offset, adminIdLength);
					offset += adminIdLength;

					var hqKeyLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var hqKey = getStringUTF8(dataView, offset, hqKeyLength);
					offset += hqKeyLength;

					var binTypeLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var binType = getStringUTF8(dataView, offset, binTypeLength);
					offset += binTypeLength;

					// TODO : image file list size
					// TODO : image 이차원 배열 기준으로 image base64 값을 image src 처리 하는 기능 적용

					var imageFileListLength = dataView.getInt32(offset);
					offset += DEFAULT_READ_BYTES;

					var imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
					offset += imageFileListLength;


					for(let i = 0; i < imageFileListSize; i++){
						// TODO image list 조회 하고
						// 해당 image base64 string 을 image src 로 조회 하는 기능 구현
						var imagefileNameLength = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
						offset += imagefileNameLength;

						var imageFileLangth = dataView.getInt32(offset);
						offset += DEFAULT_READ_BYTES;

						var imageFile = getStringUTF8(dataView, offset, imageFileLangth);
						offset += imageFileLangth;

						imageFileArr.push(imageFile);

					}

					scwin.setScreenShotLargeTabletImgBinaryData(imageFileListSize);

					var a = null,
							pureData = null,
							dataBlob = null,
							downloadURL = null;

					if(scwin.platformType == "Android"){
						// TODO: 해당 구간에서 이미지 파일을 조회 하는 기능 구현해야함..
						for(var isize = 0; isize < imageFileArr.length ; isize++){

                            if(isize == 0){
								screen_shot_android_10inch_tablet.setSrc("data:image/;base64,"+imageFileArr[isize]);
							}

							for (var shotsize = 0; shotsize < scwin.app_scrennshot_android_large_tablet_itemIdx; shotsize++){
								if(isize == (shotsize+1)){

									var screen_shot_imageset = $p.getComponentById("screen_shot_android_30_large_tablet_"+ shotsize);
									screen_shot_imageset.setSrc("data:image/;base64,"+imageFileArr[isize]);
								}
							}
						}

					}

				});
				reader.readAsArrayBuffer(resultBlob);

                break;
			default :
                break;

		}

	}


	/**
	 * 텍스트 데이터 통신 구간
	 * @param obj
	 */
	scwin.webSocketCallback = function (obj) {

		switch (obj.MsgType) {
			case "HV_MSG_DEPLOY_METADATA_IMAGE_STATUS_INFO_FROM_HEADQUATER" :
				scwin.getDeployImageMeatadataStatus(obj);
				break;

			default :
				break;
		}
	};


    scwin.getDeployImageMeatadataStatus = function(obj){

		switch (obj.status) {
			case "IMAGEREADING":
				var message = common.getLabel("lbl_build_js_creating_multi_profile");
				WebSquare.layer.showProcessMessage(message);
				break;
			case "CONFIGUPDATE" :

				break;
			case "DONE" :

				// alert("Deplay Task Config 조회 했습니다.");
				WebSquare.layer.hideProcessMessage();
				break;
			case "SUCCESSFUL":

				break;
			case "FAILED":
				break;
			default :
				break;
		}

	};

	scwin.setDeployMatadataUpdateStatus = function(obj){

		switch (obj.status) {
			case "setMetadata":
				var message = common.getLabel("lbl_app_config_list");
				WebSquare.layer.showProcessMessage(message);
				break;
			case "DONE" :

				alert("Deplay Metadata Config 수정 했습니다.");
				WebSquare.layer.hideProcessMessage();
				break;
			case "SUCCESSFUL":

				break;
			case "FAILED":
				break;
			default :
				break;
		}

	};

	function changeToolTipContentSettingStep1 (componentId, label) {
		let platform = localStorage.getItem("_platform_");
		switch (platform) {
			case "Android":
                var message = common.getLabel("lbl_sign_profile_tip");
				return message
			case "iOS":
                var message = common.getLabel("lbl_ios_sign_profile");
				return message
			default:
				return ""
		}
		
	};

	function endsWith(string, key) {
		var len = string.length;
		var keyLen = key.length;

		if(len < keyLen) {
			return false;
		}

		return string.substring(len - keyLen, len) === key;
	};

	// TODO : ios 디바이스 기준으로 onclick 기능 추가 구현하기
	/**
	 * ios iphone 5.5 인치 동적 이미지 추가 기능
	 * @param e
	 */
	scwin.btn_ios_phone55_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_phone_1_" + scwin.app_scrennshot_ios_itemIdx, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_iphone55_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_phone_1_" + scwin.app_scrennshot_ios_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_phone_" + scwin.app_scrennshot_ios_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot_phone_" + scwin.app_scrennshot_ios_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_phone55_" + scwin.app_scrennshot_ios_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_phone_" + scwin.app_scrennshot_ios_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});


		$p.getComponentById("step3_ios_input_app_screenshot_phone_"+ scwin.app_scrennshot_ios_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_phone55_"+ scwin.app_scrennshot_ios_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_scrennshot_ios_itemIdx++;

	};

	/**
	 * ios iphone 6.5 인치 동적 이미지 추가 기능
	 * @param e
	 */
	scwin.btn_ios_phone65_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_1_" + scwin.app_scrennshot_ios_phone_itemIdx, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_iphone65_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_1_" + scwin.app_scrennshot_ios_phone_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_" + scwin.app_scrennshot_ios_phone_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot" + scwin.app_scrennshot_ios_phone_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_iphone65" + scwin.app_scrennshot_ios_phone_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_" + scwin.app_scrennshot_ios_phone_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});


		$p.getComponentById("step3_ios_input_app_screenshot"+ scwin.app_scrennshot_ios_phone_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_iphone65"+ scwin.app_scrennshot_ios_phone_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_scrennshot_ios_phone_itemIdx++;

	};

	/**
	 * ios ipad pro 12.9 인치 추가 버튼
	 * @param e
	 */
	scwin.btn_ios_ipad129_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_small_tablet_1_" + scwin.app_screenshot_ios_ipad_129_itemIdx, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_ipad129_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_small_tablet_1_" + scwin.app_screenshot_ios_ipad_129_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_small_tablet_" + scwin.app_screenshot_ios_ipad_129_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot_small_tablet_" + scwin.app_screenshot_ios_ipad_129_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_30_small_tablet_" + scwin.app_screenshot_ios_ipad_129_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_small_tablet_" + scwin.app_screenshot_ios_ipad_129_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});


		$p.getComponentById("step3_ios_input_app_screenshot_small_tablet_"+ scwin.app_screenshot_ios_ipad_129_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_30_small_tablet_"+ scwin.app_screenshot_ios_ipad_129_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_screenshot_ios_ipad_129_itemIdx++;

	};


	/**
	 * ios ipad pro 12.9인치 3gen 추가 버튼
	 * @param e
	 */
	scwin.btn_ios_ipad129_3gen_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_large_tablet_1_" + scwin.app_screenshot_ios_ipad_129_3gen_itemIdx, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_ipad129_3gen_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_large_tablet_1_" + scwin.app_screenshot_ios_ipad_129_3gen_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_large_tablet_" + scwin.app_screenshot_ios_ipad_129_3gen_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot_large_tablet_" + scwin.app_screenshot_ios_ipad_129_3gen_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_30_large_tablet_" + scwin.app_screenshot_ios_ipad_129_3gen_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_large_tablet_" + scwin.app_screenshot_ios_ipad_129_3gen_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});


		$p.getComponentById("step3_ios_input_app_screenshot_large_tablet_"+ scwin.app_screenshot_ios_ipad_129_3gen_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_30_large_tablet_"+ scwin.app_screenshot_ios_ipad_129_3gen_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_screenshot_ios_ipad_129_3gen_itemIdx++;

	};


	scwin.btn_android_phone_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_phone_1_" + scwin.app_scrennshot_android_phone_itemIdx, "group", {tagname: "li", className: "w500"}, grp_android_app_screenshot_phone_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_phone_1_" + scwin.app_scrennshot_android_phone_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_phone_" + scwin.app_scrennshot_android_phone_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_android_input_app_screenshot_phone_" + scwin.app_scrennshot_android_phone_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_android_30_phone_" + scwin.app_scrennshot_android_phone_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_android_image_trigger_phone_" + scwin.app_scrennshot_android_phone_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});

		$p.getComponentById("step3_android_input_app_screenshot_phone_"+ scwin.app_scrennshot_android_phone_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_android_30_phone_"+ scwin.app_scrennshot_android_phone_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_scrennshot_android_phone_itemIdx++;

	};


	scwin.btn_android_7inch_tablet_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_small_tablet_1_" + scwin.app_scrennshot_android_small_tablet_itemIdx, "group", {tagname: "li", className: "w500"}, grp_android_app_screenshot_7inch_tablet_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_small_tablet_1_" + scwin.app_scrennshot_android_small_tablet_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_small_tablet_" + scwin.app_scrennshot_android_small_tablet_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_android_input_app_screenshot_small_tablet_" + scwin.app_scrennshot_android_small_tablet_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_android_30_small_tablet_" + scwin.app_scrennshot_android_small_tablet_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_android_image_trigger_small_tablet_" + scwin.app_scrennshot_android_small_tablet_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});

		$p.getComponentById("step3_android_input_app_screenshot_small_tablet_"+ scwin.app_scrennshot_android_small_tablet_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_android_30_small_tablet_"+ scwin.app_scrennshot_android_small_tablet_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_scrennshot_android_small_tablet_itemIdx++;

	};

	scwin.btn_android_10inch_tablet_append_upload_image_onclick = function (e) {

		// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
		var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_large_tablet_1_" + scwin.app_scrennshot_android_large_tablet_itemIdx, "group", {tagname: "li", className: "w500"}, grp_android_app_screenshot_10inch_tablet_id_viewer);

		var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_large_tablet_1_" + scwin.app_scrennshot_android_large_tablet_itemIdx, "group", {className: "ipt_app_screen_box"}, group_li1);

		var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_large_tablet_" + scwin.app_scrennshot_android_large_tablet_itemIdx, "group", {className: "thumb"}, group_box1);

		var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_android_input_app_screenshot_large_tablet_" + scwin.app_scrennshot_android_large_tablet_itemIdx, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

		var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_android_30_large_tablet_" + scwin.app_scrennshot_android_large_tablet_itemIdx, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

		var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_android_image_trigger_large_tablet_" + scwin.app_scrennshot_android_large_tablet_itemIdx, "trigger", {className: "btn_cm"}, group_box1);
		var message_dash = common.getLabel("lbl_dash");
		append_output_ios_image_trigger.setLabel(message_dash); // -
		// server info
		// trigger, onclick 기능 추가 -> 삭제 기능
		append_output_ios_image_trigger.bind("onclick", function (e) {
			var save_group_li1 = group_li1;

			save_group_li1.remove();

		});

		$p.getComponentById("step3_android_input_app_screenshot_large_tablet_"+ scwin.app_scrennshot_android_large_tablet_itemIdx).render.addEventListener("change", function (event) {
			var files = event.target.files[0];

			const reader = new FileReader();
			reader.readAsDataURL(files);
			reader.onloadend = () => {
				const base64data = reader.result;
				var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_android_30_large_tablet_"+ scwin.app_scrennshot_android_large_tablet_itemIdx);
				screen_shot_ios_30_tmp.setSrc(base64data);
			}

		},false);

		// itemIdx++ 형식으로 id 채번 하기
		scwin.app_scrennshot_android_large_tablet_itemIdx++;

	};


	scwin.setScreenShotImgBinaryData = function (cnt) {


        if(scwin.platformType == "Android"){


		}else if(scwin.platformType == "iOS"){

            for(var i = 0; i<cnt - 1 ; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_1_" + i, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_iphone65_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_iphone65" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});


				$p.getComponentById("step3_ios_input_app_screenshot"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_iphone65"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// itemIdx++ 형식으로 id 채번 하기
				scwin.app_scrennshot_ios_itemIdx++;

			}


		}

	};

	scwin.setScreenShotPhoneImgBinaryData = function (cnt) {


		if(scwin.platformType == "Android"){

			for(var i = 0; i<cnt - 1 ; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_phone_1_" + i, "group", {tagname: "li", className: "w500"}, grp_android_app_screenshot_phone_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_phone_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_phone_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_android_input_app_screenshot_phone_" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_android_30_phone_" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_android_image_trigger_phone_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});


				$p.getComponentById("step3_android_input_app_screenshot_phone_"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_android_30_phone_"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// itemIdx++ 형식으로 id 채번 하기
				scwin.app_scrennshot_android_phone_itemIdx++;

			}


		}else if(scwin.platformType == "iOS"){

			for(var i = 0; i<cnt - 1 ; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_phone_1_" + i, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_iphone55_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_phone_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_phone_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot_phone_" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_phone55_" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_phone_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});


				$p.getComponentById("step3_ios_input_app_screenshot_phone_"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_phone55_"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// itemIdx++ 형식으로 id 채번 하기
				scwin.app_scrennshot_ios_phone_itemIdx++;

			}


		}

	};


	scwin.setScreenShotLargeTabletImgBinaryData = function (cnt) {


		if(scwin.platformType == "Android"){
			for(var i = 0; i<cnt - 1; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_large_tablet_1_" + i, "group", {tagname: "li", className: "w500"}, grp_android_app_screenshot_10inch_tablet_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_large_tablet_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_large_tablet_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_android_input_app_screenshot_large_tablet_" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_android_30_large_tablet_" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_android_image_trigger_large_tablet_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});


				$p.getComponentById("step3_android_input_app_screenshot_large_tablet_"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_android_30_large_tablet_"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// app_scrennshot_android_large_tablet_itemIdx++ 형식으로 id 채번 하기
				scwin.app_scrennshot_android_large_tablet_itemIdx++;

			}

		}else if(scwin.platformType == "iOS"){

			for(var i = 0; i<(cnt-1) ; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_large_tablet_1_" + i, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_ipad129_3gen_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_large_tablet_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_large_tablet_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot_large_tablet_" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_30_large_tablet_" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_large_tablet_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});


				$p.getComponentById("step3_ios_input_app_screenshot_large_tablet_"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_30_large_tablet_"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// itemIdx++ 형식으로 id 채번 하기
				scwin.app_screenshot_ios_ipad_129_3gen_itemIdx++;

			}


		}

	};


	scwin.setScreenShotSmallTabletImgBinaryData = function (cnt) {


		if(scwin.platformType == "Android"){
			for(var i = 0; i<cnt-1 ; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_small_tablet_1_" + i, "group", {tagname: "li", className: "w500"}, grp_android_app_screenshot_7inch_tablet_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_small_tablet_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_small_tablet_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_android_input_app_screenshot_small_tablet_" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_android_30_small_tablet_" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_android_image_trigger_small_tablet_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});

				$p.getComponentById("step3_android_input_app_screenshot_small_tablet_"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_android_30_small_tablet_"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// itemIdx++ 형식으로 id 채번 하기
				scwin.app_scrennshot_android_small_tablet_itemIdx++;

			}

		}else if(scwin.platformType == "iOS"){

			for(var i = 0; i<(cnt-1) ; i++){

				var group_li1 = WebSquare.util.dynamicCreate("myid_group_li_small_tablet_1_" + i, "group", {tagname: "li", className: "w500"}, grp_ios_app_screenshot_ipad129_id_viewer);

				var group_box1 = WebSquare.util.dynamicCreate("myid_group_box_small_tablet_1_" + i, "group", {className: "ipt_app_screen_box"}, group_li1);

				var group_thumb1 = WebSquare.util.dynamicCreate("myid_group_thumb1_small_tablet_" + i, "group", {className: "thumb"}, group_box1);

				var append_appscreenshot_file_upload = WebSquare.util.dynamicCreate("step3_ios_input_app_screenshot_small_tablet_" + i, "upload", {inputStyle: "position:absolute;vertical-align:middle;word-wrap:break-word", style: "position: relative;width: 250px;height: 23px;", imageStyle: "position:absolute;vertical-align:middle;word-wrap:break-word"}, group_thumb1);

				var append_screen_shot_ios_30_image = WebSquare.util.dynamicCreate("screen_shot_ios_30_small_tablet_" + i, "image", {src: "/images/contents/img_view_36x36.png"}, group_thumb1);

				var append_output_ios_image_trigger = WebSquare.util.dynamicCreate("myid_ios_image_trigger_small_tablet_" + i, "trigger", {className: "btn_cm"}, group_box1);
				var message_dash = common.getLabel("lbl_dash");
				append_output_ios_image_trigger.setLabel(message_dash); // -
				// server info
				// trigger, onclick 기능 추가 -> 삭제 기능
				append_output_ios_image_trigger.bind("onclick", function (e) {
					var save_group_li1 = group_li1;

					save_group_li1.remove();

				});


				$p.getComponentById("step3_ios_input_app_screenshot_small_tablet_"+ i).render.addEventListener("change", function (event) {
					var files = event.target.files[0];

					const reader = new FileReader();
					reader.readAsDataURL(files);
					reader.onloadend = () => {
						const base64data = reader.result;

						var screen_shot_ios_30_tmp  = $p.getComponentById("screen_shot_ios_30_small_tablet_"+ i);
						screen_shot_ios_30_tmp.setSrc(base64data);
					}

				},false);

				// itemIdx++ 형식으로 id 채번 하기
				scwin.app_screenshot_ios_ipad_129_itemIdx++;

			}


		}

	};


	
	
}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload','ev:onpageunload':'scwin.onpageunload'},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'gal_tit fl',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_deploy_metadata_screen_shot_title'}},{T:1,N:'xf:group',A:{class:'fr',id:'fr_grp_proj_save',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',id:'',style:'',class:'txt_chk',useLocale:'true',localeRef:''}},{T:1,N:'xf:trigger',A:{id:'btn_vcspull_1',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box iOS',id:'grp_iOS',style:''},E:[{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{id:'',style:'',class:'form_wrap'},E:[{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_iphone55'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_ios_app_screenshot_iphone55_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_ios_input_app_screenshot_iphone55_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_ios_iphone55',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_ios_phone55_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_iphone65'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_ios_app_screenshot_iphone65_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_ios_input_app_screenshot_iphone65_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_ios_iphone65_path',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_ios_phone65_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_ipad129'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_ios_app_screenshot_ipad129_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_ios_input_app_screenshot_ipad129_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_ios_ipad129',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_ios_ipad129_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_ipad129_3gen'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_ios_app_screenshot_ipad129_3gen_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_ios_input_app_screenshot_ipad129_3gen_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_ios_ipad129_3gen',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_ios_ipad129_3gen_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_ios_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_ios_prev_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'이전'}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'gallery_box android',id:'grp_android',style:''},E:[{T:1,N:'xf:group',A:{class:'gal_body',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_android_app_screen_shot_phone'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_android_app_screenshot_phone_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_android_input_app_screenshot_phone_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_android_phone',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_android_phone_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_android_app_screen_shot_7inch_tablet'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_android_app_screenshot_7inch_tablet_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_android_input_app_screenshot_7inch_tablet_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_android_7inch_tablet',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_android_7inch_tablet_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'screen_shot_box',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'form_name',id:'',label:'',style:'',useLocale:'true',localeRef:'lbl_android_app_screen_shot_10inch_tablet'}},{T:1,N:'xf:group',A:{class:'viewer',id:'grp_android_app_screenshot_10inch_tablet_id_viewer',style:'',tagname:'ul'},E:[{T:1,N:'xf:group',A:{class:'w500',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'ipt_app_screen_box',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'thumb',id:'',style:''},E:[{T:1,N:'w2:upload',A:{inputStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',type:'',id:'step2_android_input_app_screenshot_10inch_tablet_path',style:'position: relative;width: 250px;height: 23px;',imageStyle:'position:absolute;vertical-align:middle;word-wrap:break-word',disabled:'false',class:''}},{T:1,N:'xf:image',A:{id:'screen_shot_android_10inch_tablet',src:'/images/contents/img_view_36x36.png',style:''}}]},{T:1,N:'xf:group',A:{id:'',style:'',class:'ipt_box',tagname:''},E:[{T:1,N:'xf:trigger',A:{id:'',style:'',class:'btn_cm',type:'button','ev:onclick':'scwin.btn_android_10inch_tablet_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_plus'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]},{T:1,N:'w2:anchor',A:{class:'gal_prev',id:'btn_android_prev',outerDiv:'false',style:'','ev:onclick':'scwin.btn_android_prev_onclick'},E:[{T:1,N:'xf:label',E:[{T:4,cdata:'이전'}]}]}]}]}]}]}]})