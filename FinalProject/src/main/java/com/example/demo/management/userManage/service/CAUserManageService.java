package com.example.demo.management.userManage.service;

import java.util.List;
import java.util.Map;

public interface CAUserManageService {
	List<UserManageVO> selectAll(UserManageVO userManage);
	int insertUser(UserManageVO vo);
	int updateUser(UserManageVO vo);
	UserManageVO selectOne(Long id);
	int bulkUpdateUsers(Map<String, Object> payload);
}