package com.example.demo.project.main.service.impl;

import org.springframework.stereotype.Service;

import com.example.demo.project.main.mapper.MainMapper;
import com.example.demo.project.main.service.MainIssueVO;




@Service
public class MainServiceImpl {
	
	private MainMapper mainMapper;
	
	public int issueBugFinishCount(MainIssueVO vo) {
		return mainMapper.issueBugFinishCount(vo);
	}
	
	public int issueBugOngoingCount(MainIssueVO vo) {
		return mainMapper.issueBugOngoingCount(vo);
	}
	
	public int issueFunctionFinishCount(MainIssueVO vo) {
		return mainMapper.issueFunctionFinishCount(vo);
	}
	
	public int issueFunctionOngoingCount(MainIssueVO vo) {
		return mainMapper.issueFunctionOngoingCount(vo);
	}
	
	public int issueWorkFinishCount(MainIssueVO vo) {
		return mainMapper.issueWorkFinishCount(vo);
	}
	
	public int issueWorkOngoingCount(MainIssueVO vo) {
		return mainMapper.issueWorkOngoingCount(vo);
	}
	
	public int issueImpFinishCount(MainIssueVO vo) {
		return mainMapper.issueImpFinishCount(vo);
	}
	
	public int issueImpOngoingCount(MainIssueVO vo) {
		return mainMapper.issueImpOngoingCount(vo);
	}
}
