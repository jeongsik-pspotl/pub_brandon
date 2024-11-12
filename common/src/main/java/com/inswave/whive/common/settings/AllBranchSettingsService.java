package com.inswave.whive.common.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AllBranchSettingsService {

    @Autowired
    private AllBranchSettingsDaoImpl allBranchSettingsDaoImpl;

    @Transactional
    public AllBranchSettings findById(String id) {
        return allBranchSettingsDaoImpl.findByBranchIdOne(id);
    }

    @Transactional
    public AllBranchSettings findByUserId(String id){
        return allBranchSettingsDaoImpl.findByBuilderUserIdOne(id);
    }


    @Transactional
    public void insert(AllBranchSettings allBranchSettings) {
        allBranchSettings.setCreate_date(LocalDateTime.now());
        allBranchSettingsDaoImpl.insert(allBranchSettings);
    }

    @Transactional
    public void updateByAllStautusN(){
        AllBranchSettings allBranchSettings = new AllBranchSettings();
        allBranchSettings.setSession_status("N");
        allBranchSettings.setLast_date(LocalDateTime.now());
        allBranchSettingsDaoImpl.update(allBranchSettings);
    }

    @Transactional
    public void updateByStatus(AllBranchSettings allBranchSettings){
        allBranchSettings.setLast_date(LocalDateTime.now());
        allBranchSettingsDaoImpl.updateBySessionStatus(allBranchSettings);

    }

}
