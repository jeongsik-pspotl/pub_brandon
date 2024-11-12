package com.inswave.whive.common.buildsetting;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class BuildSetting {

    private Long project_setting_id;

    private Long project_id;

    private String app_id;
    private String app_name;
    private String app_version;
    private String app_version_code;
    private String package_name; // Android
    private String xcode_proj_name; // iOS
    private String min_target_version;

    private String icon_image_path;
    @JsonIgnore
    private String screenshot_image_path;

    private String zip_file_name;

    private ArrayList<Object> server;

    private String profile_name; // Android
    private String target_name; // iOS
    private String build_type;
    private String debug_type;

    private LocalDateTime created_date;

    @JsonIgnore
    private LocalDateTime updated_date;
    @JsonIgnore
    private String project_setting_etc1;
    @JsonIgnore
    private String project_setting_etc2;
    @JsonIgnore
    private String project_setting_etc3;

}
