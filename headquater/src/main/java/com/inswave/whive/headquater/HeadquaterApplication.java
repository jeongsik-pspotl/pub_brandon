package com.inswave.whive.headquater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.TimeZone;

@Slf4j
@EnableJpaRepositories(value = {"com.inswave.whive"})
@ComponentScan(value = {"com.inswave.whive"})
@EntityScan(value = {"com.inswave.whive"})
@ServletComponentScan
@SpringBootApplication
@EnableWebSocket
public class HeadquaterApplication extends SpringBootServletInitializer {


	public static void main(String[] args) {
		SpringApplication.run(HeadquaterApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HeadquaterApplication.class);
	}

	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return new ServletContextInitializer() {

			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				LocalDateTime now = LocalDateTime.now();
				log.info(now.toString());
				servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
				SessionCookieConfig sessionCookieConfig=servletContext.getSessionCookieConfig();
				sessionCookieConfig.setHttpOnly(true);
			}
		};

	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
	}
}