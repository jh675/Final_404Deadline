package com.example.demo.project.messages.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.boards.mapper.BoardsMapper;
import com.example.demo.project.messages.mapper.MessagesMapper;
import com.example.demo.project.messages.service.MessagesService;
import com.example.demo.project.messages.service.MessagesVO;

@Service
public class MessagesServiceImpl implements MessagesService {

	@Autowired
	private MessagesMapper messagesMapper;
	
	@Autowired
	private BoardsMapper boardsMapper;

	@Override
	public List<MessagesVO> selectAll(MessagesVO messages) {
		return messagesMapper.selectAll(messages);
	}

	@Override
	public MessagesVO selectOne(int id) {
		return messagesMapper.selectOne(id);
	}

	@Override
	public int delete(int id) {
		return messagesMapper.delete(id);
	}

	@Override
	public int update(MessagesVO messages) {
		return messagesMapper.update(messages);
	}

	@Override
	public int insert(MessagesVO messages) {
		
		//토픽수 증가
		boardsMapper.updateTopics(messages.getBoardId());
		
		//메세지 등록
		return messagesMapper.insert(messages);
	}
}