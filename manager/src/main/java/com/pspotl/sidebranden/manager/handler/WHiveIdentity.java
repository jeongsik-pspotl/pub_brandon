package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.hazelcast.HazelcastClusterManager;
import lombok.Data;

import java.io.Serializable;

@Data
public class WHiveIdentity implements Serializable {
    private String  userId;
    private String  name;
    private String  position;
    private String  department;
    private SessionType sessionType;
    private String sessionId;

    private String clusterUuid  = HazelcastClusterManager.getLocalUuid();

    public String getIdentity(){
        return clusterUuid;
    }
}
