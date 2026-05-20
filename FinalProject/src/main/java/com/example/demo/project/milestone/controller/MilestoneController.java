package com.example.demo.project.milestone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MilestoneController {

	@GetMapping("/milestone")
	public String viewMilestone(
			@RequestParam(value = "prjId", required = false) Long prjId,
			Model model) {
		model.addAttribute("prjId", prjId);
		return "project/milestone/timeline";
	}
}
