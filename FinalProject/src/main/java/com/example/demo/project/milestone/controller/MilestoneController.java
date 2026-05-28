package com.example.demo.project.milestone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class MilestoneController {

	@GetMapping("/project/milestone")
	public String viewMilestone(
			@RequestParam(value = "prjId", required = false) Long prjId,
			HttpSession session,
			Model model) {
		session.setAttribute("currentMenu", "milestone");
		if (prjId == null) {
			prjId = (Long) session.getAttribute("currentProjectId");
		}
		if (prjId == null) {
			return "redirect:/management/project";
		}
		model.addAttribute("currentMenu", "milestone");
		model.addAttribute("prjId", prjId);
		return "project/milestone/timeline";
	}

	/** 통합 UI 미리보기 — 더미 데이터 (API 미연동) */
	@GetMapping("/project/milestone/demo")
	public String viewMilestoneDemo(
			@RequestParam(value = "prjId", required = false) Long prjId,
			Model model) {
		model.addAttribute("currentMenu", "milestone");
		model.addAttribute("prjId", prjId);
		return "project/milestone/timeline-demo";
	}
}
