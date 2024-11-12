package com.inswave.whive.common.deploy;

import lombok.Data;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

@Data
public class Deploy {

    private Long domain_id;
    private Long user_id;
    private Long deploy_id;
    private Long project_id;
    private Long workspace_id;
    private String project_name;
    private String hqKey;
    private Long all_signingkey_id;
    private Long build_id;
    private Long deploy_settings_id;
    private String all_package_name;
    private String apple_key_id;
    private String apple_issuer_id;
    private JSONObject deployTaskSetJson;
    private JSONObject deployMetadataSetJson;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;

}
