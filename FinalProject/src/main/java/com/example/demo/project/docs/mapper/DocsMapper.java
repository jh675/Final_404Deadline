package com.example.demo.project.docs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.docs.service.DocsVO;

@Mapper
public interface DocsMapper {

	List<DocsVO> selectAll(DocsVO docs);
	DocsVO selectOne(Long id);
	int delete(Long id);
	void update(DocsVO vo);
	int insert(DocsVO vo);
}
