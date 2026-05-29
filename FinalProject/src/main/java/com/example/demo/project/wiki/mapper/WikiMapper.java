package com.example.demo.project.wiki.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiPageVO;

@Mapper
public interface WikiMapper {
	WikiContentVO selectWikiContentLastVerByTitle(@Param("id") Long id, @Param("title") String title);
	WikiContentVO selectWikiContentLastVerById(@Param("id") Long id);
	WikiContentVO selectWikiContentByPageIdAndVersion(@Param("pageId") Long pageId, @Param("version") Long version);
	List<WikiContentVO> selectWikiHistoryByPageId(@Param("pageId") Long pageId);
	List<WikiPageVO> selectWikiPageForTree(@Param("prjId") Long prjId, @Param("id") Long id);
	List<WikiPageVO> selectWikiIndexPages(@Param("projectId") Long projectId);

	Long insertWikiContent(WikiContentVO wikiContentVO);
	Long updateWikiContent(WikiContentVO wikiContentVO);
	Long deleteWikiContent(@Param("id") Long id);
	List<WikiPageVO> selectWikiPageForDate(@Param("id") Long id);
	List<WikiPageVO> selectWikiPageListGroupParent(@Param("id") Long id);
	Long nameCheck(@Param("id") Long id, @Param("name") String name);
	Long insertWikiPage(WikiPageVO page);
	Long updateWikiPageParent(WikiPageVO page);
	List<WikiPageVO> selectWikiBreadcrumb(@Param("pageId") Long pageId);
	Long countWikiChildren(@Param("pageId") Long pageId);

	List<String> searchWikiTitles(@Param("projectId") Long projectId, @Param("q") String q);

}
