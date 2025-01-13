package com.pspotl.sidebranden.common.build;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class BuildProject {

    private Long project_id;
    private Long workspace_id;
    private String project_name;
    private String product_type;
    private String platform;
    private String platform_language;
    private String description;
    private String template_version;

    private LocalDateTime created_date;
    private LocalDateTime updated_date;

    private String project_etc1;
    @JsonIgnore
    private String project_etc2;
    @JsonIgnore
    private String project_etc3;

    private String project_dir_path;
    private Long builder_id;
    private Long vcs_id;
    private Long ftp_id;
    private Long key_id;

    private String read_yn;
    private String update_yn;
    private String delete_yn;
    private String build_yn;
    private String deploy_yn;
    private String export_yn;

}