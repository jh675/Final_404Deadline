package com.example.demo.project.group.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 그룹 목록 검색 조건 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupListCriteria {

    private Long prjId;
    private String grpName;
    private String createdFrom;
    private String createdTo;

    /** MyBatis 동적 SQL용 — null 을 빈 문자열로 통일 */
    public GroupListCriteria normalized() {
        grpName = nullToEmpty(grpName);
        createdFrom = nullToEmpty(createdFrom);
        createdTo = nullToEmpty(createdTo);
        return this;
    }

    public static GroupListCriteria forPrjId(Long prjId) {
        return GroupListCriteria.builder()
                .prjId(prjId)
                .grpName("")
                .createdFrom("")
                .createdTo("")
                .build();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
