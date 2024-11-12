package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class ProjectConnectMessage {
    private String MsgType;
    private String sessType;
    private String message;

}
