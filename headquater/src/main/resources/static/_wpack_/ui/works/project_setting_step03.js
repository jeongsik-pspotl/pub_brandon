/*amd /cm/ui/works/project_setting_step03.xml 30736 4a83043ff99371e44a2e3c4b19f43db090d3c905776d0fda938449a5acf9e1c9 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
                        scwin.pluginArray = [];
                        scwin.installed_plugin_json = [];
                        scwin.available_plugin_json = [];
                        scwin.available_premium_plugin_json = [];

                        scwin.platformType = ""
                        scwin.build_id = "";
                        scwin.workspace_name = "";

                        scwin.onpageload = function () {
                            scwin.step01Data = $p.parent().dtl_build_setting_step1.getAllJSON();
                            scwin.platformType = scwin.step01Data[0].platform;
                            scwin.workspace_name = scwin.step01Data[0].workspace_name;
                            scwin.build_id = scwin.step01Data[0].project_id;

                            let getResultPluginData = $p.parent().scwin.getResultAppConfigData;
                            scwin.setpluginlisttag(getResultPluginData);

                            scwin.installed_plugin_json = $p.parent().dtl_plugin_setting_installed_step3.getAllJSON();
                            scwin.available_plugin_json = $p.parent().dtl_plugin_setting_available_step3.getAllJSON();
                            scwin.available_premium_plugin_json = $p.parent().dtl_plugin_setting_available_premium_step3.getAllJSON();

                            scwin.setPluginListSettingDataList(scwin.installed_plugin_json,scwin.available_plugin_json, scwin.available_premium_plugin_json);
                            scwin.pluginArray = [];
                        };

                        // 플러그인 적용 서비스
                        scwin.setTemplateProjectPluginUpdate = async function(data){
                            const uri = common.uri.updatePlugin;
                            const resData = await common.http.fetch(uri,"POST",{"Content-Type":"application/json"},data);
                            if(resData != null){

                            } else {

                            }
                        };

                        scwin.setBuilderPluginListStatusNoBuildPage = function (data) {
                            let message = "";
                            switch (data.message) {
                                case "SEARCHING":
                                    // project_build_status.setLabel("빌드중");
                                    message = common.getLabel("lbl_project_add_step04_js_plugin_list");
                                    WebSquare.layer.showProcessMessage(message);
                                    break;
                                case "ADDLOADING":
                                    message = common.getLabel("lbl_plugin_add");
                                    WebSquare.layer.showProcessMessage(message);
                                    break;
                                case "REMOVELOADING":
                                    message = common.getLabel("lbl_plugin_remove");
                                    WebSquare.layer.showProcessMessage(message);
                                    break;
                                case "SUCCESSFUL":
                                    scwin.setpluginlisttag(data);
                                    WebSquare.layer.hideProcessMessage();
                                    // plugin list data setting
                                    scwin.installed_plugin_json = $p.parent().dtl_plugin_setting_installed_step3.getAllJSON();
                                    scwin.available_plugin_json = $p.parent().dtl_plugin_setting_available_step3.getAllJSON();
                                    scwin.available_premium_plugin_json = $p.parent().dtl_plugin_setting_available_premium_step3.getAllJSON();
                                    scwin.setPluginListSettingDataList(scwin.installed_plugin_json, scwin.available_plugin_json, scwin.available_premium_plugin_json);

                                    // 적용할 플러그인 리스트 초기화
                                    scwin.pluginArray = [];
                                    break;
                                case "FAILED":
                                    WebSquare.layer.hideProcessMessage();
                                    break;
                                default:
                                    break;
                            }
                        };

                        // plugin icon을 설정
                        scwin.setPluginIcon = function(type, comp){
                            switch (type.toLowerCase()){
                                case "app" :
                                    comp.addClass("ico01");
                                    break;
                                case "misc" :
                                    comp.addClass("ico016");
                                    break;
                                case "device" :
                                    comp.addClass("ico08");
                                    break;
                                case "camera" :
                                    comp.addClass("ico05");
                                    break;
                                case "barcode" :
                                    comp.addClass("ico02");
                                    break;
                                case "contents" :
                                    comp.addClass("ico07");
                                    break;
                                case "file" :
                                    comp.addClass("ico09");
                                    break;
                                case "geolocation":
                                    comp.addClass("ico013");
                                    break;
                                case "biometric" :
                                    comp.addClass("ico010");
                                    break;
                                case "inappbrowser":
                                    comp.addClass("ico04");
                                    break;
                                case "screenprotector":
                                    comp.addClass("ico020");
                                    break;
                                case "barcodescanner":
                                case "scanner":
                                    comp.addClass("ico02");
                                    break;
                                case "screen" :
                                    comp.addClass("ico022");
                                    break;
                                case "paint" :
                                    comp.addClass("ico012");
                                    break;
                                case "websquare" :
                                    comp.addClass("ico018");
                                    break;
                                case "view" :
                                    comp.addClass("ico014");
                                    break;
                                case "license" :
                                    comp.addClass("ico011");
                                    break;
                                case "audiorecorder" :
                                    comp.addClass("ico03");
                                    break;
                                case "screenrecorder":
                                    comp.addClass("ico017");
                                    break;
                                case "log":
                                    comp.addClass("ico023");
                                    break;
                                case "storage":
                                    comp.addClass("ico20");
                                    break;
                                case "speech":
                                    comp.addClass("ico09");
                                    break;
                                case "logger":
                                default :
                                    comp.addClass("ico09");
                                    break;
                            }
                        };

                        scwin.setBranchPluginListStatus = async function (data) {
                            let message = "";
                            switch(data.message){
                                case "SEARCHING":
                                    message = common.getLabel("lbl_project_add_step04_js_plugin_list");
                                    WebSquare.layer.showProcessMessage(message);
                                    break;
                                case "ADDLOADING":
                                    message = common.getLabel("lbl_plugin_add");
                                    WebSquare.layer.showProcessMessage(message);
                                    break;
                                case "REMOVELOADING":
                                    message = common.getLabel("lbl_plugin_remove");
                                    WebSquare.layer.showProcessMessage(message);
                                    break;
                                case "SUCCESSFUL":
                                    scwin.setpluginlisttag(data);
                                    WebSquare.layer.hideProcessMessage();
                                    // plugin list data setting
                                    scwin.installed_plugin_json = $p.parent().dtl_plugin_setting_installed_step3.getAllJSON();
                                    scwin.available_plugin_json = $p.parent().dtl_plugin_setting_available_step3.getAllJSON();
                                    scwin.available_premium_plugin_json = $p.parent().dtl_plugin_setting_available_premium_step3.getAllJSON();

                                    scwin.setPluginListSettingDataList(scwin.installed_plugin_json, scwin.available_plugin_json, scwin.available_premium_plugin_json);
                                    scwin.pluginArray = [];

                                    message = common.getLabel("lbl_move_build");
                                    if(await common.win.confirm(message)){
                                        const buildproj_json = $p.parent().dtl_build_setting_step1.getRowJSON(0);
                                        let buildAction = [];
                                        let data = {};
                                        data.platform = buildproj_json.platform;;
                                        data.projectName = buildproj_json.project_name;
                                        data.project_pkid = buildproj_json.project_id;
                                        data.workspace_pkid = buildproj_json.workspace_id;
                                        data.product_type = buildproj_json.product_type;
                                        buildAction.push(data);
                                        $p.parent().$p.parent().__buildaction_data__.setJSON(buildAction);

                                        // 현재탭 닫기
                                        const tabIdx = $p.top().tabc_layout.selectedIndex;
                                        $p.top().tabc_layout.deleteTab(tabIdx);

                                        //"/ui/build/build.xml" open
                                        const tabId = $p.top().scwin.convertMenuCodeToMenuKey("m0101000000");
                                        $p.top().scwin.add_tab(tabId);
                                    } else {
                                        // 설치할 플러그인 리스트 초기화
                                        scwin.pluginArray = [];
                                    }
                                    break;
                                case "FAILED":
                                    WebSquare.layer.hideProcessMessage();
                                    break;
                                default:
                                    break;
                            }
                        };

                        scwin.setBranchGitPullStatus = function (obj) {
                            switch (obj.gitStatus) {
                                case "GITPULL":
                                    const message = common.getLabel("lbl_synchronous");
                                    WebSquare.layer.showProcessMessage(message); //Synchronous
                                    break;
                                case "DONE":
                                    WebSquare.layer.hideProcessMessage();
                                    break;
                                default :
                                    break;
                            }
                        };

                        // 플러그인 JSON을 installed, available, availabePremium으로 나눠 가공 및 데이터리스트에 저장
                        scwin.setpluginlisttag = function (data) {
                            let platform = scwin.platformType;
                            let installedPluginvar = [];
                            let availablePluginvar = [];

                            let pluginAddInstalld = [];
                            let pluginAddAvailable = [];
                            let pluginAddAvailablePremium = [];

                            if(platform == "Android"){
                                if(data.resultAppConfigListObj === undefined){
                                    installedPluginvar = data.installedPlugin;
                                    availablePluginvar = data.availablePlugin;
                                }else {
                                    installedPluginvar = data.resultAppConfigListObj.installedPlugin;
                                    availablePluginvar = data.resultAppConfigListObj.availablePlugin;
                                }


                            } else if(platform == "iOS"){
                                installedPluginvar = data.resultAppConfigListObj.plugin.installedPlugin;
                                availablePluginvar = data.resultAppConfigListObj.plugin.availablePlugin;
                            }

                            pluginAddInstalld = installedPluginvar.map((item)=>{
                                let temp = {};
                                temp["installed_plugin_module"] = item.module;
                                temp["installed_plugin_version"] = item.version;
                                temp["installed_plugin_name"] = item.name;
                                temp["installed_plugin_type"] = item.type;
                                return temp;
                            }).filter(e => e);

                            pluginAddAvailable = availablePluginvar.map((item)=>{
                                if(item.type != "premium"){
                                    let temp = {};
                                    temp["available_plugin_module"] = item.module;
                                    temp["available_plugin_version"] = item.version;
                                    temp["available_plugin_name"] = item.name;
                                    temp["available_plugin_type"] = item.type;
                                    return temp;
                                }
                            }).filter(e => e);

                            pluginAddAvailablePremium = availablePluginvar.map((item)=>{
                                if(item.type == "premium"){
                                    let temp = {};
                                    temp["available_plugin_module"] = item.module;
                                    temp["available_plugin_version"] = item.version;
                                    temp["available_plugin_name"] = item.name;
                                    temp["available_plugin_type"] = item.type;
                                    return temp;
                                }
                            }).filter(e => e);

                            $p.parent().dtl_plugin_setting_installed_step3.removeAll();
                            $p.parent().dtl_plugin_setting_available_step3.removeAll();
                            $p.parent().dtl_plugin_setting_available_premium_step3.removeAll();

                            $p.parent().dtl_plugin_setting_installed_step3.setJSON(pluginAddInstalld, true);
                            $p.parent().dtl_plugin_setting_available_step3.setJSON(pluginAddAvailable, true);
                            $p.parent().dtl_plugin_setting_available_premium_step3.setJSON(pluginAddAvailablePremium,true);
                        };

                        // Generator로 플러그인화면 생성
                        // available과 availablePremium은 동일한 영역에 그림.
                        //
                        scwin.setPluginListSettingDataList = function (installed_plugin_json, available_plugin_json, available_premium_plugin_json) {
                            const platform = scwin.platformType;
                            generator_installed_plugin_list.removeAll();
                            generator_available_plugin_list.removeAll();

                            for (const [installed_idx, installed_value] of installed_plugin_json.entries()) {
                                // installed_idx => installed json array index
                                // installedGenIdx => generator child index
                                const installedGenIdx = generator_installed_plugin_list.insertChild();

                                // plugin_type 추가
                                const li_plugin_type = generator_installed_plugin_list.getChild(installedGenIdx,"plugin_type");
                                const span_plugin_id = generator_installed_plugin_list.getChild(installedGenIdx,"plugin_id");
                                const txt_create_id = generator_installed_plugin_list.getChild(installedGenIdx, "plugin_create_id");
                                const txt_plugin_id = generator_installed_plugin_list.getChild(installedGenIdx,"plugin_text_id");
                                txt_plugin_id.setValue(installed_value.installed_plugin_name);

                                const txt_plugin_version = generator_installed_plugin_list.getChild(installedGenIdx,"plugin_version_id");
                                const message_inswave = common.getLabel("lbl_by_inswave_ver"); // by Inswave Systems
                                txt_create_id.setValue(message_inswave);

                                // version
                                txt_plugin_version.setValue("Version "+installed_value.installed_plugin_version);

                                // btn_plugin_remove
                                const btn_plugin_remove = generator_installed_plugin_list.getChild(installedGenIdx,"btn_remove_plugin");
                                btn_plugin_remove.setUserData("setModuleVersion",installed_value.installed_plugin_version);

                                // plugin image 동적으로 변경하기
                                scwin.setPluginIcon(installed_value.installed_plugin_name,span_plugin_id);

                                // type change css class pt_li_basic || pt_li_premium
                                if(installed_value.installed_plugin_type == "premium"){
                                    li_plugin_type.changeClass("pt_li_basic","pt_li_premium");
                                }else {
                                    li_plugin_type.changeClass("pt_li_basic","pt_li_basic");
                                }

                                btn_plugin_remove.setUserData("platform",platform);
                                btn_plugin_remove.setUserData("setModule",installed_value.installed_plugin_module);
                                btn_plugin_remove.setUserData("setModuleName",installed_value.installed_plugin_name);
                                btn_plugin_remove.setUserData("setModuleVersionList",installed_value.installed_plugin_version_list);
                                btn_plugin_remove.setUserData("setModuleType",installed_value.installed_plugin_type);
                            }

                            for (const [available_idx, available_value] of available_plugin_json.entries()) {
                                const availableGenIdx = generator_available_plugin_list.insertChild();

                                // avaliable_plugin_type
                                let li_available_plugin_type = generator_available_plugin_list.getChild(availableGenIdx,"available_plugin_type");
                                let span_available_plugin_id = generator_available_plugin_list.getChild(availableGenIdx,"available_plugin_id");
                                let txt_available_plugin_id = generator_available_plugin_list.getChild(availableGenIdx, "available_plugin_text_id");
                                let txt_available_plugin_create_id = generator_available_plugin_list.getChild(availableGenIdx, "available_plugin_create_id");
                                let txt_available_plugin_version_id = generator_available_plugin_list.getChild(availableGenIdx, "available_plugin_version_id");

                                txt_available_plugin_id.setValue(available_value.available_plugin_name);
                                const message_inswave = common.getLabel("lbl_by_inswave_ver"); // by Inswave Systems
                                txt_available_plugin_create_id.setValue(message_inswave);

                                let pluginVersionList = "";
                                const select_label = common.getLabel("lbl_select");
                                txt_available_plugin_version_id.addItem("", select_label);
                                if(!available_value.available_plugin_version_list){
                                    if(Array.isArray(available_value.available_plugin_version) && available_value.available_plugin_version.length > 0 ){
                                        for(const [idx,value] of available_value.available_plugin_version.entries()){
                                            txt_available_plugin_version_id.addItem(value, value);
                                        }
                                    } else {
                                        txt_available_plugin_version_id.addItem(available_value.available_plugin_version,available_value.available_plugin_version);
                                    }
                                } else {
                                    pluginVersionList = available_value.available_plugin_version_list;
                                    for(const [idx,value] of pluginVersionList.entries()){
                                        txt_available_plugin_version_id.addItem(value, value,false);
                                    }
                                }

                                txt_available_plugin_version_id.setSelectedIndex(0);

                                scwin.setPluginIcon(available_value.available_plugin_name,span_available_plugin_id);

                                if(available_value.available_plugin_type == "premium"){
                                    // pt_li_premium
                                    li_available_plugin_type.changeClass("pt_li_basic","pt_li_premium");
                                }

                                // plugin_add_btn_id
                                let btn_plugin_add = generator_available_plugin_list.getChild(availableGenIdx, "btn_plugin_add");
                                btn_plugin_add.setUserData("platform", platform);
                                btn_plugin_add.setUserData("setModule", available_value.available_plugin_module);

                                let component_available_plugin_version_id = $p.getComponentById(txt_available_plugin_version_id.id);

                                txt_available_plugin_version_id.bind("onchange",function(e){
                                    btn_plugin_add.setUserData("setModuleVersion",component_available_plugin_version_id);
                                });

                                btn_plugin_add.setUserData("setModuleVersion",component_available_plugin_version_id);
                                btn_plugin_add.setUserData("setModuleVersionList",pluginVersionList);
                                btn_plugin_add.setUserData("setModuleName", available_value.available_plugin_name);
                                btn_plugin_add.setUserData("setModuleType", available_value.available_plugin_type);
                            }

                            for(const [available_basic_idx,available_premium_value] of available_premium_plugin_json.entries()){
                                const availableGenIdx = generator_available_plugin_list.insertChild();
                                const li_available_plugin_type = generator_available_plugin_list.getChild(availableGenIdx,"available_plugin_type");
                                const span_available_plugin_id = generator_available_plugin_list.getChild(availableGenIdx,"available_plugin_id");
                                const txt_available_plugin_create_id = generator_available_plugin_list.getChild(availableGenIdx, "available_plugin_create_id");
                                const txt_available_plugin_version_id = generator_available_plugin_list.getChild(availableGenIdx,"available_plugin_version_id");

                                const message_inswave = common.getLabel("lbl_by_inswave_ver"); // by Inswave Systems
                                txt_available_plugin_create_id.setValue(message_inswave);
                                let txt_available_plugin_id = generator_available_plugin_list.getChild(availableGenIdx,"available_plugin_text_id");
                                txt_available_plugin_id.setValue(available_premium_value.available_plugin_name);

                                const message = common.getLabel("lbl_select");
                                txt_available_plugin_version_id.addItem("", message);
                                let pluginVersionList = [];
                                if(available_premium_value.available_plugin_version_list == undefined){
                                    pluginVersionList = available_premium_value.available_plugin_version;
                                }else {
                                    pluginVersionList = available_premium_value.available_plugin_version_list;
                                }
                                for(const [idx,value] of pluginVersionList.entries()){
                                    txt_available_plugin_version_id.addItem(value, value);
                                }

                                txt_available_plugin_version_id.setSelectedIndex(0);

                                scwin.setPluginIcon(available_premium_value.available_plugin_name,span_available_plugin_id);

                                // type change css class pt_li_basic || pt_li_premium
                                if(available_premium_value.available_plugin_type == "premium"){
                                    // pt_li_premium
                                    li_available_plugin_type.changeClass("pt_li_basic","pt_li_premium");
                                }else {
                                    li_available_plugin_type.changeClass("pt_li_basic","pt_li_basic");
                                }

                                let btn_plugin_add = generator_available_plugin_list.getChild(availableGenIdx,"btn_plugin_add");
                                btn_plugin_add.setUserData("platform",platform);
                                btn_plugin_add.setUserData("setModule",available_premium_value.available_plugin_module);

                                const component_available_plugin_version_id = $p.getComponentById(txt_available_plugin_version_id.id);
                                txt_available_plugin_version_id.bind("onchange",function(e){
                                    btn_plugin_add.setUserData("setModuleVersion",component_available_plugin_version_id);
                                });

                                btn_plugin_add.setUserData("setModuleVersion",component_available_plugin_version_id);
                                btn_plugin_add.setUserData("setModuleVersionList",pluginVersionList);
                                btn_plugin_add.setUserData("setModuleName", available_premium_value.available_plugin_name);
                                btn_plugin_add.setUserData("setModuleType", available_premium_value.available_plugin_type);
                            }

                        };

                        // 플러그인 적용
                        scwin.btn_apply_plugin_onclick = async function (e) {
                            const product_type = scwin.step01Data[0].product_type;
                            const platform = scwin.step01Data[0].platform;
                            const createBuildID = scwin.build_id;
                            const workspace_name = scwin.workspace_name;

                            if (!createBuildID && !platform && !workspace_name) {
                                return;
                            }

                            if(scwin.pluginArray.length == 0){
                                const message = common.getLabel("lbl_project_add_step03_js_add_del_plugin");
                                if(await common.win.alert(message)){
                                    return false;
                                }
                            }

                            let data = {};
                            data.build_id = createBuildID;
                            data.product_type = product_type;
                            data.platform = platform;
                            data.moduleList = scwin.pluginArray;
                            data.workspace_name = workspace_name;
                            data.project_dir_name = scwin.step01Data[0].project_dir_path;
                            scwin.setTemplateProjectPluginUpdate(data);
                        };


                        // 플러그인 추가 이벤트
                        scwin.btn_plugin_add_onclick = async function (e) {
                            const getModule = this.getUserData("setModule");
                            const getModuleVersion = this.getUserData("setModuleVersion");
                            const getModuleName = this.getUserData("setModuleName");
                            const setModuleVersionList = this.getUserData("setModuleVersionList");
                            const getModuleType = this.getUserData("setModuleType");

                            let addPluginTemp = {};
                            let installedPlugin = {};
                            let pluginVersion = getModuleVersion.getValue();

                            if(!pluginVersion){
                                const message = common.getLabel("lbl_project_add_step04_js_select_plugin_version");
                                //Plugin Version을 선택해주세요
                                if(await common.win.alert(message)){
                                    return false;
                                }
                            }

                            addPluginTemp.module = getModule;
                            addPluginTemp.moduleName = getModuleName;
                            addPluginTemp.moduleVersion = pluginVersion;

                            // plugin_add_btn_id, 여기는 단건으로 처리하기 위한 방법
                            if(getModuleType == "basic"){

                                for(let [idx,value] of scwin.available_plugin_json.entries()){
                                    if(value.available_plugin_module == getModule){
                                        addPluginTemp = value;
                                        addPluginTemp.pluginMode = "ADD";
                                        addPluginTemp.available_plugin_version = pluginVersion;
                                        // data format 추가 정의 및 기존 정의 붙이기..
                                        const findPluginItem = scwin.pluginArray.findIndex(function(pluginItem){
                                            return pluginItem.installed_plugin_module == getModule;
                                        });

                                        if(findPluginItem == -1){
                                            scwin.pluginArray.push(addPluginTemp);
                                        }else {
                                            scwin.pluginArray.splice(findPluginItem,1);
                                        }
                                    }
                                }

                                // 설치 가능한 플러그인 선택한 모듈 채번
                                const findItem = scwin.available_plugin_json.findIndex(function(item){
                                    return item.available_plugin_module == getModule;
                                });

                                // 선택한 플러그인 모듈 삭제
                                scwin.available_plugin_json.splice(findItem,1);

                            } else if(getModuleType == "premium"){

                                for(let [idx,value] of scwin.available_premium_plugin_json.entries()){
                                    if(value.available_plugin_module == getModule){
                                        addPluginTemp = value;
                                        addPluginTemp.pluginMode = "ADD";
                                        addPluginTemp.available_plugin_version = pluginVersion;

                                        const findPluginItem = scwin.pluginArray.findIndex(function(pluginItem){
                                            return pluginItem.installed_plugin_module == getModule;
                                        });

                                        if(findPluginItem === -1){
                                            scwin.pluginArray.push(addPluginTemp);
                                        } else {
                                            scwin.pluginArray.splice(findPluginItem,1);
                                        }
                                    }
                                }

                                // 설치 가능한 플러그인 선택한 모듈 채번
                                const findItem = scwin.available_premium_plugin_json.findIndex(function(item){
                                    return item.available_plugin_module == getModule;
                                });

                                // 선택한 플러그인 모듈 삭제
                                scwin.available_premium_plugin_json.splice(findItem,1);
                            }

                            installedPlugin.installed_plugin_module = getModule;
                            installedPlugin.installed_plugin_version = pluginVersion;
                            installedPlugin.installed_plugin_name = getModuleName;
                            installedPlugin.installed_plugin_type = getModuleType;
                            installedPlugin.installed_plugin_version_list = setModuleVersionList;

                            // 다시 조회 할때는 선택한 add plugin 은 remove plugin push 한다.
                            // 설치 가능한 플러그인으로 이동
                            scwin.installed_plugin_json.push(installedPlugin);
                            scwin.setPluginListSettingDataList(scwin.installed_plugin_json, scwin.available_plugin_json, scwin.available_premium_plugin_json);
                        };

                        // 플러그인 삭제 이벤트
                        scwin.btn_remove_plugin_onclick = function (e) {
                            const getModule = this.getUserData("setModule");
                            const getModuleVersion = this.getUserData("setModuleVersion");
                            const getModuleName = this.getUserData("setModuleName");
                            const getModuleVersionList = this.getUserData("setModuleVersionList");
                            const getModuleType = this.getUserData("setModuleType");

                            let removePlugin = {};
                            let availablePlugin = {};

                            for(const [idx,value] of scwin.installed_plugin_json.entries()){
                                if(value.installed_plugin_module == getModule){
                                    removePlugin = value;
                                    removePlugin.pluginMode = "REMOVE";
                                    removePlugin.available_plugin_version = getModuleVersion;

                                    const findPluginItem = scwin.pluginArray.findIndex(function(pluginItem){
                                        return pluginItem.available_plugin_module == getModule;
                                    });

                                    if(findPluginItem === -1){
                                        scwin.pluginArray.push(removePlugin);
                                    }else {
                                        scwin.pluginArray.splice(findPluginItem,1);
                                    }
                                }
                            }

                            const findItem = scwin.installed_plugin_json.findIndex(function(item){
                                return item.installed_plugin_module == getModule;
                            });

                            availablePlugin.available_plugin_module = getModule;
                            availablePlugin.available_plugin_version = [];
                            if(typeof getModuleVersion == "string"){
                                availablePlugin.available_plugin_version.push(getModuleVersion);
                            } else {
                                availablePlugin.available_plugin_version = getModuleVersion;
                            }

                            availablePlugin.available_plugin_name = getModuleName;
                            availablePlugin.available_plugin_version_list = getModuleVersionList;
                            availablePlugin.available_plugin_type = getModuleType;

                            scwin.installed_plugin_json.splice(findItem,1);
                            // 다시 조회 할때는 선택한 add plugin 은 remove plugin push 한다.
                            if(getModuleType == "basic"){
                                scwin.available_plugin_json.push(availablePlugin);
                            }else if(getModuleType == "premium"){
                                scwin.available_premium_plugin_json.push(availablePlugin);
                            }

                            scwin.setPluginListSettingDataList(scwin.installed_plugin_json, scwin.available_plugin_json, scwin.available_premium_plugin_json);
                        };

                        // 이전 버튼 이벤트
                        scwin.btn_prev_onclick = function (e) {
                            $p.parent().scwin.selected_step(2);
                        };


                    }}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'sub_contents',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'contents_inner top nosch',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'pgtbox_inner',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h2',useLocale:'true',localeRef:'lbl_input_project_setting'}}]},{T:1,N:'xf:group',A:{id:'',class:'step_bar',tagname:'ul'},E:[{T:1,N:'xf:group',A:{id:'',tagname:'li',class:''},E:[{T:1,N:'w2:anchor',A:{outerDiv:'false',style:'',id:'',useLocale:'true',localeRef:'lbl_input_project'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',tagname:'li',style:'',class:'prev'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_app_default_info'},E:[{T:1,N:'xf:label'}]}]},{T:1,N:'xf:group',A:{id:'',tagname:'li',style:'',class:'on'},E:[{T:1,N:'w2:anchor',A:{id:'',outerDiv:'false',style:'',useLocale:'true',localeRef:'lbl_plugin_setting'},E:[{T:1,N:'xf:label'}]}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'contents_inner bottom nosch'},E:[{T:1,N:'xf:group',A:{style:'',id:'',class:'titbox'},E:[{T:1,N:'xf:group',A:{id:'',class:'lt'},E:[{T:1,N:'w2:textbox',A:{tagname:'h3',style:'',id:'',label:'',class:'',useLocale:'true',localeRef:'lbl_plugin_setting'}}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'rt'},E:[{T:1,N:'xf:group',A:{id:'',class:'btnbox mb0'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'',useLocale:'true',localeRef:'lbl_confirm_plugin_setting'}},{T:1,N:'xf:trigger',A:{style:'',id:'btn_apply_plugin',type:'button',class:'btn_cm pt','ev:onclick':'scwin.btn_apply_plugin_onclick',toolTip:'tooltip',toolTipDisplay:'true',useLocale:'true',localeRef:'lbl_apply',tooltipLocaleRef:'lbl_plugin_apply'},E:[{T:1,N:'xf:label'}]}]}]}]},{T:1,N:'xf:group',A:{id:'',class:'plugingrp on'},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h4',useLocale:'true',localeRef:'lbl_applied_plugin'}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{id:'',class:'',tagname:''},E:[{T:1,N:'w2:generator',A:{class:'pluginbox',tagname:'ul',style:'',id:'generator_installed_plugin_list'},E:[{T:1,N:'xf:group',A:{tagname:'li',style:'',id:'plugin_type',class:'plugin_list'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:span',A:{style:'',label:'',id:'plugin_id',class:'pluginicon'}},{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'w2:textbox',A:{style:'',id:'plugin_text_id',label:'',class:'plugin_tit'}},{T:1,N:'w2:textbox',A:{class:'plugin_txt',id:'plugin_create_id',label:'',style:''}}]}]},{T:1,N:'xf:group',A:{style:'',id:'',class:'flex'},E:[{T:1,N:'xf:group',A:{id:'',class:'flex'},E:[{T:1,N:'w2:span',A:{class:'plugin_ver',id:'plugin_version_id',label:'',style:''}}]},{T:1,N:'xf:group',A:{id:'',class:'icobox'},E:[{T:1,N:'xf:trigger',A:{style:'',id:'btn_remove_plugin',type:'button',class:'btn_cm icon btn_i_minus','ev:onclick':'scwin.btn_remove_plugin_onclick',useLocale:'true',localeRef:'lbl_insert_text'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'plugingrp',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'titbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'lt',id:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'',style:'',tagname:'h4',useLocale:'true',localeRef:'lbl_applicable_plugin'}}]},{T:1,N:'xf:group',A:{class:'rt',id:'',style:''}}]},{T:1,N:'xf:group',A:{class:'',id:'',tagname:''},E:[{T:1,N:'w2:generator',A:{class:'pluginbox',tagname:'ul',style:'',id:'generator_available_plugin_list'},E:[{T:1,N:'xf:group',A:{class:'plugin_list',id:'',style:'',tagname:'li'},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'w2:span',A:{class:'pluginicon',id:'available_plugin_id',label:'',style:''}},{T:1,N:'xf:group',A:{id:''},E:[{T:1,N:'w2:textbox',A:{class:'plugin_tit',id:'available_plugin_text_id',label:'',style:''}},{T:1,N:'w2:textbox',A:{class:'plugin_txt',id:'available_plugin_create_id',label:'',style:''}}]}]},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'flex',id:''},E:[{T:1,N:'w2:span',A:{class:'plugin_ver',id:'',label:'',useLocale:'true',localeRef:'lbl_version',style:''}},{T:1,N:'xf:select1',A:{allOption:'false',appearance:'minimal',chooseOption:'',class:'',direction:'auto',disabled:'false',disabledClass:'w2selectbox_disabled',id:'available_plugin_version_id',ref:'',renderType:'auto',style:'',submenuSize:'auto'}}]},{T:1,N:'xf:group',A:{class:'icobox',id:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm icon btn_i_plus',id:'btn_plugin_add',style:'',type:'button','ev:onclick':'scwin.btn_plugin_add_onclick',useLocale:'true',localeRef:'lbl_insert_text'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]},{T:1,N:'xf:group',A:{class:'btnbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{class:'rt',id:'',style:''},E:[{T:1,N:'xf:trigger',A:{class:'btn_cm step_prev',id:'btn_prev',style:'',type:'button','ev:onclick':'scwin.btn_prev_onclick',useLocale:'true',localeRef:'lbl_prev'},E:[{T:1,N:'xf:label'}]}]}]}]}]}]}]}]})