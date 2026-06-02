package com.example.demo.project.boards.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.boards.mapper.BoardsMapper;
import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.boards.service.BoardsVO;
import com.example.demo.project.messages.mapper.MessagesMapper;
import com.example.demo.project.messages.service.MessagesVO;

@Service
public class BoardsServiceImpl implements BoardsService {

	@Autowired
	private BoardsMapper boardsMapper;
	
	@Autowired
	private MessagesMapper messagesMapper;

	@Override
	public List<BoardsVO> selectAll(BoardsVO boards) {
		List<BoardsVO> list = boardsMapper.selectAll(boards);
		
		for (BoardsVO board : list) {
			MessagesVO msgSearch = new MessagesVO();
			msgSearch.setBoardId(board.getId());
			
			List<MessagesVO> allMsgs = messagesMapper.selectAll(msgSearch);
			
			// 원글(parent가 없거나 0) 개수 계산
			long topicCount = allMsgs.stream()
									 .filter(m -> m.getFieldparentId() == null || m.getFieldparentId() == 0)
									 .count();
			
			// setTopicsCount로 수정 (VO의 필드명과 일치시킴)
			board.setTopicsCount(topicCount); 
		}
		return list;
	}

	@Override
	public BoardsVO selectOne(Long id) {
		return boardsMapper.selectOne(id);
	}

	@Override
	public Long delete(Long id) {
		return boardsMapper.delete(id);
	}

	@Override
	public Long update(BoardsVO boards) {
		return boardsMapper.update(boards);
	}

	@Override
	public Long insert(BoardsVO boards) {
		return boardsMapper.insert(boards);
	}
}