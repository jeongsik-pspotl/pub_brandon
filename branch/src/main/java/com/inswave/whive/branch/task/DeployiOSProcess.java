package com.inswave.whive.branch.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.DeployStatusMessage;
import com.inswave.whive.branch.enums.DeployServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Async
public class DeployiOSProcess implements Runnable{

    private String deployLogs;
    private WebSocketSession _session;
    private CommandLine commandLine;
    private String platform = "iOS";
    private String workspacePath = "";
    private String projectPath = "";
    private String projectDirName = ""; // ex 03_WHive_Presentation
    private int history_id;
    private String fullPath = "";
    private String hqKeyStr = "";

    private Map<String, Object> parseResultTemp = new HashMap<>();


    HeadQuaterClientHandler headQuaterClientHandler = new HeadQuaterClientHandler();


    public DeployiOSProcess(WebSocketSession session, CommandLine commandLine,
                            Map<String, Object> parseResult, int history_id, String hqKey, String iOSdeployLogs){
        this._session = session;
        this.commandLine = commandLine;

        this.workspacePath = workspacePath;
        this.projectPath = projectPath;
        this.projectDirName = projectDirName;
        this.history_id = Integer.parseInt(parseResult.get("deployHistoryID").toString());
        // fullpath 하드코딩 해서 테스트 진행하기
        this.hqKeyStr = hqKey;

        this.deployLogs = iOSdeployLogs;

        this.parseResultTemp = parseResult;
    }


    @Override
    public void run() {
        startDeployCLIAction();

    }

    private void startDeployCLIAction() {

        DeployStatusMessage deployStatusMessage = new DeployStatusMessage();

        String logData = "";
        ObjectMapper Mapper = new ObjectMapper();
        JSONObject resultLogfileObj = new JSONObject();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();

        // DataInputStream
        BufferedReader is = null;
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DaemonExecutor executor = new DaemonExecutor();

        try {

            deployStatusMessage.setBuild_id(parseResultTemp.get(PayloadMsgType.project_id.name()).toString());
            handler.start();
            deployMessageHandler(deployStatusMessage, "web_deploy","DEPLOYING", "deploy start");

            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            LocalDateTime date = LocalDateTime.now();

            String deployAfterLogFile = deployLogs+platform+"_deploy_log"+date+".txt";
            String logfileName = platform+"_deploy_log"+date+".txt";

            // log file download json data set
            resultLogfileObj.put("log_path",deployLogs);
            resultLogfileObj.put("logfile_name",logfileName);
            log.info("{}", deployAfterLogFile);
            // logfile 생성
            File f = new File(deployAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
                log.info(" deploy Service PipedInputStream deploying log data : {}", logData);
                deployMessageHandler(deployStatusMessage, "web_deploy","DEPLOYING", logData);
                out.write(logData+"\n");

            }

            resultHandler.waitFor();
            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){

                // app file 스캔
                if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){ // Android apk file
                    deployStatusMessage.setHistory_id(history_id);
                    deployStatusMessage.setLog_path(resultLogfileObj.get("log_path").toString());
                    deployStatusMessage.setLogfile_name(resultLogfileObj.get("logfile_name").toString());
                    deployMessageHandler(deployStatusMessage, "web_deploy",PayloadMsgType.SUCCESSFUL.name(), null);
                }

            } else if(exitCode == 1){
                deployStatusMessage.setHistory_id(history_id);
                deployStatusMessage.setLog_path(resultLogfileObj.get("log_path").toString());
                deployStatusMessage.setLogfile_name(resultLogfileObj.get("logfile_name").toString());
                deployMessageHandler(deployStatusMessage, "web_deploy",PayloadMsgType.FAILED.name(), null);

            } else {
                deployStatusMessage.setHistory_id(history_id);
                deployStatusMessage.setLog_path(resultLogfileObj.get("log_path").toString());
                deployStatusMessage.setLogfile_name(resultLogfileObj.get("logfile_name").toString());
                deployMessageHandler(deployStatusMessage, "web_deploy",PayloadMsgType.FAILED.name(), null);

            }

            handler.stop();
            is.close();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                // exception.printStackTrace();
            }
        }

    }

    private void deployMessageHandler(DeployStatusMessage deployStatusMessage, String status, String messageValue, String buildLogsValue){
        ObjectMapper Mapper = new ObjectMapper();

        deployStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        deployStatusMessage.setMsgType(DeployServiceType.HV_MSG_DEPLOY_STATUS_INFO.name());
        deployStatusMessage.setStatus(status);
        deployStatusMessage.setMessage(messageValue);
        deployStatusMessage.setHqKey(hqKeyStr);


        // build log message
        if(messageValue.equals("DEPLOYING")){
            deployStatusMessage.setLogValue(buildLogsValue);
        }

        // build log file
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals(PayloadMsgType.FAILED.name())){
            deployStatusMessage.setHistory_id(history_id);
            // buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);

        }

        // build file obj, qrcode url(구조 변경)
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){

            // buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);

        }
        log.info("{}", deployStatusMessage.toString());
        Map<String, Object> parseResult = Mapper.convertValue(deployStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(this._session, parseResult);
    }
}
