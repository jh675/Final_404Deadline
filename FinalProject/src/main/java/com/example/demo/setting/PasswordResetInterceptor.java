package com.example.demo.setting;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.login.service.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PasswordResetInterceptor implements HandlerInterceptor {
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof UserVO) {
            UserVO vo = (UserVO) auth.getPrincipal();
            
            // 사용자의 상태가 01ACTIVE(변경 필요 상태)인데, 요청 URL이 /login/password-reset 계열이 아닐 경우
            String uri = request.getRequestURI();
            if ("01ACTIVE".equals(vo.getMcpCd()) && !uri.startsWith("/login/password-reset") && !uri.startsWith("/css") && !uri.startsWith("/js")) {
                // 무조건 비밀번호 변경 페이지로 튕겨버림
                response.sendRedirect("/login/password-reset");
                return false; 
            }
        }
        return true;
    }
}