package com.example.demo.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;

@Controller
public class CompanyController {

	@Autowired
	CompanyService companyService;

	@GetMapping("/company/list")
	public String companylist(Model model, @ModelAttribute("company") CompanyVO company) {

		model.addAttribute( "companyList", companyService.selectAll(company));
		return "company/list";
	}

	@GetMapping("/company/form")
	public void register(@ModelAttribute("company") CompanyVO Company, Model model) {

	}
	

}
