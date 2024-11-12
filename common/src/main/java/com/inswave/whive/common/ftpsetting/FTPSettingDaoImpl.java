package com.inswave.whive.common.ftpsetting;

import com.inswave.whive.common.vcssetting.VCSSelectBoxList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class FTPSettingDaoImpl extends NamedParameterJdbcDaoSupport implements FTPSettingDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO ftp_settings(ftp_name,ftp_url,ftp_type,ftp_etc1,ftp_etc2,create_date, update_date, ftp_ip, ftp_port, ftp_user_id, ftp_user_pwd) " +
            "VALUES(:ftp_name,:ftp_url,:ftp_type,:ftp_etc1,:ftp_etc2,:create_date, :update_date, :ftp_ip, :ftp_port, :ftp_user_id, :ftp_user_pwd)";

    private static final String UPDAET_SQL = "UPDATE ftp_settings SET ftp_url = :ftp_url,  ftp_ip = :ftp_ip, ftp_port = :ftp_port, ftp_user_id = :ftp_user_id, ftp_user_pwd = :ftp_user_pwd " +
            " WHERE ftp_id = :ftp_id";

    private static final String SELECT_ALL = "SELECT * FROM ftp_settings";

    private static final String SELECT_BY_FTP_NAME = "SELECT * FROM ftp_settings where ftp_name = :ftp_name";

    private static final String SELECT_BY_SELECTBOX = "SELECT ftp_id, ftp_name FROM ftp_settings";

    private static final String SELECT_BY_SELECTBOX_ID = "SELECT ftp_id, ftp_name FROM ftp_settings where ftp_id = :ftp_id";

    private static final String SELECT_BY_ID = "SELECT * FROM ftp_settings where ftp_id = :ftp_id";

    @Override
    public void insert(FTPSetting ftpSetting) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(ftpSetting));
    }

    @Override
    public void update(FTPSetting ftpSetting) {
        namedParameterJdbcTemplate.update(UPDAET_SQL, new BeanPropertySqlParameterSource(ftpSetting));
    }

    @Override
    public List<FTPSetting> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(FTPSetting.class));
    }

    @Override
    public FTPSetting findByID(Long ftp_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("ftp_id", ftp_id),
                new BeanPropertyRowMapper<>(FTPSetting.class));
    }

    @Override
    public FTPSetting findByName(String ftp_name) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_FTP_NAME,
                    new MapSqlParameterSource("ftp_name", ftp_name),
                    new BeanPropertyRowMapper<>(FTPSetting.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<FTPSelectBoxList> findBySelectList() {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX, new BeanPropertyRowMapper<>(FTPSelectBoxList.class));
    }

    @Override
    public FTPSelectBoxList findBySelectListFTPID(Long ftp_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_SELECTBOX_ID,
                new MapSqlParameterSource("ftp_id", ftp_id),
                new BeanPropertyRowMapper<>(FTPSelectBoxList.class));
    }
}
