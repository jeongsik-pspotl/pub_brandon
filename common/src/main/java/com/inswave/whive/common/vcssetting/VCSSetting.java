package com.inswave.whive.common.vcssetting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VCSSetting {
    private String vcs_id ;
    private String vcs_name;
    private String vcs_type;
    private String vcs_url;
    private String vcs_user_id;
    private String vcs_user_pwd;
    private Long admin_id;

    private LocalDateTime create_date;
    @JsonIgnore
    private LocalDateTime update_date;
}
