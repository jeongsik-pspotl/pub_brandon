package com.pspotl.sidebranden.builder.domain;

import com.pspotl.sidebranden.builder.enums.SessionType;
import lombok.Data;

@Data
public class HeadquarterIdentity {
    private SessionType sessionType;
    private String sessionId;

}
