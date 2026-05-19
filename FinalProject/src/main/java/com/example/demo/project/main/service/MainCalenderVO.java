package com.example.demo.project.main.service;

import java.util.Date;

import lombok.Data;

@Data
public class MainCalenderVO {
	private Integer id;
	private Integer memId;
	private Date calDay;
	private String place;
	private String calText;
	private String typeCd;
	private String sharingCd;
	private String stateCd;
	private String colorTagCd;
	private Date calStart;
	private Date calEnd;
	private Date calStartTime;
	private Date calEndTime;
}
