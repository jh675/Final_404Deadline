package com.example.demo.mypage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.login.service.UserVO;
import com.example.demo.mypage.mapper.MypageMepper;
import com.example.demo.mypage.service.MypageService;

@Service
public class MypageServiceImpl implements MypageService {
	
	@Autowired
	MypageMepper mypageMepper;

	
	@Override
	public int updateUser(UserVO vo) {
		if (vo.getPassword() != null && !vo.getPassword().isEmpty()) {
		}
		return mypageMepper.updateUser(vo);
		
	}

}
