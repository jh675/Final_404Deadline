package com.example.demo.mypage.controller;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.mypage.service.MypageService;
import com.example.demo.project.calender.service.CalenderService;
import com.example.demo.project.calender.service.CalenderVO;
import com.example.demo.project.calender.service.HolidayService;
import com.example.demo.project.calender.service.HolidayVO;


@Controller
public class mypageController {
	
	@Autowired
	ProjectService projectService; 
	
	@Autowired
	MypageService mypageService;
	
	@Autowired
	CalenderService calenderService;
	
	@Autowired
	HolidayService holidayService;

	// 로그인한 유저 정보 가져오기
	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserVO) auth.getPrincipal();
	}
	
	// 마이페이지 접속 	
	@GetMapping("/mypage")
	public String mypage(Model model) {
		UserVO loginUser = getLoginUser();
		
		// 프로젝트 목록 
		ProjectVO vo = new ProjectVO();
		vo.setUserId(loginUser.getId());
		List<ProjectVO> list = projectService.userProjectList(vo);
		model.addAttribute("projectList", list);
		
		
		// 캘린더 목록 
		CalenderVO calvo = new CalenderVO();
		calvo.setMemId(loginUser.getId().intValue());
		List<CalenderVO> callist = calenderService.selectAll(calvo);
		model.addAttribute("calenderList", callist);
		
		// 공휴일 목록 
		int year = java.time.LocalDate.now().getYear();
		List<HolidayVO> holiday = holidayService.getHolidays(year);
		model.addAttribute("holidayList", holiday);

		return "mypage/myPage";
	}
	// 마이페이지 내 정보 수정 
	@PostMapping("/mypage/update")
	@ResponseBody
	public String updateInfo(@RequestBody UserVO update) {
		UserVO loginUser = getLoginUser();
		update.setId(loginUser.getId());
		
		int result = mypageService.updateUser(update);
		return result > 0 ? "success" : "fail";
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
