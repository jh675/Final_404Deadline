package com.example.demo.mypage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.login.service.UserVO;
import com.example.demo.mypage.service.MypageIssueVO;
import com.example.demo.mypage.service.MypageVO;

@Mapper
public interface MypageMepper {
 int updateUser(UserVO vo);
 List<MypageVO>selectMyProjectList(Long userId);
 List<MypageIssueVO> selectWeeklyIssueList(List<Long> prjIdList);
}
