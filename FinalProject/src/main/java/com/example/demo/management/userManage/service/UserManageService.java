package com.example.demo.management.userManage.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.company.service.CompanyVO;

public interface UserManageService {
	List<UserManageVO> selectAll(UserManageVO userManage);
	int insertUser(UserManageVO vo);
	int updateUser(UserManageVO vo);
	UserManageVO selectOne(Long id);
	List<CompanyVO> selectCompanyList();
}
