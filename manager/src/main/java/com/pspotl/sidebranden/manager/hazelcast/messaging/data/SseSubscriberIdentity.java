package com.pspotl.sidebranden.manager.hazelcast.messaging.data;

import com.pspotl.sidebranden.manager.hazelcast.HazelcastClusterManager;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
public class SseSubscriberIdentity implements Serializable {
    private String subscriberId;        // userId => admin@inswave.com
    private String subscribeType;       // => DeviceAccessController
    private String subscribeTarget;     // deviceIdentity =>  appId|deviceId => Windows.KPIC.Dev|Windows.73807084-9305-4668-BED3-25BC1BCC7CCE.{C53D3B5A-2761-4184-8BCA-E1AE0A1E6170}.KPIC.Dev
    @Builder.Default
    private String clusterUuid = HazelcastClusterManager.getLocalUuid();

    @Override
    public int hashCode() {
        return Objects.hash(subscriberId, subscribeType, subscribeTarget);
    }
}


