package com.example.demo.project.notice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/project")
@Controller
public class NoticeController {

	@Autowired
	NoticeService noticeService;

	@Autowired
	AttachService attachService;

	@GetMapping("/notice/register")
	public String insertForm(Model model) {
		model.addAttribute("notice", new NoticeVO());
		return "project/notice/noticeRegister";
	}

	@GetMapping("/notice/attach")
	@ResponseBody
	public List<AttachVO> attach(@RequestParam("id") Long id) {
		return attachService.selectAttachList("01MODULE", id);
	}

	@PostMapping("/notice/insert")
	public String insert(NoticeVO notice, HttpSession session,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {

		Long projectId = (Long) session.getAttribute("currentProjectId");
		notice.setPrjId(projectId);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserVO user = (UserVO) auth.getPrincipal();
		notice.setMemId(user.getId());

		noticeService.insert(notice);

		boolean hasFiles = attachService.hasAttachmentFiles(attachments);
		if (hasFiles && notice.getId() != null) {
			attachService.saveAndInsertAttachments((long) notice.getId(), attachments, "01MODULE", "notice");
		}

		return "redirect:/project/notice/list";
	}

	@GetMapping("/notice/list")
	public String noticeList(HttpSession session, Model model, NoticeVO notice) {
		session.setAttribute("currentMenu", "notice");
		Long projectId = (Long) session.getAttribute("currentProjectId");
		notice.setPrjId(projectId);

		model.addAttribute("list", noticeService.selectAll(notice));
		return "project/notice/noticeList";
	}

	@GetMapping("/notice/modify")
	public String modifyForm(Model model, @RequestParam("id") Long id) {
		model.addAttribute("notice", noticeService.selectOne(id));
		return "project/notice/noticeRegister";
		

	}

	@PostMapping("/notice/modify")
	public String modify(HttpSession session,NoticeVO notice) {
		Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
		UserVO user = (UserVO) auth.getPrincipal();
		notice.setMemId(user.getId());
		noticeService.update(notice);
		return "redirect:/project/notice/list";
	}

	@GetMapping("/notice/delete")
	public String delete(@RequestParam("id") Long id) {
		noticeService.delete(id);
		return "redirect:/project/notice/list";
	}
}