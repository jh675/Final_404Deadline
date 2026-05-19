package com.example.demo.project.milestone.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.project.issue.service.IssueSummaryVO;
import com.example.demo.project.milestone.mapper.MilestoneMapper;
import com.example.demo.project.milestone.service.MilestoneIssueVO;
import com.example.demo.project.milestone.service.MilestoneService;
import com.example.demo.project.milestone.service.MilestoneTimelineVO;
import com.example.demo.project.milestone.service.MilestoneVO;

@Service
public class MilestoneServiceImpl implements MilestoneService {

	@Autowired
	private MilestoneMapper mapper;
	
	@Override
	public List<MilestoneVO> selectMilestoneList(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectMilestoneList(id);
	}

	@Override
	public List<MilestoneIssueVO> selectMilestoneIssueList(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectMilestoneIssueList(id);
	}

	@Override
	public List<MilestoneTimelineVO> selectTimelineList(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long,String> selectNotExistsIssueList(Long projectId) {
		// TODO Auto-generated method stub
		return mapper.selectNotExistsIssueList(projectId);
	}

	@Override
	public Long insertMilestone(MilestoneVO milestoneVO) {
		// TODO Auto-generated method stub
		return mapper.insertMilestone(milestoneVO);
	}

	@Override
	public Long insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO) {
		// TODO Auto-generated method stub
		return mapper.insertMilestoneIssue(milestoneIssueVO);
	}

	@Override
	public Long insertTimeline(MilestoneTimelineVO timelineVO) {
		// TODO Auto-generated method stub
		return mapper.insertTimeline(timelineVO);
	}

	@Override
	public int updateTimeline(MilestoneTimelineVO timelineVO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteMilestone(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteMilestoneIssue(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteTimeline(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<IssueSummaryVO> selectMilestoneNotInIssue(@PathVariable("id") Long id) {
		return mapper.selectMilestoneNotInIssue(id);
	}

}
