package com.example.demo.project.member.service;

import lombok.Getter;
import lombok.Setter;

/** 구성원 상세·수정·등록 화면 요청 */
@Getter
@Setter
public class MemberInfoCriteria {

    /** 목록 그리드 링크용(선택) — 조회는 세션 currentProjectId 기준 */
    private Long prjId;
    private Long userId;
    private Long grpId;
    private boolean edit;
}
