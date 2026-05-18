package com.example.demo.login.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.login.mapper.EmailVerifyMapper;
import com.example.demo.login.mapper.LoginMapper;
import com.example.demo.login.service.EmailSendService;
import com.example.demo.login.service.EmailVerifyService;
import com.example.demo.login.service.EmailVerifyVO;
import com.example.demo.login.service.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerifyServiceImpl implements EmailVerifyService {

	private final EmailVerifyMapper verifyMapper;
	private final EmailSendService emailSend;
	private final LoginMapper loginMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserVO verifyUser(UserVO user) {

		verifyMapper.sendVerifyCode(user);
		if ("success".equals(user.getResult())) {
			emailSend.sendVerifyMail(user.getEmail(), user.getVerifyNum());
		}
		return user;
	}

	@Override
	public EmailVerifyVO verifyCode(EmailVerifyVO vo) {
		verifyMapper.verifyCode(vo);
		return vo;
	}
	
	 @Override
	    public String resetPassword(UserVO user) {
	        // 비밀번호 암호화
	        String encodedPassword = passwordEncoder.encode(user.getPassword());
	        user.setPassword(encodedPassword);
	        int result = loginMapper.updatePassword(user);
	        if (result > 0) {
	            return "success";
	        }
	        return "fail";
	    }
}
