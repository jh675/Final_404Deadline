package com.example.demo.login.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.UserVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;
	
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
  	
//	    userVO 꺼내서 쓰는 방법 > 회의록 밑의 개발표준 9번 확인
	    
	}
	
	@GetMapping("/login/companies/search")
	@ResponseBody
	public List<CompanyVO> searchCompanies(@RequestParam("keyword") String keyword) {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return List.of(); // 빈 리스트 반환
	    }
	    return loginService.searchActiveCompanies(keyword);
	}

}
