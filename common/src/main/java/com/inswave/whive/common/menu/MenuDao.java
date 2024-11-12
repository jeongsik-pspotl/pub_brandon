package com.inswave.whive.common.menu;

import java.util.List;

/**
 * W-Hive 메뉴 정보를 DB에서 가져올 DAO
 *
 * @soorink
 */
public interface MenuDao {

    //모든 메뉴 검색
    List<Menu> findByAll();

    //menu_id에 따른 메뉴 검색
    Menu findById(Integer id);

    //menu_page_name에 따른 메뉴 검색
    Menu findByMenupagename(String name);

    //menu_profile_type에 따른 메뉴 검색
    List<Menu> findByProfile(String profile);

    //menu_role_type에 따른 메뉴 검색
    List<Menu> findByRole(String role);

    //menu_role_type과 menu_profile_type, Lang에 따른 메뉴 검색
    public List<Menu> findByRoleAndProfileAndLang(String role, String profile, String Lang);

    // menu pay_type과 menu_profile_type, Lang에 따른 메뉴 검색
    List<Menu> findByPayAndProfileAndLang(String pay, String profile, String lang);

    //menu 추가
    void insert(Menu menu);
}
