package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.manager.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class ServiceInquiryController {

    @Autowired
    EmailUtil emailUtil;

    @RequestMapping(value = "/manager/service/contactus", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map contactUs(@RequestBody Map<String, String> payload) {
        Map<String, String> Result = new HashMap<>();

        try {
            emailUtil.sendServiceContactUsEmail(payload);
        } catch (Exception e) {
            Result.put("result", "fail");
            return Result;
        }

        Result.put("result", "success");
        return Result;
    }
}
