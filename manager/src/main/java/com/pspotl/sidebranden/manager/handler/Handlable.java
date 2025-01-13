package com.pspotl.sidebranden.manager.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.manager.domian.BaseMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

public interface Handlable {

    ObjectMapper mapper = new ObjectMapper();

    void handle(WebSocketSession session, Map<String, Object> parseResult);

    default TextMessage getTextMessage(BaseMessage message) {

        try {
            return new TextMessage(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            return new TextMessage("{\"result\": \"ERROR\", \"resultMessage\": " + e.getMessage() + "\"}");
        }
    }

    default void sendResponse(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            synchronized(session) {
                session.sendMessage(message);
            }
        } catch (IOException e) {

        }
    }

}
