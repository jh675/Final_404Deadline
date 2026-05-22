package com.example.demo.project.notice.service;

import java.util.List;

public interface NoticeService {
	List<NoticeVO> selectAll(NoticeVO notice);
    NoticeVO selectOne(Long id);
    Long delete(Long id);
    Long update(NoticeVO notice);
    Long insert(NoticeVO notice);
}
