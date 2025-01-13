package com.pspotl.sidebranden.common.signingkeysetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SigningKeySettingService {

    @Autowired
    private KeySettingDaoImpl keySettingDao;

    @Transactional
    public int insertToAndroid (KeyAndroidSetting keyAndroidSetting){
        keyAndroidSetting.setCreated_date(LocalDateTime.now());
        keyAndroidSetting.setUpdated_date(LocalDateTime.now());

        keySettingDao.insertMainAndroid(keyAndroidSetting);
        keyAndroidSetting.setKey_id(keySettingDao.findSigningkeyLastCount());
        keySettingDao.insertToDetailAndroid(keyAndroidSetting);

        return keyAndroidSetting.getKey_id();
    }

    @Transactional
    public int insertToiOS (KeyiOSSetting keyiOSSetting){
        keyiOSSetting.setCreated_date(LocalDateTime.now());
        keyiOSSetting.setUpdated_date(LocalDateTime.now());

        keySettingDao.insertMainiOS(keyiOSSetting);
        keyiOSSetting.setKey_id(keySettingDao.findSigningkeyLastCount());
        keySettingDao.insertToDetailiOS(keyiOSSetting);


        return keyiOSSetting.getKey_id();
    }

    @Transactional
    public List<KeySetting> findAll() {
        return keySettingDao.findAll();
    }

    @Transactional
    public List<KeySetting> findAdminAll(Long domain_id, Long member_id){
        return keySettingDao.findByAdmin(domain_id, member_id);
    }

    @Transactional
    public List<KeySetting> findByAdminOneAll(Long admin_id, String platform){
        // android
        if(platform.toLowerCase().equals("android")){
            return keySettingDao.findByAndroidBuildTypeAdminAll(admin_id);
        }else {
            // ios
            return keySettingDao.findByiOSBuildTypeAdminAll(admin_id);
        }
    }

    // findByID
    @Transactional
    public KeyAndroidSetting findByAndroidID(Long key_id){
        return keySettingDao.findByAndroidID(key_id);
    }

    @Transactional
    public KeySetting findByID(int key_id) {
        return keySettingDao.findByID(key_id);
    }


    @Transactional
    public KeyiOSSetting findByiOSKeyID(Long key_id){
        return keySettingDao.findByiOSKeyID(key_id);
    }


    @Transactional
    public List<SigningKeySelectBoxList> findBySelectList(){
        return keySettingDao.findBySelectList();
    }

    @Transactional
    public List<SigningKeySelectBoxList> findBySelectPlatformList(String platform){
        return keySettingDao.findBySelectPlatformList(platform);
    }

    @Transactional
    public List<SigningKeySelectBoxList> findByKeyType(String platform, String signingkey_type, Long builder_id, Long domain_id){
        return keySettingDao.findByKeyType(platform, signingkey_type, builder_id, domain_id);
    }

    @Transactional
    public List<SigningKeySelectBoxList> findByKeyAndroidType(String platform, String key_type, Long builder_id, Long role_id){
        return keySettingDao.findByKeyType(platform, key_type, builder_id, role_id);
    }

    @Transactional
    public List<KeyDeploySettingList> findByDeployTypeList(Long role_id){
        return keySettingDao.findByDeployTypeList(role_id);
    }

    @Transactional
    public List<KeySetting> findByDeployTypeServiceList(Long admin_id, String platform){

        // android
        if(platform.toLowerCase().equals("android")){
            return keySettingDao.findByDeployTypeAndroidServiceList(admin_id);
        }else { // ios
            return keySettingDao.findByDeployTypeiOSServiceList(admin_id);
        }

    }

    public KeyDeploySettingList findByDeployTypeOne(int key_id){
        return keySettingDao.findByDeployTypeOne(key_id);
    }


    @Transactional
    public List<KeySettingList> findByKeyTypeRE(Long role_id){
        return keySettingDao.findByKeyTypeRE(role_id);
    }

    @Transactional
    public List<SigningKeySelectBoxList> findByBuildType(String platform, String build_type, String provisioning, Long builder_id, Long domain_id){
        return keySettingDao.findByBuildType(platform, build_type, provisioning, builder_id, domain_id);
    }

    @Transactional
    public List<KeySetting> findByKeyName(String key_name){
        return keySettingDao.findByKeyName(key_name);
    }

    @Transactional
    public KeyiOSSetting findByiOSProfilesKeyName(String key_id) {return keySettingDao.findByiOSProfilesKeyName(key_id); }

    @Transactional
    public void updateByToDetailiOS(KeyiOSSetting keyiOSSetting){

        // key setting main update...
        keySettingDao.updateToMainiOS(keyiOSSetting);

        // key setting detail update..
        keySettingDao.updateToDetailiOS(keyiOSSetting);

    }

    @Transactional
    public void updateByKeyfilePath(int signingkey_id, String signingkey_path, String deployfilePath){
        keySettingDao.updateByKeyfilePath(signingkey_id, signingkey_path, deployfilePath);
    }

    @Transactional
    public void updateByiOSKeyfilePath(int key_id, String ios_key_path, String ios_debug_profile, String ios_release_profile){
        keySettingDao.updateByiOSKeyfilePath(key_id, ios_key_path, ios_debug_profile, ios_release_profile);
    }

    @Transactional
    public void udpateByTODetailAndroid(KeyAndroidSetting keyAndroidSetting){

        // key setting main update...
        keySettingDao.updateToAndroidMainData(keyAndroidSetting);

        // key setting detail data update..
        keySettingDao.updateToAndroidAliasData(keyAndroidSetting);

    }

    @Transactional
    public void updateByiOSDeployKeyfilePath(int key_id, String ios_key_path){
        keySettingDao.updateByiOSDeployfilePath(key_id, ios_key_path);
    }

    @Transactional
    public void updateByAlliOSKeyFileObj(int key_id, String profilesListObj, String certificateListObj, String deployKeyFilePath){
        keySettingDao.updateByAlliOSKeyFileObj(key_id, profilesListObj, certificateListObj, deployKeyFilePath);
    }

}
