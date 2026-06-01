package com.example.demo.project.milestone.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.project.calender.mapper.HolidayMapper;
import com.example.demo.project.calender.service.HolidayVO;
import com.example.demo.project.issue.service.IssueSummaryVO;
import com.example.demo.project.milestone.mapper.MilestoneMapper;
import com.example.demo.project.milestone.service.MilestoneExpectedProgressCalculator;
import com.example.demo.project.milestone.service.MilestoneIssueVO;
import com.example.demo.project.milestone.service.MilestoneService;
import com.example.demo.project.milestone.service.MilestoneSyncException;
import com.example.demo.project.milestone.service.MilestoneTimelineVO;
import com.example.demo.project.milestone.service.MilestoneVO;

@Service
public class MilestoneServiceImpl implements MilestoneService {

	@Autowired
	private MilestoneMapper mapper;

	@Autowired
	private HolidayMapper holidayMapper;
	
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
		return mapper.updateTimeline(timelineVO);
	}


	@Override
	public Long deleteMilestoneIssue(Long id) {
		if (id == null) {
			return null;
		}
		Long count = mapper.countTimelineByMilestoneIssueId(id);
		if (count != null && count > 0) {
			throw new MilestoneSyncException(
					"타임라인이 등록된 이슈는 마일스톤에서 해제할 수 없습니다. "
					+ "마일스톤 화면에서 타임라인을 삭제하거나 다른 마일스톤으로 이동해 주세요.");
		}
		return mapper.deleteMilestoneIssue(id);
	}

	@Override
	public Long deleteTimeline(Long id) {
		return mapper.deleteTimeline(id);
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

	@Override
	public Long getExpectedProgress(Long id) {
		MilestoneVO milestone = mapper.selectMilestoneById(id);
		if (milestone == null || milestone.getStartDate() == null || milestone.getEndDate() == null) {
			return null;
		}

		LocalDate start = milestone.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = milestone.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		List<HolidayVO> holidays = holidayMapper.getHolidaysByYearRange(start.getYear(), end.getYear());
		return MilestoneExpectedProgressCalculator.calculate(
				milestone.getStartDate(),
				milestone.getEndDate(),
				holidays);
	}

	@Override
	public Long moveIssue(MilestoneIssueVO milestoneIssueVO) {
		// TODO Auto-generated method stub
		return mapper.moveIssue(milestoneIssueVO);
	}



}
