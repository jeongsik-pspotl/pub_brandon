package com.pspotl.sidebranden.common.workspace;

import com.pspotl.sidebranden.common.member.MemberDetail;
import com.pspotl.sidebranden.common.member.MemberLoginDaoImpl;
import com.pspotl.sidebranden.common.role.WorkspaceGroupRoleList;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceDaoImpl workspaceDaoImpl;

    @Autowired
    private MemberLoginDaoImpl memberLoginDaoImpl;

    @Transactional
    public Workspace findById(Long id) {
        return workspaceDaoImpl.findById(id);
    }

    @Transactional
    public Workspace findByMemberId(Long id, String workspaceName) {
        return workspaceDaoImpl.findByMemberId(id, workspaceName);
    }

    @Transactional
    public Workspace findbyWorkspaceNameAndRoleName(String roleName, String workspaceName){

        return workspaceDaoImpl.findByWorkspaceNameAndRoleName(roleName, workspaceName);
    }

    @Transactional
    public List<Workspace> findByWorkspaceMemberId(Long member_id) {
        // return workspaceDaoImpl.findByWorkspaceMemberId(member_id);
        return null;
    }

    @Transactional
    public List<Workspace> findAll() {
        return workspaceDaoImpl.findAll();
    }

    @Transactional
    public List<Workspace> findWorkspaceRoleListAll(){
        return workspaceDaoImpl.findWorkspaceRoleListAll();
    }

    @Transactional
    public List<Workspace> findGroupRoleAll(){
        return workspaceDaoImpl.findGroupRoleAll();
    }

    @Transactional
    public List<WorkspaceGroupRoleList> findByWorkspaceGroupRoleAll(int role_id) { return workspaceDaoImpl.findByWorkspaceGroupRoleAll(role_id);}

    @Transactional
    public Workspace insert(Workspace workspace) {
        Workspace workspaceTemp = new Workspace();

        workspaceDaoImpl.insert(workspace);

        return workspaceTemp;
    }

    public Workspace insert_bak(Workspace workspace) {
        Workspace workspaceTemp = new Workspace();
        MemberMapping memberMappingTemp;
        MemberDetail memberDetail = new MemberDetail();

        // workspace.setCreated_date(LocalDateTime.now());
        workspaceDaoImpl.insert(workspace);

        if(workspace.getMember_id() != null){
            memberDetail = memberLoginDaoImpl.findById(workspace.getMember_id());
            workspaceTemp = workspaceDaoImpl.findByMemberId(workspace.getMember_id(), workspace.getWorkspace_name());
            memberMappingTemp = workspace.getMemberMapping();
            RoleCode roleCode = workspaceDaoImpl.findByRoleCodeId(memberMappingTemp.getMember_role());

            // member_mapping insert
            memberMappingTemp.setWorkspace_id(workspaceTemp.getWorkspace_id());
            memberMappingTemp.setRole_code_id(String.valueOf(roleCode.getRole_code_id()));
            memberMappingTemp.setDomain_id(memberDetail.getDomain_id());
            workspaceTemp.setMemberMapping(memberMappingTemp);
            workspaceDaoImpl.memberRoleInsert(memberMappingTemp);
            return workspaceTemp;
        }else {
            return workspaceTemp;
        }


    }


    @Transactional
    public void MemberMappingInsert(MemberMapping memberMapping){
        workspaceDaoImpl.memberRoleInsert(memberMapping);
    }

    @Transactional
    public void updateByWorkspaceCheckYn(Workspace workspace) {
        // workspace.setUpdated_date(LocalDateTime.now());
        workspaceDaoImpl.updateByWorkspaceCheckYn(workspace);
    }

    @Transactional
    public void updateName(Long id, String name) {
        workspaceDaoImpl.updateByName(id,name);
    }

    @Transactional
    public void updateByRoleCode(Long workspace_id, String role_code_id, String status){
        // parameter  workspace_id, member_role, status
        LocalDateTime updated_date;
        updated_date = LocalDateTime.now();
        workspaceDaoImpl.updateByRoleCodeId(workspace_id, status, updated_date);
        updated_date = LocalDateTime.now();
        workspaceDaoImpl.updateByRoleCode(status, workspace_id, updated_date);


    }

    @Transactional
    public JSONObject delete(Long id, String workspace_name) {

        Workspace workspace;
        JSONObject obj = new JSONObject();

        workspace = workspaceDaoImpl.findByWorkspaceNameCheckID(id, workspace_name);

        if(workspace != null){
            workspaceDaoImpl.deleteById(id);
            obj.put("workspace_name_found_check","yes");
            return obj;
        }else {

            try {
                obj.put("workspace_name_found_check","no");

                throw new Exception("삭제할 Workspace Name과 동일하지 않거나 없습니다.");

            } catch (Exception e) {
                    
                log.info(String.valueOf(e.getStackTrace()));
            }
            return obj;
        }
    }

    @Transactional
    public List<WorkspaceBuildProject> findAllProject(Long id) { return workspaceDaoImpl.findAllProject(id); }

    @Transactional
    public List<Workspace> findByWorkspaceMemberRoleName(String MemberRoleName) {

        // super admin 일떄, 전체 조회
        if(MemberRoleName.equals("SUPERADMIN")){
            return workspaceDaoImpl.findAll();
        }else {
            return workspaceDaoImpl.findByWorkspaceMemberRoleCode(MemberRoleName);
        }

    }

    @Transactional
    public Workspace findByWorkspaceNameChekcObj(String workspaceName){ return workspaceDaoImpl.findByWorkspaceNameCheck(workspaceName); }

    @Transactional
    public List<Workspace> findByWorkspaceMemberID(Long member_id, Long domain_id){

        MemberDetail memberDetail = memberLoginDaoImpl.findById(member_id);

        if(memberDetail.getUser_role().equals("SUPERADMIN")){
            return workspaceDaoImpl.findAll();
        }else if(memberDetail.getUser_role().equals("ADMIN")){
            return workspaceDaoImpl.findByWorkspaceMemberRole(member_id, domain_id);

        } else {
            // 조건이 추가되어야 하는데 ..
            // 해당 구간은 추후에 2/3 구간 정도 완료 되고 나서 구현하기..
            return workspaceDaoImpl.findByWorkspaceGroupList(memberDetail.getRole_id());
        }

    }

    @Transactional
    public List<Workspace> findByWorkspaceIndexListMemberID(Long member_id, Long domain_id){

        MemberDetail memberDetail = memberLoginDaoImpl.findById(member_id);

        if(memberDetail.getUser_role().equals("SUPERADMIN")){
            return workspaceDaoImpl.findAll();
        }else if(memberDetail.getUser_role().equals("ADMIN")){
            return workspaceDaoImpl.findByWorkspaceIndexListMemberRole(member_id, domain_id);

        } else {
            // 조건이 추가되어야 하는데 ..
            // 해당 구간은 추후에 2/3 구간 정도 완료 되고 나서 구현하기..
            return workspaceDaoImpl.findByWorkspaceGroupList(memberDetail.getRole_id());
        }
    }

    @Transactional
    public MemberMapping findByMemberRoleWorkspaceID(Long workspcae_id){
        return workspaceDaoImpl.findByMemberRoleWorkspaceID(workspcae_id);
    }


}