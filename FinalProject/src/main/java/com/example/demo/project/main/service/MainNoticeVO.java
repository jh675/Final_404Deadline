package com.example.demo.project.main.service;

import java.util.Date;

import lombok.Data;

@Data
public class MainNoticeVO {
	private Integer id;
	private Integer prjId;
	private Integer memId;
	private String title;
	private String description;
	private Date createdOn;
}
