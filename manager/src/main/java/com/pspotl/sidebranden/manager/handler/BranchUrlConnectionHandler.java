package com.pspotl.sidebranden.manager.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pspotl.sidebranden.manager.domian.HeadquaterConnectURLMessage;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BranchUrlConnectionHandler implements Handlable {

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;
    TextMessage message = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.HV_MSG_CONNETCION_CHECK_INFO.name())){
            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();

            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                } catch (IOException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

            }else{

                ObjectMapper Mapper = new ObjectMapper();
                HeadquaterConnectURLMessage headquaterConnectURLMessage = new HeadquaterConnectURLMessage();

                headquaterConnectURLMessage.setMsgType(ClientMessageType.HV_MSG_CONNETCION_CHECK_INFO.name());
                headquaterConnectURLMessage.setSessType(SessionType.HEADQUATER.toString());
                headquaterConnectURLMessage.setMessage("FAILD");
                Map<String, Object> resultMsg = Mapper.convertValue(headquaterConnectURLMessage, Map.class);
                try {
                    message = new TextMessage(new ObjectMapper().writeValueAsString(resultMsg));
                    headQuaterWebsocketSession = WHiveWebSocketHandler.getServerHeadquaterSession();

                    if(headQuaterWebsocketSession != null){
                        headQuaterWebsocketSession.sendMessage(message);
                    }
                } catch (IOException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

            }

        }

        if(messageType.equals(ClientMessageType.HV_MSG_CONNETCION_CHECK_INFO_FROM_HEADQUATER.name())){
            try {
                message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                headQuaterWebsocketSession = WHiveWebSocketHandler.getServerHeadquaterSession();
                if(headQuaterWebsocketSession != null){
                    headQuaterWebsocketSession.sendMessage(message);
                }
            } catch (IOException e) {
                 
                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        }
    }

}
