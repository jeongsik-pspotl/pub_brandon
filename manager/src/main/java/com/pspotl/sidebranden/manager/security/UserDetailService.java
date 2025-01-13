package com.pspotl.sidebranden.manager.security;

import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

     private final MemberService memberService;

    @Autowired
    public UserDetailService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public UserDetails loadUserByUsername(String userLoginID) throws UsernameNotFoundException {
        try{
            UserDetails userDetails = null;
            Optional<MemberLogin> optionalUserEntity = Optional.ofNullable(memberService.findByUserLoginID(userLoginID));

            if (optionalUserEntity.isPresent()) {
                MemberLogin entity = optionalUserEntity.get();

                userDetails = User.withUsername(entity.getUser_name())
                        .password(entity.getPassword())
                        .roles("ADMIN")
                        .build();
            }

            return userDetails;
        }catch (Exception e){
            log.info(e.getMessage(), e);
        }

        return null;

    }


}
