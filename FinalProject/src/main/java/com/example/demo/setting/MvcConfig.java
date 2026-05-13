package com.example.demo.setting;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login/login");
		registry.addViewController("/home").setViewName("login/home");
		registry.addViewController("/admin").setViewName("mainpage/test");
		registry.addViewController("/cadmin").setViewName("mainpage/test2");
		registry.addViewController("/user").setViewName("mainpage/test3");

	}
	
}