package com.pspotl.sidebranden.manager.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class WindowsConfigListHandler implements Handlable {

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        if(messageType.equals(ClientMessageType.HV_MSG_WINDOWS_CONFIG_LIST_INFO.name())){

            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();
            TextMessage message = null;
            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    log.info(String.valueOf(message));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                    log.warn(e.getMessage(),e);
                } catch (IOException e) {
                    log.warn(e.getMessage(),e);
                }

            }

        }else if(messageType.equals(ClientMessageType.HV_MSG_WINDOWS_CONFIG_LIST_INFO_FROM_HEADQUATER.name())){
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                headQuaterWebsocketSession = WHiveWebSocketHandler.getServerHeadquaterSession();
                if(headQuaterWebsocketSession != null){
                    headQuaterWebsocketSession.sendMessage(message);
                }
            } catch (IOException e) {
                log.warn(e.getMessage(),e);
            }
        }

    }

}
