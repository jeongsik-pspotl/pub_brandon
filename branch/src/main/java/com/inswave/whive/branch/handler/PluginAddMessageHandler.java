package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.domain.PluginMode;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.SessionType;
import com.inswave.whive.branch.service.PluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class PluginAddMessageHandler implements BranchHandlable {

    @Autowired
    PluginService pluginService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        // HV_MSG_PLUGIN_ADD_LIST_INFO
        if(messageType.equals(BuildServiceType.HV_MSG_PLUGIN_ADD_LIST_INFO.name())){
            pluginService.startPluginAdd(session, parseResult, PluginMode.PLUGIN_ADD);
        }

    }

}
