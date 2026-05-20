package com.example.demo.project.calender.service;


import java.util.List;

public interface CalenderService {
	List<CalenderVO> selectAll(CalenderVO vo);
	List<CalenderVO> getList(CalenderVO vo);
	 CalenderVO selectOne(int id);
	 int insert(CalenderVO vo);
	 int update(CalenderVO vo);
	 int delete(int id);
}

