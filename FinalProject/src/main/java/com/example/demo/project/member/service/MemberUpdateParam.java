package com.example.demo.project.member.service;

import lombok.Getter;
import lombok.Setter;

/** 구성원 수정 — MEMBER 종료일·그룹 */
@Getter
@Setter
public class MemberUpdateParam {
    private Long prjId;
    private Long userId;
    private Long oldGrpId;
    private Long grpId;
    private String prjEndDate;
}
