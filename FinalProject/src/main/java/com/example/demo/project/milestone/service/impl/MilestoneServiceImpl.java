package com.example.demo.project.milestone.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.milestone.mapper.MilestoneMapper;
import com.example.demo.project.milestone.service.MilestoneIssueVO;
import com.example.demo.project.milestone.service.MilestoneService;
import com.example.demo.project.milestone.service.MilestoneTimeline;
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
	public Map<Long,List<MilestoneTimeline>> selectMilestoneIssueList(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectMilestoneIssueList(id);
	}

	@Override
	public List<MilestoneTimeline> selectTimelineList(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<Long,String>> selectNotExistsIssueList(Long projectId) {
		// TODO Auto-generated method stub
		return mapper.selectNotExistsIssueList(projectId);
	}

	@Override
	public int insertMilestone(MilestoneVO milestoneVO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertTimeline(MilestoneTimeline timelineVO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateTimeline(MilestoneTimeline timelineVO) {
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

}
