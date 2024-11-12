package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.client.j2se.CommandLineEncoder;
import com.inswave.whive.branch.domain.BuildRequest;
import com.inswave.whive.branch.domain.BuildResponse;
import com.inswave.whive.branch.domain.DeploySettingMsg;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.DeployMeataDataTask;
import com.inswave.whive.branch.task.SigningCreateTask;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class DeploySettingInitService extends BaseService {

    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private ReentrantReadWriteLock lock;

    private ReentrantLock reentrantLock = new ReentrantLock();

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    ProjectGitPushService projectGitPushService;

    @Autowired
    DeployMeataDataTask deployMeataDataTask;

    @Value("${whive.distribution.UserRootPath}")
    private String rootPath;

    private String systemUserHomePath = System.getProperty("user.home");


    private JSONParser parser = new JSONParser();
    private ObjectMapper Mapper = new ObjectMapper();


    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

    // @Async("asyncThreadPool")
    public void deploySettingInit(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        log.debug("Current waiting Task : " + waitingQueue.size());
        try {
            lock.readLock().lock();

            deployCommonExecArgument(session, parseResult, builderPath);
        }catch (Exception e) {
            log.info(e.getMessage(), e);
        }finally {
            lock.readLock().unlock();
        }



    }

    public void deployDotENVConfigSettingCLI(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        deployDotENVConfigSettingCLIPrivate(session, parseResult, builderPath);

    }

    private void deployCommonExecArgument(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        String jsonDeployObjStr = parseResult.get("jsonDeployObj").toString();
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();

        DeploySettingMsg deploySettingMsg = new DeploySettingMsg();

        String hqKey;
        JSONObject jsonDeployObj;
        JSONObject jsonRepositoryObj;

        try {
            jsonDeployObj = (JSONObject) parser.parse(jsonDeployObjStr);
            jsonRepositoryObj = (JSONObject) parser.parse(parseResult.get("jsonRepository").toString());
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())) {

                String shellscriptAndroidFileName = getClassPathResourcePath("fastlaneDeployCLI.sh");

                String path = systemUserHomePath + rootPath + "builder_main/" + builderPath;
                // android / ios 분기 처리 해야함..
//                String cmd = "cd " + builderPath + " && mkdir fastlane && touch ./fastlane/Appfile";
                String cmd ="cd " + builderPath + " && printf '\n\nn\n\n\n' | fastlane init";
                CommandLine commandLine;
                if (parseResult.get("product_type").toString().equals("generalApp")) {
                    commandLine = CommandLine.parse("/bin/sh");
                    commandLine.addArgument("-c");
                    commandLine.addArgument(cmd, false);
                } else {
                    commandLine = CommandLine.parse(shellscriptAndroidFileName);
//                // commandLine.addArgument(path + "/" + parseResult.get("buildProjectdir").toString());
//                // commandLineFastfile.addArgument(path + "/" + parseResult.get("buildProjectdir").toString());
//                // builderPath
                    commandLine.addArgument(builderPath);
                    commandLine.addArgument("android-init");
                }

                CommandLine commandLineAppfile = CommandLine.parse(shellscriptAndroidFileName);
                // commandLineFastfile.addArgument(path + "/" + parseResult.get("buildProjectdir").toString());
                // builderPath
                commandLineAppfile.addArgument(builderPath);
                commandLineAppfile.addArgument("android-appfile");
                commandLineAppfile.addArgument(jsonDeployObj.get("googlePlayAccessJson").toString());
                commandLineAppfile.addArgument(jsonDeployObj.get("all_package_name").toString());

                /**
                 * android dotenv 생성
                 */
                CommandLine commandLineDotEnv = CommandLine.parse(shellscriptAndroidFileName);
                commandLineDotEnv.addArgument(builderPath);
                commandLineDotEnv.addArgument("android-env-create");
                commandLineDotEnv.addArgument("");
                commandLineDotEnv.addArgument("");
                commandLineDotEnv.addArgument("");

                CommandLine commandLineFastfile = CommandLine.parse(shellscriptAndroidFileName);

                // builderPath
                commandLineFastfile.addArgument(builderPath);
                commandLineFastfile.addArgument("android-fastfile-create");

                deployExecuteCallBack(session, commandLine, "FASTLANEINIT", hqKey);
                deployExecuteCallBack(session, commandLineAppfile, "APPFILE", hqKey);
                deployExecuteCallBack(session, commandLineFastfile, "FASTFILE", hqKey);
                deployExecuteCallBack(session, commandLineDotEnv, "SETENV", hqKey);

                // TODO supply init 명령어 추가
                if (parseResult.get("product_type").toString().toLowerCase().equals("wmatrix")) {
                    CommandLine cmdLineMeataDataInit = CommandLine.parse(shellscriptAndroidFileName);
                    cmdLineMeataDataInit.addArgument(builderPath);
                    cmdLineMeataDataInit.addArgument("android-metadata-init");

                    deployExecuteCallBack(session, cmdLineMeataDataInit, "METADATAINIT", hqKey);
                }
                // projectGitPushService.gitPushAction(session, platform, path, parseResult.get("buildProjectdir").toString(), parseResult, jsonRepositoryObj, "ProjectDeploySet");
                deploySendMessage(session, deploySettingMsg, hqKey, "DONE", "");

            }else if (platform.toLowerCase().equals(PayloadMsgType.ios.name())){

                jsonDeployObj = (JSONObject) parser.parse(jsonDeployObjStr);


                String shellscriptAndroidFileName = getClassPathResourcePath("fastlaneDeployCLI.sh");

                String path = systemUserHomePath + rootPath + "builder_main/" + builderPath;

                // 1. fastlane init 생성
                CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);
                // commandLineFastfile.addArgument(path + "/" + parseResult.get("buildProjectdir").toString());
                // builderPath
                commandLine.addArgument(builderPath);
                commandLine.addArgument("ios-init");

                // 2. .env 파일 생성
                CommandLine commandLineEnv = CommandLine.parse(shellscriptAndroidFileName);
                // commandLineFastfile.addArgument(path + "/" + parseResult.get("buildProjectdir").toString());
                // builderPath
                commandLineEnv.addArgument(builderPath);
                commandLineEnv.addArgument("ios-env-create");
                commandLineEnv.addArgument(""); // app_path
                commandLineEnv.addArgument(""); //app_file_name
                commandLineEnv.addArgument(jsonDeployObj.get("apple_key_id").toString());
                commandLineEnv.addArgument(jsonDeployObj.get("apple_issuer_id").toString());
                commandLineEnv.addArgument(jsonDeployObj.get("apple_app_store_connect_api_key").toString());
                commandLineEnv.addArgument(""); // BUILD_APP_BUILD_NUMBER


                // 3. Fastfile 생성
                CommandLine commandLineFastfile = CommandLine.parse(shellscriptAndroidFileName);
                // commandLineFastfile.addArgument(path + "/" + parseResult.get("buildProjectdir").toString());
                // builderPath
                commandLineFastfile.addArgument(builderPath);
                commandLineFastfile.addArgument("ios-fastfile-create");
                commandLineFastfile.addArgument(jsonDeployObj.get("ios_app_bundleID").toString());

                deployExecuteCallBack(session, commandLineEnv, "FASTENV", hqKey);
                deployExecuteCallBack(session, commandLineFastfile, "FASTFILE", hqKey);

                if (parseResult.get("product_type").toString().toLowerCase().equals("wmatrix")) {

                    // TODO: file read witre 기능 구현...
                    String iosAppStoreConnectAPIKey = deployMeataDataTask.getiOSAppStoreConnectAPIKeyDataListSearch(parseResult, builderPath);

                    // TODO : Create ios-file.json 생성
                    /**
                     *
                     * 아래 json 파일 생성 하기...
                     *  {
                     *   "key_id": "AAAAAA93",
                     *   "issuer_id": "",
                     *   "key": "-----BEGIN PRIVATE KEY-----\n55422\n-----END PRIVATE KEY-----",
                     *   "duration": 1200,
                     *   "in_house": false
                     * }
                     */
                    log.info("iosAppStoreConnectAPIKey = {}", iosAppStoreConnectAPIKey);
                    parseResult.put("iosAppStoreConnectAPIKey",iosAppStoreConnectAPIKey);

                    // deployExecuteCallBack(session, commandLine, "FASTLANEINIT", hqKey);
                    //deployProcessBuilderExec(commands);

                    deployMeataDataTask.setiOSJSONfileDataListWrite(parseResult, builderPath);

                    // TODO : ios-metadata-init
                    CommandLine cmdLineMetaDataInit = CommandLine.parse(shellscriptAndroidFileName);
                    cmdLineMetaDataInit.addArgument(builderPath);
                    cmdLineMetaDataInit.addArgument("ios-metadata-init");
                    cmdLineMetaDataInit.addArgument(builderPath + "/fastlane/ios-file.json");
                    cmdLineMetaDataInit.addArgument(jsonDeployObj.get("ios_app_bundleID").toString()); // TODO : app_bundleidentifer 화면 단에 처리 하기
                    deployDefaultExecuteCallBack(session, cmdLineMetaDataInit, "METADATAINIT", hqKey);
                }

                // projectGitPushService.gitPushAction(session, platform, path, parseResult.get("buildProjectdir").toString(), parseResult, jsonRepositoryObj, "ProjectDeploySet");
                deploySendMessage(session, deploySettingMsg, hqKey, "DONE", "");
            }



        } catch (ParseException e) {

        }
    }



    private void deployDotENVConfigSettingCLIPrivate(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();

        JSONObject jsonDeployObj;
        String hqKey;

        hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptAndroidFileName = getClassPathResourcePath("fastlaneDeployCLI.sh");
            // android / ios 분기 처리 해야함..
            CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);
            commandLine.addArgument(builderPath);
            commandLine.addArgument("android-env-create");
            commandLine.addArgument(parseResult.get("app_path").toString());
            commandLine.addArgument(parseResult.get("appfile_name").toString(),false);

            if (parseResult.get(PayloadMsgType.AppVersionCode.name()).toString().equals("")) {
                commandLine.addArgument("1");
            }else {
                commandLine.addArgument(parseResult.get(PayloadMsgType.AppVersionCode.name()).toString());
            }


            deployExecuteCallBack(session, commandLine, "ENVSETTING", hqKey);

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            String jsonDeployObjStr = parseResult.get("jsonDeployObj").toString();
            try {

                jsonDeployObj = (JSONObject) parser.parse(jsonDeployObjStr);

                String shellscriptAndroidFileName = getClassPathResourcePath("fastlaneDeployCLI.sh");
                // android / ios 분기 처리 해야함..
                CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);
                commandLine.addArgument(builderPath);
                commandLine.addArgument("ios-env-create");
                commandLine.addArgument(parseResult.get("app_path").toString());
                commandLine.addArgument(parseResult.get("appfile_name").toString());
                commandLine.addArgument(jsonDeployObj.get("apple_key_id").toString());
                commandLine.addArgument(jsonDeployObj.get("apple_issuer_id").toString());
                commandLine.addArgument(jsonDeployObj.get("apple_app_store_connect_api_key").toString());
                commandLine.addArgument(parseResult.get(PayloadMsgType.AppVersionCode.name()).toString());
                // 기존의 ios app store api key data

                deployExecuteCallBack(session, commandLine, "ENVSETTING", hqKey);

            } catch (ParseException e) {

            }

        }


    }

    public void deployExecuteCallBack(WebSocketSession session, CommandLine cli, String deploySettingMode, String hqKey){

        DeploySettingMsg deploySettingMsg = new DeploySettingMsg();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();

        // DataInputStream
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DaemonExecutor executor = new DaemonExecutor();
        BufferedReader is = null;
        BufferedReader iserr = null;

        try {

            handler.start();
            // handler.setProcessInputStream(out);
            String logData = "";
            String errData = "";

            executor.setStreamHandler(handler);
            executor.execute(cli, resultHandler);
            deploySendMessage(session, deploySettingMsg, hqKey, deploySettingMode, "");
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
                log.info(" deploy Service PipedInputStream deploying log data : {}", logData);


            }

            iserr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutErr.toByteArray())));
            while((errData = iserr.readLine()) != null){

                log.error(" deploy error {}", errData);
            }

            resultHandler.waitFor();
            // log file download json data set
            // logfile 생성
            int exitCode = resultHandler.getExitValue();


            log.info(" exitCode : {}", exitCode);



            handler.stop();

        } catch (ExecuteException e){
            log.info(e.getMessage(), e);
            resultHandler.onProcessFailed(e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                log.info(exception.getMessage(), exception);
            }
        }

    }

    public void deployDefaultExecuteCallBack(WebSocketSession session, CommandLine cli, String deploySettingMode, String hqKey){

        DeploySettingMsg deploySettingMsg = new DeploySettingMsg();

        try {

//            ByteArrayInputStream input =
//                    new ByteArrayInputStream("4".getBytes("ISO-8859-1"));
            PipedInputStream pipedInputStream = new PipedInputStream();
            PipedOutputStream pipedOutput = new PipedOutputStream();
            ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();

            // DataInputStream
            PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, pipedInputStream);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            BufferedReader is = null;
            BufferedReader iserr = null;

            handler.start();
//            handler.setProcessInputStream(out);
            String logData = "";
            String errData = "";

            executor.setStreamHandler(handler);
            executor.execute(cli, resultHandler);


            deploySendMessage(session, deploySettingMsg, hqKey, deploySettingMode, "");
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
                log.info(" deploy Service PipedInputStream deploying log data : {}", logData);


            }

            iserr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutErr.toByteArray())));
            while((errData = iserr.readLine()) != null){

                log.error(" deploy error {}", errData);
            }

            resultHandler.waitFor();


            int exitCode = resultHandler.getExitValue();
                    // logfile 생성
            log.info(" exitCode : {}", exitCode);
            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {

            }
        }

    }

    private void deploySendMessage(WebSocketSession session, DeploySettingMsg deploySettingMsg, String hqKeyTemp, String deployStatus, String logMessage){

        deploySettingMsg.setMsgType(ProjectServiceType.HV_MSG_DEPLOY_SETTING_STATUS_INFO.name());
        deploySettingMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        deploySettingMsg.setHqKey(hqKeyTemp); // hqKey User 아이디

        deploySettingMsg.setStatus(deployStatus);

        deploySettingMsg.setMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(deploySettingMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);


    }
}
