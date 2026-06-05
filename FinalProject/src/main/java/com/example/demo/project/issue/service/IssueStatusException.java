package com.example.demo.project.issue.service;

/**
 * 이슈 상태 변경 시 담당자·상태 조합이 유효하지 않을 때 사용합니다.
 */
public class IssueStatusException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IssueStatusException(String message) {
		super(message);
	}
}
