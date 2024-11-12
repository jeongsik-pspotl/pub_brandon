package com.inswave.whive.headquater.handler;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class ManagerDeployMetadataSettingMsgHandler implements Handlable{

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        WebSocketSession headQuaterWebsocketSession = null;
        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO_FROM_HEADQUATER.name())){

            String status = parseResult.get("status").toString();

            if(status.equals("DONE")){
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("build_id").toString()));

                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);

            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);


        }else if(messageType.equals(ClientMessageType.HV_MSG_DEPLOY_METADATA_IMAGE_STATUS_INFO_FROM_HEADQUATER.name())){

            String status = parseResult.get("status").toString();

            if(status.equals("DONE")){
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("build_id").toString()));

                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);

            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);

        }

    }

}
