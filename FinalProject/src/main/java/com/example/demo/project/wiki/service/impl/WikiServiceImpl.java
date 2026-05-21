package com.example.demo.project.wiki.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.project.wiki.mapper.WikiMapper;
import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiPageVO;
import com.example.demo.project.wiki.service.WikiService;

@Service
public class WikiServiceImpl implements WikiService {

	@Autowired
	WikiMapper mapper;
	
	@Override
	public WikiContentVO selectWikiContentLastVer(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectWikiContent(id);
	}

	@Override
	public Long insertWikiContent(WikiContentVO wikiContentVO) {
		// TODO Auto-generated method stub
		return insertWikiContent(wikiContentVO);
	}

	@Override
	public Long updateWikiContent(WikiContentVO wikiContentVO) {
		// TODO Auto-generated method stub
		return updateWikiContent(wikiContentVO);
	}

	@Override
	public Long deleteWikiContent(Long id) {
		// TODO Auto-generated method stub
		return deleteWikiContent(id);
	}

	@Override
	public List<WikiPageVO> selectWikiPageListGroupDate(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectWikiPageListGroupDate(id);
	}

	@Override
	public List<WikiPageVO> selectWikiPageListGroupParent(Long id) {
		// TODO Auto-generated method stub
		return selectWikiPageListGroupParent(id);
	}



	
	
}
