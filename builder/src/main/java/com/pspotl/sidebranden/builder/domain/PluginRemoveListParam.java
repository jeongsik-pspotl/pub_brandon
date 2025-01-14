package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

@Data
public class PluginRemoveListParam {
    private String MsgType;
    private String sessType;
    private String status;
    private String message;
    private Object resultPluginListObj;
    private Object installedPluginlistObj;
    private Object availablePluginlistObj;
}
