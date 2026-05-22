package com.example.demo.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.service.UserVO;

@Mapper
public interface LoginMapper {
	UserVO selectOne(UserVO vo);
	int updatePassword(UserVO user);
	List<CompanyVO> searchActiveCompanies(String keyword);
	int updateMcpCd(UserVO vo);
	int updateLastLogOn(UserVO vo);
}
