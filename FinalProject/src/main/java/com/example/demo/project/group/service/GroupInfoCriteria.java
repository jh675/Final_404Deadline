package com.example.demo.project.group.service;

import lombok.Getter;
import lombok.Setter;

/** 그룹 상세·등록 화면 요청 */
@Getter
@Setter
public class GroupInfoCriteria {

    private Long prjId;
    private Long grpId;
}
