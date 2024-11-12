package com.inswave.whive.branch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin
public class BranchBaseController {
    @GetMapping("/")
    public RedirectView rootRedirect() {

        return new RedirectView("/index.html");

    }
}
