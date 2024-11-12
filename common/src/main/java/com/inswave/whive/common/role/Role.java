package com.inswave.whive.common.role;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role {

    private int role_id;
    private String role_name;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;
    private List<WorkspaceGroupRole> workspaceGroupRole;
    private List<KeyGroupRole> keyGroupRole;
    private List<ProjectGroupRole> projectGroupRole;

}
