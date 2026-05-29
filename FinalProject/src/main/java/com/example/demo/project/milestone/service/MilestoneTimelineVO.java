package com.example.demo.project.milestone.service;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	/** DB·화면 모두 한국 시각(벽시계) 기준 — datetime-local 과 동일하게 취급 */
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private Date entryDate;
	private String typeCd;
	private String contents;
}
