package com.example.demo.project.issue.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInputVO {

	private Long id;
	private Long prjId;
	private String subject;
	private String description;
	private String categoryCd;
	private String statusCd;
	private String priorityCd;
	private Date createdOn;
	private Date updatedOn;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date estStartDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dueDate;
	private Date startDate;
	private Date closedDate;
	private Long parentIssue;
	private Long rootIssue;
	private Long memId;
	/** 목록 검색: 내가 맡은 이슈 (체크 시 "Y") */
	private String myIssue;
	private Long lft;
	private Long rgt;
	private Long doneRatio;
	private String isAttachCd;
	private Long writer;
	private Long lastUpdater;
	/** 하위이슈 목록 페이징(무한스크롤) — MyBatis에서만 사용 */
	private Long milestoneId;
}
