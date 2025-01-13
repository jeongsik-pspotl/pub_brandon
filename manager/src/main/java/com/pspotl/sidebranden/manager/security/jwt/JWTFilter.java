package com.pspotl.sidebranden.manager.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JWTFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    public JWTFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        logger.info("dofilter jwt : "+jwt);
        if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) {
            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(servletRequest, servletResponse);

    }

    // TODO : 토큰 header key 값 명칭 정의 하기 
    public static String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("");
        if (StringUtils.hasText(token)) {
            if (token.startsWith("Bearer ")) {
                return token.substring(7);  // Bearer token 일 때 처리
            } else {
                return token;
            }
        }
        String jwt = request.getParameter("");
        if (StringUtils.hasText(jwt)) {
            return jwt;
        }
        String edgeToken = request.getHeader("");
        if (StringUtils.hasText(edgeToken)) {
            return edgeToken;
        }
        return null;
    }

}
