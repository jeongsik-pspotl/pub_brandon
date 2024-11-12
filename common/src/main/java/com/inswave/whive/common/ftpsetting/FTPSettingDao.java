package com.inswave.whive.common.ftpsetting;


import java.util.List;

public interface FTPSettingDao {

    void insert(FTPSetting ftpSetting);

    void update(FTPSetting ftpSetting);

    List<FTPSetting> findAll();

    FTPSetting findByID(Long ftp_id);

    FTPSetting findByName(String ftp_name);

    List<FTPSelectBoxList> findBySelectList();

    FTPSelectBoxList findBySelectListFTPID(Long ftp_id);

}
