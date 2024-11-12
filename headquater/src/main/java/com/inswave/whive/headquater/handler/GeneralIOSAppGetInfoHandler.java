package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class GeneralIOSAppGetInfoHandler implements Handlable {
    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    BuildProjectService buildProjectService;
    WebSocketSession headQuaterWebsocketSession = null;
    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        try {
            TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
            String statusType = parseResult.get("message").toString();

            if (statusType.equals("SUCCESSFUL") || statusType.equals("DONE")) {
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("projectID").toString()));

                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);
            }


            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        } catch (Exception e) {
            log.error("General iOS APP Get Info Handler Error = {}", e.getMessage(), e);
        }
    }
}
