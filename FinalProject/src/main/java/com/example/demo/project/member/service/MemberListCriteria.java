package com.example.demo.project.member.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 프로젝트 멤버 목록 검색 조건 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberListCriteria {

    private Long prjId;
    /** 사용자 이름 부분 일치 */
    private String memberName;
    /** 소속 그룹명 부분 일치 */
    private String grpName;

    public MemberListCriteria normalized() {
        memberName = nullToEmpty(memberName);
        grpName = nullToEmpty(grpName);
        return this;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
