package com.pspotl.sidebranden.common.branchsetting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BranchSetting {

    private Long builder_id;
    private Long role_code_id;
    private String builder_user_id;
    private String builder_name;
    private String session_status;
    private String session_type;
    private String builder_url;
    private String builder_password;

    private LocalDateTime create_date;

    @JsonIgnore
    private LocalDateTime update_date;

    @JsonIgnore
    private LocalDateTime last_date;

}
