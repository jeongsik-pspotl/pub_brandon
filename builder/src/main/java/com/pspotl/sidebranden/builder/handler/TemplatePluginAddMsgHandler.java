package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.PluginMode;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.TemplatePluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class TemplatePluginAddMsgHandler implements BranchHandlable{

    @Autowired
    TemplatePluginService templatePluginService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        if(messageType.equals(ProjectServiceType.HV_MSG_TEMPLATE_PLUGIN_ADD_INFO.name())){

            templatePluginService.startPluginAdd(session, parseResult, null, PluginMode.PLUGIN_ADD);

        }

    }


}
