package com.inswave.whive.branch.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.BuildMode;
import com.inswave.whive.branch.domain.BuildStatusMessage;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.util.BranchRestTempleteUtil;
import com.inswave.whive.branch.util.FileNameAwareByteArrayResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Async
public class BuildGeneralAndroidProcess implements Runnable {

    private String buildLogs;
    private String headquaterUrl;

    HeadQuaterClientHandler headQuaterClientHandler = new HeadQuaterClientHandler();
    BranchRestTempleteUtil branchRestTempleteUtil = new BranchRestTempleteUtil();
    private WebSocketSession _session;
    private CommandLine commandLine;
    private String platform = "Android";
    private String domainID = "";
    private String userID = "";
    private String workspacePath = "";
    private String projectPath = "";
    private String projectDirName = "";
    private int history_id;
    private String fullPath = "";
    private String appfilePath = "";
    private LocalDateTime date = LocalDateTime.now();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private String nowString = date.format(dateTimeFormatter);
    private BuildMode tempMode = null;
    private Map<String, Object> parseResultTemp;
    private JSONObject ftpSettingObj;
    private JSONObject buildServiceHistoryObj;
    private JSONObject resultURLObj = new JSONObject();
    private JSONParser parser = new JSONParser();

    public BuildGeneralAndroidProcess(WebSocketSession session, CommandLine commandLine,
                                      String userRootPath, Map<String, Object> parseResult,
                                      String headquaterUrl, JSONObject ftpSettingObj, BuildMode mode) {

        try {
            this._session = session;
            this.commandLine = commandLine;

            this.domainID = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            this.userID = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            this.workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            this.projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            this.projectDirName = parseResult.get("buildProjectdir").toString();
            this.history_id = Integer.parseInt(parseResult.get("id").toString());
            this.fullPath = userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;

            this.buildLogs = userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/build_logfiles/";
            this.appfilePath = userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/appfiles/";
            this.headquaterUrl = headquaterUrl;

            this.ftpSettingObj = ftpSettingObj;

            // build history data set fix
            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();
            this.buildServiceHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);

            this.tempMode = mode;

            this.parseResultTemp = parseResult;
        } catch (ParseException e) {
            log.warn(e.getMessage(), e);
        }
    }
    @Override
    public void run() {
        startGeneralAndroidBuildCLI();
    }

    private void startGeneralAndroidBuildCLI() {
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
        DaemonExecutor executor = new DaemonExecutor();

        try {
            handler.start();
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", "", null,null);

            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            String buildAfterLogFile = buildLogs + platform + "_log" + nowString + ".txt";
            String logfileName = platform + "_log" + nowString + ".txt";

            // log file download json data set
            resultLogfileObj.put("log_path", buildLogs);
            resultLogfileObj.put("logfile_name", logfileName);

            // logfile 생성
            // log file 생성전에 build_logfile 디렉토리 생성하기
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null) {
//                log.info(" Buile Service PipedInputStream building log data : {}", logData);
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", logData, null, null);
                out.write(logData+"\n");
            }
            resultHandler.waitFor();
            JSONObject resultBuildFileObj = new JSONObject();

            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            if (exitCode == 0) {
                if (platform.toLowerCase().equals(PayloadMsgType.android.name())) { // Android apk file
                    log.info(" exitCode : {}", exitCode);
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    // build result -> chmod 권한 수정
                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("775");

                    commandLine.addArgument(fullPath);
                    executeCommonsChmod(commandLine);

                    buildAfterSendToHeadquaterFileObjAndroid(fullPath, resultBuildFileObj, resultLogfileObj, platform);
                }
            } else {
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            }

            handler.stop();
            is.close();

        } catch (ParseException e){
            log.warn(e.getMessage(), e);
        } catch (StringIndexOutOfBoundsException e) {
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            log.warn(e.getMessage(),e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                log.warn(exception.getMessage(), exception);
            }
        }
    }

    private void buildAfterSendToHeadquaterFileObjAndroid(String buildPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, String platform){

        try (Stream<Path> filePathStream = Files.walk(Paths.get(buildPath + "/app/build/outputs/"))) {
            MultiValueMap<String, Object> reqToFileObj = new LinkedMultiValueMap<String, Object>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());
                if (fileNameToString.matches(".*.apk.*") || fileNameToString.matches(".*.aab.*")) {
                    Path path = Paths.get(filePath.toUri());
//                    reqToFileObj.add("filename", fileNameToString);
//                    reqToFileObj.add("filePath", buildPath);
                    reqToFileObj.add(PayloadMsgType.platform.name(), platform);
                    reqToFileObj.add("projectDir", projectPath);

                    byte[] content = new byte[1024];

                    try {
                        content = Files.readAllBytes(path);
                        reqToFileObj.add("file",new FileNameAwareByteArrayResource(filePath.getFileName().toString(), content, null));

                        File appfilesPath = new File(appfilePath + nowString + "/");
                        appfilesPath.mkdir();

                        // 파일명 시간 정보 동기화
                        fileNameToString = makeBuildedFileName(fileNameToString, nowString);
                        File appfile = new File(appfilePath + nowString + "/" + fileNameToString);
                        FileUtils.writeByteArrayToFile(appfile, content);

                        reqToFileObj.add("filename", fileNameToString);
                        reqToFileObj.add("file", appfile);
                        reqToFileObj.add("filePath", appfile.getPath());

                        resultBuildFileObj.put("platform_build_file_path", appfilePath + nowString + "/");
                        // project_history 테이블의 platform_build_file_name 컬럼에 저장되는 파일명 시간 정보 동기화
                        resultBuildFileObj.put("platform_build_file_name", fileNameToString);
                        buildAfterAppFileUploadToManagerObj(fileNameToString, platform, projectPath, resultBuildFileObj, buildAfterLogFile, reqToFileObj);
                    } catch (IOException e) {
                        log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                    }
                }
            });
        } catch (IOException e) {

        }
    }

    private void buildAfterAppFileUploadToManagerObj(String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, MultiValueMap<String, Object> reqToFileObj){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
        try {
            boolean uploadCheck = false;
            //웹서버에 빌드결과 파일(ipk, aab 등) 저장 시, 스프링 기준으로 연월일시분초가 생성될 수 있게끔, 시간정보 전달
            reqToFileObj.add("nowString", nowString);
            reqToFileObj.add("bucket", parseResultTemp.get("bucket").toString());

            // uploadCheck = branchRestTempleteUtil.getUrlRestQRCodeUrl(headquaterUrl, reqToFileObj, parseResultTemp.get("builderUserID").toString(), parseResultTemp.get("password").toString());
            uploadCheck = branchRestTempleteUtil.getUrlRestQRCodeUrlToAwsS3(headquaterUrl, reqToFileObj, parseResultTemp.get("builderUserName").toString(), parseResultTemp.get("password").toString());

            JSONObject buildHistoryVO = (JSONObject) parser.parse(parseResultTemp.get("buildHistoryVo").toString());
            String projectDirName = buildHistoryVO.get("buildProjectName").toString();
            JSONObject repositoryObj = (JSONObject) parser.parse(parseResultTemp.get("repositoryObj").toString());
            String vcsType = repositoryObj.get("vcsType").toString();

            if(uploadCheck){
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    // 파일명 시간 정보 동기화
                    filename = makeBuildedFileName(filename, nowString);

                    // resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "/resource/upload/" + platform + "/" + projectPath + "/" + nowString + "/" + filename); // date 추가 ...
                    resultURLObj.put("qrCodeUrl", "AppFileDir/" + platform + "/" + projectPath + "/" + nowString + "/" + filename); // date 추가 ... // TODO tobe 경로임 ...
                    resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                    resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.SUCCESSFUL.name(), null, resultURLObj, resultBuildFileObj);
                }
            } else {
                resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultURLObj,null);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private int executeCommonsChmod(CommandLine commandLineParse) throws Exception {
        DefaultExecutor executor = new DefaultExecutor();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        executor.execute(commandLineParse, resultHandler);
        resultHandler.waitFor();

        int exitCode = resultHandler.getExitValue();
        log.info(" executeCommonsChmod exitCode : {}", exitCode);
        return exitCode;
    }

    private void buildMessageHandler(BuildStatusMessage buildStatusMessage, String status, String messageValue, String buildLogsValue, JSONObject resultURLObj ,JSONObject resultBuildFileObj){
        ObjectMapper Mapper = new ObjectMapper();

        buildStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        buildStatusMessage.setMsgType(BuildServiceType.HV_MSG_BUILD_STATUS_INFO.name());
        buildStatusMessage.setStatus(status);
        buildStatusMessage.setMessage(messageValue);
        buildStatusMessage.setHqKey(buildServiceHistoryObj.get(PayloadMsgType.hqKey.name()).toString());
        buildStatusMessage.setHistory_id(history_id);

        // build log message
        if(messageValue.equals("BUILDING") || messageValue.equals("CLEANBUILING") || messageValue.equals("STOP")){
            buildStatusMessage.setLogValue(buildLogsValue);
        }

        // build log file
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals(PayloadMsgType.FAILED.name())){
            buildStatusMessage.setLog_path(resultURLObj.get("log_path").toString());
            buildStatusMessage.setLogfile_name(resultURLObj.get("logfile_name").toString());
            buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);
        }

        // build file obj, qrcode url(구조 변경)
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name())){
            buildStatusMessage.setBuildFileObj(resultBuildFileObj);
            buildStatusMessage.setBuildMode(tempMode.toString());
            log.info(" Build SUCCESSFUL qrcode : {}",resultURLObj.get("qrCodeUrl").toString());
            buildStatusMessage.setQrCode(resultURLObj.get("qrCodeUrl").toString());
            buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(buildStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(this._session, parseResult);
    }

    /**
     * 빌드된 파일명에 yyyyMMddHHmmss 형태의 시간정보를 붙여서 리턴한다.
     *
     * @param originalFileName 원래 파일명
     * @param timeString 기준이 되는 시간 정보 (빌드 시작때 서버에서 만든 시간)
     * @return 빌드된 파일명에 yyyyMMddHHmmss 형태의 시간정보를 붙여서 리턴
     */
    public String makeBuildedFileName(String originalFileName, String timeString) {
        String pattern = "\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])([0-1][0-9]|2[0-3])([0-5][0-9])+";

        // 기존 파일명의 다른 yyyyMMddHHmmss 형태의 문자열이 있으면 인자로 들어온 값으로 바꿔서 리턴
        if (Pattern.compile(pattern).matcher(originalFileName).find()) {
            return originalFileName.replaceAll(pattern, timeString);
        }
        // yyyyMMddHHmmss 형태의 문자열이 없으면 뒤에 붙여서 리턴
        else {
            return originalFileName;//
        }
    }
}
