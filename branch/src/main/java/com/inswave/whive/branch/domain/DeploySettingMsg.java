package com.inswave.whive.branch.domain;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class DeploySettingMsg {
    private String MsgType;
    private String sessType;
    private String hqKey;
    private String status;
    private String message;
    private int deploy_id;
    private String build_id;
    private JSONObject jsonDeploy;
    private JSONObject jsonDeployMetaData;

}
