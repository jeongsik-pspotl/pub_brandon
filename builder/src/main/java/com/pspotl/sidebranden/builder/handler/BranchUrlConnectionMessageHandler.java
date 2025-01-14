package com.pspotl.sidebranden.builder.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.PluginAddListParam;
import com.pspotl.sidebranden.builder.domain.ProjectConnectMessage;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class BranchUrlConnectionMessageHandler implements BranchHandlable{

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    // websocket message test...
    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        ProjectConnectMessage projectConnectMessage = new ProjectConnectMessage();
        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        if(messageType.equals(BuildServiceType.HV_MSG_CONNETCION_CHECK_INFO.name())){
            // if session obj remote address check ??

            // send headquater
            projectConnectMessageHandler(projectConnectMessage, PayloadMsgType.SUCCESSFUL.name());

        }

    }

    private void projectConnectMessageHandler (ProjectConnectMessage projectConnectMessage, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        projectConnectMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        projectConnectMessage.setMsgType(BuildServiceType.HV_MSG_CONNETCION_CHECK_INFO.name());
        projectConnectMessage.setMessage(messageValue);

        Map<String, Object> parseResult = Mapper.convertValue(projectConnectMessage, Map.class);
        headQuaterClientHandler.sendMessage(parseResult);
    }

}
