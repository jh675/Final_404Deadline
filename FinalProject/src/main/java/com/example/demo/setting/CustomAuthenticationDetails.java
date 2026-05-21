package com.example.demo.setting;

import jakarta.servlet.http.HttpServletRequest;

public class CustomAuthenticationDetails {
	// 로그인시 기업번호 파라미터도 추가로 넣기 위한 class
    private final String bizNo;

    public CustomAuthenticationDetails(HttpServletRequest request) {
        this.bizNo = request.getParameter("bizNo");
    }

    public String getBizNo() {
        return bizNo;
    }
}