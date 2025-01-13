package com.pspotl.sidebranden.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class BaseController {

    @Value("${spring.profiles}")
    private String profiles;

    // ROOT URL 리다이렉트 처리
    @GetMapping("/")
    public RedirectView rootRedirect() {

        if(profiles.toLowerCase().equals("prod")){
            return new RedirectView("/index.html");
        }else {
            /// return new RedirectView("/websquare/websquare.html?w2xPath=/login.xml");
            return new RedirectView("/index.html");
        }


    }

}
