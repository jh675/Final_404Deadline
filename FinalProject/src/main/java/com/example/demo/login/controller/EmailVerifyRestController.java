package com.example.demo.login.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.login.service.EmailVerifyService;
import com.example.demo.login.service.EmailVerifyVO;
import com.example.demo.login.service.UserVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailVerifyRestController {

	private final EmailVerifyService verifyService;

	@PostMapping("/send")
	public String sendVerifyCode(@RequestBody UserVO user) {

		UserVO resultUser = verifyService.verifyUser(user);

		return resultUser.getResult();
	}

	@PostMapping("/verify")
	public String verifyCode(@RequestBody EmailVerifyVO vo) {

		EmailVerifyVO result = verifyService.verifyCode(vo);

		return result.getResult();
	}
}
