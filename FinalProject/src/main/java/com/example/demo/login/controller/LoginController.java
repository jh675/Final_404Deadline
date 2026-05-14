package com.example.demo.login.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.login.service.UserVO;

@Controller
public class LoginController {
	
	@GetMapping("/")
	public String main(Model model) {
		// 시큐리티 컨텍스트 객체를 얻습니다.
		SecurityContext context = SecurityContextHolder.getContext();

		// 인증 객체를 얻습니다.
		Authentication authentication = context.getAuthentication();

		// 로그인한 사용자정보를 가진 객체를 얻습니다.
	    Object principal = authentication.getPrincipal();
	    
	    if(principal instanceof UserVO vo) {

	        // 권한 목록
	        List<String> roles = vo.getRole();
	        model.addAttribute("user", vo);
	        
	        // 시스템 관리자
	        if(roles.contains("ROLE_ADMIN")) {
	            return "mainpage/test";
	        }

	        // 기업 관리자
	        if(roles.contains("ROLE_CADMIN")) {
	            return "mainpage/test2";
	        }

	        // 일반 사용자
	        if(roles.contains("ROLE_USER")) {
	            return "mainpage/test3";
	        }

	        // 권한이 없는 경우
	        return "error/403";

	    } else {
	        // 비로그인 사용자
	        return "login/info";
	    }
  	
//	    userVO 꺼내서 쓰는 방법 > 참고자료 폴더의 security_userVO 확인
	    
	}
	
	@GetMapping("/admin/test1")
	public String test(Model model) {
		return "login/test2";
	}
}
