package com.example.demo.project.calender.mapper;


import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.calender.service.CalenderVO;

@Mapper
public interface CalenderMapper {
 List<CalenderVO> selectAll(CalenderVO vo);
 CalenderVO selectOne(int id);
 int insert(CalenderVO vo);
 int update(CalenderVO vo);
 int delete(int id);
 
//새 일정 등록 감지용
List<CalenderVO> findByCreatedOnAfter(Date date);

//1시간 전 알림용
List<CalenderVO> findByCalStartBetween(Date start, Date end);
 
}

