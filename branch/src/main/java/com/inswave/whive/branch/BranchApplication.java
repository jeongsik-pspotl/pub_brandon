package com.inswave.whive.branch;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.inswave.whive"})
@ServletComponentScan
@SpringBootApplication
public class BranchApplication extends SpringBootServletInitializer {

	public static void main(String[] args)
	{
		SpringApplication app = new SpringApplication(BranchApplication.class);
		app.run(args);
//		branchClient.connect();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BranchApplication.class);
	}

}
