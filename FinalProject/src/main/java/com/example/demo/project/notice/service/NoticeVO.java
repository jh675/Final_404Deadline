package com.example.demo.project.notice.service;

import java.util.Date;

import lombok.Data;
@Data
public class NoticeVO {
	private Integer id;
	private Integer prjId;
	private String title;
	private String description;
	private Integer memId;
	private Date createdOn;
	private String isFixed;
}
