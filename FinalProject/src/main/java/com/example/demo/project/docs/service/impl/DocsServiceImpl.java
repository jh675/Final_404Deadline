package com.example.demo.project.docs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.docs.mapper.DocsMapper;
import com.example.demo.project.docs.service.DocsService;
import com.example.demo.project.docs.service.DocsVO;

@Service
public class DocsServiceImpl implements DocsService {
	
	@Autowired
	private DocsMapper docsMapper;

	@Override
	public List<DocsVO> selectAll(DocsVO docs) {
		return docsMapper.selectAll(docs);
	}

	@Override
	public DocsVO selectOne(Long id) {
		return docsMapper.selectOne(id);
	}

	@Override
	public int delete(Long id) {
		return docsMapper.delete(id);
	}

	@Override
	public int update(DocsVO vo) {
		return docsMapper.update(vo);
	}

	@Override
	public int insert(DocsVO vo) {
		return docsMapper.insert(vo);
	}

	
}
