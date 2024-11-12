package com.inswave.whive.common.member;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberLogin {

    private Long user_id;
    private String email;
    private String password;
    private String user_role;
    private String user_name;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;
    private LocalDateTime last_login_date;
    private String build_yn;
    private String user_etc1;
    private String user_etc2;
    private String user_etc3;
    private String domain_id;
    private Long role_id;
    private String domain_name;
    private String user_login_id;
    private String token;
    private String pay_change_yn;
    private Object appid_json;

}
