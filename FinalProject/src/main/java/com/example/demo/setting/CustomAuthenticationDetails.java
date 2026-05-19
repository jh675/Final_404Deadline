package com.example.demo.setting;

import jakarta.servlet.http.HttpServletRequest;

public class CustomAuthenticationDetails {

    private final String bizNo;

    public CustomAuthenticationDetails(HttpServletRequest request) {
        this.bizNo = request.getParameter("bizNo");
    }

    public String getBizNo() {
        return bizNo;
    }
}