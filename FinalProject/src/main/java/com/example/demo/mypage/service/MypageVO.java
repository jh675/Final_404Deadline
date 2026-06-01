package com.example.demo.mypage.service;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MypageVO {
  private Long id;
  private String prjName;
  private String prjDesc;
  private String prjStatusCd;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date startDate;
  private Date closedDate;
  private Date createdOn;
  
  
}
