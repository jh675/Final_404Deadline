package com.example.demo.setting;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
class MvcConfig implements WebMvcConfigurer {

	private final PasswordResetInterceptor passwordResetInterceptor;
	
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
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(passwordResetInterceptor)
				.addPathPatterns("/**") // 모든 주소(URL)로 들어오는 요청에 대해 이 인터셉터를 실행하겠다!
				.excludePathPatterns(
					"/login/**",        // 로그인 및 비밀번호 재설정 관련 URL은 제외 
					"/css/**",          // 화면 스타일 정적 리소스 제외
					"/js/**",           // 자바스크립트 정적 리소스 제외
					"/images/**",       // 이미지 정적 리소스 제외
					"/error/**"         // 에러 페이지 제외
				);
	}
}