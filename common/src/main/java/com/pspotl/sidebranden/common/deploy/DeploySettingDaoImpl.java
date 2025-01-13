package com.pspotl.sidebranden.common.deploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DeploySettingDaoImpl extends NamedParameterJdbcDaoSupport implements DeploySettingDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "insert into deploy_setting(build_id, all_package_name, apple_key_id, apple_issuer_id, created_date , updated_date, all_signingkey_id) " +
            "values (:build_id, :all_package_name, :apple_key_id, :apple_issuer_id, NOW(), NOW(), :all_signingkey_id) ";

    private static final String SELECT_BY_BUILD_ID = "select * from deploy_setting where build_id = :build_id";

    private static final String UPDATE_BY_ALL = "update deploy_setting set  where deploy_id = :deploy_id  and build_id = :build_id";

    @Override
    public void insert(Deploy deploy) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(deploy));
    }

    @Override
    public Deploy findById(Long build_id) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_BUILD_ID,
                    new MapSqlParameterSource("build_id", build_id),
                    new BeanPropertyRowMapper<>(Deploy.class));

        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }
}
