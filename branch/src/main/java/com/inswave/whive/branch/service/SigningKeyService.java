package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hazelcast.internal.json.Json;
import com.inswave.whive.branch.domain.*;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.SigningCreateTask;
import com.inswave.whive.branch.util.BranchRestTempleteUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SigningKeyService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(SigningKeyService.class);
    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private Map<String, String> taskList;
    private ReentrantReadWriteLock lock;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private String buildLogs;

    private ObjectMapper Mapper = new ObjectMapper();

    @Value("${whive.distribution.deployLogPath}")
    private String rootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    BranchRestTempleteUtil branchRestTempleteUtil;

    @Autowired
    private Executor asyncThreadPool;

    @Autowired
    private SigningCreateTask signingCreateTask;

    WebSocketSession sessionTemp;
    JSONObject builderSettingTemp;
    JSONParser parser = new JSONParser();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        logger.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

    @Async("asyncThreadPool")
    public void startSigninKeyAdd(String path, String platform, SigningMode mode, Map<String, Object> keytoolResult) {
        try {
            logger.debug("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            executueSigningCreate(uuid, path, platform, mode, keytoolResult);
        } catch (Exception e) {
            logger.warn("Exception occur while checking waiting queue", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    // 사용하지 않지만 가지고 있기
    public void executueProcessSigniningCreate(String createTaskId, String path, String platform, SigningMode mode, Map<String, Object> keytoolResult) {
        logger.info(" #### buildservice parameter check ####{} {}" ,platform ,mode);

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
            if (mode.equals(SigningMode.CREATE)) {
                List<ProcessBuilder> signingCreateList = Collections.synchronizedList(new ArrayList<ProcessBuilder>());
                signingCreateList.add(new ProcessBuilder("cd",path,";","keytool"
                        ,"-genkey","-dname"
                        ,"CN=MarkJones,OU=JavaSoft,O=organizationName,L=localityName,C=country"
                        ,"-alias",keytoolResult.get("key_alias").toString()
                        ,"-keypass",keytoolResult.get("key_password").toString()
                        ,"-keystore",keytoolResult.get("keystore_file_name").toString()
                        ,"-storepass",keytoolResult.get("all_keyfile_password").toString()
                        ,"-keyalg","RSA"
                        ,"-keysize","2048"
                        ,"-validity","10000"));

                executeSigningCreateListSync(signingCreateList, createTaskId);
            } else {

            }
        } else {

        }

    }

    // 사용하지 않지만 가지고 있기
    /* android clean build cli executueApacheBuild method */
    public void executueSigningCreate(String buildTaskId, String path, String platform, SigningMode mode, Map<String, Object> keytoolResult) throws Exception {
        logger.info(" #### buildservice parameter check ####{} {}", platform, mode);

        // android build 수행 조건
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(SigningMode.CREATE)) {
                CommandLine commandLine = CommandLine.parse("keytool");
                commandLine.addArgument("-genkey");
                commandLine.addArgument("-dname");
                commandLine.addArgument("\"CN=MarkJones,OU=JavaSoft,O=organizationName,L=localityName,C=country\"");
                commandLine.addArgument("-alias");
                commandLine.addArgument(keytoolResult.get("key_alias").toString());
                commandLine.addArgument("-keypass");
                commandLine.addArgument(keytoolResult.get("key_password").toString());
                commandLine.addArgument("-keystore");
                commandLine.addArgument(keytoolResult.get("keystore_file_name").toString());
                commandLine.addArgument("-storepass");
                commandLine.addArgument(keytoolResult.get("all_keyfile_password").toString());
                commandLine.addArgument("-keyalg");
                commandLine.addArgument("RSA");
                commandLine.addArgument("-keysize");
                commandLine.addArgument(String.valueOf(2048));
                commandLine.addArgument("-validity");
                commandLine.addArgument(String.valueOf(10000));

                CommandLine commandLineMove = CommandLine.parse("mv");
                commandLineMove.addArgument(System.getProperty("user.dir")+"/"+keytoolResult.get("keystore_file_name").toString());
                commandLineMove.addArgument(""+keytoolResult.get("keystore_file_name").toString());

                // executeCommonsSigningCreate(commandLine, androidPath, platform, keytoolResult);
                // executeCommonsSigningCreate(commandLineMove, androidPath, platform, keytoolResult);

            }
            // ios build 조건
        }
    }

    // headquater signingkey file 요청
    @Async("asyncThreadPool")
    public void signingKeyFileTransferHttp(WebSocketSession session, JSONObject jsonBuilderSetting, String userProjectPath, JSONObject signingKeyJson){
        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        sessionTemp = session;
        builderSettingTemp = jsonBuilderSetting;
        // send message msg type = HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO
        projectCreateMessage(projectGitCloneMessage, null, "FILEUPLOAD", userProjectPath, signingKeyJson);



    }

    // android signing key create
    public void createSigningkeyFileToProperties(String path, String platform,JSONObject signingKeyJson){
        try {
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                createKeystorePropertiesFile(path, signingKeyJson);
            }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            }


        } catch (IOException e) {

        }
    }

    public void createProfileSettingToDebugProject(String path, Map<String, Object> iosProvisioningResult, String productType){
        createiOSProvisioningFileSetting("debug", path, iosProvisioningResult, productType);
    }

    public void createProfileSettingToReleaseProject(String path, Map<String, Object> iosProvisioningResult, String productType){
        createiOSProvisioningFileSetting("release", path, iosProvisioningResult, productType);
    }

    public void createProfileSettingToMultiProfilesProject(String path, String iosProvisioningResult, String productType, String[] target, JsonArray jsonMatrix){
        createiOSProvisioningFileMultiProfilesSetting("all", path, iosProvisioningResult, productType, target, jsonMatrix);
    }

    public void importiOSProvisioningFileMultiProfilesSetting(String path, String iosProvisioningResult, String productType, String[] target, JsonArray jsonMatrix){
        importiOSProvisioningFileMultiProfilesSetting("all", path, iosProvisioningResult, productType, target, jsonMatrix);
    }

    public void createSigningkeyToCertFile(String path, Map<String, Object> iosSigningkeyResult, String productType){
        createiOSSigningKeyCertFileSetting(path, iosSigningkeyResult, productType);
    }

    public void createSigningkeyToMultiCertFile(String path, String arrayJSONCertificateStr, String productType){
        createiOSSigningKeyMultiCertFileSetting(path, arrayJSONCertificateStr, productType);
    }

    public String[] showProfilesInfo(WebSocketSession session, Map<String, Object> iosProvisioningResult, String productType){
        sessionTemp = session;
        String[] returnStr = showiOSProfilesInfo(iosProvisioningResult, productType);

        return returnStr;
    }

    public JSONObject showProfileOneInfo(WebSocketSession session, String iosProvisioningResult, String productType){
        sessionTemp = session;
        JSONObject returnStr = showiOSProfilesOneInfo(iosProvisioningResult, productType);

        return returnStr;
    }

    public void setExportOptionsDebug(WebSocketSession session, String path, String[] showProfileStr, JSONObject applyConfigObj, String productType){
        sessionTemp = session;
        setiOSExportOptionsDebug(path, showProfileStr, applyConfigObj, productType);
    }

    public void setExportOptionsRelease(WebSocketSession session, String path, String[] showProfileStr, JSONObject applyConfigObj, String productType){
        sessionTemp = session;
        setiOSExportOptionsRelease(path, showProfileStr, applyConfigObj, productType);
    }

    public void setExportOptionsMultiProfiles(WebSocketSession session, String path, JSONObject showProfileStr, JSONObject jsonProfile, String productType){
        sessionTemp = session;
        setiOSExportOptionsMultiProfiles(path, showProfileStr, jsonProfile, productType);
    }

    public void setExportOptionsAdhoc(WebSocketSession session, String path, String[] showProfileStr, JSONObject applyConfigObj){
        sessionTemp = session;
        setiOSExportOptionsAdhoc(path, showProfileStr, applyConfigObj);
    }

    private void setiOSExportOptionsDebug(String path, String[] showProfileStr, JSONObject applyConfigObj, String productType) {

        String setExportJsonString = "{\"compileBitcode\":\"false\",\"destination\":\"export\"," +
                "\"method\":\"development\",\"provisioningProfiles\":{\""+applyConfigObj.get(PayloadMsgType.ApplicationID.name()).toString()+"\":\""+ showProfileStr[2] +"\"}," +
                "\"signingCertificate\":\"Apple Development\",\"signingStyle\":\"manual\",\"stripSwiftSymbols\":\"true\"," +
                "\"teamID\":\""+showProfileStr[0] +"\",\"thinning\":\"<none>\",\"fileName\":\"ExportOptionsDebug\"}";

        CommandLine commandLineiOSExportOptions = null;

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineiOSExportOptions = CommandLine.parse("wmatrixmanager");
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineiOSExportOptions = CommandLine.parse("whmanager");
        }else {
            commandLineiOSExportOptions = CommandLine.parse("whmanager");
        }

        commandLineiOSExportOptions.addArgument("setexportoptions");
        commandLineiOSExportOptions.addArgument("-p");
        commandLineiOSExportOptions.addArgument(path);
        commandLineiOSExportOptions.addArgument("-i");
        commandLineiOSExportOptions.addArgument(setExportJsonString, false);

        // cli exec 처리
        try {
            executeCommonsExecSetExportOptions(sessionTemp, commandLineiOSExportOptions);

        } catch (Exception e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    private void setiOSExportOptionsRelease(String path, String[] showProfileStr, JSONObject applyConfigObj, String productType) {

        String setExportJsonString = "{\"compileBitcode\":\"false\",\"destination\":\"export\"," +
                "\"method\":\""+applyConfigObj.get("release_type").toString()+"\",\"provisioningProfiles\":{\""+applyConfigObj.get(PayloadMsgType.ApplicationID.name()).toString()+"\":\""+ showProfileStr[2] +"\"}," +
                "\"signingCertificate\":\"Apple Distribution\",\"signingStyle\":\"manual\",\"stripSwiftSymbols\":\"true\"," +
                "\"teamID\":\""+showProfileStr[0] +"\",\"thinning\":\"<none>\",\"fileName\":\"ExportOptionRelease\"}"; // method : enterprise
        CommandLine commandLineiOSExportOptions = null;

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineiOSExportOptions = CommandLine.parse("wmatrixmanager");
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineiOSExportOptions = CommandLine.parse("whmanager");
        }else {
            commandLineiOSExportOptions = CommandLine.parse("whmanager");
        }


        commandLineiOSExportOptions.addArgument("setexportoptions");
        commandLineiOSExportOptions.addArgument("-p");
        commandLineiOSExportOptions.addArgument(path);
        commandLineiOSExportOptions.addArgument("-i");
        commandLineiOSExportOptions.addArgument(setExportJsonString, false);

        // cli exec 처리
        try {
            executeCommonsExecSetExportOptions(sessionTemp, commandLineiOSExportOptions);

        } catch (Exception e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    private void setiOSExportOptionsAdhoc(String path, String[] showProfileStr, JSONObject applyConfigObj) {

        String setExportJsonString = "{\"compileBitcode\":\"false\",\"destination\":\"export\"," +
                "\"method\":\"development\",\"provisioningProfiles\":{\""+applyConfigObj.get(PayloadMsgType.ApplicationID.name()).toString()+"\":\""+ showProfileStr[2] +"\"}," +
                "\"signingCertificate\":\"Apple Distribution\",\"signingStyle\":\"manual\",\"stripSwiftSymbols\":\"true\"," +
                "\"teamID\":\""+showProfileStr[0] +"\",\"thinning\":\"<none>\",\"fileName\":\"ExportOptionAdhoc\"}";
        CommandLine commandLineiOSExportOptions = null;

        commandLineiOSExportOptions = CommandLine.parse("whmanager");
        commandLineiOSExportOptions.addArgument("setexportoptions");
        commandLineiOSExportOptions.addArgument("-p");
        commandLineiOSExportOptions.addArgument(path);
        commandLineiOSExportOptions.addArgument("-i");
        commandLineiOSExportOptions.addArgument(setExportJsonString, false);

        // cli exec 처리
        try {
            executeCommonsExecSetExportOptions(sessionTemp, commandLineiOSExportOptions);

        } catch (Exception e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    private void setiOSExportOptionsMultiProfiles(String path, JSONObject showProfileStr, JSONObject jsonProfile, String productType) {

        try {

            JSONArray teamID = (JSONArray) showProfileStr.get("TeamIdentifier");
            log.info(teamID.get(0).toString());

            JSONObject jsonEntitlements = (JSONObject) parser.parse(showProfileStr.get("Entitlements").toString());

            String applicationIdentifier = jsonEntitlements.get("application-identifier").toString();
            applicationIdentifier = applicationIdentifier.replace(teamID.get(0).toString()+".","");

            String setExportJsonString = "{\"compileBitcode\":\"false\",\"destination\":\"export\"," +
                    "\"method\":\""+jsonProfile.get("profiles_build_type").toString()+"\",\"provisioningProfiles\":{\""+applicationIdentifier+"\":\""+ showProfileStr.get("Name").toString() +"\"}," +
                    "\"signingCertificate\":\"Apple Distribution\",\"signingStyle\":\"manual\",\"stripSwiftSymbols\":\"true\"," +
                    "\"teamID\":\""+teamID.get(0).toString() +"\",\"thinning\":\"<none>\",\"fileName\":\"ExportOption"+jsonProfile.get("profiles_key_name").toString()+"\"}"; // method : enterprise

            CommandLine commandLineiOSExportOptions = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineiOSExportOptions = CommandLine.parse("wmatrixmanager");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineiOSExportOptions = CommandLine.parse("whmanager");
            }else {
                commandLineiOSExportOptions = CommandLine.parse("whmanager");
            }


            commandLineiOSExportOptions.addArgument("setexportoptions");
            commandLineiOSExportOptions.addArgument("-p");
            commandLineiOSExportOptions.addArgument(path);
            commandLineiOSExportOptions.addArgument("-j");
            commandLineiOSExportOptions.addArgument(setExportJsonString, false);

            // cli exec 처리
            try {
                executeCommonsExecSetExportOptions(sessionTemp, commandLineiOSExportOptions);

            } catch (Exception e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    // Plugman showProfile 호출 메소드
    private String[] showiOSProfilesInfo(Map<String, Object> iosProvisioningResult, String productType) {
        String[] returnStr = new String[0];
        CommandLine commandLineiOSProvisioningSetting = null;

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }else {
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }

        commandLineiOSProvisioningSetting.addArgument("showprofile");
        commandLineiOSProvisioningSetting.addArgument("-pp");
        commandLineiOSProvisioningSetting.addArgument(iosProvisioningResult.get("profilePath").toString(),false);

        // cli exec 처리
        try {
            returnStr = executeCommonsExecShowProfileInfoList(sessionTemp, commandLineiOSProvisioningSetting, "iOS");
            return returnStr;
        } catch (Exception e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return returnStr;
        }

    }

    private JSONObject showiOSProfilesOneInfo(String iosProvisioningResult, String productType) {
        JSONObject returnStr = null;
        CommandLine commandLineiOSProvisioningSetting = null;

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }else {
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }

        commandLineiOSProvisioningSetting.addArgument("showprofile");
        commandLineiOSProvisioningSetting.addArgument("-pp");
        commandLineiOSProvisioningSetting.addArgument(iosProvisioningResult,false);

        // cli exec 처리
        try {
            returnStr = executeCommonsExecMultiProfilesShowProfileInfoList(commandLineiOSProvisioningSetting, "iOS");
            return returnStr;
        } catch (Exception e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return returnStr;
        }

    }

    public void executeSigningCreateListSync(List<ProcessBuilder> signingCreateList, String createTaskId){
        ProcessBuilder builder;

        synchronized(signingCreateList) {
            reentrantLock.lock();
            Iterator i = signingCreateList.iterator();
            while (i.hasNext()) {
                builder = (ProcessBuilder) i.next();
                builder.redirectErrorStream(true);
                Process p = null;
                try {
                    p = builder.start();
                    signingCreateTask.setProcess(p);
                    signingCreateTask.setSigningKeyService(this);
                    signingCreateTask.setSigningCreateTaskId(createTaskId);
                    asyncThreadPool.execute(signingCreateTask);

                } catch (Exception e) {

                    if (p != null) {
                        p.destroy();
                    }
                } finally {

                    reentrantLock.unlock();
                }
            }
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecBuild method   */
    private void executeCommonsSigningCreate(CommandLine commandLineParse, String path, String platform, Map<String, Object> keytoolResult) throws Exception {

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        try {
            handler.start();
            CreateSiginingMessage createSiginingMessage = new CreateSiginingMessage();
            signingKeyAddMessageHandler(createSiginingMessage, "CREATING");

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();
            LocalDateTime date = LocalDateTime.now();
            String buildAfterLogFile = systemUserHomePath + rootPath+platform+"_log"+date+".txt";

            Reader reader = new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray()));
            BufferedReader r = new BufferedReader(reader);
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            String tmp = null;

            while ((tmp = r.readLine()) != null)
            {
                out.write(tmp+"\n");
                logger.info(" #### Build CommandLine log data ### : " + tmp);

            }
            r.close();
            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(String.valueOf(exitCode));
            if(exitCode == 0){
                // keystore.properties 생성 메소드 추가
                // android 일때 처리
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    createKeystorePropertiesFile(path, keytoolResult);
                }

                signingKeyAddMessageHandler(createSiginingMessage, PayloadMsgType.SUCCESSFUL.name());
                // ios 일때 처리

            } else if(exitCode == 1){
                signingKeyAddMessageHandler(createSiginingMessage, PayloadMsgType.FAILED.name());

            } else {
                signingKeyAddMessageHandler(createSiginingMessage, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (Exception e) {
            logger.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecPluginList method   */
    public String[] executeCommonsExecShowProfileInfoList(WebSocketSession session, CommandLine commandLineParse, String platform) throws Exception {

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        sessionTemp = session;
        // pluginListStatusMessage.setHqKey(JSONbranchSettingObj.get("hqKey").toString()); // session headquarter key setting
        try {
            handler.start();

            executor.execute(commandLineParse, resultHandler);
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
            result = result.replaceAll("(\r\n|\r|\n|\n\r)","");

            String[] resultArr = new String[3];

            String resultProfileName = "";
            String resultTeamId = "";
            String reulstTeamName = "";

            int idxProfileNameStart;
            int idxProfileNameEnd;
            int idxTeamIdStart;
            int idxTeamIdEnd;
            int idxTeamNameStart;
            int idxTeamNameEnd;


            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // function,
                // show Profile sub string index 처리
                idxProfileNameStart = result.indexOf("***name*start***");
                idxProfileNameEnd = result.indexOf("***name*end***");

                idxTeamIdStart = result.indexOf("***teamIdentifer*start***");
                idxTeamIdEnd = result.indexOf("***teamIdentifer*end***");

                idxTeamNameStart = result.indexOf("***teamName*start***");
                idxTeamNameEnd = result.indexOf("***teamName*end***");

                // teamID, profilename, teamName 파싱해서 String[] 값에 쌓아두기
                // TeamID
                resultTeamId = result.substring(idxTeamIdStart,idxTeamIdEnd);
                resultTeamId = resultTeamId.replace("***teamIdentifer*start***","");

                log.info(" input teamIdentifer : {}", resultTeamId);
                resultArr[0] = resultTeamId;

                // TeamName
                reulstTeamName = result.substring(idxTeamNameStart,idxTeamNameEnd);
                reulstTeamName = reulstTeamName.replace("***teamName*start***","");

                log.info(" input teamName : {}", reulstTeamName);
                resultArr[1] = reulstTeamName;

                // Profile Name

                resultProfileName = result.substring(idxProfileNameStart,idxProfileNameEnd);
                resultProfileName = resultProfileName.replace("***name*start***","");

                log.info(" input profile name : {}", resultProfileName);
                resultArr[2] = resultProfileName;

            }


            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);
            handler.stop();

            if(exitCode == 0){
                return resultArr;
            } else if(exitCode == 1){
                return resultArr;
            } else {
                log.info(" exitCode : {}", exitCode);
                return resultArr;
            }

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecPluginList method   */
    public JSONObject executeCommonsExecMultiProfilesShowProfileInfoList(CommandLine commandLineParse, String platform) throws Exception {

        JSONObject multiProfilesShowProfileInto = null;
        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        // pluginListStatusMessage.setHqKey(JSONbranchSettingObj.get("hqKey").toString()); // session headquarter key setting
        try {
            handler.start();

            executor.execute(commandLineParse, resultHandler);
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
            result = result.replaceAll("(\r\n|\r|\n|\n\r)","");

            String[] resultArr = new String[3];

            String resultProfileName = "";
            String resultTeamId = "";
            String reulstTeamName = "";

            int idxProfileNameStart;
            int idxProfileNameEnd;
            int idxTeamIdStart;
            int idxTeamIdEnd;
            int idxTeamNameStart;
            int idxTeamNameEnd;


            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // function,
                // show Profile sub string index 처리
                log.info(" input platform : {}", platform);
                log.info(" result PluginList : {}", result);
                multiProfilesShowProfileInto = (JSONObject) parser.parse(result);


            }


            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);
            handler.stop();

            if(exitCode == 0){
                return multiProfilesShowProfileInto;
            } else if(exitCode == 1){
                return multiProfilesShowProfileInto;
            } else {
                log.info(" exitCode : {}", exitCode);
                return multiProfilesShowProfileInto;
            }

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    private void executeCommonsExecSetExportOptions(WebSocketSession session, CommandLine commandLineParse) throws Exception {
        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        try {
            handler.start();
            CreateSiginingMessage createSiginingMessage = new CreateSiginingMessage();
            signingKeyAddMessageHandler(createSiginingMessage, "CREATING");

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();

            Reader reader = new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray()));
            BufferedReader r = new BufferedReader(reader);
            String tmp = null;

            while ((tmp = r.readLine()) != null)
            {
                logger.info(" #### Build CommandLine log data ### : " + tmp);

            }
            r.close();
            resultHandler.getExitValue();

            handler.stop();

        } catch (Exception e) {
            logger.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    private void signingKeyAddMessageHandler(CreateSiginingMessage createSiginingMessage, String messageValue){

        createSiginingMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        createSiginingMessage.setMsgType(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name());
        createSiginingMessage.setStatus("signing");
        createSiginingMessage.setMessage(messageValue);

        Map<String, Object> parseResult = Mapper.convertValue(createSiginingMessage, Map.class);
        headQuaterClientHandler.sendMessage(parseResult);
    }

    private void projectCreateMessage(ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus, String userProjectPath, JSONObject signingKeyJson){

        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        projectGitCloneMessage.setHqKey(builderSettingTemp.get(PayloadMsgType.branchUserId.name()).toString());

        if (gitStatus.equals("GITCLONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("SVNCHECKOUT")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("FILEUPLOAD")){
            projectGitCloneMessage.setGitStatus(gitStatus);
            // signingkey json object setting
            projectGitCloneMessage.setKeystorePath(signingKeyJson.get("signingkey_path").toString());
            projectGitCloneMessage.setSigningKeyObj(signingKeyJson);
            projectGitCloneMessage.setUserProjectPath(userProjectPath);
        } else if(gitStatus.equals("DONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        // session 추가
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }

    // create key store file / path : android vcs clone 연동 시 필요
    private void createKeystorePropertiesFile(String path, Map<String, Object> keytoolResult) throws IOException {

        // String buildAfterLogFile = path+"/02_WMatrix_Presentation"+"/keystore.properties"; // WHybrid_Android 수정 필요...
        String buildAfterLogFile = path + "/keystore.properties"; // path 값을 받을 떄 full path 를 받는다..

        File f = new File(buildAfterLogFile);
        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);

        out.append("storePassword = "+keytoolResult.get("profile_password").toString()+"\n");
        out.append("keyPassword = "+keytoolResult.get("key_password").toString()+"\n");
        out.append("keyAlias = "+keytoolResult.get("key_alias").toString()+"\n");
        out.append("storeFile = "+keytoolResult.get("signingkey_path").toString() +"\n");

        out.flush();
        out.close();

    }

    // ios provisioning file 처리
    private void createiOSProvisioningFileSetting(String buildMode, String path, Map<String, Object> iosProvisioningResult, String productType){

        // cli 처리 및 파라미터 입력
        String setProfileStr = "";
        CommandLine commandLineiOSProvisioningSetting = null;

        // debug, release 일때 처리 및 각 path 경로 시점 구분해서 처리하기
        if(buildMode.toLowerCase().equals("debug")){
            setProfileStr = "{\"BuildConfig\":\"Debug\"" +
                    ",\"ProfilePath\":\"" + iosProvisioningResult.get("profilePath").toString() +
                    "\",\"ProjectPath\":\""+ path + "\"}";
        }else if(buildMode.toLowerCase().equals("release")){
            setProfileStr = "{\"BuildConfig\":\"Release\"" +
                    ",\"ProfilePath\":\"" + iosProvisioningResult.get("profilePath").toString() +
                    "\",\"ProjectPath\":\""+ path + "\"}";
        }

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }else {
            commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
        }

        commandLineiOSProvisioningSetting.addArgument("setprofile");
        commandLineiOSProvisioningSetting.addArgument("-j");
        commandLineiOSProvisioningSetting.addArgument(setProfileStr,false);

        // cli exec 처리
        try {
            executeCommonsSigningCreate(commandLineiOSProvisioningSetting, path, "iOS", null);
        } catch (Exception e) {

        }
    }

    private void createiOSProvisioningFileMultiProfilesSetting(String buildMode, String path, String iosProvisioningResult, String productType, String[] target, JsonArray jsonMatrix) {

        JsonParser parser = new JsonParser();

        CommandLine commandLineiOSProvisioningSetting = null;

        for (int i = 0; i < target.length; i++) {
            JsonObject setProfile = new JsonObject();
            String targetName = target[i].toString();

            for (int j = 0; j < jsonMatrix.size(); j++) {
                JsonObject targetConfig = (JsonObject) jsonMatrix.get(j);

                JsonArray profileConfigs = parser.parse(iosProvisioningResult.toString()).getAsJsonArray();
                JsonObject profileConfig = new JsonObject();

                for (int k = 0; k < profileConfigs.size(); k++) {
                    profileConfig = profileConfigs.get(k).getAsJsonObject();
                    if (profileConfig.get("profiles_key_name").toString().equals(targetName)) {
                        break;
                    }
                }

                // wmatrixmanager 1.3.7.2_fix2 버전 이후부터 BuildConfig값을 wmatrixmanager에서 설정한다.
                // 하위호환성을 위해서 공백만 넣어준다.
//                setProfile.addProperty("BuildConfig", targetConfig.get("profile_name").toString().replace("\"", ""));
                setProfile.addProperty("BuildConfig", "");
                setProfile.addProperty("ProfilePath", profileConfig.get("profiles_path").toString().replace("\"", ""));
                setProfile.addProperty("ProjectPath", path.toString());
                setProfile.addProperty("Target", targetName);

                String setProfileString = setProfile.toString();
                commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager setprofile -j");
                commandLineiOSProvisioningSetting.addArgument(setProfileString, false);

                try {
                    executeCommonsSigningCreate(commandLineiOSProvisioningSetting, path, "iOS", null);
                } catch (Exception e) {
                    log.error("iOS set profile error = {}", e.getMessage(), e);
                }
            }
        }
    }

    private void importiOSProvisioningFileMultiProfilesSetting(String buildMode, String path, String iosProvisioningResult, String productType, String[] target, JsonArray jsonMatrix){

        try {
            // cli 처리 및 파라미터 입력
            String setProfileStr = "";
            String setReleaseProfileStr= "";

            CommandLine commandLineiOSProvisioningSetting = null;
            CommandLine commandLineiOSProvisioningReleaseSetting = null;

            for(int t = 0; t < target.length; t++){

                String targetOne = target[t].toString();

                for(int tm = 0; tm <jsonMatrix.size(); tm++) {

                    JsonObject jsonTarget = (JsonObject) jsonMatrix.get(tm);

                    setProfileStr = iosProvisioningResult;
                    setProfileStr = setProfileStr.replaceAll(" ","");
                    setProfileStr = setProfileStr.replace("[{","");
                    setProfileStr = setProfileStr.replace("}]","");
                    String[] setProfilesArrayJson = setProfileStr.split("\\},\\{");

                    for (int i = 0; i < setProfilesArrayJson.length; i++) {
                        JSONObject jsonProfilesOne = null;
                        setProfilesArrayJson[i] = "{" + setProfilesArrayJson[i] + "}";

                        jsonProfilesOne = (JSONObject) parser.parse(setProfilesArrayJson[i]);

//                            log.info(jsonTarget.toJSONString());
//                            log.info(jsonProfilesOne.toJSONString());

                        // profile debug 일때
                        if (targetOne.contains(jsonProfilesOne.get("profiles_key_name").toString()) && jsonTarget.get("debug_type").getAsString().equals(jsonProfilesOne.get("profiles_key_name"))) {
                            // debug, release 일때 처리 및 각 path 경로 시점 구분해서 처리하기
                            if (jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("development") && jsonTarget.get("debug_type").getAsString().equals(jsonProfilesOne.get("profiles_key_name"))) {
                                setReleaseProfileStr = "{\"Target\":\"" + targetOne + "\",\"BuildConfig\":\"Debug\"" +
                                        ",\"ProfilePath\":\"" + jsonProfilesOne.get("profiles_path").toString() +
                                        "\",\"ProjectPath\":\"" + path + "\"}";

                            }

                        }

                        if (!targetOne.equals("") && !jsonTarget.get("debug_type").getAsString().equals("")) {
                            // debug, release 일때 처리 및 각 path 경로 시점 구분해서 처리하기
                            if (jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("development") && !jsonTarget.get("debug_type").getAsString().equals("")) {
                                setReleaseProfileStr = "{\"Target\":\"" + targetOne + "\",\"BuildConfig\":\"Debug\"" +
                                        ",\"ProfilePath\":\"" + jsonProfilesOne.get("profiles_path").toString() +
                                        "\",\"ProjectPath\":\"" + path + "\"}";

                            }

                        }

                        // profile release 일떄
                        if (!targetOne.equals("") && jsonTarget.get("build_type").equals(jsonProfilesOne.get("profiles_key_name"))) {
                            // debug, release 일때 처리 및 각 path 경로 시점 구분해서 처리하기
                            if ((jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("enterprise")
                                    || jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("app-store")) && jsonTarget.get("build_type").equals(jsonProfilesOne.get("profiles_key_name"))) {
                                setReleaseProfileStr = "{\"Target\":\"" + targetOne + "\",\"BuildConfig\":\"Release\"" +
                                        ",\"ProfilePath\":\"" + jsonProfilesOne.get("profiles_path").toString() +
                                        "\",\"ProjectPath\":\"" + path + "\"}";

                            }

                        }

                        if (!targetOne.equals("") && jsonProfilesOne.get("profiles_key_name").toString().equals("Release")) {

                            if (jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("development") && !jsonTarget.get("debug_type").equals("")) {
                                setReleaseProfileStr = "{\"Target\":\"" + targetOne + "\",\"BuildConfig\":\"Debug\"" +
                                        ",\"ProfilePath\":\"" + jsonProfilesOne.get("profiles_path").toString() +
                                        "\",\"ProjectPath\":\"" + path + "\"}";

                            }

                            if ((jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("enterprise")
                                    || jsonProfilesOne.get("profiles_build_type").toString().toLowerCase().equals("app-store")) && !jsonTarget.get("build_type").equals("")) {
                                setReleaseProfileStr = "{\"Target\":\"" + targetOne + "\",\"BuildConfig\":\"Release\"" +
                                        ",\"ProfilePath\":\"" + jsonProfilesOne.get("profiles_path").toString() +
                                        "\",\"ProjectPath\":\"" + path + "\"}";
                            }

                        }

                        if (productType.toLowerCase().equals("wmatrix")) {
                            commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
                            commandLineiOSProvisioningReleaseSetting = CommandLine.parse("wmatrixmanager");
                        } else if (productType.toLowerCase().equals("whybrid")) {
                            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
                        } else {
                            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
                        }

//                            commandLineiOSProvisioningSetting.addArgument("setprofile");
//                            commandLineiOSProvisioningSetting.addArgument("-j");
//                            commandLineiOSProvisioningSetting.addArgument(setProfileStr, false);

                        commandLineiOSProvisioningReleaseSetting.addArgument("setprofile");
                        commandLineiOSProvisioningReleaseSetting.addArgument("-j");
                        commandLineiOSProvisioningReleaseSetting.addArgument(setReleaseProfileStr, false);
//                            log.info(setProfileStr);
//                            log.info(setReleaseProfileStr);


                        // cli exec 처리
                        if (!setReleaseProfileStr.equals("")) {
                            executeCommonsSigningCreate(commandLineiOSProvisioningReleaseSetting, path, "iOS", null);

                        } else {

                        }

                        // setProfileStr = "";
                        setReleaseProfileStr = "";

                    }
                }

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void createiOSSigningKeyCertFileSetting(String path, Map<String, Object> iosSigningResult, String productType){

        String setCertFileStr = "{\"Path\"" + ":\""+iosSigningResult.get("signingkey_path").toString() +"\"" + ",\"Password\": "+ "\""+iosSigningResult.get("password").toString()+"\"" + "}";
        CommandLine commandLineiOSProvisioningSetting = null;

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }else {
            commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
        }

        commandLineiOSProvisioningSetting.addArgument("setcertificate");
        commandLineiOSProvisioningSetting.addArgument("-i");
        commandLineiOSProvisioningSetting.addArgument(setCertFileStr,false);

        // cli exec 처리
        try {
            executeCommonsSigningCreate(commandLineiOSProvisioningSetting, path, "iOS", null);
        } catch (Exception e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

    }

    private void createiOSSigningKeyMultiCertFileSetting(String path, String iosSigningResult, String productType){

        // array list json data type 변경 처리 해야함..

        String iosSigningkeyReusultTemp = iosSigningResult;

        iosSigningkeyReusultTemp = iosSigningkeyReusultTemp.replaceAll(" ","");
        iosSigningkeyReusultTemp = iosSigningkeyReusultTemp.replace("[{","");
        iosSigningkeyReusultTemp = iosSigningkeyReusultTemp.replace("}]","");

        String[] iosSigningkeyResultArrayJson = iosSigningkeyReusultTemp.split("\\},\\{");


        for(int i = 0; i < iosSigningkeyResultArrayJson.length; i++){
            JSONObject jsonSigningkeyOne = null;
            log.info(iosSigningkeyResultArrayJson[i]);
            iosSigningkeyResultArrayJson[i] = "{"+iosSigningkeyResultArrayJson[i] +"}";

            try {
                jsonSigningkeyOne = (JSONObject) parser.parse(iosSigningkeyResultArrayJson[i]);
                String setCertFileStr = "{\"Path\"" + ":\""+jsonSigningkeyOne.get("certificate_path").toString() +"\"" + ",\"Password\": "+ "\""+jsonSigningkeyOne.get("certificate_password").toString()+"\"" + "}";

                CommandLine commandLineiOSProvisioningSetting = null;

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLineiOSProvisioningSetting = CommandLine.parse("whmanager");
                }else {
                    commandLineiOSProvisioningSetting = CommandLine.parse("wmatrixmanager");
                }

                commandLineiOSProvisioningSetting.addArgument("setcertificate");
                commandLineiOSProvisioningSetting.addArgument("-j");
                commandLineiOSProvisioningSetting.addArgument(setCertFileStr,false);

                // cli exec 처리
                try {
                    executeCommonsSigningCreate(commandLineiOSProvisioningSetting, path, "iOS", null);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // create key store file / path : android vcs clone 연동 시 필요
    private void createKeystorePropertiesFileBak(String path, Map<String, Object> keytoolResult) throws IOException {
        // 경로를 android project에 설정하기
        String buildAfterLogFile = path+"/keystore.properties";

        File f = new File(buildAfterLogFile);
        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);

        out.append("storePassword = "+keytoolResult.get("all_keyfile_password").toString()+"\n");
        out.append("keyPassword = "+keytoolResult.get("key_password").toString()+"\n");
        out.append("keyAlias = "+keytoolResult.get("key_alias").toString()+"\n");
        out.append("storeFile = "+"" + keytoolResult.get("keystore_file_name").toString() +"\n");

        out.flush();
        out.close();
    }

    public void generaliOSAppSetProfile(String path, String profileStr) throws ParseException {
        JSONArray profiles = (JSONArray) parser.parse(profileStr);

        for (int i = 0; i < profiles.size(); i++) {
            JSONObject profile = (JSONObject) profiles.get(i);
            JSONObject setProfile = new JSONObject();

            // wmatrixmanager 1.3.7.2_fix2 버전 이후부터 BuildConfig값을 wmatrixmanager에서 설정한다.
            // 하위호환성을 위해서 공백만 넣어준다.
//            setProfile.put("BuildConfig", "Debug");
            setProfile.put("BuildConfig", "");
            setProfile.put("ProfilePath", profile.get("profiles_path"));
            setProfile.put("ProjectPath", path);
            setProfile.put("Target", profile.get("profiles_key_name"));

            generaliOSAppSetProfileExecute(path, setProfile);
        }
    }

    /**
     *
     * @param path Full URI
     * @param jsonSigningKey key setting info
     */
    public void generalAndroidAppSigning(String path, String jsonSigningKey) {
        String buildGradlePath = getBuildGradleFolder(path);
        String buildGradleLang = "groovy";

        try {
            if (buildGradlePath.substring(buildGradlePath.length() - 3, buildGradlePath.length()).equals("kts")) {
                buildGradleLang = "kotlin";
            }

            File buildGradleFile = new File(path + buildGradlePath);
            String[] getWhiveGradlePath = buildGradlePath.split("/");
            getWhiveGradlePath = Arrays.copyOf(getWhiveGradlePath, getWhiveGradlePath.length - 1);

            String whiveGradlePath = path + String.join("/", getWhiveGradlePath);

            /**
             *  build.gradle.kts 파일 아래의 apply(from="whive-build.gradle.kts") 와 같이 하면 unresolved 에러가 발생한다.
             *  따라서, 당장은 whive-build.gradle.kts / whive-build.gralde 파일을 만들지 않고,
             *  build.gralde / build.gradle.kts 파일 아래에 내용을 추가한다.
             *  추후, 에러의 원인이 알고 해결하게 되어서 whive-build 파일을 만들려면 아래 주석을 모두 풀면된다.
             *  2023.11.22
             */
//            if (buildGradleLang.equals("groovy")) {
//                Files.write(Paths.get(buildGradleFile.getAbsolutePath()), (System.lineSeparator() + System.lineSeparator() + "apply from: \"whive-build.gradle\"").getBytes(), StandardOpenOption.APPEND);
//            } else {
//                Files.write(Paths.get(buildGradleFile.getAbsolutePath()), (System.lineSeparator() + System.lineSeparator() + "apply(from = \"whive-build.gradle.kts\")").getBytes(), StandardOpenOption.APPEND);
//            }

            FileReader fr = new FileReader(buildGradleFile);
            BufferedReader br = new BufferedReader(fr);

            JSONObject siginingKey = (JSONObject) parser.parse(jsonSigningKey);

            changeSigningInBuildGradle(br, siginingKey, buildGradleLang, buildGradleFile, whiveGradlePath);

            br.close();
        } catch (Exception e) {
            log.error("General App Signing Error = {}", e.getMessage(), e);
        }
    }

    private void changeSigningInBuildGradle(BufferedReader br, JSONObject siginingKey, String buildGradleLang, File buildGradleFile, String whiveGradlePath) throws IOException, ParseException {

        /**
         *  build.gradle.kts 파일 아래의 apply(from="whive-build.gradle.kts") 와 같이 하면 unresolved 에러가 발생한다.
         *  따라서, 당장은 whive-build.gradle.kts / whive-build.gralde 파일을 만들지 않고,
         *  build.gralde / build.gradle.kts 파일 아래에 내용을 추가한다.
         *  추후, 에러의 원인이 알고 해결하게 되어서 whive-build 파일을 만들려면 아래 주석을 모두 풀면된다.
         *  2023.11.22
         */

        StringBuffer buildGradleString = new StringBuffer();
        StringBuffer tempGradleString = new StringBuffer();
        ArrayList<String> buildTypesString = new ArrayList<>();

        String str;
        String signingName = "";
        int signingConfigBraceletCount = 0;
        int buildTypesBraceletCount = 0;

        while(true) {
            str = br.readLine();
            if (str.replace(" ", "").equals("android{")) {
                break;
            } else {
                buildGradleString.append(str).append(System.lineSeparator());
            }
        }

        while ((str = br.readLine()) != null) {
            if (str.replace(" ", "").equals("signingConfigs{")) {
                signingConfigBraceletCount++;
                while (signingConfigBraceletCount > 0) {
                    str = br.readLine();
                    if (str.contains("{")) {
                        signingName = str.replace(" ", "").replace("{", "").replace("getByName(", "").replace(")", "");
                        signingConfigBraceletCount++;
                    } else if (str.contains("}")) {
                        signingConfigBraceletCount--;
                    }
                }
                } else if (str.replace(" ", "").equals("buildTypes{")) {
                buildTypesBraceletCount++;
                buildTypesString.add(str);
                buildTypesString.add(System.lineSeparator());
                while (buildTypesBraceletCount > 0) {
                    str = br.readLine();
                    if (str.contains("{")) {
                        buildTypesBraceletCount++;
                        buildTypesString.add(str);
                        buildTypesString.add(System.lineSeparator());
                    } else if (str.contains("}")) {
                        buildTypesBraceletCount--;
                        buildTypesString.add(str);
                        buildTypesString.add(System.lineSeparator());
                    } else {
                        int size = buildTypesString.size();
                        if (buildTypesBraceletCount == 2 && buildTypesString.get(size-1).equals(System.lineSeparator()) && buildTypesString.get(size-2).contains("{")) {
                            switch (buildGradleLang) {
                                case "groovy":
                                    buildTypesString.add("            signingConfig signingConfigs." + (signingName.equals("") ? "release" : signingName));
                                    break;
                                case "kotlin":
                                    buildTypesString.add("            signingConfig = signingConfigs.getByName(\"" + (signingName.equals("") ? "release" : signingName) + "\")");
                                    break;
                            }
                            buildTypesString.add(System.lineSeparator());
                        }
                        buildTypesString.add(str);
                        buildTypesString.add(System.lineSeparator());
                    }
                }
            } else {
                tempGradleString.append(str).append(System.lineSeparator());
            }
        }

        StringBuffer buildGradleSigningWithAndroid = new StringBuffer();
        buildGradleSigningWithAndroid.append("android {").append(System.lineSeparator());

        buildGradleString.append(buildGradleSigningWithAndroid);
        buildGradleString.append(tempGradleString);

        FileWriter fw = new FileWriter(buildGradleFile);
        fw.write(buildGradleString.toString());
//        fw.flush();
//        fw.close();

//        File whiveBuildGradle = buildGradleLang.equals("groovy") ? new File(whiveGradlePath + "/whive-build.gradle") : new File(whiveGradlePath + "/whive-build.gradle.kts");

//        fw = new FileWriter(whiveBuildGradle);
        fw.write("android {");
        fw.write(System.lineSeparator());
        fw.write(selectSigningString(buildGradleLang, siginingKey).toString());
        fw.write(String.join("", buildTypesString));
        fw.write("}");
        fw.flush();
        fw.close();
    }

    private StringBuffer selectSigningString(String buildGradleLang, JSONObject siginingKey) {
        switch (buildGradleLang) {
            case "groovy":
                StringBuffer signingStringForGroovy = new StringBuffer();
                signingStringForGroovy.append("    signingConfigs {").append(System.lineSeparator());
                signingStringForGroovy.append("        release {").append(System.lineSeparator());
                signingStringForGroovy.append("            keyAlias ").append("\"").append(siginingKey.get("key_alias").toString()).append("\"").append(System.lineSeparator());
                signingStringForGroovy.append("            keyPassword ").append("\"").append(siginingKey.get("key_password").toString()).append("\"").append(System.lineSeparator());
                signingStringForGroovy.append("            storeFile ").append("file(\"").append(siginingKey.get("signingkey_path").toString()).append("\")").append(System.lineSeparator());
                signingStringForGroovy.append("            storePassword ").append("\"").append(siginingKey.get("android_key_store_password").toString()).append("\"").append(System.lineSeparator());
                signingStringForGroovy.append("        }").append(System.lineSeparator()).append("    }");
                signingStringForGroovy.append(System.lineSeparator());

                return signingStringForGroovy;
            case "kotlin":
                StringBuffer signingStringForKotlin = new StringBuffer();
                signingStringForKotlin.append("    signingConfigs {").append(System.lineSeparator());
                signingStringForKotlin.append("        create(\"release\") {").append(System.lineSeparator());
                signingStringForKotlin.append("            keyAlias = ").append("\"").append(siginingKey.get("key_alias").toString()).append("\"").append(System.lineSeparator());
                signingStringForKotlin.append("            keyPassword = ").append("\"").append(siginingKey.get("key_password").toString()).append("\"").append(System.lineSeparator());
                signingStringForKotlin.append("            storeFile = ").append("file(\"").append(siginingKey.get("signingkey_path").toString()).append("\")").append(System.lineSeparator());
                signingStringForKotlin.append("            storePassword = ").append("\"").append(siginingKey.get("android_key_store_password").toString()).append("\"").append(System.lineSeparator());
                signingStringForKotlin.append("        }").append(System.lineSeparator()).append("    }");
                signingStringForKotlin.append(System.lineSeparator());
                return signingStringForKotlin;
        }
        return new StringBuffer();
    }

    private String getBuildGradleFolder(String path) {
        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";
        ArrayList<String> gradleCandidate = new ArrayList<>();

        String cmd = "cd " + path + " && grep -r *.gradle* | grep -ir 'com.android.application' | grep -v version";
        CommandLine commandLine = CommandLine.parse("/bin/sh");
        commandLine.addArgument("-c");
        commandLine.addArgument(cmd, false);

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            while ((tmp = is.readLine()) != null) {
                gradleCandidate.add(tmp);
            }

            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);
            handler.stop();

            if (gradleCandidate.size() > 1) {
                for (int i = 0; i < gradleCandidate.size(); i++) {
                    String candidateFilePath = gradleCandidate.get(i).split(":")[0].substring(1);
                    File candidateFile = new File(path + candidateFilePath);
                    FileReader fr = new FileReader(candidateFile);
                    BufferedReader br = new BufferedReader(fr);

                    String line = "";
                    while((line = br.readLine()) != null) {
                        if (line.toLowerCase().replaceAll(" ", "").equals("android{")) {
                            return candidateFilePath;
                        }
                    }
                }
            } else {
                return gradleCandidate.get(0).split(":")[0].substring(1);
            }

        } catch (Exception e) {
            log.error("Error");
        }
        return "";
    }


    private void generaliOSAppSetProfileExecute(String path, JSONObject profile) {
        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";
        ArrayList<String> gradleCandidate = new ArrayList<>();


        String profileSTR = "\"" + profile.toJSONString().replace("\"", "\\\"") + "\"";

        String cmd = "cd " + path + " && wmatrixmanager setprofile -j " + profileSTR + " --c";
        log.info(cmd);
        CommandLine commandLine = CommandLine.parse("/bin/sh");
        commandLine.addArgument("-c");
        commandLine.addArgument(cmd, false);

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            log.info("exitCode : {}", exitCode);
            handler.stop();

        } catch (Exception e) {
            log.error("Error");
        }
    }
}
