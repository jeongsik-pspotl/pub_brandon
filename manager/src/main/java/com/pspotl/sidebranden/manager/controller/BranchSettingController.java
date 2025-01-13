package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingService;
import com.pspotl.sidebranden.common.branchsetting.BuilderSelectBoxList;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.manager.SessionConstants;
import com.pspotl.sidebranden.manager.enums.SessionKeyContents;
import com.pspotl.sidebranden.manager.model.BuilderLoginSessionData;
import com.pspotl.sidebranden.manager.security.AuthService;
import com.pspotl.sidebranden.manager.security.jwt.TokenData;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
public class BranchSettingController {

    @Autowired
    BranchSettingService branchSettingService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    MemberService memberService;

    @Autowired
    Common common;

    @Autowired
    private ResponseUtility responseUtility;

    private final AuthService authService;

    private static final String BRANCH_NAME = "branch_name";
    private static final String BRANCH_USER_ID = "branch_user_id";

    @Autowired
    public BranchSettingController(AuthService authService) {
        this.authService = authService;

    }

    @RequestMapping(value = "/manager/branchSetting/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createBuildProjectAll(@RequestBody BranchSetting branchSetting) {
        if( branchSetting.getBuilder_name() == null  || branchSetting.getBuilder_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        branchSettingService.insert(branchSetting);

        int builderID = branchSettingService.findByBuilderID();
        BuilderQueueManaged builderQueueManaged = new BuilderQueueManaged();

        builderQueueManaged.setBuilder_id((long) builderID);
        builderQueueManaged.setProject_queue_status_cnt(0L);
        builderQueueManaged.setDeploy_queue_status_cnt(0L);
        builderQueueManaged.setBuild_queue_status_cnt(0L);
        builderQueueManaged.setEtc_queue_status_cnt(0L);
        builderQueueManaged.setDeploy_queue_cnt(5L);
        builderQueueManaged.setProject_queue_cnt(5L);
        builderQueueManaged.setBuild_queue_cnt(5L);
        builderQueueManaged.setEtc_queue_cnt(20L);

        builderQueueManagedService.insert(builderQueueManaged);

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/branchSetting/update", method = RequestMethod.POST)
    public ResponseEntity<Object> updateBuilderPassword(@RequestBody BranchSetting branchSetting){


        if(branchSetting.getBuilder_password() == null || branchSetting.getBuilder_password().equals("")){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_NEW_USER_PASSWORD.getMessage());
        }

        branchSettingService.updateBuilderPassword(branchSetting);

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/branchSetting/getAll", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<BranchSetting> findAll() {
        return branchSettingService.findAll();
    }

    @RequestMapping(value = "/manager/branchSetting/selectByBranchId/{branch_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    BranchSetting findByID(@PathVariable("branch_id") Long branch_id) {
        return branchSettingService.findbyID(branch_id);
    }

    @RequestMapping(value = "/manager/builderSetting/selectBySelectBoxList", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<BuilderSelectBoxList> findBySelectBOXList() {
        return branchSettingService.findBySelectBOXLIst();
    }

    @RequestMapping(value = "/manager/branchSetting/selectBySelectBoxListBranchId/{branch_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody BuilderSelectBoxList  findBySelectBOXListByID(@PathVariable("branch_id") Long branch_id){
        return branchSettingService.findBySelectBOXListByID(branch_id);
    }

    // branch 이름 체크 조회
    @RequestMapping(value = "/manager/branchSetting/checkName", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> branchNameCheck(HttpServletRequest request, @RequestBody Map<String, Object> payload) {

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin == null){
            return null;
        }

        if( payload.get(BRANCH_NAME).toString() == null  || payload.get(BRANCH_NAME).toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        BranchSetting branchSetting = branchSettingService.findByBranchName(payload.get(BRANCH_NAME).toString());

        JSONObject obj = new JSONObject();
        if (branchSetting == null) {
            // 동일한 빌더 이름이 없음
            obj.put("builder_name_can_use","yes");
        } else {
            // 동일한 빌더 이름이 있음
            obj.put("builder_name_can_use","no");
        }
        return responseUtility.makeSuccessResponse(obj);
    }

    // branch user id 체크 조회
    @RequestMapping(value = "/manager/branchSetting/checkUserId", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> branchUserIdCheck(HttpServletRequest request, @RequestBody Map<String, Object> payload) {

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if (memberLogin == null) {
            return null;
        }

        if (payload.get(BRANCH_USER_ID).toString() == null  || payload.get(BRANCH_USER_ID).toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        BranchSetting branchSetting = branchSettingService.findByUserID(payload.get(BRANCH_USER_ID).toString());

        JSONObject obj = new JSONObject();
        if (branchSetting == null) {
            // 동일한 user id 없음
            obj.put("builder_userid_can_use","yes");
        } else {
            // 동일한 user id 있음
            obj.put("builder_userid_can_use","no");
        }
        return responseUtility.makeSuccessResponse(obj);
    }

    @RequestMapping(value = "/manager/branchSetting/builderLoginCheck", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> builderLoginCheck(HttpServletRequest request){

        JSONObject obj = new JSONObject();

        try {


        String branch_user_id = request.getParameter("userid").toString();
        String branch_user_powd = request.getParameter("pword").toString();
        // password 체크 기능 추가 ..
        boolean pswdCheck = true;//branchSettingService.findByBuilderPasswordCheck(branchSetting);

        if(pswdCheck){

            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(SessionConstants.TIME_OUT);

            BuilderLoginSessionData builderLoginSessionData = new BuilderLoginSessionData();

            session.setAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name(), builderLoginSessionData);

            // TODO : jwt 토큰 연계 작업해야함
//            log.info(branchSetting.toString());

            TokenData tokens = authService.authorize(branch_user_id, branch_user_powd);
            log.info(tokens.getAccessToken());
            log.info(tokens.getRefreshToken());
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.add(HttpHeaders.SET_COOKIE, "accessToken=" + tokens.getAccessToken() + "; Max-Age=43200; Path=/; Secure; HttpOnly"); // "; Max-Age:216000; Path=/; Secure; HttpOnly"
            tokenHeaders.add(HttpHeaders.SET_COOKIE, "refreshToken=" + tokens.getRefreshToken() + "; Max-Age=43200; Path=/; Secure; HttpOnly"); //  "; Max-Age:216000; Path=/; Secure; HttpOnly"

            obj.put("result_pswd_check","yes");
            return responseUtility.makeSuccessHeaderResponse(tokenHeaders, obj);

            }else {

                obj.put("result_pswd_check","no");
                return responseUtility.makeSuccessResponse(obj);
            }

        }catch (Exception e) {
            log.warn(e.getMessage(),e);
            obj.put("result_pswd_check","no");
            return responseUtility.makeSuccessResponse(obj);
        }


    }

}
