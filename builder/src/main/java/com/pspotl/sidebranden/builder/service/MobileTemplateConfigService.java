package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.pspotl.sidebranden.builder.domain.*;
import com.pspotl.sidebranden.builder.enums.*;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MobileTemplateConfigService extends BaseService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(PluginRemoveService.class);
    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private Map<String, String> taskList;
    private ReentrantReadWriteLock lock;

    @Value("${whive.distribution.deployLogPath}")
    private String logPath;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${whive.distribution.UserBranchResource}")
    private String UserBranchResourcePath;

    @Value("${whive.distribution.PluginPathiOS}")
    private String iOSPluginPath;

    @Value("${whive.distribution.WMatrixPluginPathiOS}")
    private String iOSWMatrxiPluginPath;

    @Value("${whive.distribution.WMatrixPluginPathAndroid}")
    private String WMatrixPluginPathAndroid;

    @Value("${whive.distribution.wmatrixmanager}")
    private String wmatrixmanager;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    WebSocketSession _session;
    private String hqKey = "";
    private String profileType = "";

    private ObjectMapper Mapper = new ObjectMapper();
    private String systemUserHomePath = System.getProperty("user.home");
    private JSONParser parser = new JSONParser();

    private Gson gson = new Gson();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

    // application config cli 처리
    // @Async("asyncThreadPool")
    public void templateConfigCLI(String platform, String userProjectPath, String appVersionCode, Map<String, Object> parseResultTemp){

        try {
            // log.info("Current waiting Task : " + waitingQueue.size());
            // lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();
            String projectDirName = "";
            String productType = parseResultTemp.get("product_type").toString();
            JSONObject configJson;
            profileType = parseResultTemp.get("profile_type").toString();

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                executueApplyConfigCLI(uuid, platform, appVersionCode, userProjectPath, productType);
            }else {
                projectDirName = BuilderDirectoryType.PROJECT_P + parseResultTemp.get(PayloadMsgType.projectID.name()).toString();
                configJson = (JSONObject) parser.parse(parseResultTemp.get("appConfigJSON").toString());

                executeiOSBuildApplyConfigAllCLI(uuid, platform, userProjectPath, projectDirName, parseResultTemp, null);
            }

            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            // lock.readLock().unlock();
        }

    }

    public void templateConfigCLI(String platform, String appVersion){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            executueApplyConfigCLI(uuid, platform, appVersion);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    // @Async("asyncThreadPool")
    public void templateConfigAllCLI(WebSocketSession session, String platform, String userProjectPath, String projectDirName, JSONObject configJson, String serverConfigs, ServerConfigMode serverMode, String productType, String hqKey){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            executeApplyConfigAllCLI(session, uuid, platform, userProjectPath, projectDirName, configJson, serverConfigs, serverMode, productType, hqKey);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    public void templateMultiProfileConfigUpdateAllCLI(WebSocketSession session, Map<String, Object> parseResult){

        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            // 수정 반영 해야할 메소드가 있음...
            executeMultiProfileConfigUpdateAllCLI(session, parseResult);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }

    }

    public void importiOSMultiProfileConfigSettingALLCLI(WebSocketSession session, JSONObject parseResult, String fullPath){

        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            // 수정 반영 해야할 메소드가 있음...
            executeImportiOSAfterMultiConfigAllCLI(session, parseResult, fullPath);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }


    }

    public void templateMultiProfileAutoKeySetUpdateAllCLI(WebSocketSession session, Map<String, Object> parseResult, JsonObject jsonGetConfigKey){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            // 수정 반영 해야할 메소드가 있음...
            executeMultiProfileAutoKeyConfigUpdateAllCLI(session, parseResult, jsonGetConfigKey);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }

    }

    public void templateMultiProfileAutoKeySetCreateAllCLI(WebSocketSession session, Map<String, Object> parseResult, JsonObject jsonGetConfigKey, ServerConfigMode serverMode){

        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            // 수정 반영 해야할 메소드가 있음...
            executeMultiProfileAutokeyConfigCreateAllCLI(session, parseResult, jsonGetConfigKey, serverMode);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }

    }

    public void templateMultiProfileConfigAllCLI(WebSocketSession session, String platform, String userProjectPath, String projectDirName, JSONObject configJson, String serverConfigs, String appprofileConfigs, ServerConfigMode serverMode, String productType){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            executeApplyMultiProfileConfigAllCLI(session, uuid, platform, userProjectPath, projectDirName, configJson, serverConfigs, appprofileConfigs, serverMode, productType);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }

    }

    @Async("asyncThreadPool")
    public void getTemplateConfigAllCLI(WebSocketSession session, Map<String, Object> parseResult, String platform, String userProjectPath, String projectDirName){
        try {
            ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            // websocket session, hqKey setting
            String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            serverConfigListMessageHandler(session, serverConfigListStatusMsg,"SEARCHING", null, "SEARCHING", hqKey);
            executeGetServerConfigCLI(session, platform, parseResult, userProjectPath, projectDirName, hqKey);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    @Async("asyncThreadPool")
    public void getMultiConfigAllCLI(WebSocketSession session, Map<String, Object> parseResult){

        executeGetMultiProfileAppConfigCLI(session, parseResult);

    }

    @Async("asyncThreadPool")
    public CompletableFuture<JSONObject> getMultiDomainTemplateConfig(WebSocketSession session, Map<String, Object> parseResult) {
        return CompletableFuture.completedFuture(executeGetMultiDomainAppConfig(session, parseResult));
    }

    public void setMultiConfigBuildAfterChangeAppVersionCode(WebSocketSession session, Map<String, Object> parseResult, String fullPath){

        // 추가 메소드 소스코드 작성 해야함.
        try {
            executeBuildAfterMultiConfigAllCLI(session, parseResult, fullPath);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
            // return send websocket message
        }finally {
            log.info(" end apply config cli ");

        }

    }


    public JSONObject getBuildConfigAllCLI(WebSocketSession session, Map<String, Object> parseResult, String platform, String userProjectPath){
        try {
            JSONObject resultAppConfigObj = null;

            ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            // websocket session, hqKey setting
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            profileType = parseResult.get(PayloadMsgType.profile_type.name()).toString();
            _session = session;

            serverConfigListMessageHandler(session, serverConfigListStatusMsg,"SEARCHING", null, "SEARCHING", hqKey);
            resultAppConfigObj = executeGetBuildCodeAppConfigCLI(platform, parseResult, userProjectPath);

            log.info(" Current apply config cli ");
            return resultAppConfigObj;
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
            return null;
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    public JSONObject getProjectCreateAfterConfigAllCLI(Map<String, Object> parseResult, String platform, String userProjectPath){

        try {
            JSONObject resultAppConfigObj = null;

            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            // websocket session, hqKey setting
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            resultAppConfigObj = executeGetBuildCodeAppConfigCLI(platform, parseResult, userProjectPath);

            log.info(" Current apply config cli ");
            return resultAppConfigObj;
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
            return null;
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    public void setiOSServerConfigAndProjectName(WebSocketSession session, String platform, String userProjectPath, String projectDirName, JSONObject configJson, String serverConfigs, String productType, String hqKey){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            _session = session;

            executeiOSServerConfigAndProjectName(session, uuid, platform, userProjectPath, projectDirName, configJson, serverConfigs, productType, hqKey);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }


    // 현재 사용하지 않는 메소드
    public void getiOSAppConfigAndProjectName(WebSocketSession session, Map<String, Object> parseResult, String platform, String userProjectPath, String projectDirName){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            _session = session;
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            executeGetAppConfigCLI(platform, parseResult, userProjectPath, projectDirName);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    @Async("asyncThreadPool")
    public void getiOSGetInformationList(WebSocketSession session, Map<String, Object> parseResult, String platform, String userProjectPath, String projectDirName){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            _session = session;
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            executeGetInformationCLI(session, platform, parseResult, userProjectPath, projectDirName);
            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }
    }

    @Async("asyncThreadPool")
    public void getAndroidGetConfigList(WebSocketSession session, Map<String, Object> parseResult, String platform, String userProjectPath, String projectDirName){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            _session = session;
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            executeGetInformationCLI(session, platform, parseResult, userProjectPath, projectDirName);

            log.info(" Current apply config cli ");
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }

    }

    public String getiOSProjectPath(WebSocketSession session, Map<String, Object> parseResult, String platform, String userProjectPath, String projectDirName){

        String projectPath = "";

        try {

            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            _session = session;
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            log.info(" Current apply config cli ");

            projectPath = executeGetProjectPath(session, platform, parseResult, userProjectPath, projectDirName);

            return projectPath;

        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
            return projectPath;
        } finally {
            log.info(" end apply config cli ");
            lock.readLock().unlock();
        }

    }


    private void executeApplyConfigAllCLI(WebSocketSession session, String buildTaskId, String platform, String userProjectPath, String projectDirName,JSONObject configJson, String serverConfigs, ServerConfigMode serverConfigMode, String productType, String hqKey) throws Exception {

        ArrayList<JSONObject> applyServerArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            String AppName = configJson.get(PayloadMsgType.AppName.name()).toString();
            AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..
            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
            String serverConfigStr = serverConfigs;

            shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

            if(serverConfigStr.equals("")){

            }else {

                String serverConfigList = serverConfigStr;
//                serverConfigList = serverConfigList.replaceAll(" ","");
                serverConfigList = serverConfigList.replace("[{","");
                serverConfigList = serverConfigList.replace("}]","");

                String[] items = serverConfigList.split("\\},\\{");

                for(int i = 0; i < items.length; i++) {

                    items[i] = "{" + items[i] + "}";
                    items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                    items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                    items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                    items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                    // available_plugin_module


                    // configjson 하나로 여러개의 처리로 전환하기
                    config1JSON = (JSONObject) parser.parse(items[i].toString());

                    applyServerArrayJson.add(config1JSON); // 동적 처리 적용

                }

            }

            String applyConfigString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString() + "\"" +
                    ",\"AppName\":\""+ AppName + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"MinSDKVersion\":\""+ configJson.get("MinTargetVersion").toString() +"\"";

            String applyConfigServerJsonArr = "";

            if(serverConfigStr.equals("")){
                applyConfigString = applyConfigString + ", \"PackageName\":\""+configJson.get("PackageName").toString() +"\"}"; // app config + server config string 합치기
            }else {
                for (int config = 0 ; config < applyServerArrayJson.size() ; config++){

                    JSONObject jsonServerJsonSetting = applyServerArrayJson.get(config);
                    log.info(jsonServerJsonSetting.toJSONString());

                    if(config == 0){
                        applyConfigServerJsonArr += ", \"Server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                    }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]" +
//                            ", \"PackageName\":\""+configJson.get("PackageName").toString() +"\"}";
//                }
                    else {
                        applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                    }

                }
                applyConfigString = applyConfigString + applyConfigServerJsonArr + "]" + ", \"PackageName\":\""+configJson.get("PackageName").toString() +"\"}"; // app config + server config string 합치기
                //
            }

            log.info(applyConfigString);


            CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
            if(projectDirName.equals("")){
                commandLineShell.addArgument(userProjectPath+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }else {
                commandLineShell.addArgument(userProjectPath+"/"+projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }
            // commandLineShell.addArgument(userProjectPath+"/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

            // product_type
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell.addArgument(":wmatrix-plugins:setConfig");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            commandLineShell.addArgument("-P");
            // ApplicationID, AppName, AppVersion, AppVersionCode
            commandLineShell.addArgument("data=");
            //commandLineShell.addArgument("data="+"{ApplicationID:"+configJson.get("ApplicationID").toString()+
            commandLineShell.addArgument(applyConfigString, false);

            executeCommonsExecApplyConfig(session, commandLineShell, platform, serverConfigMode, hqKey);

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            String serverConfigStr = serverConfigs;
            String language = configJson.get("language").toString();
            String applyConfigDebugString = "";
            String applyConfigReleaseString = "";

            String applyConfigServerJsonArr = "";

            if(serverConfigStr.equals("")){

            }else {

                String serverConfigList = serverConfigStr;
//                serverConfigList = serverConfigList.replaceAll(" ","");
                serverConfigList = serverConfigList.replace("[{","");
                serverConfigList = serverConfigList.replace("}]","");

                String[] items = serverConfigList.split("\\},\\{");

                for(int i = 0; i < items.length; i++) {

                    items[i] = "{" + items[i] + "}";
                    items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                    items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                    items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                    items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                    // available_plugin_module
                    log.info(items[i]);

                    // configjson 하나로 여러개의 처리로 전환하기
                    config1JSON = (JSONObject) parser.parse(items[i].toString());

                    applyServerArrayJson.add(config1JSON); // 동적 처리 적용


                }

            }

            // 건들지 말기
            if(language.toLowerCase().equals("swift")){

                if(productType.toLowerCase().equals("wmatrix")){
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            //",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                            //",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/" + configJson.get("getProjectNamePath").toString() + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            //",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                            //",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";


                }else {
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/WHybridTemplate.xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                }



            }else if(language.toLowerCase().equals("objc")){
                if(productType.toLowerCase().equals("wmatrix")){
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            //",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
//                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";


                }else {
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/WHybridTemplate.xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                }

            }

            String applySetServerConfig = "";

            if(productType.toLowerCase().equals("wmatrix")){

                applySetServerConfig = "{";

                for (int config = 0 ; config < applyServerArrayJson.size() ; config++){

                    JSONObject jsonServerJsonSetting = applyServerArrayJson.get(config);

                    if(config == 0){
                        applyConfigServerJsonArr += "\"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                    }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]}";
//                }
                    else {
                        applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                    }

                }

                applySetServerConfig += applyConfigServerJsonArr + "]}";

            }else {
                applySetServerConfig = "{" + "\"LocalWebServerPort\":\"24680\"";

                for (int config = 0 ; config < applyServerArrayJson.size() ; config++){

                    JSONObject jsonServerJsonSetting = applyServerArrayJson.get(config);

                    if(config == 0){
                        applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                    }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]}";
//                }
                    else {
                        applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                    }

                }

                applySetServerConfig += applyConfigServerJsonArr + "]}";
            }




            log.info(applySetServerConfig);

            // cmd cli 초기 설정
            CommandLine commandLineShell = null;
            CommandLine commandLineReleasePlugman = null;
            CommandLine commandLineSetServerConfig = null;

            // CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
            if(productType.toLowerCase().equals("wmatrix")) {
                commandLineShell = CommandLine.parse("wmatrixmanager");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell = CommandLine.parse("whmanager");
            }else {
                commandLineShell = CommandLine.parse("whmanager");
            }


            commandLineShell.addArgument("setconfig");
            commandLineShell.addArgument("-i");
            //commandLineShell.addArgument(shellscriptFileName); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
            //commandLineShell.addArgument("setconfig");
            // commandLineShell.addArgument(userProjectPath + projectDirName+"/Config/Debug.xcconfig"); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
            commandLineShell.addArgument(applyConfigDebugString, false);


            if(productType.toLowerCase().equals("wmatrix")) {
                commandLineReleasePlugman = CommandLine.parse("wmatrixmanager");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineReleasePlugman = CommandLine.parse("whmanager");
            }else {
                commandLineReleasePlugman = CommandLine.parse("whmanager");
            }

            commandLineReleasePlugman.addArgument("setconfig");
            commandLineReleasePlugman.addArgument("-i");
            commandLineReleasePlugman.addArgument(applyConfigReleaseString, false);

            // setServerConfig
            if(productType.toLowerCase().equals("wmatrix")) {
                commandLineSetServerConfig = CommandLine.parse("wmatrixmanager");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineSetServerConfig = CommandLine.parse("whmanager");
            }else {
                commandLineSetServerConfig = CommandLine.parse("whmanager");
            }

            commandLineSetServerConfig.addArgument("setserverconfig");
            commandLineSetServerConfig.addArgument("-p");


            if(projectDirName.equals("")){
                commandLineSetServerConfig.addArgument(userProjectPath+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }else {
                commandLineSetServerConfig.addArgument(userProjectPath+"/"+projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }
            commandLineSetServerConfig.addArgument("-i");
            commandLineSetServerConfig.addArgument(applySetServerConfig, false);


            // executeCommonsExecApplyConfig(session, commandLineShell, platform, serverConfigMode); // debug
            executeCommonsExecApplyConfig(session, commandLineReleasePlugman, platform, serverConfigMode, hqKey); // relase
            executeCommonsExecApplyConfig(session, commandLineSetServerConfig, platform, serverConfigMode, hqKey); // server config
        }

    }

    private void executeBuildAfterMultiConfigAllCLI(WebSocketSession session, Map<String, Object> parseResult, String fullPath) throws Exception {

        String productType = parseResult.get("product_type").toString();
        String platform = parseResult.get("platform").toString();
        JSONObject config = (JSONObject) parser.parse(parseResult.get("multiProfileConfig").toString());

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            JSONObject wmatrix = (JSONObject) config.get("wmatrix");
            JSONObject oldProfiles = (JSONObject) wmatrix.get("profiles");

            String actvie = wmatrix.get("active").toString();
            String packageName = "";

            JSONObject newProfiles = new JSONObject();
            for (String s : (Iterable<String>) oldProfiles.keySet()) {
                JSONObject profile = new JSONObject();

                profile = (JSONObject) oldProfiles.get(s);
                profile.replace("appVersionCode", parseResult.get("appVersionCode").toString());
                profile.replace("minSdkVersion", profile.get("minSdkVersion").toString());
                newProfiles.put(s, profile);

                /*
                    기존에는 안드로이드 템플릿 프로젝트의 프로파일이 고정되어 있었다.
                    그 중, product 프로파일의 applicationId로 셋팅을 하고 있었는데,
                    템플릿이 계속 변경되면서, 프로파일도 바뀌는 경우가 있어서,
                    현재 active 되어 있는 프로파일의 applicationId를 사용하게끔 변경.
                 */
                if (s.toLowerCase().equals(actvie)) {
                    packageName = profile.get("applicationId").toString();
                }
            }

            wmatrix.clear();
            wmatrix.put("active", actvie);
            wmatrix.put("profiles", newProfiles);

            config.clear();
            config.put("wmatrix", wmatrix);
            config.put("packageName", packageName);

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
            CommandLine commandLineShell = CommandLine.parse(shellscriptFileName);
            commandLineShell.addArgument(fullPath);

            if (productType.toLowerCase().equals("wmatrix")) {
                commandLineShell.addArgument("setConfig");
            } else if (productType.toLowerCase().equals("whybrid")) {
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            commandLineShell.addArgument("-P");
            commandLineShell.addArgument("data=");
            commandLineShell.addArgument(config.toString(), false);

            executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.NONE, hqKey);
        }

        if (platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
            JSONObject targets = (JSONObject) config.get("targets");

            String targetName = targets.keySet().iterator().next().toString();
            JSONObject target = (JSONObject) targets.get(targetName);

            JSONObject newTargets = new JSONObject();
            JSONObject newConfig = new JSONObject();

            for (String s : (Iterable<String>) target.keySet()) {
                JSONObject temp = (JSONObject) target.get(s);
                temp.replace("versionCode", parseResult.get("AppVersionCode").toString());

                newTargets.put(s, temp);
            }

            newConfig.put("projectPath", fullPath);
            newConfig.put("targets", newTargets);

            CommandLine commandLineShell = CommandLine.parse("wmatrixmanager");

            if (productType.toLowerCase().equals("wmatrix")) {
                commandLineShell.addArgument("setconfig");
            } else if (productType.toLowerCase().equals("whybrid")) {
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            String newConfigString = newConfig.toJSONString();
            commandLineShell.addArgument("-j");
            commandLineShell.addArgument(newConfigString, false);

            executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.NONE, hqKey);
        }
    }

    private void executeImportiOSAfterMultiConfigAllCLI(WebSocketSession session, JSONObject parseResult, String fullPath) throws Exception {

        ArrayList<JSONObject> applyServerArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();

        List<String> appConfigKeyList = new ArrayList<>(Arrays.asList("applicationId", "appName","appVersion", "appVersionCode","minSdkVersion","server"));
        List<String> configServerDetailKeyList = new ArrayList<>(Arrays.asList("server","name", "appId","url")); // TODO : create server config Key List

        String domainID = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userID = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();

        String platform = parseResult.get("platform").toString();
        String productType = parseResult.get("product_type").toString();
        JSONObject configJson = (JSONObject) parseResult.get("multiProfileConfig");
        JSONArray targetArrJson = (JSONArray) parseResult.get("multiProfileList");

        log.info(configJson.toJSONString());

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            JSONObject getProfiles = (JSONObject) configJson.get("targets");
            log.info(String.valueOf(getProfiles.keySet().toArray(new String[getProfiles.size()])));
            String[] getProfileKeyStrArray = (String[]) getProfiles.keySet().toArray(new String[getProfiles.size()]);

            String applyMultiAppConfigAndServerJsonArr = "";

            String applyConfigString = "";
            // appconfig for 문 처리해야함
            for(int appconfigcnt = 0; appconfigcnt < targetArrJson.size(); appconfigcnt++){
                JSONObject jsonTargetListOne = (JSONObject) targetArrJson.get(appconfigcnt);
                //log.info(getProfileKeyStrArray[appconfigcnt]);
                //log.info(jsonTargetListOne.toJSONString());
                JSONObject jsonAppConfigOneSetting = (JSONObject) getProfiles.get(getProfileKeyStrArray[appconfigcnt]);
                //log.info(jsonAppConfigOneSetting.toJSONString());
                JSONObject jsonReleaseConfig = (JSONObject) jsonAppConfigOneSetting.get("Release");
                String profileType = jsonTargetListOne.get("targetAlias").toString();

                if(appconfigcnt == 0){
                    ArrayList<JSONObject> applyServerArrayJson1 = new ArrayList<>();
                    if(profileType.equals(getProfileKeyStrArray[appconfigcnt])){
                        applyConfigString += "{\"projectPath\":\""+ fullPath +"\",\"projectName\": \""+ parseResult.get("packageName").toString()
                                +"\", \"targets\": {\""+profileType
                                +"\": {\"applicationID\":\""+ jsonReleaseConfig.get("applicationID").toString() + "\"" +
                                ",\"appName\":\""+ jsonReleaseConfig.get("appName").toString() + "\"" +
                                ",\"version\":\"" + jsonReleaseConfig.get("version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonReleaseConfig.get("versionCode").toString() + "\"" +
                                ",\"deploymentTarget\":\""+ jsonReleaseConfig.get("deploymentTarget").toString() +"\"";
                    }else {
                        applyConfigString += "{\"projectPath\":\""+ fullPath +"\",\"projectName\": \""+ parseResult.get("packageName").toString()
                                +"\", \"targets\": {\""+profileType
                                +"\": {\"applicationID\":\""+ jsonReleaseConfig.get("applicationID").toString() + "\"" +
                                ",\"appName\":\""+ jsonReleaseConfig.get("appName").toString() + "\"" +
                                ",\"version\":\"" + jsonReleaseConfig.get("version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonReleaseConfig.get("versionCode").toString() + "\"" +
                                ",\"deploymentTarget\":\""+ jsonReleaseConfig.get("deploymentTarget").toString() +"\"";
                    }

                    String MultiServerConfigStr = jsonReleaseConfig.get("servers").toString();

                    MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                    String[] items = MultiServerConfigStr.split("\\},\\{");

                    for(int i = 0; i < items.length; i++) {

                        items[i] = "{" + items[i] + "}";
                        // available_plugin_module

                        // configjson 하나로 여러개의 처리로 전환하기
                        config1JSON = (JSONObject) parser.parse(items[i].toString());

                        applyServerArrayJson1.add(config1JSON); // 동적 처리 적용

                    }

                    String applyConfigServerJsonArr = "";

                    for (int config = 0 ; config < applyServerArrayJson1.size() ; config++){

                        JSONObject jsonServerJsonSetting = applyServerArrayJson1.get(config);
                        String name = jsonServerJsonSetting.get("Name") == null ? "" : jsonServerJsonSetting.get("Name").toString();
                        String appId = jsonServerJsonSetting.get("AppID") == null ? "" : jsonServerJsonSetting.get("AppID").toString();
                        String url = jsonServerJsonSetting.get("ServerURL") == null ? "" : jsonServerJsonSetting.get("ServerURL").toString();

                        if(config == 0){
                            applyConfigServerJsonArr += ", \"servers\": [{\"Name\":\""+ name +"\",\"AppID\":\""+ appId + "\",\"ServerURL\":\"" + url + "\"}";
                        } else {
                            applyConfigServerJsonArr += ", {\"Name\":\"" + name + "\",\"AppID\":\""+ appId +"\",\"ServerURL\":\""+ url +"\"}";
                        }

                    }

                    applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기


                } else {
                    ArrayList<JSONObject> applyServerArrayJson2 = new ArrayList<>();
                    // AppName = jsonReleaseConfig.get("appName").toString();
                    // AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..
                    //log.info(" profile key str : appconfigcnt ~ ");
                    //log.info(getProfileKeyStrArray[appconfigcnt]);

                    if(profileType.equals(getProfileKeyStrArray[appconfigcnt])){
                        applyConfigString += "\"" + profileType
                                +"\": {\"applicationID\":\""+ jsonReleaseConfig.get("applicationID").toString() + "\"" +
                                ",\"appName\":\""+ jsonReleaseConfig.get("appName").toString() + "\"" +
                                ",\"version\":\"" + jsonReleaseConfig.get("version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonReleaseConfig.get("versionCode").toString() + "\"" +  // appVersionCode 수정 하는 구간
                                ",\"deploymentTarget\":\""+ jsonReleaseConfig.get("deploymentTarget").toString() +"\"";
                    }else {
                        applyConfigString += "\"" + profileType
                                +"\": {\"applicationID\":\""+ jsonReleaseConfig.get("applicationID").toString() + "\"" +
                                ",\"appName\":\""+ jsonReleaseConfig.get("appName").toString() + "\"" +
                                ",\"version\":\"" + jsonReleaseConfig.get("version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonReleaseConfig.get("versionCode").toString() + "\"" +
                                ",\"deploymentTarget\":\""+ jsonReleaseConfig.get("deploymentTarget").toString() +"\"";
                    }

                    String MultiServerConfigStr = jsonReleaseConfig.get("servers").toString();

                    MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                    String[] items = MultiServerConfigStr.split("\\},\\{");

                    for(int i = 0; i < items.length; i++) {

                        items[i] = "{" + items[i] + "}";

                        // configjson 하나로 여러개의 처리로 전환하기
                        config1JSON = (JSONObject) parser.parse(items[i].toString());

                        applyServerArrayJson2.add(config1JSON); // 동적 처리 적용

                    }

                    String applyConfigServerJsonArr = "";

                    for (int config = 0 ; config < applyServerArrayJson2.size() ; config++){

                        JSONObject jsonServerJsonSetting = applyServerArrayJson2.get(config);
                        String name = jsonServerJsonSetting.get("Name") == null ? "" : jsonServerJsonSetting.get("Name").toString();
                        String appId = jsonServerJsonSetting.get("AppID") == null ? "" : jsonServerJsonSetting.get("AppID").toString();
                        String url = jsonServerJsonSetting.get("ServerURL") == null ? "" : jsonServerJsonSetting.get("ServerURL").toString();

                        if(config == 0){
                            applyConfigServerJsonArr += ", \"servers\": [{\"Name\":\""+ name +"\",\"AppID\":\""+ appId + "\",\"ServerURL\":\"" + url + "\"}";
                        } else {
                            applyConfigServerJsonArr += ", {\"Name\":\"" + name + "\",\"AppID\":\""+ appId +"\",\"ServerURL\":\""+ url +"\"}";
                        }

                    }

                    if(getProfileKeyStrArray.length -1 == appconfigcnt){
                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]}"; // app config + server config string 합치기
                    }else {
                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기
                    }

                }

            }

             applyConfigString = applyConfigString + "}}"; // app config + server config string 합치기

            CommandLine commandLineShell = CommandLine.parse("wmatrixmanager");

            // product_type
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell.addArgument("setconfig");
            }

            commandLineShell.addArgument("-j");
            commandLineShell.addArgument(applyConfigString, false);


            log.info("wmatrixmanager setconfig -j " + applyConfigString + " action cli ");

            executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.NONE, hqKey);
        }

    }

    private void executeApplyMultiProfileConfigAllCLI(WebSocketSession session, String buildTaskId, String platform, String userProjectPath, String projectDirName,JSONObject configJson, String serverConfigs, String appprofileConfigs, ServerConfigMode serverConfigMode, String productType) throws Exception {


        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();
        JSONObject appConfigAddJSON = new JSONObject();

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
            String appprofileConfigStr = appprofileConfigs;

            shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

            String applyMultiAppConfigAndServerJsonArr = "";

            String applyConfigString = "";
            // appconfig for 문 처리해야함
            for(int appconfigcnt = 0; appconfigcnt < appconfigMultiArrayJson.size(); appconfigcnt++){

                JSONObject jsonAppConfigOneSetting = appconfigMultiArrayJson.get(appconfigcnt);
                log.info(jsonAppConfigOneSetting.toJSONString());

                if(appconfigcnt == 0){
                    ArrayList<JSONObject> applyServerArrayJson1 = new ArrayList<>();
                    String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                    AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..

                    applyConfigString += "{\"packageName\":\"" + jsonAppConfigOneSetting.get("package_name").toString() +"\", \"wmatrix\": {\"active\": \""+ jsonAppConfigOneSetting.get("profile_name").toString()
                            +"\", \"profiles\": {\""+jsonAppConfigOneSetting.get("profile_name").toString()
                            +"\": {\"applicationId\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                            ",\"appName\":\""+ AppName + "\"" +
                            ",\"appVersion\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                            ",\"appVersionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\"" +
                            ",\"minSdkVersion\":\""+ jsonAppConfigOneSetting.get("min_target_version").toString() +"\"";
                            // ",\"debuggable\":false";

                    String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();
                    log.info(MultiServerConfigStr);

//                    MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                    String[] items = MultiServerConfigStr.split("\\},\\{");

                    for(int i = 0; i < items.length; i++) {

                        items[i] = "{" + items[i] + "}";
                        //items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                        //items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                        //items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                        //items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                        // available_plugin_module

                        log.info(items[i]);
                        // configjson 하나로 여러개의 처리로 전환하기
                        config1JSON = (JSONObject) parser.parse(items[i].toString());

                        applyServerArrayJson1.add(config1JSON); // 동적 처리 적용

                    }

                    String applyConfigServerJsonArr = "";

                    for (int config = 0 ; config < applyServerArrayJson1.size() ; config++){

                        JSONObject jsonServerJsonSetting = applyServerArrayJson1.get(config);
                        log.info(jsonServerJsonSetting.toJSONString());

                        if(config == 0){
                            applyConfigServerJsonArr += ", \"server\": [{\"name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"url\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                        } else {
                            applyConfigServerJsonArr += ", {\"name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"url\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                        }

                    }

                    applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기

                } else {
                    ArrayList<JSONObject> applyServerArrayJson2 = new ArrayList<>();
                    String AppName = jsonAppConfigOneSetting.get("app_name").toString();
//                    AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..

                    applyConfigString += "\"" + jsonAppConfigOneSetting.get("profile_name").toString()
                            +"\": {\"applicationId\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                            ",\"appName\":\""+ AppName + "\"" +
                            ",\"appVersion\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                            ",\"appVersionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\"" +
                            ",\"minSdkVersion\":\""+ jsonAppConfigOneSetting.get("min_target_version").toString() +"\"";
                            // ",\"debuggable\":false";

                    String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();

//                    MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                    String[] items = MultiServerConfigStr.split("\\},\\{");

                    for(int i = 0; i < items.length; i++) {

                        items[i] = "{" + items[i] + "}";
                        //items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                        //items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                        //items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                        //items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                        // available_plugin_module


                        // configjson 하나로 여러개의 처리로 전환하기
                        config1JSON = (JSONObject) parser.parse(items[i].toString());

                        applyServerArrayJson2.add(config1JSON); // 동적 처리 적용

                    }

                    String applyConfigServerJsonArr = "";

                    for (int config = 0 ; config < applyServerArrayJson2.size() ; config++){

                        JSONObject jsonServerJsonSetting = applyServerArrayJson2.get(config);
                        log.info(jsonServerJsonSetting.toJSONString());

                        if(config == 0){
                            applyConfigServerJsonArr += ", \"server\": [{\"name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"url\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                        } else {
                            applyConfigServerJsonArr += ", {\"name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"url\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                        }

                    }

                    if(appconfigMultiArrayJson.size() -1 == appconfigcnt){
                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]}"; // app config + server config string 합치기
                    }else {
                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기
                    }

                }

            }

            applyConfigString = applyConfigString + "}}}"; // app config + server config string 합치기


            log.info("applyConfigString : {}",applyConfigString);


            CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
            if(projectDirName.equals("")){
                commandLineShell.addArgument(userProjectPath+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }else {
                commandLineShell.addArgument(userProjectPath+"/"+projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }
            // commandLineShell.addArgument(userProjectPath+"/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

            // product_type
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell.addArgument("setConfig");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            commandLineShell.addArgument("-P");
            // ApplicationID, AppName, AppVersion, AppVersionCode
            commandLineShell.addArgument("data=");
            //commandLineShell.addArgument("data="+"{ApplicationID:"+configJson.get("ApplicationID").toString()+
            commandLineShell.addArgument(applyConfigString, false);

            executeCommonsExecApplyConfig(session, commandLineShell, platform, serverConfigMode, hqKey);

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
            ArrayList<JSONObject> applyServerArrayJson = new ArrayList<>();
            String serverConfigStr = serverConfigs;
            String language = configJson.get("language").toString();
            String applyConfigDebugString = "";
            String applyConfigReleaseString = "";
            String appprofileConfigStr = appprofileConfigs;

            // 건들지 말기
            if(language.toLowerCase().equals("swift")){

                if(productType.toLowerCase().equals("wmatrix")){

                }else {
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/WHybridTemplate.xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                }



            }else if(language.toLowerCase().equals("objc")){
                if(productType.toLowerCase().equals("wmatrix")){
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            //",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
//                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";


                }else {
                    applyConfigDebugString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/WHybridTemplate.xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                    applyConfigReleaseString = "{" +
                            "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                            ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                            ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                            ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                            ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                            ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                            ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

                }

            }

            String applySetServerConfig = "";
            String applyMultiAppConfigAndServerJsonArr = "";
            String applyConfigString = "";

            if(productType.toLowerCase().equals("wmatrix")){

                for(int appconfigcnt = 0; appconfigcnt < appconfigMultiArrayJson.size(); appconfigcnt++) {

                    JSONObject jsonAppConfigOneSetting = appconfigMultiArrayJson.get(appconfigcnt);
                    log.info(jsonAppConfigOneSetting.toJSONString());

                    if(appconfigcnt == 0){
                        ArrayList<JSONObject> applyServerArrayJson1 = new ArrayList<>();
                        String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                        AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..
                        // getProjectNamePath configJson.get("getProjectNamePath").toString().replace(".xcodeproj","")
                        applyConfigString += "{\"projectPath\": \""+ userProjectPath+"/" +"\", \"projectName\":\"" + jsonAppConfigOneSetting.get("package_name").toString()
                                +"\", \"targets\": {\""+jsonAppConfigOneSetting.get("profile_name").toString()
                                +"\": {\"applicationID\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                                ",\"appName\":\""+ AppName + "\"" +
                                ",\"version\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\"" +
                                ",\"deploymentTarget\":\""+jsonAppConfigOneSetting.get("min_target_version").toString()+"\"";

                        String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();
                        log.info(MultiServerConfigStr);

//                        MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                        String[] items = MultiServerConfigStr.split("\\},\\{");

                        for(int i = 0; i < items.length; i++) {

                            items[i] = "{" + items[i] + "}";
                            //items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                            //items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                            //items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                            //items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                            // available_plugin_module

                            log.info(items[i]);
                            // configjson 하나로 여러개의 처리로 전환하기
                            config1JSON = (JSONObject) parser.parse(items[i].toString());

                            applyServerArrayJson1.add(config1JSON); // 동적 처리 적용

                        }

                        String applyConfigServerJsonArr = "";

                        for (int config = 0 ; config < applyServerArrayJson1.size() ; config++){

                            JSONObject jsonServerJsonSetting = applyServerArrayJson1.get(config);
                            log.info(jsonServerJsonSetting.toJSONString());

                            if(config == 0){
                                applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                            } else {
                                applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                            }

                        }

                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기


                    } else {
                        ArrayList<JSONObject> applyServerArrayJson2 = new ArrayList<>();
                        String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                        AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..

                        applyConfigString += "\"" + jsonAppConfigOneSetting.get("profile_name").toString()
                                +"\": {\"applicationID\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                                ",\"appName\":\""+ AppName + "\"" +
                                ",\"version\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\""+
                                ",\"deploymentTarget\":\""+jsonAppConfigOneSetting.get("min_target_version").toString()+"\"";

                        String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();

//                        MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                        String[] items = MultiServerConfigStr.split("\\},\\{");

                        for(int i = 0; i < items.length; i++) {

                            items[i] = "{" + items[i] + "}";

                            // configjson 하나로 여러개의 처리로 전환하기
                            config1JSON = (JSONObject) parser.parse(items[i].toString());

                            applyServerArrayJson2.add(config1JSON); // 동적 처리 적용

                        }

                        String applyConfigServerJsonArr = "";

                        for (int config = 0 ; config < applyServerArrayJson2.size() ; config++){

                            JSONObject jsonServerJsonSetting = applyServerArrayJson2.get(config);
                            log.info(jsonServerJsonSetting.toJSONString());

                            if(config == 0){
                                applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                            } else {
                                applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                            }

                        }

                        if(appconfigMultiArrayJson.size() -1 == appconfigcnt){
                            applyConfigString = applyConfigString + applyConfigServerJsonArr + "]}"; // app config + server config string 합치기
                        }else {
                            applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기
                        }

                    }

                }

                applyConfigString = applyConfigString + "}}"; // app config + server config string 합치기

                log.info("applyConfigString : {}",applyConfigString);

            }else {
                applySetServerConfig = "{" + "\"LocalWebServerPort\":\"24680\"";

                for (int config = 0 ; config < applyServerArrayJson.size() ; config++){

                    JSONObject jsonServerJsonSetting = applyServerArrayJson.get(config);

                    if(config == 0){
                        // applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                    }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]}";
//                }
                    else {
                        // applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                    }

                }

                // applySetServerConfig += applyConfigServerJsonArr + "]}";
            }




            log.info(applyConfigString);

            // cmd cli 초기 설정
            CommandLine commandLineShell = null;
            CommandLine commandLineReleasePlugman = null;
            CommandLine commandLineSetServerConfig = null;

            // CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
            if(productType.toLowerCase().equals("wmatrix")) {
                commandLineShell = CommandLine.parse(wmatrixmanager);
                commandLineShell.addArgument("setconfig");
                commandLineShell.addArgument("-j");
                //commandLineShell.addArgument(shellscriptFileName); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                //commandLineShell.addArgument("setconfig");
                // commandLineShell.addArgument(userProjectPath + projectDirName+"/Config/Debug.xcconfig"); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                commandLineShell.addArgument(applyConfigString, false);

            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell = CommandLine.parse("whmanager");
            }else {
                commandLineShell = CommandLine.parse("whmanager");
                commandLineShell.addArgument("setconfig");
                commandLineShell.addArgument("-i");
                //commandLineShell.addArgument(shellscriptFileName); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                //commandLineShell.addArgument("setconfig");
                // commandLineShell.addArgument(userProjectPath + projectDirName+"/Config/Debug.xcconfig"); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                commandLineShell.addArgument(applyConfigDebugString, false);

            }




            if(productType.toLowerCase().equals("wmatrix")) {

            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineReleasePlugman = CommandLine.parse("whmanager");
            }else {
                commandLineReleasePlugman = CommandLine.parse("whmanager");
                commandLineReleasePlugman.addArgument("setconfig");
                commandLineReleasePlugman.addArgument("-i");
                commandLineReleasePlugman.addArgument(applyConfigReleaseString, false);
            }


            // setServerConfig
            if(productType.toLowerCase().equals("wmatrix")) {
                commandLineSetServerConfig = CommandLine.parse(wmatrixmanager);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineSetServerConfig = CommandLine.parse("whmanager");
            }else {
                commandLineSetServerConfig = CommandLine.parse("whmanager");
                commandLineSetServerConfig.addArgument("setserverconfig");
                commandLineSetServerConfig.addArgument("-p");

                if(projectDirName.equals("")){
                    commandLineSetServerConfig.addArgument(userProjectPath+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                }else {
                    commandLineSetServerConfig.addArgument(userProjectPath+"/"+projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                }
                commandLineSetServerConfig.addArgument("-i");
                commandLineSetServerConfig.addArgument(applySetServerConfig, false);
            }



            if (productType.toLowerCase().equals("wmatrix")) {
                executeCommonsExecApplyConfig(session, commandLineShell, platform, serverConfigMode, hqKey); // debug

            }else {
                executeCommonsExecApplyConfig(session, commandLineShell, platform, serverConfigMode, hqKey); // debug
                executeCommonsExecApplyConfig(session, commandLineReleasePlugman, platform, serverConfigMode, hqKey); // relase
                executeCommonsExecApplyConfig(session, commandLineSetServerConfig, platform, serverConfigMode, hqKey); // server config

            }


        }

    }

    private void executeMultiProfileAutokeyConfigCreateAllCLI(WebSocketSession session, Map<String, Object> parseResult, JsonObject jsonGetConfigKey,  ServerConfigMode serverMode){

        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();
        JSONObject appConfigAddJSON = new JSONObject();

        JsonArray jsonProfileArr = new JsonArray();

        List<String> appConfigKeyList = new ArrayList<>(Arrays.asList("applicationId", "appName","appVersion", "appVersionCode","minSdkVersion","server"));
        List<String> configServerDetailKeyList = new ArrayList<>(Arrays.asList("server","name", "appId","url"));

        String platform = parseResult.get("platform").toString();
        String productType = parseResult.get("product_type").toString();
        String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString(); //

        String userProjectPath = systemUserHomePath + userRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString()+ "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String appprofileConfigs = parseResult.get("BuildSettings").toString();
        String appProfileConfgsGson = parseResult.get("BuildSettingsGson").toString();


        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

            jsonProfileArr = (JsonArray) JsonParser.parseString(appProfileConfgsGson);

            boolean isConfigUpdate = false;
            for (int i = 0; i < jsonProfileArr.size(); i++) {
                for (String s : jsonProfileArr.get(i).getAsJsonObject().keySet()) {
                    String nowKey = s;
                    if (!nowKey.equals("min_target_version")) {
                        if (!jsonProfileArr.get(i).getAsJsonObject().get(nowKey).toString().equals("\"\"")) {
                            isConfigUpdate = true;
                        }
                    }
                }
            }

            JSONObject applyConfigString = new JSONObject();

            JSONObject templateConfig = (JSONObject) parseResult.get("templateConfig");

            /** WmatrixConfig.yaml 파일에 저장된 config 정보 **/
            JSONObject getConfig = (JSONObject) templateConfig.get("getConfig");
            JSONObject wmatrix = (JSONObject) getConfig.get("wmatrix");
            JSONObject getConfigProfiles = (JSONObject) wmatrix.get("profiles");

            String activateProfile = wmatrix.get("active").toString();
            String packageName = ((JSONObject) getConfigProfiles.get("product")).get("applicationId").toString();

            if (isConfigUpdate) {
                JSONObject profiles = new JSONObject();

                for (int i = 0; i < jsonProfileArr.size(); i++) {
                    JSONObject profile = new JSONObject();

                    /**
                     *  wmatrix app config > server config 정보 저장 json, array
                     */
                    JsonElement serverConfig = null;
                    JsonObject serverConfigObj = new JsonObject();
                    JSONObject dataConfig = new JSONObject();
                    JSONArray dataJsonArray = new JSONArray();
                    JsonElement serverArr = null;
                    JsonArray serverJsonArray = new JsonArray();
                    JsonArray tempServerJsonArray = new JsonArray();

                    JsonObject profileJsonObject = ((JsonObject) jsonProfileArr.get(i));

                    /** 첫번째 프로파일을 active profile로 지정한다. **/
                    if (i == 0) {
                        activateProfile = profileJsonObject.get("profile_name").toString().replace("\"", "");
                        packageName = profileJsonObject.get("package_name").toString().replace("\"", "");
                    }

                    profile.put("applicationId", profileJsonObject.get("app_id").toString().replace("\"", ""));
                    profile.put("appName", profileJsonObject.get("app_name").toString().replace("\"", ""));
                    profile.put("appVersion", profileJsonObject.get("app_version").toString().replace("\"", ""));
                    profile.put("appVersionCode", profileJsonObject.get("app_version_code").toString().replace("\"", ""));
                    profile.put("minSdkVersion", profileJsonObject.get("min_target_version").toString().replace("\"", ""));
                    profile.put("useServerSelectScreen", false);

//                    if (profileJsonObject.has("icon_image_path") && profileJsonObject.has("icon_image_path")) {
//                        profile.put("icon_image_path", profileJsonObject.get("icon_image_path"));
//                    }
                    serverArr = profileJsonObject.get("server");
                    serverJsonArray = serverArr.getAsJsonArray();
                    for(int s = 0 ; s < serverJsonArray.size(); s++){
                        serverConfig = serverJsonArray.get(s);
                        tempServerJsonArray.add(serverConfig);
                    }

                    dataConfig.put("data",tempServerJsonArray);
                    dataConfig.put("group","W-Matrix Release");

                    dataJsonArray.add(dataConfig);

                    profile.put("server", dataJsonArray);

                    profiles.put(profileJsonObject.get("profile_name").toString().replace("\"", ""), profile);
                }
                JSONObject wmatrixJSON = new JSONObject();
                wmatrixJSON.put("active", activateProfile);
                wmatrixJSON.put("profiles", profiles);

                applyConfigString.put("wmatrix", wmatrixJSON);
            } else {
                applyConfigString.put("wmatrix", wmatrix);
            }

            applyConfigString.put("packageName", packageName);

            CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
            commandLineShell.addArgument(userProjectPath); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

            // product_type
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell.addArgument("setConfig");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            commandLineShell.addArgument("-P");
            commandLineShell.addArgument("data=");
            commandLineShell.addArgument(applyConfigString.toString(), false);

            try {
                executeCommonsExecApplyConfig(session, commandLineShell, platform, serverMode, hqKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
            /** 2023.11.23 작업목록
             * 1. 지금 기존 방식에서 iOS의
             *    - profile_name -> target_name
             *    - package_name -> xcode_proj_name
             *    으로 변경
             *
             * 2. 현재 사용자가 입력한 config 정보로 setConfig가 되지 않고,
             *    템플릿이 가지고 있는 정보로 setConfig가 되는 부분 수정
             *
             * 3. gson, simplejson 사용 부분 -> jackson 으로 변경
             */
            try {
                ObjectMapper objectMapper =
                        new ObjectMapper()
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

                JsonNode jsonNode = objectMapper.readTree(appProfileConfgsGson);
                Map<String, Object> setConfig = new HashMap<String, Object>();

                setConfig.put("projectPath", userProjectPath);

                Map<String, Object> targetConfigs = new HashMap<String, Object>();

                Iterator<JsonNode> targetIter = jsonNode.iterator();

                while(targetIter.hasNext()) {
                    JsonNode target = targetIter.next();
                    Map<String, Object> targetConfig = new HashMap<String, Object>();

                    targetConfig.put("applicationID", target.get("app_id").asText());
                    targetConfig.put("appName", target.get("app_name").asText());
                    targetConfig.put("version", target.get("app_version").asText());
                    targetConfig.put("versionCode", target.get("app_version_code").asText());
                    targetConfig.put("deploymentTarget", target.get("min_target_version").asText());
                    targetConfig.put("startServerGroupName", target.get("target_name").asText());
                    targetConfig.put("useServerSelectScreen", false);

                    Map<String, Object> serverGroupListObject = new HashMap<String, Object>();
                    List<Map<String, String>> serverGroupList = new ArrayList<Map<String, String>>();
                    Map<String, String> serverGroup;

                    Iterator<JsonNode> serverIter = target.get("server").iterator();
                    while(serverIter.hasNext()) {
                        serverGroup = new HashMap<String, String>();
                        JsonNode server = serverIter.next();

                        serverGroup.put("name", server.get("name").asText());
                        serverGroup.put("appID", server.get("appId").asText());
                        serverGroup.put("url", server.get("url").asText());
                        serverGroup.put("localWebServerURL", "24680"); // 추후, 입력 받는 걸로 변경해야 함.

                        serverGroupList.add(serverGroup);
                    }

                    serverGroupListObject.put(target.get("target_name").asText(), serverGroupList);
                    targetConfig.put("serverGroups", serverGroupListObject);
                    targetConfigs.put(target.get("target_name").asText(), targetConfig);

                    setConfig.put("projectName", target.get("xcode_proj_name"));
                }

                setConfig.put("targets", targetConfigs);

                CommandLine commandLineShell = null;
                if(productType.toLowerCase().equals("wmatrix")) {
                    commandLineShell = CommandLine.parse(wmatrixmanager);
                    commandLineShell.addArgument("setconfig");
                    commandLineShell.addArgument("-j");
                    commandLineShell.addArgument(objectMapper.writeValueAsString(setConfig), false);
                }

                if (productType.toLowerCase().equals("wmatrix")) {
                    try {
                        executeCommonsExecApplyConfig(session, commandLineShell, platform, serverMode, hqKey); // debug
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (JsonParseException e) {
                log.error("JsonParseException ", e.getLocalizedMessage(), e);
            } catch (JsonMappingException e) {
                log.error("JsonMappingException ", e.getLocalizedMessage(), e);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException ", e.getLocalizedMessage(), e);
            }
        }
    }

    private void executeMultiProfileAutoKeyConfigUpdateAllCLI(WebSocketSession session, Map<String, Object> parseResult, JsonObject jsonGetConfigKey ) throws Exception {

        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();
        JSONObject appConfigAddJSON = new JSONObject();

        JsonArray jsonProfileArr = new JsonArray();

        List<String> appConfigKeyList = new ArrayList<>(Arrays.asList("applicationId", "appName","appVersion", "appVersionCode","minSdkVersion","server"));
        List<String> configServerDetailKeyList = new ArrayList<>(Arrays.asList("server","name", "appId","url"));

        String platform = parseResult.get("platform").toString();
        String productType = parseResult.get("product_type").toString();
        String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString(); //HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO_FROM_HEADQUATER

        String userProjectPath = systemUserHomePath + userRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString()+ "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String appprofileConfigs = parseResult.get("BuildSettings").toString();
        String appProfileConfgsGson = parseResult.get("BuildSettingsGson").toString();
        String hqKey  = parseResult.get("hqKey").toString();


        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
            shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");
            jsonProfileArr = (JsonArray) JsonParser.parseString(appProfileConfgsGson);

            boolean isConfigUpdate = false;
            for (int i = 0; i < jsonProfileArr.size() && isConfigUpdate == false; i++) {
                for (String s : jsonProfileArr.get(i).getAsJsonObject().keySet()) {
                    String nowKey = s;
                    if (!nowKey.equals("min_target_version")) {
                        if (!jsonProfileArr.get(i).getAsJsonObject().get(nowKey).toString().equals("\"\"")) {
                            isConfigUpdate = true;
                        }
                    }
                }
            }

            JSONObject applyConfigString = new JSONObject();

            JSONObject getConfig = (JSONObject) parser.parse(jsonGetConfigKey.toString());
            JSONObject wmatrix = (JSONObject) getConfig.get("wmatrix");
            JSONObject getConfigProfiles = (JSONObject) wmatrix.get("profiles");

            String activateProfile = wmatrix.get("active").toString();
            String packageName = ((JSONObject) getConfigProfiles.get(activateProfile)).get("applicationId").toString();

            if (isConfigUpdate) {
                JSONObject profiles = new JSONObject();

                for (int i = 0; i < jsonProfileArr.size(); i++) {
                    JSONObject profile = new JSONObject();

                    /**
                     *  wmatrix app config > server config 정보 저장 json, array
                     */
                    JsonElement serverConfig = null;
                    JsonObject serverConfigObj = new JsonObject();
                    JSONObject dataConfig = new JSONObject();
                    JSONArray dataJsonArray = new JSONArray();
                    JsonElement serverArr = null;
                    JsonArray serverJsonArray = new JsonArray();
                    JsonArray tempServerJsonArray = new JsonArray();

                    JsonObject profileJsonObject = ((JsonObject) jsonProfileArr.get(i));

                    profile.put("applicationId", profileJsonObject.get("app_id").toString().replace("\"", ""));
                    profile.put("appName", profileJsonObject.get("app_name").toString().replace("\"", ""));
                    profile.put("appVersion", profileJsonObject.get("app_version").toString().replace("\"", ""));
                    profile.put("appVersionCode", profileJsonObject.get("app_version_code").toString().replace("\"", ""));
                    profile.put("minSdkVersion", profileJsonObject.get("min_target_version").toString().replace("\"", ""));
                    profile.put("useServerSelectScreen", false);

//                    if (profileJsonObject.has("icon_image_path") && profileJsonObject.has("icon_image_path")) {
//                        profile.put("icon_image_path", profileJsonObject.get("icon_image_path"));
//                    }
                    serverArr = profileJsonObject.get("server");
                    serverJsonArray = serverArr.getAsJsonArray();
                    for(int s = 0 ; s < serverJsonArray.size(); s++){
                        serverConfig = serverJsonArray.get(s);
                        tempServerJsonArray.add(serverConfig);
                    }

                    dataConfig.put("data",tempServerJsonArray);
                    dataConfig.put("group","W-Matrix Release");

                    dataJsonArray.add(dataConfig);

                    profile.put("server", dataJsonArray);

                    profiles.put(profileJsonObject.get("profile_name").toString().replace("\"", ""), profile);
                }
                JSONObject wmatrixJSON = new JSONObject();
                wmatrixJSON.put("active", activateProfile);
                wmatrixJSON.put("profiles", profiles);

                applyConfigString.put("wmatrix", wmatrixJSON);
            } else {
                applyConfigString.put("wmatrix", wmatrix);
            }

            applyConfigString.put("packageName", packageName);
            CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
            commandLineShell.addArgument(userProjectPath); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

            // product_type
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell.addArgument("setConfig");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            commandLineShell.addArgument("-P");
            commandLineShell.addArgument("data=");
            commandLineShell.addArgument(applyConfigString.toString(), false);

            try {
                executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.UPDATE, hqKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())) {

            try {
                ObjectMapper objectMapper =
                        new ObjectMapper()
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

                JsonNode jsonNode = objectMapper.readTree(appProfileConfgsGson);
                Map<String, Object> setConfig = new HashMap<String, Object>();

                setConfig.put("projectPath", userProjectPath);

                Map<String, Object> targetConfigs = new HashMap<String, Object>();

                Iterator<JsonNode> targetIter = jsonNode.iterator();

                while(targetIter.hasNext()) {
                    JsonNode target = targetIter.next();
                    Map<String, Object> targetConfig = new HashMap<String, Object>();

                    targetConfig.put("applicationID", target.get("app_id").asText());
                    targetConfig.put("appName", target.get("app_name").asText());
                    targetConfig.put("version", target.get("app_version").asText());
                    targetConfig.put("versionCode", target.get("app_version_code").asText());
                    targetConfig.put("deploymentTarget", target.get("min_target_version").asText());
                    targetConfig.put("startServerGroupName", target.get("target_name").asText());
                    targetConfig.put("useServerSelectScreen", false);

                    Map<String, Object> serverGroupListObject = new HashMap<String, Object>();
                    List<Map<String, String>> serverGroupList = new ArrayList<Map<String, String>>();
                    Map<String, String> serverGroup;

                    Iterator<JsonNode> serverIter = target.get("server").iterator();
                    while(serverIter.hasNext()) {
                        serverGroup = new HashMap<String, String>();
                        JsonNode server = serverIter.next();

                        serverGroup.put("name", server.get("name").asText());
                        serverGroup.put("appID", server.get("appId").asText());
                        serverGroup.put("url", server.get("url").asText());
                        serverGroup.put("localWebServerURL", "24680"); // 추후, 입력 받는 걸로 변경해야 함.

                        serverGroupList.add(serverGroup);
                    }

                    serverGroupListObject.put(target.get("target_name").asText(), serverGroupList);
                    targetConfig.put("serverGroups", serverGroupListObject);
                    targetConfigs.put(target.get("target_name").asText(), targetConfig);

                    setConfig.put("projectName", target.get("xcode_proj_name"));
                }

                setConfig.put("targets", targetConfigs);

                CommandLine commandLineShell = null;
                if(productType.toLowerCase().equals("wmatrix")) {
                    commandLineShell = CommandLine.parse(wmatrixmanager);
                    commandLineShell.addArgument("setconfig");
                    commandLineShell.addArgument("-j");
                    commandLineShell.addArgument(objectMapper.writeValueAsString(setConfig), false);
                }

                if (productType.toLowerCase().equals("wmatrix")) {
                    try {
                        executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.UPDATE, hqKey); // debug
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (JsonParseException e) {
                log.error("JsonParseException ", e.getLocalizedMessage(), e);
            } catch (JsonMappingException e) {
                log.error("JsonMappingException ", e.getLocalizedMessage(), e);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException ", e.getLocalizedMessage(), e);
            }
        }
    }

    // 기존 프로젝트 생성 내부 작업 관련 소스 코드 붙이는 작업 했음
    // update 에 맞춰서 추가 작업이 필요함.
    // 먼저 처리 하는 과정을 디버깅 하면서 처리해야함..
    private void executeMultiProfileConfigUpdateAllCLI(WebSocketSession session, Map<String, Object> parseResult) throws Exception {

        String platform = parseResult.get("platform").toString();
        String productType = parseResult.get("product_type").toString();
        String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString(); //

        String projectDirName = systemUserHomePath + userRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString()+ "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String userProjectPath = "";
        JSONObject appprofileConfigs = null;

        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();
        JSONObject appConfigAddJSON = new JSONObject();

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
            // String arrayMultiProfileConfigStr = parseResult.get("BuildSettings").toString();
            String appprofileConfigStr = parseResult.get("BuildSettings").toString();

            shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

            if(appprofileConfigStr.equals("")){

            }else {

                String appConfigList = appprofileConfigStr;
                appConfigList = appConfigList.replaceAll(" ", "");
                appConfigList = appConfigList.replace("[BuildSetting(","{");
                // appConfigList = appConfigList.replaceAll("BuildSetting","");
                 appConfigList = appConfigList.replace("(project_setting_id=null","{project_setting_id=null");
                 appConfigList = appConfigList.replace("project_setting_etc3=null)","project_setting_etc3=null}");
                // appConfigList = appConfigList.replace("}]","");

                String[] appConfigItems = appConfigList.split("\\},BuildSetting");

                log.info(appConfigList);
                log.info(appConfigItems.toString());
                log.info(String.valueOf(appConfigItems.length));

                log.info(appConfigItems[0]);

                for(int j = 0; j < appConfigItems.length; j++){
                    appConfigItems[j] = appConfigItems[j] +"}";
                    //appConfigItems[j] = appConfigItems[j].replaceAll(":","\":\"");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\{","{\"");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\}","\"}");
                    appConfigItems[j] = appConfigItems[j].replaceAll("=","\":\"");
                    appConfigItems[j] = appConfigItems[j].replaceAll(",","\",\"");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\"\\[\"\\{","[{\"");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\}\"\\]\",","\"}],");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\}\",\"\\{","},{");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\"\\{\"","{\"");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\"\\}\"","\"}");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\"\\[\\{","[{");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\}\\]\"","\"}]");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\}\\]\"","\"}]");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\"\\}\\]","}]");
                    appConfigItems[j] = appConfigItems[j].replaceAll("\\}\\]\\}","}");
                    // }]}}


                    log.info(appConfigItems[j]);

                    appConfigAddJSON = (JSONObject) parser.parse(appConfigItems[j].toString());

                    appconfigMultiArrayJson.add(appConfigAddJSON);
                }

            }

            String applyMultiAppConfigAndServerJsonArr = "";

            String applyConfigString = "";
            // appconfig for 문 처리해야함
            for(int appconfigcnt = 0; appconfigcnt < appconfigMultiArrayJson.size(); appconfigcnt++){

                JSONObject jsonAppConfigOneSetting = appconfigMultiArrayJson.get(appconfigcnt);
                log.info(jsonAppConfigOneSetting.toJSONString());

                if(appconfigcnt == 0){
                    ArrayList<JSONObject> applyServerArrayJson1 = new ArrayList<>();
                    String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                    AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..

                    applyConfigString += "{\"packageName\":\"" + jsonAppConfigOneSetting.get("package_name").toString() +"\", \"wmatrix\": {\"active\": \""+ jsonAppConfigOneSetting.get("profile_name").toString()
                            +"\", \"profiles\": {\""+jsonAppConfigOneSetting.get("profile_name").toString()
                            +"\": {\"applicationId\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                            ",\"appName\":\""+ AppName + "\"" +
                            ",\"appVersion\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                            ",\"appVersionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\"" +
                            ",\"minSdkVersion\":\""+ jsonAppConfigOneSetting.get("min_target_version").toString() +"\"";
                            // ",\"debuggable\":false";

                    String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();
                    log.info(MultiServerConfigStr);

//                    MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                    String[] items = MultiServerConfigStr.split("\\},\\{");

                    for(int i = 0; i < items.length; i++) {

                        items[i] = "{" + items[i] + "}";
                        //items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                        //items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                        //items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                        //items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                        // available_plugin_module

                        log.info(items[i]);
                        // configjson 하나로 여러개의 처리로 전환하기
                        config1JSON = (JSONObject) parser.parse(items[i].toString());

                        applyServerArrayJson1.add(config1JSON); // 동적 처리 적용

                    }

                    String applyConfigServerJsonArr = "";

                    for (int config = 0 ; config < applyServerArrayJson1.size() ; config++){

                        JSONObject jsonServerJsonSetting = applyServerArrayJson1.get(config);
                        log.info(jsonServerJsonSetting.toJSONString());

                        if(config == 0){
                            applyConfigServerJsonArr += ", \"server\": [{\"name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"url\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                        }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]" +
//                            ", \"PackageName\":\""+configJson.get("PackageName").toString() +"\"}";
//                }
                        else {
                            applyConfigServerJsonArr += ", {\"name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"url\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                        }
                    }
                    applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기
                }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]" +
//                            ", \"PackageName\":\""+configJson.get("PackageName").toString() +"\"}";
//                }
                else {
                    ArrayList<JSONObject> applyServerArrayJson2 = new ArrayList<>();
                    String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                    AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..

                    applyConfigString += "\"" + jsonAppConfigOneSetting.get("profile_name").toString()
                            +"\": {\"applicationId\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                            ",\"appName\":\""+ AppName + "\"" +
                            ",\"appVersion\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                            ",\"appVersionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\"" +
                            ",\"minSdkVersion\":\""+ jsonAppConfigOneSetting.get("min_target_version").toString() +"\"";
                            // ",\"debuggable\":false";

                    String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();

//                    MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                    MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                    String[] items = MultiServerConfigStr.split("\\},\\{");

                    for(int i = 0; i < items.length; i++) {

                        items[i] = "{" + items[i] + "}";
                        //items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                        //items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                        //items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                        //items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                        // available_plugin_module

                        // configjson 하나로 여러개의 처리로 전환하기
                        config1JSON = (JSONObject) parser.parse(items[i].toString());

                        applyServerArrayJson2.add(config1JSON); // 동적 처리 적용
                    }

                    String applyConfigServerJsonArr = "";

                    for (int config = 0 ; config < applyServerArrayJson2.size() ; config++){

                        JSONObject jsonServerJsonSetting = applyServerArrayJson2.get(config);
                        log.info(jsonServerJsonSetting.toJSONString());

                        if(config == 0){
                            applyConfigServerJsonArr += ", \"server\": [{\"name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"url\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                        }
//                else if(config == applyServerArrayJson.size() - 1){
//                    applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]" +
//                            ", \"PackageName\":\""+configJson.get("PackageName").toString() +"\"}";
//                }
                        else {
                            applyConfigServerJsonArr += ", {\"name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"appId\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"url\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                        }
                    }

                    if(appconfigMultiArrayJson.size() -1 == appconfigcnt){
                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]}"; // app config + server config string 합치기
                    }else {
                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기
                    }
                }
            }

            applyConfigString = applyConfigString + "}}}"; // app config + server config string 합치기


            log.info("applyConfigString : {}",applyConfigString);


            CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
            if(projectDirName.equals("")){
                commandLineShell.addArgument(userProjectPath+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }else {
                commandLineShell.addArgument(userProjectPath+"/"+projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
            }
            // commandLineShell.addArgument(userProjectPath+"/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

            // product_type
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell.addArgument("setConfig");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell.addArgument(":whybrid-plugins:setConfig");
            }

            commandLineShell.addArgument("-P");
            // ApplicationID, AppName, AppVersion, AppVersionCode
            commandLineShell.addArgument("data=");
            //commandLineShell.addArgument("data="+"{ApplicationID:"+configJson.get("ApplicationID").toString()+
            commandLineShell.addArgument(applyConfigString, false);

            executeCLIExecUpdateMultiProfileConfig(session, commandLineShell, platform, ServerConfigMode.UPDATE, hqKey);

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            String appprofileConfigStr = parseResult.get("BuildSettings").toString();

                if(productType.toLowerCase().equals("wmatrix")){

                    String appConfigList = appprofileConfigStr;
                    appConfigList = appConfigList.replaceAll(" ", "");
                    appConfigList = appConfigList.replace("[BuildSetting(","{");
                    // appConfigList = appConfigList.replaceAll("BuildSetting","");
                    appConfigList = appConfigList.replace("(project_setting_id=null","{project_setting_id=null");
                    appConfigList = appConfigList.replace("project_setting_etc3=null)","project_setting_etc3=null}");
                    // appConfigList = appConfigList.replace("}]","");

                    String[] appConfigItems = appConfigList.split("\\},BuildSetting");

                    log.info(appConfigList);
                    log.info(appConfigItems.toString());
                    log.info(String.valueOf(appConfigItems.length));

                    log.info(appConfigItems[0]);

                    for(int j = 0; j < appConfigItems.length; j++){
                        appConfigItems[j] = appConfigItems[j] +"}";
                        //appConfigItems[j] = appConfigItems[j].replaceAll(":","\":\"");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\{","{\"");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\}","\"}");
                        appConfigItems[j] = appConfigItems[j].replaceAll("=","\":\"");
                        appConfigItems[j] = appConfigItems[j].replaceAll(",","\",\"");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\"\\[\"\\{","[{\"");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\}\"\\]\",","\"}],");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\}\",\"\\{","},{");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\"\\{\"","{\"");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\"\\}\"","\"}");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\"\\[\\{","[{");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\}\\]\"","\"}]");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\}\\]\"","\"}]");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\"\\}\\]","}]");
                        appConfigItems[j] = appConfigItems[j].replaceAll("\\}\\]\\}","}");
                        // }]}}


                        log.info(appConfigItems[j]);

                        appConfigAddJSON = (JSONObject) parser.parse(appConfigItems[j].toString());

                        appconfigMultiArrayJson.add(appConfigAddJSON);
                    }

                }

            String applySetServerConfig = "";
            String applyMultiAppConfigAndServerJsonArr = "";
            String applyConfigString = "";

            if(productType.toLowerCase().equals("wmatrix")){

                for(int appconfigcnt = 0; appconfigcnt < appconfigMultiArrayJson.size(); appconfigcnt++) {

                    JSONObject jsonAppConfigOneSetting = appconfigMultiArrayJson.get(appconfigcnt);
                    log.info(jsonAppConfigOneSetting.toJSONString());

                    if(appconfigcnt == 0){
                        ArrayList<JSONObject> applyServerArrayJson1 = new ArrayList<>();
                        String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                        AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..
                        // getProjectNamePath configJson.get("getProjectNamePath").toString().replace(".xcodeproj","")
                        applyConfigString += "{\"projectPath\": \""+ userProjectPath+"/"+projectDirName +"\", \"projectName\":\"" + jsonAppConfigOneSetting.get("package_name").toString()
                                +"\", \"targets\": {\""+jsonAppConfigOneSetting.get("profile_name").toString()
                                +"\": {\"applicationID\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                                ",\"appName\":\""+ AppName + "\"" +
                                ",\"version\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\"" +
                                ",\"deploymentTarget\":\""+jsonAppConfigOneSetting.get("min_target_version").toString()+"\"";

                        String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();
                        log.info(MultiServerConfigStr);

//                        MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                        String[] items = MultiServerConfigStr.split("\\},\\{");

                        for(int i = 0; i < items.length; i++) {

                            items[i] = "{" + items[i] + "}";
                            //items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                            //items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                            //items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                            //items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                            // available_plugin_module

                            log.info(items[i]);
                            // configjson 하나로 여러개의 처리로 전환하기
                            config1JSON = (JSONObject) parser.parse(items[i].toString());

                            applyServerArrayJson1.add(config1JSON); // 동적 처리 적용

                        }

                        String applyConfigServerJsonArr = "";

                        for (int config = 0 ; config < applyServerArrayJson1.size() ; config++){

                            JSONObject jsonServerJsonSetting = applyServerArrayJson1.get(config);
                            log.info(jsonServerJsonSetting.toJSONString());

                            if(config == 0){
                                applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                            } else {
                                applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                            }

                        }

                        applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기


                    } else {
                        ArrayList<JSONObject> applyServerArrayJson2 = new ArrayList<>();
                        String AppName = jsonAppConfigOneSetting.get("app_name").toString();
                        AppName = AppName.replace(" "," "); // 건들지 말기 공백특수문자임..

                        applyConfigString += "\"" + jsonAppConfigOneSetting.get("profile_name").toString()
                                +"\": {\"applicationID\":\""+ jsonAppConfigOneSetting.get("app_id").toString() + "\"" +
                                ",\"appName\":\""+ AppName + "\"" +
                                ",\"version\":\"" + jsonAppConfigOneSetting.get("app_version").toString() + "\"" +
                                ",\"versionCode\":\"" + jsonAppConfigOneSetting.get("app_version_code").toString() + "\""+
                                ",\"deploymentTarget\":\""+jsonAppConfigOneSetting.get("min_target_version").toString()+"\"";

                        String MultiServerConfigStr = jsonAppConfigOneSetting.get("server").toString();

//                        MultiServerConfigStr = MultiServerConfigStr.replaceAll(" ","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("[{","");
                        MultiServerConfigStr = MultiServerConfigStr.replace("}]","");

                        String[] items = MultiServerConfigStr.split("\\},\\{");

                        for(int i = 0; i < items.length; i++) {

                            items[i] = "{" + items[i] + "}";

                            // configjson 하나로 여러개의 처리로 전환하기
                            config1JSON = (JSONObject) parser.parse(items[i].toString());

                            applyServerArrayJson2.add(config1JSON); // 동적 처리 적용
                        }

                        String applyConfigServerJsonArr = "";

                        for (int config = 0 ; config < applyServerArrayJson2.size() ; config++){

                            JSONObject jsonServerJsonSetting = applyServerArrayJson2.get(config);
                            log.info(jsonServerJsonSetting.toJSONString());

                            if(config == 0){
                                applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                            } else {
                                applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                            }
                        }

                        if(appconfigMultiArrayJson.size() -1 == appconfigcnt){
                            applyConfigString = applyConfigString + applyConfigServerJsonArr + "]}"; // app config + server config string 합치기
                        }else {
                            applyConfigString = applyConfigString + applyConfigServerJsonArr + "]},"; // app config + server config string 합치기
                        }
                    }
                }

                applyConfigString = applyConfigString + "}}"; // app config + server config string 합치기

                log.info("applyConfigString : {}",applyConfigString);

            }

            log.info(applyConfigString);

            // cmd cli 초기 설정
            CommandLine commandLineShell = null;
            CommandLine commandLineReleasePlugman = null;
            CommandLine commandLineSetServerConfig = null;

            // CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
            if(productType.toLowerCase().equals("wmatrix")) {
                commandLineShell = CommandLine.parse(wmatrixmanager);
                commandLineShell.addArgument("setconfig");
                commandLineShell.addArgument("-j");
                //commandLineShell.addArgument(shellscriptFileName); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                //commandLineShell.addArgument("setconfig");
                // commandLineShell.addArgument(userProjectPath + projectDirName+"/Config/Debug.xcconfig"); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                commandLineShell.addArgument(applyConfigString, false);

            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell = CommandLine.parse("whmanager");
            }else {
                commandLineShell = CommandLine.parse("whmanager");
                commandLineShell.addArgument("setconfig");
                commandLineShell.addArgument("-i");
                //commandLineShell.addArgument(shellscriptFileName); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                //commandLineShell.addArgument("setconfig");
                // commandLineShell.addArgument(userProjectPath + projectDirName+"/Config/Debug.xcconfig"); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
                commandLineShell.addArgument(applyConfigString, false);

            }

            if (productType.toLowerCase().equals("wmatrix")) {
                executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.UPDATE, hqKey); // debug

            }else {
                // executeCommonsExecApplyConfig(commandLineShell, platform, ServerConfigMode.UPDATE); // debug
                // executeCommonsExecApplyConfig(commandLineReleasePlugman, platform, ServerConfigMode.NONE); // relase
                // executeCommonsExecApplyConfig(commandLineSetServerConfig, platform, ServerConfigMode.NONE); // server config
            }
        }
    }

    private void executeiOSBuildBeforeApplyConfigAllCLI(String buildTaskId, String platform, String userProjectPath, String projectDirName,JSONObject configJson, String serverConfigs) throws Exception {

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            // 건들지 말기
            // debug config
            String applyConfigDebugString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                    ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Debug.xcconfig" + "\"" +
                    ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                    ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

            // release config
            String applyConfigReleaseString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                    ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                    ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                    ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

            // CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
            CommandLine commandLineShell = CommandLine.parse("whmanager");
            commandLineShell.addArgument("setconfig");
            commandLineShell.addArgument("-i");
            commandLineShell.addArgument(applyConfigDebugString, false);

            CommandLine commandLineReleasePlugman = CommandLine.parse("whmanager");
            commandLineReleasePlugman.addArgument("setconfig");
            commandLineReleasePlugman.addArgument("-i");
            commandLineReleasePlugman.addArgument(applyConfigReleaseString, false);

            // executeCommonsExecApplyConfig(commandLineShell, platform, ServerConfigMode.NONE); // debug
            // executeCommonsExecApplyConfig(commandLineReleasePlugman, platform, ServerConfigMode.NONE); // relase
        }

    }

    private void executeiOSBuildApplyConfigAllCLI(String buildTaskId, String platform, String userProjectPath, String projectDirName, Map<String, Object> parseResult, String serverConfigs) throws Exception {

        String productType = parseResult.get("product_type").toString();
        String hqKey = parseResult.get("hqKey").toString();

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            // product type 기준으로 분기 처리 해야함

            // 건들지 말기

            String applyConfigDebugString = "";
            String applyConfigReleaseString = "";


            if(productType.toLowerCase().equals("wmatrix")){

                // debug config
                applyConfigDebugString = "{" +
                        "\"ApplicationID\":\""+ parseResult.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                        ",\"AppName\":\""+ parseResult.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                        ",\"AppVersion\":\"" + parseResult.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                        ",\"AppVersionCode\":\"" + parseResult.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                        ",\"ProjectPath\":\"" + userProjectPath + "\"" +
                        ",\"ProjectName\":\"" + parseResult.get("iOSProjectName").toString() +"\"}";

                // release config
                applyConfigReleaseString = "{" +
                        "\"ApplicationID\":\""+ parseResult.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                        ",\"AppName\":\""+ parseResult.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                        ",\"AppVersion\":\"" + parseResult.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                        ",\"AppVersionCode\":\"" + parseResult.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                        ",\"ProjectPath\":\"" + userProjectPath + "\"" +
                        ",\"ProjectName\":\"" + parseResult.get("iOSProjectName").toString() +"\"}";

            }else {

                // debug config
                applyConfigDebugString = "{" +
                        "\"ApplicationID\":\""+ parseResult.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                        ",\"AppName\":\""+ parseResult.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                        ",\"AppVersion\":\"" + parseResult.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                        ",\"AppVersionCode\":\"" + parseResult.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                        ",\"ConfigPath\":\""+ userProjectPath +"/Config/Debug.xcconfig" + "\"" +
                        ",\"ProjectPath\":\"" + userProjectPath +"/"+parseResult.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                        ",\"ProjectName\":\"" + parseResult.get("iOSProjectName").toString() +"\"}";

                // release config
                applyConfigReleaseString = "{" +
                        "\"ApplicationID\":\""+ parseResult.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                        ",\"AppName\":\""+ parseResult.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                        ",\"AppVersion\":\"" + parseResult.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                        ",\"AppVersionCode\":\"" + parseResult.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                        ",\"ConfigPath\":\""+ userProjectPath +"/Config/Release.xcconfig" + "\"" +
                        ",\"ProjectPath\":\"" + userProjectPath +"/"+parseResult.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                        ",\"ProjectName\":\"" + parseResult.get("iOSProjectName").toString() +"\"}";
            }

            // CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");

            CommandLine commandLineShell = null;
            CommandLine commandLineReleasePlugman = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineShell = CommandLine.parse(wmatrixmanager);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineShell = CommandLine.parse("whmanager");
            }

            if(commandLineShell != null){
                commandLineShell.addArgument("setconfig");
                commandLineShell.addArgument("-i");
                commandLineShell.addArgument(applyConfigDebugString, false);
            }

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineReleasePlugman = CommandLine.parse(wmatrixmanager);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineReleasePlugman = CommandLine.parse("whmanager");
            }

            if(commandLineReleasePlugman != null){
                commandLineReleasePlugman.addArgument("setconfig");
                commandLineReleasePlugman.addArgument("-i");
                commandLineReleasePlugman.addArgument(applyConfigReleaseString, false);
            }

            executeCommonsExecApplyConfig(null, commandLineShell, platform, ServerConfigMode.NONE, hqKey); // debug
            executeCommonsExecApplyConfig(null, commandLineReleasePlugman, platform, ServerConfigMode.NONE, hqKey); // relase
        }

    }

    private void executeiOSServerConfigAndProjectName(WebSocketSession session, String buildTaskId, String platform, String userProjectPath, String projectDirName,JSONObject configJson, String serverConfigs, String productType, String hqKey) throws Exception {

        ArrayList<JSONObject> applyServerArrayJson = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject config1JSON = new JSONObject();
        JSONObject config2JSON = new JSONObject();
        JSONObject config3JSON = new JSONObject();

        String serverConfigStr = serverConfigs;

        String applyConfigServerJsonArr = "";

        if(serverConfigStr.equals("")){

        }else {

            String serverConfigList = serverConfigStr;
//            serverConfigList = serverConfigList.replaceAll(" ","");
            serverConfigList = serverConfigList.replace("[{","");
            serverConfigList = serverConfigList.replace("}]","");

            String[] items = serverConfigList.split("\\},\\{");

            for(int i = 0; i < items.length; i++) {

                items[i] = "{" + items[i] + "}";
                items[i] = items[i].replaceAll("\\{", "{\""); // replace 1
                items[i] = items[i].replaceAll("\\}", "\"}"); // replace 2
                items[i] = items[i].replaceAll("=", "\":\""); // replace 3
                items[i] = items[i].replaceAll(",", "\",\""); // replace 4
                // available_plugin_module
                log.info(items[i]);

                // configjson 하나로 여러개의 처리로 전환하기
                config1JSON = (JSONObject) parser.parse(items[i].toString());

                applyServerArrayJson.add(config1JSON); // 동적 처리 적용
            }

        }

        // 건들지 말기
        // debug config
        String applyConfigDebugString = "";
        String applyConfigReleaseString = "";
        String applySetServerConfig = "";

        if(productType.toLowerCase().equals("wmatrix")){
            applyConfigDebugString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                    ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                    ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

            applyConfigReleaseString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                    ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                    ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

            applySetServerConfig = "{";

            for (int config = 0 ; config < applyServerArrayJson.size() ; config++){

                JSONObject jsonServerJsonSetting = applyServerArrayJson.get(config);

                if(config == 0){
                    applyConfigServerJsonArr += " \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                }
//            else if(config == applyServerArrayJson.size() - 1){
//                applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]}";
//            }
                else {
                    applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                }

            }

        }else {

            applyConfigDebugString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                    ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName + "\"" +
                    ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

            applyConfigReleaseString = "{" +
                    "\"ApplicationID\":\""+ configJson.get(PayloadMsgType.ApplicationID.name()).toString()+"\"" +
                    ",\"AppName\":\""+ configJson.get(PayloadMsgType.AppName.name()).toString() + "\"" +
                    ",\"AppVersion\":\"" + configJson.get(PayloadMsgType.AppVersion.name()).toString() + "\"" +
                    ",\"AppVersionCode\":\"" + configJson.get(PayloadMsgType.AppVersionCode.name()).toString() + "\"" +
                    ",\"ConfigPath\":\""+ userProjectPath +"/"+ projectDirName+"/Config/Release.xcconfig" + "\"" +
                    ",\"ProjectPath\":\"" + userProjectPath +"/"+ projectDirName +"/"+configJson.get("iOSProjectName").toString()+".xcodeproj" + "\"" +
                    ",\"ProjectName\":\"" + configJson.get("iOSProjectName").toString() +"\"}";

            applySetServerConfig = "{" + "\"LocalWebServerPort\":\"24680\"";

            for (int config = 0 ; config < applyServerArrayJson.size() ; config++){

                JSONObject jsonServerJsonSetting = applyServerArrayJson.get(config);

                if(config == 0){
                    applyConfigServerJsonArr += ", \"server\": [{\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\"" + jsonServerJsonSetting.get("ServerURL").toString() + "\"}";
                }
//            else if(config == applyServerArrayJson.size() - 1){
//                applyConfigServerJsonArr += ", {\"Name\":\""+ jsonServerJsonSetting.get("Name").toString() +"\",\"AppID\":\"" + jsonServerJsonSetting.get("AppID").toString() + "\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}]}";
//            }
                else {
                    applyConfigServerJsonArr += ", {\"Name\":\"" + jsonServerJsonSetting.get("Name").toString() + "\",\"AppID\":\""+ jsonServerJsonSetting.get("AppID").toString() +"\",\"ServerURL\":\""+ jsonServerJsonSetting.get("ServerURL").toString() +"\"}";
                }
            }
        }

        applySetServerConfig += applyConfigServerJsonArr + "]}";

        CommandLine commandLineShell = null;
        CommandLine commandLineReleasePlugman = null;
        CommandLine commandLineSetServerConfig = null;

        // CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineShell = CommandLine.parse(wmatrixmanager);
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineShell = CommandLine.parse("whmanager");
        }else {
            commandLineShell = CommandLine.parse("whmanager");
        }

        commandLineShell.addArgument("setconfig");
        commandLineShell.addArgument("-i");
        //commandLineShell.addArgument(shellscriptFileName); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
        //commandLineShell.addArgument("setconfig");
        // commandLineShell.addArgument(userProjectPath + projectDirName+"/Config/Debug.xcconfig"); // userRootPath + workspacePath + "/" + projectPath + "/WHybrid_Android/
        commandLineShell.addArgument(applyConfigDebugString, false);


        if(productType.toLowerCase().equals("wmatrix")){
            commandLineReleasePlugman = CommandLine.parse(wmatrixmanager);
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineReleasePlugman = CommandLine.parse("whmanager");
        }else {
            commandLineReleasePlugman = CommandLine.parse("whmanager");
        }

        commandLineReleasePlugman.addArgument("setconfig");
        commandLineReleasePlugman.addArgument("-i");
        commandLineReleasePlugman.addArgument(applyConfigReleaseString, false);

        // setServerConfig

        if(productType.toLowerCase().equals("wmatrix")){
            commandLineSetServerConfig = CommandLine.parse(wmatrixmanager);
        }else if(productType.toLowerCase().equals("whybrid")){
            commandLineSetServerConfig = CommandLine.parse("whmanager");
        }else {
            commandLineSetServerConfig = CommandLine.parse("whmanager");
        }

        commandLineSetServerConfig.addArgument("setserverconfig");
        commandLineSetServerConfig.addArgument("-p");

        if(projectDirName.equals("")){
            commandLineSetServerConfig.addArgument(userProjectPath+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
        }else {
            commandLineSetServerConfig.addArgument(userProjectPath+"/"+projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
        }
        commandLineSetServerConfig.addArgument("-i");
        commandLineSetServerConfig.addArgument(applySetServerConfig, false);
        log.info("applySetServerConfig.. ");
        log.info(applySetServerConfig);
//        serverConfigListMessageHandler(_session, serverConfigListStatusMsg,"CONFIGUPDATE", null, "CONFIGUPDATE");

        executeCommonsExecApplyConfig(session, commandLineShell, platform, ServerConfigMode.NONE, hqKey); // debug
        executeCommonsExecApplyConfig(session, commandLineReleasePlugman, platform, ServerConfigMode.NONE, hqKey); // relase
        executeCommonsExecApplyConfig(session, commandLineSetServerConfig, platform, ServerConfigMode.UPDATE, hqKey); // server config

//        serverConfigListMessageHandler(_session, serverConfigListStatusMsg,"DONE", null, "DONE");
    }

    /* plugin list cli executueApacheBuild method */
    private void executueApplyConfigCLI(String buildTaskId, String platform, String appVersionCode, String userProjectPath, String productType) throws Exception {
        log.info(" #### buildservice parameter check #### {} {}", platform, appVersionCode);

        // android pluginlist  수행 조건
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

                CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                commandLineGradleStop.addArgument("--stop");

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(userProjectPath); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                if(productType.toLowerCase().equals("wmatrix")){
                    commandLineShell.addArgument("setConfig");
                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument(":whybrid-plugins:setConfig");
                }

                commandLineShell.addArgument("-P");
                commandLineShell.addArgument("data=",false); // "AppVersion": ""
                // commandLineShell.addArgument("{\"AppVersionCode\":\""+appVersionCode+"\"}",false); // json 데이터 객체 대폭 수정해야함...
                commandLineShell.addArgument("{\"wmatrix\":{\"profiles\":{\""+profileType+"\":{\"appVersionCode\": \""+ appVersionCode +"\"}}}}",false); // json 데이터 객체 대폭 수정해야함...

                // {"wmatrix":{"profiles":{"onpremise":{"appVersionCode":54}}}}

                // gradle stop 수행 이후 -> gradle clean -> plugin list 조회 기능 수행
                // executeCommonsGradleDeamonStop(commandLineGradleStop);
                executeCommonsExecApplyConfig(null, commandLineShell, platform, ServerConfigMode.NONE, null);

            // ios plugin list 수행 조건
        } else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())) {
                // Windows DEV
                CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
                commandLine.addArgument("setInfo");
                commandLine.addArgument("common/version="+appVersionCode);

                executeCommonsExecApplyConfig(null, commandLine, platform, ServerConfigMode.UPDATE, null);
        }
    }

    private void executueApplyConfigCLI(String buildTaskId, String platform, String appVersion) throws Exception {
        log.info(" #### buildservice parameter check #### {} {}", platform, appVersion);

        if(platform.toLowerCase().equals(PayloadMsgType.windows.name())) {
            // Windows DEV
            CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
            commandLine.addArgument("setInfo");
            commandLine.addArgument("common/version="+appVersion);

            executeCommonsExecApplyConfig(null, commandLine, platform, ServerConfigMode.UPDATE, null);
        }
    }

    private void executeGetServerConfigCLI(WebSocketSession session, String platform, Map<String, Object> parseResult, String userProjectPath, String project_dir_path, String hqKey){

        String productType = parseResult.get("product_type").toString();

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            CommandLine commandLineGetServerConfigCLI = CommandLine.parse(shellscriptFileName);
            commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + project_dir_path);
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI.addArgument(":wmatrix-plugins:getConfig");
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI.addArgument(":whybrid-plugins:getConfig");
            }

            try {
                executeCommonsExecGetServerConfig(session, commandLineGetServerConfigCLI, platform, hqKey);

            } catch (Exception e) {

            }

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            CommandLine commandLineGetServerConfigCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI = CommandLine.parse(wmatrixmanager);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
            }

            if(commandLineGetServerConfigCLI != null){
                commandLineGetServerConfigCLI.addArgument("getserverconfig");
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + project_dir_path);
            }

            try {
                executeCommonsExecGetServerConfig(session, commandLineGetServerConfigCLI, platform, hqKey);

            } catch (Exception e) {

            }
        }
    }

    private void executeGetMultiProfileAppConfigCLI(WebSocketSession session, Map<String, Object> parseResult){

        String userProjectPath = "";

        String productType = parseResult.get("product_type").toString();
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

        userProjectPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            CommandLine commandLineGetServerConfigCLI = CommandLine.parse(shellscriptFileName);
            commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + projectPath);
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI.addArgument("getConfigAll");
                commandLineGetServerConfigCLI.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);
            }else if(productType.toLowerCase().equals("whybrid")){
                // commandLineGetServerConfigCLI.addArgument(":whybrid-plugins:getConfig");
            }

            try {
                executeGetMultiProfileAppConfigList(session, commandLineGetServerConfigCLI, platform, hqKey);
            } catch (Exception e) {

            }

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            CommandLine commandLineGetServerConfigCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI = CommandLine.parse(wmatrixmanager);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
            }

            if(commandLineGetServerConfigCLI != null){
                commandLineGetServerConfigCLI.addArgument("getconfig");
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + projectPath);
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSWMatrxiPluginPath);
            }

            try {
                executeGetMultiProfileAppConfigList(session, commandLineGetServerConfigCLI, platform, hqKey);
            } catch (Exception e) {

            }
        }
    }

    private JSONObject executeGetMultiDomainAppConfig(WebSocketSession session, Map<String, Object> parseResult) {
        String userProjectPath = "";

        String productType = parseResult.get("product_type").toString();
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

        userProjectPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            CommandLine commandLineGetServerConfigCLI = CommandLine.parse(shellscriptFileName);
            commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + projectPath);
            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI.addArgument("getConfigAll");
                commandLineGetServerConfigCLI.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);
            }

            try {
                return executeGetMultiDomainAppConfigList(session, commandLineGetServerConfigCLI, platform, hqKey);
            } catch (Exception e) {
                log.error("executeGetMultiDomainAppConfigCLI Error = {}", e.getMessage(), e);
            }
        }

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())) {

            CommandLine commandLineGetServerConfigCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI = CommandLine.parse(wmatrixmanager);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
            }

            if(commandLineGetServerConfigCLI != null){
                commandLineGetServerConfigCLI.addArgument("getconfig");
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + projectPath);
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSWMatrxiPluginPath);
            }

            try {
                return executeGetMultiDomainAppConfigList(session, commandLineGetServerConfigCLI, platform, hqKey);
            } catch (Exception e) {
                log.error("executeGetMultiDomainAppConfigCLI Error = {}", e.getMessage(), e);
            }
        }
        return null;
    }

    @Async("asyncThreadPool")
    private JSONObject executeGetBuildCodeAppConfigCLI(String platform, Map<String, Object> parseResult, String userProjectPath){
        JSONObject resultAppConfigObj = null;
        String productType = parseResult.get("product_type").toString();

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {

            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

            CommandLine commandLineGetServerConfigCLI = CommandLine.parse(shellscriptFileName);
            commandLineGetServerConfigCLI.addArgument(userProjectPath);
            if(productType.toLowerCase().equals("wmatrix")){
                // commandLineGetServerConfigCLI.addArgument(":wmatrix-plugins:getConfig"); // 28일 수정 해야함...
                commandLineGetServerConfigCLI.addArgument("getConfigAll"); // 28일 수정 해야함...
                commandLineGetServerConfigCLI.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);

            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI.addArgument(":whybrid-plugins:getConfig");

            }

            try {
                resultAppConfigObj = executeCommonsExecGetAllServerConfig(commandLineGetServerConfigCLI, platform);
            } catch (Exception e) {
                return null;
            }

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            CommandLine commandLineGetServerConfigCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI = CommandLine.parse(wmatrixmanager);
                commandLineGetServerConfigCLI.addArgument("getconfig");
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSWMatrxiPluginPath);

            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
                commandLineGetServerConfigCLI.addArgument("getinformation");
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSPluginPath);

            }

            if(commandLineGetServerConfigCLI != null){
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(userProjectPath);
            }

            try {
                resultAppConfigObj = executeCommonsExecGetBuildCodeInformationList(commandLineGetServerConfigCLI, platform);

            } catch (Exception e) {
                log.error("==============={}", e.getMessage(), e);
                return  null;
            }
        }
        return resultAppConfigObj;
    }

    // executeCommonsExecGetAllServerConfig
    // 현재 사용하지 않는 메소드
    private void executeGetAppConfigCLI(String platform, Map<String, Object> parseResult, String userProjectPath, String project_dir_path){

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            CommandLine commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
            commandLineGetServerConfigCLI.addArgument("getconfig");
            commandLineGetServerConfigCLI.addArgument("-p");
            commandLineGetServerConfigCLI.addArgument(systemUserHomePath + userProjectPath + "/" + project_dir_path);

            try {
                executeCommonsExecGetAppConfig(commandLineGetServerConfigCLI, platform);

            } catch (Exception e) {

            }
        }
    }

    private void executeGetInformationCLI(WebSocketSession session, String platform, Map<String, Object> parseResult, String userProjectPath, String project_dir_path){

        String productType = parseResult.get("product_type").toString();
        String hqKey = parseResult.get("hqKey").toString();

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            CommandLine commandLineGetServerConfigCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI = CommandLine.parse(wmatrixmanager);
                commandLineGetServerConfigCLI.addArgument("getconfig");
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + userProjectPath + "/" + project_dir_path, false);
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSWMatrxiPluginPath, false);
            }else if(productType.toLowerCase().equals("whybrid")){
                commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
                commandLineGetServerConfigCLI.addArgument("getinformation");
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSPluginPath);
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + userProjectPath + "/" + project_dir_path);
            }else {
                commandLineGetServerConfigCLI = CommandLine.parse("whmanager");
                commandLineGetServerConfigCLI.addArgument("getinformation");
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + userProjectPath + "/" + project_dir_path);
                commandLineGetServerConfigCLI.addArgument("-r");
                commandLineGetServerConfigCLI.addArgument(systemUserHomePath + iOSPluginPath);
            }

            try {
                executeCommonsExecGetInformationList(session, commandLineGetServerConfigCLI, platform, parseResult.get("XCodeProjectName").toString(), parseResult);

            } catch (Exception e) {

            }

        } else if(platform.toLowerCase().equals(PayloadMsgType.android.name())){


            String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
            CommandLine commandLineGetConfigCLI = CommandLine.parse(shellscriptFileName);

            if(productType.toLowerCase().equals("wmatrix")){

                commandLineGetConfigCLI.addArgument(systemUserHomePath + userProjectPath + "/"+ project_dir_path);
                commandLineGetConfigCLI.addArgument("getConfigAll"); // getconfig 로 수정 해야함.
                commandLineGetConfigCLI.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);
            }

            try {
                executeCommonsExecGetAllConfigList(session, commandLineGetConfigCLI, platform, hqKey, parseResult);

            } catch (Exception e) {
                log.info(e.getMessage(), e);
                throw new RuntimeException(e);
            }

        }


    }

    private String executeGetProjectPath(WebSocketSession session, String platform, Map<String, Object> parseResult, String userProjectPath, String project_dir_path){

        String productType = parseResult.get("product_type").toString();
        String resultProjectPath = "";

        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            CommandLine commandLineGetServerConfigCLI = null;

            if(productType.toLowerCase().equals("wmatrix")){
                commandLineGetServerConfigCLI = CommandLine.parse(wmatrixmanager);
                commandLineGetServerConfigCLI.addArgument("getprojectpath");
                commandLineGetServerConfigCLI.addArgument("-p");
                commandLineGetServerConfigCLI.addArgument(userProjectPath + "/" + project_dir_path);

            }else if(productType.toLowerCase().equals("whybrid")){


            }else {
                return resultProjectPath;

            }

            try {
                resultProjectPath = executeCommonsExecGetProjectPath(commandLineGetServerConfigCLI, platform, userProjectPath + "/" + project_dir_path + "/");
                return resultProjectPath;

            } catch (Exception e) {

                return resultProjectPath;
            }

        }else {
            return resultProjectPath;

        }

    }

    /* commons_exec 라이브러리 executeCommonsExecApplyConfig method   */
    public void executeCommonsExecApplyConfig(WebSocketSession session, CommandLine commandLineParse, String platform, ServerConfigMode serverMode, String hqKey) throws Exception {

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutResult = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";

        if(serverMode.equals(ServerConfigMode.CREATE)){
            projectCreateMessage(session, projectGitCloneMessage,"","CONFIG");
        }else if(serverMode.equals(ServerConfigMode.UPDATE)){
            serverConfigListMessageHandler(session, serverConfigListStatusMsg,"CONFIGUPDATE", null, "CONFIGUPDATE", hqKey);
        }

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle git app config CLI CommandLine log  data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                if(serverMode.equals(ServerConfigMode.UPDATE)){
                    serverConfigListMessageHandler(session, serverConfigListStatusMsg,"DONE", null, "DONE", hqKey);
                }

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    /* commons_exec 라이브러리 executeCommonsExecGetServerConfig method   */
    public void executeCommonsExecGetServerConfig(WebSocketSession session, CommandLine commandLineParse, String platform, String hqKey) throws Exception {

        ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";

        try {
            handler.start();

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();
            //TODO 수정
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray())));
            StringBuilder resultBuilder = new StringBuilder();
            char[] buffer = new char[8192]; // 적절한 버퍼 크기 설정
            int bytesRead;
            while ((bytesRead = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                resultBuilder.append(buffer, 0, bytesRead);
            }
            String result = resultBuilder.toString();
            result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // function,
                // "//!!--##"
                int idxStart = result.indexOf("//!!--##");
                int idxEnd = result.indexOf("##--!!//");

                String resultsub = "";
                if( idxStart > -1 && idxEnd > -1 ) {
                    resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                } else {
                    resultsub = result;
                }

                obj = parser.parse(resultsub);
                resultPluginObj = (JSONObject) obj;
                // "##--!!//"
                // installedPlugin
                // availablePlugin

            } else {
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;
            }


            int exitCode = resultHandler.getExitValue();


            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                serverConfigListMessageHandler(session, serverConfigListStatusMsg,"SEARCHING", resultPluginObj, PayloadMsgType.SUCCESSFUL.name(), hqKey);
            }else if(exitCode == 1){
                serverConfigListMessageHandler(session, serverConfigListStatusMsg,"SEARCHING", null, PayloadMsgType.FAILED.name(), hqKey);
            }else {
                serverConfigListMessageHandler(session, serverConfigListStatusMsg,"SEARCHING", null, PayloadMsgType.FAILED.name(), hqKey);
            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    public JSONObject executeGetMultiDomainAppConfigList(WebSocketSession session, CommandLine commandLineParse, String platform, String hqKey) throws Exception {

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

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

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                int idxStart = result.indexOf("//!!--##");
                int idxEnd = result.indexOf("##--!!//");

                String resultsub = "";
                if( idxStart > -1 && idxEnd > -1 ) {
                    resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                } else {
                    resultsub = result;
                }

                obj = parser.parse(resultsub);
                resultPluginObj = (JSONObject) obj;
            }

            if (platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;
            }

            int exitCode = resultHandler.getExitValue();
            handler.stop();

            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                return resultPluginObj;
            }
        } catch (Exception e) {
            log.error("executeGetMultiDomainAppConfigList Error = {}", e.getMessage(), e);
        }
        return null;
    }

    /* commons_exec 라이브러리 executeGetMultiProfileAppConfigList method  */
    public void executeGetMultiProfileAppConfigList(WebSocketSession session, CommandLine commandLineParse, String platform, String hqKey) throws Exception {

        MultiProfileAppConfigStatusMsg multiProfileAppConfigStatusMsg = new MultiProfileAppConfigStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        multiProfileAppConfigListMsgeHandler(session, multiProfileAppConfigStatusMsg,"", null, "SEARCHING", hqKey);

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
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // function,
                // "//!!--##"
                int idxStart = result.indexOf("//!!--##");
                int idxEnd = result.indexOf("##--!!//");

                String resultsub = "";
                if( idxStart > -1 && idxEnd > -1 ) {
                    resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                } else {
                    resultsub = result;
                }

                obj = parser.parse(resultsub);
                resultPluginObj = (JSONObject) obj;

                //
                /*JSONObject getConfig = (JSONObject) resultPluginObj.get("getConfig");
                JSONObject wmatrix = (JSONObject) getConfig.get("wmatrix");
                JSONObject profiles = (JSONObject) wmatrix.get("profiles"); */
                // "##--!!//"
                // installedPlugin
                // availablePlugin

            } else {
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;
            }


            int exitCode = resultHandler.getExitValue();


            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                multiProfileAppConfigListMsgeHandler(session, multiProfileAppConfigStatusMsg,"", resultPluginObj, PayloadMsgType.SUCCESSFUL.name(), hqKey);
            }else if(exitCode == 1){
                multiProfileAppConfigListMsgeHandler(session, multiProfileAppConfigStatusMsg,"", null, PayloadMsgType.FAILED.name(), hqKey);
            }else {
                multiProfileAppConfigListMsgeHandler(session, multiProfileAppConfigStatusMsg,"", null, PayloadMsgType.FAILED.name(), hqKey);
            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    /* commons_exec 라이브러리 executeCommonsExecGetAppConfig method   */
    public void executeCommonsExecGetAppConfig(CommandLine commandLineParse, String platform) throws Exception {

        AppConfigListStatusMsg appConfigListStatusMsg = new AppConfigListStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        appConfigListMessageHandler(_session, appConfigListStatusMsg,"SEARCHING", null, "SEARCHING");
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
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;
            }


            int exitCode = resultHandler.getExitValue();


            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                appConfigListMessageHandler(_session, appConfigListStatusMsg,"", resultPluginObj, PayloadMsgType.SUCCESSFUL.name());
            }else if(exitCode == 1){
                appConfigListMessageHandler(_session, appConfigListStatusMsg,"", null, PayloadMsgType.FAILED.name());
            }else {
                appConfigListMessageHandler(_session, appConfigListStatusMsg,"", null, PayloadMsgType.FAILED.name());
            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    /* commons_exec 라이브러리 executeCommonsExecGetAppConfig method   */
    public void executeCommonsExecGetInformationList(WebSocketSession session, CommandLine commandLineParse, String platform, String XCodeProjectName, Map<String, Object> parseResult) throws Exception {

        AppConfigListStatusMsg appConfigListStatusMsg = new AppConfigListStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        appConfigListStatusMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
        getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"SEARCHING", null, "SEARCHING");
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
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;
                resultPluginObj.put("XCodeProjectName",XCodeProjectName);
            }


            int exitCode = resultHandler.getExitValue();


            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", resultPluginObj, PayloadMsgType.SUCCESSFUL.name());
            }else if(exitCode == 1){
                getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", null, PayloadMsgType.FAILED.name());
            }else {
                getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", null, PayloadMsgType.FAILED.name());
            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    public JSONObject executeCommonsExecGetBuildCodeInformationList(CommandLine commandLineParse, String platform) throws Exception {

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"SEARCHING", null, "SEARCHING");
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
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;
            }


            int exitCode = resultHandler.getExitValue();
            handler.stop();

            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", resultPluginObj, "SUCCESSFUL");
                return resultPluginObj;
            }else if(exitCode == 1){
                // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", null, "FAILED");
                return null;
            }else {
                // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", null, "FAILED");
                return null;
            }

        } catch (IOException e) {

            return null;
        } catch (InterruptedException e) {

            return null;
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecGetServerConfig method   */
    public JSONObject executeCommonsExecGetAllServerConfig(CommandLine commandLineParse, String platform) throws Exception {

        ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";

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
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;
            JSONObject getConfig = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // function,
                // "//!!--##"
                int idxStart = result.indexOf("//!!--##");
                int idxEnd = result.indexOf("##--!!//");

                String resultsub = "";
                if( idxStart > -1 && idxEnd > -1 ) {
                    resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                } else {
                    resultsub = result;
                }

                obj = parser.parse(resultsub);
                resultPluginObj = (JSONObject) obj;

                getConfig = (JSONObject) resultPluginObj.get("getConfig");

                // "##--!!//"
                // installedPlugin
                // availablePlugin


            } else {
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);
                resultPluginObj = (JSONObject) obj;


            }


            int exitCode = resultHandler.getExitValue();

            handler.stop();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                return getConfig;
                //serverConfigListMessageHandler(_session, serverConfigListStatusMsg,"SEARCHING", resultPluginObj, "SUCCESSFUL");
            }else if(exitCode == 1){
                //serverConfigListMessageHandler(_session, serverConfigListStatusMsg,"SEARCHING", null, "FAILED");
                return null;
            }else {
                // serverConfigListMessageHandler(_session, serverConfigListStatusMsg,"SEARCHING", null, "FAILED");
                return null;
            }


        } catch (IOException e) {

            return null;
        } catch (InterruptedException e) {

            return null;
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecPluginList method   */
    public void executeCommonsExecGetAllConfigList(WebSocketSession session, CommandLine commandLineParse, String platform, String hqKey, Map<String, Object> parseResult) throws Exception {
        MultiProfileAppConfigStatusMsg multiProfileAppConfigStatusMsg = new MultiProfileAppConfigStatusMsg();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        multiProfileAppConfigStatusMsg.setHqKey(hqKey); // session headquarter key setting
        multiProfileAppConfigStatusMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
        try {
            handler.start();
            getAllConfigListMsgHandler(session, multiProfileAppConfigStatusMsg, null, null, "SEARCHING",hqKey);

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();
            LocalDateTime date = LocalDateTime.now();
            String buildAfterLogFile = logPath+platform+"_log"+date+".txt";
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
//            result = result.replaceAll(" ","");

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultPluginObj = null;

            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // function,
                // "//!!--##"
                int idxStart = result.indexOf("//!!--##");
                int idxEnd = result.indexOf("##--!!//");

                String resultsub = "";
                if( idxStart > -1 && idxEnd > -1 ) {
                    resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                } else {
                    resultsub = result;
                }

                obj = parser.parse(resultsub);

                resultPluginObj = (JSONObject) obj;
                // "##--!!//"
                // installedPlugin
                // availablePlugin

            } else {
                log.info(" input resultsub : {}", result);
                obj = parser.parse(result);

                resultPluginObj = (JSONObject) obj;
            }


            int exitCode = resultHandler.getExitValue();

            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                getAllConfigListMsgHandler(session, multiProfileAppConfigStatusMsg, "", resultPluginObj, PayloadMsgType.SUCCESSFUL.name(), hqKey);

            } else if(exitCode == 1){
                getAllConfigListMsgHandler(session, multiProfileAppConfigStatusMsg, "", null, PayloadMsgType.FAILED.name(), hqKey);

            } else {
                log.info(" exitCode : {}", exitCode);
                getAllConfigListMsgHandler(session, multiProfileAppConfigStatusMsg, "", null, PayloadMsgType.FAILED.name(), hqKey);

            }

            handler.stop();

        } catch (ParseException e){
            e.getStackTrace();
            getAllConfigListMsgHandler(session, multiProfileAppConfigStatusMsg, "", null, PayloadMsgType.FAILED.name(), hqKey);
            throw new Exception(e.getMessage(), e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    private String executeCommonsExecGetProjectPath(CommandLine commandLineParse, String platform, String userAllPath){

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        BufferedReader is = null;
        String tmp = "";
        // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"SEARCHING", null, "SEARCHING");
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
//            result = result.replaceAll(" ","");
            result = result.replaceAll(userAllPath, ""); // .xcodeproj 파일명 String 처리


            log.info(" input platform : {}", platform);
            log.info(" result PluginList : {}", result);
            log.info(" result userAllPath : {}", userAllPath);
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // log.info(" input platform : {}", platform);
                // result = result.replace(path,"");
                log.info(" input resultsub : {}", result);
            }


            int exitCode = resultHandler.getExitValue();
            handler.stop();

            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");
                // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", resultPluginObj, "SUCCESSFUL");
                return result;
            }else if(exitCode == 1){
                // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", null, "FAILED");
                return "";
            }else {
                // getiOSGetinformationMsgHandler(session, appConfigListStatusMsg,"", null, "FAILED");
                return "";
            }

        } catch (IOException e) {

            return null;
        } catch (InterruptedException e) {

            return null;
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecApplyConfig method   */
    public void executeCLIExecUpdateMultiProfileConfig(WebSocketSession session, CommandLine commandLineParse, String platform, ServerConfigMode serverMode, String hqKey) throws Exception {

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        ServerConfigListStatusMsg serverConfigListStatusMsg = new ServerConfigListStatusMsg();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutResult = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";

        if(serverMode.equals(ServerConfigMode.CREATE)){
            projectCreateMessage(session, projectGitCloneMessage,"","CONFIG");
        }else if(serverMode.equals(ServerConfigMode.UPDATE)){
            multiProfileConfigUpdateMessageHandler(session, serverConfigListStatusMsg,"CONFIGUPDATE", null, "CONFIGUPDATE");
        }

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle git app config CLI CommandLine log  data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                if(serverMode.equals(ServerConfigMode.UPDATE)){
                    multiProfileConfigUpdateMessageHandler(session, serverConfigListStatusMsg,"DONE", null, "DONE");
                }

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    /* websocket msg Type  */

    private void projectCreateMessage(WebSocketSession session, ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){

        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        projectGitCloneMessage.setHqKey(hqKey);

        if (gitStatus.equals("GITCLONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("SVNCHECKOUT")) {
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("CONFIG")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("DONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void serverConfigListMessageHandler(WebSocketSession session, ServerConfigListStatusMsg serverConfigListStatusMsg, String buildLog, JSONObject resultServerConfigObj, String messageValue, String hqKey){
        ObjectMapper Mapper = new ObjectMapper();

        serverConfigListStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            serverConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO.name()); /// msgtype 수정 하기
        }else if(messageValue.equals("CONFIGUPDATE") || messageValue.equals("DONE")){
            serverConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_SERVER_CONFIG_UPDATE_STATUS_INFO.name()); /// msgtype 수정 하기
        }

        serverConfigListStatusMsg.setStatus("serverconfig");
        serverConfigListStatusMsg.setMessage(messageValue);
        serverConfigListStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            serverConfigListStatusMsg.setResultServerConfigListObj(resultServerConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(serverConfigListStatusMsg, Map.class);
        log.info(parseResult.toString());
        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void appConfigListMessageHandler(WebSocketSession session, AppConfigListStatusMsg appConfigListStatusMsg, String buildLog, JSONObject resultServerConfigObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        appConfigListStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            appConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO.name());
        }

        appConfigListStatusMsg.setStatus("appconfig");
        appConfigListStatusMsg.setMessage(messageValue);
        appConfigListStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            appConfigListStatusMsg.setResultAppConfigListObj(resultServerConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(appConfigListStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void getiOSGetinformationMsgHandler(WebSocketSession session, AppConfigListStatusMsg appConfigListStatusMsg, String buildLog, JSONObject resultServerConfigObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        appConfigListStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            appConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO.name());
        }

        appConfigListStatusMsg.setStatus("appconfig");
        appConfigListStatusMsg.setMessage(messageValue);
        appConfigListStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            appConfigListStatusMsg.setResultAppConfigListObj(resultServerConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(appConfigListStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void multiProfileAppConfigListMsgeHandler(WebSocketSession session, MultiProfileAppConfigStatusMsg multiProfileAppConfigStatusMsg, String buildLog, JSONObject resultMultiProfileConfigObj, String messageValue, String hqKey){
        ObjectMapper Mapper = new ObjectMapper();

        multiProfileAppConfigStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            multiProfileAppConfigStatusMsg.setMsgType(BuildServiceType.HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO.name()); /// msgtype 수정 하기
        }

        multiProfileAppConfigStatusMsg.setStatus("multiprofileappconfig");
        multiProfileAppConfigStatusMsg.setMessage(messageValue);
        multiProfileAppConfigStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            multiProfileAppConfigStatusMsg.setResultMultiProfileConfigListObj(resultMultiProfileConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(multiProfileAppConfigStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    public void getAllConfigListMsgHandler(WebSocketSession session, MultiProfileAppConfigStatusMsg multiProfileAppConfigStatusMsg, String buildLog, JSONObject resultMultiProfileConfigObj, String messageValue, String hqKey){
        ObjectMapper Mapper = new ObjectMapper();

        multiProfileAppConfigStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            multiProfileAppConfigStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO.name()); /// msgtype 수정 하기
        }

        multiProfileAppConfigStatusMsg.setStatus("multiprofileappconfig");
        multiProfileAppConfigStatusMsg.setMessage(messageValue);
        multiProfileAppConfigStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            multiProfileAppConfigStatusMsg.setResultMultiProfileConfigListObj(resultMultiProfileConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(multiProfileAppConfigStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void multiProfileConfigUpdateMessageHandler(WebSocketSession session, ServerConfigListStatusMsg serverConfigListStatusMsg, String buildLog, JSONObject resultServerConfigObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        serverConfigListStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals("SEARCHING") || messageValue.equals(PayloadMsgType.FAILED.name())){
            serverConfigListStatusMsg.setMsgType(ProjectServiceType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO.name()); /// msgtype 수정 하기
        }else if(messageValue.equals("CONFIGUPDATE") || messageValue.equals("DONE")){
            serverConfigListStatusMsg.setMsgType(BuildServiceType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO.name()); /// msgtype 수정 하기
        }

        serverConfigListStatusMsg.setStatus("multiprofileconfig");
        serverConfigListStatusMsg.setMessage(messageValue);
        serverConfigListStatusMsg.setHqKey(hqKey);

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            serverConfigListStatusMsg.setResultServerConfigListObj(resultServerConfigObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(serverConfigListStatusMsg, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

}
