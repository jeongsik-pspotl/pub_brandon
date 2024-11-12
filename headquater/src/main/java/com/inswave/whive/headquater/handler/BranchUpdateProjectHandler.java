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
public class BranchUpdateProjectHandler  implements Handlable {

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());
        String hqKey = parseResult.get(PayloadKeyType.hqKey.name()).toString();

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;


        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO_FROM_HEADQUATER.name())){
            try {
                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }
            } catch (NullPointerException e ) {
                 
                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }


    }
}
