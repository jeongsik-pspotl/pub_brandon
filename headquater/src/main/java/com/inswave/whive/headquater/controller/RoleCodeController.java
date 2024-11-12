package com.inswave.whive.headquater.controller;

import com.inswave.whive.common.rolecode.RoleCode;
import com.inswave.whive.common.rolecode.RoleCodeSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class RoleCodeController {

    @Autowired
    private RoleCodeSerivce roleCodeSerivce;

    @RequestMapping(value = "/manager/role/search/codeList/{codetype}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<RoleCode> getRoleCodeList(@PathVariable("codetype") String codeType){


        if(codeType == null){
            return null;
        }

        return roleCodeSerivce.findByIdDetail(codeType);

    }

}
