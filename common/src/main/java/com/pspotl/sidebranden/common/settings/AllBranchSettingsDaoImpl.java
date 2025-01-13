package com.pspotl.sidebranden.common.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
public class AllBranchSettingsDaoImpl extends NamedParameterJdbcDaoSupport implements AllBranchSettingsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "insert into all_branch_settings(branch_id, branch_name, session_status, session_type, last_date, create_date, update_date) \n" +
            "values (:branch_id, :branch_name, :session_status, :session_type, :last_date, :create_date, :update_date)";

    private static final String UPDATE_All_STATUS_N_SQL = "UPDATE all_branch_settings set last_date = :last_date, session_status = :session_status \n"
            +"where id <> 0";

    private static final String UPDATE_BY_STATUS_SQL = "UPDATE all_branch_settings set last_date = :last_date, session_status = :session_status \n" +
            "where id = :id";

    private static final String SELECT_BY_ID = "SELECT * FROM all_branch_settings WHERE branch_id = :branch_id";

    private static final String SELECT_BY_USER_ID = "SELECT * FROM all_branch_settings WHERE branch_user_id = :branch_user_id";

    @Override
    public void insert(AllBranchSettings allBranchSettings) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(allBranchSettings));
    }

    @Override
    public AllBranchSettings findByBranchIdOne(String branch_id) {
        try {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("branch_id", branch_id),
                new BeanPropertyRowMapper<>(AllBranchSettings.class));
        } catch(EmptyResultDataAccessException e){
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(AllBranchSettings allBranchSettings) {
        namedParameterJdbcTemplate.update(UPDATE_All_STATUS_N_SQL, new BeanPropertySqlParameterSource(allBranchSettings));
    }

    @Override
    public void updateBySessionStatus(AllBranchSettings allBranchSettings) {
        namedParameterJdbcTemplate.update(UPDATE_BY_STATUS_SQL, new BeanPropertySqlParameterSource(allBranchSettings));
    }

    @Override
    public AllBranchSettings findByBuilderUserIdOne(String id) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                    new MapSqlParameterSource("branch_user_id", id),
                    new BeanPropertyRowMapper<>(AllBranchSettings.class));
        } catch(EmptyResultDataAccessException e){
            e.printStackTrace();
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return null;
        }
    }
}
