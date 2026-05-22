package com.example.demo.project.wiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiPageVO;
import com.example.demo.project.wiki.service.WikiService;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WikiController {

	
	@Autowired
	WikiService service;
	
	@Autowired
	AttachService attachService;
	@GetMapping("/wiki/register")
	public String wikiWrite(Model model, @RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			WikiContentVO page = service.selectWikiContentLastVerById(id);
			page.setId(id);
			model.addAttribute("page", page);
			System.out.println(page);
			List<AttachVO> existingFiles = attachService.selectAttachList("06MODULE", id);
			model.addAttribute("existingFiles",
					existingFiles != null ? existingFiles : Collections.emptyList());
		}
		return "project/wiki/wikiWrite";
	}
	
	@PostMapping(path = "/wiki/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String wikiWrite(@ModelAttribute WikiContentVO wikiContentVO,
			@RequestParam(value = "files", required = false) MultipartFile[] files) {

		Long id = service.insertWikiPage(wikiContentVO.getTitle(),(long)66);
		if(attachService.hasAttachmentFiles(files)) {
			attachService.saveAndInsertAttachments(id, files, "06MODULE", "Wiki");
		}
		wikiContentVO.setPageId(id);
		wikiContentVO.setMemId((long)3);
		service.insertWikiContent(wikiContentVO);
		String encodedTitle = UriUtils.encodePathSegment(
				wikiContentVO.getTitle(), StandardCharsets.UTF_8);
		return "redirect:/wiki/view/" + encodedTitle;
	}
	
	@PostMapping(path = "/wiki/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String wikiUpdate(@ModelAttribute WikiContentVO wikiContentVO,
			@RequestParam(value = "files", required = false) MultipartFile[] files) {


		if(attachService.hasAttachmentFiles(files)) {
			attachService.saveAndInsertAttachments(wikiContentVO.getPageId(), files, "06MODULE", "Wiki");
		}
		wikiContentVO.setMemId((long)3);
		service.updateWikiContent(wikiContentVO);
		String encodedTitle = UriUtils.encodePathSegment(
				wikiContentVO.getTitle(), StandardCharsets.UTF_8);
		return "redirect:/wiki/view/" + encodedTitle;
	}
	
	@GetMapping("/wiki/check")
	public ResponseEntity<Void> nameCheck(@RequestParam(name = "name") String name) {
		if(service.nameCheck((long)66,name)==0){
			return ResponseEntity.ok().build();
		}else{
			return ResponseEntity.badRequest().build();
		}

	}
	
	
	@GetMapping({"/wiki","/wiki/view/{name}"})
	public String wikiView(@PathVariable(required = false,name = "name") String name,Model model) {
		WikiContentVO vo=service.selectWikiContentLastVerByTitle((long)66,name);
		List<AttachVO> existingFiles = attachService.selectAttachList("06MODULE", vo.getPageId());
		model.addAttribute("attachments", existingFiles);
		model.addAttribute("page", vo);
		return "project/wiki/wikiView";
	}
	
	
}
