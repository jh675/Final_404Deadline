package com.example.demo.setting;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, CustomAuthenticationDetails> {

    @Override
    public CustomAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new CustomAuthenticationDetails(context);
    }
}