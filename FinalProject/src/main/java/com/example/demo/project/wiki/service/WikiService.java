package com.example.demo.project.wiki.service;

import java.util.List;

public interface WikiService {
	WikiContentVO selectWikiContentLastVer(Long id);
	Long insertWikiContent(WikiContentVO wikiContentVO);
	Long updateWikiContent(WikiContentVO wikiContentVO);
	Long deleteWikiContent(Long id);
	List<WikiPageVO> selectWikiPageListGroupDate(Long id);
	List<WikiPageVO> selectWikiPageListGroupParent(Long id);

}
