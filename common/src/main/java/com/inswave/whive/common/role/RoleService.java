package com.inswave.whive.common.role;

import com.inswave.whive.common.signingkeysetting.KeyAndroidSetting;
import com.inswave.whive.common.signingkeysetting.KeySetting;
import com.inswave.whive.common.signingkeysetting.KeyiOSSetting;
import com.inswave.whive.common.signingkeysetting.SigningKeySettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RoleService {

    @Autowired
    RoleDaoImpl roleDaoImpl;

    @Autowired
    SigningKeySettingService signingKeySettingService;

    @Transactional
    public void insert(Role role){
        WorkspaceGroupRole workspaceGroupRole;
        KeyGroupRole keyGroupRoleTemp;
        KeyGroupRole keyGroupRole = new KeyGroupRole();
        KeySetting keySetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        ProjectGroupRole projectGroupRole;

        role.setCreated_date(LocalDateTime.now());
        role.setUpdated_date(LocalDateTime.now());
        roleDaoImpl.insert(role);

        int roleListConut = roleDaoImpl.findRoleLastCount();

        // next workspace group insert
        List<WorkspaceGroupRole> workspaceGroupRoleList = role.getWorkspaceGroupRole();
        List<ProjectGroupRole> projectGroupRoleList = role.getProjectGroupRole();

        // role id set
        for(int i = 0; i<workspaceGroupRoleList.size(); i++){

            workspaceGroupRole = workspaceGroupRoleList.get(i);
            workspaceGroupRole.setRole_id(roleListConut);

            roleDaoImpl.insertToWorkspaceGroup(workspaceGroupRole);

            int workspaceGroupConut = roleDaoImpl.findRoleLastCount();

            // find workspaceGroup -> workspace id
            for( int p = 0; p<projectGroupRoleList.size(); p++){

                projectGroupRole = projectGroupRoleList.get(p);
                // workspace == project 기준 참고해서 처리하기
                // check  workspace id
                if(projectGroupRole.getWorkspace_id() == workspaceGroupRole.getWorkspace_id()){
                    projectGroupRole.setWorkspace_group_role_id(workspaceGroupConut); // workspace group id 추가

                    // insert to project group list
                    roleDaoImpl.insertToProjectGroup(projectGroupRole);

                }else {

                }

            }

        }

        // project group list insert 추가해야함.

//

        // next key group insert
        List<KeyGroupRole> keyGroupRoleList = role.getKeyGroupRole();

        // role id set
        for (int k = 0; k<keyGroupRoleList.size(); k++) {

            keyGroupRoleTemp = keyGroupRoleList.get(k);
            keyGroupRole.setRole_id(roleListConut);

            keySetting = signingKeySettingService.findByID(keyGroupRoleTemp.getKey_id());

            if(keySetting.getPlatform().toLowerCase().equals("android")) {

                keyAndroidSetting = signingKeySettingService.findByAndroidID(Long.valueOf(keySetting.getKey_id()));

                String androidKeyType = keyAndroidSetting.getAndroid_key_type().toLowerCase();

                // key type이 all인 경우는 임시로 build와 같이 처리한다.
                // 추후, DB 변경하면서 all 타입을 만들 예정이고, 관련해서 다시 변경 예정이다.
                if (androidKeyType.equals("all")) {
                    keyGroupRole.setKey_build_android_id(keyAndroidSetting.getKey_id());
                } else if(androidKeyType.equals("build")){
                    keyGroupRole.setKey_build_android_id(keyAndroidSetting.getKey_id());
                } else if(androidKeyType.equals("deploy")){
                    keyGroupRole.setKey_deploy_android_id(keyAndroidSetting.getKey_id());
                }

            } else if(keySetting.getPlatform().toLowerCase().equals("ios")) {

                keyiOSSetting = signingKeySettingService.findByiOSKeyID(Long.valueOf(keySetting.getKey_id()));

                String iosKeyType = keyiOSSetting.getIos_key_type().toLowerCase();

                // key type이 all인 경우는 임시로 build와 같이 처리한다.
                // 추후, DB 변경하면서 all 타입을 만들 예정이고, 관련해서 다시 변경 예정이다.
                if (iosKeyType.equals("all")) {
                    keyGroupRole.setKey_build_ios_id(keyiOSSetting.getKey_id());
                } else if(iosKeyType.equals("build")){
                    keyGroupRole.setKey_build_ios_id(keyiOSSetting.getKey_id());
                } else if(iosKeyType.equals("deploy")){
                    keyGroupRole.setKey_deploy_ios_id(keyiOSSetting.getKey_id());
                }

            }

        }

        roleDaoImpl.insertToKeyGroup(keyGroupRole);



    }

    @Transactional
    public void update(Role role){
        WorkspaceGroupRole workspaceGroupRole;
        KeyGroupRole keyGroupRoleTemp;
        KeyGroupRole keyGroupRole = new KeyGroupRole();
        KeySetting keySetting;
        KeyAndroidSetting keyAndroidSetting;
        KeyiOSSetting keyiOSSetting;
        ProjectGroupRole projectGroupRole;

        // 선택이 되어 지지 않은 경우 데이터 삭제 기능 구현해야함

        // 1. role id key 값 가지고 업데이트 작업 진행햐애함.
        log.info(role.toString());

        // 2. workspace 수정 목록 있는지 확인 필요
        List<WorkspaceGroupRole> workspaceGroupRoleList = role.getWorkspaceGroupRole();
        List<ProjectGroupRole> projectGroupRoleList = role.getProjectGroupRole();
        for(int i = 0; i<workspaceGroupRoleList.size(); i++){
            workspaceGroupRole = workspaceGroupRoleList.get(i);
            List<WorkspaceGroupRole> workspaceGroupRoles = roleDaoImpl.findByWorkspaceGroupRole(workspaceGroupRole.getRole_id());
            if(workspaceGroupRole.getSelect_yn().equals("1")) {
                if (workspaceGroupRole.getWorkspace_group_role_id() == 0) {
                    roleDaoImpl.insertToWorkspaceGroup(workspaceGroupRole);
                }
            }else if(workspaceGroupRole.getSelect_yn().equals("0") && workspaceGroupRole.getWorkspace_group_role_id() != 0){
                roleDaoImpl.deleteToWorkspaceGroup(workspaceGroupRole);
            }

            for(int k = 0; k < workspaceGroupRoles.size(); k++){
                WorkspaceGroupRole workspaceGroupRoleTemp = workspaceGroupRoles.get(k);
                if(workspaceGroupRole.getSelect_yn().equals("1")){
                    //log.info("check workspace id..");
                    //log.info(workspaceGroupRole.toString());
                    // find workspaceGroup -> workspace id
                    for( int p = 0; p<projectGroupRoleList.size(); p++){

                        projectGroupRole = projectGroupRoleList.get(p);
                        if(projectGroupRole.getProject_group_role_id() == 0){
                            projectGroupRole.setWorkspace_group_role_id(workspaceGroupRole.getWorkspace_group_role_id());
                            roleDaoImpl.insertToProjectGroup(projectGroupRole);
                        }else{
                            // update to project group list
                            projectGroupRole.setWorkspace_group_role_id(workspaceGroupRole.getWorkspace_group_role_id());
                            roleDaoImpl.updateToProjectGroup(projectGroupRole);
                        }



                    }

                }else if(workspaceGroupRole.getSelect_yn().equals("0") && workspaceGroupRole.getWorkspace_group_role_id() != 0){
                    // delete workspace id


                    int workspaceGroupConut = roleDaoImpl.findRoleLastCount();

                    // find workspaceGroup -> workspace id
                    for( int p = 0; p<projectGroupRoleList.size(); p++){

                        projectGroupRole = projectGroupRoleList.get(p);
                        // workspace == project 기준 참고해서 처리하기
                        // check  workspace id
                        if(projectGroupRole.getWorkspace_id() == workspaceGroupRole.getWorkspace_id()){
                            projectGroupRole.setWorkspace_group_role_id(workspaceGroupConut); // workspace group id 추가

                            // insert to project group list
                            roleDaoImpl.insertToProjectGroup(projectGroupRole);

                        }else {

                        }

                    }
                }
            }

        }

        // 3. key 수정 목록 있는지 확인 필요
        List<KeyGroupRole> keyGroupRoleList = role.getKeyGroupRole();
        // role id set
        for(int k = 0; k<keyGroupRoleList.size(); k++){

            keyGroupRoleTemp = keyGroupRoleList.get(k);

            List<KeyGroupRole> keyGroupRoleLists = roleDaoImpl.findByKeyGroupRole(keyGroupRoleTemp.getRole_id());

            for( int f = 0 ;f<keyGroupRoleLists.size();f++){

                if(keyGroupRoleTemp.getSelect_yn().equals("1")){

                    keySetting = signingKeySettingService.findByID(keyGroupRoleTemp.getKey_id());
                    KeyGroupRole keyGroupRole_2 = keyGroupRoleLists.get(f);
                    keyGroupRole.setKey_group_role_id(keyGroupRole_2.getKey_group_role_id());
                    keyGroupRole.setRole_id(keyGroupRole_2.getRole_id());
                    if(keySetting.getPlatform().toLowerCase().equals("android")){

                        keyAndroidSetting = signingKeySettingService.findByAndroidID(Long.valueOf(keySetting.getKey_id()));

                        if(keyGroupRole_2.getKey_id() == keyGroupRoleTemp.getKey_id()){
                            log.info("check key id.... ");
                            if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("build")){
                                keyGroupRole.setKey_build_android_id(keyAndroidSetting.getKey_id());
                            }else if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("deploy")){
                                keyGroupRole.setKey_deploy_android_id(keyAndroidSetting.getKey_id());
                            }
                        }else {
                            if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("build")){
                                keyGroupRole.setKey_build_android_id(keyAndroidSetting.getKey_id());
                            }else if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("deploy")){
                                keyGroupRole.setKey_deploy_android_id(keyAndroidSetting.getKey_id());
                            }
                        }


                    }else if(keySetting.getPlatform().toLowerCase().equals("ios")){

                        keyiOSSetting = signingKeySettingService.findByiOSKeyID(Long.valueOf(keySetting.getKey_id()));

                        if( keyGroupRole_2.getKey_id() == keyGroupRoleTemp.getKey_id() ){
                            log.info("check key id.... ");
                            if(keyiOSSetting.getIos_key_type().toLowerCase().equals("build")){
                                keyGroupRole.setKey_build_ios_id(keyiOSSetting.getKey_id());
                            }else if(keyiOSSetting.getIos_key_type().toLowerCase().equals("deploy")){
                                keyGroupRole.setKey_deploy_ios_id(keyiOSSetting.getKey_id());

                            }
                        }else {

                            if(keyiOSSetting.getIos_key_type().toLowerCase().equals("build")){
                                keyGroupRole.setKey_build_ios_id(keyiOSSetting.getKey_id());
                            }else if(keyiOSSetting.getIos_key_type().toLowerCase().equals("deploy")){
                                keyGroupRole.setKey_deploy_ios_id(keyiOSSetting.getKey_id());

                            }

                        }

                    }

                }else if(keyGroupRoleTemp.getSelect_yn().equals("0") || keyGroupRoleTemp.getSelect_yn().equals("")){

                    keySetting = signingKeySettingService.findByID(keyGroupRoleTemp.getKey_id());
                    KeyGroupRole keyGroupRole_2 = keyGroupRoleLists.get(f);
                    keyGroupRole.setKey_group_role_id(keyGroupRole_2.getKey_group_role_id());
                    keyGroupRole.setRole_id(keyGroupRole_2.getRole_id());

                    if(keySetting.getPlatform().toLowerCase().equals("android")){

                        keyAndroidSetting = signingKeySettingService.findByAndroidID(Long.valueOf(keySetting.getKey_id()));

                        if(keyGroupRole_2.getKey_id() == keyGroupRoleTemp.getKey_id()){

                            if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("build")){
                                keyGroupRole.setKey_build_android_id(0);
                            }else if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("deploy")){
                                keyGroupRole.setKey_deploy_android_id(0);
                            }
                        }else if(keyGroupRole.getKey_build_android_id() != 0 || keyGroupRole.getKey_build_ios_id() != 0){

                        } else if(keyGroupRole.getKey_deploy_android_id() != 0 || keyGroupRole.getKey_deploy_ios_id() != 0){

                        } else {
//                            if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("build")){
//                                keyGroupRole.setKey_build_android_id(0);
//                            }else if(keyAndroidSetting.getAndroid_key_type().toLowerCase().equals("deploy")){
//                                keyGroupRole.setKey_deploy_android_id(0);
//                            }
                        }


                    }else if(keySetting.getPlatform().toLowerCase().equals("ios")){

                        keyiOSSetting = signingKeySettingService.findByiOSKeyID(Long.valueOf(keySetting.getKey_id()));

                        if( keyGroupRole_2.getKey_id() == keyGroupRoleTemp.getKey_id() ){

                            if(keyiOSSetting.getIos_key_type().toLowerCase().equals("build")){
                                keyGroupRole.setKey_build_ios_id(0);
                            }else if(keyiOSSetting.getIos_key_type().toLowerCase().equals("deploy")){
                                keyGroupRole.setKey_deploy_ios_id(0);

                            }
                        }else {


                        }

                    }

                }




            }


        }
        log.info(keyGroupRole.toString());
        // update key group
        roleDaoImpl.updateToKeyGroup(keyGroupRole);

        /// role name 수정
        roleDaoImpl.updateToRoleName(role.getRole_id(),role.getRole_name());


    }

    @Transactional
    public List<Role> findAll(){
        return roleDaoImpl.findAll();
    }

    @Transactional
    public Role findByRoleID(int role_id){
        return roleDaoImpl.findByRoldID(role_id);
    }

    @Transactional
    public RoleOne findByRoleNameCheck(String role_name){
        return roleDaoImpl.findByRoleNameCheck(role_name);
    }

    @Transactional
    public List<WorkspaceGroupRole> findByWorkspaceGroupRoleID(int role_id){

        return roleDaoImpl.findByWorkspaceGroupRole(role_id);
    }

    @Transactional
    public List<KeyGroupRole> findByKeyGroupRoleID(int role_id){
        return roleDaoImpl.findByKeyGroupRole(role_id);
    }

    @Transactional
    public void insertToProjectGroupRole(ProjectGroupRole projectGroupRole){ roleDaoImpl.insertToProjectGroup(projectGroupRole);}


}
