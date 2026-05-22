package com.example.demo.project.boards.service;

import java.util.List;

public interface BoardsService {
    List<BoardsVO> selectAll(BoardsVO boards);

    BoardsVO selectOne(Long id);

    Long delete(Long id);

    Long update(BoardsVO boards);

    Long insert(BoardsVO boards);
}