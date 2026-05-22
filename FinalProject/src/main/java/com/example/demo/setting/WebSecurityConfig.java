package com.example.demo.setting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class WebSecurityConfig {

	private final CustomAuthenticationDetailsSource detailsSource;
	private final UserDetailsService userDetailsService;
	private final CustomLoginSuccessHandler customLoginSuccessHandler;
	private final CustomAuthFailureHandler customAuthFailureHandler;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/**", "/login/**", "/css/**", "/js/**").permitAll()
				.requestMatchers("/admin/**").hasAnyRole("ADMIN") // 시스템관리자 01ROLE
				.requestMatchers("/cadmin/**").hasAnyRole("CADMIN") // 기업관리자 02ROLE
				.requestMatchers("/user/**").hasAnyRole("USER") // 일반 이용자 03ROLE
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.authenticationDetailsSource(detailsSource)
				.failureHandler(customAuthFailureHandler)
				.successHandler(customLoginSuccessHandler)
				.permitAll()
			)
			.logout(LogoutConfigurer::permitAll)
//			 .csrf(a -> a.disable())
			
			// 자동 로그인 설정 추가
			.rememberMe((remember) -> remember
							.key("my-secret-key") // 쿠키 암호화에 사용될 고유 키 
							.rememberMeParameter("remember-me") // HTML의 체크박스 name 속성과 일치해야 함
							.tokenValiditySeconds(86400 * 30) // 유지 시간 (초 단위, 예: 30일)
							.userDetailsService(userDetailsService) // 사용자 조회를 위한 서비스 세팅
			);
		// @formatter:on

		return http.build();
	}

}
