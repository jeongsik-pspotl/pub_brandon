package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BuilderAppIconStatusHandler  implements Handlable {

    WebSocketSession headQuaterWebsocketSession = null;
    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        if(messageType.equals(ClientMessageType.BIN_FILE_APP_ICON_APPEND_SEND_INFO_FROM_HEADQUATER.name())){

            TextMessage message = null;
            try {
                message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

            } catch (JsonProcessingException e) {

                log.warn(e.getMessage(),e);
            }

            log.info(" ======== AppIconImageLoadHandler Manager WebsocketSession =========== : {} ",headQuaterWebsocketSession);
            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                //                    headQuaterWebsocketSession.sendMessage(message);
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        }

    }

}
