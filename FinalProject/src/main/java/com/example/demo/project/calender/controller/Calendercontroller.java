package com.example.demo.project.calender.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.calender.service.CalenderService;
import com.example.demo.project.calender.service.CalenderVO;



@Controller
public class Calendercontroller {
	
	@Autowired
	CalenderService calenderService;
 
	// 캘린더 페이지접속 
	@GetMapping({"/calender/list"})
	 public String callenderlist() {
	  
	  return "project/calender/calender";
	}
	
	// 일정목록 가져오기 
	@GetMapping("/calender/listJson" )
	@ResponseBody
	public List<CalenderVO> calenderListJson(@RequestParam(name = "typeCd", defaultValue = "") String typeCd) {
		CalenderVO vo = new CalenderVO();
		vo.setTypeCd(typeCd);
		vo.setMemId(2);
		return calenderService.getList(vo);
	}
	
	// 일정 등록
	@PostMapping("calender/insert")
	@ResponseBody
	 public int post(CalenderVO vo) {
			/*
			 * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			 * Object principal = auth.getPrincipal(); if (principal instanceof UserVO user)
			 * { vo.setMemId(user.getId().intValue()); }
			 */
	    vo.setMemId(2); 
		
		return calenderService.insert(vo);
	}
	// 일정 수정
	@PutMapping("/calender/update")
	@ResponseBody
	 public int update(CalenderVO vo) {
		return calenderService.update(vo);
	}
	
	// 일정 수정에서 가져온 데이터 단건 조회 API 
	@GetMapping("/calender/detail")
	@ResponseBody
	public CalenderVO getEventDetail(@RequestParam("id") int id) {
	    return calenderService.selectOne(id); 
	}
	
	// 일정 삭제 
	@DeleteMapping("/calender/delete")
	@ResponseBody
	 public int delete(@RequestParam(name="id") int id) {
		return calenderService.delete(id);
	}

	// 일정 검색 
	@GetMapping("calender/search")
	@ResponseBody
	 public List<CalenderVO>search(CalenderVO vo) {
		vo.setMemId(2);
		
		 System.out.println("keyword: " + vo.getKeyword());
		    System.out.println("memId: " + vo.getMemId());
//		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	        Object principal = auth.getPrincipal();
//	    if (principal instanceof UserVO user) {
//	    	vo.setMemId(user.getId().intValue()); 
//	    }
	    
	    // System.out.println("principal 타입: " + principal.getClass().getName());
		return calenderService.selectAll(vo);
	}
	
	
	
}
