package com.pspotl.sidebranden.manager.hazelcast.messaging.message;

import com.pspotl.sidebranden.manager.hazelcast.HazelcastClusterManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 엣지스퀘어는 사용하고 있었던 클래스였지만 whive 에서는 필요가 없음 무시하면됨
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@ConditionalOnExpression(value = "${whive.hazelcast.cluster.use}")
public class ClusterBroadcastLogBinaryList implements Serializable {
    private String commandId;
    // private List<LogFileDTO> allList;

    private String clusterUuid;

    public void processAfterTransfer() {
        if (HazelcastClusterManager.isSame(clusterUuid)) {
            log.info("[ClusterBroadcastLogBinaryList][processAfterTransfer] Ignore same clusterNodeId's message.");
            return;
        }

//        Common common =  null;
//
//        try {
//            Class<?> cls = Class.forName("com.inswave.appplatform.wedgemanager.common.Common");
//            common = (Common) BeanUtils.getBeanByClass(cls);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        log.info("[ClusterBroadcastLogBinaryList][processAfterTransfer] process other clusterNodeId's message.");
//        log.debug("[ClusterBroadcastLogBinaryList] msg : " + commandId + ", " + allList);
//
//        try {
//            log.info("[ClusterBroadcastLogBinaryList][processAfterTransfer] data : " + commandId + ", " + allList);
//            common.getLogBinaryList(commandId, allList, true);
//            log.info("[ClusterBroadcastLogBinaryList][processAfterTransfer] End");
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
    }
}
