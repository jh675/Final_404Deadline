package com.example.demo.project.docs.service;

import java.util.List;

public interface DocsService {

	List<DocsVO> selectAll(DocsVO docs);
	DocsVO selectOne(Long id);
	int delete(Long id);
	void update(DocsVO vo);
	int insert(DocsVO vo);
	
}
