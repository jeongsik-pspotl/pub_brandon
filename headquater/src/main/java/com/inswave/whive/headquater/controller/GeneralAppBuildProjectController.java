package com.inswave.whive.headquater.controller;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.branchsetting.BranchSettingService;
import com.inswave.whive.common.build.BuildProject;
import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.build.MultiProfileBuildObject;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.common.deploy.Deploy;
import com.inswave.whive.common.deploy.DeploySettingService;
import com.inswave.whive.common.enums.MessageString;
import com.inswave.whive.common.error.WHiveInvliadRequestException;
import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.member.MemberService;
import com.inswave.whive.common.role.ProjectGroupRole;
import com.inswave.whive.common.role.RoleService;
import com.inswave.whive.common.signingkeysetting.KeyAndroidSetting;
import com.inswave.whive.common.signingkeysetting.KeyiOSSetting;
import com.inswave.whive.common.signingkeysetting.SigningKeySettingService;
import com.inswave.whive.common.vcssetting.VCSSetting;
import com.inswave.whive.common.vcssetting.VCSSettingService;
import com.inswave.whive.common.workspace.MemberMapping;
import com.inswave.whive.common.workspace.Workspace;
import com.inswave.whive.common.workspace.WorkspaceService;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.handler.WHiveIdentity;
import com.inswave.whive.headquater.service.ClusterWebSocketBuilderService;
import com.inswave.whive.headquater.util.ResponseUtility;
import com.inswave.whive.headquater.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
@Controller
public class GeneralAppBuildProjectController {
    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private BuildProjectService buildProjectService;

    @Autowired
    private BranchSettingService branchSettingService;

    @Autowired
    private VCSSettingService vcsSettingService;

    @Autowired
    private SigningKeySettingService signingKeySettingService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RoleService roleService;

    @Autowired
    DeploySettingService deploySettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    private ResponseUtility responseUtility;

    @Value("${whive.distribution.macPassword}")
    private String macPassword;

    @Autowired
    Common common;

    @RequestMapping(value = "/manager/general/build/project/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createGeneralAppProject(HttpServletRequest request, @RequestBody MultiProfileBuildObject generalProject) {

        MemberLogin memberLogin;
        MemberMapping memberMapping;
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();
        BuildProject buildProject = generalProject.getBuildProject();
        Workspace workspace;
        VCSSetting vcsSetting;
        String platform = buildProject.getPlatform().toLowerCase();
        KeyAndroidSetting keyAndroidSetting = new KeyAndroidSetting();
        KeyiOSSetting keyiOSSetting = new KeyiOSSetting();

        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonBranchSetting = new JSONObject();
        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonSigningKey = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject entity = new JSONObject();

        Deploy deploy = new Deploy();

        String android_app_id = generalProject.getBuildSetting().get(0).getApp_id();
        String android = PayloadKeyType.android.name();
        String ios = PayloadKeyType.ios.name();

        if (buildProject.getProject_name() == null || buildProject.getProject_name().equals("")) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        // 프로젝트 설정 시작
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build request queue 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if (builderQueueManaged.getProject_queue_cnt() > builderQueueManaged.getProject_queue_status_cnt()) {
            builderQueueManaged.setProject_queue_cnt(builderQueueManaged.getProject_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);
        } else {
            entity.put(PayloadKeyType.error.name(),MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }

        buildProjectService.insert(buildProject);
        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());
        // 프로젝트 설정 끝

        // 권한관리 시작
        String workspace_group_role_id = generalProject.getWorkspace_group_role_id();
        createProjectGroupRole(projectGroupRole, buildProjectSelect, memberLogin, workspace_group_role_id);
        roleService.insertToProjectGroupRole(projectGroupRole);
        //권한관리 끝

        //인증서 셋팅 및 배포 설정 시작
        createDeploySettingObj(jsonDeployObj, buildProject, buildProjectSelect, deploy, platform, android_app_id, keyAndroidSetting, keyiOSSetting);
        deploySettingService.insert(deploy);
        //인증서 셋팅 및 배포 설정 끝

        // 빌더 쪽에 보낼 메세지 객체 작성 시작
        HashMap<String, Object> msg = new HashMap<>();

        //워크스페이스 셋팅
        workspace = workspaceService.findById(buildProject.getWorkspace_id());

        //빌드 관련 메시지 작성
        memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
        createBuildSettingMsg(msg, buildProject, memberLogin, memberMapping, buildProjectSelect, workspace, jsonDeployObj);

        //빌더 관련 메시지 작성
        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

        //형상관리 셋팅
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        createVcsSettingObject(jsonRepository, vcsSetting);

        //인증키 셋팅
        createSigningKeySettingObject(msg, platform, jsonSigningKey, keyAndroidSetting, keyiOSSetting);

        //msg 객체에 위에서 만든 셋팅 정보들 넣기
        msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
        msg.put(PayloadKeyType.jsonSigningkey.name(), jsonSigningKey.toJSONString());

        if (platform.equals(ios) && keyiOSSetting != null) {
            jsonApplyConfig.put("release_type", keyiOSSetting.getIos_release_type());
        }

        msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
        msg.put(PayloadKeyType.jsonBranchSetting.name(), jsonBranchSetting.toJSONString());
        msg.put(PayloadKeyType.projectName.name(), buildProject.getProject_name());
        msg.put(PayloadKeyType.templateVersion.name(), buildProject.getTemplate_version());
        msg.put(PayloadKeyType.language.name(), buildProject.getPlatform_language());

        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());
        return responseUtility.makeSuccessResponse(entity);
    }

    private void createSigningKeySettingObject(HashMap<String, Object> msg, String platform, JSONObject jsonSigningkey, KeyAndroidSetting keyAndroidSetting, KeyiOSSetting keyiOSSetting) {
        // Android Signing
        if (platform.equals(PayloadKeyType.android.name()) && keyAndroidSetting != null) {
            jsonSigningkey.put(PayloadKeyType.signingkeyID.name(), keyAndroidSetting.getKey_id());
            jsonSigningkey.put(PayloadKeyType.platform.name(), keyAndroidSetting.getPlatform());
            jsonSigningkey.put(PayloadKeyType.signingkey_path.name(), keyAndroidSetting.getAndroid_key_path());
            jsonSigningkey.put(PayloadKeyType.key_password.name(), keyAndroidSetting.getAndroid_key_password());
            jsonSigningkey.put(PayloadKeyType.android_key_store_password.name(), keyAndroidSetting.getAndroid_key_store_password());
            jsonSigningkey.put(PayloadKeyType.key_alias.name(), keyAndroidSetting.getAndroid_key_alias());
            jsonSigningkey.put(PayloadKeyType.adminID.name(), keyAndroidSetting.getAdmin_id());
        }
        // iOS Signing
        else if (platform.equals(PayloadKeyType.ios.name()) && keyiOSSetting != null) {
            jsonSigningkey.put(PayloadKeyType.signingkeyID.name(), keyiOSSetting.getKey_id());
            jsonSigningkey.put(PayloadKeyType.platform.name(), keyiOSSetting.getPlatform());
            jsonSigningkey.put(PayloadKeyType.signingkey_path.name(), keyiOSSetting.getIos_key_path());
            jsonSigningkey.put(PayloadKeyType.signingkey_type.name(), keyiOSSetting.getIos_key_type());
            msg.put("arrayJSONProfiles", keyiOSSetting.getIos_profiles_json());
            msg.put("arrayJSONCertificate", keyiOSSetting.getIos_certificates_json());
        }
    }

    private void createVcsSettingObject(JSONObject jsonRepository, VCSSetting vcsSetting) {
        if (vcsSetting != null) {
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(), vcsSetting.getVcs_url());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(), vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryId.name(), vcsSetting.getVcs_user_id());
        }
    }

    private void createBuildSettingMsg(HashMap<String, Object> msg, BuildProject buildProject, MemberLogin memberLogin, MemberMapping memberMapping, BuildProject buildProjectSelect, Workspace workspace, JSONObject jsonDeployObj) {
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(), buildProject.getProduct_type());
        msg.put(PayloadKeyType.platform.name(), buildProject.getPlatform());
        msg.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());
        msg.put(PayloadKeyType.userID.name(), memberMapping.getMember_id());
        msg.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id());
        msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id());
        msg.put(PayloadKeyType.projectDirName.name(), buildProjectSelect.getProject_dir_path());
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        //-- 일반앱은 현재 빌드셋팅을 따로 하지 않으므로, 빌드 셋팅 관련은 공백으로 전달한다.
        msg.put(PayloadKeyType.BuildSettings.name(), "");
        msg.put(PayloadKeyType.BuildSettingsGson.name(), "");
        msg.put(PayloadKeyType.appIconFileName.name(), "");
        msg.put("jsonDeployObj", jsonDeployObj.toJSONString());
    }

    private void createProjectGroupRole(ProjectGroupRole projectGroupRole, BuildProject buildProjectSelect, MemberLogin memberLogin, String workspace_group_role_id) {
        if (workspace_group_role_id.equals("") || Integer.parseInt(workspace_group_role_id) == 0) {
            projectGroupRole.setWorkspace_group_role_id(0);
        } else {
            projectGroupRole.setProject_group_role_id(Integer.parseInt(workspace_group_role_id));
        }

        if (memberLogin.getUser_role().contains("ADMIN")) {
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");
        } else {
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("1");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("1");
            projectGroupRole.setExport_yn("1");
        }
        projectGroupRole.setProject_id(buildProjectSelect.getProject_id().intValue());
    }

    private void createDeploySettingObj(JSONObject jsonDeployObj, BuildProject buildProject, BuildProject buildProjectSelect, Deploy deploy, String platform, String android_app_id, KeyAndroidSetting keyAndroid, KeyiOSSetting keyiOS) {
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;

        jsonDeployObj.put(PayloadKeyType.build_id.name(), buildProject.getProject_id());
        jsonDeployObj.put("all_package_name", android_app_id);

        if (platform.equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

            keyAndroid.setKey_id(keyAndroidSetting.getKey_id());
            keyAndroid.setBuilder_id(keyAndroidSetting.getBuilder_id());
            keyAndroid.setAdmin_id(keyAndroidSetting.getAdmin_id());
            keyAndroid.setDomain_id(keyAndroidSetting.getDomain_id());
            keyAndroid.setKey_name(keyAndroidSetting.getKey_name());
            keyAndroid.setBuilder_name(keyAndroidSetting.getBuilder_name());
            keyAndroid.setAdmin_name(keyAndroidSetting.getAdmin_name());
            keyAndroid.setPlatform(keyAndroidSetting.getPlatform());
            keyAndroid.setAndroid_key_type(keyAndroidSetting.getAndroid_key_type());
            keyAndroid.setAndroid_key_path(keyAndroidSetting.getAndroid_key_path());
            keyAndroid.setAndroid_deploy_key_path(keyAndroidSetting.getAndroid_deploy_key_path());
            keyAndroid.setAndroid_key_password(keyAndroidSetting.getAndroid_key_password());
            keyAndroid.setAndroid_key_alias(keyAndroidSetting.getAndroid_key_alias());
            keyAndroid.setAndroid_key_store_password(keyAndroidSetting.getAndroid_key_store_password());
            keyAndroid.setCreated_date(keyAndroidSetting.getCreated_date());
            keyAndroid.setUpdated_date(keyAndroidSetting.getUpdated_date());

            jsonDeployObj.put("googlePlayAccessJson", keyAndroid.getAndroid_deploy_key_path());

            deploy.setAll_package_name(android_app_id);
        } else if (platform.equals(PayloadKeyType.ios.name())) {
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

            keyiOS.setKey_id(keyiOSSetting.getKey_id());
            keyiOS.setBuilder_id(keyiOSSetting.getBuilder_id());
            keyiOS.setAdmin_id(keyiOSSetting.getAdmin_id());
            keyiOS.setDomain_id(keyiOSSetting.getDomain_id());
            keyiOS.setKey_name(keyiOSSetting.getKey_name());
            keyiOS.setBuilder_name(keyiOSSetting.getBuilder_name());
            keyiOS.setAdmin_name(keyiOSSetting.getAdmin_name());
            keyiOS.setDomain_name(keyiOSSetting.getDomain_name());
            keyiOS.setPlatform(keyiOSSetting.getPlatform());
            keyiOS.setIos_key_type(keyiOSSetting.getIos_key_type());
            keyiOS.setIos_release_type(keyiOSSetting.getIos_release_type());
            keyiOS.setIos_key_path(keyiOSSetting.getIos_key_path());
            keyiOS.setIos_debug_profile_path(keyiOSSetting.getIos_debug_profile_path());
            keyiOS.setIos_release_profile_path(keyiOSSetting.getIos_release_profile_path());
            keyiOS.setIos_key_password(keyiOSSetting.getIos_key_password());
            keyiOS.setIos_issuer_id(keyiOSSetting.getIos_issuer_id());
            keyiOS.setIos_key_id(keyiOSSetting.getIos_key_id());
            keyiOS.setIos_unlock_keychain_password(keyiOSSetting.getIos_unlock_keychain_password());
            keyiOS.setCreated_date(keyiOSSetting.getCreated_date());
            keyiOS.setUpdated_date(keyiOSSetting.getUpdated_date());
            keyiOS.setIos_profiles_json(keyiOSSetting.getIos_profiles_json());
            keyiOS.setIos_certificates_json(keyiOSSetting.getIos_certificates_json());

            jsonDeployObj.put("apple_key_id", keyiOS.getIos_key_id());
            jsonDeployObj.put("apple_issuer_id", keyiOS.getIos_issuer_id());
            jsonDeployObj.put("apple_app_store_connect_api_key", keyiOS.getIos_key_path());
        } else {
            log.error(platform + " 타입은 현재 지원하지 않는 플랫폼 입니다.");
        }

        deploy.setBuild_id(buildProjectSelect.getProject_id());
        deploy.setAll_signingkey_id(buildProject.getKey_id());
    }
}
