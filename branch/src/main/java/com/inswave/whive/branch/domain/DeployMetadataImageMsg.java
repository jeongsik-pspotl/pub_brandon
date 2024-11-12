package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class DeployMetadataImageMsg {
    private String MsgType;
    private String sessType;
    private String hqKey;
    private String status;
    private String message;
    private String build_id;

}
