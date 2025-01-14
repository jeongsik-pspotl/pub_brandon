package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.TemplateListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class ProjectTemplateVersionListMsgHandler implements BranchHandlable{

    @Autowired
    TemplateListService templateListService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_LIST_INFO.name())){
            // template service 호출
            templateListService.startTemplateList(session, parseResult);

        }

    }
}
