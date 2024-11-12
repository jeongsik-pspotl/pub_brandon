package com.inswave.whive.common.signingkeysetting;

import lombok.Data;

@Data
public class KeyDeploySettingList {

   private int key_id;
   private String key_name;
   private int key_deploy_android_id;
   private String android_key_name;
   private int key_deploy_ios_id;
   private String ios_key_name;
}
