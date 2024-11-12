package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inswave.whive.common.buildhistory.BuildHistory;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class AppFileDownloadHandler implements Handlable{

    WebSocketSession webSocketSession = null;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        if(messageType.equals(ClientMessageType.HV_MSG_APPFILE_DOWNLOAD_INFO.name())){

            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();
            TextMessage message = null;

            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                } catch (IOException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

            }

        }

    }

}
