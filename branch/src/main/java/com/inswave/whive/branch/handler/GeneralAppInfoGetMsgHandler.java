package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.service.GeneraliOSAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class GeneralAppInfoGetMsgHandler implements BranchHandlable {

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Autowired
    GeneraliOSAppService generaliOSAppService;

    private String systemUserHomePath = System.getProperty("user.home");

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String workspacePath = BuilderDirectoryType.DOMAIN_.name() + parseResult.get(PayloadMsgType.domainID.name()).toString() +
                "/" + BuilderDirectoryType.USER_.name() + parseResult.get(PayloadMsgType.userID.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.name() + parseResult.get(PayloadMsgType.workspaceID.name()).toString() +
                "/" + BuilderDirectoryType.PROJECT_P.name() + parseResult.get(PayloadMsgType.projectID.name()).toString() +
                "/" + BuilderDirectoryType.PROJECT_P.name() + parseResult.get(PayloadMsgType.projectID.name()).toString();


        String userProjectPath = systemUserHomePath + userRootPath + "builder_main/" + workspacePath;

        generaliOSAppService.getGeneralIOSAppInfoCLI(session, userProjectPath, parseResult);
    }
}
