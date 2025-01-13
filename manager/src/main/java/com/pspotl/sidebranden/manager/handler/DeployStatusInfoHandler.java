package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.deployhistory.DeployHistory;
import com.pspotl.sidebranden.common.deployhistory.DeployHistoryService;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberLoginDao;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import com.pspotl.sidebranden.manager.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DeployStatusInfoHandler implements Handlable {

    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    DeployHistoryService deployHistoryService;

    @Autowired
    MemberLoginDao memberLoginDao;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    EmailUtil emailUtil;

    @Value("${whive.server.type}")
    private String serverType;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());
        String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());
        String messageStatus = (String) parseResult.get("message");

        DeployHistory deployHistory = new DeployHistory();

        //HEADQUATER
        if(messageType == null || messageType.equals("")) return;
        if(sessType == null || sessType.equals("")) return;

        log.info(" ======== BuildStatusInfoHandler session =========== : {} ",session);

        // headquater 보내는 msgType
        if(messageType.equals(ClientMessageType.HV_MSG_DEPLOY_STATUS_INFO_FROM_HEADQUATER.name())){

            log.info(parseResult.toString());
            // status done 값 받으면 아래 기능 수행
            // 일단 deploy_history 테이블 생성 하기
            // 배포 완료 이후 db update 처리
            if(messageStatus.equals("SUCCESSFUL") || messageStatus.equals("FAILED")){

                deployHistory.setResult(messageStatus);
                deployHistory.setDeploy_history_id(Long.valueOf(parseResult.get("history_id").toString()));
                deployHistory.setLog_path(parseResult.get("log_path").toString());
                deployHistory.setLogfile_name(parseResult.get("logfile_name").toString());

                deployHistory.setDeploy_end_date(LocalDateTime.now());

                deployHistoryService.update(deployHistory);

                if(serverType.equals("service")){
                    MemberLogin memberLogin = memberLoginDao.findByOnlyEmail(parseResult.get(PayloadKeyType.hqKey.name()).toString());

                    // emailUtil send build result
                    Map<String, Object> payload = new HashMap<>();

                    payload.put(PayloadKeyType.email.name(),memberLogin.getEmail());
                    payload.put("status", messageStatus);
                    payload.put("user_login_id",memberLogin.getUser_login_id());

                    emailUtil.sendDeployResultToEmail(payload);
                }

                //TODO : builder 큐 관리 상태 값 업데이트 -1 적용
                BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(parseResult.get("build_id").toString()));
                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                builderQueueManaged.setDeploy_queue_status_cnt(builderQueueManaged.getDeploy_queue_status_cnt() - 1);
                builderQueueManagedService.deployUpdate(builderQueueManaged);

            }


            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());

            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }


        }

    }

}
