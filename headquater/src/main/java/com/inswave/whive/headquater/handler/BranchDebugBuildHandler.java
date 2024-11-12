package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.common.build.BuildProject;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.workspace.Workspace;
import com.inswave.whive.common.workspace.WorkspaceService;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BranchDebugBuildHandler implements Handlable {

    WebSocketSession webSocketSession = null;
    WebSocketSession webSocketBranchSession = null;

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private BuildProjectService buildProjectService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());

        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        if(messageType.equals(ClientMessageType.HV_MSG_DEBUG_BUILD.toString()) || messageType.equals(ClientMessageType.HV_MSG_RELEASE_BUILD.toString())){
            Workspace workspace;
            BuildProject buildProject;

            // project id, workspace id 변수 처리
            Long workspaceId = Long.valueOf(parseResult.get(PayloadKeyType.workspaceID.name()).toString());
            Long projectId = Long.valueOf(parseResult.get(PayloadKeyType.projectID.name()).toString());
            // WorkspaceService, BuildProjectService 호출...
            if(workspaceId != null){
                workspace = workspaceService.findById(workspaceId);
                parseResult.put("workspaceName",workspace.getWorkspace_name());
            }

            if(projectId != null){
                buildProject = buildProjectService.findById(projectId);
                parseResult.put("projectName",buildProject.getProject_name());
            }

            // worksapce, project 조호 해서 name 호출
            // path 설정을 위한 name 값 호출 하는 방법이다.

            // branch build server session
            webSocketBranchSession = WHiveWebSocketHandler.getLocalBranchSession();

            TextMessage message = null;

            if(webSocketBranchSession != null){
                try {
                    message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                    webSocketBranchSession.sendMessage(message);

                } catch (JsonProcessingException e) {
                     
                    log.error("manager build msg hanlder ",e);
                } catch (IOException e) {
                     
                    log.error("manager build msg hanlder ",e);
                }

            }

        }
    }

}
