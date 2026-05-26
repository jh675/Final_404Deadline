package com.example.demo.project.wiki.service;

import java.util.List;

import lombok.Data;

@Data
public class WikiDateGroupVO {
	private String groupDate;
	private List<WikiPageVO> pages;
}
