package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.ftpsetting.FTPSelectBoxList;
import com.pspotl.sidebranden.common.ftpsetting.FTPSetting;
import com.pspotl.sidebranden.common.ftpsetting.FTPSettingService;
import com.pspotl.sidebranden.common.member.MemberDetail;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class FTPSettingsController {

    @Autowired
    FTPSettingService ftpSettingService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    Common common;

    @RequestMapping(value = "/manager/ftp/setting/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createBuildProjectAll(HttpServletRequest request, @RequestBody FTPSetting ftpSetting) {

        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());

        }else {
            return null;
        }

        if( ftpSetting.getFtp_name() == null  || ftpSetting.getFtp_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        ftpSettingService.insert(ftpSetting);

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/ftp/setting/update", method = RequestMethod.POST)
    public ResponseEntity<Object> updateFTFSettingData(@RequestBody FTPSetting ftpSetting) {
        if( ftpSetting.getFtp_name() == null  || ftpSetting.getFtp_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        ftpSettingService.update(ftpSetting);

        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/ftp/setting/searchAll", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<FTPSetting> findAll() {
        return ftpSettingService.findAll();
    }

    @RequestMapping(value = "/manager/ftp/setting/search/{ftp_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    FTPSetting findByID(@PathVariable("ftp_id") Long ftp_id) {
        return ftpSettingService.findbyID(ftp_id);
    }

    @RequestMapping(value = "/manager/ftp/setting/search/checkName", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody FTPSetting findByName(HttpServletRequest request, @RequestBody Map<String, Object> payload){
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());

        }else {
            return null;
        }

        return ftpSettingService.findByName(payload.get("ftp_name").toString());
    }

    // findBySelectList
    @RequestMapping(value = "/manager/ftp/setting/search/selectBoxListAll", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<FTPSelectBoxList> findBySelectList() {
        return ftpSettingService.findBySelectList();
    }


    @RequestMapping(value = "/manager/ftp/setting/search/selectBoxList/{ftp_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    FTPSelectBoxList findBySelectList(@PathVariable("ftp_id") Long ftp_id) {
        return ftpSettingService.findBySelectListFTPID(ftp_id);
    }
}
