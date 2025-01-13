package com.pspotl.sidebranden.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingService;
import com.pspotl.sidebranden.common.build.BuildProject;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.buildsetting.BuildSetting;
import com.pspotl.sidebranden.common.buildsetting.BuildSettingService;
import com.pspotl.sidebranden.common.deploy.Deploy;
import com.pspotl.sidebranden.common.deploy.DeploySettingService;
import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.role.ProjectGroupRole;
import com.pspotl.sidebranden.common.role.RoleService;
import com.pspotl.sidebranden.common.signingkeysetting.KeyAndroidSetting;
import com.pspotl.sidebranden.common.signingkeysetting.KeyiOSSetting;
import com.pspotl.sidebranden.common.signingkeysetting.SigningKeySettingService;
import com.pspotl.sidebranden.common.vcssetting.VCSSetting;
import com.pspotl.sidebranden.common.vcssetting.VCSSettingService;
import com.pspotl.sidebranden.common.workspace.MemberMapping;
import com.pspotl.sidebranden.common.workspace.Workspace;
import com.pspotl.sidebranden.common.workspace.WorkspaceService;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.util.FileNameAwareByteArrayResource;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ImportProjectController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private BuildProjectService buildProjectService;

    @Autowired
    private BuildSettingService buildSettingService;

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
    Common common;

    @Autowired
    private ResponseUtility responseUtility;

    JSONObject jsonBranchSetting = new JSONObject();
    JSONObject entity = new JSONObject(); // make success

    JSONParser parser = new JSONParser();

    private static final String JSONBRANCHSETTING = "jsonBranchSetting";

    private SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();


    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/project/import/multiProfileTemplateProject", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createMultiProfileTemplateProject(
            HttpServletRequest request,
            @RequestParam("zipFile") MultipartFile zipfile,
            @RequestParam("projectImportJson") String projectImportJson
    ) throws JsonProcessingException {

        // parse to multi profile map.
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, Object> multiProfileMap = objectMapper.readValue(projectImportJson, new TypeReference<Map<String, Object>>() {});

        // parse to buildProject
        BuildProject buildProject = objectMapper.convertValue(multiProfileMap.get("buildProject"), BuildProject.class);

        Workspace workspace;

        ArrayList<BuildSetting> buildSetting = null;
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();
        Deploy deploy = new Deploy();

        String workspaceGruopRoleID = (String) multiProfileMap.get("workspace_group_role_id");//multiProfileBuildObject.getWorkspace_group_role_id();

        VCSSetting vcsSetting;
        KeyAndroidSetting keyAndroidSetting = null;
        KeyiOSSetting keyiOSSetting = null;
        MemberLogin memberLogin = null;
        MemberMapping memberMapping;

        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonSigningkey = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonProfileDebug = new JSONObject();
        JSONObject jsonProfileRelease = new JSONObject();
        JSONObject jsonDeployObj = new JSONObject();

        /**
         *  import 할 파일 전송 기능 구현 이후
         *  아래 주석 처리 구간 해제 하고 작업 하기
         */
        if (buildProject.getProject_name() == null || buildProject.getProject_name().equals("")) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

//            if (loginData != null) {
//                memberLogin = memberService.findByUserLoginID(loginData.getUserLoginId());
//
//            } else {
//                return null;
//            }

        // target server insert
        // TODO : test 값 넣어둠 나중에 수정해야함
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getProject_queue_cnt() > builderQueueManaged.getBuild_queue_status_cnt()){
            builderQueueManaged.setProject_queue_status_cnt(builderQueueManaged.getProject_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        buildProjectService.insert(buildProject);

        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());

        // Project Group Role 테이블 추가 반영
        if (memberLogin.getUser_role().equals("SUPERADMIN")) {
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            if (workspaceGruopRoleID.equals("") || Integer.parseInt(workspaceGruopRoleID) == 0) {
                projectGroupRole.setWorkspace_group_role_id(0);
            } else {
                projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            }

            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        } else if (memberLogin.getUser_role().equals("ADMIN")) {
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            if (workspaceGruopRoleID.equals("") || Integer.parseInt(workspaceGruopRoleID) == 0) {
                projectGroupRole.setWorkspace_group_role_id(0);
            } else {
                projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            }

            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        } else {
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("1");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("1");
            projectGroupRole.setExport_yn("1");

        }


        roleService.insertToProjectGroupRole(projectGroupRole);


        workspace = workspaceService.findById(buildProject.getWorkspace_id());

        // buildSetting.setProject_id(buildProjectSelect.getProject_id()); // build_id setting

        // VCSSettingService
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        // buildSettingService.insert(buildSetting);

        // signing key Service
        if (buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        } else if (buildProject.getPlatform().toLowerCase().equals("ios")) {
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }

        jsonDeployObj.put(PayloadKeyType.build_id.name(), buildProject.getProject_id());

        // android / ios platform 기준 분기 처리
        if (buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());
            // package name data set 은 builder 내에서 처리하기
            jsonDeployObj.put("all_package_name", (String) multiProfileMap.get("packageName"));
            jsonDeployObj.put("googlePlayAccessJson", keyAndroidSetting.getAndroid_deploy_key_path());

            deploy.setBuild_id(buildProjectSelect.getProject_id());
            // deploy.setAll_package_name(buildSetting.get(0).getPackage_name());
            deploy.setAll_signingkey_id(buildProject.getKey_id());
            // 1. 배포 설정 생성시 db 정보 new insert
            deploySettingService.insert(deploy);

        } else if (buildProject.getPlatform().toLowerCase().equals("ios")) {
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
            // package name data set 은 builder 내에서 처리하기
            jsonDeployObj.put("all_package_name", (String) multiProfileMap.get("packageName"));
            jsonDeployObj.put("apple_key_id", keyiOSSetting.getIos_key_id());
            jsonDeployObj.put("apple_issuer_id", keyiOSSetting.getIos_issuer_id());
            jsonDeployObj.put("apple_app_store_connect_api_key", keyiOSSetting.getIos_key_path());

            deploy.setBuild_id(buildProjectSelect.getProject_id());
            deploy.setApple_key_id(keyiOSSetting.getIos_key_id());
            deploy.setApple_issuer_id(keyiOSSetting.getIos_issuer_id());
            deploy.setAll_signingkey_id(buildProject.getKey_id());
            // 1. 배포 설정 생성시 db 정보 new insert
            deploySettingService.insert(deploy);

        }

        HashMap<String, Object> msg = new HashMap<>();

        multiProfileMap.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        multiProfileMap.put(PayloadKeyType.product_type.name(), buildProject.getProduct_type());
        multiProfileMap.put(PayloadKeyType.platform.name(), buildProject.getPlatform());
        multiProfileMap.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());

        memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
        multiProfileMap.put(PayloadKeyType.userID.name(), memberMapping.getMember_id());

        String buildSettingJsonArr = objectMapper.writeValueAsString(buildSetting);

        log.info(" buildSettings : " + buildSettingJsonArr);

        multiProfileMap.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id()); // build project id
        multiProfileMap.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id()); // workspace name -> 다른 방식의 id 처리 방식이든
        multiProfileMap.put(PayloadKeyType.projectDirName.name(), buildProjectSelect.getProject_dir_path());
        multiProfileMap.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        // jsonMultiprofile.put(PayloadKeyType.BuildSettings.name(), buildSetting.toString()); // 처음에 array list 를 담아 둔다.
        // jsonMultiprofile.put(PayloadKeyType.BuildSettingsGson.name(), buildSettingJsonArr);
//            if (buildSetting.get(0).getIcon_image_path() == null || buildSetting.get(0).getIcon_image_path().toLowerCase().equals("")) {
        // jsonMultiprofile.put("appIconFileName", "");
//            } else {
        // jsonMultiprofile.put("appIconFileName", buildSetting.get(0).getIcon_image_path());
//            }

        multiProfileMap.put("jsonDeployObj", jsonDeployObj.toJSONString()); // json deploy obj

        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

        if (buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

        } else if (buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null) {
            jsonApplyConfig.put("release_type", keyiOSSetting.getIos_release_type());


        }

        // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
        if (vcsSetting != null) {
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(), vcsSetting.getVcs_url());
        }

        if (buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name()) && keyAndroidSetting != null) {

            jsonSigningkey.put(PayloadKeyType.signingkeyID.name(), keyAndroidSetting.getKey_id());
            jsonSigningkey.put(PayloadKeyType.platform.name(), keyAndroidSetting.getPlatform());
            jsonSigningkey.put(PayloadKeyType.signingkey_path.name(), keyAndroidSetting.getAndroid_key_path());
            jsonSigningkey.put("key_password", keyAndroidSetting.getAndroid_key_password());
            jsonSigningkey.put("profile_key", keyAndroidSetting.getAndroid_key_store_password());
            jsonSigningkey.put("android_key_store_password", keyAndroidSetting.getAndroid_key_store_password());
            jsonSigningkey.put("key_alias", keyAndroidSetting.getAndroid_key_alias());
            jsonSigningkey.put("profile_password", keyAndroidSetting.getAndroid_key_password());
            jsonSigningkey.put("adminID", keyAndroidSetting.getAdmin_id());

        } else if (buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null) {

            jsonSigningkey.put(PayloadKeyType.signingkeyID.name(), keyiOSSetting.getKey_id());
            jsonSigningkey.put(PayloadKeyType.platform.name(), keyiOSSetting.getPlatform());
            jsonSigningkey.put(PayloadKeyType.signingkey_path.name(), keyiOSSetting.getIos_key_path());

            jsonSigningkey.put("signingkey_type", keyiOSSetting.getIos_key_type());

        }

        if (buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null) {
            jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
        }

        if (buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null) {
            jsonProfileRelease.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_release_profile_path());
            multiProfileMap.put("arrayJSONProfiles", keyiOSSetting.getIos_profiles_json());
            multiProfileMap.put("arrayJSONCertificates", keyiOSSetting.getIos_certificates_json());
        }

        multiProfileMap.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toString());
        multiProfileMap.put("jsonSigningkey", jsonSigningkey.toString());
        multiProfileMap.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toString());
        multiProfileMap.put(JSONBRANCHSETTING, jsonBranchSetting.toString());

        multiProfileMap.put("projectName", buildProject.getProject_name());
        multiProfileMap.put("templateVersion", buildProject.getTemplate_version());
        multiProfileMap.put(PayloadKeyType.language.name(), buildProject.getPlatform_language());

        log.info(multiProfileMap.toString());
        // jsonMultiprofile.put("resultParse",msg);

        // TODO : resttemplate 이나 다른 http client lib 사용해서 builder controller file 값 및 파라미터 전송하기

        try {

            MultiValueMap<String, Object> reqToFileObj =
                    new LinkedMultiValueMap<String, Object>();

            requestFactory.setBufferRequestBody(false);
            requestFactory.setOutputStreaming(false);

            RestTemplate restTemplate = new RestTemplate(requestFactory);
            List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
            mc.add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
//        headers.add(HttpHeaders.COOKIE, "JSESSIONID="+cookies.getCookies().get(0).getValue());

            reqToFileObj.add("zipFile", new FileNameAwareByteArrayResource(zipfile.getOriginalFilename(), zipfile.getBytes(), ""));
            reqToFileObj.add("projectImportJson", multiProfileMap);

            HttpEntity<MultiValueMap<String, Object>> requestBuilderAPI = new HttpEntity<MultiValueMap<String, Object>>(reqToFileObj, headers);
            String totalUrl = branchSetting.getBuilder_url() + "/builder/project/import/Upload";
            try {
                 String response = restTemplate.postForObject(totalUrl, requestBuilderAPI, String.class);
                 log.info("getUrlRestTemplete response | {}", response);

            } catch (ResourceAccessException e) {
                log.info(e.getMessage());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // plugin list 기능 수행을 위한 map key
        return responseUtility.makeSuccessResponse(entity);
    }


}
