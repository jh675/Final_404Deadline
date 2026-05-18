package com.example.demo.project.boards.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.boards.service.BoardsVO;

@Mapper
public interface BoardsMapper {
	
	List<BoardsVO> selectAll(BoardsVO boards);
	BoardsVO selectOne(int id);
	int delete(int id);
	int update(BoardsVO boards);
	int insert(BoardsVO boards);
}