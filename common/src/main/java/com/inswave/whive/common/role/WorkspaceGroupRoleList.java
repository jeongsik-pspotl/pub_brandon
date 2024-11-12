package com.inswave.whive.common.role;

import lombok.Data;

import java.util.List;

@Data
public class WorkspaceGroupRoleList {

    private int workspace_group_role_id;
    private String workspace_name;
    private int role_id;
    private int workspace_id;

}
