package com.inswave.whive.common.signingkeysetting;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeySetting {

    private int key_id;
    private int builder_id;
    private int admin_id;
    private String key_name;
    private String platform;
    private String key_type;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

}
