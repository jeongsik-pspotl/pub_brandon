package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.AppConfigListStatusMsg;
import com.inswave.whive.branch.domain.MultiProfileStatusMsg;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MultiProfileService extends BaseService{


    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    private String systemUserHomePath = System.getProperty("user.home");

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${whive.distribution.wmatrixmanager}")
    private String wmatrixmanager;

    private JSONParser parser = new JSONParser();

    @Async("asyncThreadPool")
    public void getAllMultiProfileList(WebSocketSession session, Map<String, Object> parseResult){

        startCLIAllMultiProfileList(session, parseResult);

    }

    public void setMultiProfile(WebSocketSession session, Map<String, Object> parseResult){
        startCLISetActiveProfile(session, parseResult);

    }

    /**
     * getiOSAllMultiProfileList
     * iOS CLI : wmatrixmanager getalltarget -p {Path}
     * MultiProfile Target LIst 조회 기능
     *
     * @param parseResult
     * @return resultMultiProfileList
     */
    public JSONArray getiOSAllMultiProfileList(JSONObject parseResult){

        JSONArray resultMultiProfileList = startiOSMultiProfileList(parseResult);

        return resultMultiProfileList;

    }

    /* start CLI Method */
    private void startCLIAllMultiProfileList(WebSocketSession session, Map<String, Object> parseResult){

        // cli 실행하는 소스 코드 추가하기
        String userProjectPath = "";

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String productType = parseResult.get("product_type").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

        userProjectPath = userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name + "/" + project_name; // path

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

            CommandLine commandLineGetMultiProfileListCLI = null;
            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetMultiProfileListCLI = CommandLine.parse(shellscriptFileName);
                commandLineGetMultiProfileListCLI.addArgument(systemUserHomePath + userProjectPath);
                commandLineGetMultiProfileListCLI.addArgument("getAllProfiles");

            }

            try {
                executeCommonsExecGetMultiProfileList(session, commandLineGetMultiProfileListCLI, platform, hqKey, "", parseResult);

            } catch (Exception e) {

            }

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){


            String fullUserProjectPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name;

            String iosProjectName = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platform, fullUserProjectPath, project_name);

            CommandLine commandLineGetMultiProfileListCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetMultiProfileListCLI = CommandLine.parse(wmatrixmanager);
                commandLineGetMultiProfileListCLI.addArgument("getalltarget");
                commandLineGetMultiProfileListCLI.addArgument("-p");
                commandLineGetMultiProfileListCLI.addArgument(systemUserHomePath + userProjectPath);

            }

            try {
                executeCommonsExecGetMultiProfileList(session, commandLineGetMultiProfileListCLI, platform, hqKey, iosProjectName, parseResult);

            } catch (Exception e) {

            }

        }

    }

    private void startCLISetActiveProfile(WebSocketSession session, Map<String, Object> parseResult){

        // cli 실행하는 소스 코드 추가하기
        String userProjectPath = "";

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String productType = parseResult.get("product_type").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
        String proflieType = parseResult.get(PayloadMsgType.profile_type.name()).toString();

        userProjectPath = userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name + "/" + project_name; // path

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

            CommandLine commandLineSetActiveProfileListCLI = null;
            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineSetActiveProfileListCLI = CommandLine.parse(shellscriptFileName);
                commandLineSetActiveProfileListCLI.addArgument(systemUserHomePath + userProjectPath);
                commandLineSetActiveProfileListCLI.addArgument("setActiveProfile");
                commandLineSetActiveProfileListCLI.addArgument("-P");
                commandLineSetActiveProfileListCLI.addArgument("profile=");
                commandLineSetActiveProfileListCLI.addArgument(proflieType);

            }else if(productType.toLowerCase().equals("whybrid")){

            }else {

            }



            try {
                executeCommonsExecSetActiveProfile(session, commandLineSetActiveProfileListCLI, platform, hqKey);

            } catch (Exception e) {

            }

        }

    }

    private  JSONArray startiOSMultiProfileList(JSONObject parseResult){

        JSONArray resultMultiProfileList = new JSONArray();

        String userProjectPath = "";

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String productType = parseResult.get("product_type").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

        userProjectPath = userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name + "/" + project_name; // path

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            String fullUserProjectPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name;

            // String iosProjectName = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platform, fullUserProjectPath, project_name);

            CommandLine commandLineGetMultiProfileListCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetMultiProfileListCLI = CommandLine.parse(wmatrixmanager);
                commandLineGetMultiProfileListCLI.addArgument("getalltarget");
                commandLineGetMultiProfileListCLI.addArgument("-p");
                commandLineGetMultiProfileListCLI.addArgument(systemUserHomePath + userProjectPath);

            }

            try {

               resultMultiProfileList = executeExecGetiOSMultiProfileList(commandLineGetMultiProfileListCLI, platform, hqKey);
                return resultMultiProfileList;
            } catch (Exception e) {
                return resultMultiProfileList;
            }

        }

        return resultMultiProfileList;
    }


    /* execute Common Exec Method */
    private void executeCommonsExecGetMultiProfileList(WebSocketSession session, CommandLine commandLineGetMultiProfileListCLI, String platform, String hqKey, String iosProjectName, Map<String, Object> parseResult){

        MultiProfileStatusMsg multiProfileStatusMsg = new MultiProfileStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";

        multiProfileStatusMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
        getAllMultiProfileListMsgHandler(session, multiProfileStatusMsg,"SEARCHING", null, "SEARCHING", hqKey, parseResult);
        try {
            handler.start();

            executor.execute(commandLineGetMultiProfileListCLI, resultHandler);
            resultHandler.waitFor();

            //TODO 성능 개선 이 필요한 소스 코드
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray())));
            StringBuilder resultBuilder = new StringBuilder();
            char[] buffer = new char[8192]; // 적절한 버퍼 크기 설정
            int bytesRead;
            while ((bytesRead = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                resultBuilder.append(buffer, 0, bytesRead);
            }
            String result = resultBuilder.toString();
            result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
            result = result.replaceAll(" ","");

            ArrayList<String> resultMultiProfileArr = new ArrayList<>();


            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);

                int idxStart = result.indexOf("//!!--##");
                int idxEnd = result.indexOf("##--!!//");
                String resultsub = "";

                if( idxStart > -1 && idxEnd > -1 ) {
                    resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);

                    String[] resultMultiProfileStr = resultsub.split(",");


                    for(int i = 0; i < resultMultiProfileStr.length; i++){
                            resultMultiProfileArr.add(resultMultiProfileStr[i]);
                    }

                } else {
                    resultsub = result;
                }
            }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                JSONArray resultMultiProfileList = new JSONArray();

                resultMultiProfileList = (JSONArray) parser.parse(result);

//                iosProjectName = iosProjectName.replace(".xcodeproj","");
//                String iosReusultMultiProfile = result;
//                iosReusultMultiProfile = iosReusultMultiProfile.replace(" ","");
//                iosReusultMultiProfile = iosReusultMultiProfile.replace("[\"" + iosProjectName +"\":","");
//                iosReusultMultiProfile = iosReusultMultiProfile.replace("[\"","");
//                iosReusultMultiProfile = iosReusultMultiProfile.replace("\",\"",",");
//                iosReusultMultiProfile = iosReusultMultiProfile.replace("\"]","");

                // String[] resultMultiProfileStr = iosReusultMultiProfile.split(",");

                for(int i = 0; i < resultMultiProfileList.size(); i++){
                    JSONObject targetObj = (JSONObject) resultMultiProfileList.get(i);
                    log.info(" result targetName : {}", targetObj.get("targetName").toString());
                    resultMultiProfileArr.add(targetObj.get("targetName").toString());
                }

            }


            int exitCode = resultHandler.getExitValue();


            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                getAllMultiProfileListMsgHandler(session, multiProfileStatusMsg,"", resultMultiProfileArr, PayloadMsgType.SUCCESSFUL.name(), hqKey, parseResult);
            }else if(exitCode == 1){
                getAllMultiProfileListMsgHandler(session, multiProfileStatusMsg,"", null, PayloadMsgType.FAILED.name(), hqKey, parseResult);
            }else {
                getAllMultiProfileListMsgHandler(session, multiProfileStatusMsg,"", null, PayloadMsgType.FAILED.name(), hqKey, parseResult);
            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    private JSONArray executeExecGetiOSMultiProfileList(CommandLine commandLineGetMultiProfileListCLI, String platform, String hqKey){

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        JSONArray resultMultiProfileList = new JSONArray();

        try {
            handler.start();

            executor.execute(commandLineGetMultiProfileListCLI, resultHandler);
            resultHandler.waitFor();
            // TODO 보완 필요
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray())));
            StringBuilder resultBuilder = new StringBuilder();
            char[] buffer = new char[8192]; // 적절한 버퍼 크기 설정
            int bytesRead;
            while ((bytesRead = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                resultBuilder.append(buffer, 0, bytesRead);
            }
            String result = resultBuilder.toString();
            result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
            result = result.replaceAll(" ","");

            ArrayList<String> resultMultiProfileArr = new ArrayList<>();

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                resultMultiProfileList = (JSONArray) parser.parse(result);

                log.info(" result PluginList : {}", result);

            }

            int exitCode = resultHandler.getExitValue();
            handler.stop();

            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                return resultMultiProfileList;
            }else if(exitCode == 1){
                return resultMultiProfileList;
            }else {
                return resultMultiProfileList;
            }

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return resultMultiProfileList;
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
            return resultMultiProfileList;
        } catch (ParseException e) {
            log.warn(e.getMessage(), e);
            return resultMultiProfileList;
        }

    }

    private void executeCommonsExecSetActiveProfile(WebSocketSession session, CommandLine commandLineSetActiveProfileListCLI, String platform, String hqKey){

        MultiProfileStatusMsg multiProfileStatusMsg = new MultiProfileStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        setActiveProfileListMsgHandler(session, multiProfileStatusMsg,"SEARCHING", "SETTINGS", hqKey);
        try {
            handler.start();

            executor.execute(commandLineSetActiveProfileListCLI, resultHandler);
            resultHandler.waitFor();
            // TODO 수정
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray())));
            StringBuilder resultBuilder = new StringBuilder();
            char[] buffer = new char[8192]; // 적절한 버퍼 크기 설정
            int bytesRead;
            while ((bytesRead = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                resultBuilder.append(buffer, 0, bytesRead);
            }
            String result = resultBuilder.toString();
            // result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
            // result = result.replaceAll(" ","");

            ArrayList<String> resultMultiProfileArr = new ArrayList<>();


            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);

            }


            int exitCode = resultHandler.getExitValue();


            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                setActiveProfileListMsgHandler(session, multiProfileStatusMsg,"", PayloadMsgType.SUCCESSFUL.name(), hqKey);
            }else if(exitCode == 1){
                setActiveProfileListMsgHandler(session, multiProfileStatusMsg,"", PayloadMsgType.FAILED.name(), hqKey);
            }else {
                setActiveProfileListMsgHandler(session, multiProfileStatusMsg,"", PayloadMsgType.FAILED.name(), hqKey);
            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }


    }

    private void getAllMultiProfileListMsgHandler(WebSocketSession session, MultiProfileStatusMsg multiProfileStatusMsg, String buildLog, ArrayList<String> resultMultiProfileArr, String messageValue, String hqKey, Map<String, Object> parseResult){
        ObjectMapper Mapper = new ObjectMapper();

        multiProfileStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            multiProfileStatusMsg.setMsgType(BuildServiceType.HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO.name());
        }

        multiProfileStatusMsg.setStatus("getmultiprofile");
        multiProfileStatusMsg.setMessage(messageValue);
        multiProfileStatusMsg.setHqKey(hqKey);
        multiProfileStatusMsg.setManagerClusterId(parseResult.get("managerClusterId").toString());

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
             multiProfileStatusMsg.setResultMultiProfileList(resultMultiProfileArr);
        }

        Map<String, Object> sendParseResult = Mapper.convertValue(multiProfileStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, sendParseResult);
    }


    private void setActiveProfileListMsgHandler(WebSocketSession session, MultiProfileStatusMsg multiProfileStatusMsg, String buildLog, String messageValue, String hqKey){
        ObjectMapper Mapper = new ObjectMapper();

        multiProfileStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SETTINGS") || messageValue.equals(PayloadMsgType.FAILED.name())){
            multiProfileStatusMsg.setMsgType(BuildServiceType.HV_MSG_BUILD_SET_ACTIVE_PROFILE_INFO.name());
        }

        multiProfileStatusMsg.setStatus("getmultiprofile");
        multiProfileStatusMsg.setMessage(messageValue);
        multiProfileStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){

        }

        Map<String, Object> parseResult = Mapper.convertValue(multiProfileStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

}
