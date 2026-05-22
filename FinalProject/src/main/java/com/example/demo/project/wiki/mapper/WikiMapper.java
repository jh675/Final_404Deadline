package com.example.demo.project.wiki.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiPageVO;

@Mapper
public interface WikiMapper {
	WikiContentVO selectWikiContentLastVerByTitle (Long id,String title);
	WikiContentVO selectWikiContentLastVerById(Long id);

	Long insertWikiContent(WikiContentVO wikiContentVO);
	Long updateWikiContent(WikiContentVO wikiContentVO);
	Long deleteWikiContent(Long id);
	List<WikiPageVO> selectWikiPageListGroupDate(Long id);
	List<WikiPageVO> selectWikiPageListGroupParent(Long id);
	Long nameCheck(Long id,String name);
	Long insertWikiPage(WikiPageVO page);

}
