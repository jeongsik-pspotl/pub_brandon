package com.inswave.whive.common.branchsetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

public class BranchSettingDaoImpl extends NamedParameterJdbcDaoSupport implements BranchSettingDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO builder_setting(role_code_id,builder_user_id,builder_name,session_status,session_type,created_date,updated_date, builder_url, builder_password) " +
            "VALUES(:role_code_id,:builder_user_id,:builder_name,:session_status,:session_type,NOW(),NOW(),:builder_url ,:builder_password)";

    private static final String UPDATE_SQL = "UPDATE builder_setting set builder_password = :builder_password where builder_id = :builder_id";

    private static final String SELECT_ALL = "SELECT * FROM builder_setting";

    private static final String SELECT_BY_SELECTBOX = "SELECT builder_id, builder_name FROM builder_setting";

    private static final String SELECT_BY_SELECTBOX_ID = "SELECT builder_id, builder_name FROM builder_setting where builder_id = :builder_id";

    private static final String SELECT_BY_ID = "SELECT * FROM builder_setting where builder_id = :builder_id";

    private static final String SELECT_BY_USER_ID = "SELECT * FROM builder_setting where builder_user_id = :builder_user_id";

    private static final String UPDATE_BY_STATUS_SQL = "UPDATE builder_setting set last_date = :last_date, session_status = :session_status " +
            "where builder_id = :builder_id";

    private static final String SELECT_BY_BRANCH_NAME = "SELECT * FROM builder_setting where builder_name = :builder_name";

    private static final String SELECT_BY_COUNT = "SELECT LAST_INSERT_ID()";

    @Override
    public void insert(BranchSetting branchSetting) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(branchSetting));
    }

    @Override
    public void update(BranchSetting branchSetting) {
        namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(branchSetting));
    }

    @Override
    public List<BranchSetting> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(BranchSetting.class));
    }

    @Override
    public BranchSetting findByID(Long branch_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("builder_id", branch_id),
                new BeanPropertyRowMapper<>(BranchSetting.class));
    }

    @Override
    public List<BuilderSelectBoxList> findBySelectBOXLIst() {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX, new BeanPropertyRowMapper<>(BuilderSelectBoxList.class));
    }

    @Override
    public BuilderSelectBoxList findBySelectBOXListByID(Long builder_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_SELECTBOX_ID,
                new MapSqlParameterSource("builder_id", builder_id),
                new BeanPropertyRowMapper<>(BuilderSelectBoxList.class));
    }

    @Override
    public BranchSetting findByUserID(String branch_user_id) {

        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_USER_ID,
                    new MapSqlParameterSource("builder_user_id", branch_user_id),
                    new BeanPropertyRowMapper<>(BranchSetting.class));

        } catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    @Override
    public void updateByStatus(BranchSetting branchSetting){
        namedParameterJdbcTemplate.update(UPDATE_BY_STATUS_SQL, new BeanPropertySqlParameterSource(branchSetting));
    }

    @Override
    public BranchSetting findByBranchName(String branch_name) {
        try{
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_BRANCH_NAME,
                    new MapSqlParameterSource("builder_name", branch_name),
                    new BeanPropertyRowMapper<>(BranchSetting.class));
        } catch (EmptyResultDataAccessException e){
            return null;
        }


    }

    @Override
    public int findByBuilderID() {
        // return namedParameterJdbcTemplate.queryForObject(SELECT_BY_COUNT, null, new BeanPropertyRowMapper());
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_COUNT, new MapSqlParameterSource("id", 0),Integer.class);
    }


}
