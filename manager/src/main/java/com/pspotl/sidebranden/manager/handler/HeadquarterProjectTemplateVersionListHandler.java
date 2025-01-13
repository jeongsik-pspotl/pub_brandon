package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class HeadquarterProjectTemplateVersionListHandler implements Handlable {

    WebSocketSession headQuaterWebsocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());


        // headquater 보내는 msgType
        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_TEMPLATE_LIST_INFO_FROM_HEADQUATER.name())){

            log.info(parseResult.toString());
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }

            } catch (IOException e) {

                log.error("manager project template list send message error",e);
            }

        }else {
            log.info(" ======== BranchHandler handle messageType =========== : {} ",messageType);

        }

    }

}
