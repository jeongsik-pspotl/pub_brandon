package com.pspotl.sidebranden.common.deploy;

public interface DeploySettingDao {

    void insert(Deploy deploy);

    Deploy findById(Long build_id);

}
