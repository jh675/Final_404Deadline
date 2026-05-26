package com.example.demo.project.calender.service;

import java.util.List;

public interface HolidayService {
	List<HolidayVO> getHolidays(int year);
	
	void fetchAndSave(int year);
	void fetchAndRange(int startYear, int endYear);
}
