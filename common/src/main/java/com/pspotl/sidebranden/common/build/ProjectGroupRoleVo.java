package com.pspotl.sidebranden.common.build;

import lombok.Data;

@Data
public class ProjectGroupRoleVo {

    private int project_id;
    private int project_group_role_id;
    private int workspace_group_role_id;
    private int workspace_id;
    private String workspace_name;
    private String project_name;
    private String all_yn;
    private String read_yn;
    private String update_yn;
    private String delete_yn;
    private String build_yn;
    private String deploy_yn;
    private String export_yn;

}
