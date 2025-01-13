package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.common.build.BuildProject;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class GeneralAppProjectCreateStatusHandler implements Handlable {

    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    BuildProjectService buildProjectService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String gitStatus = (String) parseResult.get("gitStatus");

        if (messageType.equals(ClientMessageType.HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO_FROM_HEADQUATER.name())) {
            try {
                BuildProject buildProject = new BuildProject();

                if (gitStatus.equals("DONE")) {
                    buildProject.setProject_id(Long.valueOf(parseResult.get(PayloadKeyType.build_id.name()).toString()));
                    buildProject.setProject_dir_path(parseResult.get(PayloadKeyType.projectDirPath.name()).toString());

                    updateProjectDirPath(buildProject);
                }

                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }
            } catch (Exception e) {
                log.error("General App HQ Send Message Error = {}", e.getMessage(), e);
            }
        }

        else {
            log.info(" ======== BranchHandler handle messageType =========== : {} ", messageType);
        }
    }

    private void updateProjectDirPath(BuildProject buildProject) {
        buildProjectService.updateToProjectDirPath(buildProject);
    }
}
