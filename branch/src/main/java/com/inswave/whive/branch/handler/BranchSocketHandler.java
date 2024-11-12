package com.inswave.whive.branch.handler;

/*
* BranchSocketHandler Handler
* Branch 웹소켓 메인 핸들러
* HeadQuater 웹 소켓 서비스에서 메시지를 주고 받을 때 사용되는 객체
*
*
*
*
 */


import com.inswave.whive.branch.domain.HeadquarterIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class BranchSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    private BuildServiceManager buildServiceManager;


    private static WebSocketSession webSocketSession;

    private static Map<WebSocketSession, HeadquarterIdentity> websocketSessions = new ConcurrentHashMap<WebSocketSession, HeadquarterIdentity>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // text type payload data call
        buildServiceManager.handleTextRequest(session, message);

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // log.info(String.format("[ Binary message ] [ text ] : %s", message.getPayload()));

        buildServiceManager.handleBinaryRequest(session, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected");
        String payload = "{\"MsgType\": \"HV_MSG_LOG_INFO\",\"name\": \"ticker\",\"message\":\"test.. message call\"}";


        addIdentityToSession(session, new HeadquarterIdentity());
        // webSocketSession = session;
        if(session != null) {
            try {
                synchronized(session) {
                    session.sendMessage(new TextMessage(payload));

                }
            } catch (IOException e) {
                 
                log.error("builder send message error", e);
            }
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport Error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("Connection Closed [" + status + "]");
        log.info("Connection Closed [" + status.getReason() + "]");

        try {
            websocketSessions.remove(session);
            session.close();
         //  branchClient.connect();

        } catch ( Exception ex ) {

            // log.error("builder send message error", ex);
        }finally {

        }



    }

    public static WebSocketSession getSessionByOne(WebSocketSession session) {

        for(WebSocketSession _session : websocketSessions.keySet()) {
            if(_session == session){

                HeadquarterIdentity identity = websocketSessions.get(_session);
                if(identity == null ) {
                    continue;
                }else {
                    session = _session;
                }

            }

        }

        return session;

    }

    public static void addIdentityToSession(WebSocketSession session, HeadquarterIdentity identity) {
        websocketSessions.put(session, identity);
        // log.info(identity.toString());
        // log.info(String.format("[ connections ] : %d [ identity ] : %s", websocketSessions.size(), identity.toString()));
        log.info("connections websocketSessions {} : ",websocketSessions);

    }

    // 처음 기동할시, session 호출 method.
    public static WebSocketSession sendServerHeadquaterSession() { return webSocketSession; }

}
