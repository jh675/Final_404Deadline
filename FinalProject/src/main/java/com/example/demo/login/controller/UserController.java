package com.example.demo.login.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.login.service.UserVO;

@Controller
public class UserController {
	
	@GetMapping("/")
	public String main() {
		// 시큐리티 컨텍스트 객체를 얻습니다.
		SecurityContext context = SecurityContextHolder.getContext();

		// 인증 객체를 얻습니다.
		Authentication authentication = context.getAuthentication();

		// 로그인한 사용자정보를 가진 객체를 얻습니다.
	    Object principal = authentication.getPrincipal();

	    if(principal instanceof UserVO user) {
//	    	사용자 로그인 한 경우
	        return "mainpage/test";
	    } else {
//	    	사용자가 로그인 하지 않은 경우
	        return "login/home";

	    }


	}
}
