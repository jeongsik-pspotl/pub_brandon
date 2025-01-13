package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class WindowsConfigListMessage {
    private String MsgType;
    private String sessType;
    private String status;
    private String platform;
    private Object resultConfigListObj;

}
