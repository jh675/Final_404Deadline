package com.example.demo.project.wiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.example.demo.login.service.UserVO;
import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiPageVO;
import com.example.demo.project.wiki.service.WikiService;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WikiController {

	
	@Autowired
	WikiService service;
	
	@Autowired
	AttachService attachService;

	private Long getCurrentProjectId(HttpSession session) {
		return (Long) session.getAttribute("currentProjectId");
	}

	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserVO u) {
			return u;
		}
		return null;
	}

	@GetMapping("/wiki/register")
	public String wikiWrite(Model model, @RequestParam(value = "id", required = false) Long id, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		List<WikiPageVO> pageList = service.selectWikiPageForTree(projectId, id);
		model.addAttribute("currentMenu", "wiki");
		model.addAttribute("pageList", pageList);
		if (id != null) {
			WikiContentVO page = service.selectWikiContentLastVerById(id);
			page.setId(id);
			model.addAttribute("page", page);
			model.addAttribute("selectedParentId", page.getParentId());
			System.out.println(page);
			List<AttachVO> existingFiles = attachService.selectAttachList("06MODULE", id);
			model.addAttribute("existingFiles",
					existingFiles != null ? existingFiles : Collections.emptyList());
		}
		return "project/wiki/wikiWrite";
	}
	
	@PostMapping(path = "/wiki/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String wikiWrite(@ModelAttribute WikiContentVO wikiContentVO,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		UserVO loginUser = getLoginUser();
		if (projectId == null) {
			return "redirect:/management/project";
		}
		if (loginUser == null) {
			return "redirect:/";
		}

		Long id = service.insertWikiPage(wikiContentVO.getTitle(), projectId, wikiContentVO.getParentId());
		if(attachService.hasAttachmentFiles(files)) {
			attachService.saveAndInsertAttachments(id, files, "06MODULE", "Wiki");
		}
		wikiContentVO.setPageId(id);
		wikiContentVO.setMemId(loginUser.getId());
		service.insertWikiContent(wikiContentVO);
		String encodedTitle = UriUtils.encodePathSegment(
				wikiContentVO.getTitle(), StandardCharsets.UTF_8);
		return "redirect:/wiki/view/" + encodedTitle;
	}
	
	@PostMapping(path = "/wiki/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String wikiUpdate(@ModelAttribute WikiContentVO wikiContentVO,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			HttpSession session) {
		UserVO loginUser = getLoginUser();
		if (getCurrentProjectId(session) == null) {
			return "redirect:/management/project";
		}
		if (loginUser == null) {
			return "redirect:/";
		}
		service.updateWikiPageParent(wikiContentVO.getPageId(), wikiContentVO.getParentId());

		if(attachService.hasAttachmentFiles(files)) {
			attachService.saveAndInsertAttachments(wikiContentVO.getPageId(), files, "06MODULE", "Wiki");
		}
		wikiContentVO.setMemId(loginUser.getId());
		service.updateWikiContent(wikiContentVO);
		String encodedTitle = UriUtils.encodePathSegment(
				wikiContentVO.getTitle(), StandardCharsets.UTF_8);
		return "redirect:/wiki/view/" + encodedTitle;
	}
	
	@GetMapping("/wiki/check")
	public ResponseEntity<Void> nameCheck(@RequestParam(name = "name") String name, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return ResponseEntity.badRequest().build();
		}
		if(service.nameCheck(projectId,name)==0){
			return ResponseEntity.ok().build();
		}else{
			return ResponseEntity.badRequest().build();
		}

	}
	
	@GetMapping("wiki/index/tree")
	public String wikiTree(Model model, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		model.addAttribute("currentMenu", "wiki");
		model.addAttribute("type", "title");
		model.addAttribute("titleTree", service.getTitleTree(projectId));
		return "project/wiki/wikiIndex";
	}

	@GetMapping("/wiki/index")
	public String wikiIndex(@RequestParam(value = "type", defaultValue = "title") String type,
			Model model, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}

		String currentType = "date".equalsIgnoreCase(type) ? "date" : "title";
		model.addAttribute("currentMenu", "wiki");
		model.addAttribute("type", currentType);
		if ("date".equals(currentType)) {
			model.addAttribute("dateGroups", service.getDateGroups(projectId));
		} else {
			model.addAttribute("titleTree", service.getTitleTree(projectId));
		}
		return "project/wiki/wikiIndex";
	}
	
	
	@GetMapping({"/wiki","/wiki/view/{name}"})
	public String wikiView(@PathVariable(required = false,name = "name") String name,
			@RequestParam(value = "version", required = false) Long version,
			Model model, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		WikiContentVO latestPage = service.selectWikiContentLastVerByTitle(projectId,name);
		if (latestPage == null) {
			return "redirect:/wiki";
		}
		WikiContentVO pageToShow = latestPage;
		if (version != null && latestPage.getPageId() != null) {
			WikiContentVO revisionPage = service.selectWikiContentByPageIdAndVersion(latestPage.getPageId(), version);
			if (revisionPage != null) {
				pageToShow = revisionPage;
			}
		}
		List<AttachVO> existingFiles = attachService.selectAttachList("06MODULE", latestPage.getPageId());
		model.addAttribute("currentMenu", "wiki");
		model.addAttribute("attachments", existingFiles);
		model.addAttribute("page", pageToShow);
		model.addAttribute("latestVersion", latestPage.getVersion());
		model.addAttribute("isHistoricalVersion",
				pageToShow.getVersion() != null && latestPage.getVersion() != null
				&& !pageToShow.getVersion().equals(latestPage.getVersion()));
		return "project/wiki/wikiView";
	}

	@GetMapping("/wiki/history/{name}")
	public String wikiHistory(@PathVariable("name") String name, Model model, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}

		WikiContentVO page = service.selectWikiContentLastVerByTitle(projectId, name);
		if (page == null || page.getPageId() == null) {
			return "redirect:/wiki";
		}

		model.addAttribute("currentMenu", "wiki");
		model.addAttribute("page", page);
		model.addAttribute("historyList", service.selectWikiHistoryByPageId(page.getPageId()));
		return "project/wiki/wikiHistory";
	}
	
	
}
