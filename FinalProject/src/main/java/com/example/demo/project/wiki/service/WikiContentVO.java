package com.example.demo.project.wiki.service;

import java.util.Date;

import lombok.Data;

@Data
public class WikiContentVO {
	private Long id;
	private Long pageId;
	private Long parentId;
	private Long version;
	private String describe;
	private String content;
	private String title;
	private Date updatedOn;
	private Long memId;
	private String modifierName;
}
