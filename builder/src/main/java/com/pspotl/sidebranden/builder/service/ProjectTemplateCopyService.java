package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.pspotl.sidebranden.builder.domain.ProjectTemplateCreateStatusMessage;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.ServerConfigMode;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.task.GitTask;
import com.pspotl.sidebranden.builder.task.SvnTask;
import com.pspotl.sidebranden.builder.util.SSHClientUtil;
import com.pspotl.sidebranden.builder.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProjectTemplateCopyService extends BaseService{

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
    BuildDependencyService buildDependencyService;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    private ZipUtils zipUtils;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    private ObjectMapper Mapper = new ObjectMapper();
    private JSONParser parser = new JSONParser();

    private void globalParameterSetting(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj, JSONObject builderSettingObj){

    }

    // 내부 비동기 처리로 구현이 되어야 함...

    @Async("asyncThreadPool")
    public void createTemplateProjectCLI(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj, JSONObject builderSettingObj){
        log.info(" createProjectGitHubCloneCLI : check... ");
        ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage = new ProjectTemplateCreateStatusMessage();
        CommandLine commandLineTemplateMkdir;
        CommandLine commandLineGitRepositoryMkdir;
        CommandLine commandLineVCSBareCLI;

        String UserProjectPath;
        String platformZip;
        String hqKeyTemp; // 수정
        JSONObject signingKeyJson;
        JSONObject repositoryJson;
        JSONObject jsonProfileDebug;
        JSONObject jsonProfileRelease;

        WebSocketSession sessionTemp; // 수정
        try {
        String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");
        //String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh"); // 삭제


        String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString(); //

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String templateVersion = parseResult.get("templateVersion").toString();
        String language = parseResult.get("language").toString();
        String appIconFileName = parseResult.get("appIconFileName").toString();
        String build_id = parseResult.get(PayloadMsgType.projectID.name()).toString();
        String arrayServerConfigStr = parseResult.get("serverConfig").toString(); // server config
        String product_type = parseResult.get("product_type").toString();
        String vcsType = repositoryObj.get("vcsType").toString().toLowerCase();
        String repositoryID = repositoryObj.get("repositoryId").toString();
        String repositoryPassword = repositoryObj.get("repositoryPassword").toString();

        UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();
        signingKeyJson = signingkeyObj;
        repositoryJson = repositoryObj;

            jsonProfileDebug = (JSONObject) parser.parse(parseResult.get("jsonProfileDebug").toString());
            jsonProfileRelease = (JSONObject) parser.parse(parseResult.get("jsonProfileRelease").toString());


        hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();
        sessionTemp = session;

        projectTemplateCreateStatusMessage.setBuild_id(Integer.parseInt(build_id));
        projectTemplateCreateStatusMessage.setProjectDirPath(projectName);
        projectTemplateCreateStatusMessage.setHqKey(hqKeyTemp);

        // 전역 변수 값 저장
        globalParameterSetting(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj, builderSettingObj);

        // language set
        applyConfigObj.put("language",language);
        log.info("1 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // 1. mkdir
        // message mkdir
        projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "","MKDIR");

        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName); // 3
        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir");

        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName +"/build_logfiles"); // 3
        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName +"/appfiles"); // 3
        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

        CommandLine commandLineProjectChmod = CommandLine.parse("chmod");
        commandLineProjectChmod.addArgument("-R");
        commandLineProjectChmod.addArgument("777");
        commandLineProjectChmod.addArgument(systemUserHomePath + UserRootPath + "builder_main/"+domainName +"/"+userName +"/"+workspaceName+"/"+projectName);
        executueCommonsCLIToTemplateCopy(commandLineProjectChmod,"chmod"); // method name 수정 필요.

        // 1.1 mkdir git repository
        commandLineGitRepositoryMkdir = CommandLine.parse(shellscriptFileName);
        commandLineGitRepositoryMkdir.addArgument("createGitRepository"); // 1 createGitRepository
        commandLineGitRepositoryMkdir.addArgument(UserGitRepositoryPath + domainName +"/" + userName + "/" + workspaceName); // 2
        commandLineGitRepositoryMkdir.addArgument(projectName + ".git"); // 3
        executueCommonsCLIToTemplateCopy(commandLineGitRepositoryMkdir,"mkdir"); // method name 수정 필요.

        CommandLine commandLineGitChmod = CommandLine.parse("chmod");
        commandLineGitChmod.addArgument("-R");
        commandLineGitChmod.addArgument("775");
        commandLineGitChmod.addArgument(UserGitRepositoryPath + domainName +"/" + userName + "/" + workspaceName +"/"+ projectName +".git");
        executueCommonsCLIToTemplateCopy(commandLineGitChmod,"chmod"); // method name 수정 필요.


        // message git bare create
        // 2.1 git bare, svn create 시작
        projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "","GITBARE");
        try {
            if ("localgit".equals(vcsType)) {
                // git remote dir mkdir
                gitTask.gitWithSSH("mkdir", UserGitRepositoryPath, domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
                // git init --bare set
                gitTask.gitWithSSH("bareInit", UserGitRepositoryPath, domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
            } else if ("localsvn".equals(vcsType)) {
                // svnadmin create
                svnTask.svnCreate(repositoryID, repositoryPassword, new URI(UserSvnRepositoryPath + domainName + "/" + userName + "/" + workspaceName + "/" + projectName));
            }
        } catch (Exception e) {
            log.error("Git Bare, SVN Create Error = {}", e.getLocalizedMessage(), e);
        }

        // message git clone
        projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "","GITCLONE");
        // 2.2 git 초기 repository clone
        try {
            if ("localgit".equals(vcsType)) {
                gitTask.gitClone(parseResult, UserProjectPath + "/" + projectName, repositoryJson);
            } else if ("localsvn".equals(vcsType)) {
                svnTask.svnCheckout(repositoryID, repositoryPassword, new URI(UserSvnRepositoryPath + domainName + "/" + userName + "/" + workspaceName + "/" + projectName), new URI(UserProjectPath + "/" + projectName));
            }
        } catch (Exception e) {
            log.error("Git Clone, SVN Checkout Error = {}", e.getLocalizedMessage(), e);
        }

        // copy은 java 로 처리하기
        // 2. copy
        // 필요한 파라미터
        // param(파일 원본 dir) : UserRootPath + "template/Android"
        // param : file name
        // param(복사할 곳 dir) : UserRootPath + projectName + filename

        // 추가로 unzip, app config, app icon setting
        // 내부에 처리가 되어야함
        // 파라미터 도 추가로 값을 넣어야함.
        // message project copy
        projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "","PROJCOPY");
        // product type 수정 하기
        String resultZipfileName = "";
        if(product_type.toLowerCase().equals("wmatrix")) {
            resultZipfileName = copyTemplateZipFile(systemUserHomePath + UserRootPath + "template/wmatrix/"+platform, "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/"+projectName+"/"+projectName, templateVersion, language);
        }else if (product_type.toLowerCase().equals("whybrid")){
            resultZipfileName = copyTemplateZipFile(systemUserHomePath + UserRootPath + "template/whybrid/"+platform, "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/"+projectName+"/"+projectName, templateVersion, language);
        }else {
            resultZipfileName = "";
        }

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
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

        // 4. app icon setting 실행 Android 만
        // android, ios 구분해서 처리
        if(!appIconFileName.equals("")){
            try {
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    zipUtils.decompressFormAndroidIcon(systemUserHomePath+ profileSetDir+parseResult.get(PayloadMsgType.userID.name()).toString()+appIconFileName, systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName +"/" + projectName + "/" + projectName +androidxhdpiAppIconPath);
                }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

                }

            } catch (Throwable throwable) {
                log.warn(throwable.getMessage(), throwable); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

            }
        }
        log.info("2 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // 5. app config 설정
        // serverConfig 추가
        // project path 조회 기능 추가
        if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
            String getProjectNamePath = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platform, UserProjectPath, projectName);
            applyConfigObj.put("getProjectNamePath",getProjectNamePath);
        }

        mobileTemplateConfigService.templateConfigAllCLI(sessionTemp, platform, UserProjectPath, projectName, applyConfigObj, arrayServerConfigStr, ServerConfigMode.CREATE, product_type, hqKeyTemp);

        // 6. signing key 설정
        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
            signingKeyService.createSigningkeyFileToProperties(UserProjectPath+"/"+projectName, platformZip, signingKeyJson);

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
            // set cli signingkey
            // matrix 제품 타입으로 변경
            signingKeyService.createSigningkeyToCertFile(UserProjectPath, signingKeyJson, product_type);

            // debug, release profile file send ...
            // set cli profile
            if(language.toLowerCase().equals("swift")){
                signingKeyService.createProfileSettingToDebugProject(UserProjectPath +"/"+projectName, jsonProfileDebug, product_type);
                signingKeyService.createProfileSettingToReleaseProject(UserProjectPath+"/"+projectName, jsonProfileRelease, product_type);
            }else if(language.toLowerCase().equals("objc")){
                signingKeyService.createProfileSettingToDebugProject(UserProjectPath+"/"+projectName, jsonProfileDebug, product_type);
                signingKeyService.createProfileSettingToReleaseProject(UserProjectPath+"/"+projectName, jsonProfileRelease, product_type);
            }

            // show profile
            String[] showProfileDebugStr = signingKeyService.showProfilesInfo(session, jsonProfileDebug, product_type);
            String[] showProfileReleaseStr = signingKeyService.showProfilesInfo(session, jsonProfileRelease, product_type);
            // 정상 처리되면 return 값 호출 하기
            // set exportoptions plist setting
            signingKeyService.setExportOptionsDebug(session, UserProjectPath+"/"+projectName, showProfileDebugStr, applyConfigObj, product_type);
            signingKeyService.setExportOptionsRelease(session, UserProjectPath+"/"+projectName, showProfileReleaseStr, applyConfigObj, product_type);
        }

        // 7. app icon setting 실행 iOS 만
        // android, ios 구분해서 처리
        if(!appIconFileName.equals("")){
            try {
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

                }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    unzipTemplateDirAndFile(systemUserHomePath + profileSetDir+parseResult.get(PayloadMsgType.userID.name()).toString() + "/" + appIconFileName, systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName +"/" + projectName + "/" + projectName +"/" + applyConfigObj.get("iOSProjectName").toString() +iosAppIconPath);
                }
            } catch (Throwable throwable) {
                log.warn(throwable.getMessage(), throwable); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }

        // message Git, SVN push
        try {
            if ("localgit".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "GITPUSH");
                gitTask.gitPush(parseResult, UserProjectPath + "/" + projectName, repositoryJson, "init first commit");
            } else if ("localsvn".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "SVNCOMMIT");
                svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "init first commit");
            }
        } catch (Exception e) {
            log.error("Git, SVN push error = {}", e.getLocalizedMessage(), e);
        }
        log.info("3 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // send message template(Project) 생성 완료 메시지 전송
        projectCreateMessage(sessionTemp,projectTemplateCreateStatusMessage, "","DONE");

        } catch (ParseException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    @Async("asyncThreadPool")
    public void createMultiProfileTemplateProjectCLI(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj, JSONObject builderSettingObj) throws ExecutionException, InterruptedException {
        log.info(" createProjectGitHubCloneCLI : check... ");
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
        JSONObject signingKeyJson;
        JSONObject repositoryJson;
        JSONObject jsonProfileDebug;
        JSONObject jsonProfileRelease;
        JSONObject iosProfileBuildSettings;

        Gson gson = new Gson();

        ArrayList<JSONObject> appconfigMultiArrayJson = new ArrayList<>();

        WebSocketSession sessionTemp; // 수정
        String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");
        //String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh"); // 삭제

        String domainName = BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();

        String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
        String templateVersion = parseResult.get("templateVersion").toString();
        String language = parseResult.get("language").toString();
        String appIconFileName = parseResult.get("appIconFileName").toString();
        String build_id = parseResult.get(PayloadMsgType.projectID.name()).toString();
        String arrayServerConfigStr = ""; //parseResult.get("serverConfig").toString(); // server config
        String arrayMultiProfileConfigStr = parseResult.get("BuildSettings").toString();
        String product_type = parseResult.get("product_type").toString();

        UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
        platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();
        signingKeyJson = signingkeyObj;
        repositoryJson = repositoryObj;

        String vcsType = repositoryObj.get("vcsType").toString().toLowerCase();
        String repositoryID = repositoryObj.get("repositoryId").toString();
        String repositoryPassword = repositoryObj.get("repositoryPassword").toString();

        //jsonProfileDebug = (JSONObject) parser.parse(parseResult.get("jsonProfileDebug").toString());
        //jsonProfileRelease = (JSONObject) parser.parse(parseResult.get("jsonProfileRelease").toString());

        hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();
        sessionTemp = session;

        projectTemplateCreateStatusMessage.setBuild_id(Integer.parseInt(build_id));
        projectTemplateCreateStatusMessage.setProjectDirPath(projectName);
        projectTemplateCreateStatusMessage.setHqKey(hqKeyTemp);

        // 전역 변수 값 저장
        globalParameterSetting(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj, builderSettingObj);

        // language set
        applyConfigObj.put("language",language);
        log.info("1 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // 1. mkdir
        // message mkdir
        projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "","MKDIR");

        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName); // 3
        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir");

        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName +"/build_logfiles"); // 3
        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

        commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
        commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
        commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
        commandLineTemplateMkdir.addArgument(domainName +"/"+userName +"/"+workspaceName +"/"+ projectName +"/appfiles"); // 3
        executueCommonsCLIToTemplateCopy(commandLineTemplateMkdir,"mkdir"); // method name 수정 필요.

        CommandLine commandLineProjectChmod = CommandLine.parse("chmod");
        commandLineProjectChmod.addArgument("-R");
        commandLineProjectChmod.addArgument("777");
        commandLineProjectChmod.addArgument(systemUserHomePath + UserRootPath + "builder_main/"+domainName +"/"+userName +"/"+workspaceName+"/"+projectName);
        executueCommonsCLIToTemplateCopy(commandLineProjectChmod,"chmod"); // method name 수정 필요.

        // 1.1 mkdir vcs repository
        if ("localgit".equals(vcsType)) {
            commandLineGitRepositoryMkdir = CommandLine.parse(shellscriptFileName);
            commandLineGitRepositoryMkdir.addArgument("createGitRepository"); // 1 createGitRepository
            commandLineGitRepositoryMkdir.addArgument(UserGitRepositoryPath + domainName + "/" + userName + "/" + workspaceName); // 2
            commandLineGitRepositoryMkdir.addArgument(projectName + ".git"); // 3
            executueCommonsCLIToTemplateCopy(commandLineGitRepositoryMkdir,"mkdir"); // method name 수정 필요.

            CommandLine commandLineGitChmod = CommandLine.parse("chmod");
            commandLineGitChmod.addArgument("-R");
            commandLineGitChmod.addArgument("775");
            commandLineGitChmod.addArgument(UserGitRepositoryPath + domainName + "/" + userName + "/" + workspaceName +"/"+ projectName +".git");
            executueCommonsCLIToTemplateCopy(commandLineGitChmod,"chmod"); // method name 수정 필요.
        }

        // 2.1 git bare / svn creat 시작
        try {
            if ("localgit".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "GITBARE");
                // git init --bare set
                gitTask.gitWithSSH("bareInit", UserGitRepositoryPath, domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
            } else if ("localsvn".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "SVNCREATE");
                // svnadmin create
                svnTask.svnCreate(repositoryID, repositoryPassword, new URI(UserSvnRepositoryPath + domainName + "/" + userName + "/" + workspaceName + "/" + projectName));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

        try {
            if ("localgit".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "GITCLONE");
                // 2.2 git 초기 repository clone
                gitTask.gitClone(parseResult, UserProjectPath + "/" + projectName, repositoryJson);
            } else if ("localsvn".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "SVNCHECKOUT");
                svnTask.svnCheckout(repositoryID, repositoryPassword, new URI("/" + domainName + "/" + userName + "/" + workspaceName + "/" + projectName), new URI(UserProjectPath + "/" + projectName));
            }
        } catch (Exception e) {
            log.error("Git Clone / SVN Checkout Error = {}", e.getLocalizedMessage(), e);
        }

        // copy은 java 로 처리하기
        // 2. copy
        // 필요한 파라미터
        // param(파일 원본 dir) : UserRootPath + "template/Android"
        // param : file name
        // param(복사할 곳 dir) : UserRootPath + projectName + filename

        // 추가로 unzip, app config, app icon setting
        // 내부에 처리가 되어야함
        // 파라미터 도 추가로 값을 넣어야함.
        // message project copy
        projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "","PROJCOPY");
        // product type 수정 하기
        String resultZipfileName = "";
        if(product_type.toLowerCase().equals("wmatrix")) {
             resultZipfileName = copyTemplateZipFile(systemUserHomePath + UserRootPath + "template/wmatrix/"+platform, "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/"+projectName+"/"+projectName, templateVersion, language);
        }else if (product_type.toLowerCase().equals("whybrid")){
            resultZipfileName = copyTemplateZipFile(systemUserHomePath + UserRootPath + "template/whybrid/"+platform, "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/"+projectName+"/"+projectName, templateVersion, language);
        }else {
            resultZipfileName = "";
        }

        // unzip 도 java 로 처리하기
        // 3. unzip
        // param : UserRootPath + projectName, fileName
        unzipTemplateDirAndFile(systemUserHomePath + UserRootPath + "builder_main/" + domainName + "/" + userName + "/" + workspaceName + "/" +projectName + "/" +projectName +"/" +resultZipfileName, systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" +projectName+ "/" +projectName);

        // __macosx 폴더 삭제
        CommandLine commandLineMACOSRm = CommandLine.parse("rm");
        commandLineMACOSRm.addArgument("-rf");
        commandLineMACOSRm.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectName +"/__MACOSX");
        executueCommonsCLIToTemplateCopy(commandLineMACOSRm, "rm");

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())) {
            CommandLine commandLine = CommandLine.parse("chmod");
            commandLine.addArgument("-R");
            commandLine.addArgument("755");
            commandLine.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectName + "/gradlew");

            executueCommonsCLIToTemplateCopy(commandLine, "chmod");

            // fastlane 내부 권한 수정r
            CommandLine commandLineFastlaneChmod = CommandLine.parse("chmod");
            commandLineFastlaneChmod.addArgument("-R");
            commandLineFastlaneChmod.addArgument("777");
            commandLineFastlaneChmod.addArgument(systemUserHomePath + UserRootPath + "builder_main/" + domainName + "/" + userName  + "/" + workspaceName + "/" + projectName + "/" + projectName + "/fastlane");
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
       }

        try {
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())) {
                createLocalPropertiesFile(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName+ "/" + projectName); // local properties file setting
                if(springProfileMode.equals("tomcat_builder")){
                    // gradle propertise file 건들지 않기
                }else {
                    // 분가처리
                    updateGradlePropertiesFile(systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName + "/" + projectName+ "/" + projectName); // gradle.java.home setting
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
        }

        // MultiDomain Template Get Config
        JSONObject templateConfig = mobileTemplateConfigService.getMultiDomainTemplateConfig(session, parseResult).get();
        parseResult.put("templateConfig", templateConfig);

        // multi profile android
        if(platform.toLowerCase().equals("android")){
            JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platform, UserProjectPath+"/"+projectName);
            JsonElement element = JsonParser.parseString(jsonAppConfig.toJSONString());
            JsonObject jsonReulst =  gson.fromJson(element, JsonObject.class);

            // multi profile ios 작업 진행하기
            mobileTemplateConfigService.templateMultiProfileAutoKeySetCreateAllCLI(sessionTemp, parseResult, jsonReulst, ServerConfigMode.CREATE);

        }else {
            // multi profile ios 작업 진행하기
            // mobileTemplateConfigService.templateMultiProfileConfigAllCLI(sessionTemp, platform, UserProjectPath, projectName, applyConfigObj, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, product_type);
            mobileTemplateConfigService.templateMultiProfileAutoKeySetCreateAllCLI(sessionTemp, parseResult, null, ServerConfigMode.CREATE);
        }

        // 6. app icon generator 실행 Android 만
        log.info(" app icon file name : "+appIconFileName);
        if(!appIconFileName.equals("")){
            try {
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    log.info("start app icon generator... ");
                    // zipUtils.decompressFormAndroidIcon(systemUserHomePath+ profileSetDir+appIconFileName, systemUserHomePath + UserRootPath + "builder_main/" + domainName +"/" + userName + "/" + workspaceName +"/" + projectName + "/" + projectName +androidxhdpiAppIconPath);
                    appIconService.setAppIconGeneratorCLI(session, parseResult);
                }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    appIconService.setiOSAppIconGeneratorCLI(session, parseResult);
                }
            } catch (Throwable throwable) {
                log.warn(throwable.getMessage(), throwable); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }

        // 6. signing key 설정, deploy profile 설정
        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
            signingKeyService.createSigningkeyFileToProperties(UserProjectPath+"/"+projectName, platformZip, signingKeyJson);

             // android deploy profile 설정
            deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);

        }else if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){

            arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();
            arrayJSONCertificateStr = parseResult.get("arrayJSONCertificates").toString();
            String textToBuildSettingsGson = parseResult.get("BuildSettingsGson").toString();

            JsonArray toJsonBuildSettingsGson = (JsonArray) JsonParser.parseString(textToBuildSettingsGson);

            // ios get config ..
            JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platform, UserProjectPath+"/"+projectName);
            JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("targets");
            String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

            log.info(jsonAppConfig.toJSONString());
            log.info(jsonMatrix.toJSONString());
            log.info(arrayJSONProfilesStr);
            log.info(arrayJSONCertificateStr);
            log.info(targetKeyList[0]);
            // set cli signingkey
            // matrix 제품 타입으로 변경
            signingKeyService.createSigningkeyToMultiCertFile(UserProjectPath, arrayJSONCertificateStr, product_type);

            // debug, release profile file send ...
            // set cli profile
            if(language.toLowerCase().equals("swift")){
                // signingKeyService.createProfileSettingToDebugProject(UserProjectPath +"/"+projectName, jsonProfileDebug, product_type);
                 signingKeyService.createProfileSettingToMultiProfilesProject(UserProjectPath+"/"+projectName, arrayJSONProfilesStr, product_type, targetKeyList, toJsonBuildSettingsGson);
            }else if(language.toLowerCase().equals("objc")){
                // signingKeyService.createProfileSettingToDebugProject(UserProjectPath+"/"+projectName, jsonProfileDebug, product_type);
                // signingKeyService.createProfileSettingToReleaseProject(UserProjectPath+"/"+projectName, jsonProfileRelease, product_type);
            }

//            String tempProfilesOneStr = arrayJSONProfilesStr;
//
////            tempProfilesOneStr = tempProfilesOneStr.replaceAll(" ","");
//            tempProfilesOneStr = tempProfilesOneStr.replace("[{","");
//            tempProfilesOneStr = tempProfilesOneStr.replace("}]","");
//
//            String[] jsonProfilesArray = tempProfilesOneStr.split("\\},\\n\\t \\{");
//
//            for(int p = 0; p < jsonProfilesArray.length; p++){
//                jsonProfilesArray[p] = "{" + jsonProfilesArray[p] + "}";
//                try {
//                    JSONObject jsonProfile = (JSONObject) parser.parse(jsonProfilesArray[p]);
//
//                    // show profiles
//                    JSONObject resultShwoProfiles = signingKeyService.showProfileOneInfo(session, jsonProfile.get("profiles_path").toString(), product_type);
//                    log.info(resultShwoProfiles.toJSONString());
//                    // signingKeyService.setExportOptionsMultiProfiles(session, UserProjectPath+"/"+projectName, resultShwoProfiles, jsonProfile, product_type);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }

            /**
             * ios pod 파일 있을때 pod install 명령어 수행하는 기능
             */
            buildDependencyService.validatePodFileInstall(UserProjectPath+"/"+projectName);

            // ios deploy profile 설정
            deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);
        }

        // Git, Svn Push
        try {
            if ("localgit".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "GITPUSH");
                gitTask.gitPush(parseResult, UserProjectPath + "/" + projectName, repositoryJson, "init first commit");
            } else if ("localsvn".equals(vcsType)) {
                projectCreateMessage(sessionTemp, projectTemplateCreateStatusMessage, "", "SVNCOMMIT");
                svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "init first commit");
            }
        } catch (Exception e) {
            log.error("Git, Svn Push Error");
        }

        log.info("3 hqKey : {}, session id {}", hqKeyTemp, sessionTemp);
        // send message template(Project) 생성 완료 메시지 전송
        projectCreateMessage(sessionTemp,projectTemplateCreateStatusMessage, "","DONE");
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

    private void updateGradlePropertiesFile(String path) throws IOException {
        String buildAfterLogFile = path+"/gradle.properties";

        File f = new File(buildAfterLogFile);
        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);
        String android_home = System.getProperty("GRADLE_JAVA_HOME"); // GRADLE_JAVA_HOME
        out.append("org.gradle.java.home="+android_home+"\n");
        out.append("org.gradle.jvmargs=-XX\\:MaxHeapSize\\=4608m -Xmx4608m\n");
        out.append("android.useAndroidX=true\n");
        out.append("android.enableJetifier=true\n");
        out.append("android.enableR8.fullMode=false\n");
        out.append("android.defaults.buildfeatures.buildconfig=true");

        out.flush();
        out.close();
    }

    // 2.1 common cli mkdir
    private void executueCommonsCLIToTemplateCopy(CommandLine commandLineParse, String commandType){

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
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
            zipUtils.decompress(zipFileName, projectDir);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    private void projectCreateMessage(WebSocketSession session, ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage, String logMessage, String gitStatus){

        projectTemplateCreateStatusMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_STATUS_INFO.name());
        projectTemplateCreateStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        // projectTemplateCreateStatusMessage.setHqKey(hqKeyTemp); // hqKey User 아이디

        projectTemplateCreateStatusMessage.setGitStatus(gitStatus);

        projectTemplateCreateStatusMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectTemplateCreateStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}