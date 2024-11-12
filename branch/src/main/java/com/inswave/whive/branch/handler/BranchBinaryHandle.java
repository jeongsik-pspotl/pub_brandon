package com.inswave.whive.branch.handler;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;


public interface BranchBinaryHandle {

    void handleBinary(WebSocketSession session, Map<String, Object> parseResult);

    void handleBinaryPayLoad(WebSocketSession session, String msgType, byte[] payload);
}
