package com.example.demo.project.calender.service;

import lombok.Data;

@Data
public class HolidayVO {
  private Integer id;
  private int year;
  private int month;
  private int day;
  private String holidayName;
}
