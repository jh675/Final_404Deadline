package com.example.demo.project.issue.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.issue.mapper.IssueMapper;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;

@Service
public class IssueServiceImpl implements IssueService {

	@Autowired
	IssueMapper mapper;

	@Override
	public List<IssueOutputVO> selectIssueList(IssueInputVO issueVO) {
		// TODO Auto-generated method stub

		return mapper.selectIssueList(issueVO);
	}

	@Override
	public IssueOutputVO selectIssue(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectIssue(id);
	}

	@Override
	public int insertIssue(IssueInputVO issueVO) {
		// TODO Auto-generated method stub
		return mapper.insertIssue(issueVO);
	}

	@Override
	public int updateIssue(IssueInputVO issueVO) {
		// TODO Auto-generated method stub
		return mapper.updateIssue(issueVO);
	}

	@Override
	public int deleteIssue(Long id) {
		// TODO Auto-generated method stub
		return mapper.deleteIssue(id);
	}

}
