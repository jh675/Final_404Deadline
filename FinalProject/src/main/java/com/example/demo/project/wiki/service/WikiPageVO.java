package com.example.demo.project.wiki.service;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class WikiPageVO {
	public Long id;
	public Long wikiId;
	public String title;
	public Long lastVer;
	public Long prjId;
	public Long parentId;
	public String describe;
	public Date updatedOn;
	public List<WikiPageVO> children;
}
