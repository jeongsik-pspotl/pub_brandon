package com.inswave.whive.branch.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.ProjectGitCloneMessage;
import com.inswave.whive.branch.enums.*;
import com.inswave.whive.branch.service.*;
import com.inswave.whive.branch.task.GitTask;
import com.inswave.whive.branch.task.SvnTask;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class ProjetCreateMsgHandler implements BranchHandlable{

    @Autowired
    CreateProjectService createProjectService;

    @Autowired
    AppIconService appIconService;

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    @Autowired
    ProjectSvnCommitService projectSvnCommitService;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    SigningKeyService signingKeyService;

    WebSocketSession sessionTemp;

    private String UserProjectPath;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;
    private String hqKey;
    private String systemUserHomePath = System.getProperty("user.home");

    private ObjectMapper Mapper = new ObjectMapper();

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

        ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();

        JSONParser parser = new JSONParser();
        Object obj = null;
        JSONObject repositoryObj = null;
        JSONObject signingkeyObj = null;
        JSONObject applyConfigObj = null;
        JSONObject jsonProfileDebug = null;
        JSONObject jsonProfileRelease = null;


        // workspace dir 생성 cli
        if(messageType.equals(ProjectServiceType.HV_MSG_WORKSPACE_CREATE_INFO_FROM_BRANCH.toString()) ){
            String workspacePath = parseResult.get("workspace_name").toString();

            createProjectService.createWorkspaceStartCLI(workspacePath);

            // project dir 생성 cli
        }else if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_CREATE_INFO_FROM_BRANCH.toString())){
            String workspacePath = parseResult.get("workspace_name").toString();
            String platformPath = parseResult.get(PayloadMsgType.platform.name()).toString();
            String projectPath = parseResult.get("project_name").toString();

            createProjectService.createProjectStartCLI(workspacePath, platformPath, projectPath);
        }

        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_BRANCH.toString()) || messageType.equals(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name())){

            // workspcae name 처리 부터 하기
            // domain, user, workspace, project
            String workspacePath = parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            // string to json 변환
            String jsonRepository = parseResult.get("jsonRepository").toString();
            String jsonSigningkey = parseResult.get("jsonSigningkey").toString();
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();

            try {
                obj = parser.parse(jsonRepository);
                repositoryObj = (JSONObject) obj;
                signingkeyObj = (JSONObject) parser.parse(jsonSigningkey);
                applyConfigObj = (JSONObject) parser.parse(jsonApplyConfig);

                // vcs type check
                if(repositoryObj.get(PayloadMsgType.vcsType.name()).toString().equals("git")){
                    // workspace 디렉토리 확인
                    // domain, user, workspace 디렉토리 생성
                    workspacePath = BuilderDirectoryType.DOMAIN_.toString()+parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"+ BuilderDirectoryType.USER_.toString()+parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W.toString()+parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
                    createProjectService.createWorkspaceStartCLI(workspacePath);

                    // git hub Clone CLI 실행 Service
                    // platform, workspace_name, project_name, =>> parseResult 파라미터 줄이기
                    createProjectService.createProjectGitHubCloneCLI(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj);
                }
                else if(repositoryObj.get(PayloadMsgType.vcsType.name()).toString().equals("svn")){
                    // workspace 디렉토리 확인
                    createProjectService.createWorkspaceStartCLI(workspacePath);
                    // svn Checkout CLI 실행 Service
                    createProjectService.createProjectSvnCheckoutCLI(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj);
                }

            } catch (ParseException e) {

                log.error("builder project create msg error",e);
            }

        }// msg type 추가 : HV_MSG_PROJECT_APP_CONFIG_INFO
        else if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_APP_CONFIG_INFO.name())){

            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();
            String jsonRepositoryObj = parseResult.get("jsonRepositoryObj").toString();
            String productType = parseResult.get("product_type").toString();
            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String projectDirName = parseResult.get("UserProjectDirName").toString();

            // domain + user + workspace + project path 구조로 변경하기
            UserProjectPath = systemUserHomePath + userRootPath + "builder_main/"+ BuilderDirectoryType.DOMAIN_.toString() + parseResult.get(PayloadMsgType.domainID.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                    "/" + BuilderDirectoryType.WORKSPACE_W.toString() +parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();

            try {
                applyConfigObj = (JSONObject) parser.parse(jsonApplyConfig);
                repositoryObj = (JSONObject) parser.parse(jsonRepositoryObj);

                mobileTemplateConfigService.templateConfigAllCLI(session ,platform, UserProjectPath, projectDirName, applyConfigObj, "", ServerConfigMode.UPDATE, productType, parseResult.get(PayloadMsgType.hqKey.name()).toString());

                if(parseResult.get(PayloadMsgType.platform.name()).toString().toLowerCase().equals(PayloadMsgType.ios.name())){
                    jsonProfileDebug = (JSONObject) parser.parse(parseResult.get("jsonProfileDebug").toString());
                    jsonProfileRelease = (JSONObject) parser.parse(parseResult.get("jsonProfileRelease").toString());

                    String[] showProfileDebugStr = signingKeyService.showProfilesInfo(session, jsonProfileDebug, productType);
                    String[] showProfileReleaseStr = signingKeyService.showProfilesInfo(session, jsonProfileRelease, productType);

                    signingKeyService.setExportOptionsDebug(session, UserProjectPath+"/"+projectDirName, showProfileDebugStr, applyConfigObj, productType);
                    signingKeyService.setExportOptionsRelease(session, UserProjectPath+"/"+projectDirName, showProfileReleaseStr, applyConfigObj, productType);

                }
                // vcs 소스 동기화
                // git, svn 기준 분기처리해서
                // 소스 동기화 처리하기
                Object vcsType = repositoryObj.get(PayloadMsgType.vcsType.name());

                if(vcsType == null || vcsType.toString().equals("")) {
                    sessionTemp = session;
                    projectCreateMessage(session, projectGitCloneMessage,"","DONE");
                } else if(vcsType.toString().equals("git")) {
                    gitTask.gitPush(parseResult, UserProjectPath + "/" + projectDirName, repositoryObj, "appConfigSet");
                } else if(vcsType.toString().equals("svn")) {
                    svnCommit(repositoryObj, projectDirName);
                } else if(vcsType.toString().equals("localgit")) {
                    gitTask.gitPush(parseResult, UserProjectPath + "/" + projectDirName, repositoryObj, "appConfigSet");
                } else if(vcsType.toString().equals("localsvn")) {
                    svnCommit(repositoryObj, projectDirName);
                }

            } catch (ParseException e) {

                log.error("builder project create msg error",e);
            }

        }else if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_MULTI_PROFILE_VCS_CLONE_INFO.name())){

            // workspcae name 처리 부터 하기
            // domain, user, workspace, project
            String workspacePath = parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            // string to json 변환
            String jsonRepository = parseResult.get("jsonRepository").toString();
            String jsonSigningkey = parseResult.get("jsonSigningkey").toString();
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();

            try {
                obj = parser.parse(jsonRepository);
                repositoryObj = (JSONObject) obj;
                signingkeyObj = (JSONObject) parser.parse(jsonSigningkey);
                applyConfigObj = (JSONObject) parser.parse(jsonApplyConfig);

                // vcs type check
                if(repositoryObj.get(PayloadMsgType.vcsType.name()).toString().equals("git")){
                    // workspace 디렉토리 확인
                    // domain, user, workspace 디렉토리 생성
                    workspacePath = BuilderDirectoryType.DOMAIN_.toString()+parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"+ BuilderDirectoryType.USER_.toString()+parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W.toString()+parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + parseResult.get(PayloadMsgType.projectID.name()).toString();
                    createProjectService.createWorkspaceStartCLI(workspacePath);

                    // git hub Clone CLI 실행 Service
                    // platform, workspace_name, project_name, =>> parseResult 파라미터 줄이기
                    createProjectService.createProjectGitHubCloneCLI(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj);
                }
                else if(repositoryObj.get(PayloadMsgType.vcsType.name()).toString().equals("svn")){
                    // workspace 디렉토리 확인
                    createProjectService.createWorkspaceStartCLI(workspacePath);
                    // svn Checkout CLI 실행 Service
                    createProjectService.createProjectSvnCheckoutCLI(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj);
                }

            } catch (ParseException e) {

                log.error("builder project create msg error",e);
            }
        }
    }

    private void svnCommit(JSONObject repositoryObj, String projectDirName) {
        try {
            String repositoryID = repositoryObj.get("repositoryId").toString();
            String repositoryPassword = repositoryObj.get("repositoryPassword").toString();

            svnTask.svnAdd(new URI(UserProjectPath + "/" + projectDirName));
            svnTask.svnCommit(repositoryID, repositoryPassword, new URI(UserProjectPath + "/" + projectDirName), "appConfigSet");
        } catch (Exception e) {
            log.error("SVN Commit Error = {}", e.getLocalizedMessage(), e);
        }
    }

    private void projectCreateMessage(WebSocketSession session, ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){


        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        projectGitCloneMessage.setHqKey(hqKey);

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

        } else if(gitStatus.equals("DONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}
