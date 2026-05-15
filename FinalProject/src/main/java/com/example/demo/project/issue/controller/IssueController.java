package com.example.demo.project.issue.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IssueController {

	@Autowired
	private IssueService service;

	@Autowired
	private AttachService attachService;

	@GetMapping("/issue/list")
	public String issueList(Model model, @ModelAttribute("filter") IssueInputVO issueVO) {
		List<IssueOutputVO> issueList = service.selectIssueList(issueVO);
		model.addAttribute("issueList", issueList);
		return "project/issue/issueList";
	}

	@GetMapping("/issue/detail")
	public String issueDetail(Model model, @RequestParam("id") Long id) {
		IssueOutputVO issue = service.selectIssue(id);
		model.addAttribute("issue", issue);

		List<AttachVO> attachments = Collections.emptyList();
		if (issue != null && issue.getId() != null) {
			List<AttachVO> loaded = attachService.selectAttachList("04MODULE", id);
			attachments = loaded != null ? loaded : Collections.emptyList();
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
		model.addAttribute("attachments", attachments);
		return "project/issue/issueDetail";
	}

	@PostMapping("/issue/comment")
	public String insertComment(@ModelAttribute CommentInputVO vo) {
		if (vo.getIssueId() == null) {
			return "redirect:/issue/list";
		}
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
	public String issueRegister(Model model, @RequestParam(required = false) Long id) {
		IssueInputVO issue;
		//id값이 있는경우(수정버튼 클릭해서 온경우)
		if (id != null) {
			//해당 아이디로 검색해서 값은 가져온다
			issue = service.selectIssueForForm(id);
			//만약 값이 없다면 잘못된 경로(임의로 id값을 집어넣은경우)이므로
			if (issue == null || issue.getId() == null) {
				//리스트쪽으로 돌려보낸다
				return "redirect:/issue/list";
			}
		} else {
			//id값이 없는경우 빈vo를 만든다
			issue = IssueInputVO.builder().build();
		}
		//모델에 담아서 보낸다
		model.addAttribute("issue", issue);
		return "project/issue/issueRegist";
	}

	@PostMapping(value = "/issue/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String issueInsert(@RequestPart("issue") IssueInputVO issueVO,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {

		boolean hasFiles = hasAttachmentFiles(attachments);
		if (hasFiles) {
			issueVO.setIsAttachCd("01ISATTACH");
		}
		Long issueId = service.insertIssue(issueVO);
		if (hasFiles && issueId != null) {
			saveIssueAttachments(issueId, attachments);
		}
		return "redirect:/issue/list";
	}

	@PostMapping(value = "/issue/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String issueUpdate(@RequestPart("issue") IssueInputVO issueVO,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {

		if (issueVO.getId() == null) {
			return "redirect:/issue/list";
		}
		boolean hasFiles = hasAttachmentFiles(attachments);
		if (hasFiles) {
			issueVO.setIsAttachCd("01ISATTACH");
			saveIssueAttachments(issueVO.getId(), attachments);
		}
		service.updateIssue(issueVO);
		return "redirect:/issue/detail?id=" + issueVO.getId();
	}



	private boolean hasAttachmentFiles(MultipartFile[] attachments) {
		if (attachments == null) {
			return false;
		}
		for (MultipartFile f : attachments) {
			if (f != null && !f.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void saveIssueAttachments(Long issueId, MultipartFile[] attachments) {
		List<AttachVO> saved = attachService.saveAttach(attachments, "04MODULE");
		if (saved.isEmpty()) {
			return;
		}
		for (AttachVO a : saved) {
			a.setContainerId(issueId);
			a.setContainerType("ISSUE");
			a.setTableName("04MODULE");
		}
		attachService.insertAttach(saved);
	}

}
