package com.pspotl.sidebranden.common.build;

import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingDaoImpl;
import com.pspotl.sidebranden.common.ftpsetting.FTPSetting;
import com.pspotl.sidebranden.common.ftpsetting.FTPSettingDaoImpl;
import com.pspotl.sidebranden.common.member.MemberDetail;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberLoginDaoImpl;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingDaoImpl;
import com.pspotl.sidebranden.common.role.ProjectGroupRole;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BuildProjectService {
    @Autowired
    private BuildProjectDaoImpl buildProjectDaoImpl;

    @Autowired
    private FTPSettingDaoImpl ftpSettingDaoImpl;

    @Autowired
    private BranchSettingDaoImpl branchSettingDaoImpl;

    @Autowired
    private MemberLoginDaoImpl memberLoginDaoImpl;

    @Autowired
    private  PricingDaoImpl pricingDaoImpl;

    @Transactional
    public BuildProject findById(Long id) {
        return buildProjectDaoImpl.findById(id);
    }

    @Transactional
    public FTPSetting findByIdAndFTPId(Long id) {
        FTPSetting ftpSetting = new FTPSetting();
        BuildProject buildProject = buildProjectDaoImpl.findById(id);

        if (!buildProject.getFtp_id().equals("") || buildProject.getFtp_id() != null){
            ftpSetting = ftpSettingDaoImpl.findByID(buildProject.getFtp_id());
            return ftpSetting;
        }else {
            return ftpSetting;
        }

    }

    @Transactional
    public BranchSetting findByIdAndBranchId(Long id){
        BranchSetting branchSetting = new BranchSetting();
        BuildProject buildProject = buildProjectDaoImpl.findById(id);

        if(buildProject.getBuilder_id().equals("") || buildProject.getBuilder_id() != null){
            branchSetting = branchSettingDaoImpl.findByID(buildProject.getBuilder_id());
            return branchSetting;
        }else {
            return branchSetting;
        }

    }


    @Transactional
    public BuildProject findByWorkspaceId(Long id, String name) {
        return buildProjectDaoImpl.findByWorkspaceId(id, name);
    }

    @Transactional
    public List<ProjectGroupRoleVo> findByWorkspaceIDProjectAll(int workspace_id){
        return buildProjectDaoImpl.findByWorkspaceID(workspace_id);
    }

    @Transactional
    public List<ProjectGroupRoleVo> findByAllProject(){
        return buildProjectDaoImpl.findAllProject();
    }

    @Transactional
    public List<ProjectGroupRoleVo> findByWorkspaceIDProjectCheckAll(int workspace_id, int workspace_group_role_id) {
        return buildProjectDaoImpl.findByWorkspaceProjectCheckedAll(workspace_id, workspace_group_role_id);
    }

    @Transactional
    public List<ProjectGroupRole> findByWorkspaceGroupIDProjectAll(int workspace_group_id){
        return buildProjectDaoImpl.findByWorkspaceGroupID(workspace_group_id);
    }


    @Transactional
    public BuildProject findByProjectNameCheck(Long domain_id, Long member_id, String projectName){
        // member id + domain 기준으로 조회
        // 추가로 admin, superadmin 권한 기준으로 조회 쿼리 구현하기
        return buildProjectDaoImpl.findByProjectName(projectName);
    }

    @Transactional
    public List<BuildProject> findAll() {
        return buildProjectDaoImpl.findAll();
    }

    @Transactional
    public List<BuildProject> findRoleCodeAll(Long user_id){

        // member 데이터 호출
        MemberDetail memberDetail = memberLoginDaoImpl.findById(user_id);


        if(memberDetail.getUser_role().equals("SUPERADMIN")){
            return buildProjectDaoImpl.findAll();
        }else if(memberDetail.getUser_role().equals("ADMIN")){
            return buildProjectDaoImpl.findAllAdminProject(user_id);
        }else {
            return buildProjectDaoImpl.findProjectGroupAll(memberDetail.getRole_id());
        }

    }

    @Transactional
    public List<BuildProject> findServiceTierAll(String user_email){

        // member 데이터 호출
        MemberLogin memberLogin = memberLoginDaoImpl.findByEmail(user_email);
        Pricing pricing = pricingDaoImpl.findById(memberLogin.getUser_id());
        if(pricing != null){
            memberLogin.setPay_change_yn(pricing.getPay_change_yn());
        }else {
            memberLogin.setPay_change_yn("N");
        }

        if(memberLogin.getUser_role().equals("SUPERADMIN")){
            return buildProjectDaoImpl.findAll();
        }else if(memberLogin.getPay_change_yn().equals("Y")){
            // return buildProjectDaoImpl.findAllAdminProject(user_email);
            return buildProjectDaoImpl.findAllProjectProfessional(memberLogin.getUser_id());
        }else {
            return buildProjectDaoImpl.findAllProjectFree(memberLogin.getUser_id());
        }

    }

    @Transactional
    public List<ProjectGroupRoleVo> findProjectAllByRoleID(int role_id){
        return buildProjectDaoImpl.findAllProjectByRoleID(role_id);
    }


    @Transactional
    public void insert(BuildProject project) {
        project.setCreated_date(LocalDateTime.now());
        project.setUpdated_date(LocalDateTime.now());
        buildProjectDaoImpl.insert(project);
    }

    @Transactional
    public void updateBuildProject(BuildProject project) {
        project.setUpdated_date(LocalDateTime.now());
        buildProjectDaoImpl.updateBuildProject(project);
    }

    @Transactional
    public JSONObject delete(Long id, String workspace_name) {

        BuildProject buildProject;
        JSONObject obj = new JSONObject();

        buildProject = buildProjectDaoImpl.findByProjectNameCheckedID(id, workspace_name);

        if(buildProject != null){
            buildProjectDaoImpl.deleteById(id);
            obj.put("project_name_found_check","yes");
            return obj;
        }else {

            try {
                obj.put("project_name_found_check","no");

                // throw new Exception("삭제할 Project Name과 동일하지 않거나 없습니다.");
                return obj;

            } catch (Exception e) {
                e.printStackTrace();
                // log.info(String.valueOf(e.getStackTrace()));
            }
            return obj;
        }
    }

    @Transactional
    public BuildProject updateByVersionBuild(BuildProject buildProject){
        buildProjectDaoImpl.updateBuildVersionProject(buildProject);
        return buildProjectDaoImpl.findById(buildProject.getProject_id());
    }

    @Transactional
    public List<PlatformSelectAll> selectPlatformAll(){
        return buildProjectDaoImpl.selectPlatformFIndAll("platform");
    }

    @Transactional
    public List<ProgrammingSelectAll> selectFindAll(String platform){
        String role_code_type = "";

        if(platform.toLowerCase().equals("android")){
            role_code_type = "Andprogram";
        }else if(platform.toLowerCase().equals("ios")){
            role_code_type = "iosprogram";
        }else if(platform.toLowerCase().equals("windows")){
            role_code_type = "winprogram";
        }

        return buildProjectDaoImpl.selectProgramFindAll(role_code_type);
    }

    @Transactional
    public ProgrammingSelectAll selectFindID(String role_code_id){
        return buildProjectDaoImpl.selectProgramFindOne(role_code_id);

    }

    @Transactional
    public void updateToProjectDirPath(BuildProject buildProject){
        buildProject.setUpdated_date(LocalDateTime.now());
        buildProjectDaoImpl.updateBuildProjectDirPath(buildProject);
    }

    @Transactional
    public int findByProjectCount(){
        return buildProjectDaoImpl.findByProjectCount();
    }


}
