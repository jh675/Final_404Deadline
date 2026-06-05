package com.example.demo.management.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

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
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date closedDate;
	private String prjHide;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	private String managerName;
	/** PROC_PROJECT_CREATE IN — 체크된 모듈 코드 콤마 구분 (GANTT,ISSUE,...) */
	private String enaId;
	/** PROC_PROJECT_CREATE OUT */
	private String resultStatus;
	private String resultMsg;
	
	private Long   copyPrjId;    // 복사 원본 프로젝트 ID (없으면 null)
	private String copyGroups;   // "Y" / "N"
	private String copyUsers;    // "Y" / "N"
	private String copyRoles;
	//유저 이름(매니저 검색용)
	private String userName;
	//그룹테이블
	private Long prjId;
    private Long grpId;
    private String gname;
    
    //멤버테이블
    private Long memberId;
    private Long mUserId;
    private Long mGrpId;
    
    //캘린더
    private Long Cid;
    private String place;
    private String calText;
    private Date calStart;
    private Date calEnd;
    private String calType;
    
    
    private Long managerId;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
    
}
