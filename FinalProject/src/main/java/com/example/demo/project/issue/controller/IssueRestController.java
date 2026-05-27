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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.history.service.HistoryVO;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;

@RestController
@RequestMapping("/issue/api")
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
			// TODO: handle exception
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/{issueId}/relIssue")
	public ResponseEntity<List<IssueOutputVO>> getRelIssue(@PathVariable("issueId") Long issueId) {
		try {
			return ResponseEntity.ok(issueService.getRelationIssue(issueId));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	@GetMapping("/{issueId}/history")
	public ResponseEntity<List<HistoryVO>> getHistory(@PathVariable("issueId") Long issueId) {
		try {
			return ResponseEntity.ok(issueService.getHistory(issueId));
		} catch (Exception e) {
			e.printStackTrace();
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

		int inserted = issueService.insertComment(input);
		if (inserted <= 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("ok", false, "message", "댓글 등록에 실패했습니다."));
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("ok", true, "message", "댓글이 등록되었습니다."));
	}
}
