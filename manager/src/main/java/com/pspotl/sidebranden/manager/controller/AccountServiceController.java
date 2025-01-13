package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.inswave.whive.common.member.*;
import com.pspotl.sidebranden.common.member.*;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.usercode.UserKeyCode;
import com.pspotl.sidebranden.common.usercode.UserKeyCodeService;
import com.pspotl.sidebranden.common.workspace.Workspace;
import com.pspotl.sidebranden.common.workspace.WorkspaceService;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.util.EmailUtil;
import com.pspotl.sidebranden.manager.util.PortOneAPIUtil;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/***
    W-Hive 서비스
    회원가입 및 비밀번호 변경, 탈퇴 관련 메일 발송 기능 Controller
 ***/
@Slf4j
@RestController
public class AccountServiceController {

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    MemberService memberService;

    @Autowired
    UserKeyCodeService userKeyCodeService;

    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    Common common;

    @Autowired
    PortOneAPIUtil portOneAPIUtil;

    private JSONParser parser = new JSONParser();

    @Autowired
    private ResponseUtility responseUtility;

    private String userNotFoundStr = "user_name_not_found";

    private String userEmailNotFound= "user_email_not_found";
    private String userCheckPassword = "checkPassword";

    // test time calculate
    @RequestMapping(value = "/api/account/signUp/checkEmail", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> signupCheckEmail() {

        // test send email
        // 회원가입 완료 이후 인증 메일 보내기
        emailUtil.sendEmail();


        return responseUtility.makeSuccessResponse();
    }

    // 회원가입 화면에서 인증코드 요청 보내는 api
    @RequestMapping(value = "/manager/account/signUp/readyEmail", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> signUpReadyEmail(@RequestBody MemberLogin member) {
        // 1. validation check...
        if(member.getEmail() == null || member.getEmail().equals("")) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }

        // 3. db id check / findByEmail
        // DB체크
        Member memberCheck = memberService.findByEmail(member.getEmail());
        JSONObject obj = new JSONObject();
        if(memberCheck != null){
            obj.put(userEmailNotFound,"no");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            // 존재하지 않는 email이면 인증코드 전송하기

            String codeStr = emailUtil.sendCodeDataToEmail(member);
            if(codeStr.equals("")){
                obj.put(userEmailNotFound,"notsend");
            }else {
                obj.put(userEmailNotFound,"yes");
            }

            // 체크 완료 이후
            // user_key_code 테이블에 insert
            // 13일 즈음에 작업
            UserKeyCode userKeyCode = new UserKeyCode();
            userKeyCode.setUser_key_email(member.getEmail());
            userKeyCode.setUser_key_code_value(codeStr);

            userKeyCodeService.userCodeCreate(userKeyCode);

            return responseUtility.makeSuccessResponse(obj);


        }



    }

    // 회원가입 인증 코드 값 체크
    @RequestMapping(value = "/manager/account/signUp/checkCodeValue", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> signUpCheckCodeValue(@RequestBody Map<String, Object> payload){

        JSONObject obj = new JSONObject();
        UserKeyCode userKeyCode = new UserKeyCode();
        userKeyCode.setUser_key_email(payload.get(PayloadKeyType.email.name()).toString());
        userKeyCode.setUser_key_code_value(payload.get("keyCode").toString());

        log.info(userKeyCode.toString());
        UserKeyCode userKeyCodeResult = userKeyCodeService.userCodeCheck(userKeyCode);

        if(userKeyCodeResult == null){
            obj.put("checkResult","fail");

        }else {
            obj.put("checkResult","success");

        }


        return responseUtility.makeSuccessResponse(obj);

    }

    // 회원가입 완료
    // 회원가입 완료 url 보내기
    @RequestMapping(value = "/manager/account/signUp/resultEmail", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> signUpResultEmail(@RequestBody MemberUserCreate memberUserCreate) {

        JSONObject jsonObject = new JSONObject();

        // parameter 검증

        MemberLogin memberLoginPhoneNumber = memberService.userPhoneNumberCheck(memberUserCreate.getPhone_number());

        if(memberLoginPhoneNumber == null){

        }else {
            jsonObject.put("checkPhoneNumber","fail_phone");
            return responseUtility.makeSuccessResponse(jsonObject);
        }

        // 완료 이후 DB insert 적용
        memberUserCreate.setUser_role("ADMIN"); // 기본 admin
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

        // 메일 발송
        emailUtil.sendSignResultEmailToLoginPage(memberUserCreate);

        return responseUtility.makeSuccessResponse();
    }

    // 비밀번호 재설정
    // 인증 완료 기능 체크
    // 인증완료 url 받기
    @RequestMapping(value = "/manager/account/signUp/resetPassword", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> userPasswordReset(@RequestBody Map<String, Object> payload) {

        MemberUserCreate memberUserCreate = new MemberUserCreate();
        Map<String, Object> payloadMap = new HashMap<>();
        JSONObject obj = new JSONObject();

        MemberLogin memberLoginCheck = memberService.findByUserLoginID(payload.get("user_login_id").toString());

        if(memberLoginCheck == null){
            obj.put(userCheckPassword,"fail_id");
            return responseUtility.makeSuccessResponse(obj);
        }

        memberUserCreate.setEmail(payload.get(PayloadKeyType.email.name()).toString());

        MemberLogin member = memberService.findByEmailDetail(payload.get(PayloadKeyType.email.name()).toString());

        if(member == null){
            obj.put(userCheckPassword,"fail_email");


            return responseUtility.makeSuccessResponse(obj);
        }else {
            // 비밀번호 재설정
            String uuid =  memberService.updateUserPasswordReset(memberUserCreate);

            payloadMap.put(PayloadKeyType.email.name(), payload.get(PayloadKeyType.email.name()).toString());
            payloadMap.put("username", member.getUser_name());
            payloadMap.put("uuid", uuid);

            // 비밀번호 url 받기
            emailUtil.sendPasswordResetToEmail(payloadMap);

            obj.put(userCheckPassword,"success_email");

            return responseUtility.makeSuccessResponse(obj);
        }

    }

    // 아이디 찾기 기능
    @RequestMapping(value = "/manager/account/signUp/checkUserId", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> userIdCheck(@RequestBody Map<String, Object> payload) {

        Map<String, Object> payloadMap = new HashMap<>();
        JSONObject obj = new JSONObject();
        // 아이디 찾기 기능
        MemberLogin member = memberService.findByEmailDetail(payload.get(PayloadKeyType.email.name()).toString());

        if(member == null){
            obj.put("checkUserID","fail_email");

            return responseUtility.makeSuccessResponse(obj);
        }else {

            payloadMap.put(PayloadKeyType.email.name(), payload.get(PayloadKeyType.email.name()).toString());
            payloadMap.put("username", member.getUser_name());
            payloadMap.put("user_login_id",member.getUser_login_id());

            // 비밀번호 url 받기
            emailUtil.sendIDCheckToEmail(payloadMap);

            obj.put("checkUserID","success_email");

            return responseUtility.makeSuccessResponse(obj);
        }

    }

    // 탈퇴기능
    @RequestMapping(value = "/manager/account/resign/sendEmail", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> userSecession(@RequestBody Map<String, Object> payload) {

        // 인증 완료 기능 체크
        // 인증완료 url 받기
        Member memberCheck = memberService.findByEmail(payload.get(PayloadKeyType.email.name()).toString());
        JSONObject obj = new JSONObject();
        if(memberCheck == null){
            obj.put(userNotFoundStr,"no");
            return responseUtility.makeSuccessResponse(obj);

        }else {

            // 체크 완료 이후
            emailUtil.sendUserSecessionToEmail(payload);
            obj.put(userNotFoundStr,"yes");

            return responseUtility.makeSuccessResponse(obj);


        }
    }

    // 탈퇴 화면 이동 기능
    @GetMapping("/manager/account/resign/sendPageUrl")
    public RedirectView userSendSecessionURL(HttpServletRequest request, HttpServletResponse response){
        String paramsEmailStr = request.getParameter(PayloadKeyType.email.name());

        return new RedirectView("/websquare/websquare.html?w2xPath=/secssion.xml&email="+paramsEmailStr);

    }

    /**
     * 탈퇴 기능 수행시 구독 여부에 따라서 구독 취소가 동일하게 이루어진다.
     * @param request
     * @param payload
     * @return
     */
    // 탈퇴 완료 기능
    @RequestMapping(value = "/manager/account/resign/result", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> userSecessionResult(HttpServletRequest request, @RequestBody Map<String, Object> payload) {

        JSONObject reqJsonObj = new JSONObject();
        JSONObject reqJsonDetail = new JSONObject();
        JSONObject responeObj = new JSONObject();

        JSONObject reqJsonCancelObj = new JSONObject();
        JSONObject reqJsonCancelDetailObj = new JSONObject();

        // 인증 완료 기능 체크
        // 인증완료 url 받기
        Member memberCheck = memberService.findByEmail(payload.get(PayloadKeyType.email.name()).toString());
        JSONObject obj = new JSONObject();
        if(memberCheck == null){
            obj.put("secssionResult","fail");
            return responseUtility.makeSuccessResponse(obj);

        }else {

            MemberDetail memberDetailResult = memberService.findByEmailDetailAndPricing(payload.get(PayloadKeyType.email.name()).toString());

            /**
             * TODO 결제서비스 통해서 처리하는 기능 추가하기
             ** 그리고 결제 내역 조회는 가능하니 아래 api 로 처리 하기
             */
            if(memberDetailResult.getPricing() == null){
                memberService.updateMemberSecssionYn(payload.get(PayloadKeyType.email.name()).toString());
            }else {
                // TODO 탈퇴 이후 유료 계정일 경우 다음 결제 예정 취소 기능 구현
                JSONObject resultToken = portOneAPIUtil.getPricingToken("/token/create/customer.pwkjson");
                memberDetailResult.getPricing().getImp_uid();
                Pricing pricing = memberDetailResult.getPricing();
                pricing.setTeamId("WHive");
                reqJsonObj.put("userId",memberDetailResult.getEmail());
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
                    log.info(jsonObject.toJSONString());
                    log.info(jsonObject.get("response").toString());
                    ArrayList<Object> list = new ArrayList<>();
                    list = (ArrayList<Object>) jsonObject.get("response");
                    log.info(String.valueOf(list.size()));
                    log.info(list.get(list.size()-1).toString());
                    JSONObject nowpaymentObj = (JSONObject) parser.parse(list.get(list.size()-1).toString());
                    pricing.setPricingObj((JSONObject) nowpaymentObj.get("now"));
                    memberDetailResult.setPricing(pricing);


                    //TODO data.pricing.pay_change_yn  분기 처리 기능 구현
                    JSONObject pricingObj =  memberDetailResult.getPricing().getPricingObj();


                    /**
                     * pay change yn 이 N 일 경우 바로 탈퇴 회원으로 전환
                     * Y일 경우 구독 취소 이후 탈퇴 회원으로 전환
                     */
                    if(memberDetailResult.getPricing().getPay_change_yn().equals("N")){
                        memberService.updateMemberSecssionYn(payload.get(PayloadKeyType.email.name()).toString());
                    }else if(memberDetailResult.getPricing().getPay_change_yn().equals("Y")){
                        // TODO 탈퇴 시 구독 취소 전환 이후 sql 수정
                        SimpleDateFormat reqNexFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();

                        Date formatDate = reqNexFormat.parse(pricingObj.get("regNextDate").toString());
                        calendar.setTime(formatDate);
                        calendar.add(Calendar.DATE, 1);

                        formatDate =   calendar.getTime();
                        String resultNextDate = reqNexFormat.format(formatDate);

                        reqJsonObj.put("teamId", pricing.getTeamId());
                        reqJsonObj.put("userId",memberDetailResult.getEmail());
                        reqJsonObj.put("from", pricingObj.get("regDate").toString());
                        reqJsonObj.put("to", resultNextDate.toString());
                        reqJsonObj.put("status","scheduled");
                        reqJsonDetail.put("elData", reqJsonObj);

                        String uri = "/search/customer/schedule/all.pwkjson";

                        JSONObject jsonObjectAuth = portOneAPIUtil.commonPortOneAPI(uri, reqJsonDetail, resultToken.get("Authorization").toString());

                        JSONObject jsonObjectScheduleOneResponse = (JSONObject) parser.parse(jsonObjectAuth.get("response").toString());

                        JSONArray jsonArray = (JSONArray) jsonObjectScheduleOneResponse.get("list");

                        ArrayList arrayList = (ArrayList) jsonArray.get(0);
                        JSONObject jsonResponseDetail = (JSONObject) arrayList.get(0);

                        reqJsonCancelDetailObj.put("teamId",pricing.getTeamId());
                        reqJsonCancelDetailObj.put("orderId",jsonResponseDetail.get("merchant_uid").toString());
                        reqJsonCancelDetailObj.put("customerUid",jsonResponseDetail.get("customer_uid".toString()));
                        reqJsonCancelObj.put("elData", reqJsonCancelDetailObj);

                        String cancelUri ="/payment/subscribe/cancel.pwkjson";

                        JSONObject resultCancelObj =  portOneAPIUtil.commonPortOneAPI(cancelUri, reqJsonCancelObj, resultToken.get("Authorization").toString());
                        log.info(resultCancelObj.toJSONString());
                        emailUtil.sendUserSecessionResultToEmail(payload);
                    }


                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (java.text.ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            obj.put("secssionResult","success");

            return responseUtility.makeSuccessResponse(obj);


        }
    }

    public static final String SECRET_KEY = "6LccytwkAAAAAHjCcDWnYQjRwkbi7ejAC0FSZZlg";
    public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    RestTemplateBuilder builder;

    @RequestMapping(value = "/manager/account/resign/validation", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Object ajax(@RequestBody Map<String, Object> payload){
        JSONObject secretKeyObj = new JSONObject();
        JSONParser parser = new JSONParser();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        log.info(payload.get("token").toString());
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("secret", SECRET_KEY);
        map.add("response", payload.get("token").toString());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = builder.build().postForEntity( SITE_VERIFY_URL, request , String.class );

        log.info(String.valueOf(response));
        log.info(response.getBody());
        log.info(response.getStatusCode().toString());
        log.info(String.valueOf(response.getStatusCodeValue()));

        try {
            secretKeyObj = (JSONObject) parser.parse(response.getBody().toString());

            if(response.getStatusCodeValue() == 200 && secretKeyObj.get("success").toString().equals("true")){
                return secretKeyObj;
            }else if(response.getStatusCodeValue() == 200 && secretKeyObj.get("success").toString().equals("false")){
                return secretKeyObj;
            }else {
                return secretKeyObj;
            }



        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    // 아이디 찾기 기능
    @RequestMapping(value = "/manager/account/resign/userPhoneNumberCheck", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> userPhoneNumberCheck(@RequestBody Map<String, Object> payload) {

        JSONObject obj = new JSONObject();
        // 아이디 찾기 기능
        MemberLogin member = memberService.userPhoneNumberCheck(payload.get(PayloadKeyType.phone_number.name()).toString());

        if(member == null){
            obj.put("checkPhoneNumber","success_phone");

            return responseUtility.makeSuccessResponse(obj);
        }else {
            obj.put("checkPhoneNumber","fail_phone");

            return responseUtility.makeSuccessResponse(obj);
        }

    }

    public static String convertMapToURLParameters(Map<String, String[]> paramMap) throws UnsupportedEncodingException {

        String parameterString = "";

        for(Map.Entry<String, String[]> mapEntry : paramMap.entrySet()){

            for(String value : mapEntry.getValue()){
                parameterString += (parameterString.isEmpty() ? "?" : "&") + mapEntry.getKey() + "=" + URLEncoder.encode(value,"UTF-8");
            }

        }

        return parameterString;
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {  String [] p=param.split("=");
            String name = p[0];
            if(p.length>1)  {String value = p[1];
                map.put(name, value);
            }
        }
        return map;
    }



}
