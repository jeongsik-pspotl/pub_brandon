package com.inswave.whive.headquater.hazelcast.messaging.data;

import com.inswave.whive.headquater.hazelcast.HazelcastClusterManager;
import com.inswave.whive.headquater.hazelcast.messaging.type.SyncMessageType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class ClusterFileSyncMessage implements Serializable {
    @Builder.Default
    private SyncMessageType syncMessageType = SyncMessageType.REQUEST;
    private String          shareDirKey;
    private String          relativeItemPath;
    private String          contents;
    @Builder.Default
    private String          clusterUuid     = HazelcastClusterManager.getLocalUuid();
    private String          resultClusterUuid;

    public static ClusterFileSyncMessage from(ClusterFileSyncItem remoteItem) {
        return ClusterFileSyncMessage.builder()
                                     .shareDirKey(remoteItem.getShareDirKey())
                                     .relativeItemPath(remoteItem.getRelativeItemPath())
                                     .resultClusterUuid(remoteItem.getResultClusterUuid())
                                     .build();
    }
}
