package com.example.demo.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;

@Controller
public class CompanyController {

	@Autowired
	CompanyService companyService;

	@GetMapping("/company/list")
	public String companylist(Model model, @ModelAttribute("company") CompanyVO company,
			@RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum) {

		model.addAttribute("pageInfo", companyService.selectAll(company, pageNum));
		return "company/list";
		
		
	}
	
	@GetMapping("/company/register")
	public void register(@ModelAttribute("company") CompanyVO Company, Model model) {
	
	}

}
