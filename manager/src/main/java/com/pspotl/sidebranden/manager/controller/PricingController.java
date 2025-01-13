package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
import com.pspotl.sidebranden.manager.util.PortOneAPIUtil;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
public class PricingController {

    @Autowired
    PricingService pricingService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    Common common;

    @Autowired
    PortOneAPIUtil portOneAPIUtil;

    private JSONParser parser = new JSONParser();

    /**
     * wspay 에서 발급하는 토큰 값 가져 오기
     *
     * @param request
     * @return : /token/create/customer.pwkjson api 에서 바로 json Object 세팅 이후 바로 전달
     */
    @RequestMapping(value="/manager/pricing/create/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> pricingBrforeCreateToken(HttpServletRequest request){

        JSONObject jsonObject = portOneAPIUtil.getPricingToken("/token/create/customer.pwkjson");

        return responseUtility.makeSuccessResponse(jsonObject);

    }

    @RequestMapping(value="/manager/pricing/search/userinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> pricingAfterDataResult(HttpServletRequest request, @RequestBody Pricing pricing){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        pricing.setUser_id(memberLogin.getUser_id());
        pricing.setPay_change_yn("Y");

        pricingService.resultPricingInsert(pricing);

        return responseUtility.makeSuccessResponse();

    }

    /**
     * api /manager/pricing/cancel/id
     * 구독 취소 기능
     * @param request
     * @param payload
     * @return
     */
    @RequestMapping(value="/manager/pricing/cancel/id", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object>  pricingCancelBeForeFIndId(HttpServletRequest request, @RequestBody Map<String, Object> payload ){
        JSONObject reqJsonObj = new JSONObject();
        JSONObject reqJsonDetail = new JSONObject();
        JSONObject responeObj = new JSONObject();

        JSONObject reqJsonCancelObj = new JSONObject();
        JSONObject reqJsonCancelDetailObj = new JSONObject();

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        /**
         * whive 내 구독 정보를 저장한 테이블에 user id 기준으로 조회 한다.
         * 해당 정보 기준으로 구독 취소 기능을 수행한다.
         */
        Pricing pricing = pricingService.findById(memberLogin.getUser_id());

        pricing.setTeamId("WHive");
        String regNextDate = payload.get("regNextDate").toString();

        SimpleDateFormat reqNexFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        try {

            Date formatDate = reqNexFormat.parse(regNextDate.toString());
            calendar.setTime(formatDate);
            calendar.add(Calendar.DATE, 1);

            formatDate =   calendar.getTime();
            String resultNextDate = reqNexFormat.format(formatDate);

            reqJsonObj.put("teamId", pricing.getTeamId());
            reqJsonObj.put("userId",memberLogin.getEmail());
            reqJsonObj.put("from", payload.get("regDate"));
            reqJsonObj.put("to", resultNextDate.toString());
            reqJsonObj.put("status","scheduled");
            reqJsonDetail.put("elData", reqJsonObj);

            String uri = "/search/customer/schedule/all.pwkjson";

            /**
             * wspay 사이트에 token 값 세팅 한다.
             */
            JSONObject resultToken = portOneAPIUtil.getPricingToken("/token/create/customer.pwkjson");

            // call /search/customer/schedule/all.pwkjson api
            /**
             * /search/customer/schedule/all.pwkjson 호출 해서 구독 취소에 필요한 정보를 조회 한다.
             */
            JSONObject jsonObject = portOneAPIUtil.commonPortOneAPI(uri, reqJsonDetail, resultToken.get("Authorization").toString());

            JSONObject jsonObjectScheduleOneResponse = (JSONObject) parser.parse(jsonObject.get("response").toString());

            JSONArray jsonArray = (JSONArray) jsonObjectScheduleOneResponse.get("list");

            ArrayList arrayList = (ArrayList) jsonArray.get(0);
            JSONObject jsonResponseDetail = (JSONObject) arrayList.get(0);
            /**
             * 구독 취소에 필요한 정보를 세팅 한다.
             */
            reqJsonCancelDetailObj.put("teamId",pricing.getTeamId());
            reqJsonCancelDetailObj.put("orderId",jsonResponseDetail.get("merchant_uid").toString());
            reqJsonCancelDetailObj.put("customerUid",jsonResponseDetail.get("customer_uid".toString()));
            reqJsonCancelObj.put("elData", reqJsonCancelDetailObj);

            String cancelUri ="/payment/subscribe/cancel.pwkjson";
            /**
             * wspay 도메인으로 통해서 /payment/subscribe/cancel.pwkjson api 주소로 구독 취소를 수행한다.
             */
            JSONObject resultCancelObj =  portOneAPIUtil.commonPortOneAPI(cancelUri, reqJsonCancelObj, resultToken.get("Authorization").toString());
            log.info(resultCancelObj.toJSONString());
            /**
             * 구독 취소 여부가 있는데 code 가 0이면 성공이다.
             * 나머지 값은 실패다.
             */
            if(resultCancelObj.get("code").toString().equals("0")){
                pricing.setPay_change_yn("N");
                pricingService.resultPricingInsert(pricing);
                responeObj.put("code",resultCancelObj.get("code").toString());
            }else {
                responeObj.put("code",resultCancelObj.get("code").toString());
            }

        } catch (ParseException e) {
            log.warn(e.getMessage(),e);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }


        return responseUtility.makeSuccessResponse(responeObj);

    }


}
