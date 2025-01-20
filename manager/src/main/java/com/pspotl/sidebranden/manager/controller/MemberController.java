package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.*;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
import com.pspotl.sidebranden.common.workspace.Workspace;
import com.pspotl.sidebranden.common.workspace.WorkspaceService;
import com.pspotl.sidebranden.manager.SessionConstants;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionKeyContents;
import com.pspotl.sidebranden.manager.model.LoginSessionData;
import com.pspotl.sidebranden.manager.security.AuthService;
import com.pspotl.sidebranden.manager.security.jwt.TokenData;
import com.pspotl.sidebranden.manager.security.jwt.TokenProvider;
import com.pspotl.sidebranden.manager.util.PortOneAPIUtil;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    RestTemplateBuilder builder;

    private final AuthService authService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    Common common;

    @Autowired
    PricingService pricingService;

    @Autowired
    PortOneAPIUtil portOneAPIUtil;

    private final PasswordEncoder pwdEncoder;

    private JSONParser parser = new JSONParser();

    private static final String USER_LOGIN_ID = "user_login_id";
    private static final String user_name_not_found = "user_name_not_found";
    private static final String auth_check_result = "auth_check_result";

    public static final String SECRET_KEY = "6LccytwkAAAAAHjCcDWnYQjRwkbi7ejAC0FSZZlg"; // 6LccytwkAAAAAHjCcDWnYQjRwkbi7ejAC0FSZZlg  6LfroBohAAAAAE_JeeATeEF9NGFAWSHu3IM5sA00
    public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    public MemberController(AuthService authService, PasswordEncoder pwdEncoder) {
        this.authService = authService;
        this.pwdEncoder = pwdEncoder;
    }

    @RequestMapping(value = "/manager/member/login", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> userLoigin(HttpServletRequest request, @RequestBody MemberLogin memberLogin) {
        if( memberLogin.getUser_login_id() == null    || memberLogin.getUser_login_id().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }
        else if( memberLogin.getPassword() == null    || memberLogin.getPassword().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_PASSWORD.getMessage());
        }

        // rechatcha 연계
        /**
         * TODO : service <==> onpremise 타입에 따른 login 기능 전환 구현 해야함.
         */
        JSONObject secertKeyResultObj = null;
        JSONObject obj = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("secret", SECRET_KEY);
        map.add("response", memberLogin.getToken());

//        log.info(map.toString());

        HttpEntity<MultiValueMap<String, String>> requestSecret_Key = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = builder.build().postForEntity( SITE_VERIFY_URL, requestSecret_Key , String.class );
        log.info(String.valueOf(response));
        log.info(response.getBody());
        log.info(response.getStatusCode().toString());
        log.info(String.valueOf(response.getStatusCodeValue()));
        log.info(response.getHeaders().toString());

        try {

            secertKeyResultObj = (JSONObject) parser.parse(response.getBody());


            log.info(secertKeyResultObj.toJSONString());

//            if(response.getStatusCodeValue() == 200 && secertKeyResultObj.get("success").toString().equals("false")){
//                obj.put("recaptcha_yn",false);
//                return responseUtility.checkFailedResponse(obj);
//            }

//            if(response.getStatusCodeValue() == 200 && Float.valueOf(secertKeyResultObj.get("score").toString()) < 1.1 && secertKeyResultObj.get("success").toString().equals("true")){
//                 && (Float.valueOf(secertKeyResultObj.get("score").toString()) > 0.5 && Float.valueOf(secertKeyResultObj.get("score").toString()) < 1.1)
                MemberLogin memberLoginCheck = memberService.findByUserLoginID(memberLogin.getUser_login_id());

                if(memberLoginCheck == null){
                    throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID_OR_PASSWORD.getMessage());
                }

                if(memberLoginCheck.getUser_role().equals("NOUSER")){
                    throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_SECSSION.getMessage());
                }

                MemberLogin memberLoginResult = memberService.findByLoginEmailAndPassword(memberLogin);

                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(SessionConstants.TIME_OUT);
//
                LoginSessionData loginData = new LoginSessionData();
                loginData.setUserId(memberLoginResult.getUser_id());
                loginData.setUserRole(memberLoginResult.getUser_role());
                loginData.setDomainId(memberLoginResult.getDomain_id());
                loginData.setUserLoginId(memberLoginResult.getUser_login_id());
                loginData.setRoleId(memberLoginResult.getRole_id());

                session.setAttribute(SessionKeyContents.KEY_LOGIN_DATA.name(), loginData);
                obj.put("recaptcha_yn",true);

                // TODO : jwt 토큰 연계 작업해야함
                TokenData tokens = authService.authorize(memberLoginResult.getUser_login_id(), memberLogin.getPassword());
                log.info(tokens.getAccessToken());
                log.info(tokens.getRefreshToken());
                HttpHeaders tokenHeaders = new HttpHeaders();
                tokenHeaders.add(HttpHeaders.SET_COOKIE, "accessToken=" + tokens.getAccessToken() + "; Max-Age=43200; Path=/; HttpOnly; SameSite=Strict;"); // "; Max-Age:216000; Path=/; Secure; HttpOnly"
                tokenHeaders.add(HttpHeaders.SET_COOKIE, "refreshToken=" + tokens.getRefreshToken() + "; Max-Age=43200; Path=/; HttpOnly; SameSite=Strict;"); //  "; Max-Age:216000; Path=/; Secure; HttpOnly"

                obj.put("accessToken", tokens.getAccessToken());
                // login and password check service
                // return new RedirectView("/websquare/websquare.html?w2xPath=/index.xml");
                return responseUtility.makeSuccessHeaderResponse(tokenHeaders, obj);
//            }else if(response.getStatusCodeValue() == 200 && secertKeyResultObj.get("success").toString().equals("true") && (Float.valueOf(secertKeyResultObj.get("score").toString()) > 0.0 && Float.valueOf(secertKeyResultObj.get("score").toString()) < 5.0)){
////                && (Float.valueOf(secertKeyResultObj.get("score").toString()) > 0.0 && Float.valueOf(secertKeyResultObj.get("score").toString()) < 5.0)
////                log.info(secertKeyResultObj.toJSONString());
////                 return new RedirectView("/");
//                obj.put("recaptcha_yn",false);
//                return responseUtility.makeSuccessResponse(obj);
//            }else {
////                 return new RedirectView("/");
//                return responseUtility.checkFailedResponse();
////
////
//            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    @PostMapping("/manager/member/reIssue")
    ResponseEntity<Object> reIssue(@RequestBody TokenData tokenData) throws Exception {

        JSONObject obj = new JSONObject();
        TokenData newTokens = authService.reIssue(tokenData);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "accessToken=" + newTokens.getAccessToken() + "; Max-Age=43200; Path=/; Secure; HttpOnly");
        headers.add(HttpHeaders.SET_COOKIE, "refreshToken=" + newTokens.getRefreshToken() + "; Max-Age=43200; Path=/; Secure; HttpOnly");

        return responseUtility.makeSuccessHeaderResponse(headers, obj);
    }



    @RequestMapping(value = "/manager/member/qrcodeAuthCheckDetail", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> qrcodeUserAuthCheckDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> payload){

        log.info(payload.toString());

        JSONObject obj = new JSONObject();
        String user_login_id = payload.get(USER_LOGIN_ID).toString();
        String password = payload.get("password").toString();

        if(user_login_id == null || user_login_id.equals("")){
            obj.put(auth_check_result, false);
            return responseUtility.makeSuccessResponse(obj);
        }

        if(password == null || password.equals("")){
            obj.put(auth_check_result, false);
            return responseUtility.makeSuccessResponse(obj);
        }

        boolean authCheckResult = memberService.findByAuthCheckUserAndPassword(user_login_id, password);

        obj.put(auth_check_result, authCheckResult);
        return responseUtility.makeSuccessResponse(obj);
    }

    @RequestMapping(value="/manager/member/search/userInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody MemberLogin userLoginGetJson(HttpServletRequest request){
        MemberLogin memberLogin;

        /**
         * 토큰 값을 복호화 해서 user name 값 리턴 하는 메소드
         *
         * @Methoid : common.getTokenToRealName
         *  @Param HttpServletRequest request
         * return realName
         *
         */
        String userId = common.getTokenToRealName(request);

        memberLogin = memberService.findByUserLoginID(userId);

        Pricing pricing = pricingService.findById(memberLogin.getUser_id());

        if(pricing != null ){
            memberLogin.setPay_change_yn(pricing.getPay_change_yn());;
        }else {
            memberLogin.setPay_change_yn("N");
        }

        return memberLogin;
    }

    @RequestMapping(value="/manager/member/search/loginSessionCheck", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> userLoginSessionCheck(HttpServletRequest request){
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            return null;
        }else {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
        }
    }

    @RequestMapping(value = "/manager/member/logout", method = RequestMethod.GET)
    public ResponseEntity<Object> userLogout(HttpServletRequest request){
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        boolean tokenExpiredYn = common.getExpired(request);
        log.info("tokenExpiredYn {}", tokenExpiredYn);

        memberService.updateLogout(memberLogin.getUser_id());

        return responseUtility.makeSuccessResponse();
    }


    @RequestMapping(value = "/api/members", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Member> findAll() {
        return memberService.findAll();
    }

    // MemberLogin
    @RequestMapping(value = "/manager/member/search/userListAll", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<MemberLogin> MemberLogin() {
        return memberService.findByAll();
    }

    @GetMapping(value = "/manager/member/search/userListDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody MemberDetail MemberLoginDetail(HttpServletRequest request){

        JSONObject reqJsonObj = new JSONObject();
        JSONObject reqJsonDetail = new JSONObject();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            MemberDetail memberDetailResult = memberService.findByDetailAndPricing(memberLogin.getUser_id());


            //
            /**
             * TODO 결제서비스 통해서 처리하는 기능 추가하기
             ** 그리고 결제 내역 조회는 가능하니 아래 api 로 처리 하기
             */
            if(memberDetailResult.getPricing() == null){

            }else {

                JSONObject resultToken = portOneAPIUtil.getPricingToken("/token/create/customer.pwkjson");
                memberDetailResult.getPricing().getImp_uid();
                Pricing pricing = memberDetailResult.getPricing();
                pricing.setTeamId("WHive");
                reqJsonObj.put("userId",memberLogin.getEmail());
                reqJsonObj.put("teamId", pricing.getTeamId());
                reqJsonDetail.put("elData", reqJsonObj);

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Proworks-Body","Y");
                headers.set("Proworks-Lang","ko");
                headers.set("AuthInfo","WHive");
                headers.set("Authorization", resultToken.get("Authorization").toString());

                log.info(reqJsonDetail.toJSONString());

                HttpEntity<Object> httpEntity = new HttpEntity<>( reqJsonDetail.toJSONString(), headers);
                // /search/customer/payment/now.pwkjson로    변경 하기 /search/customer/payment/all.pwkjson
                String url = "https://pay.inswave.com" +"/search/customer/payment/now.pwkjson";

                String response = restTemplate.postForObject(url,httpEntity, String.class );
                log.info(response.toString());

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) parser.parse(response.toString());
                    if(jsonObject != null){

                        ArrayList<Object> list = new ArrayList<>();
                        JSONObject jsonElHeader = (JSONObject) jsonObject.get("elHeader");
                        if(jsonElHeader.get("resSuc").toString().equals("true")){
                            list = (ArrayList<Object>) jsonObject.get("response");
                            if(list.size() == 0){

                            }else {
                                log.info(list.get(list.size()-1).toString());
                                JSONObject nowpaymentObj = (JSONObject) parser.parse(list.get(list.size()-1).toString());
                                pricing.setPricingObj((JSONObject) nowpaymentObj.get("now"));
                                memberDetailResult.setPricing(pricing);
                            }
                        }

                    }


                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }



            return memberDetailResult;
        }else {
            return null;
        }

    }

    // /manager/member/appID/Update
    @RequestMapping(value = "/manager/member/appID/Update", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> MemberAppIDUpdate(HttpServletRequest request, @RequestBody Map<String, Object> payload){
        JSONObject obj = new JSONObject();
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);
        if(memberLogin != null){
            JSONObject appIDJson = new JSONObject();
            // 3. db update
            appIDJson.put("androidAppID1",payload.get("androidAppID1").toString());
            appIDJson.put("androidAppID2",payload.get("androidAppID2").toString());
            appIDJson.put("iOSAppID1",payload.get("iOSAppID1").toString());
            appIDJson.put("iOSAppID2",payload.get("iOSAppID2").toString());

            memberService.userDetailAppIDUpdate(memberLogin.getUser_id(), appIDJson.toJSONString());  // TODO 새로운 update 쿼리 호출 메소드 기능 추가하기
            obj.put("appid_update_ok","yes");
        }else{
            obj.put("appid_update_ok","no");
        }

        return responseUtility.makeSuccessResponse(obj);

    }

    @GetMapping(value = "/manager/member/search/userOneDetail/{user_id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody MemberDetail MemberFindOneDetail(HttpServletRequest request, @PathVariable("user_id") Long user_id){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            return memberService.findByIdDetail(user_id);
        }else {
            return null;
        }

    }

    @RequestMapping(value = "/manager/member/create", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> MemberCreate(@RequestBody MemberUserCreate memberUserCreate){

        // 1. validation check...
        if( memberUserCreate.getUser_login_id() == null    || memberUserCreate.getUser_login_id().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }
        else if( memberUserCreate.getUser_name() == null    || memberUserCreate.getUser_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_NAME.getMessage());
        }

        // 3. db insert
        memberService.createHiveUser(memberUserCreate);
        MemberLogin  memberLogin =  memberService.findByUserLoginID(memberUserCreate.getUser_login_id());
        Workspace workspace = new Workspace();
        workspace.setMember_id(memberLogin.getUser_id());
        workspace.setWorkspace_name("Workspace_Sample_"+memberLogin.getUser_login_id());
        workspace.setStatus("1");
        workspace.setFavorite_flag("Y");
        workspace.setDelete_yn("0");

        // 4. sample workspace 생성
        workspaceService.insert(workspace);

        return responseUtility.makeSuccessResponse();

    }

    @RequestMapping(value = "/manager/member/update", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> MemberUpdate(@RequestBody MemberUserCreate memberUserCreate){

        // 1. validation check...
        if( memberUserCreate.getUser_login_id() == null || memberUserCreate.getUser_login_id().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }
        else if( memberUserCreate.getUser_name() == null || memberUserCreate.getUser_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_NAME.getMessage());
        }

        // 3. db update
        memberService.updateHiveUser(memberUserCreate);

        return responseUtility.makeSuccessResponse();

    }

    @RequestMapping(value = "/manager/member/search/checkEmail", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> MemberIdCheck(@RequestBody Map<String, Object> payload){

        // 1. validation check...
        if( payload.get(PayloadKeyType.email.name()).toString() == null) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }

        // 3. db id check / findByEmail
        Member member = memberService.findByEmail(payload.get(PayloadKeyType.email.name()).toString());
        JSONObject obj = new JSONObject();
        if(member != null){
            obj.put("user_email_not_found" ,"no");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            obj.put("user_email_not_found" ,"yes");
            return responseUtility.makeSuccessResponse(obj);


        }



    }

    @RequestMapping(value = "/manager/member/search/checkUserId", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> UserLoginIdCheck(@RequestBody Map<String, Object> payload){

        // 1. validation check...
        if( payload.get(USER_LOGIN_ID).toString() == null    || payload.get(USER_LOGIN_ID).toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }

        // 3. db id check / findByEmail
        MemberLogin memberLogin = memberService.findByUserLoginID(payload.get(USER_LOGIN_ID).toString());
        JSONObject obj = new JSONObject();
        if(memberLogin != null){
            obj.put(user_name_not_found,"no");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            obj.put(user_name_not_found,"yes");
            return responseUtility.makeSuccessResponse(obj);


        }



    }

    // /manager/member/search/userInfoForSelectBox/
    @RequestMapping(value = "/manager/member/search/userInfoForSelectBox/{domainID}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<MemberAdminList> memberAdminAndDomainList(@PathVariable("domainID") Long domainID){

        // 1. validation check...
        if( domainID == null || domainID.toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }

        // 3. db id check / findByEmail
        List<MemberAdminList> memberAdminList = memberService.findByAdminList(domainID);

        if(memberAdminList != null){
            return memberAdminList;
        }else {
            return null;
        }

    }

    @GetMapping(value = "/api/member/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Member findById(@PathVariable("id") Long id) {
        return memberService.findById(id);
    }

    @RequestMapping(value = "/manager/member/update/userAuthToken", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateConfirmPassword(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if (memberLogin.getUser_role().equals("ADMIN") || memberLogin.getUser_role().equals("SUPERADMIN")) {
            memberLogin = memberService.findByUserLoginID(payload.get("user_login_id").toString());
        }

        if(memberLogin != null){
            payload.put("user_id",memberLogin.getUser_id());
            memberService.updateConfirmPassword(payload);
            return responseUtility.makeSuccessResponse();
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/manager/member/loginCheck", method = RequestMethod.POST)
    public ResponseEntity<Object> loginCheck(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        JSONObject obj = new JSONObject();

        try {
            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);

            if (memberLogin != null) {
                obj.put("is_login", "yes");
                return responseUtility.makeSuccessResponse(obj);
            } else {
                obj.put("is_login", "no");
                return responseUtility.makeSuccessResponse(obj);
            }
        } catch (Exception e) {
            obj.put("is_login", "no");
            return responseUtility.makeSuccessResponse(obj);
        }
    }

    @RequestMapping(value = "/manager/member/payCheck", method = RequestMethod.POST)
    public ResponseEntity<Object> payCheck(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        JSONObject obj = new JSONObject();

        try {
            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);
            int isPayedCheck = memberService.payCheck(memberLogin.getUser_id());

            if (isPayedCheck > 0) {
                obj.put("is_payed", "yes");
            } else {
                obj.put("is_payed", "no");
            }

            return responseUtility.makeSuccessResponse(obj);
        } catch (Exception e) {
            obj.put("is_payed", "no");
            return responseUtility.makeSuccessResponse(obj);
        }
    }
}
