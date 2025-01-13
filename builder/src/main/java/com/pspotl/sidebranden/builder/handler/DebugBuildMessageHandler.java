package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.BuildMode;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.inswave.whive.branch.service.*;
import com.pspotl.sidebranden.builder.service.BuildService;
import com.pspotl.sidebranden.builder.service.MobileTemplateConfigService;
import com.pspotl.sidebranden.builder.service.ProjectSvnCommitService;
import com.pspotl.sidebranden.builder.task.GitTask;
import com.pspotl.sidebranden.builder.task.SvnTask;
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
public class DebugBuildMessageHandler implements BranchHandlable {

    @Autowired
    BuildService buildService;

    @Autowired
    private MobileTemplateConfigService mobileTemplateConfigService;

    @Autowired
    private ProjectSvnCommitService projectSvnCommitService;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    @Value("${whive.branch.name}")
    private String branchName;

    @Value("${whive.branch.id}")
    private String userId;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    JSONObject ftpSettingObj = null;
    JSONObject branchObj = null;
    JSONObject buildHistoryObj = null;
    JSONObject vcsSettingObj = null;
    JSONObject appConfigObj = null;

    Object obj = null;
    JSONParser parser = new JSONParser();
    JSONParser branchParser = new JSONParser();
    JSONParser buildHistoryParser = new JSONParser();

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        String debugPlatform = parseResult.get(PayloadMsgType.platform.name()).toString();
        int history_id = Integer.parseInt(parseResult.get("id").toString());

        log.info("messageType" + messageType);
        if(messageType.equals(BuildServiceType.HV_MSG_DEBUG_BUILD.toString())){
            // domain + user + workspace + project
            String workspaceName = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String jsonRepository = parseResult.get("ftpSettingObj").toString();
            String jsonBranchObj = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();

            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();
            String appVersionCode = parseResult.get(PayloadMsgType.appVersionCode.name()).toString();
            String appConfigJSONVo = parseResult.get("appConfigJSON").toString();

            try {
                String repositoryObj = "";
                if(parseResult.get(PayloadMsgType.repositoryObj.name()) == null){
                    repositoryObj = "{}";
                }else {
                    repositoryObj = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
                }

                // branch id, name 체크
                Object bObj = branchParser.parse(jsonBranchObj);
                branchObj = (JSONObject) bObj;
                appConfigObj = (JSONObject) parser.parse(appConfigJSONVo);
                log.info("======================== HV_MSG_RELEASE_BUILD_FROM_BRANCH  ======================");

                if(branchObj.get(PayloadMsgType.branchUserId.name()).toString() == null){
                    return;
                }

                if(branchObj.get(PayloadMsgType.branchName.name()).toString().toLowerCase().equals("")){
                    return;
                }

                obj = parser.parse(jsonRepository);
                ftpSettingObj = (JSONObject) obj;

                buildHistoryObj = (JSONObject) buildHistoryParser.parse(jsonBuildHistoryVo);
                vcsSettingObj = (JSONObject) parser.parse(repositoryObj);

                if (debugPlatform.toLowerCase().equals(PayloadMsgType.windows.name()) && projectName != null) {
                    String projectDir = "";//
                    // template version setting
                    mobileTemplateConfigService.templateConfigCLI(debugPlatform, appVersionCode);

                    // buildHistoryObj 추가
                    buildService.startBuild(session, projectDir, projectName, history_id, debugPlatform, BuildMode.DEV, ftpSettingObj, buildHistoryObj, appVersionCode);
                }

                if ((workspaceName != null || workspaceName.equals("")) && (projectName != null || projectName.equals("")) && !debugPlatform.toLowerCase().equals(PayloadMsgType.windows.name())) {
                    // template version setting
                    if(debugPlatform.toLowerCase().equals(PayloadMsgType.android.name())){

                        // 여기서 svn은 왜 add, commit, push를 하는지? - 수린
                        // else if svn 일 경우
                        if(vcsSettingObj.get(PayloadMsgType.vcsType.name()) != null){
                            if(vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("git")){

                            }else if(vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("svn")){
//                                projectSvnCommitService.projectSvnCommitAction(session, debugPlatform, projectName,"","", appVersionCode + "version update build",vcsSettingObj);
                            }
                        }
                        buildService.startBuild(session, parseResult, debugPlatform, BuildMode.DEBUG);
                    }else {
                        buildService.startBuild(session, parseResult, debugPlatform, BuildMode.DEBUG);
                    }
                    // buildHistoryObj 추가
                    // DEBUG 모드로 변경
                    // String to Map <key, value> 처리 변경
                }
            } catch (ParseException | NullPointerException e) {
                log.error("builder build error", e);
            }


        }else if(messageType.equals(BuildServiceType.HV_MSG_RELEASE_BUILD.toString()) || messageType.equals(BuildServiceType.HV_MSG_RELEASE_BUILD_FROM_BRANCH.toString())){

            String workspaceName = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String jsonRepository = parseResult.get("ftpSettingObj").toString();
            String jsonBranchObj = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();

            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.buildHistoryVo.name()).toString();
            String appVersionCode = parseResult.get(PayloadMsgType.appVersionCode.name()).toString();
            String appConfigJSONVo = parseResult.get("appConfigJSON").toString();

            try {

                String repositoryObj = "";
                if(parseResult.get(PayloadMsgType.repositoryObj.name()) == null){
                    repositoryObj = "{}";
                }else {
                    repositoryObj = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
                }

                // branch id, name 체크
                Object bObj = branchParser.parse(jsonBranchObj);
                branchObj = (JSONObject) bObj;
                appConfigObj = (JSONObject) parser.parse(appConfigJSONVo);
                log.info("======================== HV_MSG_RELEASE_BUILD_FROM_BRANCH  ======================");

                if(branchObj.get(PayloadMsgType.branchUserId.name()).toString().toLowerCase().equals("") || branchObj.get(PayloadMsgType.branchUserId.name()).toString() == null){
                    return;
                }

                if(branchObj.get(PayloadMsgType.branchName.name()).toString().toLowerCase().equals("") || branchObj.get(PayloadMsgType.branchName.name()).toString() == null){
                    return;
                }

                    obj = parser.parse(jsonRepository);
                    ftpSettingObj = (JSONObject) obj;

                    buildHistoryObj = (JSONObject) buildHistoryParser.parse(jsonBuildHistoryVo);
                    vcsSettingObj = (JSONObject) parser.parse(repositoryObj);

                    if (debugPlatform.toLowerCase().equals(PayloadMsgType.windows.name()) && (projectName != null || projectName.equals(""))) {
                        String projectDir = "";//

                        // template version setting
                        mobileTemplateConfigService.templateConfigCLI(debugPlatform, appVersionCode);

                        // buildHistoryObj 추가
                        buildService.startBuild(session, projectDir, projectName, history_id, debugPlatform, BuildMode.TEST, ftpSettingObj, buildHistoryObj, appVersionCode);
                    }

                    if ((workspaceName != null || workspaceName.equals("")) && (projectName != null || projectName.equals("")) && !debugPlatform.toLowerCase().equals(PayloadMsgType.windows.name())) {

                        // template version setting
                        if(debugPlatform.toLowerCase().equals(PayloadMsgType.android.name())){
                            // version code 변경 작업 진행...
                            // else if svn 일 경우
                            if(vcsSettingObj.get(PayloadMsgType.vcsType.name()) != null){
                                if(vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("git")){

                                }else if(vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("svn")){
//                                    projectSvnCommitService.projectSvnCommitAction(session, debugPlatform, projectName,"","", appVersionCode + "version update build",vcsSettingObj);
                                }
                            }
                            buildService.startBuild(session, parseResult, debugPlatform, BuildMode.RELEASE);
                            // buildService.startBuild(session, parseResult, debugPlatform, BuildMode.RELEASE, ftpSettingObj, buildHistoryObj, vcsSettingObj);
                        } else if(debugPlatform.toLowerCase().equals(PayloadMsgType.ios.name())){
                            buildService.startBuild(session, parseResult, debugPlatform, BuildMode.RELEASE);
                        }
                        // buildHistoryObj 추가
                        // DEBUG 모드로 변경
                        // String to Map <key, value> 처리 변경
                    }
            } catch (ParseException | NullPointerException e) {
                log.error("builder build error", e);
            }

        } else if(messageType.equals(BuildServiceType.HV_MSG_AAB_DEBUG_BUILD.toString())){

            // domain + user + workspace + project
            String domainName = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userName = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspaceName = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String jsonRepository = parseResult.get("ftpSettingObj").toString();
            String jsonBranchObj = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();

            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();
            String appVersionCode = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();
            String appConfigJSONVo = parseResult.get("appConfigJSON").toString();

            try {
                String repositoryObj = "";
                if(parseResult.get(PayloadMsgType.repositoryObj.name()) == null){
                    repositoryObj = "{}";
                }else {
                    repositoryObj = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
                }

                // branch id, name 체크
                Object bObj = branchParser.parse(jsonBranchObj);
                branchObj = (JSONObject) bObj;
                appConfigObj = (JSONObject) parser.parse(appConfigJSONVo);
                log.info("======================== HV_MSG_AAB_DEBUG_BUILD  ======================");

                if(branchObj.get(PayloadMsgType.branchUserId.name()).toString().toLowerCase().equals("") || branchObj.get(PayloadMsgType.branchUserId.name()).toString() == null){
                    return;
                }

                if(branchObj.get(PayloadMsgType.branchName.name()).toString().toLowerCase().equals("") || branchObj.get(PayloadMsgType.branchName.name()).toString() == null){
                    return;
                }

                obj = parser.parse(jsonRepository);
                ftpSettingObj = (JSONObject) obj;

                buildHistoryObj = (JSONObject) buildHistoryParser.parse(jsonBuildHistoryVo);
                vcsSettingObj = (JSONObject) parser.parse(repositoryObj);

                if ((workspaceName != null || workspaceName.equals("")) && (projectName != null || projectName.equals("")) && !debugPlatform.toLowerCase().equals(PayloadMsgType.repositoryVcsType.name())) {

                    String projectDir = parseResult.get("buildProjectdir").toString();//

                    // template version setting
                    if(debugPlatform.toLowerCase().equals(PayloadMsgType.android.name())){
                        String buildToPath = userRootPath + "builder_main/" + domainName + "/" + userName + "/" + workspaceName + "/" + projectName + "/" +projectDir;
                        // version code 변경 작업 진행...
                        mobileTemplateConfigService.templateConfigCLI(debugPlatform, buildToPath, appVersionCode, parseResult);
                        // else if svn 일 경우
                        try {
                            if(vcsSettingObj.get(PayloadMsgType.vcsType.name()) != null){
                                if(vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("git")){
                                    gitTask.gitPush(parseResult, buildToPath, vcsSettingObj, "appConfigSet");
                                }else if(vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("svn")){
                                    //projectSvnCommitService.projectSvnCommitAction(session, debugPlatform, projectName,"","", appVersionCode + "version update build",vcsSettingObj);
                                    String repositoryID = vcsSettingObj.get("repositoryId").toString();
                                    String repositoryPassword = vcsSettingObj.get("repositoryPassword").toString();

                                    svnTask.svnAdd(new URI(buildToPath));
                                    svnTask.svnCommit(repositoryID, repositoryPassword, new URI(buildToPath), "appConfigSet");
                                }
                            }
                        } catch (Exception e) {
                            log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                        }
                    }
                    buildService.startAabBuild(session, parseResult, debugPlatform, BuildMode.AAB_DEBUG, ftpSettingObj, buildHistoryObj, vcsSettingObj, appConfigObj);
                }
            } catch (ParseException | NullPointerException e) {
                log.warn(e.getMessage(), e);
            }

        }else if(messageType.equals(BuildServiceType.HV_MSG_AAB_RELEASE_BUILD.toString())){
            // domain + user + workspace + project
            String domainName = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userName = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspaceName = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String projectName = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String jsonRepository = parseResult.get("ftpSettingObj").toString();
            String jsonBranchObj = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();

            String jsonBuildHistoryVo = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();
            String appVersionCode = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();
            String appConfigJSONVo = parseResult.get("appConfigJSON").toString();

            try {

                String repositoryObj = "";
                if(parseResult.get(PayloadMsgType.repositoryObj.name()) == null){
                    repositoryObj = "{}";
                }else {
                    repositoryObj = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();
                }

                // branch id, name 체크
                Object bObj = branchParser.parse(jsonBranchObj);
                branchObj = (JSONObject) bObj;
                appConfigObj = (JSONObject) parser.parse(appConfigJSONVo);
                log.info("======================== HV_MSG_AAB_RELEASE_BUILD  ======================");

                if(branchObj.get(PayloadMsgType.branchUserId.name()).toString().toLowerCase().equals("") || branchObj.get(PayloadMsgType.branchUserId.name()).toString() == null){
                    return;
                }

                if(branchObj.get(PayloadMsgType.branchName.name()).toString().toLowerCase().equals("") || branchObj.get(PayloadMsgType.branchName.name()).toString() == null){
                    return;
                }

                obj = parser.parse(jsonRepository);
                ftpSettingObj = (JSONObject) obj;

                buildHistoryObj = (JSONObject) buildHistoryParser.parse(jsonBuildHistoryVo);
                vcsSettingObj = (JSONObject) parser.parse(repositoryObj);

                if ((workspaceName != null || workspaceName.equals("")) && (projectName != null || projectName.equals("")) && !debugPlatform.toLowerCase().equals(PayloadMsgType.repositoryVcsType.name())) {

                    String projectDir = parseResult.get("buildProjectdir").toString();//

                    // template version setting
                    if(debugPlatform.toLowerCase().equals(PayloadMsgType.android.name())){
                        String buildToPath = userRootPath + "builder_main/" + domainName + "/" + userName + "/" + workspaceName + "/" + projectName + "/" + projectDir;
                        // version code 변경 작업 진행...
                        mobileTemplateConfigService.templateConfigCLI(debugPlatform, buildToPath, appVersionCode, parseResult);
                        // else if svn 일 경우
                        try {
                            if (vcsSettingObj.get(PayloadMsgType.vcsType.name()) != null) {
                                if (vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("git")) {
                                    gitTask.gitPush(parseResult, buildToPath, vcsSettingObj, "appConfigSet");
                                } else if (vcsSettingObj.get(PayloadMsgType.vcsType.name()).toString().equals("svn")) {
                                    //projectSvnCommitService.projectSvnCommitAction(session, debugPlatform, projectName,"","", appVersionCode + "version update build",vcsSettingObj);
                                    String repositoryID = vcsSettingObj.get("repositoryId").toString();
                                    String repositoryPassword = vcsSettingObj.get("repositoryPassword").toString();

                                    svnTask.svnAdd(new URI(buildToPath));
                                    svnTask.svnCommit(repositoryID, repositoryPassword, new URI(buildToPath), "appConfigSet");
                                }
                            }
                        } catch (Exception e) {
                            log.error("Git Push, SVN Commit Error = {}", e.getLocalizedMessage(), e);
                        }
                    }
                    buildService.startAabBuild(session, parseResult, debugPlatform, BuildMode.AAB_RELEASE, ftpSettingObj, buildHistoryObj, vcsSettingObj, appConfigObj);
                }
            } catch (ParseException | NullPointerException e) {

            }

        }else if(messageType.equals(BuildServiceType.HV_MSG_MULTI_PROFILE_IOS_BUILD.toString())){
            buildService.startiOSMultiProfileBuild(session, parseResult, debugPlatform);
        }
    }

    public Boolean setTimeout(int delayTime){
        long now = System.currentTimeMillis();
        long currentTime = 0;
        while( currentTime - now< delayTime){
            currentTime  = System.currentTimeMillis();
        }
        return true;
    }
}
