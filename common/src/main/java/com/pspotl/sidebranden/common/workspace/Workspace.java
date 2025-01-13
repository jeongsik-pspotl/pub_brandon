package com.pspotl.sidebranden.common.workspace;

import lombok.*;


@Data
public class Workspace {

    private Long workspace_id;
    private Long workspace_group_role_id;

    private Long member_id;

    private String workspace_name;
    private String status;
    private String favorite_flag;
    private String delete_yn;
    private String created_date;

    private String updated_date;

    private MemberMapping memberMapping;


}
