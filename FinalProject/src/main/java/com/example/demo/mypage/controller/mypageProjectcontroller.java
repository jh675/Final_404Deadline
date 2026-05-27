package com.example.demo.mypage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;


@Controller
public class mypageProjectcontroller {
	
	@Autowired
	ProjectService projectService;

	// 로그인한 유저 정보 가져오기
	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserVO) auth.getPrincipal();
	}

	// 마이페이지 프로젝트 페이지 접속
	@GetMapping("/mypage/project")
	public String projectlist(

			@RequestParam(name = "prjName", required = false) String prjName,
			@RequestParam(name = "prjStatusCd", required = false) String prjStatusCd, Model model) {

		UserVO loginUser = getLoginUser();

		ProjectVO vo = new ProjectVO();
		vo.setUserId(loginUser.getId());
		vo.setPrjName(prjName);
		vo.setPrjStatusCd(prjStatusCd);

		List<ProjectVO> list = projectService.userProjectList(vo);

		model.addAttribute("list", list);
		model.addAttribute("prjName", prjName);
		model.addAttribute("prjStatusCd", prjStatusCd);

		return "mypage/mypageProject";
	}

}
