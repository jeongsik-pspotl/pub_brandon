package com.inswave.whive.common.config;

import com.inswave.whive.headquater.enums.SessionKeyContents;
import com.inswave.whive.headquater.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    public CorsFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");

        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();

        if(session.getAttribute(SessionKeyContents.KEY_LOGIN_DATA.name()) != null){
            response.setHeader("session_yn","Y");
        }else {
            response.setHeader("session_yn","N");
        }

        //response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Access-Control-Allow-Credentials, authorization, X-Frame-Options, X-Ajax-call");
//        response.setHeader("Access-Control-Allow-Headers", "Origin");
        String session_yn = request.getHeader("session_yn");
        // log.info("[CorsFilter] URI={}, URL={} session_yn={}", requestURI, request.getRequestURL(), session_yn);
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // log.info("[CorsFilter] URI={}, URL={} ", requestURI, request.getRequestURL());
            chain.doFilter(req, res);
            return;
        }

//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        }else if(request.getParameter("w2xPath") == null){
//            chain.doFilter(req, res);
//        }else if(request.getParameter("w2xPath").toString().equals("/index.xml") && session.getAttribute(SessionKeyContents.KEY_LOGIN_DATA.name()) == null){
//             response.sendRedirect("/");
//            response.sendRedirect("/websquare/websquare.html?w2xPath=/login.xml");

//        }else {
//
//            chain.doFilter(req, res);
//        }
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}

