package com.pspotl.sidebranden.common.signingkeysetting;

import lombok.Data;

@Data
public class KeySettingList {

    private int key_build_android_id;
    private String android_key_name;
    private int key_build_ios_id;
    private String ios_key_name;

}
