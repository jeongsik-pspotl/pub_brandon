package com.pspotl.sidebranden.common.signingkeysetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class KeySettingDaoImpl extends NamedParameterJdbcDaoSupport implements KeySettingDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // private static final String INSERT_SQL = "INSERT INTO signingkey_settings(signingkey_id, signingkey_name, signingkey_type, platform, signingkey_path, key_password, profile_key, key_alias, profile_password, create_date, update_date, build_type, branch_id, domain_id, admin_id) " +
       //     "VALUES(:signingkey_id, :signingkey_name, :signingkey_type, :platform, :signingkey_path, :key_password, :profile_key, :key_alias, :profile_password, :create_date, :update_date, :build_type, :branch_id, :domain_id, :admin_id)";

    private static final String INSERT_MAIN_SQL = "INSERT INTO key_setting(builder_id, admin_id, key_name, platform, created_date, updated_date) " +
            "VALUES(:builder_id, :admin_id, :key_name, :platform, NOW(), NOW())";


    private static final String INSERT_ANDROID_DETAIL_SQL = "INSERT INTO key_android_setting(key_id, android_key_type, android_key_path, android_key_password, android_key_alias, android_key_store_password) " +
            "VALUES(:key_id, :android_key_type, :android_key_path, :android_key_password, :android_key_alias, :android_key_store_password)";

    private static final String INSERT_IOS_DETAIL_SQL = "INSERT INTO key_ios_setting(key_id, ios_key_type, ios_key_path, ios_debug_profile_path, ios_release_profile_path, ios_key_password, ios_issuer_id, ios_key_id, ios_release_type, ios_unlock_keychain_password) " +
            "VALUES(:key_id, :ios_key_type, :ios_key_path, :ios_debug_profile_path, :ios_release_profile_path, :ios_key_password, :ios_issuer_id, :ios_key_id, :ios_release_type, :ios_unlock_keychain_password)";


    private static final String UPDATE_MAIN_SQL = "UPDATE key_setting SET builder_id = :builder_id, admin_id = :admin_id, platform = :platform, updated_date = NOW() WHERE key_id = :key_id";

    private static final String UPDATE_IOS_DETAIL_SQL = "UPDATE key_ios_setting SET ios_unlock_keychain_password = :ios_unlock_keychain_password, ios_key_id = :ios_key_id, ios_issuer_id = :ios_issuer_id, ios_key_password = :ios_key_password, ios_key_path = :ios_key_path, ios_debug_profile_path = :ios_debug_profile_path, ios_release_profile_path = :ios_release_profile_path WHERE key_id = :key_id";

    private static final String UPDATE_ANDROID_DETAIL_SQL = "UPDATE key_android_setting SET android_key_password = :android_key_password, android_key_alias = :android_key_alias, android_key_store_password = :android_key_store_password WHERE key_id = :key_id";

    private static final String SELECT_ALL = "SELECT key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            " case when platform = 'Android' then (select key_android.android_key_type from key_android_setting key_android where key_android.key_id = k.key_id) " +
            " when platform = 'iOS' then (select key_ios.ios_key_type from key_ios_setting key_ios where key_ios.key_id = k.key_id) " +
            " end as key_type " +
            " FROM key_setting k";

    private static final String SELECT_BY_ADMIN = "SELECT key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            "case when platform = 'Android' then (select key_android.android_key_type from key_android_setting key_android where key_android.key_id = k.key_id) " +
            "when platform = 'iOS' then (select key_ios.ios_key_type from key_ios_setting key_ios where key_ios.key_id = k.key_id) " +
            "end as key_type " +
            "FROM key_setting k where k.admin_id = :admin_id";

    private static final String SELECT_BY_SELECTBOX = "SELECT signingkey_id, signingkey_name FROM signingkey_settings";

    private static final String SELECT_BY_ID = "SELECT key_set.key_id, key_set.key_name, key_set.platform, key_set.created_date, key_set.updated_date, key_set.builder_id" +
            ", key_set.admin_id FROM key_setting key_set where key_set.key_id = :key_id";

    private static final String SELECT_BY_ANDROID_ID = "SELECT key_set.key_id, key_set.key_name, key_set.platform, android_key.android_key_type, android_key.android_key_path, android_key.android_key_password, " +
            "android_key.android_key_alias, android_key.android_key_store_password, key_set.created_date, key_set.updated_date, key_set.builder_id, android_key.android_deploy_key_path " +
            ", (select builder_name from builder_setting where builder_id = key_set.builder_id ) as builder_name " +
            ", key_set.admin_id ,(select user_name from user where user_id = key_set.admin_id) as admin_name, (select domain_id from user where user_id = key_set.admin_id) as domain_id " +
            ", (select d.domain_name from domain d, user u where d.domain_id = u.domain_id and u.user_id = key_set.admin_id) as domain_name " +
            "FROM key_setting key_set, key_android_setting android_key where key_set.key_id = android_key.key_id and key_set.key_id = :key_id";

    private static final String SELECT_BY_IOS_ID = "SELECT key_set.key_id, key_set.key_name, key_set.platform, ios_key.ios_key_type, ios_key.ios_key_path, ios_key.ios_debug_profile_path, " +
            "ios_key.ios_release_profile_path, ios_key.ios_key_password, ios_key.ios_issuer_id, ios_key.ios_key_id, key_set.created_date, key_set.updated_date, key_set.builder_id " +
            ", (select builder_name from builder_setting where builder_id = key_set.builder_id ) as builder_name, key_set.admin_id " +
            ", (select user_name from user where user_id = key_set.admin_id) as admin_name, (select domain_id from user where user_id = key_set.admin_id) as domain_id " +
            ", (select d.domain_name from domain d, user u where d.domain_id = u.domain_id and u.user_id = key_set.admin_id) as domain_name, ios_key.ios_release_type, ios_key.ios_unlock_keychain_password, " +
            " ios_key.ios_profiles_json, ios_key.ios_certificates_json " +
            " FROM key_setting key_set, key_ios_setting ios_key where key_set.key_id = ios_key.key_id and key_set.key_id = :key_id";

    private static final String SELECT_BY_SELECTBOX_PLATFORM = "SELECT signingkey_id, signingkey_name FROM signingkey_settings where platform = :platform";

    private static final String SELECT_BY_SELECTBOX_KEY_TYPE = "SELECT signingkey_id, signingkey_name FROM signingkey_settings where platform = :platform and signingkey_type = :signingkey_type and branch_id = :branch_id and domain_id = :domain_id";

    private static final String SELECT_BY_SELECTBOX_DEPLOY_KEY_TYPE = "SELECT key_deploy_android_id, (SELECT key_name FROM key_setting WHERE key_id = key_deploy_android_id) as android_key_name," +
            " key_deploy_ios_id, (SELECT key_name FROM key_setting WHERE key_id = key_deploy_ios_id) as ios_key_name " +
            " FROM key_group_role WHERE role_id = :role_id";

    private static final String SELECT_BY_SELECTBOX_DEPLOY_KEY_TYPY_SERVICE = "SELECT k.key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            " case when (select count(*) from key_android_setting key_android where key_android.key_id = k.key_id and key_android.android_key_type = 'deploy') then (select key_android.android_key_type from key_android_setting key_android where key_android.key_id = k.key_id and key_android.android_key_type = 'deploy') " +
            " when (select count(*) from key_ios_setting key_ios where key_ios.key_id = k.key_id and key_ios.ios_key_type = 'deploy') then (select key_ios.ios_key_type from key_ios_setting key_ios where key_ios.key_id = k.key_id and key_ios.ios_key_type = 'deploy') " +
            " end as key_type " +
            " FROM key_setting k where k.admin_id = :admin_id and k.key_id in (select case when k.platform = 'iOS' and k_ios.ios_key_type = 'deploy' then k_ios.key_id " +
            " when k.platform = 'Android' and k_android.android_key_type = 'deploy' then k_android.key_id " +
            " end as key_id from key_ios_setting k_ios, key_android_setting k_android)";

    private static final String SELECT_BY_SELECTBOX_ANDROID_DEPLOY_KEY_TYPE_SERVICE = "SELECT k.key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            " key_android.android_key_type as key_type " +
            " FROM key_setting k, key_android_setting key_android " +
            " where k.admin_id = :admin_id and k.key_id = key_android.key_id and key_android.android_key_type = 'deploy'";

    private static final String SELECT_BY_SELECTBOX_IOS_DEPLOY_KEY_TYPE_SERVICE = "SELECT k.key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            " key_ios.ios_key_type as key_type " +
            " FROM key_setting k, key_ios_setting key_ios " +
            " where k.admin_id = :admin_id and k.key_id = key_ios.key_id and key_ios.ios_key_type = 'deploy'";

    private static final String SELECT_BY_SELECTBOX_DEPLOY_KEY_TYPE_ONE = "SELECT key_id, key_name FROM key_setting where key_id = :key_id";

    private static final String SELECT_BY_SELECTBOX_BUILD_TYPE = "SELECT signingkey_id, signingkey_name FROM signingkey_settings where platform = :platform and build_type = :build_type and signingkey_type = :signingkey_type and branch_id = :branch_id and domain_id = :domain_id";

    private static final String SELECT_BY_SELECTBOX_IOS_PROFILES_KEY_NAME = "SELECT ios_profiles_json FROM key_setting k, key_ios_setting key_ios where k.key_id = key_ios.key_id and k.key_id = :key_id";

    private static final String SELECT_BY_SELECTBOX_KEY_TYPE_RE = "SELECT key_build_android_id, (SELECT key_name FROM key_setting WHERE key_id = key_build_android_id) as android_key_name, " +
            " key_build_ios_id, (SELECT key_name FROM key_setting WHERE key_id = key_build_ios_id) as ios_key_name" +
            " FROM key_group_role WHERE role_id in (SELECT role_id FROM role WHERE role_id = :role_id)";

    private static final String SELECT_BY_SELECT_BOX_KEY_TYPE_AMDIN_ALL = "SELECT k.key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            "       case when (select count(*) from key_android_setting key_android where key_android.key_id = k.key_id and key_android.android_key_type = 'build') then (select key_android.android_key_type from key_android_setting key_android where key_android.key_id = k.key_id and key_android.android_key_type = 'build') " +
            "            when (select count(*) from key_ios_setting key_ios where key_ios.key_id = k.key_id and key_ios.ios_key_type = 'build') then (select key_ios.ios_key_type from key_ios_setting key_ios where key_ios.key_id = k.key_id and key_ios.ios_key_type = 'build') " +
            "           end as key_type" +
            " FROM key_setting k where k.admin_id = :admin_id " +
            " and k.key_id in (select case when k.platform = 'iOS' and k_ios.ios_key_type = 'build' then k_ios.key_id " +
            " when k.platform = 'Android' and k_android.android_key_type = 'build' then k_android.key_id " +
            " end as key_id from key_ios_setting k_ios, key_android_setting k_android)";

    private static final String SELECT_BY_SELECT_BOX_BUILD_ANDROID_KEY_TYPE_AMDIN_ALL = "SELECT k.key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            " key_android.android_key_type as key_type " +
            " FROM key_setting k, key_android_setting key_android " +
            " where k.admin_id = :admin_id and k.key_id = key_android.key_id and key_android.android_key_type = 'all'"; // key type : all/build

    private static final String SELECT_BY_SELECT_BOX_BUILD_IOS_KEY_TYPE_AMDIN_ALL = "SELECT k.key_id, builder_id, admin_id, key_name, platform, created_date, updated_date, " +
            " key_ios.ios_key_type as key_type " +
            " FROM key_setting k, key_ios_setting key_ios " +
            " where k.admin_id = :admin_id and k.key_id = key_ios.key_id and key_ios.ios_key_type = 'all'";

    private static final String SELECT_BY_SIGNINGKEY_NAME = "SELECT key_name FROM key_setting where key_name = :key_name";

    private static final String SELECT_BY_LAST_INDEX_ID = "SELECT LAST_INSERT_ID()";

    private static final String UPDATE_BY_ANDROID_KEY_FILE_PATH = "UPDATE key_android_setting SET android_key_path = :android_key_path, android_deploy_key_path = :android_deploy_key_path where key_id = :key_id";

    private static final String UPDATE_BY_IOS_KEY_FILE_PATH = "UPDATE key_ios_setting SET ios_key_path = :ios_key_path, ios_debug_profile_path = :ios_debug_profile_path, ios_release_profile_path = :ios_release_profile_path WHERE key_id = :key_id";

    private static final String UPDATE_BY_IOS_DEPLOY_KEY_FILE_PATH = "UPDATE key_ios_setting SET ios_key_path = :ios_key_path  WHERE key_id = :key_id";

    private static final String UPDATE_BY_IOS_ALL_KEY_FILE_OBJ = "UPDATE key_ios_setting SET ios_certificates_json = :ios_certificates_json, ios_profiles_json = :ios_profiles_json, ios_key_path = :ios_key_path  WHERE key_id = :key_id";

    //private static final String UPDATE_BY_KEY_FILE_PATH = "UPDATE signingkey_settings SET signingkey_path = :signingkey_path where signingkey_id = :signingkey_id";


    @Override
    public void insertMainAndroid(KeyAndroidSetting keyAndroidSetting) {
        namedParameterJdbcTemplate.update(INSERT_MAIN_SQL, new BeanPropertySqlParameterSource(keyAndroidSetting));
    }

    @Override
    public void insertToDetailAndroid(KeyAndroidSetting keyAndroidSetting) {
        namedParameterJdbcTemplate.update(INSERT_ANDROID_DETAIL_SQL, new BeanPropertySqlParameterSource(keyAndroidSetting));
    }

    @Override
    public void insertMainiOS(KeyiOSSetting keyiOSSetting) {
        namedParameterJdbcTemplate.update(INSERT_MAIN_SQL, new BeanPropertySqlParameterSource(keyiOSSetting));
    }

    @Override
    public void insertToDetailiOS(KeyiOSSetting keyiOSSetting) {
        namedParameterJdbcTemplate.update(INSERT_IOS_DETAIL_SQL, new BeanPropertySqlParameterSource(keyiOSSetting));
    }


    @Override
    public List<KeySetting> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public List<KeySetting> findByAdminAll(int admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECT_BOX_KEY_TYPE_AMDIN_ALL,new MapSqlParameterSource("admin_id", admin_id), new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public List<KeySetting> findByAndroidBuildTypeAdminAll(Long admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECT_BOX_BUILD_ANDROID_KEY_TYPE_AMDIN_ALL,new MapSqlParameterSource("admin_id", admin_id), new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public List<KeySetting> findByiOSBuildTypeAdminAll(Long admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECT_BOX_BUILD_IOS_KEY_TYPE_AMDIN_ALL,new MapSqlParameterSource("admin_id", admin_id), new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public List<KeySetting> findByAdmin(Long domain_id, Long member_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_ADMIN,
                new MapSqlParameterSource("admin_id", member_id),
                new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public KeySetting findByID(int key_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID,
                new MapSqlParameterSource("key_id", key_id),
                new BeanPropertyRowMapper<>(KeySetting.class));
    }

    // SELECT_BY_ANDROID_ID
    @Override
    public KeyAndroidSetting findByAndroidID(Long key_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ANDROID_ID,
                new MapSqlParameterSource("key_id", key_id),
                new BeanPropertyRowMapper<>(KeyAndroidSetting.class));
    }

    // SELECT_BY_IOS_ID
    @Override
    public KeyiOSSetting findByiOSKeyID(Long key_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_IOS_ID,
                new MapSqlParameterSource("key_id", key_id),
                new BeanPropertyRowMapper<>(KeyiOSSetting.class));
    }

    @Override
    public List<KeyDeploySettingList> findByDeployTypeList(Long role_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_DEPLOY_KEY_TYPE,
                new MapSqlParameterSource("role_id", role_id),
                new BeanPropertyRowMapper<>(KeyDeploySettingList.class));
    }

    @Override
    public List<KeySetting> findByDeployTypeServiceLis(int admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_DEPLOY_KEY_TYPY_SERVICE,
                new MapSqlParameterSource("admin_id", admin_id),
                new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public List<KeySetting> findByDeployTypeAndroidServiceList(Long admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_ANDROID_DEPLOY_KEY_TYPE_SERVICE,
                new MapSqlParameterSource("admin_id", admin_id),
                new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public List<KeySetting> findByDeployTypeiOSServiceList(Long admin_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_IOS_DEPLOY_KEY_TYPE_SERVICE,
                new MapSqlParameterSource("admin_id", admin_id),
                new BeanPropertyRowMapper<>(KeySetting.class));
    }

    @Override
    public KeyDeploySettingList findByDeployTypeOne(int key_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_SELECTBOX_DEPLOY_KEY_TYPE_ONE,
                new MapSqlParameterSource("key_id", key_id),
                new BeanPropertyRowMapper<>(KeyDeploySettingList.class));
    }

    @Override
    public List<SigningKeySelectBoxList> findBySelectList() {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX, new BeanPropertyRowMapper<>(SigningKeySelectBoxList.class));
    }

    @Override
    public List<SigningKeySelectBoxList> findBySelectPlatformList(String platform) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_PLATFORM,
                new MapSqlParameterSource("platform", platform),
                new BeanPropertyRowMapper<>(SigningKeySelectBoxList.class));
    }

    @Override
    public List<KeySetting> findByKeyName(String key_name) {
        try {
            return namedParameterJdbcTemplate.query(SELECT_BY_SIGNINGKEY_NAME,
                    new MapSqlParameterSource("key_name", key_name),
                    new BeanPropertyRowMapper<>(KeySetting.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<SigningKeySelectBoxList> findByKeyType(String platform, String signingkey_type, Long builder_id, Long domain_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_KEY_TYPE,
                new MapSqlParameterSource("platform", platform).addValue("signingkey_type",signingkey_type).addValue("branch_id",builder_id).addValue("domain_id",domain_id),
                new BeanPropertyRowMapper<>(SigningKeySelectBoxList.class));
    }

    @Override
    public List<KeySettingList> findByKeyTypeRE(Long role_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_KEY_TYPE_RE,
                new MapSqlParameterSource("role_id", role_id),
                new BeanPropertyRowMapper<>(KeySettingList.class));
    }

    @Override
    public List<SigningKeySelectBoxList> findByBuildType(String platform, String build_type, String provisioning, Long builder_id, Long domain_id) {
        return namedParameterJdbcTemplate.query(SELECT_BY_SELECTBOX_BUILD_TYPE,
                new MapSqlParameterSource("platform", platform).addValue("build_type",build_type)
                        .addValue("signingkey_type", provisioning).addValue("branch_id", builder_id).addValue("domain_id",domain_id),
                new BeanPropertyRowMapper<>(SigningKeySelectBoxList.class));
    }

    @Override
    public KeyiOSSetting findByiOSProfilesKeyName(String key_id) {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_SELECTBOX_IOS_PROFILES_KEY_NAME,
                new MapSqlParameterSource("key_id", key_id),
                new BeanPropertyRowMapper<>(KeyiOSSetting.class));
    }

    @Override
    public int findSigningkeyLastCount() {
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_LAST_INDEX_ID, new MapSqlParameterSource("id", 0),Integer.class);
    }

    @Override
    public void updateToAndroidMainData(KeyAndroidSetting keyAndroidSetting) {
        namedParameterJdbcTemplate.update(UPDATE_MAIN_SQL, new BeanPropertySqlParameterSource(keyAndroidSetting));
    }


    @Override
    public void updateToAndroidAliasData(KeyAndroidSetting keyAndroidSetting) {
        namedParameterJdbcTemplate.update(UPDATE_ANDROID_DETAIL_SQL, new BeanPropertySqlParameterSource(keyAndroidSetting));
    }

    @Override
    public void updateToMainiOS(KeyiOSSetting keyiOSSetting) {
        namedParameterJdbcTemplate.update(UPDATE_MAIN_SQL, new BeanPropertySqlParameterSource(keyiOSSetting));
    }

    @Override
    public void updateToDetailiOS(KeyiOSSetting keyiOSSetting) {
        namedParameterJdbcTemplate.update(UPDATE_IOS_DETAIL_SQL, new BeanPropertySqlParameterSource(keyiOSSetting));
    }

    @Override
    public void updateByKeyfilePath(int signingkey_id, String signingkey_path, String deployfilePath) {
        namedParameterJdbcTemplate.update(UPDATE_BY_ANDROID_KEY_FILE_PATH, new MapSqlParameterSource("key_id",signingkey_id).addValue("android_key_path", signingkey_path).addValue("android_deploy_key_path", deployfilePath));
    }

    @Override
    public void updateByiOSKeyfilePath(int key_id, String ios_key_path, String ios_debug_profile, String ios_release_profile) {
        namedParameterJdbcTemplate.update(UPDATE_BY_IOS_KEY_FILE_PATH, new MapSqlParameterSource("key_id",key_id).addValue("ios_key_path", ios_key_path)
                .addValue("ios_debug_profile_path", ios_debug_profile).addValue("ios_release_profile_path", ios_release_profile));
    }

    @Override
    public void updateByiOSDeployfilePath(int key_id, String ios_key_path) {
        namedParameterJdbcTemplate.update(UPDATE_BY_IOS_DEPLOY_KEY_FILE_PATH, new MapSqlParameterSource("key_id",key_id).addValue("ios_key_path", ios_key_path));
    }

    @Override
    public void updateByAlliOSKeyFileObj(int key_id, String profilesListObj, String certificateListObj, String deployKeyFilePath) {
        namedParameterJdbcTemplate.update(UPDATE_BY_IOS_ALL_KEY_FILE_OBJ, new MapSqlParameterSource("key_id",key_id).addValue("ios_profiles_json", profilesListObj)
                .addValue("ios_certificates_json", certificateListObj).addValue("ios_key_path",deployKeyFilePath));
    }


}
