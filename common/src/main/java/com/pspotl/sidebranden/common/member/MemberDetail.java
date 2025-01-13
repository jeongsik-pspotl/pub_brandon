package com.pspotl.sidebranden.common.member;

import com.pspotl.sidebranden.common.pricing.Pricing;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberDetail {

    private Long user_id;
    private String email;
    private String user_role;
    private String user_name;
    private Long domain_id;
    private String domain_name;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;
    private String last_login_date;
    private String role_name;
    private int role_id;
    private String user_login_id;
    private String phone_number;
    private String token;
    private String pay_change_yn;
    private Pricing pricing;
    private Object appid_json;

}
