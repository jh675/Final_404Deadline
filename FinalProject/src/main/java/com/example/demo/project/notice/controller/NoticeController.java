package com.example.demo.project.notice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;

@Controller
public class NoticeController {
	
	@Autowired
    NoticeService noticeService;
	    
	//등록페이지
	@GetMapping("/notice/insert")
	public String insertForm(Model model) {
		model.addAttribute("notice", new NoticeVO());
		return "project/notice/noticeRegister"; 
	}
	
	//등록처리
    @PostMapping("/notice/insert")
    public String insert(NoticeVO notice) {
        noticeService.insert(notice);
        return "redirect:/notice/list";
    }
    
    //목록페이지
    @GetMapping("/notice/list")
    public String noticeList(Model model) {
        model.addAttribute("list", noticeService.selectAll(null));
        return "project/notice/noticeList";
    }

}