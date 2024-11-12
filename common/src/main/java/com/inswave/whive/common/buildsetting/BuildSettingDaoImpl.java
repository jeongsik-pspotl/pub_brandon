package com.inswave.whive.common.buildsetting;

import com.inswave.whive.common.build.BuildProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class BuildSettingDaoImpl extends NamedParameterJdbcDaoSupport implements BuildSettingDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO project_setting(project_id, app_id, app_name, app_version, app_version_code," +
            " package_name, min_target_version, icon_image_path, zip_file_name, created_date,"+
            " updated_date, project_setting_etc1, project_setting_etc2, project_setting_etc3)"+
            " VALUES(:project_id, :app_id, :app_name, :app_version, :app_version_code, " +
            " :package_name, :min_target_version, :icon_image_path, :zip_file_name, NOW(), " +
            " NOW(), :project_setting_etc1, :project_setting_etc2, :project_setting_etc3)";

    private static final String SELECT_BY_ID = "SELECT * FROM build_setting WHERE id = :id";

    private static final String SELECT_BY_BUILD_ID = "SELECT * FROM project_setting WHERE project_id = :project_id";

    private static final String SELECT_BY_BUILDPROJECTID = "SELECT * FROM build_setting WHERE build_project_id = :build_project_id";

    private static final String SELECT_ALL = "SELECT * FROM build_setting";

    private static final String DELETE_BY_ID = "DELETE from build_setting WHERE build_id = :build_id";

    private static final String UPDATE_SETTING = "UPDATE build_setting SET  platform_type = :platform_type, target = :target" +
            ",app_id = :app_id, app_name = :app_name, app_version = :app_version" +
            ",app_version_code = :app_version_code, server_URL = :server_URL ,package_name = :package_name" +
            ",test_url = :test_url, updated_date = NOW() WHERE id = :id";

    private static final String UPDATE_APP_VERSION_SETTING = "UPDATE project_setting SET app_version_code = :app_version_code, updated_date = NOW() WHERE project_setting_id = :project_setting_id";

    public void insert(BuildSetting buildSetting){
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(buildSetting));
    }

    public BuildSetting findById(Long id){
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("id", id),
                new BeanPropertyRowMapper<>(BuildSetting.class));
    }

    @Override
    public BuildSetting findByBuildId(Long buildId) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_BUILD_ID,
                new MapSqlParameterSource("project_id", buildId),
                new BeanPropertyRowMapper<>(BuildSetting.class));
    }

    public void deleteById(Long build_id){
        namedParameterJdbcTemplate.update(DELETE_BY_ID, new MapSqlParameterSource("build_id", build_id));
    }

    public List<BuildSetting> findAll(){
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(BuildSetting.class));
    }

    public void updateBuildProject(BuildSetting buildSetting){
        namedParameterJdbcTemplate.update(UPDATE_SETTING, new BeanPropertySqlParameterSource(buildSetting));
    }

    @Override
    public void updateAppVersionProject(BuildSetting buildSetting) {
        namedParameterJdbcTemplate.update(UPDATE_APP_VERSION_SETTING, new BeanPropertySqlParameterSource(buildSetting));
    }
}
