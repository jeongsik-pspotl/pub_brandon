package com.pspotl.sidebranden.manager.security;

import com.pspotl.sidebranden.manager.security.jwt.TokenData;
import com.pspotl.sidebranden.manager.security.jwt.TokenProvider;
import com.pspotl.sidebranden.manager.security.jwt.TokenValidationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    TokenProvider jwtProvider;

    @Autowired
    UserAuthenticationProvider authenticationProvider;

    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public AuthService(AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider jwtProvider, UserAuthenticationProvider authenticationProvider) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtProvider = jwtProvider;
        this.authenticationProvider = authenticationProvider;
    }

    public TokenData authorize(String userId, String userPwd) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, userPwd);
        log.info(authenticationToken.toString());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String authorities = getAuthorities(authentication);

        return jwtProvider.createToken(userId, authorities);
    }

    public TokenData reIssue(TokenData tokenData) throws Exception {
        String token = "";
        TokenValidationData accessTokenValidation = jwtProvider.validationToken(tokenData.getAccessToken());

        // accessToken 이 아직 유요한 경우 accessToken 으로 token 을 재발급 한다.
        if (accessTokenValidation.isValid()) {
            token = tokenData.getAccessToken();
        }

        // accessToken 이 유효하지 않은 경우 refreshToken 으로 token 을 재발급 한다.
        else {
            TokenValidationData validation = jwtProvider.validationRefreshToken(tokenData.getRefreshToken());
            if (!validation.isValid()) {
                throw validation.getException();
            }

            token = tokenData.getRefreshToken();
        }

        Authentication authentication = jwtProvider.getAuthentication(token);
        String userId = (String) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        return jwtProvider.createToken(userId, authorities);
    }

    private String getAuthorities(Authentication authentication) {
        try{
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }catch (Exception e){
            log.info(e.getMessage(),e);
        }
        return "";
    }

}
