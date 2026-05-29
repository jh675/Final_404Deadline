package com.example.demo.project.notice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.demo.alarm.event.NotificationEvent;
import com.example.demo.management.mapper.ProjectMapper;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.notice.mapper.NoticeMapper;
import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeMapper noticeMapper;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher; // ← 추가

	@Autowired
	private ProjectMapper projectMapper; // ← 추가

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
		
		// ✅ 공지사항 등록 알림
	    ProjectVO project = projectMapper.getprojectid(notice.getPrjId());
	    String prjName = project != null ? project.getPrjName() : "알 수 없음";

	    eventPublisher.publishEvent(new NotificationEvent(
	        this,
	        "새 공지사항이 등록되었습니다: [" + prjName + "] " + notice.getTitle()
	    ));
	    
		return noticeMapper.insert(notice);
	}
}