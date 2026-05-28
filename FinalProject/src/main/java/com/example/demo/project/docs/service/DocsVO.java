package com.example.demo.project.docs.service;

import java.util.Date;

import lombok.Data;

@Data
public class DocsVO {
	private Long id;
	private Long prjId;
	private Long memId;
	private String title;
	private String description;
	private Date createdOn;
	private int fileCount;
}
