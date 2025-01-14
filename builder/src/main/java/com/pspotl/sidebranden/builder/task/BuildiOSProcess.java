package com.pspotl.sidebranden.builder.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.BuildMode;
import com.pspotl.sidebranden.builder.domain.BuildStatusMessage;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.service.MobileTemplateConfigService;
import com.pspotl.sidebranden.builder.util.BranchRestTempleteUtil;
import com.pspotl.sidebranden.builder.util.FTPClientUtil;
import com.pspotl.sidebranden.builder.util.iOSFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import java.util.stream.Stream;

@Slf4j
public class BuildiOSProcess implements Runnable {

    private String buildLogs;
    private String headquaterUrl;

    FTPClientUtil ftpClientUtil = new FTPClientUtil();

    iOSFileUtil iOSFileUtil = new iOSFileUtil();
    HeadQuaterClientHandler headQuaterClientHandler = new HeadQuaterClientHandler();
    BranchRestTempleteUtil branchRestTempleteUtil = new BranchRestTempleteUtil();

    private MobileTemplateConfigService mobileTemplateConfigService = new MobileTemplateConfigService();

    // String 대신 추가 vo 객체로 받아서 처리하는 방식으로 구현해보기
    private static Map<WebSocketSession, String> websocketSessions = new ConcurrentHashMap<WebSocketSession, String>();

    private WebSocketSession _session;
    private CommandLine commandLine;
    private String platform = "iOS";
    private String domainID = "";
    private String userID = "";
    private String workspacePath = "";
    private String projectPath = "";
    private String projectDirName = ""; // ex 03_WHive_Presentation
    private int history_id;
    private String fullPath = "";
    private String rootPath = "";
    private String appfilePath = "";
    private BuildMode tempMode = null;
    private LocalDateTime date = LocalDateTime.now();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private String nowString = date.format(dateTimeFormatter);

    private JSONObject ftpSettingObj;
    private JSONObject buildServiceHistoryObj;
    private JSONObject getVcsSettingObj;
    private JSONObject resultURLObj = new JSONObject();
    private JSONParser parser = new JSONParser();

    private Map<String, Object> parseResultTemp;

    private GitTask gitTask = new GitTask();
    private SvnTask svnTask = new SvnTask();

    public BuildiOSProcess(WebSocketSession session, CommandLine commandLine,
                           String userRootPath , Map<String, Object> parseResult,
                           String buildLogs, String headquaterUrl,
                           JSONObject ftpSettingObj, JSONObject buildServiceHistoryObj, JSONObject getVcsSettingObj, BuildMode mode){

        this._session = session;
        this.commandLine = commandLine;

        this.domainID = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        this.userID = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        this.workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        this.projectPath = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        this.projectDirName = parseResult.get("buildProjectdir").toString();
        this.history_id = Integer.parseInt(parseResult.get("id").toString());
        this.fullPath = userRootPath +"builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/" + projectDirName;

        this.buildLogs = userRootPath +"builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/build_logfiles/";
        this.appfilePath = userRootPath +"builder_main/" + domainID + "/" + userID + "/" + workspacePath + "/" + projectPath + "/appfiles/";
        this.headquaterUrl = headquaterUrl;

        this.ftpSettingObj = ftpSettingObj;
        this.buildServiceHistoryObj = buildServiceHistoryObj;
        this.getVcsSettingObj = getVcsSettingObj;

        this.tempMode = mode;

        this.parseResultTemp = parseResult;

    }

    @Override
    public void run() {

        startiOSBuildCLI();

    }

    public void startiOSBuildCLI(){
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
        // DefaultExecutor executor = new DefaultExecutor();
        DaemonExecutor executor = new DaemonExecutor();

        try {

            handler.start();
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"BUILDING", "", null,null);

            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            // LocalDateTime date = LocalDateTime.now();

            String buildAfterLogFile = buildLogs+platform+"_log"+nowString+".txt";
            String logfileName = platform+"_log"+nowString+".txt";

            // log file download json data set
            resultLogfileObj.put("log_path",buildLogs);
            resultLogfileObj.put("logfile_name",logfileName);

            // logfile 생성
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            while((logData = is.readLine()) != null){
                // log.info(" Buile Service PipedInputStream building log data : {}", logData);
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

            JSONParser parser = new JSONParser();
            Object obj = null;
            JSONObject resultBuildFileObj = null;

            String setResultBuildFilePath = null;
            String setResultBuildFileName = null;

            resultHandler.waitFor();
            out.flush();
            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);

            String result = "";
            if(exitCode == 0){

                // app file 스캔
                log.info(" start result value : {}", logData);
                log.info(" start result value : {}", tempMode.toString());
                resultBuildFileObj = new JSONObject();
                String profileType = parseResultTemp.get(PayloadMsgType.profile_type.name()).toString();

                if(tempMode.equals(BuildMode.DEBUG)){
                    resultBuildFileObj.put("ios_build_file_path","/build/debug");
                }else if(tempMode.equals(BuildMode.RELEASE)){
                    resultBuildFileObj.put("ios_build_file_path","/build/release");
                }else {
                    resultBuildFileObj.put("ios_build_file_path","/build/"+profileType);
                }

                setResultBuildFilePath = (String) resultBuildFileObj.get("ios_build_file_path");

                if(platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
                    // resultBuildFileObj.put();
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"FILEUPLOADING", null, null, null);
                    // TimeUnit.MINUTES.sleep(1); // 1분간격으로 통신 상태 확인 하기

                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("775");
                    commandLine.addArgument(fullPath + setResultBuildFilePath);

                    executeCommonsChmod(commandLine, resultLogfileObj);

                    buildAfterSendToHeadquaterFileObjiOS(fullPath + setResultBuildFilePath, resultBuildFileObj, resultLogfileObj, platform);
                }
            } else if(exitCode == 1){
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            } else {
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
            }

            handler.stop();
            is.close();

        } catch (ParseException e){
            e.getStackTrace();
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                // exception.printStackTrace();
            }
        }
    }

    /* android gradle clean build 전용 method */
    private int executeCommonsChmod(CommandLine commandLineParse, JSONObject resultLogfileObj) throws Exception {

        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();

        DefaultExecutor executor = new DefaultExecutor();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        executor.execute(commandLineParse, resultHandler);
        resultHandler.waitFor();

        int exitCode = resultHandler.getExitValue();
        log.info(" executeCommonsChmod exitCode : {}", exitCode);

        if(exitCode == 0){
            //buildMessageHandler(buildStatusMessage, "web_build","FAILED", null, resultLogfileObj, null);
        }else if(exitCode != 0){
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultLogfileObj, null);
        }


        return exitCode;
    }

    private void buildAfterAppFileUploadToManagerObj(String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, MultiValueMap<String, Object> reqToFileObj){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
//        filename = filename.replace(" ", "_");
        try {
            boolean uploadCheck = false;
            //웹서버에 빌드결과 파일(ipk, aab 등) 저장 시, 스프링 기준으로 연월일시분초가 생성될 수 있게끔, 시간정보 전달
            reqToFileObj.add("nowString", nowString);
            reqToFileObj.add("bucket", parseResultTemp.get("bucket").toString());

            // 분기처리..
            // uploadCheck = branchRestTempleteUtil.getUrlRestQRCodeUrl(headquaterUrl, reqToFileObj, parseResultTemp.get("builderUserID").toString(), parseResultTemp.get("password").toString());
            uploadCheck = branchRestTempleteUtil.getUrlRestQRCodeUrlToAwsS3(headquaterUrl, reqToFileObj, parseResultTemp.get("builderUserName").toString(), parseResultTemp.get("password").toString());
            // QRCode 정상 처리 하면
            // restTemplate 객체로 -> 파일 전송...
            // FTP 파일 업로드중... 완료 되었을때 표시..ftpStoreUrl + projectPath + ".html"

            // action app version code + 1 증가 기능 호출
            buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"APPCONFIG", null, null, null);
            mobileTemplateConfigService.setMultiConfigBuildAfterChangeAppVersionCode(_session, parseResultTemp, fullPath);
            buildStatusMessage.setBuildNumber(parseResultTemp.get(PayloadMsgType.appVersionCode.name()).toString());

            JSONObject buildHistoryVO = (JSONObject) parser.parse(parseResultTemp.get(PayloadMsgType.buildHistoryVo.name()).toString());
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
            }

            if(uploadCheck){
                if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    // ftpStoreUrl -> headquater 에서 받아오는 걸로 수정

                    // json data set
                    JSONObject resultToJSON = new JSONObject();

                    resultToJSON.put("ftpUrl" ,ftpSettingObj.get("ftpUrl").toString());
                    resultToJSON.put(PayloadMsgType.ApplicationID.name(), parseResultTemp.get(PayloadMsgType.ApplicationID.name()).toString());
                    resultToJSON.put(PayloadMsgType.AppName.name(), filename);
                    resultToJSON.put(PayloadMsgType.AppVersion.name(), parseResultTemp.get(PayloadMsgType.AppVersion.name()).toString());
                    resultToJSON.put("Project" ,projectPath);
                    resultToJSON.put("plistPath" ,reqToFileObj.get("plistPath").get(0).toString());
                    resultToJSON.put("nowString" ,nowString);
                    resultToJSON.put(PayloadMsgType.platform.name() ,platform);
                    resultToJSON.put("createdDateTime", nowString);
                    resultToJSON.put("builderUserName", parseResultTemp.get("builderUserName").toString());
                    resultToJSON.put("password", parseResultTemp.get("password").toString());
                    resultToJSON.put("bucket", parseResultTemp.get("bucket").toString());

                    // ftp ftpSettingObj 여부 체크
                    // TODO createPlistFile 메소드 연계 작업 진행하기 .. 1
                    iOSFileUtil.createPlistFile(resultToJSON);

                    // branchRestTempleteUtil.getiOSPlistAndHTMLFileDataSetURLtoAws(ftpSettingObj.get("ftpUrl").toString(),resultToJSON);


                    // itms-services://?action=download-manifest&url="+ jsonResult.get("ftpUrl").toString() +"/resource/upload/iOS/"+ jsonResult.get(PayloadKeyType.Project.name()).toString() +"/manifest.plist
                    // resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + "/resource/upload/"+ platform  + "/" + projectPath + "/" + filename); // date 추가 ...
//                    resultURLObj.put("qrCodeUrl", "itms-services://?action=download-manifest&url=" + ftpSettingObj.get("ftpUrl").toString() + "/resource/upload/iOS/" + projectPath + "/" + nowString + "/manifest.plist"); // date 추가 ...
                    // resultURLObj.put("qrCodeUrl", "AppFileDir/iOS/" + projectPath + "/" + nowString + "/manifest.plist"); // date 추가 ..
                    //resultURLObj.put("qrCodeUrl", "AppFileDir/" + platform + "/" + projectPath + "/" + nowString + "/" + "manifest.plist");
                    resultURLObj.put("qrCodeUrl", "AppFileDir/" + platform + "/" + projectPath + "/" + nowString + "/");
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
        }
    }

    private void buildAfterSendToHeadquaterFileObjiOS(String buildPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile, String platform){

        try (Stream<Path> filePathStream= Files.walk(Paths.get(buildPath))) {
            MultiValueMap<String, Object> reqToFileObj =
                    new LinkedMultiValueMap<String, Object>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());
                log.info("buildAfterSendToHeadquaterFileObj filePath data : {} ", filePath);
                if (fileNameToString.matches(".*.ipa.*")) {
                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);
                    Path path = Paths.get(buildPath + "/" + fileNameToString);
//                    reqToFileObj.add("fileName", fileNameToString);

                    reqToFileObj.add(PayloadMsgType.platform.name(),platform);
                    reqToFileObj.add("projectDir",projectPath);

                    byte[] content = new byte[1024];
                    Base64.Encoder encoder = Base64.getEncoder();

                    try {
                        content = Files.readAllBytes(path);
                        String encodedString =  encoder.encodeToString(content);
                        log.info("encodedString String add");
                        // reqToFileObj.add("file", encodedString);
                        // reqToFileObj.add("file",new FileNameAwareByteArrayResource(filePath.getFileName().toString(), content, null));

                        File appfilesPath = new File(appfilePath + nowString +"/");
                        appfilesPath.mkdir();

                        // 파일명에 현재 시간정보를 넣는다.
                        String[] disAssembleFileName = fileNameToString.split("\\.");
                        String fileName = disAssembleFileName[0];
                        String fileExt = disAssembleFileName[1];
                        fileNameToString = fileName + "." + fileExt;

                        resultBuildFileObj.put("platform_build_file_name",fileNameToString);

                        File appfile = new File(appfilePath + nowString +"/"+ fileNameToString);
                        FileUtils.writeByteArrayToFile(appfile, content);

                        reqToFileObj.add("file",appfile);
                        reqToFileObj.add("filePath", appfile.getPath());
                        reqToFileObj.add("plistPath", appfilesPath.getPath());
                        reqToFileObj.add("filename", fileNameToString);

                        CommandLine commandLineIPAFile = CommandLine.parse("chmod");
                        commandLineIPAFile.addArgument("-R");
                        commandLineIPAFile.addArgument("775");
                        commandLineIPAFile.addArgument(appfilePath + nowString +"/");
                        executeCommonsChmod(commandLineIPAFile, null);

                        // resultBuildFileObj.put("platform_build_file_path",  "AppFileDir/" + platform + "/" + projectPath + "/"  + nowString +"/");
                        resultBuildFileObj.put("platform_build_file_path",  appfilePath + nowString +"/");
                        InputStream ins = Files.newInputStream(path);
                        // ios appfile 저장 directory 로 추가 저장
                        // directory 추가 생성해야함
                        // /Users/name/appfile/workspace/project/appname + now date /logfile + appfile
                        // buildAfterCopyAppFileObj(encodedString, name);
                        buildAfterAppFileUploadToManagerObj(fileNameToString, platform, projectPath, resultBuildFileObj, buildAfterLogFile, reqToFileObj);
                        // buildAfterSendFTPClientObj(ins, name, platform, projectPath, resultBuildFileObj, buildAfterLogFile);
                    } catch (IOException e) {
                        log.warn(e.getMessage(), e);
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            });
        } catch (IOException e) {
             
        }

    }

    // 빌드 기능 완료 되면 작업 이어서 진행하기로 하기
    private void buildAfterCopyAppFileObj(String encodedString, String getFilename) {

        Base64.Decoder decoder = Base64.getDecoder();
        try {

            byte[] bytes = null;
            bytes = decoder.decode(encodedString);

            BufferedOutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(rootPath +"/appfile/" + workspacePath + "/" + projectPath + "/" + getFilename + LocalDateTime.now(), getFilename)));
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();


        } catch (IOException e) {
             
        }

    }

    private void buildAfterSendFTPClientObj(InputStream ins, String filename, String platform, String projectPath, JSONObject resultBuildFileObj, JSONObject buildAfterLogFile){
        BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
        String qrCodeStr = "";

        try {

            boolean uploadCheck = ftpClientUtil.upload(ins, filename, platform, projectPath, ftpSettingObj, parseResultTemp.get(PayloadMsgType.ApplicationID.name()).toString());


            // ftp 앱 파일 업로드 성공시 QRCode 생성 기능 수행
            if(uploadCheck){
                // QRCode 정상 처리 하면
                // restTemplate 객체로 -> 파일 전송...
                // FTP 파일 업로드중... 완료 되었을때 표시..ftpStoreUrl + projectPath + ".html"
                if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    // ftpStoreUrl + projectPath +".html"
                    resultURLObj.put("qrCodeUrl", ftpSettingObj.get("ftpUrl").toString() + projectPath +".html");
                    resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                    resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());

                    // action app version code + 1 증가 기능 호출
                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),"APPCONFIG", null, null, null);
                    mobileTemplateConfigService.templateConfigCLI(platform, fullPath, parseResultTemp.get(PayloadMsgType.appVersionCode.name()).toString(), parseResultTemp);
                    buildStatusMessage.setBuildNumber(parseResultTemp.get(PayloadMsgType.appVersionCode.name()).toString());

                    JSONObject buildHistoryVO = (JSONObject) parser.parse(parseResultTemp.get(PayloadMsgType.buildHistoryVo.name()).toString());
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
                    }

                    buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.SUCCESSFUL.name(), null, resultURLObj, resultBuildFileObj);
                }

            }else {
                resultURLObj.put("log_path", buildAfterLogFile.get("log_path").toString());
                resultURLObj.put("logfile_name", buildAfterLogFile.get("logfile_name").toString());
                buildMessageHandler(buildStatusMessage, PayloadMsgType.web_build.name(),PayloadMsgType.FAILED.name(), null, resultURLObj,null);
            }

            log.info(" buildAfterSendFTPClientObj uploadCheck : {}", uploadCheck);
        } catch (Exception e) {
             
        }

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
            buildStatusMessage.setBuildMode(tempMode.toString());
            buildStatusMessage.setBuildFileObj(resultBuildFileObj);
            buildStatusMessage.setQrCode(resultURLObj.get("qrCodeUrl").toString());
            buildStatusMessage.setBuildHistoryObj(buildServiceHistoryObj);

        }

        Map<String, Object> parseResult = Mapper.convertValue(buildStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(_session, parseResult);
    }

}
