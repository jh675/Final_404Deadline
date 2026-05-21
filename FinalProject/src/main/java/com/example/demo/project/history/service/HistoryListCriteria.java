package com.example.demo.project.history.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 히스토리 목록 검색 조건 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryListCriteria {

    private Long prjId;
    /** 일시 시작 (yyyy-MM-dd) */
    private String updatedFrom;
    /** 일시 종료 (yyyy-MM-dd) */
    private String updatedTo;
    /** 수정자 이름 부분 일치 */
    private String modifierName;
    /** 항목명 — ISSUE | PROJECT (빈 값이면 전체) */
    private String tableName;
    /** 내용 부분 일치 */
    private String detail;
}
