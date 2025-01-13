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
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class ManagerMultiProfileMsgHandler implements Handlable{

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        WebSocketSession headQuaterWebsocketSession = null;
        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO_FROM_HEADQUATER.name())){

            TextMessage message = null;

            String status = parseResult.get("message").toString();

            if(status.equals("SUCCESSFUL")){
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("build_id").toString()));

                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);

            }

//                headQuaterWebsocketSession = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
//                if(headQuaterWebsocketSession != null){
//                    headQuaterWebsocketSession.sendMessage(message);
//                }

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                log.info(wHiveIdentity.toString());
                Map<String, WHiveIdentity> allList  =  ClusterWebSocketService.getAllIdentities();
                log.info(allList.toString());
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        }else if(messageType.equals(ClientMessageType.HV_MSG_BUILD_SET_ACTIVE_PROFILE_INFO_FROM_HEADQUATER.name())){
            TextMessage message = null;
            try {

                message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

//                headQuaterWebsocketSession = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
//                if(headQuaterWebsocketSession != null){
//                    headQuaterWebsocketSession.sendMessage(message);
//                }
//                WHiveIdentity wHiveIdentity = WHiveWebSocketHandler.getIdentityByUserId(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }

            } catch (IOException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        }else if(messageType.equals(ClientMessageType.HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO_FROM_HEADQUATER.name())){


            TextMessage message = null;

//            WHiveIdentity wHiveIdentity = WHiveWebSocketHandler.getIdentityByUserId(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                // headQuaterWebsocketSession.sendMessage(message);
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }


        }else if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO_FROM_HEADQUATER.name())){

            TextMessage message = null;

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        }else if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO_FROM_HEADQUATER.name())){

            TextMessage message = null;

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        }

    }



}
