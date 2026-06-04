package com.example.demo.project.milestone.service;

import java.util.List;

import com.example.demo.project.issue.service.IssueSummaryVO;

public interface MilestoneService {
	List<MilestoneVO> selectMilestoneList(Long id);
	MilestoneVO selectMilestoneById(Long id);
	List<MilestoneIssueVO> selectMilestoneIssueList(Long id);
	List<MilestoneTimelineVO> selectTimelineList(Long id);
	Long insertMilestone(MilestoneVO milestoneVO);
	Long insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO);
	Long insertTimeline(MilestoneTimelineVO timelineVO);
	Long updateTimeline(MilestoneTimelineVO timelineVO);
	Long deleteTimeline(Long id);
	Long deleteMilestoneIssue(Long id);
	Long deleteMilestone(Long id);
	Long moveIssue(MilestoneIssueVO milestoneIssueVO);
	List<IssueSummaryVO> selectMilestoneNotInIssue(Long id);
	Long updateMilestone(MilestoneVO milestoneVO);
	Long getAvg(Long id);
	Long getExpectedProgress(Long id);
}
