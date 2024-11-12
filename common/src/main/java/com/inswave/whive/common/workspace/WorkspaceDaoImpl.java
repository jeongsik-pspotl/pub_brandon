package com.inswave.whive.common.workspace;

import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.role.WorkspaceGroupRoleList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class WorkspaceDaoImpl extends NamedParameterJdbcDaoSupport implements WorkspaceDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO workspace(member_id, workspace_name, status, favorite_flag, created_date, updated_date, delete_yn) VALUES(:member_id, :workspace_name, :status, :favorite_flag, NOW(), NOW(), :delete_yn)";

    private static final String SELECT_BY_ID = "SELECT * FROM workspace WHERE workspace_id = :workspace_id";

    private static final String SELECT_BY_MEMBERID = "SELECT * FROM workspace WHERE member_id = :member_id and workspace_name = :workspace_name and delete_yn = '0' and status = 1";

    private static final String SELECT_BY_ROLE_NAME = "SELECT * FROM workspace where workspace_name = :workspace_name and id IN (select workspace_id from member_role)";

    private static final String SELECT_ALL = "SELECT * FROM workspace where delete_yn = '0'";

    private static final String SELECT_WORKSPACE_ROLE_LIST_ALL = "SELECT * FROM workspace where delete_yn = '0' and status = '1'";

    private static final String SELECT_WORKSPACE_GROUP_ALL = "select * from workspace_group_role wgr where wgr.workspace_id in (select workspace_id from workspace) and wgr.role_id = :role_id";

    private static final String SELECT_BY_MBMBER_ROLE_CODE = "SELECT * FROM workspace " +
            "WHERE id IN (SELECT workspace_id " +
            "FROM member_role " +
            "WHERE role_code_id = " +
            "(SELECT role_code_id FROM role_code WHERE role_code_type = 'member' AND role_code_name = :role_code_name ) )";

    private static final String DELETE_BY_ID = "UPDATE workspace SET delete_yn = :delete_yn WHERE workspace_id = :workspace_id";

    private static final String DELETE_BY_ID_BAK = "DELETE from workspace WHERE workspace_id = :workspace_id";

    private static final String UPDATE_BY_NAME = "UPDATE workspace SET name = :name where  id = :id";

    private static final String UPDATE_BY_ROLE_CODE_ID = "UPDATE workspace SET status = :status, updated_date = NOW()  WHERE id = :id";

    private static final String UPDATE_BY_ROLE_MEMBER_CODE = "UPDATE member_role SET role_code_id = :role_code_id WHERE workspace_id = :workspace_id";

    private static final String UPDATE_BY_CHECK_ALL_YN = "UPDATE workspace SET workspace_name = :workspace_name, delete_yn = :delete_yn, status = :status, updated_date = NOW() WHERE workspace_id = :workspace_id ";

    private static final String INSERT_MEMBER_ROLE_SQL = "INSERT INTO member_role(member_id, workspace_id, role_code_id, domain_id) VALUES(:member_id, :workspace_id, :role_code_id, :domain_id)";

    private static final String SELECT_BY_ROLE_ONE_SQL = "SELECT * FROM role_code WHERE role_code_name = :role_code_name AND role_code_type = 'member'";

    private static final  String SELECT_BY_WORKSPACE_MEMBERID_bak = "SELECT * FROM workspace WHERE id IN (SELECT workspace_id FROM member_role " +
            " WHERE role_code_id = " +
            " (SELECT role_code_id FROM role_code WHERE role_code_type = 'member' AND role_code_name = 'USER' ) and domain_id = :domain_id ) and member_id = :member_id";

    private static final String SELECT_BY_WORKSPACE_MEMBERID = "SELECT w.id, w.member_id, w.workspace_name, w.status, w.favorite_flag, w.workspace_path, w.created_date, w.updated_date, w.workspace_etc1, w.workspace_etc2, w.workspace_etc3 " +
            "FROM member_role mr join role_code rc on mr.role_code_id = rc.role_code_id join workspace w on w.id = mr.workspace_id where w.id in ( select id from workspace order by id desc ) " +
            "and rc.role_code_type = 'member' and rc.role_code_name = 'USER' and mr.domain_id = :domain_id and w.member_id = :member_id";

    private static final String SELECT_BY_WORKSPACE_GROUP_ROLE_ID_ALL = "select wgr.workspace_group_role_id, work.workspace_id, work.workspace_name " +
            "from role r, workspace_group_role wgr, workspace work " +
            "where r.role_id = :role_id and wgr.role_id  = r.role_id and wgr.workspace_id = work.workspace_id and work.status = '1' and work.delete_yn = '0'";

    private static final String SELECT_BY_WORKSPACE_ROLE_CHECK = "SELECT * FROM workspace WHERE workspace_id IN (SELECT workspace_id FROM workspace_group_role work_group, role r WHERE r.role_id = work_group.role_id AND r.role_id = :role_id)";

    private static final String SELECT_BY_WORKSPACE_ROLE_CEHCK_USER = "SELECT w.workspace_id, w.member_id, w.workspace_name, w.status, w.favorite_flag, w.created_date, w.updated_date, wgr.workspace_group_role_id, wgr.role_id " +
            "FROM workspace w, workspace_group_role wgr WHERE w.workspace_id = wgr.workspace_id and wgr.role_id = :role_id and w.delete_yn = '0' and w.status = '1'";

    private static final String SELECT_BY_WORKSPACE_MEMBER_ROLE = "SELECT * FROM workspace WHERE member_id in (select user_id from user where domain_id = :domain_id) and member_id = :member_id and delete_yn = 0";

    private static final String SELECT_BY_WORKSPACE_INDEX_LIST_MEMBER_ROLE = "SELECT * FROM workspace WHERE member_id in (select user_id from user where domain_id = :domain_id) and member_id = :member_id and delete_yn = 0 and status = 1";

    private static final  String SELECT_BY_WORKSPACE_NAME = "select * from workspace where workspace_name = :workspace_name and delete_yn = '0'";

    private static final  String SELECT_BY_WORKSPACE_NAME_BAK = "select * from workspace where id in ( select workspace_id from member_role where domain_id = :domain_id and member_id = :member_id) and workspace_name = :workspace_name";

    private static final String SELECT_ALL_INNER_PROJECT = "SELECT workspace.id, workspace.workspace_name ,workspace.favorite, " +
            "JSON_OBJECT('project_name',build_project.project_name, " +
            "'platform',build_project.platform,'target_server',build_project.target_server, " +
            "'description',build_project.description,'status',build_project.status, " +
            "'template_version',build_project.template_version,'created_date',build_project.created_date, " +
            "'updated_date',build_project.updated_date) as buildproject " +
            "FROM workspace INNER JOIN member ON workspace.user_id = member.id " +
            "LEFT JOIN build_project " +
            "ON workspace.id = build_project.workspace_id " +
            "WHERE member.id = :id ";

    private static final String SELECT_BY_MEMBER_ROLE_WORKSPACE_ID = "SELECT * FROM workspace where workspace_id = :workspace_id";

    private static final String SELECT_BY_WORKSPACE_NAME_CHECK_BY_ID = "SELECT * FROM workspace where workspace_id = :workspace_id and workspace_name = :workspace_name";


    @Override
    public void insert(Workspace workspace){
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(workspace));
    }

    @Override
    public void updateByName(long id, String workspace_name){
        namedParameterJdbcTemplate.update(UPDATE_BY_NAME, new MapSqlParameterSource("id", id).addValue("workspace_name", workspace_name));
    }

    @Override
    public Workspace findById(Long id){
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("workspace_id", id),
                new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public List<Workspace> findAll(){
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public List<Workspace> findWorkspaceRoleListAll() {
        return namedParameterJdbcTemplate.query(SELECT_WORKSPACE_ROLE_LIST_ALL, new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public List<Workspace> findGroupRoleAll() {
        return namedParameterJdbcTemplate.query(SELECT_WORKSPACE_GROUP_ALL, new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public void deleteById(Long id){
        namedParameterJdbcTemplate.update(DELETE_BY_ID, new MapSqlParameterSource("workspace_id", id).addValue("delete_yn","1"));
    }

    @Override
    public Workspace findByMemberId(Long id, String workspaceName){
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_MEMBERID,
                new MapSqlParameterSource("member_id", id).addValue("workspace_name",workspaceName),
                new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public Workspace findByWorkspaceNameAndRoleName(String roleName, String workspaceName) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ROLE_NAME,
                new MapSqlParameterSource("role_code_id", roleName).addValue("workspace_name",workspaceName),
                new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public List<Workspace> findByWorkspaceMemberId(Long member_id, Long domain_id) {
        try {
            List<Workspace> WorkspaceList = namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_MEMBERID,
                    new MapSqlParameterSource("member_id",member_id).addValue("domain_id", domain_id),
                    new BeanPropertyRowMapper<>(Workspace.class));
            return WorkspaceList;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Workspace> findByWorkspaceMemberRole(Long id, Long domain_id) {
        try {
            List<Workspace> WorkspaceList = namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_MEMBER_ROLE,
                    new MapSqlParameterSource("member_id",id).addValue("domain_id", domain_id),
                    new BeanPropertyRowMapper<>(Workspace.class));
            return WorkspaceList;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Workspace> findByWorkspaceIndexListMemberRole(Long id, Long domain_id) {
        try {
            List<Workspace> WorkspaceList = namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_INDEX_LIST_MEMBER_ROLE,
                    new MapSqlParameterSource("member_id",id).addValue("domain_id", domain_id),
                    new BeanPropertyRowMapper<>(Workspace.class));
            return WorkspaceList;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<WorkspaceGroupRoleList> findByWorkspaceGroupRoleAll(int role_id) {
        try {
            List<WorkspaceGroupRoleList> WorkspaceList = namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_GROUP_ROLE_ID_ALL,
                    new MapSqlParameterSource("role_id",role_id),
                    new BeanPropertyRowMapper<>(WorkspaceGroupRoleList.class));
            return WorkspaceList;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void memberRoleInsert(MemberMapping memberMapping) {
        namedParameterJdbcTemplate.update(INSERT_MEMBER_ROLE_SQL, new BeanPropertySqlParameterSource(memberMapping));
    }

    @Override
    public MemberMapping findByMemberRoleWorkspaceID(Long workspace_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_MEMBER_ROLE_WORKSPACE_ID,
                new MapSqlParameterSource("workspace_id", workspace_id),
                new BeanPropertyRowMapper<>(MemberMapping.class));
    }

    @Override
    public Workspace findByWorkspaceNameCheck(String workspaceName) {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_WORKSPACE_NAME,
                    new MapSqlParameterSource("workspace_name",workspaceName),
                    new BeanPropertyRowMapper<>(Workspace.class));
    }

    @Override
    public List<Workspace> findByWorkspaceMemberRoleCode(String memberRoleCode) {
        try {
            List<Workspace> WorkspaceList = namedParameterJdbcTemplate.query(SELECT_BY_MBMBER_ROLE_CODE,
                    new MapSqlParameterSource("role_code_name",memberRoleCode),
                    new BeanPropertyRowMapper<>(Workspace.class));
            return WorkspaceList;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateByRoleCode(String role_code_id, Long workspaceId, LocalDateTime update_date) {
        namedParameterJdbcTemplate.update(UPDATE_BY_ROLE_MEMBER_CODE, new MapSqlParameterSource("role_code_id", role_code_id).addValue("workspace_id", workspaceId).addValue("updated_date",update_date));
    }

    @Override
    public List<WorkspaceBuildProject> findAllProject(Long member_id){
        List<WorkspaceBuildProject> workpspace_buildProject_list = namedParameterJdbcTemplate.query(SELECT_ALL_INNER_PROJECT,
                new MapSqlParameterSource("id", member_id),
                new BeanPropertyRowMapper<>(WorkspaceBuildProject.class));
        return workpspace_buildProject_list;
    }

    @Override
    public void updateByRoleCodeId(long id, String status, LocalDateTime update_date){
        namedParameterJdbcTemplate.update(UPDATE_BY_ROLE_CODE_ID, new MapSqlParameterSource("id", id).addValue("status", status).addValue("updated_date",update_date));
    }

    @Override
    public RoleCode findByRoleCodeId(String member_role){
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ROLE_ONE_SQL,
                new MapSqlParameterSource("role_code_name", member_role),
                new BeanPropertyRowMapper<>(RoleCode.class));
    }

    @Override
    public void updateByWorkspaceCheckYn(Workspace workspace) {
        namedParameterJdbcTemplate.update(UPDATE_BY_CHECK_ALL_YN, new BeanPropertySqlParameterSource(workspace));
    }

    @Override
    public List<Workspace> findByWorkspaceGroupList(int role_id) {
        try {
            List<Workspace> WorkspaceList = namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_ROLE_CEHCK_USER,
                    new MapSqlParameterSource("role_id",role_id),
                    new BeanPropertyRowMapper<>(Workspace.class));
            return WorkspaceList;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Workspace findByWorkspaceNameCheckID(Long workspace_id, String workspace_name) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_WORKSPACE_NAME_CHECK_BY_ID,
                    new MapSqlParameterSource("workspace_id", workspace_id).addValue("workspace_name", workspace_name),
                    new BeanPropertyRowMapper<>(Workspace.class));

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
