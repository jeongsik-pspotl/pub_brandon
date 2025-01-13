package com.pspotl.sidebranden.common.usercode;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserKeyCode {

    private int user_key_id;
    private String user_key_email;
    private String user_key_code_value;
    private LocalDateTime user_key_expired_date;

}
