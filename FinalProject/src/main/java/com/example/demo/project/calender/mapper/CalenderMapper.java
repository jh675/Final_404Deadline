package com.example.demo.project.calender.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.calender.service.CalenderVO;

@Mapper
public interface CalenderMapper {
 List<CalenderVO> selectAll(CalenderVO vo);
 CalenderVO selectOne(int id);
 int insert(CalenderVO vo);
 int updete(CalenderVO vo);
 int delete(int id);
 
}

