package com.pspotl.sidebranden.common.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class DomainDaoImpl extends NamedParameterJdbcDaoSupport implements DomainDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_BY_ID = "SELECT * FROM domain WHERE domain_id = :domain_id";

    private static final String SELECT_BY_DOMAIN_LIST = "SELECT domain_id, domain_name FROM domain";

    private static final String SELECT_BY_DOMAIN_ALL = "SELECT * FROM domain";

    private static final String SELECT_BY_DOMAIN_NAME = "SELECT * FROM domain WHERE domain_name = :domain_name";

    private static final String  INSERT_SQL = "INSERT INTO domain(domain_name, create_date, updated_date) VALUES(:domain_name, NOW(), NOW())";

    private static final String UPDATE_SQL = "UPDATE domain SET domain_name = :domain_name, updated_date = NOW()  WHERE domain_id = :domain_id ";


    @Override
    public Domain findByID(Long domain_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("domain_id", domain_id),
                new BeanPropertyRowMapper<>(Domain.class));
    }

    @Override
    public List<Domain> findByDomainList() {
        return namedParameterJdbcTemplate.query(SELECT_BY_DOMAIN_LIST, new BeanPropertyRowMapper<>(Domain.class));
    }

    @Override
    public List<Domain> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_BY_DOMAIN_ALL, new BeanPropertyRowMapper<>(Domain.class));
    }

    @Override
    public Domain findByDomainName(String domain_name) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_BY_DOMAIN_NAME,
                    new MapSqlParameterSource("domain_name", domain_name),
                    new BeanPropertyRowMapper<>(Domain.class));
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    @Override
    public void createDomain(Domain domain) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(domain));
    }

    @Override
    public void updateDomain(Domain domain) {
        namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(domain));
    }


}
