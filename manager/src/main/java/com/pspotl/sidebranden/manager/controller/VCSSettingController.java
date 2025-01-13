package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.MemberDetail;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
import com.pspotl.sidebranden.common.vcssetting.VCSSelectBoxList;
import com.pspotl.sidebranden.common.vcssetting.VCSSetting;
import com.pspotl.sidebranden.common.vcssetting.VCSSettingService;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class VCSSettingController {

    @Autowired
    VCSSettingService vcsSettingService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private  PricingService pricingService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    Common common;

    @Value("${spring.profiles}")
    private String springProfile;

    @RequestMapping(value = "/manager/vcs/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createVCSSetting(HttpServletRequest request, @RequestBody VCSSetting vcsSetting) {
        MemberDetail memberDetail;
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());
            vcsSetting.setAdmin_id(memberDetail.getUser_id());
        }else {
            return null;
        }

        if( vcsSetting.getVcs_name() == null  || vcsSetting.getVcs_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        vcsSettingService.insert(vcsSetting);

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/vcs/update", method = RequestMethod.POST)
    public ResponseEntity<Object> updateVCSSetting(HttpServletRequest request, @RequestBody VCSSetting vcsSetting) {
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());
            vcsSetting.setAdmin_id(memberDetail.getUser_id());
        }else {
            return null;
        }

        if( vcsSetting.getVcs_user_id() == null  || vcsSetting.getVcs_user_id().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        if( vcsSetting.getVcs_user_pwd() == null  || vcsSetting.getVcs_user_pwd().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        vcsSettingService.update(vcsSetting);

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/vcs/search/profileListAll", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<VCSSetting> findAll(HttpServletRequest request) {
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());

        }else {
            return null;
        }

        return vcsSettingService.findAll(memberDetail.getUser_role(),memberDetail.getUser_id());
    }

    @RequestMapping(value = "/manager/vcs/search/profile/{vcs_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    VCSSetting findByID(@PathVariable("vcs_id") Long vcs_id) {
        return vcsSettingService.findbyID(vcs_id);
    }

    // vcs list select box 조회 controller
    @RequestMapping(value = "/manager/vcs/search/profileListAllBySelectBox", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<VCSSelectBoxList> findByID() {
        return vcsSettingService.finfindBySelectList();
    }

    // findBySelectListVcsID
    @RequestMapping(value = "/manager/vcs/search/profileIdBySelectBox/{vcs_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody VCSSelectBoxList findBySelectListVcsID(@PathVariable("vcs_id") Long vcs_id) {
        return vcsSettingService.findBySelectListVcsID(vcs_id);
    }

    @RequestMapping(value = "/manager/vcs/search/profileTypeBySelectBox/{vcs_type}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<VCSSelectBoxList> findBySelectListVCSType(@PathVariable("vcs_type") String vcs_type){
        return vcsSettingService.findBySelectListType(vcs_type);
    }

    // vcs list select box 조회 controller
    @RequestMapping(value = "/manager/vcs/search/profileAdminBySelectBox", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<VCSSelectBoxList> findBySelectListAdminID(HttpServletRequest request) {
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());
            return vcsSettingService.findBySelectListAdminID(memberDetail.getUser_id());
        }else {
            return null;
        }

    }

    @RequestMapping(value = "/manager/vcs/search/profileTypeAndAdminBySelectBox/{vcs_type}/{sample_yn}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<VCSSelectBoxList> findBySelectListAdminVCSType(HttpServletRequest request, @PathVariable("vcs_type") String vcs_type, @PathVariable("sample_yn") String sample_yn){
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());

            if(springProfile.equals("onpremiss")){
                if(memberDetail.getUser_role().equals("ADMIN") && sample_yn.equals("N")){
                    return vcsSettingService.findBySelectListAdminIdAndVcsType(memberDetail.getUser_id(), vcs_type);
                }else if(sample_yn.equals("Y")){
                    return vcsSettingService.findBySelectListAdminIdAndVcsType(1L, vcs_type);
                }

            }else {
                Pricing pricing =  pricingService.findById(memberDetail.getUser_id());
                if(pricing != null){
                    memberDetail.setPay_change_yn(pricing.getPay_change_yn());
                }else {
                    memberDetail.setPay_change_yn("N");
                }

                if(memberDetail.getPay_change_yn().equals("Y")){
                    return vcsSettingService.findBySelectListAdminIdAndVcsType(memberDetail.getUser_id(), vcs_type);
                }else {
                    return vcsSettingService.findBySelectListAdminIdAndVcsType(1L, vcs_type);
                }

            }

        }

        return null;

    }


    @RequestMapping(value = "/manager/vcs/search/checkProfileName", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Object> findVCSSettingName(@RequestBody Map<String, Object> payload) {
        if( payload.get(PayloadKeyType.vcs_name.name()).toString() == null  || payload.get(PayloadKeyType.vcs_name.name()).toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        JSONObject obj = new JSONObject();
        VCSSetting vcsSetting = vcsSettingService.findByVCSName(payload.get(PayloadKeyType.vcs_name.name()).toString());

        if(vcsSetting != null){
            obj.put("vcs_name_not_found","yes");
            return responseUtility.makeSuccessResponse(obj);
        }else {
            obj.put("vcs_name_not_found","no");
            return responseUtility.makeSuccessResponse(obj);
        }

    }

}
