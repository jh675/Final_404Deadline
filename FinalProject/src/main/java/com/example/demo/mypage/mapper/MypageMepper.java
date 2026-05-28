package com.example.demo.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.login.service.UserVO;

@Mapper
public interface MypageMepper {
 int updateUser(UserVO vo);
}
