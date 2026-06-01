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
	/** 신규 위키 페이지 + 첫 버전 본문을 한 트랜잭션으로 저장 */
	Long saveNewWiki(String title, long prjId, Long parentId, WikiContentVO content, Long memId);
	/** 위키 수정(본문 신규 버전) */
	void reviseWiki(WikiContentVO content, Long memId);
	Long updateWikiPageParent(Long pageId, Long parentId);
	List<WikiPageVO> getBreadcrumb(Long pageId);
	boolean hasWikiChildren(Long pageId);

	List<WikiLinkSuggestVO> suggestWikiLinks(Long projectId, String q);
}
