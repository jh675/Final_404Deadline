package com.example.demo.project.milestone.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.project.issue.service.IssueSummaryVO;
import com.example.demo.project.milestone.service.MilestoneIssueVO;
import com.example.demo.project.milestone.service.MilestoneVO;
import com.example.demo.project.milestone.service.MilestoneTimelineVO;


@Mapper
public interface MilestoneMapper {
	List<MilestoneVO> selectMilestoneList(Long id);
	MilestoneVO selectMilestoneById(Long id);
	List<MilestoneIssueVO> selectMilestoneIssueList(Long id);
	MilestoneIssueVO selectMilestoneIssueByIssueId(@Param("issueId") Long issueId);
	Long countTimelineByMilestoneIssueId(@Param("milestoneIssueId") Long milestoneIssueId);
	List<MilestoneTimelineVO> selectTimelineList(Long id);
	Long insertMilestone(MilestoneVO milestoneVO);
	Long insertMilestoneIssue(MilestoneIssueVO milestoneIssueVO);
	Long insertTimeline(MilestoneTimelineVO timelineVO);
	Long updateTimeline(MilestoneTimelineVO timelineVO);
	Long moveIssue(MilestoneIssueVO milestoneIssueVO);
	Long deleteMilestone(Long id);
	Long deleteMilestoneIssue(Long id);
	Long deleteTimeline(Long id);
	List<IssueSummaryVO> selectMilestoneNotInIssue(Long id);
	Long updateMilestone(MilestoneVO milestoneVO);
	Long getAvg(Long id);
	
}
