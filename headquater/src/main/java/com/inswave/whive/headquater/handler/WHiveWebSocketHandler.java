package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.branchsetting.BranchSettingService;
import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.member.MemberService;
import com.inswave.whive.common.settings.AllBranchSettings;
import com.inswave.whive.common.settings.AllBranchSettingsService;
import com.inswave.whive.headquater.client.ClientHandler;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.EOFException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WHiveWebSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    private HandlerManager requestHandler;

    @Autowired
    BranchSettingService branchSettingService;

    @Autowired
    MemberService memberService;

    @Autowired
    ClientHandler clientHandler;

    private static Map<WebSocketSession, WHiveIdentity> websocketSessions = new ConcurrentHashMap<WebSocketSession, WHiveIdentity>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        requestHandler.handleTextRequest(session, message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        if ( message.getPayloadLength() > 0 ) {
            requestHandler.handleBinaryRequest(session, message);
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info(String.format("[ WHiveWebSocketHandler ] [ PongMessage ] [ message ] : %s", message.getPayload()));
        log.info(String.format("[ WHiveWebSocketHandler ] [ WebSocketSession ] [ session ] : %s", session.getId()));

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        /// create identity
        WHiveWebSocketHandler.addIdentityToSession(session, new WHiveIdentity());

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        /// get identity
        WHiveIdentity identity = websocketSessions.get(session);
        ClusterWebSocketService.remove(identity);
        BranchSetting branchSetting;

        if(identity != null) {
            if (identity.getUserId() != null && identity.getSessionType().equals(SessionType.BRANCH)) {
                // all Branch Setting Service 호출
                try {

                    // settings table db update
                    // builder setting update 처리 기능 보완 하기
                    branchSetting = branchSettingService.findByUserID(identity.getUserId());

                    if (branchSetting != null) {
                        branchSetting.setSession_status("N");
                        branchSettingService.updateByStatus(branchSetting);

                    }
                    websocketSessions.remove(session);
                    session.close();

                    log.info("afterConnectionClosed (" + session.getId() + ") : " + String.format("[ connections ] : %d, %s", websocketSessions.size(), closeStatus.toString()));

                    return;
                } catch (Exception e) {
                    
                    log.error("manager afterConnectionClosed error",e);
                }
            }else if(identity.getUserId() != null && identity.getSessionType().equals(SessionType.HEADQUATER)){

                // email member 테이블 조회
                MemberLogin memberLogin =  memberService.findByUserLoginID(identity.getUserId());
                memberService.updateLogout(memberLogin.getUser_id());

                websocketSessions.remove(session);
                session.close();

                log.info("afterConnectionClosed (" + session.getId() + ") : " + String.format("[ connections ] : %d, %s", websocketSessions.size(), closeStatus.toString()));

                return;

            }
        }


        // prepare message
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode msg = mapper.createObjectNode();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DISCONNECT.name()); // 수정 필요 msgType
        msg.put("websocketState", "close"); // 수정 필요

        TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(msg));

        /// send message to admins ?? 송신 기준은 headquater
        Set<WebSocketSession> adminSessions = getAllSession();
        for(WebSocketSession adminSession : adminSessions) {

            if ( adminSession != null && adminSession.isOpen()) {
                adminSession.sendMessage(message);

            }

        }

        /// delete session
        log.info("afterConnectionClosed ("+session.getId()+") : "+String.format("[ connections ] : %d, %s", websocketSessions.size(), closeStatus.toString()));
        try {

            websocketSessions.remove(session);
            session.close();

        } catch ( Exception ex ) {

            log.error("manager afterConnectionClosed error",ex);
        }finally {

        }

    }

    public static void addIdentityToSession(WebSocketSession session, WHiveIdentity identity) {
        WHiveIdentity oldWhiveidentity = websocketSessions.put(session, identity);
        log.info("connections websocketSessions {} : ",websocketSessions);

    }

    public static WebSocketSession getSessionByIdentity(String userId, SessionType st) {
        WebSocketSession session = null;

        for(WebSocketSession _session : websocketSessions.keySet()) {
            WHiveIdentity identity = websocketSessions.get(_session);
            if(identity == null || identity.getUserId() == null) {
                continue;
            }

            if(identity.getUserId().equals(userId) && identity.getSessionType().equals(st)) {
                session = _session;
            }
        }

        return session;
    }

    public static WHiveIdentity getIdentityByUserId(String userId, SessionType st) {
        for(WebSocketSession _session : websocketSessions.keySet()) {
            WHiveIdentity identity = websocketSessions.get(_session);
            if(identity == null || identity.getUserId() == null) {
                continue;
            }

            if(identity.getUserId().equals(userId) && identity.getSessionType() == st) {
                return identity;
            }
        }

        return null;
    }

    public static WHiveIdentity getIdentityBySession(WebSocketSession session) {
        WHiveIdentity identity = websocketSessions.get(session);
        return identity;
    }

    // session Branch 관리 및 out 처리
    public static WebSocketSession getLocalBranchSession() {
        WebSocketSession session = null;

        for(WebSocketSession _session : websocketSessions.keySet()) {
            WHiveIdentity identity = websocketSessions.get(_session);
                  if(identity == null ) {
                continue;

            } else {
                if(identity.getSessionType() != null && identity.getSessionType().equals(SessionType.BRANCH)) {
                    session = _session;

                }
            }
        }

        return session;
    }

    //sessionType, sessionId 분기처리 session 도출
    public static WebSocketSession getIdentityBySessionIdAndSessionType(WHiveIdentity oneIdentity){
        WebSocketSession session = null;

        for(WebSocketSession _session : websocketSessions.keySet()) {
            WHiveIdentity identity = websocketSessions.get(_session);
            if(identity == null || identity.getUserId() == null) {
                continue;
            }

           try {

                if(identity.getSessionType().equals(oneIdentity.getSessionType()) &&
                        identity.getSessionId().equals(oneIdentity.getSessionId())) {
                    session = _session;

                }

           }catch(NullPointerException e){
                   
               log.error("manager getIdentityBySessionIdAndSessionType error",e);
           }
        }

        return session;

    }

    public static WebSocketSession getServerHeadquaterSession() {
        WebSocketSession session = null;
        for(WebSocketSession _session : websocketSessions.keySet()) {
            WHiveIdentity identity = websocketSessions.get(_session);

            if(identity == null) {
                continue;
            }

            if(identity.getSessionType().equals(SessionType.HEADQUATER)) {
                session = _session;

            }
        }

        return session;
    }

    public static Set<WebSocketSession> getAllSession(){
        Set<WebSocketSession> sessions = new HashSet<>();

        Set<WebSocketSession> allSessions = websocketSessions.keySet();
        for(WebSocketSession _session : allSessions) {
            WHiveIdentity identity = websocketSessions.get(_session);
            if(identity == null) {
                continue;
            }

            sessions.add(_session);
        }

        return sessions;

    }

    public static void closeSession(WebSocketSession session) {
        websocketSessions.remove(session);

        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch( Exception e ) {
            log.error("[ closeSession ] Exception : ", e );
        }
    }

}
