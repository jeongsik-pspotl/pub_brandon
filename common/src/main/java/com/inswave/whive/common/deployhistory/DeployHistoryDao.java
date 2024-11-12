package com.inswave.whive.common.deployhistory;

import java.util.List;

public interface DeployHistoryDao {

    void insert(DeployHistory deployHistory);

    DeployHistory findByID(DeployHistory deployHistory);

    List<DeployHistory> findByIDList(Long projectID);

    DeployHistory findByIdOne(Long deployHistoryId);

    int findByDeployHistoryCount();

    void update(DeployHistory deployHistory);
}
