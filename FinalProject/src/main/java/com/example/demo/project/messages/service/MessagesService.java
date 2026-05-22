package com.example.demo.project.messages.service;

import java.util.List;

public interface MessagesService {
    List<MessagesVO> selectAll(MessagesVO messages);

    MessagesVO selectOne(Long id);

    Long delete(Long id);

    Long update(MessagesVO messages);

    Long insert(MessagesVO messages);
}