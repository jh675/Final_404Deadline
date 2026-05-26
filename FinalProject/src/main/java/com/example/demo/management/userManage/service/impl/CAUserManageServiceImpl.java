package com.example.demo.management.userManage.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.management.userManage.mapper.CAUserManageMapper;
import com.example.demo.management.userManage.service.CAUserManageService;
import com.example.demo.management.userManage.service.UserManageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CAUserManageServiceImpl implements CAUserManageService { // 🚨 CA 서비스로 변경
	
	// 🚨 CA 매퍼 주입으로 변경
	private final CAUserManageMapper caUserManageMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public List<UserManageVO> selectAll(UserManageVO userManage) {
		return caUserManageMapper.selectAll(userManage);
	}
	
	@Override
	public int insertUser(UserManageVO vo) {
        String rawPassword = vo.getLogin();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        vo.setPassword(encodedPassword);

        return caUserManageMapper.insertUser(vo);
	}

	@Override
	public int updateUser(UserManageVO vo) {
	    return caUserManageMapper.updateUser(vo);
	}

	@Override
	public UserManageVO selectOne(Long id) {
	    return caUserManageMapper.selectOne(id);
	}
}