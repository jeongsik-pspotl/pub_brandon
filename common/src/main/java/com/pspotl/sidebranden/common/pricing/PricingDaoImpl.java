package com.pspotl.sidebranden.common.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class PricingDaoImpl extends NamedParameterJdbcDaoSupport implements  PricingDao{

    private static final String INSERT_SQL = "INSERT INTO pricing(user_id, order_id, customer_uid, imp_uid, pay_type_cd, pay_change_yn, pricing_etc1, pricing_etc2, create_date, update_date) " +
            "VALUES(:user_id, :order_id, :customer_uid, :imp_uid, :pay_type_cd,  :pay_change_yn, NULL, NULL, NOW(), NOW())";

    private static final String UPDATE_SQL = "UPDATE pricing SET order_id = :order_id, customer_uid = :customer_uid, pay_type_cd = :pay_type_cd,  pay_change_yn = :pay_change_yn, imp_uid = :imp_uid, update_date = NOW() WHERE pricing_id = :pricing_id";

    private static final String UPDATE_PASSWORD_RESET_SQL = "UPDATE user SET password = :password, updated_date = NOW() where email = :email";

    private static final String UPDATE_PASSWORD_USERID_RESET_SQL = "UPDATE user SET password = :password, updated_date = NOW() where user_id = :user_id";

    private static final String UPDATE_SECSSION_YN_SQL = "UPDATE user SET user_role = :user_role, updated_date = NOW() where email = :email";

    private static final String SELECT_BY_ID = "SELECT * FROM pricing WHERE user_id = :user_id";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insert(Pricing pricing) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(pricing));
    }

    @Override
    public Pricing findById(Long user_id) {

        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                    new MapSqlParameterSource("user_id", user_id),
                    BeanPropertyRowMapper.newInstance(Pricing.class));

        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public void update(Pricing pricing) {
        namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(pricing));
    }
}
