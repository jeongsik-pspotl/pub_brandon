package com.pspotl.sidebranden.manager.model;

import lombok.Data;

@Data
public class BuilderLoginSessionData {
    Long builderId;
    String builderUserId;
    String builderName;
    String sessionType;

}
