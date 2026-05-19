package com.example.demo.project.milestone.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneIssueVO {

	private Long id;
	private Long milestoneId;
	private Long issueId;
	private String issueSubject;
}
