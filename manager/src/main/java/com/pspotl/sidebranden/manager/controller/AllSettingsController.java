package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.settings.AllBranchSettings;
import com.pspotl.sidebranden.common.settings.AllBranchSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class AllSettingsController {

    @Autowired
    AllBranchSettingsService allBranchSettingsService;

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    @RequestMapping(value = "/api/buildhistory/{build_id}", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody
    AllBranchSettings findById(@PathVariable("build_id") String build_id){
        return allBranchSettingsService.findById(build_id);
    }
}
