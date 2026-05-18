package com.example.demo.setting;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class MvcConfig implements WebMvcConfigurer {

//	빠르게 url 매핑해보는 설정, /login url이 들어오면 template/login/login.html로 연결된다
//	controller가 url을 사용한다면 controller 우선, 여기 설정은 무시됨
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login/login");
		registry.addViewController("/home").setViewName("login/info");
		registry.addViewController("/admin").setViewName("mainpage/test");
		registry.addViewController("/cadmin").setViewName("mainpage/test2");
		registry.addViewController("/user").setViewName("mainpage/test3");

	}
	
}