package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BranchSigingKeyAddHandler implements Handlable {

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());
        String platform = (String) parseResult.get(PayloadKeyType.platform.name());

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;
        if(platform == null || platform.equals("")) return;

        if(messageType.equals(ClientMessageType.MV_MSG_SIGNIN_KEY_ADD_INFO.name())){
            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();
            TextMessage message = null;

            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                     
                    log.error("builder signingkey add error", e);
                } catch (IOException e) {
                     
                    log.error("builder signingkey add error", e);
                }

            }

        }

        if(messageType.equals(ClientMessageType.MV_MSG_SIGNIN_KEY_ADD_INFO_FROM_HEADQUATER.name())){
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                headQuaterWebsocketSession = WHiveWebSocketHandler.getServerHeadquaterSession();
                if(headQuaterWebsocketSession != null){
                    headQuaterWebsocketSession.sendMessage(message);
                }
            } catch (IOException e) {
                 
                log.error("builder signingkey add send manager error", e);
            }

        }

    }

}
