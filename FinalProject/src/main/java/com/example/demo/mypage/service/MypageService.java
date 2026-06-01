package com.example.demo.mypage.service;

import java.util.List;

import com.example.demo.login.service.UserVO;

public interface MypageService {
	int updateUser(UserVO vo);
	List<MypageVO> selectMyProjectList(Long userId);
}
