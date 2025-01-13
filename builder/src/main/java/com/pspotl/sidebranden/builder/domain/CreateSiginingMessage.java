package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class CreateSiginingMessage {
    private String MsgType;
    private String sessType;
    private String status;
    private String message;

}
