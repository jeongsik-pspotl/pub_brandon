package com.pspotl.sidebranden.manager.filter;


import com.pspotl.sidebranden.manager.enums.SessionKeyContents;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = {"/manager/*","/builder/*"})
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
         // log.info("[LoginFilter] URI={}, URL={}", requestURI, httpRequest.getRequestURL());
        HttpSession session = httpRequest.getSession();

        if("/manager/member/login".contains(requestURI) || "/manager/branchSetting/builderLoginCheck".contains(requestURI)
                || requestURI.contains("/builder/build/history/CheckAuthPopup/") || "/manager/member/qrcodeAuthCheckDetail".contains(requestURI)
                || "/manager/account/signUp/resetPassword".contains(requestURI) || "/manager/account/signUp/checkUserId".contains(requestURI)
                || "/manager/member/search/checkUserId".contains(requestURI) || "/manager/account/signUp/resultEmail".contains(requestURI)
                || "/manager/account/signUp/checkCodeValue".contains(requestURI) || "/manager/account/signUp/readyEmail".contains(requestURI)
                || "/manager/account/resign/validation".contains(requestURI) || "/builder/build/history/plistAndHTMLFileToWas".contains(requestURI)
                || "/manager/account/resign/sendPageUrl".contains(requestURI) || "/manager/account/resign/result".contains(requestURI)
                || "/manager/account/resign/userPhoneNumberCheck".contains(requestURI) || "/api/account/signUp/checkEmail".contains(requestURI)
                || "/manager/member/search/userInfo".contains(requestURI) ) { // /api/account/signUp/checkEmail

            log.info("[LoginFilter] URI={}, URL={}", requestURI, httpRequest.getRequestURL());
            chain.doFilter(request, response);

        }
        else if(session.getAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name()) != null){
            //BuilderLoginSessionData loginData = (BuilderLoginSessionData) session.getAttribute(SessionKeyContents.KEY_BUILDER_LOGIN_DATA.name());
            //log.info("[KEY_BUILDER_LOGIN_DATA] URI={}, sessionId={}, loginData={}", requestURI, session.getId(), loginData.toString());
            chain.doFilter(request, response);

        } else {
            // log.info("[LoginFilter] URI={}, URL={}", requestURI, httpRequest.getRequestURL());
//            httpResponse.sendRedirect("/");
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
