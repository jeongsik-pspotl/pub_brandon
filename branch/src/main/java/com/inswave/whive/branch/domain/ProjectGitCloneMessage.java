package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class ProjectGitCloneMessage {
    private String MsgType;
    private String sessType;
    private String hqKey;
    private String gitStatus;
    private String message;
    private String logMessage;
    private Object signingKeyObj;
    private String userProjectPath;
    private String keystorePath;
    private String projectDirPath;
    private Integer build_id;


}
