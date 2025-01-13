package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class AppConfigListStatusMsg {

    private String MsgType;
    private String sessType;
    private String status;
    private String message;
    private String hqKey;
    private String build_id;
    private Object resultAppConfigListObj;

}
