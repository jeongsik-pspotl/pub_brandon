package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.task.*;
import com.pspotl.sidebranden.builder.util.BranchRestTempleteUtil;
import com.pspotl.sidebranden.builder.util.FTPClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class BuildService extends BaseService {

    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private Map<String, String> taskList;
    private ReentrantReadWriteLock lock;
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Value("${whive.distribution.deployLogPath}")
    private String buildLogs;

    // Mac, Linux Root Path
    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    // Windows Log Path
    @Value("${whive.distribution.deployWindowsLogPath}")
    private String userWindowsRootPath;

    // Windows Root Path
    @Value("${whive.distribution.WindowsRootPath}")
    private String WindowsRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    private String domainPath;

    private String userPath;

    private String workspacePath;

    private String projectPath;

    private String iOSUnlockKeychainPassword;

    @Value("${whive.server.target}")
    private String headquaterUrl;

    // storeUrl
    @Value("${whive.ftpserver.storeUrl}")
    private String ftpStoreUrl;

    @Value("${spring.profiles}")
    private String springProfile;

    @Value("${whive.branch.id}")
    private String userId;

    @Value("${whive.branch.name}")
    private String branchName;

    @Value("${whive.branch.password}")
    private String builderPassword;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private int history_id;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    BranchRestTempleteUtil branchRestTempleteUtil;

    @Autowired
    private Executor asyncThreadPool;

    @Autowired
    private BuildTask buildTask;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    @Autowired
    private FTPClientUtil ftpClientUtil;

    @Autowired
    private UpdateProjectService updateProjectService;

    @Autowired
    private MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    private MultiProfileService multiProfileService;

    String appVersionStr = "";
    WebSocketSession sessionTemp;

    private String projectDirName = "";
    private String iOSProjectName = "";
    private boolean buildStatusYn = false;
    private boolean buildiOSStatusYn = false;
    private boolean buildWindowsStatusYn = false;
    private String aabAppName = "";

    // String 대신 추가 vo 객체로 받아서 처리하는 방식으로 구현해보기
    private static Map<WebSocketSession, String> websocketSessions = new ConcurrentHashMap<WebSocketSession, String>();

    private Map<String, Object> parseResultObj;

    private JSONObject resultURLObj = new JSONObject();
    private JSONObject ftpSettingObj = new JSONObject();
    private JSONObject buildServiceHistoryObj = new JSONObject();
    private JSONObject getVcsSettingObj = new JSONObject();
    private JSONObject buildConfigObj = new JSONObject();
    private JSONParser parser = new JSONParser();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());
    }

    public void addBuildProj(BuildRequest request, DeferredResult<BuildResponse> deferredResult) {
        log.info("## execute build project request. {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());
        if (request == null || deferredResult == null) {
            return;
        }

        try {
            lock.writeLock().lock();
            waitingQueue.put(request, deferredResult);

        } finally {
            lock.writeLock().unlock();
        }
    }

    public void cancelBuildProj(BuildRequest buildRequest) {
        try {
            lock.writeLock().lock();
            setJoinResult(waitingQueue.remove(buildRequest), new BuildResponse(BuildResponse.ResponseResult.CANCEL, null, buildRequest.getSessionId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void timeout(BuildRequest buildRequest) {
        try {
            lock.writeLock().lock();
            setJoinResult(waitingQueue.remove(buildRequest), new BuildResponse(BuildResponse.ResponseResult.TIMEOUT, null, buildRequest.getSessionId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Async("asyncThreadPool")
    public void startBuild(WebSocketSession session, String path, String projectName, int id, String platform, BuildMode mode, JSONObject buildFTPSettingObj, JSONObject buildHistoryObj, String appVersion) {
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
        try {
            log.debug("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();
            String buildToPath;
            history_id = id;
            projectPath = projectName;
            appVersionStr = appVersion;
            sessionTemp = session;
            // buildServiceHistoryObj = buildHistoryObj;
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                iOSProjectName = buildHistoryObj.get("iOSProjectName").toString();
            }else {
                iOSProjectName = "";
            }
            // 세션 관리 기능 추가
            // websocketSessions.put(session, buildServiceHistoryObj.get(PayloadMsgType.hqKey.name()).toString());

            if(buildWindowsStatusYn){
                // 빌드 진행중입니다. 빌드가 완료 된 이후 다시 진행해주세요.

                // buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"STOP", "빌드 진행중입니다. 빌드가 완료 된 이후 다시 진행해주세요.", null,null);
            }else {
                // 빌드 처음 실행시 빌드 cli 수행하기

                // Windows Path 하드 코딩
                buildToPath = "C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe";
                workspacePath = buildToPath; // 임시로 이동 ... 추후에는 동적 처리
                // ftpSettingObj = buildFTPSettingObj;

                executueWindowsBuild(uuid,buildToPath,platform,mode);
            }
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Async("asyncThreadPool")
    public void startBuild(WebSocketSession session, Map<String, Object> parseResult, String platform, BuildMode mode) {

        try {
            log.debug("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();
            String buildToPath;
            String hqKey;
            JSONObject buildConfigObj = new JSONObject();

            BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

            domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            projectDirName = parseResult.get("buildProjectdir").toString(); // ex 03_WHive_Presentation
            history_id = Integer.parseInt(parseResult.get("id").toString());

            String ftpSettingObj = parseResult.get("ftpSettingObj").toString();
            String repositoryObj = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
            String jsonBranchObj = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();

            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();

            JSONObject buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);
            JSONObject vcsSettingObj = (JSONObject) parser.parse(repositoryObj);
            JSONObject buildFTPSettingObj = (JSONObject) parser.parse(ftpSettingObj);

            hqKey = buildHistoryObj.get(PayloadMsgType.hqKey.name()).toString();
            String vcsType = vcsSettingObj.get("vcsType").toString();

            // buildServiceHistoryObj = buildHistoryObj;
            // getVcsSettingObj = vcsSettingObj;
            sessionTemp = session;

            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                iOSProjectName = buildHistoryObj.get("iOSProjectName").toString();
                iOSUnlockKeychainPassword = parseResult.get("ios_unlock_keychain_password").toString();
            }else {
                iOSProjectName = "";
                iOSUnlockKeychainPassword = "";
            }

            String uuid = UUID.randomUUID().toString();
            if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
                if(projectDirName.equals("")){
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;
                }else {
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/"+ projectPath + "/" + projectDirName;
                }
                log.info(" android session : {}",session);
                log.info(" android : {}",parseResult.toString());
                // git pull
                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"GITPULL", null, null,null, hqKey);

                if ("localgit".equals(vcsType)) {
                    gitTask.gitPull(parseResult, buildToPath, vcsSettingObj);
                } else if ("localsvn".equals(vcsType)) {
                    svnTask.svnUpdate(new URI(buildToPath));
                }

                // active profile set 처리하는 기능 추가하기 ...
                multiProfileService.setMultiProfile(session, parseResult);

                // app version code 조회 및 값 설정 적용
                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"READAPPCONFIG", null, null,null, hqKey);
                buildConfigObj = mobileTemplateConfigService.getBuildConfigAllCLI(session, parseResult, platform, buildToPath);

                String profileType = parseResult.get("profile_type").toString();
                JSONObject getWMatrix = (JSONObject) buildConfigObj.get("wmatrix");
                JSONObject getProfiles = (JSONObject) getWMatrix.get("profiles");
                JSONObject getProfileKey = (JSONObject) getProfiles.get(profileType);
                log.info(getProfileKey.toJSONString());

                String template_versionCodeStr = getProfileKey.get(PayloadMsgType.appVersionCode.name()).toString();
                int versionCodeInt = new Integer(template_versionCodeStr);
                versionCodeInt = versionCodeInt + 1;

                parseResult.replace(PayloadMsgType.appVersionCode.name(), versionCodeInt);
                parseResult.put("profileVal",springProfile);
                parseResult.put("builderUserID",userId);
                parseResult.put("builderUserName",branchName);
                parseResult.put("password",builderPassword);
                parseResult.put("multiProfileConfig",buildConfigObj);
                parseResult.put("profileType", profileType);
                parseResult.put("bucket",bucket);

                parseResultObj = parseResult;

                executueApacheBuild(session, uuid, buildToPath, platform, mode, parseResult);
            } else if (platform.toLowerCase().equals(PayloadMsgType.windows.name())) {
                buildToPath = userRootPath + "/" + workspacePath + "/" + projectPath + "/" + "WHybrid_Android";
                parseResult.put("profileVal",springProfile);

                // ftpSettingObj = buildFTPSettingObj;
                // buildServiceHistoryObj = buildHistoryObj;
                parseResultObj = parseResult;
                executueApacheBuild(session, uuid, buildToPath, platform, mode, parseResult);
            } else {

                if(projectDirName.equals("")){
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;
                }else {
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;
                }
                log.info(" ios session : {}",session);
                log.info(" ios : {}",parseResult.toString());
                log.info(" ios hqKey : {}",session);
                // git pull
                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"GITPULL", null, null,null, hqKey);

                if ("localgit".equals(vcsType)) {
                    gitTask.gitPull(parseResult, buildToPath, vcsSettingObj);
                } else if ("localsvn".equals(vcsType)) {
                    svnTask.svnUpdate(new URI(buildToPath));
                }

                // app version code 조회 및 값 설정 적용
                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"READAPPCONFIG", null, null,null, hqKey);
                buildConfigObj = mobileTemplateConfigService.getBuildConfigAllCLI(session, parseResult, platform, buildToPath);

                String profileType = parseResult.get("profile_type").toString();
                JSONObject getWMatrix = (JSONObject) buildConfigObj.get("wmatrix");
                JSONObject getProfiles = (JSONObject) getWMatrix.get("profiles");
                JSONObject getProfileKey = (JSONObject) getProfiles.get(profileType);
                log.info(getProfileKey.toJSONString());

                String template_versionCodeStr = getProfileKey.get(PayloadMsgType.appVersionCode.name()).toString();
                int versionCodeInt = new Integer(template_versionCodeStr);
                versionCodeInt = versionCodeInt + 1;

                parseResult.replace(PayloadMsgType.appVersionCode.name(), versionCodeInt);
                parseResult.put("profileVal",springProfile);
                parseResult.put("builderUserID",userId);
                parseResult.put("builderUserName",branchName);
                parseResult.put("password",builderPassword);
                parseResult.put("multiProfileConfig",buildConfigObj);
                parseResult.put("bucket",bucket);

                parseResultObj = parseResult;

                if ("localgit".equals(vcsType)) {
                    gitTask.gitPull(parseResult, buildToPath, vcsSettingObj);
                } else if ("localsvn".equals(vcsType)) {
                    svnTask.svnUpdate(new URI(buildToPath));
                }

                executueApacheBuild(session, uuid, buildToPath, platform, mode, parseResult);
            }
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    // Android aab build start method
    @Async("asyncThreadPool")
    public void startAabBuild(WebSocketSession session, Map<String, Object> parseResult, String platform, BuildMode mode, JSONObject buildFTPSettingObj, JSONObject buildHistoryObj, JSONObject vcsSettingObj, JSONObject configObj) {

        try {
            log.debug("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();
            String buildToPath;
            String hqKey;
            JSONObject buildConfigObj = new JSONObject();
            BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

            parseResultObj = parseResult;

            domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            projectDirName = parseResult.get("buildProjectdir").toString(); // ex 03_WHive_Presentation
            history_id = Integer.parseInt(parseResult.get("id").toString());
            appVersionStr = configObj.get(PayloadMsgType.AppVersion.name()).toString();
            aabAppName = configObj.get(PayloadMsgType.AppName.name()).toString();
            hqKey = buildHistoryObj.get(PayloadMsgType.hqKey.name()).toString();
            String vcsType = vcsSettingObj.get("vcsType").toString();
            // AppName
            // AppVersion

            JSONObject ftpSettingObj = buildFTPSettingObj;
            // buildServiceHistoryObj = buildHistoryObj;
            // getVcsSettingObj = vcsSettingObj;
            buildConfigObj = configObj;
            sessionTemp = session;

            // 세션 관리 기능 추가
            // websocketSessions.put(session, buildServiceHistoryObj.get(PayloadMsgType.hqKey.name()).toString());

            String uuid = UUID.randomUUID().toString();
            if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
                if(projectDirName.equals("")){
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;
                }else {
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/"+ projectPath + "/" + projectDirName;
                }

                // git pull
                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"GITPULL", null, null,null, hqKey);

                if ("localgit".equals(vcsType)) {
                    gitTask.gitPull(parseResult, buildToPath, vcsSettingObj);
                } else if ("localsvn".equals(vcsType)) {
                    svnTask.svnUpdate(new URI(buildToPath));
                }

                multiProfileService.setMultiProfile(session, parseResult);

                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"READAPPCONFIG", null, null,null, hqKey);
                buildConfigObj = mobileTemplateConfigService.getBuildConfigAllCLI(session, parseResult, platform, buildToPath);

                String profileType = parseResult.get("profile_type").toString();
                JSONObject getWMatrix = (JSONObject) buildConfigObj.get("wmatrix");
                JSONObject getProfiles = (JSONObject) getWMatrix.get("profiles");
                JSONObject getProfileKey = (JSONObject) getProfiles.get(profileType);

                // app version code 조회 및 값 설정 적용
//                buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"READAPPCONFIG", null, null,null);
//                buildConfigObj = mobileTemplateConfigService.getBuildConfigAllCLI(session, parseResult, platform, buildToPath);

                String template_versionCodeStr = getProfileKey.get(PayloadMsgType.appVersionCode.name()).toString();
                int versionCodeInt = new Integer(template_versionCodeStr);
                versionCodeInt = versionCodeInt + 1;

                parseResult.replace(PayloadMsgType.appVersionCode.name(), versionCodeInt);
                parseResult.put("profileVal",springProfile);
                parseResult.put("builderUserID",userId);
                parseResult.put("builderUserName",branchName);
                parseResult.put("password",builderPassword);
                parseResult.put("multiProfileConfig",buildConfigObj);
                parseResult.put("bucket",bucket);

                executueApacheBuild(session, uuid, buildToPath, platform, mode, parseResult);
            }
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue", e);
        } finally {

        }
    }

    @Async("asyncThreadPool")
    public void startiOSMultiProfileBuild(WebSocketSession session, Map<String, Object> parseResult, String platform) {

        try {
            log.debug("Current waiting Task : " + waitingQueue.size());
            String buildToPath;
            String projectNamePath = "";
            BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

            domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            projectDirName = parseResult.get("buildProjectdir").toString(); // ex 03_WHive_Presentation
            history_id = Integer.parseInt(parseResult.get("id").toString());
            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();
            String vcsSettingObjStr = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
            String ftpSettingStr = parseResult.get("ftpSettingObj").toString();

            JSONObject buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);
            JSONObject vcsSettingObj = (JSONObject) parser.parse(vcsSettingObjStr);
            String vcsType = vcsSettingObj.get("vcsType").toString();

            ftpSettingObj = null;
            buildServiceHistoryObj = null;
            getVcsSettingObj = vcsSettingObj;
            sessionTemp = session;

            String uuid = UUID.randomUUID().toString();
            if (platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
                buildServiceHistoryObj = buildHistoryObj;
                ftpSettingObj = (JSONObject) parser.parse(ftpSettingStr);
                if(projectDirName.equals("")){
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;
                    projectNamePath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;

                }else {
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;
                    projectNamePath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath;
                }

                if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    iOSProjectName = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platform, projectNamePath, projectPath); // buildHistoryObj.get("iOSProjectName").toString();
                    iOSUnlockKeychainPassword = parseResult.get("ios_unlock_keychain_password").toString();
                }else {
                    iOSProjectName = "";
                    iOSUnlockKeychainPassword = "";
                }

                // git pull
                // buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"GITPULL", null, null,null);
                if ("localgit".equals(vcsType)) {
                    gitTask.gitPull(parseResult, buildToPath, vcsSettingObj);
                } else if ("localsvn".equals(vcsType)) {
                    svnTask.svnUpdate(new URI(buildToPath));
                }

                // app version code 조회 및 값 설정 적용
                // buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"READAPPCONFIG", null, null,null, hqKey);
                buildConfigObj = mobileTemplateConfigService.getBuildConfigAllCLI(session, parseResult, platform, buildToPath);
                log.info(buildConfigObj.toJSONString());

                String profileType = parseResult.get("profile_type").toString();
                JSONObject getWMatrix = (JSONObject) buildConfigObj.get("targets");
                JSONObject getProfiles =  (JSONObject) getWMatrix.get(profileType);
                JSONObject getProfileKey = (JSONObject) getProfiles.get(parseResult.get("BuildType").toString());
                log.info(getProfileKey.toJSONString());

                String template_versionCodeStr = getProfileKey.get("versionCode").toString();
                int versionCodeInt = new Integer(template_versionCodeStr);
                versionCodeInt = versionCodeInt + 1;

                parseResult.put(PayloadMsgType.ApplicationID.name(), getProfileKey.get("applicationID").toString());
                parseResult.put(PayloadMsgType.AppName.name(), getProfileKey.get("appName").toString());
                parseResult.put(PayloadMsgType.AppVersion.name(), getProfileKey.get("version").toString());
                parseResult.put(PayloadMsgType.AppVersionCode.name(), versionCodeInt);
                parseResult.put("profileVal",springProfile);
                parseResult.put("builderUserID",userId);
                parseResult.put("builderUserName",branchName);
                parseResult.put("password",builderPassword);
                parseResult.put("multiProfileConfig",buildConfigObj);
                parseResult.put("iOSProjectName",iOSProjectName);
                parseResult.put("bucket",bucket);

                parseResultObj = parseResult;
                executueMultiProfilesiOSBuild(session, buildToPath, platform, parseResult);
            }
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue", e);
        }
    }

    @Async("asyncThreadPool")
    public void generalAppStartBuild(WebSocketSession session, Map<String, Object> parseResult, String platform, BuildMode buildMode) {
        try {
            lock.readLock().lock();
            BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

            domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            projectDirName = parseResult.get("buildProjectdir").toString();
            history_id = Integer.parseInt(parseResult.get("id").toString());

            String repositoryObj = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();
            JSONObject buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);
            JSONObject vcsSettingObj = (JSONObject) parser.parse(repositoryObj);

            String hqKey = buildHistoryObj.get(PayloadMsgType.hqKey.name()).toString();
            String buildToPath = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;

            buildMessageHandler(session, buildStatusMessage, PayloadMsgType.web_build.name(),"GITPULL", null, null,null, hqKey);
            gitTask.gitPull(parseResult, buildToPath, vcsSettingObj);

//            if(platform.equals(PayloadMsgType.ios.name())){
//                iOSProjectName = buildHistoryObj.get("iOSProjectName").toString();
//                iOSUnlockKeychainPassword = parseResult.get("ios_unlock_keychain_password").toString();
//            }

            parseResult.put("builderUserID", userId);
            parseResult.put("builderUserName",branchName);
            parseResult.put("password", builderPassword);
            parseResult.put("bucket",bucket);

            executueGeneralAppBuild(session, buildToPath, platform, buildMode, parseResult);

        } catch (Exception e) {
            log.error("General App Start Build Error = {}", e.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void sendMessage(String buildTaskId, BuildMessage buildMessage) {

    }

    public void connectTask(String buildTaskId, String websocketSessionId) {
        taskList.put(websocketSessionId, buildTaskId);
    }

    public void disconnectTask(String websocketSessionId) {
        String buildTaskId = taskList.get(websocketSessionId);
        BuildMessage buildMessage = new BuildMessage();

        buildMessage.setMessageType(MessageType.DISCONNECTED);
        sendMessage(buildTaskId, buildMessage);
    }

    private String getDestination(String buildTaskId) {
        return "/topic/build/" + buildTaskId;
    }

    private void setJoinResult(DeferredResult<BuildResponse> result, BuildResponse response) {
        if (result != null) {
            result.setResult(response);
        }
    }

    public void executueWindowsBuild(String buildTaskId, String path, String platform, BuildMode mode) throws Exception {
        log.info(" #### buildservice parameter check ####{} {}", platform, mode);
        if (platform.toLowerCase().equals(PayloadMsgType.windows.name())) {

            if (mode.equals(BuildMode.DEV)) {
                // Windows DEV
                CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
                commandLine.addArgument("makesetup");

                // 세션 관리 기능 추가
                // WebSocketSession _sessionTemp = getHqKeyToSession(buildServiceHistoryObj.get(PayloadMsgType.hqKey.name()).toString());

                // thread 객체 선언
                // getStartThread(_sessionTemp, commandLine, platform, null, null);

            }else if (mode.equals(BuildMode.TEST)) {
                // Windows TEST
                CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_test\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
                commandLine.addArgument("makesetup");

                // 세션 관리 기능 추가
                // WebSocketSession _sessionTemp = getHqKeyToSession(buildServiceHistoryObj.get(PayloadMsgType.hqKey.name()).toString());

                // thread 객체 선언
                // getStartThread(_sessionTemp, commandLine, platform, null, null);
            }
        }
    }

    /* android clean build cli executueApacheBuild method */
    public void executueApacheBuild(WebSocketSession session, String buildTaskId, String path, String platform, BuildMode mode, Map<String, Object> parseResultObj) throws Exception {
        log.info(" #### buildservice parameter check ####{} {}", platform, mode);
        String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
        String shellscriptAndroidFileName = getClassPathResourcePath("AndroidPlug.sh");

        // android build 수행 조건
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
//            String profileType = parseResultObj.get("profileType").toString();

            if (mode.equals(BuildMode.DEBUG)) {
                CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);  // gradlew 전환
                commandLine.addArgument(path);
                commandLine.addArgument("debugbuild");

                // new thread 생성
                getStartThread(session, commandLine, platform, mode, parseResultObj, path);

            } else if(mode.equals(BuildMode.RELEASE)) {

                CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);
                commandLine.addArgument(path);
                commandLine.addArgument("releasebuild");

                // new thread 생성
                getStartThread(session, commandLine, platform, mode, parseResultObj, path);

            } else if(mode.equals(BuildMode.AAB_DEBUG)) {

                CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);  // gradlew 전환
                commandLine.addArgument(path);
                commandLine.addArgument("makeDebugAab");
//                commandLine.addArgument(profileType);
//                commandLine.addArgument(aabAppName);
//                commandLine.addArgument(appVersionStr);

                // new thread 생성
                getStartThread(session, commandLine, platform, mode, parseResultObj, path);

            } else if(mode.equals(BuildMode.AAB_RELEASE)) {

                CommandLine commandLine = CommandLine.parse(shellscriptAndroidFileName);
                commandLine.addArgument(path);
                commandLine.addArgument("makeReleaseAab");
//                commandLine.addArgument(profileType);
//                commandLine.addArgument(aabAppName);
//                commandLine.addArgument(appVersionStr);

                // new thread 생성
                getStartThread(session, commandLine, platform, mode, parseResultObj, path);
            }

            // windows build 수행 조건
        } else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
            CommandLine commandLineWindowsDir = CommandLine.parse("dir");

            executeCommonsCleanBuild(commandLineWindowsDir);
            // send headquater

            // ios build 수행 조건
        } else {

            String jsonBuildHistoryVo = parseResultObj.get(PayloadMsgType.buildHistoryVo.name()).toString();
            JSONObject buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);

            String iOSProjectName = buildHistoryObj.get("iOSProjectName").toString();
            String iOSUnlockKeychainPassword = parseResultObj.get("ios_unlock_keychain_password").toString();

            if (mode.equals(BuildMode.DEBUG)) {
                CommandLine commandLine = CommandLine.parse(shellscriptFileName);
                commandLine.addArgument("archive");
                commandLine.addArgument(path);
                commandLine.addArgument("debug");
                commandLine.addArgument(iOSProjectName);
                commandLine.addArgument(iOSUnlockKeychainPassword);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[0]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[1]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[2]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[3]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[4]);
                // new thread 생성
                getStartThread(session, commandLine, platform, mode, parseResultObj, path);
            } else {
                CommandLine commandLine = CommandLine.parse(shellscriptFileName);
                commandLine.addArgument("archive");
                commandLine.addArgument(path);
                commandLine.addArgument("release");
                commandLine.addArgument(iOSProjectName);
                commandLine.addArgument(iOSUnlockKeychainPassword);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[0]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[1]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[2]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[3]);
                log.info((String) Arrays.stream(commandLine.getArguments()).toArray()[4]);
                // new thread 생성
                getStartThread(session, commandLine, platform, mode, parseResultObj, path);
            }
        }
    }

    public void executueMultiProfilesiOSBuild(WebSocketSession session, String path, String platform, Map<String, Object> parseResultObj) throws Exception {
        log.info(" #### buildservice parameter check ####{} {}", platform, parseResultObj);

        String setExportJsonString = "{\"projPath\":\""+path+"\",\"password\":\""+parseResultObj.get("ios_unlock_keychain_password").toString()+"\"," +
            "\"target\":\""+parseResultObj.get("profile_type").toString()+"\",\"exportMethod\":\""+parseResultObj.get("exportMethod").toString()+"\"," +
            "\"configuration\":\""+parseResultObj.get("BuildType").toString()+"\"}";

        log.info(setExportJsonString);
        CommandLine commandLine = CommandLine.parse("wmatrixmanager");
        commandLine.addArgument("archive");
        commandLine.addArgument("-j");
        commandLine.addArgument(setExportJsonString,false);
        // commandLine.addArgument(iOSProjectName);
        // commandLine.addArgument(iOSUnlockKeychainPassword);

        // new thread 생성
        if(parseResultObj.get("BuildType").toString().toLowerCase().equals("debug")){
            getStartThread(session, commandLine, platform, BuildMode.DEBUG, parseResultObj, path);

        }else if(parseResultObj.get("BuildType").toString().toLowerCase().equals("release")){
            getStartThread(session, commandLine, platform, BuildMode.RELEASE, parseResultObj, path);
        }
    }

    private void executueGeneralAppBuild(WebSocketSession session, String buildToPath, String platform, BuildMode buildMode, Map<String, Object> parseResult) {

        String cmd = "";
        String gradleJavaHome = "";
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            try {
                double gradleVersion = Double.parseDouble(getGradleVersion(buildToPath).substring(0, 3));
                gradleJavaHome = "-Dorg.gradle.java.home=";

                if (gradleVersion >= 8.0) {
                    gradleJavaHome += System.getProperty("GRADLE_JAVA_HOME17");
                } else if (gradleVersion >= 4.3) {
                    gradleJavaHome += System.getProperty("GRADLE_JAVA_HOME11");
                } else if (gradleVersion < 4.3) {
                    gradleJavaHome += System.getProperty("GRADLE_JAVA_HOME8");
                } else {
                    gradleJavaHome += System.getProperty("GRADLE_JAVA_HOME");
                }

            } catch (Exception e) {
                log.error("Get Gradle Version Error = {}", e.getMessage(), e);
            }

            cmd = "cd " + buildToPath + " && ./gradlew clean";
            switch (buildMode) {
                case DEBUG:
                    cmd += " && ./gradlew assembleDebug";
                    break;
                case RELEASE:
                    cmd += " && ./gradlew assembleRelease";
                    break;
                case AAB_DEBUG:
                    cmd += " && ./gradlew bundleDebug";
                    break;
                case AAB_RELEASE:
                    cmd += " && ./gradlew bundleRelease";
                    break;
            }
            cmd = cmd + " " + gradleJavaHome;

            CommandLine commandLine = CommandLine.parse("/bin/sh");
            commandLine.addArgument("-c");
            commandLine.addArgument(cmd, false);

            getStartGeneralAppBuildThread(session, commandLine, platform, buildMode, parseResult, buildToPath);
        } else if (platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
            Map<String, Object> iosAppConfig = (Map<String, Object>) parseResult.get("iosAppConfig");
            JSONObject iosBuildString = new JSONObject();
            iosBuildString.put("projPath", buildToPath);
            iosBuildString.put("password", parseResult.get("macPassword"));
            iosBuildString.put("target", iosAppConfig.get("scheme"));
            iosBuildString.put("exportMethod", iosAppConfig.get("exportMethod"));
            iosBuildString.put("configuration", iosAppConfig.get("buildType"));

            cmd = "rm -rf " + buildToPath + "/build && wmatrixmanager archive -j \"" + iosBuildString.toJSONString().replace("\"", "\\\"") + "\"";

            CommandLine commandLine = CommandLine.parse("/bin/sh");
            commandLine.addArgument("-c");
            commandLine.addArgument(cmd, false);

            getStartGeneralAppBuildThread(session, commandLine, platform, buildMode, parseResult, buildToPath);
        }
    }

    private void getStartGeneralAppBuildThread(WebSocketSession session, CommandLine commandLine, String platform, BuildMode buildMode, Map<String, Object> parseResult, String buildToPath) {
        try {
            String ftpSettingObj = parseResult.get("ftpSettingObj").toString();
            JSONObject buildFTPSettingObj = (JSONObject) parser.parse(ftpSettingObj);

            if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
                Thread buildStart = new Thread(new BuildGeneralAndroidProcess(session, commandLine,
                        systemUserHomePath + userRootPath, parseResult,
                         headquaterUrl, buildFTPSettingObj, buildMode));
                buildStatusYn = true;
                buildStart.start();
                buildStart.join();
                buildStatusYn = false;
            } else if (platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
                Thread buildStart = new Thread(new BuildGeneralIOSProcess(session, commandLine,
                        systemUserHomePath + userRootPath, parseResult,
                        headquaterUrl, buildFTPSettingObj, buildMode));
                buildStatusYn = true;
                buildStart.start();
                buildStart.join();
                buildStatusYn = false;
            }
        } catch (Exception e) {
            log.error("getStartGeneralAppBuildThread Error = {}", e.getMessage(), e);
        }
    }

    private String getGradleVersion(String buildToPath) {
        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        BufferedReader is = null;

        String cmd = "cd " + buildToPath + " && ./gradlew --version | grep -E 'Gradle [0-9.]*' | sed 's/Gradle //g'";
        CommandLine commandLine = CommandLine.parse("/bin/sh");
        commandLine.addArgument("-c");
        commandLine.addArgument(cmd, false);

        String gradleVersion;

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            gradleVersion = is.readLine();

            resultHandler.waitFor();
            is.close();
            int exitCode = resultHandler.getExitValue();

            log.info(" exitCode : {}", exitCode);

            handler.stop();

        } catch (Exception e) {
            log.error("Get Gradle Version Error = {}", e.getMessage(), e);
            gradleVersion = "0";
        }
        return gradleVersion;
    }

    /* android gradle clean build 전용 method */
    public void executeCommonsCleanBuild(CommandLine commandLineParse) throws Exception {
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        BufferedReader is = null;

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            String tmp = null;

            while ((tmp = is.readLine()) != null)
            {
                // buildMessageHandler(null,buildStatusMessage, PayloadMsgType.web_build.name(),"CLEANBUILING", tmp, null,null);
                log.info(" #### Gradle Clean Build CommandLine log data ### : " + tmp);
            }
            resultHandler.waitFor();
            is.close();
            int exitCode = resultHandler.getExitValue();

            log.info(" exitCode : {}", exitCode);

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /* windows executeCommonsExecBuild method   */
    private void executeCommonsExecWindowsBuild(CommandLine commandLineParse, String path, String platform) throws Exception {
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        String logData = "";
        JSONObject resultLogfileObj = new JSONObject();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();

        // DataInputStream
        BufferedReader is = null;
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DaemonExecutor executor = new DaemonExecutor();

        try {
            handler.start();

            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-ddHHmmss"));

            String buildAfterLogFile = userWindowsRootPath+platform+"_log"+date+".txt";
            String logfileName = platform+"_log"+date+".txt";

            // log file download json data set
            resultLogfileObj.put("log_path",userWindowsRootPath);
            resultLogfileObj.put("logfile_name",logfileName);

            // logfile 생성
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
                log.info(" Buile Service PipedInputStream building log data : {}", logData);
                // buildMessageHandler(null,buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", logData, null, null);
                out.write(logData+"\n");
            }

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultBuildFileObj = null;

            String setResultBuildFilePath = null;
            handler.stop();
            resultHandler.waitFor();
            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            String result = "";
            if(exitCode == 0){

                // app file 스캔
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){ // Android apk file

                    result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
                    result = result.replaceAll(" ","");

                    int idxStart = result.indexOf("//!!--##");
                    int idxEnd = result.indexOf("##--!!//");
                    String resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                    // rest templete -> headquater send
                    obj = parser.parse(resultsub);
                    resultBuildFileObj = (JSONObject) obj;

                    setResultBuildFilePath = (String) resultBuildFileObj.get("platform_build_file_path");
                    log.info(" input resultBuildFileObj : {}", resultBuildFileObj);
                }else if (platform.toLowerCase().equals(PayloadMsgType.windows.name())){
                    resultBuildFileObj = new JSONObject();

                    resultBuildFileObj.put("platform_build_file_path",path);
                    setResultBuildFilePath = (String) resultBuildFileObj.get("platform_build_file_path");

                }else { // iOS ipa file
                    log.info(" start result value : {}", logData);
                    resultBuildFileObj = new JSONObject();
                    resultBuildFileObj.put("platform_build_file_path","/build/release");

                    setResultBuildFilePath = (String) resultBuildFileObj.get("platform_build_file_path");
                }

                if(platform.toLowerCase().equals(PayloadMsgType.android.name())) { // Android apk file
                    log.info(" exitCode : {}", exitCode);
                    // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    // build result -> chmod 권한 수정
                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("775");
                    commandLine.addArgument(path + setResultBuildFilePath);

                    executeCommonsChmod(commandLine);
                    // file obj Resttemplate

                }else if (platform.toLowerCase().equals(PayloadMsgType.windows.name())){

                    resultURLObj.put("qrCodeUrl", ftpStoreUrl + "app/"+ platform  + "/" + projectPath + "/"); // file name ...
                    resultURLObj.put("log_path", resultLogfileObj.get("log_path").toString());
                    resultURLObj.put("logfile_name", resultLogfileObj.get("logfile_name").toString());

                    // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    // build result -> chmod 권한 수정

                    // exe file download 구현
                    // path replace -> ""
                    String todayFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    String tempPath = path;
                    tempPath = tempPath.replace("\\cli\\W-MatrixCLI.exe","\\dist\\"+todayFormat);

                    // 해당 포멧에 맞춰서 exe file name 셋팅 하기
                    // W-Matrix + Setup + project name + opmode + version + todayFormat

                    // path + setResultBuildFilePath windows file path
                    buildAfterSendToHeadquaterFileObjWindows(tempPath, resultBuildFileObj, resultLogfileObj, platform);

                }else {
                    // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("775");
                    commandLine.addArgument(path + setResultBuildFilePath);

                    executeCommonsChmod(commandLine);
                }
            } else if(exitCode == 1){
                // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            } else {
                // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            }

            is.close();

        } catch (ParseException e){
            e.getStackTrace();
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }
    }

    /* android gradle clean build 전용 method */
    private int executeCommonsChmod(CommandLine commandLineParse) throws Exception {
        DefaultExecutor executor = new DefaultExecutor();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        executor.execute(commandLineParse, resultHandler);
        resultHandler.waitFor();

        int exitCode = resultHandler.getExitValue();
        return exitCode;
    }

    private void buildAfterSendToHeadquaterFileObjWindows(String buildPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, String platform){
        try (Stream<Path> filePathStream= Files.walk(Paths.get(buildPath))) {
            MultiValueMap<String, Object> reqToFileObj =
                new LinkedMultiValueMap<String, Object>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());
                log.info("buildAfterSendToHeadquaterFileObj filePath data : {} ", filePath);
                // appVersionStr
                if (fileNameToString.matches(".*"+appVersionStr+".*.exe")) {
                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);
                    Path path = Paths.get(buildPath + "/" + fileNameToString);
                    String name = fileNameToString;
                    reqToFileObj.add("filename", name);
                    reqToFileObj.add("filePath", buildPath);

                    resultBuildFileObj.put("platform_build_file_name",name);

                    byte[] content = new byte[1024];
                    Base64.Encoder encoder = Base64.getEncoder();

                    try {
                        content = Files.readAllBytes(path);
                        String encodedString =  encoder.encodeToString(content);
                        log.info("encodedString String add");
                        reqToFileObj.add("file", encodedString);

                        InputStream ins = Files.newInputStream(path);
                        buildAfterSendFTPClientObj(ins, name, platform, projectPath, resultBuildFileObj, buildAfterLogFile);
                    } catch (IOException e) {
                    }
                }
            });
        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                log.warn(exception.getMessage(), exception); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }
    }

    private void buildAfterSendFTPClientObj(InputStream ins, String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        try {
            boolean uploaCheck = ftpClientUtil.upload(ins, filename, platform, projectPath, null, null);
            // ftp 앱 파일 업로드 성공시 QRCode 생성 기능 수행
            if(uploaCheck){
                // QRCode 정상 처리 하면
                // restTemplate 객체로 -> 파일 전송...
                // FTP 파일 업로드중... 완료 되었을때 표시..ftpStoreUrl + projectPath + ".html"
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    // ftpStoreUrl -> headquater 에서 받아오는 걸로 수정
                    // resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "app/"+ platform  + "/" + projectPath + "/" + filename);
                    resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                    resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                    // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.SUCCESSFUL.name(), null, resultURLObj, resultBuildFileObj);
                } else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    // ftpStoreUrl + projectPath +".html"
                    // resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + projectPath +".html");
                    resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                    resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                    // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.SUCCESSFUL.name(), null, resultURLObj, resultBuildFileObj);
                } else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
                    // resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "app/"+ platform  + "/" + projectPath + "/" + filename);
                    resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                    resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                    // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.SUCCESSFUL.name(), null, resultURLObj, resultBuildFileObj, null);
                }
            }else {
                resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                // buildMessageHandler(null, buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultURLObj,null);
            }

            log.info(" buildAfterSendFTPClientObj uploaCheck : {}", uploaCheck);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    // [buildLogsValue, resultURLObj, resultBuildFileObj] json obj 묶기.
    // session 파리미터 추가하기
    private void buildMessageHandler(WebSocketSession session, BuildStatusMessage buildStatusMessage, String status, String messageValue, String buildLogsValue, JSONObject resultURLObj ,JSONObject resultBuildFileObj, String hqKey){
        ObjectMapper Mapper = new ObjectMapper();

        buildStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        buildStatusMessage.setMsgType(BuildServiceType.HV_MSG_BUILD_STATUS_INFO.name());
        buildStatusMessage.setStatus(status);
        buildStatusMessage.setMessage(messageValue);
        buildStatusMessage.setHqKey(hqKey);

        // 세션 관리 기능 추가
        WebSocketSession _sessionTemp = session;

        // build log message
        if(messageValue.equals("BUILDING") || messageValue.equals("CLEANBUILING") || messageValue.equals("STOP")){
            buildStatusMessage.setLogValue(buildLogsValue);
        }

        // build log file
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals(PayloadMsgType.FAILED.name())){
            buildStatusMessage.setHistory_id(history_id);
            buildStatusMessage.setLog_path(resultURLObj.get("log_path").toString());
            buildStatusMessage.setLogfile_name(resultURLObj.get("logfile_name").toString());
            // buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);
            buildStatusYn = false;
        }

        // build file obj, qrcode url(구조 변경)
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            buildStatusMessage.setBuildFileObj(resultBuildFileObj);
            buildStatusMessage.setQrCode(resultURLObj.get("qrCodeUrl").toString());
            // buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);
            buildStatusYn = false;
        }

        Map<String, Object> parseResult = Mapper.convertValue(buildStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private static WebSocketSession getHqKeyToSession(String hqKey) {
        WebSocketSession session = null;

        for(WebSocketSession _session : websocketSessions.keySet()) {

            log.info("check WebSocketSession session key {} ",  _session);
            String hqKeyTemp = websocketSessions.get(_session);
            if(hqKeyTemp == null) {
                continue;
            }else if(hqKeyTemp.equals(hqKey)){
                session = _session;
            }
        }
        return session;
    }

    private void getStartThread(WebSocketSession _sessionTemp, CommandLine commandLine, String platform, BuildMode mode, Map<String, Object> parseResultObj, String path) throws IOException {
        // new thread 생성
        try {
            JSONObject buildHistoryObj;
            JSONObject vcsSettingObj;
            JSONObject buildFTPSettingObj;

            String ftpSettingObj;
            String repositoryObj;
            String jsonBranchObj;
            String jsonBuildHistoryVo;

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

                ftpSettingObj = parseResultObj.get("ftpSettingObj").toString();
                repositoryObj = parseResultObj.get(PayloadMsgType.repositoryObj.name()).toString();
                jsonBranchObj = parseResultObj.get(PayloadMsgType.branchSettingObj.name()).toString();

                jsonBuildHistoryVo = parseResultObj.get(PayloadMsgType.buildHistoryVo.name()).toString();

                buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);
                vcsSettingObj = (JSONObject) parser.parse(repositoryObj);
                buildFTPSettingObj = (JSONObject) parser.parse(ftpSettingObj);

                Thread buildStart = new Thread(new BuildAndroidProcess(_sessionTemp, commandLine,
                    systemUserHomePath + userRootPath, parseResultObj,
                    buildLogs, headquaterUrl,
                    buildFTPSettingObj, buildHistoryObj, vcsSettingObj, mode));
                buildStatusYn = true;
                buildStart.start();

                buildStart.join();
                // 빌드 완료 이후 해당 session 객체 삭제

                buildStatusYn = false;
                // sessino remove

            }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

                ftpSettingObj = parseResultObj.get("ftpSettingObj").toString();
                repositoryObj = parseResultObj.get(PayloadMsgType.repositoryObj.name()).toString();
                jsonBranchObj = parseResultObj.get(PayloadMsgType.branchSettingObj.name()).toString();

                jsonBuildHistoryVo = parseResultObj.get(PayloadMsgType.buildHistoryVo.name()).toString();

                buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);
                vcsSettingObj = (JSONObject) parser.parse(repositoryObj);
                buildFTPSettingObj = (JSONObject) parser.parse(ftpSettingObj);

                if(!parseResultObj.get(PayloadMsgType.profile_type.name()).toString().equals("")){
                    Thread buildStart = new Thread(new BuildiOSProcess(_sessionTemp, commandLine,
                        systemUserHomePath + userRootPath, parseResultObj,
                        buildLogs, headquaterUrl,
                        buildFTPSettingObj, buildServiceHistoryObj, getVcsSettingObj, mode));
                    buildiOSStatusYn = true;
                    buildStart.start();

                    buildStart.join();
                    buildiOSStatusYn = false;
                }else {
                    Thread buildStart = new Thread(new BuildiOSProcess(_sessionTemp, commandLine,
                        systemUserHomePath + userRootPath, parseResultObj,
                        buildLogs, headquaterUrl,
                        buildFTPSettingObj, buildServiceHistoryObj, getVcsSettingObj, mode));
                    buildiOSStatusYn = true;
                    buildStart.start();

                    buildStart.join();
                    buildiOSStatusYn = false;
                    // sessino remove
                }
            }else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){

                ftpSettingObj = parseResultObj.get("ftpSettingObj").toString();
                repositoryObj = parseResultObj.get(PayloadMsgType.repositoryObj.name()).toString();
                jsonBranchObj = parseResultObj.get(PayloadMsgType.branchSettingObj.name()).toString();

                jsonBuildHistoryVo = parseResultObj.get(PayloadMsgType.buildHistoryVo.name()).toString();

                buildHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);
                vcsSettingObj = (JSONObject) parser.parse(repositoryObj);
                buildFTPSettingObj = (JSONObject) parser.parse(ftpSettingObj);

                if(!buildWindowsStatusYn){
                    Thread buildStart = new Thread(new BuildWindowsProcess(_sessionTemp, commandLine,
                        WindowsRootPath, workspacePath, projectPath, projectDirName, history_id,
                        userWindowsRootPath, headquaterUrl, appVersionStr,
                        buildFTPSettingObj, buildHistoryObj, vcsSettingObj));
                    buildWindowsStatusYn = true;
                    buildStart.start();

                    buildStart.join();
                    // 빌드 완료 이후 해당 session 객체 삭제
                    WebSocketSession sessionRemove = getHqKeyToSession(buildHistoryObj.get(PayloadMsgType.hqKey.name()).toString());
                    websocketSessions.remove(sessionRemove);
                    buildWindowsStatusYn = false;
                }
            }
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 임시 APK, ipa 파일을 삭제한다
        deleteTempArchive(path, parseResultObj);
    }

    /**
     * 빌드 후, 빌더서버에 APK, ipa 파일이 중복으로 생성되고 있어서, 기본 프로젝트에서 자동으로 생성하는 파일 삭제
     *
     * @param path 해당 프로젝트의 루트 디렉토리 경로
     * @param parseResultObj 빌드 시, 필요한 정보를 가지고 있는 객체, platform을 확인하기 위해 사용한다 (iOS, Android)
     * @throws IOException
     */
    private void deleteTempArchive(String path, Map<String, Object> parseResultObj) throws IOException {
        log.info("platform = {}", parseResultObj.get("platform"));
        String platform = parseResultObj.get("platform").toString().toLowerCase();
        List<File> directoryPath = new ArrayList<File>();
        List<String> androidPaths = Arrays.asList("/APK", "/AAB", "/app/build/outputs/apk", "/app/build/outputs/bundle");
        List<String> iosPaths = Arrays.asList("/build");

        try {
            switch (platform) {
                case "ios":
                    directoryPath = iosPaths.stream()
                        .map(f -> new File(path + f))
                        .collect(Collectors.toList());
                    break;
                case "android":
                    directoryPath = androidPaths.stream()
                        .map(f -> new File(path + f))
                        .collect(Collectors.toList());
                    break;
            }
            directoryPath.forEach(this::runDeleteTempArchive);
            directoryPath = null;
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    /**
     * 실제 임시 APK, ipa 파일을 삭제한다.
     *
     * @param f 실제 삭제할 디렉토리 path
     */
    private void runDeleteTempArchive(File f) {
        try {
            FileUtils.deleteDirectory(f);
        } catch (Exception e){
            log.debug(e.getLocalizedMessage());
        }
    }
}