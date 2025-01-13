package com.pspotl.sidebranden.common.usercode;

public interface UserKeyCodeDao {

    void insert(UserKeyCode userKeyCode);

    UserKeyCode findByKeyCode(UserKeyCode userKeyCode);


}
