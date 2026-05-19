package com.example.demo.project.main.service;

import java.util.Date;

import lombok.Data;

@Data
public class MainIssueVO {
	private Integer id;
	private Integer prjId;
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
	private Integer parentIssue;
	private Integer rootIssue;
	private Integer memId;
	private Integer lft;
	private Integer rgt;
	private Integer doneRatio;
	private String isAttachCd;
}
