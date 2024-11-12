package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.*;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
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
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PluginService extends BaseService {

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
    private String androidWMatrixPluginPath;

    @Value("${whive.distribution.PluginPathiOS}")
    private String iOSPluginPath;

    @Value("${whive.distribution.WMatrixPluginPathiOS}")
    private String iOSWNatrixPluginPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;
    WebSocketSession sessionTemp;

    @Autowired
    ProjectSvnCommitService projectSvnCommitService;

    JSONObject JSONbranchSettingObj = null;
    JSONObject JSONbranchSettingobj = null;
    JSONParser parser = new JSONParser();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

    public void startWindowsPluginList(WebSocketSession session, Map<String, Object> parseResult, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            sessionTemp = session;

            executuePluginList(uuid, parseResult.get(PayloadMsgType.platform.name()).toString(), mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    // branchSettingObj, workspacePath, projectPath, pluginPlatform
    @Async("asyncThreadPool")
    public void startPluginList(WebSocketSession session, Map<String, Object> parseResult, PluginMode mode) {
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();
            sessionTemp = session;

            JSONbranchSettingobj = (JSONObject) parser.parse( parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

            executuePluginList(uuid, parseResult, parseResult.get(PayloadMsgType.platform.name()).toString(), parseResult.get("projectDirName").toString(),mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Async("asyncThreadPool")
    public void startPluginAdd(WebSocketSession session, Map<String, Object> parseResult, PluginMode mode){
        try {
            log.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();

            sessionTemp = session;
            JSONbranchSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());


            executuePluginAdd(uuid, parseResult, mode);
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /* plugin list cli executueApacheBuild method */
    private void executuePluginList(String buildTaskId, Map<String, Object> parseResult, String platform, String projectDirName, PluginMode mode) throws Exception {
        log.info(" #### buildservice parameter check ####{} {} {}", platform, mode, "");
        String userProjectPath = systemUserHomePath + userRootPath + "builder_main/"+ BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/" + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() + "/"+ BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString() + "/" +  BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString() +"/";
        String productType = parseResult.get("product_type").toString();

        // android pluginlist  수행 조건
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PULGIN_LIST)) {
                CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                commandLineGradleStop.addArgument("--stop");

                String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");

                shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                commandLineShell.addArgument(userProjectPath);
                if(productType.toLowerCase().equals("wmatrix")){
                    commandLineShell.addArgument(":wmatrix-plugins:GetInformation");
                    commandLineShell.addArgument("wmatrixPluginsDir="+systemUserHomePath + androidWMatrixPluginPath);
                }else if (productType.toLowerCase().equals("whybrid")){
                    commandLineShell.addArgument(":whybrid-plugins:GetInformation");
                    commandLineShell.addArgument("whybridPluginsDir="+systemUserHomePath + androidPluginPath);
                }


                // gradle stop 수행 이후 -> gradle clean -> plugin list 조회 기능 수행
                executeCommonsExecPluginList(sessionTemp, commandLineShell, "", platform);

            }
            // ios plugin list 수행 조건
        } else {
            if (mode.equals(PluginMode.PULGIN_LIST)) {

                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");
                CommandLine commandLinePlugman = null;


                if(productType.toLowerCase().equals("wmatrix")){
                    commandLinePlugman = CommandLine.parse("wmatrixmanager"); // path + shellscript
                    commandLinePlugman.addArgument("getinformation"); // getPluginInfo
                    commandLinePlugman.addArgument("-r");
                    commandLinePlugman.addArgument(systemUserHomePath + iOSWNatrixPluginPath);
                    commandLinePlugman.addArgument("-p");
                    commandLinePlugman.addArgument(userProjectPath); // branch path : userRootPath + workspaec + project + "/plugins"

                }else if(productType.toLowerCase().equals("whybrid")){
                    commandLinePlugman = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript
                    commandLinePlugman.addArgument("getinformation"); // getPluginInfo
                    commandLinePlugman.addArgument(systemUserHomePath + iOSPluginPath);
                    commandLinePlugman.addArgument(userProjectPath); // branch path : userRootPath + workspaec + project + "/plugins"

                }

                executeCommonsExecPluginList(sessionTemp, commandLinePlugman, "", platform);
            }

        }
    }

    /* plugin list windows cmd executueApacheBuild method */
    private void executuePluginList(String buildTaskId, String platform, PluginMode mode) throws Exception {
        log.info("start executuePluginList");
        if (platform.toLowerCase().equals(PayloadMsgType.windows.name())) {
            CommandLine commandLine = CommandLine.parse("C:\\W-Matrix\\rel_inswave_dev\\cli\\W-MatrixCLI.exe"); // 경로 설정 추가
            commandLine.addArgument("getinfo");

            executeCommonsExecPluginList(sessionTemp, commandLine, "C:\\W-Matrix\\rel_inswave_dev\\cli\\", platform);
        }
    }


    /* plugin add cli executueApacheBuild method */
    private void executuePluginAdd(String buildTaskId, Map<String, Object> parseResult, PluginMode mode) throws Exception {

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String moduleName = parseResult.get("moduleName").toString();
        String workspacePath = parseResult.get("workspacePath").toString();
        String projectPath = parseResult.get("projectPath").toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String commitMessage = parseResult.get("commitMessage").toString();


        log.info(" #### plugin service parameter check ####{} {} {}", platform, mode, moduleName);
        // 해당 구간 git push 구간 수정하기
        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PLUGIN_ADD)) {
                log.info(" #### plugin service in a mode check ####{} {} {}", platform, mode, moduleName);
                // repositoryVcsType
                if(JSONbranchSettingObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("git")){

                    CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                    commandLineGradleStop.addArgument("-p");
                    commandLineGradleStop.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName+"/");  //  03_WHive_Presentation
                    commandLineGradleStop.addArgument("--stop");

                    String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                    shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                    CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShell.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                    commandLineShell.addArgument(":whybrid-plugins:AddPlugin");

                    commandLineShell.addArgument("moduleName="+moduleName); // 3
                    commandLineShell.addArgument("vcsPush="+true); // 4
                    commandLineShell.addArgument("commitMessage="+commitMessage); // 5
                    commandLineShell.addArgument("destDir="+systemUserHomePath + userRootPath + workspacePath + "/" + projectPath); // 6
                    commandLineShell.addArgument("id="+JSONbranchSettingObj.get("repositoryId").toString()); // 7
                    commandLineShell.addArgument("pin="+JSONbranchSettingObj.get("repositoryPassword").toString()); // 8

                    CommandLine commandLineShellList = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShellList.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                    commandLineShellList.addArgument( ":whybrid-plugins:GetInformation");

                    // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                    executeCommonsExecPluginAdd(commandLineShell, moduleName, platform);
                    executeCommonsExecPluginList(sessionTemp, commandLineShellList, "", platform);
                }else if(JSONbranchSettingObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("svn")){

                    CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                    commandLineGradleStop.addArgument("-p");
                    commandLineGradleStop.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName+"/");  //  03_WHive_Presentation
                    commandLineGradleStop.addArgument("--stop");

                    String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                    shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                    CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShell.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                    commandLineShell.addArgument(":whybrid-plugins:AddPlugin");

                    commandLineShell.addArgument("moduleName="+moduleName); // 3

                    CommandLine commandLineSvnAdd = CommandLine.parse(shellscriptFileName+"/projectVCS.sh");
                    commandLineSvnAdd.addArgument("svn"); // 1
                    commandLineSvnAdd.addArgument("add"); // 2
                    commandLineSvnAdd.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath); // 3

                    CommandLine cliSvnCommitALL = CommandLine.parse(shellscriptFileName + "/projectVCS.sh");
                    cliSvnCommitALL.addArgument("svn"); // 1
                    cliSvnCommitALL.addArgument("commit"); // 2
                    cliSvnCommitALL.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath); // 3
                    cliSvnCommitALL.addArgument(moduleName + "isadded"); // 4

                    CommandLine commandLineShellList = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShellList.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/

                    commandLineShellList.addArgument( ":whybrid-plugins:GetInformation");

                    // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                    executeCommonsExecPluginAdd(commandLineShell, moduleName, platform);
                    // svn add commit 추가하기
                    executueVCSCommandALL(commandLineSvnAdd, "svn", "svnadd");
                    executueVCSCommandALL(cliSvnCommitALL, "svn", "svncommit");

                    executeCommonsExecPluginList(sessionTemp, commandLineShellList, "", platform);



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

            executeCommonsExecPluginAdd(commandLine, moduleName, platform);
            executeCommonsExecPluginAdd(commandLineUpdater, moduleName, platform); // updater
            // plignin list 실행 추가
            executeCommonsExecPluginList(sessionTemp, commandLinePluginList, "C:\\W-Matrix\\rel_inswave_dev\\cli\\", platform);
        } else {
            if (mode.equals(PluginMode.PLUGIN_ADD)) {
                log.info(" #### plugin service in a mode check ####{} {} {}", platform, mode, moduleName);

                // iOSPlugman.sh 호출 수정, addPlugin 추가
                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");

                CommandLine commandLinePlugmanAdd = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript

                commandLinePlugmanAdd.addArgument("addPlugin"); // addPlugin

                commandLinePlugmanAdd.addArgument(systemUserHomePath + userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName); // branch path : userRootPath + workspaec + 03_WHive_Presentation + "/plugins"
                commandLinePlugmanAdd.addArgument(moduleName);
                // add plugin 어차피 변경되어야 하니
                // 그리고 두번 작업 해야하는 현상이 발생될거 같으니
                //

                CommandLine commandLinePlugman = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript

                commandLinePlugman.addArgument("getPluginInfo"); // getPluginInfo

                commandLinePlugman.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName); // branch path : userRootPath + workspaec + 03_WHive_Presentation + "/plugins"

                executeCommonsExecPluginAdd(commandLinePlugmanAdd, moduleName, platform);
                executeCommonsExecPluginList(sessionTemp, commandLinePlugman, "", platform);
                // plignin list 실행
            }

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

            if(exitCode == 0){

            } else if(exitCode == 1){

            } else {

            }

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }
    }

    /* commons_exec 라이브러리 executeCommonsExecBuild method   */
    public void executeCommonsExecPluginList(WebSocketSession session, CommandLine commandLineParse, String path, String platform) throws Exception {
        PluginListStatusMessage pluginListStatusMessage = new PluginListStatusMessage();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        sessionTemp = session;
        pluginListStatusMessage.setHqKey(JSONbranchSettingobj.get(PayloadMsgType.hqKey.name()).toString()); // session headquarter key setting
        try {
            handler.start();
            pluginListMessageHandler(sessionTemp, pluginListStatusMessage, null, null, "SEARCHING");

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
                pluginListMessageHandler(sessionTemp, pluginListStatusMessage, buildAfterLogFile, resultPluginObj, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginListMessageHandler(sessionTemp, pluginListStatusMessage, buildAfterLogFile, null, PayloadMsgType.FAILED.name());

            } else {
                log.info(" exitCode : {}", exitCode);
                pluginListMessageHandler(sessionTemp, pluginListStatusMessage, buildAfterLogFile, null, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (ParseException e){
            e.getStackTrace();
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            pluginListMessageHandler(sessionTemp, pluginListStatusMessage, "", null, PayloadMsgType.FAILED.name());
            throw new Exception(e.getMessage(), e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

    /* commons_exec 라이브러리 executeCommonsExecBuild method   */
    private void executeCommonsExecPluginAdd(CommandLine commandLineParse, String moduleName, String platform) throws Exception {

        PluginAddListParam pluginAddListParam = new PluginAddListParam();
        pluginAddListParam.setHqKey(JSONbranchSettingObj.get(PayloadMsgType.hqKey.name()).toString());

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        try {
            handler.start();
            pluginAddMessageHandler(sessionTemp, pluginAddListParam, "ADDLOADING");

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
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

                log.info(" plugin add cli platform{} modulename {} result : {}",platform, moduleName, result);
            }else {

                log.info(" plugin add cli platform{} modulename {} result : {}",platform, moduleName, result);
            }

            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                pluginAddMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginAddMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.FAILED.name());

            } else {
                pluginAddMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }

    }

    private void executueVCSCommandALL(CommandLine commandLineParse, String VcsType, String commmandOrder){
        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";
        log.info(" #### Gradle git clone CLI CommandLine status ... ### : ");
        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            // vcs clone, checkout ... send message
            if (commmandOrder.toLowerCase().equals("gitadd")){

            }else if (commmandOrder.toLowerCase().equals("gitcommit")){

            }else if (commmandOrder.toLowerCase().equals("gitpush")){

            }else if (commmandOrder.toLowerCase().equals("svn")){

            }else if (commmandOrder.toLowerCase().equals("svnadd")){

            }else if (commmandOrder.toLowerCase().equals("svncommit")){

            }


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### VCS CLI CommandLine log  "+commmandOrder+" data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            handler.stop();

        } catch (IOException e) {

            log.error("builder plugin service error",e);
        } catch (InterruptedException e) {

            log.error("builder plugin service error",e);
        }

    }

    private void pluginListMessageHandler (WebSocketSession session, PluginListStatusMessage pluginListStatusMessage, String buildLog, JSONObject resultPluginObj, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        pluginListStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        pluginListStatusMessage.setMsgType(BuildServiceType.HV_MSG_PLUGIN_LIST_INFO.name());
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

    private void pluginAddMessageHandler (WebSocketSession session, PluginAddListParam pluginAddListParam, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        pluginAddListParam.setSessType(PayloadMsgType.HEADQUATER.name());
        pluginAddListParam.setMsgType(BuildServiceType.HV_MSG_PLUGIN_ADD_LIST_INFO.name());
        pluginAddListParam.setStatus("pluginadd");
        pluginAddListParam.setMessage(messageValue);

        Map<String, Object> parseResult = Mapper.convertValue(pluginAddListParam, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }


}
