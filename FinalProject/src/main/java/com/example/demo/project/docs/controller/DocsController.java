package com.example.demo.project.docs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.project.docs.service.DocsService;
import com.example.demo.project.docs.service.DocsVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class DocsController {

	@Autowired
	DocsService docsService;
	
	@Autowired
	AttachService attactchservice;
	
	@GetMapping("/docs/list")
    public String noticeList(HttpSession session, Model model, DocsVO doc) {
		Long projectId = (Long) session.getAttribute("currentProjectId");

    	doc.setPrjId(projectId);
        model.addAttribute("list", docsService.selectAll(doc));
        return "project/docs/docList";
    }
	
	@GetMapping("/docs/form")
	public String form(@RequestParam("id") Long id,
	                   Model model) {

	    DocsVO docs = docsService.selectOne(id);
	    	    
	    model.addAttribute("docs", docs);
	    
	    List<AttachVO> files =
	            attactchservice.selectAttachList("07MODULE", id);

	    model.addAttribute("files", files);

	    return "project/docs/docForm";
	}
	
	@GetMapping("/docs/register")
	public String registerForm(Model model) {

	    model.addAttribute("docs", new DocsVO());

	    return "project/docs/docForm";
	}
	
		
	@PostMapping("/docs/register")
	public String register(
	        DocsVO vo,
	        HttpSession session,
	        @RequestParam("attachments") MultipartFile[] attachments) {

	    vo.setPrjId((Long) session.getAttribute("currentProjectId"));
	    
	    vo.setMemId((Long) session.getAttribute("memId"));
	    
	    // [최소한의 추가] 시스템 관리자 테스트 시 memId가 null로 넘어와 발생하는 ORA-01400 에러 방어
	    if (vo.getMemId() == null) {
	        vo.setMemId(1L); // 데이터베이스에 존재하는 테스트용 회원 ID 번호 지정
	    }

	    // 1. 문서 저장
	    docsService.insert(vo);

	    // 2. 첨부파일 저장
	    attactchservice.saveAndInsertAttachments(
	            vo.getId(),
	            attachments,
	            "07MODULE",
	            "DOCUMENT"
	    );

	    return "redirect:/docs/list";
	}
	
	@PostMapping("/docs/update")
	public String update(
	        DocsVO vo,
	        HttpSession session,
	        @RequestParam("attachments") MultipartFile[] attachments) {

	    vo.setPrjId((Long) session.getAttribute("currentProjectId"));
	    vo.setMemId((Long) session.getAttribute("memId"));

	    if (vo.getMemId() == null) {
	        vo.setMemId(1L); 
	    }

	    
	    docsService.update(vo);

	   
	    attactchservice.saveAndInsertAttachments(
	            vo.getId(),
	            attachments,
	            "07MODULE",
	            "DOCUMENT"
	    );

	    return "redirect:/docs/list";
	}
	
	@GetMapping("/docs/delete")
	public String delete(@RequestParam("id") Long id) {
	    
	    
	    docsService.delete(id);
	    
	    
	    return "redirect:/docs/list";
	}

}
