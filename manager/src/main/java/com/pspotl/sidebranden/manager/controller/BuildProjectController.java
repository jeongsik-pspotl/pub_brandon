package com.pspotl.sidebranden.manager.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingService;
import com.pspotl.sidebranden.common.build.*;
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
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
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
import com.pspotl.sidebranden.manager.client.ClientHandler;
import com.pspotl.sidebranden.manager.enums.*;
import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
import com.pspotl.sidebranden.manager.handler.WHiveWebSocketHandler;
import com.pspotl.sidebranden.manager.model.LoginSessionData;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketBuilderService;
import com.pspotl.sidebranden.manager.util.PostControllerException;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.Utility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class BuildProjectController {

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
    private PricingService pricingService;

    @Autowired
    DeploySettingService deploySettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    private ClientHandler clientHandler;

    @Autowired
    Common common;

    private Utility utility;

    @Value("${whive.distribution.deployExportDownloadPath}")
    private String exportDownloadDir;

    @Value("${whive.distribution.macPassword}")
    private String iosMacPassword;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.profiles}")
    private String springProfile;

    private final AmazonS3Client amazonS3 = new AmazonS3Client();

    WebSocketSession websocketSession;

    ObjectMapper objectMapper = new ObjectMapper();
    ConcurrentHashMap<Object, Object> branchSettings = new ConcurrentHashMap<Object, Object>();

//    JSONObject jsonBranchSetting = new JSONObject();
    JSONObject entity = new JSONObject(); // make success

    private static final String WINDOWS = "windows";
    private static final String SERVERCONFIG = "serverConfig";
    private static final String JSONBRANCHSETTING = "jsonBranchSetting";
    private static final String PROJECT_DIR_NAME = "project_dir_name";

    private static final String MODULELIST = "moduleList";

    private JSONParser parser = new JSONParser();

    // project 생성 시작 단계
    @RequestMapping(value = "/manager/build/project/create/vcs", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createBuildProject(HttpServletRequest request, @RequestBody AllBuildObject allBuildObject) throws JsonProcessingException {

        Workspace workspace;
        BuildProject buildProject = allBuildObject.getBuildProject();
        BuildSetting buildSetting = allBuildObject.getBuildSetting();
        ArrayList<Object> serverConfig = allBuildObject.getServerConfig();
        VCSSetting vcsSetting;
        KeyAndroidSetting keyAndroidSetting = null;
        KeyiOSSetting keyiOSSetting = null;
        MemberLogin memberLogin = null;
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();

        String workspaceGruopRoleID = allBuildObject.getWorkspace_group_role_id();

        log.info(serverConfig.toString());

        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonSigningkey = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonProfileDebug = new JSONObject();
        JSONObject jsonProfileRelease = new JSONObject();

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        buildProjectService.insert(buildProject);
        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());

        // Project Group Role 테이블 추가 반영
        if(memberLogin.getUser_role().equals("SUPERADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            projectGroupRole.setWorkspace_group_role_id(Integer.parseInt("0"));
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else if(memberLogin.getUser_role().equals("ADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            projectGroupRole.setWorkspace_group_role_id(Integer.parseInt("0"));
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else {
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

        buildSetting.setProject_id(buildProjectSelect.getProject_id()); // build_id setting

        // VCSSettingService
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        buildSettingService.insert(buildSetting);

        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }


        // HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO
        if(!buildProjectSelect.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProjectSelect.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProjectSelect.getPlatform());
            msg.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());
            msg.put(PayloadKeyType.userID.name(), workspace.getMember_id());
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id());
            msg.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id());
            msg.put(PayloadKeyType.projectDirName.name(), buildProjectSelect.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put(SERVERCONFIG,serverConfig.toString());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

            if(buildSetting != null){
                jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),buildSetting.getApp_id());
                jsonApplyConfig.put(PayloadKeyType.AppName.name(),buildSetting.getApp_name());
                jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),buildSetting.getApp_version());
                jsonApplyConfig.put(PayloadKeyType.AppVersionCode.name(),buildSetting.getApp_version_code());

            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
            }

            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());
            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){

                jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
                jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());

            }

            if(buildSetting != null){
                jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),buildSetting.getApp_id());
                jsonApplyConfig.put(PayloadKeyType.AppName.name(),buildSetting.getApp_name());
                jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),buildSetting.getApp_version());
                jsonApplyConfig.put(PayloadKeyType.AppVersionCode.name(),buildSetting.getApp_version_code());
                jsonApplyConfig.put(PayloadKeyType.MinTargetVersion.name(),buildSetting.getMin_target_version());

            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            }

            // android, ios 기준에 따라서 들어가는 input 값이 달라짐..
            // android 기준
            // platform, signingkey_path, key_password, profile_key, key_alias, profile_password
            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                if (keyAndroidSetting != null ){
                        jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyAndroidSetting.getKey_id());
                        jsonSigningkey.put(PayloadKeyType.platform.name(),keyAndroidSetting.getPlatform());
                        jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyAndroidSetting.getAndroid_key_path());
                        jsonSigningkey.put("key_password",keyAndroidSetting.getAndroid_key_password());
                        jsonSigningkey.put("profile_key",keyAndroidSetting.getAndroid_key_store_password());
                        jsonSigningkey.put("key_alias",keyAndroidSetting.getAndroid_key_alias());
                        jsonSigningkey.put("profile_password",keyAndroidSetting.getAndroid_key_password());
                        jsonSigningkey.put("adminID",keyAndroidSetting.getAdmin_id());
                }

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){


                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyiOSSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyiOSSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyiOSSetting.getIos_key_path());
                jsonSigningkey.put("signingkey_type",keyiOSSetting.getIos_key_type());

                // signingkey 하나가 아니라 여러개의 signingkey, debug profile, release profile 키를 추가로 값을 넣어서 처리해야함...
                if(keyiOSSetting.getIos_key_password() != null || !keyiOSSetting.getIos_key_password().equals("")){
                    jsonSigningkey.put("password",keyiOSSetting.getIos_key_password());
                }


            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
            }

            // android, ios 기준에 따라서 들어가는 input 값이 달라짐..

            jsonRepository.put(PayloadKeyType.zipFileName.name(),buildSetting.getZip_file_name());

            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put("jsonSigningkey", jsonSigningkey.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));
            msg.put("jsonProfileDebug", jsonProfileDebug.toJSONString());
            msg.put("jsonProfileRelease", jsonProfileRelease.toJSONString());

            log.info(msg.toString());

            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSettings.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);

            if(websocketSession != null){
                try {
                    synchronized(websocketSession) {
                        websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
                    }
                } catch (IOException ex) {

                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }

            }

        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }


    // template project 생성 기능
    @RequestMapping(value = "/manager/build/project/create/templateProject", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createTemplateProject(HttpServletRequest request, @RequestBody AllBuildObject allBuildObject) throws JsonProcessingException {
        Workspace workspace;
        BuildProject buildProject = allBuildObject.getBuildProject();
        BuildSetting buildSetting = allBuildObject.getBuildSetting();
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();

        ArrayList<Object> serverConfig = allBuildObject.getServerConfig();
        String workspaceGruopRoleID = allBuildObject.getWorkspace_group_role_id();

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

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);


        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        buildProjectService.insert(buildProject);

        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());

        // Project Group Role 테이블 추가 반영
        if(memberLogin.getUser_role().equals("SUPERADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            if(workspaceGruopRoleID.equals("") || Integer.parseInt(workspaceGruopRoleID) == 0){
                projectGroupRole.setWorkspace_group_role_id(0);
            }else {
                projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            }

            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else if(memberLogin.getUser_role().equals("ADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            if(workspaceGruopRoleID.equals("") || Integer.parseInt(workspaceGruopRoleID) == 0){
                projectGroupRole.setWorkspace_group_role_id(0);
            }else {
                projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            }

            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else {
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

        buildSetting.setProject_id(buildProjectSelect.getProject_id()); // build_id setting

        // VCSSettingService
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        buildSettingService.insert(buildSetting);

        // signing key Service
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }



        // HV_MSG_PROJECT_TEMPLATE_CREATE_INFO
        if(!buildProject.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_TEMPLATE_CREATE_INFO.name());

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

            memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
            msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());


            msg.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id()); // build project id
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id()); // workspace name -> 다른 방식의 id 처리 방식이든
            msg.put(PayloadKeyType.projectDirName.name(), buildProjectSelect.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put(SERVERCONFIG,serverConfig.toString());
            // msg 템플릿 버전 추가

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());

            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){
                jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
                jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());
            }

            if(buildSetting != null){
                jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),buildSetting.getApp_id());
                jsonApplyConfig.put(PayloadKeyType.AppName.name(),buildSetting.getApp_name());
                jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),buildSetting.getApp_version());
                jsonApplyConfig.put(PayloadKeyType.AppVersionCode.name(),buildSetting.getApp_version_code());
                jsonApplyConfig.put(PayloadKeyType.MinTargetVersion.name(),buildSetting.getMin_target_version());

            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            }


            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name()) && keyAndroidSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyAndroidSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyAndroidSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyAndroidSetting.getAndroid_key_path());
                jsonSigningkey.put("key_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("profile_key",keyAndroidSetting.getAndroid_key_store_password());
                jsonSigningkey.put("key_alias",keyAndroidSetting.getAndroid_key_alias());
                jsonSigningkey.put("profile_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("adminID",keyAndroidSetting.getAdmin_id());
                // signingkey domain, admin, signingkey_type

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyiOSSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyiOSSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyiOSSetting.getIos_key_path());


                jsonSigningkey.put("signingkey_type",keyiOSSetting.getIos_key_type());


                if(keyiOSSetting.getIos_key_password() != null || !keyiOSSetting.getIos_key_password().equals("")){
                    jsonSigningkey.put("password",keyiOSSetting.getIos_key_password());
                }
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
            }

            jsonRepository.put(PayloadKeyType.zipFileName.name(),buildSetting.getZip_file_name());


            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put("jsonSigningkey", jsonSigningkey.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));
            msg.put("jsonProfileDebug", jsonProfileDebug.toJSONString());
            msg.put("jsonProfileRelease", jsonProfileRelease.toJSONString());
            msg.put("projectName", buildProject.getProject_name());
            msg.put("templateVersion",buildProject.getTemplate_version());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());
            msg.put("appIconFileName",buildSetting.getZip_file_name());

            log.info(msg.toString());


            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSettings.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);

            if (websocketSession != null){
                try {
                    synchronized(websocketSession) {
                        websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
                    }
                } catch (IOException ex) {
                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }
            }
        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }

    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/build/project/create/multiProfileTemplateProject", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createMultiProfileTemplateProject(HttpServletRequest request, @RequestBody MultiProfileBuildObject multiProfileBuildObject) throws JsonProcessingException {
        Workspace workspace;
        BuildProject buildProject = multiProfileBuildObject.getBuildProject();
        ArrayList<BuildSetting> buildSetting = multiProfileBuildObject.getBuildSetting();
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();
        Deploy deploy = new Deploy();

        String workspaceGruopRoleID = multiProfileBuildObject.getWorkspace_group_role_id();

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

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);


        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());


        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getProject_queue_cnt() > builderQueueManaged.getProject_queue_status_cnt()){
            builderQueueManaged.setProject_queue_cnt(builderQueueManaged.getProject_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(),MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        buildProjectService.insert(buildProject);

        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());

        // Project Group Role 테이블 추가 반영
        if(memberLogin.getUser_role().equals("SUPERADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            if(workspaceGruopRoleID.equals("") || Integer.parseInt(workspaceGruopRoleID) == 0){
                projectGroupRole.setWorkspace_group_role_id(0);
            }else {
                projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            }

            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else if(memberLogin.getUser_role().equals("ADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            if(workspaceGruopRoleID.equals("") || Integer.parseInt(workspaceGruopRoleID) == 0){
                projectGroupRole.setWorkspace_group_role_id(0);
            }else {
                projectGroupRole.setWorkspace_group_role_id(Integer.parseInt(workspaceGruopRoleID));
            }

            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else {
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
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }

        jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

        // android / ios platform 기준 분기 처리
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());
            // package name data set 은 builder 내에서 처리하기
            jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
            jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_deploy_key_path());

            deploy.setBuild_id(buildProjectSelect.getProject_id());
            deploy.setAll_package_name(buildSetting.get(0).getPackage_name());
            deploy.setAll_signingkey_id(buildProject.getKey_id());
            // 1. 배포 설정 생성시 db 정보 new insert
            deploySettingService.insert(deploy);

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
            // package name data set 은 builder 내에서 처리하기
            log.info(buildSetting.get(0).getApp_id());
            jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
            jsonDeployObj.put("ios_app_bundleID",buildSetting.get(0).getApp_id());
            jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
            jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
            jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());

            deploy.setBuild_id(buildProjectSelect.getProject_id());
            deploy.setApple_key_id(keyiOSSetting.getIos_key_id());
            deploy.setApple_issuer_id(keyiOSSetting.getIos_issuer_id());
            deploy.setAll_signingkey_id(buildProject.getKey_id());
            // 1. 배포 설정 생성시 db 정보 new insert
            deploySettingService.insert(deploy);

        }



        // HV_MSG_PROJECT_TEMPLATE_CREATE_INFO
        if(!buildProject.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();
            // msgtype 추가 명시하고
            // builder 내에서 처리하는 방법도 달라야한다.
            // 해당 나머지 처리해야하는 구간은 고민을 해야한다.

            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_MULTI_PROFILE_TEMPLATE_CREATE_INFO.name());

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

            memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
            msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

            String buildSettingJsonArr = objectMapper.writeValueAsString(buildSetting);

            log.info(" buildSettings : "+buildSettingJsonArr);

            msg.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id()); // build project id
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id()); // workspace name -> 다른 방식의 id 처리 방식이든
            msg.put(PayloadKeyType.projectDirName.name(), buildProjectSelect.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put(PayloadKeyType.BuildSettings.name(), buildSetting.toString()); // 처음에 array list 를 담아 둔다.
            msg.put(PayloadKeyType.BuildSettingsGson.name(),  buildSettingJsonArr);
            if(buildSetting.get(0).getIcon_image_path() == null || buildSetting.get(0).getIcon_image_path().toLowerCase().equals("")){
                msg.put("appIconFileName","");
            }else {
                msg.put("appIconFileName",buildSetting.get(0).getIcon_image_path());
            }

            msg.put("jsonDeployObj", jsonDeployObj.toJSONString()); // json deploy obj

            // 나머지 setting 하지 말아야 할 것을 정리한다.

            // msg 템플릿 버전 추가

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                 // jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){
                 // jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
                jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());


            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            }


            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name()) && keyAndroidSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyAndroidSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyAndroidSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyAndroidSetting.getAndroid_key_path());
                jsonSigningkey.put("key_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("profile_key",keyAndroidSetting.getAndroid_key_store_password());
                jsonSigningkey.put("key_alias",keyAndroidSetting.getAndroid_key_alias());
                jsonSigningkey.put("profile_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("adminID",keyAndroidSetting.getAdmin_id());
                // signingkey domain, admin, signingkey_type

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyiOSSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyiOSSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyiOSSetting.getIos_key_path());


                jsonSigningkey.put("signingkey_type",keyiOSSetting.getIos_key_type());


//                if(keyiOSSetting.getIos_key_password() != null || !keyiOSSetting.getIos_key_password().equals("")){
//                    jsonSigningkey.put("password",keyiOSSetting.getIos_key_password());
//                }


            }




            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
                msg.put("arrayJSONProfiles",keyiOSSetting.getIos_profiles_json());
                msg.put("arrayJSONCertificates",keyiOSSetting.getIos_certificates_json());
            }

            // jsonRepository.put(PayloadKeyType.zipFileName.name(),buildSetting.getZip_file_name());

            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put("jsonSigningkey", jsonSigningkey.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));

            msg.put("projectName", buildProject.getProject_name());
            msg.put("templateVersion",buildProject.getTemplate_version());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());
//            msg.put("appIconFileName",buildSetting.getZip_file_name());

            log.info(msg.toString());

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
            // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            if(websocketSession != null){
//                try {
//                    synchronized(websocketSession) {
//                         websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//                    }
//                } catch (IOException ex) {
//
//                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//                }
//
//            }

        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }

    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/build/project/create/vcsMultiProfiles", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createVcsMultiProfilesProject(HttpServletRequest request, @RequestBody MultiProfileBuildObject multiProfileBuildObject) throws JsonProcessingException {

        Workspace workspace;
        BuildProject buildProject = multiProfileBuildObject.getBuildProject();
        ArrayList<BuildSetting> buildSetting = multiProfileBuildObject.getBuildSetting();
        // ArrayList<Object> serverConfig = multiProfileBuildObject.getServerConfig();
        VCSSetting vcsSetting;
        KeyAndroidSetting keyAndroidSetting = null;
        KeyiOSSetting keyiOSSetting = null;
        MemberLogin memberLogin = null;
        MemberMapping memberMapping;
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();
        Deploy deploy = new Deploy();

        String workspaceGruopRoleID = multiProfileBuildObject.getWorkspace_group_role_id();

        // log.info(serverConfig.toString());

        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonSigningkey = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonProfileDebug = new JSONObject();
        JSONObject jsonProfileRelease = new JSONObject();
        JSONObject jsonDeployObj = new JSONObject();

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getProject_queue_cnt() > builderQueueManaged.getProject_queue_status_cnt()){
            builderQueueManaged.setProject_queue_cnt(builderQueueManaged.getProject_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(),MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        buildProjectService.insert(buildProject);
        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());

        // Project Group Role 테이블 추가 반영
        if(memberLogin.getUser_role().equals("SUPERADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            projectGroupRole.setWorkspace_group_role_id(Integer.parseInt("0"));
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else if(memberLogin.getUser_role().equals("ADMIN")){
            projectGroupRole.setProject_id(Integer.valueOf(String.valueOf(buildProjectSelect.getProject_id())));
            projectGroupRole.setWorkspace_group_role_id(Integer.parseInt("0"));
            projectGroupRole.setRead_yn("1");
            projectGroupRole.setUpdate_yn("0");
            projectGroupRole.setDelete_yn("0");
            projectGroupRole.setBuild_yn("1");
            projectGroupRole.setDeploy_yn("0");
            projectGroupRole.setExport_yn("0");

        }else {
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

        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }


        // HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO
        if(!buildProjectSelect.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();

            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_MULTI_PROFILE_VCS_CLONE_INFO.name());

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

            memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
            msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

            String buildSettingJsonArr = toJsonString(buildSetting);

            msg.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id()); // build project id
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id()); // workspace name -> 다른 방식의 id 처리 방식이든
            msg.put(PayloadKeyType.projectDirName.name(), buildProjectSelect.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put(PayloadKeyType.BuildSettings.name(), buildSetting.toString()); // 처음에 array list 를 담아 둔다.
            msg.put(PayloadKeyType.BuildSettingsGson.name(),  buildSettingJsonArr);

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                // jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){
                // jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
                jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());
            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            }


            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name()) && keyAndroidSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyAndroidSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyAndroidSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyAndroidSetting.getAndroid_key_path());
                jsonSigningkey.put("key_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("profile_key",keyAndroidSetting.getAndroid_key_store_password());
                jsonSigningkey.put("key_alias",keyAndroidSetting.getAndroid_key_alias());
                jsonSigningkey.put("profile_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("adminID",keyAndroidSetting.getAdmin_id());
                // signingkey domain, admin, signingkey_type

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyiOSSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyiOSSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyiOSSetting.getIos_key_path());


                jsonSigningkey.put("signingkey_type",keyiOSSetting.getIos_key_type());

            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
                msg.put("arrayJSONProfiles",keyiOSSetting.getIos_profiles_json());
                msg.put("arrayJSONCertificates",keyiOSSetting.getIos_certificates_json());
            }

            jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

            // android / ios platform 기준 분기 처리
            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

                keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());
                // package name data set 은 builder 내에서 처리하기
                jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
                jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_deploy_key_path());

                deploy.setBuild_id(buildProjectSelect.getProject_id());
                deploy.setAll_package_name(buildSetting.get(0).getPackage_name());
                deploy.setAll_signingkey_id(buildProject.getKey_id());
                // 1. 배포 설정 생성시 db 정보 new insert
                deploySettingService.insert(deploy);

            }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
                keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
                // package name data set 은 builder 내에서 처리하기
                jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
                jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
                jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
                jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());

                deploy.setBuild_id(buildProjectSelect.getProject_id());
                deploy.setApple_key_id(keyiOSSetting.getIos_key_id());
                deploy.setApple_issuer_id(keyiOSSetting.getIos_issuer_id());
                deploy.setAll_signingkey_id(buildProject.getKey_id());
                // 1. 배포 설정 생성시 db 정보 new insert
                deploySettingService.insert(deploy);

            }




            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
                msg.put("arrayJSONProfiles",keyiOSSetting.getIos_profiles_json());
                msg.put("arrayJSONCertificates",keyiOSSetting.getIos_certificates_json());
            }

            jsonRepository.put(PayloadKeyType.zipFileName.name(),"");

            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put("jsonSigningkey", jsonSigningkey.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));

            msg.put("projectName", buildProject.getProject_name());
            msg.put("templateVersion",buildProject.getTemplate_version());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());
            if(buildSetting.get(0).getIcon_image_path() == null || buildSetting.get(0).getIcon_image_path().toLowerCase().equals("")){
                msg.put("appIconFileName","");
            }else {
                msg.put("appIconFileName",buildSetting.get(0).getIcon_image_path());
            }

            msg.put("jsonDeployObj", jsonDeployObj.toJSONString()); // json deploy obj

            log.info(msg.toString());

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
            // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            if(websocketSession != null){
//                try {
//                    synchronized(websocketSession) {
//                        websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//                    }
//                } catch (IOException ex) {
//
//                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//                }
//
//            }

        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }

    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/build/project/update/multiProfileConfig", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatemultiProfileConfig(HttpServletRequest request, @RequestBody MultiProfileBuildObject multiProfileBuildObject) throws JsonProcessingException {
        Workspace workspace;
        BuildProject buildProject = multiProfileBuildObject.getBuildProject();
        ArrayList<BuildSetting> buildSetting = multiProfileBuildObject.getBuildSetting();
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();

        String workspaceGruopRoleID = multiProfileBuildObject.getWorkspace_group_role_id();

        VCSSetting vcsSetting;
        KeyAndroidSetting keyAndroidSetting = null;
        KeyiOSSetting keyiOSSetting = null;
        MemberLogin memberLogin = null;
        MemberMapping memberMapping;
        Deploy deploy = new Deploy();

        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonSigningkey = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonProfileDebug = new JSONObject();
        JSONObject jsonProfileRelease = new JSONObject();
        JSONObject jsonDeployObj = new JSONObject();

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getProject_queue_cnt() > builderQueueManaged.getProject_queue_status_cnt()){
            builderQueueManaged.setProject_queue_cnt(builderQueueManaged.getProject_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(),MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        workspace = workspaceService.findById(buildProject.getWorkspace_id());

        // VCSSettingService
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        // signing key Service
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }



        // HV_MSG_PROJECT_TEMPLATE_CREATE_INFO
        if(!buildProject.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();
            // msgtype 추가 명시하고
            // builder 내에서 처리하는 방법도 달라야한다.
            // 해당 나머지 처리해야하는 구간은 고민을 해야한다.
            // TODO : builder 모듈에서 msg 전달 기능이 전혀 없음 해당 기능 구현하고 진행해아함.
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO.name());

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

            memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
            msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

            String buildSettingJsonArr = toJsonString(buildSetting);
            log.info(buildSettingJsonArr);

            msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id()); // build project id
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id()); // workspace name -> 다른 방식의 id 처리 방식이든
            msg.put(PayloadKeyType.projectDirName.name(), buildProject.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put(PayloadKeyType.BuildSettings.name(), buildSetting.toString()); // 처음에 array list 를 담아 둔다.
            msg.put(PayloadKeyType.BuildSettingsGson.name(),  buildSettingJsonArr);
            if(buildSetting.get(0).getIcon_image_path() == null || buildSetting.get(0).getIcon_image_path().toLowerCase().equals("")){
                msg.put("appIconFileName","");
            }else {
                msg.put("appIconFileName",buildSetting.get(0).getIcon_image_path());
            }

            // 나머지 setting 하지 말아야 할 것을 정리한다.

            // msg 템플릿 버전 추가

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                // jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){
                // jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
                jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());
            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            }

            jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

            // android / ios platform 기준 분기 처리
            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

                keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());
                // package name data set 은 builder 내에서 처리하기
                jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
                jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_deploy_key_path());

                deploy = deploySettingService.findById(buildProject.getProject_id());

                if(deploy == null){
                   deploy = new Deploy();

                   deploy.setBuild_id(buildProject.getProject_id());
                   deploy.setAll_package_name(buildSetting.get(0).getPackage_name());
                   deploy.setAll_signingkey_id(buildProject.getKey_id());
                   deploySettingService.insert(deploy);
                }else {

                }





            }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
                keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
                // package name data set 은 builder 내에서 처리하기
                jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
                jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
                jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
                jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());
                jsonDeployObj.put("ios_app_bundleID", buildSetting.get(0).getApp_id());

                deploy = deploySettingService.findById(buildProject.getProject_id());

                if(deploy == null){
                    deploy = new Deploy();

                    deploy.setBuild_id(buildProject.getProject_id());
                    deploy.setApple_key_id(keyiOSSetting.getIos_key_id());
                    deploy.setApple_issuer_id(keyiOSSetting.getIos_issuer_id());
                    deploy.setAll_signingkey_id(buildProject.getKey_id());
                    deploySettingService.insert(deploy);
                }else {

                }

            }


            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name()) && keyAndroidSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyAndroidSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyAndroidSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyAndroidSetting.getAndroid_key_path());
                jsonSigningkey.put("key_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("profile_key",keyAndroidSetting.getAndroid_key_store_password());
                jsonSigningkey.put("key_alias",keyAndroidSetting.getAndroid_key_alias());
                jsonSigningkey.put("profile_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("adminID",keyAndroidSetting.getAdmin_id());
                // signingkey domain, admin, signingkey_type

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyiOSSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyiOSSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyiOSSetting.getIos_key_path());
                msg.put("arrayJSONProfiles",keyiOSSetting.getIos_profiles_json());
                msg.put("arrayJSONCertificates",keyiOSSetting.getIos_certificates_json());

                log.info(keyiOSSetting.toString());
                jsonSigningkey.put("signingkey_type",keyiOSSetting.getIos_key_type());


//                if(keyiOSSetting.getIos_key_password() != null || !keyiOSSetting.getIos_key_password().equals("")){
                    jsonSigningkey.put("password",iosMacPassword);
//                }


            }



            // ios profile update 처리 기능 내용을 보니 소스 코드 내 구현이 안되어 있음
            // 해당 구간 수정 작업을 진행해야함.
            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
            }

            // jsonRepository.put(PayloadKeyType.zipFileName.name(),buildSetting.getZip_file_name());

            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put("jsonSigningkey", jsonSigningkey.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));
            // msg.put("jsonProfileDebug", jsonProfileDebug.toJSONString());
            // msg.put("jsonProfileRelease", jsonProfileRelease.toJSONString());
            msg.put("projectName", buildProject.getProject_name());
            msg.put("templateVersion",buildProject.getTemplate_version());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());
            msg.put("jsonDeployObj", jsonDeployObj.toJSONString()); // json deploy obj

            log.info(msg.toString());


            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProject.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }


    @RequestMapping(value = "/manager/build/project/update/vcsMultiProfiles", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateVcsmultiProfileConfig(HttpServletRequest request, @RequestBody MultiProfileBuildObject multiProfileBuildObject) throws JsonProcessingException {
        Workspace workspace;
        BuildProject buildProject = multiProfileBuildObject.getBuildProject();
        ArrayList<BuildSetting> buildSetting = multiProfileBuildObject.getBuildSetting();
        ProjectGroupRole projectGroupRole = new ProjectGroupRole();

        String workspaceGruopRoleID = multiProfileBuildObject.getWorkspace_group_role_id();

        VCSSetting vcsSetting;
        KeyAndroidSetting keyAndroidSetting = null;
        KeyiOSSetting keyiOSSetting = null;
        MemberLogin memberLogin = null;
        MemberMapping memberMapping;
        Deploy deploy = new Deploy();

        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonSigningkey = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonProfileDebug = new JSONObject();
        JSONObject jsonProfileRelease = new JSONObject();
        JSONObject jsonDeployObj = new JSONObject();

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        HttpSession session = request.getSession();
        LoginSessionData loginData = (LoginSessionData) session.getAttribute(SessionKeyContents.KEY_LOGIN_DATA.name());

        if(loginData != null){
            memberLogin = memberService.findByUserLoginID(loginData.getUserLoginId());

        }else {
            return null;
        }


        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getProject_queue_cnt() > builderQueueManaged.getProject_queue_status_cnt()){
            builderQueueManaged.setProject_queue_cnt(builderQueueManaged.getProject_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(),MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }

        // buildProjectService.insert(buildProject);

        // BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());


        // roleService.insertToProjectGroupRole(projectGroupRole);


        workspace = workspaceService.findById(buildProject.getWorkspace_id());

        // buildSetting.setProject_id(buildProjectSelect.getProject_id()); // build_id setting

        // VCSSettingService
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        // buildSettingService.insert(buildSetting);

        // signing key Service
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {
            keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());

        }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());

        }



        // HV_MSG_PROJECT_TEMPLATE_CREATE_INFO
        if(!buildProject.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();
            // msgtype 추가 명시하고
            // builder 내에서 처리하는 방법도 달라야한다.
            // 해당 나머지 처리해야하는 구간은 고민을 해야한다.

            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO.name());

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

            memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
            msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

            String buildSettingJsonArr = toJsonString(buildSetting);
            log.info(buildSettingJsonArr);

            msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id()); // build project id
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id()); // workspace name -> 다른 방식의 id 처리 방식이든
            msg.put(PayloadKeyType.projectDirName.name(), buildProject.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), loginData.getUserLoginId());
            msg.put(PayloadKeyType.BuildSettings.name(), buildSetting.toString()); // 처음에 array list 를 담아 둔다.
            msg.put(PayloadKeyType.BuildSettingsGson.name(),  buildSettingJsonArr);
            if(buildSetting.get(0).getIcon_image_path() == null || buildSetting.get(0).getIcon_image_path().toLowerCase().equals("")){
                msg.put("appIconFileName","");
            }else {
                msg.put("appIconFileName",buildSetting.get(0).getIcon_image_path());
            }

            // 나머지 setting 하지 말아야 할 것을 정리한다.

            // msg 템플릿 버전 추가

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());

            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                // jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){
                // jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
                jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());

            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            }

            jsonDeployObj.put(PayloadKeyType.build_id.name(),buildProject.getProject_id());

            // android / ios platform 기준 분기 처리
            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())) {

                keyAndroidSetting = signingKeySettingService.findByAndroidID(buildProject.getKey_id());
                // package name data set 은 builder 내에서 처리하기
                jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
                jsonDeployObj.put("googlePlayAccessJson",keyAndroidSetting.getAndroid_deploy_key_path());

                deploy = deploySettingService.findById(buildProject.getProject_id());

                if(deploy == null){
                    deploy = new Deploy();

                    deploy.setBuild_id(buildProject.getProject_id());
                    deploy.setAll_package_name(buildSetting.get(0).getPackage_name());
                    deploy.setAll_signingkey_id(buildProject.getKey_id());
                    deploySettingService.insert(deploy);
                }else {

                }

            }else if(buildProject.getPlatform().toLowerCase().equals("ios")){
                keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
                // package name data set 은 builder 내에서 처리하기
                jsonDeployObj.put("all_package_name",buildSetting.get(0).getPackage_name());
                jsonDeployObj.put("ios_app_bundleID",buildSetting.get(0).getApp_id());
                jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
                jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
                jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());

                deploy = deploySettingService.findById(buildProject.getProject_id());

                if(deploy == null){
                    deploy = new Deploy();

                    deploy.setBuild_id(buildProject.getProject_id());
                    deploy.setApple_key_id(keyiOSSetting.getIos_key_id());
                    deploy.setApple_issuer_id(keyiOSSetting.getIos_issuer_id());
                    deploy.setAll_signingkey_id(buildProject.getKey_id());
                    deploySettingService.insert(deploy);
                }else {

                }

            }


            if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name()) && keyAndroidSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyAndroidSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyAndroidSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyAndroidSetting.getAndroid_key_path());
                jsonSigningkey.put("key_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("profile_key",keyAndroidSetting.getAndroid_key_store_password());
                jsonSigningkey.put("key_alias",keyAndroidSetting.getAndroid_key_alias());
                jsonSigningkey.put("profile_password",keyAndroidSetting.getAndroid_key_password());
                jsonSigningkey.put("adminID",keyAndroidSetting.getAdmin_id());
                // signingkey domain, admin, signingkey_type

            }else if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null){

                jsonSigningkey.put(PayloadKeyType.signingkeyID.name(),keyiOSSetting.getKey_id());
                jsonSigningkey.put(PayloadKeyType.platform.name(),keyiOSSetting.getPlatform());
                jsonSigningkey.put(PayloadKeyType.signingkey_path.name(),keyiOSSetting.getIos_key_path());
                msg.put("arrayJSONProfiles",keyiOSSetting.getIos_profiles_json());
                msg.put("arrayJSONCertificates",keyiOSSetting.getIos_certificates_json());

                log.info(keyiOSSetting.toString());
                jsonSigningkey.put("signingkey_type",keyiOSSetting.getIos_key_type());

//                if(keyiOSSetting.getIos_key_password() != null || !keyiOSSetting.getIos_key_password().equals("")){
                jsonSigningkey.put("password",iosMacPassword);
//                }

            }

            // ios profile update 처리 기능 내용을 보니 소스 코드 내 구현이 안되어 있음
            // 해당 구간 수정 작업을 진행해야함.
            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            }

            if(buildProject.getPlatform().toLowerCase().equals("ios") && keyiOSSetting != null ){
                jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
            }

            // jsonRepository.put(PayloadKeyType.zipFileName.name(),buildSetting.getZip_file_name());


            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put("jsonSigningkey", jsonSigningkey.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));
            // msg.put("jsonProfileDebug", jsonProfileDebug.toJSONString());
            // msg.put("jsonProfileRelease", jsonProfileRelease.toJSONString());
            msg.put("projectName", buildProject.getProject_name());
            msg.put("templateVersion",buildProject.getTemplate_version());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());
            msg.put("jsonDeployObj", jsonDeployObj.toJSONString()); // json deploy obj

            log.info(msg.toString());

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            if(websocketSession != null){
//                try {
//                    synchronized(websocketSession) {
//                        websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//                    }
//                } catch (IOException ex) {
//
//                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//                }
//
//            }

        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProject.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }

    // project template 수정 기능
    @RequestMapping(value = "/manager/build/project/update/templateProject", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateTemplateProject(HttpServletRequest request, @RequestBody AllBuildObject allBuildObject) throws JsonProcessingException {
        Workspace workspace;
        MemberMapping memberMapping;
        BuildProject buildProject = allBuildObject.getBuildProject();
        BuildSetting buildSetting = allBuildObject.getBuildSetting();
        ArrayList<Object> serverConfig = allBuildObject.getServerConfig();
        String packageName = allBuildObject.getPackageName();

        VCSSetting vcsSetting;

        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();

        if( buildProject.getProject_name() == null  || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        // target server insert
        BranchSetting branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());
        // update

        BuildProject buildProjectSelect = buildProjectService.findByWorkspaceId(buildProject.getWorkspace_id(), buildProject.getProject_name());

        workspace = workspaceService.findById(buildProject.getWorkspace_id());

        buildSetting.setProject_id(buildProjectSelect.getProject_id()); // build_id setting
        buildSetting.setPackage_name(packageName);

        // VCSSettingService
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        // update

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        // HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO
        if(!buildProjectSelect.getPlatform().toLowerCase().equals(WINDOWS)){
            HashMap<String, Object> msg = new HashMap<>();
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_TEMPLATE_UPDATE_INFO.name());

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
            msg.put(PayloadKeyType.platform.name(),buildProjectSelect.getPlatform());
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());


            // member role superadmin/admin 일 경우 조회 해서 처리
            memberMapping = workspaceService.findByMemberRoleWorkspaceID(workspace.getWorkspace_id());
            msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id());
            msg.put(PayloadKeyType.projectID.name(), buildProjectSelect.getProject_id());
            msg.put(PayloadKeyType.projectDirPath.name(), buildProjectSelect.getProject_dir_path());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());
            msg.put(SERVERCONFIG,serverConfig.toString());
            // msg 템플릿 버전 추가

            // set json
            // 추가로 받을 param json 타입 분석...
            branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
            branchSettings.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());

            if(buildProjectSelect.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
                jsonApplyConfig.put(PayloadKeyType.PackageName.name(),packageName);
            }else if(buildProjectSelect.getPlatform().toLowerCase().equals("ios")){
                jsonApplyConfig.put(PayloadKeyType.branchUserId.name(),packageName);
            }

            if(buildSetting != null){
                jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),buildSetting.getApp_id());
                jsonApplyConfig.put(PayloadKeyType.AppName.name(),buildSetting.getApp_name());
                jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),buildSetting.getApp_version());
                jsonApplyConfig.put(PayloadKeyType.AppVersionCode.name(),buildSetting.getApp_version_code());
                jsonApplyConfig.put(PayloadKeyType.MinTargetVersion.name(),buildSetting.getMin_target_version());

            }

            // 환경설정 id 값을 가지고 동적 처리 ... vcs, branch, ftp ...
            if (vcsSetting != null){
                jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            }

            jsonRepository.put(PayloadKeyType.zipFileName.name(),buildSetting.getZip_file_name());

            msg.put(PayloadKeyType.jsonRepository.name(), jsonRepository.toJSONString());
            msg.put(PayloadKeyType.jsonApplyConfig.name(), jsonApplyConfig.toJSONString());
            msg.put(JSONBRANCHSETTING, toJsonString(branchSettings));
            msg.put("projectName", buildProject.getProject_name());
            msg.put("templateVersion",buildProject.getTemplate_version());
            msg.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());

            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSettings.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);

            if(websocketSession != null){
                try {
                    synchronized(websocketSession) {
                        websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
                    }
                } catch (IOException ex) {
                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }
            }
        }

        // plugin list 기능 수행을 위한 map key
        entity.put(PayloadKeyType.build_id.name(), buildProjectSelect.getProject_id());

        return responseUtility.makeSuccessResponse(entity);

    }

    // 라이센스 체크 기능
    @RequestMapping(value = "/manager/build/project/checkLicense", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPlatformAppIDLicenseCheck(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        JSONObject jsonentity = new JSONObject(); // make success

        ArrayList<String> AndroidAppIDList = new ArrayList<String>();
        ArrayList<String> iOSAppIDList = new ArrayList<String>();
        ArrayList<String> platformTypeList = new ArrayList<String>();

        // app id, platform
        String platform = payload.get(PayloadKeyType.platform.name()).toString();
        String AppID = payload.get("appID").toString();


        // onpremiss 일경우 whive 라이센스 체크
        if(springProfile.equals("onpremiss")){
            // license key 상에 Android, iOS AppID List 가져오기
//                utility.checkHybridLicense(AndroidAppIDList, iOSAppIDList, platformTypeList);

            if(!Utility.getIsDemoLicense()) {
                if (AppID != null && platform != null) {

                    if(!platformTypeList.contains(platform)){
                        System.err.println("Platform does not match to license file. please reissue license.");
                        jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_PLATFORM.getMessage());
                        return responseUtility.checkFailedResponse(jsonentity);
                    }

                    if(platform.toLowerCase().equals(PayloadKeyType.android.name())){
                        if (!AndroidAppIDList.contains(AppID)) {
                            System.err.println("Real Android AppID does not match to license file. please reissue license.");
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_ANDROID_APPID.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }else if(platform.toLowerCase().equals("ios")){
                        if (!iOSAppIDList.contains(AppID)) {
                            System.err.println("Real iOS AppID does not match to license file. please reissue license.");
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_iOS_APPID.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }

                } else {
                    // null이면 pass (client legacy)
                }
            }else {
                // Demo License이면 appID Check pass
            }


        }else{
            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);
            /**
             * whive 서비스 일 경우 라이센스 기반이 아난 사용자 정보 내 에서 app id 기준으로 조회 된다.
             */
            try {
                JSONObject appIDJSON = (JSONObject) parser.parse(memberLogin.getAppid_json().toString());
                log.info(appIDJSON.toJSONString());
                if(memberLogin.getPay_change_yn() == null || memberLogin.getPay_change_yn().equals("N")){
                    if(platform.toLowerCase().equals(PayloadKeyType.android.name())){
                        if(!appIDJSON.get("androidAppID1").toString().equals(AppID)){
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }
                    }

                }else {
                    if(platform.toLowerCase().equals(PayloadKeyType.android.name())){
                        if(!appIDJSON.get("androidAppID1").toString().equals(AppID) && !appIDJSON.get("androidAppID2").toString().equals(AppID)){
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }else if(platform.toLowerCase().equals(PayloadKeyType.ios.name())){
                        if(!appIDJSON.get("iOSAppID1").toString().equals(AppID) && !appIDJSON.get("iOSAppID2").toString().equals(AppID)){
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }
                }


            } catch (ParseException e) {
                log.warn(e.getMessage(), e);
                jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                return responseUtility.checkFailedResponse(jsonentity);
            }catch (NullPointerException e){
                jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                return responseUtility.checkFailedResponse(jsonentity);
            }
        }




        return responseUtility.makeSuccessResponse();
    }

    // 플러그인 리스트 조회 기능
    @RequestMapping(value = "/api/buildproject/pluginlist", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProjectToPluginlist(HttpServletRequest request, @RequestBody Map<String, Object> payload) throws JsonProcessingException {
        BuildProject buildProject;
        VCSSetting vcsSetting;

        String buildID = payload.get(PayloadKeyType.build_id.name()).toString();

        // build project service
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(buildID));
        buildProject = buildProjectService.findById(Long.valueOf(buildID));
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        // branch setting service
        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PLUGIN_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(),payload.get(PayloadKeyType.product_type.name()).toString());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        // member role superadmin/admin 일 경우 조회 해서 처리
        MemberMapping memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

        // domain, user, workspace, project
        msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());
        msg.put(PayloadKeyType.workspaceID.name(), buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), payload.get(PayloadKeyType.build_id.name()).toString());
        msg.put(PayloadKeyType.projectDirName.name(), payload.get(PROJECT_DIR_NAME).toString());

        branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
        branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());
        branchSettings.put("repositoryVcsType", vcsSetting.getVcs_type());
        branchSettings.put(PayloadKeyType.repositoryURL.name(), vcsSetting.getVcs_url());
        branchSettings.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
        branchSettings.put(PayloadKeyType.repositoryPassword.name(), vcsSetting.getVcs_user_pwd());
        branchSettings.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());


        msg.put(PayloadKeyType.branchSettingObj.name(), toJsonString(branchSettings));

        try {
            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSettings.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);

            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
        } catch (IOException e) {
                
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }


        return responseUtility.makeSuccessResponse();
    }

    // 플러그인 추가 삭제 기능
    @RequestMapping(value = "/api/buildproject/pluginupdate", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> setProjectToPluginAdd(@RequestBody Map<String, Object> payload) throws JsonProcessingException {

        String buildID = payload.get(PayloadKeyType.build_id.name()).toString();

        // build project service
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(buildID));
        BuildProject buildProject = buildProjectService.findById(Long.valueOf(buildID));
        VCSSetting vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        // branch setting service
        HashMap<String, Object> msg = new HashMap<>();
        String pluginStatus = payload.get("pluginActStatus").toString();

        if(pluginStatus.toLowerCase().equals("add")){
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PLUGIN_ADD_LIST_INFO.name());
            // commit msg 처리 구간 변경 해야함
            msg.put("commitMessage", payload.get("moduleName").toString() + "Added");
        }else if(pluginStatus.toLowerCase().equals("remove")){
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name());
            // commit msg 처리 구간 변경 해야함
            msg.put("commitMessage", payload.get("moduleName").toString() + "Removed");
        }

        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());

        msg.put("workspacePath", payload.get(PayloadKeyType.workspace_name.name()).toString());
        msg.put("projectPath", payload.get("project_name").toString());
        msg.put(PayloadKeyType.projectDirName.name(), payload.get(PROJECT_DIR_NAME).toString());
        msg.put(MODULELIST, payload.get(MODULELIST).toString()); // moduleList
        // 파라미터 추가
        msg.put("vcsPush", true);

        branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
        branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());
        branchSettings.put("repositoryVcsType", vcsSetting.getVcs_type());
        branchSettings.put(PayloadKeyType.repositoryId.name(), vcsSetting.getVcs_user_id());
        branchSettings.put(PayloadKeyType.repositoryPassword.name(), vcsSetting.getVcs_user_pwd());
        branchSettings.put(PayloadKeyType.hqKey.name(), payload.get(PayloadKeyType.hqKey.name()).toString());

        msg.put(PayloadKeyType.branchSettingObj.name(), toJsonString(branchSettings));
        log.info(msg.toString());

        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSettings.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
        if(websocketSession != null){
            try {
                synchronized(websocketSession) {
                    websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
                }
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
        }

        return responseUtility.makeSuccessResponse();
    }

    // TODO: builder 큐 관리 기능 추가
    // template plugin list 조회 기능
    @RequestMapping(value = "/manager/build/project/search/pluginList", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTemplatePluginList(HttpServletRequest request, @RequestBody Map<String, Object> payload) throws JsonProcessingException {

        String buildID = payload.get(PayloadKeyType.build_id.name()).toString();

        // build project service
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(buildID));
        BuildProject buildProject = buildProjectService.findById(Long.valueOf(buildID));

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getEtc_queue_cnt() > builderQueueManaged.getEtc_queue_status_cnt()){
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() + 1);
            builderQueueManagedService.projectUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        // branch setting service
        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_TEMPLATE_PLUGIN_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(), payload.get(PayloadKeyType.product_type.name()).toString());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }


        // member role superadmin/admin 일 경우 조회 해서 처리
        MemberMapping memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

        // domain, user, workspace, project
        msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());
        msg.put(PayloadKeyType.workspaceID.name(), buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), payload.get(PayloadKeyType.build_id.name()).toString());
        msg.put(PayloadKeyType.projectDirName.name(), payload.get(PROJECT_DIR_NAME).toString());

        branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
        branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());
        branchSettings.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        msg.put(PayloadKeyType.branchSettingObj.name(), toJsonString(branchSettings));

        // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);

//            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));


        return responseUtility.makeSuccessResponse();
    }

    // TODO: builder 큐 관리 기능 추가
    // template plugin 수정 기능
    @RequestMapping(value = "/manager/build/project/update/plugin", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> setTemplatePluginUpdate(HttpServletRequest request, @RequestBody Map<String, Object> payload) throws JsonProcessingException {

        String buildID = payload.get(PayloadKeyType.build_id.name()).toString();
        MemberMapping memberMapping;

        // build project service
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(buildID));
        BuildProject buildProject = buildProjectService.findById(Long.valueOf(buildID));
        VCSSetting vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getEtc_queue_cnt() > builderQueueManaged.getEtc_queue_status_cnt()){
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() + 1);
            builderQueueManagedService.etcUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        // branch setting service
        HashMap<String, Object> msg = new HashMap<>();

        // HV_MSG_TEMPLATE_PLUGIN_INFO 로 통일하기
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_TEMPLATE_PLUGINS_INFO.name());

        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());
        msg.put(PayloadKeyType.product_type.name(), payload.get(PayloadKeyType.product_type.name()).toString()); // product type 추가

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

        // member role superadmin/admin 일 경우 조회 해서 처리
        memberMapping = workspaceService.findByMemberRoleWorkspaceID(buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());



        msg.put(PayloadKeyType.workspaceID.name(), buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), buildID);
        msg.put(PayloadKeyType.projectDirName.name(), payload.get(PROJECT_DIR_NAME).toString());
        msg.put(MODULELIST, payload.get(MODULELIST).toString()); // moduleListMODULELIST

        branchSettings.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
        branchSettings.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());
        branchSettings.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
        branchSettings.put(PayloadKeyType.repositoryURL.name(), vcsSetting.getVcs_url());
        branchSettings.put(PayloadKeyType.repositoryId.name(), vcsSetting.getVcs_user_id());
        branchSettings.put(PayloadKeyType.repositoryPassword.name(), vcsSetting.getVcs_user_pwd());

        branchSettings.put(PayloadKeyType.hqKey.name(),memberLogin.getUser_login_id());

        msg.put(PayloadKeyType.branchSettingObj.name(), toJsonString(branchSettings));
        log.info(msg.toString());

//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
        // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//                }
//            } catch (IOException ex) {
//
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//            }
//
//        }


        return responseUtility.makeSuccessResponse();


    }

    @RequestMapping(value = "/manager/build/project/search/platformSelect", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<PlatformSelectAll> selectProgrammingAllList(HttpServletRequest request){
        PlatformSelectAll platformSelectAll = new PlatformSelectAll();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);
        Pricing pricing = pricingService.findById(memberLogin.getUser_id());
        if(pricing == null){
            memberLogin.setPay_change_yn("N");
        }else {
            memberLogin.setPay_change_yn(pricing.getPay_change_yn());
        }


        List<PlatformSelectAll> platformSelectAllList = buildProjectService.selectPlatformAll();

        for(int i =-0 ; i < platformSelectAllList.size(); i++){
            platformSelectAll = platformSelectAllList.get(i);
            if(memberLogin.getPay_change_yn() == null || memberLogin.getPay_change_yn().equals("N")){
                if(platformSelectAll.getRole_code_name().toLowerCase().equals("ios")){
                    platformSelectAllList.remove(i);
                }
            }

        }
        return platformSelectAllList;
    }

    // project 생성 시 Programming List 조회
    @RequestMapping(value = "/manager/build/project/search/programLanguageList/{platform}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<ProgrammingSelectAll> selectProgrammingAllList(@PathVariable("platform") String platform){
        return buildProjectService.selectFindAll(platform);
    }

    // project 설정 화면에서 Programming List 결과 조회
    @RequestMapping(value = "/manager/build/project/search/programLanguage/{role_code_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ProgrammingSelectAll selectProgrammingFindId(@PathVariable("role_code_id") String role_code_id){
        return buildProjectService.selectFindID(role_code_id);
    }

    //
    @RequestMapping(value = "/api/builproject", method = RequestMethod.POST)
    public String createBuildProjectAll(@RequestBody BuildProject buildProject) {
        if( buildProject.getProject_name() == null    || buildProject.getProject_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        buildProjectService.insert(buildProject);

        return "redirect:/websquare/websquare.html?w2xPath=/xml/workspace.xml";
    }

    // 사용 안함
    @RequestMapping(value = "/api/buildprojects", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<BuildProject> findAll() {
        return buildProjectService.findAll();
    }


    @RequestMapping(value = "/manager/build/project/search/projectRoles", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<BuildProject> findRoleAll(HttpServletRequest request) {
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if (memberLogin != null) {
            //TODO : service profile type 추가
            if(springProfile.equals("onpremiss") || memberLogin.getUser_role().equals("SUPERADMIN")){
                return buildProjectService.findRoleCodeAll(memberLogin.getUser_id());
            }else {
                return buildProjectService.findServiceTierAll(memberLogin.getEmail());
            }

        } else {
            return new ArrayList<BuildProject>();
        }
    }

    @GetMapping(value = "/manager/build/project/search/projectConfig/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody BuildProject findById(@PathVariable("id") Long id) {
        return buildProjectService.findById(id);
    }

    @GetMapping(value = "/api/builproject/{workspace_id}/{project_name}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody BuildProject findById(@PathVariable("workspace_id") Long id, @PathVariable("project_name") String name) {

        return buildProjectService.findByWorkspaceId(id, name);
    }

    // 기존에 project name |
    @RequestMapping(value = "/manager/build/project/search/checkProjectName/{project_name}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findByWorkspaceNameCheck(HttpServletRequest request, @PathVariable("project_name") String projectName) {
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        log.info(" ====== WorkspaceController | Workspace | workspace_name : {}",projectName);
        BuildProject buildProject = null;
        // project name 조건 외에도 추가로 domain, memberid 키 값 추가하기
        buildProject = buildProjectService.findByProjectNameCheck(Long.valueOf(memberLogin.getDomain_id()), memberLogin.getUser_id(), projectName);
        JSONObject obj = new JSONObject();
        if(buildProject != null){
            obj.put("projectname_not_found","no");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            obj.put("projectname_not_found","yes");
            return responseUtility.makeSuccessResponse(obj);

        }

    }

    // project export 기능 구현
    @RequestMapping(value = "/manager/build/project/export/start", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> exportProjectSourceFileStart(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        HashMap<String, Object> msg = new HashMap<>();

        String buildID = payload.get(PayloadKeyType.build_id.name()).toString();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }else {
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());
            msg.put(PayloadKeyType.userID.name(),memberLogin.getUser_id());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
        }

        // build project service
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(buildID));

        BuildProject buildProject = buildProjectService.findById(Long.valueOf(buildID));

        msg.put(SessionType.MsgType.name(), ClientMessageType.MV_MSG_PROJECT_EXPORT_ZIP_REQUEST_INFO_BRNACH.toString());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());

        // if 구간 조건 추가 해서 해당 구간 수정하기
        if(buildProject.getProject_dir_path().toLowerCase().equals("") || buildProject.getProject_dir_path() == null){
            // msg.put("path", ManagerDirectoryType.DOMAIN_ + payload.get(PayloadKeyType.domainID.name()).toString() + "/" + ManagerDirectoryType.USER_ + payload.get(PayloadKeyType.userID.name()).toString() + "/" + ManagerDirectoryType.WORKSPACE_W + buildProject.getWorkspace_id() +"/"+ ManagerDirectoryType.PROJECT_P + buildProject.getProject_id());
            msg.put("path", ManagerDirectoryType.DOMAIN_ + memberLogin.getDomain_id() + "/" + ManagerDirectoryType.USER_ + memberLogin.getUser_id() + "/" + ManagerDirectoryType.WORKSPACE_W + buildProject.getWorkspace_id() +"/"+ ManagerDirectoryType.PROJECT_P + buildProject.getProject_id() +"/"+ ManagerDirectoryType.PROJECT_P + buildProject.getProject_id() );
        }else {
            // msg.put("path", ManagerDirectoryType.DOMAIN_ + payload.get(PayloadKeyType.domainID.name()).toString() + "/" + ManagerDirectoryType.USER_ + payload.get(PayloadKeyType.userID.name()).toString() + "/" + ManagerDirectoryType.WORKSPACE_W + buildProject.getWorkspace_id()+"/"+ ManagerDirectoryType.PROJECT_P + buildProject.getProject_id() +"/"+buildProject.getProject_dir_path());
            msg.put("path", ManagerDirectoryType.DOMAIN_ + memberLogin.getDomain_id() + "/" + ManagerDirectoryType.USER_ + memberLogin.getUser_id() + "/" + ManagerDirectoryType.WORKSPACE_W + buildProject.getWorkspace_id() +"/"+ ManagerDirectoryType.PROJECT_P + buildProject.getProject_id() +"/"+ ManagerDirectoryType.PROJECT_P + buildProject.getProject_id() );
        }

        //msg.put(PayloadKeyType.domainID.name(),payload.get(PayloadKeyType.domainID.name()).toString());
        //msg.put(PayloadKeyType.userID.name(),payload.get(PayloadKeyType.userID.name()).toString());

        msg.put(PayloadKeyType.workspaceID.name(), buildProject.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());
        msg.put(PayloadKeyType.projectDirName.name(), buildProject.getProject_dir_path());
        msg.put(PayloadKeyType.platform.name(), buildProject.getPlatform());


//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//                }
//            } catch (IOException ex) {
//
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//            }
//
//        }


        return responseUtility.checkSuccessResponse();

    }

    @RequestMapping(value = "/manager/build/project/export/upload", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> exportProjectSourceFile(@RequestParam(value ="file", required = false) MultipartFile file, @RequestParam("filePath") String filePath, @RequestParam("filename") String filename, @RequestParam("hqKey") String hqKey) throws IOException {

        // msg type MV_MSG_PROJECT_EXPORT_ZIP_REQUEST_INFO_BRNACH 로 요청

        String getFilename = file.getOriginalFilename();
        BufferedOutputStream outputStream = null;

        MemberLogin memberLogin = memberService.findByUserLoginID(hqKey);

        byte[] bytes = new byte[0];
        bytes = file.getBytes();

        try {

            File userDir = new File(exportDownloadDir+"/"+memberLogin.getUser_id().toString());

            if(userDir != null && !userDir.exists()){
                userDir.mkdir();
            }else {

            }


            outputStream = new BufferedOutputStream(
                    new FileOutputStream(
                            new File(exportDownloadDir+"/"+memberLogin.getUser_id().toString()+"/", getFilename)));
            outputStream.write(bytes);
            outputStream.flush();

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        } finally {
            if(outputStream != null){
                outputStream.close();
            }

        }


        HashMap<String, Object> msg = new HashMap<>();
        msg.put(SessionType.MsgType.name(), ClientMessageType.MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO_HEADQUATER.toString());
        msg.put(PayloadKeyType.filename.name(),filename);
        // 메시지 타입 및 데이터 파라미터 정리하기

        /**
         *  TODO : ws manager 로 전달하는 기능 구현해야함.
         */
//        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
//        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
//        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());
//
//        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(hqKey, SessionType.HEADQUATER);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
        if(websocketSession != null){
            try {
                synchronized(websocketSession) {
                    websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
                }
            } catch (IOException ex) {

                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        }

        return responseUtility.checkSuccessResponse();

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @RequestMapping(value = "/manager/build/project/export/download", method = RequestMethod.POST, produces= MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> getExportZipDownloadAws(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> payload) throws UnsupportedEncodingException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        InputStream in = null;
        OutputStream os = null;
        File file = null;
        S3ObjectInputStream objectInputStream = null;

        try {

            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);


            if(springProfile.equals("onpremiss")){
                String zipFileName = payload.get("zipfileName").toString();
                String savePath = exportDownloadDir + "/" + memberLogin.getUser_id().toString() + "/";
                String client = request.getHeader("User-Agent");

                //파일 다운로드 헤더 지정
                response.reset();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Description", "JSP Generated Data");

                file = new File(savePath, zipFileName);

                if (file.exists()) {
                    in = new FileInputStream(file);

                    // IE
                    if (client.contains("MSIE")) {
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "\\ ") + "\"");
                    }

                    // IE 11 이상.
                    else if (client.contains("Trident")) {
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "\\ ") + "\"");
                    }

                    // 한글 파일명 처리
                    else {
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(zipFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
                        response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
                    }

                    response.setHeader("Content-Length", "" + file.length());

                    os = response.getOutputStream();
                    byte[] bytes = new byte[(int) file.length()];
                    int leng = 0;
                    while ((leng = in.read(bytes)) > 0) {
                        os.write(bytes, 0, leng);
                    }

                } else {
                    response.setContentType("text/html;charset=UTF-8");
                    log.info("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
                }
            }else {
                S3Object o;
                byte[] bytes = new byte[1024];
                HttpHeaders httpHeaders = new HttpHeaders();
                String zipFileUrl = payload.get("zipFileUrl").toString();

                String fileNameFirst = FilenameUtils.getBaseName(zipFileUrl);

                String extension = FilenameUtils.getExtension(zipFileUrl);

                o = amazonS3.getObject(new GetObjectRequest(bucket, zipFileUrl));

                objectInputStream = o.getObjectContent();
                bytes = IOUtils.toByteArray(objectInputStream);
                log.info(fileNameFirst+"."+extension);
                String fileName = URLEncoder.encode(fileNameFirst+"."+extension, "UTF-8").replaceAll("\\+", "%20");
                httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                httpHeaders.setContentLength(bytes.length);
                httpHeaders.setContentDispositionFormData("attachment", fileName);
//            httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
                httpHeaders.set("filename", fileNameFirst+"."+extension); // no encode filename

                return ResponseEntity.ok().headers(httpHeaders)
                        .body(bytes);
            }


        } catch (Exception e) {
            log.info("ERROR : " + e.getMessage());
            return responseUtility.checkFailedResponse();
        } finally {
            if (in != null) {try { in.close(); } catch (IOException ignored) {}}
            if (os != null) {try {os.close();} catch (IOException ignored) {}}
            if (file != null) {if (file.exists()){
                if(!file.delete()){

                }
            }
            }
        }

        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
//    @RequestMapping(value = "/manager/build/project/export/download/{filename}", method = RequestMethod.GET, produces= MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public void getApkFileDownload_bak(@PathVariable("filename") String zipFileName, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        InputStream in = null;
        OutputStream os = null;
        File file = null;
        S3ObjectInputStream objectInputStream = null;

        try {

            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);

            if(springProfile.equals("onpremiss")){
                String savePath = exportDownloadDir + "/" + memberLogin.getUser_id().toString() + "/";
                String client = request.getHeader("User-Agent");

                //파일 다운로드 헤더 지정
                response.reset();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Description", "JSP Generated Data");

                file = new File(savePath, zipFileName);

                if (file.exists()) {
                    in = new FileInputStream(file);

                    // IE
                    if (client.contains("MSIE")) {
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "\\ ") + "\"");
                    }

                    // IE 11 이상.
                    else if (client.contains("Trident")) {
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "\\ ") + "\"");
                    }

                    // 한글 파일명 처리
                    else {
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(zipFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
                        response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
                    }

                    response.setHeader("Content-Length", "" + file.length());

                    os = response.getOutputStream();
                    byte[] bytes = new byte[(int) file.length()];
                    int leng = 0;
                    while ((leng = in.read(bytes)) > 0) {
                        os.write(bytes, 0, leng);
                    }

                } else {
                    response.setContentType("text/html;charset=UTF-8");
                    log.info("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
                }
            }else {

            }


        } catch (Exception e) {
            log.info("ERROR : " + e.getMessage());
        } finally {
            if (in != null) {try { in.close(); } catch (IOException ignored) {}}
            if (os != null) {try {os.close();} catch (IOException ignored) {}}
            if (file != null) {if (file.exists()){
                if(!file.delete()){

                }
            }
            }
        }
    }

    // template 버전 리스트 조회
    @RequestMapping(value = "/manager/build/project/search/templateList", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getTemplateVersionList(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        HashMap<String, Object> msg = new HashMap<>();
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_TEMPLATE_LIST_INFO.name());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        }else {
            return null;
        }

        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());
        // product type 추가
        msg.put(PayloadKeyType.product_type.name(),payload.get(PayloadKeyType.product_type.name()).toString());


        BranchSetting branchSetting = branchSettingService.findbyID(Long.valueOf(payload.get("branch_id").toString()));


//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//                }
//            } catch (IOException ex) {
//
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//            }
//
//        }


        return responseUtility.checkSuccessResponse();

    }

    @RequestMapping(value = "/manager/build/project/update/projectSetting", method = RequestMethod.POST)
    public ResponseEntity<Object> updateBuildProject(@RequestBody BuildProject buildProject) {
        buildProjectService.updateBuildProject(buildProject);
        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/build/project/delete", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        JSONObject obj;
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            obj = buildProjectService.delete(Long.valueOf(payload.get("id").toString()), payload.get("project_name").toString());
            return responseUtility.makeSuccessResponse(obj);
        }else{
            return null;
        }
    }

    /**
     * Jackson Json String Mapper
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    private String toJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}