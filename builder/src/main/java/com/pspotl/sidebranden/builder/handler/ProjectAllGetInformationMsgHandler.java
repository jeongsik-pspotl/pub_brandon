package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.MobileTemplateConfigService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class ProjectAllGetInformationMsgHandler implements BranchHandlable{

    @Autowired
    MobileTemplateConfigService mobileTemplateConfigService;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String userProjectPath = "";

        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO.name())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String project_dir_path = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();

            userProjectPath = userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name; // path

            // XCodeProjectName 조회
            String XCodeProjectNameFullName =  mobileTemplateConfigService.getiOSProjectPath(session, parseResult, platform, systemUserHomePath + userProjectPath, project_dir_path);
            XCodeProjectNameFullName = XCodeProjectNameFullName.replace(".xcodeproj","");
            parseResult.put("XCodeProjectName",XCodeProjectNameFullName);

            mobileTemplateConfigService.getiOSGetInformationList(session, parseResult, platform, userProjectPath, project_dir_path);

        }else if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO.name())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String domainPath = BuilderDirectoryType.DOMAIN_ + parseResult.get(PayloadMsgType.domainID.name()).toString();
            String userPath = BuilderDirectoryType.USER_ + parseResult.get(PayloadMsgType.userID.name()).toString();
            String workspace_name = BuilderDirectoryType.WORKSPACE_W + parseResult.get(PayloadMsgType.workspaceID.name()).toString();
            String project_name = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();
            String project_dir_path = BuilderDirectoryType.PROJECT_P + parseResult.get(PayloadMsgType.projectID.name()).toString();

            userProjectPath = userRootPath + "builder_main/" + domainPath + "/" + userPath + "/" + workspace_name + "/" + project_name; // path

            mobileTemplateConfigService.getAndroidGetConfigList(session, parseResult, platform, userProjectPath, project_dir_path);

        }

    }
}
