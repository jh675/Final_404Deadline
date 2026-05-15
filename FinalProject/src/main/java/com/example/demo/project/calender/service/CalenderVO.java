package com.example.demo.project.calender.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CalenderVO {
	private Integer id;
	private Integer memId;
	private String calText;
	private String place;
	private String typeCd;
	private String sharingCd;
	private String stateCd;
	private String colorTagCd;
	private String keyword;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date calStart;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date calEnd;
	private Date createdOn;
	private Date updatedOn;
}
