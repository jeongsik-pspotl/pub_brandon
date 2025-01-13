package com.pspotl.sidebranden.common.buildhistory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BuildHistory {

    private Long project_history_id;
    private Long project_id;
    private String hqKey;

    private String project_name;
    private String platform;
    private String status;
    private String status_log;
    private String log_path;
    private String logfile_name;
    private String result;
    private String platform_build_file_path;
    private String platform_build_file_name;
    private String qrcode;
    private String build_number;
    private String ios_builded_target_or_bundle_id; // iOS 프로젝트 빌드한 타겟 이름 또는 번들 ID

    private LocalDateTime history_started_date;
    private LocalDateTime history_ended_date;

}
