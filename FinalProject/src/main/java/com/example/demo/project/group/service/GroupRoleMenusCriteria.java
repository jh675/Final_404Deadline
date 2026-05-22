package com.example.demo.project.group.service;

import lombok.Getter;
import lombok.Setter;

/** 그룹 보유 권한 — 메뉴 목록 AJAX */
@Getter
@Setter
public class GroupRoleMenusCriteria {

    private Long prjId;
    private Long grpId;
    private Long roleCd;
}
