package com.example.demo.project.calender.service;

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
	private String calType;
	private String memName;
	
	private String calStart;
	private String  calEnd;
	private String  createdOn;
	private String  updatedOn;
}
