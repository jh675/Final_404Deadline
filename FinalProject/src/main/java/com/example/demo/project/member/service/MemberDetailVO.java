package com.example.demo.project.member.service;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 구성원 상세 — 기본·계정·프로젝트 정보 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetailVO {

    private Long memberId;
    private Long userId;
    private Long grpId;
    private Long prjId;

    private String userName;
    private String hireDate;
    private String genderCd;
    private String email;
    private String tel;

    private String login;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastLoginOn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date pwUpdatedOn;

    private String grpName;
}
