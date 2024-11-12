package com.inswave.whive.headquater.handler;

import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BuilderServerConfigListHandler implements Handlable {


    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        WebSocketSession headQuaterWebsocketSession = null;
        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO_FROM_HEADQUATER.name()) || messageType.equals(ClientMessageType.HV_MSG_PROJECT_SERVER_CONFIG_UPDATE_STATUS_INFO_FROM_HEADQUATER.name())){

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }


        }

    }

}
