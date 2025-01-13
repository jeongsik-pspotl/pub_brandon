package com.pspotl.sidebranden.common.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MenuDaoImple을 편하게 사용하게 해주는 MenuService
 *
 * @soorink
 */

@Service
public class MenuService {

    @Autowired
    private MenuDaoImpl menuDaoImpl;

    @Transactional
    public List<Menu> findByAll() {
        return menuDaoImpl.findByAll();
    }

    @Transactional
    public Menu findById(Integer id) {
        return menuDaoImpl.findById(id);
    }

    @Transactional
    public Menu findByMenupagename(String name) {
        return menuDaoImpl.findByMenupagename(name);
    }

    @Transactional
    public List<Menu> findByProfile(String profile) {
        return menuDaoImpl.findByProfile(profile);
    }

    @Transactional
    public List<Menu> findByRole(String role) {
        return menuDaoImpl.findByRole(role);
    }

    @Transactional
    public List<Menu> findByRoleAndProfile(Menu menu) {
        String role = menu.getMenu_role_type();
        String profile = menu.getMenu_profile_type();
        String lang = menu.getMenu_lang_type();

        return menuDaoImpl.findByRoleAndProfileAndLang(role, profile, lang);
    }

    @Transactional
    public List<Menu> findByPayAndProfile(Menu menu){
        String pay = String.valueOf(menu.getMenu_pay_type());
        String profile = menu.getMenu_profile_type();
        String lang = menu.getMenu_lang_type();


        return menuDaoImpl.findByPayAndProfileAndLang(pay, profile, lang);

    }

    @Transactional
    public void insert(Menu menu) {
        menu.setCreate_date(LocalDateTime.now());
        menu.setUpdate_date(LocalDateTime.now());

        menuDaoImpl.insert(menu);
    }
}
