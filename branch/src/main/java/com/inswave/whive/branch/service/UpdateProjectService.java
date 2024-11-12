package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.inswave.whive.branch.domain.ProjectGitCloneMessage;
import com.inswave.whive.branch.domain.ServerConfigListStatusMsg;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.GitTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UpdateProjectService extends BaseService {

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    SigningKeyService signingKeyService;

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Autowired
    AppIconService appIconService;

    @Autowired
    GitTask gitTask;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    private JSONObject JSONbranchSettingobj;
    private JSONParser parser = new JSONParser();
    private ObjectMapper Mapper = new ObjectMapper();
    private String UserProjectPath;

    WebSocketSession sessionTemp;


    @Async
    public void projectAppConfigUpdateRemoteGit(WebSocketSession session, Map<String, Object> parseResult){

        ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();

        JsonArray appconfigMultiArrayJson = new JsonArray();
        String platform = parseResult.get("platform").toString();
        String product_type = "wmatrix";
        String userProjectPath = "";
        Gson gson = new Gson();
        Map map = new HashMap();

        userProjectPath = systemUserHomePath + userRootPath + "builder_main/"+ BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String appIconFileName = parseResult.get("appIconFileName").toString();

        String signingKeyStr = parseResult.get("jsonSigningkey").toString();

        try {
            JSONObject jsonRepogitoryData = (JSONObject) parser.parse(parseResult.get("jsonRepository").toString());



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

                // TODO : git task 추가 적용
                synchronized (this){
                    gitTask.gitPush(parseResult, userProjectPath+"/"+projectName, jsonRepogitoryData, "Project App Config Update 완료");
                    multiProfileConfigUpdateMessageHandler(session, serverConfigListStatusMsg, null, "DONE");
                }


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

                // TODO : git task 추가 적용
                synchronized (this){
                    gitTask.gitPush(parseResult, userProjectPath+"/"+projectName, jsonRepogitoryData, "Project App Config Update 완료");
                    multiProfileConfigUpdateMessageHandler(session, serverConfigListStatusMsg, null, "DONE");
                }


            }




        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    // 2.1 common cli mkdir
    // 아래 executueCommonsCLIToMkdirAdd 함수 삭제
    @Async
    public void executueCommonsCLIToMkdirAdd(WebSocketSession session, CommandLine commandLineParse, String VcsType, JSONObject repositoryObj, String hqKey){
        log.info(" #### Gradle git pull CLI CommandLine start ### : ");
        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        projectGitCloneMessage.setHqKey(hqKey);

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            // vcs clone, checkout ... send message
            if (VcsType.toLowerCase().equals("git")){
                projectUpdateMessage(session, projectGitCloneMessage,"","GITPULL", hqKey);
            }else if(VcsType.toLowerCase().equals("svn")){
                projectUpdateMessage(session, projectGitCloneMessage,"","SVNUPDATE", hqKey);
            }


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle git pull CLI CommandLine log data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                 projectUpdateMessage(session, projectGitCloneMessage,"","DONE", hqKey);

            }else if(exitCode == 1){

            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    private void projectUpdateMessage(WebSocketSession session, ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus, String hqKey){

        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        projectGitCloneMessage.setHqKey(hqKey);

        if (gitStatus.equals("GITPULL")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }else if(gitStatus.equals("SVNUPDATE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }else if(gitStatus.equals("DONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);
        log.info(projectGitCloneMessage.toString());
        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);

        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void multiProfileConfigUpdateMessageHandler(WebSocketSession session, ServerConfigListStatusMsg serverConfigListStatusMsg, JSONObject resultServerConfigObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        serverConfigListStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            serverConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO.name()); /// msgtype 수정 하기
        }else if(messageValue.equals("CONFIGSEARCHING") || messageValue.equals("CONFIGUPDATE") || messageValue.equals("DONE")){
            serverConfigListStatusMsg.setMsgType(BuildServiceType.HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO.name()); /// msgtype 수정 하기
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
