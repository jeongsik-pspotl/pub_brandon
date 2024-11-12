package com.inswave.whive.branch.domain;

import lombok.Data;

@Data
public class PluginListStatusMessage {
    private String MsgType;
    private String sessType;
    private String status;
    private String message;
    private String platform_plugin_proj_path;
    private String hqKey;
    private String build_id;
    private Object resultAppConfigListObj;
    private Object installedPluginlistObj;
    private Object availablePluginlistObj;

}
