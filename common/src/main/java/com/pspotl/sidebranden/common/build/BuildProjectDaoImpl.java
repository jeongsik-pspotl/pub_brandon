package com.pspotl.sidebranden.common.build;

import com.pspotl.sidebranden.common.role.ProjectGroupRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@Slf4j
public class BuildProjectDaoImpl extends NamedParameterJdbcDaoSupport implements BuildProjectDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

//    private static final String INSERT_SQL = "INSERT INTO build_project(user_id,name,status,workspace_path,created_date) VALUES(:user_id, :name, :status, :workspace_path, :created_date)";
    private static final String INSERT_SQL = "INSERT INTO project(workspace_id, project_name, platform," +
        " description, template_version, project_dir_path, platform_language, ftp_id, builder_id, vcs_id, key_id, created_date, updated_date, project_etc1, project_etc2, project_etc3, delete_yn, product_type) " +
        "VALUES(:workspace_id, :project_name, :platform, " +
        " :description, :template_version, :project_dir_path, :platform_language, :ftp_id, :builder_id, :vcs_id, :key_id, NOW(), NOW(), :project_etc1, :project_etc2, :project_etc3, '0', :product_type)";

    private static final String SELECT_BY_ID = "SELECT * FROM project WHERE project_id = :project_id";

    private static final String SELECT_BY_WORKSPACE_ID = "SELECT * FROM project WHERE workspace_id = :workspace_id AND project_name = :project_name and delete_yn = '0'";

    private static final String SELECT_BY_PROEJECT_NAME_CHECK = "select * from project where project_name = :project_name and delete_yn = '0'";

    private static final String SELECT_FREE_TIRE_ALL = "SELECT p.project_id, p.workspace_id, p.project_name, p.product_type, p.platform, p.description, p.description, p.template_version, p.project_dir_path, p.platform_language " +
            " , p.ftp_id, p.builder_id, p.vcs_id, p.key_id, p.created_date, p.updated_date " +
            " , 1 as read_yn, 1 as update_yn, 1 as delete_yn, 1 as build_yn, 0 as deploy_yn, 1 as export_yn FROM project p, workspace w where w.workspace_id = p.workspace_id and w.member_id = :user_id and p.delete_yn = '0'";

    private static final String SELECT_PROFESSIONAL_TIRE_ALL = "SELECT p.project_id, p.workspace_id, p.project_name, p.product_type, p.platform, p.description, p.description, p.template_version, p.project_dir_path, p.platform_language " +
            " , p.ftp_id, p.builder_id, p.vcs_id, p.key_id, p.created_date, p.updated_date " +
            " , 1 as read_yn, 1 as update_yn, 1 as delete_yn, 1 as build_yn, 1 as deploy_yn, 1 as export_yn FROM project p, workspace w where w.workspace_id = p.workspace_id and w.member_id = :user_id and p.delete_yn = '0'";

    private static final String SELECT_ALL = "SELECT p.project_id, p.workspace_id, p.project_name, p.product_type, p.platform, p.description, p.description, p.template_version, p.project_dir_path, p.platform_language " +
            " , p.ftp_id, p.builder_id, p.vcs_id, p.key_id, p.created_date, p.updated_date " +
            " , 1 as read_yn, 1 as update_yn, 1 as delete_yn, 1 as build_yn, 1 as deploy_yn, 1 as exprot_yn FROM project p, workspace w where w.workspace_id = p.workspace_id and p.delete_yn = '0'";

    private static final String SELECT_ADMIN_ALL = "SELECT p.project_id, p.workspace_id, p.project_name, p.product_type, p.platform, p.description, p.description, p.template_version, p.project_dir_path, p.platform_language " +
            " , p.ftp_id, p.builder_id, p.vcs_id, p.key_id, p.created_date, p.updated_date " +
            " , 1 as read_yn, 1 as update_yn, 1 as delete_yn, 1 as build_yn, 1 as deploy_yn, 1 as export_yn FROM project p, workspace w where w.workspace_id = p.workspace_id and w.member_id = :user_id and p.delete_yn = '0'";

    private static final String SELECT_PROJECT_GROUP_ALL = "SELECT p.project_id, p.workspace_id, p.project_name, p.product_type, p.platform, p.description, p.description, p.template_version, p.project_dir_path, p.platform_language" +
            ", p.ftp_id, p.builder_id, p.vcs_id, p.key_id, p.created_date, p.updated_date" +
            ", pgr.read_yn, pgr.update_yn, pgr.delete_yn, pgr.build_yn, pgr.deploy_yn, pgr.export_yn FROM project p, project_group_role pgr, workspace_group_role wgr, role r " +
            "where p.project_id = pgr.project_id and pgr.workspace_group_role_id = wgr.workspace_group_role_id  and wgr.role_id = r.role_id and r.role_id = :role_id and p.delete_yn = '0'";

    private static final String SELECT_PROJECT_ALL_BY_ROLE_ID = "SELECT pgr.project_group_role_id ,pgr.workspace_group_role_id ,p.project_id, p.workspace_id, (select w.workspace_name from workspace w where p.workspace_id = w.workspace_id ) as workspace_name, p.project_name, pgr.read_yn, pgr.update_yn, pgr.delete_yn, pgr.build_yn, pgr.deploy_yn, pgr.export_yn " +
            " FROM project p, project_group_role pgr, workspace_group_role wgr, role r " +
            " where p.project_id = pgr.project_id and pgr.workspace_group_role_id = wgr.workspace_group_role_id " +
            " and wgr.role_id = r.role_id and r.role_id = :role_id and p.delete_yn = '0'";

    private static final String SELECT_PROGRAM_TYPE_ALL = "SELECT * FROM role_code WHERE role_code_type = :role_code_type";

    private static final String SELECT_PROGRAM_TYPE_ID = "SELECT * FROM role_code WHERE role_code_id = :role_code_id";

    private static final String DELETE_BY_ID = "update project set delete_yn = '1'  WHERE project_id = :project_id";

    // private static final String UPDATE_BY_NAME = "UPDATE build_project SET project_name = :project_name, description = :description, updated_date = NOW() where  id = :id";

    private static final String UPDATE_BY_NAME = "UPDATE project SET project_name = :project_name, description = :description, updated_date = NOW() where  project_id = :project_id";

    private static final String UPDATE_BY_VERSION = "UPDATE build_project SET template_version = :template_version, updated_date = NOW() where  id = :id";

    private static final String UPDATE_BY_PROJECTDIRNAME = "UPDATE project SET project_dir_path = :project_dir_path, updated_date = NOW() where project_id = :project_id";

    private static final String SELECT_BY_WORKSPACE_ID_PROJECT_ALL = "select project_id, workspace_id, (select w.workspace_name from workspace w where w.workspace_id = p.workspace_id) as workspace_name, project_name from project p where p.workspace_id = :workspace_id and p.delete_yn = '0'";

    private static final String SELECT_BY_WORKSPACE_ID_PROJECT_CHECKED_ALL = "select pg.project_group_role_id, pg.workspace_group_role_id, pg.project_id, wg.workspace_id, (select workspace_name from workspace where workspace_id = wg.workspace_id ) as workspace_name, p.project_name, pg.read_yn, pg.update_yn, pg.delete_yn, pg.build_yn, pg.deploy_yn, pg.export_yn " +
            " from project_group_role pg, workspace_group_role wg, project p where pg.project_id = p.project_id"+
            " and pg.workspace_group_role_id = :workspace_group_role_id " +
            " and pg.workspace_group_role_id = wg.workspace_group_role_id" +
            " and p.workspace_id = :workspace_id" +
            " and p.delete_yn = '0'";

    private static final String SELECT_BY_PROJECT_ALL = "select project_id, workspace_id, (select w.workspace_name from workspace w where w.workspace_id = p.workspace_id) as workspace_name, project_name from project p where p.delete_yn = '0'";

    private static final String SELECT_BY_WORKSPACE_GROUP_ID_BY_PROJECT_ALL = "select pg.project_group_role_id, pg.workspace_group_role_id, pg.project_id, wg.workspace_id, (select workspace_name from workspace where workspace_id = wg.workspace_id ) as workspace_name, p.project_name, pg.read_yn, pg.update_yn, pg.delete_yn, pg.build_yn, pg.deploy_yn, pg.export_yn " +
            "from project_group_role pg, workspace_group_role wg, project p where pg.project_id = p.project_id and pg.workspace_group_role_id = :workspace_group_role_id and pg.workspace_group_role_id = wg.workspace_group_role_id";

    private static final String SELCT_LAST_INSERT_COUNT = "SELECT LAST_INSERT_ID()";

    private static final String SELECT_BY_PROJECT_NAME_CHECK_BY_ID = "SELECT * FROM project where project_id = :project_id and project_name = :project_name";

    public void insert(BuildProject buildProject){
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(buildProject));
    }

    @Override
    public BuildProject findById(Long id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("project_id", id),
                new BeanPropertyRowMapper<>(BuildProject.class));
    }

    @Override
    public BuildProject findByProjectName(String projectName) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_PROEJECT_NAME_CHECK,
                    new MapSqlParameterSource("project_name", projectName),
                    // new MapSqlParameterSource("project_name", projectName).addValue("domain_id", domain_id).addValue("member_id", member_id),
                    new BeanPropertyRowMapper<>(BuildProject.class));

        } catch (EmptyResultDataAccessException e) {
                
            // log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;
        }

    }

    @Override
    public BuildProject findByProjectNameCheckedID(Long project_id, String project_name) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_PROJECT_NAME_CHECK_BY_ID,
                    new MapSqlParameterSource("project_id", project_id).addValue("project_name", project_name),
                    new BeanPropertyRowMapper<>(BuildProject.class));
        } catch (EmptyResultDataAccessException e) {

            // log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;
        }
    }

    public List<BuildProject> findAll(){
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(BuildProject.class));
    }

    @Override
    public List<BuildProject> findAllAdminProject(Long user_id) {
        return namedParameterJdbcTemplate.query(SELECT_ADMIN_ALL, new MapSqlParameterSource("user_id", user_id), new BeanPropertyRowMapper<>(BuildProject.class));
    }

    @Override
    public List<ProjectGroupRoleVo> findAllProject() {
        return namedParameterJdbcTemplate.query(SELECT_BY_PROJECT_ALL, new BeanPropertyRowMapper<>(ProjectGroupRoleVo.class));
    }

    @Override
    public List<ProjectGroupRoleVo> findAllProjectByRoleID(int role_id) {
        return namedParameterJdbcTemplate.query(SELECT_PROJECT_ALL_BY_ROLE_ID, new MapSqlParameterSource("role_id", role_id), new BeanPropertyRowMapper<>(ProjectGroupRoleVo.class));
    }

    @Override
    public List<BuildProject> findAllProjectFree(Long user_id) {
        return namedParameterJdbcTemplate.query(SELECT_FREE_TIRE_ALL, new MapSqlParameterSource("user_id", user_id), new BeanPropertyRowMapper<>(BuildProject.class));
    }

    @Override
    public List<BuildProject> findAllProjectProfessional(Long user_id) {
        return namedParameterJdbcTemplate.query(SELECT_PROFESSIONAL_TIRE_ALL, new MapSqlParameterSource("user_id", user_id), new BeanPropertyRowMapper<>(BuildProject.class));
    }

    @Override
    public List<BuildProject> findProjectGroupAll(int role_id) {
        return namedParameterJdbcTemplate.query(SELECT_PROJECT_GROUP_ALL, new MapSqlParameterSource("role_id", role_id), new BeanPropertyRowMapper<>(BuildProject.class));
    }

    // SELECT_PROJECT_GROUP_ALL


    @Override
    public List<ProjectGroupRoleVo> findByWorkspaceID(int workspace_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_ID_PROJECT_ALL, new MapSqlParameterSource("workspace_id", workspace_id), new BeanPropertyRowMapper<>(ProjectGroupRoleVo.class));
    }

    @Override
    public List<ProjectGroupRoleVo> findByWorkspaceProjectCheckedAll(int workspace_id, int workspace_group_role_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_ID_PROJECT_CHECKED_ALL, new MapSqlParameterSource("workspace_id", workspace_id).addValue("workspace_group_role_id",workspace_group_role_id), new BeanPropertyRowMapper<>(ProjectGroupRoleVo.class));
    }

    @Override
    public List<ProjectGroupRole> findByWorkspaceGroupID(int workspace_group_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_WORKSPACE_GROUP_ID_BY_PROJECT_ALL, new MapSqlParameterSource("workspace_group_role_id", workspace_group_id), new BeanPropertyRowMapper<>(ProjectGroupRole.class));
    }

    @Override
    public List<PlatformSelectAll> selectPlatformFIndAll(String role_code_type) {
        try {
            return namedParameterJdbcTemplate.query(SELECT_PROGRAM_TYPE_ALL,
                    new MapSqlParameterSource("role_code_type", role_code_type),
                    new BeanPropertyRowMapper<>(PlatformSelectAll.class));

        } catch (EmptyResultDataAccessException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;
        }
    }

    @Override
    public List<ProgrammingSelectAll> selectProgramFindAll(String role_code_type) {
        try {
            return namedParameterJdbcTemplate.query(SELECT_PROGRAM_TYPE_ALL,
                    new MapSqlParameterSource("role_code_type", role_code_type),
                    new BeanPropertyRowMapper<>(ProgrammingSelectAll.class));

        } catch (EmptyResultDataAccessException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;
        }
    }

    @Override
    public ProgrammingSelectAll selectProgramFindOne(String role_code_id) {
        try{
            return namedParameterJdbcTemplate.queryForObject(SELECT_PROGRAM_TYPE_ID,
                    new MapSqlParameterSource("role_code_id", role_code_id),
                    new BeanPropertyRowMapper<>(ProgrammingSelectAll.class));

        } catch (EmptyResultDataAccessException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;

        }
    }

    public BuildProject findByWorkspaceId(Long id, String name){
        try{
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_WORKSPACE_ID,
                    new MapSqlParameterSource("workspace_id", id).addValue("project_name",name),
                    new BeanPropertyRowMapper<>(BuildProject.class));

        } catch (EmptyResultDataAccessException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;

        }
    }

    public void updateBuildProject(BuildProject buildProject){
        namedParameterJdbcTemplate.update(UPDATE_BY_NAME, new BeanPropertySqlParameterSource(buildProject));
    }

    @Override
    public void updateBuildVersionProject(BuildProject buildProject) {
        namedParameterJdbcTemplate.update(UPDATE_BY_VERSION, new BeanPropertySqlParameterSource(buildProject));
    }

    @Override
    public void updateBuildProjectDirPath(BuildProject buildProject) {
        namedParameterJdbcTemplate.update(UPDATE_BY_PROJECTDIRNAME, new BeanPropertySqlParameterSource(buildProject));
    }

    @Override
    public int findByProjectCount() {
        return namedParameterJdbcTemplate.queryForObject(SELCT_LAST_INSERT_COUNT, new MapSqlParameterSource("id", 0),Integer.class);
    }

    public void deleteById(Long id){
        namedParameterJdbcTemplate.update(DELETE_BY_ID, new MapSqlParameterSource("project_id", id));
    }


}
