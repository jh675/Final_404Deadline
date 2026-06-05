package com.example.demo.project.issue.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.history.service.HistoryVO;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;
import com.example.demo.project.issue.service.IssueVulkVO;

@RestController
@RequestMapping("/project/issue/api")
public class IssueRestController {

	@Autowired
	private IssueService issueService;

	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserVO u) {
			return u;
		}
		return null;
	}

	@GetMapping("/{issueId}/comments")
	public ResponseEntity<List<CommentOutputVO>> getComments(@PathVariable("issueId") Long issueId) {
		try {
			return ResponseEntity.ok(issueService.getComment(issueId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/{issueId}/relIssue")
	public ResponseEntity<List<IssueOutputVO>> getRelIssue(@PathVariable("issueId") Long issueId) {
		try {
			return ResponseEntity.ok(issueService.getRelationIssue(issueId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	@GetMapping("/{issueId}/history")
	public ResponseEntity<List<HistoryVO>> getHistory(@PathVariable("issueId") Long issueId) {
		try {
			return ResponseEntity.ok(issueService.getHistory(issueId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/{issueId}/comments")
	public ResponseEntity<Map<String, Object>> createComment(@PathVariable("issueId") Long issueId,
			@RequestBody CommentInputVO vo) {
		UserVO loginUser = getLoginUser();
		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("ok", false, "message", "로그인이 필요합니다."));
		}

		String content = vo != null && vo.getContent() != null ? vo.getContent().trim() : "";
		if (content.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("ok", false, "message", "댓글 내용을 입력해주세요."));
		}

		CommentInputVO input = new CommentInputVO();
		input.setIssueId(issueId);
		input.setParentId(vo != null ? vo.getParentId() : null);
		input.setContent(content);
		input.setMemId(loginUser.getId());

		Long inserted = issueService.insertComment(input);
		if (inserted <= 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("ok", false, "message", "댓글 등록에 실패했습니다."));
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("ok", true, "message", "댓글이 등록되었습니다."));
	}
	
	@PutMapping("/start-date")
	public ResponseEntity<Map<String, Object>> registerStartDate(@RequestBody IssueVulkVO vulkVO) {
		UserVO loginUser = getLoginUser();
		if(loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("ok", false, "message", "로그인이 필요합니다."));
		}
		if (vulkVO == null || vulkVO.getIds() == null || vulkVO.getIds().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("ok", false, "message", "등록할 이슈를 선택해 주세요."));
		}
		int requested = vulkVO.getIds().size();
		vulkVO.setUpdater(loginUser.getId());
		Long count = issueService.registerStartDate(vulkVO);
		if(count == null) {
			return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "이슈를 찾을 수 없습니다."));
		}
		long updated = count;
		long skipped = Math.max(0, requested - updated);
		if (updated == 0) {
			return ResponseEntity.badRequest().body(Map.of(
					"ok", false,
					"message", "담당자가 없거나 이미 시작일이 등록된 이슈는 시작일을 등록할 수 없습니다.",
					"updated", 0,
					"skipped", skipped));
		}
		return ResponseEntity.ok(Map.of(
				"ok", true,
				"message", updated + "개의 이슈의 시작일이 등록되었습니다.",
				"success", updated,
				"skipped", skipped));
	}

	@PutMapping("/closed-date")
	public ResponseEntity<Map<String, Object>> registerClosedDate(@RequestBody IssueVulkVO vulkVO) {
		UserVO loginUser = getLoginUser();
		if(loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("ok", false, "message", "로그인이 필요합니다."));
		}
		vulkVO.setUpdater(loginUser.getId());
		Long count = issueService.registerClosedDate(vulkVO);
		if(count == null) {
			return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "이슈를 찾을 수 없습니다."));
		}
		return ResponseEntity.ok(Map.of("ok", true, "message", count+"개의 이슈의 종료일이 등록되었습니다."));
	}
	@PutMapping("/bulk")
	public ResponseEntity<Map<String, Object>> bulk(@RequestBody IssueVulkVO vulkVO){
		UserVO loginUser = getLoginUser();
		if(loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("ok", false, "message", "로그인이 필요합니다."));
		}
		if (vulkVO == null || vulkVO.getIds() == null || vulkVO.getIds().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("ok", false, "message", "변경할 이슈를 선택해 주세요."));
		}
		int requested = vulkVO.getIds().size();
		vulkVO.setUpdater(loginUser.getId());
		Long count = issueService.updateVulk(vulkVO);
		if(count == null) {
			return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "이슈를 찾을 수 없습니다."));
		}
		long updated = count;
		long skipped = Math.max(0, requested - updated);
		if (vulkVO.getStatusCd() != null && !vulkVO.getStatusCd().isBlank() && updated == 0) {
			String statusMsg = ("02ISSUESTAT".equals(vulkVO.getStatusCd()) || "03ISSUESTAT".equals(vulkVO.getStatusCd()))
					? "담당자가 없는 이슈는 진행중·검토 상태로 변경할 수 없습니다."
					: "상태를 변경할 수 있는 이슈가 없습니다.";
			return ResponseEntity.badRequest().body(Map.of(
					"ok", false,
					"message", statusMsg,
					"updated", 0,
					"skipped", skipped));
		}
		return ResponseEntity.ok(Map.of(
				"ok", true,
				"message", updated + "개의 이슈가 수정되었습니다.",
				"updated", updated,
				"skipped", skipped));
	}
}
