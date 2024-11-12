package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.service.CreateProjectService;
import com.inswave.whive.branch.service.ProjectGeneralAppCopyService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class GeneralAppProjectCreateMsgHandler implements BranchHandlable {

    @Autowired
    CreateProjectService createProjectService;

    @Autowired
    ProjectGeneralAppCopyService projectGeneralAppCopyService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        JSONParser parser = new JSONParser();

        try {
            JSONObject repositoryObj = (JSONObject) parser.parse(parseResult.get("jsonRepository").toString());
            JSONObject signingkeyObj = (JSONObject) parser.parse(parseResult.get("jsonSigningkey").toString());
            JSONObject applyConfigObj = (JSONObject) parser.parse(parseResult.get("jsonApplyConfig").toString());
            JSONObject builderSettingObj = (JSONObject) parser.parse(parseResult.get("jsonBranchSetting").toString());

            // domain + user + workspace
            String workspacePath = BuilderDirectoryType.DOMAIN_.name() + parseResult.get(PayloadMsgType.domainID.name()).toString() +
                    "/" + BuilderDirectoryType.USER_.name() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                    "/" + BuilderDirectoryType.WORKSPACE_W.name() + parseResult.get(PayloadMsgType.workspaceID.name()).toString();

            // DOMAIN/USER/WORKSPACE 폴더 생성
            createProjectService.createWorkspaceStartCLI(workspacePath);
            createProjectService.createVcsWorkspaceStartCLI(workspacePath, repositoryObj.get("vcsType").toString());
            projectGeneralAppCopyService.createGeneralAppProject(session, parseResult, repositoryObj, signingkeyObj, applyConfigObj, builderSettingObj);
        } catch (Exception e) {
            log.error("General App Project Create Error = {}", e.getMessage(), e);
        }
    }
}
