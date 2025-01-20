package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.build.BuildProject;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.buildhistory.BuildHistory;
import com.pspotl.sidebranden.common.buildhistory.BuildHistoryService;
import com.pspotl.sidebranden.common.buildsetting.BuildSettingService;
import com.pspotl.sidebranden.common.deploy.Deploy;
import com.pspotl.sidebranden.common.deploy.DeploySettingService;
import com.pspotl.sidebranden.common.deployhistory.DeployHistory;
import com.pspotl.sidebranden.common.deployhistory.DeployHistoryService;
import com.pspotl.sidebranden.common.deployhistory.DeployReqeusetHistory;
import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
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
import com.pspotl.sidebranden.manager.enums.StatusType;
import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketBuilderService;
import com.pspotl.sidebranden.manager.util.PostControllerException;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.Utility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
   스토어 배포 이력 관련 Controller
 **/
@Slf4j
@RestController
public class DeployHistoryController {

    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BuildSettingService buildSettingService;

    @Autowired
    MemberService memberService;

    @Autowired
    VCSSettingService vcsSettingService;

    @Autowired
    DeployHistoryService deployHistoryService;

    @Autowired
    DeploySettingService deploySettingService;

    @Autowired
    BuildHistoryService buildHistoryService;

    @Autowired
    SigningKeySettingService signingKeySettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    Common common;

    @Autowired
    PricingService pricingService;

    @Autowired
    private ResponseUtility responseUtility;

    private Utility utility;

    @Value("${spring.profiles}")
    private String springProfile;

    private JSONParser parser = new JSONParser();

    WebSocketSession websocketSession;

    JSONObject jsonBranchSetting = new JSONObject();
    JSONObject entity = new JSONObject();


    // TODO: builder 큐 관리 기능 추가
    // 배포 시작 기능
    @RequestMapping(value = "/manager/deploy/history/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createDeployHistory(HttpServletRequest request, @RequestBody DeployReqeusetHistory deployReqeusetHistory){

        JSONObject jsonApplyConfig = new JSONObject();
        JSONObject jsonRepository = new JSONObject();
        JSONObject jsonDeployObj = new JSONObject();
        JSONObject jsonentity = new JSONObject();

        ArrayList<String> AndroidAppIDList = new ArrayList<String>();
        ArrayList<String> iOSAppIDList = new ArrayList<String>();
        ArrayList<String> platformTypeList = new ArrayList<String>();

        Workspace workspace;
        BranchSetting branchSetting;
        MemberLogin memberLogin;
        VCSSetting vcsSetting;
        KeyiOSSetting keyiOSSetting;

        DeployHistory deployHistory = deployReqeusetHistory.getDeployHistory();
        String[] testlength = new String[0];
        HashSet<Integer> setint = new HashSet<>();

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            if(memberLogin.getBuild_yn().equals("Y")) {
                return responseUtility.makeSuccessResponse(entity);
            }
        }else {
            return responseUtility.makeSuccessResponse(entity);
        }

        Pricing pricing = pricingService.findById(memberLogin.getUser_id());
        if(pricing == null){
            memberLogin.setPay_change_yn("N");
        }else{
            memberLogin.setPay_change_yn(pricing.getPay_change_yn());
        }

        String AppID = ""; // 어떻게 가지고 올지 고민해보기
        String platform = ""; //

        // deploy 시 license 체크 기능 필수! 한번 물어보기
        //TODO  onpremiss 일경우 whive 라이센스 체크
        if(springProfile.equals("onpremiss")){
            // license key 상에 Android, iOS AppID List 가져오기
//                utility.checkHybridLicense(AndroidAppIDList, iOSAppIDList, platformTypeList);

            if(!Utility.getIsDemoLicense()) {
                if (AppID != null && platform != null) {

                    if(!platformTypeList.contains(platform)){
                        System.err.println("Platform does not match to license file. please reissue license.");

                        // platform 에러 관련 문구 추가
                        jsonentity.put(PayloadKeyType.error.name(), MessageString.LICENSE_DOES_NOT_MATCH_PLATFORM.getMessage());
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

                        if (!appIDJSON.get("androidAppID1").toString().equals(AppID) || !appIDJSON.get("androidAppID2").toString().equals(AppID)) {
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                    } else if (platform.toLowerCase().equals(PayloadKeyType.ios.name())) {
                        if(appIDJSON.get("iOSAppID1").toString().equals("") && appIDJSON.get("iOSAppID2").toString().equals("")){
                            jsonentity.put(PayloadKeyType.error.name(), MessageString.APP_ID_INVALID_TO_HIVE.getMessage());
                            return responseUtility.checkFailedResponse(jsonentity);
                        }

                        if (!appIDJSON.get("iOSAppID1").toString().equals(AppID) || !appIDJSON.get("iOSAppID2").toString().equals(AppID)) {
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

        HashMap<String, Object> msg = new HashMap<>();
        // 조건에 따라서 msg type 변경 하기
        if(deployReqeusetHistory.getDeploy_type().toUpperCase().equals(StatusType.ALPHA.toString())){
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_ALPHA_INFO.name());
        }else if(deployReqeusetHistory.getDeploy_type().toUpperCase().equals(StatusType.BETA.toString())){
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_BETA_INFO.name());
        }else if(deployReqeusetHistory.getDeploy_type().toUpperCase().equals(StatusType.DEPLOY.toString())){
            msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_DEPLOY_REAL_DEPLOY_INFO.name());
        }

        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(),deployHistory.getPlatform_type());
        msg.put(PayloadKeyType.hqKey.name(),memberLogin.getUser_login_id());

        if(deployReqeusetHistory.getWorkspace_id() != null){
            workspace = workspaceService.findById(deployReqeusetHistory.getWorkspace_id());
            msg.put("workspaceName",workspace.getWorkspace_name());
            msg.put(PayloadKeyType.workspace_id.name(),workspace.getWorkspace_id());
            msg.put("user_id",workspace.getMember_id());
        }

        BuildProject buildProject = buildProjectService.findById(deployHistory.getProject_id());

        msg.put("product_type", buildProject.getProduct_type());
        // BuildSetting buildSetting =  buildSettingService.findByBuildId(deployHistory.getProject_id());

        vcsSetting = vcsSettingService.findbyID(buildProject.getVcs_id());
        Deploy deploy = deploySettingService.findById(deployHistory.getProject_id());

        if(deploy.getAll_signingkey_id() == null){
            return responseUtility.checkFailedResponse();
        }

        if(buildProject.getPlatform().toLowerCase().equals("ios")){
            jsonApplyConfig.put("iOSProjectName","");
            keyiOSSetting = signingKeySettingService.findByiOSKeyID(deploy.getAll_signingkey_id());

            // ios 배포 인증서 (p8 키 파일)가 있는지 확인
            if (keyiOSSetting.getIos_key_path().isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("has_deploy_key", "no");
                return responseUtility.makeSuccessResponse(obj);
            }

            jsonDeployObj.put("apple_key_id",keyiOSSetting.getIos_key_id());
            jsonDeployObj.put("apple_issuer_id",keyiOSSetting.getIos_issuer_id());
            jsonDeployObj.put("apple_app_store_connect_api_key",keyiOSSetting.getIos_key_path());
            jsonDeployObj.put("all_package_name", deploy.getAll_package_name());
            jsonDeployObj.put("build_id", deploy.getBuild_id());
            jsonDeployObj.put("ios_app_bundleID", deployReqeusetHistory.getIos_app_bundleID());

            msg.put("jsonDeployObj",jsonDeployObj.toJSONString());
        } else if (buildProject.getPlatform().toLowerCase().equals("android")) {
            KeyAndroidSetting keyAndroidSetting = signingKeySettingService.findByAndroidID(deploy.getAll_signingkey_id());

            // Adnroid 배포 인증서 (json 키 파일)가 있는지 확인
            if (keyAndroidSetting.getAndroid_deploy_key_path().isEmpty()){
                JSONObject obj = new JSONObject();
                obj.put("has_deploy_key", "no");
                return responseUtility.makeSuccessResponse(obj);
            }

            jsonDeployObj.put("all_package_name", deploy.getAll_package_name());
            jsonDeployObj.put("googlePlayAccessJson", keyAndroidSetting.getAndroid_deploy_key_path());
            jsonDeployObj.put("build_id", deploy.getBuild_id());

            msg.put("jsonDeployObj",jsonDeployObj.toJSONString());
        }

//        if(buildSetting != null){
//            jsonApplyConfig.put(PayloadKeyType.ApplicationID.name(),buildSetting.getApp_id());
//            jsonApplyConfig.put(PayloadKeyType.AppName.name(),buildSetting.getApp_name());
//            jsonApplyConfig.put(PayloadKeyType.AppVersion.name(),buildSetting.getApp_version());
//            jsonApplyConfig.put(PayloadKeyType.AppVersionCode.name(),buildSetting.getApp_version_code()); // 수정해야함...
//
//        }

        if (vcsSetting != null){
            jsonRepository.put(PayloadKeyType.vcsType.name(), vcsSetting.getVcs_type());
            jsonRepository.put(PayloadKeyType.repositoryId.name(),vcsSetting.getVcs_user_id());
            jsonRepository.put(PayloadKeyType.repositoryPassword.name(),vcsSetting.getVcs_user_pwd());
            jsonRepository.put(PayloadKeyType.repositoryURL.name(),vcsSetting.getVcs_url());
        }

        // deploy .env 설정 값 수정 및 추가에 필요한 파라미터 추가하기
        BuildHistory buildHistory = buildHistoryService.findByHistoryIdOne(deployReqeusetHistory.getProject_history_id());

        // log path, logfile name 경로 및 file 이름
        // android 는 해당 파라미터로 처리 가능
        msg.put("app_path",buildHistory.getPlatform_build_file_path());
        msg.put("appfile_name",buildHistory.getPlatform_build_file_name());

        // ios 는 app store api key 에 필요한 값들도 추가해야함..


        msg.put(PayloadKeyType.jsonApplyConfig.name(),jsonApplyConfig.toJSONString());
        msg.put(PayloadKeyType.AppVersionCode.name(),buildHistory.getBuild_number());

        msg.put("domain_id",memberLogin.getDomain_id());

        msg.put("project_id",deployReqeusetHistory.getDeployHistory().getProject_id());


        branchSetting = buildProjectService.findByIdAndBranchId(deployHistory.getProject_id());

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getDeploy_queue_cnt() > builderQueueManaged.getDeploy_queue_status_cnt()){
            builderQueueManaged.setDeploy_queue_cnt(builderQueueManaged.getDeploy_queue_status_cnt() + 1);
            builderQueueManagedService.deployUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }

        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(),branchSetting.getBuilder_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());
        log.info("branchSetting.getBranch_user_id() : {}", branchSetting.getBuilder_user_id());
        msg.put(PayloadKeyType.branchSettingObj.name(),jsonBranchSetting.toJSONString());
        msg.put(PayloadKeyType.jsonRepository.name(),jsonRepository.toJSONString());


        deployHistory.setDeploy_id(deploy.getDeploy_id());

        // deploy history data insert
        int deployHistoryCount = deployHistoryService.insert(deployHistory);


        msg.put("deployHistoryID",deployHistoryCount);

        // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        try {
//
//                websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
//                websocketSession.sendMessage(new TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(msg)));
//                entity.put("deployHistoryID",deployHistoryCount);
//                return responseUtility.makeSuccessResponse(entity);
//
//        } catch (IOException e) {
//
//            log.warn(e.getMessage(),e);
//        }

        return responseUtility.makeSuccessResponse(entity);

    }

    @RequestMapping(value = "/manager/deploy/history/search/historyList", method = RequestMethod.POST, produces = {"application/json"})
    public @ResponseBody
    List<DeployHistory> findById(@RequestBody Map<String, Object> payload){
        return deployHistoryService.findByIDList(Long.valueOf(payload.get(PayloadKeyType.projectID.name()).toString()));
    }

    // TODO: builder 큐 관리 기능 추가
    // log file download
    @RequestMapping(value = "/manager/deploy/history/download/logFileFromWebSocket", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLogFileDownload(HttpServletRequest request, @RequestBody Map<String, Object> requestMap){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        // id to select logpath
        DeployHistory deployHistory = deployHistoryService.findByIdOne(Long.valueOf(requestMap.get("history_id").toString()));
        String savePath = deployHistory.getLog_path(); // log file temp
        String fileName = deployHistory.getLogfile_name();

        BranchSetting branchSetting = buildProjectService.findByIdAndBranchId(deployHistory.getProject_id());

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


        BuildProject buildProject = buildProjectService.findById(deployHistory.getProject_id());

        jsonBranchSetting.put(PayloadKeyType.branchUserId.name(),branchSetting.getBuilder_user_id());
        jsonBranchSetting.put(PayloadKeyType.branchName.name(),branchSetting.getBuilder_name());

        //파일을 읽어 스트림에 담기
        // send message 처리
        // HV_MSG_LOGFILE_DOWNLOAD_INFO
        HashMap<String, Object> msg = new HashMap<>();

        msg.put(SessionType.MsgType.name(), ClientMessageType.HV_MSG_LOGFILE_DOWNLOAD_INFO.name());
        msg.put(PayloadKeyType.sessType.name(), SessionType.BRANCH.name());
        msg.put(PayloadKeyType.platform.name(),buildProject.getPlatform());
        msg.put("filePath", savePath);
        msg.put("fileName", fileName);
        msg.put(PayloadKeyType.hqKey.name(), memberLogin.getUser_login_id());

        msg.put(PayloadKeyType.branchSettingObj.name(),jsonBranchSetting.toJSONString());

        // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendMessage(wHiveIdentity, msg);

//        try {
//
//            websocketSession = WHiveWebSocketHandler.getSessionByIdentity(jsonBranchSetting.get(PayloadKeyType.branchUserId.name()).toString(), SessionType.BRANCH);
//            log.info(String.valueOf(websocketSession));
//
//            websocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(msg)));
//        } catch (IOException e) {
//
//            log.warn(e.getMessage(),e);
//        }

        // 결과는 web ui 단에서 받는다..
        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/deploy/project/getProductType/{project_id}", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProductType(@PathVariable("project_id") Long project_id) {
        JSONObject obj = new JSONObject();

        BuildProject buildProject = buildProjectService.findById(project_id);

        obj.put("product_type", buildProject.getProduct_type());

        return responseUtility.makeSuccessResponse(obj);
    }
}
