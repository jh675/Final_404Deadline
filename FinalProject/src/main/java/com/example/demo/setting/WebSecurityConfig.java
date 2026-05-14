package com.example.demo.setting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/**", "/login/**").permitAll()
				.requestMatchers("/admin/**").hasAnyRole("ADMIN") // 시스템관리자 롤 01ROLE
				.requestMatchers("/cadmin/**").hasAnyRole("CADMIN") // 기업관리자 롤 02ROLE
				.requestMatchers("/user/**").hasAnyRole("USER") // 일반 이용자 롤 03ROLE
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
				.successHandler(successHandler())
			)
			.logout(LogoutConfigurer::permitAll)
//			.csrf(a -> a.disable())
			;
		// @formatter:on

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	AuthenticationSuccessHandler successHandler() {
		return new CustomLoginSuccessHandler();
	}

}
