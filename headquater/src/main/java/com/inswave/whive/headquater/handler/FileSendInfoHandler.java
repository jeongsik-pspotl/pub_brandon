package com.inswave.whive.headquater.handler;

import com.inswave.whive.common.buildhistory.BuildHistory;
import com.inswave.whive.headquater.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class FileSendInfoHandler implements Handlable {

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType == null || messageType.equals("")) return;
        log.info(" ======== LogMessageHandler handle messageType =========== : {} ",messageType);

    }
}
