package com.pspotl.sidebranden.common.buildhistory;

import java.util.List;

public interface BuildHistoryDao {
    void insert(BuildHistory buildHistory);

    List<BuildHistory> findById(Long build_id, String platform_type, String project_name);

    List<BuildHistory> findAll(Long user_id);

    void deleteById(Long build_id);

    BuildHistory findByHistoryId(Integer build_history_id, Integer build_id);

    BuildHistory findByHistoryIdOne(Long build_history_id);

    int findByHistoryCount();

    void update(BuildHistory buildHistory);

    void updateStatusLog(BuildHistory buildHistory);
}
