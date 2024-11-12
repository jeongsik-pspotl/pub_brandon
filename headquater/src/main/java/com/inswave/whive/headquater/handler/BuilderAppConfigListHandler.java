package com.inswave.whive.headquater.handler;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BuilderAppConfigListHandler implements Handlable {

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    BuildProjectService buildProjectService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO_FROM_HEADQUATER.name()) || messageType.equals(ClientMessageType.HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO_FROM_HEADQUATER.name())){

            TextMessage message = null;

            //TODO : builder 큐 관리 상태 값 업데이트 -1 적용

            String statusType = parseResult.get("message").toString();

            if(statusType.equals("SUCCESSFUL")){
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("build_id").toString()));

                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);
            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }


        }

    }
}
