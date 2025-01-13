package com.pspotl.sidebranden.common.signingkeysetting;

import lombok.Data;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

@Data
public class KeyiOSSetting {
    private int key_id;
    private int builder_id;
    private Long admin_id;
    private int domain_id;
    private String key_name;
    private String builder_name;
    private String admin_name;
    private String domain_name;
    private String platform;
    private String ios_key_type;
    private String ios_release_type;
    private String ios_key_path;
    private String ios_debug_profile_path;
    private String ios_release_profile_path;
    private String ios_key_password;
    private String ios_issuer_id;
    private String ios_key_id;
    private String ios_unlock_keychain_password;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;
    private Object ios_profiles_json;
    private Object ios_certificates_json;

}
