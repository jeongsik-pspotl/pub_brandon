package com.inswave.whive.common.buildsetting;

import java.util.List;

public interface BuildSettingDao {
    void insert(BuildSetting buildProject);

    BuildSetting findById(Long id);

    BuildSetting findByBuildId(Long buildId);

    void deleteById(Long build_id);

    List<BuildSetting> findAll();

    void updateBuildProject(BuildSetting buildSetting);

    void updateAppVersionProject(BuildSetting buildSetting);
}
