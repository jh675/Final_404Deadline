package com.example.demo.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.github.pagehelper.PageInfo;

import jakarta.servlet.http.HttpSession;

@Controller

public class ProjectController {


	@Autowired
	ProjectService projectservice;
	@Autowired
     CompanyService companyService;

	@GetMapping("management/project")
	public String listProject(ProjectVO vo, Model model, CompanyVO cvo,
			@RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum) {
		List<ProjectVO> list = projectservice.listProject(vo);
		
		PageInfo<CompanyVO> pageInfo = (PageInfo<CompanyVO>) companyService.selectAll(cvo, pageNum);
	    List<CompanyVO> Clist = pageInfo.getList();
		
		model.addAttribute("projectinfo", Map.of("list", list != null ? list : List.of()));
		model.addAttribute("companyList", Clist);
		return "management/projectlist";
	}
	
	@GetMapping("/management/projectcreate")
	public String projectCreate(Model model, HttpSession session,ProjectVO vo) {
		UserVO user = (UserVO) session.getAttribute("loginUser");
		if (user != null) {
	        // userId 필드가 String이라면 user.getUserId()를, 
	        // Integer라면 Integer.parseInt(user.getUserId()) 등을 사용하세요.
	        vo.setUserId(user.getId()); 
	        vo.setBizNo(user.getBizNo());
	    }
		List<ProjectVO> list = projectservice.listProject(null);
		model.addAttribute("projectList", list != null ? list : List.of());
		return "management/projectcreate";
	}

	@GetMapping("/management/searchUsers")
	@ResponseBody
	public List<Map<String, Object>> searchUsers(@RequestParam("term") String term, Authentication authentication) {
	    
	    UserVO vo = (UserVO) authentication.getPrincipal();
	    List<UserVO> userList = projectservice.searchUsersByBizNo(vo.getBizNo(), term);

	    // 필요한 정보만 Map에 담아서 리스트로 반환 (UserVO 수정 불필요)
	    return userList.stream().map(user -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", user.getId());
	        map.put("name", user.getName());
	        return map;
	    }).collect(Collectors.toList());
	}
	
	@PostMapping("/management/projectcreate")
	public String projectInsert(ProjectVO vo, 
	                             @RequestParam(value="moduleList", required=false) List<String> moduleList, 
	                             Authentication authentication) {
	    
	    // 1. 인증 객체에서 로그인 유저 정보 가져오기 (가장 확실한 방법)
	    if (authentication != null && authentication.getPrincipal() instanceof UserVO loginUser) {
	        // 프로젝트를 생성하는 사람의 정보 세팅
	        vo.setUserId(loginUser.getId()); 
	        vo.setBizNo(loginUser.getBizNo());
	    }

	    // 2. 서비스 호출 (프로젝트 정보와 모듈 리스트를 함께 넘김)
	    // 기존의 projectservice.projectInsert(vo) 대신 새로운 메서드를 호출합니다.
	    projectservice.insertProjectWithModules(vo, moduleList);
	    
	    return "redirect:/management/project";
	}

}
