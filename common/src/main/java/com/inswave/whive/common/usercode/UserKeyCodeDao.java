package com.inswave.whive.common.usercode;

public interface UserKeyCodeDao {

    void insert(UserKeyCode userKeyCode);

    UserKeyCode findByKeyCode(UserKeyCode userKeyCode);


}
