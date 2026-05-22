package com.example.demo.project.wiki.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.wiki.mapper.WikiMapper;
import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiPageVO;
import com.example.demo.project.wiki.service.WikiService;

@Service
public class WikiServiceImpl implements WikiService {

	@Autowired
	WikiMapper mapper;
	
	@Override
	public WikiContentVO selectWikiContentLastVerByTitle(Long id,String title) {
		// TODO Auto-generated method stub
		return mapper.selectWikiContentLastVerByTitle(id,title);
	}

	@Override
	public Long insertWikiContent(WikiContentVO wikiContentVO) {
		mapper.insertWikiContent(wikiContentVO);
		return wikiContentVO.getId();
	}

	@Override
	public Long updateWikiContent(WikiContentVO wikiContentVO) {
		// TODO Auto-generated method stub
		return mapper.updateWikiContent(wikiContentVO);
	}

	@Override
	public Long deleteWikiContent(Long id) {
		// TODO Auto-generated method stub
		return mapper.deleteWikiContent(id);
	}

	@Override
	public List<WikiPageVO> selectWikiPageListGroupDate(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectWikiPageListGroupDate(id);
	}

	@Override
	public List<WikiPageVO> selectWikiPageListGroupParent(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectWikiPageListGroupParent(id);
	}

	@Override
	public Long nameCheck(Long id,String name) {
		// TODO Auto-generated method stub
		return mapper.nameCheck(id,name);
	}

	@Override
	public Long insertWikiPage(String title, long prjId) {
		WikiPageVO page = new WikiPageVO();
		page.setTitle(title);
		page.setPrjId(prjId);
		mapper.insertWikiPage(page);
		return page.getId();
	}

	@Override
	public WikiContentVO selectWikiContentLastVerById(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectWikiContentLastVerById(id);
	}

	
}
