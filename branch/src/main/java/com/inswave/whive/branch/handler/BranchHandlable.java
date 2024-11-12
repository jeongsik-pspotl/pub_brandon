package com.inswave.whive.branch.handler;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public interface BranchHandlable {

    void handle(WebSocketSession session, Map<String, Object> parseResult);

}
