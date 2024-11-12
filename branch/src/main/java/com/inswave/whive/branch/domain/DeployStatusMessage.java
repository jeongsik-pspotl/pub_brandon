package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class DeployStatusMessage {

    private String MsgType;
    private String sessType;
    private String hqKey;
    private String status;
    private String message;
    private String qrCode;
    private String logValue;
    private String log_path;
    private String logfile_name;
    private int history_id;
    private String build_id;
}
