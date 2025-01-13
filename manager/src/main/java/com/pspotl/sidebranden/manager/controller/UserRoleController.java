package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.inswave.whive.common.role.*;
import com.pspotl.sidebranden.common.role.*;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class UserRoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    private ResponseUtility responseUtility;

    @RequestMapping(value = "/manager/userRole/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUserRole(@RequestBody Role role) {

        Role roleTemp;
        roleTemp = role;

        //
        log.info("role data check : {}", roleTemp.toString());


        // 1. role 테이블 insert
        // 2. workspace, key group insert
        roleService.insert(role);


        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/userRole/update", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUserRole(@RequestBody Role role) {

        Role roleTemp;
        roleTemp = role;

        log.info("role data check : {}", roleTemp.toString());

        roleService.update(role);


        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value="/manager/userRole/search/profileListAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Role> findUserRoleAll(){

        return roleService.findAll();
    }

    @RequestMapping(value="/manager/userRole/search/roleId/{role_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Role findByRoleID(@PathVariable("role_id") int role_id){
            return roleService.findByRoleID(role_id);

    }

    @RequestMapping(value="/manager/userRole/search/workspaceGroup/{role_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<WorkspaceGroupRole> findByWorkspaceGroupRoleID(@PathVariable("role_id") int role_id){

        return roleService.findByWorkspaceGroupRoleID(role_id);

    }

    @RequestMapping(value="/manager/userRole/search/profileGroup/{role_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<KeyGroupRole> findByKeyGroupRoleID(@PathVariable("role_id") int role_id){

       return roleService.findByKeyGroupRoleID(role_id);
    }

    @RequestMapping(value = "/manager/userRole/search/checkRoleName", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> MemberIdCheck(@RequestBody Map<String, Object> payload){

        // 1. validation check...
        if( payload.get(PayloadKeyType.role_name.name()).toString() == null || payload.get(PayloadKeyType.role_name.name()).toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID.getMessage());
        }

        // 3. db id check / findByEmail
        RoleOne roleOne = roleService.findByRoleNameCheck(payload.get(PayloadKeyType.role_name.name()).toString());
        JSONObject obj = new JSONObject();
        if(roleOne != null){
            obj.put("role_name_not_found","no");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            obj.put("role_name_not_found","yes");
            return responseUtility.makeSuccessResponse(obj);


        }



    }

}
