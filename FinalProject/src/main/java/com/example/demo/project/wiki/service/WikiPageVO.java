package com.example.demo.project.wiki.service;

import lombok.Data;

@Data
public class WikiPageVO {
	private Long id;
	private Long wikiId;
	private String title;
	private Long lastVer;
	private Long prjId;
}
