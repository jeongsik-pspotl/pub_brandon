package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProjectImportMessageHandler implements BranchBinaryHandle{

    @Override
    public void handleBinary(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        // 1. 기본 msg type, json type 데이터 전달 받고 있는지 확인...
        log.info("AppleApiRestfulMessageHandler handleBinary messageType : {} ", messageType);
        log.info("AppleApiRestfulMessageHandler handleBinary jsonObj : {} ", parseResult.get("jsonObj"));

        //

    }

    @Override
    public void handleBinaryPayLoad(WebSocketSession session, String msgType, byte[] payload) {

    }

}
