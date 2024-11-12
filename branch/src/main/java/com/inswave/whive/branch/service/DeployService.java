package com.inswave.whive.branch.service;

import com.inswave.whive.branch.domain.*;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.task.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class DeployService extends BaseService {

    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private ReentrantReadWriteLock lock;

    private static Map<WebSocketSession, String> websocketSessions = new ConcurrentHashMap<WebSocketSession, String>();

    WebSocketSession sessionTemp;

    // Mac, Linux Root Path
    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${whive.distribution.deployLogPath}")
    private String buildLogs;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    ProjectGitPushService projectGitPushService;

    private Map<String, Object> parseResultObj = null;
    private JSONParser parser = new JSONParser();
    private String domainPath;
    private String userPath;
    private String workspacePath;
    private String projectPath;
    private String deployLogs = "";
    private String hqKeyStr = "";
    private String platform;

    private int history_id;

    private boolean deployStatusYn = false;
    private boolean deployiOSStatusYn = false;

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

    @Async("asyncThreadPool")
    public void startBuild(WebSocketSession session, Map<String, Object> parseResult, DeployMode mode) {

        try {
            log.debug("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();
            String buildToPath;

            parseResultObj = parseResult;

            platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            domainPath = parseResult.get(PayloadMsgType.domain_id.name()).toString();
            userPath = parseResult.get(PayloadMsgType.user_id.name()).toString();
            workspacePath = parseResult.get(PayloadMsgType.workspace_id.name()).toString();
            projectPath = parseResult.get(PayloadMsgType.project_id.name()).toString();
            // projectDirName = parseResult.get("").toString(); // ex 03_WHive_Presentation
            history_id = 1;
            hqKeyStr = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            deployLogs = systemUserHomePath + userRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + domainPath + "/" + BuilderDirectoryType.USER_ + userPath + "/"
                    + BuilderDirectoryType.WORKSPACE_W + workspacePath + "/" + BuilderDirectoryType.PROJECT_P + projectPath + "/build_logfiles/";

            sessionTemp = session;

            // 세션 관리 기능 추가
            websocketSessions.put(session, hqKeyStr);

            if(deployStatusYn){
                // 빌드 진행중입니다. 빌드가 완료 된 이후 다시 진행해주세요.

            }else {
                // 빌드 처음 실행시 빌드 cli 수행하기
                String uuid = UUID.randomUUID().toString();
                if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
                    // /Users/jeongsikkim/hybrid-android-poc/04_WHybrid_WebTop
                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + domainPath + "/" + BuilderDirectoryType.USER_ + userPath + "/"
                    + BuilderDirectoryType.WORKSPACE_W + workspacePath + "/" + BuilderDirectoryType.PROJECT_P + projectPath + "/" + BuilderDirectoryType.PROJECT_P + projectPath;

                    executueApacheDeploy(uuid, buildToPath, platform, mode);
                } else if (platform.toLowerCase().equals(PayloadMsgType.windows.name())) {
                    buildToPath = systemUserHomePath + userRootPath + "/" + workspacePath + "/" + projectPath + "/" + "WHybrid_Android";

                    executueApacheDeploy(uuid, buildToPath, platform, mode);
                } else {

                    buildToPath = systemUserHomePath + userRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + domainPath + "/" + BuilderDirectoryType.USER_ + userPath + "/"
                            + BuilderDirectoryType.WORKSPACE_W + workspacePath + "/" + BuilderDirectoryType.PROJECT_P + projectPath + "/" + BuilderDirectoryType.PROJECT_P + projectPath;

                    executueApacheDeploy(uuid, buildToPath, platform, mode);
                }

            }


        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void executueApacheDeploy(String buildTaskId, String path, String platform, DeployMode mode) {
        log.info(" #### buildservice parameter check ####{} {}", platform, mode);

        String shellscriptFileName = getClassPathResourcePath("fastlaneDeployCLI.sh");
        shellscriptFileName = shellscriptFileName.replace("/fastlaneDeployCLI.sh","");
        try {


        // android build 수행 조건
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(DeployMode.ALPHA)) {
                log.info(path);
                // fastlane 수행 로직
                CommandLine commandLine = CommandLine.parse(shellscriptFileName+"/fastlaneDeployCLI.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("android-alpha");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);

            } else if(mode.equals(DeployMode.BETA)) {

                CommandLine commandLine = CommandLine.parse(shellscriptFileName+"/fastlaneDeployCLI.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("android-beta");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);

            } else if(mode.equals(DeployMode.DEPLOY)) {

                CommandLine commandLine = CommandLine.parse(shellscriptFileName+"/fastlaneDeployCLI.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("android-deploy");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);

            } else {
                CommandLine commandLine = CommandLine.parse("gradle");
                commandLine.addArgument("-p");
                commandLine.addArgument(path);
                commandLine.addArgument(":app:releasebuild");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);
            }

            // ios build 수행 조건
        }  else {
            if (mode.equals(DeployMode.ALPHA)) {

                // ios 호출 받는 구간 수정
                CommandLine commandLine = CommandLine.parse(shellscriptFileName+"/fastlaneDeployCLI.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("ios-beta");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);

            } else if(mode.equals(DeployMode.BETA)){

                // ios 호출 받는 구간 수정
                CommandLine commandLine = CommandLine.parse(shellscriptFileName+"/fastlaneDeployCLI.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("ios-beta");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);

            } else if(mode.equals(DeployMode.DEPLOY)){

                // ios 호출 받는 구간 수정
                CommandLine commandLine = CommandLine.parse(shellscriptFileName+"/fastlaneDeployCLI.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("ios-deploy");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);

            } else {

                CommandLine commandLine = CommandLine.parse(path+"/archive.sh");
                commandLine.addArgument(path);
                commandLine.addArgument("release");

                // 세션 관리 기능 추가
                WebSocketSession _sessionTemp = getHqKeyToSession(hqKeyStr);

                // new thread 생성
                getStartThread(_sessionTemp, commandLine, platform);


            }
        }
        }catch (Exception e){
            log.info(e.getMessage(),e);
        }
    }

    private void getStartThread(WebSocketSession _sessionTemp, CommandLine commandLine, String platform){
        // new thread 생성
        try {
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

                if(!deployStatusYn){
                    Thread buildStart = new Thread(new DeployAndroidProcess(_sessionTemp, commandLine,
                            parseResultObj, history_id, hqKeyStr, deployLogs));
                    deployStatusYn = true;
                    buildStart.start();


                    buildStart.join();
                    // 빌드 완료 이후 해당 session 객체 삭제
                    WebSocketSession sessionRemove = getHqKeyToSession(hqKeyStr);
                    websocketSessions.remove(sessionRemove);
                    deployStatusYn = false;
                    // sessino remove

                }

            }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                if(!deployiOSStatusYn){
                    // deployiOSProcess 객체 생성해서 처리하기
                    Thread buildStart = new Thread(new DeployiOSProcess(_sessionTemp, commandLine,
                            parseResultObj, history_id, hqKeyStr, deployLogs));
                    deployiOSStatusYn = true;
                    buildStart.start();


                    buildStart.join();
                    // 빌드 완료 이후 해당 session 객체 삭제
                    WebSocketSession sessionRemove = getHqKeyToSession(hqKeyStr);
                    websocketSessions.remove(sessionRemove);
                    deployiOSStatusYn = false;
                }

            }


        } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
        }
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

}
