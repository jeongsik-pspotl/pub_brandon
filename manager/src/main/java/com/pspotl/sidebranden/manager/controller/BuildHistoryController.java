package com.pspotl.sidebranden.manager.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.build.BuildProject;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.buildhistory.BuildHistory;
import com.pspotl.sidebranden.common.buildhistory.BuildHistoryService;
import com.pspotl.sidebranden.common.buildhistory.BuildRequestJsonObject;
import com.pspotl.sidebranden.common.buildsetting.BuildSetting;
import com.pspotl.sidebranden.common.buildsetting.BuildSettingService;
import com.pspotl.sidebranden.common.deploy.Deploy;
import com.pspotl.sidebranden.common.deploy.DeploySettingService;
import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.ftpsetting.FTPSetting;
import com.pspotl.sidebranden.common.member.MemberDetail;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
import com.pspotl.sidebranden.common.signingkeysetting.KeyiOSSetting;
import com.pspotl.sidebranden.common.signingkeysetting.SigningKeySettingService;
import com.pspotl.sidebranden.common.vcssetting.VCSSetting;
import com.pspotl.sidebranden.common.vcssetting.VCSSettingService;
import com.pspotl.sidebranden.common.workspace.MemberMapping;
import com.pspotl.sidebranden.common.workspace.Workspace;
import com.pspotl.sidebranden.common.workspace.WorkspaceService;
import com.pspotl.sidebranden.manager.SessionConstants;
import com.pspotl.sidebranden.manager.client.ClientHandler;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionKeyContents;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
import com.pspotl.sidebranden.manager.handler.WHiveWebSocketHandler;
import com.pspotl.sidebranden.manager.model.BuilderLoginSessionData;
import com.pspotl.sidebranden.manager.model.LoginSessionData;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketBuilderService;
import com.pspotl.sidebranden.manager.util.*;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Slf4j
@Controller
public class BuildHistoryController extends BaseUtility {

    @Autowired
    BuildHistoryService buildHistoryService;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    VCSSettingService vcsSettingService;

    @Autowired
    MemberService memberService;

    @Autowired
    BuildSettingService buildSettingService;

    @Autowired
    SigningKeySettingService signingKeySettingService;

    @Autowired
    DeploySettingService deploySettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    Common common;

    @Autowired
    PricingService pricingService;

    AWSClientUtil awsClientUtil = new AWSClientUtil();

    private final AmazonS3Client amazonS3 = new AmazonS3Client();

    @Autowired
    private ResponseUtility responseUtility;

    @Value("${whive.distribution.deployQRCodeUrlPath}")
    private String qrCodeUploadDir;

    @Value("${whive.server.whubTarget}")
    private String whubTargetUrl;

    @Value("${whive.distribution.macPassword}")
    private String macPassword;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${spring.profiles}")
    private String springProfile;

    @Autowired
    private ClientHandler clientHandler;

    @Autowired
    private APIHandler apiHandler;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private iOSFileUtil iosSFileUtil;

    private Utility utility;

    WebSocketSession websocketSession;

    JSONObject jsonBranchSetting = new JSONObject();
    JSONObject entity = new JSONObject();

    private JSONParser parser = new JSONParser();

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    private static final String CONTENT_DISPOSTION = "Content-Disposition";
    private static final String ATTACHMENT_FILENAME = "attachment; filename=\"";
    private static final String CHMOD = "chmod";


    /**  빌드 시작 기능 */
    @RequestMapping(value = "/manager/build/history/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createBuildProject(HttpServletRequest request, @RequestBody BuildRequestJsonObject buildRequestJsonObject) {

        JSONObject jsonFtpSetting = new JSONObject();
        JSONObject jsonHistoryVo = new JSONObject();
        JSONObject jsonVCSSetting = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonentity = new JSONObject();

        ArrayList<String> AndroidAppIDList = new ArrayList<String>();
        ArrayList<String> iOSAppIDList = new ArrayList<String>();
        ArrayList<String> platformTypeList = new ArrayList<String>();

        FTPSetting ftpSetting; // ftp 정보
        BranchSetting branchSetting;
        BuildHistory buildHistory;
        Workspace workspace;
        BuildProject buildProject;
        VCSSetting vcsSetting;
        BuildSetting buildSetting;
        MemberMapping memberMapping;
        JSONObject appConfigJSON = buildRequestJsonObject.getAppConfig();


        buildHistory = buildRequestJsonObject.getBuildHistory();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        int historyTotalCnt = 0;

        if(memberLogin == null){
            return null;
        }

        Pricing pricing = pricingService.findById(memberLogin.getUser_id());

        if(pricing == null){
            memberLogin.setPay_change_yn("N");
        }else{
            memberLogin.setPay_change_yn(pricing.getPay_change_yn());
        }



        log.info(buildRequestJsonObject.getAppConfig().toJSONString());

        // license check 메소드 호출
        // platform, appID
        // buildSetting = buildSettingService.findByBuildId(buildHistory.getProject_id());

        String AppID = "";
        String platform = buildHistory.getPlatform(); //buildSetting.getPlatform_type(); // 수정해야함.

        if(platform.toLowerCase().equals(PayloadKeyType.android.name())){
            AppID = appConfigJSON.get("applicationId").toString();
            jsonApplyConfig.put(PayloadKeyType.PackageName.name(),appConfigJSON.get("applicationId").toString());
            jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),appConfigJSON.get("appVersion").toString());
            jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),appConfigJSON.get("applicationId").toString());
            jsonApplyConfig.put(PayloadKeyType.AppName.name(),appConfigJSON.get("appName").toString());

        }else if(platform.toLowerCase().equals("ios")){
            AppID = appConfigJSON.get("applicationID").toString();
            jsonApplyConfig.put(PayloadKeyType.iOSProjectName.name(),""); // iOS 프로젝트 작업시, 수정이 필요함
            jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),appConfigJSON.get("version").toString());
            jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),appConfigJSON.get("applicationID").toString());
            jsonApplyConfig.put(PayloadKeyType.AppName.name(),appConfigJSON.get("appName").toString());
        }


        //TODO  onpremiss 일경우 whive 라이센스 체크
        if(springProfile.equals("onpremiss")){
            // license key 상에 Android, iOS AppID List 가져오기
//                utility.checkHybridLicense(AndroidAppIDList, iOSAppIDList, platformTypeList);

            if(!Utility.getIsDemoLicense()) {
                if (AppID != null && platform != null) {

                    if(!platformTypeList.contains(platform)){
                        System.err.println("Platform does not match to license file. please reissue license.");
                        // throw new PostControllerException("Utility", "Invalid license file..");

                        // platform 에러 관련 문구 추가
                        jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_PLATFORM.getMessage());
                        return responseUtility.checkFailedResponse(jsonentity);
                    }

                    if(platform.toLowerCase().equals(PayloadKeyType.android.name())){
                        if (!AndroidAppIDList.contains(AppID)) {
                            System.err.println("Real Android AppID does not match to license file. please reissue license.");

                            // appID 에러 관련 문구 추가
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_ANDROID_APPID.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }else if(platform.toLowerCase().equals("ios")){
                        if (!iOSAppIDList.contains(AppID)) {
                            System.err.println("Real iOS AppID does not match to license file. please reissue license.");

                            // appID 에러 관련 문구 추가
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

        }else {
            /**
             * whive 서비스 일 경우 라이센스 기반이 아난 사용자 정보 내 에서 app id 기준으로 조회 된다.
             */
            try {
                JSONObject appIDJSON = (JSONObject) parser.parse(memberLogin.getAppid_json().toString());
                if (memberLogin.getPay_change_yn() == null || memberLogin.getPay_change_yn().equals("N")) {
                    if (platform.toLowerCase().equals(PayloadKeyType.android.name())) {
                        if (!appIDJSON.get("androidAppID1").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }
                    }

                    if (platform.toLowerCase().equals(PayloadKeyType.ios.name())) {
                        jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                        return responseUtility.checkFailedResponse(jsonentity);

                    }

                } else {
                    if (platform.toLowerCase().equals(PayloadKeyType.android.name())) {
                        if(appIDJSON.get("androidAppID1").toString().equals("") && appIDJSON.get("androidAppID2").toString().equals("")){
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                        if (!appIDJSON.get("androidAppID1").toString().equals(AppID) && !appIDJSON.get("androidAppID2").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    } else if (platform.toLowerCase().equals(PayloadKeyType.ios.name())) {
                        if(appIDJSON.get("iOSAppID1").toString().equals("") && appIDJSON.get("iOSAppID2").toString().equals("")){
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                        if (!appIDJSON.get("iOSAppID1").toString().equals(AppID) && !appIDJSON.get("iOSAppID2").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }
                }
            }catch (ParseException e) {
                log.warn(e.getMessage(), e);
                jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                return responseUtility.checkFailedResponse(jsonentity);
            }catch (NullPointerException e){
                jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                return responseUtility.checkFailedResponse(jsonentity);
            }

        }


        // member build yn 체크 및 활성화

        if( buildHistory.getProject_id() == null) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        ftpSetting = buildProjectService.findByIdAndFTPId(buildHistory.getProject_id());


        try {
            HashMap<String, Object> msg = new HashMap<>();

            if(platform.toLowerCase().equals("android") && buildRequestJsonObject.getBuildType().toLowerCase().equals("debug")){
                msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEBUG_BUILD.name());
            }else if(platform.toLowerCase().equals("android") && buildRequestJsonObject.getBuildType().toLowerCase().equals("release")){
                msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_RELEASE_BUILD.name());
            }// aab build 관련 분기 처리
            else if(platform.toLowerCase().equals("android") && buildRequestJsonObject.getBuildType().toLowerCase().equals("aabdebug")){
                msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_AAB_DEBUG_BUILD.name());
            }// aab build 관련 분기 처리
            else if(platform.toLowerCase().equals("android") && buildRequestJsonObject.getBuildType().toLowerCase().equals("aabrelease")){
                msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_AAB_RELEASE_BUILD.name());
            }else if(platform.toLowerCase().equals("ios") && !buildRequestJsonObject.getBuildType().equals("")){ // multi profile ios build
                msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_MULTI_PROFILE_IOS_BUILD.name());
            }

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.platform.name(),buildHistory.getPlatform());
            msg.put("BuildType", buildRequestJsonObject.getBuildType().toString());

            if(buildRequestJsonObject.getWorkspace_id() != null){
                workspace = workspaceService.findById(buildRequestJsonObject.getWorkspace_id());
                msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id());

                memberMapping = workspaceService.findByMemberRoleWorkspaceID(workspace.getWorkspace_id());
                msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());


                msg.put(PayloadKeyType.domainID.name(),memberLogin.getDomain_id());
                jsonHistoryVo.put(PayloadKeyType.workspace_id.name(),workspace.getWorkspace_id());
            }

            if(buildHistory.getProject_id() != null){

                buildProject = buildProjectService.findById(buildHistory.getProject_id());

                // send message set build project name, project dir path name
                jsonHistoryVo.put("buildProjectName",buildProject.getProject_name());
                msg.put("buildProjectdir",buildProject.getProject_dir_path());
                String template_versionCodeStr = "";
                int versionCodeInt = 0;

                if(platform.toLowerCase().equals("android")){

                    template_versionCodeStr = appConfigJSON.get("appVersionCode").toString();
                    versionCodeInt = new Integer(template_versionCodeStr);
                    versionCodeInt = versionCodeInt + 1;

                }else if(platform.toLowerCase().equals("ios")){

                    template_versionCodeStr = appConfigJSON.get("versionCode").toString();
                    versionCodeInt = new Integer(template_versionCodeStr);
                    versionCodeInt = versionCodeInt + 1;

                }


                // App Version code update...
                // buildSetting.setApp_version_code(String.valueOf(versionCodeInt));
                // buildSettingService.updateAppVersionSetting(buildSetting);
                msg.put(PayloadKeyType.projectID.name(),buildProject.getProject_id());

                // branch config setting
                branchSetting = buildProjectService.findByIdAndBranchId(buildProject.getProject_id());
                jsonBranchSetting.put(PayloadKeyType.branchUserId.name(),branchSetting.getBuilder_user_id());
                jsonBranchSetting.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());
                jsonBranchSetting.put(PayloadKeyType.hqKey.name(),memberLogin.getUser_login_id());
                jsonBranchSetting.put(PayloadKeyType.hqKeyPassword.name(),memberLogin.getPassword());

                if(buildProject.getVcs_id() != null){
                    vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
                    jsonVCSSetting.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
                    jsonVCSSetting.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
                    jsonVCSSetting.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
                    jsonVCSSetting.put(PayloadKeyType.vcsType.name(),vcsSetting.getVcs_type());
                    jsonVCSSetting.put(PayloadKeyType.hqKey.name(),memberLogin.getUser_login_id());
                    msg.put("repositoryObj",jsonVCSSetting.toJSONString());
                }

                if(buildProject.getKey_id() != null && platform.toLowerCase().equals("ios")){
                    KeyiOSSetting keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
                    msg.put("ios_unlock_keychain_password",aes256Decrypt(keyiOSSetting.getIos_unlock_keychain_password()));
                    msg.put("ios_profiles_json", keyiOSSetting.getIos_profiles_json());


                    // 1. jsonarray for 문 처리
                    // 2. key 값에 profile key name 기준으로 찾아내기
                    JSONParser parserToString = new JSONParser();
                    JSONArray iOSProfileJsonArray = (JSONArray) parserToString.parse(String.valueOf(keyiOSSetting.getIos_profiles_json()));
                    JSONObject jsonKeyNameObj = null;
                    for(int i = 0; i < iOSProfileJsonArray.size(); i++ ){
                        jsonKeyNameObj = (JSONObject) iOSProfileJsonArray.get(i);
                        String profileKeyStr = jsonKeyNameObj.get("profiles_key_name").toString();

                        // 단 키 값을 구할때 targets 이름에서 *_Debug, *_Release 기준을 분라해서 처리 해야한다.
                        if(buildRequestJsonObject.getProfileKeyName().equals(profileKeyStr)){
                            msg.put("exportMethod", jsonKeyNameObj.get("profiles_build_type").toString());
                        }
                    }

                }

                /**
                 *  TODO : build requeat quere 관리 기능 구현하기
                 */
                BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

                // apiHandler.handleRequest(request);
                if(builderQueueManaged.getBuild_queue_cnt() > builderQueueManaged.getBuild_queue_status_cnt()){
                    builderQueueManaged.setBuild_queue_status_cnt(builderQueueManaged.getBuild_queue_status_cnt() + 1);
                    builderQueueManagedService.update(builderQueueManaged);

                }else {

                    // apiHandler.processQueue();
                    entity.put(PayloadKeyType.error.name(),MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
                    return responseUtility.checkFailedResponse(entity);
                }



                msg.put("appConfigJSON",jsonApplyConfig.toJSONString());
                msg.put("appVersionCode",versionCodeInt);
                msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
                msg.put(PayloadKeyType.profile_type.name(),buildRequestJsonObject.getProfileType());

                // FTPSetting, JSONObject 로 필요한 값 세팅
                jsonFtpSetting.put("ftpUserId",ftpSetting.getFtp_user_id());
                jsonFtpSetting.put("ftpUserPwd",ftpSetting.getFtp_user_pwd());
                jsonFtpSetting.put("ftpUrl",ftpSetting.getFtp_url());
                jsonFtpSetting.put("ftpIP",ftpSetting.getFtp_ip());
                jsonFtpSetting.put("ftpPort",ftpSetting.getFtp_port());

                // Build History Vo
                jsonHistoryVo.put(PayloadKeyType.build_id.name(),buildHistory.getProject_id());
                jsonHistoryVo.put(PayloadKeyType.hqKey.name(),memberLogin.getUser_login_id());
                jsonHistoryVo.put(PayloadKeyType.platform.name(),buildHistory.getPlatform());
                // jsonHistoryVo.put("iOSProjectName",""); // iOS 프로젝트 작업시, 수정이 필요함

                historyTotalCnt = buildHistoryService.insert(buildHistory);

                msg.put("ftpSettingObj", jsonFtpSetting.toJSONString());
                msg.put(PayloadKeyType.branchSettingObj.name(),jsonBranchSetting.toJSONString());
                msg.put("id",historyTotalCnt);
                msg.put("buildHistoryVo",jsonHistoryVo.toJSONString()); // buildHistoryVo json string
                msg.put(PayloadKeyType.hqKey.name(),memberLogin.getUser_login_id());
                msg.put("iOSProjectName",""); // iOS 프로젝트 작업시, 수정이 필요함

                WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
                msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

                // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
                // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
                ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);
                // websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));

            }
            // branch id 가져오기
             //
            entity.put("historyCnt",historyTotalCnt);
            return responseUtility.makeSuccessResponse(entity);
        } catch (Exception e) {
            log.info(e.getMessage(),e);
            return responseUtility.makeSuccessResponse(entity);
        }
    }

    /** 빌드 완료 및 실패 적용 기능  */
    @RequestMapping(value = "/manager/build/history/update", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateBuildProject(@RequestBody BuildHistory buildHistory) {
        log.info("BuildHistoryController updateBuildProject  || {}", buildHistory);
        if( buildHistory.getProject_id() == null) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        memberService.updateMemberBuildYn(buildHistory.getHqKey(),"N");

        BuildProject buildProject = buildProjectService.findById(buildHistory.getProject_id());
        List<String> targetCode = new ArrayList<>();


        requestFactory.setBufferRequestBody(false);
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();

        targetCode.add("0000");

        // W-Hub restful api ftp  정보 전송
        // buildProject.template_version/ buildHistory.qrcode / buildHistory.platform_type
        parts.add(SessionType.MsgType.name(), "SVR_BUILB_INFO");
        parts.add(PayloadKeyType.platform.name(),buildHistory.getPlatform());
        parts.add("version",buildProject.getTemplate_version()); // targetVersion setting
        parts.add("scope", "F");
        parts.add("targetCode", "[\"0000\"]");
        parts.add("path", buildHistory.getQrcode());

        entity.put("historyCnt",buildHistory.getProject_history_id());
        return responseUtility.makeSuccessResponse(entity);
    }

    /** 빌드 이후 W-Hive 이벤트 추첨 신청 api */
    @RequestMapping(value = "/manager/build/history/whiveEvent", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getWHiveEventBuildSubMit(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        log.info("BuildHistoryController updateBuildProject  || {}", payload);

        HttpSession session = request.getSession();
        LoginSessionData loginSessionData = (LoginSessionData) session.getAttribute(SessionKeyContents.KEY_LOGIN_DATA.name());

        if(loginSessionData != null){

            // 유저 아이디 v
            // 코멘트 ...
            memberService.updateUserHiveEventCheckYn(loginSessionData.getUserId(), "Y", payload.get("comment").toString()); // 이벤트 소감 내용 저장 하는 기능 추가해야함...
        }


        // entity.put("historyCnt",buildHistory.getProject_history_id());
        return responseUtility.makeSuccessResponse(entity);
    }

    /** 빌드 이력 조회  */
    @RequestMapping(value = "/manager/build/history/getAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<BuildHistory> findAll(HttpServletRequest request) {
        String userId = common.getTokenToRealName(request);

        MemberLogin memberLogin = memberService.findByUserLoginID(userId);
        if (memberLogin != null) {
            // log.info("userId = {}", memberLogin.getUser_id());

            return buildHistoryService.findAll(memberLogin.getUser_id());
        } else {
            return new ArrayList<BuildHistory>();
        }
    }

    @RequestMapping(value = "/manager/build/history/{build_id}/{platform_type}/{project_name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<BuildHistory> findById(@PathVariable("build_id") Long build_id, @PathVariable("platform_type") String platform_type, @PathVariable("project_name") String project_name){
        return buildHistoryService.findById(build_id, platform_type, project_name);
    }

    /**
     * hisotryID 기준으로 project_history 테이블의
     * qrcode 컬럼 값(웹 서버에 저장된 아카이빙 파일 주소)을 읽어서 브라우저로 전달한다.
     * 2023/09/08 비활성화 처리
     * @soorink
     */
//    @RequestMapping(value = "/manager/build/history/startFileDownload", method = RequestMethod.POST)//, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> getApkFileDownloadStart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> payload) throws IOException {
        // send message
        Long historyID = Long.valueOf(payload.get("history_id").toString());

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);
        if(memberLogin == null) return null;

        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(historyID);
        String appDownloadUrl = buildHistory.getQrcode();

        // Java 내부에서는 URL Encode시, 공백을 예전 스펙인 '+'로 치환한다. 따라서, %20 으로 바꾸는 작업이 필요하다 / @soorink
        String fileName = URLEncoder.encode(buildHistory.getPlatform_build_file_name(), "UTF-8").replaceAll("\\+", "%20");
        String downloadFileName = buildHistory.getPlatform_build_file_name();

        String urlExceptFileName = ""; // 파일 이름을 제외한 http로 시작하는 주소

        switch (buildHistory.getPlatform()) {
            case "Android":
                urlExceptFileName = appDownloadUrl.substring(0, appDownloadUrl.lastIndexOf('/'));
                break;
            case "iOS":
                urlExceptFileName = appDownloadUrl.substring(46, appDownloadUrl.lastIndexOf('/'));
                break;
        }
        appDownloadUrl = urlExceptFileName + "/" + fileName;

        try {
            Resource resource = new UrlResource(appDownloadUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
            headers.setContentLength(resource.contentLength());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("filename", downloadFileName); // no encode filename

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 2023/09/08
     * hisotryID 기준으로 project_history 테이블의
     * qrcode 컬럼 값(웹 서버에 저장된 아카이빙 파일 주소)을 읽어서 브라우저로 전달한다.
     * 추가로 aws s3 스토리지 서버에 파일을 다운로드 하는 기능이다.
     * @pspotl87
     */
    @RequestMapping(value = "/manager/build/history/startFileDownload", method = RequestMethod.POST,  produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)//, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> getAwsS3FileDownloadStart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> payload) throws IOException {
        // send message
        Long historyID = Long.valueOf(payload.get("history_id").toString());
        String appDownloadUrl = null;
        S3ObjectInputStream objectInputStream = null;

        S3Object o;
        byte[] bytes = new byte[1024];
        HttpHeaders httpHeaders = new HttpHeaders();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);
        if(memberLogin == null) return null;

        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(historyID);
        if(buildHistory.getPlatform().toLowerCase().equals("android")){
            appDownloadUrl = buildHistory.getQrcode();

        }else if(buildHistory.getPlatform().toLowerCase().equals("ios")){
            appDownloadUrl = buildHistory.getQrcode().replace("manifest.plist", "")+ buildHistory.getPlatform_build_file_name();
        }

        log.info(buildHistory.toString());
        log.info("s3AppFilePath  {}  ", appDownloadUrl );
        // String fileNameBase = FilenameUtils.getBaseName(appDownloadUrl);

//        String fileNameBase = URLEncoder.encode(buildHistory.getQrcode(), "UTF-8").replaceAll("\\+", "%20");

        String fileNameFirst = FilenameUtils.getBaseName(appDownloadUrl);

        String extension = FilenameUtils.getExtension(appDownloadUrl);

//        String s3AppFilePath = appDownloadUrl.replace(fileNameBase+"."+extension, "");

//        if(buildHistory.getPlatform().toLowerCase().equals("android")){
            try{


            o = amazonS3.getObject(new GetObjectRequest(bucket, appDownloadUrl));



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

            }catch (Exception e){
                log.warn(e.getMessage(),e);
                return responseUtility.checkFailedResponse();
            }finally {
                objectInputStream.close();
            }

    }

    // 필수 파라미터 값 받아서 처리하기
    @RequestMapping(value = "/builder/build/history/plistAndHTMLFileToWas", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getPlistAndHTMlFileWtireToWas(HttpServletRequest request,@RequestBody String requestMap){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null) {
            JSONObject jsonObject = new JSONObject();
            JSONParser parserToString = new JSONParser();
            try {
                jsonObject = (JSONObject) parserToString.parse(requestMap);
                iosSFileUtil.createPlistFile(jsonObject);
                // iosSFileUtil.createHtmlFile(jsonObject);

                // chmod -R 775 권한 cli 실행하기
                CommandLine commandLineGitChmod = CommandLine.parse(CHMOD);
                commandLineGitChmod.addArgument("-R");
                commandLineGitChmod.addArgument("775");
                commandLineGitChmod.addArgument(qrCodeUploadDir + jsonObject.get(PayloadKeyType.platform.name()).toString() + "/" + jsonObject.get(PayloadKeyType.Project.name()).toString());
                executueCommonsCLIToTemplateCopy(commandLineGitChmod, CHMOD); // method name 수정 필요.

                return responseUtility.makeSuccessResponse();
            } catch (ParseException | IOException e) {
                log.warn("plistAndHTMLFileToWas error : {}", e.getMessage());
                return responseUtility.checkFailedResponse();
            }
        }else {
            return responseUtility.checkFailedResponse();
        }
    }

    /**
     *
     * @param request
     * @return
     */
    // //awsClientUtil.upload(getMultipartFile(path.toString()), "AppFileDir/PROJECT_P123");
    // {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @RequestMapping(value = "/builder/build/history/uploadSetupFileToAwsS3Server", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> getApkFileUploadToAwsS3(HttpServletRequest request){

        try {

        HttpSession session = request.getSession();
        BuilderLoginSessionData builderLoginSessionData = (BuilderLoginSessionData) session.getAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name());
        session.setMaxInactiveInterval(SessionConstants.TIME_OUT);


//        if(builderLoginSessionData != null){

            String getFilename = request.getParameterMap().get(PayloadKeyType.filename.name())[0];
            String getPlatformPath = request.getParameterMap().get(PayloadKeyType.platform.name())[0];
            String getProjectDir = request.getParameterMap().get("projectDir")[0];
            String getNowString = request.getParameterMap().get("nowString")[0];

            // 파일명의 yyyyMMddHHmmss 형태의 시간정보를 붙여준다
            getFilename = makeBuildedFileName(getFilename, getNowString);

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;

            log.info(multipartRequest.toString());
            log.info(multipartRequest.getFile("file").getName());

                if(multipartRequest.getFile("file") == null){
                    return responseUtility.checkFailedResponse();
                }else {
                    // bytes = multipartRequest.getFile("file").getBytes();
                    String parentDirString = qrCodeUploadDir + getPlatformPath + "/" + getProjectDir + "/" + getNowString;

                    awsClientUtil.upload(multipartRequest.getFile("file"), parentDirString, bucket);
                }

//                File parentDir = new File(parentDirString);
//                //디렉토리가 없으면 생성하자
//                if (!parentDir.exists()) {
//                    parentDir.mkdirs();
//                    parentDir.setWritable(true);
//                    parentDir.setReadable(true);
//                }
//
//                File childAppFile = new File(parentDirString, getFilename);
//                outputStream = new BufferedOutputStream(
//                        new FileOutputStream(childAppFile));
//                outputStream.write(bytes);
//                outputStream.flush();
//
//                CommandLine commandLineGitChmod = CommandLine.parse(CHMOD);
//                commandLineGitChmod.addArgument("-R");
//                commandLineGitChmod.addArgument("775");
//                commandLineGitChmod.addArgument(parentDirString);
//                executueCommonsCLIToTemplateCopy(commandLineGitChmod,CHMOD); // method name 수정 필요.


                    return responseUtility.makeSuccessResponse();
//                }else {
//                    return responseUtility.checkFailedResponse();
//                }

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return responseUtility.checkFailedResponse();
        } finally {

        }
    }


    @RequestMapping(value = "/builder/build/history/uploadSetupFileToWebServer", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> getApkFileUploadToWas(HttpServletRequest request){

        HttpSession session = request.getSession();
        BuilderLoginSessionData builderLoginSessionData = (BuilderLoginSessionData) session.getAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name());
        BufferedOutputStream outputStream = null;
        session.setMaxInactiveInterval(SessionConstants.TIME_OUT);


        if(builderLoginSessionData != null){

            String getFilename = request.getParameterMap().get(PayloadKeyType.filename.name())[0];
            String getPlatformPath = request.getParameterMap().get(PayloadKeyType.platform.name())[0];
            String getProjectDir = request.getParameterMap().get("projectDir")[0];
            String getNowString = request.getParameterMap().get("nowString")[0];

            // 파일명의 yyyyMMddHHmmss 형태의 시간정보를 붙여준다
            getFilename = makeBuildedFileName(getFilename, getNowString);

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;

            byte[] bytes = new byte[0];

            try {
                if(multipartRequest.getFile("file") == null){
                    return responseUtility.checkFailedResponse();
                }else {
                    bytes = multipartRequest.getFile("file").getBytes();
                }

                String parentDirString = qrCodeUploadDir + getPlatformPath + "/" + getProjectDir + "/" + getNowString;
                File parentDir = new File(parentDirString);
                //디렉토리가 없으면 생성하자
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                    parentDir.setWritable(true);
                    parentDir.setReadable(true);
                }

                File childAppFile = new File(parentDirString, getFilename);
                outputStream = new BufferedOutputStream(
                        new FileOutputStream(childAppFile));
                outputStream.write(bytes);
                outputStream.flush();

                CommandLine commandLineGitChmod = CommandLine.parse(CHMOD);
                commandLineGitChmod.addArgument("-R");
                commandLineGitChmod.addArgument("775");
                commandLineGitChmod.addArgument(parentDirString);
                executueCommonsCLIToTemplateCopy(commandLineGitChmod,CHMOD); // method name 수정 필요.

            } catch (IOException e) {
                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            } finally {
                try {
                    if(outputStream != null) outputStream.close();
                } catch (IOException e) {
                    log.warn(e.getMessage(),e);
                }
            }
            return responseUtility.makeSuccessResponse();
        }else {
            return responseUtility.checkFailedResponse();
        }
    }

    @RequestMapping(value = "/builder/build/history/CheckAuthPopup/{history_id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public void beforeCheckAuthPopup(HttpServletRequest request, HttpServletResponse response, @PathVariable("history_id") Long history_id) throws IOException {

        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(history_id);
        BuildProject buildProject = buildProjectService.findById(buildHistory.getProject_id());
        Workspace workspace = workspaceService.findById(buildProject.getWorkspace_id());
        MemberDetail memberDetail = memberService.findByIdDetail(workspace.getMember_id());

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 604800 * 100;

        URL url = null;
        if(buildHistory.getPlatform().toLowerCase().equals("android")){
            url = amazonS3.getUrl(bucket, buildHistory.getQrcode());//amazonS3.generatePresignedUrl(bucket, buildHistory.getQrcode(), expiration, com.amazonaws.HttpMethod.valueOf(HttpMethod.GET.name()));
        }else if(buildHistory.getPlatform().toLowerCase().equals("ios")){
            url = amazonS3.getUrl(bucket, buildHistory.getQrcode()+"manifest.plist");//amazonS3.generatePresignedUrl(bucket, buildHistory.getQrcode(), expiration, com.amazonaws.HttpMethod.valueOf(HttpMethod.GET.name()));
        }


        log.info("URL is {}", url.toString());

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>W-Hive 앱 파일 다운로드</title>\n" +
                "    <link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.13.1/themes/base/jquery-ui.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"/resources/demos/style.css\">\n" +
                "    <style>\n" +
                "        label, input { display:block; }\n" +
                "        input.text { margin-bottom:12px; width:95%; padding: .4em; }\n" +
                "        fieldset { padding:0; border:0; margin-top:25px; }\n" +
                "        h1 { font-size: 1.2em; margin: .6em 0; }\n" +
                "        div#users-contain { width: 350px; margin: 20px 0; }\n" +
                "        div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }\n" +
                "        div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }\n" +
                "        .ui-dialog .ui-state-error { padding: .3em; }\n" +
                "        .validateTips { border: 1px solid transparent; padding: 0.3em; }\n" +
                "    </style>\n" +
                "    <script src=\"https://code.jquery.com/jquery-3.6.0.js\"></script>\n" +
                "    <script src=\"https://code.jquery.com/ui/1.13.1/jquery-ui.js\"></script>");
        out.println("<script>\n" +
                "        $( function() {\n" +
                "            var dialog, form,\n" +
                "\n" +
                "                // From http://www.whatwg.org/specs/web-apps/current-work/multipage/states-of-the-type-attribute.html#e-mail-state-%28type=email%29\n" +
                "                email = $( \"#userID\" ),\n" +
                "                password = $( \"#password\" ),\n" +
                "                allFields = $( [] ).add( email ).add( password ),\n" +
                "                tips = $( \".validateTips\" );\n" +
                "\n" +
                "            function updateTips( t ) {\n" +
                "                tips\n" +
                "                    .text( t )\n" +
                "                    .addClass( \"ui-state-highlight\" );\n" +
                "                setTimeout(function() {\n" +
                "                    tips.removeClass( \"ui-state-highlight\", 1500 );\n" +
                "                }, 500 );\n" +
                "            }\n" +
                "\n" +
                "            function checkLength( o, n, min, max ) {\n" +
                "                if ( o.val().length > max || o.val().length < min ) {\n" +
                "                    o.addClass( \"ui-state-error\" );\n" +
                "                    updateTips( \"Length of \" + n + \" must be between \" +\n" +
                "                        min + \" and \" + max + \".\" );\n" +
                "                    return false;\n" +
                "                } else {\n" +
                "                    return true;\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            function checkRegexp( o, regexp, n ) {\n" +
                "                if ( !( regexp.test( o.val() ) ) ) {\n" +
                "                    o.addClass( \"ui-state-error\" );\n" +
                "                    updateTips( n );\n" +
                "                    return false;\n" +
                "                } else {\n" +
                "                    return true;\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            function addUser() {\n" +
                "                var valid = true;\n" +
                "                allFields.removeClass( \"ui-state-error\" );\n" +
                "                var passwordtemp = password.val(); \n" +
                "                if(passwordtemp != null) {\n" +
                "                    var params = {\n" +
                "                        user_login_id: \""+memberDetail.getUser_login_id()+"\",\n" +
                "                        password: passwordtemp\n" +
                "                    };\n" +
                "\n" +
                "                    const http = new XMLHttpRequest();\n" +
                "                    http.open('POST', '/manager/member/qrcodeAuthCheckDetail');\n" +
                "                    http.setRequestHeader('Content-type', 'application/json');\n" +
                "                    http.send(JSON.stringify(params)); // Make sure to stringify\n" +
                "                    http.onload = function() {\n" +
                "                        // Do whatever with response\n" +
                "                        //alert(JSON.stringify(params));\n" +
                "                        //alert(http.responseText);\n" +
                "                        var data = JSON.parse(http.responseText);\n" +
                "                        // console.log(data);\n" +
                "                        let _osCheck = navigator.userAgent;\n" +
                "                        if(data[0].auth_check_result){ \n" +
                "                        if(_osCheck.includes('Mac')) {\n" +
//                "                           let _a = document.createElement('a');\n" +
//                "                           _a.target = '_blank';\n" +
                "                           location.href = 'itms-services://?action=download-manifest&url="+url.toString()+"';\n" +
//                "                           document.body.appendChild(_a);\n" +
//                "                           _a.click();\n" +
                "                         }else{\n" +
                "                           location.href = '"+url.toString()+"'\n" +
                "                         }" +
                "                        \n" +
                "                            \n" +
                "                        }else {\n" +
                "                            alert('비밀번호를 다시 입력해주세요.')\n" +
                "                        }\n" +
                "                    };\n" +
                "\n" +
                "                    // alert('"+memberDetail.getUser_login_id()+" 님 반갑습니다');\n" +
                "                } else {\n" +
                "                    alert('"+memberDetail.getUser_login_id()+" 님 비밀번호를 입력하세요');\n" +
                "                }\n" +
//                "                dialog.dialog( \"close\" );\n" +
                "\n" +
                "                return valid;\n" +
                "            }\n" +
                "\n" +
                "            dialog = $( \"#dialog-form\" ).dialog({\n" +
                "                autoOpen: false,\n" +
                "                height: 400,\n" +
                "                width: 350,\n" +
                "                modal: true,\n" +
                "                buttons: {\n" +
                "                    \"확인\": addUser,\n" +
                "                    Cancel: function() {\n" +
                "                        dialog.dialog( \"close\" );\n" +
                "                    }\n" +
                "                },\n" +
                "                close: function() {\n" +
                "                    form[ 0 ].reset();\n" +
                "                    allFields.removeClass( \"ui-state-error\" );\n" +
                "                }\n" +
                "            });\n" +
                "\n" +
                "            form = dialog.find( \"form\" ).on( \"submit\", function( event ) {\n" +
                "                event.preventDefault();\n" +
                "                addUser();\n" +
                "            });\n" +
                "\n" +
                "            $( \"#create-user\" ).button().on( \"click\", function() {\n" +
                "                dialog.dialog( \"open\" );\n" +
                "            });\n" +
                "\n" +
                "            dialog.dialog( \"open\" );\n" +
                "        } );\n" +
                "    </script>");
        out.println("</head>\n" +
                "<body>\n" +
                "\n" +
                "<div id=\"dialog-form\" title=\"\">\n" +
                "    <p class=\"validateTips\">Login</p>\n" +
                "\n" +
                "    <form>\n" +
                "        <fieldset>\n" +
                "            <label for=\"email\">ID</label>\n" +
                "            <input type=\"text\" name=\"email\" id=\"userID\" value=\""+memberDetail.getUser_login_id()+"\" class=\"text ui-widget-content ui-corner-all\">\n" +
                "            <label for=\"password\">Password</label>\n" +
                "            <input type=\"password\" name=\"password\" id=\"password\" value=\"\" class=\"text ui-widget-content ui-corner-all\">\n" +
                "\n" +
                "            <input type=\"submit\" tabindex=\"-1\" style=\"position:absolute; top:-1000px\">\n" +
                "        </fieldset>\n" +
                "    </form>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<div id=\"users-contain\" class=\"ui-widget\">\n" +
                "</div>\n" +
                "<button id=\"create-user\">Login</button>\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>");
        out.flush();
        out.close();;
    }

    @RequestMapping(value = "/builder/build/history/getInstallUrl/{history_id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getInstallUrl(HttpServletRequest request, HttpServletResponse response, @PathVariable("history_id") Long history_id) throws IOException {
        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(history_id);
        BuildProject buildProject = buildProjectService.findById(buildHistory.getProject_id());
        Workspace workspace = workspaceService.findById(buildProject.getWorkspace_id());

        URL url = null;
        if(buildHistory.getPlatform().toLowerCase().equals("android")){
            url = amazonS3.getUrl(bucket, buildHistory.getQrcode());//amazonS3.generatePresignedUrl(bucket, buildHistory.getQrcode(), expiration, com.amazonaws.HttpMethod.valueOf(HttpMethod.GET.name()));
        }else if(buildHistory.getPlatform().toLowerCase().equals("ios")){
            url = amazonS3.getUrl(bucket, buildHistory.getQrcode()+"manifest.plist");//amazonS3.generatePresignedUrl(bucket, buildHistory.getQrcode(), expiration, com.amazonaws.HttpMethod.valueOf(HttpMethod.GET.name()));
        }

        log.info("URL is {}", url.toString());
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("url",url.toString());
        entities.add(entity);
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/buildhistory/logfileDownloadWebsocket/{id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLogFileDownload(@PathVariable("id") Long id){

        // id to select logpath
        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(id);
        String savePath = buildHistory.getLog_path(); // log file temp
        String fileName = buildHistory.getLogfile_name();

        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(buildHistory.getProject_id());

        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(),branchSetting.getBuilder_user_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());

        //파일을 읽어 스트림에 담기
        // send message 처리
        // HV_MSG_LOGFILE_DOWNLOAD_INFO
        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_LOGFILE_DOWNLOAD_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(),buildHistory.getPlatform());
        msg.put("filePath", savePath);
        msg.put("fileName", fileName);
        // hqkey

        msg.put(PayloadKeyType.branchSettingObj.name(),jsonBranchSetting.toJSONString());

        try {

            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
            log.info(String.valueOf(websocketSession));

            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
        } catch (IOException e) {
             log.warn(e.getMessage(),e);
        }

        // 결과는 web ui 단에서 받는다..
        return responseUtility.makeSuccessResponse();
    }

    // /manager/build/history/downloadLogFile
    // /api/buildhistory/logfileDownloadWebsocket
    @RequestMapping(value = "/manager/build/history/downloadLogFile", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLogFileDownload(HttpServletRequest request, @RequestBody Map<String, Object> requestMap){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        // id to select logpath
        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(Long.valueOf(requestMap.get("history_id").toString()));
        String savePath = buildHistory.getLog_path(); // log file temp
        String fileName = buildHistory.getLogfile_name();

        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(buildHistory.getProject_id());

        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(),branchSetting.getBuilder_user_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());

        //파일을 읽어 스트림에 담기
        // send message 처리
        // HV_MSG_LOGFILE_DOWNLOAD_INFO
        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_LOGFILE_DOWNLOAD_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(),buildHistory.getPlatform());
        msg.put("filePath", savePath);
        msg.put("fileName", fileName);
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        msg.put(PayloadKeyType.branchSettingObj.name(),jsonBranchSetting.toJSONString());

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
//            log.info(String.valueOf(websocketSession));
//
//            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));

        // 결과는 web ui 단에서 받는다..
        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/general/history/getInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generalAppGetInfo(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        MemberLogin memberLogin;

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        BuildProject buildProject = buildProjectService.findById(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));
        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));

        if (buildProject.getProduct_type().equals("generalApp") && buildProject.getPlatform().toLowerCase().equals("ios")) {
            HashMap<String, Object> msg = new HashMap<>();
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_BUILD_GENERAL_APP_INFO.name());
            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()));
            msg.put(PayloadKeyType.product_type.name(), buildProject.getProduct_type());
            msg.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());
            msg.put(PayloadKeyType.userID.name(), memberLogin.getUser_id());
            msg.put(PayloadKeyType.workspaceID.name(), buildProject.getWorkspace_id());
            msg.put(PayloadKeyType.projectID.name(), payload.get(PayloadKeyType.projectID.name()));
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

            // apiHandler.handleRequest(request);
            if (builderQueueManaged.getEtc_queue_cnt() > builderQueueManaged.getEtc_queue_status_cnt()){
                builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() + 1);
                builderQueueManagedService.etcUpdate(builderQueueManaged);
            } else {
                // apiHandler.processQueue();
                entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
                return responseUtility.checkFailedResponse(entity);
            }

            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);
        } else {
            Deploy deploySetting = deploySettingService.findById(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));
            JSONObject entity = new JSONObject();
            entity.put(PayloadKeyType.ApplicationID.name(), deploySetting.getAll_package_name());

            return responseUtility.makeSuccessResponse(entity);
        }

        return responseUtility.makeSuccessResponse();
    }

    // get all profile list
    // platform 기준으로 분기 처리 해야함
    @RequestMapping(value = "/manager/build/history/allMultiProfileList", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> allMultiProfileList(HttpServletRequest request, @RequestBody Map<String, Object> payload) {

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        BuildProject buildProject = buildProjectService.findById(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));

        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));

        HashMap<String, Object> msg = new HashMap<>();
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()));
        msg.put(PayloadKeyType.product_type.name(), buildProject.getProduct_type());
        msg.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());
        msg.put(PayloadKeyType.userID.name(), memberLogin.getUser_id());
        msg.put(PayloadKeyType.workspaceID.name(), payload.get(PayloadKeyType.workspaceID.name()));
        msg.put(PayloadKeyType.projectID.name(), payload.get(PayloadKeyType.projectID.name()));
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        // WHiveIdentity wHiveIdentity = WHiveWebSocketHandler.getIdentityByUserId(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);

        // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.
        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//            log.info(String.valueOf(websocketSession));
//
//            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));

        return responseUtility.makeSuccessResponse();
    }

    // /manager/build/history/getMultiProfileAppConfig
    @RequestMapping(value = "/manager/build/history/getMultiProfileAppConfig", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getMultiProfileList(HttpServletRequest request, @RequestBody Map<String, Object> payload) {

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        BuildProject buildProject = buildProjectService.findById(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));

        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));

        HashMap<String, Object> msg = new HashMap<>();
        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(), payload.get(PayloadKeyType.platform.name()));
        msg.put(PayloadKeyType.product_type.name(), buildProject.getProduct_type());
        msg.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());
        msg.put(PayloadKeyType.userID.name(), memberLogin.getUser_id());
        msg.put(PayloadKeyType.workspaceID.name(), payload.get(PayloadKeyType.workspaceID.name()));
        msg.put(PayloadKeyType.projectID.name(), payload.get(PayloadKeyType.projectID.name()));
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        log.info(msg.toString());

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//            log.info(String.valueOf(websocketSession));
//
//            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/general/build/history/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createGeneralAppBuildProject(HttpServletRequest request, @RequestBody BuildRequestJsonObject buildRequestJsonObject) {
        JSONObject jsonFtpSetting = new JSONObject();
        JSONObject jsonHistoryVo = new JSONObject();
        JSONObject jsonVCSSetting = new JSONObject();
        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonentity = new JSONObject();

        ArrayList<String> AndroidAppIDList = new ArrayList<String>();
        ArrayList<String> iOSAppIDList = new ArrayList<String>();
        ArrayList<String> platformTypeList = new ArrayList<String>();

        FTPSetting ftpSetting; // ftp 정보
        BranchSetting branchSetting = null;
        BuildHistory buildHistory;
        Workspace workspace;
        BuildProject buildProject;
        VCSSetting vcsSetting;
        MemberMapping memberMapping;
        String AppID = ""; // android appid

        buildHistory = buildRequestJsonObject.getBuildHistory();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        Pricing pricing = pricingService.findById(memberLogin.getUser_id());

        if(pricing == null){
            memberLogin.setPay_change_yn("N");
        }else{
            memberLogin.setPay_change_yn(pricing.getPay_change_yn());
        }

        String platform = buildHistory.getPlatform().toLowerCase();
        if (platform.toLowerCase().equals("android")) {
            if (buildRequestJsonObject.getAppConfig().get("appId") != null) {
                AppID = buildRequestJsonObject.getAppConfig().get("appId").toString();
            } else {
                Deploy getDeployObject = deploySettingService.findById(Long.valueOf(buildRequestJsonObject.getBuildHistory().getProject_id().toString()));
                AppID = getDeployObject.getAll_package_name();
            }
        } else if (platform.toLowerCase().equals("ios")) {
            AppID = buildHistory.getIos_builded_target_or_bundle_id();
        }

        if(springProfile.equals("onpremiss")){
            // license key 상에 Android, iOS AppID List 가져오기
//                utility.checkHybridLicense(AndroidAppIDList, iOSAppIDList, platformTypeList);

            if (!Utility.getIsDemoLicense()) {
                if (AppID != null && platform != null) {
                    if (!platformTypeList.contains(platform)){
                        System.err.println("Platform does not match to license file. please reissue license.");

                        // platform 에러 관련 문구 추가
                        jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_PLATFORM.getMessage());
                        return responseUtility.checkFailedResponse(jsonentity);
                    }
                    if (platform.toLowerCase().equals(PayloadKeyType.android.name())){
                        if (!AndroidAppIDList.contains(AppID)) {
                            System.err.println("Real Android AppID does not match to license file. please reissue license.");

                            // appID 에러 관련 문구 추가
                            jsonentity.put(PayloadKeyType.error.name(),MessageString.LICENSE_DOES_NOT_MATCH_ANDROID_APPID.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }
                    } else if(platform.toLowerCase().equals("ios")){
                        if (!iOSAppIDList.contains(AppID)) {
                            System.err.println("Real iOS AppID does not match to license file. please reissue license.");

                            // appID 에러 관련 문구 추가
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
        } else {
            try {
                /**
                 * whive 서비스 일 경우 라이센스 기반이 아난 사용자 정보 내 에서 app id 기준으로 조회 된다.
                 */
                JSONObject appIDJSON = (JSONObject) parser.parse(memberLogin.getAppid_json().toString());
                if (memberLogin.getPay_change_yn() == null || memberLogin.getPay_change_yn().equals("N")) {
                    if (platform.toLowerCase().equals(PayloadKeyType.android.name())) {
                        if (!appIDJSON.get("androidAppID1").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }
                    }

                    if (platform.toLowerCase().equals(PayloadKeyType.ios.name())) {
                        jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                        return responseUtility.checkFailedResponse(jsonentity);

                    }

                } else {
                    if (platform.toLowerCase().equals(PayloadKeyType.android.name())) {
                        if(appIDJSON.get("androidAppID1").toString().equals("") && appIDJSON.get("androidAppID2").toString().equals("")){
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                        if (!appIDJSON.get("androidAppID1").toString().equals(AppID) && !appIDJSON.get("androidAppID2").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    } else if (platform.toLowerCase().equals(PayloadKeyType.ios.name())) {
                        if(appIDJSON.get("iOSAppID1").toString().equals("") && appIDJSON.get("iOSAppID2").toString().equals("")){
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                        if (!appIDJSON.get("iOSAppID1").toString().equals(AppID) && !appIDJSON.get("iOSAppID2").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    }
                }
            } catch (ParseException e) {
                log.warn(e.getMessage(), e);
                jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                return responseUtility.checkFailedResponse(jsonentity);
            } catch (NullPointerException e){
                jsonentity.put(PayloadKeyType.error.name(),MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                return responseUtility.checkFailedResponse(jsonentity);
            }

        }

        if( buildHistory.getProject_id() == null) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        ftpSetting = buildProjectService.findByIdAndFTPId(buildHistory.getProject_id());

        int historyTotalCnt = buildHistoryService.insert(buildHistory);

        try {

            HashMap<String, Object> msg = new HashMap<>();
            String buildType = buildRequestJsonObject.getBuildType().toLowerCase();

            switch (platform) {
                case "android":
                    msg.put(PayloadKeyType.PackageName.name(), AppID);
                    switch (buildType) {
                        case "debug":
                            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_GENERAL_ANDROID_DEBUG_BUILD.name());
                            break;
                        case "release":
                            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_GENERAL_ANDROID_RELEASE_BUILD.name());
                            break;
                        case "aabdebug":
                            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_GENERAL_ANDROID_AAB_DEBUG_BUILD.name());
                            break;
                        case "aabrelease":
                            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_GENERAL_ANDROID_AAB_RELEASE_BUILD.name());
                            break;
                    }
                    break;
                case "ios":
                    jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(), AppID);
                    switch (buildType) {
                        case "debug":
                            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_GENERAL_IOS_DEBUG_BUILD.name());
                            break;
                        case "release":
                            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_GENERAL_IOS_RELEASE_BUILD.name());
                            break;
                    }
                    break;
            }

            msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
            msg.put(PayloadKeyType.platform.name(), platform);
            msg.put(PayloadKeyType.buildType.name(), buildType);

            if (platform.toLowerCase().equals("ios")) {
                msg.put("iosAppConfig", buildRequestJsonObject.getAppConfig());
            }

            if (buildRequestJsonObject.getWorkspace_id() != null) {
                workspace = workspaceService.findById(buildRequestJsonObject.getWorkspace_id());
                msg.put(PayloadKeyType.workspaceID.name(), workspace.getWorkspace_id());

                memberMapping = workspaceService.findByMemberRoleWorkspaceID(workspace.getWorkspace_id());
                msg.put(PayloadKeyType.userID.name(),memberMapping.getMember_id());

                msg.put(PayloadKeyType.domainID.name(), memberLogin.getDomain_id());
                jsonHistoryVo.put(PayloadKeyType.workspace_id.name(),workspace.getWorkspace_id());
            }

            if (buildHistory.getProject_id() != null) {

                buildProject = buildProjectService.findById(buildHistory.getProject_id());

                // send message set build project name, project dir path name
                jsonHistoryVo.put("buildProjectName", buildProject.getProject_name());
                msg.put("buildProjectdir", buildProject.getProject_dir_path());
                msg.put(PayloadKeyType.projectID.name(), buildProject.getProject_id());

                // branch config setting
                branchSetting = buildProjectService.findByIdAndBranchId(buildProject.getProject_id());
                jsonBranchSetting.put(PayloadKeyType.branchUserId.name(), branchSetting.getBuilder_user_id());
                jsonBranchSetting.put(PayloadKeyType.branchName.name(), branchSetting.getBuilder_name());
                jsonBranchSetting.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

                if (buildProject.getVcs_id() != null) {
                    vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
                    jsonVCSSetting.put(PayloadKeyType.repositoryURL.name(), vcsSetting.getVcs_url());
                    jsonVCSSetting.put(PayloadKeyType.repositoryId.name(), vcsSetting.getVcs_user_id());
                    jsonVCSSetting.put(PayloadKeyType.repositoryPassword.name(), vcsSetting.getVcs_user_pwd());
                    jsonVCSSetting.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
                    jsonVCSSetting.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
                    msg.put("repositoryObj", jsonVCSSetting.toJSONString());
                }

                if (buildProject.getKey_id() != null && platform.equals("ios")) {
                    KeyiOSSetting keyiOSSetting = signingKeySettingService.findByiOSKeyID(buildProject.getKey_id());
                    msg.put("ios_unlock_keychain_password", aes256Decrypt(keyiOSSetting.getIos_unlock_keychain_password()));
                    msg.put("ios_profiles_json", keyiOSSetting.getIos_profiles_json());
                }
                msg.put("exportMethod", buildType);
                msg.put("appConfigJSON",jsonApplyConfig.toJSONString());
                msg.put(PayloadKeyType.product_type.name(),buildProject.getProduct_type());
                msg.put(PayloadKeyType.profile_type.name(),buildRequestJsonObject.getProfileType());
            }

            // FTPSetting, JSONObject 로 필요한 값 세팅
            jsonFtpSetting.put("ftpUserId", ftpSetting.getFtp_user_id());
            jsonFtpSetting.put("ftpUserPwd", ftpSetting.getFtp_user_pwd());
            jsonFtpSetting.put("ftpUrl", ftpSetting.getFtp_url());
            jsonFtpSetting.put("ftpIP", ftpSetting.getFtp_ip());
            jsonFtpSetting.put("ftpPort", ftpSetting.getFtp_port());

            // Build History Vo
            jsonHistoryVo.put(PayloadKeyType.build_id.name(), buildHistory.getProject_id());
            jsonHistoryVo.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            jsonHistoryVo.put(PayloadKeyType.platform.name(), buildHistory.getPlatform());

            msg.put("ftpSettingObj", jsonFtpSetting.toJSONString());
            msg.put(PayloadKeyType.branchSettingObj.name(), jsonBranchSetting.toJSONString());
            msg.put("id", historyTotalCnt);
            msg.put("buildHistoryVo", jsonHistoryVo.toJSONString()); // buildHistoryVo json string
            msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());
            msg.put("macPassword", macPassword);
            
            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
//            websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));
            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

            entity.put("historyCnt",historyTotalCnt);
            return responseUtility.makeSuccessResponse(entity);
        } catch (Exception e) {
            log.error("Exception createGeneralAppBuildProject = {}", e.getMessage(), e);
            return responseUtility.makeSuccessResponse(entity);
        }
    }

    // project_history 테이블의 project_history_id가 맞는 row 검색
    @RequestMapping(value = "/manager/build/history/{project_history_id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody BuildHistory findByBuildHistoryId(@PathVariable("project_history_id") Long project_history_id) {
        return buildHistoryService.findByHistoryIdOne(project_history_id);
    }
}