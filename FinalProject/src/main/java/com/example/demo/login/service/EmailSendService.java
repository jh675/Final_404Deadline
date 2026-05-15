package com.example.demo.login.service;

public interface EmailSendService {
	void sendVerifyMail(String to, String verifyNum);
}
