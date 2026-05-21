package com.example.demo.project.wiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiService;

@Controller
public class WikiController {

	
	@Autowired
	WikiService service;
	
	@GetMapping("/wiki/write")
	public String wikiWrite(Model model) {
		return "project/wiki/wikiWrite";
	}
	
//	@PostMapping("/wiki/write")
//	public String wikiWrite(@RequestBody WikiContentVO wikiContentVO) {
//		service.insertWikiContent(wikiContentVO);
//		return "redirect:/wiki/view?id=" + wikiVO.getId();
//	}
	@GetMapping("/wiki/view")
	public String wikiView() {
		return "project/wiki/wikiView";
	}
	
	
}
