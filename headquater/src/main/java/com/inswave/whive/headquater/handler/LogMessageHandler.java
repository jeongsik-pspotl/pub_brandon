package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.headquater.client.ClientHandler;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketBuilderService;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class LogMessageHandler implements Handlable {

    @Autowired
    ClientHandler clientHandler;

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        WHiveIdentity identity = new WHiveIdentity();

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        if(messageType.equals(ClientMessageType.HV_MSG_BRANCH_SESSID_INFO.name())){
            if(sessType.equals("BRANCH")){
                identity.setSessionType(SessionType.BRANCH);
                identity.setSessionId(session.getId());

            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(parseResult.get("userId").toString());
            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, parseResult);
            WebSocketSession webSocketSessionBuilder = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get("userId").toString(), SessionType.BRANCH);

            if(webSocketSessionBuilder != null){
                try {
                    synchronized (webSocketSessionBuilder){
                        webSocketSessionBuilder.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(parseResult)));
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }else if(messageType.equals(ClientMessageType.HV_MSG_HEADQUATER_SESSID_INFO.name())){
            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            WebSocketSession webSocketSessionManager = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
            if(webSocketSessionManager != null){
                try {
                    synchronized (webSocketSessionManager){
                        webSocketSessionManager.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(parseResult)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        }else if(messageType.equals(ClientMessageType.HV_MSG_LOG_INFO.name())){
            log.info(" ======== LogMessageHandler handle messageType =========== : {} ",messageType);
//                Set<WebSocketSession> allSessions = WHiveWebSocketHandler.getAllSession();
//                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

//                for(WebSocketSession webSession : allSessions) {
//                    // send to headquater
//                    webSession.sendMessage(message);
//                }
//                // send to branch
//                clientHandler.sendMessage(parseResult);
        }

    }
}
