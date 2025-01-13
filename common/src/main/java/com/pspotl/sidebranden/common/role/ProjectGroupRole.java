package com.pspotl.sidebranden.common.role;

import lombok.Data;

@Data
public class ProjectGroupRole {

    private int project_group_role_id;
    private int workspace_group_role_id;
    private int project_id;
    private int workspace_id;
    private String project_name;
    private String workspace_name;
    private String read_yn;
    private String update_yn;
    private String delete_yn;
    private String build_yn;
    private String deploy_yn;
    private String export_yn;


}
