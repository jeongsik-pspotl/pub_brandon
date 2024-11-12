package com.inswave.whive.headquater.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.emailname}")
    private String username;

    @Value("${spring.mail.emailpassword}")
    private String password;

    @Value("${spring.mail.whiveDomain}")
    private String whiveDomaim;

}
