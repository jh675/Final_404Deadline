package com.example.demo.project.notice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

@Controller
public class NoticeController {
	
	@Autowired
    NoticeService noticeService;
	
	@Autowired
	AttachService attachService;
	
	//등록페이지
	@GetMapping("/notice/register")
	public String insertForm(Model model) {
		model.addAttribute("notice", new NoticeVO());
		return "project/notice/noticeRegister"; 
	}
	
	//getmapping로 첨부파일 조회
	@GetMapping("/notice/attach")
	@ResponseBody
	public List<AttachVO> attach(@RequestParam("id") Long id) {
		return  attachService.selectAttachList("01MODULE", id);
	}
	
	//등록처리
    @PostMapping("/notice/insert")
    public String insert(NoticeVO notice,   
    		             @RequestPart(value="attachments", required = false) MultipartFile[] attachments) {
    	//공지사항 등록
        noticeService.insert(notice);
        
        //첨부파일 등록
        boolean hasFiles = attachService.hasAttachmentFiles(attachments);
        if (hasFiles && notice.getId() != null) {
            attachService.saveAndInsertAttachments((long)notice.getId(), attachments, "01MODULE", "notice");
        }

        return "redirect:/notice/list";
    }
    
    //목록페이지
    @GetMapping("/notice/list")
    public String noticeList(Model model,NoticeVO notice) {
    	notice.setPrjId((long) 4);
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