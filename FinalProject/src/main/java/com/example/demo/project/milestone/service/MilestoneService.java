package com.example.demo.project.milestone.service;

import java.util.List;
import java.util.Map;

public interface MilestoneService {
	List<MilestoneVO> selectMilestoneList(Long id);
	Map<Long,List<MilestoneTimeline>> selectMilestoneIssueList(Long id);
	List<MilestoneTimeline> selectTimelineList(Long id);
	List<Map<Long,String>> selectNotExistsIssueList(Long projectId);
	int insertMilestone(MilestoneVO milestoneVO);
	int insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO);
	int insertTimeline(MilestoneTimeline timelineVO);
	int updateTimeline(MilestoneTimeline timelineVO);
	int deleteMilestone(Long id);
	int deleteMilestoneIssue(Long id);
	int deleteTimeline(Long id);
}
