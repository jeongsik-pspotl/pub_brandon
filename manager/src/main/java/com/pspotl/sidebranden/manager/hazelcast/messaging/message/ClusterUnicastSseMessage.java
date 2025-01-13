package com.pspotl.sidebranden.manager.hazelcast.messaging.message;

import com.pspotl.sidebranden.manager.hazelcast.messaging.data.SseSubscriberIdentity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ClusterUnicastSseMessage implements Serializable {
    private ProcessingType        processingType;
    private SseSubscriberIdentity sseIdentity;
    private String                content;

}

