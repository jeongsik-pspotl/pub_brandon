package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.ProjectGitCloneMessage;
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
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
public class ProjectGitPushService extends BaseService {

    private HeadQuaterClientHandler headQuaterClientHandler = new HeadQuaterClientHandler();

    private Integer projectID = 0;
    private ObjectMapper Mapper = new ObjectMapper();

    @Autowired
    private GitTask gitTask;
    WebSocketSession sessionTemp;

    // git task로 변환완료, 삭제
    public void gitPushAction(WebSocketSession session, String platform, String userProject, String projectDirName, Map<String, Object> parseResult, JSONObject repositoryJson, String commitMessage){

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();

        sessionTemp = session;
        String hqKey;

        try {
            projectGitCloneMessage.setHqKey(parseResult.get(PayloadMsgType.hqKey.name()).toString());
            hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            projectID = Integer.valueOf(parseResult.get(PayloadMsgType.projectID.name()).toString());

            projectGitCloneMessage.setBuild_id(Integer.parseInt(parseResult.get(PayloadMsgType.projectID.name()).toString()));
            projectGitCloneMessage.setProjectDirPath(BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString());

            gitTask.gitPush(parseResult, "", repositoryJson, commitMessage);
            projectCreateMessage(session, projectGitCloneMessage,"","GIT ADD & COMMIT");
            projectCreateMessage(session, projectGitCloneMessage,"","GIT PUSH");
            projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//  삭제
//            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
//                // android
//                if(repositoryJson != null){
//
//                    if (repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("localgit")){
//                        String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh");
//
//                        CommandLine commandLineVCSBareCLI;
//
//                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
//                        commandLineVCSBareCLI.addArgument("git"); // 1 git
//                        commandLineVCSBareCLI.addArgument("remote-set-url"); // 2 command name
//                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
//                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() +projectDirName+".git"); // 3
//                        executueVCSCommandALL(session, commandLineVCSBareCLI,"localgit", "remoteset", hqKey); // method name 수정 필요.
//
//                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
//                        commandLineVCSBareCLI.addArgument("git"); // 1 git
//                        commandLineVCSBareCLI.addArgument("appconfigset"); // 2 command name
//                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
//                        commandLineVCSBareCLI.addArgument(commitMessage,false); // 4 appconfigsetting
//                        executueVCSCommandALL(session, commandLineVCSBareCLI,"localgit", "appconfigcommit", hqKey); // method name 수정 필요.
//
//
//                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
//                        commandLineVCSBareCLI.addArgument("git"); // 1 git
//                        commandLineVCSBareCLI.addArgument("push"); // 2 bare
//                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() + projectDirName+".git"); // 3
//                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 4
//                        executueVCSCommandALL(session, commandLineVCSBareCLI,"localgit", "gitpush", hqKey); // method name 수정 필요.
//
//                        projectGitCloneMessage.setBuild_id(Integer.parseInt(parseResult.get(PayloadMsgType.projectID.name()).toString()));
//                        projectGitCloneMessage.setProjectDirPath(BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString());
//
//                        projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//                    }else if(repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("git")){
//                        String shellscriptFileName = getClassPathResourcePath("projectGradleVCS.sh");
//                        shellscriptFileName = shellscriptFileName.replace("/projectGradleVCS.sh","");
//
//                        // add, commit, push 작업 시작
//                        // app icon add all
//
//                        log.info(" appIconUnzipAction, userProject : {}",userProject);
//                        CommandLine cliGitAddALL;
//
//                        cliGitAddALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
//                        cliGitAddALL.addArgument(":git:addAll"); // 1
//                        cliGitAddALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
//                        cliGitAddALL.addArgument("destDir="+userProject+"/"+projectDirName); // 3
//                        //cliGitAddALL.addArgument("files="+usreProject); // 4
//                        executueVCSCommandALL(session, cliGitAddALL, "git", "gitADD", hqKey);
//
//                        // app icon commit all
//                        CommandLine cliGitCommitALL;
//                        cliGitCommitALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
//                        cliGitCommitALL.addArgument(":git:commitAll"); // 1
//                        cliGitCommitALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
//                        cliGitCommitALL.addArgument("destDir="+userProject+"/"+projectDirName); // 3
//                        cliGitCommitALL.addArgument("message="+commitMessage); // 4 appconfigupdate
//                        executueVCSCommandALL(session, cliGitCommitALL, "git", "gitCommit", hqKey);
//
//                        // app icon push
//                        CommandLine cliGitPush;
//                        cliGitPush = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
//                        cliGitPush.addArgument(":git:push"); // 1
//                        cliGitPush.addArgument(shellscriptFileName + "/vcsControl"); // 2
//                        cliGitPush.addArgument("destDir="+userProject+"/"+projectDirName); // 3
//                        cliGitPush.addArgument("id="+repositoryJson.get("repositoryId").toString()); // 4
//                        cliGitPush.addArgument("pin="+repositoryJson.get("repositoryPassword").toString()); // 5
//
//                        executueVCSCommandALL(session, cliGitPush, "git", "gitPush", hqKey);
//
//                        projectGitCloneMessage.setBuild_id(Integer.parseInt(parseResult.get(PayloadMsgType.projectID.name()).toString()));
//                        projectGitCloneMessage.setProjectDirPath(BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString());
//
//                        projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//                    }
//
//                }else {
//
//                    projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//                }
//
//            }else {
//
//                if(repositoryJson != null) {
//
//                    if (repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("localgit")){
//                        String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh");
//
//                        CommandLine commandLineVCSBareCLI;
//
//                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
//                        commandLineVCSBareCLI.addArgument("git"); // 1 git
//                        commandLineVCSBareCLI.addArgument("remote-set-url"); // 2 command name
//                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
//                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() +projectDirName+".git"); // 3
//                        executueVCSCommandALL(session, commandLineVCSBareCLI,"localgit", "remoteset", hqKey); // method name 수정 필요.
//
//                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
//                        commandLineVCSBareCLI.addArgument("git"); // 1 git
//                        commandLineVCSBareCLI.addArgument("appconfigset"); // 2 command name
//                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
//                        commandLineVCSBareCLI.addArgument(commitMessage,false); // 4 appconfigsetting
//                        executueVCSCommandALL(session, commandLineVCSBareCLI,"localgit", "appconfigcommit", hqKey); // method name 수정 필요.
//
//                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
//                        commandLineVCSBareCLI.addArgument("git"); // 1 git
//                        commandLineVCSBareCLI.addArgument("push"); // 2 bare
//                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() + projectDirName+".git"); // 3
//                        commandLineVCSBareCLI.addArgument(userProject); // 4
//                        executueVCSCommandALL(session, commandLineVCSBareCLI,"localgit", "gitpush", hqKey); // method name 수정 필요.
//
//                        projectGitCloneMessage.setBuild_id(Integer.parseInt(parseResult.get(PayloadMsgType.projectID.name()).toString()));
//                        projectGitCloneMessage.setProjectDirPath(BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString());
//
//                        projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//
//
//                    }else if(repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("git")){
//                        // git add, commit, push 소스 추가
//                        String shellscriptFileName = getClassPathResourcePath("projectGradleVCS.sh");
//
//                        shellscriptFileName = shellscriptFileName.replace("/projectGradleVCS.sh","");
//
//                        // add, commit, push 작업 시작
//                        // app icon add all
//                        CommandLine cliGitAddALL;
//                        cliGitAddALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
//                        cliGitAddALL.addArgument(":git:addAll"); // 1
//                        cliGitAddALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
//                        cliGitAddALL.addArgument("destDir="+userProject+"/"+projectDirName); // 3
//
//                        executueVCSCommandALL(session, cliGitAddALL, "git", "gitADD", hqKey);
//
//                        // app icon commit all
//                        CommandLine cliGitCommitALL;
//                        cliGitCommitALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
//                        cliGitCommitALL.addArgument(":git:commitAll"); // 1
//                        cliGitCommitALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
//                        cliGitCommitALL.addArgument("destDir="+userProject+"/"+projectDirName); // 3
//                        cliGitCommitALL.addArgument("message="+commitMessage); // 4 appconfigupdate
//
//                        executueVCSCommandALL(session, cliGitCommitALL, "git", "gitCommit", hqKey);
//
//                        // app icon push
//                        CommandLine cliGitPush;
//                        cliGitPush = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
//                        cliGitPush.addArgument(":git:push"); // 1
//                        cliGitPush.addArgument(shellscriptFileName + "/vcsControl"); // 2
//                        cliGitPush.addArgument("destDir="+userProject+"/"+projectDirName); // 3
//                        cliGitPush.addArgument("id="+repositoryJson.get("repositoryId").toString()); // 4
//                        cliGitPush.addArgument("pin="+repositoryJson.get("repositoryPassword").toString()); // 5
//
//                        executueVCSCommandALL(session, cliGitPush, "git", "gitPush", hqKey);
//
//                        projectGitCloneMessage.setBuild_id(Integer.parseInt(parseResult.get(PayloadMsgType.projectID.name()).toString()));
//                        projectGitCloneMessage.setProjectDirPath(projectDirName);
//
//                        projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//
//                    }
//
//                }else {
//                    projectCreateMessage(session, projectGitCloneMessage,"","DONE");
//                }
//
//            }

        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }

    }

    // git task로 변환 완료, 삭제
    public void localGitPushPlugins(WebSocketSession session, String platform, String userProject, String projectDirName, Map<String, Object> parseResult, JSONObject repositoryJson, String commitMessage){

        sessionTemp = session;

        try {
            gitTask.gitPush(parseResult, "", repositoryJson, commitMessage);
            /* 삭제
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // android
                if(repositoryJson != null){

                    if (repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("localgit")){
                        String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh");

                        CommandLine commandLineVCSBareCLI;

                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
                        commandLineVCSBareCLI.addArgument("git"); // 1 git
                        commandLineVCSBareCLI.addArgument("remote-set-url"); // 2 command name
                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() +projectDirName+".git"); // 3
                        executueVCSPluginsCommandALL(commandLineVCSBareCLI,"localgit", "remoteset"); // method name 수정 필요.

                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
                        commandLineVCSBareCLI.addArgument("git"); // 1 git
                        commandLineVCSBareCLI.addArgument("appconfigset"); // 2 command name
                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
                        commandLineVCSBareCLI.addArgument(commitMessage,false); // 4 appconfigsetting
                        executueVCSPluginsCommandALL(commandLineVCSBareCLI,"localgit", "appconfigcommit"); // method name 수정 필요.


                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
                        commandLineVCSBareCLI.addArgument("git"); // 1 git
                        commandLineVCSBareCLI.addArgument("push"); // 2 bare
                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() + projectDirName+".git"); // 3
                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 4
                        executueVCSPluginsCommandALL(commandLineVCSBareCLI,"localgit", "gitpush"); // method name 수정 필요.

                    }else if(repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("git")) {
                        String shellscriptFileName = getClassPathResourcePath("projectGradleVCS.sh");
                        shellscriptFileName = shellscriptFileName.replace("/projectGradleVCS.sh", "");

                        // add, commit, push 작업 시작
                        // app icon add all

                        log.info(" appIconUnzipAction, userProject : {}", userProject);
                        CommandLine cliGitAddALL;

                        cliGitAddALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                        cliGitAddALL.addArgument(":git:addAll"); // 1
                        cliGitAddALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
                        cliGitAddALL.addArgument("destDir=" + userProject+"/"+projectDirName); // 3
                        //cliGitAddALL.addArgument("files="+usreProject); // 4
                        executueVCSPluginsCommandALL(cliGitAddALL, "git", "gitADD");

                        // app icon commit all
                        CommandLine cliGitCommitALL;
                        cliGitCommitALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                        cliGitCommitALL.addArgument(":git:commitAll"); // 1
                        cliGitCommitALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
                        cliGitCommitALL.addArgument("destDir=" + userProject+"/"+projectDirName); // 3
                        cliGitCommitALL.addArgument("message=" + commitMessage); // 4 appconfigupdate
                        executueVCSPluginsCommandALL(cliGitCommitALL, "git", "gitCommit");

                        // app icon push
                        CommandLine cliGitPush;
                        cliGitPush = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                        cliGitPush.addArgument(":git:push"); // 1
                        cliGitPush.addArgument(shellscriptFileName + "/vcsControl"); // 2
                        cliGitPush.addArgument("destDir=" + userProject+"/"+projectDirName); // 3
                        cliGitPush.addArgument("id=" + repositoryJson.get("repositoryId").toString()); // 4
                        cliGitPush.addArgument("pin=" + repositoryJson.get("repositoryPassword").toString()); // 5

                        executueVCSPluginsCommandALL(cliGitPush, "git", "gitPush");

                    }

                }else {

                }

            }else {

                if(repositoryJson != null) {

                    if (repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("localgit")){
                        String shellscriptVCSFileName = getClassPathResourcePath("projectVCS.sh");

                        CommandLine commandLineVCSBareCLI;

                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
                        commandLineVCSBareCLI.addArgument("git"); // 1 git
                        commandLineVCSBareCLI.addArgument("remote-set-url"); // 2 command name
                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() +projectDirName+".git"); // 3
                        executueVCSPluginsCommandALL(commandLineVCSBareCLI,"localgit", "remoteset"); // method name 수정 필요.

                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
                        commandLineVCSBareCLI.addArgument("git"); // 1 git
                        commandLineVCSBareCLI.addArgument("appconfigset"); // 2 command name
                        commandLineVCSBareCLI.addArgument(userProject +"/"+projectDirName); // 3 remote url
                        commandLineVCSBareCLI.addArgument("appconfigsetting",false); // 4 appconfigsetting
                        executueVCSPluginsCommandALL(commandLineVCSBareCLI,"localgit", "appconfigcommit"); // method name 수정 필요.

                        commandLineVCSBareCLI = CommandLine.parse(shellscriptVCSFileName);
                        commandLineVCSBareCLI.addArgument("git"); // 1 git
                        commandLineVCSBareCLI.addArgument("push"); // 2 bare
                        commandLineVCSBareCLI.addArgument(repositoryJson.get("repositoryURL").toString() + projectDirName+".git"); // 3
                        commandLineVCSBareCLI.addArgument(userProject); // 4
                        executueVCSPluginsCommandALL(commandLineVCSBareCLI,"localgit", "gitpush"); // method name 수정 필요.


                    }else if(repositoryJson.get(PayloadMsgType.vcsType.name()).toString().toLowerCase().equals("git")){
                        // git add, commit, push 소스 추가
                        String shellscriptFileName = getClassPathResourcePath("projectGradleVCS.sh");

                        shellscriptFileName = shellscriptFileName.replace("/projectGradleVCS.sh","");

                        // add, commit, push 작업 시작
                        // app icon add all
                        CommandLine cliGitAddALL;
                        cliGitAddALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                        cliGitAddALL.addArgument(":git:addAll"); // 1
                        cliGitAddALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
                        cliGitAddALL.addArgument("destDir="+userProject+"/"+projectDirName); // 3

                        executueVCSPluginsCommandALL(cliGitAddALL, "git", "gitADD");

                        // app icon commit all
                        CommandLine cliGitCommitALL;
                        cliGitCommitALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                        cliGitCommitALL.addArgument(":git:commitAll"); // 1
                        cliGitCommitALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
                        cliGitCommitALL.addArgument("destDir="+userProject+"/"+projectDirName); // 3
                        cliGitCommitALL.addArgument("message="+"appconfigupdate"); // 4 appconfigupdate

                        executueVCSPluginsCommandALL(cliGitCommitALL, "git", "gitCommit");

                        // app icon push
                        CommandLine cliGitPush;
                        cliGitPush = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                        cliGitPush.addArgument(":git:push"); // 1
                        cliGitPush.addArgument(shellscriptFileName + "/vcsControl"); // 2
                        cliGitPush.addArgument("destDir="+userProject+"/"+projectDirName); // 3
                        cliGitPush.addArgument("id="+repositoryJson.get("repositoryId").toString()); // 4
                        cliGitPush.addArgument("pin="+repositoryJson.get("repositoryPassword").toString()); // 5

                        executueVCSPluginsCommandALL(cliGitPush, "git", "gitPush");

                    }

                }else {

                }

            }
        */
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    private void executueVCSCommandALL(WebSocketSession session, CommandLine commandLineParse, String VcsType, String commmandOrder,String hqKey){

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
        projectGitCloneMessage.setHqKey(hqKey);
        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutResult = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
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
            // log.info(" #### Gradle git clone CLI CommandLine try ... ### : ");
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            // vcs clone, checkout ... send message
            if (commmandOrder.toLowerCase().equals("gitadd")){
                projectCreateMessage(session, projectGitCloneMessage,"","GITADD");
            }else if (commmandOrder.toLowerCase().equals("gitcommit") || commmandOrder.toLowerCase().equals("appconfigcommit")){
                projectCreateMessage(session, projectGitCloneMessage,"","GITCOMMIT");
            }else if (commmandOrder.toLowerCase().equals("gitpush")){
                projectCreateMessage(session, projectGitCloneMessage,"","GITPUSH");
            }else if (commmandOrder.toLowerCase().equals("svn")){
                projectCreateMessage(session, projectGitCloneMessage,"","SVNCHECKOUT");
            }else if (commmandOrder.toLowerCase().equals("remoteset")){
                projectCreateMessage(session, projectGitCloneMessage,"","REMOTESET");
            }


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle git clone CLI CommandLine log  "+commmandOrder+" data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    private void executueVCSPluginsCommandALL(CommandLine commandLineParse, String VcsType, String commmandOrder){

        PipedOutputStream pipedOutput = new PipedOutputStream();
        org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream stdoutResult = new org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream();
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
            // log.info(" #### Gradle git clone CLI CommandLine try ... ### : ");
            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            // vcs clone, checkout ... send message
            if (commmandOrder.toLowerCase().equals("gitadd")){

            }else if (commmandOrder.toLowerCase().equals("gitcommit") || commmandOrder.toLowerCase().equals("appconfigcommit")){

            }else if (commmandOrder.toLowerCase().equals("gitpush")){

            }else if (commmandOrder.toLowerCase().equals("svn")){

            }else if (commmandOrder.toLowerCase().equals("remoteset")){

            }


            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle git clone CLI CommandLine log  "+commmandOrder+" data ### : " + tmp);


            }

            is.close();
            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                log.info(" exitCode : {}", exitCode);
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    private void projectCreateMessage(WebSocketSession session, ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){


        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        // projectGitCloneMessage.setHqKey(hqKey);

        if (gitStatus.equals("GITCLONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("SVNCHECKOUT")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("APPICONUNZIP")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("GITCOMMIT")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("GITADD")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("GITPUSH")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("REMOTESET")){
            projectGitCloneMessage.setGitStatus(gitStatus);

        } else if(gitStatus.equals("DONE")){
            // projectGitCloneMessage.setBuild_id(projectID);
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

}
