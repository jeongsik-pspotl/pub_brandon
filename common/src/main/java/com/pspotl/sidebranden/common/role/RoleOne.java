package com.pspotl.sidebranden.common.role;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleOne {
    private int role_id;
    private String role_name;
    private LocalDateTime created_date;
    private LocalDateTime updated_date;
}
