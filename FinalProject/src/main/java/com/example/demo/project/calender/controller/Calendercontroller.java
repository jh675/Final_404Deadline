package com.example.demo.project.calender.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;

// import com.example.demo.project.calender.CalenderVO;


@Controller
public class Calendercontroller {
	
 
	// 일정 조회페이지
	@GetMapping({"/calender/list"})
	 public String callenderlist() {
		return "project/calender/calenderlist";
	}
	
	// 일정 등록 
	
	
	// 일정 수정, 삭제
}
