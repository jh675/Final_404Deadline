package com.example.demo.project.boards.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.boards.service.BoardsVO;

@Mapper
public interface BoardsMapper {
	
	List<BoardsVO> selectAll(BoardsVO boards);
	BoardsVO selectOne(Long id);
	Long delete(Long id);
	Long update(BoardsVO boards);
	Long updateTopics(Long id);
	Long insert(BoardsVO boards);
}