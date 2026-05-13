package com.example.demo.project.notice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.notice.mapper.NoticeMapper;
import com.example.demo.project.notice.service.NoticeService;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeMapper noticeMapper;

	@Override
	public List<NoticeVO> selectAll(NoticeVO notice) {
		return noticeMapper.selectAll(notice);
	}

	@Override
	public NoticeVO selectOne(int id) {
		return noticeMapper.selectOne(id);
	}

	@Override
	public int delete(int id) {
		return noticeMapper.delete(id);
	}

	@Override
	public int update(NoticeVO notice) {
		return noticeMapper.update(notice);
	}

	@Override
	public int insert(NoticeVO notice) {
		return noticeMapper.insert(notice);
	}
}