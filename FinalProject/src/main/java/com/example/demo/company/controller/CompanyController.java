package com.example.demo.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String companyform(
	        @RequestParam(name = "bizNo", required = false) String bizNo,
	        Model model) {

	    // 수정모드
	    if (bizNo != null) {

	        CompanyVO company = companyService.selectOne(bizNo);

	        model.addAttribute("company", company);
	    }

	    // 등록모드면 company 안넣음

	    return "company/form";
	}
	
	@PostMapping("/company/form")
	public String companyinsert(CompanyVO company) {
		 //등록
		if(companyService.selectOne(company.getBizNo()) == null) {
		
			companyService.insert(company);
		
		} else {
		
		//수정
		
			companyService.update(company);
		}
		return "redirect:/company/list";
	}
	

}
