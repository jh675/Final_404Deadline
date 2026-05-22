package com.example.demo.project.notice.service;

import java.util.Date;

import lombok.Data;
@Data
public class NoticeVO {
	private Long id;
	private Long prjId;
	private String title;
	private String description;
	private Long memId;
	private Date createdOn;
	private String isFixed;
	
}
