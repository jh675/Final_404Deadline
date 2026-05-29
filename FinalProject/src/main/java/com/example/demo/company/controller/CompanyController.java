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
import org.springframework.web.bind.annotation.ResponseBody;

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
		return "company/list";
	}

	/** 그리드용 JSON (페이지 인라인 직렬화 오류·응답 중단 방지) */
	@GetMapping("/company/list-data")
	@ResponseBody
	public List<CompanyVO> companyListData(@ModelAttribute CompanyVO company) {
		return companyService.selectAll(company);
	}

	// ⭕ [신규 개설] 오직 등록/수정 모달창에서 들어오는 데이터를 원스톱으로 받아서 처리하는 저장 전용 API 주소입니다.
	@PostMapping("/company/save")
	public String companysave(CompanyVO company, UserManageVO user) {
	    
	    // 기업 등록 여부 확인 (기존 비즈니스 로직 연동 보존)
	    if(companyService.selectOne(company.getBizNo()) == null) {
	        // 등록 : 기업 등록 + 관리자 계정 동시 생성
	        companyService.insertCompanyWithAdmin(company, user);
	    } else {
	        // 수정 : 기업 정보 및 관리자 이메일 수정
	        companyService.updateCompanyWithAdmin(company, user);
	    }
	    
	    // 처리가 완벽히 끝나면 기존 폼 페이지가 아닌, 우리가 작업 중인 모달 게시판 목록 화면으로 리다이렉트합니다.
	    return "redirect:/admin/company/list";
	}
	
	// ⭕ [확인 및 수정] 화면단 fetch 방식에 맞춰 @PostMapping으로 선언합니다.
	@PostMapping("/company/detail")
	@ResponseBody 
	public CompanyVO companyDetail(@RequestParam("bizNo") String bizNo) {
		return companyService.selectOne(bizNo);
	}


	

}
