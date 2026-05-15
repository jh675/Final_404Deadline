package com.example.demo.project.calender.service;


import java.util.List;

public interface CalenderService {
	List<CalenderVO> selectAll(CalenderVO vo);
	 CalenderVO selectOne(int id);
	 int insert(CalenderVO vo);
	 int updete(CalenderVO vo);
	 int delete(int id);
}

