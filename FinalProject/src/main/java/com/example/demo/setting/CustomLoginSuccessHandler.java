package com.example.demo.setting;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.demo.login.service.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
//		List<GrantedAuthority> roles = (List<GrantedAuthority>) authentication.getAuthorities();
//		List<String> strRoles = roles.stream()
//									 .map(e -> e.getAuthority())
//									 .collect(Collectors.toList());
		
		UserVO vo = (UserVO) authentication.getPrincipal();
		List<String> strRoles = vo.getRole();
		
		// session : 클라이언트 브라우저 접속정보를 가지는 객체(최근접속시간, .... 로그인정보, sessionID -> 브라우저 쿠키)
		request.getSession().setAttribute("ROLE", vo.getRole());
		
		if(strRoles.contains("ROLE_ADMIN")) {
			response.sendRedirect("/admin"); // 리다이렉트 주소 변경필요
		} else if(strRoles.contains("ROLE_CADMIN")) {
			response.sendRedirect("/cadmin"); // 리다이렉트 주소 변경필요
		} else {
			response.sendRedirect("/user"); // 리다이렉트 주소 변경필요
		}
//		response.sendRedirect("/");
	}
}
