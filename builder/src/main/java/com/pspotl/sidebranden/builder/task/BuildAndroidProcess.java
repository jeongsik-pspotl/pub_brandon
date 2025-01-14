package com.pspotl.sidebranden.builder.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.BuildMode;
import com.pspotl.sidebranden.builder.domain.BuildStatusMessage;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.service.MobileTemplateConfigService;
import com.pspotl.sidebranden.builder.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Async
public class BuildAndroidProcess implements Runnable {

    private String springProfileMode;

    private String buildLogs;
    private String headquaterUrl;

    FTPClientUtil ftpClientUtil = new FTPClientUtil();
    HeadQuaterClientHandler headQuaterClientHandler = new HeadQuaterClientHandler();
    BranchRestTempleteUtil branchRestTempleteUtil = new BranchRestTempleteUtil();
    AgentFileSendUtil agentFileSendUtil = new AgentFileSendUtil();

    private MobileTemplateConfigService mobileTemplateConfigService = new MobileTemplateConfigService();

//    private AWSClientUtil awsClientUtil = new AWSClientUtil();


    private final GitTask gitTask = new GitTask();
    private final SvnTask svnTask = new SvnTask();

    // String 대신 추가 vo 객체로 받아서 처리하는 방식으로 구현해보기
    private static Map<WebSocketSession, String> websocketSessions = new ConcurrentHashMap<WebSocketSession, String>();

    private WebSocketSession _session;
    private CommandLine commandLine;
    private String platform = "Android";
    private String domainID = "";
    private String userID = "";
    private String workspacePath = "";
    private String projectPath = "";
    private String projectDirName = ""; // ex 03_WHive_Presentation
    private int history_id;
    private String fullPath = "";
    private String appfilePath = "";
    private LocalDateTime date = LocalDateTime.now();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private String nowString = date.format(dateTimeFormatter);
    private String productType = "";
    private BuildMode tempMode = null;
    private Map<String, Object> parseResultTemp;

    private JSONObject ftpSettingObj;
    private JSONObject buildServiceHistoryObj;
    private JSONObject getVcsSettingObj;
    private JSONObject resultURLObj = new JSONObject();
    private JSONParser parser = new JSONParser();

    public BuildAndroidProcess(WebSocketSession session, CommandLine commandLine,
                               String userRootPath, Map<String, Object> parseResult,
                               String buildLogs, String headquaterUrl,
                               JSONObject ftpSettingObj, JSONObject buildServiceHistoryObj, JSONObject getVcsSettingObj, BuildMode mode){
        try {
            this._session = session;
            this.commandLine = commandLine;

            this.domainID = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            this.userID = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            this.workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            this.projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            this.projectDirName = parseResult.get("buildProjectdir").toString();
            this.springProfileMode = parseResult.get("profileVal").toString(); // profile set
            this.history_id = Integer.parseInt(parseResult.get("id").toString());
            this.fullPath = userRootPath +"builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;

            this.buildLogs = userRootPath +"builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/build_logfiles/";
            this.appfilePath = userRootPath +"builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/appfiles/";
            this.headquaterUrl = headquaterUrl;

            this.productType = parseResult.get("product_type").toString();

            this.ftpSettingObj = ftpSettingObj;

            // build history data set fix
            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();
            this.buildServiceHistoryObj = (JSONObject) parser.parse(jsonBuildHistoryVo);


            this.getVcsSettingObj = getVcsSettingObj;
            this.tempMode = mode;

            this.parseResultTemp = parseResult;
        } catch (ParseException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        startAndroidBuildCLI();
    }

    private void startAndroidBuildCLI(){

        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        String logData = "";
        ObjectMapper Mapper = new ObjectMapper();
        JSONObject resultLogfileObj = new JSONObject();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();

        // DataInputStream
        BufferedReader is = null;
        BufferedReader errIs = null;
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
            resultLogfileObj.put("log_path",buildLogs);
            resultLogfileObj.put("logfile_name",logfileName);

            // logfile 생성
            // log file 생성전에 build_logfile 디렉토리 생성하기
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
//                log.info(" Buile Service building log data : {}", logData);
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", logData, null, null);
                out.write(logData+"\n");

            }

            // TODO : build error log 출력 기능 구현...
            errIs = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdoutErr.toByteArray())));
            while((logData = errIs.readLine()) != null){
                log.error(" Buile Service error log data : {}", logData);
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", logData, null, null);
                out.write(logData+"\n");

            }

            resultHandler.waitFor();
            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultBuildFileObj = null;

            String setResultBuildFilePath = null;
            String setResultBuildFileName = null;

            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            String result = "";
            if(exitCode == 0){

                // app file 스캔
                // apk build
                if(platform.toLowerCase().equals(PayloadMsgType.android.name()) && (tempMode.equals(BuildMode.DEBUG) || tempMode.equals(BuildMode.RELEASE))){ // Android apk file


                    StringBuilder fileContents = new StringBuilder();

                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String line;
                    while((line = br.readLine()) != null){
                        fileContents.append(line);
                    }

                    result = fileContents.toString();
                    result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
                    // result = result.replaceAll(" ","");

                    int idxStart = result.indexOf("//!!--##");
                    int idxEnd = result.indexOf("##--!!//");
                    String resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                    // rest templete -> headquater send
                    obj = parser.parse(resultsub);
                    resultBuildFileObj = (JSONObject) obj;

                    setResultBuildFilePath = (String) resultBuildFileObj.get("platform_build_file_path");
                    setResultBuildFileName = (String) resultBuildFileObj.get("platform_build_file_name");
                    log.info(" input resultBuildFileObj : {}", resultBuildFileObj);

                }

                // aab build
                if(platform.toLowerCase().equals(PayloadMsgType.android.name()) && (tempMode.equals(BuildMode.AAB_DEBUG) || tempMode.equals(BuildMode.AAB_RELEASE))){ // Android apk file

                    StringBuilder fileContents = new StringBuilder();

                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String line;
                    while((line = br.readLine()) != null){
                        fileContents.append(line);
                    }

                    result = fileContents.toString();
                    result = result.replaceAll("(\r\n|\r|\n|\n\r)","");
                    // result = result.replaceAll(" ","");

                    int idxStart = result.indexOf("//!!--##");
                    int idxEnd = result.indexOf("##--!!//");
                    String resultsub = result.substring(idxStart,idxEnd);
                    resultsub = resultsub.replace("//!!--##","");
                    log.info(" input resultsub : {}", resultsub);
                    // rest templete -> headquater send
                    obj = parser.parse(resultsub);
                    resultBuildFileObj = (JSONObject) obj;

                    setResultBuildFilePath = (String) resultBuildFileObj.get("platform_build_file_path");
                    setResultBuildFileName = (String) resultBuildFileObj.get("platform_build_file_name");
                    log.info(" input resultAABBuildFileObj : {}", resultBuildFileObj);

                }

                if(platform.toLowerCase().equals(PayloadMsgType.android.name())) { // Android apk file
                    log.info(" exitCode : {}", exitCode);
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    // build result -> chmod 권한 수정
                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("775");
                    if(tempMode.equals(BuildMode.AAB_DEBUG)) {
                        commandLine.addArgument(fullPath +setResultBuildFilePath, false);
                        executeCommonsChmod(commandLine);
                        // file obj Resttemplate
                        buildAfterSendToHeadquaterFileObjAndroid(fullPath +setResultBuildFilePath, resultBuildFileObj, resultLogfileObj, setResultBuildFileName, platform);

                    }else if(tempMode.equals(BuildMode.AAB_RELEASE)){
                        log.info(fullPath + setResultBuildFilePath);
                        commandLine.addArgument(fullPath + setResultBuildFilePath, false);
                        executeCommonsChmod(commandLine);
                        // file obj Resttemplate
                        buildAfterSendToHeadquaterFileObjAndroid(fullPath + setResultBuildFilePath, resultBuildFileObj, resultLogfileObj, setResultBuildFileName, platform);

                    }else {
                        commandLine.addArgument(fullPath + setResultBuildFilePath);
                        executeCommonsChmod(commandLine);
                        // file obj Resttemplate
                        buildAfterSendToHeadquaterFileObjAndroid( fullPath + setResultBuildFilePath, resultBuildFileObj, resultLogfileObj, setResultBuildFileName, platform);
                    }
                }
            } else if(exitCode == 1){
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            } else {
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            }

            handler.stop();
            is.close();
            errIs.close();

        } catch (ParseException e){
            log.warn(e.getMessage(), e);
        } catch (StringIndexOutOfBoundsException e) {
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            log.warn(e.getMessage(),e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                // exception.printStackTrace();
                log.warn(exception.getMessage(), exception);
            }
        }finally {
            try {
                pipedOutput.close();
                stdoutErr.close();
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*  chmod cli 전용 method */
    private int executeCommonsChmod(CommandLine commandLineParse) throws Exception {
        DefaultExecutor executor = new DefaultExecutor();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        executor.execute(commandLineParse, resultHandler);
        resultHandler.waitFor();

        int exitCode = resultHandler.getExitValue();
        log.info(" executeCommonsChmod exitCode : {}", exitCode);
        return exitCode;
    }

    private void buildAfterSendToHeadquaterFileObjAndroid(String buildPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, String buildFileName, String platform){

        try (Stream<Path> filePathStream= Files.walk(Paths.get(buildPath))) {
            MultiValueMap<String, Object> reqToFileObj =
                new LinkedMultiValueMap<String, Object>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());
                log.info("buildAfterSendToHeadquaterFileObj filePath data : {} ", filePath);

                if (fileNameToString.matches(".*.apk.*")) {
                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);
                    //Path path = Paths.get(androidPath + buildPath + "/" + fileNameToString);
                    Path path = Paths.get(buildPath + "/" + fileNameToString);
                    String name = fileNameToString;

                    reqToFileObj.add(PayloadMsgType.platform.name(),platform);
                    reqToFileObj.add("projectDir",projectPath);

                    byte[] content = new byte[1024];
                    Base64.Encoder encoder = Base64.getEncoder();

                    try {
                        content = Files.readAllBytes(path);
                        log.info("encodedString String add");
                        File appfilesPath = new File(appfilePath + nowString +"/");
                        appfilesPath.mkdir();

                        // 파일명 시간 정보 동기화
                        name = makeBuildedFileName(fileNameToString, nowString);

                        File appfile = new File(appfilePath + nowString +"/"+ name);
                        FileUtils.writeByteArrayToFile(appfile, content);


                        reqToFileObj.add("filename", name);
                        reqToFileObj.add("file", appfile);
                        reqToFileObj.add("filePath", appfile.getPath());

                        resultBuildFileObj.put("platform_build_file_path", appfilePath + nowString +"/");
                        // project_history 테이블의 platform_build_file_name 컬럼에 저장되는 파일명 시간 정보 동기화
                        resultBuildFileObj.replace("platform_build_file_name", name);
                        // InputStream ins = Files.newInputStream(path);
                        // TODO : aws s3 api 전송 기능 추가 프로토 타입
                        // TODO : 동적 처리 기능 및 서비스 기준으로 업로드 기능 구현해야함.
                        buildAfterAppFileUploadToManagerObj(fileNameToString, platform, projectPath, resultBuildFileObj, buildAfterLogFile, reqToFileObj);
                        // buildAfterSendFTPClientObj(ins, name, platform, projectPath, resultBuildFileObj, buildAfterLogFile);

                    } catch (IOException e) {
                        log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                    }

                    // aab 파일 인 경우
                }else if (fileNameToString.matches(".*.aab.*")){

                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);
                    //Path path = Paths.get(androidPath + buildPath + "/" + fileNameToString);
                    Path path = Paths.get(buildPath + "/" + fileNameToString);
                    String name = fileNameToString;
                    reqToFileObj.add(PayloadMsgType.platform.name(),platform);
                    reqToFileObj.add("projectDir",projectPath);

                    byte[] content = new byte[1024];
                    Base64.Encoder encoder = Base64.getEncoder();

                    try {
                        content = Files.readAllBytes(path);
                        log.info("encodedString String add");
                        File appfilesPath = new File(appfilePath + nowString +"/");
                        appfilesPath.mkdir();

                        // 파일명 시간 정보 동기화
                        name = makeBuildedFileName(fileNameToString, nowString);

                        File appfile = new File(appfilePath + nowString +"/"+ name);
                        FileUtils.writeByteArrayToFile(appfile, content);


                        reqToFileObj.add("filename", name);
                        reqToFileObj.add("file", appfile);
                        reqToFileObj.add("filePath", appfile.getPath());

                        resultBuildFileObj.put("platform_build_file_path", appfilePath + nowString +"/");
                        // project_history 테이블의 platform_build_file_name 컬럼에 저장되는 파일명 시간 정보 동기화
                        resultBuildFileObj.replace("platform_build_file_name", name);
                        // InputStream ins = Files.newInputStream(path);
                        // TODO : aws s3 api 전송 기능 추가 프로토 타입
                        // TODO : 동적 처리 기능 및 서비스 기준으로 업로드 기능 구현해야함.
                        buildAfterAppFileUploadToManagerObj(name, platform, projectPath, resultBuildFileObj, buildAfterLogFile, reqToFileObj);
                        // buildAfterSendFTPClientObj(ins, name, platform, projectPath, resultBuildFileObj, buildAfterLogFile);

                    } catch (IOException e) {
                        log.info(e.getMessage(),e);
                    }
                }
            });
        } catch (IOException e) {

        }
    }

    private void buildAfterSendFTPClientObj(InputStream ins, String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
        String qrCodeStr = "";
        try {
            boolean uploaCheck = ftpClientUtil.upload(ins, filename, platform, projectPath, ftpSettingObj, null); // date
            // ftp 앱 파일 업로드 성공시 QRCode 생성 기능 수행
            if(uploaCheck){
                // QRCode 정상 처리 하면
                // restTemplate 객체로 -> 파일 전송...
                // FTP 파일 업로드중... 완료 되었을때 표시..ftpStoreUrl + projectPath + ".html"
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    // ftpStoreUrl -> headquater 에서 받아오는 걸로 수정
                    // ftp ftpSettingObj 여부 체크

                    resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "app/"+ platform  + "/" + projectPath + "/" + filename); // date 추가 ...
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

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

    }

    private void buildAfterAppFileUploadToManagerObj(String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, MultiValueMap<String, Object> reqToFileObj){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
        try {
            boolean uploaCheck = false;
            //웹서버에 빌드결과 파일(ipk, aab 등) 저장 시, 스프링 기준으로 연월일시분초가 생성될 수 있게끔, 시간정보 전달
            reqToFileObj.add("nowString", nowString);
            reqToFileObj.add("bucket", parseResultTemp.get("bucket").toString());

            // 분기처리..
            if(springProfileMode.equals("tomcat_builder")){
                // test 검증..
                uploaCheck = agentFileSendUtil.getUrlRestAndroidAppFileUrl(ftpSettingObj.get("ftpUrl").toString(), reqToFileObj);
            }else {
                // 해당 구간 처리 방법 알아보기
//                uploaCheck = agentFileSendUtil.getUrlRestAndroidAppFileUrl(ftpSettingObj.get("ftpUrl").toString(), reqToFileObj);
                uploaCheck = branchRestTempleteUtil.getUrlRestQRCodeUrlToAwsS3(headquaterUrl, reqToFileObj, parseResultTemp.get("builderUserName").toString(), parseResultTemp.get("password").toString());

            }

            // QRCode 정상 처리 하면
            // restTemplate 객체로 -> 파일 전송...
            // FTP 파일 업로드중... 완료 되었을때 표시..ftpStoreUrl + projectPath + ".html"

            // action app version code + 1 증가 기능 호출
            buildMessageHandler(buildStatusMessage, "web_build","APPCONFIG", null, null, null);
            // mobileTemplateConfigService.templateConfigCLI(platform, fullPath, parseResultTemp.get("appVersionCode").toString(), parseResultTemp);
            mobileTemplateConfigService.setMultiConfigBuildAfterChangeAppVersionCode(_session, parseResultTemp, fullPath); // 내부적으로 많은 작업이 필요함..
            buildStatusMessage.setBuildNumber(parseResultTemp.get("appVersionCode").toString());

            JSONObject buildHistoryVO = (JSONObject) parser.parse(parseResultTemp.get("buildHistoryVo").toString());
            String projectDirName = buildHistoryVO.get("buildProjectName").toString();
            JSONObject repositoryObj = (JSONObject) parser.parse(parseResultTemp.get("repositoryObj").toString());
            String vcsType = repositoryObj.get("vcsType").toString();

            try {
                if ("localgit".equals(vcsType)) {
                    // action git push
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(), "GITPUSH", null, null, null);
                    gitTask.gitPush(parseResultTemp, fullPath, getVcsSettingObj, "appVersionCodeAutoUp");
                } else if ("localsvn".equals(vcsType)) {
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(), "SVNCOMMIT", null, null, null);
                    String repositoryID = repositoryObj.get("repositoryId").toString();
                    String repositoryPassword = repositoryObj.get("repositoryPassword").toString();

                    svnTask.svnAdd(new URI(fullPath));
                    svnTask.svnCommit(repositoryID, repositoryPassword, new URI(fullPath), "appVersionCodeAutoUp");
                }
            } catch (Exception e) {
                log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultURLObj,null);
            }

            if(uploaCheck){
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    // ftpStoreUrl -> headquater 에서 받아오는 걸로 수정
                    // ftp ftpSettingObj 여부 체크

                    // 파일명 시간 정보 동기화
                    filename = makeBuildedFileName(filename, nowString);

//                    resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "/resource/upload/" + platform + "/" + projectPath + "/" + nowString + "/" + filename); // date 추가 ... TODO : asis 경로임 ..
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
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
            resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultURLObj,null);
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
        buildStatusMessage.setHistory_id(history_id);

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
            buildStatusMessage.setBuildMode(tempMode.toString());
            log.info(" Build SUCCESSFUL qrcode : {}",resultURLObj.get("qrCodeUrl").toString());
            buildStatusMessage.setQrCode(resultURLObj.get("qrCodeUrl").toString());
            buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);

        }

        Map<String, Object> parseResult = Mapper.convertValue(buildStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(this._session, parseResult);
    }

    private MultipartFile getMultipartFile(String path) throws IOException {
        File file = new File(path);
        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            // Or faster..
            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            // do something.
        }

        //jpa.png -> multipart 변환
        MultipartFile mFile = new CommonsMultipartFile(fileItem);
        return mFile;
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
