package com.example.demo.project.calender.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.project.calender.mapper.HolidayMapper;
import com.example.demo.project.calender.service.HolidayService;
import com.example.demo.project.calender.service.HolidayVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class HolidayServiceImpl implements HolidayService {
  private final HolidayMapper holidayMapper;
  
  
  @Override
  public List<HolidayVO> getHolidays(int year) {
	  return holidayMapper.getHolidays(year);
  }
}
