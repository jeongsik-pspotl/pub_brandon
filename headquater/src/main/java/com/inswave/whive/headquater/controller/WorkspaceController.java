package com.inswave.whive.headquater.controller;


import com.inswave.whive.common.build.BuildProjectService;
import com.inswave.whive.common.build.ProjectGroupRoleVo;
import com.inswave.whive.common.member.MemberDetail;
import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.member.MemberService;
import com.inswave.whive.common.role.ProjectGroupRole;
import com.inswave.whive.common.role.WorkspaceGroupRoleList;
import com.inswave.whive.common.workspace.Workspace;
import com.inswave.whive.common.workspace.WorkspaceBuildProject;
import com.inswave.whive.common.workspace.WorkspaceService;
import com.inswave.whive.headquater.SessionConstants;
import com.inswave.whive.headquater.client.ClientHandler;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionKeyContents;
import com.inswave.whive.headquater.model.LoginSessionData;
import com.inswave.whive.headquater.util.ResponseUtility;
import com.inswave.whive.headquater.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private BuildProjectService buildProjectService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ResponseUtility responseUtility;

    @Autowired
    private ClientHandler clientHandler;

    @Autowired
    Common common;

    // workspace 생성 기능
    @RequestMapping(value = "/manager/workspace/create", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createWorkspace(HttpServletRequest request, @RequestBody Workspace workspace) {
        MemberDetail memberDetail;
        String userId = common.getTokenToRealName(request);

        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());
            workspace.setMember_id(memberDetail.getUser_id());
        }else {
            return null;
        }

        Workspace workspaceCheck = null;
        JSONObject entity = new JSONObject();
        try {

            // 이름 중복 기능 개선
            // 파라미터 기준 domain id, user id 조회 추가하고 해당
            // key 값을 기준으로 중복 조회 하기
            String worksapceName = workspace.getWorkspace_name();

            workspaceCheck = workspaceService.findByWorkspaceNameChekcObj(worksapceName);

            if(workspaceCheck != null){
                // 해당 기능 200 으로 처리하면서
                // result key value 내용 가지고 처리하기
                // 에러로 떨어뜨리지 않기
                entity.put("workspaceCheck","yes");
                return responseUtility.makeSuccessResponse(entity);
            }else {
                log.info("result return ????");

                entity.put("workspaceCheck","no");
                return responseUtility.makeSuccessResponse(entity);

            }

        }catch(Exception e) {
            // workspace
            workspaceService.insert(workspace);

        }
        return responseUtility.makeSuccessResponse();
    }

    // workspace 전체 조회 기능
    @RequestMapping(value = "/manager/workspace/search/workspaceAll", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<Workspace> findAll() {
        return workspaceService.findAll();
    }

    // workspace group 리스트 조회 기능
    @RequestMapping(value = "/api/workspacegroups", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Workspace> findGroupRoleAll(){
        return workspaceService.findGroupRoleAll();
    }

    // workspace 그룹 리스트 전체 조회 기능
    @RequestMapping(value = "/manager/workspace/search/roleListAll", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<Workspace> findWorkspaceRoleListAll() {
        return workspaceService.findWorkspaceRoleListAll();
    }

    // domain id도 추가
    // workspace name -> workspace_id 키 값을 변환 하기
    @RequestMapping(value = "/manager/workspace/search/workspaceId/{member_id}/{workspace_name}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Workspace findByMemberId(@PathVariable("member_id") Long member_id, @PathVariable("workspace_name") String workspace_name) {

        return workspaceService.findByMemberId(member_id, workspace_name);
    }

    // workspace key id 기준 조회
    @GetMapping(value = "/api/workspace/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Workspace findById(@PathVariable("id") Long id) {
        return workspaceService.findById(id);
    }

    // workspace user id 기준 조회
    @RequestMapping(value = "/api/workspace/member/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Workspace> findByWorkspaceMemberId(@PathVariable("id") Long member_id) {

        return workspaceService.findByWorkspaceMemberId(member_id);
    }

    // workspace check yn 기능
    @RequestMapping(value = "/manager/workspace/update/useYN", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateChekcYnWorkspace(@RequestBody List<Workspace> workspace) {

        List<Workspace> workspaceList = workspace;

        log.info(String.valueOf(workspaceList.size()));
        log.info(workspaceList.toString());
        for(int i = 0; i< workspaceList.size(); i++){

             Workspace workspaceByOne = workspaceList.get(i);
             log.info(workspaceByOne.toString());

             Workspace workspaceFindOne = workspaceService.findById(workspaceByOne.getWorkspace_id());
             //
             if(workspaceFindOne.getStatus().equals(workspaceByOne.getStatus()) && !workspaceFindOne.getWorkspace_name().equals(workspaceByOne.getWorkspace_name())){
                 workspaceService.updateByWorkspaceCheckYn(workspaceByOne);
             }else if(!workspaceFindOne.getStatus().equals(workspaceByOne.getStatus()) && workspaceFindOne.getWorkspace_name().equals(workspaceByOne.getWorkspace_name())){
                 workspaceService.updateByWorkspaceCheckYn(workspaceByOne);
             } else if(workspaceFindOne.getWorkspace_name().equals(workspaceByOne.getWorkspace_name())){

             } else if(workspaceFindOne.getStatus().equals(workspaceByOne.getStatus())){

             } else {
                 workspaceService.updateByWorkspaceCheckYn(workspaceByOne);
             }

        }

        return responseUtility.makeSuccessResponse();
    }


    @RequestMapping(value = "/api/workspace/menager/{role_code}/{workspace_name}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Workspace findbyWorkspaceNameAndRoleName(@PathVariable("role_code") String role_name, @PathVariable("workspace_name") String workspace_name) {

        return workspaceService.findbyWorkspaceNameAndRoleName(role_name, workspace_name);
    }

    // Workspace 권한 변경 호출 Controller
    @RequestMapping(value = "/manager/workspace/role_code_update", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateRoleCodeToWorkspace(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        String userId = common.getTokenToRealName(request);

        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            workspaceService.updateByRoleCode(Long.valueOf(payload.get(PayloadKeyType.workspace_id.name()).toString()), payload.get("role_code_id").toString(), payload.get("status").toString());

            return responseUtility.makeSuccessResponse();

        }else {

            return null;

        }


    }


    @RequestMapping(value = "/api/workspace/{id}", method = RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePassword(@PathVariable("id") Long id,@RequestBody Workspace workspace) {
        workspaceService.updateName(id,workspace.getWorkspace_name());
        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/workspace/delete", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        JSONObject obj;

        String userId = common.getTokenToRealName(request);

        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            obj = workspaceService.delete(Long.valueOf(payload.get("workspace_id").toString()), payload.get(PayloadKeyType.workspace_name.name()).toString());

            return responseUtility.makeSuccessResponse(obj);
        }else {
            return null;
        }


    }

    // workspace 하위 project 리스트 조회 기능
    @RequestMapping(value = "/api/workspaces/projects/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<WorkspaceBuildProject> findAllProject(@PathVariable("id") Long id) {
        return workspaceService.findAllProject(id);
    }

    // workspace 권한 관리 목록 조회 기능
    @RequestMapping(value = "/manager/workspace/search/projectList/{workspace_id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<ProjectGroupRoleVo> findByWorkspaceIDProjectAll(@PathVariable("workspace_id") int workspace_id) {
        return  buildProjectService.findByWorkspaceIDProjectAll(workspace_id);
    }


    // workspace 권한 관리 목록 조회 관련 기능
    @RequestMapping(value = "/manager/workspace/search/projectListWithGroupRole/{workspace_id}/{workspace_group_role_id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<ProjectGroupRoleVo> findByWorkspaceIDProjectCheckedAll(@PathVariable("workspace_id") int workspace_id, @PathVariable("workspace_group_role_id") int workspace_group_role_id) {
        return  buildProjectService.findByWorkspaceIDProjectCheckAll(workspace_id, workspace_group_role_id);
    }

    // 권한 관리 목록 조회 관련 기능
    @RequestMapping(value = "/manager/workspace/search/projectListAll", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<ProjectGroupRoleVo> findByProjectAll() {
        return buildProjectService.findByAllProject();
    }


    // 권한 관리 목록 조회 관련 기능 role_id
    @RequestMapping(value = "/manager/workspace/search/roleIdListInProject/{role_id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<ProjectGroupRoleVo> findByProjectAllByRoleID(@PathVariable("role_id") int role_id) {
        return buildProjectService.findProjectAllByRoleID(role_id);
    }

    // 권한 관리 목록 조회 관련 기능 role_id
    @RequestMapping(value = "/manager/workspace/search/roleIdListInWorkspaceGroup/{role_id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<WorkspaceGroupRoleList> findByProjectAll(@PathVariable("role_id")int role_id) {
        return workspaceService.findByWorkspaceGroupRoleAll(role_id);
    }

    // 권한 관리 목록 조회 관련 기능 workspace_group_id
    @RequestMapping(value = "/manager/workspace/search/projectGroup/{workspace_group_id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<ProjectGroupRole> findByWorkspaceIDProjectGroupAll(@PathVariable("workspace_group_id") int workspace_group_id) {
        return  buildProjectService.findByWorkspaceGroupIDProjectAll(workspace_group_id);
    }

    // findByWorkspaceMemberRoleName
    @RequestMapping(value = "/api/workspaces/memberRoleName/{member_role_name}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<Workspace> findAllProject(@PathVariable("member_role_name") String member_role_name) {
        return workspaceService.findByWorkspaceMemberRoleName(member_role_name);
    }
    
    //
    @RequestMapping(value = "/manager/workspace/search/workspaceListByMemberId", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Workspace> findAllWorkspaceMemberId(HttpServletRequest request) {

        String userId = common.getTokenToRealName(request);

        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        return workspaceService.findByWorkspaceMemberID(memberLogin.getUser_id(), Long.valueOf(memberLogin.getDomain_id()));
    }

    @RequestMapping(value = "/manager/workspace/search/workspaceListByMemberRole", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Workspace> findAllWorkspaceIndexListMemberId(HttpServletRequest request) {
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            return workspaceService.findByWorkspaceIndexListMemberID(memberLogin.getUser_id(), Long.valueOf(memberLogin.getDomain_id()));
        }else{
            return null;
        }


    }
}
