package com.inswave.whive.common.settings;

public interface AllBranchSettingsDao {

    void insert(AllBranchSettings allBranchSettings);

    AllBranchSettings findByBranchIdOne(String branch_id);

    void update(AllBranchSettings allBranchSettings);

    void updateBySessionStatus(AllBranchSettings allBranchSettings);

    AllBranchSettings findByBuilderUserIdOne(String id);
}
