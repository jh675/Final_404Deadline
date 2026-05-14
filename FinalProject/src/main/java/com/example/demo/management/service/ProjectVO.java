package com.example.demo.management.service;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ProjectVO {
	private Integer id;
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
	private Integer userId;
	private String enaNameCd;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date closedDate;
}
