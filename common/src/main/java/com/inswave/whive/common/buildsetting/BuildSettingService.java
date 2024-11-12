package com.inswave.whive.common.buildsetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BuildSettingService {
    @Autowired
    private BuildSettingDaoImpl buildSettingDaoImpl;

    @Transactional
    public BuildSetting findById(Long id) {
        return buildSettingDaoImpl.findById(id);
    }

    @Transactional
    public BuildSetting findByBuildId(Long buildId){
        return buildSettingDaoImpl.findByBuildId(buildId);
    }

    @Transactional
    public List<BuildSetting> findAll() {
        return buildSettingDaoImpl.findAll();
    }

    @Transactional
    public void insert(BuildSetting setting) {
        setting.setCreated_date(LocalDateTime.now());
        setting.setUpdated_date(LocalDateTime.now());
        buildSettingDaoImpl.insert(setting);
    }

    @Transactional
    public void updateBuildSetting(BuildSetting setting) {
        setting.setUpdated_date(LocalDateTime.now());
        buildSettingDaoImpl.updateBuildProject(setting);
    }

    @Transactional
    public void updateAppVersionSetting(BuildSetting buildSetting){
        buildSetting.setUpdated_date(LocalDateTime.now());
        buildSettingDaoImpl.updateAppVersionProject(buildSetting);
    }

    @Transactional
    public void delete(Long build_id) {
        buildSettingDaoImpl.deleteById(build_id);
    }
}
