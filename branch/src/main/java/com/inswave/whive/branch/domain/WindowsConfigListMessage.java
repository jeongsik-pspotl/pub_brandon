package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class WindowsConfigListMessage {
    private String MsgType;
    private String sessType;
    private String status;
    private String platform;
    private Object resultConfigListObj;

}
