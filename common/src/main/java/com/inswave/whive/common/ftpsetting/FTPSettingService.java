package com.inswave.whive.common.ftpsetting;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FTPSettingService {

    @Autowired
    private FTPSettingDaoImpl ftpSettingDao;

    @Transactional
    public void insert(FTPSetting ftpSetting) {
        ftpSetting.setCreate_date(LocalDateTime.now());
        ftpSetting.setUpdate_date(LocalDateTime.now());
        ftpSettingDao.insert(ftpSetting);
    }

    @Transactional
    public void update(FTPSetting ftpSetting){
        ftpSettingDao.update(ftpSetting);
    }

    @Transactional
    public List<FTPSetting> findAll() {
        return ftpSettingDao.findAll();
    }

    // findByID
    @Transactional
    public FTPSetting findbyID(Long ftp_id) {
        return ftpSettingDao.findByID(ftp_id);
    }

    @Transactional
    public FTPSetting findByName(String ftp_name) {
        return ftpSettingDao.findByName(ftp_name);
    }

    @Transactional
    public List<FTPSelectBoxList> findBySelectList() { return ftpSettingDao.findBySelectList(); }

    @Transactional
    public FTPSelectBoxList findBySelectListFTPID(Long ftp_id) { return ftpSettingDao.findBySelectListFTPID(ftp_id); }
}
