package com.example.demo.project.notice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.notice.mapper.NoticeMapper;
import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeMapper noticeMapper;

	@Override
	public List<NoticeVO> selectAll(NoticeVO notice) {
		return noticeMapper.selectAll(notice);
	}

	@Override
	public NoticeVO selectOne(Long id) {
		return noticeMapper.selectOne(id);
	}

	@Override
	public Long delete(Long id) {
		return noticeMapper.delete(id);
	}

	@Override
	public Long update(NoticeVO notice) {
		return noticeMapper.update(notice);
	}

	@Override
	public Long insert(NoticeVO notice) {
		return noticeMapper.insert(notice);
	}
}