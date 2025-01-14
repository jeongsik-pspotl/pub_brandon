package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.CreateProjectService;
import com.pspotl.sidebranden.builder.service.ProjectTemplateCopyService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class ProjectTemplateCreateMsgHandler implements BranchHandlable{

    @Autowired
    CreateProjectService createProjectService;

    @Autowired
    ProjectTemplateCopyService projectTemplateCopyService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        JSONParser parser = new JSONParser();

        JSONObject repositoryObj = null;
        JSONObject signingkeyObj = null;
        JSONObject applyConfigObj = null;
        JSONObject builderSettingObj = null;

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        // workspace dir 생성 cli
        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_CREATE_INFO.name()) ){

            String jsonRepository = parseResult.get("jsonRepository").toString();
            String jsonSigningkey = parseResult.get("jsonSigningkey").toString();
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();
            String jsonBuilderSetting = parseResult.get("jsonBranchSetting").toString();

            try {
                repositoryObj = (JSONObject) parser.parse(jsonRepository);
                signingkeyObj = (JSONObject) parser.parse(jsonSigningkey);
                applyConfigObj = (JSONObject) parser.parse(jsonApplyConfig);
                builderSettingObj = (JSONObject) parser.parse(jsonBuilderSetting);

                // domain + user + workspace
                String workspacePath = BuilderDirectoryType.DOMAIN_.toString()+parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"+ BuilderDirectoryType.USER_.toString()+parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W.toString()+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
                createProjectService.createWorkspaceStartCLI(workspacePath);

                createProjectService.createVcsWorkspaceStartCLI(workspacePath, repositoryObj.get("vcsType").toString());

                projectTemplateCopyService.createTemplateProjectCLI(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj, builderSettingObj);

            } catch (ParseException e) {

                log.error("project template create error", e);
            }

        }else if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_MULTI_PROFILE_TEMPLATE_CREATE_INFO.name())){

            String jsonRepository = parseResult.get("jsonRepository").toString();
            String jsonSigningkey = parseResult.get("jsonSigningkey").toString();
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();
            String jsonBuilderSetting = parseResult.get("jsonBranchSetting").toString();

            try {
                repositoryObj = (JSONObject) parser.parse(jsonRepository);
                signingkeyObj = (JSONObject) parser.parse(jsonSigningkey);
                applyConfigObj = (JSONObject) parser.parse(jsonApplyConfig);
                builderSettingObj = (JSONObject) parser.parse(jsonBuilderSetting);

                // domain + user + workspace
                String workspacePath = BuilderDirectoryType.DOMAIN_.toString()+parseResult.get(PayloadMsgType.domainID.name()).toString() +"/"+ BuilderDirectoryType.USER_.toString()+parseResult.get(PayloadMsgType.userID.name()).toString() +"/"+ BuilderDirectoryType.WORKSPACE_W.toString()+parseResult.get(PayloadMsgType.workspaceID.name()).toString();
                createProjectService.createWorkspaceStartCLI(workspacePath);
                createProjectService.createVcsWorkspaceStartCLI(workspacePath, repositoryObj.get("vcsType").toString());

//                if ("localgit".equals(repositoryObj.get("vcsType").toString())) {
                    projectTemplateCopyService.createMultiProfileTemplateProjectCLI(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj, builderSettingObj);
//                } else if ("localsvn".equals(repositoryObj.get("vcsType").toString())) {

//                }

            } catch (ParseException e) {

                log.error("project template create error", e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
