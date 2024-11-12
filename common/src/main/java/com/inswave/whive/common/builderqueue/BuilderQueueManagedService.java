package com.inswave.whive.common.builderqueue;

import com.inswave.whive.common.branchsetting.BuilderSelectBoxList;
import com.inswave.whive.common.enums.MessageString;
import com.inswave.whive.common.error.WHiveInvliadRequestException;
import com.inswave.whive.common.member.BCryptEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BuilderQueueManagedService {

    @Autowired
    private BuilderQueueManagedgDaoImpl builderQueueManagedgDao;

    @Transactional
    public void insert(BuilderQueueManaged branchSetting) {
        branchSetting.setCreate_date(LocalDateTime.now());
        branchSetting.setUpdate_date(LocalDateTime.now());
        builderQueueManagedgDao.insert(branchSetting);
    }

    @Transactional
    public void update(BuilderQueueManaged builderQueueManaged){
        builderQueueManagedgDao.update(builderQueueManaged);
    }

    @Transactional
    public void projectUpdate(BuilderQueueManaged builderQueueManaged){
        builderQueueManagedgDao.projectUpdate(builderQueueManaged);
    }

    @Transactional
    public void deployUpdate(BuilderQueueManaged builderQueueManaged){
        builderQueueManagedgDao.deployUpdate(builderQueueManaged);
    }

    @Transactional
    public void etcUpdate(BuilderQueueManaged builderQueueManaged){
        builderQueueManagedgDao.etcUpdate(builderQueueManaged);
    }

    @Transactional
    public void clusterIdUpdate(BuilderQueueManaged builderQueueManaged){
        builderQueueManagedgDao.clusterIdUpdate(builderQueueManaged);
    }


    @Transactional
    public BuilderQueueManaged findByID(Long builder_id){

        return builderQueueManagedgDao.findByID(builder_id);

    }

    @Transactional
    public List<BuilderQueueManaged> findByAll(){

        return builderQueueManagedgDao.findByAll();
    }


}
