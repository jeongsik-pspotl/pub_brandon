package com.inswave.whive.common.role;

import com.inswave.whive.common.branchsetting.BranchSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class RoleDaoImpl extends NamedParameterJdbcDaoSupport implements RoleDao{

    private static final String INSERT_SQL = "INSERT INTO role(role_name, created_date, updated_date) " +
            "VALUES(:role_name, NOW(), NOW())";

    private static final String INSERT_WORKSPACE_GROUP_SQL = "INSERT INTO workspace_group_role (role_id, workspace_id) VALUES(:role_id, :workspace_id)";

    private static final String INSERT_KEY_GROUP_SQL = "INSERT INTO key_group_role (role_id, key_build_android_id, key_build_ios_id, key_deploy_android_id, key_deploy_ios_id) " +
            "VALUES(:role_id, :key_build_android_id, :key_build_ios_id, :key_deploy_android_id, :key_deploy_ios_id)";

    private static final String INSERT_PROJECT_GROUP_SQL = "INSERT INTO project_group_role (workspace_group_role_id, project_id, read_yn, update_yn, delete_yn, build_yn, deploy_yn, export_yn) " +
            "VALUES (:workspace_group_role_id, :project_id, :read_yn, :update_yn, :delete_yn, :build_yn, :deploy_yn, :export_yn)";

    private static final String UPDATE_KEY_GROUP_SQL = "UPDATE key_group_role SET key_build_android_id = :key_build_android_id, key_build_ios_id = :key_build_ios_id, key_deploy_android_id = :key_deploy_android_id, key_deploy_ios_id = :key_deploy_ios_id " +
        "WHERE role_id = :role_id";

    private static final String UPDAETE_PROJECT_GROUP_SQL = "UPDATE project_group_role SET read_yn = :read_yn, update_yn = :update_yn, delete_yn = :delete_yn" +
            ", build_yn = :build_yn, deploy_yn = :deploy_yn, export_yn = :export_yn " +
            "WHERE project_group_role_id = :project_group_role_id";

    private static final String UPDAETE_ROLE_NAME_SQL = "UPDATE role SET role_name = :role_name WHERE role_id = :role_id";

    private static final String DELETE_WORKSPACE_GROUP_SQL = "delete from workspace_group_role where workspace_group_role_id = :workspace_group_role_id and role_id = :role_id and workspace_id = :workspace_id";

    private static final String SELECT_BY_LAST_INDEX_ID = "SELECT LAST_INSERT_ID()";

    private static final String SELECT_BY_ALL = "SELECT * FROM role";

    private static final String SELECT_BY_ROLE_ID = "SELECT * FROM role where role_id = :role_id";

    private static final String SELECT_BY_ROLE_NAME_CHECK = "SELECT role_id, role_name, created_date, updated_date FROM role where role_name = :role_name";

    private static final String SELECT_BY_WORKSPACE_GROUP_LIST = "select r.role_id,  wg.workspace_id, wg.workspace_group_role_id " +
            "from role r, workspace_group_role wg where r.role_id = wg.role_id and r.role_id = :role_id";

    private static final String SELECT_BY_KEY_GROUP_LIST = "SELECT r.role_id, key_build_android_id, key_build_ios_id, key_deploy_android_id, key_deploy_ios_id, key_group_role_id " +
            "FROM role r, key_group_role kg where r.role_id = kg.role_id and r.role_id = :role_id";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insert(Role role) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(role));
    }

    @Override
    public void insertToWorkspaceGroup(WorkspaceGroupRole workspaceGroupRole) {
        namedParameterJdbcTemplate.update(INSERT_WORKSPACE_GROUP_SQL, new BeanPropertySqlParameterSource(workspaceGroupRole));

    }

    @Override
    public void insertToKeyGroup(KeyGroupRole keyGroupRole) {
        namedParameterJdbcTemplate.update(INSERT_KEY_GROUP_SQL, new BeanPropertySqlParameterSource(keyGroupRole));
    }

    @Override
    public void insertToProjectGroup(ProjectGroupRole projectGroupRole) {
        namedParameterJdbcTemplate.update(INSERT_PROJECT_GROUP_SQL, new BeanPropertySqlParameterSource(projectGroupRole));
    }

    @Override
    public void updateToKeyGroup(KeyGroupRole keyGroupRole) {
        namedParameterJdbcTemplate.update(UPDATE_KEY_GROUP_SQL, new BeanPropertySqlParameterSource(keyGroupRole));
    }

    @Override
    public void updateToProjectGroup(ProjectGroupRole projectGroupRole) {
        namedParameterJdbcTemplate.update(UPDAETE_PROJECT_GROUP_SQL, new BeanPropertySqlParameterSource(projectGroupRole));
    }

    @Override
    public void updateToRoleName(int role_id, String role_name) {
        namedParameterJdbcTemplate.update(UPDAETE_ROLE_NAME_SQL, new MapSqlParameterSource("role_name", role_name).addValue("role_id",role_id));
    }

    @Override
    public void deleteToWorkspaceGroup(WorkspaceGroupRole workspaceGroupRole) {
        namedParameterJdbcTemplate.update(DELETE_WORKSPACE_GROUP_SQL, new BeanPropertySqlParameterSource(workspaceGroupRole));
    }


    @Override
    public int findRoleLastCount() {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_LAST_INDEX_ID, new MapSqlParameterSource("id", 0),Integer.class);
    }

    @Override
    public List<Role> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_BY_ALL, new BeanPropertyRowMapper<>(Role.class));
    }

    @Override
    public Role findByRoldID(int role_id) {
        try{

            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ROLE_ID, new MapSqlParameterSource("role_id", role_id),Role.class);
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public RoleOne findByRoleNameCheck(String role_name) {
        try{
            return (RoleOne) namedParameterJdbcTemplate.queryForObject(SELECT_BY_ROLE_NAME_CHECK, new MapSqlParameterSource("role_name", role_name),new BeanPropertyRowMapper<>(RoleOne.class));
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public List<WorkspaceGroupRole> findByWorkspaceGroupRole(int role_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_GROUP_LIST, new MapSqlParameterSource("role_id", role_id),new BeanPropertyRowMapper<>(WorkspaceGroupRole.class));
    }

    @Override
    public List<KeyGroupRole> findByKeyGroupRole(int role_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_KEY_GROUP_LIST, new MapSqlParameterSource("role_id", role_id),new BeanPropertyRowMapper<>(KeyGroupRole.class));
    }


}
