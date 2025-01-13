package com.pspotl.sidebranden.common.usercode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class UserKeyCodeService {

    @Autowired
    UserKeyCodeImpl userKeyCodeImpl;

    @Transactional
    public void userCodeCreate(UserKeyCode userKeyCode){
        userKeyCode.setUser_key_expired_date(LocalDateTime.now());
        userKeyCodeImpl.insert(userKeyCode);

    }

    @Transactional
    public UserKeyCode userCodeCheck(UserKeyCode userKeyCode){

        UserKeyCode userKeyCodeResult = userKeyCodeImpl.findByKeyCode(userKeyCode);
        log.info(userKeyCodeResult.toString());
        log.info(String.valueOf(LocalDateTime.now()));
        log.info(String.valueOf(ChronoUnit.MINUTES.addTo(LocalDateTime.now(),3)));
        Long minute = ChronoUnit.MINUTES.between(LocalDateTime.now(),ChronoUnit.MINUTES.addTo(userKeyCodeResult.getUser_key_expired_date(),3));

        // 정상 체크
        if(minute >= 3){
            log.info(String.valueOf(minute));

            return null;

        }else {
            log.info(String.valueOf(minute));

            if(userKeyCodeResult.getUser_key_code_value().equals(userKeyCode.getUser_key_code_value())){

                return userKeyCodeResult;

            }else {

                return null;

            }


        }

        // 만료 체크
        // log.info(String.valueOf(minute));

    }

}
