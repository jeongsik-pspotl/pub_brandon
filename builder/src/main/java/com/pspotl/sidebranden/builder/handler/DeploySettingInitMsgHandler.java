package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.DeployServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.DeploySettingInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class DeploySettingInitMsgHandler implements BranchHandlable{

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String msgType = parseResult.get(SessionType.MsgType.name()).toString();

        if(msgType.equals(DeployServiceType.HV_MSG_DEPLOY_SETTING_INIT_INFO.name())){

            //
            String domainDir = parseResult.get(PayloadMsgType.domain_id.name()).toString();
            String userDir = parseResult.get(PayloadMsgType.user_id.name()).toString();
            String workspaceDir = parseResult.get(PayloadMsgType.workspace_id.name()).toString();
            String projectDir = parseResult.get(PayloadMsgType.projectID.name()).toString();
            String projectDirPath = parseResult.get("buildProjectdir").toString();

            String builderPath = BuilderDirectoryType.DOMAIN_ + domainDir + "/" + BuilderDirectoryType.USER_ + userDir +
                    "/" + BuilderDirectoryType.WORKSPACE_W + workspaceDir + "/" + BuilderDirectoryType.PROJECT_P + projectDir;
            // deploy setting init 설정
            deploySettingInitService.deploySettingInit(session, parseResult, builderPath);

        }

    }

}
