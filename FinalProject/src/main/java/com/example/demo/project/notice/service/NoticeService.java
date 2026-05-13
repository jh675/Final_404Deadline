package com.example.demo.project.notice.service;

import java.util.List;

import com.example.demo.project.notice.service.impl.NoticeVO;

public interface NoticeService {
	List<NoticeVO> selectAll(NoticeVO notice);
    NoticeVO selectOne(int id);
    int delete(int id);
    int update(NoticeVO notice);
    int insert(NoticeVO notice);
}
