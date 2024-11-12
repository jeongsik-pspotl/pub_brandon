package com.inswave.whive.branch.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.inswave.whive.branch.BranchClient;
import com.inswave.whive.branch.domain.ProjectTemplateCreateStatusMessage;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.GitTask;
import com.inswave.whive.branch.task.SvnTask;
import com.inswave.whive.branch.util.SSHClientUtil;
import com.inswave.whive.branch.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

// project import service
@Slf4j
@Component
public class ProjectImportService extends BaseService{

    @Value("${whive.distribution.UserRootPath}")
    private String UserRootPath;

    @Value("${whive.distribution.UserGitRepositoryPath}")
    private String UserGitRepositoryPath;

    @Value("${whive.distribution.UserSvnRepositoryPath}")
    private String UserSvnRepositoryPath;

    @Value("${whive.distribution.profilePath}")
    private String profileSetDir;

    @Value("${whive.distribution.AndroidAppIconPath}")
    private String androidxhdpiAppIconPath;

    // ios AppIcon Path
    @Value("${whive.distribution.iOSAppIconPath}")
    private String iosAppIconPath;

    @Value("${spring.profiles}")
    private String springProfileMode;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    ProjectGitBareCloneCreateService projectGitBareCloneCreateService;

    @Autowired
    SigningKeyService signingKeyService;

    @Autowired
    SSHClientUtil sshClientUtil;

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Autowired
    AppIconService appIconService;

    @Autowired
    ProjectGitPushService projectGitPushService;

    @Autowired
    BuildDependencyService buildDependencyService;

    @Autowired
    MultiProfileService multiProfileService;

    @Autowired
    CreateProjectService createProjectService;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    BranchClient branchClient;

    @Autowired
    private ZipUtils zipUtils;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    private ObjectMapper Mapper = new ObjectMapper();
    private JSONParser parser = new JSONParser();

    // deployhistory root path

    public void startProjectLocalGitImport(MultipartFile importZipFile, JSONObject parseResult){

        WebSocketSession session = (WebSocketSession) branchClient.session;

        log.info(" import ProjectGitHubCloneCLI : check... ");
        ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage = new ProjectTemplateCreateStatusMessage();
        CommandLine commandLineTemplateMkdir;
        CommandLine commandLineGitRepositoryMkdir;
        CommandLine commandLineVCSBareCLI;

        String UserProjectPath;
        String platformZip;
        String hqKeyTemp; // 수정
        String arrayJSONProfilesStr;
        String arrayJSONCertificateStr;
        String getProjectNamePath = "";
        JSONObject signingKeyJson = null;
        JSONObject repositoryJson = null;

        JSONObject jsonProfileDebug;
        JSONObject jsonProfileRelease;
        JSONObject iosProfileBuildSettings;

        Gson gson = new Gson();

        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();

        WebSocketSession sessionTemp; // 수정
        String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");
        //String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh"); // 삭제

        try {
            repositoryJson = (JSONObject) parser.parse(parseResult.get("jsonRepository").toString());
            signingKeyJson = (JSONObject) parser.parse(parseResult.get("jsonSigningkey").toString());

        String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String product_type = parseResult.get("product_type").toString();

        String language = "";
        if (product_type.toLowerCase().equals("wmatrix")) {
            language = parseResult.get("language").toString();
        }

        String build_id = parseResult.get(PayloadMsgType.projectID.name()).toString();
        String arrayServerConfigStr = ""; //parseResult.get("serverConfig").toString(); // server config

        UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();

        hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();
        sessionTemp = session;

        projectTemplateCreateStatusMessage.setBuild_id(Integer.parseInt(build_id));
        projectTemplateCreateStatusMessage.setProjectDirPath(projectName);
        projectTemplateCreateStatusMessage.setHqKey(hqKeyTemp);

        // 전역 변수 값 저장
        // globalParameterSetting(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj, builderSettingObj);

        // language set
        // applyConfigObj.put("language",language);
        projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","FILEUPLOAD");
        // project zip file upload
        String copyToProjectImportZipFilePath = systemUserHomePath + UserRootPath + "tmpfiles/"+ BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() ;
        String zipToProjectImportFile = systemUserHomePath + UserRootPath + "tmpfiles/"+ BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +"/" + importZipFile.getOriginalFilename();

        File zipFileTmp = new File(copyToProjectImportZipFilePath);
        File importzipFile = new File(zipToProjectImportFile);
        if ( zipFileTmp != null && !zipFileTmp.exists()) {
              zipFileTmp.mkdir(); //폴더 생성합니다.
              //System.out.println("폴더가 생성되었습니다.");
        }else {
            // System.out.println("이미 폴더가 생성되어 있습니다.");
        }

        importzipFile.createNewFile();
        importZipFile.transferTo(importzipFile);

        log.info("1 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // mkdir cli 수행
        // message mkdir
        projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","MKDIR");

        // vcs 프로젝트 경로 생성
        File vcsWorkspaceDir = new File(UserGitRepositoryPath + domainName + "/" + userName + "/" + workspaceName);

        if (!vcsWorkspaceDir.exists()) {
            try {
                vcsWorkspaceDir.mkdirs();
                log.info("create Workspace Dir");
            } catch (Exception e) {
                log.error("Error in Create Workspace Dir {}", e.getMessage(), e);
            }
        }

//        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
//        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
//        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
//        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName); // 3
//        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir");

        String projectDirPath = systemUserHomePath + UserRootPath + "builder_main/" + domainName + "/" + userName + "/" + workspaceName + "/" + projectName;
        File projectDir = new File(projectDirPath);
        if (!projectDir.exists()) {
            try {
                projectDir.mkdirs();
                log.info("create Project Dir");
            } catch (Exception e) {
                log.error("Error in Create Project Dir {}", e.getMessage(), e);
            }
        }

//        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
//        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
//        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
//        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName +"/build_logfiles"); // 3
//        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

        File buildLogFilesDir = new File(projectDirPath + "/build_logfiles");
        if (!buildLogFilesDir.exists()) {
            try {
                buildLogFilesDir.mkdirs();
                log.info("create build_logfiles Dir");
            } catch (Exception e) {
                log.error("Error in Create build_logfiles Dir {}", e.getMessage(), e);
            }
        }

//        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
//        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
//        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
//        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName +"/appfiles"); // 3
//        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

        File appfilesDir = new File(projectDirPath + "/appfiles");
        if (!appfilesDir.exists()) {
            try {
                appfilesDir.mkdirs();
                log.info("create appfiles Dir");
            } catch (Exception e) {
                log.error("Error in Create appfiles Dir {}", e.getMessage(), e);
            }
        }

        CommandLine commandLineProjectChmod = CommandLine.parse("chmod");
        commandLineProjectChmod.addArgument("-R");
        commandLineProjectChmod.addArgument("777");
        commandLineProjectChmod.addArgument(systemUserHomePath + UserRootPath + "builder_main/"+domainName +"/"+userName +"/"+workspaceName+"/"+projectName);
        executueCommonsCLIToTemplateCopy(commandLineProjectChmod,"chmod"); // method name 수정 필요.

        String vcsType = repositoryJson.get("vcsType").toString();

        if (vcsType.contains("git")) {
            // 1.1 mkdir git repository
            commandLineGitRepositoryMkdir = CommandLine.parse(shellscriptFileName);
            commandLineGitRepositoryMkdir.addArgument("createGitRepository"); // 1 createGitRepository
            commandLineGitRepositoryMkdir.addArgument(UserGitRepositoryPath + domainName + "/" + userName + "/" + workspaceName); // 2
            commandLineGitRepositoryMkdir.addArgument(projectName + ".git"); // 3
            executueCommonsCLIToTemplateCopy(commandLineGitRepositoryMkdir, "mkdir"); // method name 수정 필요.

            CommandLine commandLineGitChmod = CommandLine.parse("chmod");
            commandLineGitChmod.addArgument("-R");
            commandLineGitChmod.addArgument("775");
            commandLineGitChmod.addArgument(UserGitRepositoryPath + domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
            executueCommonsCLIToTemplateCopy(commandLineGitChmod, "chmod"); // method name 수정 필요.
        }

        // message git bare create
        // 2.1 git bare 시작
        projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","GITBARE");
        try {
            if (vcsType.contains("git")) {
                // git init --bare set
                gitTask.gitWithSSH("bareInit", UserGitRepositoryPath, domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
            } else if (vcsType.contains("svn")) {
                String repositoryId = repositoryJson.get("repositoryId").toString();
                String repositoryPassword = repositoryJson.get("repositoryPassword").toString();

                svnTask.svnCreate(repositoryId, repositoryPassword, new URI(UserSvnRepositoryPath + domainName + "/" + userName + "/" + workspaceName + "/" + projectName));
            }
        } catch (Exception e) {
            log.error("Git Init, Svn Create Error = {}", e.getLocalizedMessage(), e);
        }

        // message git clone
        projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","GITCLONE");

        // 2.2 git 초기 repository clone
        try {
            if (vcsType.contains("git")) {
                gitTask.gitClone(parseResult, UserProjectPath + "/" + projectName, repositoryJson);
            } else if (vcsType.contains("svn")) {
                String repositoryId = repositoryJson.get("repositoryId").toString();
                String repositoryPassword = repositoryJson.get("repositoryPassword").toString();

                svnTask.svnCheckout(repositoryId, repositoryPassword, new URI("/" + domainName + "/" + userName + "/" + workspaceName + "/" + projectName), new URI(UserProjectPath + "/" + projectName));

            }
        } catch (Exception e) {
            log.error("Git clone, SVN checkout Error = {}", e.getLocalizedMessage(), e);
        }

        // message project copy
        projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","PROJCOPY");
        // product type 수정 하기
        String resultZipfileName = copyImportProjectZipFile(systemUserHomePath + UserRootPath + "tmpfiles/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString(), "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/"+projectName+"/"+projectName, language);

        // unzip 도 java 로 처리하기
        // 3. unzip
        // param : UserRootPath + projectName, fileName
        unzipTemplateDirAndFile(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" +projectName + "/" +projectName +"/" +resultZipfileName, systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" +projectName+ "/" +projectName);

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            CommandLine commandLine = CommandLine.parse("chmod");
            commandLine.addArgument("-R");
            commandLine.addArgument("755");
            commandLine.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectName + "/gradlew");

            executueCommonsCLIToTemplateCopy(commandLine, "chmod");

            // fastlane 내부 권한 수정
            CommandLine commandLineFastlaneChmod = CommandLine.parse("chmod");
            commandLineFastlaneChmod.addArgument("-R");
            commandLineFastlaneChmod.addArgument("777");
            commandLineFastlaneChmod.addArgument(systemUserHomePath + UserRootPath + "builder_main/"+domainName +"/"+userName +"/"+workspaceName+"/"+projectName + "/" + projectName + "/fastlane");
            executueCommonsCLIToTemplateCopy(commandLineFastlaneChmod,"chmod");

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
            CommandLine commandLine = CommandLine.parse("chmod");
            commandLine.addArgument("-R");
            commandLine.addArgument("755");
            commandLine.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectName + "/archive.sh");

            executueCommonsCLIToTemplateCopy(commandLine, "chmod");

            CommandLine commandLineProj = CommandLine.parse("chmod");
            commandLineProj.addArgument("-R");
            commandLineProj.addArgument("777");
            commandLineProj.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectName);

            executueCommonsCLIToTemplateCopy(commandLineProj, "chmod");


            CommandLine commandLineMACOSRm = CommandLine.parse("rm");
            commandLineMACOSRm.addArgument("-rf");
            commandLineMACOSRm.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectName +"/__MACOSX");

            executueCommonsCLIToTemplateCopy(commandLineMACOSRm, "rm");
        }

        try {
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())) {
                createLocalPropertiesFile(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName+ "/" + projectName); // local properties file setting
                if(springProfileMode.equals("tomcat_builder")){
                    // gradle propertise file 건들지 않기
                }else {
                    // 분가처리
                    updateGradlePropertiesFile(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName+ "/" + projectName); // gradle.java.home setting

                    if (product_type.toLowerCase().equals("generalapp")) {
                        signingKeyService.generalAndroidAppSigning(UserProjectPath+"/"+projectName, parseResult.get("jsonSigningkey").toString());
                    }
                }

            }

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

        }

        log.info("2 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // 5. app config 설정
        // serverConfig 추가
        // project path 조회 기능 추가
        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
            getProjectNamePath = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platform, UserProjectPath, projectName);
            parseResult.put("getProjectNamePath",getProjectNamePath);
        }else {

        }


        // 6. signing key 설정, deploy profile 설정
        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
            signingKeyService.createSigningkeyFileToProperties(UserProjectPath+"/"+projectName, platformZip, signingKeyJson);

            // android deploy profile 설정
            deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);

            importzipFile.delete(); // import zip file 삭제

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();
            arrayJSONCertificateStr = parseResult.get("arrayJSONCertificates").toString();

            if (product_type.toLowerCase().equals("wmatrix")) {
                JsonArray toJsonBuildSettingsGson = new JsonArray();

                JSONArray jsonAllTargetConfig = multiProfileService.getiOSAllMultiProfileList(parseResult);
                parseResult.put("multiProfileList", jsonAllTargetConfig);

                // ios get config ..
                JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platform, UserProjectPath + "/" + projectName);

                parseResult.put("multiProfileConfig", jsonAppConfig);

                mobileTemplateConfigService.importiOSMultiProfileConfigSettingALLCLI(session, parseResult, UserProjectPath + "/" + projectName);


                JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("targets");
                String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

                log.info(jsonAppConfig.toJSONString());
                log.info(jsonMatrix.toJSONString());
                log.info(arrayJSONProfilesStr);
                log.info(arrayJSONCertificateStr);
                log.info(targetKeyList[0]);

                if(language.toLowerCase().equals("swift")){
                    signingKeyService.importiOSProvisioningFileMultiProfilesSetting(UserProjectPath+"/"+projectName, arrayJSONProfilesStr, product_type, targetKeyList, toJsonBuildSettingsGson);
                }else if(language.toLowerCase().equals("objc")){
                    // signingKeyService.createProfileSettingToDebugProject(UserProjectPath+"/"+projectName, jsonProfileDebug, product_type);
                    // signingKeyService.createProfileSettingToReleaseProject(UserProjectPath+"/"+projectName, jsonProfileRelease, product_type);
                }
            }
            // set cli signingkey
            // matrix 제품 타입으로 변경
            signingKeyService.createSigningkeyToMultiCertFile(UserProjectPath, arrayJSONCertificateStr, product_type);

            // debug, release profile file send ...
            // set cli profile
            String tempProfilesOneStr = arrayJSONProfilesStr;

//            tempProfilesOneStr = tempProfilesOneStr.replaceAll(" ","");
            tempProfilesOneStr = tempProfilesOneStr.replace("[{","");
            tempProfilesOneStr = tempProfilesOneStr.replace("}]","");

            String[] jsonProfilesArray = tempProfilesOneStr.split("\\},\\{");

            for(int p = 0; p < jsonProfilesArray.length; p++){
                jsonProfilesArray[p] = "{" + jsonProfilesArray[p] + "}";
                try {
                    JSONObject jsonProfile = (JSONObject) parser.parse(jsonProfilesArray[p]);

                    // show profiles
                    // JSONObject resultShwoProfiles = signingKeyService.showProfileOneInfo(session, jsonProfile.get("profiles_path").toString(), product_type);
                    // log.info(resultShwoProfiles.toJSONString());
                    // signingKeyService.setExportOptionsMultiProfiles(session, UserProjectPath+"/"+projectName, resultShwoProfiles, jsonProfile, product_type);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }

            /**
             * ios pod 파일 있을때 pod install 명령어 수행하는 기능
             */
            buildDependencyService.validatePodFileInstall(UserProjectPath+"/"+projectName);

            // ios deploy profile 설정
            deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);

            importzipFile.delete(); // import zip file 삭제

        }

        try {
            if (vcsType.contains("git")) {
                // message Git push
                projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","GITPUSH");
                // 7. git add, commit
                gitTask.gitPush(parseResult, UserProjectPath + "/" + projectName, repositoryJson, "init first commit");
            } else if (vcsType.contains("svn")) {
                String repositoryId = repositoryJson.get("repositoryId").toString();
                String repositoryPassword = repositoryJson.get("repositoryPassword").toString();
                svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                svnTask.svnCommit(repositoryId, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "init first commit");
            }
        } catch (Exception e) {
            log.error("Git clone, SVN checkout Error = {}", e.getLocalizedMessage(), e);
        }



        // send message template(Project) 생성 완료 메시지 전송
        projectImportMessage(sessionTemp,projectTemplateCreateStatusMessage, "","DONE");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startProjectGitImport(MultipartFile importZipFile, JSONObject parseResult){

        WebSocketSession session = (WebSocketSession) branchClient.session;

        log.info(" import ProjectGitHubCloneCLI : check... ");
        ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage = new ProjectTemplateCreateStatusMessage();
        CommandLine commandLineTemplateMkdir;
        CommandLine commandLineGitRepositoryMkdir;
        CommandLine commandLineVCSBareCLI;

        String UserProjectPath;
        String platformZip;
        String hqKeyTemp; // 수정
        String arrayJSONProfilesStr;
        String arrayJSONCertificateStr;
        String getProjectNamePath = "";
        JSONObject signingKeyJson = null;
        JSONObject repositoryJson = null;

        JSONObject jsonProfileDebug;
        JSONObject jsonProfileRelease;
        JSONObject iosProfileBuildSettings;

        Gson gson = new Gson();

        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();

        WebSocketSession sessionTemp; // 수정
        String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");
        //String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh"); // 삭제

        try {
            repositoryJson = (JSONObject) parser.parse(parseResult.get("jsonRepository").toString());
            signingKeyJson = (JSONObject) parser.parse(parseResult.get("jsonSigningkey").toString());

            String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString(); //

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            // String templateVersion = parseResult.get("templateVersion").toString();

            String product_type = parseResult.get("product_type").toString();
            if (product_type.toLowerCase().equals("wmatrix")) {
                String language = parseResult.get("language").toString();
            }
            // String appIconFileName = parseResult.get("appIconFileName").toString();
            String build_id = parseResult.get(PayloadMsgType.projectID.name()).toString();
            String arrayServerConfigStr = ""; //parseResult.get("serverConfig").toString(); // server config
            // String arrayMultiProfileConfigStr = parseResult.get("BuildSettings").toString();

            UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                    "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
            platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();

            //jsonProfileDebug = (JSONObject) parser.parse(parseResult.get("jsonProfileDebug").toString());
            //jsonProfileRelease = (JSONObject) parser.parse(parseResult.get("jsonProfileRelease").toString());

            hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            sessionTemp = session;

            projectTemplateCreateStatusMessage.setBuild_id(Integer.parseInt(build_id));
            projectTemplateCreateStatusMessage.setProjectDirPath(projectName);
            projectTemplateCreateStatusMessage.setHqKey(hqKeyTemp);

            /**
             *  project directory 생성
             */
            createProjectService.createWorkspaceStartCLI(BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                    "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString());

            projectImportMessage(sessionTemp, projectTemplateCreateStatusMessage, "","FILEUPLOAD");

            /**
             *  Git Server clone 시작 ..
             */
            gitTask.gitClone(parseResult, UserProjectPath + "/" + projectName, repositoryJson);

            executueGitCloneToImportAdd(session, projectTemplateCreateStatusMessage, "git", parseResult, importZipFile); // method name 수정 필요.


            log.info("1 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
            // mkdir cli 수행
            // message mkdir
            // message git bare create
            // 2.1 git bare 시작

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // local.properties 파일 생성 method
    private void createLocalPropertiesFile(String path) throws IOException {
        String buildAfterLogFile = path+"/local.properties";

        File f = new File(buildAfterLogFile);
        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);
        String android_home = System.getProperty("ANDROID_HOME");
        log.info("android_home : {}", android_home);
        out.append("sdk.dir="+android_home+"\n");

        out.flush();
        out.close();
    }

//    private void updateGradlePropertiesFile(String path) throws IOException {
//        String buildAfterLogFile = path+"/gradle.properties";
//
//        File f = new File(buildAfterLogFile);
//        FileWriter fstream = new FileWriter(f);
//        BufferedWriter out = new BufferedWriter(fstream);
//        String android_home = System.getProperty("GRADLE_JAVA_HOME"); // GRADLE_JAVA_HOME
//        out.append("org.gradle.java.home="+android_home+"\n");
//        out.append("org.gradle.jvmargs=-XX\\:MaxHeapSize\\=4608m -Xmx4608m\n");
//        out.append("android.useAndroidX=true\n");
//        out.append("android.enableJetifier=true\n");
//        out.append("android.enableR8.fullMode=false\n");
//        out.append("android.defaults.buildfeatures.buildconfig=true");
//
//        out.flush();
//        out.close();
//    }

    private void updateGradlePropertiesFile(String path) throws IOException {
        String buildAfterLogFile = path + "/gradle.properties";

        File f = new File(buildAfterLogFile);
        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);

        ArrayList<String> jdkVersion = new ArrayList<>(Arrays.asList("8", "11", "17"));
        String android_home = "";
        int index = jdkVersion.size() - 1;
        while (index >= 0) {
            android_home = getGradleJavaHome(path, jdkVersion, index);
            if (android_home.equals("")) {
                index -= 1;
            } else {
                break;
            }
        }

        if (index > 0) {
            android_home = System.getProperty("GRADLE_JAVA_HOME" + android_home);
            out.append("org.gradle.java.home=" + android_home + "\n");
            out.append("org.gradle.jvmargs=-XX\\:MaxHeapSize\\=4608m -Xmx4608m\n");
            out.append("android.useAndroidX=true\n");
            out.append("android.enableJetifier=true\n");
        } else {
            try {
                throw new Exception("현재 프로젝트에 맞는 Java 버전이 빌더에 제공되지 않았습니다.");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        out.flush();
        out.close();

    }

    private String getGradleJavaHome(String path, ArrayList<String> jdkVersion, int index) {
        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        String cmd;
        CommandLine commandLine;
        String android_home = "";
        try {
            cmd = "cd " + path + " && gradle wrapper -Dorg.gradle.java.home='/Library/Java/JavaVirtualMachines/zulu-" + jdkVersion.get(index) + ".jdk/Contents/Home'";
            commandLine = CommandLine.parse("/bin/sh");
            commandLine.addArgument("-c");
            commandLine.addArgument(cmd, false);

            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLine, resultHandler);

            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            log.info(" exitCode : {}", exitCode);
            handler.stop();
            if (exitCode > 0) {
                return android_home;
            } else {
                return jdkVersion.get(index);
            }
        } catch (Exception e) {
            log.error("gradle wrapper error = {}", e.getMessage(), e);
            return android_home;
        }
    }

    // 2.1 common cli mkdir
    private void executueCommonsCLIToTemplateCopy(CommandLine commandLineParse, String commandType){

        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
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
                // log.info(" #### Gradle git clone CLI CommandLine log data ### : " + tmp);
            }

            is.close();
            resultHandler.waitFor();

            resultHandler.getExitValue();

            handler.stop();

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    private void executueGitCloneToImportAdd(WebSocketSession session, ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage, String VcsType, JSONObject parseResult, MultipartFile zipFile){
        try {
            // vcs clone, checkout ... send message
            if (VcsType.toLowerCase().equals("git")){
                projectTemplateCreateStatusMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
                projectImportMessage(session, projectTemplateCreateStatusMessage,"","GITCLONE");
            }else if (VcsType.toLowerCase().equals("svn")){
                projectTemplateCreateStatusMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
                projectImportMessage(session, projectTemplateCreateStatusMessage,"","SVNCHECKOUT");
            }

            // App Icon Setting 구간 시작..
            if(VcsType.toLowerCase().equals("mkdir")){

            }else{

                Map<String, Object> parseResultObj;
                String UserProjectPath;
                String appIconZipFileName = "";
                String productType;
                String platformZip;
                String hqKeyTemp;
                String projectName;
                String projectDirName;
                String arrayServerConfigStr;
                String arrayJSONProfilesStr;
                String arrayJSONCertificateStr;
                String language = "";

                JSONObject signingKeyJson;
                JSONObject applyConfigJson;
                JSONObject repositoryJson;
                JSONObject jsonProfileDebug;
                JSONObject jsonProfileRelease;
                JSONObject appendBuildSettings;

                Gson gson = new Gson();

                JsonArray appconfigMultiArrayJson = new JsonArray();

                String jsonRepository = parseResult.get("jsonRepository").toString();
                String jsonSigningkey = parseResult.get("jsonSigningkey").toString();
                String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();

                productType = parseResult.get("product_type").toString();
                if (productType.toLowerCase().equals("wmatrix")) {
                    language = parseResult.get("language").toString();
                }
                parseResultObj = parseResult;

                UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"
                        + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString()
                        + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString() ; // 해당 구간 수정 필요


                platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();
                projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
                projectDirName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
                signingKeyJson = (JSONObject) parser.parse(jsonSigningkey);
                applyConfigJson = (JSONObject) parser.parse(jsonApplyConfig);
                repositoryJson = (JSONObject) parser.parse(jsonRepository);
                // String arrayMultiProfileConfigStr = parseResult.get("BuildSettings").toString();

                //appIconZipFileName = repositoryJson.get("zipFileName").toString();
                String zipToProjectImportFile = systemUserHomePath + UserRootPath + "tmpfiles/"+ BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +"/" + zipFile.getOriginalFilename();

                if (productType.toLowerCase().equals("wmatrix")) {
                    applyConfigJson.put("language", language);
                }
                arrayServerConfigStr = ""; // server config 지워야함...

                // project zip file upload
                String copyToProjectImportZipFilePath = systemUserHomePath + UserRootPath + "tmpfiles/"+ BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() ;

                File zipFileTmp = new File(copyToProjectImportZipFilePath);

                if ( zipFileTmp != null && !zipFileTmp.exists()) {
                    zipFileTmp.mkdir(); //폴더 생성합니다.
                    //System.out.println("폴더가 생성되었습니다.");

                }else {
                    // System.out.println("이미 폴더가 생성되어 있습니다.");
                }

                File importzipFile = new File(zipToProjectImportFile);

                importzipFile.createNewFile();
                zipFile.transferTo(importzipFile);

                hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();

                CommandLine commandLineProjectChmod = CommandLine.parse("chmod");
                commandLineProjectChmod.addArgument("-R");
                commandLineProjectChmod.addArgument("777");
                commandLineProjectChmod.addArgument(systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_+ parseResultObj.get(PayloadMsgType.domainID.name()).toString() +"/"+BuilderDirectoryType.USER_+ parseResultObj.get(PayloadMsgType.userID.name()).toString() +"/"+BuilderDirectoryType.WORKSPACE_W+ parseResultObj.get(PayloadMsgType.workspaceID.name()).toString()+"/"+projectName);
                executueCommonsCLIToTemplateCopy(commandLineProjectChmod,"chmod"); // method name 수정 필요.

                if(appIconZipFileName.equals("")){
                    String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");

                    CommandLine commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_+ parseResultObj.get(PayloadMsgType.domainID.name()).toString() +"/"+BuilderDirectoryType.USER_+ parseResultObj.get(PayloadMsgType.userID.name()).toString() +"/"+BuilderDirectoryType.WORKSPACE_W+ parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() +"/"+ projectName +"/build_logfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

                    commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_+ parseResultObj.get(PayloadMsgType.domainID.name()).toString() +"/"+BuilderDirectoryType.USER_+ parseResultObj.get(PayloadMsgType.userID.name()).toString() +"/"+BuilderDirectoryType.WORKSPACE_W+ parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() +"/"+ projectName +"/appfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.
                }else {
                    String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");

                    CommandLine commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_+ parseResultObj.get(PayloadMsgType.domainID.name()).toString() +"/"+BuilderDirectoryType.USER_+ parseResultObj.get(PayloadMsgType.userID.name()).toString() +"/"+BuilderDirectoryType.WORKSPACE_W+ parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() +"/"+ projectName +"/build_logfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

                    commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_+ parseResultObj.get(PayloadMsgType.domainID.name()).toString() +"/"+BuilderDirectoryType.USER_+ parseResultObj.get(PayloadMsgType.userID.name()).toString() +"/"+BuilderDirectoryType.WORKSPACE_W+ parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() +"/"+ projectName +"/appfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

                    // local properties 파일 생성
                }

                // message project copy
                projectImportMessage(session, projectTemplateCreateStatusMessage, "","PROJCOPY");
                // product type 수정 하기
                String resultZipfileName = copyImportProjectZipFile(systemUserHomePath + UserRootPath + "tmpfiles/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString(), "builder_main/" + BuilderDirectoryType.DOMAIN_+ parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"
                            + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/"+projectName+"/"+projectName, language);
                // unzip 도 java 로 처리하기
                // 3. unzip
                // param : UserRootPath + projectName, fileName
                unzipTemplateDirAndFile(systemUserHomePath + UserRootPath + "builder_main/" +BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"
                        + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString()
                        + "/" +projectName + "/" +projectName +"/" +resultZipfileName, systemUserHomePath + UserRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"
                        + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString()
                        + "/" +projectName+ "/" +projectName);

                // keystore file edit
                if(platformZip.toLowerCase().equals(PayloadMsgType.android.name())){

                    // local properties 파일 생성
                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("755");
                    commandLine.addArgument(UserProjectPath + "gradlew");

                    executueCommonsCLIToResult(commandLine, "chmod");

                    createLocalPropertiesFile(UserProjectPath +"/"+projectName);
                    updateGradlePropertiesFile(UserProjectPath+"/"+projectName); // gradle.java.home setting

                    if (productType.toLowerCase().equals("generalapp")) {
                        signingKeyService.generalAndroidAppSigning(UserProjectPath+"/"+projectName, jsonSigningkey);
                    }

                    if (productType.toLowerCase().equals("wmatrix")) {
                        JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platformZip, UserProjectPath + "/" + projectName);
                        JsonElement element = JsonParser.parseString(jsonAppConfig.toJSONString());
                        JsonObject jsonReulst = gson.fromJson(element, JsonObject.class);
                    }

                    // app config 수정 cli 진행
                    // mobileTemplateConfigService.templateMultiProfileConfigAllCLI(sessionTemp, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);
                    // mobileTemplateConfigService.templateMultiProfileAutoKeySetCreateAllCLI(session, parseResult, jsonReulst, ServerConfigMode.CREATE);

                    // signing key service 시작
                    signingKeyService.createSigningkeyFileToProperties(UserProjectPath+"/"+projectDirName, platformZip, signingKeyJson);

                    // 앱 아이콘 적용 이후 git add/commit/push 진행
//                        if(appIconZipFileName.equals("")) {
//
//                        }else {
//                            if (VcsType.toLowerCase().equals("git")) {
//                                // appIconService.appIconUnzipAction(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, null, hqKeyTemp);
//                                appIconService.setAppIconGeneratorCLI(session, parseResult);
//                            } else if (VcsType.toLowerCase().equals("svn")) {
//                                //appIconService.appIconUnzipAndSVNCLI(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, repositoryJson);
//                                appIconService.setAppIconGeneratorCLI(session, parseResult);
//                            }
//                        }

                    // android deploy profile 설정
                    deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);
                    projectImportMessage(session, projectTemplateCreateStatusMessage, "","GITPUSH");
                    // 삭제
                    //projectGitPushService.gitPushAction(session, platformZip, UserProjectPath, projectDirName, parseResultObj, repositoryJson, "ProjectImportSet");
                    gitTask.gitPush(parseResult, UserProjectPath + "/" + projectName, repositoryJson, "ProjectImportSet");
                    projectImportMessage(session, projectTemplateCreateStatusMessage, "","DONE");

                }else if(platformZip.toLowerCase().equals(PayloadMsgType.ios.name())){

                    arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();
                    arrayJSONCertificateStr = parseResult.get("arrayJSONCertificates").toString();
                    // String textToBuildSettingsGson = parseResult.get("BuildSettingsGson").toString();

                    // appconfigMultiArrayJson = (JsonArray) JsonParser.parseString(textToBuildSettingsGson);

                    log.info(arrayJSONProfilesStr);
                    log.info(arrayJSONCertificateStr);

                    // jsonProfileDebug = (JSONObject) parser.parse(parseResult.get("jsonProfileDebug").toString());
                    // jsonProfileRelease = (JSONObject) parser.parse(parseResult.get("jsonProfileRelease").toString());

                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("755");
                    commandLine.addArgument(UserProjectPath +"/" + projectName + "/archive.sh");

                    executueCommonsCLIToResult(commandLine, "chmod");

                    CommandLine commandLineMACOSRm = CommandLine.parse("rm");
                    commandLineMACOSRm.addArgument("-rf");
                    commandLineMACOSRm.addArgument(UserProjectPath + "/" + projectName +"/__MACOSX");

                    executueCommonsCLIToResult(commandLineMACOSRm, "rm");


                    String getProjectNamePath = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platformZip, UserProjectPath, projectName);
                    applyConfigJson.put("getProjectNamePath",getProjectNamePath);

                    // app config 수정 cli 진행
                    JSONArray jsonAllTargetConfig = multiProfileService.getiOSAllMultiProfileList(parseResult);
                    parseResult.put("multiProfileList", jsonAllTargetConfig);

                    // signingKeyService.createSigningkeyToCertFile(UserProjectPath, signingKeyJson, productType);
                    signingKeyService.createSigningkeyToMultiCertFile(UserProjectPath, arrayJSONCertificateStr, productType);

                    /* TODO  createProfileSettingToMultiProfilesProject 기반 메소드 target, jsonMatrix 값 적용 적용 */
                    JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platformZip, UserProjectPath+"/"+projectName);


                    parseResult.put("multiProfileConfig", jsonAppConfig);

                    JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("targets");
                    String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

                    /* TODO  appconfigMultiArrayJson 객체 data setting 적용  */
                    mobileTemplateConfigService.importiOSMultiProfileConfigSettingALLCLI(session, parseResult, UserProjectPath+"/"+projectName);

                    String tempProfilesOneStr = arrayJSONProfilesStr;

                    //tempProfilesOneStr = tempProfilesOneStr.replaceAll(" ",""); // 인증서 파일명에 공백이 있을경우 에러가 발생하므로 주석
                    tempProfilesOneStr = tempProfilesOneStr.replace("[{","");
                    tempProfilesOneStr = tempProfilesOneStr.replace("}]","");

                    String[] jsonProfilesArray = tempProfilesOneStr.split("\\},\\{");

                    signingKeyService.importiOSProvisioningFileMultiProfilesSetting(UserProjectPath+"/"+projectName, arrayJSONProfilesStr, productType, targetKeyList, appconfigMultiArrayJson);

                    for(int i = 0; i < jsonProfilesArray.length; i++){
                        jsonProfilesArray[i] = "{" + jsonProfilesArray[i] + "}";
                        try {
                            JSONObject jsonProfile = (JSONObject) parser.parse(jsonProfilesArray[i]);

                            // show profiles
                            //JSONObject resultShwoProfiles = signingKeyService.showProfileOneInfo(session, jsonProfile.get("profiles_path").toString(), productType);
                            // log.info(resultShwoProfiles.toJSONString());
                            /* TODO  사용 안하는 메소드 */
                            // signingKeyService.setExportOptionsMultiProfiles(session, UserProjectPath+"/"+projectName, resultShwoProfiles, jsonProfile, productType);

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    // .xcodeproj 디렉토리 수정
                    String getProjectProjNameTemp = getProjectNamePath.replace(".xcodeproj", "");
                    // 앱 아이콘 적용 이후 git add/commit/push 진행

                    /**
                     * ios pod 파일 있을때 pod install 명령어 수행하는 기능
                     */
                    buildDependencyService.validatePodFileInstall(UserProjectPath+"/"+projectName);

                    deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);
                    projectImportMessage(session, projectTemplateCreateStatusMessage, "","GITPUSH");
                    /* TODO  그외 메소드 적용 */
                    //삭제
                    //projectGitPushService.gitPushAction(session, platformZip, UserProjectPath, projectDirName, parseResultObj, repositoryJson, "ProjectImportSet");
                    gitTask.gitPush(parseResult, UserProjectPath + "/" + projectName, repositoryJson, "ProjectImportSet");
                    projectImportMessage(session, projectTemplateCreateStatusMessage, "","DONE");

                }
                importzipFile.delete(); // import zip file 삭제
            }
        } catch (IOException e) {
            log.error("builder create project service error",e);
        } catch (ParseException e) {
            log.error("builder create project service error",e);
        }
    }

    // userRootPath + template + platfrom, userRootPath + projectName
    // filename은 내부 검색하여 처리
    private String copyTemplateZipFile(String templatePath, String projectName, String templateVersion, String language){
        final String[] fileNameToStringResult = new String[1];
        log.info(templatePath);
        try (Stream<Path> filePathStream= Files.walk(Paths.get(templatePath))) {

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());

                if (fileNameToString.matches(".*"+templateVersion+".*"+language+".*.zip")) {
                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);

                    File file = new File(templatePath + "/" + fileNameToString);
                    File temp = new File(systemUserHomePath + UserRootPath+ projectName +"/"+ fileNameToString);

                    fileNameToStringResult[0] = fileNameToString;
                    try {
                        byte[] zipFile = FileUtils.readFileToByteArray(file);

                        FileCopyUtils.copy(zipFile, temp);

                    } catch (IOException e) {
                        log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                    }
                }
            });
            return fileNameToStringResult[0];
        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

            return fileNameToStringResult[0];
        }
    }

    private void unzipTemplateDirAndFile(String zipFileName, String projectDir){
        try {
            zipUtils.unzipAction(Paths.get(zipFileName) , Paths.get(projectDir));
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    /**
     * @param templatePath
     * @param projectPath
     * @param language
     * @return
     */
    private String copyImportProjectZipFile(String templatePath, String projectPath, String language){
        final String[] fileNameToStringResult = new String[1];
        log.info(templatePath);
        try (Stream<Path> filePathStream= Files.walk(Paths.get(templatePath))) {

            filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
                String fileNameToString = String.valueOf(filePath.getFileName());

                if (fileNameToString.matches(".*.zip")) {
                    log.info("buildAfterSendToHeadquaterFileObj fileNameToString data : {} ", fileNameToString);

                    File file = new File(templatePath + "/" + fileNameToString);
                    File temp = new File(systemUserHomePath + UserRootPath+ projectPath +"/"+ fileNameToString);

                    fileNameToStringResult[0] = fileNameToString;
                    try {

                        byte[] zipFile = FileUtils.readFileToByteArray(file);

                        FileCopyUtils.copy(zipFile, temp);

                    } catch (IOException e) {
                        log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

                    }
                }
            });
            return fileNameToStringResult[0];
        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

            return fileNameToStringResult[0];
        }
    }

    private void projectImportMessage(WebSocketSession session, ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage, String logMessage, String gitStatus){

        projectTemplateCreateStatusMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_IMPORT_STATUS_INFO.name());
        projectTemplateCreateStatusMessage.setSessType(PayloadMsgType.BRANCH.name());
        // projectTemplateCreateStatusMessage.setHqKey(hqKeyTemp); // hqKey User 아이디

        projectTemplateCreateStatusMessage.setGitStatus(gitStatus);

        projectTemplateCreateStatusMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectTemplateCreateStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}
