package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

import java.util.List;

@Data
public class iOSAllKeyCreateMsg {

    private String MsgType;
    private String sessType;
    private String hqKey;
    private String message;
    private String keyfilePath;
    private String debugProfilePath;
    private String releaseProfilePath;
    private String signingkeyID;
    private String builderUserID;
    private List<Object> profilesList;
    private List<Object> certificatesList;
}
