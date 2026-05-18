package com.example.demo.project.boards.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.boards.mapper.BoardsMapper;
import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.boards.service.BoardsVO;

@Service
public class BoardsServiceImpl implements BoardsService {

	@Autowired
	private BoardsMapper boardsMapper;

	@Override
	public List<BoardsVO> selectAll(BoardsVO boards) {
		return boardsMapper.selectAll(boards);
	}

	@Override
	public BoardsVO selectOne(int id) {
		return boardsMapper.selectOne(id);
	}

	@Override
	public int delete(int id) {
		return boardsMapper.delete(id);
	}

	@Override
	public int update(BoardsVO boards) {
		return boardsMapper.update(boards);
	}

	@Override
	public int insert(BoardsVO boards) {
		return boardsMapper.insert(boards);
	}
}