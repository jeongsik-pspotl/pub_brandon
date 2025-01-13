package com.pspotl.sidebranden.common.role;

import java.util.List;

public interface RoleDao {

    void insert(Role role);

    void insertToWorkspaceGroup(WorkspaceGroupRole workspcaeGroupRole);

    void insertToKeyGroup(KeyGroupRole keyGroupRole);

    void insertToProjectGroup(ProjectGroupRole projectGroupRole);

    void updateToKeyGroup(KeyGroupRole keyGroupRole);

    void updateToProjectGroup(ProjectGroupRole projectGroupRole);

    void updateToRoleName(int role_id, String role_name);

    void deleteToWorkspaceGroup(WorkspaceGroupRole workspaceGroupRole);

    int findRoleLastCount();

    List<Role> findAll();

    Role findByRoldID(int role_id);

    RoleOne findByRoleNameCheck(String role_name);

    List<WorkspaceGroupRole> findByWorkspaceGroupRole(int role_id);

    List<KeyGroupRole> findByKeyGroupRole(int role_id);


}
