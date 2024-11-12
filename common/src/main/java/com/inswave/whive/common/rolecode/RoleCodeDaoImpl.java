package com.inswave.whive.common.rolecode;

import com.inswave.whive.common.branchsetting.BranchSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class RoleCodeDaoImpl extends NamedParameterJdbcDaoSupport implements RoleCodeDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_BY_ALL = "SELECT * FROM role_code";

    private static final String SELECT_BY_TYPE_LIST = "SELECT * FROM role_code WHERE role_code_type = :role_code_type AND role_code_id != '10'";


    @Override
    public List<RoleCode> findByCodeTypeList(String coodeType) {
        return namedParameterJdbcTemplate.query(SELECT_BY_TYPE_LIST,
                new MapSqlParameterSource("role_code_type", coodeType),
                new BeanPropertyRowMapper<>(RoleCode.class));
    }
}
