package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.enums.SessionType;
import com.inswave.whive.branch.service.WindowsConfigListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class WindowsConfigListMessageHandler implements BranchHandlable {

    @Autowired
    WindowsConfigListService windowsConfigListService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String debugPlatform = parseResult.get(PayloadMsgType.platform.name()).toString();

        if(messageType.equals(ProjectServiceType.HV_MSG_WINDOWS_CONFIG_LIST_INFO_FROM_BRANCH.toString())){
            if(debugPlatform.toLowerCase().equals(PayloadMsgType.repositoryVcsType.name())){
                windowsConfigListService.getJsonTextPublic();
            }
        }else if(messageType.equals(ProjectServiceType.HV_MSG_WINDOWS_CONFIG_LIST_INFO.name())){

            if(debugPlatform.toLowerCase().equals(PayloadMsgType.repositoryVcsType.name())){
                windowsConfigListService.getJsonTextPublic();
            }

        }

    }


}
