package com.inswave.whive.common.build;

import com.inswave.whive.common.buildsetting.BuildSetting;
import lombok.Data;

import java.util.ArrayList;

@Data
public class MultiProfileBuildObject {

    private String hqKey;
    private String packageName;
    private String domainID;
    private String userID;
    private String workspace_group_role_id;
    private BuildProject buildProject;
    private ArrayList<BuildSetting> buildSetting;



}
