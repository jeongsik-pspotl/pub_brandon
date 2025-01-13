package com.pspotl.sidebranden.common.workspace;

import lombok.Data;

@Data
public class MemberMapping {

    private Long id;
    private Long member_id;
    private Long workspace_id;
    private String role_code_id;
    private String member_role;
    private Long domain_id;
}
