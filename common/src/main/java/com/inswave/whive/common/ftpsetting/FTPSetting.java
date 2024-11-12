package com.inswave.whive.common.ftpsetting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FTPSetting {

    private Long ftp_id;
    private String ftp_name;
    private String ftp_url;
    private String ftp_type;
    private String ftp_etc1;
    private String ftp_etc2;
    private String ftp_ip;
    private String ftp_port;
    private String ftp_user_id;
    private String ftp_user_pwd;

    private LocalDateTime create_date;

    @JsonIgnore
    private LocalDateTime update_date;

}
