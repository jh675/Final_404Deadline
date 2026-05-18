package com.example.demo.project.milestone.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneTimeline {

	private Long id;
	private Long milestoneIssueId;
	private Long years;
	private Long months;
	private Long dates;
	private String typeCd;
	private String contents;
}
