package com.pspotl.sidebranden.builder.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.BuildStatusMessage;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.util.FTPClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@Async
public class BuildWindowsProcess implements Runnable{

    private String buildLogs;
    private String headquaterUrl;

    FTPClientUtil ftpClientUtil = new FTPClientUtil();
    HeadQuaterClientHandler headQuaterClientHandler = new HeadQuaterClientHandler();

    // String 대신 추가 vo 객체로 받아서 처리하는 방식으로 구현해보기
    private static Map<WebSocketSession, String> websocketSessions = new ConcurrentHashMap<WebSocketSession, String>();

    private WebSocketSession _session;
    private CommandLine commandLine;
    private String platform = "Windows";
    private String workspacePath = ""; // workspacePath
    private String projectPath = "";
    private String projectDirName = ""; // ex 03_WHive_Presentation
    private int history_id;
    private String fullPath = "";
    private String appVersionStr = "";

    private JSONObject ftpSettingObj;
    private JSONObject buildServiceHistoryObj;
    private JSONObject getVcsSettingObj;
    private JSONObject resultURLObj = new JSONObject();

    // workspacePath => full path 로 이동 추후에는 수정이 필요함.
    public BuildWindowsProcess(WebSocketSession session, CommandLine commandLine,
                               String userRootPath ,String workspacePath, String projectPath, String projectDirName, int history_id,
                               String buildLogs, String headquaterUrl, String appVersionStr,
                               JSONObject ftpSettingObj, JSONObject buildServiceHistoryObj, JSONObject getVcsSettingObj){

        this._session = session;
        this.commandLine = commandLine;


        this.projectPath = projectPath;
        this.projectDirName = projectDirName;
        this.history_id = history_id;
        // this.fullPath = userRootPath + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;
        this.fullPath = workspacePath;
        this.appVersionStr = appVersionStr;

        this.buildLogs = buildLogs;
        this.headquaterUrl = headquaterUrl;

        this.ftpSettingObj = ftpSettingObj;
        this.buildServiceHistoryObj = buildServiceHistoryObj;
        this.getVcsSettingObj = getVcsSettingObj;

    }

    @Override
    public void run() {
        startWindowsBuildCLI();

    }

    private void startWindowsBuildCLI(){

        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        String logData = "";
        ObjectMapper Mapper = new ObjectMapper();
        JSONObject resultLogfileObj = new JSONObject();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();

        // DataInputStream
        BufferedReader is = null;
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        // DefaultExecutor executor = new DefaultExecutor();
        DaemonExecutor executor = new DaemonExecutor();

        try {
            handler.start();
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", null, null,null);

            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-ddHHmmss"));

            String buildAfterLogFile = buildLogs+platform+"_log"+date+".txt";
            String logfileName = platform+"_log"+date+".txt";

            // log file download json data set
            resultLogfileObj.put("log_path",buildLogs);
            resultLogfileObj.put("logfile_name",logfileName);

            // logfile 생성
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
                log.info(" Buile Service PipedInputStream building log data : {}", logData);
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", logData, null, null);
                out.write(logData+"\n");

            }

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultBuildFileObj = null;

            String setResultBuildFilePath = null;
            String setResultBuildFileName = null;
            handler.stop();
            resultHandler.waitFor();
            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            String result = "";
            if(exitCode == 0){

                // app file 스캔
                if (platform.toLowerCase().equals(PayloadMsgType.windows.name())){
                    resultBuildFileObj = new JSONObject();

                    resultBuildFileObj.put("platform_build_file_path",this.fullPath); // build path 수정
                    setResultBuildFilePath = (String) resultBuildFileObj.get("platform_build_file_path");

                }


                if (platform.toLowerCase().equals(PayloadMsgType.windows.name())){

                    resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "app/"+ platform  + "/" + projectPath + "/"); // file name ...
                    resultURLObj.put("log_path", resultLogfileObj.get("log_path").toString());
                    resultURLObj.put("logfile_name", resultLogfileObj.get("logfile_name").toString());

                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    // build result -> chmod 권한 수정
//                    CommandLine commandLine = CommandLine.parse("chmod");
//                    commandLine.addArgument("-R");
//                    commandLine.addArgument("775");
//                    commandLine.addArgument(path + setResultBuildFilePath);
//
//                    executeCommonsChmod(commandLine);

                    // exe file download 구현
                    // path replace -> ""
                    String todayFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    String tempPath = this.fullPath;
                    tempPath = tempPath.replace("\\cli\\W-MatrixCLI.exe","\\dist\\"+todayFormat);

                    // 해당 포멧에 맞춰서 exe file name 셋팅 하기
                    // W-Matrix + Setup + project name + opmode + version + todayFormat

                    // path + setResultBuildFilePath windows file path
                    buildAfterSendToHeadquaterFileObjWindows(tempPath, resultBuildFileObj, resultLogfileObj, platform);

                }

            } else if(exitCode == 1){
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);

            } else {
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);

            }


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

    private void buildAfterSendToHeadquaterFileObjWindows(String buildPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, String platform){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        try (Stream<Path> filePathStream= Files.walk(Paths.get(buildPath))) {
            MultiValueMap<String, Object> reqToFileObj =
                    new LinkedMultiValueMap<String, Object>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());
                log.info("buildAfterSendToHeadquaterFileObj filePath data : {} ", filePath);
                // appVersionStr
                if (fileNameToString.matches(".*"+this.appVersionStr+".*.exe")) {
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
                        log.warn(e.getMessage(), e);
                    }
                }
            });
        } catch (IOException e) {
             
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                // exception.printStackTrace();
            }
        }
    }

    private void buildAfterSendFTPClientObj(InputStream ins, String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
        String qrCodeStr = "";

        try {
            boolean uploaCheck = ftpClientUtil.upload(ins, filename, platform, projectPath, ftpSettingObj, null);
            // ftp 앱 파일 업로드 성공시 QRCode 생성 기능 수행
            if(uploaCheck){
                // QRCode 정상 처리 하면
                // restTemplate 객체로 -> 파일 전송...
                // FTP 파일 업로드중... 완료 되었을때 표시..ftpStoreUrl + projectPath + ".html"
                if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
                    resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "app/"+ platform  + "/" + projectPath + "/" + filename);
                    resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                    resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.SUCCESSFUL.name(), null, resultURLObj, resultBuildFileObj);
                }

            }else {
                resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultURLObj,null);
            }

            log.info(" buildAfterSendFTPClientObj uploaCheck : {}", uploaCheck);
        } catch (Exception e) {
             
        }

    }

    // [buildLogsValue, resultURLObj, resultBuildFileObj] json obj 묶기.
    // session 파리미터 추가하기
    private void buildMessageHandler(BuildStatusMessage buildStatusMessage, String status, String messageValue, String buildLogsValue, JSONObject resultURLObj ,JSONObject resultBuildFileObj){
        ObjectMapper Mapper = new ObjectMapper();

        buildStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        buildStatusMessage.setMsgType(BuildServiceType.HV_MSG_BUILD_STATUS_INFO.name());
        buildStatusMessage.setStatus(status);
        buildStatusMessage.setMessage(messageValue);
        buildStatusMessage.setHqKey(buildServiceHistoryObj.get(PayloadMsgType.hqKey.name()).toString());

        // build log message
        if(messageValue.equals("BUILDING") || messageValue.equals("CLEANBUILING") || messageValue.equals("STOP")){
            buildStatusMessage.setLogValue(buildLogsValue);
        }

        // build log file
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals(PayloadMsgType.FAILED.name())){
            buildStatusMessage.setHistory_id(history_id);
            buildStatusMessage.setLog_path(resultURLObj.get("log_path").toString());
            buildStatusMessage.setLogfile_name(resultURLObj.get("logfile_name").toString());
            buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);

        }

        // build file obj, qrcode url(구조 변경)
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            buildStatusMessage.setBuildFileObj(resultBuildFileObj);
            buildStatusMessage.setQrCode(resultURLObj.get("qrCodeUrl").toString());
            buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);

        }

        Map<String, Object> parseResult = Mapper.convertValue(buildStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(this._session, parseResult);
    }
}
