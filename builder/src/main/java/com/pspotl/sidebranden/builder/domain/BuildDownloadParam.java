package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

import java.nio.file.Path;

@Data
public class BuildDownloadParam {
    private String project_name;
    private String platform_type;
    private String target_server_id;
    private String platform_build_file_path;
    private Path platform_build_file_name;

}
