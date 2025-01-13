package com.pspotl.sidebranden.common.deployhistory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeployHistory {

    private Long deploy_history_id;
    private Long project_id;
    private Long deploy_id;
    private Long build_id;
    private Long deploy_setting_id;
    private String hqKey;

    private String project_name;
    private String platform_type;
    private String target_server_id;
    private String status;
    private String status_log;
    private String log_path;
    private String logfile_name;
    private String result;

    private LocalDateTime deploy_start_date;
    private LocalDateTime deploy_end_date;

}
