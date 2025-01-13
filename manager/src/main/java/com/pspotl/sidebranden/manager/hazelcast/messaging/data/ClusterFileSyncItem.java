package com.pspotl.sidebranden.manager.hazelcast.messaging.data;

import com.pspotl.sidebranden.manager.hazelcast.HazelcastClusterManager;
import com.pspotl.sidebranden.manager.hazelcast.messaging.type.SyncActionType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@ToString
public class ClusterFileSyncItem implements Serializable {
    @Builder.Default
    private SyncActionType actionType  = SyncActionType.NORMAL;
    private String         shareDirKey;
    private String         relativeItemPath;
    @Builder.Default
    private String         clusterUuid = HazelcastClusterManager.getLocalUuid();    // 메세지 발생한 노드
    private String         resultClusterUuid;                                       // 메세지 수신한 노드가 회신할 노드

    public static ClusterFileSyncItem from(ClusterFileSyncChunk clusterFileChunk) {
        return builder().shareDirKey(clusterFileChunk.getShareDirKey())
                        .relativeItemPath(clusterFileChunk.getRelativeItemPath())
                        .build();
    }

    public boolean equals(ClusterFileSyncItem other) {
        return hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(shareDirKey, relativeItemPath);
    }
}

