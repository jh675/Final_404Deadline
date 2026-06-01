package com.example.demo.project.milestone.service;

/**
 * 마일스톤-이슈 연동(등록/해제/이동) 시 비즈니스 규칙 위반.
 */
public class MilestoneSyncException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MilestoneSyncException(String message) {
		super(message);
	}
}
