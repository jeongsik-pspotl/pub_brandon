package com.pspotl.sidebranden.common.vcssetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VCSSettingService {

    @Autowired
    private VCSSettingDaoImpl vcsSettingDao;

    @Value("${whive.distribution.localgitUrl}")
    private String localgitUrl;

    @Value("${whive.distribution.localsvnUrl}")
    private String localsvnUrl;

    @Transactional
    public void insert(VCSSetting vcsSetting) {
        vcsSetting.setCreate_date(LocalDateTime.now());
        vcsSetting.setUpdate_date(LocalDateTime.now());

        // localgit 일 경우 url 정보 기본 저장
        if(vcsSetting.getVcs_type().equals("localgit")) {
            vcsSetting.setVcs_url(localgitUrl);
        } else if(vcsSetting.getVcs_type().equals("localsvn")) {
            vcsSetting.setVcs_url(localsvnUrl);
        }

        vcsSettingDao.insert(vcsSetting);
    }

    @Transactional
    public void update(VCSSetting vcsSetting){
        vcsSetting.setUpdate_date(LocalDateTime.now());
        // vcsSetting.setVcs_user_pwd(new BCryptPasswordEncoder().encode(vcsSetting.getVcs_user_pwd()));
        vcsSettingDao.update(vcsSetting);
    }

    @Transactional
    public List<VCSSetting> findAll(String userRole, Long user_id) {

        if(userRole.toLowerCase().equals("superadmin")){
            return vcsSettingDao.findAll();
        }else if(userRole.toLowerCase().equals("admin")){
            return vcsSettingDao.findAdminAll(user_id);
        }else{
            return null;
        }

    }

    // findByID
    @Transactional
    public VCSSetting findbyID(Long vcs_id) {
        return vcsSettingDao.findByID(vcs_id);
    }

    // findBySelectList
    @Transactional
    public List<VCSSelectBoxList> finfindBySelectList(){
        return vcsSettingDao.findBySelectList();
    }

    // findBySelectListVcsID
    @Transactional
    public VCSSelectBoxList findBySelectListVcsID(Long vcs_id){
        return vcsSettingDao.findBySelectListVcsID(vcs_id);
    }

    @Transactional
    public List<VCSSelectBoxList> findBySelectListType(String vcs_type) { return vcsSettingDao.findBySelectListType(vcs_type); }

    @Transactional
    public List<VCSSelectBoxList> findBySelectListAdminID(Long admin_id) { return vcsSettingDao.findBySelectListAdminID(admin_id); }

    @Transactional
    public VCSSetting findByVCSName(String vcs_name) { return vcsSettingDao.findByVCSName(vcs_name); }

    @Transactional
    public List<VCSSelectBoxList> findBySelectListAdminIdAndVcsType(Long admin_id, String vcs_type) { return vcsSettingDao.findBySelectListAdminVcsType(admin_id, vcs_type); }

}
