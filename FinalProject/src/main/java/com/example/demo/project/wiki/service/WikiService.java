package com.example.demo.project.wiki.service;

import java.util.List;

public interface WikiService {
	WikiContentVO selectWikiContentLastVerByTitle(Long id,String title);
	WikiContentVO selectWikiContentLastVerById(Long id);
	Long insertWikiContent(WikiContentVO wikiContentVO);
	Long updateWikiContent(WikiContentVO wikiContentVO);
	Long deleteWikiContent(Long id);
	List<WikiPageVO> selectWikiPageListGroupDate(Long id);
	List<WikiPageVO> selectWikiPageListGroupParent(Long id);
	Long nameCheck(Long id,String name);
	List<WikiPageVO> selectWikiPageForTree(Long prjId, Long id);
	Long insertWikiPage(String title, long prjId);
}
