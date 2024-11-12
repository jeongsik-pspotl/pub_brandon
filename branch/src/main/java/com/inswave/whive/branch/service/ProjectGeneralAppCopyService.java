package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.ProjectTemplateCreateStatusMessage;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.GitTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

@Slf4j
@Service
public class ProjectGeneralAppCopyService extends BaseService {

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Value("${whive.distribution.UserGitRepositoryPath}")
    private String userGitRepositoryPath;

    @Autowired
    SigningKeyService signingKeyService;

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Autowired
    BuildDependencyService buildDependencyService;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    private GitTask gitTask;

    private String systemUserHomePath = System.getProperty("user.home");

    @Async("asyncThreadPool")
    public void createGeneralAppProject(WebSocketSession session, Map<String, Object> parseResult, JSONObject repositoryObj, JSONObject signingkeyObj, JSONObject applyConfigObj, JSONObject builderSettingObj) {

        ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage = new ProjectTemplateCreateStatusMessage();
        String userProjectPath;

        try {
            // 프로젝트 생성에 필요한 변수 설정
            String domainName = BuilderDirectoryType.DOMAIN_.name() + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userName = BuilderDirectoryType.USER_.name() + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspaceName = BuilderDirectoryType.WORKSPACE_W.name() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P.name() + parseResult.get(PayloadMsgType.projectID.name()).toString();

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            platform = platform.toLowerCase();
            String build_id = parseResult.get(PayloadMsgType.projectID.name()).toString();
            String product_type = parseResult.get("product_type").toString();
            String jsonSigningKey = parseResult.get("jsonSigningkey").toString();
            String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            userProjectPath = systemUserHomePath + userRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_.name() + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/" + BuilderDirectoryType.USER_.name() + parseResult.get(PayloadMsgType.userID.name()).toString()
                            + "/" + BuilderDirectoryType.WORKSPACE_W.name() + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.name() + parseResult.get(PayloadMsgType.projectID.name()).toString();

            String realProjectPath = userProjectPath + "/" + projectName;

            // 프로젝트 메시지 셋팅
            projectCreateMessageSetting(projectTemplateCreateStatusMessage, build_id, projectName, hqKey);

            // HQ에 MKDIR 메시지 발송
            projectCreateMessage(session, projectTemplateCreateStatusMessage, "","MKDIR");

            // 프로젝트 디렉토리 생성
            File localProjectFolder = new File(userProjectPath + "/build_logfiles");
            if (!localProjectFolder.exists()) {
                localProjectFolder.mkdirs();
                localProjectFolder = new File(userProjectPath + "/appfiles");
                localProjectFolder.mkdirs();
                // 프로젝트 폴더에 777 권한 할당
                localProjectFolder = new File(userProjectPath);
                localProjectFolder.setReadable(true, false);
                localProjectFolder.setWritable(true, false);
                localProjectFolder.setExecutable(true, false);
            }

            // git Repository 디렉토리 만들고, 775 권한 할당
            Path gitRepoPath = Paths.get(userGitRepositoryPath + domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
            File gitRepoDir = new File(gitRepoPath.toUri());
            if (!gitRepoDir.exists()) {
                Files.createDirectories(gitRepoPath);

                Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
                permissions.add(PosixFilePermission.OWNER_READ);
                permissions.add(PosixFilePermission.OWNER_WRITE);
                permissions.add(PosixFilePermission.OWNER_EXECUTE);

                permissions.add(PosixFilePermission.GROUP_READ);
                permissions.add(PosixFilePermission.GROUP_WRITE);
                permissions.add(PosixFilePermission.GROUP_EXECUTE);

                permissions.add(PosixFilePermission.OTHERS_READ);
                permissions.add(PosixFilePermission.OTHERS_EXECUTE);

                Files.setPosixFilePermissions(gitRepoPath, permissions);
            }

            //HQ에 GITBARE 메시지 발송
            projectCreateMessage(session, projectTemplateCreateStatusMessage, "", "GITBARE");
            //git init --bare
            gitTask.gitWithSSH("mkdir", userGitRepositoryPath, domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");
            gitTask.gitWithSSH("bareInit", userGitRepositoryPath, domainName + "/" + userName + "/" + workspaceName + "/" + projectName + ".git");

            //HQ에 GITCLONE 메시지 발송
            projectCreateMessage(session, projectTemplateCreateStatusMessage, "","GITCLONE");
            gitTask.gitClone(parseResult, realProjectPath, repositoryObj);

            // fastlane init을 원할히 하기 위해,
            // fastlane 폴더와 비어있는 AppFile, FastFile을 생성한다.
            URI fastlaneFolderPath = new URI(userProjectPath + "/" + projectName + "/fastlane");
            File fastlaneFolder = new File(fastlaneFolderPath.toString());
            if (!fastlaneFolder.exists()) {
                fastlaneFolder.mkdir();

                File AppFile = new File(fastlaneFolderPath.toString() + "/AppFile");
                if (!AppFile.exists()) {
                    AppFile.createNewFile();
                }

                File FastFile = new File(fastlaneFolderPath.toString() + "/FastFile");
                if (!FastFile.exists()) {
                    FastFile.createNewFile();
                }
            }

            if (platform.equals(PayloadMsgType.android.name())) {
                // local.properties, gradle.properties 생성
                createLocalPropertiesFile(realProjectPath);
                updateGradlePropertiesFile(realProjectPath);
                // Signing key 설정
                signingKeyService.generalAndroidAppSigning(realProjectPath, jsonSigningKey);
            } else if (platform.equals(PayloadMsgType.ios.name())) {
                // iOS Signing key 설정
                String arrayJSONCertificateStr = parseResult.get("arrayJSONCertificate").toString();
                String arrayJSONProfilesStr = parseResult.get("arrayJSONProfiles").toString();

                signingKeyService.createSigningkeyToMultiCertFile(userProjectPath, arrayJSONCertificateStr, product_type);

                signingKeyService.generaliOSAppSetProfile(realProjectPath, arrayJSONProfilesStr);
                buildDependencyService.validatePodFileInstall(realProjectPath);
            } else {
                log.error(platform + "은 현재 지원하지 않는 플랫폼 입니다.");
            }

            // Deploy Setting
            deploySettingInitService.deploySettingInit(session, parseResult, realProjectPath);

            //HQ에 DONE 메시지 발송
            projectCreateMessage(session, projectTemplateCreateStatusMessage, "","DONE");
        } catch (Exception e) {
            log.error("Create General App Project Failed = {}", e.getMessage(), e);
        }
    }

    private void createLocalPropertiesFile(String path) throws IOException {
        String buildAfterLogFile = path + "/local.properties";

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

    private void projectCreateMessageSetting(ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage, String buildId, String projectName, String hqKey) {
        projectTemplateCreateStatusMessage.setBuild_id(Integer.parseInt(buildId));
        projectTemplateCreateStatusMessage.setProjectDirPath(projectName);
        projectTemplateCreateStatusMessage.setHqKey(hqKey);
    }

    private void projectCreateMessage(WebSocketSession session, ProjectTemplateCreateStatusMessage projectTemplateCreateStatusMessage, String logMessage, String gitStatus) {
        ObjectMapper Mapper = new ObjectMapper();

        projectTemplateCreateStatusMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO.name());
        projectTemplateCreateStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        projectTemplateCreateStatusMessage.setGitStatus(gitStatus);
        projectTemplateCreateStatusMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectTemplateCreateStatusMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}
