package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class BuildStatusMessage {
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
    private Object BuildFileObj;
    private Object buildHistoryObj;
    private String buildMode;
    private String buildNumber;

}
