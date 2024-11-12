package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.ProjectGitCloneMessage;
import com.inswave.whive.branch.domain.ProjectSettingImageMessage;
import com.inswave.whive.branch.enums.*;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.util.FileNameAwareByteArrayResource;
import com.inswave.whive.branch.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class AppIconService extends BaseService {

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    private ZipUtils zipUtils;

    @Value("${whive.branch.id}")
    private String userId;

    // deployhistory Root Path
    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${whive.distribution.profilePath}")
    private String profileSetDir;

    private String systemUserHomePath = System.getProperty("user.home");

    // ios AppIcon Path
    @Value("${whive.distribution.iOSAppIconPath}")
    private String iosAppIconPath;

    @Value("${whive.distribution.AndroidAppIconPath}")
    private String androidAppIconProject;

    @Value("${whive.distribution.iOSAppIconPath}")
    private String iosAppIconProject;

    @Value("${whive.distribution.AndoridLogohdpiPath}")
    private String androidhdpiAppIconPath;

    @Value("${whive.distribution.AndoridLogomdpiPath}")
    private String androidmdpiAppIconPath;

    @Value("${whive.distribution.AndoridLogoxhdpiPath}")
    private String androidxhdpiAppIconPath;

    private ObjectMapper Mapper = new ObjectMapper();
    WebSocketSession sessionTemp;

    JSONParser parser = new JSONParser();

    private String hqKeyTemp;


    public void appIconTempDir(InputStream in, String filename, String Projectname, String platform){
        // app icon temp upload start
        File appIconfile = new File(systemUserHomePath + userRootPath+"tempZipDir/"+filename);

        try {
            FileUtils.copyInputStreamToFile(in, appIconfile);
            // 앱 아이콘 업로드 완료
        } catch (IOException e) {

            log.error("builder appicon service error ",e);
        }

    }

    public void setAppIconGeneratorCLI(WebSocketSession session, Map<String, Object> parseResult){

        commandLineAppIconGeneratorParameter(session, parseResult);

    }

    public void setiOSAppIconGeneratorCLI(WebSocketSession session, Map<String, Object> parseResult){

        commandLineiOSMatrixManagerAppIconGeneratorParam(session, parseResult);

    }

    // app icon generator cli
    private void commandLineAppIconGeneratorParameter(WebSocketSession session, Map<String, Object> parseResult){

        // android project ID full path
        String path = systemUserHomePath + userRootPath +"builder_main/"+ BuilderDirectoryType.DOMAIN_ + parseResult.get("domainID").toString() + "/" + BuilderDirectoryType.USER_ + parseResult.get("userID").toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W + parseResult.get("workspaceID").toString() + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get("projectID").toString() + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get("projectID").toString();

        // gradle appIconGenerator full cli
        // s= >> 저장된 앱아이콘 이미지 파일 경로
        // p= >> icon generator 될 디렉토리 경로
        String cmdStr = "cd " + path + " && ./gradlew --stop && ./gradlew appIconGenerator -P s="+systemUserHomePath + profileSetDir +parseResult.get("userID").toString() +"/"+ parseResult.get("appIconFileName").toString() + " -P d="+ path + androidAppIconProject;
        log.info(cmdStr);
        CommandLine commandLineAppIconGeneraterCLI = CommandLine.parse("/bin/sh");
        commandLineAppIconGeneraterCLI.addArgument("-c");
        commandLineAppIconGeneraterCLI.addArgument(cmdStr, false);

        // gradle 명령어 execute 실행 구간
        executueCommonsCLIToTemplateCopy(commandLineAppIconGeneraterCLI, "");


    }

    private void commandLineiOSMatrixManagerAppIconGeneratorParam(WebSocketSession session, Map<String, Object> parseResult){

        try {
            JSONObject jsonDeployObj = (JSONObject) parser.parse(parseResult.get("jsonDeployObj").toString());

            String path = systemUserHomePath + userRootPath +"builder_main/"+ BuilderDirectoryType.DOMAIN_ + parseResult.get("domainID").toString() + "/" + BuilderDirectoryType.USER_ + parseResult.get("userID").toString() +
                    "/" + BuilderDirectoryType.WORKSPACE_W + parseResult.get("workspaceID").toString() + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get("projectID").toString() + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get("projectID").toString();

            String cmdStr = "wmatrixmanager appIconGenerator -s "+systemUserHomePath + profileSetDir +parseResult.get("userID").toString() +"/"+ parseResult.get("appIconFileName").toString() + " -d "+ path + "/" + jsonDeployObj.get("all_package_name").toString() + iosAppIconProject;
            CommandLine commandLineAppIconGeneratoriOSCLI = CommandLine.parse(cmdStr);
//            commandLineAppIconGeneratoriOSCLI.addArgument("appIconGenerator");
//            commandLineAppIconGeneratoriOSCLI.addArgument("s="+systemUserHomePath + profileSetDir +parseResult.get("userID").toString() +"/"+ parseResult.get("appIconFileName").toString());
//            commandLineAppIconGeneratoriOSCLI.addArgument("d="+ path + "/" + jsonDeployObj.get("all_package_name").toString() + iosAppIconProject);

            executueCommonsCLIToTemplateCopy(commandLineAppIconGeneratoriOSCLI, "");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    private void executueCommonsCLIToTemplateCopy(CommandLine commandLineParse, String commandType){

        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            while ((tmp = is.readLine()) != null) {
                log.info(" #### Gradle app icon generater CLI CommandLine log data ### : " + tmp);

            }

            is.close();
            resultHandler.waitFor();


            int exitCode = resultHandler.getExitValue();

            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);

            }else if(exitCode == 1){
                log.info(" exitCode : {}", exitCode);
            }else {
                log.info(" exitCode : {}", exitCode);

            }

            handler.stop();

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

        }

    }

    private void executueVCSCommandALL(WebSocketSession session, CommandLine commandLineParse, String VcsType, String commmandOrder){

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        projectGitCloneMessage.setHqKey(hqKeyTemp);

        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutResult = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
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
                projectCreateMessage(session, projectGitCloneMessage,"","GITADD");
            }else if (commmandOrder.toLowerCase().equals("gitcommit")){
                projectCreateMessage(session, projectGitCloneMessage,"","GITCOMMIT");
            }else if (commmandOrder.toLowerCase().equals("gitpush")){
                projectCreateMessage(session, projectGitCloneMessage,"","GITPUSH");
            }else if (commmandOrder.toLowerCase().equals("svn")){
                projectCreateMessage(session, projectGitCloneMessage,"","SVNCHECKOUT");
            }else if (commmandOrder.toLowerCase().equals("svnadd")){
                projectCreateMessage(session, projectGitCloneMessage,"","SVNADD");
            }else if (commmandOrder.toLowerCase().equals("svncommit")){
                projectCreateMessage(session, projectGitCloneMessage,"","SVNCOMMIT");
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

                // App Icon Setting 구간 시작..
                if(VcsType.toLowerCase().equals("mkdir")){

                }else{


                }

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {

            log.error("builder appicon service error ",e);
        } catch (InterruptedException e) {

            log.error("builder appicon service error ",e);
        }

    }

    public void appIconImageLoadClientPath(String platform, String workspaceName, String projectName){


        try (Stream<Path> filePathStream= Files.walk(Paths.get(systemUserHomePath + userRootPath + workspaceName +"/"+ projectName + "/WHybridTemplate" +iosAppIconPath))) {
            MultiValueMap<String, Object> reqToFileObj =
                    new LinkedMultiValueMap<String, Object>();
            Base64.Encoder encoder = Base64.getEncoder();
            List<FileNameAwareByteArrayResource> fileArr = new ArrayList<>();

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());

                byte[] content = new byte[1024];


                Path path = Paths.get(systemUserHomePath + userRootPath + workspaceName +"/"+ projectName + "/WHybridTemplate" +iosAppIconPath + "/" + fileNameToString);
                try {
                    content = Files.readAllBytes(path);

                    // String encodedString =  encoder.encodeToString(content);
                    fileArr.add(new FileNameAwareByteArrayResource(fileNameToString, content, ""));
                } catch (IOException e) {

                    log.error("builder app icon service error",e);
                }

                //files.add(new ByteArrayResource(file.));

                log.info("buildAfterSendToHeadquaterFileObj filePath data : {} ", filePath);
                log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);
                //Path path = Paths.get(androidPath + buildPath + "/" + fileNameToString);

            });
            reqToFileObj.add("file", fileArr);


        } catch (IOException e) {

            log.error("builder app icon service error",e);
        }

    }

    // websocket file send 미사용.
    public void appIconImageLoadAction(String platform, String workspaceName, String projectName){

        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_logfile_name = null;
        byte[] byte_logfile_name_length = null;
        byte[] byte_logfile = null;
        byte[] byte_logfile_length = null;

        byte[] byte_ios_appicon_20_name = null;
        byte[] byte_ios_appicon_29_name = null;
        byte[] byte_ios_appicon_40_name = null;
        byte[] byte_ios_appicon_57_name = null;
        byte[] byte_ios_appicon_58_name = null;
        byte[] byte_ios_appicon_60_name = null;
        byte[] byte_ios_appicon_76_name = null;
        byte[] byte_ios_appicon_80_name = null;
        byte[] byte_ios_appicon_87_name = null;
        byte[] byte_ios_appicon_114_name = null;
        byte[] byte_ios_appicon_120_name = null;
        byte[] byte_ios_appicon_152_name = null;
        byte[] byte_ios_appicon_167_name = null;
        byte[] byte_ios_appicon_180_name = null;
        byte[] byte_ios_appicon_1024_name = null;

        byte[] byte_ios_appicon_20_name_length = null;
        byte[] byte_ios_appicon_29_name_length = null;
        byte[] byte_ios_appicon_40_name_length = null;
        byte[] byte_ios_appicon_57_name_length = null;
        byte[] byte_ios_appicon_58_name_length = null;
        byte[] byte_ios_appicon_60_name_length = null;
        byte[] byte_ios_appicon_76_name_length = null;
        byte[] byte_ios_appicon_80_name_length = null;
        byte[] byte_ios_appicon_87_name_length = null;
        byte[] byte_ios_appicon_114_name_length = null;
        byte[] byte_ios_appicon_120_name_length = null;
        byte[] byte_ios_appicon_152_name_length = null;
        byte[] byte_ios_appicon_167_name_length = null;
        byte[] byte_ios_appicon_180_name_length = null;
        byte[] byte_ios_appicon_1024_name_length = null;

        byte[] byte_ios_appicon_20 = null;
        byte[] byte_ios_appicon_20_length = null;
        byte[] byte_ios_appicon_29 = null;
        byte[] byte_ios_appicon_29_length = null;
        byte[] byte_ios_appicon_40 = null;
        byte[] byte_ios_appicon_40_length = null;
        byte[] byte_ios_appicon_57 = null;
        byte[] byte_ios_appicon_57_length = null;
        byte[] byte_ios_appicon_58 = null;
        byte[] byte_ios_appicon_58_length = null;
        byte[] byte_ios_appicon_60 = null;
        byte[] byte_ios_appicon_60_length = null;
        byte[] byte_ios_appicon_76 = null;
        byte[] byte_ios_appicon_76_length = null;
        byte[] byte_ios_appicon_80 = null;
        byte[] byte_ios_appicon_80_length = null;
        byte[] byte_ios_appicon_87 = null;
        byte[] byte_ios_appicon_87_length = null;
        byte[] byte_ios_appicon_114 = null;
        byte[] byte_ios_appicon_114_length = null;
        byte[] byte_ios_appicon_120 = null;
        byte[] byte_ios_appicon_120_length = null;
        byte[] byte_ios_appicon_152 = null;
        byte[] byte_ios_appicon_152_length = null;
        byte[] byte_ios_appicon_167 = null;
        byte[] byte_ios_appicon_167_length = null;
        byte[] byte_ios_appicon_180 = null;
        byte[] byte_ios_appicon_180_length  = null;
        byte[] byte_ios_appicon_1024 = null;
        byte[] byte_ios_appicon_1024_length = null;

        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;

        // 1. 파라미터 get
        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
            // 2. 프로젝트 조회
            // userRootPath + workspaceName + projectName + androidhdpiAppIconPath | androidmdpiAppIconPath | androidxhdpiAppIconPath;

            File fileDir = new File(systemUserHomePath + userRootPath + workspaceName + projectName + androidhdpiAppIconPath);
            File tempZipFile = new File("zipfile.zip");
            if (!fileDir.isDirectory())
                throw new IllegalArgumentException("Not a directory:  " + fileDir);

            String[] entries = fileDir.list();

            byte[] buffer = new byte[4096];
            int bytesRead;

            try {
                FileOutputStream zipfilestm = new FileOutputStream(tempZipFile);
                ZipOutputStream out = new ZipOutputStream(zipfilestm);

                for (int i = 0; i < entries.length; i++) {
                    File f = new File(fileDir, entries[i]);

                    if (f.isDirectory())
                        continue; // Ignore directory

                    //스트림으로 파일을 읽음
                    FileInputStream in = new FileInputStream(f); // Stream to read file

                    //zip파일을 만들기 위하여 out객체에 write하여 zip파일 생성
                    ZipEntry entry = new ZipEntry(f.getPath()); // Make a ZipEntry

                    out.putNextEntry(entry); // Store entry
                    while ((bytesRead = in.read(buffer)) != -1)
                        out.write(buffer, 0, bytesRead);
                    in.close();
                }
                out.closeEntry();


            } catch (FileNotFoundException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            } catch (IOException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

            // 4. zip file -> byte

            // 5. 완료 이후, send binary message to headquarter

            // user id
            try {

                byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
                message_length += byte_amdin_id.length;
                // user id length
                byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
                message_length += byte_admin_id_length.length;
                // logfile name
                byte_logfile_name = tempZipFile.getName().getBytes(StandardCharsets.UTF_8); // 수정 필요.
                message_length += byte_logfile_name.length;
                // logfile name length
                byte_logfile_name_length = convertIntToByteArray(byte_logfile_name.length);
                message_length += byte_logfile_name_length.length;
                // logfile

                // byte_logfile = FileUtils.readFileToByteArray(tempZipFile); // 수정 필요.
                byte_logfile = FileUtils.readFileToByteArray(tempZipFile); // file util 대신 base64 방식으로 처리 해서 byte 보낸다...
                ///
                String urlEncode = Base64.getUrlEncoder().encodeToString(tempZipFile.getPath().getBytes(StandardCharsets.UTF_8));
                log.info("Base64  urlEncode {}", urlEncode);
                message_length += byte_logfile.length;
                // logfile length
                byte_logfile_length = convertIntToByteArray(byte_logfile.length);
                message_length += byte_logfile_length.length;
                byte_message = new byte[message_length];

                System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);

                offset += byte_admin_id_length.length;
                System.arraycopy(byte_amdin_id, 0, byte_message, offset, byte_amdin_id.length);
                offset += byte_amdin_id.length;
                System.arraycopy(byte_logfile_name_length, 0, byte_message, offset, byte_logfile_name_length.length);
                offset += byte_logfile_name_length.length;
                System.arraycopy(byte_logfile_name, 0, byte_message, offset, byte_logfile_name.length);
                offset += byte_logfile_name.length;
                System.arraycopy(byte_logfile_length, 0, byte_message, offset, byte_logfile_length.length);
                offset += byte_logfile_name_length.length;
                System.arraycopy(byte_logfile, 0, byte_message, offset, byte_logfile.length);

                // websocket binary 대신 restTempatre 전송 시도 해보기...

                log.info(" zip file byte_message end ");
            } catch (IOException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        }else {
            // 2. 프로젝트 조회
            // userRootPath + workspaceName + projectName + iosAppIconPath;
            File fileDir = new File(systemUserHomePath + userRootPath + workspaceName +"/"+ projectName + "/WHybridTemplate" +iosAppIconPath);
            File tempZipFile = new File("zipfile.zip");

//            if (!fileDir.isDirectory())
//                throw new IllegalArgumentException("Not a directory:  " + fileDir);

            log.info(String.valueOf(fileDir));
            String[] entries = fileDir.list();
            log.info(String.valueOf(fileDir.list()));
            byte[] buffer = new byte[4096];
            int bytesRead;

            try {
                FileOutputStream zipfilestm = new FileOutputStream(tempZipFile);
                ZipOutputStream out = new ZipOutputStream(zipfilestm);

                for (int i = 0; i < entries.length; i++) {
                    File f = new File(fileDir, entries[i]);

                    if (f.isDirectory())
                        continue; // Ignore directory

                    //스트림으로 파일을 읽음
                    FileInputStream in = new FileInputStream(f); // Stream to read file

                    //zip파일을 만들기 위하여 out객체에 write하여 zip파일 생성
                    ZipEntry entry = new ZipEntry(f.getName()); // Make a ZipEntry

                    out.putNextEntry(entry); // Store entry
                    while ((bytesRead = in.read(buffer)) != -1)
                        out.write(buffer, 0, bytesRead);
                    in.close();
                }
                out.closeEntry();


            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }

            // 4. zip file -> byte
            // 5. 완료 이후, send binary message to headquarter
            // 6. file list byte, byte length 같이 변환

            // user id


            // logfile name
//                byte_logfile_name = tempZipFile.getName().getBytes(StandardCharsets.UTF_8); // 수정 필요.
//                message_length += byte_logfile_name.length;
//                // logfile name length
//                byte_logfile_name_length = convertIntToByteArray(byte_logfile_name.length);
//                message_length += byte_logfile_name_length.length;
//                // logfile
//
//                byte_logfile = FileUtils.readFileToByteArray(tempZipFile); // 수정 필요.
//                message_length += byte_logfile.length;
            // logfile length
//                byte_logfile_length = convertIntToByteArray(byte_logfile.length);
//                message_length += byte_logfile_length.length;


//                System.arraycopy(byte_logfile_name_length, 0, byte_message, offset, byte_logfile_name_length.length);
//                offset += byte_logfile_name_length.length;
//                System.arraycopy(byte_logfile_name, 0, byte_message, offset, byte_logfile_name.length);
//                offset += byte_logfile_name.length;
//                System.arraycopy(byte_logfile_length, 0, byte_message, offset, byte_logfile_length.length);
//                offset += byte_logfile_length.length;
//                System.arraycopy(byte_logfile, 0, byte_message, offset, byte_logfile.length);
//                offset += byte_logfile.length;

            log.info(" zip file byte_message end ");


            File dir = new File(systemUserHomePath + userRootPath + workspaceName +"/"+ projectName + "/WHybridTemplate" +iosAppIconPath + "/");
            File[] fileList = dir.listFiles();

            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;
            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            // bin typee
            byte_bin_type = BinaryServiceType.HV_BIN_APP_ICON_READ.toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            for(int i = 0 ; i < fileList.length ; i++){

                File file = fileList[i];
                if(file.isFile()){
                    try {
                        if(file.getName().equals("87.png")){
                            byte_ios_appicon_20_name = file.getName().getBytes(StandardCharsets.UTF_8);
                            message_length += byte_ios_appicon_20_name.length;
                            byte_ios_appicon_20_name_length = convertIntToByteArray(byte_ios_appicon_20_name.length);
                            message_length += byte_ios_appicon_20_name_length.length;

                            byte_ios_appicon_20 = FileUtils.readFileToByteArray(file);

                            String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_20);
                            log.info("Base64  urlEncode {}", urlEncode.length());
                            log.info("Base64  urlEncode {}", urlEncode);
                            byte[] decodeData = Base64.getDecoder().decode(urlEncode);
                            byte_ios_appicon_20 = decodeData;
                            log.info(" decodeData : {}",decodeData.length);

                            message_length += byte_ios_appicon_20.length;
                            byte_ios_appicon_20_length = convertIntToByteArray(byte_ios_appicon_20.length);
                            message_length += byte_ios_appicon_20_length.length;


                        }

                    } catch (IOException e) {

                    }
                    // 파일이 있다면 파일 이름 출력
                    // System.out.println("\t 파일 이름 = " + file.getName());
                    log.info(" 파일 이름  : {}",file.getName());

                }else if(file.isDirectory()){
                    log.info(" 디렉토리 이름  : {}",file.getName());
                    // System.out.println("디렉토리 이름 = " + file.getName());
                    // 서브디렉토리가 존재하면 재귀적 방법으로 다시 탐색
                    //subDirList(file.getCanonicalPath().toString());

                }

            }

            byte_message = new byte[message_length];

            System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
            offset += byte_admin_id_length.length;
            System.arraycopy(byte_amdin_id, 0, byte_message, offset, byte_amdin_id.length);
            offset += byte_amdin_id.length;
            System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
            offset += byte_bin_type_length.length;
            System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
            offset += byte_bin_type.length;

            System.arraycopy(byte_ios_appicon_20_name_length, 0, byte_message, offset, byte_ios_appicon_20_name_length.length);
            offset += byte_ios_appicon_20_name_length.length;
            System.arraycopy(byte_ios_appicon_20_name, 0, byte_message, offset, byte_ios_appicon_20_name.length);
            offset += byte_ios_appicon_20_name.length;
            System.arraycopy(byte_ios_appicon_20_length, 0, byte_message, offset, byte_ios_appicon_20_length.length);
            offset += byte_ios_appicon_20_length.length;
            System.arraycopy(byte_ios_appicon_20, 0, byte_message, offset, byte_ios_appicon_20.length);


            headQuaterClientHandler.sendMessage(new BinaryMessage(byte_message));
            byte[] content = new byte[1024];

            // String encodedString =  encoder.encodeToString(content);
            // logfile name

//            byte_ios_appicon_20_name = fileNameToString.getBytes(StandardCharsets.UTF_8);
//            message_length += byte_ios_appicon_20_name.length;
//
//            // logfile name length
//            byte_ios_appicon_20_name_length = convertIntToByteArray(byte_ios_appicon_20_name.length);
//            message_length += byte_ios_appicon_20_name_length.length;

            // content = Files.readAllBytes(path);


        }

    }

    // public void sendAppIconImageToBase64(WebSocketSession session, String platform, String workspaceName, String projectName, String projectDir, String hqKey){
    @Async("asyncThreadPool")
    public void sendAppIconImageToBase64(WebSocketSession session, Map<String, Object> parseResult, String hqKey){
        File fileDir = null;
        File[] fileList = null;
        String[] entries = null;
        ByteArrayOutputStream byteOutStream = null;
        JSONObject sendHeadquaterObj = new JSONObject();
        JSONObject sendImageObj = new JSONObject();

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String domainID = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userID =  BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
        String projectDir = parseResult.get("project_dir").toString();
        String packageName = parseResult.get("packageName").toString();


        ProjectSettingImageMessage projectSettingImageMessage = new ProjectSettingImageMessage();

        byte[] byte_ios_appicon_29 = null;
        byte[] byte_ios_appicon_40 = null;
        byte[] byte_ios_appicon_57 = null;
        byte[] byte_ios_appicon_58 = null;
        byte[] byte_ios_appicon_60 = null;
        byte[] byte_ios_appicon_76 = null;
        byte[] byte_ios_appicon_80 = null;
        byte[] byte_ios_appicon_87 = null;
        byte[] byte_ios_appicon_114 = null;
        byte[] byte_ios_appicon_120 = null;
        byte[] byte_ios_appicon_152 = null;
        byte[] byte_ios_appicon_167 = null;
        byte[] byte_ios_appicon_180 = null;
        byte[] byte_ios_appicon_1024 = null;




        try {

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                if(projectDir.toLowerCase().equals("")){
                    fileDir = new File(systemUserHomePath + userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspaceName +"/"+ projectName +androidxhdpiAppIconPath); // 03_WHive_Presentation
                }else{
                    fileDir = new File(systemUserHomePath + userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspaceName +"/"+ projectName + "/" + projectDir +androidxhdpiAppIconPath); // 03_WHive_Presentation
                }

                fileList = fileDir.listFiles();
                sendImageObj.put(PayloadMsgType.platform.name(),"Android");

            }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                if(projectDir.toLowerCase().equals("")){
                    fileDir = new File(systemUserHomePath + userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspaceName +"/"+ projectName + "/" + packageName+iosAppIconPath); // 03_WHive_Presentation
                }else{
                    fileDir = new File(systemUserHomePath + userRootPath + "builder_main/" + domainID + "/" + userID + "/" + workspaceName +"/"+ projectName + "/" + projectDir + "/" + packageName+iosAppIconPath); // 03_WHive_Presentation
                }

                fileList = fileDir.listFiles();
                sendImageObj.put(PayloadMsgType.platform.name(),"iOS");

            }

            for(int i = 0 ; i < fileList.length ; i++){

            File file = fileList[i];
            if(file.isFile()){
                try {

                    // 29, 40, 57, 58, 60, 76, 80, 114, 120, 152, 167, 180, 1024, logo
                    if(file.getName().equals("logo.png")){
                        byte_ios_appicon_29 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_29);
                        sendImageObj.put("imageurl_logo",urlEncode);
                    }

                    if(file.getName().equals("29.png")){
                        byte_ios_appicon_29 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_29);
                        sendImageObj.put("imageurl_29",urlEncode);
                    }

                    if(file.getName().equals("40.png")){
                        byte_ios_appicon_40 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_40);
                        sendImageObj.put("imageurl_40",urlEncode);
                    }

                    if(file.getName().equals("57.png")){
                        byte_ios_appicon_57 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_57);
                        sendImageObj.put("imageurl_57",urlEncode);
                    }

                    if(file.getName().equals("58.png")){
                        byte_ios_appicon_58 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_58);
                        sendImageObj.put("imageurl_58",urlEncode);
                    }

                    if(file.getName().equals("60.png")){
                        byte_ios_appicon_60 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_60);
                        sendImageObj.put("imageurl_60",urlEncode);
                    }

                    if(file.getName().equals("76.png")){
                        byte_ios_appicon_76 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_76);
                        sendImageObj.put("imageurl_76",urlEncode);
                    }

                    if(file.getName().equals("80.png")){
                        byte_ios_appicon_80 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_80);
                        sendImageObj.put("imageurl_80",urlEncode);
                    }

                    if(file.getName().equals("87.png")){

                        byte_ios_appicon_87 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_87);
                        sendImageObj.put("imageurl_87",urlEncode);

                    }

                    if(file.getName().equals("114.png")){

                        byte_ios_appicon_114 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_114);
                        sendImageObj.put("imageurl_114",urlEncode);

                    }

                    if(file.getName().equals("120.png")){

                        byte_ios_appicon_120 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_120);
                        sendImageObj.put("imageurl_120",urlEncode);

                    }

                    if(file.getName().equals("152.png")){

                        byte_ios_appicon_152 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_152);
                        sendImageObj.put("imageurl_152",urlEncode);

                    }

                    if(file.getName().equals("167.png")){

                        byte_ios_appicon_167 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_167);
                        sendImageObj.put("imageurl_167",urlEncode);

                    }

                    if(file.getName().equals("180.png")){

                        byte_ios_appicon_180 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_180);
                        sendImageObj.put("imageurl_180",urlEncode);

                    }


                    if(file.getName().equals("1024.png")){

                        byte_ios_appicon_1024 = FileUtils.readFileToByteArray(file);

                        String urlEncode = Base64.getEncoder().encodeToString(byte_ios_appicon_1024);
                        sendImageObj.put("imageurl_1024",urlEncode);

                    }


                } catch (IOException e) {

                }
                // 파일이 있다면 파일 이름 출력
                // System.out.println("\t 파일 이름 = " + file.getName());
                log.info(" 파일 이름  : {}",file.getName());



                }else if(file.isDirectory()){
                    log.info(" 디렉토리 이름  : {}",file.getName());
                    // System.out.println("디렉토리 이름 = " + file.getName());
                    // 서브디렉토리가 존재하면 재귀적 방법으로 다시 탐색
                    //subDirList(file.getCanonicalPath().toString());

                }



            }

            // set AppIcon image List
            projectSettingImageMessage.setImageList(sendImageObj);
            projectSettingImageMessage.setHqKey(hqKey);

            projectSettingMessage(session, projectSettingImageMessage);

        }catch (NullPointerException ex){

            log.error("builder appicon service error ",ex);
        }


    }

    private void projectCreateMessage(WebSocketSession session, ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){


        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        // projectGitCloneMessage.setHqKey("");

        if (gitStatus.equals("GITCLONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("SVNCHECKOUT")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }else if(gitStatus.equals("APPICONUNZIP")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("GITCOMMIT")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("GITADD")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("GITPUSH")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("DONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    private void projectSettingMessage(WebSocketSession session, ProjectSettingImageMessage projectSettingImageMessage){

        projectSettingImageMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_APP_CONFIG_IMAGE_LIST_INFO.name());
        projectSettingImageMessage.setSessType(PayloadMsgType.HEADQUATER.name());

        projectSettingImageMessage.setStatus("");

        Map<String, Object> parseResult = Mapper.convertValue(projectSettingImageMessage, Map.class);
        // session 객체 추가
        headQuaterClientHandler.sendMessage(session, parseResult);
        //headQuaterClientHandler.sendMessage(parseResult);

    }

    // util 로 하나의 객체에 넣어두기
    // int to byteArray 변환
//    private byte[] convertIntToByteArray(int value) {
//        byte[] byteArray = new byte[4];
//        byteArray[0] = (byte)(value >> 24);
//        byteArray[1] = (byte)(value >> 16);
//        byteArray[2] = (byte)(value >> 8);
//        byteArray[3] = (byte)(value);
//        return byteArray;
//    }

    private Boolean setTimeout(int delayTime){
        long now = System.currentTimeMillis();
        long currentTime = 0;
        while( currentTime - now< delayTime){
            currentTime  = System.currentTimeMillis();
        }
        return true;
    }

}
