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
}
