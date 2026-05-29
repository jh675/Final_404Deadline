package com.example.demo.project.group.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 그룹 등록 — 구성원 선택 모달 Grid 행 (MEMBER + USERS + GRP) */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberPickRowVO {

    /** MEMBER.ID — 모달 사번 컬럼 */
    private Long memberId;
    /** MEMBER.USER_ID — 그룹 등록 시 PROC_GRP_INSERT memIds */
    private Long userId;
    private String userName;
    private String tel;
    private String email;
    private String grpName;
}
