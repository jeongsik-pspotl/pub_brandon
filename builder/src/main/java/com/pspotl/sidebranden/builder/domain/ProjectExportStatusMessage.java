package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class ProjectExportStatusMessage {
    private String MsgType;
    private String sessType;
    private String hqKey;
    private String filename;
    private String zipFileUrl;
}
