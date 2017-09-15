package com.lyca.api.controller;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * @author Krishna
 *
 */

public class MvcConfig extends WebMvcConfigurerAdapter {

//	@Autowired
//	@Qualifier("responseMessage")
//	private Properties responseMessage;
//	
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//          .addResourceHandler("/resources/**")
//          .addResourceLocations(responseMessage.getProperty("upload.path"));
//          
//    }

}
