package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class ProjectTemplateCreateStatusMessage {
    private String MsgType;
    private String sessType;
    private String hqKey;
    private String gitStatus;
    private String message;
    private String logMessage;
    private int build_id;
    private String projectDirPath;
    private Object signingKeyObj;
    private String userProjectPath;
    private String keystorePath;

}
