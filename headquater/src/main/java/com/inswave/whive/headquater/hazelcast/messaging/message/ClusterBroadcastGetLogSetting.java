package com.inswave.whive.headquater.hazelcast.messaging.message;

import com.inswave.whive.headquater.hazelcast.HazelcastClusterManager;
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
public class ClusterBroadcastGetLogSetting implements Serializable {
    // private LogSettingDTO logDTO;
    private String commandId;
    private String clusterUuid;

    public void processAfterTransfer() {
        if (HazelcastClusterManager.isSame(clusterUuid)) {
            log.info("[ClusterBroadcastLogList][processAfterTransfer] Ignore same clusterNodeId's message.");
            return;
        }

        // Common common =  null;

//        try {
//            Class<?> cls = Class.forName("com.inswave.appplatform.wedgemanager.common.Common");
//            common = (Common) BeanUtils.getBeanByClass(cls);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        log.info("[ClusterBroadcastLogList][processAfterTransfer] process other clusterNodeId's message.");
//        log.debug("[ClusterBroadcastLogList] msg : " + logDTO);

        try {
   //         log.info("[ClusterBroadcastLogList][processAfterTransfer] data :" + logDTO);
            // common.getWGearConfig(logDTO, true, commandId);
            log.info("[ClusterBroadcastLogList][processAfterTransfer] End");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}