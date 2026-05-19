package com.example.demo.management.userManage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.management.userManage.mapper.UserManageMapper;
import com.example.demo.management.userManage.service.UserManageService;
import com.example.demo.management.userManage.service.UserManageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService{
	
	private final UserManageMapper userManageMapper;
	
	@Override
	public List<UserManageVO> selectAll(UserManageVO userManage) {
		return userManageMapper.selectAll(userManage);
	}
}
