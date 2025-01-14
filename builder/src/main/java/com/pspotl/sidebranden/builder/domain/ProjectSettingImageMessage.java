package com.pspotl.sidebranden.builder.domain;

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
