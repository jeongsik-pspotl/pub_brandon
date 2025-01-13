package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserLoginSessionHandler implements Handlable {

    WebSocketSession exitSession;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());
        String userId = (String) parseResult.get("userId");
        String name = (String) parseResult.get("name");
        String member_role = (String) parseResult.get("member_role");

        WHiveIdentity identity = new WHiveIdentity();

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;
        if(name == null || name.equals("")) return;
        if(member_role == null || member_role.equals("")) return;
        if(userId == null || userId.equals("")) return;

        if(messageType.equals(ClientMessageType.HV_MSG_USER_LOGIN_SESSION_INFO.name())){

            // 기존 세션 로그인 체크
            WHiveIdentity oldWHiveIdentity = ClusterWebSocketService.getIdentityManager(userId);

            // 강제 로그 아웃 요청 기능
            if(oldWHiveIdentity != null && oldWHiveIdentity.getUserId() != null){
                log.info("old WHive into.. ");
                Map<String, WHiveIdentity> allList  = ClusterWebSocketService.getAllIdentities();
                log.info(allList.toString());

//                if(exitSession != null){
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("MsgType",ClientMessageType.HV_MSG_FORCE_LOGOUT);

                    ClusterWebSocketService.sendMessage(oldWHiveIdentity, msg);
                     ClusterWebSocketService.remove(oldWHiveIdentity);
            }else {


            }


            if(sessType.equals("HEADQUATER")){
                log.info("HV_MSG_USER_LOGIN_SESSION_INFO HEADQUATER");
                identity.setSessionType(SessionType.HEADQUATER);
                identity.setSessionId(session.getId());
                identity.setName(name);
                identity.setPosition(member_role);
                identity.setUserId(userId);
            }

            // HV_MSG_FORCE_LOGOUT 메시지  타입 추가

            WHiveWebSocketHandler.addIdentityToSession(session, identity);
            ClusterWebSocketService.put(identity);

        }

    }


}
