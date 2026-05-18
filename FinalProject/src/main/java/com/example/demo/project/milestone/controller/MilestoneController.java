package com.example.demo.project.milestone.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.project.milestone.service.MilestoneService;
import com.example.demo.project.milestone.service.MilestoneVO;

@Controller
public class MilestoneController {

	@Autowired
	MilestoneService service;
	
	@GetMapping("/milestone")
	public String viewMilestone(Model model) {
		Long id= 4L;
		List<MilestoneVO> list=service.selectMilestoneList(id);
		model.addAttribute("list",list);
		return "project/milestone/timeline";
	}
}
