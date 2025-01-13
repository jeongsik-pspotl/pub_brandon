package com.pspotl.sidebranden.common.build;

import com.pspotl.sidebranden.common.role.ProjectGroupRole;

import java.util.List;

public interface BuildProjectDao {
    void insert(BuildProject buildProject);

    BuildProject findById(Long id);

    BuildProject findByProjectName(String projectName);

    BuildProject findByProjectNameCheckedID(Long project_id, String project_name);

    void deleteById(Long id);

    List<BuildProject> findAll();

    List<BuildProject> findAllAdminProject(Long user_id);

    List<ProjectGroupRoleVo> findAllProject();

    List<ProjectGroupRoleVo> findAllProjectByRoleID(int role_id);

    List<BuildProject> findAllProjectFree(Long user_id);

    List<BuildProject> findAllProjectProfessional(Long user_id);

    List<BuildProject> findProjectGroupAll(int role_id);

    List<ProjectGroupRoleVo> findByWorkspaceID(int workspace_id);

    List<ProjectGroupRoleVo> findByWorkspaceProjectCheckedAll(int workspace_id, int workspace_group_role_id);

    List<ProjectGroupRole> findByWorkspaceGroupID(int workspace_group_id);

    List<PlatformSelectAll> selectPlatformFIndAll(String role_code_type);

    List<ProgrammingSelectAll> selectProgramFindAll(String role_code_type);

    ProgrammingSelectAll selectProgramFindOne(String role_code_id);

    BuildProject findByWorkspaceId(Long id, String name);

    void updateBuildProject(BuildProject buildProject);

    void updateBuildVersionProject(BuildProject buildProject);

    void updateBuildProjectDirPath(BuildProject buildProject);

    int findByProjectCount();
}
