package com.pspotl.sidebranden.common.member;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberUserCreate {

    private Long user_id;
    private String email;
    private String password;
    private String confirmPassword;
    private String user_role;
    private String user_name;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;
    private LocalDateTime last_login_date;
    private String build_yn;
    private String user_etc1;
    private String user_etc2;
    private String user_etc3;
    private Long domain_id;
    private int role_id;
    private String user_login_id;
    private String phone_number;
    private Object appid_json;


}
