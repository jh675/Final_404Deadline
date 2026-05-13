package com.example.demo.project.calender;

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
	private String statecd;
	private String colorTagCd;
	
	@DateTimeFormat(pattern = "YYYY-MM-dd")
	private Date calStart;
	private Date calEnd;
	private Date createdOn;
	private Date updatedOn;
}
