package com.inswave.whive.headquater.handler;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.build.BuildProject;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
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

@Slf4j
@Component
public class HeadquarterProjectTemplateCreateStatusHandler implements Handlable{

    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String gitStatus = (String) parseResult.get("gitStatus");

        // headquater 보내는 msgType
        if(messageType.equals(ClientMessageType.HV_MSG_PROJECT_TEMPLATE_STATUS_INFO_FROM_HEADQUATER.name())){

            log.info(parseResult.toString());

            BuildProject buildProject = new BuildProject();

            if(gitStatus.equals("DONE")){

                buildProject.setProject_id(Long.valueOf(parseResult.get(PayloadKeyType.build_id.name()).toString()));
                buildProject.setProject_dir_path(parseResult.get(PayloadKeyType.projectDirPath.name()).toString());

                updateProjectDirPath(buildProject);

                //TODO : builder 큐 관리 상태 값 업데이트 -1 적용 OK
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(buildProject.getProject_id());
                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setProject_queue_status_cnt(builderQueueManaged.getProject_queue_status_cnt() - 1);
                builderQueueManagedService.projectUpdate(builderQueueManaged);
            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        }else {
            log.info(" ======== BranchHandler handle messageType =========== : {} ",messageType);

        }

    }

    private void updateProjectDirPath(BuildProject buildProject){
        buildProjectService.updateToProjectDirPath(buildProject);
    }

}
