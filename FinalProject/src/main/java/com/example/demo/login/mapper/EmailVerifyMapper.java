package com.example.demo.login.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.login.service.EmailVerifyVO;
import com.example.demo.login.service.UserVO;

@Mapper
public interface EmailVerifyMapper {
	// 유저 계정 정보 존재 확인
	void sendVerifyCode(UserVO user);
	
	// 인증 코드 일치 확인
	EmailVerifyVO verifyCode(EmailVerifyVO vo);
}
