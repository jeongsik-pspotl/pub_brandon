package com.inswave.whive.common.buildhistory;

import com.inswave.whive.common.buildsetting.BuildSetting;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class BuildHistoryDaoImpl extends NamedParameterJdbcDaoSupport implements BuildHistoryDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO project_history(project_id, platform, status, status_log," +
            "log_path ,logfile_name , platform_build_file_path, platform_build_file_name," +
            " result, project_name, qrcode, history_started_date, history_ended_date, build_number, ios_builded_target_or_bundle_id) VALUES(:project_id, :platform, :status, :status_log, " +
            " :log_path ,:logfile_name , :platform_build_file_path, :platform_build_file_name, " +
            " :result, :project_name, :qrcode, NOW(), NOW(), '', :ios_builded_target_or_bundle_id)";

    private static final String SELECT_ALL = "SELECT bh.project_history_id, bh.project_id ,bp.project_name ,bh.platform " +
            "            ,bh.status ,bh.status_log ,bh.log_path ,bh.result ,bh.platform_build_file_path, bh.platform_build_file_name " +
            "            ,bh.qrcode ,bh.history_started_date ,bh.history_ended_date " +
            "             FROM project_history AS bh JOIN project AS bp JOIN workspace w on bp.workspace_id = w.workspace_id WHERE bp.project_id = bh.project_id and w.member_id = :user_id ORDER BY bh.project_history_id DESC;";

    private static final String UPDATE_HISOTRY = "UPDATE project_history SET  platform = :platform, " +
            "status_log = :status_log, log_path = :log_path, platform_build_file_path = :platform_build_file_path, status = :status " +
            ",qrcode = :qrcode, result = :result, project_name = :project_name, logfile_name = :logfile_name" +
            ",platform_build_file_name = :platform_build_file_name, history_ended_date = NOW(), build_number = :build_number WHERE project_history_id = :project_history_id";

    private static final String SELECT_BY_ID = "SELECT * FROM project_history WHERE project_id = :project_id AND platform = :platform AND project_name = :project_name ORDER BY project_history_id DESC";

    private static final String SELECT_BY_COUNT = "SELECT LAST_INSERT_ID()";

    private static final String SELECT_BY_HISTORY_ID = "SELECT * FROM build_history WHERE build_id = :build_id AND id = :build_history_id";

    private static final String SELECT_BY_HISTORY_ID_ONE = "SELECT * FROM project_history WHERE project_history_id = :project_history_id";

    private static final String DELETE_HISTORY = "DELETE from build_history WHERE build_id = :build_id";

    private static final String UPDATE_HISTORY_STATUS_LOG = "UPDATE project_history SET status_log = :status_log, history_ended_date = :history_ended_date WHERE project_history_id = :project_history_id";

    @Override
    public void insert(BuildHistory buildHistory) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(buildHistory));
    }

    @Override
    public List<BuildHistory> findById(Long project_id, String platform, String project_name) {
        return namedParameterJdbcTemplate.query(SELECT_BY_ID,
                new MapSqlParameterSource("project_id", project_id).addValue("platform",platform).addValue("project_name",project_name),
                new BeanPropertyRowMapper<>(BuildHistory.class));
    }

    @Override
    public BuildHistory findByHistoryId(Integer build_history_id, Integer build_id){
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_HISTORY_ID,
                new MapSqlParameterSource("build_history_id", build_history_id).addValue("build_id",build_id),
                new BeanPropertyRowMapper<>(BuildHistory.class));
    }

    @Override
    public BuildHistory findByHistoryIdOne(Long project_history_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_HISTORY_ID_ONE,
                new MapSqlParameterSource("project_history_id", project_history_id),
                new BeanPropertyRowMapper<>(BuildHistory.class));
    }

    @Override
    public int findByHistoryCount() {
        // return namedParameterJdbcTemplate.queryForObject(SELECT_BY_COUNT, null, new BeanPropertyRowMapper());
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_COUNT, new MapSqlParameterSource("id", 0),Integer.class);
    }

    @Override
    public void update(BuildHistory buildHistory) {
        namedParameterJdbcTemplate.update(UPDATE_HISOTRY, new BeanPropertySqlParameterSource(buildHistory));
    }

    @Override
    public List<BuildHistory> findAll(Long user_id) {

        return namedParameterJdbcTemplate.query(SELECT_ALL, new MapSqlParameterSource("user_id", user_id),new BeanPropertyRowMapper<>(BuildHistory.class));
    }

    @Override
    public void deleteById(Long build_id) {
        namedParameterJdbcTemplate.update(DELETE_HISTORY, new MapSqlParameterSource("build_id", build_id));
    }

    @Override
    public void updateStatusLog(BuildHistory buildHistory) {
        namedParameterJdbcTemplate.update(UPDATE_HISTORY_STATUS_LOG, new BeanPropertySqlParameterSource(buildHistory));
    }
}
