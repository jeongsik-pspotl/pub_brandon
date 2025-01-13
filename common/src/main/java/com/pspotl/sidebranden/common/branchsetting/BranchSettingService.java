package com.pspotl.sidebranden.common.branchsetting;

import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.BCryptEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BranchSettingService {

    @Autowired
    private BranchSettingDaoImpl branchSettingDao;

    @Transactional
    public void insert(BranchSetting branchSetting) {
        branchSetting.setCreate_date(LocalDateTime.now());
        branchSetting.setUpdate_date(LocalDateTime.now());
        branchSetting.setBuilder_password(new BCryptPasswordEncoder().encode(branchSetting.getBuilder_password()));
        branchSettingDao.insert(branchSetting);
    }

    @Transactional
    public void update(BranchSetting branchSetting){

    }

    @Transactional
    public List<BranchSetting> findAll() {
        return branchSettingDao.findAll();
    }

    // findByID
    @Transactional
    public BranchSetting findbyID(Long branch_id) {
        return branchSettingDao.findByID(branch_id);
    }

    @Transactional
    public List<BuilderSelectBoxList> findBySelectBOXLIst(){
        return branchSettingDao.findBySelectBOXLIst();
    }

    @Transactional
    public BuilderSelectBoxList findBySelectBOXListByID(Long branch_id){
            return branchSettingDao.findBySelectBOXListByID(branch_id);
    }

    @Transactional
    public BranchSetting findByUserID(String branch_user_id) { return branchSettingDao.findByUserID(branch_user_id); }

    @Transactional
    public void updateByStatus(BranchSetting branchSetting){
        branchSetting.setUpdate_date(LocalDateTime.now());
        branchSetting.setLast_date(LocalDateTime.now());
        branchSettingDao.updateByStatus(branchSetting);
    }

    @Transactional
    public BranchSetting findByBranchName(String branch_name) { return branchSettingDao.findByBranchName(branch_name); }

    @Transactional
    public boolean findByBuilderPasswordCheck(BranchSetting branchSetting){

        BranchSetting branchSettingTmp = null;
        boolean isPasswordMatching = false;
        // 내부에서 암호화 체크 기능 추가..
        String rawPassword = branchSetting.getBuilder_password();
        try {
            branchSettingTmp = branchSettingDao.findByID(branchSetting.getBuilder_id());
        }catch (Exception e){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID_OR_PASSWORD.getMessage());
        }


        BCryptEncoder encoder = new BCryptEncoder();
        try{
            isPasswordMatching = encoder.matches(rawPassword, branchSettingTmp.getBuilder_password());
        }catch(Exception e){
             
            log.error("password error ", e);
        }


        if(!isPasswordMatching){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID_OR_PASSWORD.getMessage());
        }


        return isPasswordMatching;
    }

    @Transactional
    public void updateBuilderPassword(BranchSetting branchSetting){
        branchSetting.setBuilder_password(new BCryptPasswordEncoder().encode(branchSetting.getBuilder_password()));
        branchSettingDao.update(branchSetting);
    }

    @Transactional
    public int findByBuilderID(){
        return branchSettingDao.findByBuilderID();
    }

}
