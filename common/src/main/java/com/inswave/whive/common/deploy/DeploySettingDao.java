package com.inswave.whive.common.deploy;

public interface DeploySettingDao {

    void insert(Deploy deploy);

    Deploy findById(Long build_id);

}
