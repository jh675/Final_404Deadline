package com.example.demo.project.wiki.service;

import java.util.List;

public interface WikiService {
	WikiContentVO selectWikiContentLastVerByTitle(Long id,String title);
	WikiContentVO selectWikiContentLastVerById(Long id);
	WikiContentVO selectWikiContentByPageIdAndVersion(Long pageId, Long version);
	List<WikiContentVO> selectWikiHistoryByPageId(Long pageId);
	Long insertWikiContent(WikiContentVO wikiContentVO);
	Long updateWikiContent(WikiContentVO wikiContentVO);
	Long deleteWikiContent(Long id);
	List<WikiPageVO> selectWikiPageForDate(Long id);
	List<WikiPageVO> selectWikiPageListGroupParent(Long id);
	Long nameCheck(Long id,String name);
	List<WikiPageVO> selectWikiPageForTree(Long prjId, Long id);
	List<WikiPageVO> selectWikiIndexPages(Long projectId);
	List<WikiPageVO> getTitleTree(Long projectId);
	List<WikiDateGroupVO> getDateGroups(Long projectId);
	Long insertWikiPage(String title, long prjId, Long parentId);
	Long updateWikiPageParent(Long pageId, Long parentId);
}
