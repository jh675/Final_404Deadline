package com.example.demo.company.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.service.UserManageVO;
import com.example.demo.util.subCode.service.SubcodeService;
import com.example.demo.util.subCode.service.SubcodeVO;

@Controller
@RequestMapping("/admin")
public class CompanyController {

	@Autowired
	CompanyService companyService;
	@Autowired
	SubcodeService subCodeService;

	@GetMapping("/company/list")
	public String companylist(Model model, @ModelAttribute("company") CompanyVO company) {

		List<SubcodeVO> activeCodeList = subCodeService.getSubCodeList("00ACTIVE");
		model.addAttribute("activeCodeList", activeCodeList);
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
	public String companyinsert(CompanyVO company, UserManageVO user) {
	    
	    // 기업 등록 여부 확인
	    if(companyService.selectOne(company.getBizNo()) == null) {
	        
	        // 등록 : 기업 등록 + 관리자 계정 동시 생성
	        companyService.insertCompanyWithAdmin(company, user);
	        
	    } else {
	        
	        // 기업 정보 및 관리자 이메일 수정
	        companyService.updateCompanyWithAdmin(company, user);
	    }
	    
	    return "redirect:/admin/company/list";
	}
	

}
