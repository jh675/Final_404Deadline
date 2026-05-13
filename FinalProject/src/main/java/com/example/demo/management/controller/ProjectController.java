package com.example.demo.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.management.mapper.ProjectMapper;
import com.example.demo.management.service.ProjectVO;

@Controller
public class ProjectController {
	
	@Autowired
	ProjectMapper projectMapper;
	
	@PostMapping("/management/projectcreate")
	public String projectInsert(ProjectVO vo) {
		projectMapper.projectInsert(vo);	
		return "redirect:/management/project";
	}
}
