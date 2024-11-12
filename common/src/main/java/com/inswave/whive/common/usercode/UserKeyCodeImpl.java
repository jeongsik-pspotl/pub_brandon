package com.inswave.whive.common.usercode;

import com.inswave.whive.common.build.BuildProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class UserKeyCodeImpl extends NamedParameterJdbcDaoSupport implements UserKeyCodeDao{

    private static final String INSERT_SQL = "INSERT INTO user_key_code(user_key_email, user_key_code_value, user_key_expired_date) " +
            "VALUES(:user_key_email, :user_key_code_value, NOW())";

    private static final String SELECT_BY_KEY_CODE = "SELECT * FROM user_key_code WHERE user_key_email = :user_key_email and user_key_code_value = :user_key_code_value";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insert(UserKeyCode userKeyCode) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(userKeyCode));
    }

    @Override
    public UserKeyCode findByKeyCode(UserKeyCode userKeyCode) {
        List<UserKeyCode> userKeyCodeList = namedParameterJdbcTemplate.query(SELECT_BY_KEY_CODE, new BeanPropertySqlParameterSource(userKeyCode), new BeanPropertyRowMapper<>(UserKeyCode.class));

        if(userKeyCodeList.size() != 0){
            return userKeyCodeList.get(0);

        }else {
            return null;
        }


    }
}
