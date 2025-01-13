package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.BuilderDirectoryType;
import com.pspotl.sidebranden.builder.enums.DeployServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.DeployMetadataImageService;
import com.pspotl.sidebranden.builder.service.DeployMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class DeployMetadataMsgHandler implements BranchHandlable{

    @Autowired
    DeployMetadataService deployMetadataService;

    @Autowired
    DeployMetadataImageService deployMetadataImageService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String msgType = parseResult.get(SessionType.MsgType.name()).toString();

        if(msgType.equals(DeployServiceType.HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO.name())){

            String domainDir = parseResult.get(PayloadMsgType.domain_id.name()).toString();
            String userDir = parseResult.get(PayloadMsgType.user_id.name()).toString();
            String workspaceDir = parseResult.get(PayloadMsgType.workspace_id.name()).toString();
            String projectDir = parseResult.get(PayloadMsgType.projectID.name()).toString();
            String projectDirPath = parseResult.get("buildProjectdir").toString();

            String builderPath = "builder_main/"+ BuilderDirectoryType.DOMAIN_ + domainDir + "/" + BuilderDirectoryType.USER_ + userDir +
                    "/" + BuilderDirectoryType.WORKSPACE_W + workspaceDir + "/" + BuilderDirectoryType.PROJECT_P + projectDir+ "/" + BuilderDirectoryType.PROJECT_P + projectDir;


            //TODO : metadata text update 실행 메소드..
            deployMetadataService.setDeployMetaDataTextUpdate(session, parseResult, builderPath);

        }else if(msgType.equals(DeployServiceType.HV_MSG_DEPLOY_METADATA_SEARCH_STATUS_INFO.name())){

            String domainDir = parseResult.get(PayloadMsgType.domain_id.name()).toString();
            String userDir = parseResult.get(PayloadMsgType.user_id.name()).toString();
            String workspaceDir = parseResult.get(PayloadMsgType.workspace_id.name()).toString();
            String projectDir = parseResult.get(PayloadMsgType.projectID.name()).toString();
            String projectDirPath = parseResult.get("buildProjectdir").toString();

            String builderPath = "builder_main/"+ BuilderDirectoryType.DOMAIN_ + domainDir + "/" + BuilderDirectoryType.USER_ + userDir +
                    "/" + BuilderDirectoryType.WORKSPACE_W + workspaceDir + "/" + BuilderDirectoryType.PROJECT_P + projectDir+ "/" + BuilderDirectoryType.PROJECT_P + projectDir;

            deployMetadataImageService.getDeployMetadataImageRead(session, parseResult, builderPath);

        }


    }

}
