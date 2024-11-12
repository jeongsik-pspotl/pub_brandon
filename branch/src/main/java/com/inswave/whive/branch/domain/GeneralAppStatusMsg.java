package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class GeneralAppStatusMsg {
    private String MsgType;
    private String sessType;
    private String status;
    private String message;
    private String hqKey;
    private Object data;
    private String projectID;
}
