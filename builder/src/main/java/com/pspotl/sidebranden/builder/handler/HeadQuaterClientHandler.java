package com.pspotl.sidebranden.builder.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pspotl.sidebranden.builder.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class HeadQuaterClientHandler {

    private String suffixString = "_FROM_HEADQUATER";

    public void sendMessage(Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get("msgType") + suffixString;

        parseResult.put(SessionType.MsgType.name(), messageType);
        WebSocketSession session = BranchSocketHandler.sendServerHeadquaterSession();

        if(session != null) {
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                synchronized(session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {

                log.error("manager send message error", e);
            }
        }
    }

    public void sendMessage(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get("msgType") + suffixString;

        parseResult.put(SessionType.MsgType.name(), messageType);
        WebSocketSession _session = BranchSocketHandler.getSessionByOne(session);

        if(_session != null) {
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                synchronized(_session) {
                    _session.sendMessage(message);
                }
            } catch (IOException e) {

                log.error("manager send message error", e);
            }
        }
    }

    public void sendMessage(ObjectNode parseResult) {
        String messageType = parseResult.get(SessionType.MsgType.name()).textValue() + suffixString;

        parseResult.put(SessionType.MsgType.name(), messageType);
        WebSocketSession session = BranchSocketHandler.sendServerHeadquaterSession();

        if(session != null) {
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                synchronized(session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {

                log.error("manager send message error", e);
            }
        }
    }

    public void sendMessage(BinaryMessage message) {
        WebSocketSession session = BranchSocketHandler.sendServerHeadquaterSession();

        if (session != null) {
            session.setBinaryMessageSizeLimit(67108864);
            try {
                synchronized(session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                log.error("BusClientHandler.sendMessage(BinaryMessage) error : ", e);
            }
        }
    }

    public void sendMessage(WebSocketSession session, BinaryMessage message) {
        WebSocketSession _session = BranchSocketHandler.getSessionByOne(session);

        if (session != null) {
            _session.setBinaryMessageSizeLimit(671088640);
            try {
                synchronized(_session) {
                    _session.sendMessage(message);
                }
            } catch (IOException e) {
                log.error("BusClientHandler.sendMessage(BinaryMessage) error : ", e);
            }
        }
    }
}
