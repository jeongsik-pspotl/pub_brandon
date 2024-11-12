package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.enums.SessionType;
import com.inswave.whive.branch.service.MobileTemplateConfigService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class ProjectiOSAppConfigInfoMsgHandler implements BranchHandlable{

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;


    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String userProjectPath = "";

        JSONObject configJSON = new JSONObject();

        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO.name())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String project_dir_path = parseResult.get("projectDirPath").toString();

            userProjectPath = userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name; // path

            mobileTemplateConfigService.getiOSAppConfigAndProjectName(session, parseResult, platform, userProjectPath, project_dir_path);

        }

    }
}
