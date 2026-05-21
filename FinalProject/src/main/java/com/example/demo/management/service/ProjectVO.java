package com.example.demo.management.service;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ProjectVO {
	
//	프로젝트 테이블
	private Long id;
	private String bizNo;
	private Integer prjParId;
	private Integer prjRootId;
	private String prjName;
	private String prjDesc;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createdOn;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date updatedOn;
	private String prjIdentifier;
	private String prjStatusCd;
	private Integer prjLft;
	private Integer prjRgt;
	private Long userId;
	private String enaNameCd;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date closedDate;
	private String prjHide;
	
	//그룹테이블
	private Long prjId;
    private Long grpId;
    
    //멤버테이블
    private Long memberId;
    private Long mUserId;
    private Long mGrpId;
    
    
}
