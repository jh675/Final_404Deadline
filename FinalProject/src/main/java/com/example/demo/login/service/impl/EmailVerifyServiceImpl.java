package com.example.demo.login.service.impl;

import org.springframework.stereotype.Service;

import com.example.demo.login.mapper.EmailVerifyMapper;
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

	@Override
	public UserVO verifyUser(UserVO user) {
		System.out.println("프로시저 호출 전");
	    System.out.println(user);

	    verifyMapper.sendVerifyCode(user);

	    System.out.println("프로시저 호출 후");
	    System.out.println(user);

		if ("success".equals(user.getResult())) {

			emailSend.sendVerifyMail(user.getEmail(), user.getVerifyNum());
		}

		return user;
	}

	@Override
	public EmailVerifyVO verifyCode(EmailVerifyVO vo) {
		// TODO Auto-generated method stub
		return verifyMapper.verifyCode(vo);
	}
}
