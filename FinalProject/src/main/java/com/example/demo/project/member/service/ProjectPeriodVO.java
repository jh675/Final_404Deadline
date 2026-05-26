package com.example.demo.project.member.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 프로젝트 기간 — 구성원 등록 시 투입기간 제한용 (PROJECT.START_DATE / CLOSED_DATE) */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPeriodVO {

    private String prjName;
    private String startDate;
    private String closedDate;
}
