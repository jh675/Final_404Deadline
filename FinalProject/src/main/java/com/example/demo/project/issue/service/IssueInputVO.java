package com.example.demo.project.issue.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
	private Date estStartDate;
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

	/** 하위이슈 목록 페이징(무한스크롤) — MyBatis에서만 사용 */
	private Integer offset;
	private Integer limit;
}
