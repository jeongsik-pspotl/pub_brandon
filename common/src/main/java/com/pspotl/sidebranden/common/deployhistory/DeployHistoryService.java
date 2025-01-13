package com.pspotl.sidebranden.common.deployhistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeployHistoryService {

    @Autowired
    DeployHistoryDaoImpl deployHistoryDao;

    @Transactional
    public int insert(DeployHistory deployHistory){
        deployHistory.setDeploy_start_date(LocalDateTime.now());
        deployHistoryDao.insert(deployHistory);
        return deployHistoryDao.findByDeployHistoryCount();
    }

    @Transactional
    public DeployHistory findByID(DeployHistory deployHistory){
        return deployHistoryDao.findByID(deployHistory);
    }

    @Transactional
    public int findByDeployHistoryCount(){
        return deployHistoryDao.findByDeployHistoryCount();
    }

    @Transactional
    public void update(DeployHistory deployHistory){
        deployHistoryDao.update(deployHistory);
    }

    @Transactional
    public List<DeployHistory> findByIDList(Long projectID) { return deployHistoryDao.findByIDList(projectID); }

    @Transactional
    public DeployHistory findByIdOne(Long deployHistoryId) { return deployHistoryDao.findByIdOne(deployHistoryId); }

}
