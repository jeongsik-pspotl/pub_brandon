package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class TemplateMessage {
    private String MsgType;
    private String sessType;
    private String hqKey;
    private String status;
    private String message;
    private Object templateVersionList;

}
