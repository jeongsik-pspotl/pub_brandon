package com.inswave.whive.common.role;

import lombok.Data;

@Data
public class WorkspaceGroupRole {

    private int workspace_group_role_id;
    private int role_id;
    private int workspace_id;
    private String select_yn;

}
