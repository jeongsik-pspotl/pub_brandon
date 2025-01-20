package com.pspotl.sidebranden.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingService;
import com.pspotl.sidebranden.common.build.BuildProject;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.buildsetting.BuildSetting;
import com.pspotl.sidebranden.common.buildsetting.BuildSettingService;
import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.member.MemberDetail;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.signingkeysetting.KeyiOSSetting;
import com.pspotl.sidebranden.common.signingkeysetting.SigningKeySettingService;
import com.pspotl.sidebranden.common.vcssetting.VCSSetting;
import com.pspotl.sidebranden.common.vcssetting.VCSSettingService;
import com.pspotl.sidebranden.common.workspace.MemberMapping;
import com.pspotl.sidebranden.common.workspace.Workspace;
import com.pspotl.sidebranden.common.workspace.WorkspaceService;
import com.pspotl.sidebranden.manager.client.ClientHandler;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.ManagerDirectoryType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
import com.pspotl.sidebranden.manager.handler.WHiveWebSocketHandler;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketBuilderService;
import com.pspotl.sidebranden.manager.util.FileNameAwareByteArrayResource;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.RestFileUploadUtil;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class BuildSettingController {

    private static final Logger logger = LoggerFactory.getLogger(BuildSettingController.class);

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private BuildProjectService buildProjectService;

    @Autowired
    private BuildSettingService buildSettingService;

    @Autowired
    private BranchSettingService branchSettingService;

    @Autowired
    private SigningKeySettingService signingKeySettingService;

    @Autowired
    private VCSSettingService vcsSettingService;

    @Autowired
    private MemberService memberService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    private RestFileUploadUtil restFileUploadUtil;

    @Autowired
    private ClientHandler clientHandler;

    @Autowired
    Common common;

    WebSocketSession websocketSession;

    JSONObject jsonBranchSetting = new JSONObject();

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    JSONParser parser = new JSONParser();

    private static final String CREATED = "Created";
    private static final String PROJECT_DIR_PATH = "project_dir_path";

    @RequestMapping(value = "/api/builsetting", method = RequestMethod.POST)
    public ResponseEntity<Object> createBuildSetting(@RequestBody BuildSetting buildSetting) {
        buildSettingService.insert(buildSetting);
        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/api/builsettings", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<BuildSetting> findAll() {
        return buildSettingService.findAll();
    }

    @GetMapping(value = "/api/buildsetting/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody BuildSetting findById(@PathVariable("id") Long id) {
        return buildSettingService.findById(id);
    }

    @GetMapping(value = "/manager/build/setting/search/projectDetail/{build_id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody BuildSetting findByBuildId(@PathVariable("build_id") Long buildId) {
        return buildSettingService.findByBuildId(buildId);
    }

    @RequestMapping(value = "/manager/build/setting/update/projectDetail", method = RequestMethod.POST)
    public ResponseEntity<Object> updateBuildProject(@RequestBody BuildSetting buildSetting) {
        BranchSetting branchSetting;
        BuildProject buildProject;
        VCSSetting vcsSetting;
        Workspace workspace;
        MemberDetail memberDetail;
        KeyiOSSetting keyiOSSetting;

        HashMap<String, Object> msg = new HashMap<>();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonRepositoryObj = new JSONObject();
        JSONObject jsonProfileDebug = new JSONObject();
        JSONObject jsonProfileRelease = new JSONObject();

        // buildSetting 대신 projectSetting 으로 변경하기
        // projectSetting 값에는 추가로 domain, user, hqkey 값 필요

        // build_id branch id 조회
        branchSetting = buildProjectService.findByIdAndBranchId(buildSetting.getProject_id());
        // build id buildProject 테이블 조회
        buildProject = buildProjectService.findById(buildSetting.getProject_id());

        if(buildProject.getVcs_id() != null) {
            vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

            jsonRepositoryObj.put(PayloadKeyType.vcsType.name(),vcsSetting.getVcs_type());
            jsonRepositoryObj.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
            jsonRepositoryObj.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepositoryObj.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
        }

        workspace = workspaceService.findById(buildProject.getWorkspace_id());

        memberDetail = memberService.findByIdDetail(workspace.getMember_id());

        if(buildProject.getPlatform().toLowerCase().equals("ios")){
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
            jsonProfileDebug.put(PayloadKeyType.profilePath.name(), keyiOSSetting.getIos_debug_profile_path());
            jsonProfileRelease.put(PayloadKeyType.profilePath.name(),keyiOSSetting.getIos_release_profile_path());
            jsonApplyConfig.put("release_type",keyiOSSetting.getIos_release_type());

            msg.put("jsonProfileDebug", jsonProfileDebug.toJSONString());
            msg.put("jsonProfileRelease", jsonProfileRelease.toJSONString());

        }

        jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),buildSetting.getApp_version());
        jsonApplyConfig.put(PayloadKeyType.AppVersionCode.name(),buildSetting.getApp_version_code());
        jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),buildSetting.getApp_id());
        jsonApplyConfig.put(PayloadKeyType.AppName.name(),buildSetting.getApp_name());
        jsonApplyConfig.put(PayloadKeyType.MinTargetVersion.name(),buildSetting.getMin_target_version());

        jsonApplyConfig.put(PayloadKeyType.language.name(),buildProject.getPlatform_language());
        if(buildProject.getPlatform().toLowerCase().equals(PayloadKeyType.android.name())){
            jsonApplyConfig.put(PayloadKeyType.PackageName.name(),buildSetting.getPackage_name());
        }else{
            jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),buildSetting.getPackage_name());
        }



        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_APP_CONFIG_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(), buildProject.getProduct_type());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put(PayloadKeyType.jsonApplyConfig.name(),jsonApplyConfig.toJSONString());
        msg.put("jsonRepositoryObj",jsonRepositoryObj.toJSONString());
        msg.put(PayloadKeyType.hqKey.name(),memberDetail.getEmail());
        msg.put(PayloadKeyType.domainID.name(),memberDetail.getDomain_id());
        msg.put(PayloadKeyType.userID.name(),workspace.getMember_id());
        msg.put(PayloadKeyType.workspaceID.name(),workspace.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());

        msg.put("UserWorkspacePath",workspace.getWorkspace_name());
        msg.put("UserProjectPath",buildProject.getProject_name());
        msg.put("UserProjectDirName",buildProject.getProject_dir_path());

        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
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

        // build id 기준으로 branch id 조회 및

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(path = "/manager/build/setting/upload/icon", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody ResponseEntity<String> appIconUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("branch_id") String branch_id
            , @RequestParam("projectname") String projectname, @RequestParam("workspacename") String workspacename, @RequestParam("projectDirName") String projectDirName
            , @RequestParam("target_server") String target_server, @RequestParam("platform") String platform) throws IOException {

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        HashMap<String, Object> msg = new HashMap<>();
        BranchSetting branchSetting;
        String userID = "";
        String hqKey = "";

        int message_length = 0;
        int offset = 0;
        byte[] byte_message = null;

        if(file.isEmpty()){
            return new ResponseEntity<>("File is not Found", HttpStatus.BAD_REQUEST);
        }

        if(file.getOriginalFilename() == null){
            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
        }

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            userID = String.valueOf(memberLogin.getUser_id());
            hqKey = memberLogin.getUser_login_id();
        }else {
            return null;
        }

        parts.add(PayloadKeyType.platform.name(),platform);
        parts.add("projectname",projectname);

        // build_id
        branchSetting = branchSettingService.findbyID(new Long(branch_id));


        // user id
        byte[] byte_branch_id = branchSetting.getBuilder_user_id().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_branch_id.length;
        byte[] byte_branch_id_length = convertIntToByteArray(byte_branch_id.length);
        message_length += byte_branch_id_length.length;

        // bin type
        byte[] byte_bin_type = ClientMessageType.BIN_FILE_APP_ICON_APPEND_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
        message_length += byte_bin_type.length;

        // bin type length
        byte[] byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
        message_length += byte_bin_type_length.length;

        // userID
        byte[] byte_user_id = userID.getBytes(StandardCharsets.UTF_8);
        message_length += byte_user_id.length;

        byte[] byte_user_id_length = convertIntToByteArray(byte_user_id.length);
        message_length += byte_user_id_length.length;

        byte[] byte_hqkey = hqKey.getBytes(StandardCharsets.UTF_8);
        message_length += byte_hqkey.length;

        byte[] byte_hqkey_length = convertIntToByteArray(byte_hqkey.length);
        message_length += byte_hqkey_length.length;

        // platform 처리 || json 데이터 타입으로 처리  sendBranchSigningKeyAPI obj 처리
        byte[] byte_zipfile_name = file.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
        message_length += byte_zipfile_name.length;

        // zip file name length
        byte[] byte_zipfile_name_length = convertIntToByteArray(byte_zipfile_name.length);
        message_length += byte_zipfile_name_length.length;

        // zip file
        byte[] byte_zipfile = file.getBytes();
        message_length += byte_zipfile.length;

        // zip file length
        byte[] byte_zipfile_length = convertIntToByteArray(byte_zipfile.length);
        message_length += byte_zipfile_length.length;

        byte_message = new byte[message_length];

        System.arraycopy(byte_branch_id_length, 0, byte_message, offset, byte_branch_id_length.length);
        offset += byte_branch_id_length.length;
        System.arraycopy(byte_branch_id, 0, byte_message, offset, byte_branch_id.length);
        offset += byte_branch_id.length;

        System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
        offset += byte_bin_type_length.length;
        System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
        offset += byte_bin_type.length;

        System.arraycopy(byte_user_id_length, 0, byte_message, offset, byte_user_id_length.length);
        offset += byte_user_id_length.length;

        System.arraycopy(byte_user_id, 0, byte_message, offset, byte_user_id.length);
        offset += byte_user_id.length;

        System.arraycopy(byte_hqkey_length, 0, byte_message, offset, byte_hqkey_length.length);
        offset += byte_hqkey_length.length;

        System.arraycopy(byte_hqkey, 0, byte_message, offset, byte_hqkey.length);
        offset += byte_hqkey.length;

        System.arraycopy(byte_zipfile_name_length, 0, byte_message, offset, byte_zipfile_name_length.length);
        offset += byte_zipfile_name_length.length;

        System.arraycopy(byte_zipfile_name, 0, byte_message, offset, byte_zipfile_name.length);
        offset += byte_zipfile_name.length;

        System.arraycopy(byte_zipfile_length, 0, byte_message, offset, byte_zipfile_length.length);
        offset += byte_zipfile_length.length;
        System.arraycopy(byte_zipfile, 0, byte_message, offset, byte_zipfile.length);

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//            }
//
//        }

        return new ResponseEntity<>(CREATED, HttpStatus.OK);
    }

    @RequestMapping(path = "/manager/build/setting/upload/updateIcon", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody ResponseEntity<String> appIconUpdateToSend(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("branch_id") String branch_id
            , @RequestParam("projectID") String projectID, @RequestParam("workspaceID") String workspaceID, @RequestParam("projectDirName") String projectDirName
            ,@RequestParam("platform") String platform) throws IOException {
        requestFactory.setBufferRequestBody(false);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        BranchSetting branchSetting;

        String projectPath = "";
        String domainID = "";

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            domainID = memberLogin.getDomain_id();
        }else {
            return null;
        }

        if(file.isEmpty()){
            return new ResponseEntity<>("File is not Found", HttpStatus.BAD_REQUEST);
        }

        if(file.getOriginalFilename() == null){
            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
        }

        Workspace workspace = workspaceService.findById(Long.valueOf(workspaceID));

        BuildSetting buildSetting = buildSettingService.findByBuildId(Long.valueOf(projectID));

        if(platform.toLowerCase().equals(PayloadKeyType.android.name())){
            projectPath = ManagerDirectoryType.DOMAIN_ + domainID + "/" + ManagerDirectoryType.USER_ + workspace.getMember_id().toString() + "/"
                    + ManagerDirectoryType.WORKSPACE_W + workspaceID + "/" + ManagerDirectoryType.PROJECT_P + projectID + "/" + projectDirName;

        }else {
            projectPath = ManagerDirectoryType.DOMAIN_ + domainID + "/" + ManagerDirectoryType.USER_ + workspace.getMember_id().toString() + "/"
                    + ManagerDirectoryType.WORKSPACE_W + workspaceID + "/" + ManagerDirectoryType.PROJECT_P + projectID + "/" + projectDirName +"/"+ buildSetting.getPackage_name();

        }

        int message_length = 0;
        int offset = 0;
        byte[] byte_message = null;

        parts.add(PayloadKeyType.platform.name(),platform);

        // build_id
        branchSetting = branchSettingService.findbyID(new Long(branch_id));


        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        mc.add(new MappingJackson2HttpMessageConverter());

        // user id
        byte[] byte_branch_id = branchSetting.getBuilder_user_id().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_branch_id.length;
        byte[] byte_branch_id_length = convertIntToByteArray(byte_branch_id.length);
        message_length += byte_branch_id_length.length;

        // bin type
        byte[] byte_bin_type = ClientMessageType.BIN_FILE_APP_ICON_UPDATE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
        message_length += byte_bin_type.length;

        // bin type length
        byte[] byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
        message_length += byte_bin_type_length.length;

        byte[] byte_user_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
        message_length += byte_user_id.length;

        byte[] byte_user_id_length = convertIntToByteArray(byte_user_id.length);
        message_length += byte_user_id_length.length;

        // platform
        byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
        message_length += byte_platform.length;

        // platform length
        byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
        message_length += byte_platform_length.length;

        // project path
        byte[] byte_project_path = projectPath.getBytes(StandardCharsets.UTF_8);
        message_length += byte_project_path.length;

        byte[] byte_project_path_length = convertIntToByteArray(byte_project_path.length);
        message_length += byte_project_path_length.length;

        // platform 처리 || json 데이터 타입으로 처리  sendBranchSigningKeyAPI obj 처리
        byte[] byte_zipfile_name = file.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
        message_length += byte_zipfile_name.length;

        // zip file name length
        byte[] byte_zipfile_name_length = convertIntToByteArray(byte_zipfile_name.length);
        message_length += byte_zipfile_name_length.length;

        // zip file
        byte[] byte_zipfile = file.getBytes();
        message_length += byte_zipfile.length;

        // zip file length
        byte[] byte_zipfile_length = convertIntToByteArray(byte_zipfile.length);
        message_length += byte_zipfile_length.length;

        byte_message = new byte[message_length];

        System.arraycopy(byte_branch_id_length, 0, byte_message, offset, byte_branch_id_length.length);
        offset += byte_branch_id_length.length;
        System.arraycopy(byte_branch_id, 0, byte_message, offset, byte_branch_id.length);
        offset += byte_branch_id.length;

        System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
        offset += byte_bin_type_length.length;
        System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
        offset += byte_bin_type.length;

        System.arraycopy(byte_user_id_length, 0, byte_message, offset, byte_user_id_length.length);
        offset += byte_user_id_length.length;

        System.arraycopy(byte_user_id, 0, byte_message, offset, byte_user_id.length);
        offset += byte_user_id.length;

        System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
        offset += byte_platform_length.length;
        System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
        offset += byte_platform.length;

        System.arraycopy(byte_project_path_length, 0, byte_message, offset, byte_project_path_length.length);
        offset += byte_project_path_length.length;
        System.arraycopy(byte_project_path, 0, byte_message, offset, byte_project_path.length);
        offset += byte_project_path.length;

        System.arraycopy(byte_zipfile_name_length, 0, byte_message, offset, byte_zipfile_name_length.length);
        offset += byte_zipfile_name_length.length;

        System.arraycopy(byte_zipfile_name, 0, byte_message, offset, byte_zipfile_name.length);
        offset += byte_zipfile_name.length;

        System.arraycopy(byte_zipfile_length, 0, byte_message, offset, byte_zipfile_length.length);
        offset += byte_zipfile_length.length;
        System.arraycopy(byte_zipfile, 0, byte_message, offset, byte_zipfile.length);

//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//            }
//
//        }


        return new ResponseEntity<>(CREATED, HttpStatus.OK);
    }

    @RequestMapping(path = "/api/buildsetting/signingkeyupload", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody ResponseEntity<String> iOSSigningKeyUpload(@RequestParam("file") MultipartFile file, @RequestParam("target_server") String target_server, @RequestParam("platform") String platform) throws IOException {
        requestFactory.setBufferRequestBody(false);
        log.info(" iOSSigningKeyUpload : check...  ");
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();

        log.info(" iOSSigningKeyUpload : file length check....  {}", file.getSize());

        parts.add("file", new FileNameAwareByteArrayResource(file.getOriginalFilename(),file.getBytes(),""));
        parts.add(PayloadKeyType.platform.name(),platform);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        mc.add(new MappingJackson2HttpMessageConverter());

        String url = "http://"+target_server+"/branch/signingkey/upload";
        String response = restTemplate.postForObject(url, parts, String.class);
        logger.info("response data {}", response);

        return new ResponseEntity<>(CREATED, HttpStatus.OK);
    }

    @RequestMapping(path = "/api/buildsetting/signingkeyfile", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody ResponseEntity<String> iOSSigningKeyCreate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("app_id") String app_id,
            @RequestParam("target_server") String target_server,
            @RequestParam("app_name") String app_name,
            @RequestParam("platform") String platform,
            @RequestParam("signing_path") String signing_path,
            @RequestParam("signing_issuer_id") String signing_issuer_id,
            @RequestParam("signing_key_id") String signing_key_id,
            @RequestParam("provisioning_profiles_name") String provisioning_profiles_name) {
        logger.info("file : {}",file);
        logger.info("target_server : {}",target_server);
        requestFactory.setBufferRequestBody(false);

        MultiValueMap<String, Object> parts =
                new LinkedMultiValueMap<String, Object>();

        try {
            parts.add("file", file.getBytes());
        } catch (IOException e) {
             
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

        parts.add(PayloadKeyType.filename.name(), file.getOriginalFilename());
        parts.add("bundleId", app_id);
        parts.add("app_name", app_name);
        parts.add(PayloadKeyType.platform.name(), platform);
        parts.add("signing_path", signing_path);
        parts.add("signing_issuer_id", signing_issuer_id);
        parts.add("signing_key_id", signing_key_id);
        parts.add("provisioning_profiles_name", provisioning_profiles_name);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        mc.add(new MappingJackson2HttpMessageConverter());
        logger.info("iOSsigningKeyCreate parts obj setting .. ");
        logger.info("{}",parts);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);
        String url = "http://"+target_server+"/branch/signingkey/create";

        // 1. branch /branch/signingkey/create controller 로 보내기
        logger.info("signingKeyUpload check request All {}",request.getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        logger.info("response data {}", response);

        // 결과 값에 따라 db insert 처리하기
        return new ResponseEntity<>(CREATED, HttpStatus.OK);
    }

    @RequestMapping(value ="/manager/build/setting/search/appIcon", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> appIconImageReadFindById(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        Workspace workspace = null;
        BuildProject buildProject = null;
        BuildSetting buildSetting;
        BranchSetting branchSetting;
        MemberMapping memberMapping;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode msg = mapper.createObjectNode();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_APPICON_IMAGE_LOAD_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());

        workspace = workspaceService.findById(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()));
        buildProject = buildProjectService.findById(Long.valueOf(payload.get("project_id").toString()));
        buildSetting = buildSettingService.findByBuildId(Long.valueOf(payload.get("project_id").toString()));
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        memberMapping = workspaceService.findByMemberRoleWorkspaceID(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()));
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());


        // branch id 조회

        if(workspace != null) {
            msg.put(PayloadKeyType.workspace_name.name(), workspace.getWorkspace_name());
            msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id());
        }

        if(buildProject != null){
            msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());
            msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
            msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put("project_dir", buildProject.getProject_dir_path());
            msg.put("packageName",buildSetting.getPackage_name());

        }

        // send message HV_MSG_APPICON_IMAGE_LOAD_INFO
        // branch id 기준 websocket send message 처리 추가

        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(String.valueOf(branchSetting.getBuilder_user_id()), SessionType.BRANCH);
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

        log.info(String.valueOf(websocketSession));

        return new ResponseEntity<>(CREATED, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/buildsetting/gitpull", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> setProjectGitPull(@RequestBody Map<String, Object> payload) {

        String buildID = payload.get(PayloadKeyType.build_id.name()).toString();
        BuildProject buildProject;
        VCSSetting vcsSetting;

        // build project service
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(buildID));
        buildProject = buildProjectService.findById(Long.valueOf(buildID));
        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());

        // branch setting service
        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROEJCT_PULL_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());

        msg.put(PayloadKeyType.workspace_name.name(), payload.get(PayloadKeyType.workspace_name.name()).toString());
        msg.put("project_name", payload.get("project_name").toString());

        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(),branchSetting.getBuilder_user_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());
        jsonBranchSetting.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        jsonBranchSetting.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
        jsonBranchSetting.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
        jsonBranchSetting.put(PayloadKeyType.hqKey.name(),payload.get(PayloadKeyType.hqKey.name()).toString());

        msg.put(PayloadKeyType.jsonRepository.name(),jsonBranchSetting.toJSONString());

        // FTPSetting, JSONObject 로 필요한 값 세팅

        log.info(msg.toString());

        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
        if(websocketSession != null){
            try {
                synchronized(websocketSession) {
                    websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
                }
            } catch (IOException ex) {
                   
                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }
            // 5분간격 SESSID message send

        }

        log.info(String.valueOf(websocketSession));


        return responseUtility.makeSuccessResponse();
    }

    // workspace, project name, project dir path
    @RequestMapping(value = "/manager/build/setting/search/serverConfig", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getServerConfigRequest(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        Workspace workspace;
        BuildProject buildProject;
        BranchSetting branchSetting;
        MemberMapping memberMapping;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        String hqKey = memberLogin.getUser_login_id();
        String build_id = payload.get(PayloadKeyType.build_id.name()).toString();
        String workspace_id = payload.get(PayloadKeyType.workspace_id.name()).toString();
        String projectDirPath = payload.get(PROJECT_DIR_PATH).toString();

        workspace = workspaceService.findById(Long.valueOf(workspace_id));

        buildProject = buildProjectService.findById(Long.valueOf(build_id));
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(), payload.get(PayloadKeyType.product_type.name()).toString());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());
        msg.put(PayloadKeyType.hqKey.name(), hqKey);
        msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());

        memberMapping = workspaceService.findByMemberRoleWorkspaceID(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()));
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());


        msg.put(PayloadKeyType.workspaceID.name(),workspace.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());
        msg.put(PayloadKeyType.projectDirPath.name(), projectDirPath);

        // send message
        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
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

    @RequestMapping(value = "/manager/build/setting/search/getiOSAppConfigInfo", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getiOSAppConfigInfoRequest(@RequestBody Map<String, Object> payload){

        Workspace workspace;
        BuildProject buildProject;
        BranchSetting branchSetting;
        MemberMapping memberMapping;

        String hqKey = payload.get(PayloadKeyType.hqKey.name()).toString();
        String build_id = payload.get(PayloadKeyType.build_id.name()).toString();
        String workspace_id = payload.get(PayloadKeyType.workspace_id.name()).toString();
        String projectDirPath = payload.get(PROJECT_DIR_PATH).toString();

        workspace = workspaceService.findById(Long.valueOf(workspace_id));

        buildProject = buildProjectService.findById(Long.valueOf(build_id));
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(),payload.get(PayloadKeyType.product_type.name()).toString());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());
        msg.put(PayloadKeyType.hqKey.name(), hqKey);
        msg.put(PayloadKeyType.domainID.name(),payload.get(PayloadKeyType.domainID.name()).toString());


        memberMapping = workspaceService.findByMemberRoleWorkspaceID(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()));
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

        msg.put(PayloadKeyType.workspaceID.name(),workspace.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());
        msg.put(PayloadKeyType.projectDirPath.name(), projectDirPath);

        // send message
        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
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
    @RequestMapping(value = "/manager/build/setting/search/getAndroidAllGetConfig", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAndroidAllGetConfigRequest(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        Workspace workspace;
        BuildProject buildProject;
        BranchSetting branchSetting;
        MemberMapping memberMapping;
        HashMap<String, Object> msg = new HashMap<>();

        JSONObject entity = new JSONObject();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        String hqKey = memberLogin.getUser_login_id();
        String build_id = payload.get(PayloadKeyType.build_id.name()).toString();
        String workspace_id = payload.get(PayloadKeyType.workspace_id.name()).toString();
        String projectDirPath = payload.get(PROJECT_DIR_PATH).toString();

        workspace = workspaceService.findById(Long.valueOf(workspace_id));

        buildProject = buildProjectService.findById(Long.valueOf(build_id));
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getEtc_queue_cnt() > builderQueueManaged.getEtc_queue_status_cnt()){
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() + 1);
            builderQueueManagedService.update(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }



        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(), payload.get(PayloadKeyType.product_type.name()).toString());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());
        msg.put(PayloadKeyType.hqKey.name(), hqKey);
        msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());


        memberMapping = workspaceService.findByMemberRoleWorkspaceID(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()));
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

        msg.put(PayloadKeyType.workspaceID.name(),workspace.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());
        msg.put(PayloadKeyType.projectDirPath.name(), projectDirPath);
        try {
            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());
            log.info(wHiveIdentity.toString());
            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

        }catch (Exception e){
            log.warn(e.getMessage(), e);
        }
        // send message
        // builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
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

    // TODO: builder 큐 관리 기능 추가
    // /manager/build/setting/search/getiOSGetInformation
    @RequestMapping(value = "/manager/build/setting/search/getiOSGetInformation", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getiOSAllGetInformationRequest(HttpServletRequest request, @RequestBody Map<String, Object> payload){

        Workspace workspace;
        BuildProject buildProject;
        BranchSetting branchSetting;
        MemberMapping memberMapping;
        HashMap<String, Object> msg = new HashMap<>();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        JSONObject entity = new JSONObject();

        if(memberLogin == null){
            return null;
        }

        String hqKey = memberLogin.getUser_login_id();
        String build_id = payload.get(PayloadKeyType.build_id.name()).toString();
        String workspace_id = payload.get(PayloadKeyType.workspace_id.name()).toString();
        String projectDirPath = payload.get(PROJECT_DIR_PATH).toString();

        workspace = workspaceService.findById(Long.valueOf(workspace_id));

        buildProject = buildProjectService.findById(Long.valueOf(build_id));
        branchSetting = branchSettingService.findbyID(buildProject.getBuilder_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getEtc_queue_cnt() > builderQueueManaged.getEtc_queue_status_cnt()){
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() + 1);
            builderQueueManagedService.update(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.product_type.name(), payload.get(PayloadKeyType.product_type.name()).toString());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()).toString());
        msg.put(PayloadKeyType.hqKey.name(), hqKey);
        msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());


        memberMapping = workspaceService.findByMemberRoleWorkspaceID(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()));
        msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

        msg.put(PayloadKeyType.workspaceID.name(),workspace.getWorkspace_id());
        msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());
        msg.put(PayloadKeyType.projectDirPath.name(), projectDirPath);

        // send message
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        log.info(wHiveIdentity.toString());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);
//        websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
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

    // int to byteArray 변환
    private byte[] convertIntToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value >> 24);
        byteArray[1] = (byte)(value >> 16);
        byteArray[2] = (byte)(value >> 8);
        byteArray[3] = (byte)(value);
        return byteArray;
    }

}