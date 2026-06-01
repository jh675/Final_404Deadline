package com.example.demo.project.calender.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.project.calender.service.CalenderVO;

@Mapper
public interface CalenderMapper {
	List<CalenderVO> selectAll(CalenderVO vo);

	List<CalenderVO> getList(CalenderVO vo);

	CalenderVO selectOne(int id);

	int insert(CalenderVO vo);

	int update(CalenderVO vo);

	int delete(int id, int memId);

//새 일정 등록 감지용
	List<CalenderVO> findByCreatedOnAfter(Date date);

//1시간 전 알림용
	List<CalenderVO> findByCalStartBetween(@Param("start") Date start, @Param("end") Date end);
	
	String findBizNoByMemId(Long memId);
	List<String> findUsernamesByBizNo(String bizNo);
}
