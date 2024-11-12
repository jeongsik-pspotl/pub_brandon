package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class DisconnectHandler implements Handlable {

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        try {
            String msgType = (String) parseResult.get(SessionType.MsgType.name());

            // 기능 테스트 해보고 고민해보기
            if(msgType.equals(ClientMessageType.HV_MSG_DISCONNECT_FROM_BRANCH.toString()) || msgType.equals(ClientMessageType.HV_MSG_DISCONNECT.toString())) {
                TextMessage message = null;
                try {
                    message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                } catch (JsonProcessingException e) {

                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

                // 수정 해야함.
                Set<WebSocketSession> allSessions = WHiveWebSocketHandler.getAllSession();
                for(WebSocketSession allSession : allSessions) {
                    if ( allSession != null ) {
                        if ( allSession.isOpen() ) {
                            allSession.sendMessage(message);
                        }
                    }
                }
            }
        } catch (IOException e) {
            //log.error("DisconnectHandler.handle() error : ", e);
        }
    }
}
