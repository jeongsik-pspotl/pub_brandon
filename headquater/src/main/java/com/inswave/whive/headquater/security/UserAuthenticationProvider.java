package com.inswave.whive.headquater.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private PasswordEncoder pwdEncoder;

    @Autowired
    public UserAuthenticationProvider(UserDetailService userDetailService, PasswordEncoder pwdEncoder) {
        this.userDetailService = userDetailService;
        this.pwdEncoder = pwdEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName() == null ? "" : authentication.getName();
        String userPwd = (String) authentication.getCredentials();


        UserDetails accountContext = userDetailService.loadUserByUsername(userName);
        if (!pwdEncoder.matches(userPwd, accountContext.getPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        return new UserAuthentication(accountContext.getUsername(), null, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
