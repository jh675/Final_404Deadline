package com.example.demo.login.service;

public interface EmailVerifyService {
	// 유저 계정정보 존재 확인
	UserVO verifyUser(UserVO user);
	
	// 인증코드 일치 확인
	EmailVerifyVO verifyCode(EmailVerifyVO vo);
}
