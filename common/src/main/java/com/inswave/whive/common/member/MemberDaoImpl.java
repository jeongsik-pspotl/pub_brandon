package com.inswave.whive.common.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;

import java.util.List;


public class MemberDaoImpl extends NamedParameterJdbcDaoSupport implements MemberDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO member(email,password,username,name,team,company,enabled,accountNonLocked,accountNonExpired,credentialsNonExpired,created_date) VALUES(:email, :password, :username, :name, :team, :company, :enabled, :accountNonLocked, :accountNonExpired, :credentialsNonExpired, :created_date)";

    private static final String SELECT_BY_ID = "SELECT * FROM member WHERE id = :id";

    private static final String SELECT_BY_EMAIL = "SELECT * FROM user WHERE email = :email and user_role != 'NOUSER'";

    private static final String SELECT_ALL = "SELECT * FROM member";

    private static final String DELETE_BY_ID = "DELETE from member WHERE id = :id";

    private static final String UPDATE_SQL = "UPDATE member SET email = :email, password = :password, username = :username, team = :team, company = :company where  id = :id";

    private static final String UPDATE_BY_LOGOUT_SQL = "UPDATE user SET last_login_date = NOW() WHERE user_id = :user_id";

    @Override
    public void insert(Member user){
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(user));
    }

    @Override
    public void updateMember(long id, Member member){
        namedParameterJdbcTemplate.update(UPDATE_SQL, new MapSqlParameterSource("id", id)
                .addValue("email", member.getEmail())
                .addValue("password", member.getPassword())
                .addValue("username", member.getUsername())
                .addValue("name", member.getName())
                .addValue("team", member.getTeam())
                .addValue("company", member.getCompany())

        );
    }

    @Override
    public Member findById(Long id){
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("id", id),
                new BeanPropertyRowMapper<>(Member.class));
    }

    @Override
    public List<Member> findAll(){
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Member.class));
    }

    @Override
    public void deleteById(Long id){
        namedParameterJdbcTemplate.update(DELETE_BY_ID, new MapSqlParameterSource("id", id));
    }

    @Override
    public Member findByEmail(String email){
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_EMAIL,
                    new MapSqlParameterSource("email", email),
                    new BeanPropertyRowMapper<>(Member.class));
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public void updateByLogoutAndID(MemberLogin memberLogin) {
        namedParameterJdbcTemplate.update(UPDATE_BY_LOGOUT_SQL, new BeanPropertySqlParameterSource(memberLogin));
    }

}
