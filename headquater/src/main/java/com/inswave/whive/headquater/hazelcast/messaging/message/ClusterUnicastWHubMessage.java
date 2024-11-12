package com.inswave.whive.headquater.hazelcast.messaging.message;

import com.inswave.whive.headquater.handler.WHiveIdentity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ClusterUnicastWHubMessage implements Serializable {
    private ProcessingType processingType;
    private WHiveIdentity wHiveIdentity;
    private String         content;

}

