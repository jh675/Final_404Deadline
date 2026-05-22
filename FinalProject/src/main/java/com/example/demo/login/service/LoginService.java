package com.example.demo.login.service;

import java.util.List;

import com.example.demo.company.service.CompanyVO;

public interface LoginService {
	List<CompanyVO> searchActiveCompanies(String keyword);
	void updateMcpCd(UserVO vo);
	void updatePassword(UserVO vo, String newPassword);
}
