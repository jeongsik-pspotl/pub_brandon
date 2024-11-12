package com.inswave.whive.common.signingkeysetting;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeyAndroidSetting {
    private int key_id;
    private int builder_id;
    private Long admin_id;
    private int domain_id;
    private String key_name;
    private String builder_name;
    private String admin_name;
    private String domain_name;
    private String platform;
    private String android_key_type;
    private String android_key_path;
    private String android_deploy_key_path;
    private String android_key_password;
    private String android_key_alias;
    private String android_key_store_password;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;

}
