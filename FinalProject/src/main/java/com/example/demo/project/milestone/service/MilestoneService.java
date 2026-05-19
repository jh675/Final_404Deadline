package com.example.demo.project.milestone.service;

import java.util.List;
import java.util.Map;

import com.example.demo.project.issue.service.IssueSummaryVO;

public interface MilestoneService {
	List<MilestoneVO> selectMilestoneList(Long id);
	List<MilestoneIssueVO> selectMilestoneIssueList(Long id);
	List<MilestoneTimelineVO> selectTimelineList(Long id);
	Map<Long,String> selectNotExistsIssueList(Long projectId);
	Long insertMilestone(MilestoneVO milestoneVO);
	Long insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO);
	Long insertTimeline(MilestoneTimelineVO timelineVO);
	int updateTimeline(MilestoneTimelineVO timelineVO);
	int deleteMilestone(Long id);
	int deleteMilestoneIssue(Long id);
	int deleteTimeline(Long id);
	List<IssueSummaryVO> selectMilestoneNotInIssue(Long id);
}
