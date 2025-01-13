package com.pspotl.sidebranden.common.menu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

/**
 * MenuDao를 구현한 MenuDaoImpl
 *
 * @soorink
 */

@Slf4j
public class MenuDaoImpl extends NamedParameterJdbcDaoSupport implements MenuDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String SELECT_BY_ALL = "SELECT * FROM menu";

    public static final String SELECT_BY_ID = "SELECT * FROM menu WHERE menu_id = :menu_id";

    public static final String SELECT_BY_MENUPAGENAME = "SELECT * FROM menu WHERE menu_page_name = :menu_page_name";

    public static final String SELECT_BY_PROFILE = "SELECT * FROM menu WHERE menu_profile_type = :menu_profile_type";

    public static final String SELECT_BY_ROLE = "SELECT * FROM menu WHERE menu_role_type = :menu_role_type";

    public static final String SELECT_BY_ROLE_AND_PROFILE_AND_LANG = "SELECT * FROM menu WHERE (menu_role_type = :menu_role_type OR menu_role_type = 'all') AND menu_lang_type = :menu_lang_type AND (menu_profile_type = :menu_profile_type OR menu_profile_type = 'all')";

    public static final String SELECT_BY_PAY_AND_PROFILE_AND_LANG = "SELECT * FROM menu WHERE (menu_pay_type <= :menu_pay_type ) AND menu_lang_type = :menu_lang_type";

    public static final String INSERT = "INSERT INTO menu(menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_lang_type, text_label, `key`, depth, icon, create_date, update_date)" +
        "VALUES(:menu_page_name, :menu_code, :menu_profile_type, :menu_role_type, :menu_lang_type, :text_label, :key, :depth, :icon, :create_date, :update_date)";

    @Override
    public List<Menu> findByAll() {
        return namedParameterJdbcTemplate.query(SELECT_BY_ALL, new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public Menu findById(Integer id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
            new MapSqlParameterSource("member_id", id),
            new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public Menu findByMenupagename(String name) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_MENUPAGENAME,
            new MapSqlParameterSource("menu_page_name", name),
            new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public List<Menu> findByProfile(String profile) {
        return namedParameterJdbcTemplate.query(SELECT_BY_PROFILE,
            new MapSqlParameterSource("menu_profile_type", profile),
            new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public List<Menu> findByRole(String role) {
        return namedParameterJdbcTemplate.query(SELECT_BY_ROLE,
            new MapSqlParameterSource("menu_role_type", role),
            new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public List<Menu> findByRoleAndProfileAndLang(String role, String profile, String lang) {
        if (role.contains("SUPERADMIN")) {
            role = "ADMIN";
        }
        MapSqlParameterSource param = new MapSqlParameterSource();
        if (role.contains("ADMIN")) {
            role = "ADMIN";
        }
        param.addValue("menu_role_type", role);
        param.addValue("menu_profile_type", profile);
        param.addValue("menu_lang_type", lang);

        return namedParameterJdbcTemplate.query(SELECT_BY_ROLE_AND_PROFILE_AND_LANG, param, new BeanPropertyRowMapper<>(Menu.class));
    }

    @Override
    public List<Menu> findByPayAndProfileAndLang(String pay, String profile, String lang) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("menu_pay_type", pay);
        param.addValue("menu_profile_type", profile);
        param.addValue("menu_lang_type", lang);

        return namedParameterJdbcTemplate.query(SELECT_BY_PAY_AND_PROFILE_AND_LANG, param, new BeanPropertyRowMapper<>(Menu.class));

    }

    @Override
    public void insert(Menu menu) {
        int update = namedParameterJdbcTemplate.update(INSERT, new BeanPropertySqlParameterSource(menu));
    }
}
