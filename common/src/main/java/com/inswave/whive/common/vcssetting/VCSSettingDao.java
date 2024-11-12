package com.inswave.whive.common.vcssetting;

import com.inswave.whive.common.branchsetting.BranchSetting;

import java.util.List;

public interface VCSSettingDao {

    void insert(VCSSetting vcsSetting);

    void update(VCSSetting vcsSetting);

    List<VCSSetting> findAll();

    List<VCSSetting> findAdminAll(Long user_id);

    VCSSetting findByID(Long vcs_id);

    List<VCSSelectBoxList> findBySelectList();

    VCSSelectBoxList findBySelectListVcsID(Long vcs_id);

    List<VCSSelectBoxList> findBySelectListAdminID(Long admin_id);

    VCSSetting findByVCSName(String vcs_name);

    List<VCSSelectBoxList> findBySelectListType(String vcs_type);

    List<VCSSelectBoxList> findBySelectListAdminVcsType(Long admin_id, String vcs_type);
}
