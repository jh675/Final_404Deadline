package com.example.demo.login.service;

import java.util.List;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.service.UserManageVO;

public interface LoginService {
	List<CompanyVO> searchActiveCompanies(String keyword);
	void updateMcpCd(UserVO vo);
	void updatePassword(UserVO vo, String newPassword);
	void updateLastLogOn(UserVO vo);
	void requestCompanyRegistration(CompanyVO company, UserManageVO user);
	
}
