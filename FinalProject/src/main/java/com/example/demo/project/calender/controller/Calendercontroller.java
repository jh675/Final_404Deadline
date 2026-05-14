package com.example.demo.project.calender.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.calender.CalenderVO;
import com.example.demo.project.calender.service.CalenderService;



@Controller
public class Calendercontroller {
	
	@Autowired
	CalenderService calenderService;
 
	// 캘린더 페이지접속 
	@GetMapping({"/calender/list"})
	 public String callenderlist() {
	  
	  return "project/calender/calenderlist";
	}
	
	// 일정 등록
	@PostMapping("calender/post")
	@ResponseBody
	 public int post(CalenderVO vo) {
		return calenderService.insert(vo);
	}
	// 일정 수정
	@PostMapping("calender/update")
	@ResponseBody
	 public int update(CalenderVO vo) {
		calenderService.updete(vo);
		return calenderService.updete(vo);
	}
	// 일정 삭제 
	@PostMapping("calender/delete")
	@ResponseBody
	 public int delete(int id) {
		return calenderService.delete(id);
	}

	// 일정 검색 
	@GetMapping("calender/search")
	@ResponseBody
	 public List<CalenderVO>search(CalenderVO vo) {
		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Object principal = auth.getPrincipal();
	    if (principal instanceof UserVO user) {
	    	vo.setMemId(user.getId().intValue()); 
	    }
		return calenderService.selectAll(vo);
	}
	
	// 일정 데이터 가져오기
	@GetMapping("calender/data")
	@ResponseBody
	public List<CalenderVO>date(CalenderVO vo) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();
		if (principal instanceof UserVO user) {
			vo.setMemId(user.getId().intValue()); 
		}
		return calenderService.selectAll(vo);
	}
	
}
