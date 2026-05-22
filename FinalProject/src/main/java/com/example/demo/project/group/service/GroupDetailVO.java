package com.example.demo.project.group.service;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 그룹 상세 — 기본정보 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDetailVO {

    private Long prjId;
    private Long grpId;
    private String prjName;
    private String grpName;
    private LocalDateTime createdOn;
    
    //프로젝트메인에 그룹별 인원 카운트용
    private Long count;
}
