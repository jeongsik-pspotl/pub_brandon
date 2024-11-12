package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class SigningKeyCreateMessage {

    private String MsgType;
    private String sessType;
    private String hqKey;
    private String message;
    private String keyfilePath;
    private String deployfilePath;
    private String signingkeyID;
    private String builderUserID;
    private String builderID;

}
