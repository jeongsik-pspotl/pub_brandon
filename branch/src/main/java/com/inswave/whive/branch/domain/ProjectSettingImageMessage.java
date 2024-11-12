package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class ProjectSettingImageMessage {

    private String MsgType;
    private String sessType;
    private String message;
    private String status;
    private String hqKey;
    private Object imageList;

}
