package com.example.demo.project.group.service;

import lombok.Getter;
import lombok.Setter;

/** 그룹명 중복 확인 */
@Getter
@Setter
public class GroupNameDuplicateCriteria {

    private Long prjId;
    private String grpName;
}
