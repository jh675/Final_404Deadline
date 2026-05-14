package com.example.demo.project.issue.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.issue.mapper.IssueMapper;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
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

	@Override
	public List<IssueOutputVO> selectChildIssueList(Long id) {
		return mapper.selectChildIssueList(id);
	}

	@Override
	public long countChildIssues(Long id){
		if (id == null) {
			return 0L;
		}
		return mapper.countChildIssues(id);
	}

	@Override
	public Map<Long, String> getParentIssue(Long id) {
		// TODO Auto-generated method stub
		return mapper.getParentIssue(id);
	}
	
	@Override
	public List<CommentOutputVO> getComment(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		List<CommentOutputVO> list = mapper.getComment(id);
		return list != null ? list : Collections.emptyList();
	}

	@Override
	public int insertComment(CommentInputVO vo) {
		return mapper.insertComment(vo);
	}

	@Override
	public List<Map<Long, String>> getIssueIds() {
		// TODO Auto-generated method stub
		return mapper.getIssueIds();
	}

}
