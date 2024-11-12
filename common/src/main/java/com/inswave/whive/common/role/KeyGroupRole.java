package com.inswave.whive.common.role;

import lombok.Data;

@Data
public class KeyGroupRole {
   private int key_group_role_id;
   private int role_id;
   private int key_id;
   private int key_build_android_id;
   private int key_build_ios_id;
   private int key_deploy_android_id;
   private int key_deploy_ios_id;
   private String select_yn;


}
