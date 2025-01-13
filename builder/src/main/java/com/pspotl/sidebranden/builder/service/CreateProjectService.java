package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.pspotl.sidebranden.builder.domain.ProjectGitCloneMessage;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.ServerConfigMode;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.task.GitTask;
import com.pspotl.sidebranden.builder.task.SvnTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class CreateProjectService extends BaseService {

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    SigningKeyService signingKeyService;

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Autowired
    BuildDependencyService buildDependencyService;

    @Autowired
    private AppIconService appIconService;

    @Value("${whive.distribution.UserRootPath}")
    private String UserRootPath;

    @Value("${whive.distribution.UserGitRepositoryPath}")
    private String UserGitRepositoryPath;

    @Value("${whive.distribution.UserSvnRepositoryPath}")
    private String UserSvnRepositoryPath;

    @Value("${whive.distribution.UserBranchResource}")
    private String UserBranchResourcePath;

    private String systemUserHomePath = System.getProperty("user.home");

    WebSocketSession sessionTemp;

    private ObjectMapper Mapper = new ObjectMapper();
    private JSONParser parser = new JSONParser();

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    // 1. Workspace Create
    // @param : workspace name
    public void createWorkspaceStartCLI(String addWorkspacePath){
        File createWorkspaceDir = new File(systemUserHomePath + UserRootPath +"builder_main/"+ addWorkspacePath);
        // 디렉토리 생성 과정 만드는 과정 소스 코드 변경하기
        if (!createWorkspaceDir.exists()) {
            try{
                createWorkspaceDir.mkdirs();
                log.info("create Workspace Dir.. ");

            }
            catch(Exception e){
                log.error(e.getMessage(), e);
            }
        }else {

        }

    }

    // 1. Git workspace Create
    // @param : workspace name
    public void createVcsWorkspaceStartCLI(String addWorkspacePath, String vcsType) {
        File createWorkspaceDir;
        if (vcsType.toLowerCase().contains("git")) {
            createWorkspaceDir = new File(UserGitRepositoryPath + addWorkspacePath);
        } else {
            createWorkspaceDir = new File(UserSvnRepositoryPath + addWorkspacePath);
        }

        // 디렉토리 생성 과정 만드는 과정 소스 코드 변경하기
        if (!createWorkspaceDir.exists()) {
            try{
                createWorkspaceDir.mkdirs(); //폴더 생성합니다.
                log.info("폴더가 생성되었습니다.");
            }
            catch(Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }


    // 2. Project Create
    // @param : workspace name
    // @param : platform type
    // @param : project name
    // @param : json | Git repogitory url, userID, password
    public void createProjectStartCLI(String addWorkspacePath, String addPlatformPath, String addProjectPath){

        CommandLine commandLineCdPlaform;
        CommandLine commandLineCdProject;

        // find.. platform 이 있는 경우 없는 경우 일때 체크
        File dir = new File(systemUserHomePath + UserRootPath + "/" + addWorkspacePath + "/" + addPlatformPath);

        // platform dir 있으면 project dir 생성
        if(dir.isDirectory()){
            commandLineCdProject = CommandLine.parse("mkdir");
            commandLineCdProject.addArgument(systemUserHomePath + UserRootPath + "/" + addWorkspacePath + "/" + addPlatformPath + "/" +addProjectPath);

            executueCommonsCLIToMkdirAdd(null, commandLineCdProject, "", null); // project mkdir

            // platform dir 없으면  platform, project 순차로 dir 생성
        }else {
            commandLineCdPlaform = CommandLine.parse("mkdir");
            commandLineCdPlaform.addArgument(systemUserHomePath + UserRootPath + "/" + addWorkspacePath + "/" + addPlatformPath);

            commandLineCdProject = CommandLine.parse("mkdir");
            commandLineCdProject.addArgument(systemUserHomePath + UserRootPath + "/" + addWorkspacePath + "/" + addPlatformPath + "/" +addProjectPath);

            executueCommonsCLIToMkdirAdd(null, commandLineCdPlaform, "", null); // project mkdir
            executueCommonsCLIToMkdirAdd(null, commandLineCdProject, "", null); // project mkdir

        }

    }

    // 2. git checkout
    // @param parseResult
    // @param repositoryObj
    // @param jsonSigningkey
    @Async("asyncThreadPool")
    public void createProjectGitHubCloneCLI(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj){
        log.info(" createProjectGitHubCloneCLI : check... ");
        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();


        CommandLine commandLineGitCloneProject;
        projectGitCloneMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
        projectCreateMessage(session, projectGitCloneMessage,"","GITCLONE");
//        gitTask.gitClone(parseResult, "", repositoryObj);
        if (gitTask.gitClone(parseResult, "", repositoryObj)) {
            try {
                createProject(session, "git", parseResult);
            } catch (Exception e) {
                log.error("project create failed = {}", e.getLocalizedMessage());
            }
        }
    }

    // 전역 변수 값 저장
    private void globalParameterSetting(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj){


    }

    // 2. svn check out
    // @param addWorkspacePath
    // @param addProjectName
    // @param repositoryObj
    public void createProjectSvnCheckoutCLI(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj){

        CommandLine commandLineSvnCheckProject;

        String shellscriptFileName = getClassPathResourcePath("projectVCS.sh");
        shellscriptFileName = shellscriptFileName.replace("/projectVCS.sh","");

        // 전역 변수 값 저장
        globalParameterSetting(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj);

        try {
            String repositoryID = repositoryObj.get("repositoryId").toString();
            String repositoryPassword = repositoryObj.get("repositoryPassword").toString();
            String repoPath = repositoryObj.get("repositoryURL").toString();
            String localPath = systemUserHomePath + UserRootPath + "builder_main/DOMAIN_" + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/USER_" + parseResult.get(PayloadMsgType.userID.name()).toString() + "/WORKSPACE_W"
                    + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString();

            svnTask.svnCheckout(repositoryID, repositoryPassword, new URI(repoPath), new URI(localPath));
        } catch (Exception e) {
            log.error("SVN Checkout Error = {}", e.getLocalizedMessage(), e);
        }
    }

    private void createProject(WebSocketSession session, String VcsType, Map<String, Object> parseResult) throws IOException {
        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        try {
            if (VcsType.toLowerCase().equals("mkdir")) {

            } else {

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

                String language = parseResult.get("language").toString();
                parseResultObj = parseResult;

                UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/"
                        + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() + "/" + BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString()
                        + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString(); // 해당 구간 수정 필요

                productType = parseResult.get("product_type").toString();
                platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();
                projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
                projectDirName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
                signingKeyJson = (JSONObject) parser.parse(jsonSigningkey);
                applyConfigJson = (JSONObject) parser.parse(jsonApplyConfig);
                repositoryJson = (JSONObject) parser.parse(jsonRepository);
                String arrayMultiProfileConfigStr = parseResult.get("BuildSettings").toString();

                applyConfigJson.put("language", language);
                arrayServerConfigStr = ""; // server config 지워야함...

                hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();

                if (appIconZipFileName.equals("")) {

                    String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");

                    CommandLine commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_ + parseResultObj.get(PayloadMsgType.domainID.name()).toString() + "/" + BuilderDirectoryType.USER_ + parseResultObj.get(PayloadMsgType.userID.name()).toString() + "/" + BuilderDirectoryType.WORKSPACE_W + parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() + "/" + projectName + "/build_logfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir, "mkdir"); // method name 수정 필요.

                    commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_ + parseResultObj.get(PayloadMsgType.domainID.name()).toString() + "/" + BuilderDirectoryType.USER_ + parseResultObj.get(PayloadMsgType.userID.name()).toString() + "/" + BuilderDirectoryType.WORKSPACE_W + parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() + "/" + projectName + "/appfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir, "mkdir"); // method name 수정 필요.

                } else {
                    String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");

                    CommandLine commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_ + parseResultObj.get(PayloadMsgType.domainID.name()).toString() + "/" + BuilderDirectoryType.USER_ + parseResultObj.get(PayloadMsgType.userID.name()).toString() + "/" + BuilderDirectoryType.WORKSPACE_W + parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() + "/" + projectName + "/build_logfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir, "mkdir"); // method name 수정 필요.

                    commandLineTemplateMkdir = CommandLine.parse(shellscriptFileName);
                    commandLineTemplateMkdir.addArgument("createTemplateMkdir"); // 1
                    commandLineTemplateMkdir.addArgument(systemUserHomePath + UserRootPath + "builder_main/"); // 2
                    commandLineTemplateMkdir.addArgument(BuilderDirectoryType.DOMAIN_ + parseResultObj.get(PayloadMsgType.domainID.name()).toString() + "/" + BuilderDirectoryType.USER_ + parseResultObj.get(PayloadMsgType.userID.name()).toString() + "/" + BuilderDirectoryType.WORKSPACE_W + parseResultObj.get(PayloadMsgType.workspaceID.name()).toString() + "/" + projectName + "/appfiles"); // 3
                    executueCommonsCLIToResult(commandLineTemplateMkdir, "mkdir"); // method name 수정 필요.
                }

                // keystore file edit
                if (platformZip.toLowerCase().equals(PayloadMsgType.android.name())) {

                    // local properties 파일 생성
                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("755");
                    commandLine.addArgument(UserProjectPath + "/" + projectName + "/" + "gradlew");

                    executueCommonsCLIToResult(commandLine, "chmod");

                    createLocalPropertiesFile(UserProjectPath + "/" + projectName);
                    updateGradlePropertiesFile(UserProjectPath + "/" + projectName); // gradle.java.home setting

                    JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platformZip, UserProjectPath + "/" + projectName);
                    JsonElement element = JsonParser.parseString(jsonAppConfig.toJSONString());
                    JsonObject jsonReulst = gson.fromJson(element, JsonObject.class);

                    // app config 수정 cli 진행
                    // mobileTemplateConfigService.templateMultiProfileConfigAllCLI(sessionTemp, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);
                    mobileTemplateConfigService.templateMultiProfileAutoKeySetCreateAllCLI(session, parseResult, jsonReulst, ServerConfigMode.CREATE);

                    // signing key service 시작
                    signingKeyService.createSigningkeyFileToProperties(UserProjectPath + "/" + projectDirName, platformZip, signingKeyJson);

                    // 앱 아이콘 적용 이후 git add/commit/push 진행
                    if (appIconZipFileName.equals("")) {

                    } else {
                        if (VcsType.toLowerCase().equals("git")) {
                            // appIconService.appIconUnzipAction(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, null, hqKeyTemp);
                            appIconService.setAppIconGeneratorCLI(session, parseResult);
                        } else if (VcsType.toLowerCase().equals("svn")) {
                            //appIconService.appIconUnzipAndSVNCLI(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, repositoryJson);
                            appIconService.setAppIconGeneratorCLI(session, parseResult);
                        }
                    }
                    projectGitCloneMessage.setHqKey(hqKeyTemp);
                    projectGitCloneMessage.setBuild_id(Integer.valueOf(parseResult.get("projectID").toString()));
                    projectGitCloneMessage.setProjectDirPath(projectDirName);
                    //projectGitCloneMessage.set
                    // android deploy profile 설정
                    deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath + "/" + projectName);

                    try {
                        if (VcsType.toLowerCase().contains("git")) {
                            projectCreateMessage(session, projectGitCloneMessage,"","GIT PUSH");
                            gitTask.gitPush(parseResult, UserProjectPath + "/" + projectDirName, repositoryJson, "ProjectCreateSet");
                        } else if (VcsType.toLowerCase().contains("svn")) {
                            projectCreateMessage(session, projectGitCloneMessage,"","SVN COMMIT");
                            String repositoryID = repositoryJson.get("repositoryId").toString();
                            String repositoryPassword = repositoryJson.get("repositoryPassword").toString();

                            svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                            svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "ProjectCreateSet");
                        }
                    } catch (Exception e) {
                        log.error("Git push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                    }

                    projectCreateMessage(session, projectGitCloneMessage,"","DONE");

                } else if (platformZip.toLowerCase().equals(PayloadMsgType.ios.name())) {

                    arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();
                    arrayJSONCertificateStr = parseResult.get("arrayJSONCertificates").toString();
                    String textToBuildSettingsGson = parseResult.get("BuildSettingsGson").toString();

                    appconfigMultiArrayJson = (JsonArray) JsonParser.parseString(textToBuildSettingsGson);

                    log.info(arrayJSONProfilesStr);
                    log.info(arrayJSONCertificateStr);

                    CommandLine commandLine = CommandLine.parse("chmod");
                    commandLine.addArgument("-R");
                    commandLine.addArgument("755");
                    commandLine.addArgument(UserProjectPath + "/" + projectName + "/archive.sh");

                    executueCommonsCLIToResult(commandLine, "chmod");

                    CommandLine commandLineMACOSRm = CommandLine.parse("rm");
                    commandLineMACOSRm.addArgument("-rf");
                    commandLineMACOSRm.addArgument(UserProjectPath + "/" + projectName + "/__MACOSX");

                    executueCommonsCLIToResult(commandLineMACOSRm, "rm");

                    String getProjectNamePath = mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platformZip, UserProjectPath, projectName);
                    applyConfigJson.put("getProjectNamePath", getProjectNamePath);

                    // app config 수정 cli 진행
                    /* TODO:  multi profile setconfig targot 기반 메소드 적용 기존과 동일함 */
                    // mobileTemplateConfigService.templateMultiProfileConfigAllCLI(sessionTemp, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);
                    mobileTemplateConfigService.templateMultiProfileConfigAllCLI(session, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);

                    // signingKeyService.createSigningkeyToCertFile(UserProjectPath, signingKeyJson, productType);
                    signingKeyService.createSigningkeyToMultiCertFile(UserProjectPath, arrayJSONCertificateStr, productType);

                    /* TODO  createProfileSettingToMultiProfilesProject 기반 메소드 target, jsonMatrix 값 적용 적용 */
                    JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platformZip, UserProjectPath + "/" + projectName);
                    JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("targets");
                    String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

                    /* TODO  appconfigMultiArrayJson 객체 data setting 적용  */
                    signingKeyService.createProfileSettingToMultiProfilesProject(UserProjectPath + "/" + projectName, arrayJSONProfilesStr, productType, targetKeyList, appconfigMultiArrayJson);

                    String tempProfilesOneStr = arrayJSONProfilesStr;

                    //tempProfilesOneStr = tempProfilesOneStr.replaceAll(" ", ""); // 인증서 파일명에 공백이 있을경우 에러가 발생하므로 주석
                    tempProfilesOneStr = tempProfilesOneStr.replace("[{", "");
                    tempProfilesOneStr = tempProfilesOneStr.replace("}]", "");

                    String[] jsonProfilesArray = tempProfilesOneStr.split("\\},\\{");

                    for (int i = 0; i < jsonProfilesArray.length; i++) {
                        jsonProfilesArray[i] = "{" + jsonProfilesArray[i] + "}";
                        try {
                            JSONObject jsonProfile = (JSONObject) parser.parse(jsonProfilesArray[i]);

                            // show profiles
                            JSONObject resultShwoProfiles = signingKeyService.showProfileOneInfo(session, jsonProfile.get("profiles_path").toString(), productType);
                            log.info(resultShwoProfiles.toJSONString());

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // .xcodeproj 디렉토리 수정
                    String getProjectProjNameTemp = getProjectNamePath.replace(".xcodeproj", "");
                    // 앱 아이콘 적용 이후 git add/commit/push 진행
                    /* TODO  app icon generator 메소드 적용 */
                    if (appIconZipFileName.equals("")) {

                    } else {
                        if (VcsType.toLowerCase().equals("git")) {
                            appIconService.setAppIconGeneratorCLI(session, parseResult);
                        } else if (VcsType.toLowerCase().equals("svn")) {
                            appIconService.setAppIconGeneratorCLI(session, parseResult);
                        }
                    }

                    /**
                     * ios pod 파일 있을때 pod install 명령어 수행하는 기능
                     */
                    buildDependencyService.validatePodFileInstall(UserProjectPath + "/" + projectName);

                    deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath + "/" + projectName);

                    projectGitCloneMessage.setHqKey(hqKeyTemp);
                    projectGitCloneMessage.setBuild_id(Integer.valueOf(parseResult.get("projectID").toString()));
                    projectGitCloneMessage.setProjectDirPath(projectDirName);

                    try {
                        if (VcsType.toLowerCase().contains("git")) {
                            projectCreateMessage(session, projectGitCloneMessage,"","GIT PUSH");
                            gitTask.gitPush(parseResult, UserProjectPath + "/" + projectDirName, repositoryJson, "ProjectCreateSet");
                        } else if (VcsType.toLowerCase().contains("svn")) {
                            projectCreateMessage(session, projectGitCloneMessage,"","SVN COMMIT");
                            String repositoryID = repositoryJson.get("repositoryId").toString();
                            String repositoryPassword = repositoryJson.get("repositoryPassword").toString();

                            svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                            svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "ProjectCreateSet");
                        }
                    } catch (Exception e) {
                        log.error("Git push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                    }
                    projectCreateMessage(session, projectGitCloneMessage,"","DONE");
                }
            }
        } catch (Exception e) {
            log.error("error create project = {}", e.getLocalizedMessage(), e);
        }
    }

    // 2.1 common cli mkdir // 추후 svn 없애면서 삭제하기
    private void executueCommonsCLIToMkdirAdd(WebSocketSession session, CommandLine commandLineParse, String VcsType, Map<String, Object> parseResult){
        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();

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
            // vcs clone, checkout ... send message
            if (VcsType.toLowerCase().equals("git")){
                projectGitCloneMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
                projectCreateMessage(session, projectGitCloneMessage,"","GITCLONE");
            }else if (VcsType.toLowerCase().equals("svn")){
                projectGitCloneMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
                projectCreateMessage(session, projectGitCloneMessage,"","SVNCHECKOUT");
            }


            while ((tmp = is.readLine()) != null)
            {
                  log.info(" #### Gradle git clone CLI CommandLine log data ### : " + tmp);
            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                // send message type

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

                    String language = parseResult.get("language").toString();
                    parseResultObj = parseResult;

                    UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"
                            + BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString()
                            + "/" + BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString() ; // 해당 구간 수정 필요

                    productType = parseResult.get("product_type").toString();
                    platformZip = parseResult.get(PayloadMsgType.platform.name()).toString();
                    projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
                    projectDirName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
                    signingKeyJson = (JSONObject) parser.parse(jsonSigningkey);
                    applyConfigJson = (JSONObject) parser.parse(jsonApplyConfig);
                    repositoryJson = (JSONObject) parser.parse(jsonRepository);
                    String arrayMultiProfileConfigStr = parseResult.get("BuildSettings").toString();

                    //appIconZipFileName = repositoryJson.get("zipFileName").toString();

                    applyConfigJson.put("language",language);
                    arrayServerConfigStr = ""; // server config 지워야함...





                    hqKeyTemp = parseResult.get(PayloadMsgType.hqKey.name()).toString();

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

                        JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platformZip, UserProjectPath+"/"+projectName);
                        JsonElement element = JsonParser.parseString(jsonAppConfig.toJSONString());
                        JsonObject jsonReulst =  gson.fromJson(element, JsonObject.class);

                        // app config 수정 cli 진행
                        // mobileTemplateConfigService.templateMultiProfileConfigAllCLI(sessionTemp, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);
                        mobileTemplateConfigService.templateMultiProfileAutoKeySetCreateAllCLI(session, parseResult, jsonReulst, ServerConfigMode.CREATE);

                        // signing key service 시작
                        signingKeyService.createSigningkeyFileToProperties(UserProjectPath+"/"+projectDirName, platformZip, signingKeyJson);

                        // 앱 아이콘 적용 이후 git add/commit/push 진행
                        if(appIconZipFileName.equals("")) {

                        }else {
                            if (VcsType.toLowerCase().equals("git")) {
                                // appIconService.appIconUnzipAction(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, null, hqKeyTemp);
                                appIconService.setAppIconGeneratorCLI(session, parseResult);
                            } else if (VcsType.toLowerCase().equals("svn")) {
                                 //appIconService.appIconUnzipAndSVNCLI(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, repositoryJson);
                                appIconService.setAppIconGeneratorCLI(session, parseResult);
                            }
                        }

                        // android deploy profile 설정
                        deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);

                        try {
                            if (VcsType.toLowerCase().contains("git")) {
                                gitTask.gitPush(parseResult, UserProjectPath + "/" + projectDirName, repositoryJson, "ProjectCreateSet");
                            } else if (VcsType.toLowerCase().contains("svn")) {
                                String repositoryID = repositoryJson.get("repositoryId").toString();
                                String repositoryPassword = repositoryJson.get("repositoryPassword").toString();

                                svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                                svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "ProjectCreateSet");
                            }
                        } catch (Exception e) {
                            log.error("Git push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                        }

                    }else if(platformZip.toLowerCase().equals(PayloadMsgType.ios.name())){

                        arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();
                        arrayJSONCertificateStr = parseResult.get("arrayJSONCertificates").toString();
                        String textToBuildSettingsGson = parseResult.get("BuildSettingsGson").toString();

                        appconfigMultiArrayJson = (JsonArray) JsonParser.parseString(textToBuildSettingsGson);

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
                        /* TODO:  multi profile setconfig targot 기반 메소드 적용 기존과 동일함 */
                        // mobileTemplateConfigService.templateMultiProfileConfigAllCLI(sessionTemp, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);
                        mobileTemplateConfigService.templateMultiProfileConfigAllCLI(session, platformZip, UserProjectPath, projectName, applyConfigJson, arrayServerConfigStr, arrayMultiProfileConfigStr, ServerConfigMode.CREATE, productType);


                        // signingKeyService.createSigningkeyToCertFile(UserProjectPath, signingKeyJson, productType);
                        signingKeyService.createSigningkeyToMultiCertFile(UserProjectPath, arrayJSONCertificateStr, productType);

                        /* TODO  createProfileSettingToMultiProfilesProject 기반 메소드 target, jsonMatrix 값 적용 적용 */
                        JSONObject jsonAppConfig = mobileTemplateConfigService.getProjectCreateAfterConfigAllCLI(parseResult, platformZip, UserProjectPath+"/"+projectName);
                        JSONObject jsonMatrix = (JSONObject) jsonAppConfig.get("targets");
                        String[] targetKeyList = (String[]) jsonMatrix.keySet().toArray(new String[jsonMatrix.size()]);

                        /* TODO  appconfigMultiArrayJson 객체 data setting 적용  */
                        signingKeyService.createProfileSettingToMultiProfilesProject(UserProjectPath+"/"+projectName, arrayJSONProfilesStr, productType, targetKeyList, appconfigMultiArrayJson);
//                        signingKeyService.createProfileSettingToDebugProject(UserProjectPath +"/"+projectName, jsonProfileDebug, productType);
//                        signingKeyService.createProfileSettingToReleaseProject(UserProjectPath+"/"+projectName, jsonProfileRelease, productType);

                        // show profile
//                        String[] showProfileDebugStr = signingKeyService.showProfilesInfo(session, jsonProfileDebug, productType);
//                        String[] showProfileReleaseStr = signingKeyService.showProfilesInfo(session, jsonProfileRelease, productType);

                        // set exportoptions plist setting
//                        signingKeyService.setExportOptionsDebug(session, UserProjectPath+"/"+projectName, showProfileDebugStr, applyConfigJson, productType);
//                        signingKeyService.setExportOptionsRelease(session, UserProjectPath+"/"+projectName, showProfileReleaseStr, applyConfigJson, productType);
                        String tempProfilesOneStr = arrayJSONProfilesStr;

                        //tempProfilesOneStr = tempProfilesOneStr.replaceAll(" ",""); // 인증서 파일명에 공백이 있을경우 에러가 발생하므로 주석
                        tempProfilesOneStr = tempProfilesOneStr.replace("[{","");
                        tempProfilesOneStr = tempProfilesOneStr.replace("}]","");

                        String[] jsonProfilesArray = tempProfilesOneStr.split("\\},\\{");

                        for(int i = 0; i < jsonProfilesArray.length; i++){
                            jsonProfilesArray[i] = "{" + jsonProfilesArray[i] + "}";
                            try {
                                JSONObject jsonProfile = (JSONObject) parser.parse(jsonProfilesArray[i]);

                                // show profiles
                                JSONObject resultShwoProfiles = signingKeyService.showProfileOneInfo(session, jsonProfile.get("profiles_path").toString(), productType);
                                log.info(resultShwoProfiles.toJSONString());
                                /* TODO  사용 안하는 메소드 */
                                // signingKeyService.setExportOptionsMultiProfiles(session, UserProjectPath+"/"+projectName, resultShwoProfiles, jsonProfile, productType);

                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        }

                        // .xcodeproj 디렉토리 수정
                        String getProjectProjNameTemp = getProjectNamePath.replace(".xcodeproj", "");
                        // 앱 아이콘 적용 이후 git add/commit/push 진행
                        /* TODO  app icon generator 메소드 적용 */
                        if(appIconZipFileName.equals("")) {

                        }else {
                            if (VcsType.toLowerCase().equals("git")) {
                                appIconService.setAppIconGeneratorCLI(session, parseResult);
                                // appIconService.appIconUnzipAction(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName + "/" + getProjectProjNameTemp, null, hqKeyTemp);
                            } else if (VcsType.toLowerCase().equals("svn")) {
                                appIconService.setAppIconGeneratorCLI(session, parseResult);
                                // appIconService.appIconUnzipAndSVNCLI(session, platformZip, UserProjectPath, appIconZipFileName, projectDirName, repositoryJson);
                            }
                        }

                        /**
                         * ios pod 파일 있을때 pod install 명령어 수행하는 기능
                         */
                        buildDependencyService.validatePodFileInstall(UserProjectPath+"/"+projectName);

                        deploySettingInitService.deploySettingInit(session, parseResult, UserProjectPath+"/"+projectName);

                        try {
                            if (VcsType.toLowerCase().contains("git")) {
                                gitTask.gitPush(parseResult, UserProjectPath + "/" + projectDirName, repositoryJson, "ProjectCreateSet");
                            } else if (VcsType.toLowerCase().contains("svn")) {
                                String repositoryID = repositoryJson.get("repositoryId").toString();
                                String repositoryPassword = repositoryJson.get("repositoryPassword").toString();

                                svnTask.svnAdd(new URI(UserProjectPath + "/" + projectName));
                                svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectName), "ProjectCreateSet");
                            }
                        } catch (Exception e) {
                            log.error("Git push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                        }
                    }

                    // applyConfig 비정상 수행 원인 확인 CLI 수정 요청 해야함.
                    // platformZip
                    // UserProjectPath
                    // applyConfigJson

                }


            }else if(exitCode == 1){
                if(VcsType.toLowerCase().equals("mkdir")){
                    log.info(" exitCode : {}", exitCode);
                }

            }

            handler.stop();

        } catch (IOException e) {

            log.error("builder create project service error",e);
        } catch (InterruptedException e) {

            log.error("builder create project service error",e);
        } catch (ParseException e) {

            log.error("builder create project service error",e);
        }

    }

    private void projectCreateMessage(WebSocketSession session, ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){

        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        // projectGitCloneMessage.setHqKey(hqKeyTemp); // hqKey 브라우저 아이디

        projectGitCloneMessage.setGitStatus(gitStatus);

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
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

        out.flush();
        out.close();
    }


}
