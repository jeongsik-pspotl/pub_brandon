package com.inswave.whive.branch.domain;

import com.inswave.whive.branch.enums.SessionType;
import lombok.Data;

@Data
public class HeadquarterIdentity {
    private SessionType sessionType;
    private String sessionId;

}
