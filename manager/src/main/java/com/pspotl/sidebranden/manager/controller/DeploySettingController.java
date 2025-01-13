package com.pspotl.sidebranden.manager.controller;

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
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.signingkeysetting.KeyAndroidSetting;
import com.pspotl.sidebranden.common.signingkeysetting.KeyiOSSetting;
import com.pspotl.sidebranden.common.signingkeysetting.SigningKeySettingService;
import com.pspotl.sidebranden.common.vcssetting.VCSSetting;
import com.pspotl.sidebranden.common.vcssetting.VCSSettingService;
import com.pspotl.sidebranden.common.workspace.Workspace;
import com.pspotl.sidebranden.common.workspace.WorkspaceService;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketBuilderService;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 배포 설정 기능 및 조회 Controller
 */

@Slf4j
@RestController
public class DeploySettingController {

    @Autowired
    DeploySettingService deploySettingService;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuildSettingService buildSettingService;

    @Autowired
    BranchSettingService branchSettingService;

    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    VCSSettingService vcsSettingService;

    @Autowired
    SigningKeySettingService signingKeySettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    MemberService memberService;

    @Autowired
    Common common;

    @Autowired
    private ResponseUtility responseUtility;

    WebSocketSession websocketSession;

    private JSONParser parser = new JSONParser();

    private SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    // 배포 생성 기능
    @RequestMapping(value = "/manager/deploy/setting/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public void createDeploySetting(HttpServletRequest request, @RequestBody Deploy deploy){
        // 0. 테이블 vo 및 dao 생성하기
        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonRepository = new JSONObject();

        Workspace workspace;
        BuildProject buildProject;
        BuildSetting buildSetting;
        BranchSetting branchSetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        VCSSetting vcsSetting;


        buildProject = buildProjectService.findByWorkspaceId(deploy.getWorkspace_id(), deploy.getProject_name());

        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        workspace = workspaceService.findById(deploy.getWorkspace_id());

        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return;
        }

        // setting deploy json data
        deploy.setBuild_id(buildProject.getProject_id());
        // 1. 배포 설정 생성시 db 정보 new insert
        deploySettingService.insert(deploy);

        jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

        // android / ios platform 기준 분기 처리
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

            keyAndroidSetting = signingKeySettingService.findByAndroidID(deploy.getAll_signingkey_id());
            jsonDeployObj.put("all_package_name",deploy.getAll_package_name());
            jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_key_path());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(deploy.getAll_signingkey_id());
            jsonDeployObj.put("all_package_name",deploy.getAll_package_name());
            jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
            jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
            jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());

        }

        if (vcsSetting != null){
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        }



        // 2. 배포 정보 기준으로 send message map 값 설정
        HashMap<String, Object> msg = new HashMap<>();
        // 2.1 message type 추가하기
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_SETTING_INIT_INFO.name()); // sample msg type 새로운 msg type 필요함
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        msg.put("domain_id",memberLogin.getDomain_id()); // 추가
        msg.put("user_id",workspace.getMember_id()); // 조건에 따라서 추가하기
        msg.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
        msg.put("buildProjectdir", buildProject.getProject_dir_path());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put("jsonDeployObj", jsonDeployObj.toJSONString());
        msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());


        // 3. builder 로 send message 할 수 있게 구현

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);


    }

    // 배포 설정 조회
    @RequestMapping(value = "/manager/deploy/setting/search/getBuildId/{build_id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public Deploy selectDeploySetting(@PathVariable("build_id") Long build_id){
        // 1. 배포에 필요한 정보 id 값 가지고 오기
        return deploySettingService.findById(build_id);

    }

    /**  Deploy Task Setting DB datalist 조회 api
     *
     * @param request
     * @param deploy
     * @return deployTask
     */
    @RequestMapping(value = "/manager/deploy/setting/search/getDeployTask/{build_id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Deploy getDeployTaskDataOneList(HttpServletRequest request, @RequestBody Deploy deploy){
        Deploy deployTask = new Deploy();

        // TODO : 파라미터 설정 및 수정

        /**
         * deploy setting 관련 소스 코드 참조 하기 ..
         */
        deployTask = deploySettingService.findById(deploy.getBuild_id());


        // TODO : deploy 정보 vo 세팅

        return deployTask;
    }

    // TODO: builder 큐 관리 기능 추가
    /** Deploy Task Setting 실행 api
     *
     * @param request
     * @param deploy
     */
    @RequestMapping(value = "/manager/deploy/setting/update/task", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public void startDeployTaskDataSetting(HttpServletRequest request, @RequestBody Deploy deploy){

        // send msg 전에 body 값 설정
        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonRepository = new JSONObject();

        Workspace workspace;
        BuildProject buildProject;
        BuildSetting buildSetting;
        BranchSetting branchSetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        VCSSetting vcsSetting;


        buildProject = buildProjectService.findById(deploy.getProject_id());
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
        workspace = workspaceService.findById(deploy.getWorkspace_id());
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return;
        }

        // setting deploy json data
        deploy.setBuild_id(buildProject.getProject_id());

        jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

        // android / ios platform 기준 분기 처리
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

//            keyAndroidSetting = signingKeySettingService.findByAndroidID(deploy.getAll_signingkey_id());
//            jsonDeployObj.put("all_package_name",deploy.getAll_package_name());
//            jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_key_path());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){

            /**
             *  deploy task update data json set 설정 값 기능 파싱...
             */

//            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
//            jsonDeployObj.put("all_package_name","");
//            jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
//            jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
//            jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());


        }

        if (vcsSetting != null){
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        }



        // 2. 배포 정보 기준으로 send message map 값 설정
        HashMap<String, Object> msg = new HashMap<>();
        // 2.1 message type 추가하기
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO.name());
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        msg.put("domain_id",memberLogin.getDomain_id()); // 추가
        msg.put("user_id",workspace.getMember_id()); // 조건에 따라서 추가하기
        msg.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
        msg.put("buildProjectdir", buildProject.getProject_dir_path());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put("jsonDeployObj", jsonDeployObj.toJSONString());
        msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());

        /**
         *  jsonDeployTaskSetJson update data 전달하기 ...
         */
        msg.put("jsonDeployTaskSetJson", deploy.getDeployTaskSetJson().toJSONString());


        // 3. builder 로 send message 할 수 있게 구현

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        try {
//
//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//
//            websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));
//
//
//        } catch (IOException e) {
//
//            log.warn(e.getMessage(),e);
//        }

    }

    // TODO: builder 큐 관리 기능 추가
    /**  Deploy Task Setting data 조회 api
     *
     * @param request
     * @param deploy
     * @return deployTask
     */
    @RequestMapping(value = "/manager/deploy/setting/search/task", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public void startDeployTaskDataSearch(HttpServletRequest request, @RequestBody Deploy deploy){

        // send msg 전에 body 값 설정
        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonRepository = new JSONObject();

        Workspace workspace;
        BuildProject buildProject;
        BuildSetting buildSetting;
        BranchSetting branchSetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        VCSSetting vcsSetting;


        buildProject = buildProjectService.findById(deploy.getProject_id());
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
        workspace = workspaceService.findById(deploy.getWorkspace_id());
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return;
        }

        // setting deploy json data
        deploy.setBuild_id(buildProject.getProject_id());

        jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

        // android / ios platform 기준 분기 처리
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

//            keyAndroidSetting = signingKeySettingService.findByAndroidID(deploy.getAll_signingkey_id());
//            jsonDeployObj.put("all_package_name",deploy.getAll_package_name());
//            jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_key_path());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
//            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
//            jsonDeployObj.put("all_package_name","");
//            jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
//            jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
//            jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());


        }

        if (vcsSetting != null){
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        }



        // 2. 배포 정보 기준으로 send message map 값 설정
        HashMap<String, Object> msg = new HashMap<>();
        // 2.1 message type 추가하기
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO.name());
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        msg.put("domain_id",memberLogin.getDomain_id()); // 추가
        msg.put("user_id",workspace.getMember_id()); // 조건에 따라서 추가하기
        msg.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
        msg.put("buildProjectdir", buildProject.getProject_dir_path());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put("jsonDeployObj", jsonDeployObj.toJSONString());
        msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());


        // 3. builder 로 send message 할 수 있게 구현

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        try {
//
//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//
//            websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));
//
//
//        } catch (IOException e) {
//
//            log.warn(e.getMessage(),e);
//        }

    }

    // TODO: builder 큐 관리 기능 추가
    /** Deploy metadata Setting 실행 api
     *
     * @param request
     * @param deploy
     */
    @RequestMapping(value = "/manager/deploy/setting/update/metadata", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public void startDeployMetadataDataSetting(HttpServletRequest request, @RequestBody Deploy deploy){

        // send msg 전에 body 값 설정
        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonRepository = new JSONObject();

        Workspace workspace;
        BuildProject buildProject;
        BuildSetting buildSetting;
        BranchSetting branchSetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        VCSSetting vcsSetting;


        buildProject = buildProjectService.findById(deploy.getProject_id());
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
        workspace = workspaceService.findById(deploy.getWorkspace_id());
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return;
        }

        // setting deploy json data
        deploy.setBuild_id(buildProject.getProject_id());

        jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

        // android / ios platform 기준 분기 처리
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

//            keyAndroidSetting = signingKeySettingService.findByAndroidID(deploy.getAll_signingkey_id());
//            jsonDeployObj.put("all_package_name",deploy.getAll_package_name());
//            jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_key_path());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){

            /**
             *  deploy task update data json set 설정 값 기능 파싱...
             */

//            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
//            jsonDeployObj.put("all_package_name","");
//            jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
//            jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
//            jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());


        }

        if (vcsSetting != null){
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        }



        // 2. 배포 정보 기준으로 send message map 값 설정
        HashMap<String, Object> msg = new HashMap<>();
        // 2.1 message type 추가하기
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO.name());
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        msg.put("domain_id",memberLogin.getDomain_id()); // 추가
        msg.put("user_id",workspace.getMember_id()); // 조건에 따라서 추가하기
        msg.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
        msg.put("buildProjectdir", buildProject.getProject_dir_path());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put("jsonDeployObj", jsonDeployObj.toJSONString());
        msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());

        /**
         *  jsonDeployTaskSetJson update data 전달하기 ...
         */
        msg.put("jsonDeployMetadataSetJson", deploy.getDeployMetadataSetJson().toJSONString());


        // 3. builder 로 send message 할 수 있게 구현

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        try {
//
//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//
//            websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));
//
//
//        } catch (IOException e) {
//
//            log.warn(e.getMessage(),e);
//        }

    }

    // TODO: builder 큐 관리 기능 추가
    /**
     * api : /manager/deploy/setting/update/metadata/android/image
     * @param request
     * @param imagefile
     * @param projectDeployJson
     */
    @RequestMapping(value = "/manager/deploy/setting/update/metadata/android/image", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public void startAndroidDeployMetadataImageSetting(HttpServletRequest request, @RequestParam("phoneImagefile") List<MultipartFile> imagefile, @RequestParam("sevenInchTabletImagefile") List<MultipartFile> sevenInchTabletImagefile,
            @RequestParam("tenInchTabletImagefile") List<MultipartFile> tenInchTabletImagefile, @RequestParam("projectDeploytJson") String projectDeployJson){

        try {

            // send msg 전에 body 값 설정
            JSONObject jsonDeploySendToBuilder = new JSONObject();
            JSONObject jsonresultToBuilder = new JSONObject();
            JSONObject jsonDeployObj = new JSONObject();
            JSONObject jsonRepository = new JSONObject();

            Workspace workspace;
            BuildProject buildProject;
            BranchSetting branchSetting;
            VCSSetting vcsSetting;

            jsonDeploySendToBuilder = (JSONObject) parser.parse(projectDeployJson);

            buildProject = buildProjectService.findById(Long.valueOf(jsonDeploySendToBuilder.get("project_id").toString()));
            branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
            workspace = workspaceService.findById(buildProject.getWorkspace_id());
            vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);

            if(memberLogin == null){
                return;
            }

            jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
            }

            // 2. 배포 설정 정보 기준으로 jsonDeploySendToBuilder 값 설정

            jsonresultToBuilder.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            jsonresultToBuilder.put("domain_id",memberLogin.getDomain_id());
            jsonresultToBuilder.put("user_id",workspace.getMember_id());
            jsonresultToBuilder.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
            jsonresultToBuilder.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
            jsonresultToBuilder.put("buildProjectdir", buildProject.getProject_dir_path());
            jsonresultToBuilder.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            jsonresultToBuilder.put("jsonDeployObj", jsonDeployObj.toString());
            jsonresultToBuilder.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toString());


            /** resttemplate get post 방식으로 전달하기
             * 폼데이터 형식으로 전달하기
             * */

            try {

                MultiValueMap<String, Object> reqToFileObj = new LinkedMultiValueMap<String, Object>();

                requestFactory.setBufferRequestBody(false);
                requestFactory.setOutputStreaming(false);

                RestTemplate restTemplate = new RestTemplate(requestFactory);
                ObjectMapper objectMapper = new ObjectMapper();

                List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
                MappingJackson2HttpMessageConverter setMappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
                mc.add(setMappingJackson2HttpMessageConverter);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));

                // files array 전송 방식으로 처리해야함.
                /**
                 * List MultipartFile 객체 생성 및 등록
                 */
                List<ByteArrayResource> imageFileList = new ArrayList<>();
                List<ByteArrayResource> sevenInchTabletImagefileList = new ArrayList<>();
                List<ByteArrayResource> tenInchTabletImagefileList = new ArrayList<>();

                log.info(String.valueOf(imagefile.size()));
                for(int i = 0; i < imagefile.size(); i++){
                    MultipartFile multipartFile = imagefile.get(i);
                    // 파일 이름과 내용을 담는 MultipartFile 객체 생성
                    ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
                        @Override
                        public String getFilename() {
                            return multipartFile.getOriginalFilename();
                        }
                    };

                    imageFileList.add(i, fileResource);
                }

                for(int sevenI = 0 ; sevenI < sevenInchTabletImagefile.size(); sevenI++){
                    MultipartFile multipartFile = sevenInchTabletImagefile.get(sevenI);
                    // 파일 이름과 내용을 담는 MultipartFile 객체 생성
                    ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
                        @Override
                        public String getFilename() {
                            return multipartFile.getOriginalFilename();
                        }
                    };

                    sevenInchTabletImagefileList.add(sevenI, fileResource);
                }


                for(int tenI = 0 ; tenI < tenInchTabletImagefile.size(); tenI++){
                    MultipartFile multipartFile = tenInchTabletImagefile.get(tenI);
                    // 파일 이름과 내용을 담는 MultipartFile 객체 생성
                    ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
                        @Override
                        public String getFilename() {
                            return multipartFile.getOriginalFilename();
                        }
                    };

                    tenInchTabletImagefileList.add(tenI, fileResource);
                }

                reqToFileObj.add("projectDeployJson", jsonresultToBuilder);
                reqToFileObj.addAll("phoneImagefiles", imageFileList);
                reqToFileObj.addAll("sevenInchTabletImagefiles", sevenInchTabletImagefileList); // TODO : sevenInchTabletImagefile
                reqToFileObj.addAll("tenInchTabletImagefiles", tenInchTabletImagefileList); // TODO : tenInchTabletImagefile

                HttpEntity<MultiValueMap<String, Object>> requestBuilderAPI = new HttpEntity<MultiValueMap<String, Object>>(reqToFileObj, headers);
                String totalUrl = branchSetting.getBuilder_url() + "/builder/project/deploy/android/metadataImageFile";
                try {
                    String response = restTemplate.postForObject(totalUrl, requestBuilderAPI, String.class);
                    log.info("getUrlRestTemplete response | {}", response);

                } catch (ResourceAccessException e) {
                    log.info(e.getMessage());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }


    // TODO: builder 큐 관리 기능 추가
    /** iOS Deploy metadata image Setting 실행 api
     * form data 형식으로 처리 하기 ...
     *
     * @param request
     * @param imagefile
     * @param projectDeployJson
     */
    @RequestMapping(value = "/manager/deploy/setting/update/metadata/image", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> startDeployMetadataImageSetting(HttpServletRequest request, @RequestParam("imagefile") List<MultipartFile> imagefile
            , @RequestParam("projectDeploytJson") String projectDeployJson){

        try {

            // send msg 전에 body 값 설정
            JSONObject jsonDeploySendToBuilder = new JSONObject();
            JSONObject jsonresultToBuilder = new JSONObject();
            JSONObject jsonDeployObj = new JSONObject();
            JSONObject jsonRepository = new JSONObject();

            Workspace workspace;
            BuildProject buildProject;
            BranchSetting branchSetting;
            VCSSetting vcsSetting;

            jsonDeploySendToBuilder = (JSONObject) parser.parse(projectDeployJson);

            buildProject = buildProjectService.findById(Long.valueOf(jsonDeploySendToBuilder.get("project_id").toString()));
            branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
            workspace = workspaceService.findById(buildProject.getWorkspace_id());
            vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);

            if(memberLogin == null){
                return responseUtility.checkFailedResponse();
            }

            jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
            }

            // 2. 배포 설정 정보 기준으로 jsonDeploySendToBuilder 값 설정

            jsonresultToBuilder.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            jsonresultToBuilder.put("domain_id",memberLogin.getDomain_id());
            jsonresultToBuilder.put("user_id",workspace.getMember_id());
            jsonresultToBuilder.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
            jsonresultToBuilder.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
            jsonresultToBuilder.put("buildProjectdir", buildProject.getProject_dir_path());
            jsonresultToBuilder.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            jsonresultToBuilder.put("jsonDeployObj", jsonDeployObj.toString());
            jsonresultToBuilder.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toString());


            /** resttemplate get post 방식으로 전달하기
             * 폼데이터 형식으로 전달하기
             * */

            try {

                MultiValueMap<String, Object> reqToFileObj = new LinkedMultiValueMap<String, Object>();

                requestFactory.setBufferRequestBody(false);
                requestFactory.setOutputStreaming(false);

                RestTemplate restTemplate = new RestTemplate(requestFactory);
                ObjectMapper objectMapper = new ObjectMapper();

                List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
                MappingJackson2HttpMessageConverter setMappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
                mc.add(setMappingJackson2HttpMessageConverter);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));

                // files array 전송 방식으로 처리해야함.
                /**
                 * List MultipartFile 객체 생성 및 등록
                 */
                List<ByteArrayResource> imageFileList = new ArrayList<>();
                log.info(String.valueOf(imagefile.size()));
                for(int i = 0; i < imagefile.size(); i++){
                    MultipartFile multipartFile = imagefile.get(i);
                    // 파일 이름과 내용을 담는 MultipartFile 객체 생성
                    ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
                        @Override
                        public String getFilename() {
                            return multipartFile.getOriginalFilename();
                        }
                    };

                    imageFileList.add(i, fileResource);
                }

                reqToFileObj.add("projectDeployJson", jsonresultToBuilder);
                reqToFileObj.addAll("imageFiles", imageFileList);

                HttpEntity<MultiValueMap<String, Object>> requestBuilderAPI = new HttpEntity<MultiValueMap<String, Object>>(reqToFileObj, headers);
                String totalUrl = branchSetting.getBuilder_url() + "/builder/project/deploy/metadataImageFile";
                try {
                    String response = restTemplate.postForObject(totalUrl, requestBuilderAPI, String.class);
                    log.info("getUrlRestTemplete response | {}", response);
                    return responseUtility.makeSuccessResponse();

                } catch (ResourceAccessException e) {
                    log.info(e.getMessage());
                    return responseUtility.checkFailedResponse();
                }

            } catch (IOException e) {
                log.info(e.getMessage());
                return responseUtility.checkFailedResponse();
            }


        } catch (ParseException e) {
            log.info(e.getMessage());
            return responseUtility.checkFailedResponse();
        }


    }

    // TODO: builder 큐 관리 기능 추가
    /**
     *
     * startDeployMetadataDataImageScreenShotFileSearch
     * @param request
     * @param deploy
     */
    @RequestMapping(value = "/manager/deploy/setting/search/metadata/image", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public void startDeployMetadataDataImageScreenShotFileSearch(HttpServletRequest request, @RequestBody Deploy deploy){

        /**
         * TODO : image file 바이너리 조회 요청 파라미터 전송 기능 ...
         * 현재는 어떻게 해결할지 찾아봐야하는 상황이다..
         *
          */
        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonRepository = new JSONObject();

        Workspace workspace;
        BuildProject buildProject;
        BuildSetting buildSetting;
        BranchSetting branchSetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        VCSSetting vcsSetting;


        buildProject = buildProjectService.findById(deploy.getProject_id());
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
        workspace = workspaceService.findById(deploy.getWorkspace_id());
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return;
        }

        // setting deploy json data
        deploy.setBuild_id(buildProject.getProject_id());

        jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

        if (vcsSetting != null){
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        }



        // 2. 배포 정보 기준으로 send message map 값 설정
        HashMap<String, Object> msg = new HashMap<>();
        // 2.1 message type 추가하기
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_METADATA_SEARCH_STATUS_INFO.name());
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        msg.put("domain_id",memberLogin.getDomain_id()); // 추가
        msg.put("user_id",workspace.getMember_id()); // 조건에 따라서 추가하기
        msg.put(PayloadKeyType.workspace_id.name(),buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());
        msg.put("buildProjectdir", buildProject.getProject_dir_path());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put("jsonDeployObj", jsonDeployObj.toJSONString());
        msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());


        // 3. builder 로 send message 할 수 있게 구현


        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        try {
//
//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//
//            websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));
//
//
//        } catch (IOException e) {
//
//            log.warn(e.getMessage(),e);
//        }




    }


}
