package com.inswave.whive.headquater.hazelcast.messaging.message;

import com.inswave.whive.headquater.hazelcast.messaging.data.SseSubscriberIdentity;
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

