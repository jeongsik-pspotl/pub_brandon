package com.pspotl.sidebranden.manager.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BranchPluginAddHandler implements Handlable   {

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    BuildProjectService buildProjectService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        if(messageType.equals(ClientMessageType.HV_MSG_PLUGIN_ADD_LIST_INFO.name())){
            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();
            TextMessage message = null;

            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                     
                    log.error("manager send plugin add list error", e);
                } catch (IOException e) {
                     
                    log.error("manager send plugin add list error", e);
                }

            }

        }

        // headquater 보내는 msgType
        if(messageType.equals(ClientMessageType.HV_MSG_PLUGIN_ADD_LIST_INFO_FROM_HEADQUATER.name())){

            //TODO : builder 큐 관리 상태 값 업데이트 -1 적용
            // TODO : status, build_id 값 bypass 처리 해야함...

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
