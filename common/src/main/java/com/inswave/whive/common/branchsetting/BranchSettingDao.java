package com.inswave.whive.common.branchsetting;

import java.util.List;

public interface BranchSettingDao {

    void insert(BranchSetting branchSetting);

    void update(BranchSetting branchSetting);

    List<BranchSetting> findAll();

    BranchSetting findByID(Long brand_id);

    List<BuilderSelectBoxList> findBySelectBOXLIst();

    BuilderSelectBoxList findBySelectBOXListByID(Long branch_id);

    BranchSetting findByUserID(String branch_user_id);

    void updateByStatus(BranchSetting branchSetting);

    BranchSetting findByBranchName(String branch_name);

    int findByBuilderID();

}
