package com.pspotl.sidebranden.manager.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingService;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.buildhistory.BuildHistory;
import com.pspotl.sidebranden.common.buildhistory.BuildHistoryService;
import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberLoginDao;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import com.pspotl.sidebranden.manager.enums.StatusType;
import com.pspotl.sidebranden.manager.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BuildStatusInfoHandler implements Handlable {

    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    BuildHistoryService buildHistoryService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberLoginDao memberLoginDao;

    @Autowired
    BranchSettingService branchSettingService;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    EmailUtil emailUtil;

    @Value("${whive.server.type}")
    private String serverType;

    // DB 접근을 최소화 하기 위해서, 현재 빌드 상태를 기록하기 위한 static 변수 선언 / @soorink
    private static String nowStatus = "";

    /**
     * 빌드가 완료된 후, 결과를 project_build 테이블에 업데이트 한다.
     *
     * @soorink
     */
    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        BuildHistory buildHistory = new BuildHistory();

        try {
            String messageType = (String) parseResult.get(SessionType.MsgType.name());
            String sessType = (String) parseResult.get(PayloadKeyType.sessType.name());
            String messageStatus = (String) parseResult.get("message");

            String historyId = parseResult.get("history_id").toString();

            //HEADQUATER
            if(messageType == null || messageType.equals("")) return;
            if(sessType == null || sessType.equals("")) return;

            // headquater 보내는 msgType
            if(messageType.equals(ClientMessageType.HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER.name())){
                //                buildHistory = new BuildHistory();
                ObjectMapper objectMapper = new ObjectMapper();
                Object buildHistoryObjStr = parseResult.get("buildHistoryObj");

                buildHistory.setStatus_log(messageStatus);

                if(messageStatus.equals(StatusType.SUCCESSFUL.name()) || messageStatus.equals(StatusType.FAILED.name())) {
                    String status = parseResult.get("status").toString();
                    String logPath = parseResult.get("log_path").toString();
                    String logFileName = parseResult.get("logfile_name").toString();
                    String qrCode = messageStatus.equals(StatusType.SUCCESSFUL.name()) ? parseResult.get("qrCode").toString() : "";
                    String buildNumber = StringUtils.isEmpty(parseResult.get("buildNumber")) ? "" : parseResult.get("buildNumber").toString();
                    Map<String, Object> historyMap = objectMapper.convertValue(buildHistoryObjStr, Map.class);
                    MemberLogin memberLogin = memberLoginDao.findByOnlyEmail(parseResult.get(PayloadKeyType.hqKey.name()).toString());

                    // build history vo setting
                    buildHistory.setProject_id(Long.valueOf(historyMap.get(PayloadKeyType.build_id.name()).toString()));
                    buildHistory.setProject_history_id(Long.valueOf(historyId));
                    buildHistory.setHqKey(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                    buildHistory.setProject_name(historyMap.get("buildProjectName").toString());
                    buildHistory.setPlatform(historyMap.get(PayloadKeyType.platform.name()).toString());
                    buildHistory.setLog_path(logPath);
                    buildHistory.setLogfile_name(logFileName);
                    buildHistory.setStatus(status);
                    buildHistory.setQrcode(qrCode);
                    buildHistory.setBuild_number(buildNumber);
                    buildHistory.setResult(messageStatus);

                    if (buildHistory.getProject_id() == null) {
                        throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
                    }
                    try {
                        if (messageStatus.equals(StatusType.SUCCESSFUL.name())) {
                            Object BuildFileObjStr = parseResult.get("buildFileObj");
                            String buildMode = parseResult.get("buildMode").toString();

                            log.info(" Build SUCCESSFUL qrcode manager ... :  {}", qrCode);

                            Map<String, Object> fileMap = objectMapper.convertValue(BuildFileObjStr, Map.class);

                            buildHistory.setPlatform_build_file_path(fileMap.get("platform_build_file_path").toString());
                            buildHistory.setPlatform_build_file_name(fileMap.get("platform_build_file_name").toString());
                            buildHistory.setStatus_log(StatusType.SUCCESSFUL.name()); //빌드완료

                            parseResult.put("user_name", memberLogin.getUser_name());
                            parseResult.put("user_etc1", memberLogin.getUser_etc1());
                        } else if (messageStatus.equals(StatusType.FAILED.name())) {
                            buildHistory.setStatus_log(StatusType.FAILED.name()); //빌드실패
                        }

                        Long historyTotalCnt = updateBuildResultAction(buildHistory);
                        parseResult.put("historyCnt", historyTotalCnt);

                        if (serverType.equals("service")) {
                            // emailUtil send build result
                            Map<String, Object> payload = new HashMap<>();

                            payload.put(PayloadKeyType.email.name(), memberLogin.getEmail());
                            payload.put("status", messageStatus);
                            payload.put("user_login_id", memberLogin.getUser_login_id());

                            emailUtil.sendBuildResultToEmail(payload);
                        }

                        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(buildHistory.getProject_id());

                        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
                        builderQueueManaged.setBuild_queue_status_cnt(builderQueueManaged.getBuild_queue_status_cnt() - 1);
                        builderQueueManagedService.update(builderQueueManaged);

                    } catch (Exception e) {
                        buildHistory.setStatus_log(StatusType.FAILED.name());
                        updateBuildResultAction(buildHistory);
                    }
                } else {
                    if (!messageStatus.equals(nowStatus)) {
                        buildHistory.setProject_history_id(Long.valueOf(historyId));
                        buildHistoryService.updateStatusLog(buildHistory);
                        nowStatus = messageStatus;
                    }
                }

                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }
            }
        } catch (NullPointerException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
    }

    public Long updateBuildResultAction(BuildHistory buildHistory){
        Long historyTotalCnt = buildHistoryService.update(buildHistory);
        memberService.updateMemberBuildYn(buildHistory.getHqKey(),"N");
        return historyTotalCnt;
    }
}