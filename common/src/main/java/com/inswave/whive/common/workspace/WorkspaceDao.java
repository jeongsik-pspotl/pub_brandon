package com.inswave.whive.common.workspace;

import com.inswave.whive.common.role.WorkspaceGroupRoleList;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkspaceDao {
    void insert(Workspace workspace);

    void updateByName(long id, String workspace_name);

    Workspace findById(Long id);

    void deleteById(Long id);

    List<Workspace> findAll();

    List<Workspace> findWorkspaceRoleListAll();

    List<Workspace> findGroupRoleAll();

    List<WorkspaceBuildProject> findAllProject(Long id);

    Workspace findByMemberId(Long id, String workspaceName);

    Workspace findByWorkspaceNameAndRoleName(String roleName, String workspaceName);

    List<Workspace> findByWorkspaceMemberId(Long id, Long domain_id);

    List<Workspace> findByWorkspaceMemberRole(Long id, Long domain_id);

    List<Workspace> findByWorkspaceIndexListMemberRole(Long id, Long domain_id);

    List<WorkspaceGroupRoleList> findByWorkspaceGroupRoleAll(int role_id);

    void memberRoleInsert(MemberMapping memberMapping);

    MemberMapping findByMemberRoleWorkspaceID(Long workspace_id);

    Workspace findByWorkspaceNameCheck(String workspaceName);

    List<Workspace> findByWorkspaceMemberRoleCode(String memberRoleCode);

    void updateByRoleCode(String role_code_id, Long workspaceId, LocalDateTime update_date);

    void updateByRoleCodeId(long id, String status, LocalDateTime update_date);

    RoleCode findByRoleCodeId(String member_role);

    void updateByWorkspaceCheckYn(Workspace workspace);

    List<Workspace> findByWorkspaceGroupList(int role_id);

    Workspace findByWorkspaceNameCheckID(Long workspace_id, String workspace_name);
}
