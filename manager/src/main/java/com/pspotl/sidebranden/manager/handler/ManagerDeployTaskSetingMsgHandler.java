package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class ManagerDeployTaskSetingMsgHandler implements Handlable{

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        WebSocketSession headQuaterWebsocketSession = null;
        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO_FROM_HEADQUATER.name())){

            String status = parseResult.get("status").toString();

            if(status.equals("DONE")){
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("build_id").toString()));

                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);

            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);


        }else if(messageType.equals(ClientMessageType.HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO_FROM_HEADQUATER.name())){

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
