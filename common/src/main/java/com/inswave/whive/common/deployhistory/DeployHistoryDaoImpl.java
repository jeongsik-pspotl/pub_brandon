package com.inswave.whive.common.deployhistory;

import com.inswave.whive.common.buildhistory.BuildHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class DeployHistoryDaoImpl extends NamedParameterJdbcDaoSupport implements DeployHistoryDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "insert into deploy_history(project_id, deploy_id, status_log, log_path, result, logfile_name, deploy_start_date, deploy_end_date) " +
            "values (:project_id, :deploy_id, :status_log, :log_path, :result, :logfile_name, NOW(), NOW()) ";

    private static final String SELECT_BY_BUILD_ID = "select * from deploy_history where deploy_history_id = :deploy_history_id";

    private static final String SELECT_BY_DEPLOY_LIST = "select * from deploy_history where project_id = :project_id ORDER BY deploy_history_id DESC";

    private static final String SELECT_BY_COUNT = "SELECT LAST_INSERT_ID()";

    private static final String UPDATE_SQL = "update deploy_history set result = :result, log_path = :log_path, logfile_name = :logfile_name, deploy_end_date = NOW() where deploy_history_id = :deploy_history_id ";

    @Override
    public void insert(DeployHistory deployHistory) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(deployHistory));
    }

    @Override
    public DeployHistory findByID(DeployHistory deployHistory) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_BUILD_ID,
                new MapSqlParameterSource("deploy_history_id", deployHistory.getDeploy_history_id()),
                new BeanPropertyRowMapper<>(DeployHistory.class));
    }

    @Override
    public List<DeployHistory> findByIDList(Long projectID) {
        return namedParameterJdbcTemplate.query(SELECT_BY_DEPLOY_LIST,
                new MapSqlParameterSource("project_id", projectID),
                new BeanPropertyRowMapper<>(DeployHistory.class));
    }

    @Override
    public DeployHistory findByIdOne(Long deployHistoryId) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_BUILD_ID,
                new MapSqlParameterSource("deploy_history_id", deployHistoryId),
                new BeanPropertyRowMapper<>(DeployHistory.class));
    }

    @Override
    public int findByDeployHistoryCount() {
        // return namedParameterJdbcTemplate.queryForObject(SELECT_BY_COUNT, null, new BeanPropertyRowMapper());
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_COUNT, new MapSqlParameterSource("id", 0),Integer.class);
    }

    @Override
    public void update(DeployHistory deployHistory) {
        namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(deployHistory));
    }
}
