package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.PluginMode;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.PluginRemoveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class PluginRemoveMessageHandler  implements BranchHandlable{

    @Autowired
    PluginRemoveService pluginRemoveService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        // HV_MSG_PLUGIN_REMOVE_LIST_INFO
        if(messageType.equals(BuildServiceType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name())){
            pluginRemoveService.startPluginRemove(session, parseResult, PluginMode.PLUGIN_REMOVE);
        }

    }
}
