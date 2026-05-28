package com.example.demo.project.calender.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.project.calender.service.HolidayVO;

@Mapper
public interface HolidayMapper {
  List<HolidayVO> getHolidays(int year);
  List<HolidayVO> getHolidaysByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
  void insertHoliday(HolidayVO holiday);
  void deleteByYear(int year);
  List<HolidayVO> existsByYear(int year);
}
