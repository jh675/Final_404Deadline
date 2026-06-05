package com.example.demo.project.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.main.mapper.MainMapper;
import com.example.demo.project.main.service.IssueCountVO;
import com.example.demo.project.main.service.MainService;
import com.example.demo.project.notice.service.NoticeVO;




@Service
public class MainServiceImpl implements MainService {
	
	@Autowired
	MainMapper mainMapper;
	
	@Override
	public IssueCountVO issueCount(IssueCountVO vo) {
		return mainMapper.issueCount(vo);
	}
	

	
	@Override
	public List<NoticeVO> selectNotice(NoticeVO nvo) {
		return mainMapper.selectNotice(nvo);
	}
	
	@Override
	public List<GroupDetailVO> selectGroupMemberCount(GroupDetailVO gmvo){
		return mainMapper.selectgroup(gmvo);
	}
}
