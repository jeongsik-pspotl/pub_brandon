package com.pspotl.sidebranden.builder;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@ServletComponentScan
@SpringBootApplication
public class BuilderApplication extends SpringBootServletInitializer {

	public static void main(String[] args)
	{
		SpringApplication app = new SpringApplication(BuilderApplication.class);
		app.run(args);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BuilderApplication.class);
	}

}
