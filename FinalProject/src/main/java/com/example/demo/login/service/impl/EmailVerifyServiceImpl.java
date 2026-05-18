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

//		long totalStart = System.currentTimeMillis();
		
//		프로시저 실행 시간
//		long procStart = System.currentTimeMillis();
		verifyMapper.sendVerifyCode(user);
//		long procEnd = System.currentTimeMillis();
//		System.out.println("프로시저 실행 시간 : " + (procEnd - procStart) + "ms");
		
//		메일 발송 시간
		if ("success".equals(user.getResult())) {
//			long mailStart = System.currentTimeMillis();
			emailSend.sendVerifyMail(user.getEmail(), user.getVerifyNum());
//			long mailEnd = System.currentTimeMillis();
//			System.out.println("메일 발송 시간 : " + (mailEnd - mailStart) + "ms");
		}
//		long totalEnd = System.currentTimeMillis();
//		System.out.println("전체 처리 시간 : " + (totalEnd - totalStart) + "ms");
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
