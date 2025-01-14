package com.pspotl.sidebranden.builder.handler;


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
public class TemplatePluginsMsgHandler implements BranchHandlable {

    @Autowired
    TemplatePluginService templatePluginService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        if(messageType.equals(ProjectServiceType.HV_MSG_TEMPLATE_PLUGINS_INFO.name())){

            templatePluginService.startTemplateAllPlugins(session,parseResult);

        }

    }

}
