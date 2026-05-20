package com.example.demo.project.milestone.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.project.issue.service.IssueSummaryVO;
import com.example.demo.project.milestone.service.MilestoneIssueVO;
import com.example.demo.project.milestone.service.MilestoneService;
import com.example.demo.project.milestone.service.MilestoneTimelineVO;
import com.example.demo.project.milestone.service.MilestoneVO;

@RestController
@RequestMapping("/api/milestone")
public class MilestoneRestController {

	@Autowired
	private MilestoneService service;

	@GetMapping("/list/{id}") 
	public List<MilestoneIssueVO> getMilestoneIssueList(@PathVariable("id") Long id) {
		List<MilestoneIssueVO>result= service.selectMilestoneIssueList(id);
		return result;
	}
	
	@GetMapping("/{id}")
	public List<MilestoneVO> selectMilestoneList(@PathVariable("id") Long id) {
		return service.selectMilestoneList(id);
	}
	@PostMapping("/milestone")
	public Long insertMilestone(@RequestBody MilestoneVO milestoneVO) {
		try {
//			System.out.println(milestoneVO);
			return service.insertMilestone(milestoneVO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	@PostMapping("/issue")
	public Long insertMilestoneIssue(@RequestBody MilestoneIssueVO milestoneIssueVO) {
		try {
			return service.insertMilestoneIssue(milestoneIssueVO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	@PostMapping("/timeline")
	public Long insertTimeline(@RequestBody MilestoneTimelineVO timelineVO) {
		try {
			return service.insertTimeline(timelineVO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	@GetMapping("/notinissue/{id}")
	public List<IssueSummaryVO> selectMilestoneNotInIssue(@PathVariable("id") Long id) {
		return service.selectMilestoneNotInIssue(id);
	}
	@DeleteMapping("/issue/{id}")
	public Long deleteMilestoneIssue(@PathVariable("id")Long id) {
		return service.deleteMilestoneIssue(id);
	}
	@PutMapping("/milestone")
	public Long updateMilestone(@RequestBody MilestoneVO milestoneVO) {
		try {
			return service.updateMilestone(milestoneVO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	@DeleteMapping("/milestone/{id}")
	public Long deleteMilestone(@PathVariable("id")Long id) {
		return service.deleteMilestone(id);
	}
	@GetMapping("/avg/{id}")
	public Long getAvg(@PathVariable("id") Long id) {
		return service.getAvg(id);
	}
}
