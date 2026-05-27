package com.example.demo.project.ganttchart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;

import jakarta.servlet.http.HttpSession;

@Controller
public class Ganttchartconttoller {

	@Autowired
	IssueService issueService;
	
	// 로그인한 유저 정보 가져오기
	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserVO) auth.getPrincipal();
	}

	// 간트차트 페이지 접속
	@GetMapping("/project/gantt/list")
	public String ganttchartlist(Model model) {
		model.addAttribute("currentMenu", "gantt");
		return "project/issue/ganttChart";  
	}

	// 이슈목록 가져오기
	@GetMapping("/project/gantt/listget")
	@ResponseBody
	public List<IssueOutputVO> getIssue(@RequestParam(name = "filter", defaultValue = "all") String filter,
			HttpSession session) {

		// 세션에서 프로젝트 ID를 꺼냅니다.
		Long prjId = (Long) session.getAttribute("currentProjectId"); // 대소문자 주의
		
		IssueInputVO param = new IssueInputVO();
		param.setPrjId(prjId);
		Long loginMemId = getLoginUser().getId();
		
		// 개인필터 적용 
		 // 조회 시 개인/ 그룹 필터 추가 
		 switch (filter) {
		  case "my": // 개인 필터
			  param.setMemId(loginMemId); // 로그인한 memId를 가져오기 
			  break;
		  case "all": // 그룹 전체 조회 
			  default:
				  break;
		 }	
		
		return issueService.selectIssueList(param);
	}

}
