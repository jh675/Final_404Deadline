package com.example.demo.project.issue.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 이슈 id·제목만 필요할 때(상위 이슈 표시, 등록 폼 선택 목록 등) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueSummaryVO {

	private Long id;
	private String subject;
}
