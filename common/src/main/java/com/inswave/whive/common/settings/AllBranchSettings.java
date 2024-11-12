package com.inswave.whive.common.settings;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllBranchSettings {

    private int id;
    private String branch_id;
    private String branch_name;
    private String session_name;
    private String session_status;
    private String session_type;
    private LocalDateTime last_date;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

}
