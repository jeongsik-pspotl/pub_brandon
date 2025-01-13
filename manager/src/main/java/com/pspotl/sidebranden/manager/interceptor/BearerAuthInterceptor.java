package com.pspotl.sidebranden.manager.interceptor;

import com.pspotl.sidebranden.manager.security.jwt.AuthorizationExtractor;
import com.pspotl.sidebranden.manager.security.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class BearerAuthInterceptor implements HandlerInterceptor {
    private AuthorizationExtractor authExtractor;
    private TokenProvider jwtTokenProvider;

    public BearerAuthInterceptor(AuthorizationExtractor authExtractor, TokenProvider jwtTokenProvider) {
        this.authExtractor = authExtractor;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {

        try {

            String token = authExtractor.extract(request, "Bearer");
            if (StringUtils.isEmpty(token)) {
                return true;
            }

            if (!jwtTokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("유효하지 않은 토큰");
            }

            String name = jwtTokenProvider.getSubject(token);
            request.setAttribute("name", name);

        }catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return true;
    }

}
