package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.ProjectExportStatusMessage;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.util.BranchRestTempleteUtil;
import com.pspotl.sidebranden.builder.util.FileNameAwareByteArrayResource;
import com.pspotl.sidebranden.builder.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProjectExportSourceService {

    @Autowired
    private ZipUtils zipUtils;

    @Autowired
    private BranchRestTempleteUtil branchRestTempleteUtil;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Value("${whive.server.target}")
    private String headquaterUrl;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${spring.profiles}")
    private String profiles;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private LocalDateTime date = LocalDateTime.now();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private String nowString = date.format(dateTimeFormatter);

    private String systemUserHomePath = System.getProperty("user.home");

    // 세션 관리 기능 추가
    WebSocketSession _sessionTemp;


    // start Export 추가
    @Async("asyncThreadPool")
    public void startExportSource(WebSocketSession session, Map<String, Object> parseResult){

        // parameter 설정
        String path = parseResult.get("path").toString(); // userRootPath + workspace + projectname
        String domainIDPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userIDPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspacePath = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDirName = parseResult.get("projectDirName").toString(); // userRootPath + workspace + projectname + project dir
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();

        ProjectExportStatusMessage projectExportStatusMessage = new ProjectExportStatusMessage();

        //log.info("userRootPath : {} ",userRootPath);
        //log.info("path : {} ",path);
        //log.info("platform : {} ",platform);

        // export status message send to Headquarter
        exportMessageHandler(session, projectExportStatusMessage, parseResult);

        // 1. project clean
        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

            //
            String cmdStr = "cd " + systemUserHomePath + userRootPath + "builder_main/"+path + " && ./gradlew clean" ;
            CommandLine commandLineGradleClean = CommandLine.parse("/bin/sh");
            commandLineGradleClean.addArgument("-c");
            commandLineGradleClean.addArgument(cmdStr, false);

            excuterProjectClean(commandLineGradleClean);
        } else if (platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            /**
             *  iOS Project Clean 명령 수행 기능
             */
            String projExportCleanString = "{\"projPath\":\""+systemUserHomePath + userRootPath + "builder_main/"+path+"\"}";

            CommandLine commandLineClean = CommandLine.parse("wmatrixmanager"); // acrhive clean 기능 요청,,
            commandLineClean.addArgument("clean");
            commandLineClean.addArgument("-j");
            commandLineClean.addArgument(projExportCleanString, false);

            log.info(" wmatrixmanager clean -j " + projExportCleanString + " cli action ");
            excuterProjectClean(commandLineClean);
        }

        // 2. project dir zip 압축 기능 호출
        // return zip file
        if(projectDirName.equals("") || projectDirName == null){
            getProjectDirZipFile(systemUserHomePath + userRootPath + "builder_main/"+path, projectName);

        }else {
            getProjectDirZipFile(systemUserHomePath + userRootPath + "builder_main/"+path, projectDirName);
        }



        // send to Headquarter
//        if(projectDirName.equals("") || projectDirName == null){
//            getSendHttpClientToHeadquarter(systemUserHomePath + userRootPath + "builder_main/" + domainIDPath + "/" + userIDPath + "/" + workspacePath+"/" + projectName, projectDirName, projectName, parseResult.get(PayloadMsgType.hqKey.name()).toString());
//        }else {
            getSendHttpClientToHeadquarter(session, systemUserHomePath + userRootPath + "builder_main/" + domainIDPath + "/" + userIDPath + "/" + workspacePath+"/" + projectName, projectDirName, projectName, parseResult.get(PayloadMsgType.hqKey.name()).toString(), parseResult);
//        }


    }

    // project clean
    public void excuterProjectClean(CommandLine commandLineParse){
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

            LocalDateTime date = LocalDateTime.now();
            // String buildAfterLogFile = buildLogs+"clean_build"+"_log"+date+".txt";

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

//            File f = new File(buildAfterLogFile);
//            FileWriter fstream = new FileWriter(f);
//            BufferedWriter out = new BufferedWriter(fstream);
            String tmp = null;

            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle Clean Build CommandLine log data ### : " + tmp);
                //out.write(tmp+"\n");

            }
            resultHandler.waitFor();
            is.close();
            // out.flush();
            int exitCode = resultHandler.getExitValue();

            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                // buildMessageHandler(buildStatusMessage, "web_build","CLEANBUILD", null, null,null);
            } else if(exitCode == 1){

            } else {

            }

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {
                // exception.printStackTrace();
                log.warn(e.getMessage(), exception); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }

    }

    // project dir zip 압축 기능 호출
    private void getProjectDirZipFile(String path, String projectDirName){

        // ZipUtil.pack(new File(path), new File(path + ".zip"));
        try {
            zipUtils.makeZipFromDir(path +"/", path + ".zip");
        } catch (IOException e) {
             
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

    }


    // send http client 요청 보내기
    private void getSendHttpClientToHeadquarter(WebSocketSession session, String zipFilepath, String projectDirName, String projectName, String hqKey, Map<String, Object> parseResult){

        // file path 경로 검색
        try (Stream<Path> filePathStream= Files.walk(Paths.get(zipFilepath))) {
            MultiValueMap<String, Object> reqToFileObj =
                    new LinkedMultiValueMap<String, Object>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());
                String resultTemplate = "";
                // log.info("buildAfterSendToHeadquaterFileObj filePath data : {} ", filePath);
                // log.info("buildAfterSendToHeadquaterFileObj filePath fileNameToString : {} ", fileNameToString);
                if(projectDirName.indexOf("][") != -1){
                    String[] templateVersion = projectDirName.split("\\]\\[");
                    for (int b = 0; b < templateVersion.length; b++){

                        if(b == 0){
                            resultTemplate = templateVersion[b];
                            resultTemplate = resultTemplate.replace("WHybridTemplate[","");
                            resultTemplate = resultTemplate.replace("WHybrid_Template[","");

                        }

                        if(b == 3 || b == 2){
                            resultTemplate = templateVersion[b];
                            resultTemplate = resultTemplate.replace("]","");
                        }

                    }

                }else if(projectDirName == null || projectDirName.equals("")) {
                    resultTemplate = projectName;
                } else {
                    resultTemplate = projectDirName;
                }

                if (fileNameToString.matches(".*"+resultTemplate+".*.zip")) {
                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);
                    log.info("buildAfterSendToHeadquaterFileObj zipFilepath data : {} ", zipFilepath);
                    Path path = Paths.get(zipFilepath + "/" + fileNameToString);
                    String name = "";
                    if(fileNameToString.indexOf("WHybridTemplate[") != -1 || fileNameToString.indexOf("WHybrid_Template[") != -1){
                        name = fileNameToString.replace("Template[","Template");
                        name = name.replaceAll("\\]\\[","");
                        name = name.replaceAll("]","");
                    }else{
                        name = fileNameToString;
                    }


                    reqToFileObj.add("filename", name);
                    reqToFileObj.add("filePath", path);
                    reqToFileObj.add("projectDirName", projectDirName);
                    reqToFileObj.add("nowString", nowString);
                    reqToFileObj.add(PayloadMsgType.hqKey.name(), hqKey);

                    try {
                        File file = new File(String.valueOf(path));
                        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, name, (int) file.length(), file.getParentFile());
                        // path to file obj 변환

                        InputStream input = new FileInputStream(file);
                        OutputStream os = fileItem.getOutputStream();
                        IOUtils.copy(input, os);

                        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                        log.info("encodedString String add");

                        // ftpClient -> FTPClientUtil 객체 생성 및 호출하기 플랫폼 마다 기준을 잡고 유동성 있게 소스코드 구현하기
                        // TODO aws s3  서비스로 업로드 작업 진행하기
                        if(profiles.equals("onpremiss")){
                            reqToFileObj.add("file", new FileNameAwareByteArrayResource(multipartFile.getOriginalFilename(),multipartFile.getBytes(),""));
                            branchRestTempleteUtil.getUrlProjectZipFileRestTemplete(headquaterUrl, reqToFileObj);
                        }else {

                            File zipfile = new File(String.valueOf(path));
                            FileUtils.copyInputStreamToFile(input, zipfile);
                            multipartFile.transferTo(zipfile);

                            reqToFileObj.add("file", zipfile);
                            reqToFileObj.add("bucket", bucket);
                            parseResult.put("filename", name );
                            boolean  resultYn = branchRestTempleteUtil.getUrlRestExportZipUrlToAwsS3(reqToFileObj,"","");
                            if(resultYn == true){
                                String AppFileDir = "AppFileDir/";
                                String projectDir = reqToFileObj.get("projectDirName").get(0).toString();
                                String nowString = reqToFileObj.get("nowString").get(0).toString();
                                String parentDirString = AppFileDir + projectDir + "/" + nowString;
                                ProjectExportStatusMessage projectExportStatusMessage = new ProjectExportStatusMessage();

                                projectExportStatusMessage.setZipFileUrl(parentDirString + "/"+name);
                                exportResultMessageHandler(session, projectExportStatusMessage, parseResult);
                            }else{
                                ProjectExportStatusMessage projectExportStatusMessage = new ProjectExportStatusMessage();
                                exportResultMessageHandler(session, projectExportStatusMessage, parseResult);
                            }
                        }


                        file.delete();
                        input.close();
                        os.close();
                        //fileItem.delete();

                    } catch (IOException e) {
                        log.warn(e.getMessage(), e);
                    }finally {

                    }

                }
            });
        } catch (IOException e) {
             
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }finally {
            //                input.get().close();
//                os.get().close();
        }

        // file, path , file obj 설정
        //

    }

    private void exportMessageHandler(WebSocketSession session, ProjectExportStatusMessage projectExportStatusMessage, Map<String, Object> parseResult){
        ObjectMapper Mapper = new ObjectMapper();

        projectExportStatusMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_EXPORT_STATUS_INFO.name());
        projectExportStatusMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());

        Map<String, Object> parseStatusResult = Mapper.convertValue(projectExportStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseStatusResult);
    }

    // MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO_HEADQUATER
    private void exportResultMessageHandler(WebSocketSession session, ProjectExportStatusMessage projectExportStatusMessage, Map<String, Object> parseResult){
        ObjectMapper Mapper = new ObjectMapper();

        projectExportStatusMessage.setMsgType(ProjectServiceType.MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO.name());
        projectExportStatusMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
        projectExportStatusMessage.setFilename(parseResult.get(PayloadMsgType.filename.name()).toString());

        Map<String, Object> parseStatusResult = Mapper.convertValue(projectExportStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseStatusResult);
    }

}
