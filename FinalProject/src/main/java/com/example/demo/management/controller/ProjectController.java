package com.example.demo.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProjectController {


	@Autowired
	ProjectService projectservice;

	@GetMapping("management/project")
	public String listProject(ProjectVO vo, Model model) {
		List<ProjectVO> list = projectservice.listProject(vo);
		model.addAttribute("projectinfo", Map.of("list", list != null ? list : List.of()));
		return "management/projectlist";
	}
	
	@GetMapping("/management/projectcreate")
	public String projectCreate(Model model, HttpSession session) {
		UserVO user = (UserVO) session.getAttribute("loginUser");
		List<ProjectVO> list = projectservice.listProject(null);
		model.addAttribute("projectList", list != null ? list : List.of());
		return "management/projectcreate";
	}

	@PostMapping("/management/projectcreate")
	public String projectInsert(ProjectVO vo) {
		projectservice.projectInsert(vo);
		return "redirect:/management/project";
	}

}
