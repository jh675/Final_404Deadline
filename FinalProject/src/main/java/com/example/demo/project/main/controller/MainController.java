package com.example.demo.project.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.management.service.ModulesVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.main.service.MainService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	MainService mainService;
	@Autowired
	ProjectService projectService;
	
	
	
}

	

