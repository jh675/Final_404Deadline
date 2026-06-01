package com.example.demo.project.milestone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import com.example.demo.project.milestone.service.MilestoneSyncException;
import com.example.demo.project.milestone.service.MilestoneTimelineVO;
import com.example.demo.project.milestone.service.MilestoneVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/project/milestone/api")
public class MilestoneRestController {

	@Autowired
	private MilestoneService service;

	private Long getCurrentProjectId(HttpSession session) {
		return (Long) session.getAttribute("currentProjectId");
	}

	@GetMapping("/list/{id}") 
	public List<MilestoneIssueVO> getMilestoneIssueList(@PathVariable("id") Long id) {
		List<MilestoneIssueVO>result= service.selectMilestoneIssueList(id);
		return result;
	}
	
	@GetMapping("/{id}")
	public List<MilestoneVO> selectMilestoneList(@PathVariable("id") Long id, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return List.of();
		}
		return service.selectMilestoneList(projectId);
	}
	@PostMapping("/milestone")
	public Long insertMilestone(@RequestBody MilestoneVO milestoneVO, HttpSession session) {
		try {
			Long projectId = getCurrentProjectId(session);
			if (projectId == null) {
				return null;
			}
			milestoneVO.setPrjId(projectId);
//			System.out.println(milestoneVO);
			return service.insertMilestone(milestoneVO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@PostMapping("/issue")
	public Long insertMilestoneIssue(@RequestBody MilestoneIssueVO milestoneIssueVO) {
		try {
			return service.insertMilestoneIssue(milestoneIssueVO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@PostMapping("/timeline")
	public Long insertTimeline(@RequestBody MilestoneTimelineVO timelineVO) {
		try {
			return service.insertTimeline(timelineVO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@GetMapping("/notinissue/{id}")
	public List<IssueSummaryVO> selectMilestoneNotInIssue(@PathVariable("id") Long id, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return List.of();
		}
		return service.selectMilestoneNotInIssue(projectId);
	}
	@DeleteMapping("/issue/{id}")
	public Long deleteMilestoneIssue(@PathVariable("id")Long id) {
		return service.deleteMilestoneIssue(id);
	}

	@ExceptionHandler(MilestoneSyncException.class)
	public ResponseEntity<Map<String, Object>> handleMilestoneSync(MilestoneSyncException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("ok", false, "message", ex.getMessage()));
	}

	@PutMapping("/milestone")
	public Long updateMilestone(@RequestBody MilestoneVO milestoneVO) {
		try {
			return service.updateMilestone(milestoneVO);
		} catch (Exception e) {
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

	@GetMapping("/expected/{id}")
	public Long getExpectedProgress(@PathVariable("id") Long id) {
		return service.getExpectedProgress(id);
	}
	
	@PutMapping("/issue/move")
	public Long moveIssue(@RequestBody MilestoneIssueVO milestoneIssueVO) {
		System.out.println(milestoneIssueVO);
		return service.moveIssue(milestoneIssueVO);
	}
	@DeleteMapping("/timeline/{id}")
	public Long deleteTimeline(@PathVariable("id") Long id) {
		return service.deleteTimeline(id);
	}
	@PutMapping("/timeline")
	public Long updateTimeline(@RequestBody MilestoneTimelineVO timelineVO) {
		return service.updateTimeline(timelineVO);
	}
}
