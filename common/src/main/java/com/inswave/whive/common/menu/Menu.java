package com.inswave.whive.common.menu;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * W-Hive 메뉴 정보를 저장할 DTO
 *
 * @soorink
 */
@Data
public class Menu {
    private Integer menu_id;
    private String menu_page_name;
    private String menu_code;
    private String menu_profile_type;
    private String menu_role_type;
    private Integer menu_pay_type;
    private String menu_lang_type;
    private String text_label;
    private String menu_key;
    private Integer depth;
    private String icon;
    private String url;
    private LocalDateTime create_date;
    private LocalDateTime update_date;
}
