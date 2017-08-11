package com.lyca.api.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

/**
 * 
 * @author Krishna
 *
 */
@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages={"com.lyca.api"})// same as @Configuration @EnableAutoConfiguration @ComponentScan
public class SpringBootCRUDApp extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringBootCRUDApp.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootCRUDApp.class, args);
	}
}
