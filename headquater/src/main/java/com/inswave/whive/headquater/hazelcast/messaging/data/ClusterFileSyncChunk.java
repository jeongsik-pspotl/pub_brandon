package com.inswave.whive.headquater.hazelcast.messaging.data;

import com.inswave.whive.headquater.hazelcast.HazelcastClusterManager;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@EqualsAndHashCode(of = { "clusterUuid", "shareDirKey", "relativeItemPath" })
@ToString
public class ClusterFileSyncChunk implements Serializable {
    private int    chunkCount;
    private int    chunkPos;
    @ToString.Exclude
    private byte[] payload;
    private String relativeItemPath;
    private String shareDirKey;
    @Builder.Default
    private String clusterUuid = HazelcastClusterManager.getLocalUuid();

    @ToString.Include
    public int getPayloadLength() {
        return payload.length;
    }
}
