package com.example.demo.project.messages.service;

import java.util.List;

public interface MessagesService {
    List<MessagesVO> selectAll(MessagesVO messages);

    MessagesVO selectOne(int id);

    int delete(int id);

    int update(MessagesVO messages);

    int insert(MessagesVO messages);
}