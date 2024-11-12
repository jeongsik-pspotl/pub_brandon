package com.inswave.whive.headquater.security;

import com.inswave.whive.headquater.enums.SessionKeyContents;
import com.inswave.whive.headquater.security.jwt.TokenProvider;
import com.inswave.whive.headquater.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

    @Autowired
    Common common;

    private TokenProvider jwtTokenProvider;

    // Jwt Provier 주입
    public JwtAuthenticationFilter( TokenProvider jwtTokenProvider ) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String token = jwtTokenProvider.resolveToken( (HttpServletRequest) request );
        // log.info("JwtAuthenticationFilter dofilter : {}", token);
        // TODO : builder session 방식으로 계속 유지 할지 고민...
        HttpSession session = httpRequest.getSession();

        // log.info("[LoginFilter] URI={}, URL={}", requestURI, httpRequest.getRequestURL());


//        if("/manager/member/login".contains(requestURI) || "/manager/branchSetting/builderLoginCheck".contains(requestURI)
//                || requestURI.contains("/builder/build/history/CheckAuthPopup/") || "/manager/member/qrcodeAuthCheckDetail".contains(requestURI)
//                || "/manager/account/signUp/resetPassword".contains(requestURI) || "/manager/account/signUp/checkUserId".contains(requestURI)
//                || "/manager/member/search/checkUserId".contains(requestURI) || "/manager/account/signUp/resultEmail".contains(requestURI)
//                || "/manager/account/signUp/checkCodeValue".contains(requestURI) || "/manager/account/signUp/readyEmail".contains(requestURI)
//                || "/manager/account/resign/validation".contains(requestURI) || "/builder/build/history/plistAndHTMLFileToWas".contains(requestURI)
//                || "/manager/account/resign/sendPageUrl".contains(requestURI) || "/manager/account/resign/result".contains(requestURI)
//                || "/manager/account/resign/userPhoneNumberCheck".contains(requestURI)) {
//
//            log.info("[LoginFilter] URI={}, URL={}", requestURI, httpRequest.getRequestURL());
//            chain.doFilter(request, response);
//
//        }else

        if( token != null && jwtTokenProvider.validateToken( token ) ) {

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            httpResponse.setHeader("session_yn","Y");

             // chain.doFilter(request, response);
        } else if(session.getAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name()) != null){
                //BuilderLoginSessionData loginData = (BuilderLoginSessionData) session.getAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name());
                //log.info("[KEY_BUILDER_LOGIN_DATA] URI={}, sessionId={}, loginData={}", requestURI, session.getId(), loginData.toString());
                // chain.doFilter(request, response);

        }else {
            httpResponse.setHeader("session_yn","N");
            // httpResponse.sendRedirect("/");

        }

        chain.doFilter(request, response);
        return;


    }
}
