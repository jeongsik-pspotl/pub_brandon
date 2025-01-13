package com.pspotl.sidebranden.manager.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


@Slf4j
@Component
public class AuthorizationExtractor {

    public static final String AUTHORIZATION = "Authorization";

    public String extract(HttpServletRequest request, String type) {

        Enumeration<String> headers = request.getHeaders("Cookie");
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            Cookie[] cookies = request.getCookies();
            String cookieValue = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        cookieValue = cookie.getValue();
                        return cookieValue;
                    }
                }
            }
        }

        return Strings.EMPTY;
    }

}
