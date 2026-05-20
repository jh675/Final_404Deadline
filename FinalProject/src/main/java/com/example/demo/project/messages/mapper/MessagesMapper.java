package com.example.demo.project.messages.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.messages.service.MessagesVO;

@Mapper
public interface MessagesMapper {
	
	List<MessagesVO> selectAll(MessagesVO messages);
	MessagesVO selectOne(int id);
	int delete(int id);
	int update(MessagesVO messages);
	int insert(MessagesVO messages);
}