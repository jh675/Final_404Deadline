package com.example.demo.project.milestone.service.impl;

import java.util.List;

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
		return mapper.selectMilestoneList(id);
	}

	@Override
	public List<MilestoneIssueVO> selectMilestoneIssueList(Long id) {
		return mapper.selectMilestoneIssueList(id);
	}

	@Override
	public List<MilestoneTimelineVO> selectTimelineList(Long id) {
		return null;
	}



	@Override
	public Long insertMilestone(MilestoneVO milestoneVO) {
		return mapper.insertMilestone(milestoneVO);
	}

	@Override
	public Long insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO) {
		return mapper.insertMilestoneIssue(milestoneIssueVO);
	}

	@Override
	public Long insertTimeline(MilestoneTimelineVO timelineVO) {
		return mapper.insertTimeline(timelineVO);
	}

	@Override
	public Long updateTimeline(MilestoneTimelineVO timelineVO) {
		return null;
	}


	@Override
	public Long deleteMilestoneIssue(Long id) {
		return mapper.deleteMilestoneIssue(id);
	}

	@Override
	public Long deleteTimeline(Long id) {
		return null;
	}

	@Override
	public List<IssueSummaryVO> selectMilestoneNotInIssue(@PathVariable("id") Long id) {
		return mapper.selectMilestoneNotInIssue(id);
	}

	@Override
	public Long updateMilestone(MilestoneVO milestoneVO) {
		return mapper.updateMilestone(milestoneVO);
	}

	@Override
	public Long deleteMilestone(Long id) {
		return mapper.deleteMilestone(id);
	}

	@Override
	public Long getAvg(Long id) {
		return mapper.getAvg(id);
	}

}
