package com.example.demo.alarm.service;

import lombok.Data;
import java.util.Date;

@Data
public class AlarmIssueVO {
    private Long id;
    private Long prjId;
    private String subject;
    private String statusCd;  // 한글로 변환된 값
    private Date createdOn;
    private Date updatedOn;
}