package com.example.demo.project.milestone.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneTimelineVO {

	private Long id;
	private Long milestoneIssueId;
	private Date entryDate;
	private String typeCd;
	private String contents;
}
