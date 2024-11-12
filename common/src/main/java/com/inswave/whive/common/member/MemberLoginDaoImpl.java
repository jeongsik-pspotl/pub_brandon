package com.inswave.whive.common.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class MemberLoginDaoImpl extends NamedParameterJdbcDaoSupport implements MemberLoginDao {

    private static final String INSERT_SQL = "INSERT INTO user(domain_id, role_id, email, password, user_role, build_yn, created_date, updated_date, last_login_date," +
            " user_etc1, user_etc2, user_etc3, user_name, user_login_id, phone_number, appid_json) " +
            "VALUES(:domain_id, :role_id, :email, :password, :user_role, :build_yn, NOW(), NOW(), :last_login_date, " +
            ":user_etc1, :user_etc2, :user_etc3, :user_name, :user_login_id, :phone_number, :appid_json)";

    private static final String UPDATE_SQL = "UPDATE user SET domain_id = :domain_id, user_role = :user_role, role_id = :role_id, updated_date = NOW(), phone_number = :phone_number WHERE user_id = :user_id";

    private static final String UPDATE_PASSWORD_RESET_SQL = "UPDATE user SET password = :password, updated_date = NOW() where email = :email";

    private static final String UPDATE_PASSWORD_USERID_RESET_SQL = "UPDATE user SET password = :password, updated_date = NOW() where user_id = :user_id";

    private static final String UPDATE_SECSSION_YN_SQL = "UPDATE user SET user_role = :user_role, updated_date = NOW() where email = :email";

    private static final String SELECT_BY_LOGIN = "SELECT * FROM member WHERE email = :email AND password = :password";

    private static final String SELECT_BY_ALL = "SELECT * FROM user";

    private static final String SELECT_BY_EMAIL = "SELECT * FROM user WHERE email = :email and user_role != 'NOUSER'";

    private static final String SELECT_BY_ONLY_EMAIL = "SELECT user_id, u.domain_id, (select d.domain_name from domain d where d.domain_id = u.domain_id) as domain_name, role_id, email, password, user_role, build_yn, created_date, updated_date, last_login_date, user_name, u.user_login_id, u.user_etc1 FROM user u WHERE user_login_id = :user_login_id and user_role != 'NOUSER'";

    private static final String SELECT_BY_USER_LOGIN_ID_SQL = "SELECT * FROM user WHERE user_login_id = :user_login_id and user_role != 'NOUSER'";

    private static final String SELECT_BY_USER_PHONE_NUMBER_SQL = "SELECT * FROM user WHERE phone_number = :phone_number";

    private static final String SELECT_BY_ID = "SELECT u.user_id, email, user_role, user_name, domain_id, (select d.domain_name from domain d where d.domain_id = u.domain_id) as domain_name, created_date, updated_date, last_login_date, (SELECT r.role_name FROM role r where r.role_id = u.role_id ) as role_name, u.role_id,  u.user_role, u.user_login_id, u.phone_number, u.appid_json  FROM user as u WHERE user_id = :id";

    private static final String SELECT_BY_EMAIL_BEFORE_CANCEL = "SELECT u.user_id, email, user_role, user_name, domain_id, (select d.domain_name from domain d where d.domain_id = u.domain_id) as domain_name, created_date, updated_date, last_login_date, (SELECT r.role_name FROM role r where r.role_id = u.role_id ) as role_name, u.role_id,  u.user_role, u.user_login_id, u.phone_number  FROM user as u WHERE email = :email and user_role != 'NOUSER'";

    private static final String UPDATE_BY_BUILD_YN_SQL =  "UPDATE user SET build_yn = :build_yn WHERE user_login_id = :user_login_id";

    private static final String UPDATE_BY_EVENT_YN_SQL =  "UPDATE user SET user_etc1 = :event_yn, user_etc2 = :commnet WHERE user_id = :user_id";

    private static final String SELECT_BY_AMDIN_LIST = "SELECT user_id, user_name FROM user WHERE domain_id = :domain_id and user_role = 'ADMIN' ";

    private static final String SELECT_USER_PAY_CHECK = "SELECT COUNT(*) FROM user AS u LEFT OUTER JOIN pricing AS p ON u.user_id = p.user_id WHERE p.pay_change_yn = 'Y' AND u.user_id = :user_id and u.user_role != 'NOUSER'";

    private static final String UPDATE_BY_USER_APP_ID_SQL = "UPDATE user SET appid_json = :appid_json WHERE user_id = :user_id";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insert(MemberUserCreate memberUserCreate){
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(memberUserCreate));
    }

    @Override
    public MemberLogin findByLoginEmailAndPasswrod(MemberLogin memberLogin) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_LOGIN,
                    new BeanPropertySqlParameterSource(memberLogin),
                    BeanPropertyRowMapper.newInstance(MemberLogin.class));

        } catch (EmptyResultDataAccessException e) {
            return null;

        }

    }

    @Override
    public MemberLogin findByEmail(String email){
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_EMAIL,
                    new MapSqlParameterSource("email", email),
                    BeanPropertyRowMapper.newInstance(MemberLogin.class));

        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public MemberLogin findByOnlyEmail(String user_login_id) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ONLY_EMAIL,
                    new MapSqlParameterSource("user_login_id", user_login_id),
                    BeanPropertyRowMapper.newInstance(MemberLogin.class));

        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public MemberLogin findByUserLoginID(String user_login_id) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_USER_LOGIN_ID_SQL,
                    new MapSqlParameterSource("user_login_id", user_login_id),
                    BeanPropertyRowMapper.newInstance(MemberLogin.class));

        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public List<MemberAdminList> findByAdminList(Long doaminID) {
        return namedParameterJdbcTemplate.query(SELECT_BY_AMDIN_LIST, new MapSqlParameterSource("domain_id", doaminID), new BeanPropertyRowMapper<>(MemberAdminList.class));
    }

    @Override
    public MemberLogin findByUserPhoneNumberOne(String phoneNumber) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_USER_PHONE_NUMBER_SQL,
                    new MapSqlParameterSource("phone_number", phoneNumber),
                    BeanPropertyRowMapper.newInstance(MemberLogin.class));

        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public void update(MemberUserCreate memberUserCreate) {
        namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(memberUserCreate));
    }

    @Override
    public void updateBuildYn(String email, String build_yn) {
        namedParameterJdbcTemplate.update(UPDATE_BY_BUILD_YN_SQL, new MapSqlParameterSource("user_login_id",email).addValue("build_yn",build_yn));
    }

    @Override
    public void updatePasswordReset(MemberUserCreate memberUserCreate) {
        namedParameterJdbcTemplate.update(UPDATE_PASSWORD_RESET_SQL, new BeanPropertySqlParameterSource(memberUserCreate));
    }

    @Override
    public void updatePasswordUserIDReset(MemberUserCreate memberUserCreate) {
        namedParameterJdbcTemplate.update(UPDATE_PASSWORD_USERID_RESET_SQL, new BeanPropertySqlParameterSource(memberUserCreate));
    }

    @Override
    public void updateSecssionYn(String email, String secssion_yn) {
        MemberUserCreate memberUserCreate = new MemberUserCreate();
        memberUserCreate.setEmail(email);
        memberUserCreate.setUser_role(secssion_yn);

        namedParameterJdbcTemplate.update(UPDATE_SECSSION_YN_SQL, new BeanPropertySqlParameterSource(memberUserCreate));
    }

    @Override
    public void updateUserHiveEventCheckYn(Long user_id, String eventYn, String comment) {
        namedParameterJdbcTemplate.update(UPDATE_BY_EVENT_YN_SQL, new MapSqlParameterSource("user_id",user_id).addValue("event_yn",eventYn).addValue("commnet", comment));
    }

    @Override
    public void updateUserAppIDJSON(Long user_id, String appIDJson) {
        namedParameterJdbcTemplate.update(UPDATE_BY_USER_APP_ID_SQL, new MapSqlParameterSource("appid_json",appIDJson).addValue("user_id",user_id));
    }

    @Override
    public List<MemberLogin> findByAll() {
        return namedParameterJdbcTemplate.query(SELECT_BY_ALL, new BeanPropertyRowMapper<>(MemberLogin.class));
    }

    @Override
    public MemberDetail findById(Long id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("id", id),
                new BeanPropertyRowMapper<>(MemberDetail.class));
    }

    @Override
    public MemberDetail findByEmailBeforeCancel(String email) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_EMAIL_BEFORE_CANCEL,
                new MapSqlParameterSource("email", email),
                new BeanPropertyRowMapper<>(MemberDetail.class));
    }

    @Override
    public int payCheck(Long user_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_USER_PAY_CHECK,
                new MapSqlParameterSource("user_id", user_id), Integer.class);
    }
}
