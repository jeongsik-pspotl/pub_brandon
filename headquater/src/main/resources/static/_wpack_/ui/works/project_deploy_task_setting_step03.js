/*amd /cm/ui/works/project_deploy_task_setting_step03.xml 61151 df05211959dc4c03c064627738be4f4153f2d90f1117768ac666624349557f8e */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
						scwin.platformType = "";
						scwin.app_scrennshot_ios_itemIdx = 0;
						scwin.app_scrennshot_ios_phone_itemIdx = 0;
						scwin.app_screenshot_ios_ipad_129_itemIdx = 0;
						scwin.app_screenshot_ios_ipad_129_3gen_itemIdx = 0;
						scwin.app_scrennshot_android_phone_itemIdx = 0;
						scwin.app_scrennshot_android_small_tablet_itemIdx = 0;
						scwin.app_scrennshot_android_large_tablet_itemIdx = 0;
						scwin.onpageload = function() {

							let deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
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
							// let build_project_json = $p.parent().dtl_build_setting_step1.getAllJSON();
							// 환경설정 List 조회

							// TODO : test deploy task api
							scwin.searchStep1Data();

							// if(scwin.platformType == "Android"){
							//
							// 	const fileId = "#gen_ios_profile_" + pf + "_ios_provisioning_profile";
							// 	document.querySelector(fileId).addEventListener("change",scwin.iosIconChange,false);
							//
							// 	$p.getComponentById("step2_android_input_app_screenshot1_path").render.addEventListener("change", function (event) {
							// 		let files = event.target.files[0];
							//
							// 		const reader = new FileReader();
							// 		reader.readAsDataURL(files);
							// 		reader.onloadend = () => {
							// 			const base64data = reader.result;
							//
							// 			screen_shot_29.setSrc(base64data);
							// 		}
							//
							// 	},false);
							//
							//
							// }else if(scwin.platformType == "iOS"){
							//
							// 	$p.getComponentById("step2_ios_input_app_screenshot_iphone55_path").render.addEventListener("change", function (event) {
							// 		let files = event.target.files[0];
							//
							// 		const reader = new FileReader();
							// 		reader.readAsDataURL(files);
							// 		reader.onloadend = () => {
							// 			const base64data = reader.result;
							//
							// 			screen_shot_ios_iphone55.setSrc(base64data);
							// 		}
							//
							// 	},false);
							//
							//
							// }

						};


						scwin.searchStep1Data = async function(){

							let deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							let project_name = ""; //step1_input_projectname.getValue();
							scwin.platformType = deployTaskData[0].platform; //step1_select_platform.getValue();

							let description = "";//step1_txtarea_desc.getValue();

							// workspace id 값 세팅
							let local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
							let local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

							let data = {};
							data.project_id = parseInt(local_buildproject_id);
							data.workspace_id = parseInt(local_workspace_id);
							data.project_name = project_name;
							data.platform = scwin.platformType;
							data.description = description;
							data.status = 1; // build project 사용여부 옵션 기능 추가시 필요.

							const url = common.uri.deploySettingMetadataImageList;
							const headers = {"Content-Type": "application/json; charset=UTF-8"};
							const body = data;

							await common.http.fetchPost(url,headers, body)
								.then(async(res) => {

								}).catch((err)=>{
									common.win.alert("error status:"+err.status+", message:"+err.message);
								});


						};


						scwin.saveStep1Data = function(){

							let formData = new FormData();
							let deployTaskData = $p.parent().$p.parent().__deploytask_data__.getAllJSON();
							let platform = scwin.platformType;//step1_select_platform.getValue();

							let description = "";//step1_txtarea_desc.getValue();

							// workspace id 값 세팅
							let local_workspace_id = deployTaskData[0].workspace_pkid; // change to data list
							let local_buildproject_id = deployTaskData[0].project_pkid; // change to data list

							let deployJsonData = {};
							deployJsonData.project_id = parseInt(local_buildproject_id);
							deployJsonData.workspace_id = parseInt(local_workspace_id);
							deployJsonData.platform = platform;
							deployJsonData.description = description;

							formData.append("projectDeploytJson", JSON.stringify(deployJsonData));

							if(scwin.platformType == "Android"){


								for(let iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_android_phone_itemIdx;iosImgCnt++){
									let fileId = "#gen_android_screen_shot_phone_" + iosImgCnt + "_android_screen_shot_android_phone_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];
									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_ko-KR.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("phoneImagefile", save_screen_shot_uploadfile);
										}
									}


								}


								for(let iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_android_small_tablet_itemIdx;iosImgCnt++){
									let fileId = "#gen_android_screen_shot_7inch_tablet_" + iosImgCnt + "_android_screen_shot_android_tablet_7inch_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];
									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_ko-KR.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("sevenInchTabletImagefile", save_screen_shot_uploadfile);
										}
									}



								}

								for(let iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_android_large_tablet_itemIdx;iosImgCnt++){
									let fileId = "#gen_android_screen_shot_10inch_tablet_" + iosImgCnt + "_android_screen_shot_android_tablet_10inch_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];
									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_ko-KR.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("tenInchTabletImagefile", save_screen_shot_uploadfile);
										}
									}


								}

								if(formData.get("imagefile") == null || formData === undefined){
									const message = common.getLabel("lbl_deploy_metadata_screen_shot_image_data_file_path_check");
									common.win.alert(message);
									return false;
								}

								common.uri.deploySettingMetadataAndroidImageUpdate
								common.http.fetchFileUpload(uri, "POST", formData).then((res) => {
									const message = common.getLabel("lbl_deploy_metadata_screen_shot_image_update_ok");
									common.win.alert(message);

								}).catch((err) => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n" + "error:" + err);
								});


							}else if(scwin.platformType == "iOS"){

								for(let iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_ios_itemIdx;iosImgCnt++){
									let fileId = "#gen_ios_screen_shot_ios_iphone65_" + iosImgCnt+ "_ios_screen_shot_iphone65_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];
									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPHONE_65_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("imagefile", save_screen_shot_uploadfile);
										}

									}


								}

								for(let iosImgCnt = 0; iosImgCnt < scwin.app_scrennshot_ios_phone_itemIdx;iosImgCnt++){
									let fileId = "#gen_ios_screen_shot_ios_iphone55_" + iosImgCnt + "_ios_screen_shot_iphone55_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];
									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPHONE_55_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("imagefile", save_screen_shot_uploadfile);
										}
									}




								}

								for(let iosImgCnt = 0; iosImgCnt < scwin.app_screenshot_ios_ipad_129_itemIdx;iosImgCnt++){
									let fileId = "#gen_ios_screen_shot_ios_ipad129_" + iosImgCnt + "_ios_screen_shot_ipad129_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];

									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPAD_PRO_129_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("imagefile", save_screen_shot_uploadfile);
										}
									}


								}

								for(let iosImgCnt = 0; iosImgCnt < scwin.app_screenshot_ios_ipad_129_3gen_itemIdx;iosImgCnt++){
									let fileId = "#gen_ios_screen_shot_ios_ipad129_3gen_" + iosImgCnt + "_ios_screen_shot_ipad129_3gen_file";
									let screen_shot_uploadfile = document.querySelector(fileId).files[0];

									if(screen_shot_uploadfile === undefined){

									}else {
										let screen_shot_uploadfile_blob = screen_shot_uploadfile.slice(0, screen_shot_uploadfile.size, 'image/png');
										let save_screen_shot_uploadfile = new File([screen_shot_uploadfile_blob], (iosImgCnt+1)+'_APP_IPAD_PRO_3GEN_129_'+(iosImgCnt+1)+'.png',{type: screen_shot_uploadfile.type});
										if(save_screen_shot_uploadfile === undefined){

										}else {
											formData.append("imagefile", save_screen_shot_uploadfile);
										}
									}


								}

								if(formData.get("imagefile") == null || formData === undefined){
									const message = common.getLabel("lbl_deploy_metadata_screen_shot_image_data_file_path_check");
									common.win.alert(message);
									return false;
								}

								const uri = common.uri.deploySettingMetadataiOSImageUpdate;
								common.http.fetchFileUpload(uri, "POST", formData).then((res) => {
									const message = common.getLabel("lbl_deploy_metadata_screen_shot_image_update_ok");
									common.win.alert(message);
								}).catch((err) => {
									common.win.alert("code:" + err.status + "\n" + "message:" + err.responseText + "\n" + "error:" + err);
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
						scwin.getBinDeployMetadataImageRead = function (binType, resultBlob) {

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {
								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotImgBinaryData(imageFileArr);

							});
							reader.readAsArrayBuffer(resultBlob);

						};

						scwin.getBinDeployMetadataPhoneImageRead = function (binType, resultBlob) {

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {

								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotPhoneImgBinaryData(imageFileArr);

							});
							reader.readAsArrayBuffer(resultBlob);

						};

						scwin.getBinDeployMetadataTabletSmallImageRead = function (binType, resultBlob){

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {

								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotSmallTabletImgBinaryData(imageFileArr);


							});
							reader.readAsArrayBuffer(resultBlob);

						};

						scwin.getBinDeployMetadataTabletLargeImageRead = function (binType, resultBlob){

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {

								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotLargeTabletImgBinaryData(imageFileArr);

							});
							reader.readAsArrayBuffer(resultBlob);

						};

						scwin.getBinDeployAndroidMetadataPhoneImageRead = function(binType, resultBlob){

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {

								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotPhoneImgBinaryData(imageFileArr);

							});
							reader.readAsArrayBuffer(resultBlob);

						};

						scwin.getBinDeployAndroidMetadataTablet7InchImageRead =  function (binType, resultBlob){

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {

								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotSmallTabletImgBinaryData(imageFileArr);

							});
							reader.readAsArrayBuffer(resultBlob);
						};

						scwin.getBinDeployAndroidMetadataTablet10InchImageRead = function (binType, resultBlob){

							function getStringUTF8(dataview, offset, length) {
								let s = '';

								for (let i = 0, c; i < length;) {
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

							let reader = new FileReader();
							reader.addEventListener("loadend", function() {

								let imageFileArr = [];
								let dataView = new DataView(reader.result);

								let offset = 0;
								let DEFAULT_READ_BYTES = Uint32Array.BYTES_PER_ELEMENT;

								let adminIdLength = dataView.getInt32(offset);
								offset = DEFAULT_READ_BYTES;

								let adminID = getStringUTF8(dataView, offset, adminIdLength);
								offset += adminIdLength;

								let hqKeyLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let hqKey = getStringUTF8(dataView, offset, hqKeyLength);
								offset += hqKeyLength;

								let binTypeLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let binType = getStringUTF8(dataView, offset, binTypeLength);
								offset += binTypeLength;

								let imageFileListLength = dataView.getInt32(offset);
								offset += DEFAULT_READ_BYTES;

								let imageFileListSize = getStringUTF8(dataView, offset, imageFileListLength);
								offset += imageFileListLength;


								for(let i = 0; i < imageFileListSize; i++){
									let imagefileNameLength = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imagefileName = getStringUTF8(dataView, offset, imagefileNameLength);
									offset += imagefileNameLength;

									let imageFileLangth = dataView.getInt32(offset);
									offset += DEFAULT_READ_BYTES;

									let imageFile = getStringUTF8(dataView, offset, imageFileLangth);
									offset += imageFileLangth;

									imageFileArr.push(imageFile);

								}

								scwin.setScreenShotLargeTabletImgBinaryData(imageFileArr);

							});
							reader.readAsArrayBuffer(resultBlob);

						};

						scwin.getDeployImageMeatadataStatus = function(obj){

							switch (obj.status) {
								case "IMAGEREADING":
									let message = common.getLabel("lbl_deploy_metadata_screen_shot_image_data_load");
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
							let message;
							switch (obj.status) {
								case "setMetadata":
									message = common.getLabel("lbl_app_config_list");
									WebSquare.layer.showProcessMessage(message);
									break;
								case "DONE" :
									WebSquare.layer.hideProcessMessage();
									message = common.getLabel("lbl_changed_project_deploy_meta_config");
									common.win.alert(message);
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
							let message = ""
							switch (platform) {
								case "Android":
									message = common.getLabel("lbl_sign_profile_tip");
									return message
								case "iOS":
									message = common.getLabel("lbl_ios_sign_profile");
									return message
								default:
									return ""
							}
						};

						function endsWith(string, key) {
							let len = string.length;
							let keyLen = key.length;

							if(len < keyLen) {
								return false;
							}

							return string.substring(len - keyLen, len) === key;
						};

						/**
						 * ios iphone 5.5 인치 동적 이미지 추가 기능
						 * @param e
						 */
						scwin.btn_ios_phone55_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){
								const genId = gen_ios_screen_shot_ios_iphone55.insertChild();
								let screen_shot_ios_iphone55 = gen_ios_screen_shot_ios_iphone55.getChild(genId, "screen_shot_ios_iphone55");
								let iphone55_plus_and_minus_trigger = gen_ios_screen_shot_ios_iphone55.getChild(genId, "iphone55_plus_and_minus_trigger");

								iphone55_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");
								iphone55_plus_and_minus_trigger.setUserData("buttoncheck","minus");

								let fileId = "#gen_ios_screen_shot_ios_iphone55_" + genId + "_ios_screen_shot_iphone55_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_ios_iphone55.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);

							}else if(buttoncheck == "minus"){
								gen_ios_screen_shot_ios_iphone55.removeChild(this.getGeneratedIndex());
							}

							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_scrennshot_ios_itemIdx++;

						};

						/**
						 * ios iphone 6.5 인치 동적 이미지 추가 기능
						 * @param e
						 */
						scwin.btn_ios_phone65_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){
								const genId = gen_ios_screen_shot_ios_iphone65.insertChild();
								let screen_shot_ios_iphone65 = gen_ios_screen_shot_ios_iphone65.getChild(genId, "screen_shot_ios_iphone65_path");
								let iphone65_plus_and_minus_trigger = gen_ios_screen_shot_ios_iphone65.getChild(genId, "iphone65_plus_and_minus_trigger");

								iphone65_plus_and_minus_trigger.setUserData("buttoncheck","minus");
								iphone65_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

								let fileId = "#gen_ios_screen_shot_ios_iphone65_" + genId+ "_ios_screen_shot_iphone65_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_ios_iphone65.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);

							}else if(buttoncheck == "minus"){
								gen_ios_screen_shot_ios_iphone65.removeChild(this.getGeneratedIndex());
							}

							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_scrennshot_ios_phone_itemIdx++;

						};

						/**
						 * ios ipad pro 12.9 인치 추가 버튼
						 * @param e
						 */
						scwin.btn_ios_ipad129_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){
								const genId = gen_ios_screen_shot_ios_ipad129.insertChild();
								let screen_shot_ios_ipad129 = gen_ios_screen_shot_ios_ipad129.getChild(genId, "screen_shot_ios_ipad129");
								let ipad129_plus_and_minus_trigger = gen_ios_screen_shot_ios_ipad129.getChild(genId, "ipad129_plus_and_minus_trigger");

								ipad129_plus_and_minus_trigger.setUserData("buttoncheck","minus");
								ipad129_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

								let fileId = "#gen_ios_screen_shot_ios_ipad129_" + genId + "_ios_screen_shot_ipad129_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_ios_ipad129.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);

							}else if(buttoncheck == "minus"){
								gen_ios_screen_shot_ios_ipad129.removeChild(this.getGeneratedIndex());
							}

							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_screenshot_ios_ipad_129_itemIdx++;

						};


						/**
						 * ios ipad pro 12.9인치 3gen 추가 버튼
						 * @param e
						 */
						scwin.btn_ios_ipad129_3gen_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){
								const genId = gen_ios_screen_shot_ios_ipad129_3gen.insertChild();
								let screen_shot_ios_ipad129_3gen = gen_ios_screen_shot_ios_ipad129_3gen.getChild(genId, "screen_shot_ios_ipad129_3gen");
								let ipad129_3gen_plus_and_minus_trigger = gen_ios_screen_shot_ios_ipad129_3gen.getChild(genId, "ipad129_3gen_plus_and_minus_trigger");

								ipad129_3gen_plus_and_minus_trigger.setUserData("buttoncheck","minus");
								ipad129_3gen_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

								let fileId = "#gen_ios_screen_shot_ios_ipad129_3gen_" + genId + "_ios_screen_shot_ipad129_3gen_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_ios_ipad129_3gen.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);


							}else if(buttoncheck == "minus"){
								gen_ios_screen_shot_ios_ipad129_3gen.removeChild(this.getGeneratedIndex());
							}



							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_screenshot_ios_ipad_129_3gen_itemIdx++;

						};

						scwin.btn_android_phone_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){

								const genId = gen_android_screen_shot_phone.insertChild();
								let screen_shot_android_phone = gen_android_screen_shot_phone.getChild(genId, "screen_shot_android_phone");
								let android_phone_plus_and_minus_trigger = gen_android_screen_shot_phone.getChild(genId, "android_phone_plus_and_minus_trigger");

								android_phone_plus_and_minus_trigger.setUserData("buttoncheck","minus");
								android_phone_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

								let fileId = "#gen_android_screen_shot_phone_" + genId + "_android_screen_shot_android_phone_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_android_phone.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);

							}else if(buttoncheck == "minus"){
								gen_android_screen_shot_phone.removeChild(this.getGeneratedIndex());
							}



							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_scrennshot_android_phone_itemIdx++;

						};


						scwin.btn_android_7inch_tablet_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){
								// 이전 방식으로 하는 게 아나리 웹스퀘어 api 호출 방식으로 처리하기
								const genId = gen_android_screen_shot_7inch_tablet.insertChild();
								let screen_shot_android_7inch_tablet = gen_android_screen_shot_7inch_tablet.getChild(genId, "screen_shot_android_7inch_tablet");
								let android_7inch_tablet_plus_and_minus_trigger = gen_android_screen_shot_7inch_tablet.getChild(genId, "android_7inch_tablet_plus_and_minus_trigger");

								android_7inch_tablet_plus_and_minus_trigger.setUserData("buttoncheck","minus");
								android_7inch_tablet_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

								let fileId = "#gen_android_screen_shot_7inch_tablet_" + genId + "_android_screen_shot_android_tablet_7inch_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_android_7inch_tablet.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);

							}else if(buttoncheck == "minus"){
								gen_android_screen_shot_7inch_tablet.removeChild(this.getGeneratedIndex());

							}



							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_scrennshot_android_small_tablet_itemIdx++;

						};

						scwin.btn_android_10inch_tablet_append_upload_image_onclick = function (e) {

							let buttoncheck = this.getUserData("buttoncheck");

							if(buttoncheck == "plus"){
								const genId = gen_android_screen_shot_10inch_tablet.insertChild();
								let screen_shot_android_10inch_tablet = gen_android_screen_shot_10inch_tablet.getChild(genId, "screen_shot_android_10inch_tablet");
								let android_10inch_tablet_plus_and_minus_trigger = gen_android_screen_shot_10inch_tablet.getChild(genId, "android_10inch_tablet_plus_and_minus_trigger");

								android_10inch_tablet_plus_and_minus_trigger.setUserData("buttoncheck","minus");
								android_10inch_tablet_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

								let fileId = "#gen_android_screen_shot_10inch_tablet_" + genId + "_android_screen_shot_android_tablet_10inch_file";
								document.querySelector(fileId).addEventListener("change",function(e){
									const file = e.target.files[0];
									let reader = new FileReader();
									reader.addEventListener("load",function () {
										screen_shot_android_10inch_tablet.setSrc(this.result);
									},false);
									if (file) {
										reader.readAsDataURL(file);
									}
								},false);

							}else if(buttoncheck == "minus"){
								gen_android_screen_shot_10inch_tablet.removeChild(this.getGeneratedIndex());
							}


							// itemIdx++ 형식으로 id 채번 하기
							scwin.app_scrennshot_android_large_tablet_itemIdx++;

						};


						scwin.setScreenShotImgBinaryData = function (imageFileArr) {


							if(scwin.platformType == "iOS"){

								for(let i = 0; i<imageFileArr.length; i++){
									const genId = gen_ios_screen_shot_ios_iphone65.insertChild();
									let screen_shot_ios_iphone65_path = gen_ios_screen_shot_ios_iphone65.getChild(genId, "screen_shot_ios_iphone65_path")
									screen_shot_ios_iphone65_path.setSrc("data:image/;base64,"+imageFileArr[i]);
									let iphone65_plus_and_minus_trigger = gen_ios_screen_shot_ios_iphone65.getChild(genId, "iphone65_plus_and_minus_trigger");

									let fileId = "#gen_ios_screen_shot_ios_iphone65_" + i + "_ios_screen_shot_iphone65_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_ios_iphone65_path.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);

									if(i == 0){
										iphone65_plus_and_minus_trigger.setUserData("buttoncheck","plus");

									}else {
										iphone65_plus_and_minus_trigger.setUserData("buttoncheck","minus");
										iphone65_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

									}

									// itemIdx++ 형식으로 id 채번 하기
									scwin.app_scrennshot_ios_itemIdx++;

								}


							}

						};

						scwin.setScreenShotPhoneImgBinaryData = function (imageFileArr) {


							if(scwin.platformType == "Android"){

								for(let i = 0; i<imageFileArr.length ; i++){

									const genId = gen_android_screen_shot_phone.insertChild();

									let screen_shot_android_phone = gen_android_screen_shot_phone.getChild(genId, "screen_shot_android_phone");
									screen_shot_android_phone.setSrc("data:image/;base64,"+imageFileArr[i]);
									let android_phone_plus_and_minus_trigger = gen_android_screen_shot_phone.getChild(genId, "android_phone_plus_and_minus_trigger");

									let fileId = "#gen_android_screen_shot_phone_" + i + "_android_screen_shot_android_phone_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_android_phone.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);

									if(i == 0){
										android_phone_plus_and_minus_trigger.setUserData("buttoncheck","plus");

									}else {
										android_phone_plus_and_minus_trigger.setUserData("buttoncheck","minus");
										android_phone_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

									}



									// itemIdx++ 형식으로 id 채번 하기
									scwin.app_scrennshot_android_phone_itemIdx++;

								}


							}else if(scwin.platformType == "iOS"){

								for(let i = 0; i<imageFileArr.length ; i++){

									const genId = gen_ios_screen_shot_ios_iphone55.insertChild();

									let screen_shot_ios_iphone55 = gen_ios_screen_shot_ios_iphone55.getChild(genId, "screen_shot_ios_iphone55");
									screen_shot_ios_iphone55.setSrc("data:image/;base64,"+imageFileArr[i]);
									let iphone55_plus_and_minus_trigger = gen_ios_screen_shot_ios_iphone55.getChild(genId, "iphone55_plus_and_minus_trigger");

									let fileId = "#gen_ios_screen_shot_ios_iphone55_" + i + "_ios_screen_shot_iphone55_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_ios_iphone55.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);

									if(i == 0){
										iphone55_plus_and_minus_trigger.setUserData("buttoncheck","plus");
									}else {
										iphone55_plus_and_minus_trigger.setUserData("buttoncheck","minus");
										iphone55_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

									}


									// itemIdx++ 형식으로 id 채번 하기
									scwin.app_scrennshot_ios_phone_itemIdx++;

								}


							}

						};


						scwin.setScreenShotLargeTabletImgBinaryData = function (imageFileArr) {


							if(scwin.platformType == "Android"){
								for(let i = 0; i<imageFileArr.length ; i++){

									const genId = gen_android_screen_shot_10inch_tablet.insertChild();

									let screen_shot_android_10inch_tablet = gen_android_screen_shot_10inch_tablet.getChild(genId, "screen_shot_android_10inch_tablet");
									screen_shot_android_10inch_tablet.setSrc("data:image/;base64,"+imageFileArr[i]);
									let android_10inch_tablet_plus_and_minus_trigger = gen_android_screen_shot_10inch_tablet.getChild(genId, "android_10inch_tablet_plus_and_minus_trigger");

									let fileId = "#gen_android_screen_shot_10inch_tablet_" + i + "_android_screen_shot_android_tablet_10inch_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_android_10inch_tablet.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);

									if(i == 0){
										android_10inch_tablet_plus_and_minus_trigger.setUserData("buttoncheck","plus");
									}else {
										android_10inch_tablet_plus_and_minus_trigger.setUserData("buttoncheck","minus");
										android_10inch_tablet_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

									}


									// app_scrennshot_android_large_tablet_itemIdx++ 형식으로 id 채번 하기
									scwin.app_scrennshot_android_large_tablet_itemIdx++;

								}

							}else if(scwin.platformType == "iOS"){

								for(let i = 0; i<imageFileArr.length ; i++){

									const genId = gen_ios_screen_shot_ios_ipad129_3gen.insertChild();

									let screen_shot_ios_ipad129_3gen = gen_ios_screen_shot_ios_ipad129_3gen.getChild(genId, "screen_shot_ios_ipad129_3gen");
									screen_shot_ios_ipad129_3gen.setSrc("data:image/;base64,"+imageFileArr[i]);
									let ipad129_3gen_plus_and_minus_trigger = gen_ios_screen_shot_ios_ipad129_3gen.getChild(genId, "ipad129_3gen_plus_and_minus_trigger");

									let fileId = "#gen_ios_screen_shot_ios_ipad129_3gen_" + i + "_ios_screen_shot_ipad129_3gen_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_ios_ipad129_3gen.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);


									if(i == 0){
										ipad129_3gen_plus_and_minus_trigger.setUserData("buttoncheck","plus");
									}else {
										ipad129_3gen_plus_and_minus_trigger.setUserData("buttoncheck","minus");
										ipad129_3gen_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

									}

									// itemIdx++ 형식으로 id 채번 하기
									scwin.app_screenshot_ios_ipad_129_3gen_itemIdx++;

								}


							}

						};


						scwin.setScreenShotSmallTabletImgBinaryData = function (imageFileArr) {


							if(scwin.platformType == "Android"){
								for(let i = 0; i<imageFileArr.length ; i++){

									const genId = gen_android_screen_shot_7inch_tablet.insertChild();

									let screen_shot_android_7inch_tablet = gen_android_screen_shot_7inch_tablet.getChild(genId, "screen_shot_android_7inch_tablet");
									screen_shot_android_7inch_tablet.setSrc("data:image/;base64,"+imageFileArr[i]);
									let android_7inch_tablet_plus_and_minus_trigger = gen_android_screen_shot_7inch_tablet.getChild(genId, "android_7inch_tablet_plus_and_minus_trigger");

									let fileId = "#gen_android_screen_shot_7inch_tablet_" + i + "_android_screen_shot_android_tablet_7inch_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_android_7inch_tablet.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);

									if(i == 0){
										android_7inch_tablet_plus_and_minus_trigger.setUserData("buttoncheck","plus");
									}else {
										android_7inch_tablet_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");
										android_7inch_tablet_plus_and_minus_trigger.setUserData("buttoncheck","minus");

									}

									// itemIdx++ 형식으로 id 채번 하기
									scwin.app_scrennshot_android_small_tablet_itemIdx++;

								}

							}else if(scwin.platformType == "iOS"){

								for(let i = 0; i<imageFileArr.length ; i++){

									const genId = gen_ios_screen_shot_ios_ipad129.insertChild();

									let screen_shot_ios_ipad129 = gen_ios_screen_shot_ios_ipad129.getChild(genId, "screen_shot_ios_ipad129");
									screen_shot_ios_ipad129.setSrc("data:image/;base64,"+imageFileArr[i]);
									let ipad129_plus_and_minus_trigger = gen_ios_screen_shot_ios_ipad129.getChild(genId, "ipad129_plus_and_minus_trigger");

									let fileId = "#gen_ios_screen_shot_ios_ipad129_" + i + "_ios_screen_shot_ipad129_file";
									document.querySelector(fileId).addEventListener("change",function(e){
										const file = e.target.files[0];
										let reader = new FileReader();
										reader.addEventListener("load",function () {
											screen_shot_ios_ipad129.setSrc(this.result);
										},false);
										if (file) {
											reader.readAsDataURL(file);
										}
									},false);

									if(i == 0){
										ipad129_plus_and_minus_trigger.setUserData("buttoncheck","plus");
									}else {
										ipad129_plus_and_minus_trigger.setUserData("buttoncheck","minus");
										ipad129_plus_and_minus_trigger.changeClass("btn_cm icon btn_i_plus", "btn_cm icon btn_i_minus");

									}

									// itemIdx++ 형식으로 id 채번 하기
									scwin.app_screenshot_ios_ipad_129_itemIdx++;

								}


							}

						};

					}}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'dfbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_setting_deploy_metadata',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'btnbox mb0',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm pt',id:'',style:'',type:'button','ev:onclick':'scwin.btn_next_onclick',useLocale:'true',localeRef:'lbl_save'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{id:'grp_iOS'},E:[{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_iphone55'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_ios_screen_shot_ios_iphone55',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'ios_screen_shot_iphone55_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'iphone55_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_ios_phone55_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_ios_iphone55'}}]}]}]}]}]},{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_iphone65'}},{T:1,N:'xf:group',A:{style:'',id:'',class:'count'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_ios_screen_shot_ios_iphone65',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'ios_screen_shot_iphone65_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'iphone65_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_ios_phone65_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_ios_iphone65_path'}}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_ipad129'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_ios_screen_shot_ios_ipad129',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'ios_screen_shot_ipad129_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'ipad129_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_ios_ipad129_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_ios_ipad129'}}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_ios_app_screen_shot_ipad129_3gen',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_ios_screen_shot_ios_ipad129_3gen',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'ios_screen_shot_ipad129_3gen_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'ipad129_3gen_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_ios_ipad129_3gen_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_ios_ipad129_3gen'}}]}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'grp_android'},E:[{T:1,N:'xf:group',A:{id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h3',useLocale:'true',localeRef:'lbl_android_app_screen_shot_phone'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_android_screen_shot_phone',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'android_screen_shot_android_phone_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'android_phone_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_android_phone_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_android_phone'}}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_android_app_screen_shot_7inch_tablet',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_android_screen_shot_7inch_tablet',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'android_screen_shot_android_tablet_7inch_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'android_7inch_tablet_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_android_7inch_tablet_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_android_7inch_tablet'}}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',useLocale:'true',localeRef:'lbl_android_app_screen_shot_10inch_tablet',style:'',tagname:'h3'}},{T:1,N:'xf:group',A:{class:'count',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'appiconbox'},E:[{T:1,N:'w2:generator',A:{id:'gen_android_screen_shot_10inch_tablet',style:'',tagname:'ul',class:'appicon_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'',class:'appicon_item type2 w300'},E:[{T:1,N:'input',A:{type:'file',id:'android_screen_shot_android_tablet_10inch_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}},{T:1,N:'xf:trigger',A:{style:'',id:'android_10inch_tablet_plus_and_minus_trigger',type:'button',class:'btn_cm icon btn_i_plus','ev:onclick':'scwin.btn_android_10inch_tablet_append_upload_image_onclick',useLocale:'true',localeRef:'lbl_add_info'},E:[{T:1,N:'xf:label'}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'thumb'},E:[{T:1,N:'xf:image',A:{src:'/cm/images/contents/appicon_default_bg.svg',alt:'appicon이미지',style:'',id:'screen_shot_android_10inch_tablet'}}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_prev',id:'',style:'',type:'button',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]})