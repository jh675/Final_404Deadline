package com.example.demo.project.option.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 권한 목록 검색 조건 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleListCriteria {

    private Long prjId;
    private String permissionKey;
    private String permissionName;
    private String createdFrom;
    private String createdTo;

    public RoleListCriteria normalized() {
        permissionKey = nullToEmpty(permissionKey);
        permissionName = nullToEmpty(permissionName);
        createdFrom = nullToEmpty(createdFrom);
        createdTo = nullToEmpty(createdTo);
        return this;
    }

    public RoleVO toSearchVo() {
        normalized();
        return RoleVO.builder()
                .prjId(prjId)
                .permissionKey(permissionKey)
                .permissionName(permissionName)
                .createdFrom(createdFrom)
                .createdTo(createdTo)
                .build();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
