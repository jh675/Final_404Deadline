package com.example.demo.login.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.login.mapper.UserMapper;
import com.example.demo.login.service.UserService;
import com.example.demo.login.service.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserMapper userMapper;

	@Override
	public UserVO selectOne(String userId) {
		return userMapper.selectOne(userId);
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserVO vo = userMapper.selectOne(userId);

		String role = switch (vo.getAdminCd()) {
		// db에 있는 권한을 security에서 쓰이는 형태의 문구로 변환
		// xml 파일에서 함수를 사용해 이름으로 가져오는 걸로 해둬서 이름으로 표현해야됨
			case "시스템관리자" -> "ROLE_ADMIN";
			case "기업관리자" -> "ROLE_CADMIN";
			default -> "ROLE_USER";
		};

		vo.setRole(List.of(role));
		return vo;
	}
}
