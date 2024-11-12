package com.inswave.whive.common.buildhistory;

import lombok.Data;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

@Data
public class BuildRequestJsonObject {

    private Long workspace_id;
    private String msgType;
    private String sessType;
    private String hqKey;
    private String buildType;
    private String domainID;
    private String userID;
    private String profileType;
    private JSONObject appConfig;
    private String productType;
    private BuildHistory buildHistory;
    private String profileKeyName;
}
