package com.pspotl.sidebranden.manager.model;

import lombok.Data;

@Data
public class LoginSessionData {
    Long userId;
    String userRole;
    String DomainId;
    String userLoginId;
    Long roleId;
}
