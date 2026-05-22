package com.example.demo.project.docs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.project.docs.service.DocsService;
import com.example.demo.project.docs.service.DocsVO;
import com.example.demo.util.attach.service.AttachService;

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
}
