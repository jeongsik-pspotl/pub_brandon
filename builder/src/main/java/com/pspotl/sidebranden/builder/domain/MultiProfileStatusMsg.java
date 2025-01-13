package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

import java.util.ArrayList;

@Data
public class MultiProfileStatusMsg {

    private String MsgType;
    private String sessType;
    private String status;
    private String message;
    private String hqKey;
    private String managerClusterId;
    private String build_id;
    private ArrayList<String> resultMultiProfileList;

}
