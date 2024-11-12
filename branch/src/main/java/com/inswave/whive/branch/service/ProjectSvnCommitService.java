package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.ProjectGitCloneMessage;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.Map;

@Slf4j
@Service
public class ProjectSvnCommitService extends BaseService{

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    private ZipUtils zipUtils;

    //@Value("${whive.distribution.profilePath}")
    private String profileSetDir;

    private ObjectMapper Mapper = new ObjectMapper();
    WebSocketSession sessionTemp;

    public void projectSvnCommitAction(WebSocketSession session, String platform, String userProject, String filename, String projectDirName, String commitMsg, JSONObject repositoryJson){
        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();

        sessionTemp = session;

        try {
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // android
                if(repositoryJson != null){
                    // log.info(" appIconUnzipAction, usreProject projectDirName : {}",userProject + projectDirName);

                    // 아래의 cli 호출 소스 코드
                    String shellscriptFileName = getClassPathResourcePath("projectVCS.sh");
                    // shellscriptFileName = shellscriptFileName.replace("/projectVCS.sh","");
                    shellscriptFileName = shellscriptFileName.replace("/projectVCS.sh","");

                    // add, commit, push 작업 시작
                    // app icon add all
                    // log.info(" appIconUnzipAction, userProject : {}",userProject);
                    CommandLine cliSvnAddALL;

                    cliSvnAddALL = CommandLine.parse(shellscriptFileName + "/projectVCS.sh");
                    cliSvnAddALL.addArgument("svn"); // 1
                    cliSvnAddALL.addArgument("add"); // 2
                    cliSvnAddALL.addArgument(userProject); // 3
                    executueVCSCommandALL(cliSvnAddALL, "svn", "svnadd");

                    // app icon commit all
                    CommandLine cliSvnCommitALL;
                    cliSvnCommitALL = CommandLine.parse(shellscriptFileName + "/projectVCS.sh");
                    cliSvnCommitALL.addArgument("svn"); // 1
                    cliSvnCommitALL.addArgument("commit"); // 2
                    cliSvnCommitALL.addArgument(userProject); // 3
                    cliSvnCommitALL.addArgument(commitMsg); // 4
                    // cliGitCommitALL.addArgument("files="+usreProject); // 4
                    executueVCSCommandALL(cliSvnCommitALL, "svn", "svncommit");

                    projectCreateMessage(projectGitCloneMessage,"","DONE");
                }else {
                    // zipUtils.decompressFormAndroidIcon(profileSetDir+filename, userProject + projectDirName +androidxhdpiAppIconPath);

                    projectCreateMessage(projectGitCloneMessage,"","DONE");
                }

                // zipUtils.decompressFormAndroidIcon(userRootPath+"tempZipDir/"+filename, usreProject+ "/03_WHive_Presentation" +androidxhdpiAppIconPath);
                // zipUtils.decompressFormAndroid(userRootPath+"tempZipDir/"+filename,usreProject+ "/WHybrid_Android"+ androidhdpiAppIconPath, usreProject+ "/WHybrid_Android" + androidmdpiAppIconPath, usreProject+ "/WHybrid_Android" +androidxhdpiAppIconPath);

            }else {

                if(repositoryJson != null) {
                    // git 레파지토리 생성시 경로 정책 논의 필요.

                    // git add, commit, push 소스 추가
                    String shellscriptFileName = getClassPathResourcePath("projectGradleVCS.sh");

                    shellscriptFileName = shellscriptFileName.replace("/projectGradleVCS.sh","");

                    // add, commit, push 작업 시작
                    // app icon add all
                    CommandLine cliGitAddALL;
                    cliGitAddALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                    cliGitAddALL.addArgument(":git:addAll"); // 1
                    cliGitAddALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
                    cliGitAddALL.addArgument("destDir="+userProject); // 3

                    executueVCSCommandALL(cliGitAddALL, "git", "gitADD");

                    // app icon commit all
                    CommandLine cliGitCommitALL;
                    cliGitCommitALL = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                    cliGitCommitALL.addArgument(":git:commitAll"); // 1
                    cliGitCommitALL.addArgument(shellscriptFileName + "/vcsControl"); // 2
                    cliGitCommitALL.addArgument("destDir="+userProject); // 3
                    cliGitCommitALL.addArgument("message="+"imageupload"); // 4

                    executueVCSCommandALL(cliGitCommitALL, "git", "gitCommit");

                    // app icon push
                    CommandLine cliGitPush;
                    cliGitPush = CommandLine.parse(shellscriptFileName + "/projectGradleVCS.sh");
                    cliGitPush.addArgument(":git:push"); // 1
                    cliGitPush.addArgument(shellscriptFileName + "/vcsControl"); // 2
                    cliGitPush.addArgument("destDir="+userProject); // 3
                    cliGitPush.addArgument("id="+repositoryJson.get("repositoryId").toString()); // 4
                    cliGitPush.addArgument("pin="+repositoryJson.get("repositoryPassword").toString()); // 5
                    // repositoryJson
                    // push id
                    // push pwd

                    executueVCSCommandALL(cliGitPush, "git", "gitPush");

                    projectCreateMessage(projectGitCloneMessage,"","DONE");
                }else {
                    projectCreateMessage(projectGitCloneMessage,"","DONE");
                }

            }

        } catch (Throwable throwable) {
            log.warn(throwable.getMessage(), throwable); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요

        }

    }

    private void executueVCSCommandALL(CommandLine commandLineParse, String VcsType, String commmandOrder){

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();

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
                projectCreateMessage(projectGitCloneMessage,"","GITADD");
            }else if (commmandOrder.toLowerCase().equals("gitcommit")){
                projectCreateMessage(projectGitCloneMessage,"","GITCOMMIT");
            }else if (commmandOrder.toLowerCase().equals("gitpush")){
                projectCreateMessage(projectGitCloneMessage,"","GITPUSH");
            }else if (commmandOrder.toLowerCase().equals("svn")){
                projectCreateMessage(projectGitCloneMessage,"","SVNCHECKOUT");
            }else if (commmandOrder.toLowerCase().equals("svnadd")){
                projectCreateMessage(projectGitCloneMessage,"","SVNADD");
            }else if (commmandOrder.toLowerCase().equals("svncommit")){
                projectCreateMessage(projectGitCloneMessage,"","SVNCOMMIT");
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

            }else if(exitCode == 1){

            }else {

            }

            handler.stop();

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

    }

    private void projectCreateMessage(ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){


        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());

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
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }

}
