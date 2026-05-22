package com.example.demo.project.member.service;

import lombok.Getter;
import lombok.Setter;

/** 구성원 등록 모달 — 기업 사용자 목록 */
@Getter
@Setter
public class MemberCompanyMembersCriteria {

    private Long prjId;
    private boolean excludeRegistered;
}
