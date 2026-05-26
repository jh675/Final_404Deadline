package com.example.demo.setting;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
    	String errorMessage = "로그인에 실패했습니다. 관리자에게 문의하세요.";

        // 비밀번호가 틀렸거나, 아이디가 없을 때 
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        } 
        // 비활성화 계정 예외 처리
        else if (exception instanceof DisabledException) {
            errorMessage = exception.getMessage(); 
        } 
        // 스프링이 예외를 포장해서 던졌을 때 (LoginServiceImpl에서 던진 예외들이 보통 여기로 옴)
        else if (exception instanceof InternalAuthenticationServiceException) {
            // 포장지 안에 있는 실제 예외 메시지를 꺼내서 사용
            errorMessage = exception.getMessage(); 
        } 

        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
        
        setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);
        
        super.onAuthenticationFailure(request, response, exception);
    }
}