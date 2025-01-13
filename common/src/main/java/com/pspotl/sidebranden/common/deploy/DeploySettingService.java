package com.pspotl.sidebranden.common.deploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeploySettingService {

    @Autowired
    private DeploySettingDaoImpl deploySettingDaoImpl;

    @Transactional
    public void insert (Deploy deploy){
        deploy.setCreated_date(LocalDateTime.now());
        deploy.setUpdated_date(LocalDateTime.now());
        deploySettingDaoImpl.insert(deploy);

    }

    @Transactional
    public Deploy findById (Long build_id){
        return deploySettingDaoImpl.findById(build_id);
    }

    public void update (Deploy deploy){

    }

}
