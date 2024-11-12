package com.inswave.whive.common.deployhistory;

import lombok.Data;

@Data
public class DeployReqeusetHistory {

    private Long workspace_id;
    private Long project_history_id;
    private Long domain_id;
    private Long user_id;
    private String hqKey;
    private String deploy_type;
    private String ios_app_bundleID;
    private DeployHistory deployHistory;

}
