package com.inswave.whive.headquater.controller;

import com.inswave.whive.common.domain.Domain;
import com.inswave.whive.common.domain.DomainService;
import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.member.MemberService;
import com.inswave.whive.headquater.SessionConstants;
import com.inswave.whive.headquater.enums.SessionKeyContents;
import com.inswave.whive.headquater.model.LoginSessionData;
import com.inswave.whive.headquater.util.ResponseUtility;
import com.inswave.whive.headquater.util.common.Common;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class DomainController {

    @Autowired
    DomainService domainService;

    @Autowired
    MemberService memberService;

    @Autowired
    Common common;

    @Autowired
    private ResponseUtility responseUtility;

    // domain 리스트 전체 조회 Controller
    @RequestMapping(value = "/manager/domain/search/domainList", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody
    List<Domain> getDomainAllList(){

        return domainService.findByDomainList();
    }

    @RequestMapping(value = "/manager/domain/search/domainListAll", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody List<Domain> getDomainAll(){
        return domainService.findAll();
    }

    @RequestMapping(value = "/manager/domain/search/domain/{domain_id}", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody Domain getDomainById(@PathVariable("domain_id") Long domain_id){
        return domainService.findByID(domain_id);
    }

    // domain 생성
    @RequestMapping(value = "/manager/domain/create", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Object> createDomain(@RequestBody Domain domain){
        domainService.createDomain(domain);

        return responseUtility.makeSuccessResponse();

    }

    // /manager/domain/update/
    @RequestMapping(value = "/manager/domain/update", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Object> createDomain(HttpServletRequest request, @RequestBody Map<String, Object> payload){
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            domainService.updateDomain(Long.valueOf(payload.get("domain_id").toString()), payload.get("domain_name").toString());

            return responseUtility.makeSuccessResponse();

        }else {
            return null;
        }

    }

    // domain 생성
    @RequestMapping(value = "/manager/domain/search/checkName/{domain_name}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Object> getDomainNameCheck(@PathVariable("domain_name") String domain_name){


        Domain domain = domainService.findByDomainName(domain_name);

        JSONObject obj = new JSONObject();
        if(domain != null){
            obj.put("domain_name_not_found","no");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            obj.put("domain_name_not_found","yes");
            return responseUtility.makeSuccessResponse(obj);

        }

    }

}
