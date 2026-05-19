package com.example.demo.management.userManage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.service.UserManageVO;

@Mapper
public interface UserManageMapper {
	List<UserManageVO> selectAll(UserManageVO userManage);
	int insertUser(UserManageVO vo);
	int updateUser(UserManageVO vo);
	UserManageVO selectOne(Long id);
	List<CompanyVO> selectCompanyList();
}
