package com.inswave.whive.headquater.handler;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.branchsetting.BranchSettingService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketBuilderService;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class BranchSessionHandler implements Handlable {

    @Value("${maxBinaryBufferSize}")
    private Integer MAX_BINARY_MESSAGE_BUFFER_SIZE;

    @Value("${defaultBinaryBufferSize}")
    private Integer MAX_TEXT_BUFFER_SIZE;

    @Autowired
    BranchSettingService branchSettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    BranchSetting branchSetting;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());
        String name = (String) parseResult.get("name");
        String position = (String) parseResult.get("position");
        String userId = (String) parseResult.get("userId");

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;
        if(name == null || name.equals("")) return;
        if(position == null || position.equals("")) return;
        if(userId == null || userId.equals("")) return;

        // identity setting 추가
        WHiveIdentity identity = new WHiveIdentity();

        if(messageType.equals(ClientMessageType.HV_MSG_BRANCH_WEBSOCKET_CONNECTION_INFO.name())) {
            identity.setSessionType(SessionType.BRANCH);
            identity.setSessionId(session.getId());
            identity.setName(name);
            identity.setPosition(position);
            identity.setUserId(userId);

            // all_branch_settings table select 추가
            branchSetting = branchSettingService.findByUserID(userId);

            if(branchSetting != null){

                BuilderQueueManaged builderQueueManaged = new BuilderQueueManaged();

                builderQueueManaged.setBuilder_id(branchSetting.getBuilder_id());
                builderQueueManaged.setQueue_etc_1(String.valueOf(branchSetting.getBuilder_user_id()));

                branchSetting.setSession_status("Y");
                branchSettingService.updateByStatus(branchSetting);
                builderQueueManagedService.clusterIdUpdate(builderQueueManaged);

            }


            session.setTextMessageSizeLimit(MAX_TEXT_BUFFER_SIZE);
            session.setBinaryMessageSizeLimit(MAX_BINARY_MESSAGE_BUFFER_SIZE);

            log.info(identity.toString());
            WHiveWebSocketHandler.addIdentityToSession(session, identity);
            ClusterWebSocketBuilderService.put(identity);
        }

    }
}
