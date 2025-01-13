package com.pspotl.sidebranden.common.vcssetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class VCSSettingDaoImpl extends NamedParameterJdbcDaoSupport implements VCSSettingDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO vcs_settings(vcs_type,vcs_url,vcs_user_id,vcs_user_pwd,create_date, update_date, vcs_name, admin_id) " +
            "VALUES(:vcs_type,:vcs_url,:vcs_user_id,:vcs_user_pwd,NOW(), NOW(), :vcs_name, :admin_id)";

    private static final String SELECT_ALL = "SELECT * FROM vcs_settings";

    private static final String SELECT_ALL_ADMIN = "SELECT * FROM vcs_settings where admin_id = :user_id";

    private static final String SELECT_BY_SELECTBOX = "SELECT vcs_id, vcs_name FROM vcs_settings";

    private static final String SELECT_BY_SELECTBOX_ID = "SELECT vcs_id, vcs_name FROM vcs_settings where vcs_id = :vcs_id";

    private static final String SELECT_BY_SELECTBOX_TYPE = "SELECT vcs_id, vcs_name FROM vcs_settings where vcs_type = :vcs_type";

    private static final String SELECT_BY_SELECTBOX_ADMINID = "SELECT vcs_id, vcs_name FROM vcs_settings where admin_id = :admin_id";

    private static final String SELECT_BY_ID = "SELECT * FROM vcs_settings where vcs_id = :vcs_id";

    private static final String UPDATE_BY_ALL = "UPDATE vcs_settings SET vcs_user_id = :vcs_user_id, vcs_user_pwd = :vcs_user_pwd , vcs_user_id = :vcs_user_id, vcs_url = :vcs_url, update_date = NOW() WHERE vcs_id =:vcs_id";

    private static final String SELECT_BY_VCS_NAME = "SELECT * FROM vcs_settings where vcs_name = :vcs_name";

    private static final String SELECT_BY_ADMIN_VCS_TYPE = "SELECT * FROM vcs_settings " +
                                                            "WHERE admin_id = :admin_id " +
                                                            "AND (" +
                                                                "CASE WHEN INSTR(:vcs_type, ',') > 0 THEN vcs_type IN (SUBSTRING_INDEX(:vcs_type, ',', 1), SUBSTRING_INDEX(:vcs_type, ',', -1)) " +
                                                                    "WHEN INSTR(:vcs_type, 'git') > 0 THEN vcs_type = SUBSTRING_INDEX(:vcs_type, ',', 1) " +
                                                                    "ELSE vcs_type = SUBSTRING_INDEX(:vcs_type, ',', -1) " +
                                                                "END" +
                                                            ")";

    @Override
    public void insert(VCSSetting vcsSetting) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(vcsSetting));
    }

    @Override
    public void update(VCSSetting vcsSetting) {
        namedParameterJdbcTemplate.update(UPDATE_BY_ALL, new BeanPropertySqlParameterSource(vcsSetting));
    }

    @Override
    public List<VCSSetting> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(VCSSetting.class));
    }

    @Override
    public List<VCSSetting> findAdminAll(Long user_id) {
        return namedParameterJdbcTemplate.query(SELECT_ALL_ADMIN,
                new MapSqlParameterSource("user_id", user_id), new BeanPropertyRowMapper<>(VCSSetting.class));
    }

    @Override
    public VCSSetting findByID(Long vcs_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("vcs_id", vcs_id),
                new BeanPropertyRowMapper<>(VCSSetting.class));
    }

    @Override
    public List<VCSSelectBoxList> findBySelectList() {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX, new BeanPropertyRowMapper<>(VCSSelectBoxList.class));
    }

    @Override
    public VCSSelectBoxList findBySelectListVcsID(Long vcs_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_SELECTBOX_ID,
                new MapSqlParameterSource("vcs_id", vcs_id),
                new BeanPropertyRowMapper<>(VCSSelectBoxList.class));
    }

    @Override
    public List<VCSSelectBoxList> findBySelectListAdminID(Long admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_ADMINID, new MapSqlParameterSource("admin_id", admin_id),new BeanPropertyRowMapper<>(VCSSelectBoxList.class));
    }

    @Override
    public VCSSetting findByVCSName(String vcs_name) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_VCS_NAME,
                    new MapSqlParameterSource("vcs_name", vcs_name),
                    new BeanPropertyRowMapper<>(VCSSetting.class));

        } catch (EmptyResultDataAccessException e) {

            return null;
        }

    }

    @Override
    public List<VCSSelectBoxList> findBySelectListType(String vcs_type) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_TYPE
                , new MapSqlParameterSource("vcs_type", vcs_type)
                ,new BeanPropertyRowMapper<>(VCSSelectBoxList.class));
    }

    @Override
    public List<VCSSelectBoxList> findBySelectListAdminVcsType(Long admin_id, String vcs_type) {
        return namedParameterJdbcTemplate.query(SELECT_BY_ADMIN_VCS_TYPE
                , new MapSqlParameterSource("admin_id", admin_id).addValue("vcs_type", vcs_type)
                , new BeanPropertyRowMapper<>(VCSSelectBoxList.class));
    }


}
