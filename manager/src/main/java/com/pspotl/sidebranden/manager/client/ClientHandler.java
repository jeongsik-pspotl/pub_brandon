package com.pspotl.sidebranden.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.handler.WHiveWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class ClientHandler {

    @Value("${maxBinaryBufferSize}")
    private Integer MAX_BINARY_MESSAGE_BUFFER_SIZE;

    private String suffixString = "_FROM_BRANCH";

    public void sendMessage(Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name()) + suffixString;

        parseResult.put(SessionType.MsgType.name(), messageType);
        WebSocketSession session = WHiveWebSocketHandler.getLocalBranchSession();
        log.info(" | ClientHandler | sendMessage | session : {} ", session);
        if(session != null) {
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                synchronized(session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public void sendMessage(ObjectNode parseResult) {
        String messageType = parseResult.get(SessionType.MsgType.name()).textValue() + suffixString;

        parseResult.put(SessionType.MsgType.name(), messageType);
        WebSocketSession session = WHiveWebSocketHandler.getLocalBranchSession();

        if(session != null) {
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                synchronized(session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    // BinaryMessage
    public void sendMessage(BinaryMessage message) {

        log.info(" | ClientHandler | BinaryMessage | sendMessage : {} ", message);
        WebSocketSession session = WHiveWebSocketHandler.getLocalBranchSession();
        log.info(" | ClientHandler | BinaryMessage | WebSocketSession : {} ",session);
        if(session != null) {
            session.setBinaryMessageSizeLimit(MAX_BINARY_MESSAGE_BUFFER_SIZE);
            try {
                synchronized(session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                 log.warn(e.getMessage(), e);
            }
        }
    }

}
