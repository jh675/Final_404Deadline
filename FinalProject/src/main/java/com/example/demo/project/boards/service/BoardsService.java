package com.example.demo.project.boards.service;

import java.util.List;

public interface BoardsService {
    List<BoardsVO> selectAll(BoardsVO boards);

    BoardsVO selectOne(int id);

    int delete(int id);

    int update(BoardsVO boards);

    int insert(BoardsVO boards);
}