package com.example.demo.project.notice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;

@Controller
public class NoticeController {
	
	@Autowired
    NoticeService noticeService;
	    
	//등록페이지
	@GetMapping("/notice/register")
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
    public String noticeList(Model model,NoticeVO notice) {
    	notice.setPrjId(4);
        model.addAttribute("list", noticeService.selectAll(notice));
        return "project/notice/noticeList";
    }
    
 // 수정페이지 이동
 	@GetMapping("/notice/modify")
 	public String modifyForm(Model model, @RequestParam("id") Integer id) {
 		model.addAttribute("notice", noticeService.selectOne(id));
 		return "project/notice/noticeRegister"; 
 	}
 	
 	// 수정처리
 	@PostMapping("/notice/modify")
 	public String modify(NoticeVO notice) {
 		noticeService.update(notice);
 		return "redirect:/notice/list";
 	}

 	// 삭제처리
 	@GetMapping("/notice/delete")
 	public String delete(@RequestParam("id") Integer id) {
 		noticeService.delete(id);
 		return "redirect:/notice/list";
 	}
}