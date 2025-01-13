package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.*;
import com.pspotl.sidebranden.builder.domain.*;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.task.GitTask;
import com.pspotl.sidebranden.builder.task.SvnTask;
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
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class TemplatePluginService extends BaseService {

    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private Map<String, String> taskList;
    private ReentrantReadWriteLock lock;

    @Value("${whive.distribution.deployLogPath}")
    private String logPath;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${whive.distribution.PluginPathAndroid}")
    private String androidPluginPath;

    @Value("${whive.distribution.WMatrixPluginPathAndroid}")
    private String WMatrixPluginPathAndroid;

    @Value("${whive.distribution.PluginPathiOS}")
    private String iOSPluginPath;

    @Value("${whive.distribution.WMatrixPluginPathiOS}")
    private String iOSMatrixPluginPath;

    @Value("${whive.distribution.wmatrixmanager}")
    private String wmatrixmanager;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;
    WebSocketSession sessionTemp;

    JSONObject JSONbranchSettingObj = null;
    JSONObject jsonPlugins = new JSONObject();
    JSONParser parser = new JSONParser();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

    @Async("asyncThreadPool")
    public void startTemplateAllPlugins(WebSocketSession session, Map<String, Object> parseResult){

        // module list 불러오기
        String moduleList = parseResult.get("moduleList").toString();
        moduleList = moduleList.replaceAll(" ","");
        moduleList = moduleList.replace("[{","");
        moduleList = moduleList.replace("}]","");
        log.info(moduleList);
        log.info(parseResult.toString());
        String[] items = moduleList.split("\\},\\{");
        log.info(String.valueOf(items.length));

        try {
            for(int i = 0; i < items.length; i++) {

                items[i] = "{" + items[i] + "}";
                items[i] = items[i].replaceAll("\\{","{\""); // replace 1
                items[i] = items[i].replaceAll("\\}","\"}"); // replace 2
                items[i] = items[i].replaceAll("=","\":\""); // replace 3
                items[i] = items[i].replaceAll(",","\",\""); // replace 4
                // available_plugin_module
                log.info(items[i]);

                jsonPlugins = (JSONObject) parser.parse(items[i].toString());


                log.info(jsonPlugins.toJSONString());
                if(i == items.length -1){
                    if(jsonPlugins.get("pluginMode").toString().toLowerCase().equals("add")){
                        startPluginAddAndVCSPush(session, parseResult, jsonPlugins, PluginMode.PLUGIN_ADD);
                        startPluginList(session, parseResult, PluginMode.PLUGIN_ADD);
                    }else if(jsonPlugins.get("pluginMode").toString().toLowerCase().equals("remove")){
                        startPluginRemoveVCSPush(session, parseResult, jsonPlugins, PluginMode.PLUGIN_REMOVE);
                        startPluginList(session, parseResult, PluginMode.PLUGIN_REMOVE);
                    }

                }else {
                    if(jsonPlugins.get("pluginMode").toString().toLowerCase().equals("add")){
                        startPluginAdd(session, parseResult, jsonPlugins, PluginMode.PLUGIN_ADD);
                    }else if(jsonPlugins.get("pluginMode").toString().toLowerCase().equals("remove")){
                        startPluginRemove(session, parseResult, jsonPlugins, PluginMode.PLUGIN_REMOVE);
                    }

                }

            }

        } catch (ParseException e) {

            log.error("builder template plugin msg handler error",e);
        }
    }

    @Async
    public void startPluginList(WebSocketSession session, Map<String, Object> parseResult, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
             lock.readLock().lock();

            sessionTemp = session;
            JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());


            executuePluginList(session, parseResult, mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
             lock.readLock().unlock();
        }

    }

    public void startPluginAdd(WebSocketSession session, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
             lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            sessionTemp = session;
            JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());


            executuePluginAdd(session, uuid, parseResult, jsonPlugins, mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {

        }
    }

    public void startPluginAddAndVCSPush(WebSocketSession session, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
             lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            sessionTemp = session;
            JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());


            executePluginAddAndVCSPush(session, uuid, parseResult, jsonPlugins, mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
             lock.readLock().unlock();
        }
    }

    public void startPluginRemove(WebSocketSession session, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());

            String uuid = UUID.randomUUID().toString();
            sessionTemp = session; // session 전역 저장

            JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

            executuePluginRemove(session, uuid, parseResult, jsonPlugins, mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {

        }
    }

    public void startPluginRemoveVCSPush(WebSocketSession session, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());

            String uuid = UUID.randomUUID().toString();
            sessionTemp = session; // session 전역 저장

            JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

            executePluginRemoveAndVCSPush(session, uuid, parseResult, jsonPlugins, mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {

        }
    }

    /* plugin list cli executueApacheBuild method */
    private void executuePluginList(WebSocketSession session, Map<String, Object> parseResult, PluginMode mode) throws Exception {
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String domainPath = BuilderDirectoryType.DOMAIN_+parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_+parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String productType = parseResult.get("product_type").toString();

        log.info(" #### buildservice parameter check ####{} {} {}", platform, mode);

        // android pluginlist  수행 조건
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
//            if (mode.equals(PluginMode.PULGIN_LIST)) {
                CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                commandLineGradleStop.addArgument("--stop");

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath +"/" + workspacePath + "/" + projectPath + "/"+ projectDirName); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                if(productType.toLowerCase().equals("wmatrix")){
                    // commandLineShell.addArgument( ":wmatrix-plugins:GetInformation"); // getconfig 로 수정 해야함.
                    commandLineShell.addArgument("getConfigAll"); // getconfig 로 수정 해야함.
                    commandLineShell.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);

                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument( ":whybrid-plugins:GetInformation");
                    commandLineShell.addArgument("whybridPluginsDir="+systemUserHomePath + androidPluginPath);

                }
                // gradle stop 수행 이후 -> gradle clean -> plugin list 조회 기능 수행
                if(mode.equals(PluginMode.PLUGIN_ADD) || mode.equals(PluginMode.PLUGIN_REMOVE)){
                    executeExecPluginModeToList(session, commandLineShell, platform, mode, parseResult);
                }else {
                    executeCommonsExecPluginList(session, commandLineShell, platform, parseResult);
                }
        } else {
                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");
                CommandLine commandLinePlugman = null; // path + shellscript

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugman = CommandLine.parse(wmatrixmanager); // path + shellscript
                    commandLinePlugman.addArgument("getconfig"); // getPluginInfo
                    commandLinePlugman.addArgument("-p");

                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLinePlugman = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript
                    commandLinePlugman.addArgument("getinformation"); // getPluginInfo
                    commandLinePlugman.addArgument(systemUserHomePath + iOSPluginPath);
                }

                if(commandLinePlugman != null){
                    if(projectDirName == null){
                        commandLinePlugman.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath +"/" +workspacePath + "/" + projectPath); // branch path : userRootPath + workspaec + project + "/plugins"
                    }else {
                        commandLinePlugman.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath +"/" +workspacePath + "/" + projectPath + "/"+projectDirName); // branch path : userRootPath + workspaec + project + "/plugins"
                    }
                }

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugman.addArgument("-r");
                    commandLinePlugman.addArgument(systemUserHomePath + iOSMatrixPluginPath);
                }
                executeCommonsExecPluginList(session, commandLinePlugman, platform, parseResult);
            }
    }

    /* plugin add cli executueApacheBuild method */
    private void executuePluginAdd(WebSocketSession session, String buildTaskId, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode) throws Exception {

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String moduleName = jsonPlugins.get("available_plugin_module").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_+parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_+parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String moduleVersion = jsonPlugins.get("available_plugin_version").toString();
        String productType = parseResult.get("product_type").toString();

        log.info(" #### plugin service parameter check ####{} {} {}", platform, mode, moduleName);
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PLUGIN_ADD)) {
                log.info(" #### plugin service in a mode check ####{} {} {}", platform, mode, moduleName);

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                moduleVersion = moduleVersion.replace("[","");
                moduleVersion = moduleVersion.replace("]","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath +"/" +workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                if(productType.toLowerCase().equals("wmatrix")){
                    // commandLineShell.addArgument(":wmatrix-plugins:AddPlugin");
                    commandLineShell.addArgument("addPlugin");
                    commandLineShell.addArgument("moduleName="+moduleName+"@"+moduleVersion); // 3
                    commandLineShell.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);
                } else if(productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument(":whybrid-plugins:AddPlugin");
                    commandLineShell.addArgument("moduleName="+moduleName+"@"+moduleVersion); // 3
                    commandLineShell.addArgument("whybridPluginsDir="+systemUserHomePath + androidPluginPath);
                }

                // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                executeCommonsExecPluginAdd(session, commandLineShell, moduleName, platform, parseResult);
            }

        } else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
            CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLine.addArgument("addplugin");
            commandLine.addArgument(moduleName);

            CommandLine commandLineUpdater = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLineUpdater.addArgument("addplugin");
            commandLineUpdater.addArgument("updater");

            CommandLine commandLinePluginList = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
            commandLinePluginList.addArgument("getinfo");

            executeCommonsExecPluginAdd(session, commandLine, moduleName, platform, parseResult);
            executeCommonsExecPluginAdd(session, commandLineUpdater, moduleName, platform, parseResult); // updater

        } else {
            if (mode.equals(PluginMode.PLUGIN_ADD)) {
                log.info(" #### plugin service in a mode check ####{} {} {}", platform, mode, moduleName);

                moduleVersion = moduleVersion.replace("[","");
                moduleVersion = moduleVersion.replace("]","");

                // iOSPlugman.sh 호출 수정, addPlugin 추가
                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");
                CommandLine commandLinePlugmanAdd = null;

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugmanAdd = CommandLine.parse(wmatrixmanager); // path + shellscript
                    commandLinePlugmanAdd.addArgument("addplugin"); // addPlugin
                    commandLinePlugmanAdd.addArgument("-n"); // addPlugin
                    commandLinePlugmanAdd.addArgument(moduleName);
                    commandLinePlugmanAdd.addArgument("-v"); // addPlugin
                    commandLinePlugmanAdd.addArgument(moduleVersion);
                    commandLinePlugmanAdd.addArgument("-r"); // addPlugin
                    commandLinePlugmanAdd.addArgument(systemUserHomePath + iOSMatrixPluginPath); // addPlugin
                    commandLinePlugmanAdd.addArgument("-p"); // addPlugin

                } else if(productType.toLowerCase().equals("whybrid")){
                    commandLinePlugmanAdd = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript
                    commandLinePlugmanAdd.addArgument("addplugin"); // addplugin
                    commandLinePlugmanAdd.addArgument(moduleName+"@"+moduleVersion);
                    commandLinePlugmanAdd.addArgument(systemUserHomePath + iOSPluginPath);
                }

                if(commandLinePlugmanAdd != null){
                    if(projectDirName == null){
                        commandLinePlugmanAdd.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath +"/" +workspacePath + "/" + projectPath); // branch path : userRootPath + workspaec + project + "/plugins"
                    }else {
                        commandLinePlugmanAdd.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath +"/" +workspacePath + "/" + projectPath + "/"+projectDirName); // branch path : userRootPath + workspaec + project + "/plugins"
                    }
                }

                executeCommonsExecPluginAdd(session, commandLinePlugmanAdd, moduleName, platform, parseResult);
                String vcsType = this.JSONbranchSettingObj.get("vcsType").toString();
                String localRepo = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectPath;

                try {
                    if (vcsType.contains("git")) {
                        gitTask.gitPush(parseResult, localRepo, this.JSONbranchSettingObj, "PluginSetting");
                    } else if (vcsType.contains("svn")) {
                        String repositoryID = this.JSONbranchSettingObj.get("repositoryId").toString();
                        String repositoryPassword = this.JSONbranchSettingObj.get("repositoryPassword").toString();

                        svnTask.svnAdd(new URI(localRepo));
                        svnTask.svnCommit(repositoryID, repositoryPassword, new URI(localRepo), "PluginSetting");
                    }
                } catch (Exception e) {
                    log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                }
            }
        }
    }

    public void executePluginAddAndVCSPush(WebSocketSession session, String buildTaskId, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode) throws Exception {
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String moduleName = jsonPlugins.get("available_plugin_module").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_+parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_+parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String moduleVersion = jsonPlugins.get("available_plugin_version").toString();
        String productType = parseResult.get("product_type").toString();

        log.info(" #### plugin service parameter check ####{} {} {}", platform, mode, moduleName);
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PLUGIN_ADD)) {
                log.info(" #### plugin service in a mode check ####{} {} {}", platform, mode, moduleName);
                CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                commandLineGradleStop.addArgument("-p");
                commandLineGradleStop.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath + "/"+projectDirName+"/");  //  03_WHive_Presentation
                commandLineGradleStop.addArgument("--stop");

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                moduleVersion = moduleVersion.replace("[","");
                moduleVersion = moduleVersion.replace("]","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                if(productType.toLowerCase().equals("wmatrix")){
                    // commandLineShell.addArgument(":wmatrix-plugins:AddPlugin");
                    commandLineShell.addArgument("addPlugin");
                    commandLineShell.addArgument("moduleName="+moduleName+"@"+moduleVersion); // 3
                    commandLineShell.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);

                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument(":whybrid-plugins:AddPlugin");
                    commandLineShell.addArgument("moduleName="+moduleName+"@"+moduleVersion); // 3
                    commandLineShell.addArgument("whybridPluginsDir="+systemUserHomePath + androidPluginPath);

                }

                // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                executeCommonsExecPluginAdd(session, commandLineShell, moduleName, platform, parseResult);
                String vcsType = this.JSONbranchSettingObj.get("vcsType").toString();
                String localRepo = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectPath;

                try {
                    if (vcsType.contains("git")) {
                        gitTask.gitPush(parseResult, localRepo, this.JSONbranchSettingObj, "PluginAddSet");
                    } else if (vcsType.contains("svn")) {
                        String repositoryID = this.JSONbranchSettingObj.get("repositoryId").toString();
                        String repositoryPassword = this.JSONbranchSettingObj.get("repositoryPassword").toString();

                        svnTask.svnAdd(new URI(localRepo));
                        svnTask.svnCommit(repositoryID, repositoryPassword, new URI(localRepo), "PluginAddSet");
                    }
                } catch (Exception e) {
                    log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                }
            }

        }else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
            CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLine.addArgument("addplugin");
            commandLine.addArgument(moduleName);

            CommandLine commandLineUpdater = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLineUpdater.addArgument("addplugin");
            commandLineUpdater.addArgument("updater");

            CommandLine commandLinePluginList = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
            commandLinePluginList.addArgument("getinfo");

            executeCommonsExecPluginAdd(session, commandLine, moduleName, platform, parseResult);
            executeCommonsExecPluginAdd(session, commandLineUpdater, moduleName, platform, parseResult); // updater

        } else {
            if (mode.equals(PluginMode.PLUGIN_ADD)) {
                log.info(" #### plugin service in a mode check ####{} {} {}", platform, mode, moduleName);

                // type 은 local git 이므로 git으로 고정 설정 했음.
                // id pin 은 일단 제외

                moduleVersion = moduleVersion.replace("[","");
                moduleVersion = moduleVersion.replace("]","");

                // iOSPlugman.sh 호출 수정, addPlugin 추가
                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");
                CommandLine commandLinePlugmanAdd = null;

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugmanAdd = CommandLine.parse(wmatrixmanager); // path + shellscript
                    commandLinePlugmanAdd.addArgument("addplugin"); // addPlugin
                    commandLinePlugmanAdd.addArgument("-n"); // addPlugin
                    commandLinePlugmanAdd.addArgument(moduleName);
                    commandLinePlugmanAdd.addArgument("-v"); // addPlugin
                    commandLinePlugmanAdd.addArgument(moduleVersion);
                    commandLinePlugmanAdd.addArgument("-r"); // addPlugin
                    commandLinePlugmanAdd.addArgument(systemUserHomePath + iOSMatrixPluginPath); // addPlugin
                    commandLinePlugmanAdd.addArgument("-p"); // addPlugin
                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLinePlugmanAdd = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript
                    commandLinePlugmanAdd.addArgument("addplugin"); // addPlugin
                    commandLinePlugmanAdd.addArgument(moduleName+"@"+moduleVersion);
                    commandLinePlugmanAdd.addArgument(systemUserHomePath + iOSPluginPath); // addPlugin
                }

                if(commandLinePlugmanAdd != null){
                    if(projectDirName == null){
                        commandLinePlugmanAdd.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath); // branch path : userRootPath + workspaec + project + "/plugins"
                    }else {
                        commandLinePlugmanAdd.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectDirName); // branch path : userRootPath + workspaec + project + "/plugins"
                    }
                }

                executeCommonsExecPluginAdd(session, commandLinePlugmanAdd, moduleName, platform, parseResult);

                String vcsType = this.JSONbranchSettingObj.get("vcsType").toString();
                String localRepo = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectPath;

                try {
                    if (vcsType.contains("git")) {
                        gitTask.gitPush(parseResult, localRepo, this.JSONbranchSettingObj, "PluginAddSet");
                    } else if (vcsType.contains("svn")) {
                        String repositoryID = this.JSONbranchSettingObj.get("repositoryId").toString();
                        String repositoryPassword = this.JSONbranchSettingObj.get("repositoryPassword").toString();

                        svnTask.svnAdd(new URI(localRepo));
                        svnTask.svnCommit(repositoryID, repositoryPassword, new URI(localRepo), "PluginAddSet");
                    }
                } catch (Exception e) {
                    log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                }
            }
        }
    }

    /* plugin remove cli executueApacheBuild method */
    public void executuePluginRemove(WebSocketSession session, String buildTaskId, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode) throws Exception {

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String moduleName = jsonPlugins.get("installed_plugin_module").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_+parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_+parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String productType = parseResult.get("product_type").toString();

        log.info(" #### plugin service parameter check ####{} {} {}", platform, mode, moduleName);

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PLUGIN_REMOVE)) {
                log.info(" #### plugin remove service in a mode check ####{} {} {}", platform, mode, moduleName);

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                if(productType.toLowerCase().equals("wmatrix")){
                    // commandLineShell.addArgument(":wmatrix-plugins:RemovePlugin");
                    commandLineShell.addArgument("removePlugin");
                    commandLineShell.addArgument("moduleName="+moduleName); // 3
                    commandLineShell.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);

                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument(":whybrid-plugins:RemovePlugin");
                    commandLineShell.addArgument("moduleName="+moduleName); // 3
                    commandLineShell.addArgument("whybridPluginsDir="+systemUserHomePath + androidPluginPath);

                }
                // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                executeCommonsExecPluginRemove(session, commandLineShell, moduleName, platform, parseResult);
            }

        }else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
            CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLine.addArgument("removeplugin");
            commandLine.addArgument(moduleName);

            CommandLine commandLineUpdater = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLineUpdater.addArgument("removeplugin");
            commandLineUpdater.addArgument("updater");

            CommandLine commandLinePluginList = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\wmatrixcli.exe"); // 경로 설정 추가
            commandLinePluginList.addArgument("getinfo");

            executeCommonsExecPluginRemove(session, commandLine, moduleName, platform, parseResult);
            executeCommonsExecPluginRemove(session, commandLineUpdater, moduleName, platform, parseResult); // updater

        }else {
            if (mode.equals(PluginMode.PLUGIN_REMOVE)) {
                log.info(" #### plugin remove service in a mode check ####{} {} {}", platform, mode, moduleName);

                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");
                CommandLine commandLinePlugmanRemove = null;

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugmanRemove = CommandLine.parse(wmatrixmanager); // path + shellscript
                    commandLinePlugmanRemove.addArgument("removeplugin"); // addPlugin
                    commandLinePlugmanRemove.addArgument("-n"); // addPlugin
                    commandLinePlugmanRemove.addArgument(moduleName);
                    commandLinePlugmanRemove.addArgument("-r"); // addPlugin
                    commandLinePlugmanRemove.addArgument(systemUserHomePath + iOSMatrixPluginPath); // addPlugin
                    commandLinePlugmanRemove.addArgument("-p"); // addPlugin
                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLinePlugmanRemove = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
                    commandLinePlugmanRemove.addArgument("removeplugin"); // addPlugin
                    commandLinePlugmanRemove.addArgument(moduleName);
                    commandLinePlugmanRemove.addArgument(systemUserHomePath + iOSPluginPath); // addPlugin
                }

                if(commandLinePlugmanRemove != null ){
                    if(projectDirName == null){
                        commandLinePlugmanRemove.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath); // branch path : userRootPath + workspaec + project + "/plugins"
                    }else {
                        commandLinePlugmanRemove.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectDirName); // branch path : userRootPath + workspaec + project + "/plugins"
                    }
                }

                //ios plugin remove 수행
                executeCommonsExecPluginRemove(session, commandLinePlugmanRemove, moduleName, platform, parseResult);

            }
        }

    }

    public void executePluginRemoveAndVCSPush(WebSocketSession session, String buildTaskId, Map<String, Object> parseResult, JSONObject jsonPlugins, PluginMode mode) throws Exception {
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String moduleName = jsonPlugins.get("installed_plugin_module").toString();
        String domainPath = BuilderDirectoryType.DOMAIN_+parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userPath = BuilderDirectoryType.USER_+parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String productType = parseResult.get("product_type").toString();

        log.info(" #### plugin service parameter check ####{} {} {}", platform, mode, moduleName);

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PLUGIN_REMOVE)) {
                log.info(" #### plugin remove service in a mode check ####{} {} {}", platform, mode, moduleName);
                CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                commandLineGradleStop.addArgument("-p");
                commandLineGradleStop.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath + "/"+ projectDirName +"/");
                commandLineGradleStop.addArgument("--stop");

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                if(productType.toLowerCase().equals("wmatrix")){
                    // commandLineShell.addArgument(":wmatrix-plugins:RemovePlugin");
                    commandLineShell.addArgument("removePlugin");
                    commandLineShell.addArgument("moduleName="+moduleName); // 3
                    commandLineShell.addArgument("wmatrixPluginsDir="+systemUserHomePath + WMatrixPluginPathAndroid);

                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument(":whybrid-plugins:RemovePlugin");
                    commandLineShell.addArgument("moduleName="+moduleName); // 3
                    commandLineShell.addArgument("whybridPluginsDir="+systemUserHomePath + androidPluginPath);

                }

                // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                executeCommonsExecPluginRemove(session, commandLineShell, moduleName, platform, parseResult);

                String vcsType = this.JSONbranchSettingObj.get("vcsType").toString();
                String localRepo = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectPath;

                try {
                    if (vcsType.contains("git")) {
                        gitTask.gitPush(parseResult, localRepo, this.JSONbranchSettingObj, "PluginRemoveSet");
                    } else if (vcsType.contains("svn")) {
                        String repositoryID = this.JSONbranchSettingObj.get("repositoryId").toString();
                        String repositoryPassword = this.JSONbranchSettingObj.get("repositoryPassword").toString();

                        svnTask.svnAdd(new URI(localRepo));
                        svnTask.svnCommit(repositoryID, repositoryPassword, new URI(localRepo), "PluginRemoveSet");
                    }
                } catch (Exception e) {
                    log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                }
            }

        } else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
            CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLine.addArgument("removeplugin");
            commandLine.addArgument(moduleName);

            CommandLine commandLineUpdater = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe");
            commandLineUpdater.addArgument("removeplugin");
            commandLineUpdater.addArgument("updater");

            CommandLine commandLinePluginList = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\wmatrixcli.exe"); // 경로 설정 추가
            commandLinePluginList.addArgument("getinfo");

            executeCommonsExecPluginRemove(session, commandLine, moduleName, platform, parseResult);
            executeCommonsExecPluginRemove(session, commandLineUpdater, moduleName, platform, parseResult); // updater

        } else {
            if (mode.equals(PluginMode.PLUGIN_REMOVE)) {
                log.info(" #### plugin remove service in a mode check ####{} {} {}", platform, mode, moduleName);

                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");
                CommandLine commandLinePlugmanRemove = null;

                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugmanRemove = CommandLine.parse(wmatrixmanager); // path + shellscript
                    commandLinePlugmanRemove.addArgument("removeplugin"); // addPlugin
                    commandLinePlugmanRemove.addArgument("-n"); // addPlugin
                    commandLinePlugmanRemove.addArgument(moduleName);
                    commandLinePlugmanRemove.addArgument("-r"); // addPlugin
                    commandLinePlugmanRemove.addArgument(systemUserHomePath + iOSMatrixPluginPath); // addPlugin
                    commandLinePlugmanRemove.addArgument("-p"); // addPlugin
                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLinePlugmanRemove = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
                    commandLinePlugmanRemove.addArgument("removeplugin"); // addPlugin
                    commandLinePlugmanRemove.addArgument(moduleName);
                    commandLinePlugmanRemove.addArgument(systemUserHomePath + iOSPluginPath); // addPlugin
                }

                if(commandLinePlugmanRemove != null){
                    if(projectDirName == null){
                        commandLinePlugmanRemove.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath); // branch path : userRootPath + workspaec + project + "/plugins"
                    }else {
                        commandLinePlugmanRemove.addArgument(systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" +workspacePath + "/" + projectPath + "/"+projectDirName); // branch path : userRootPath + workspaec + project + "/plugins"
                    }
                }

                //ios plugin remove 수행
                executeCommonsExecPluginRemove(session, commandLinePlugmanRemove, moduleName, platform, parseResult);

                String vcsType = this.JSONbranchSettingObj.get("vcsType").toString();
                String localRepo = systemUserHomePath + userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspacePath + "/" + projectPath + "/" + projectPath;

                try {
                    if (vcsType.contains("git")) {
                        gitTask.gitPush(parseResult, localRepo, this.JSONbranchSettingObj, "PluginRemoveSet");
                    } else if (vcsType.contains("svn")) {
                        String repositoryID = this.JSONbranchSettingObj.get("repositoryId").toString();
                        String repositoryPassword = this.JSONbranchSettingObj.get("repositoryPassword").toString();

                        svnTask.svnAdd(new URI(localRepo));
                        svnTask.svnCommit(repositoryID, repositoryPassword, new URI(localRepo), "PluginRemoveSet");
                    }
                } catch (Exception e) {
                    log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                }
            }
        }
    }

    /* commons_exec 라이브러리 executeCommonsExecBuild method  */
    private void executeCommonsExecPluginRemove(WebSocketSession session, CommandLine commandLineParse, String moduleName, String platform, Map<String, Object> parseResult) throws Exception {
        PluginAddListParam pluginAddListParam = new PluginAddListParam();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        JSONObject JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        pluginAddListParam.setHqKey(JSONbranchSettingObj.get(PayloadMsgType.hqKey.name()).toString());

        try {
            handler.start();
            pluginSendMessageHandler(sessionTemp, pluginAddListParam, "REMOVELOADING");

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

            log.info(" input platform : {}", platform);
            log.info(" plugin remove cli platform : {} moduleName : {} result : {}",platform, moduleName, result);

            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                //pluginSendMessageHandler(session, pluginAddListParam, PayloadMsgType.SUCCESSFUL.name());
                // pluginSendMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginSendMessageHandler(session, pluginAddListParam, PayloadMsgType.FAILED.name());

            } else {
                pluginSendMessageHandler(session, pluginAddListParam, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }

    }

    private void executeExecPluginModeToList(WebSocketSession session, CommandLine commandLineParse, String platform, PluginMode mode, Map<String, Object> parseResult) throws Exception {

        PluginListStatusMessage pluginListStatusMessage = new PluginListStatusMessage();

        if(mode.equals(PluginMode.PLUGIN_ADD)){
            pluginListStatusMessage.setMsgType(BuildServiceType.HV_MSG_PLUGIN_ADD_LIST_INFO.name());
        }else if(mode.equals(PluginMode.PLUGIN_REMOVE)){
            pluginListStatusMessage.setMsgType(BuildServiceType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name());
        }

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        pluginListStatusMessage.setHqKey(JSONbranchSettingObj.get(PayloadMsgType.hqKey.name()).toString()); // session headquarter key setting
        pluginListStatusMessage.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
        try {
            handler.start();
            pluginModeToListMsgHandler(session, pluginListStatusMessage, null, null, "SEARCHING");

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
            result = result.replaceAll(" ","");

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
                pluginModeToListMsgHandler(session, pluginListStatusMessage, buildAfterLogFile, resultPluginObj, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginModeToListMsgHandler(session, pluginListStatusMessage, buildAfterLogFile, null, PayloadMsgType.FAILED.name());

            } else {
                log.info(" exitCode : {}", exitCode);
                pluginModeToListMsgHandler(session, pluginListStatusMessage, buildAfterLogFile, null, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (ParseException e){
            e.getStackTrace();
            pluginModeToListMsgHandler(session, pluginListStatusMessage, "", null, PayloadMsgType.FAILED.name());
            throw new Exception(e.getMessage(), e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecPluginList method   */
    public void executeCommonsExecPluginList(WebSocketSession session, CommandLine commandLineParse, String platform, Map<String, Object> parseResult) throws Exception {
        PluginListStatusMessage pluginListStatusMessage = new PluginListStatusMessage();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        JSONObject JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        pluginListStatusMessage.setHqKey(JSONbranchSettingObj.get(PayloadMsgType.hqKey.name()).toString()); // session headquarter key setting
        pluginListStatusMessage.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
        try {
            handler.start();
            pluginListMessageHandler(session, pluginListStatusMessage, null, null, "SEARCHING");

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
            result = result.replaceAll(" ","");

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
                pluginListMessageHandler(session, pluginListStatusMessage, buildAfterLogFile, resultPluginObj, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginListMessageHandler(session, pluginListStatusMessage, buildAfterLogFile, null, PayloadMsgType.FAILED.name());

            } else {
                log.info(" exitCode : {}", exitCode);
                pluginListMessageHandler(session, pluginListStatusMessage, buildAfterLogFile, null, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (ParseException e){
            e.getStackTrace();
            pluginListMessageHandler(session, pluginListStatusMessage, "", null, PayloadMsgType.FAILED.name());
            throw new Exception(e.getMessage(), e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecPluginAdd method   */
    private void executeCommonsExecPluginAdd(WebSocketSession session, CommandLine commandLineParse, String moduleName, String platform, Map<String, Object> parseResult) throws Exception {

        PluginAddListParam pluginAddListParam = new PluginAddListParam();

        JSONObject JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());
        pluginAddListParam.setHqKey(JSONbranchSettingObj.get(PayloadMsgType.hqKey.name()).toString());

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        try {
            handler.start();
            pluginAddMessageHandler(session, pluginAddListParam, "ADDLOADING");

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


            log.info(" input platform : {}", platform);
            log.info(" plugin add cli platform{} modulename {} result : {}",platform, moduleName, result);

            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                // pluginAddMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginAddMessageHandler(session, pluginAddListParam, PayloadMsgType.FAILED.name());

            } else {
                pluginAddMessageHandler(session, pluginAddListParam, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }

    }

    /* android gradle deamon stop 명령어 호출, log file 생성 method */
    private void executeCommonsGradleDeamonStop(CommandLine commandLineParse) throws Exception {

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        try {
            handler.start();

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();
            LocalDateTime date = LocalDateTime.now();
            String buildAfterLogFile = logPath+"gradle_stop"+"_log"+date+".txt";

            Reader reader = new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray()));
            BufferedReader r = new BufferedReader(reader);
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            String tmp = null;

            while ((tmp = r.readLine()) != null)
            {

                out.write(tmp+"\n");
                log.info(" #### Gradle Deamon Stop CommandLine log data toString  ### : " + r.toString());
            }
            r.close();
            out.flush();
            int exitCode = resultHandler.getExitValue();

            log.info(" exitCode : {}", exitCode);

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }
    }

    private void pluginModeToListMsgHandler (WebSocketSession session, PluginListStatusMessage pluginListStatusMessage, String buildLog, JSONObject resultPluginObj, String messageValue) {

        ObjectMapper Mapper = new ObjectMapper();
        pluginListStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());

        pluginListStatusMessage.setStatus("pluginlist");
        pluginListStatusMessage.setMessage(messageValue);

        if(!messageValue.equals("SEARCHING")){
            pluginListStatusMessage.setPlatform_plugin_proj_path(buildLog);
        }

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            pluginListStatusMessage.setResultAppConfigListObj(resultPluginObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(pluginListStatusMessage, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);

    }

    private void pluginListMessageHandler (WebSocketSession session, PluginListStatusMessage pluginListStatusMessage, String buildLog, JSONObject resultPluginObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        pluginListStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        pluginListStatusMessage.setMsgType(BuildServiceType.HV_MSG_PLUGIN_LIST_INFO.name());
        pluginListStatusMessage.setStatus("pluginlist");
        pluginListStatusMessage.setMessage(messageValue);
//        pluginListStatusMessage.setBuild_id("");

        if(!messageValue.equals("SEARCHING")){
            pluginListStatusMessage.setPlatform_plugin_proj_path(buildLog);
        }

        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            pluginListStatusMessage.setResultAppConfigListObj(resultPluginObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(pluginListStatusMessage, Map.class);

        // tobe
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void pluginAddMessageHandler (WebSocketSession session, PluginAddListParam pluginAddListParam, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        pluginAddListParam.setSessType(PayloadMsgType.HEADQUATER.name());
        pluginAddListParam.setMsgType(BuildServiceType.HV_MSG_PLUGIN_ADD_LIST_INFO.name());
        pluginAddListParam.setStatus("pluginadd");
        pluginAddListParam.setMessage(messageValue);

        Map<String, Object> parseResult = Mapper.convertValue(pluginAddListParam, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void pluginSendMessageHandler(WebSocketSession session, PluginAddListParam pluginAddListParam, String messageValue) {
        ObjectMapper Mapper = new ObjectMapper();

        pluginAddListParam.setSessType(PayloadMsgType.HEADQUATER.name());
        pluginAddListParam.setMsgType(BuildServiceType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name());
        pluginAddListParam.setStatus("pluginremove");
        pluginAddListParam.setMessage(messageValue);

        Map<String, Object> parseResult = Mapper.convertValue(pluginAddListParam, Map.class);

        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}
