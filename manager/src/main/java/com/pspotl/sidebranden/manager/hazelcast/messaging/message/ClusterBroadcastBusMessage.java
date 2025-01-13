package com.pspotl.sidebranden.manager.hazelcast.messaging.message;


import com.pspotl.sidebranden.manager.hazelcast.HazelcastClusterManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.io.Serializable;
import java.util.List;

/**
 * 엣지스퀘어는 사용하고 있었던 클래스였지만 whive 에서는 필요가 없음 무시하면됨
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConditionalOnExpression(value = "${whub.hazelcast.cluster.use}")
public class ClusterBroadcastBusMessage implements Serializable {
    private String       serviceName;
    private String       type;
    private String       appId;
    private String       deviceId;
    private String       url;
    private String       commandId;
    private String       targetType;
    private List<String> targets;

    private String clusterUuid;

    public void processAfterTransfer() {
        if (HazelcastClusterManager.isSame(clusterUuid)) {
            log.info("Ignore same clusterNodeId's message.");
            return;
        }

//        Common common = null;

        try {
            Class<?> cls = Class.forName("com.inswave.appplatform.wedgemanager.common.Common");
            // common = (Common) BeanUtils.getBeanByClass(cls);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("process other clusterNodeId's message.");
        log.debug("[ClusterBroadcastBusMessage] msg : " + clusterUuid + ", " + this.serviceName + ", " + this.appId + ", " + this.deviceId + ", " + this.targets + ", " + this.type);

//        switch (serviceName) {
//        case Constants.DIST_SERVICE_NAME_NEW_INTEGRITY:
//            common.clientSendMsg(this.targets, targetType, this.serviceName, true, null, commandId, appId);
//            break;
//        case Constants.DIST_SERVICE_NAME_NOTIFICATION:
//        case Constants.DIST_SERVICE_NAME_EXECUTE_COMMAND:
//        case Constants.DIST_SERVICE_NAME_EMERGENCY_DEPLOY:
//        case Constants.DIST_SERVICE_NAME_LOGDOWNLOAD:
//        case Constants.DIST_SERVICE_NAME_LOGFILE:
//        case Constants.DIST_SERVICE_NAME_COMMAND:
//            switch (type) {
//            case Constants.DIST_TYPE_ONE:
//                common.clientSendMsg(serviceName, appId, deviceId, true, null, commandId, null);
//                break;
//            case Constants.DIST_TYPE_ALL_PC:
//                common.clientSendMsgDevice(targets, targetType, serviceName, appId, url, true, commandId);
//                break;
//            case Constants.DIST_TYPE_ALL_AUTO:
//                common.clientSendMsgDeviceATM(targets, targetType, serviceName, appId, url, true, commandId);
//                break;
//            }
//            break;
//        default:
//            break;
//        }
    }
}