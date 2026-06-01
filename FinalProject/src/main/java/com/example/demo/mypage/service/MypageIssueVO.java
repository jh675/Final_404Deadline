package com.example.demo.mypage.service;

import lombok.Data;

@Data
public class MypageIssueVO {
	private Long id;
	private String subject;
	private String memName;
	private String statusCd ;
	private String priorityCd;
	private String dueDate;
	private Integer doneRatio;
	
}
