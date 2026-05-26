package com.example.demo.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.demo.management.service.ProjectService;
import com.example.demo.project.main.service.MainService;

@Controller
public class MainController {
	
	@Autowired
	MainService mainService;
	@Autowired
	ProjectService projectService;
	
	
	
}

	

