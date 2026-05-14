package com.example.demo.login.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.login.service.UserVO;

@Mapper
public interface LoginMapper {
	UserVO selectOne(String loginId);
	
}
