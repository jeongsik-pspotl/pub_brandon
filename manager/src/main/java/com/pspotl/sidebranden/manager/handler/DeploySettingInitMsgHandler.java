package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class DeploySettingInitMsgHandler implements Handlable {

    WebSocketSession headQuaterWebsocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        //HEADQUATER
        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        log.info(" ======== BuildStatusInfoHandler session =========== : {} ",session);

        // headquater 보내는 msgType
        if(messageType.equals(ClientMessageType.HV_MSG_DEPLOY_SETTING_STATUS_INFO_FROM_HEADQUATER.name())){

            log.info(parseResult.toString());
            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);

            }


        }

    }

}
