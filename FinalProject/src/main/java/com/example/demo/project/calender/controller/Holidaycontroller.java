package com.example.demo.project.calender.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.project.calender.service.HolidayService;
import com.example.demo.project.calender.service.HolidayVO;

@Controller
public class Holidaycontroller {
	
	@Autowired
	HolidayService holidayService;
	
	// 공휴일 목록 가져오기 
	@GetMapping({"/holiday/list"})
	@ResponseBody
	public List<HolidayVO> getHolidays(@RequestParam("year")int year) {
		return holidayService.getHolidays(year);
	}
	

}
