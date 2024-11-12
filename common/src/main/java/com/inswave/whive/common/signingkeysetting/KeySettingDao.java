package com.inswave.whive.common.signingkeysetting;


import java.util.List;

public interface KeySettingDao {

    void insertMainAndroid(KeyAndroidSetting keyAndroidSetting);

    void insertToDetailAndroid(KeyAndroidSetting androidSetting);

    void insertMainiOS(KeyiOSSetting keyiOSSetting);

    void insertToDetailiOS(KeyiOSSetting keyiOSSetting);

    List<KeySetting> findAll();

    List<KeySetting> findByAdminAll(int admin_id);

    List<KeySetting> findByAndroidBuildTypeAdminAll(Long admin_id);

    List<KeySetting> findByiOSBuildTypeAdminAll(Long admin_id);

    List<KeySetting> findByAdmin(Long domain_id, Long member_id);

    KeySetting findByID(int key_id);

    KeyAndroidSetting findByAndroidID(Long key_id);

    KeyiOSSetting findByiOSKeyID(Long key_id);

    List<KeyDeploySettingList> findByDeployTypeList(Long role_id);

    List<KeySetting> findByDeployTypeServiceLis(int admin_id);

    List<KeySetting> findByDeployTypeAndroidServiceList(Long admin_id);

    List<KeySetting> findByDeployTypeiOSServiceList(Long admin_id);

    KeyDeploySettingList findByDeployTypeOne(int key_id);

    List<SigningKeySelectBoxList> findBySelectList();

    List<SigningKeySelectBoxList> findBySelectPlatformList(String platform);

    List<KeySetting> findByKeyName(String key_name);

    List<SigningKeySelectBoxList> findByKeyType(String platform, String signingkey_type, Long builder_id, Long domain_id);

    List<KeySettingList> findByKeyTypeRE(Long role_id);

    List<SigningKeySelectBoxList> findByBuildType(String platform, String build_type, String provisioning, Long builder_id, Long domain_id);

    KeyiOSSetting findByiOSProfilesKeyName(String key_id);

    int findSigningkeyLastCount();

    void updateToAndroidMainData(KeyAndroidSetting keyAndroidSetting);

    void updateToAndroidAliasData(KeyAndroidSetting keyAndroidSetting);

    void updateToMainiOS(KeyiOSSetting keyiOSSetting);

    void updateToDetailiOS(KeyiOSSetting keyiOSSetting);

    void updateByKeyfilePath(int signingkey_id, String signingkey_path, String deployfilePath);

    void updateByiOSKeyfilePath(int key_id, String ios_key_path, String ios_debug_profile, String ios_release_profile);

    void updateByiOSDeployfilePath(int key_id, String ios_key_path);

    void updateByAlliOSKeyFileObj(int key_id, String profilesListObj, String certificateListObj, String deployKeyFilePath);

}
