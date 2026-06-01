package com.example.demo.project.issue.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;
import com.example.demo.project.member.service.MemberListCriteria;
import com.example.demo.project.member.service.MemberService;
import com.example.demo.project.milestone.service.MilestoneService;
import com.example.demo.project.milestone.service.MilestoneSyncException;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class IssueController {

	@Autowired
	private IssueService issueService;

	@Autowired
	private AttachService attachService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MilestoneService milestoneService;
	
	@Autowired
	private ProjectService projectService;

	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserVO u) {
			return u;
		}
		return null;
	}

	private Long getCurrentProjectId(HttpSession session) {
		return (Long) session.getAttribute("currentProjectId");
	}

	@GetMapping("/project/issue/list")
	public String issueList(Model model, @ModelAttribute("filter") IssueInputVO issueVO, HttpSession session) {
		session.setAttribute("currentMenu", "issue"); // 대소문자 주의
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		issueVO.setPrjId(projectId);
		MemberListCriteria filter = new MemberListCriteria();
		filter.setPrjId(projectId);
		List<IssueOutputVO> issueList = issueService.selectIssueList(issueVO);
		model.addAttribute("members", memberService.selectProjectMemberList(filter));
		model.addAttribute("currentMenu", "issue");
		model.addAttribute("issueList", issueList);
		return "project/issue/issueList";
	}

	@GetMapping("/project/issue/detail")
	public String issueDetail(Model model, @RequestParam("id") Long id, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		IssueOutputVO issue = issueService.selectIssue(id);
		if (issue == null || issue.getId() == null || !projectId.equals(issue.getPrjId())) {
			return "redirect:/project/issue/list";
		}
		model.addAttribute("currentMenu", "issue");
		model.addAttribute("issue", issue);

		List<AttachVO> attachments = Collections.emptyList();
		if (issue != null && issue.getId() != null) {
			List<AttachVO> loaded = attachService.selectAttachList("04MODULE", id);
			attachments = loaded != null ? loaded : Collections.emptyList();
			model.addAttribute("ParentIssue",
					issue.getParentIssue() == null ? null : issueService.getParentIssue(issue.getParentIssue()));
			model.addAttribute("childIssueTotal", issueService.countChildIssues(id));
			model.addAttribute("childIssueList", issueService.selectChildIssueList(id));
		} else {
			model.addAttribute("childIssueTotal", 0L);
			model.addAttribute("childIssueList", Collections.emptyList());
			if (issue != null) {
				model.addAttribute("ParentIssue", null);
			}
		}
		model.addAttribute("attachments", attachments);
		return "project/issue/issueDetail";
	}

	@PostMapping("/project/issue/comment")
	public String insertComment(@ModelAttribute CommentInputVO vo) {
		if (vo.getIssueId() == null) {
			return "redirect:/project/issue/list";
		}
		if (vo.getContent() != null) {
			vo.setContent(vo.getContent().trim());
		}
		if (vo.getContent() == null || vo.getContent().isEmpty()) {
			return "redirect:/project/issue/detail?id=" + vo.getIssueId();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserVO u) {
			vo.setMemId(u.getId());
		}
		issueService.insertComment(vo);
		return "redirect:/project/issue/detail?id=" + vo.getIssueId();
	}

	
	@GetMapping("/project/issue/register")
	public String issueRegister(Model model, @RequestParam(value = "id", required = false) Long id, HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		IssueInputVO issue;
		//id값이 있는경우(수정버튼 클릭해서 온경우)
		if (id != null) {
			//해당 아이디로 검색해서 값은 가져온다
			issue = issueService.selectIssueForForm(id);
			//만약 값이 없다면 잘못된 경로(임의로 id값을 집어넣은경우)이므로
			if (issue == null || issue.getId() == null || !projectId.equals(issue.getPrjId())) {
				//리스트쪽으로 돌려보낸다
				return "redirect:/project/issue/list";
			}
		} else {
			//id값이 없는경우 빈vo를 만든다
			issue = IssueInputVO.builder().build();
			issue.setPrjId(projectId);
		}
		//모델에 담아서 보낸다
		
		MemberListCriteria filter = new MemberListCriteria();
		filter.setPrjId(projectId);
		model.addAttribute("milestones",milestoneService.selectMilestoneList(projectId));
		model.addAttribute("members", memberService.selectProjectMemberList(filter));
		model.addAttribute("currentMenu", "issue");
		model.addAttribute("issue", issue);
		model.addAttribute("issueIds", issueService.getIssueIds(projectId, issue.getId()));
		model.addAttribute("project", projectService.getprojectid(projectId));
		if (issue.getId() != null) {
			model.addAttribute("milestoneUnlinkBlocked", issueService.isMilestoneUnlinkBlocked(issue.getId()));
		} else {
			model.addAttribute("milestoneUnlinkBlocked", false);
		}
		return "project/issue/issueRegist";
	}

	@ExceptionHandler(MilestoneSyncException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> handleMilestoneSync(MilestoneSyncException ex) {
		return ResponseEntity.badRequest()
				.body(Map.of("ok", false, "message", ex.getMessage()));
	}

	@PostMapping(value = "/project/issue/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String issueInsert(@RequestPart("issue") IssueInputVO issueVO,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments,
			HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		UserVO loginUser = getLoginUser();
		if (projectId == null) {
			return "redirect:/management/project";
		}
		if (loginUser == null) {
			return "redirect:/";
		}
		issueVO.setPrjId(projectId);
		issueVO.setWriter(loginUser.getId());
		boolean hasFiles = attachService.hasAttachmentFiles(attachments);
		if (hasFiles) {
			issueVO.setIsAttachCd("01ISATTACH");
		}
		Long issueId = issueService.insertIssue(issueVO);
		if (hasFiles && issueId != null) {
			attachService.saveAndInsertAttachments(issueId, attachments, "04MODULE", "ISSUE");
		}
		return "redirect:/project/issue/list";
	}

	@PutMapping(value = "/project/issue/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String issueUpdate(@RequestPart("issue") IssueInputVO issueVO,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments,
			HttpSession session) {
		Long projectId = getCurrentProjectId(session);
		UserVO loginUser = getLoginUser();
		if (projectId == null) {
			return "redirect:/management/project";
		}
		if (loginUser == null) {
			return "redirect:/";
		}
		if (issueVO.getId() == null) {
			return "redirect:/project/issue/list";
		}
		issueVO.setPrjId(projectId);
		issueVO.setLastUpdater(loginUser.getId());
		boolean hasFiles = attachService.hasAttachmentFiles(attachments);
		if (hasFiles) {
			issueVO.setIsAttachCd("01ISATTACH");
			attachService.saveAndInsertAttachments(issueVO.getId(), attachments, "04MODULE", "ISSUE");
		}else {
			issueVO.setIsAttachCd("02ISATTACH");
		}
		issueService.updateIssue(issueVO);
		return "redirect:/project/issue/detail?id=" + issueVO.getId();
	}

	

	@GetMapping("/project/issue/pivot")
	public String issuePivot(Model model, HttpSession session) {

		Long projectId = getCurrentProjectId(session);
		if (projectId == null) {
			return "redirect:/management/project";
		}
		List<Map<String, Object>> pivotStatus = issueService.getPivotStatus(projectId);
		List<Map<String, Object>> pivotPriority = issueService.getPivotPriority(projectId);
		List<Map<String, Object>> pivotCategory = issueService.getPivotCategory(projectId);
		model.addAttribute("pivotStatus", pivotStatus);
		model.addAttribute("pivotPriority", pivotPriority);
		model.addAttribute("pivotCategory", pivotCategory);
		model.addAttribute("priorityCols", List.of("최상", "상", "중", "하"));
		model.addAttribute("categoryCols", List.of("버그", "기능", "작업", "개선"));
		model.addAttribute("statusCols", List.of("신규", "진행중", "검토", "완료"));
		model.addAttribute("currentMenu", "issue");
		return "project/issue/issuePivot";
	}
}
