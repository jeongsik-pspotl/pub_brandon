package com.pspotl.sidebranden.manager.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class CertificationInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String session_yn = request.getHeader("session_yn");

        String requestURI = request.getRequestURI();

            if (requestURI.equals("/") && (session_yn == null || session_yn.equals("N"))) {

                return true;
            } else {
                session.setMaxInactiveInterval(30 * 60);
                return true;
            }



    }

}
