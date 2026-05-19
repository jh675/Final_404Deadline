package com.example.demo.management.userManage.service;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.login.service.UserVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManageVO {
	private Long id;
	private String bizNo;
	private String compNm;
	private String login;
	private String name;
	private String tel;
	private String email;
	private Date hireDate;
	private String genderCd;
	private String genderNm;
	private String adminCd;
	private String adminNm;
	private String statusCd;
	private String statusNm;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdOn;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date pwUpdatedOn;
	private String mcpCd;
	private String mcpNm;
	private String prjManagerCd;
	private String prjManagerNm;
}
