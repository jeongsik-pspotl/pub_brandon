package com.inswave.whive.common.builderqueue;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.member.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class BuilderQueueManagedgDaoImpl extends NamedParameterJdbcDaoSupport implements BuilderQueueManagedgDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO builder_queue_managed(builder_id, project_queue_cnt, build_queue_cnt, deploy_queue_cnt, etc_queue_cnt, project_queue_status_cnt, build_queue_status_cnt, deploy_queue_status_cnt, etc_queue_status_cnt, queue_etc_1, queue_etc_2, queue_etc_3, create_date) " +
            "VALUES(:builder_id,:project_queue_cnt, :build_queue_cnt, :deploy_queue_cnt, :etc_queue_cnt, :project_queue_status_cnt, :build_queue_status_cnt, :deploy_queue_status_cnt,:etc_queue_status_cnt, :queue_etc_1, :queue_etc_2, :queue_etc_3,  NOW())";

    private static final String BUILD_QUEUE_UPDATE_SQL = "UPDATE builder_queue_managed set build_queue_status_cnt = :build_queue_status_cnt where builder_id = :builder_id";

    private static final String PROJECT_QUEUE_UPDATE_SQL = "UPDATE builder_queue_managed set project_queue_status_cnt = :project_queue_status_cnt where builder_id = :builder_id";

    private static final String DEPLOY_QUEUE_UPDATE_SQL = "UPDATE builder_queue_managed set deploy_queue_status_cnt = :deploy_queue_status_cnt where builder_id = :builder_id";

    private static final String ETC_QUEUE_UPDATE_SQL = "UPDATE builder_queue_managed set etc_queue_status_cnt = :etc_queue_status_cnt where builder_id = :builder_id";

    private static final String BUILD_CLUSTER_ID_UPDATE_SQL = "UPDATE builder_queue_managed set queue_etc_1 = :queue_etc_1 where builder_id = :builder_id";

    private static final String SELECT_BY_ALL = "SELECT * FROM builder_queue_managed";

    private static final String SELECT_BY_CLUSTERID_ONE = "SELECT * FROM builder_queue_managed where builder_id = :builder_id";

    private static final String SELECT_BY_SELECTBOX = "SELECT builder_id, builder_name FROM builder_setting";

    private static final String SELECT_BY_SELECTBOX_ID = "SELECT builder_id, builder_name FROM builder_setting where builder_id = :builder_id";

    private static final String SELECT_BY_ID = "SELECT * FROM builder_queue_managed where builder_id = :builder_id";

    private static final String SELECT_BY_USER_ID = "SELECT * FROM builder_setting where builder_user_id = :builder_user_id";

    private static final String UPDATE_BY_STATUS_SQL = "UPDATE builder_setting set last_date = :last_date, session_status = :session_status " +
            "where builder_id = :builder_id";

    private static final String SELECT_BY_BRANCH_NAME = "SELECT * FROM builder_setting where builder_name = :builder_name";


    @Override
    public void insert(BuilderQueueManaged branchSetting) {
        namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(branchSetting));
    }

    @Override
    public void update(BuilderQueueManaged builderQueueManaged) {
        namedParameterJdbcTemplate.update(BUILD_QUEUE_UPDATE_SQL, new BeanPropertySqlParameterSource(builderQueueManaged));
    }

    @Override
    public void projectUpdate(BuilderQueueManaged builderQueueManaged) {
        namedParameterJdbcTemplate.update(PROJECT_QUEUE_UPDATE_SQL, new BeanPropertySqlParameterSource(builderQueueManaged));
    }

    @Override
    public void deployUpdate(BuilderQueueManaged builderQueueManaged) {
        namedParameterJdbcTemplate.update(DEPLOY_QUEUE_UPDATE_SQL, new BeanPropertySqlParameterSource(builderQueueManaged));
    }

    @Override
    public void etcUpdate(BuilderQueueManaged builderQueueManaged) {
        namedParameterJdbcTemplate.update(ETC_QUEUE_UPDATE_SQL, new BeanPropertySqlParameterSource(builderQueueManaged));
    }

    @Override
    public void clusterIdUpdate(BuilderQueueManaged builderQueueManaged) {
        namedParameterJdbcTemplate.update(BUILD_CLUSTER_ID_UPDATE_SQL, new BeanPropertySqlParameterSource(builderQueueManaged));
    }

    // BUILD_CLUSTER_ID_UPDATE_SQL

    @Override
    public BuilderQueueManaged findByID(Long builder_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("builder_id", builder_id),
                new BeanPropertyRowMapper<>(BuilderQueueManaged.class));
    }

    @Override
    public List<BuilderQueueManaged> findByAll() {
        return namedParameterJdbcTemplate.query(SELECT_BY_ALL, new BeanPropertyRowMapper<>(BuilderQueueManaged.class));
    }


}
