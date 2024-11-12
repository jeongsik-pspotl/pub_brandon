package com.inswave.whive.branch.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.inswave.whive.branch.domain.ServerConfigListStatusMsg;
import com.inswave.whive.branch.enums.*;
import com.inswave.whive.branch.service.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ProjectServerConfigListMsgHandler implements BranchHandlable{

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    SigningKeyService signingKeyService;

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Autowired
    AppIconService appIconService;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    UpdateProjectService updateProjectService;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String userProjectPath = "";

        JSONObject configJSON = new JSONObject();
        JSONParser parser = new JSONParser();
        JSONObject testBuildSettings;

        JsonArray appconfigMultiArrayJson = new JsonArray();

        Gson gson = new Gson();
        Map map = new HashMap();

        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO.name())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String project_dir_path = parseResult.get("projectDirPath").toString();

            userProjectPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name; // path

            mobileTemplateConfigService.getTemplateConfigAllCLI(session, parseResult, platform, userProjectPath, project_dir_path);

        }else if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_UPDATE_INFO.name())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String project_dir_path = parseResult.get("projectDirPath").toString();
            // set Server config
            String serverConfig = parseResult.get("serverConfig").toString();
            // set json config
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();
            // set product type
            String productType = parseResult.get("product_type").toString();
            String hqKey = parseResult.get("hqKey").toString();

            userProjectPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;

            try {
                configJSON = (JSONObject) parser.parse(jsonApplyConfig);
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    mobileTemplateConfigService.templateConfigAllCLI(session, platform, userProjectPath, project_dir_path, configJSON, serverConfig, ServerConfigMode.UPDATE, productType, hqKey);
                }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    mobileTemplateConfigService.setiOSServerConfigAndProjectName(session, platform, userProjectPath, project_dir_path, configJSON, serverConfig, productType, hqKey);
                }

            } catch (ParseException e) {
                    
                log.error("builder message ProjectServerConfigList exception ",e);
            }



        }else if(messageType.equals(BuildServiceType.HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO.name())){


            mobileTemplateConfigService.getMultiConfigAllCLI(session, parseResult);
        }else if(messageType.equals(BuildServiceType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO.name())){

            // parseResult.get("buildProfileYn") == "Y" 일 경우에만 처리 하도록 구현하기
            // String buildProfileYn = parseResult.get("buildProfileYn").toString();
            // String deployProfileYn = parseResult.get("deployProfileYn").toString();

            ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();
            String platform = parseResult.get("platform").toString();
            String product_type = "wmatrix";


            userProjectPath = systemUserHomePath + userRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                    "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String appIconFileName = parseResult.get("appIconFileName").toString();

            String signingKeyStr = parseResult.get("jsonSigningkey").toString();



            // if(deployProfileYn.toLowerCase().equals("y")){

                // platform 분기처리  deploySettingInit 메소드 안에서 자동으로 분기처리 하게 구현이 되어 있음
                // android : json file set
                // ios : p8 file and string data set
                deploySettingInitService.deploySettingInit(session, parseResult, userProjectPath+"/"+projectName);

            // }

            // android/ios app icon generator cli 실행 구간
            if(appIconFileName == null || appIconFileName.toLowerCase().equals("")){

            }else {
                if(platform.toLowerCase().equals("android")){
                    appIconService.setAppIconGeneratorCLI(session, parseResult);
                }else if(platform.toLowerCase().equals("ios")){
                    appIconService.setiOSAppIconGeneratorCLI(session, parseResult);
                }

            }

            try {
                // MultiDomain Template Get Config
                JSONObject templateConfig = mobileTemplateConfigService.getMultiDomainTemplateConfig(session, parseResult).get();
                parseResult.put("templateConfig", templateConfig);
            } catch (Exception e) {
                log.error("Get templateConfig = {}", e.getMessage(), e);
            }

            // get config 이후 set config 처리
            if(platform.toLowerCase().equals("android")){
               // 1. android key 기준
               // 2. wmatrix, profiles >> 이후부터 순서대로 진행하기
               // 3..profiles 기준으로 개수 가지고 처리하기
               // 4. server 제외 하고 나머지는 동일하게 처리하기 ..
               // 5. 어떻게 Map.Entry<String, JsonElement> entry: entries 방식으로 처리하기 ~~

                // TODO : send message to manager 로 전송 기능 추가
                serverConfigListStatusMsg.setHqKey(parseResult.get("hqKey").toString());
                multiProfileConfigUpdateMessageHandler(session, serverConfigListStatusMsg, null, "CONFIGSEARCHING");
                JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platform, userProjectPath+"/"+projectName);
                JsonElement element = JsonParser.parseString(jsonAppConfig.toJSONString());
                JsonObject jsonReulst =  gson.fromJson(element, JsonObject.class);


                JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("wmatrix");
                JSONObject getProfiles = (JSONObject) jsonMatrix.get("profiles");
                log.info(jsonMatrix.toJSONString());
                log.info(getProfiles.toJSONString());


                //String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

                mobileTemplateConfigService.templateMultiProfileAutoKeySetUpdateAllCLI(session, parseResult, jsonReulst);

            }else {

                // 인증서 profile 설정 저장이 끝나면 app config 정보 업데이트 작업 진행하기 ..
                mobileTemplateConfigService.templateMultiProfileAutoKeySetUpdateAllCLI(session, parseResult, null);

            }





            JSONObject signingKeyJson = null;
            try {
                signingKeyJson = (JSONObject) parser.parse(signingKeyStr);

                // if(buildProfileYn.toLowerCase().equals("y")){

                // android
                if(platform.toLowerCase().equals("android")){
                    // keystore file set
                    signingKeyService.createSigningkeyFileToProperties(userProjectPath+"/"+projectName, platform, signingKeyJson);

                    // ios
                }else if(platform.toLowerCase().equals("ios")){


                    JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platform, userProjectPath+"/"+projectName);
                    JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("targets");
                    String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

                    log.info(parseResult.toString());
                    String arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();
                    String arrayJSONCertificateStr = parseResult.get("arrayJSONCertificates").toString();
                    String textToBuildSettingsGson = parseResult.get("BuildSettingsGson").toString();

                    appconfigMultiArrayJson = (JsonArray) JsonParser.parseString(textToBuildSettingsGson);

                    // certificate, profile set
                    signingKeyService.createSigningkeyToMultiCertFile(userProjectPath, arrayJSONCertificateStr, product_type);

                    /*TODO profile appconfigMultiArrayJson 객체 data setting 하기  */
                    signingKeyService.createProfileSettingToMultiProfilesProject(userProjectPath+"/"+projectName, arrayJSONProfilesStr, product_type, targetKeyList, appconfigMultiArrayJson);

                    String tempProfilesOneStr = arrayJSONProfilesStr;

                    //tempProfilesOneStr = tempProfilesOneStr.replaceAll(" ",""); // 인증서 파일명에 공백이 있을경우 에러가 발생하므로 주석
                    tempProfilesOneStr = tempProfilesOneStr.replace("[{","");
                    tempProfilesOneStr = tempProfilesOneStr.replace("}]","");

                    String[] jsonProfilesArray = tempProfilesOneStr.split("\\},\\{");

                    for(int i = 0; i < jsonProfilesArray.length; i++){
                        jsonProfilesArray[i] = "{" + jsonProfilesArray[i] + "}";
                        try {
                            JSONObject jsonProfile = (JSONObject) parser.parse(jsonProfilesArray[i]);

                            // show profiles
                            JSONObject resultShwoProfiles = signingKeyService.showProfileOneInfo(session, jsonProfile.get("profiles_path").toString(), product_type);
                            log.info(resultShwoProfiles.toJSONString());
                            // signingKeyService.setExportOptionsMultiProfiles(session, userProjectPath+"/"+projectName, resultShwoProfiles, jsonProfile, product_type);

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }

                //}

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO
        }else if(messageType.equals(BuildServiceType.HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO.name())){

            /**
             * project update vcs multi profile config 수행 메소드
             *
            */
            updateProjectService.projectAppConfigUpdateRemoteGit(session, parseResult);


        }

    }

    // TODO : send to message method 추가히기
    private void multiProfileConfigUpdateMessageHandler(WebSocketSession session, ServerConfigListStatusMsg serverConfigListStatusMsg, JSONObject resultServerConfigObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        serverConfigListStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            serverConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO.name()); /// msgtype 수정 하기
        }else if(messageValue.equals("CONFIGSEARCHING") || messageValue.equals("CONFIGUPDATE") || messageValue.equals("DONE")){
            serverConfigListStatusMsg.setMsgType(BuildServiceType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO.name()); /// msgtype 수정 하기
        }

        serverConfigListStatusMsg.setStatus("multiprofileconfig");
        serverConfigListStatusMsg.setMessage(messageValue);
//        serverConfigListStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            serverConfigListStatusMsg.setResultServerConfigListObj(resultServerConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(serverConfigListStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }


}
