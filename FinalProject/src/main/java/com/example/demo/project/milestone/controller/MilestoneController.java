package com.example.demo.project.milestone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MilestoneController {

	@GetMapping("/milestone")
	public String viewMilestone() {
		return "project/milestone/timeline";
	}
}
