package com.inswave.whive.headquater.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.build.BuildProject;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.headquater.client.ClientHandler;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;


// Workspace, Project 생성시 수행 하는 handler
@Slf4j
@Component
public class BranchCreateProjectHandler implements Handlable {

    WebSocketSession webSocketSession = null;
    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());


        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        // 수행 시작 일때만 BRANCH 전송
        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_CREATE_INFO.name()) || messageType.equals(ClientMessageType.HV_MSG_WORKSPACE_CREATE_INFO.name()) ){
            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();
            TextMessage message = null;

            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                } catch (IOException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

            }
        }

        // git clone cli status send message
        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name())){
            webSocketSession = WHiveWebSocketHandler.getLocalBranchSession();
            TextMessage message = null;

            if(webSocketSession != null){
                try {
                    message = new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(parseResult));
                    webSocketSession.sendMessage(message);
                } catch (JsonProcessingException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                } catch (IOException e) {
                     
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

            }
        }else if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_HEADQUATER.name())){
            try {
                log.info(parseResult.toString());
                String gitStatus = (String) parseResult.get("gitStatus");

                BuildProject buildProject = new BuildProject();

                if(gitStatus.equals("DONE")){

                    buildProject.setProject_id(Long.valueOf(parseResult.get(PayloadKeyType.build_id.name()).toString()));
                    buildProject.setProject_dir_path(parseResult.get(PayloadKeyType.projectDirPath.name()).toString());

                    updateProjectDirPath(buildProject);

                    // TODO : builder 큐 관리 상태 값 업데이트 -1 적용 OK
                    BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(buildProject.getProject_id());

                    BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                    builderQueueManaged.setProject_queue_status_cnt(builderQueueManaged.getProject_queue_status_cnt() - 1);
                    builderQueueManagedService.projectUpdate(builderQueueManaged);

                }

                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));
                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    // 해당 구간 수정 필요..
                    // headQuaterWebsocketSession.sendMessage(message);
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }
            } catch (IOException e) {
                 
                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }

        // app info


        // app icon image
        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_APP_CONFIG_IMAGE_LIST_INFO_FROM_HEADQUATER.name())){
            try {
                TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    // 해당 구간 수정 필요..
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }
            } catch (IOException e) {
                 
                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }

    }

    private void updateProjectDirPath(BuildProject buildProject){
        buildProjectService.updateToProjectDirPath(buildProject);
    }

}
