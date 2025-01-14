package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.*;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
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
public class PluginRemoveService extends BaseService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(PluginRemoveService.class);
    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private Map<String, String> taskList;
    private ReentrantReadWriteLock lock;

    @Value("${whive.distribution.deployLogPath}")
    private String rootPath;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    PluginService pluginService;

    @Autowired
    BuildService buildService;

    @Autowired
    ProjectSvnCommitService projectSvnCommitService;

    WebSocketSession sessionTemp;
    private JSONObject JSONPluginRemoveSettingObj = new JSONObject();
    private JSONObject JSONbranchSettingObj = new JSONObject();
    JSONParser parser= new JSONParser();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        logger.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());

    }
    // Map<String, Object> parseResult
    // String workspacePath, String projectPath, String platform, String branchSettingObj, PluginMode mode, String moduleName
    public void startPluginRemove(WebSocketSession session, Map<String, Object> parseResult, PluginMode mode) {
        try {
            logger.info("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();

            String uuid = UUID.randomUUID().toString();
            sessionTemp = session; // session 전역 저장

            JSONPluginRemoveSettingObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

            executuePluginRemove(uuid, parseResult, mode);
        } catch (Exception e) {
            logger.warn("Exception occur while checking waiting queue {}", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /* plugin remove cli executueApacheBuild method */
    public void executuePluginRemove(String buildTaskId, Map<String, Object> parseResult, PluginMode mode) throws Exception {

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String moduleName = parseResult.get("moduleName").toString();
        String workspacePath = parseResult.get("workspacePath").toString();
        String projectPath = parseResult.get("projectPath").toString();
        String projectDirName = parseResult.get("projectDirName").toString();
        String commitMessage = parseResult.get("commitMessage").toString();

        logger.info(" #### plugin service parameter check ####{} {} {}", platform, mode, moduleName);

        if (platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            if (mode.equals(PluginMode.PLUGIN_REMOVE)) {
                if(JSONPluginRemoveSettingObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("git")){
                    logger.info(" #### plugin remove service in a mode check ####{} {} {}", platform, mode, moduleName);
                    CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                    commandLineGradleStop.addArgument("-p");
                    commandLineGradleStop.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName +"/");
                    commandLineGradleStop.addArgument("--stop");

                    String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                    shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                    CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShell.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                    commandLineShell.addArgument(":whybrid-plugins:RemovePlugin");
                    commandLineShell.addArgument("moduleName="+moduleName); // 3
                    commandLineShell.addArgument("vcsPush="+true); // 4
                    commandLineShell.addArgument("commitMessage="+commitMessage); //5
                    commandLineShell.addArgument("destDir="+userRootPath + workspacePath + "/" + projectPath); // 6
                    commandLineShell.addArgument("id="+JSONPluginRemoveSettingObj.get("repositoryId").toString()); // 7
                    commandLineShell.addArgument("pin="+JSONPluginRemoveSettingObj.get("repositoryPassword").toString()); // 8

                    CommandLine commandLineShellList = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShellList.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                    commandLineShellList.addArgument( ":whybrid-plugins:GetInformation");
                    // commandLineShell.addArgument("whybridPluginsDir="+androidPluginPath);

                    // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                    // executeCommonsGradleDeamonStop(commandLineGradleStop);
                    executeCommonsExecPluginRemove(commandLineShell, moduleName, platform);
                    pluginService.executeCommonsExecPluginList(sessionTemp, commandLineShellList, "", platform);

                }else if(JSONPluginRemoveSettingObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("svn")){
                    CommandLine commandLineGradleStop = CommandLine.parse("gradle");
                    commandLineGradleStop.addArgument("-p");
                    commandLineGradleStop.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName +"/");
                    commandLineGradleStop.addArgument("--stop");

                    String shellscriptFileName = getClassPathResourcePath("AndroidPlug.sh");
                    shellscriptFileName = shellscriptFileName.replace("/AndroidPlug.sh","");

                    CommandLine commandLineShell = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShell.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName +"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                    commandLineShell.addArgument(":whybrid-plugins:RemovePlugin");
                    commandLineShell.addArgument("moduleName="+moduleName); // 3

                    CommandLine commandLineSvnAdd = CommandLine.parse(shellscriptFileName+"/projectVCS.sh");
                    commandLineSvnAdd.addArgument("svn"); // 1
                    commandLineSvnAdd.addArgument("add"); // 2
                    commandLineSvnAdd.addArgument(userRootPath + workspacePath + "/" + projectPath); // 3

                    CommandLine cliSvnCommitALL = CommandLine.parse(shellscriptFileName + "/projectVCS.sh");
                    cliSvnCommitALL.addArgument("svn"); // 1
                    cliSvnCommitALL.addArgument("commit"); // 2
                    cliSvnCommitALL.addArgument(userRootPath + workspacePath + "/" + projectPath); // 3
                    cliSvnCommitALL.addArgument(moduleName + "isremoved"); // 4

                    CommandLine commandLineShellList = CommandLine.parse(shellscriptFileName +"/AndroidPlug.sh");
                    commandLineShellList.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+projectDirName+"/"); // userRootPath + workspacePath + "/" + projectPath + "/03_WHive_Presentation/
                    commandLineShellList.addArgument( ":whybrid-plugins:GetInformation");
                    // commandLineShell.addArgument("whybridPluginsDir="+androidPluginPath);

                    // gradle stop 수행 이후 -> gradle stop -> plugin add 기능 수행
                    // executeCommonsGradleDeamonStop(commandLineGradleStop);
                    executeCommonsExecPluginRemove(commandLineShell, moduleName, platform);
                    // svn add commit service 실행
                    executueVCSCommandALL(commandLineSvnAdd, "svn", "svnadd");
                    executueVCSCommandALL(cliSvnCommitALL, "svn", "svncommit");

                    // projectSvnCommitService.projectSvnCommitAction(sessionTemp, platform, userRootPath + workspacePath + "/" + projectPath + "/", "", projectDirName, moduleName + " module is removed", JSONPluginRemoveSettingObj);
                    pluginService.executeCommonsExecPluginList(sessionTemp, commandLineShellList, "", platform);



                }

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

            executeCommonsExecPluginRemove(commandLine, moduleName, platform);
            executeCommonsExecPluginRemove(commandLineUpdater, moduleName, platform); // updater
            pluginService.executeCommonsExecPluginList(sessionTemp,commandLinePluginList, "C:\\W-Matrix\\rel_inswave_dev\\cli\\", platform);

        }else {
            if (mode.equals(PluginMode.PLUGIN_REMOVE)) {
                logger.info(" #### plugin remove service in a mode check ####{} {} {}", platform, mode, moduleName);

                String shellscriptFileName = getClassPathResourcePath("iOSPlugman.sh");
                shellscriptFileName = shellscriptFileName.replace("/iOSPlugman.sh","");

                CommandLine commandLinePlugmanRemove = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh");
                commandLinePlugmanRemove.addArgument("removePlugin"); // removePlugin
                commandLinePlugmanRemove.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName);
                commandLinePlugmanRemove.addArgument(moduleName);

                CommandLine commandLinePlugman = CommandLine.parse(shellscriptFileName +"/iOSPlugman.sh"); // path + shellscript
                commandLinePlugman.addArgument("getPluginInfo"); // getPluginInfo
                commandLinePlugman.addArgument(userRootPath + workspacePath + "/" + projectPath + "/"+ projectDirName); // branch path : userRootPath + workspaec + project + "/plugins"

                //ios plugin remove 수행
                executeCommonsExecPluginRemove(commandLinePlugmanRemove, moduleName, platform);
                pluginService.executeCommonsExecPluginList(sessionTemp, commandLinePlugman, "", platform);
            }
        }

    }

    /* android gradle deamon stop 명령어 호출, log file 생성 method */
    private void executeCommonsGradleDeamonStop(CommandLine commandLineParse) throws Exception {
        ObjectMapper Mapper = new ObjectMapper();

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
            String buildAfterLogFile = rootPath+"gradle_stop"+"_log"+date+".txt";

            Reader reader = new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray()));
            BufferedReader r = new BufferedReader(reader);
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            String tmp = null;

            while ((tmp = r.readLine()) != null)
            {

                out.write(tmp+"\n");
                logger.info(" #### Gradle Deamon Stop CommandLine log data ### : " + tmp);
                logger.info(" #### Gradle Deamon Stop CommandLine log data toString  ### : " + r.toString());
            }
            r.close();
            out.flush();
            int exitCode = resultHandler.getExitValue();

            logger.info(" exitCode : {}", exitCode);

            if(exitCode == 0){

            } else if(exitCode == 1){

            } else {

            }

            handler.stop();

        } catch (Exception e) {
            logger.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            log.error("builder plugin remove service error",e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /* commons_exec 라이브러리 executeCommonsExecBuild method  */
    private void executeCommonsExecPluginRemove(CommandLine commandLineParse, String moduleName, String platform) throws Exception {
        PluginAddListParam pluginAddListParam = new PluginAddListParam();

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

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

            logger.info(" input platform : {}", platform);
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

                logger.info(" plugin remove cli platform : {} moduleName : {} result : {}",platform, moduleName, result);
            }else {
                logger.info(" plugin remove cli platform : {} moduleName : {} result : {}",platform, moduleName, result);
            }

            int exitCode = resultHandler.getExitValue();
            logger.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                pluginSendMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.SUCCESSFUL.name());

            } else if(exitCode == 1){
                pluginSendMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.FAILED.name());

            } else {
                pluginSendMessageHandler(sessionTemp, pluginAddListParam, PayloadMsgType.FAILED.name());

            }

            handler.stop();

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            log.error("builder plugin remove service error",e);
            throw new Exception(e.getMessage(), e);
        }

    }

    private void executueVCSCommandALL(CommandLine commandLineParse, String VcsType, String commmandOrder){

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutResult = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";
        //log.info(" #### Gradle git clone CLI CommandLine status ... ### : ");
        //log.info(" #### Gradle git clone CLI CommandLine getArguments ... ### : {}", commandLineParse.getArguments());
        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);
            // log.info(" #### Gradle git clone CLI CommandLine try ... ### : ");
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            // vcs clone, checkout ... send message
            if (commmandOrder.toLowerCase().equals("gitadd")){
                //projectCreateMessage(projectGitCloneMessage,"","GITADD");
            }else if (commmandOrder.toLowerCase().equals("gitcommit")){
                //projectCreateMessage(projectGitCloneMessage,"","GITCOMMIT");
            }else if (commmandOrder.toLowerCase().equals("gitpush")){
                //projectCreateMessage(projectGitCloneMessage,"","GITPUSH");
            }else if (commmandOrder.toLowerCase().equals("svn")){
                //projectCreateMessage(projectGitCloneMessage,"","SVNCHECKOUT");
            }else if (commmandOrder.toLowerCase().equals("svnadd")){
                //projectCreateMessage(projectGitCloneMessage,"","SVNADD");
            }else if (commmandOrder.toLowerCase().equals("svncommit")){
                //projectCreateMessage(projectGitCloneMessage,"","SVNCOMMIT");
            }


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### VCS CLI CommandLine log  "+commmandOrder+" data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {

            log.error("builder plugin remove service error",e);
        } catch (InterruptedException e) {

            log.error("builder plugin remove service error",e);
        }

    }

    private void pluginSendMessageHandler(WebSocketSession session, PluginAddListParam pluginAddListParam, String messageValue) {
        ObjectMapper Mapper = new ObjectMapper();

        pluginAddListParam.setSessType(PayloadMsgType.HEADQUATER.name());
        pluginAddListParam.setMsgType(BuildServiceType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name());
        pluginAddListParam.setStatus("pluginremove");
        pluginAddListParam.setMessage(messageValue);
        pluginAddListParam.setHqKey(JSONPluginRemoveSettingObj.get(PayloadMsgType.hqKey.name()).toString());

        Map<String, Object> parseResult = Mapper.convertValue(pluginAddListParam, Map.class);

        headQuaterClientHandler.sendMessage(session, parseResult);
    }

}
