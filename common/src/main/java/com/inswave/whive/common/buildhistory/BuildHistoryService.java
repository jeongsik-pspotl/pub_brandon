package com.inswave.whive.common.buildhistory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BuildHistoryService {

    @Autowired
    private BuildHistoryDaoImpl buildHistoryDaoImpl;

    @Transactional
    public int insert(BuildHistory history) {
        history.setHistory_started_date(LocalDateTime.now());
        log.info("insert history {}", history);
        buildHistoryDaoImpl.insert(history);

        return buildHistoryDaoImpl.findByHistoryCount();
    }

    public Long update(BuildHistory history){
        history.setHistory_ended_date(LocalDateTime.now());
        log.info("update history {}", history);
        buildHistoryDaoImpl.update(history);

        return history.getProject_history_id();
    }

    @Transactional
    public List<BuildHistory> findById(Long build_id, String platform_type, String project_name) {
        return buildHistoryDaoImpl.findById(build_id, platform_type, project_name);
    }

    @Transactional
    public BuildHistory findByHistoryId(Integer id, Integer build_id) {
        return buildHistoryDaoImpl.findByHistoryId(id, build_id);
    }

    @Transactional
    public List<BuildHistory> findAll(Long user_id) {
        return buildHistoryDaoImpl.findAll(user_id);
    }

    @Transactional
    public void deleteById(Long id) {
        buildHistoryDaoImpl.deleteById(id);
    }

    @Transactional
    public BuildHistory findByHistoryIdOne(Long id){
        return buildHistoryDaoImpl.findByHistoryIdOne(id);
    }

    @Transactional
    public Long updateStatusLog(BuildHistory history) {
        history.setHistory_ended_date(LocalDateTime.now());
        buildHistoryDaoImpl.updateStatusLog(history);

        return history.getProject_history_id();
    }
}