package com.example.demo.company.service;

import java.util.List;

import com.example.demo.management.userManage.service.UserManageVO;

public interface CompanyService {
	List<CompanyVO> selectAll(CompanyVO company);
	CompanyVO selectOne(String bizNo);
	int delete(String bizNo);
	int update(CompanyVO company);
	int insert(CompanyVO company);
	
	void insertCompanyWithAdmin(CompanyVO company, UserManageVO user);
	void updateCompanyWithAdmin(CompanyVO company, UserManageVO user);
}
