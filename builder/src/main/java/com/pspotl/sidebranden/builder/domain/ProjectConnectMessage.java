package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class ProjectConnectMessage {
    private String MsgType;
    private String sessType;
    private String message;

}
