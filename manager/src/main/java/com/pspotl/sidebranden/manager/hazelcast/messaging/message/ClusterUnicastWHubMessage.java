package com.pspotl.sidebranden.manager.hazelcast.messaging.message;

import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
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

