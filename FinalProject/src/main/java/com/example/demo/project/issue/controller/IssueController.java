package com.example.demo.project.issue.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.issue.service.AttachmentVO;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IssueController {

	@Autowired
	private IssueService service;

	@GetMapping("/issue/list")
	public String issueList(Model model, @ModelAttribute("filter") IssueInputVO issueVO) {
		List<IssueOutputVO> issueList = service.selectIssueList(issueVO);
		System.out.println(issueList);
		model.addAttribute("issueList", issueList);
		return "project/issue/issueList";
	}

	@GetMapping("/issue/detail")
	public String issueDetail(Model model, @RequestParam("id") Long id) {
		IssueOutputVO issue = service.selectIssue(id);
		model.addAttribute("issue", issue);
		if (issue != null && issue.getId() != null) {
			model.addAttribute("ParentIssue",
					issue.getParentIssue() == null ? null : service.getParentIssue(issue.getParentIssue()));
			model.addAttribute("childIssueTotal", service.countChildIssues(id));
			model.addAttribute("childIssueList", service.selectChildIssueList(id));
			List<CommentOutputVO> comments = service.getComment(id);
			model.addAttribute("comments", comments);
			model.addAttribute("commentTotal", comments.size());
		} else {
			model.addAttribute("childIssueTotal", 0L);
			model.addAttribute("childIssueList", Collections.emptyList());
			if (issue != null) {
				model.addAttribute("ParentIssue", null);
				model.addAttribute("comments", Collections.emptyList());
				model.addAttribute("commentTotal", 0);
			}
		}
		return "project/issue/issueDetail";
	}

	@PostMapping("/issue/comment")
	public String insertComment(@ModelAttribute CommentInputVO vo) {
		if (vo.getIssueId() == null) {
			return "redirect:/issue/list";
		}
		System.out.println(vo);
		if (vo.getContent() != null) {
			vo.setContent(vo.getContent().trim());
		}
		if (vo.getContent() == null || vo.getContent().isEmpty()) {
			return "redirect:/issue/detail?id=" + vo.getIssueId();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserVO u) {
			vo.setMemId(u.getId());
		}
		service.insertComment(vo);
		return "redirect:/issue/detail?id=" + vo.getIssueId();
	}

	@GetMapping("/issue/register")
	public String issueRegister(Model model) {
		model.addAttribute("issue", IssueInputVO.builder().build());
		model.addAttribute("issueIds", service.getIssueIds());
		return "project/issue/issueRegist";
	}

	@PostMapping(value = "/issue/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String issueInsert(Model model, @RequestPart("issue") IssueInputVO issueVO,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {
//		List<AttachmentVO> attachmentList = new ArrayList<>();
//		for (MultipartFile file : attachments) {
//			try {
//				String original = file.getOriginalFilename();
//				String baseName = (original != null && !original.isBlank())
//						? new File(original).getName()
//						: "file";
//				String diskFileName = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
//						.format(LocalDateTime.now()) + "_" + baseName;
//				AttachmentVO attachmentVO = new AttachmentVO();
//				attachmentVO.setFileName(original != null ? original : baseName);
//				attachmentVO.setFileSize(file.getSize());
//				attachmentVO.setContentType(file.getContentType());
//				attachmentVO.setDiskDirectory("attachments");
//				attachmentVO.setDiskFileName(diskFileName);
//				attachmentVO.setTableName("issue");
//				issueVO.setIsAttachCd("01ISATTACH");
//				file.transferTo(new File("d:/upload/" + diskFileName));
//				attachmentList.add(attachmentVO);
//			} catch (Exception e) {
//				log.error("Failed to store file {}: {}", file.getOriginalFilename(), e.getMessage());
//				return "redirect:/issue/register";
//			}
//		}
//		service.insertIssue(issueVO);
		return "redirect:/issue/list";
	}

}
