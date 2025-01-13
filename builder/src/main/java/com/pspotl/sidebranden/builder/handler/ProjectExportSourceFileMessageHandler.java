package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.ProjectExportSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class ProjectExportSourceFileMessageHandler implements BranchHandlable {

    @Autowired
    ProjectExportSourceService projectExportSourceService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        if(messageType.equals(ProjectServiceType.MV_MSG_PROJECT_EXPORT_ZIP_REQUEST_INFO_BRNACH.toString())){
            // method ProjectExportSourceService 호출
            projectExportSourceService.startExportSource(session, parseResult);

        }


    }


}
