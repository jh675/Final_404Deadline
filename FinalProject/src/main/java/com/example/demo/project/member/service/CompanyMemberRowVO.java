package com.example.demo.project.member.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 프로젝트 수행 기업 소속 사용자 — 구성원 등록·수정 시 선택용 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyMemberRowVO {

    private Long userId;
    private String login;
    private String userName;
    private String hireDate;
    private String genderCd;
    private String email;
    private String tel;
}
