package com.example.demo.login.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.login.service.EmailSendService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailSendImpl implements EmailSendService {

	private final JavaMailSender mailSender;

	@Async
	@Override
	public void sendVerifyMail(String to, String verifyNum) {
		SimpleMailMessage message = new SimpleMailMessage();

		// 받는 사람
		message.setTo(to);

		// 제목
		message.setSubject("[PMS] 이메일 인증 테스트");

		// 내용
		message.setText("인증번호는 [" + verifyNum + "] 입니다.\n" + "3분 안에 입력해주세요.");

		// 발송
		try {
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
